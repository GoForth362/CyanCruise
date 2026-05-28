## Context

当前 CyanCruise 已具备 `CosmicIdentityContextDto`、身份 helper、`CareerLoopIdentityResolver`、`ConfigurableCosmicIdentityResolver`、`CosmicIdentityAdapterConfig` 和 `IdentityAwareCareerLoopWebApiBoundary`。生产 resolver 只有在 `cc001.identity.adapter.enabled=true` 时启用，但默认 provider 仍是 `EmptyCosmicIdentityContextProvider`，因此真实租户中如果不注入平台上下文，所有受保护 WebAPI 仍会安全返回 identity-required。

IPD 侧身份语义来自前端 `auth.ts`、`request.ts`、`api/user.ts`、`App.vue`、admin API，以及后端 auth/admin 语义：区分 guest/真实用户、401、admin whoami 和角色。CyanCruise 不能迁移 IPD JWT、Spring Security、axios 或 uni-app storage，而应从苍穹登录上下文或平台 adapter 获取当前用户。

## Goals / Non-Goals

**Goals:**
- 提供 JDK 8 兼容的 Cosmic 登录上下文 provider，使生产 resolver 可以读取当前登录用户、管理员、组织、角色、IP 和 userAgent 候选字段。
- 复用既有 configurable identity adapter 的字段候选和角色归一规则，避免重复解析逻辑。
- 在无法访问真实 Cosmic API、本地测试或租户未启用时保持安全空上下文，不使用开发 fallback 或请求体身份。
- 提供可测试 provider bridge、脱敏诊断和租户验证说明。

**Non-Goals:**
- 不实现 IPD JWT 签发/校验、微信登录、注册、密码重置、Spring Security、uni-app storage 或 axios 拦截器。
- 不在本 change 中建立完整生产 RBAC/组织权限树/人员主数据同步。
- 不硬编码客户租户私有字段名或生产平台类路径；字段仍由 `CosmicIdentityAdapterConfig` 候选配置适配。

## Decisions

1. **provider 只采集平台上下文 map，resolver 继续负责解析。**
   - 理由：`ConfigurableCosmicIdentityResolver` 已经实现 user/admin/org/roles 候选字段、管理员别名和诊断；provider 只需把 Cosmic 登录上下文转换为 `Map<String,Object>`。
   - 备选：provider 直接返回 `CosmicIdentityContextDto`。放弃，因为会复制身份解析和角色归一逻辑。

2. **采用可替换 bridge，真实 Cosmic API 作为可选接入。**
   - 理由：本地构建必须 JDK 8/Cosmic 模板兼容，且不同租户的登录上下文 API 可能不同。通过 `CosmicLoginContextBridge` 或等价接口隔离平台差异。
   - 备选：直接 import 某个苍穹登录上下文静态类。放弃，因为当前工程未确认统一 API，直接引用会增加构建风险。

3. **默认 production provider 仍然安全不可用。**
   - 理由：没有真实平台上下文时，最安全行为是 identity-required，而不是用 request body、system property 或开发 fallback 冒充生产用户。
   - 备选：允许 system property 提供生产 userId。放弃，因为会形成隐蔽后门。

4. **诊断只暴露状态和字段来源，不暴露 token/session/raw context。**
   - 理由：登录上下文可能包含 token、手机号、邮箱或租户敏感字段。诊断应帮助部署定位缺字段/禁用/桥接失败，但不得泄露凭证。
   - 备选：完整打印 context map。放弃，因为风险过高。

## Risks / Trade-offs

- [真实 Cosmic 登录 API 未确认] → 通过 bridge/provider 边界先落地可替换结构；实现阶段优先查找当前工程可用 Cosmic 类，找不到则保留显式注入 provider 与 unavailable 默认。
- [字段命名因租户不同而变化] → 继续使用 `cc001.identity.adapter.*.fields` 候选配置和 route map 文档说明。
- [角色来自权限对象而不是字符串] → provider 保留集合/数组/对象列表原始值或提取 code/name 字段，resolver 继续处理集合/数组/分隔符字符串。
- [误把开发 fallback 当生产] → factory 保持 production/development 分离，provider 不读取 query/localStorage/manual userId。
- [诊断不足导致部署困难] → provider 记录安全 message：disabled、bridge unavailable、empty context、field candidates missing、provider exception 等。

## Migration Plan

1. 在 OpenSpec 中定义登录上下文 provider 新能力，并补充 identity adapter realization 的 provider 接入要求。
2. 审阅通过后实现 provider config/bridge/default unavailable provider/factory wiring，必要时补充 route map 或部署文档。
3. 将 `CareerLoopIdentityResolverFactory.production()` 从空 provider 切换为登录上下文 provider factory，但保持 adapter disabled 时返回 unavailable resolver。
4. 添加 focused provider/resolver/WebAPI boundary 测试：bridge 成功、桥接失败、禁用、空上下文、角色对象、诊断脱敏、development fallback 隔离。
5. 更新迁移地图，记录 IPD 来源、目标模块、暂不迁移项、租户验证和回滚方式。
6. 验证通过后 archive、把上一轮 archive 目录序号修正纳入同一提交，并推送迁移分支。

## Open Questions

- 当前 Kingdee Cosmic 工程中可用的登录上下文 API 是静态上下文、服务注入、线程上下文还是 WebAPI request context，需要实现阶段确认。
- 管理员角色在目标租户中来自角色 code、权限 code、岗位、组织管理员还是 KDDT 菜单权限，需要部署时配置别名。
- orgId 应使用组织、部门还是业务单元字段，需要结合租户人员主数据进一步确认。
