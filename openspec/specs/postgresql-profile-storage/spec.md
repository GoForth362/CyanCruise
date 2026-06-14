# PostgreSQL Profile Storage Specification

## Purpose
定义 CyanCruise 用户画像草稿、画像快照、画像 facts 和派生统一画像如何通过 PostgreSQL 进行生产持久化，同时保留显式配置、手工 DDL、凭据脱敏和文件存储 fallback。

## Requirements
### Requirement: 显式配置 PostgreSQL 画像存储
系统 SHALL 支持基于 PostgreSQL 的 CyanCruise 用户画像存储适配器，且该适配器只能通过显式配置启用。PostgreSQL 服务地址 SHALL 通过配置项提供，目标连接为 `jdbc:postgresql://10.0.0.8:5432/cyancruise`，schema 为 `public`，建议应用用户为 `cyancruise_app`；这些值不得硬编码到业务代码，密码不得提交到源码、OpenSpec artifacts 或 route metadata。

#### Scenario: 已配置 PostgreSQL 适配器
- **WHEN** `cc001.profile.storage.adapter=postgresql`，并且 PostgreSQL URL、用户名、密码和 schema 配置完整
- **THEN** 应用 SHALL 使用 PostgreSQL 持久化 CyanCruise 画像草稿、画像快照、画像 facts 和派生统一画像
- **AND** 连接目标 SHALL 指向 PostgreSQL 数据库 `cyancruise` 的 `public` schema，除非后续 OpenSpec change 明确批准变更

#### Scenario: 未配置 PostgreSQL 适配器
- **WHEN** 适配器未配置、未设置为 `postgresql`，或必要连接属性不完整
- **THEN** 应用 SHALL 保持现有文件存储 fallback，并且 SHALL NOT 尝试连接 PostgreSQL

#### Scenario: 审查凭据
- **WHEN** 审查源码、OpenSpec artifacts、route metadata 或已提交的配置示例
- **THEN** 其中 SHALL NOT 包含真实密码、生产密钥、Authorization header 或客户私有凭据

### Requirement: 在 PostgreSQL 中持久化画像草稿
PostgreSQL 画像存储适配器 SHALL 按用户持久化一份画像草稿，并保留路线入口字段和更新时间。

#### Scenario: 保存并重载草稿
- **WHEN** 用户保存画像草稿并重新创建应用服务实例
- **THEN** 读取同一用户草稿时 SHALL 从 PostgreSQL 返回已保存的身份类型、教育阶段、学校/专业、简历状态、目标岗位、偏好、经历、路线意向和更新时间

#### Scenario: 读取不存在的草稿
- **WHEN** 用户在 PostgreSQL 中没有草稿行
- **THEN** 适配器 SHALL 返回空草稿 DTO，而不是返回 null 或抛出未捕获数据库异常

#### Scenario: 清空草稿
- **WHEN** 用户清空已保存草稿
- **THEN** 后续读取 SHALL 返回空草稿，并且同一用户的画像快照、facts 和派生画像行保持不变

### Requirement: 在 PostgreSQL 中持久化画像快照和 facts
PostgreSQL 画像存储适配器 SHALL 持久化画像快照、画像 facts 和派生统一画像，并使用结构化 `user_id` 归属字段和稳定查询列。

#### Scenario: 保存并重载快照
- **WHEN** onboarding 或偏好操作为用户保存画像快照
- **THEN** 新的服务实例 SHALL 通过 `user_id` 从 PostgreSQL 读取该画像快照

#### Scenario: 保存并重载 facts
- **WHEN** 画像补充输入操作保存目标城市、目标行业、时间线或每周投入时间等 facts
- **THEN** PostgreSQL SHALL 以 `(user_id, fact_key)` 保存每个 fact，并且只重载该用户的 facts，不读取其他用户 facts

#### Scenario: 保存并重载派生画像
- **WHEN** 统一画像刷新并保存
- **THEN** 新的服务实例 SHALL 从 PostgreSQL 读取个性化等级、完整度分数、当前阶段、目标岗位和完整画像载荷

### Requirement: 安全使用 PostgreSQL 表结构
PostgreSQL 画像存储适配器 SHALL 使用经审核的表结构，包含结构化归属字段和 JSON/text 载荷列。默认交付 SHALL 优先提供手工 PostgreSQL DDL；程序表初始化 SHALL 显式启用，默认关闭。

#### Scenario: 初始化关闭
- **WHEN** `cc001.profile.postgresql.initialize` 缺失或为 false
- **THEN** 适配器 SHALL NOT 自动执行 DDL

#### Scenario: 手工 SQL 建表
- **WHEN** 审核 PostgreSQL 画像存储变更
- **THEN** change SHALL 提供可人工执行或 DBA 审核的 PostgreSQL DDL，覆盖画像草稿、画像快照、画像 fact 和派生统一画像四张表
- **AND** DDL SHALL 使用 `public` schema，且 SHALL NOT 包含真实密码或本地私有路径

#### Scenario: 初始化开启
- **WHEN** `cc001.profile.postgresql.initialize=true` 且数据库权限满足要求
- **THEN** 适配器 MAY 幂等创建或检查画像草稿、画像快照、画像 fact 和派生画像表，但不得删除、截断、重命名或迁移已有数据

#### Scenario: 审查表归属字段
- **WHEN** 审查拟定 SQL 或适配器表映射
- **THEN** 每张用户归属表 SHALL 在 JSON 载荷之外包含结构化 `user_id` 和更新时间字段

### Requirement: 验证 PostgreSQL 画像存储
PostgreSQL 画像存储实现 SHALL 包含测试和验证命令，用于证明存储行为、fallback 行为和 JDK 8 兼容性。

#### Scenario: 运行聚焦存储测试
- **WHEN** 提供 PostgreSQL 测试属性
- **THEN** 聚焦测试 SHALL 针对 PostgreSQL 验证草稿、快照、facts、派生画像、清空草稿和跨服务实例重载行为

#### Scenario: 普通构建时 PostgreSQL 不可用
- **WHEN** 未提供 PostgreSQL 测试属性
- **THEN** 普通 JDK 8 Gradle 构建 SHALL 仍可运行，通过跳过真实 PostgreSQL 测试或运行非 live fallback 测试完成验证

#### Scenario: 运行完整验证
- **WHEN** 实现完成
- **THEN** `openspec validate connect-career-profile-postgresql-storage --strict`、相关存储测试和 `.\gradlew.bat clean build` SHALL 在 JDK 8 下通过
