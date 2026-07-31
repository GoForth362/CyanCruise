# study-route-ai-planning Specification

## Purpose
TBD - created by archiving change support-independent-employment-study-plans. Update Purpose after archive.
## Requirements
### Requirement: 升学规划前必须选择具体方向
升学中心 SHALL 要求用户先从考研、保研、留学中选择一个主方向，未选择时 SHALL NOT 生成升学路线规划或调用升学规划智能体。

#### Scenario: 未选方向点击生成
- **WHEN** 用户未选择具体升学方向并点击生成规划
- **THEN** 页面 SHALL 提示先选择方向
- **AND** 后端 SHALL NOT 调用任何升学规划智能体

### Requirement: 按升学方向调用对应智能服务

系统 SHALL 根据已保存的升学方向选择考研、保研或留学规划配置。运行时存在已发布任务流编码时 SHALL 优先直连任务流；没有任务流编码时 MAY 使用智能体编码兼容调用。系统 SHALL 向智能服务提供当前自画像、目标院校和具体方向。

#### Scenario: 任务流编码已配置

- **WHEN** 用户生成升学规划且对应方向配置了任务流编码
- **THEN** 后端 SHALL 直接执行该任务流
- **AND** SHALL NOT 先调用可能改写结构化结果的对话智能体

#### Scenario: 仅配置智能体编码

- **WHEN** 对应方向未配置任务流编码但配置了智能体编码
- **THEN** 后端 MAY 调用智能体
- **AND** 只接受满足路线契约的最终结构化结果

### Requirement: 升学中心提供完整规划操作
升学中心 SHALL 与就业中心一样提供“完整规划”和“生成或更新未开始阶段”操作，并 SHALL 展示结构化阶段、本周重点与今日行动摘要。

#### Scenario: 查看完整升学规划
- **WHEN** 用户已选择升学方向并点击“完整规划”
- **THEN** 页面 SHALL 打开当前升学方向的完整路径规划

#### Scenario: 更新未开始阶段
- **WHEN** 用户点击更新且当前升学规划存在未开始阶段
- **THEN** 系统 SHALL 重新生成可更新阶段
- **AND** 已开始或已完成阶段及完成状态 SHALL 保留

### Requirement: 升学智能体失败保留原规划

升学规划智能服务不可用、超时、仅返回思考或工具调用过程、或返回格式错误时，系统 SHALL 保留已有升学规划，不得清空或覆盖为失败结果。

#### Scenario: 对话智能体只返回工具调用过程

- **WHEN** 平台响应只包含 `Thought`、`Action`、`Action_input` 或用户请求回显
- **THEN** 后端 SHALL NOT 将其识别为路线结果
- **AND** SHALL 返回可恢复中文错误并保留原规划

#### Scenario: 平台包装最终规划

- **WHEN** 最终路线位于 `data`、`result`、`answer` 或 `output` 包装中，或被编码为 JSON 字符串
- **THEN** 后端 SHALL 提取满足路线契约的最终对象
- **AND** SHALL 在业务完整性校验通过后持久化

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

### Requirement: 升学阶段 SHALL 以真实勾选进度判定开始
升学路线图 SHALL 仅在用户至少完成或勾选该阶段的一项阶段动作、阶段达成、子阶段动作或关联每日任务后，将该阶段判定为“进行中”。智能体返回的阶段状态、阶段顺序或页面当前聚焦 SHALL NOT 在零完成进度时单独触发“进行中”。

#### Scenario: 第一阶段尚未勾选
- **WHEN** 用户生成升学路线图后尚未勾选第一阶段的任何任务
- **THEN** 第一阶段 SHALL 显示为“未开始”
- **AND** 用户 SHALL 能够重新生成或刷新第一阶段计划

#### Scenario: 勾选阶段中的第一项任务
- **WHEN** 用户勾选某个未开始阶段中的任意一项任务
- **THEN** 该阶段 SHALL 立即显示为“进行中”
- **AND** 重新生成时该阶段及其进度 SHALL 被保留

#### Scenario: 取消阶段内唯一已勾选任务
- **WHEN** 用户取消某阶段内唯一一项已勾选任务，且该阶段没有其他持久化完成记录
- **THEN** 该阶段 SHALL 恢复显示为“未开始”
- **AND** 该阶段 SHALL 恢复为可重新生成

#### Scenario: 完成阶段全部任务
- **WHEN** 用户完成阶段内全部可勾选任务
- **THEN** 该阶段 SHALL 显示为“已完成”
- **AND** 重新生成时该阶段及其完成进度 SHALL 被保留

#### Scenario: 智能体预填进行中状态
- **WHEN** 新生成路线中的第一阶段由智能体返回 `IN_PROGRESS`，但用户没有任何勾选或每日任务完成记录
- **THEN** 系统 SHALL 将该阶段按“未开始”保存和展示
- **AND** 该状态 SHALL NOT 阻止用户刷新第一阶段

