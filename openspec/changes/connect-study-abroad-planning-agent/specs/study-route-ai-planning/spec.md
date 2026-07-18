## ADDED Requirements

### Requirement: 升学规划 SHALL 直连方向独立智能体
系统 SHALL 在用户选择考研、保研或留学并显式生成规划时，仅使用对应方向的 `agentNumber` 调用已发布的规划智能体，SHALL NOT 使用升学规划任务流编码替代或优先于该智能体。

#### Scenario: 留学智能体已配置
- **WHEN** `cc001.agent.platform.study.abroad.enabled` 为 `true` 且配置了非空 `agentNumber`
- **THEN** 系统 SHALL 使用该 `agentNumber` 调用 Agent SDK
- **AND** 系统 SHALL 将当前用户的完整留学规划输入作为字符串发送给智能体
- **AND** 系统 SHALL NOT 调用留学 `taskFlowCode`

#### Scenario: 考研或保研智能体已配置
- **WHEN** 考研或保研方向已启用且配置了对应的非空 `agentNumber`
- **THEN** 系统 SHALL 使用对应 `agentNumber` 调用 Agent SDK
- **AND** 系统 SHALL NOT 调用该方向残留的 `taskFlowCode`

#### Scenario: 发送升学规划结构化输入
- **WHEN** 系统通过 Agent SDK 调用考研、保研或留学规划智能体
- **THEN** 系统 SHALL 将完整规划输入作为原始 JSON 文本放入 `query`
- **AND** 系统 SHALL NOT 对整段 JSON 再次增加外层字符串引号

#### Scenario: 只有留学任务流编码
- **WHEN** 留学方向没有配置 `agentNumber`，即使环境中残留 `taskFlowCode`
- **THEN** 系统 SHALL 将留学智能服务视为未配置
- **AND** 系统 SHALL 返回可恢复的中文错误
- **AND** 系统 SHALL NOT 保存默认路线、示例阶段或虚假今日行动

### Requirement: 留学智能体结果 SHALL 通过共享规划校验后持久化
系统 SHALL 只保存能够解析为单一共享规划 JSON、属于 `STUDY_ABROAD` 且覆盖连续十二个月的留学智能体结果，并 SHALL 按当前用户与留学方向独立持久化路线、阶段进度和今日行动。

#### Scenario: 返回合法留学规划
- **WHEN** 留学智能体返回满足共享字段契约和十二个月覆盖要求的单一 JSON 对象
- **THEN** 系统 SHALL 将规划标记为 `AGENT_GENERATED`
- **AND** 系统 SHALL 按当前用户与 `STUDY_ABROAD` 保存规划及拆分出的今日行动
- **AND** 考研和保研方向的数据 SHALL 保持不变

#### Scenario: 返回无效留学规划
- **WHEN** 留学智能体调用失败、超时、返回两个 JSON、返回非 JSON 或缺少必要路线结构
- **THEN** 系统 SHALL 拒绝保存该结果
- **AND** 系统 SHALL 保留当前用户原有的有效留学规划
- **AND** 系统 SHALL NOT 生成任何兜底数据
