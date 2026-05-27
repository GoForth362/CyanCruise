## Why

CareerLoop 主循环已经具备画像、测评、简历、今日行动、职业计划、模拟面试、简历诊断、助手聊天、AI 基础设施和 webapp 入口，但用户仍缺少“目标岗位外部就业参照”的内容入口。IPD 已沉淀按学校、专业、目标岗位匹配公开就业数据和资源内容的业务语义，现在应先把就业洞察/资源契约迁移到 CyanCruise，支撑今日行动、职业计划和 webapp 工作台的下一阶段扩展。

## What Changes

- 新增就业洞察/资源能力规格：定义按用户学校、专业、目标岗位生成就业摘要、趋势、覆盖审计、来源列表和资源入口的契约。
- 抽取 IPD `CdutEmploymentInsightService`、`HomepageController`、home 实体和相关 DTO/SQL 中的业务规则、数据语义、流程和接口语义，但不直接迁移 Spring Boot、JPA、Flyway、Java 17 `HttpClient`、PDFBox、Redis、Bilibili 抓取或 uni-app/Vue 页面实现。
- 在后续 apply 阶段为 CyanCruise 增加 JDK 8 兼容 DTO、helper、应用服务/WebAPI 边界和 webapp route/API 映射，优先提供主循环可消费的只读洞察与资源卡片。
- 定义就业洞察的安全降级：未填写学校/专业/目标岗位、学校暂未接入、来源不足、数据过期、后端不可用时 SHALL 给出明确不可用或待核验状态，不伪造就业率、深造率或去向结论。
- 定义资源入口的迁移边界：文章、视频、咨询和职业路径卡片作为资源摘要/入口被迁移；外部内容抓取、图片代理、刷新限流、定时任务和生产内容运营后台暂不迁移。
- 本 change 先生成 proposal、spec、design、tasks 文档，等待审阅通过后再 apply 实现代码。

## Capabilities

### New Capabilities

- `employment-insights-resources`: 定义 CyanCruise CareerLoop 的就业洞察与资源入口，包括用户画像匹配、就业数据摘要、覆盖审计、来源可追溯、资源卡片、webapp 消费契约、降级状态和迁移边界。

### Modified Capabilities

- 无。本次新增就业洞察/资源规格，不修改已归档 CareerLoop 主循环后端和 webapp 入口能力的 SHALL；后续实现可作为独立 WebAPI/资源入口供既有页面消费。

## Impact

- IPD 来源路径：
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\controller\CdutEmploymentInsightController.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\CdutEmploymentInsightService.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\impl\CdutEmploymentInsightServiceImpl.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\model\dto\CdutEmploymentInsightDto.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\model\entity\CdutEmploymentRecord.java`
  - `F:\Project\IPD\backend\src\main\resources\db\migration\V16__cdut_employment_insights.sql`
  - `F:\Project\IPD\backend\src\main\resources\db\migration\V20__employment_records_school.sql`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\controller\HomepageController.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\model\entity\HomeArticle.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\model\entity\HomeVideo.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\model\entity\HomeConsultation.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\model\dto\HomeConsultationFeedDto.java`
  - `F:\Project\IPD\frontend\src\api\cdutEmployment.ts`
  - `F:\Project\IPD\frontend\src\api\home.ts`
  - `F:\Project\IPD\frontend\src\pages\cdut-employment\index.vue`
  - `F:\Project\IPD\frontend\src\pages\cdut-employment\detail.vue`
  - `F:\Project\IPD\frontend\src\pages\home\index.vue`
- CyanCruise 目标模块：
  - `code/base/v620-cc001-base-common/`：就业洞察/资源 DTO 与公共常量。
  - `code/base/v620-cc001-base-helper/`：学校归一、来源覆盖审计、摘要降级和资源卡片规则 helper。
  - `code/cloud01/v620-cc001-cloud01-app01/`：就业洞察/资源应用服务、存储边界和 Cosmic WebAPI。
  - `webapp/isv/v620/careerloop/`：就业洞察/资源入口的 route/API 映射与页面接入点。
  - `openspec/specs/`：新增就业洞察/资源主规格。
  - `docs/ipd-to-cyancruise-migration-map.md`：实现阶段更新迁移地图，记录来源、目标、数据映射、暂不迁移项和验证方式。
- API 影响：预计新增 `/cc001/career-employment-insight/*` 与 `/cc001/career-resources/*` 只读 WebAPI 或等价 Cosmic WebAPI 契约；不改变既有画像、今日行动、职业计划和 webapp 入口 API。
- 依赖影响：默认不新增外部依赖，不引入 IPD 的 Spring/JPA/Flyway/PDFBox/Redis/Java 17 HTTP/uni-app/Vue 依赖。若 apply 阶段确需内容解析或平台服务依赖，必须单独说明必要性并确认不破坏 Cosmic/KDDT/JDK 8 约束。
