## ADDED Requirements

### Requirement: PostgreSQL 支撑画像快照持久化
当 PostgreSQL 画像存储被显式启用时，onboarding、偏好、画像 facts 和派生统一画像操作 SHALL 通过 PostgreSQL 持久化，并保持现有 WebAPI 入参和出参契约不变。

#### Scenario: Onboarding 保存后服务重建仍可读取
- **WHEN** PostgreSQL 画像存储已启用，并且用户提交 onboarding 数据
- **THEN** 新创建的应用服务实例 SHALL 从 PostgreSQL 读取同一份 onboarding 快照和目标岗位偏好

#### Scenario: 画像 facts 保存后服务重建仍可读取
- **WHEN** PostgreSQL 画像存储已启用，并且用户保存画像补充 facts
- **THEN** 新创建的应用服务实例 SHALL 读取同一批 facts，并在刷新统一画像时使用这些 facts

#### Scenario: 派生画像保存后服务重建仍可读取
- **WHEN** PostgreSQL 画像存储已启用，并且统一画像被刷新
- **THEN** 新创建的应用服务实例 SHALL 从 PostgreSQL 读取最新派生统一画像

### Requirement: PostgreSQL 中保持画像用户归属隔离
PostgreSQL 画像持久化 SHALL 按用户归属隔离所有画像快照、facts、草稿和派生画像的读写。

#### Scenario: 读取自己的画像行
- **WHEN** 用户 A 读取画像快照、facts、草稿或派生画像
- **THEN** PostgreSQL 查询 SHALL 使用用户 A 的 `user_id` 进行过滤

#### Scenario: 不泄露其他用户画像
- **WHEN** 用户 A 和用户 B 都有画像数据
- **THEN** 读取用户 A 的画像存储数据 SHALL NOT 返回用户 B 的快照、facts、草稿或派生画像载荷
