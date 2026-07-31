## MODIFIED Requirements

### Requirement: 升学分析草稿按用户和任务类型持久化

系统 SHALL 在任一升学智能分析提交到智能体之前，保存该用户该 `taskType` 的完整请求 DTO JSON，并允许后续按相同用户和任务类型读取。草稿 SHALL 使用 PostgreSQL 持久化；本地内存存储仅可作为开发或回退实现。

#### Scenario: 保存并重新读取不同类型草稿

- **WHEN** 同一用户分别提交考研、保研或留学的任意两个分析表单
- **THEN** 系统 SHALL 为两个 `taskType` 分别保存最新请求
- **AND** 读取其中一个任务 SHALL NOT 返回另一个任务的字段

#### Scenario: 用户隔离

- **WHEN** 用户 A 与用户 B 提交相同 `taskType` 的分析表单
- **THEN** 用户 A 读取草稿 SHALL NOT 得到用户 B 的内容

