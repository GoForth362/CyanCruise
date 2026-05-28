# IPD 到 CyanCruise 迁移映射表

本文档是后续 OpenSpec change 的索引。每个能力迁移前，应先在本表确认 IPD 来源、目标 CyanCruise 模块和优先级。

## 项目映射总览

| 维度 | IPD | CyanCruise |
| --- | --- | --- |
| 后端技术 | Spring Boot 3.5、Java 17、Maven | Kingdee Cosmic、JDK 1.8、Gradle Wrapper |
| 数据演进 | Flyway SQL | `datamodel` 与 Cosmic 数据模型 |
| 用户端 | uni-app 小程序 | `webapp` 与 Cosmic 页面/前端资源 |
| 管理端 | Vue + Element Plus | 视 CyanCruise 平台能力重建 |
| AI 能力 | DashScope、规则编排、服务层调用 | 按 Cosmic 可用服务与外部 AI 接口重写 |
| 构建验证 | Maven/npm | `.\gradlew.bat clean build` |

## 能力迁移映射

| IPD 能力 | IPD 源位置 | 关键业务规则 | CyanCruise 目标位置 | 实现策略 | 优先级 | 状态 |
| --- | --- | --- | --- | --- | --- | --- |
| 业务主循环 | `AI_PRODUCT_HANDOFF.md` | 目标岗位驱动求职准备闭环 | `docs`、`openspec/specs` | 先固化规格，再逐项实现 | P0 | 已建立基线 change |
| 用户画像 | `AgentProfileService`、`UserProfileSnapshotService`、`UserFactService`、`AgentUserProfile` | 汇总身份、目标岗位、测评、简历、面试、计划和偏好 | `datamodel`、`code/base`、`code/cloud01/v620-cc001-cloud01-app01` | 已完成 DTO、helper、应用服务、WebAPI、helper 单元测试和文件型持久化适配；待最终 Cosmic datamodel 适配 | P0 | 后端基础已实现：`migrate-career-profile-onboarding`、`add-career-profile-onboarding-tests`、`prepare-career-profile-persistence-adapter` |
| Onboarding | `frontend/src/pages/onboarding`、`onboardingSync.ts`、用户 profile snapshot API | 收集身份类型、目标岗位、是否已有简历 | `webapp`、`code/base`、`code/cloud01/v620-cc001-cloud01-app01` | 已完成后端保存边界、合并语义、规则测试和文件型持久化适配；待 webapp 页面与最终 Cosmic datamodel 适配 | P0 | 后端基础已实现：`migrate-career-profile-onboarding`、`add-career-profile-onboarding-tests`、`prepare-career-profile-persistence-adapter` |
| AI 今日任务 | `CareerAgentService`、`CareerAgentController`、`AgentTask`、`AgentState` | 根据目标岗位、简历、测评、面试状态生成今日下一步 | `datamodel`、`code/base`、`code/cloud01/v620-cc001-cloud01-app01` | 已完成纯 Java 今日规则、画像输入源、按用户 ID WebAPI 和聚焦测试；待 AgentTask 持久化、风险看板、长期计划联动、当前用户身份解析和 webapp 页面 | P0 | 今日行动推荐已实现：`migrate-today-action-recommendation` |
| 职业测评 | `AssessmentController`、`AssessmentService`、`AssessmentScale/Question/Option/Record/Answer`、`V5/V8/V10` SQL | 量表启用、题目排序、提交答案、生成结果记录 | `datamodel`、`code/base`、`code/cloud01/v620-cc001-cloud01-app01` | 已完成 DTO、纯 Java 评分内核、画像快照写入和 Cosmic WebAPI 提交入口，含 MBTI/非 MBTI 画像规则测试、应用服务持久化测试与 WebAPI 边界测试；待 datamodel、页面和 AI 解读适配 | P0 | 评分核心、画像集成和 WebAPI 接入已实现：`migrate-assessment-core`、`integrate-assessment-profile-snapshot`、`migrate-assessment-webapi` |
| 简历基础 | `ResumeController`、`ResumeService`、`Resume`、`FileService` | 保存简历文件 key、用户简历列表、详情、删除 | `datamodel`、`filestorage`、`code/base`、`code/cloud01/v620-cc001-cloud01-app01` | 已完成 JDK 8 DTO、文件型/内存型存储边界、应用服务、Cosmic WebAPI、画像 resume block 同步和聚焦测试；待最终 Cosmic datamodel、文件上传/预览适配 | P0 | 后端基础已实现：`migrate-resume-core` |
| 文件上传预览 | `FileController`、`FileService`、`FileServiceImpl`、`PdfTextExtractor`、`api/file.ts` | 上传返回 object key、短期预览 URL、认证下载、幂等删除、文本抽取限长 | `code/base`、`code/cloud01/v620-cc001-cloud01-app01`、`webapp` | 已完成文件引用/上传/预览/下载/删除/文本抽取契约、WebAPI、webapp route/API map 和入口卡片；真实 Cosmic 文件服务、OSS SDK、PDFBox/OCR 后续 adapter 适配 | P1 | 文件上传预览基础已实现：`migrate-file-upload-preview` |
| 简历诊断 | `ResumeDiagnosisController`、`ResumeKeywordService`、`PdfTextExtractor` | 简历文本 + JD 输出匹配和建议 | `code/base`、`code/cloud01/v620-cc001-cloud01-app01` | 完成 JDK 8 DTO、纯 Java 诊断解析、关键词抽取规则、可替换诊断/关键词存储边界、应用服务、Cosmic WebAPI、诊断分数回写画像和聚焦测试；待真实 AI 调用、PDF/OSS 文本解析、通知推送、webapp 页面和最终 Cosmic datamodel | P1 | 简历诊断后端基础已实现：`migrate-resume-diagnosis` |
| 职业计划 | `CareerPlanService`、`CareerController`、`UserCareerPlan` | 按目标岗位和用户状态生成计划摘要 | `datamodel`、`code/base`、`code/cloud01/v620-cc001-cloud01-app01` | 完成 JDK 8 DTO、纯 Java 摘要规则、默认计划、可替换存储边界、应用服务、Cosmic WebAPI、画像 `hasPlan` 和今日行动周重点接入；待 AI 生成、最终 Cosmic datamodel、计划页面和周复盘 | P1 | 职业计划摘要后端基础已实现：`migrate-career-plan-summary` |
| 模拟面试 | `InterviewController`、`InterviewService`、`Interview/InterviewMessage/InterviewQuestion` | 开始面试、对话、结束、报告、历史 | `datamodel`、`code/base`、`code/cloud01/v620-cc001-cloud01-app01`、`webapp` | 完成 JDK 8 DTO、纯 Java helper、会话/消息/报告摘要存储边界、应用服务、Cosmic WebAPI、画像 interview block 同步和聚焦测试；待题库管理、AI 追问/报告生成、语音/身体语言、通知、webapp 页面和最终 Cosmic datamodel | P1 | 模拟面试基础会话与报告已实现：`migrate-interview-core` |
| 助手聊天 | `ChatController`、`ChatHistoryController`、`AiPersonas`、`AssistantSession/Message` | 多角色提示词共用聊天基础设施、会话历史、消息持久化和上下文组装 | `datamodel`、`code/base`、`code/cloud01/v620-cc001-cloud01-app01` | 完成 JDK 8 DTO、纯 Java persona/helper、聊天生成与上下文可替换边界、会话/消息存储边界、应用服务、Cosmic WebAPI 和聚焦测试；待真实 AI、function calling、SSE、长期记忆摘要生成、事实抽取、webapp 页面和最终 Cosmic datamodel | P1 | 助手聊天后端基础已实现：`migrate-assistant-chat` |
| 就业洞察/资源 | `CdutEmploymentInsightService`、`HomepageController`、home 实体 | 提供文章、视频、就业记录和资源搜索 | `code/base`、`code/cloud01/v620-cc001-cloud01-app01`、`webapp` | 迁移主循环需要的只读就业洞察、来源覆盖审计和资源卡片入口；外部抓取/全文详情/运营后台后续平台适配 | P2 | 后端契约与 webapp 入口迁移中：`migrate-employment-insights-resources` |
| 通知/订阅 | `NotificationService`、`WechatSubscribeService`、`WeeklyReportJob` | 任务通知、周报、微信订阅 | `code/base`、`code/cloud01/v620-cc001-cloud01-app01`、`webapp` | 迁移站内通知、未读/已读/删除、订阅授权配额和周报通知契约；真实微信发送/调度后续平台适配 | P2 | 迁移中：`migrate-notifications-subscriptions` |
| 管理后台 | `AdminController`、`QuestionBankController`、`AdminAuthService`、`AdminAuditAspect`、`admin-frontend` | 用户、题库、内容、广播、统计、审计 | `code/base`、`code/cloud01/v620-cc001-cloud01-app01`、`webapp`、Cosmic 管理能力 | 已完成管理治理契约、JDK 8 DTO/helper、应用服务、Cosmic WebAPI、审计边界和 route/API 映射；旧 Vue/Spring 管线不直接迁移 | P2 | 管理后台治理基础已实现：`migrate-admin-console-governance` |
| Cosmic 平台挂载 | `frontend/src/pages.json`、`App.vue`、`utils/auth.ts`、`utils/request.ts`、`api/user.ts`、`admin-frontend/src/router/index.ts`、`admin-frontend/src/api/index.ts` | 页面入口、登录守卫、请求身份、管理入口和权限意图 | `webapp/isv/v620/careerloop`、`docs`、`openspec/specs/cosmic-platform-mounting/spec.md` | 已收拢 CareerLoop webapp 平台挂载清单、生产 Cosmic 登录上下文、开发 fallback、菜单/KDDT 发布、WebAPI 调用边界和部署核查；旧 uni-app/Vue/axios/JWT 不直接迁移 | P1 | 平台挂载契约已实现：`migrate-cosmic-platform-mounting` |
| Cosmic 身份上下文 | `frontend/src/utils/auth.ts`、`utils/request.ts`、`api/user.ts`、`App.vue`、`admin-frontend/src/api/index.ts`、后端 auth/admin 语义 | 登录态、guest/真实用户区分、请求身份、401 处理、管理员 whoami 和角色意图 | `code/base`、`code/cloud01/v620-cc001-cloud01-app01`、`webapp/isv/v620/careerloop`、`docs` | 迁移 Cosmic 身份上下文 DTO/helper/resolver、开发 fallback 隔离、显式 userId/adminId 一致性校验、管理员角色归一和代表性 WebAPI 身份边界；旧 JWT/axios/uni-app storage/Spring Security 不直接迁移 | P1 | 迁移中：`migrate-cosmic-identity-context` |

## 推荐 change 顺序

1. `ipd-business-baseline`：固化 IPD 业务闭环、迁移边界和治理规则。
2. `migrate-career-profile-onboarding`：职业画像与新用户 onboarding。
3. `migrate-assessment-core`：职业测评量表、题目、提交和结果。
4. `migrate-resume-core`：简历文件与简历记录基础能力。
5. `migrate-today-action-recommendation`：AI 今日任务规则。
6. `migrate-career-plan-summary`：职业计划摘要。
7. `migrate-interview-core`：模拟面试基础会话与报告。
8. `migrate-resume-diagnosis`：简历诊断后端基础。
9. `migrate-assistant-chat`：多角色助手聊天、会话历史与上下文组装。

## 验证基线

每个实现型 change 至少应完成：

- OpenSpec 校验：`openspec validate <change-id> --strict`
- Gradle 构建：设置 JDK 8 后执行 `.\gradlew.bat clean build`
- 迁移映射更新：本表中的状态和目标模块必须同步更新
- 来源说明：change 的 design 或 tasks 中必须引用 IPD 源文件路径

## Cosmic datamodel 正式适配

| 维度 | 内容 |
| --- | --- |
| change | `migrate-cosmic-datamodel-adapters` |
| branch | `codex/migrate-cosmic-datamodel-adapters` |
| IPD 来源 | `F:\Project\IPD\backend\src\main\java\com\group1\career\model\entity\AgentUserProfile.java`、`AssessmentRecord.java`、`Resume.java`、`AgentTask.java`、`UserCareerPlan.java`、`Interview.java`、`InterviewMessage.java`、`AssistantSession.java`、`AssistantMessage.java`，以及 `F:\Project\IPD\backend\src\main\resources\db\migration\` 的字段语义 |
| CyanCruise 目标 | `datamodel/careerloop-datamodel-map.md`、`code/cloud01/v620-cc001-cloud01-app01` 的 `Cosmic*Storage` adapter、`openspec/specs/cosmic-datamodel-adapters/spec.md` |
| 数据映射 | 画像、测评、简历、今日行动任务、职业计划、模拟面试、简历诊断摘要和助手聊天均通过结构化用户归属、状态、时间、排序、父子关系字段建模；AI 输出、证据、评分维度、报告和计划明细保留 JSON 文本字段 |
| 暂不迁移 | Spring Boot、JPA、Flyway、Lombok、repository、Vue、uni-app、真实 AI SDK、生产数据补偿脚本和页面实现 |
| 验证方式 | datamodel 字段映射 contract test、storage adapter 聚焦测试、OpenSpec 严格校验、JDK 8 Gradle 测试和完整构建 |

## AI 基础设施接入

| 维度 | 内容 |
| --- | --- |
| change | `migrate-ai-infrastructure` |
| branch | `codex/migrate-ai-infrastructure` |
| IPD 来源 | `F:\Project\IPD\backend\src\main\java\com\group1\career\service\AiService.java`、`impl\AiServiceImpl.java`、`service\ai\FunctionCallingService*.java`、`service\ai\tools\`、`ConversationSummaryServiceImpl.java`、`CareerPlanServiceImpl.java`、`TaskDecomposerImpl.java`、`InterviewServiceImpl.java`、`VoiceServiceImpl.java` |
| CyanCruise 目标 | `code/base/v620-cc001-base-common` 的 AI DTO、`code/base/v620-cc001-base-helper` 的 AI helper、`code/cloud01/v620-cc001-cloud01-app01` 的 AI gateway/provider 与 CareerLoop 场景 adapter、`openspec/specs/ai-infrastructure/spec.md` |
| 迁移内容 | provider-neutral AI gateway、message/request/response/usage/tool/stream DTO、默认 system prompt 注入、JSON 提取与校验、function calling 服务端 userId 注入、调用上限、未配置 provider 降级、助手聊天/职业计划/简历诊断/任务拆解/面试/长期记忆接线点 |
| 暂不迁移 | Spring Boot Controller、Spring `SseEmitter`、Java 17 `HttpClient`、JPA/Flyway、DashScope SDK 专有对象、语音 ASR/TTS、Vue/uni-app 页面、生产密钥配置和真实 provider 网络调用 |
| 验证方式 | AI helper 聚焦测试、gateway/provider 聚焦测试、function calling 安全测试、stream event 测试、OpenSpec 严格校验、JDK 8 Gradle 测试和完整构建 |

## Webapp CareerLoop 入口

| 维度 | 内容 |
| --- | --- |
| change | `migrate-webapp-careerloop-entry` |
| branch | `codex/migrate-webapp-careerloop-entry` |
| IPD 来源 | `F:\Project\IPD\frontend\src\pages.json`、`pages\home\index.vue`、`pages\onboarding\index.vue`、`pages\agent\index.vue`、`pages\assessment\*`、`pages\resume\*`、`pages\resume-ai\index.vue`、`pages\interview\*`、`pages\assistant\*`、`utils\onboardingGate.ts`、`utils\onboardingSync.ts`、`api\agent.ts`、`api\assessment.ts`、`api\career.ts`、`api\resume.ts`、`api\interview.ts`、`api\ai.ts` |
| CyanCruise 目标 | `webapp/isv/v620/careerloop/` 的苍穹 webapp 静态入口、`careerloop-routes.json` 路由/API 契约地图、`openspec/specs/webapp-careerloop-entry/spec.md` |
| 数据/接口映射 | 工作台消费 `/cc001/career-profile/snapshot/get` 和 `/cc001/career-agent/today/get`；onboarding 调用 `/cc001/career-profile/onboarding/save`；主入口映射测评、简历、简历诊断、职业计划、模拟面试和助手聊天 WebAPI；开发/验证态 userId 来自 query/localStorage/页面输入，生产态等待苍穹登录上下文接入 |
| 迁移内容 | 重建 CareerLoop 首个 webapp 工作台入口，提供目标岗位/画像状态、准备度、今日行动、onboarding gate、主循环入口、pending 能力提示、响应式布局和可检查 workflow 视觉资产 |
| 暂不迁移 | IPD Vue/uni-app 源码、Pinia/store、Vite/uView、小程序 tabBar、消息中心、微信订阅、CDUT 就业详情、管理后台、文件上传预览、语音/数字人面试、生产登录态和前端流式聊天 |
| 验证方式 | OpenSpec 严格校验、webapp 静态资源/route-map 检查、JDK 8 `.\gradlew.bat clean build` |

## Cosmic 平台挂载

| 维度 | 内容 |
| --- | --- |
| change | `migrate-cosmic-platform-mounting` |
| branch | `codex/migrate-cosmic-platform-mounting` |
| IPD 来源 | `F:\Project\IPD\frontend\src\pages.json`、`App.vue`、`utils\auth.ts`、`utils\request.ts`、`api\user.ts`、`F:\Project\IPD\admin-frontend\src\router\index.ts`、`src\api\index.ts` |
| CyanCruise 目标 | `webapp/isv/v620/careerloop/careerloop-routes.json` 的 `identity`、`sourceEvidence`、`platformMounts`，`webapp/isv/v620/careerloop/assets/app.js` 的平台身份模式，`docs/careerloop-cosmic-platform-mounting.md`，`openspec/specs/cosmic-platform-mounting/spec.md` |
| 数据/接口映射 | 生产态用户身份映射为 Cosmic 登录上下文或平台 adapter 提供的 `userId`；管理员身份映射为 `adminId` 与 `ADMIN`/平台管理员角色；开发态 query/localStorage/manual userId 显式标记为 `identityMode=development`；菜单/KDDT 挂载通过 `platformMounts` 记录 route key、target、audience、requiredRole、publishability、fallback 和 deploymentNotes；WebAPI 仍以 `/cc001/*` route/API map 为准 |
| 迁移内容 | 平台挂载清单、IPD 来源证据、production/development 身份模式、静态入口身份解析约束、管理员入口权限提示、route/mount 静态校验、Cosmic webapp/KDDT/菜单部署说明和租户手工核查清单 |
| 暂不迁移 | IPD uni-app/Vue router/tabBar、Pinia/store、Vite/uView、axios/JWT 登录、微信登录、Spring Boot auth、JPA/Flyway、生产 SSO/RBAC 规则、客户环境 KDDT 发布脚本、真实 Cosmic 文件服务 adapter、最终 datamodel/权限接入和小程序原生能力 |
| 验证方式 | `node webapp\isv\v620\careerloop\validate-routes.js`、`node --check webapp\isv\v620\careerloop\assets\app.js`、OpenSpec 严格校验、JDK 8 `.\gradlew.bat clean build` |

## Cosmic 身份上下文

| 维度 | 内容 |
| --- | --- |
| change | `migrate-cosmic-identity-context` |
| branch | `codex/migrate-cosmic-identity-context` |
| IPD 来源 | `F:\Project\IPD\frontend\src\utils\auth.ts`、`utils\request.ts`、`api\user.ts`、`App.vue`、`F:\Project\IPD\admin-frontend\src\api\index.ts`，以及后端 auth/admin controller/service 语义 |
| CyanCruise 目标 | `code/base/v620-cc001-base-common` 的 Cosmic 身份 DTO/常量、`code/base/v620-cc001-base-helper` 的身份 helper、`code/cloud01/v620-cc001-cloud01-app01` 的 `CareerLoopIdentityResolver` 和 `IdentityAwareCareerLoopWebApiBoundary`、`webapp/isv/v620/careerloop/careerloop-routes.json` 身份元数据、`openspec/specs/cosmic-identity-context/spec.md` |
| 数据/接口映射 | IPD 真实用户/guest/token/admin whoami 语义映射为 `CosmicIdentityContextDto` 的 userId、adminId、roles、source、environment、status；生产态缺平台身份返回 `IDENTITY_REQUIRED`；开发态显式标记 `development-fallback`；请求体 userId/adminId 与平台身份冲突返回 `IDENTITY_MISMATCH` 或等价 forbidden；管理员角色归一为 `ADMIN`、`COSMIC_ADMIN`、`PLATFORM_ADMIN` |
| 迁移内容 | JDK 8 身份 DTO/常量、身份 helper、helper 聚焦测试、默认不可用生产 resolver、开发/test resolver、WebAPI 身份边界 helper、代表性 profile/today/resume/admin WebAPI 身份校验和边界测试 |
| 暂不迁移 | IPD JWT token 签发/校验、微信登录、注册/重置密码、uni-app storage、axios 拦截器、Spring Security、生产 SSO/RBAC、客户 Cosmic 登录 API 最终字段适配和全量 `/cc001/*` 接口改造 |
| 验证方式 | 身份 helper/WebAPI 边界聚焦测试、`node webapp\isv\v620\careerloop\validate-routes.js`、OpenSpec 严格校验、JDK 8 `.\gradlew.bat clean build` |

## 就业洞察/资源

| 维度 | 内容 |
| --- | --- |
| change | `migrate-employment-insights-resources` |
| branch | `codex/migrate-employment-insights-resources` |
| IPD 来源 | `F:\Project\IPD\backend\src\main\java\com\group1\career\controller\CdutEmploymentInsightController.java`、`service\CdutEmploymentInsightService.java`、`service\impl\CdutEmploymentInsightServiceImpl.java`、`model\dto\CdutEmploymentInsightDto.java`、`model\entity\CdutEmploymentRecord.java`、`db\migration\V16__cdut_employment_insights.sql`、`V20__employment_records_school.sql`、`controller\HomepageController.java`、`model\entity\HomeArticle.java`、`HomeVideo.java`、`HomeConsultation.java`、`model\dto\HomeConsultationFeedDto.java`、`F:\Project\IPD\frontend\src\api\cdutEmployment.ts`、`api\home.ts`、`pages\cdut-employment\index.vue`、`pages\cdut-employment\detail.vue`、`pages\home\index.vue` |
| CyanCruise 目标 | `code/base/v620-cc001-base-common` 的就业洞察/资源 DTO、`code/base/v620-cc001-base-helper` 的匹配/覆盖审计/helper 规则、`code/cloud01/v620-cc001-cloud01-app01` 的应用服务、存储边界和 Cosmic WebAPI、`webapp/isv/v620/careerloop` 的 route/API 映射与入口卡片、`openspec/specs/employment-insights-resources/spec.md` |
| 数据/接口映射 | 用户画像中的 school、major、targetRole 映射到 `EmploymentInsightProfileContext`；公开就业来源映射到 `EmploymentInsightRecordDto`，保留 school、year、sourceTitle、sourceUrl、sourceType、majorKeyword、careerKeyword、employmentRate、postgraduateRate、destinationSummary、rawExcerpt、fetchedAt；输出 `EmploymentInsightDto` 的 status、summary、latest metrics、trend、coverage、sources；资源文章/视频/咨询/职业路径统一映射为 `CareerResourceCardDto` 和 `CareerResourceFeedDto`；WebAPI 为 `/cc001/career-employment/insight/get` 与 `/cc001/career-employment/resources/list` |
| 迁移内容 | JDK 8 DTO、支持学校归一、来源评分、趋势聚合、覆盖审计、无学校/未支持学校/无指标/缺目标岗位降级、资源卡片 feed、只读应用服务、内存型可替换存储边界、Cosmic WebAPI、webapp route/API map 和静态入口卡片 |
| 暂不迁移 | Spring Boot Controller、JPA repository、Flyway SQL、Java 17 `HttpClient`、PDFBox、Redis、Bilibili 抓取、外部刷新任务、图片代理、Vue/uni-app 页面、Pinia/store、小程序 web-view、全文内容详情、生产内容运营后台和真实外部抓取调度 |
| 验证方式 | helper 聚焦测试、应用服务/WebAPI 聚焦测试、`node webapp\isv\v620\careerloop\validate-routes.js`、`node --check webapp\isv\v620\careerloop\assets\app.js`、OpenSpec 严格校验、JDK 8 `.\gradlew.bat clean build` |

## 通知/订阅

| 维度 | 内容 |
| --- | --- |
| change | `migrate-notifications-subscriptions` |
| branch | `codex/migrate-notifications-subscriptions` |
| IPD 来源 | `F:\Project\IPD\backend\src\main\java\com\group1\career\controller\NotificationController.java`、`WechatSubscribeController.java`、`model\NotificationTypes.java`、`model\entity\Notification.java`、`WxSubscribeQuota.java`、`repository\NotificationRepository.java`、`WxSubscribeQuotaRepository.java`、`service\NotificationService.java`、`service\impl\NotificationServiceImpl.java`、`service\WechatSubscribeService.java`、`service\impl\WechatSubscribeServiceImpl.java`、`service\WeeklyReportService.java`、`service\WeeklyReportJob.java`、`F:\Project\IPD\frontend\src\api\notification.ts`、`utils\wxSubscribe.ts`、`pages\messages\index.vue` |
| CyanCruise 目标 | `code/base/v620-cc001-base-common` 的通知/订阅 DTO 与类型常量、`code/base/v620-cc001-base-helper` 的分类/未读/配额/周报 helper、`code/cloud01/v620-cc001-cloud01-app01` 的应用服务、存储边界、不可用发送 adapter 和 Cosmic WebAPI、`webapp/isv/v620/careerloop` 的消息中心 route/API 映射、`openspec/specs/notifications-subscriptions/spec.md` |
| 数据/接口映射 | `Notification` 映射为 `NotificationRecordDto`，保留 notificationId、userId、type、title、content、link、readFlag、createdAt，并补充分组/标签/iconKey；`WxSubscribeQuota` 映射为 `SubscriptionQuotaDto`；授权结果映射为 `SubscriptionGrantRequest`；发送边界映射为 `SubscriptionSendRequest/Result`；周报输出映射为 `WeeklyReportSummaryDto`；WebAPI 为 `/cc001/notifications/list`、`/unread-count`、`/push`、`/read`、`/read-all`、`/delete`、`/subscription/grant`、`/subscription/quota`、`/subscription/send`、`/weekly-report/run` |
| 迁移内容 | JDK 8 DTO、通知类型归一、消息中心分组、未读聚合、所有权校验、best-effort push、订阅授权配额、发送前安全跳过、不可用外部 provider、周报 fallback 摘要、应用服务、内存型可替换存储边界、Cosmic WebAPI、webapp route/API map 和消息中心入口 |
| 暂不迁移 | Spring Boot Controller、JPA repository、Flyway SQL、Redis token 缓存、Java 17 `HttpClient`、真实微信 access token/模板消息网络调用、生产 appid/secret、Spring `@Scheduled`、Vue/uni-app 消息页、小程序 tabbar、Pinia/store、`wx.requestSubscribeMessage` 运行时和生产调度 |
| 验证方式 | helper 聚焦测试、应用服务/WebAPI 聚焦测试、`node webapp\isv\v620\careerloop\validate-routes.js`、`node --check webapp\isv\v620\careerloop\assets\app.js`、OpenSpec 严格校验、JDK 8 `.\gradlew.bat clean build` |

## 管理后台治理

| 维度 | 内容 |
| --- | --- |
| change | `migrate-admin-console-governance` |
| branch | `codex/migrate-admin-console-governance` |
| IPD 来源 | `F:\Project\IPD\backend\src\main\java\com\group1\career\controller\AdminController.java`、`QuestionBankController.java`、`service\AdminAuthService.java`、`service\QuestionBankService.java`、`service\ContentSafetyService.java`、`service\impl\ContentSafetyServiceImpl.java`、`aspect\AdminAuditAspect.java`、`aspect\AuditLog.java`、`model\entity\AdminAuditLog.java`、`F:\Project\IPD\admin-frontend\src\router\index.ts`、`src\api\index.ts`、`src\views\Dashboard.vue`、`Students.vue`、`Users.vue`、`QuestionBank.vue`、`ContentManager.vue`、`Broadcast.vue`、`Analytics.vue`、`AuditLog.vue` |
| CyanCruise 目标 | `code/base/v620-cc001-base-common` 的管理 DTO/常量/分页契约、`code/base/v620-cc001-base-helper` 的权限/治理/看板/内容安全/审计 helper、`code/cloud01/v620-cc001-cloud01-app01` 的管理应用服务、存储边界、内存适配和 Cosmic WebAPI、`webapp/isv/v620/careerloop` 的 admin route/API 映射、`openspec/specs/admin-console-governance/spec.md` |
| 数据/接口映射 | IPD `ADMIN` 角色校验映射为 `AdminIdentityDto` 和 storage/platform adapter；组织、学生、用户、职业路径、职业节点、题库、内容、广播、统计和审计映射为 `Admin*Dto`；面试报告 radar JSON 映射为容错聚合的 `AdminOrgDashboardDto`；用户封禁/解封和广播复用通知 `ADMIN_BROADCAST` best-effort 投递；WebAPI 使用 `/cc001/admin/*` 路径覆盖 whoami、组织、学生、用户治理、题库审核、内容管理、广播、统计和审计日志 |
| 迁移内容 | JDK 8 DTO、管理员身份校验结果、分页边界、封禁/解封规则、题库审核状态转换、用户贡献题内容安全/匿名 hash/难度归一、内容置顶/隐藏、组织看板雷达聚合、弱项排序、审计快照脱敏、管理应用服务、内存型可替换存储边界、Cosmic WebAPI、webapp route/API map 和管理入口挂载契约 |
| 暂不迁移 | Spring Boot Controller、JPA repository、Flyway SQL、AOP 运行时、Lombok builder、旧 JWT 管线、Vue、Element Plus、Vite、Pinia、axios 拦截器、admin-frontend 页面布局、生产级 RBAC/SSO、苍穹菜单权限配置和真实 Cosmic 管理菜单发布 |
| 验证方式 | helper 聚焦测试、应用服务/WebAPI 聚焦测试、`node webapp\isv\v620\careerloop\validate-routes.js`、`node --check webapp\isv\v620\careerloop\assets\app.js`、OpenSpec 严格校验、JDK 8 `.\gradlew.bat clean build` |

## 文件上传预览

| 维度 | 内容 |
| --- | --- |
| change | `migrate-file-upload-preview` |
| branch | `codex/migrate-file-upload-preview` |
| IPD 来源 | `F:\Project\IPD\backend\src\main\java\com\group1\career\controller\FileController.java`、`service\FileService.java`、`service\impl\FileServiceImpl.java`、`config\OssConfigProperties.java`、`utils\PdfTextExtractor.java`、`backend\src\test\java\com\group1\career\controller\FileControllerTest.java`、`service\impl\FileServiceTest.java`、`F:\Project\IPD\frontend\src\api\file.ts`、`F:\Project\IPD\backend\sql\2026_04_oss_url_to_key.sql` |
| CyanCruise 目标 | `code/base/v620-cc001-base-common` 的文件 DTO/状态常量、`code/base/v620-cc001-base-helper` 的 key/url/TTL/text helper、`code/cloud01/v620-cc001-cloud01-app01` 的文件应用服务、存储 adapter、文本抽取 adapter 和 Cosmic WebAPI、`webapp/isv/v620/careerloop` 的文件 route/API 映射、`openspec/specs/file-upload-preview/spec.md` |
| 数据/接口映射 | IPD 上传返回 bare object key 映射为 `FileReferenceDto.objectKey`；folder 空值映射为 `others`；旧 OSS URL 和 presigned URL 通过 helper 归一为 object key；预览 URL 映射为 `FilePreviewUrlResult` 并 clamp TTL 到 `[60, 86400]`；下载、删除和文本抽取映射为 `FileDownloadResult`、`FileDeleteResult`、`FileTextExtractionResult`；WebAPI 使用 `/cc001/files/upload`、`/preview-url`、`/download`、`/delete`、`/extract-text` |
| 迁移内容 | JDK 8 DTO、folder/key/url 归一、扩展名保留、空文件拒绝、TTL clamp、文本限长 20000 字、内存型可替换文件存储边界、纯文本默认抽取 adapter、Cosmic WebAPI、webapp route/API map 和静态入口卡片 |
| 暂不迁移 | Spring Multipart、Aliyun OSS SDK、PDFBox、Flyway SQL、Java 17 `readAllBytes`、真实生产密钥、CDN 策略、病毒扫描、OCR、Office 在线预览、Vue/uni-app 上传运行时和小程序文件选择 UI |
| 验证方式 | helper 聚焦测试、应用服务/WebAPI 聚焦测试、`node webapp\isv\v620\careerloop\validate-routes.js`、`node --check webapp\isv\v620\careerloop\assets\app.js`、OpenSpec 严格校验、JDK 8 `.\gradlew.bat clean build` |
