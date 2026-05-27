## Context

`migrate-assessment-core` 已经迁移了测评量表、题目、选项、提交请求、答案快照和评分结果 DTO，并在 `base-helper` 中实现了 JDK 8 兼容的 `AssessmentScoringService`。`integrate-assessment-profile-snapshot` 已经新增 `AssessmentApplicationService`，能够在评分后把测评摘要写入 `UserProfileSnapshot.AssessmentBlock` 并刷新统一职业画像。

当前缺口是应用入口：CyanCruise 还没有测评提交 WebAPI，webapp 或外部调用方无法通过平台 API 使用现有评分和画像写入能力。IPD 源系统通过 Spring `AssessmentController.submit` 调用 `AssessmentService.submitAndScore`，但 CyanCruise 需要按 Cosmic WebAPI 方式重建入口，而不是迁移 Spring Controller、JPA Repository 或 Flyway SQL。

IPD 来源证据：

| 来源 | 迁移参考 |
| --- | --- |
| `F:\Project\IPD\backend\src\main\java\com\group1\career\controller\AssessmentController.java` | `/api/assessments/submit` 的提交语义，答案为 `questionId -> optionId` |
| `F:\Project\IPD\backend\src\main\java\com\group1\career\service\AssessmentService.java` | `submitAndScore(userId, scaleId, answers)` 的应用语义 |
| `F:\Project\IPD\backend\src\main\java\com\group1\career\service\impl\AssessmentServiceImpl.java` | 维度计数、画像生成、测评结果写入画像的业务链路 |
| `F:\Project\IPD\backend\src\main\java\com\group1\career\model\entity\AssessmentScale.java` 等 assessment entity | 量表、题目、选项、记录和答案字段来源 |

## Goals / Non-Goals

**Goals:**

- 新增 Cosmic 风格 `AssessmentWebApi`，暴露测评提交入口。
- 入口接收 `userId`、`AssessmentScaleDto` 和 `AssessmentSubmitRequest`，返回 `AssessmentScoreResult`。
- WebAPI 只负责编排调用，复用 `AssessmentApplicationService.submitAndSaveProfile`。
- 保持 JDK 8 兼容，不新增运行期依赖。
- 增加云应用模块聚焦测试，证明 WebAPI 调用会返回评分结果并把 assessment block 写入画像快照。
- 更新迁移映射表，记录 WebAPI 已接入以及剩余 datamodel、页面、AI 解读工作。

**Non-Goals:**

- 不迁移 IPD 的 Spring Boot `AssessmentController` 注解和安全上下文。
- 不实现量表列表、题目读取、历史记录查询或记录详情 API。
- 不实现最终 Cosmic datamodel 量表/记录持久化。
- 不接入 AI 解读、通知、打卡、微信订阅或管理端题库维护。
- 不创建 webapp 测评页面。

## Decisions

1. 使用单独的 `AssessmentWebApi`

   `CareerProfileWebApi` 已经承担画像与 onboarding 操作，测评属于独立业务能力，应使用 `@ApiController(value = "assessmentWebApi", desc = "职业测评 API")` 和 `/cc001/assessment` 路径。这样后续扩展量表读取、记录查询或题库管理时，不会把画像 API 变成混杂入口。

   备选方案是把提交方法加到 `CareerProfileWebApi`。该方案入口更少，但会模糊“测评能力”和“画像能力”的边界，因此不采用。

2. WebAPI 直接接收量表 DTO，而不是在本次读取 datamodel

   现有评分核心需要完整 `AssessmentScaleDto`，而最终 Cosmic datamodel 还未定义。本次 WebAPI 允许调用方提交量表结构和答案，先打通调用链、评分链和画像写入链。后续 datamodel change 可以新增 `AssessmentScaleSource` 或 repository adapter，由 WebAPI 只接收 `scaleId` 并在服务层读取量表。

   备选方案是本次直接设计并实现测评 datamodel。该方案更接近最终形态，但会扩大范围，且需要同时处理题库、记录、答案持久化和平台数据模型约束，不适合这个补边界切片。

3. 复用 `AssessmentApplicationService`

   WebAPI 不重复评分逻辑，也不直接操作 `CareerProfileApplicationService`。所有“评分后写入画像”的事务语义集中在 `AssessmentApplicationService.submitAndSaveProfile`，保持 helper、应用服务和 WebAPI 的分层。

4. 测试优先验证应用边界行为

   本次测试聚焦 WebAPI 或 WebAPI 同等应用边界：构造内存 `CareerProfileStorage`、测试用 `AssessmentApplicationService`，提交一个小量表后断言结果为 `COMPLETED`，并断言用户画像快照已经出现 assessment block。避免依赖平台容器启动或真实 datamodel。

## Risks / Trade-offs

- [Risk] WebAPI 暂时接收完整量表 DTO，调用方负担高于最终 `scaleId` 模式。→ Mitigation: 在 design 和迁移映射中明确这是 datamodel 前的过渡入口，后续由测评 datamodel change 收敛为服务层读取量表。
- [Risk] 默认 `AssessmentApplicationService` 使用文件型画像存储，在本地和开发环境可用，但不是最终 Cosmic 持久化。→ Mitigation: 沿用既有 `CareerProfileStorage` 边界，后续 datamodel 适配器替换该实现。
- [Risk] 当前 WebAPI 不做当前登录用户解析。→ Mitigation: 本次保持与既有 `CareerProfileWebApi` 风格一致，先接收显式 `userId`；后续安全/登录集成 change 再统一处理当前用户上下文。
- [Risk] 未实现测评历史记录查询。→ Mitigation: 该能力依赖测评记录 datamodel，本次只记录为 non-goal，不在 WebAPI 中伪造历史能力。

## Migration Plan

1. 新增 `AssessmentWebApi`，按现有 `CareerProfileWebApi` 注解风格暴露 `/cc001/assessment/submit`。
2. 如测试需要，允许 `AssessmentWebApi` 通过构造函数注入 `AssessmentApplicationService`，默认构造仍使用生产服务。
3. 新增云应用模块测试，覆盖 WebAPI 提交评分并写入画像快照。
4. 更新 `docs/ipd-to-cyancruise-migration-map.md` 中职业测评状态。
5. 运行 `openspec validate migrate-assessment-webapi --strict`、`openspec validate --all --strict` 和 JDK 8 Gradle 验证。

回滚策略：删除新增 WebAPI 和对应测试，迁移映射表恢复到“待 WebAPI 适配”状态；已有评分核心和画像写入能力不受影响。

## Open Questions

- 最终测评 datamodel 是先迁移量表题库读取，还是同时迁移测评记录和答案持久化？
- 当前用户身份解析应跟随后续统一登录/权限方案，还是在测评 WebAPI 中先单独适配？
