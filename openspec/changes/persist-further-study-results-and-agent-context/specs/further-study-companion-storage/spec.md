## MODIFIED Requirements

### Requirement: Provide PostgreSQL further-study storage implementation
CyanCruise SHALL provide a PostgreSQL implementation for further-study companion storage and reuse shared `cc001.storage.*` configuration. It SHALL persist targets, records, materials and events; it SHALL NOT delegate production persistence to an in-memory implementation.

#### Scenario: 服务重启后读取深造记录
- **WHEN** 当前用户保存目标、分析记录或材料后服务重启
- **THEN** 用户 SHALL 仍能读取自己的同方向数据

#### Scenario: 用户和方向隔离
- **WHEN** 用户查询或更新记录、材料、事件
- **THEN** 系统 SHALL 只读取该用户拥有且符合所选方向的数据
