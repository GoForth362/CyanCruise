# IPD 业务盘点

本文档用于把 `F:\Project\IPD` 中的业务能力抽取为 CyanCruise 的重构输入。迁移时以业务规则、数据语义和用户流程为准，不直接照搬 Spring Boot、Vue、uni-app 的技术实现。

## 项目定位

IPD 项目中的 CareerLoop 是面向学生和应届毕业生的 AI 求职准备工作台。核心目标是让用户每次打开产品时都能知道：

- 当前准备的目标岗位是什么
- 当前最大的短板是什么
- 今天最重要的一件求职准备任务是什么
- 完成任务后能得到什么结果
- 距离投递或面试准备完成还差多少

## 核心业务闭环

```text
目标岗位 -> 用户画像/测评 -> 简历诊断 -> JD 匹配 -> 今日任务 -> 模拟面试 -> 反馈 -> 下一步行动 -> 职业计划
```

后续 CyanCruise 重构应优先保证这个闭环成立。与闭环无关的内容资源、管理后台统计、订阅通知等能力可以后置。

## IPD 技术结构

| 区域 | 本地路径 | 技术形态 | 迁移策略 |
| --- | --- | --- | --- |
| 后端 | `F:\Project\IPD\backend` | Spring Boot 3.5、Java 17、JPA、Flyway | 抽取业务规则、领域对象、接口语义，重写为 Cosmic/JDK 8 兼容实现 |
| 小程序前端 | `F:\Project\IPD\frontend` | uni-app、Vue 3、TypeScript | 抽取页面流程、交互文案、接口契约，按 CyanCruise webapp/Cosmic 页面能力重做 |
| 管理端 | `F:\Project\IPD\admin-frontend` | Vue 3、Element Plus | 抽取后台管理能力，按优先级决定是否重构 |
| 产品交接 | `F:\Project\IPD\AI_PRODUCT_HANDOFF.md` | 产品方向和阶段总结 | 作为业务迁移的主输入之一 |
| 数据迁移 | `F:\Project\IPD\backend\src\main\resources\db\migration` | Flyway SQL | 抽取数据实体、字段和枚举语义，映射到 CyanCruise datamodel |

## 业务能力清单

| 能力 | IPD 关键位置 | 业务价值 | 迁移优先级 |
| --- | --- | --- | --- |
| 用户与登录 | `AuthController`、`UserController`、`UserService` | 识别用户、维护基础资料 | P1 |
| 用户画像 | `AgentProfileService`、`UserProfileSnapshotService`、`UserFactService` | 汇总目标岗位、测评、简历、面试和偏好 | P0 |
| 新用户 Onboarding | `frontend/src/pages/onboarding`、`onboardingSync.ts` | 快速收集身份、目标岗位、简历状态 | P0 |
| AI 今日任务 | `CareerAgentService`、`CareerAgentController`、`AgentTask` | 给出今天最重要的下一步行动 | P0 |
| 职业计划 | `CareerPlanService`、`CareerController`、`UserCareerPlan` | 生成并维护长期求职计划 | P1 |
| 职业测评 | `AssessmentController`、`AssessmentService`、assessment 实体和 SQL | 提供 MBTI/Holland 等测评与结果记录 | P0 |
| 简历管理 | `ResumeController`、`ResumeService`、`Resume` | 上传、保存、查询、删除简历 | P0 |
| 简历诊断/生成 | `ResumeDiagnosisController`、`ResumeGenController` | 结合简历和 JD 输出诊断、生成简历 | P1 |
| 模拟面试 | `InterviewController`、`InterviewService`、interview 实体 | 面试对话、报告、历史记录 | P1 |
| 语音/体态 | `VoiceService`、`BodyLanguageController` | 面试语音和体态评分增强 | P2 |
| 内容资源 | `HomepageController`、`HomeArticle`、`HomeVideo`、`CdutEmploymentInsight` | 资源推荐和就业洞察 | P2 |
| 通知与订阅 | `NotificationService`、`WechatSubscribeService` | 任务提醒、订阅消息 | P2 |
| 管理后台 | `AdminController`、`admin-frontend` | 用户、题库、内容、统计、审计管理 | P2 |

## P0 迁移范围

第一阶段只迁移能构成业务主循环入口的能力：

1. 用户画像基础信息：身份类型、目标岗位、简历状态、关键偏好。
2. Onboarding：新用户进入系统时收集必要信息。
3. 职业测评基础流程：量表、题目、提交、结果。
4. 简历基础流程：上传/保存/查询简历记录。
5. AI 今日任务：基于画像、测评、简历、面试状态输出下一步行动。

## 重构原则

- CyanCruise 必须保持 JDK 1.8 与 Kingdee Cosmic 工程约束。
- 新能力应落在 CyanCruise 既有模块边界内：`datamodel` 定义数据，`code/base` 放通用能力，`code/cloud01` 放业务实现，`webapp` 放页面资源。
- IPD 中 Java 17、Spring Boot、JPA、Flyway、Vue/uni-app 的具体实现不可直接迁入。
- 每个迁移能力必须先形成 OpenSpec change，再实现代码。
- 每个 change 必须说明 IPD 来源、CyanCruise 目标模块、数据映射、验证方式和暂不迁移项。

