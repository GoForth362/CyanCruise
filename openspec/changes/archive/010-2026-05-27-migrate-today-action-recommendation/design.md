## Context

CyanCruise 已经具备 CareerLoop 的关键输入：职业画像/onboarding、测评提交、简历基础记录和画像快照同步。仓库中也已有早期迁移的 `CareerAgentTodayRuleService`、`CareerAgentRuleInput`、`CareerAgentTodayDto`、`CareerAgentTodayApplicationService` 和 `CareerAgentWebApi`。这些代码验证了 IPD `CareerAgentServiceImpl#getToday` 的纯规则决策链，但尚未形成 OpenSpec 主规格，也缺少基于当前画像存储按用户 ID 组装规则输入的正式实现。

IPD 今日任务完整能力还包含 bundle、风险看板、长期计划、AgentTask 持久化、任务完成/忽略、事件、状态刷新和 LLM reason。本次只迁移“今日行动推荐规则”和应用边界，不把自主 agent 和任务生命周期一起搬入。

IPD 来源证据：

| 来源 | 迁移参考 |
| --- | --- |
| `F:\Project\IPD\backend\src\main\java\com\group1\career\service\CareerAgentService.java` | `getToday`、bundle、风险、计划、任务等边界拆分 |
| `F:\Project\IPD\backend\src\main\java\com\group1\career\service\impl\CareerAgentServiceImpl.java` | 今日推荐阶段、风险原因、行动项、进度计算、周重点附加 |
| `F:\Project\IPD\backend\src\main\java\com\group1\career\controller\CareerAgentController.java` | `/today` 用户入口语义 |
| `F:\Project\IPD\backend\src\main\java\com\group1\career\model\dto\CareerAgentTodayDto.java` | 今日推荐输出结构 |
| `F:\Project\IPD\backend\src\test\java\com\group1\career\service\CareerAgentServiceImplTest.java` | onboarding 特殊路径测试依据 |

## Goals / Non-Goals

**Goals:**

- 新增 `today-action-recommendation` 主规格。
- 加固 `CareerAgentTodayRuleService` 聚焦测试，覆盖 P0 CareerLoop 阶段分支。
- 保持今日规则为 JDK 8 兼容纯 Java helper，不引入 Spring、JPA、LLM 或 datamodel 依赖。
- 新增基于 `CareerProfileApplicationService` 的 `CareerAgentRuleInputSource` 实现，用当前画像快照组装 `CareerAgentRuleInput`。
- 扩展 `CareerAgentWebApi`，保留直接输入 `/today`，新增按用户 ID 推荐入口。
- 更新迁移映射表，将 AI 今日任务规则标记为已实现，并记录后续任务持久化、风险看板、长期计划和前端页面。

**Non-Goals:**

- 不迁移 IPD 的 AgentTask 持久化、任务完成/忽略、任务同步和事件记录。
- 不迁移风险看板、长期计划生成或 bundle 聚合。
- 不引入 LLM reason 生成，推荐原因继续由规则服务生成。
- 不实现当前登录用户解析，WebAPI 仍使用显式 `userId`。
- 不创建 webapp 今日任务页面。

## Decisions

1. 将今日规则正式化为独立 `today-action-recommendation` 能力

   今日行动推荐是 CareerLoop 的主循环调度点，依赖画像、测评、简历、面试和计划信号。它不应继续只存在于历史代码和 `docs/career-agent-data-model.md` 中。新增主规格可以约束后续任务持久化、风险看板和计划摘要迁移。

2. 保留纯规则服务，不迁移 IPD 自主 agent 副作用

   `CareerAgentTodayRuleService` 目前只接收 `CareerAgentRuleInput` 并返回 `CareerAgentTodayDto`，没有数据库写入、任务同步或 LLM 调用。这符合 Cosmic/JDK 8 重构边界，也便于测试。IPD 中 `syncTodayTasks`、`agentStateService.refresh`、`AgentReasonService` 等副作用延后。

3. 新增画像输入源适配，而不是直接读取 datamodel

   当前可用的真实数据在 `CareerProfileApplicationService` 文件型存储中，测评和简历已经写入 `UserProfileSnapshot`。本次实现一个 `CareerProfileRuleInputSource`，通过该服务读取 snapshot，构造规则输入。后续 Cosmic datamodel、打卡和职业计划适配可以替换或装饰同一个 `CareerAgentRuleInputSource`。

4. WebAPI 双入口

   现有 `/cc001/career-agent/today` 直接接收 `CareerAgentRuleInput`，适合测试和内部调用。本次新增按用户 ID 的入口，如 `/cc001/career-agent/today/get`，供页面或后续表单插件用现有画像数据生成推荐。两个入口共用 `CareerAgentTodayApplicationService`。

5. 测试优先补齐规则分支

   现有仓库缺少 `CareerAgentTodayRuleService` 的聚焦测试。本次至少覆盖目标岗位缺失、测评缺失、简历缺失、低简历分、面试缺失、低面试分、执行节奏不足、onboarding 特殊路径和周重点追加。应用服务测试覆盖按用户 ID 从画像读取 snapshot。

## Risks / Trade-offs

- [Risk] 当前按用户 ID 推荐只读取画像快照，不读取真实打卡和职业计划。→ Mitigation: `CareerAgentRuleInputSource` 保留 `checkInStatus` 和 `weeklyFocusItems` 字段，后续迁移打卡/计划时扩展输入源。
- [Risk] 今日规则中的目标路径仍指向未来 webapp 页面。→ Mitigation: 延续 IPD 用户意图和现有 DTO 契约，本次不验证页面存在；webapp 迁移时复用这些 type/target。
- [Risk] 不持久化 AgentTask，用户不能完成/忽略今日任务。→ Mitigation: 本次只迁 P0 推荐规则；任务生命周期作为后续 change 明确拆分。
- [Risk] 显式 userId 不是最终安全模型。→ Mitigation: 与现有画像/简历 WebAPI 保持一致，后续统一登录/权限适配再收敛。

## Migration Plan

1. 新增 `today-action-recommendation` delta spec。
2. 增加 `CareerAgentTodayRuleService` 聚焦测试。
3. 新增 `CareerProfileRuleInputSource` 或等价实现，从 `CareerProfileApplicationService` 读取 snapshot。
4. 扩展 `CareerAgentTodayApplicationService` 和 `CareerAgentWebApi` 的按用户 ID 推荐入口。
5. 新增 cloud app 测试，证明按用户 ID 推荐能消费画像、测评和简历信号。
6. 更新迁移映射表。
7. 运行 OpenSpec 校验和 JDK 8 Gradle 验证。

回滚策略：删除新增输入源、WebAPI 方法、测试和 `today-action-recommendation` 主规格，迁移映射表恢复为“AI 今日任务待迁移”；已有画像、测评、简历能力不受影响。

## Open Questions

- AgentTask 持久化是否应作为 `migrate-agent-task-core` 独立 change，还是并入风险看板迁移？
- 打卡状态和长期职业计划输入源谁先迁移，才能让今日推荐更接近 IPD 完整行为？
- 前端页面路径是否需要在 webapp 迁移时统一重命名，还是保留 IPD 风格路径作为兼容层？
