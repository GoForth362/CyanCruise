# Career Plan Summary Delta

## MODIFIED Requirements
### Requirement: PostgreSQL 持久化职业计划记录
CyanCruise 职业计划记录 SHALL 在运行时通过 PostgreSQL 持久化，而不是默认写入 `filestorage/career-plan`。

#### Scenario: 保存后跨实例读取
- **WHEN** 用户保存或确保一份职业计划，并创建新的应用服务实例
- **THEN** 新实例 SHALL 从 PostgreSQL 读取该用户当前职业计划和摘要

#### Scenario: 无计划时生成默认计划
- **WHEN** 用户没有职业计划且调用确保计划
- **THEN** 系统 SHALL 将生成的默认计划保存到 PostgreSQL，并返回摘要

#### Scenario: 按用户隔离计划
- **WHEN** 用户 A 和用户 B 都存在职业计划
- **THEN** 读取用户 A 的计划 SHALL NOT 返回用户 B 的计划
