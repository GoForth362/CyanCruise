## Context

CyanCruise 已完成简历元数据、简历诊断、webapp 工作台和管理后台治理契约，但文件本身仍缺少统一平台边界。IPD 文件能力由 `FileController` 提供上传入口，由 `FileServiceImpl` 通过 Aliyun OSS 上传、下载、签名预览和删除，由 `PdfTextExtractor` 从下载后的 PDF bytes 中提取文本，前端 `api/file.ts` 明确约定后端返回 bare object key。

本次迁移目标是建立 Cosmic/JDK 8 兼容的文件契约和可替换边界。IPD 中 Spring Multipart、Aliyun OSS SDK、PDFBox、Flyway URL 迁移 SQL、uni-app 上传实现和生产密钥配置不直接迁移。CyanCruise 后续实现 SHALL 优先接 Cosmic 文件服务或平台文件存储 adapter；在无平台文件服务时，测试 adapter 只证明业务规则。

## Goals / Non-Goals

**Goals:**

- 定义 JDK 8 兼容的文件 DTO：上传请求、上传结果、文件引用、预览 URL、下载结果、删除结果、文本抽取结果和错误状态。
- 提供纯 Java helper 规则：folder 默认值/清洗、文件名扩展名保留、object key 归一、旧 URL 到 key 转换、query 剥离、TTL clamp、空引用处理、最大文本长度截断和安全文件类型提示。
- 提供 app01 文件应用服务、文件存储 adapter、文本抽取 adapter 和 Cosmic WebAPI。
- 支持 webapp 消费：上传文件、按 key 请求预览 URL、下载 bytes/文本、删除对象和简历诊断读取文本。
- 更新迁移地图和验证流程，确保实现阶段可通过 OpenSpec、聚焦测试、route 静态检查和 JDK 8 Gradle 构建。

**Non-Goals:**

- 不在 propose 阶段实现代码。
- 不直接迁移 IPD Spring `MultipartFile`、Aliyun OSS SDK、PDFBox、Java 17 `InputStream.readAllBytes`、Flyway SQL 或真实生产密钥配置。
- 不迁移 IPD uni-app 上传运行时或小程序文件选择 UI；webapp 只维护 route/API 映射和可挂载契约。
- 不在本 change 实现完整文件内容安全、病毒扫描、OCR、在线 Office 预览或 CDN 策略。
- 不改变既有 `resume-core` 保存简历元数据的主契约；文件能力作为可复用边界被后续接入。

## Decisions

### 1. 持久引用使用 object key，预览 URL 即时生成

文件上传成功 SHALL 返回 bare object key；业务记录 SHALL 保存 key。预览、头像展示或下载需要浏览器访问时，应用服务 SHALL 根据 key 生成短期访问 URL 或平台临时 URL。

原因：IPD 已经从长期 OSS URL 迁移到 key 语义，避免数据库和 AI 管线耦合 bucket、endpoint、CDN 或签名参数。

替代方案是上传后保存绝对 URL；这会导致 endpoint 更换、签名过期和 bucket 策略变化时数据失效，不采用。

### 2. 文件存储以 adapter 表达，不绑定 Aliyun OSS

`app01` SHALL 定义 `CareerFileStorage` 类似边界，支持 upload、download、presign、delete。默认测试 adapter 可用内存或本地文件模拟；生产 adapter 后续接 Cosmic 文件服务或经批准的对象存储。

原因：CyanCruise 是 Kingdee Cosmic 二开工程，不应把 IPD 的 Aliyun OSS SDK 和配置模型直接带入业务模块。

替代方案是复制 `FileServiceImpl`；这会引入 SDK、密钥、endpoint 和 Spring 配置依赖，不采用。

### 3. URL 到 key 归一必须兼容旧数据

helper SHALL 接受 bare key、带前导斜杠 key、旧 `https://host/path/key?signature=...` URL，并输出 object key。Malformed URL SHALL 返回明确错误或失败状态。

原因：IPD 有 `2026_04_oss_url_to_key.sql`，说明历史数据存在 URL 到 key 的迁移需求；CyanCruise 需要兼容旧引用。

替代方案是只接受新 key；这会让导入或历史数据预览失败，不采用。

### 4. 预览 TTL clamp 到安全窗口

预览 URL TTL SHALL clamp 到 `[60, 86400]` 秒。空 key SHALL 返回空预览结果；签名失败 SHALL 返回 unavailable/skipped 状态，不应破坏简历列表或工作台主流程。

原因：过短 TTL 会立即失效，过长 TTL 会扩大私有文件暴露窗口。

### 5. 文本抽取限长并失败降级

文本抽取 SHALL 限制输出最大字符数，默认沿用 IPD 的 20000 字语义；抽取失败 SHALL 返回空文本、状态和原因。PDFBox 或平台 OCR 能力 SHALL 通过 adapter 接入，不成为 helper 依赖。

原因：简历诊断和 AI 上下文都有 token/性能边界，坏 PDF 不能拖垮主流程。

## Risks / Trade-offs

- [Risk] Cosmic 文件服务接口尚未确定 -> Mitigation：先定义 storage adapter 和 WebAPI 契约，生产 adapter 后续替换。
- [Risk] 首轮不实现真实 PDFBox 文本抽取 -> Mitigation：文本抽取 adapter 返回明确状态，测试覆盖限长和失败降级。
- [Risk] 文件上传 UI 与平台上传控件差异较大 -> Mitigation：webapp 只记录 API 映射和挂载契约，不复制 uni-app 上传页面。
- [Risk] 文件安全策略不足 -> Mitigation：首轮校验空文件、folder、扩展名和大小提示；病毒扫描/内容安全作为后续平台能力。

## Migration Plan

1. 新增文件 DTO、状态常量和 helper 规则。
2. 新增 app01 文件存储 adapter、文本抽取 adapter 和应用服务。
3. 新增 Cosmic WebAPI：上传边界、预览 URL、下载、删除、文本抽取。
4. 更新 webapp route/API 映射，将文件上传预览从 pending 转为 entry-only/available 契约。
5. 更新迁移地图，运行 OpenSpec、helper/service/WebAPI 测试、route 静态检查和 JDK 8 Gradle 构建。

Rollback：文件上传预览是新增能力。若生产 adapter 不可用，简历元数据和用户侧工作台仍可显示；上传和预览入口 SHALL 返回 unavailable 状态，不影响既有简历列表、诊断记录或管理后台。

## Open Questions

- 生产态文件存储最终使用 Cosmic 附件服务、苍穹文件服务、私有对象存储还是混合 adapter，需要 apply 或后续 change 明确。
- 文件大小、允许扩展名和下载鉴权是否由平台统一策略控制，需要与部署环境对齐。
- 简历诊断是否在本 change 直接读取文件文本，还是只暴露文本抽取 API 后由后续页面/服务接入，需要实现阶段按现有代码边界确认。
