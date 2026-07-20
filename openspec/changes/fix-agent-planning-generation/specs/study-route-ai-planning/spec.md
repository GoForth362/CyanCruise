## MODIFIED Requirements

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
