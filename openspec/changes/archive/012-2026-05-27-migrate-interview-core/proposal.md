## Why

CyanCruise 已经具备目标岗位、画像、测评、简历、今日行动和职业计划摘要，CareerLoop 下一块缺口是“模拟面试”：用户需要围绕目标岗位开始练习、保存对话、结束会话、生成报告摘要，并把最近一次面试结果写回职业画像。迁入模拟面试基础会话与报告契约后，今日行动中的面试缺失/低分分支才能接上真实用户数据。

## What Changes

- 新增 `interview-core` 能力规格，定义模拟面试会话、消息、结束、报告摘要、历史列表、删除和所有权校验的业务契约。
- 新增 JDK 8 兼容的面试会话、消息、报告、雷达分、建议项和请求 DTO。
- 新增纯 Java 面试 helper，处理模式/难度/status 标准化、结束时长计算、报告雷达强弱项提取和画像 interview block 转换。
- 新增可替换的面试存储边界，默认先用文件型或内存型适配，后续 Cosmic datamodel 可替换。
- 新增应用服务和 Cosmic WebAPI，支持按用户 ID 开始面试、追加消息、读取消息、结束面试、保存报告、查看历史和删除面试。
- 面试结束或保存报告时同步职业画像 interview block，使今日行动和统一画像可以读取最近一次面试岗位、分数、强项/弱项。
- 更新迁移映射表，标记模拟面试基础会话与报告进入实现范围，并保留题库管理、AI 追问、语音/数字人、身体语言、订阅通知和 webapp 页面为后续迁移。

## Capabilities

### New Capabilities

- `interview-core`: 定义模拟面试基础会话、消息历史、结束、报告摘要、画像同步、应用/API 边界和可替换存储。

### Modified Capabilities

- `career-profile-onboarding`: 统一画像 SHALL 能由模拟面试能力写入 interview block，并据此更新面试准备度和缺失信号。

## Impact

- 影响模块：
  - `code/base/v620-cc001-base-common`：新增面试会话、消息、报告和请求 DTO。
  - `code/base/v620-cc001-base-helper`：新增面试 helper 和聚焦测试。
  - `code/cloud01/v620-cc001-cloud01-app01`：新增面试存储边界、应用服务、WebAPI、画像同步和测试。
  - `openspec/specs/interview-core`：新增模拟面试核心规格。
  - `openspec/specs/career-profile-onboarding`：补充面试结果写入画像的要求。
  - `docs/ipd-to-cyancruise-migration-map.md`：同步模拟面试迁移状态。
- IPD 来源：
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\InterviewService.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\impl\InterviewServiceImpl.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\controller\InterviewController.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\controller\InterviewReportController.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\model\entity\Interview.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\model\entity\InterviewMessage.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\model\entity\InterviewQuestion.java`
  - `F:\Project\IPD\backend\src\main\resources\db\migration\V1__baseline_schema.sql`
  - `F:\Project\IPD\backend\src\test\java\com\group1\career\service\InterviewServiceTest.java`
  - `F:\Project\IPD\backend\src\test\java\com\group1\career\controller\InterviewControllerTest.java`
- 不新增运行期依赖，不迁移 Spring Boot Controller、JPA Repository、Flyway SQL、真实 LLM 追问/报告生成、语音/ASR/TTS、身体语言评分、通知推送、题库管理后台、AI 出题任务、uni-app/Vue 页面或当前登录态解析。
