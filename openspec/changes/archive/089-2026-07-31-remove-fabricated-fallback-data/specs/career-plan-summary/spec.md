## MODIFIED Requirements

### Requirement: 确保用户有默认职业计划
系统 SHALL 提供读取或确保已有计划的应用边界：当用户已有真实保存计划时返回现有摘要；当用户没有计划时返回 `hasPlan=false` 的缺失摘要。系统 SHALL NOT 因调用确保接口而生成或保存确定性默认计划、兜底岗位、阶段或本周重点。

#### Scenario: 已有计划时确保计划
- **WHEN** 用户已有真实职业计划且调用确保计划
- **THEN** 系统返回该计划摘要，不覆盖已有里程碑或本周重点

#### Scenario: 缺少计划时返回空摘要
- **WHEN** 用户没有职业计划且调用确保计划
- **THEN** 系统返回 `hasPlan=false` 的缺失摘要
- **AND** 系统 SHALL NOT 保存默认计划

#### Scenario: 目标岗位缺失
- **WHEN** 用户画像没有目标岗位且没有职业计划
- **THEN** 系统提示调用方先补充目标岗位或发起真实规划生成
- **AND** SHALL NOT 使用兜底岗位生成计划

## ADDED Requirements

### Requirement: 规划生成失败不得覆盖真实计划
职业规划智能生成失败时系统 SHALL 保留原有真实计划；没有原计划时返回明确失败，不得创建规则兜底计划。

#### Scenario: 有原计划时生成失败
- **WHEN** 智能规划生成失败且用户已有真实计划
- **THEN** 系统 SHALL 保留原计划并返回可重试状态

#### Scenario: 无原计划时生成失败
- **WHEN** 智能规划生成失败且用户没有真实计划
- **THEN** 系统 SHALL 返回明确失败或缺失摘要
- **AND** SHALL NOT 持久化任何默认阶段或每日任务
