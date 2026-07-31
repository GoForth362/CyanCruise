## ADDED Requirements

### Requirement: 保研今日行动 SHALL 来源于真实保研规划
当前升学方向为保研时，今日行动 SHALL 只从该用户已持久化的真实保研规划生成和读取，不得使用考研任务或规则兜底任务。

#### Scenario: 已有真实保研规划
- **WHEN** 用户当前方向为保研且存在真实规划
- **THEN** 今日行动 SHALL 展示保研规划当前阶段对应的任务
- **AND** 任务 SHALL 使用排名、科研竞赛、材料、院校活动或导师沟通等规划原文语义

#### Scenario: 尚无保研规划
- **WHEN** 用户当前方向为保研但尚无真实智能体规划
- **THEN** 今日行动 SHALL 返回空任务和生成保研规划提示
- **AND** SHALL NOT 构造默认任务

### Requirement: 保研今日行动状态 SHALL 按方向隔离
保研任务的读取、勾选、顺延和删除 SHALL 同时约束用户与 `RECOMMENDATION` 方向。

#### Scenario: 完成保研任务
- **WHEN** 用户勾选一项保研今日行动
- **THEN** 系统 SHALL 只更新该用户的保研任务状态
- **AND** 该用户的考研和就业任务 SHALL 保持不变
