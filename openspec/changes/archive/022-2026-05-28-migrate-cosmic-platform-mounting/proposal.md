## Why

CareerLoop 已经具备多项 CyanCruise 后端 WebAPI 和 `webapp/isv/v620/careerloop` 静态入口，但当前仍以开发态 `userId`、手工 hash 路由和待定平台挂载说明为主。继续迁移前，需要先把真实苍穹登录上下文、KDDT/菜单挂载、平台 WebAPI 调用约束和部署验证边界收拢为可审阅契约，避免后续页面继续依赖临时身份或不可发布入口。

## What Changes

- 新增 `cosmic-platform-mounting` 能力规格，定义 CareerLoop webapp 在 Kingdee Cosmic 中的入口挂载、身份解析、菜单/KDDT 发布、WebAPI 调用和验证要求。
- 记录 IPD 来源路径：`F:\Project\IPD\frontend\src\pages.json`、`App.vue`、`utils\auth.ts`、`utils\request.ts`、`api\user.ts`、`admin-frontend\src\router\index.ts`、`admin-frontend\src\api\index.ts`，并明确不直接迁移 uni-app、Vue、axios、JWT 或旧 admin token 实现。
- 约定 CyanCruise 目标：`webapp/isv/v620/careerloop/`、`careerloop-routes.json`、平台挂载说明文档、必要的 Cosmic WebAPI 身份适配边界，以及 `docs/ipd-to-cyancruise-migration-map.md`。
- 定义生产态 SHALL 从苍穹登录/平台上下文解析用户和管理员身份；开发态 query/localStorage/manual userId 只能作为验证 fallback，并且不得越过显式身份检查调用用户私有 WebAPI。
- 定义菜单/KDDT 挂载的 route key、入口标题、可见性、角色/权限和部署核查清单，使工作台、消息、管理后台等入口能够被平台发布流程审阅。
- 不新增第三方依赖；本 change 先生成规格、设计和任务，待审阅后再实现。

## Capabilities

### New Capabilities

- `cosmic-platform-mounting`: CareerLoop 在苍穹/Cosmic 平台中的 webapp 入口挂载、登录身份、菜单/KDDT、WebAPI 调用边界和部署验证契约。

### Modified Capabilities

- `webapp-careerloop-entry`: 补充生产挂载后对现有 route/API map 的身份来源、入口发布和平台验证要求。

## Impact

- 影响 `webapp/isv/v620/careerloop/careerloop-routes.json`、相关静态入口脚本/验证脚本、平台挂载说明文档和迁移地图。
- 可能新增 `code/cloud01/v620-cc001-cloud01-app01` 中的身份上下文 adapter 或 WebAPI 请求边界，但不在 propose 阶段实现。
- 影响生产部署核查流程：需要确认苍穹 webapp 资源发布、KDDT/菜单挂载、平台登录态解析、管理员角色来源、用户私有 API 身份检查和 JDK 8 Gradle 构建。
- 不直接引入 IPD Spring Boot/JPA/Flyway/Vue/uni-app/axios/JWT 实现，也不替代 Cosmic/KDDT 模板约束。
