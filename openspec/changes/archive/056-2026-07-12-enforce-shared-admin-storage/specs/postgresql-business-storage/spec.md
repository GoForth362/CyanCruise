## ADDED Requirements

### Requirement: 共享服务使用 PostgreSQL 作为唯一业务状态后端

当 CyanCruise 以共享服务模式对局域网客户端提供访问时，已迁移业务状态和管理员治理状态 SHALL 使用配置指定的 PostgreSQL 数据库和 schema。系统 SHALL NOT 使用进程内存作为共享服务的状态后端。

#### Scenario: 共享服务配置完整

- **WHEN** 共享服务模式已启用且 `cc001.storage.backend=postgresql`、URL、用户名、密码和 schema 配置完整
- **THEN** 服务 SHALL 为已迁移业务状态和管理员治理状态创建 PostgreSQL 存储实现

#### Scenario: 共享服务配置缺失

- **WHEN** 共享服务模式已启用但 PostgreSQL 配置缺失、不完整或无法建立连接
- **THEN** 服务 SHALL 返回明确的配置或存储失败状态
- **AND** 服务 SHALL NOT 创建进程内存存储作为替代

#### Scenario: 两个服务实例读取共享状态

- **WHEN** 两个配置相同 PostgreSQL 数据库和 schema 的服务实例读取已保存的管理员治理记录
- **THEN** 两个实例 SHALL 返回同一份持久化记录

#### Scenario: 用户订阅额度跨实例保持一致

- **WHEN** 用户在一个共享服务实例获得或使用某个订阅模板额度，另一个共享服务实例使用相同 PostgreSQL 数据库和 schema 查询该用户额度
- **THEN** 两个实例 SHALL 返回同一份按用户和模板持久化的额度记录
- **AND** 系统 SHALL NOT 将订阅额度仅保存在进程内存中
