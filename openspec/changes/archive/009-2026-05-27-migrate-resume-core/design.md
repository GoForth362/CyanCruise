## Context

CyanCruise 已完成职业画像/onboarding、测评评分和测评提交 WebAPI。CareerLoop 的下一个 P0 输入是简历：今日任务、JD 匹配、模拟面试和职业计划都需要知道用户是否已有系统保存的简历、最近简历的目标岗位和文件引用。

IPD 源系统的简历基础由 `ResumeController`、`ResumeService`、`ResumeServiceImpl`、`Resume` 和 `FileService` 组成。它支持创建简历、读取详情、读取用户列表、更新元数据、删除记录、生成预览 URL、代理下载 PDF，并在创建/更新后把最新简历合并到用户画像快照。CyanCruise 需要迁移这些业务语义，但不能直接搬迁 Spring Controller、JPA Entity、OSS SDK 或 Multipart 实现。

IPD 来源证据：

| 来源 | 迁移参考 |
| --- | --- |
| `F:\Project\IPD\backend\src\main\java\com\group1\career\controller\ResumeController.java` | 创建、详情、列表、更新、删除、下载入口语义和 owner-only 约束 |
| `F:\Project\IPD\backend\src\main\java\com\group1\career\service\ResumeService.java` | 简历服务边界、所有权校验、预览 URL hydrate 语义 |
| `F:\Project\IPD\backend\src\main\java\com\group1\career\service\impl\ResumeServiceImpl.java` | 保存/更新后合并画像 resume block、删除后清理文件的业务链路 |
| `F:\Project\IPD\backend\src\main\java\com\group1\career\model\entity\Resume.java` | 简历字段来源：用户、标题、目标岗位、文件 key、版本、状态、解析内容、诊断分数、时间戳 |
| `F:\Project\IPD\backend\src\main\java\com\group1\career\service\FileService.java` | 文件 key、短链预览、下载、删除的抽象边界 |
| `F:\Project\IPD\backend\src\main\java\com\group1\career\service\impl\FileServiceImpl.java` | OSS 实现仅作后续适配参考，本次不迁移 SDK 细节 |

## Goals / Non-Goals

**Goals:**

- 新增 JDK 8 兼容的简历 DTO 和请求 DTO。
- 新增简历应用服务，支持创建、详情、用户列表、更新和删除。
- 新增 `ResumeStorage` 边界和默认文件型存储适配器，保留未来 Cosmic datamodel 替换点。
- 新增 `ResumeWebApi`，保持与现有 `CareerProfileWebApi`、`AssessmentWebApi` 相同的 Cosmic WebAPI 注解和 `/cc001/...` 路径风格。
- 创建或更新简历后，将最新简历写入 `UserProfileSnapshot.ResumeBlock` 并刷新统一画像。
- 删除当前画像引用的简历后，清理 resume block 或切换到用户剩余最新简历。
- 增加 cloud app 测试，覆盖 CRUD、所有权约束、文件型持久化和画像同步。

**Non-Goals:**

- 不迁移 Spring `ResumeController`、JPA `ResumeRepository` 或 Flyway SQL。
- 不实现 Multipart 上传、OSS SDK 调用、PDF 字节下载或短链预览 URL。
- 不实现简历诊断、简历生成、关键词抽取或 AI 解析。
- 不创建 webapp 简历页面。
- 不一次性建立最终 Cosmic datamodel。

## Decisions

1. 使用 `resume-core` 作为独立能力

   简历基础是 CareerLoop 的 P0 输入，和测评一样应成为独立规格能力。画像规格只描述跨工具快照，简历规格负责记录管理和 resume block 同步，避免把所有业务规则塞进 `career-profile-onboarding`。

2. 先落 DTO、服务、存储边界和 WebAPI，不直接建 datamodel

   当前仓库已有 `CareerProfileStorage` 文件型适配器模式，适合作为 datamodel 前的迁移桥。简历基础需要先稳定行为契约：记录字段、用户归属、排序、删除语义和画像同步。后续 datamodel change 可以实现同一个 `ResumeStorage` 接口，而不改 WebAPI 或 helper 层。

   备选方案是本次直接建 Cosmic 数据模型。该方案更接近最终部署，但会把数据建模、平台查询、文件服务、权限和接口一起展开，风险和范围都偏大。

3. 文件引用只保存 key，不迁移 OSS 运行依赖

   IPD 已经把 `fileUrl` 语义收敛为 OSS object key，预览 URL 由 `FileService.presignedUrl` 即时生成。CyanCruise 本次保留这个稳定引用语义，但不引入 Aliyun OSS SDK 或 Multipart 上传。WebAPI 接收已经存在的 file key，后续文件服务 change 再处理上传/下载。

4. 所有权约束放在应用服务

   IPD 依赖 JWT 当前用户判断 owner-only。CyanCruise 当前 WebAPI 仍采用显式 `userId` 参数，因此本次用应用服务方法要求 `userId + resumeId` 一起校验，跨用户读取、更新和删除应抛出明确异常。后续统一身份上下文后，可以把当前用户解析前移到 WebAPI 或平台拦截层。

5. 创建/更新后同步画像，删除后处理引用

   `UserProfileSnapshot.ResumeBlock` 已存在字段：`lastResumeId`、`lastResumeKey`、`title`、`targetJob`、`diagnosisScore`、`updatedAt`。本次应新增 resume merge 能力并在保存/更新后刷新统一画像。删除时如果被删记录是当前引用，应查找用户剩余最新简历并重建 resume block；若没有剩余记录，则清空 resume block 并刷新画像。

## Risks / Trade-offs

- [Risk] 文件型简历存储不是最终 Cosmic datamodel。→ Mitigation: 通过 `ResumeStorage` 边界隔离，后续只替换存储实现。
- [Risk] 当前 WebAPI 显式接收 `userId`，尚未接入平台当前用户。→ Mitigation: 应用服务强制所有权校验；后续安全集成 change 再统一改造当前用户来源。
- [Risk] 删除文件对象未在本次实现。→ Mitigation: 本次只删除记录并更新画像；OSS 删除作为文件服务适配的后续能力，避免新增运行期依赖。
- [Risk] 简历 parsedContent 仍是 JSON 字符串。→ Mitigation: 延续 IPD 字段语义，不在本次引入 JSON 解析依赖；后续诊断/生成可再定义结构化解析 DTO。

## Migration Plan

1. 在 `base-common` 新增简历记录、创建请求、更新请求 DTO。
2. 在 `base-helper` 或现有画像 helper 中新增 resume block 合并/清理能力。
3. 在 `cloud01-app01` 新增 `ResumeStorage`、文件型/内存型适配器和 `ResumeApplicationService`。
4. 新增 `ResumeWebApi`，暴露创建、详情、列表、更新和删除入口。
5. 新增测试覆盖简历记录 CRUD、跨用户访问拒绝、文件型存储重启可读、画像 resume block 写入和删除引用清理。
6. 更新迁移映射表。
7. 运行 OpenSpec 校验和 JDK 8 Gradle 验证。

回滚策略：删除新增 DTO、服务、WebAPI、测试和 `resume-core` 主规格，迁移映射表恢复为“简历基础待迁移”；已有画像和测评能力不受影响。

## Open Questions

- 最终 Cosmic datamodel 是否需要同时保存 parsedContent 原文和结构化字段？
- 文件上传/预览应作为独立 `resume-file-service` change，还是并入后续 `migrate-resume-datamodel`？
- 删除简历后是否需要触发今日任务重新推荐，还是由下一次读取统一画像时自然体现？
