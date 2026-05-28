## Context

CyanCruise 当前已有 `CareerLoopIdentityResolver`、`UnavailableCosmicIdentityResolver`、`DevelopmentCareerLoopIdentityResolver` 和 `IdentityAwareCareerLoopWebApiBoundary`。代表性 WebAPI 已能在进入应用服务前校验身份，但生产默认仍返回 `IDENTITY_REQUIRED`。这保证安全，却无法进入真实 Cosmic 登录上下文联调。

本 change 的目标是实现一个可配置 adapter realization：在不硬编码客户环境 API、不引入新依赖、不迁移 IPD JWT/Spring Security 的前提下，让生产 resolver 可以从可注入/可配置的平台上下文 map 中解析用户、管理员、组织和角色字段，并通过 factory 控制是否启用。

## Goals / Non-Goals

**Goals:**

- 定义 `CosmicIdentityAdapterConfig` 或等价配置，包含 enabled、userId/adminId/orgId/roles 字段候选、admin role aliases、diagnostics 开关。
- 增加 `ConfigurableCosmicIdentityResolver`，从平台上下文 provider 返回的 map/object 中按候选字段解析身份。
- 增加 resolver factory，使默认生产仍安全不可用；只有显式配置后才启用 configurable adapter。
- 保持 development fallback 与 production adapter 隔离。
- 增加测试覆盖字段候选、roles 字符串/集合、admin alias、配置关闭、缺身份和 WebAPI boundary 联动。
- 更新平台文档和迁移地图，记录 adapter 启用方式、租户核查项和仍待真实 Cosmic API 适配的事项。

**Non-Goals:**

- 不调用未知的真实 Cosmic 登录 API，不写死客户租户字段或角色编码。
- 不实现生产 SSO/RBAC 配置台、菜单权限同步、组织权限树或人员主数据同步。
- 不迁移 IPD JWT、微信登录、uni-app storage、axios 拦截器、Spring Security。
- 不把全部 `/cc001/*` WebAPI 一次性改造为当前用户接口。

## Decisions

### Decision: 使用 provider + map 字段候选，而不是直接依赖 Cosmic 运行时类

adapter 通过 `CosmicIdentityContextProvider` 获取平台上下文 map。真实 Cosmic 环境后续只需要实现 provider，把平台对象转换成 map；当前仓库可用测试 provider 和 system property provider 验证字段解析。这样避免在 JDK 8/Cosmic 模板里引入不确定 API 依赖。

替代方案是直接 import 某个 Cosmic 当前用户类；目前缺少可靠字段和运行期上下文，容易造成编译或部署耦合。

### Decision: factory 显式启用生产 adapter

默认 `IdentityAwareCareerLoopWebApiBoundary()` 继续使用安全不可用 resolver，或通过 factory 读取 `cc001.identity.adapter.enabled=true` 后启用 configurable adapter。没有显式启用时，不信任 request body userId/adminId。

替代方案是默认启用字段候选解析；这可能在未联调租户中误解析空/错误字段。

### Decision: roles 支持集合和分隔字符串

平台上下文中角色可能是 `List<String>`、数组、逗号分隔字符串或分号分隔字符串。adapter 将其归一为 DTO roles，再由现有 helper 处理 admin 等价角色。

替代方案是只支持集合；这会降低客户环境联调的容错性。

### Decision: 诊断信息安全可控

adapter 可在非敏感范围内记录 source、字段命中情况和缺失原因；默认不输出 token、手机号、邮箱等敏感值。测试只断言 status/source/message，不依赖真实个人信息。

替代方案是把整个平台上下文转储到 message；这不适合生产审计。

## Risks / Trade-offs

- [Risk] 真实 Cosmic 上下文字段与候选字段不一致 → Mitigation: 配置支持字段候选列表，租户联调时可调整，无需改业务服务。
- [Risk] 启用 adapter 后错误角色 alias 导致管理员拒绝 → Mitigation: admin role aliases 配置化，并保留 `ADMIN`、`COSMIC_ADMIN`、`PLATFORM_ADMIN` 默认集合。
- [Risk] map/provider 模式看起来比直接 API 多一层 → Mitigation: 换来编译稳定性和客户环境可替换性，后续真实 provider 可单独实现。
- [Risk] 默认不可用导致本地误以为未完成 → Mitigation: 文档明确启用 property 和开发/test adapter 用法。

## Migration Plan

1. 新增配置 DTO/provider/configurable resolver/factory。
2. 增加字段解析和角色归一测试。
3. 将默认 WebAPI boundary 构造接到 factory，同时保持未启用时安全不可用。
4. 更新 route map、平台挂载文档和迁移地图。
5. 运行 focused identity tests、route validation、OpenSpec strict validation 和 JDK 8 Gradle build。

Rollback 策略：清除 `cc001.identity.adapter.enabled` 或恢复 factory 默认即可回到不可用安全拒绝状态；已存在的 development/test adapter 不受影响。

## Open Questions

- 客户 Cosmic 租户中当前用户、人员、操作员、组织和角色字段的最终名称是什么？
- 管理员权限应来自角色、菜单权限、岗位、组织还是 KDDT 权限项？
- 是否需要把平台身份与 CareerLoop 用户表做额外映射，还是直接使用平台 user/operator/person id 作为 CareerLoop userId？
