## ADDED Requirements

### Requirement: 用户管理个人数据最小化

用户管理页面和对应 WebAPI SHALL 仅提供账号治理所需信息。系统 SHALL NOT 在用户管理列表或详情中展示、返回或搜索用户的学校和专业；系统 SHALL NOT 在用户管理页面展示原始平台组织 ID。平台组织 ID SHALL 仅用于服务端组织归属、权限范围和数据隔离。

#### Scenario: 管理员查看用户列表

- **WHEN** 管理员打开用户管理页面
- **THEN** 页面 SHALL 展示用户标识、身份类型、账号状态和可执行操作
- **AND** 页面 SHALL NOT 展示学校、专业或平台组织 ID

#### Scenario: 管理员搜索用户

- **WHEN** 管理员输入姓名或用户 ID 搜索用户
- **THEN** 系统 SHALL 按姓名或用户 ID 返回匹配结果
- **AND** 系统 SHALL NOT 使用学校或专业作为搜索条件

#### Scenario: 用户管理接口返回数据

- **WHEN** 管理员请求用户列表或用户详情
- **THEN** 返回数据 SHALL NOT 包含学校、专业或平台组织 ID 的值
- **AND** 服务端 SHALL 继续使用平台组织 ID 执行组织归属和数据隔离
