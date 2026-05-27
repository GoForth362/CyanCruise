## Why

CareerLoop 主循环的后端业务语义已经完成多轮基础迁移，但画像、测评、简历、今日行动、职业计划、模拟面试、简历诊断和助手聊天仍主要依赖文件型或内存型存储适配器。现在需要提出正式 Cosmic datamodel 适配路线，把 IPD 的核心数据语义落到 CyanCruise 可治理的数据对象与存储边界上，为后续 webapp 页面、真实 AI 接入和运营能力提供稳定数据底座。

## What Changes

- 新增 Cosmic datamodel 适配能力规格：定义主循环实体、字段语义、主子关系、状态字段、时间字段、用户归属字段和跨能力引用约束。
- 新增 datamodel 适配边界：要求现有应用服务继续依赖 storage/application boundary，正式 Cosmic 适配 SHALL 通过可替换实现接入，不把 Cosmic DynamicObject、QFilter 或平台 API 泄漏到 base-common/base-helper。
- 新增迁移分阶段路线：先覆盖 P0/P1 主循环数据对象，再逐步替换文件型持久化；每个子模块切换 SHALL 保留现有 WebAPI 契约和聚焦测试。
- 新增数据映射规则：从 IPD JPA entity/Flyway 语义抽取字段含义和流程状态，不直接迁移 Spring Boot、JPA、Flyway、Lombok、Vue 或 uni-app 实现。
- 新增验证规则：datamodel 元数据、storage 适配测试、OpenSpec 校验和 JDK 8 Gradle 构建 SHALL 共同作为正式适配完成标准。
- 暂不实现真实业务代码、KDDT 表单设计、页面入口、AI SDK、数据补偿脚本或生产数据迁移；本 change 仅提出规格、设计和任务，等待审阅后再 apply。

## Capabilities

### New Capabilities

- `cosmic-datamodel-adapters`: 定义 CareerLoop 主循环在 CyanCruise 中的 Cosmic datamodel 对象、字段映射、存储适配边界、替换文件型持久化的分阶段规则和验证要求。

### Modified Capabilities

- 无。本次先新增横向 datamodel 适配能力，不改变 `career-profile-onboarding`、`assessment-core`、`resume-core`、`today-action-recommendation`、`career-plan-summary`、`interview-core`、`resume-diagnosis` 或 `assistant-chat` 已归档业务 SHALL；后续 apply 时通过 storage 边界替换底层实现。

## Impact

- IPD 来源路径：
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\model\entity\AgentUserProfile.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\model\entity\AssessmentScale.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\model\entity\AssessmentQuestion.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\model\entity\AssessmentOption.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\model\entity\AssessmentRecord.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\model\entity\AssessmentAnswer.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\model\entity\Resume.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\model\entity\ResumeProfileKeyword.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\model\entity\AgentTask.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\model\entity\UserCareerPlan.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\model\entity\Interview.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\model\entity\InterviewMessage.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\model\entity\AssistantSession.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\model\entity\AssistantMessage.java`
  - `F:\Project\IPD\backend\src\main\resources\db\migration\`
- CyanCruise 目标模块：
  - `datamodel/`：承载正式 Cosmic 数据模型定义与对象命名约定。
  - `code/cloud01/v620-cc001-cloud01-app01/`：承载 Cosmic storage adapter 和应用服务接线。
  - `code/base/v620-cc001-base-common/`：保留 DTO/常量/公共契约，不依赖 Cosmic 平台 API。
  - `code/base/v620-cc001-base-helper/`：保留纯 Java 规则，不依赖 datamodel。
  - `openspec/specs/` 与 `docs/ipd-to-cyancruise-migration-map.md`：同步规格与迁移地图。
- 依赖影响：不新增外部依赖；必须兼容 JDK 1.8，并使用仓库内 `gradlew.bat` 验证。
- API 影响：现有 WebAPI 入参/出参 SHALL 保持兼容；本 change 只规划底层存储适配。
