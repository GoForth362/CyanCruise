## Why

CareerLoop 已经完成平台挂载契约，但后端与 webapp 仍主要依赖显式 `userId/adminId` 参数和开发 fallback。为了继续靠近真实 Cosmic 生产部署，需要把 IPD 的登录态、请求拦截和管理员鉴权语义迁移为 CyanCruise 可替换的 Cosmic 身份上下文边界，确保用户归属和管理权限不再依赖硬编码或旧 JWT 管线。

## What Changes

- 新增 `cosmic-identity-context` 能力规格，定义生产态 Cosmic 用户/管理员身份解析、开发态 fallback、角色归一、身份缺失/权限不足状态和审计上下文字段。
- 记录 IPD 来源路径：`F:\Project\IPD\frontend\src\utils\auth.ts`、`utils\request.ts`、`api\user.ts`、`App.vue`、`F:\Project\IPD\admin-frontend\src\api\index.ts`，以及后端 auth/admin controller/service 语义；不直接迁移 JWT、axios、uni-app storage 或 Spring Security 实现。
- 约定 CyanCruise 目标：`code/base/v620-cc001-base-common` 的身份 DTO/常量，`code/base/v620-cc001-base-helper` 的身份解析/校验 helper，`code/cloud01/v620-cc001-cloud01-app01` 的 Cosmic 身份 resolver/adapter 与 WebAPI 边界，`webapp/isv/v620/careerloop` 的身份契约说明，以及迁移地图。
- 定义 WebAPI 调用原则：生产态 SHALL 优先使用平台上下文身份；请求体显式 `userId/adminId` 只能与平台身份一致或作为开发态 fallback；管理员接口 SHALL 需要 `ADMIN` 或平台管理员等价角色。
- 设计缺失身份、guest/开发 fallback、权限不足、身份不匹配和平台 adapter 不可用的稳定返回语义，供现有 CareerLoop 应用服务和 webapp route/API map 复用。
- 不新增第三方依赖，不替换 Cosmic/KDDT 登录体系；本轮先生成 proposal/spec/design/tasks，审阅通过后再实现。

## Capabilities

### New Capabilities

- `cosmic-identity-context`: Cosmic 平台登录上下文到 CareerLoop 用户/管理员身份、角色、开发 fallback、WebAPI 所有权校验和审计上下文的迁移契约。

### Modified Capabilities

- `cosmic-platform-mounting`: 补充平台挂载后对后端身份 adapter、显式 ID 一致性和角色解析的要求。
- `admin-console-governance`: 补充管理员治理入口必须复用 Cosmic 身份上下文，而非孤立 `adminId` 参数或旧 admin token。

## Impact

- 影响 `code/base/v620-cc001-base-common`、`code/base/v620-cc001-base-helper`、`code/cloud01/v620-cc001-cloud01-app01` 中的身份 DTO、helper、adapter、WebAPI 边界和测试。
- 影响 `webapp/isv/v620/careerloop/careerloop-routes.json` 的身份字段说明和 `docs/ipd-to-cyancruise-migration-map.md`。
- 现有应用服务的显式 `userId/adminId` 方法签名可继续保留；新增 adapter 负责在 WebAPI 层解析/校验平台身份，降低对业务服务的侵入。
- 不迁移 IPD Spring Boot auth、JWT token 签发/校验、微信登录、uni-app guest storage、axios 拦截器、Vue/admin-frontend 运行时或生产 SSO/RBAC 配置台实现。
