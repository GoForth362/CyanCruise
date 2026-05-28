## Why

`migrate-cosmic-identity-adapter-realization` 已经提供可配置的生产身份解析器，但默认 provider 仍是空上下文，真实苍穹登录用户、组织、角色、IP 和 userAgent 尚未接入。现在需要补齐 Cosmic 登录上下文 provider，让 `/cc001/*` WebAPI 在租户生产环境中可以从苍穹平台身份解析 CareerLoop user/admin，而不是依赖开发 fallback 或请求体身份。

## What Changes

- 新增 `cosmic-login-context-provider` 能力，定义从苍穹登录上下文读取当前用户、组织、角色、客户端信息的 provider 边界。
- 复用既有 `CosmicIdentityAdapterConfig` 字段候选、管理员别名和 `ConfigurableCosmicIdentityResolver`，避免重新定义身份解析规则。
- 支持 JDK 8 兼容的 provider 实现策略：优先通过 Cosmic 平台上下文服务/反射桥接读取，无法访问真实平台 API 时返回空上下文并保留安全 identity-required 行为。
- 明确诊断、脱敏、租户验证和回滚方式：provider 未启用或取不到上下文时不使用开发 fallback，不信任请求体 userId/adminId。
- 修改既有 `cosmic-identity-adapter-realization` 能力，补充生产 provider 接入 SHALL 和验证要求。
- 不引入破坏性 WebAPI 变更；现有身份边界、route/API map 和 `/cc001/*` 业务接口继续沿用。

## Capabilities

### New Capabilities
- `cosmic-login-context-provider`: 定义 CareerLoop 从真实 Cosmic 登录上下文采集 user/admin/org/roles/client metadata 的 provider 契约、启用策略、诊断、失败降级和验证方式。

### Modified Capabilities
- `cosmic-identity-adapter-realization`: 补充生产 resolver 工厂 SHALL 使用登录上下文 provider，并在 provider 不可用时保持安全 identity-required。

## Impact

- IPD 来源：`F:\Project\IPD\frontend\src\utils\auth.ts`、`utils\request.ts`、`api\user.ts`、`App.vue`、`F:\Project\IPD\admin-frontend\src\api\index.ts`，以及后端 auth/admin controller/service 中真实用户、guest、admin whoami 和角色语义。
- CyanCruise 目标：`code/cloud01/v620-cc001-cloud01-app01` 的 `CosmicIdentityContextProvider`、provider factory、identity resolver factory、代表性 WebAPI 身份边界测试；`webapp/isv/v620/careerloop/careerloop-routes.json` 的身份 metadata 如需补充；`docs/ipd-to-cyancruise-migration-map.md`。
- API：继续保护现有 `/cc001/*` 和 `/cc001/admin/*` WebAPI，不新增用户可见 endpoint。
- 依赖：本 propose 不新增依赖。后续实现如需引用苍穹平台登录上下文类，必须确认 JDK 8/Cosmic/KDDT 兼容；优先采用可选反射桥接或 provider 注入，避免硬编码租户私有类。
