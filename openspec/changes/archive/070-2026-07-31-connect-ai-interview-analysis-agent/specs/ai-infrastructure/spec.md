## MODIFIED Requirements

### Requirement: CareerLoop AI scenario adapters
真实 AI 基础设施 SHALL 能替换现有 CareerLoop 可替换边界，包括助手聊天生成、职业计划生成、简历诊断分析、模拟面试追问/报告、今日任务拆解和长期记忆摘要。业务场景可以使用规则结果或明确错误作为降级，但模拟面试报告和简历诊断 SHALL NOT 使用规则分数或固定文案伪装成真实 AI 分析。

#### Scenario: Replace assistant generator
- **WHEN** 真实 AI provider 配置完成
- **THEN** `AssistantChatGenerator` SHALL 可通过同一边界调用真实 AI，而无需修改助手聊天 DTO 或 WebAPI 契约

#### Scenario: Keep safe fallback
- **WHEN** 真实 AI provider 调用失败
- **THEN** 职业计划和任务拆解 MAY 使用明确标记的规则结果，简历诊断和面试报告 SHALL 返回明确错误，所有场景均不得保存不完整结构

#### Scenario: Do not fabricate interview analysis
- **WHEN** 模拟面试报告的真实 AI 调用失败或输出校验失败
- **THEN** 系统 SHALL 保留问答供重试，并 SHALL NOT 按回答次数、文字长度或固定模板生成并保存分数、优点和改进方向

