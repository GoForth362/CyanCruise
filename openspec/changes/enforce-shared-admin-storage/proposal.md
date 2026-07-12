## Why

当前局域网调试中，不同电脑或不同服务实例可能因为 PostgreSQL 运行时配置缺失而回退到进程内存，导致管理员看到的用户、内容和审计信息不一致。共享服务模式需要将身份判断和业务状态统一收敛到主机服务及其 PostgreSQL 数据源，避免浏览器、管理员账号或服务实例造成数据分叉。

## What Changes

- 将共享运行模式下的 CyanCruise 业务状态、订阅额度和管理员治理状态固定到完整配置的 PostgreSQL 存储；配置缺失或连接不可用时返回明确失败，不再静默使用进程内存。
- 统一调试启动配置的 PostgreSQL 属性传递、Web 监听地址和对外访问地址，使局域网客户端始终调用主机服务。
- 明确普通用户只能读取和操作本人数据；管理员在保留普通用户能力的基础上读取统一管理数据，并对所有管理员显示一致的治理结果。
- 修复同一浏览器切换平台账号后继续使用旧身份或旧首页缓存的问题，生产模式以服务端当前登录身份为准，并按用户隔离浏览器状态。
- 调整管理后台请求失败呈现：接口失败时显示可理解的中文故障状态，不得将失败伪装为“暂无用户数据”。
- 为跨服务实例持久化、用户隔离、管理员一致性和故障可见性补充验证。

## Capabilities

### New Capabilities

无。

### Modified Capabilities

- `postgresql-business-storage`: 收紧共享运行时存储配置与配置缺失时的失败语义。
- `admin-console-governance`: 保证所有管理员读取同一持久化治理状态，并让管理端正确呈现加载失败。
- `cosmic-identity-context`: 明确共享服务模式下普通用户与管理员的身份能力边界和跨客户端一致性。

## Impact

- 启动模块：`code/v620-cosmic-debug/` 的本地调试配置及系统属性注入。
- 服务端：`CyanCruiseStorageFactory`、管理员治理存储/服务、身份边界及相关 WebAPI。
- 前端：管理后台服务模块的错误处理与状态呈现。
- 数据：继续使用主机 PostgreSQL 的既有 `cyancruise` 数据库和配置 schema；不会新增外部依赖或直接迁移 IPD 的 Spring Boot、JPA、Flyway、Vue、uni-app 实现。
- 验证：跨实例 PostgreSQL 读取、用户归属隔离、管理员一致性、接口失败可见性、OpenSpec 严格校验与 JDK 8 Gradle 构建。
