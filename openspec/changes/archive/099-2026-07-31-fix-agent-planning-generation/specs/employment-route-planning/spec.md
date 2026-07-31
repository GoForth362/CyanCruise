## MODIFIED Requirements

### Requirement: Agent-ready employment route plan

CyanCruise SHALL 提供可接入智能服务的就业路线规划。运行时配置了任务流编码时 SHALL 优先直连已发布任务流；否则 MAY 使用智能体编码兼容调用。智能结果 SHALL 映射为现有 `CareerPlanRecordDto` 结构。

#### Scenario: 任务流返回包装后的路线

- **WHEN** 就业任务流通过 `data`、`result`、`answer` 或 `output` 返回路线，或将路线编码为 JSON 字符串
- **THEN** 后端 SHALL 提取满足路线契约的最终对象
- **AND** 前端 SHALL NOT 因智能服务来源变化而修改接口结构

#### Scenario: 智能体只返回调用过程

- **WHEN** 就业智能体只返回思考、工具调用参数或用户输入回显
- **THEN** 后端 SHALL 拒绝将其保存为就业路线
- **AND** SHALL 保留已有路线和完成状态
