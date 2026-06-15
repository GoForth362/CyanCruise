## ADDED Requirements

### Requirement: PostgreSQL 支撑画像草稿持久化
当 PostgreSQL 画像存储被显式启用时，画像草稿操作 SHALL 使用 PostgreSQL 持久化，同时保持现有草稿 WebAPI 和应用服务契约不变。

#### Scenario: 草稿 API 使用 PostgreSQL 适配器
- **WHEN** PostgreSQL 画像存储已启用，并且 web 入口为同一用户保存后再读取草稿
- **THEN** 返回的草稿 SHALL 来自 PostgreSQL，并包含已保存的字段值

#### Scenario: 草稿合并规则保持不变
- **WHEN** PostgreSQL 画像存储已启用，并且用户保存部分字段或空白草稿更新
- **THEN** 现有合并行为 SHALL 继续保留未提交字段，并忽略空白覆盖值

#### Scenario: 草稿 fallback 保持可用
- **WHEN** PostgreSQL 画像存储未启用或配置不完整
- **THEN** 草稿操作 SHALL 继续通过已配置 fallback 存储工作，用于开发和回滚
