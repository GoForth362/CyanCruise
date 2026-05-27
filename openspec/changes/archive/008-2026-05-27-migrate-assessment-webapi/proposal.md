## Why

职业测评评分内核和测评结果写入职业画像快照已经完成，但当前还缺少可被页面或外部调用触达的 Cosmic WebAPI 入口。这个变更用于补齐测评提交的应用边界，让后续 webapp 测评页面和 CareerLoop 今日推荐能够基于真实提交结果继续推进。

## What Changes

- 新增职业测评 WebAPI 入口，用于接收用户 ID、测评量表和单选答案提交请求。
- WebAPI SHALL 调用现有 `AssessmentApplicationService`，完成评分、保存测评摘要到职业画像快照，并返回评分结果。
- 保持现有纯 Java 评分内核、画像快照合并和文件型持久化适配不变。
- 明确本次不迁移 IPD 的 Spring Controller、JPA Repository、Flyway SQL、AI 解读、题库管理页面和最终 Cosmic datamodel 适配。
- 更新迁移映射表，标记职业测评已补齐 WebAPI 接入，仍待 datamodel、页面和 AI 解读适配。

## Capabilities

### New Capabilities

- 无。

### Modified Capabilities

- `assessment-core`: 增加测评提交 WebAPI 边界要求，规定 WebAPI 必须复用现有评分与画像写入应用服务，并返回可供调用方使用的评分结果。

## Impact

- 影响模块：
  - `code/cloud01/v620-cc001-cloud01-app01`：新增测评 WebAPI 入口。
  - `openspec/specs/assessment-core`：补充测评提交 WebAPI 场景。
  - `docs/ipd-to-cyancruise-migration-map.md`：同步职业测评迁移状态。
- IPD 来源：
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\controller\AssessmentController.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\AssessmentService.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\impl\AssessmentServiceImpl.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\model\entity\AssessmentScale.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\model\entity\AssessmentQuestion.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\model\entity\AssessmentOption.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\model\entity\AssessmentRecord.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\model\entity\AssessmentAnswer.java`
- 不新增运行期依赖，不改变现有公开 DTO 的评分语义。
