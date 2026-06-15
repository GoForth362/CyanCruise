# PostgreSQL Profile Storage Delta

## MODIFIED Requirements
### Requirement: 用户画像运行时不再回退到文件存储
CyanCruise 用户画像运行时存储 SHALL 使用 PostgreSQL。默认应用服务构造器 SHALL NOT 在 PostgreSQL 配置缺失或不完整时回退到 `FileCareerProfileStorage`。

#### Scenario: PostgreSQL 配置完整
- **WHEN** 共享 PostgreSQL 配置完整
- **THEN** 用户画像草稿、画像快照、画像 facts 和派生统一画像 SHALL 通过 PostgreSQL 持久化

#### Scenario: PostgreSQL 配置缺失
- **WHEN** 默认用户画像应用服务构造器启动且 PostgreSQL 配置缺失或不完整
- **THEN** 系统 SHALL fail fast，并说明缺少 PostgreSQL 用户画像存储配置

#### Scenario: 显式测试替身
- **WHEN** 单元测试需要不连接 PostgreSQL 验证纯业务逻辑
- **THEN** 测试 MAY 通过构造器显式注入 in-memory 或 test storage，而不是依赖运行时 file fallback
