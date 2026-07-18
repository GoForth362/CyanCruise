# postgresql-business-storage Specification

## Purpose
TBD - created by archiving change replace-filestorage-with-postgresql. Update Purpose after archive.
## Requirements
### Requirement: 使用 PostgreSQL 作为业务状态唯一运行时存储
CyanCruise SHALL 使用 PostgreSQL 作为已迁移业务状态的唯一运行时持久化后端。已迁移业务状态包括用户画像、职业计划、简历记录、简历诊断、模拟面试和助手聊天。

#### Scenario: 运行时不再创建业务 filestorage 目录
- **WHEN** 默认应用服务构造器被使用
- **THEN** 系统 SHALL NOT 创建或写入 `filestorage/career-profile`、`filestorage/career-plan`、`filestorage/resume-core`、`filestorage/resume-diagnosis`、`filestorage/interview-core` 或 `filestorage/assistant-chat`

#### Scenario: 文件服务不在本次替换范围
- **WHEN** 简历记录保存 `fileKey` 或页面调用文件预览、上传、下载、删除能力
- **THEN** 系统 MAY 继续通过文件服务边界处理二进制文件能力，但简历元数据和业务状态 SHALL 存储在 PostgreSQL

### Requirement: 共享 PostgreSQL 配置
已迁移业务状态存储 SHALL 使用共享 PostgreSQL 配置项，并且不得在业务代码中硬编码 URL、schema、用户名、密码、本地路径或 Cosmic runtime 路径。

#### Scenario: 配置完整
- **WHEN** `cc001.storage.backend=postgresql` 且 URL、用户名、密码和 schema 配置完整
- **THEN** 已迁移业务状态存储 SHALL 连接配置指定的 PostgreSQL 数据库和 schema

#### Scenario: 配置缺失
- **WHEN** 默认应用服务构造器需要业务状态存储但共享 PostgreSQL 配置缺失或不完整
- **THEN** 系统 SHALL fail fast，并返回明确配置错误，而不是回退到本地 `filestorage`

#### Scenario: 凭据不进入源码
- **WHEN** 审查源码、OpenSpec artifacts、示例配置或 route metadata
- **THEN** 其中 SHALL NOT 包含真实数据库密码、生产密钥或私有 Authorization header

### Requirement: 提供手工 PostgreSQL DDL
本变更 SHALL 提供可由人工或 DBA 审核执行的 PostgreSQL DDL。DDL SHALL 面向 `cyancruise.public`，优先使用 `cyancruise_app` 所需的普通表和序列权限，不包含真实密码。

#### Scenario: 默认不自动建表
- **WHEN** `cc001.storage.postgresql.initialize` 缺失或为 false
- **THEN** 运行时 SHALL NOT 自动执行 DDL

#### Scenario: DDL 无破坏性
- **WHEN** 审查本变更提供的 SQL
- **THEN** SQL SHALL NOT 包含 `DROP TABLE`、`TRUNCATE`、删除数据、真实密码或本地路径

### Requirement: 验证 PostgreSQL 业务状态存储
实现 SHALL 通过测试证明 PostgreSQL 存储可跨服务实例重载、按用户归属隔离、正确排序、正确删除，并且不依赖本地 `filestorage`。

#### Scenario: 跨实例重载
- **WHEN** 一个存储实例保存业务状态后创建新的存储实例
- **THEN** 新实例 SHALL 从 PostgreSQL 读取同一份业务状态

#### Scenario: 用户隔离
- **WHEN** 用户 A 和用户 B 都有业务状态
- **THEN** 查询用户 A 的列表、详情或摘要 SHALL NOT 返回用户 B 的数据

#### Scenario: 完整验证
- **WHEN** 实现完成
- **THEN** `openspec validate replace-filestorage-with-postgresql --strict`、`openspec validate --all --strict` 和 JDK 8 `.\gradlew.bat clean build` SHALL 通过

### Requirement: Use PostgreSQL to save further-study companion business state
CyanCruise SHALL include further-study companion records in PostgreSQL business state storage. Migrated further-study state SHALL include targets, postgraduate exam records, recommendation records, study abroad records, material records, and history events.

#### Scenario: Further-study state is saved
- **WHEN** a further-study companion WebAPI completes a user-owned generation or update action
- **THEN** CyanCruise SHALL save the resulting business state through the configured PostgreSQL storage implementation

#### Scenario: PostgreSQL DDL is reviewed
- **WHEN** reviewers inspect the PostgreSQL DDL for further-study companion storage
- **THEN** the SQL SHALL NOT include destructive operations such as `DROP TABLE` or `TRUNCATE`
- **AND** the SQL SHALL define additive tables or indexes for the migrated further-study state

### Requirement: 共享服务使用 PostgreSQL 作为唯一业务状态后端

当 CyanCruise 以共享服务模式对局域网客户端提供访问时，已迁移业务状态和管理员治理状态 SHALL 使用配置指定的 PostgreSQL 数据库和 schema。系统 SHALL NOT 使用进程内存作为共享服务的状态后端。

#### Scenario: 共享服务配置完整

- **WHEN** 共享服务模式已启用，且 `cc001.storage.backend=postgresql`、URL、用户名、密码和 schema 配置完整
- **THEN** 服务 SHALL 为已迁移业务状态和管理员治理状态创建 PostgreSQL 存储实现

#### Scenario: 共享服务配置缺失

- **WHEN** 共享服务模式已启用，但 PostgreSQL 配置缺失、不完整或无法建立连接
- **THEN** 服务 SHALL 返回明确的配置或存储失败状态
- **AND** 服务 SHALL NOT 创建进程内存存储作为替代

#### Scenario: 两个服务实例读取共享状态

- **WHEN** 两个配置相同 PostgreSQL 数据库和 schema 的服务实例读取已保存的管理员治理记录
- **THEN** 两个实例 SHALL 返回同一份持久化记录

#### Scenario: 用户订阅额度跨实例保持一致

- **WHEN** 用户在一个共享服务实例获得或使用某个订阅模板额度，另一个共享服务实例使用相同 PostgreSQL 数据库和 schema 查询该用户额度
- **THEN** 两个实例 SHALL 返回同一份按用户和模板持久化的额度记录
- **AND** 系统 SHALL NOT 将订阅额度仅保存在进程内存中

