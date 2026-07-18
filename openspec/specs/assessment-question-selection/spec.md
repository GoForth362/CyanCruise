# 职业测评抽题规格

## Purpose
定义 CyanCruise 从持久化职业测评题库创建用户专属作答批次时的维度均衡随机抽题、固定题目快照、提交范围校验、跨用户隔离和重新测评降低重复规则。

## Requirements

### Requirement: 维度均衡随机抽题
CyanCruise SHALL create a user-owned assessment attempt by selecting the configured answer question count from the active question pool, balancing selections across available question dimension groups.

#### Scenario: 题库大于作答题数
- **GIVEN** 某量表题库题目数大于管理员配置的作答题数
- **WHEN** 用户开始该测评
- **THEN** 系统 SHALL 按题目维度组均衡随机抽取配置数量的题目
- **AND** 返回题目数量 SHALL 等于配置的作答题数

#### Scenario: 维度容量不均
- **GIVEN** 某些维度组的可用题目少于平均分配数量
- **WHEN** 系统抽取题目
- **THEN** 系统 SHALL 先取完不足维度的可用题目
- **AND** 将剩余名额均衡分配到仍有可用题目的维度组

### Requirement: 固定作答批次
CyanCruise SHALL persist the selected question snapshot in a user-owned assessment attempt and SHALL use that immutable snapshot throughout the attempt lifecycle.

#### Scenario: 答题过程中重新加载
- **WHEN** 同一用户使用同一 `attemptId` 重新读取测评
- **THEN** 系统 SHALL 返回与开始测评时相同的题目和选项

#### Scenario: 提交批次答案
- **WHEN** 用户提交带有 `attemptId` 的答案
- **THEN** 系统 SHALL 只使用该用户所拥有批次中的题目快照校验和评分
- **AND** 系统 SHALL 拒绝批次外题目、跨用户批次和已完成批次

### Requirement: 重新测评降低重复
CyanCruise SHALL prefer questions not used in the user's immediately preceding attempt for the same scale while preserving dimension balance.

#### Scenario: 未使用题目充足
- **GIVEN** 各维度组都有足够的上次未使用题目
- **WHEN** 用户重新开始同一测评
- **THEN** 新批次 SHALL 优先只包含上次未使用的题目

#### Scenario: 未使用题目不足
- **GIVEN** 上次未使用题目不足以满足配置数量
- **WHEN** 用户重新开始同一测评
- **THEN** 系统 SHALL 在维持题数和维度平衡的前提下允许必要重复
