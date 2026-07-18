## ADDED Requirements

### Requirement: 共享服务中的用户与管理员能力边界

共享服务 SHALL 使用当前 Cosmic 登录上下文决定功能范围。普通用户 SHALL 只能读取和操作其解析后 `userId` 归属的数据；具备管理员等价角色的用户 SHALL 在保留普通用户功能的基础上访问管理后台。

#### Scenario: 普通用户访问用户功能

- **WHEN** 已登录普通用户从任意局域网客户端访问共享服务的用户功能
- **THEN** 服务 SHALL 使用当前登录上下文中的 `userId` 处理请求
- **AND** 服务 SHALL NOT 返回其他用户归属的业务状态

#### Scenario: 管理员访问用户和管理功能

- **WHEN** 已登录用户同时具备管理员等价角色并访问共享服务
- **THEN** 用户 SHALL 能够继续访问其本人用户功能
- **AND** 用户 SHALL 能够访问管理员治理功能

#### Scenario: 非管理员访问管理功能

- **WHEN** 已登录普通用户访问管理后台或 `/cc001/admin/*` 管理接口
- **THEN** 系统 SHALL 返回无权限状态
- **AND** 系统 SHALL NOT 暴露其他用户的管理数据

### Requirement: 平台账号切换后刷新权威身份

生产模式页面 SHALL 在加载用户数据前调用服务端当前身份接口，并以服务端返回的 Cosmic 登录身份作为唯一权威身份。浏览器缓存身份 SHALL NOT 阻止或替代该调用。

#### Scenario: 同一浏览器切换平台账号

- **WHEN** 浏览器中残留用户 A 的身份上下文，但当前 Cosmic 登录会话已切换为用户 B
- **THEN** 页面 SHALL 使用 `/cc001/identity/current` 返回的用户 B 加载业务数据
- **AND** 页面 SHALL NOT 使用用户 A 的缓存 `userId` 调用受保护接口
- **AND** 服务端 SHALL 让可信 `RequestContext` 身份优先于请求体携带的旧 `platformIdentity`

#### Scenario: 服务端身份解析失败

- **WHEN** 当前身份接口失败、返回无用户标识或返回非成功状态
- **THEN** 页面 SHALL 清除当前内存中的可用用户身份并显示需要身份或加载失败状态
- **AND** 页面 SHALL NOT 回退到浏览器缓存用户继续加载业务数据

### Requirement: 浏览器业务状态按用户隔离

首页草稿、画像折叠状态及其他用户相关浏览器状态 SHALL 按服务端确认的 `userId` 分区保存和读取。

#### Scenario: 用户 B 在用户 A 使用过的浏览器进入应用

- **WHEN** 用户 A 的本地首页草稿仍保存在浏览器中，用户 B 登录并进入 CyanCruise
- **THEN** 用户 B SHALL NOT 看到用户 A 的首页草稿、姓名或画像状态
- **AND** 用户 A 再次登录时 SHALL 能读取属于用户 A 的分区状态
