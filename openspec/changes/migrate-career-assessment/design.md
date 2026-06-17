## Context

IPD 职业测评通过 Spring Boot/JPA 读取 `assessment_scales`、`assessment_questions`、`assessment_options`，提交时生成 `AssessmentRecord` 与 `AssessmentAnswer`，并把测评摘要合并进用户画像。CyanCruise 已有 JDK 1.8 DTO、纯 Java `AssessmentScoringService`、`AssessmentWebApi.submit` 和画像合并逻辑，但缺少服务端题库、记录持久化和可用前端页面。

本项目必须兼容 Kingdee Cosmic 与 JDK 1.8，不直接迁移 IPD Spring Boot、JPA、Flyway、Vue 或 uni-app 实现，不硬编码本地 ENV 路径，不提交数据库密码。

## Goals / Non-Goals

**Goals:**
- 提供 CyanCruise 服务端可控题库，前端不得传回评分密钥作为可信输入。
- 支持职业测评量表列表、题目读取、提交评分、结果保存和历史/最近结果读取。
- 将测评结果合并进职业画像 `assessment` block，供今日行动、路线规划和画像完整度使用。
- 支持 PostgreSQL 存储，并在初始化开关打开时创建必要表结构。
- 让 `webapp/isv/v620/cyancruise` 的职业测评页面可完成一次 MBTI 测评。

**Non-Goals:**
- 不迁移 IPD 的 Spring Security/JWT、JPA repository、Flyway 版本链和 Java 17 运行时。
- 不迁移微信订阅推送、签到积分、消息通知和真实 AI 个性化解读调用。
- 不在业务代码中写入数据库密码、本地 JDK 路径或 IPD 运行时路径。

## Decisions

1. 服务端内置 MBTI 题库作为第一阶段题库来源。
   - 理由：IPD 当前可确认的种子题库是 `backend\scripts\seed_assessments.sql` 中的 16 题 MBTI；内置 JDK 8 对象可避免在 Cosmic 环境里引入 Flyway/JPA。
   - 备选：直接迁移 IPD SQL 表作为唯一来源。暂缓，因为 CyanCruise 需要先形成可审查、可测试、可降级的最小闭环。

2. 新增 `AssessmentCatalog` 与 `AssessmentResultStorage` 边界。
   - 理由：题库读取和结果保存是两个变化点；内置题库、PostgreSQL 和后续 Cosmic datamodel 适配可以独立替换。
   - 备选：把题库和存储逻辑都放在 WebAPI。拒绝，因为会让 WebAPI 承担业务规则和持久化细节。

3. PostgreSQL 结果表保留结构化列和 JSONB payload。
   - 理由：结构化列便于按用户、量表、时间查询；JSONB 保留完整 `AssessmentScoreResult`、维度计数和答案快照，兼容后续字段扩展。
   - 备选：只保存 JSONB。暂不采用，因为列表和画像联动需要稳定索引字段。

4. 前端提交 `userId + scaleId + answers`，后端按 `scaleId` 获取题库并评分。
   - 理由：评分密钥必须由后端掌握，前端只负责展示和提交。
   - 备选：沿用当前 `scale + request` 入参。保留兼容构造器/方法，但新增正式 WebAPI 用服务端题库。

## Risks / Trade-offs

- [Risk] 内置题库只覆盖 IPD 当前 MBTI 种子，不能表达完整题库运营能力。→ Mitigation：接口和存储边界保留量表/题目/选项模型，后续可接 Cosmic datamodel 或管理后台。
- [Risk] PostgreSQL 配置不完整时生产存储不可用。→ Mitigation：沿用现有 `cc001.storage.*` 配置校验，不写入默认密码；测试可使用内存/文件替代。
- [Risk] 静态 webapp 不是 IPD uni-app 完整交互。→ Mitigation：实现答题、进度、提交、结果展示的核心闭环，保留复杂小程序生命周期为暂不迁移项。
- [Risk] 真实 AI 解读未迁移会使建议岗位较弱。→ Mitigation：先用规则型 MBTI 岗位建议填充 `suggestedRoles`，后续由 AI provider change 扩展。

## Migration Plan

1. 建立 OpenSpec 文档，记录 IPD 来源、目标模块、数据映射、暂不迁移项和验证方式。
2. 增加 DTO 字段、题库、应用服务、PostgreSQL 存储和 WebAPI 读写接口。
3. 更新自定义 WebAPI 路由和 route metadata。
4. 更新静态 webapp 的职业测评交互。
5. 增加 focused 测试，并执行 OpenSpec、Webapp JS/route 和 Gradle 验证。

回滚方式：保留旧 `/cc001/assessment/submit` 兼容调用；若 PostgreSQL 存储不可用，关闭或移除 `cc001.storage.backend=postgresql` 配置即可阻断新持久化，不影响其他已迁移能力。

## Open Questions

- 后续题库运营是否放入 Cosmic datamodel 管理后台，还是继续以配置/脚本维护？
- AI 个性化解读应复用现有 AI provider，还是先作为测评专用能力单独接入？
