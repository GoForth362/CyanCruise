## MODIFIED Requirements

### Requirement: CareerLoop AI scenario adapters
真实 AI 基础设施 SHALL 能替换现有 CareerLoop 可替换边界，包括助手聊天生成、职业计划生成、简历诊断分析、模拟面试追问/报告、今日任务拆解和长期记忆摘要。provider gateway 不可用时 SHALL 返回真实失败；业务场景 MAY 按各自规格选择明确错误或可识别的业务保底。当前仅模拟面试 MAY 使用临时规则保底。

#### Scenario: Replace assistant generator
- **WHEN** 真实 AI provider 配置完成
- **THEN** `AssistantChatGenerator` SHALL 可通过同一边界调用真实 AI，而无需修改助手聊天 DTO 或 WebAPI 契约

#### Scenario: Provider 失败时保留真实状态
- **WHEN** 真实 AI provider 调用失败
- **THEN** gateway SHALL 返回明确失败且 SHALL NOT 伪造模型响应

#### Scenario: 面试业务使用临时保底
- **WHEN** 模拟面试提问或报告收到 provider 失败
- **THEN** 面试业务适配层 MAY 生成明确标记的基础问题或基础规则复盘以维持核心流程
- **AND** 该结果 SHALL NOT 标记为真实 AI provider 输出

#### Scenario: 其他场景不继承面试保底
- **WHEN** 职业计划、任务拆解、简历诊断或助手聊天调用失败
- **THEN** 系统 SHALL 按各自规格返回明确错误或既有合规策略
- **AND** SHALL NOT 调用面试临时保底
