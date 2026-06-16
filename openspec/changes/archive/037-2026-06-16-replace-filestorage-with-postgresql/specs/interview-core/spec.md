# Interview Core Delta

## ADDED Requirements
### Requirement: PostgreSQL 持久化模拟面试会话和消息
CyanCruise 模拟面试会话、消息和报告摘要 SHALL 在运行时通过 PostgreSQL 持久化，而不是默认写入 `filestorage/interview-core`。

#### Scenario: 面试会话跨实例读取
- **WHEN** 用户开始面试后创建新的应用服务实例
- **THEN** 新实例 SHALL 从 PostgreSQL 读取该面试详情和用户面试历史

#### Scenario: 消息按追加顺序读取
- **WHEN** 用户和 AI 向同一面试追加多条消息
- **THEN** PostgreSQL SHALL 保存这些消息，后续读取 SHALL 按创建或追加顺序返回

#### Scenario: 删除面试同时删除消息
- **WHEN** 用户删除自己的面试
- **THEN** PostgreSQL 中该面试及其消息 SHALL 不再被后续读取返回
