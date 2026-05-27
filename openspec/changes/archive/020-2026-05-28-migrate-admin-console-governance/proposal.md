## Why

CareerLoop 已完成用户侧主循环、AI 基础设施、webapp 入口、就业资源和通知订阅，但平台侧仍缺少可审计的运营治理后台契约。IPD 已沉淀管理员鉴权、组织/学生看板、题库审核、用户治理、内容管理、广播、统计和审计语义，现在应先迁移这些业务规则，为 Cosmic 管理能力或后续苍穹 webapp 管理入口提供统一规格。

## What Changes

- 新增管理后台治理能力规格：定义管理员身份校验、组织/学生运营看板、技能图谱维护、题库审核、用户封禁/解封、运营广播、内容治理、统计摘要和审计日志契约。
- 抽取 IPD `AdminController`、`AdminAuthService`、`AdminAuditAspect`、`AuditLog`、`AdminAuditLog`、`QuestionBankController`、`QuestionBankService`、`ContentSafetyService`、`admin-frontend` 路由/API 中的业务规则、数据语义、流程和接口契约。
- 在后续 apply 阶段为 CyanCruise 增加 JDK 8 DTO、helper、应用服务/WebAPI、可替换存储边界和管理入口 route/API 映射，优先实现可审计的治理操作和只读运营看板。
- 定义权限边界：所有管理操作 SHALL 先解析管理员身份并校验 `ADMIN` 等价角色；缺失身份或权限不足 SHALL 拒绝，且不得使用硬编码管理员。
- 定义治理边界：用户封禁/解封、题库审核、内容上下架、广播发送等写操作 SHALL 记录审计日志；审计写入失败 SHALL 不掩盖主操作结果，但 SHALL 暴露可观测失败状态或日志。
- 定义平台迁移边界：不直接迁移 IPD Spring Boot Controller、JPA repository、Flyway、AOP、Vue、Element Plus、Pinia/Vite 或旧 JWT 实现；在 Cosmic/CyanCruise 中重建管理契约。
- 本 change 先生成 proposal、spec、design、tasks 文档，等待审阅通过后再 apply 实现代码。

## Capabilities

### New Capabilities

- `admin-console-governance`: 定义 CyanCruise CareerLoop 的管理后台治理契约，包括管理员鉴权、组织/学生看板、用户治理、题库审核、内容管理、广播、统计、审计日志、WebAPI 和平台迁移边界。

### Modified Capabilities

- 无。本次新增管理后台治理规格，不修改已归档用户侧主循环能力的 SHALL；后续实现可复用通知/订阅、就业资源、测评、面试、职业计划等既有应用服务边界。

## Impact

- IPD 来源路径：
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\controller\AdminController.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\controller\QuestionBankController.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\AdminAuthService.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\QuestionBankService.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\ContentSafetyService.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\impl\ContentSafetyServiceImpl.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\aspect\AdminAuditAspect.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\aspect\AuditLog.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\model\entity\AdminAuditLog.java`
  - `F:\Project\IPD\admin-frontend\src\router\index.ts`
  - `F:\Project\IPD\admin-frontend\src\api\index.ts`
  - `F:\Project\IPD\admin-frontend\src\views\Dashboard.vue`
  - `F:\Project\IPD\admin-frontend\src\views\Students.vue`
  - `F:\Project\IPD\admin-frontend\src\views\Users.vue`
  - `F:\Project\IPD\admin-frontend\src\views\QuestionBank.vue`
  - `F:\Project\IPD\admin-frontend\src\views\ContentManager.vue`
  - `F:\Project\IPD\admin-frontend\src\views\Broadcast.vue`
  - `F:\Project\IPD\admin-frontend\src\views\Analytics.vue`
  - `F:\Project\IPD\admin-frontend\src\views\AuditLog.vue`
- CyanCruise 目标模块：
  - `code/base/v620-cc001-base-common/`：管理后台 DTO、状态常量、审计动作常量、分页/筛选契约。
  - `code/base/v620-cc001-base-helper/`：管理员权限判断、治理操作校验、题库审核状态转换、内容安全本地规则、看板聚合和审计快照 helper。
  - `code/cloud01/v620-cc001-cloud01-app01/`：管理后台应用服务、存储边界、Cosmic WebAPI、通知广播接入点和审计写入边界。
  - `webapp/isv/v620/careerloop/`：管理入口 route/API 映射或 Cosmic 管理入口挂载说明。
  - `openspec/specs/`：新增管理后台治理主规格。
  - `docs/ipd-to-cyancruise-migration-map.md`：实现阶段更新迁移地图，记录来源、目标、数据映射、暂不迁移项和验证方式。
- API 影响：预计新增 `/cc001/admin/*` 或等价 Cosmic WebAPI 契约，覆盖 whoami、组织看板、学生列表、用户治理、题库审核、内容管理、广播、统计和审计查询；不改变既有用户侧 CareerLoop API。
- 依赖影响：默认不新增外部依赖，不引入 IPD 的 Spring/JPA/Flyway/AOP/Vue/Element Plus/Vite/Pinia/Java 17 依赖。若 apply 阶段确需 Cosmic 平台权限、菜单或审计能力，必须说明必要性并确认不破坏 JDK 8/Cosmic/KDDT 约束。
