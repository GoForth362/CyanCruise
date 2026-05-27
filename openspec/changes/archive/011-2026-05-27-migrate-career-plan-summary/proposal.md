## Why

今日行动推荐已经能接收职业计划周重点，但 CyanCruise 还没有迁入“长期求职计划摘要”的正式契约和应用边界。为了让 CareerLoop 从今日任务继续闭合到长期计划，需要先把 IPD `CareerPlanService`、`CareerController`、`UserCareerPlan` 和 `CareerAgentServiceImpl#getPlanSummary/ensurePlan` 中的数据语义迁入 OpenSpec 主线。

## What Changes

- 新增 `career-plan-summary` 能力规格，定义用户职业计划摘要、计划健康度、本周重点、阶段里程碑、生成/更新时间和版本语义。
- 新增 JDK 8 兼容的职业计划 DTO 和纯 Java 摘要 helper，用于把计划记录转换为可给今日行动和页面使用的摘要。
- 新增可替换的职业计划存储边界，默认实现可先使用文件型或内存型适配，后续 Cosmic datamodel 可替换该边界。
- 扩展应用服务和 Cosmic WebAPI，支持按用户 ID 获取当前计划摘要，并在计划缺失时按用户画像确保一份默认计划。
- 将职业计划本周重点接入今日行动输入源，使今日行动可以读取已保存计划的 weekly focus。
- 更新迁移映射表，标记职业计划摘要进入实现范围，并记录 AI 生成、完整 datamodel、前端计划页和周复盘仍待后续迁移。

## Capabilities

### New Capabilities

- `career-plan-summary`: 定义长期求职计划摘要、计划健康度、本周重点、默认计划和应用/API 边界。

### Modified Capabilities

- `today-action-recommendation`: 按用户 ID 推荐时，用户输入源 SHALL 能够读取已保存职业计划的本周重点并追加到今日行动。
- `career-profile-onboarding`: 统一画像的 `hasPlan` SHALL 能够由职业计划存储边界提供，而不是长期停留在固定 false 或外部占位。

## Impact

- 影响模块：
  - `code/base/v620-cc001-base-common`：新增职业计划记录、摘要、里程碑和请求 DTO。
  - `code/base/v620-cc001-base-helper`：新增职业计划摘要 helper 和默认计划规则测试。
  - `code/cloud01/v620-cc001-cloud01-app01`：新增职业计划存储边界、应用服务、WebAPI、今日行动输入源适配和测试。
  - `openspec/specs/career-plan-summary`：新增职业计划摘要主规格。
  - `openspec/specs/today-action-recommendation`：补充按用户 ID 推荐读取计划周重点的要求。
  - `openspec/specs/career-profile-onboarding`：补充画像准备度读取计划存在性的要求。
  - `docs/ipd-to-cyancruise-migration-map.md`：同步职业计划迁移状态。
- IPD 来源：
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\CareerPlanService.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\impl\CareerPlanServiceImpl.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\controller\CareerController.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\controller\CareerAgentController.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\impl\CareerAgentServiceImpl.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\model\entity\UserCareerPlan.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\model\dto\CareerAgentPlanDto.java`
  - `F:\Project\IPD\backend\src\main\resources\db\migration\V6__f28c_user_career_plans.sql`
- 不新增运行期依赖，不迁移 Spring Boot Controller、JPA Repository、Flyway SQL、真实 LLM 服务、uni-app/Vue 页面或 IPD career path 技能图谱实现。
