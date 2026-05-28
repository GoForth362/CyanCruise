## Why

`migrate-cosmic-identity-context` 已经建立了身份 DTO、helper、resolver 边界和代表性 WebAPI 校验，但生产默认 resolver 仍是不可用状态。为了进入真实 Cosmic 联调，需要实现一个可配置、可测试、可替换的 Cosmic 身份 adapter 接线，让 WebAPI 能从平台上下文候选字段解析用户/管理员身份，同时在无法读取真实上下文时保持安全拒绝。

## What Changes

- 新增 `cosmic-identity-adapter-realization` 能力规格，定义 Cosmic 身份 adapter 的字段候选、环境开关、角色归一、审计诊断和失败降级。
- 在 `cloud01-app01` 设计一个 JDK 8 兼容的 adapter 实现：优先读取 Cosmic/苍穹平台上下文提供的用户、人员、操作员、组织和角色字段；字段未知时通过可配置 system property 或 adapter provider 接入。
- 增加 adapter 工厂/选择器，使生产默认仍安全拒绝，但在显式配置后可切换到 Cosmic adapter；开发/test adapter 继续隔离为 `development-fallback`。
- 扩展身份上下文测试：候选字段解析、角色归一、缺身份拒绝、配置开关、admin 等价角色、诊断消息和 WebAPI 边界联动。
- 更新 `careerloop-routes.json`、平台挂载说明和迁移地图，明确后端 adapter 字段来源、客户租户核查项和暂不迁移的真实客户 RBAC/SSO 配置。
- 不新增第三方依赖；不假造客户租户 API，不迁移 IPD JWT/Spring Security/axios/uni-app 实现。

## Capabilities

### New Capabilities

- `cosmic-identity-adapter-realization`: Cosmic 身份 resolver 从不可用边界推进到可配置生产 adapter 的字段解析、启用策略、角色映射和验证契约。

### Modified Capabilities

- `cosmic-identity-context`: 补充身份上下文 resolver 的生产 adapter 选择、候选字段解析和安全降级要求。
- `cosmic-platform-mounting`: 补充平台挂载文档和 route map 必须记录后端 adapter 启用方式及租户核查项。

## Impact

- 影响 `code/cloud01/v620-cc001-cloud01-app01` 的身份 resolver/adapter、WebAPI boundary 默认接线和测试。
- 影响 `code/base/v620-cc001-base-common` 或 `base-helper` 中必要的 adapter 配置 DTO/常量/helper 扩展。
- 影响 `webapp/isv/v620/careerloop/careerloop-routes.json`、`docs/careerloop-cosmic-platform-mounting.md` 和 `docs/ipd-to-cyancruise-migration-map.md`。
- 不改变既有应用服务显式 `userId/adminId` 方法签名；adapter 只负责在 WebAPI 入口层提供和校验平台身份。
