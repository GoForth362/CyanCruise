# Assistant Chat Delta

## ADDED Requirements
### Requirement: PostgreSQL 持久化助手会话和消息
CyanCruise 助手聊天会话、消息和历史 SHALL 在运行时通过 PostgreSQL 持久化，而不是默认写入 `filestorage/assistant-chat`。

#### Scenario: 会话跨实例读取
- **WHEN** 用户创建助手会话后创建新的应用服务实例
- **THEN** 新实例 SHALL 从 PostgreSQL 返回该用户的会话列表

#### Scenario: 消息历史跨实例读取
- **WHEN** 用户向自己的会话追加 user 和 assistant 消息
- **THEN** 后续实例 SHALL 从 PostgreSQL 按发送顺序读取完整消息历史

#### Scenario: 删除会话同时删除消息
- **WHEN** 用户删除自己的助手会话
- **THEN** PostgreSQL 中该会话和消息历史 SHALL 不再被后续读取返回
