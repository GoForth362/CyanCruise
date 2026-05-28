## Context

`migrate-cosmic-platform-mounting` 已经把 CareerLoop webapp 的生产/开发身份模式、菜单/KDDT 挂载和 route/API map 收拢起来，但后端 WebAPI 仍以显式 `userId/adminId` 参数为主。IPD 侧身份语义来自 `utils/auth.ts` 的真实用户/guest 区分、`utils/request.ts` 的 token 注入和 401 处理、`api/user.ts` 的登录/用户接口，以及管理端 `admin-frontend/src/api/index.ts` 的 admin token 和 `/admin/whoami` 检查。CyanCruise 目标是 Cosmic 二开工程，必须兼容 JDK 1.8，不能直接迁移 Spring Security/JWT/axios/uni-app storage。

本 change 先定义并实现一个平台身份上下文边界：让 WebAPI 层能够解析 Cosmic 当前用户/管理员、校验请求体显式 ID 与平台 ID 是否一致，并将身份缺失、权限不足、身份不匹配和开发 fallback 变成稳定状态。

## Goals / Non-Goals

**Goals:**

- 增加 JDK 8 兼容的身份 DTO/常量，表达 userId、adminId、roles、orgId、source、environment、status、ip、userAgent 等上下文。
- 增加纯 Java helper，归一角色、判断管理员权限、校验显式 `userId/adminId` 与平台身份一致性。
- 在 `cloud01-app01` 增加可替换 `CosmicIdentityResolver` 边界，默认 adapter 支持不可用/开发 fallback，后续真实 Cosmic API 可插拔替换。
- 为用户归属 WebAPI 和管理员 WebAPI 提供统一校验入口，不强行改造所有应用服务签名。
- 更新 webapp route/API map 和迁移地图，使平台身份上下文成为生产调用边界的一部分。

**Non-Goals:**

- 不实现真实客户租户 SSO、RBAC、KDDT 配置台或 Cosmic 登录 API 的最终适配。
- 不迁移 IPD JWT token 签发/校验、微信登录、注册/重置密码、uni-app guest storage、axios 拦截器或 Spring Security。
- 不把所有既有 WebAPI 从显式 `userId` 改为无参当前用户接口；本轮先提供一致性校验和 adapter。
- 不新增第三方依赖。

## Decisions

### Decision: 身份上下文 DTO 放在 base-common，判定规则放在 base-helper

`CosmicIdentityContextDto`、`CosmicIdentityStatus`、`CosmicIdentitySource` 等共享契约放入 `base-common`，便于 webapp route map、WebAPI 和测试引用。角色归一、管理员判断、显式 ID 一致性校验放入 `base-helper`，保持纯 Java、无 Cosmic 运行时依赖。

替代方案是直接在 `cloud01-app01` 写私有类；这会让后续 admin、通知、AI 工具调用等跨模块能力重复实现身份规则。

### Decision: WebAPI 层接入 resolver，应用服务继续接收显式 ID

新增 `CareerLoopIdentityResolver` 或等价接口，由 WebAPI/应用边界调用，解析当前平台身份并校验请求体 `userId/adminId`。校验通过后继续调用既有应用服务方法。这样保留已有测试和服务签名，只在入口层补齐生产身份边界。

替代方案是重写所有应用服务为“当前用户”接口；范围过大，且会与已归档迁移产生不必要 churn。

### Decision: 默认 adapter 明确不可用，开发 fallback 明确隔离

默认生产 resolver 在无法读取 Cosmic 上下文时返回 `IDENTITY_REQUIRED`，不猜测用户。开发 adapter 只能通过显式参数或测试构造注入，并在 context 中标记 `environment=development`、`source=development-fallback`。生产逻辑 SHALL 不把开发 fallback 当作 Cosmic 身份。

替代方案是继续从 request body 直接信任 `userId/adminId`；这无法满足平台挂载后的数据隔离要求。

### Decision: 管理员权限使用等价角色集合归一

helper SHALL 将 `ADMIN`、`COSMIC_ADMIN`、平台管理员等价编码归一为管理员权限，并保留原始 roles 供审计。管理员 WebAPI 必须同时有可解析身份和管理员权限；缺任一项时返回 identity-required 或 forbidden。

替代方案是只检查 `adminId` 非空；这会把普通用户的显式 adminId 误当作管理权限。

## Risks / Trade-offs

- [Risk] 真实 Cosmic 登录上下文字段尚未确认 → Mitigation: resolver 接口与 DTO 先稳定，默认 adapter 返回不可用，真实字段映射留给后续客户环境 adapter。
- [Risk] 显式 userId 与平台 userId 不一致会暴露旧调用方问题 → Mitigation: 提供 `IDENTITY_MISMATCH` 稳定状态和聚焦测试，webapp 只在一致或开发 fallback 时调用。
- [Risk] 管理员角色编码在不同租户不同 → Mitigation: 角色归一 helper 支持配置/常量扩展，保留原始 role 列表。
- [Risk] 批量改 WebAPI 入口可能过宽 → Mitigation: 优先覆盖工作台、onboarding、today-action、resume、admin whoami 等高风险入口，其他接口通过 route/API map 持续推进。

## Migration Plan

1. 新增身份 DTO/常量和 helper 聚焦测试。
2. 新增 `CareerLoopIdentityResolver`、默认不可用 adapter、开发 fallback adapter 和 WebAPI 边界 helper。
3. 选择代表性用户 WebAPI 和管理员 WebAPI 接入身份校验，保留既有显式 ID 服务调用。
4. 更新 route/API map、平台挂载文档和迁移地图。
5. 运行 OpenSpec 校验、身份 helper/WebAPI 聚焦测试、webapp route 校验、JDK 8 Gradle 构建。

Rollback 策略：resolver 默认不可用/开发 fallback 与原显式 ID 服务方法解耦；若真实平台 adapter 未就绪，可回退 WebAPI 入口到显式开发验证路径，并保留 helper 测试。

## Open Questions

- 真实 Cosmic Java API 中当前用户、人员、组织和角色字段名称是什么？
- `ADMIN` 等价角色最终来自 Cosmic 角色编码、菜单权限、岗位还是组织授权？
- 哪些 `/cc001/*` WebAPI 应在第一轮实现中强制平台身份一致，哪些暂时只记录 contract？
