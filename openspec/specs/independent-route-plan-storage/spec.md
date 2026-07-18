# independent-route-plan-storage Specification

## Purpose
TBD - created by archiving change support-independent-employment-study-plans. Update Purpose after archive.
## Requirements
### Requirement: 就业与升学规划独立持久化
系统 SHALL 为同一用户分别持久化就业规划和升学规划，任一路线的生成、更新或完成状态变化 SHALL NOT 覆盖另一条路线的数据。

#### Scenario: 先生成就业再生成升学规划
- **WHEN** 用户已有就业规划并生成升学规划
- **THEN** 系统 SHALL 保存升学规划
- **AND** 原就业规划及版本 SHALL 保持可读取

#### Scenario: 来回切换路线
- **WHEN** 用户从就业切换到升学再切回就业
- **THEN** 系统 SHALL 返回切换前的就业规划及完成状态

### Requirement: 两条路线每日任务独立持久化
系统 SHALL 将就业每日任务和升学每日任务分开保存，并 SHALL 按路线隔离任务查询、完成状态和顺延逻辑。

#### Scenario: 完成升学任务不影响就业任务
- **WHEN** 用户在升学路线完成一项今日任务
- **THEN** 对应升学任务 SHALL 标记完成
- **AND** 同一用户的就业今日任务 SHALL 保持原状态

### Requirement: 当前路线持久化
系统 SHALL 在用户画像快照中保存当前路线，规范值为 `employment` 或 `study`。切换当前路线 SHALL NOT 删除或重新生成任何规划。

#### Scenario: 跨实例读取当前路线
- **WHEN** 用户保存升学路线后重新登录或由新服务实例读取画像
- **THEN** 系统 SHALL 返回 `study` 作为当前路线

### Requirement: PostgreSQL 使用独立升学规划表
系统 SHALL 保留既有就业规划与每日任务表，并使用独立的 PostgreSQL 表保存升学规划与升学每日任务。

#### Scenario: 跨实例读取两套规划
- **WHEN** 同一用户分别保存就业与升学规划并创建新服务实例
- **THEN** 新实例 SHALL 分别读回两套规划
- **AND** 两套规划 SHALL NOT 共享同一条数据库记录

