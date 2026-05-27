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
| 简历诊断 | `ResumeDiagnosisController`、`ResumeKeywordService`、`PdfTextExtractor` | 简历文本 + JD 输出匹配和建议 | `code/base`、`code/cloud01/v620-cc001-cloud01-app01` | 完成 JDK 8 DTO、纯 Java 诊断解析、关键词抽取规则、可替换诊断/关键词存储边界、应用服务、Cosmic WebAPI、诊断分数回写画像和聚焦测试；待真实 AI 调用、PDF/OSS 文本解析、通知推送、webapp 页面和最终 Cosmic datamodel | P1 | 简历诊断后端基础已实现：`migrate-resume-diagnosis` |
| 职业计划 | `CareerPlanService`、`CareerController`、`UserCareerPlan` | 按目标岗位和用户状态生成计划摘要 | `datamodel`、`code/base`、`code/cloud01/v620-cc001-cloud01-app01` | 完成 JDK 8 DTO、纯 Java 摘要规则、默认计划、可替换存储边界、应用服务、Cosmic WebAPI、画像 `hasPlan` 和今日行动周重点接入；待 AI 生成、最终 Cosmic datamodel、计划页面和周复盘 | P1 | 职业计划摘要后端基础已实现：`migrate-career-plan-summary` |
| 模拟面试 | `InterviewController`、`InterviewService`、`Interview/InterviewMessage/InterviewQuestion` | 开始面试、对话、结束、报告、历史 | `datamodel`、`code/base`、`code/cloud01/v620-cc001-cloud01-app01`、`webapp` | 完成 JDK 8 DTO、纯 Java helper、会话/消息/报告摘要存储边界、应用服务、Cosmic WebAPI、画像 interview block 同步和聚焦测试；待题库管理、AI 追问/报告生成、语音/身体语言、通知、webapp 页面和最终 Cosmic datamodel | P1 | 模拟面试基础会话与报告已实现：`migrate-interview-core` |
| 助手聊天 | `ChatController`、`ChatHistoryController`、`AiPersonas`、`AssistantSession/Message` | 多角色提示词共用聊天基础设施、会话历史、消息持久化和上下文组装 | `datamodel`、`code/base`、`code/cloud01/v620-cc001-cloud01-app01` | 完成 JDK 8 DTO、纯 Java persona/helper、聊天生成与上下文可替换边界、会话/消息存储边界、应用服务、Cosmic WebAPI 和聚焦测试；待真实 AI、function calling、SSE、长期记忆摘要生成、事实抽取、webapp 页面和最终 Cosmic datamodel | P1 | 助手聊天后端基础已实现：`migrate-assistant-chat` |
| 就业洞察/资源 | `CdutEmploymentInsightService`、`HomepageController`、home 实体 | 提供文章、视频、就业记录和资源搜索 | `datamodel`、`webapp` | 延后，只迁业务主循环需要的内容入口 | P2 | 待迁移 |
| 通知/订阅 | `NotificationService`、`WechatSubscribeService`、`WeeklyReportJob` | 任务通知、周报、微信订阅 | `code/cloud01/v620-cc001-cloud01-app01` | 等核心任务模型稳定后迁移 | P2 | 待迁移 |
| 管理后台 | `AdminController`、`admin-frontend` | 用户、题库、内容、广播、统计、审计 | `webapp`、Cosmic 管理能力 | 按平台能力重新设计 | P2 | 待迁移 |

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
