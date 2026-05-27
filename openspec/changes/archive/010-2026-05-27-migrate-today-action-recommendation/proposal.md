## Why

职业画像、测评提交和简历基础记录已经落地，CareerLoop 现在具备生成“今日下一步行动”的核心输入。仓库中已有早期 `CareerAgentTodayRuleService` 和 WebAPI 薄入口，但它还没有对应主规格，也没有基于当前画像/简历/测评存储的正式应用边界，因此需要将 IPD 今日任务规则正式纳入 OpenSpec 主线。

## What Changes

- 新增 `today-action-recommendation` 能力规格，定义今日行动推荐的输入、阶段、风险原因、进度、行动项和 WebAPI 边界。
- 加固现有纯 Java 今日规则服务的测试，覆盖目标岗位缺失、测评缺失、简历缺失、低简历分、面试缺失、低面试分、执行节奏不足和稳定推进等关键分支。
- 新增或完善 `CareerAgentRuleInputSource` 实现，使 `CareerAgentTodayApplicationService.recommendByUserId` 可以从现有职业画像应用服务读取 `UserProfileSnapshot` 并组装规则输入。
- 扩展 `CareerAgentWebApi`，在保持现有直接规则输入入口的同时，新增按用户 ID 获取今日推荐的 Cosmic WebAPI 入口。
- 更新迁移映射表，标记 AI 今日任务规则进入已实现状态，并保留 Agent 任务持久化、风险看板、长期计划联动、当前用户身份解析和前端页面为后续迁移。

## Capabilities

### New Capabilities

- `today-action-recommendation`: 定义基于目标岗位、画像、测评、简历、面试和执行状态生成今日行动建议的规则、输入源和 API 契约。

### Modified Capabilities

- 无。

## Impact

- 影响模块：
  - `code/base/v620-cc001-base-common`：沿用现有 `CareerAgentRuleInput` 和 `CareerAgentTodayDto`。
  - `code/base/v620-cc001-base-helper`：加固 `CareerAgentTodayRuleService` 测试，必要时微调规则实现。
  - `code/cloud01/v620-cc001-cloud01-app01`：新增画像输入源适配、扩展应用服务和 WebAPI、补充测试。
  - `openspec/specs/today-action-recommendation`：新增今日行动推荐主规格。
  - `docs/ipd-to-cyancruise-migration-map.md`：同步 AI 今日任务迁移状态。
- IPD 来源：
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\CareerAgentService.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\impl\CareerAgentServiceImpl.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\controller\CareerAgentController.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\model\dto\CareerAgentTodayDto.java`
  - `F:\Project\IPD\backend\src\test\java\com\group1\career\service\CareerAgentServiceImplTest.java`
- 不新增运行期依赖，不迁移 Spring Controller、JPA Repository、AgentTask 持久化、LLM reason 生成、风险看板或长期职业计划生成。
