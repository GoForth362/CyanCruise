## MODIFIED Requirements

### Requirement: Structured JSON generation
系统 SHALL 为需要 JSON 的 AI 场景提供结构化生成契约，支持提取 markdown fence 中的 JSON、定位最外层 JSON object/array、校验必需字段。解析失败时 SHALL 返回明确失败状态，不得保存不完整结构或生成规则替代结果。

#### Scenario: Extract JSON from fenced response
- **WHEN** 模型返回包含 markdown 代码块的 JSON
- **THEN** helper SHALL 提取可解析 JSON 内容供业务场景使用

#### Scenario: Reject invalid JSON
- **WHEN** 职业计划、任务拆解、简历诊断或面试报告收到不可解析或字段不完整的 JSON
- **THEN** 对应场景 SHALL 返回明确失败状态
- **AND** SHALL NOT 保存部分结构、默认分数或规则兜底结果

### Requirement: CareerLoop AI scenario adapters
真实 AI 基础设施 SHALL 能替换 CareerLoop 的助手聊天生成、职业计划生成、简历诊断分析、模拟面试追问/报告、今日任务拆解和长期记忆摘要边界。智能能力失败时 SHALL 返回明确错误且不得构造看似由智能分析产生的内容。

#### Scenario: Replace assistant generator
- **WHEN** 真实 AI provider 配置完成
- **THEN** `AssistantChatGenerator` SHALL 可通过同一边界调用真实 AI，而无需修改助手聊天 DTO 或 WebAPI 契约

#### Scenario: AI provider fails
- **WHEN** 真实 AI provider 调用失败、超时、未配置或响应无效
- **THEN** 职业计划、任务拆解、简历诊断、面试问题和面试报告 SHALL 返回明确不可用或失败状态
- **AND** SHALL NOT 生成规则问题、规则路线、规则建议、默认分数或其他替代成功内容
