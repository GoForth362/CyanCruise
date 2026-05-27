## Context

CyanCruise 目前已经完成职业画像/onboarding、测评、简历和今日行动推荐迁移。`today-action-recommendation` 的规则输入已经预留 `weeklyFocusItems`，职业画像准备度也预留 `hasPlan`，但仓库中还没有职业计划本身的 DTO、摘要规则、存储边界或 WebAPI。

IPD 职业计划能力由 `CareerPlanService`、`CareerPlanServiceImpl`、`CareerController`、`UserCareerPlan`、`CareerAgentPlanDto` 和 `CareerAgentServiceImpl#getPlanSummary/ensurePlan` 共同组成。其核心语义是：每个用户最多一份长期求职计划，计划包含目标岗位、生成时起点、阶段里程碑、本周重点、模型来源、生成/更新时间和版本；计划摘要要判断是否存在、是否需要刷新，并把本周重点供今日任务使用。

本次迁移只抽取业务规则、数据语义、流程和接口契约，不直接迁移 Spring Boot、JPA、Flyway、DashScope/LLM 或 uni-app 页面。

IPD 来源证据：

| 来源 | 迁移参考 |
| --- | --- |
| `F:\Project\IPD\backend\src\main\java\com\group1\career\service\CareerPlanService.java` | 生成、读取、异步刷新边界 |
| `F:\Project\IPD\backend\src\main\java\com\group1\career\service\impl\CareerPlanServiceImpl.java` | JSON 输出结构、默认 fallback、版本更新语义 |
| `F:\Project\IPD\backend\src\main\java\com\group1\career\controller\CareerController.java` | `/plan/generate`、`/plan/current` 用户流程 |
| `F:\Project\IPD\backend\src\main\java\com\group1\career\controller\CareerAgentController.java` | `/plan-summary`、`/plan/ensure` agent 边界 |
| `F:\Project\IPD\backend\src\main\java\com\group1\career\service\impl\CareerAgentServiceImpl.java` | 计划摘要、健康度、本周重点解析 |
| `F:\Project\IPD\backend\src\main\java\com\group1\career\model\entity\UserCareerPlan.java` | 计划字段和一人一计划约束 |
| `F:\Project\IPD\backend\src\main\java\com\group1\career\model\dto\CareerAgentPlanDto.java` | 摘要输出结构 |
| `F:\Project\IPD\backend\src\main\resources\db\migration\V6__f28c_user_career_plans.sql` | 数据字段和唯一用户约束 |

## Goals / Non-Goals

**Goals:**

- 新增 `career-plan-summary` 主规格。
- 新增 JDK 8 DTO，表达职业计划记录、计划摘要、阶段里程碑、默认计划请求和计划健康度。
- 新增纯 Java helper，把计划记录转换为摘要，并覆盖缺失计划、过期计划、缺少本周重点、旧版本和正常计划。
- 新增职业计划存储边界和默认存储实现，使应用服务可以按用户 ID 读取、保存和确保计划。
- 在计划缺失时生成一份确定性的默认计划，使用画像目标岗位或兜底目标岗位，不调用 LLM。
- 将职业计划本周重点接入 `CareerAgentRuleInputSource`，让今日行动按用户 ID 推荐时使用已保存计划。
- 让统一画像 `hasPlan` 从职业计划边界获取真实状态。
- 暴露 Cosmic WebAPI，支持获取当前计划摘要、确保计划、保存或更新计划。

**Non-Goals:**

- 不迁移 IPD LLM 提示词调用、AI 模型选择、token 统计或异步刷新实现。
- 不迁移 Spring Controller、JPA Repository、Flyway SQL 或数据库外键。
- 不创建 webapp/uni-app 计划页面。
- 不迁移 career path 技能图谱、节点解锁、节点完成等地图能力。
- 不实现登录态当前用户解析，仍使用显式 `userId` 与现有迁移 WebAPI 风格保持一致。

## Decisions

1. 将“计划摘要”拆成独立能力，而不是并入今日行动

   今日行动只消费 `weeklyFocusItems`，但职业计划本身还承担长期目标、阶段里程碑、健康度和默认生成语义。独立 `career-plan-summary` 规格能避免今日规则承担计划存储与默认生成职责，也为后续 AI 生成和 datamodel 落库保留边界。

2. 使用 DTO + helper 表达计划语义

   IPD 的 `UserCareerPlan` 使用 JPA entity 和 JSON 字段。CyanCruise 本次将其迁为普通 DTO，例如 `CareerPlanRecordDto`、`CareerPlanMilestoneDto`、`CareerPlanSummaryDto`。helper 解析里程碑和本周重点，并复刻 IPD 健康度规则：缺失更新时间、超过 14 天未更新、缺少本周重点或版本低于 2 时需要刷新。

3. 默认计划为确定性 fallback，不接入 LLM

   IPD 在 AI 失败时 fallback 到固定里程碑和周重点。本次先实现同类确定性默认计划，使“确保计划”不会返回空计划，也不引入外部 AI 依赖。未来 AI 计划生成可通过同一应用服务或生成器边界替换。

4. 存储边界先服务应用层，datamodel 延后

   新增 `CareerPlanStorage`，默认可用文件型或内存型实现；文件型存储与现有画像/简历迁移的测试模式一致。未来 Cosmic datamodel 只需实现该边界，不需要改动 helper 或 WebAPI 契约。

5. 今日行动输入源读取计划周重点

   现有 `CareerProfileRuleInputSource` 已能读取用户画像。本次扩展或组合职业计划应用服务，将已保存计划中的最多若干本周重点填入 `CareerAgentRuleInput.weeklyFocusItems`。实际追加数量仍由今日行动规则控制。

6. 画像 `hasPlan` 使用职业计划存在性

   职业画像准备度已经有 `hasPlan` 字段和缺失信号。应用服务刷新统一画像时应通过职业计划边界判断该用户是否已有计划，使计划迁移能够反映到准备度，而不是只存在于独立 API。

## Risks / Trade-offs

- [Risk] 默认计划不是 AI 个性化计划。→ Mitigation: 明确标记 `modelUsed` 或来源为 `fallback`/`default`，后续 AI 生成迁移可替换生成器。
- [Risk] 文件型存储不是最终 Cosmic datamodel。→ Mitigation: 所有持久化访问收敛到 `CareerPlanStorage`，未来 datamodel 适配不改变 DTO/helper/API。
- [Risk] 计划健康度使用当前时间，测试容易不稳定。→ Mitigation: helper 接受当前时间参数或在测试中构造相对时间。
- [Risk] 计划周重点可能为空或 JSON/列表数据异常。→ Mitigation: helper 对空值和格式异常返回空列表，并将健康度标记为 `NEEDS_REFRESH`。
- [Risk] `hasPlan` 刷新可能引入应用服务循环依赖。→ Mitigation: 画像应用层只依赖轻量查询边界或由调用方传入 `hasPlan`，避免 helper 层依赖 cloud 应用服务。

## Migration Plan

1. 新增 `career-plan-summary` delta spec，并补充今日行动、职业画像增量规格。
2. 新增 base-common 职业计划 DTO。
3. 新增 base-helper 摘要 helper、默认计划 helper 和聚焦测试。
4. 新增 cloud app 职业计划存储边界、默认存储、应用服务和 WebAPI。
5. 扩展今日行动输入源读取计划周重点。
6. 扩展职业画像应用服务，使 `hasPlan` 能来自计划存储边界。
7. 更新迁移映射表。
8. 运行 `openspec validate migrate-career-plan-summary --strict`、`openspec validate --all --strict`、JDK 8 Gradle 测试和 `.\gradlew.bat clean build`。

回滚策略：删除新增职业计划 DTO/helper/storage/WebAPI 和对应测试，撤回今日行动输入源及画像 `hasPlan` 适配，删除 active change；已有画像、测评、简历和今日行动直接规则入口不受影响。

## Open Questions

- 后续 AI 计划生成应单独作为 `migrate-career-plan-generation`，还是与 Cosmic datamodel 落库合并？
- 计划更新是否需要记录历史版本，还是继续沿用 IPD “一人一计划，原地递增 version”的模型？
- 未来当前用户身份解析完成后，计划 WebAPI 是否要保留显式 `userId` 调试入口？
