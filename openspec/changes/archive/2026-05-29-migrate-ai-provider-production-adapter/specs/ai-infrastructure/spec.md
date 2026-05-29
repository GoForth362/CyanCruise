## MODIFIED Requirements

### Requirement: Provider-neutral AI gateway
系统 SHALL 提供 provider-neutral AI gateway，用于同步聊天、指定模型调用、结构化生成、工具调用和流式事件输出；业务服务 SHALL 依赖 gateway 契约，而不是直接依赖 DashScope、Spring SSE、JPA 或具体 HTTP 实现。WHEN 显式启用并完整配置生产 provider adapter 时，gateway SHALL 调用该 provider；WHEN provider 未启用、配置缺失或不可用时，gateway SHALL 返回明确 unavailable，不得伪造真实 AI 回复。

#### Scenario: Call configured provider
- **WHEN** 业务服务提交包含 messages 和 model 的 AI 请求，且生产 provider adapter 已启用并配置完整
- **THEN** gateway SHALL 调用已配置 provider，并返回统一 AI 响应对象

#### Scenario: Provider unavailable
- **WHEN** 未配置真实 provider、密钥缺失或 provider 显式不可用
- **THEN** gateway SHALL 返回明确不可用状态或抛出明确未配置错误，不得伪造真实 AI 回复

#### Scenario: Keep business services provider neutral
- **WHEN** 生产 provider 从 unavailable adapter 切换为 OpenAI-compatible adapter
- **THEN** 助手聊天、职业计划、简历诊断、模拟面试和任务拆解服务 SHALL 继续依赖 `AiGateway` 或场景 adapter，不得直接读取 provider endpoint 或 apiKey

### Requirement: Timeout retry and audit baseline
AI provider 调用 SHALL 有明确超时、可选一次重试、错误分类和最小审计信息，避免请求无限挂起或吞掉失败原因；生产 adapter 的审计和诊断 SHALL 对密钥、Authorization header 和完整用户内容脱敏。

#### Scenario: Timeout
- **WHEN** provider 在配置超时时间内没有返回
- **THEN** gateway SHALL 终止等待并返回 timeout 错误或降级结果

#### Scenario: Retry server error
- **WHEN** provider 返回可重试的 5xx 错误
- **THEN** gateway MAY 重试一次，并 SHALL 记录模型名、状态码和耗时

#### Scenario: Redact diagnostics
- **WHEN** gateway 或 provider adapter 生成错误、诊断、日志可见文本或测试输出
- **THEN** 输出 SHALL NOT 包含 apiKey、Authorization header、bearer token 或完整用户消息正文
