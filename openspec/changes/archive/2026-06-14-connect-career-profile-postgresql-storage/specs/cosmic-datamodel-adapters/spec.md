## ADDED Requirements

### Requirement: 支持 PostgreSQL 作为当前租户选定存储
对于当前租户，正式 CyanCruise 画像存储替换 SHALL 支持 PostgreSQL 作为选定数据库后端，而不是要求画像草稿、画像快照、画像 facts 和派生画像必须依赖 Cosmic datamodel 持久化。

#### Scenario: PostgreSQL 替换文件画像存储
- **WHEN** PostgreSQL 画像存储通过验证并被显式启用
- **THEN** CyanCruise 画像应用服务 SHALL 通过存储边界使用 PostgreSQL，而不是文件存储

#### Scenario: Cosmic datamodel 不阻塞画像存储
- **WHEN** 画像数据的 Cosmic datamodel 对象不可用，但 PostgreSQL 画像存储已配置
- **THEN** 画像草稿、画像快照、画像 facts 和派生画像持久化 SHALL NOT 因缺少 Cosmic datamodel 对象而被阻塞

#### Scenario: 其他模块保持不变
- **WHEN** PostgreSQL 画像存储被实现
- **THEN** 简历、测评、今日行动、职业计划、模拟面试、AI 助手、通知和管理后台存储 SHALL 继续使用当前已有存储适配器，直到后续单独批准的 change 替换它们

### Requirement: 记录 PostgreSQL 存储替换
存储替换文档 SHALL 记录 PostgreSQL 配置、表映射、回滚行为和验证结果，但不得保存数据库凭据。

#### Scenario: 更新迁移地图
- **WHEN** PostgreSQL 画像存储实现完成
- **THEN** 迁移地图或实现说明 SHALL 记录来源语义、目标存储适配器、表名、配置项名称、验证命令和回滚步骤

#### Scenario: 排除密钥
- **WHEN** 审查 PostgreSQL 存储文档
- **THEN** 文档 SHALL 只包含配置项名称和占位值，不包含真实数据库密码或私有连接凭据
