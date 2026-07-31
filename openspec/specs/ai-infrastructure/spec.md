# AI Infrastructure 规格

## Purpose

定义 CyanCruise 真实 AI 接入的 provider-neutral gateway、消息/响应契约、结构化输出、工具调用、安全降级、流式事件和 CareerLoop 场景适配要求。
## Requirements
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

### Requirement: AI message and usage contract
AI 请求和响应 SHALL 使用 JDK 8 兼容 DTO 表达 role、content、model、tool calls、stream 标记、usage、cost、finish reason 和 error code。

#### Scenario: Preserve ordered messages
- **WHEN** 调用方提交多轮 messages
- **THEN** gateway SHALL 保留消息顺序，并 SHALL 支持 `system`、`user`、`assistant` 和 `tool` 角色

#### Scenario: Return usage fields
- **WHEN** provider 返回 token 或成本统计
- **THEN** AI 响应 SHALL 保存 promptTokens、completionTokens、totalTokens、costMicros 和 modelName；缺失时 SHALL 使用零值或空值

### Requirement: Default system prompt handling
系统 SHALL 只在调用方没有提供首条 system message 时注入默认 system prompt；当调用方已经提供 persona 或场景 system prompt 时，系统 SHALL NOT 叠加第二条默认 system prompt。

#### Scenario: Caller provides system prompt
- **WHEN** messages 第一条角色为 `system`
- **THEN** gateway SHALL 原样保留该 system prompt，不再注入默认 system prompt

#### Scenario: Caller omits system prompt
- **WHEN** messages 没有 system prompt
- **THEN** gateway MAY 注入默认职业助手 system prompt

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

### Requirement: Function calling safety
系统 SHALL 支持 OpenAI-compatible function calling loop，但工具调用 SHALL 使用服务端认证 userId，模型不得通过工具参数决定用户归属。

#### Scenario: Inject user ownership server-side
- **WHEN** 模型请求调用工具
- **THEN** 工具执行 SHALL 使用服务端传入 userId，并 SHALL 忽略或拒绝模型参数中的用户归属字段

#### Scenario: Enforce tool call cap
- **WHEN** 模型连续请求工具调用
- **THEN** function calling loop SHALL 在达到配置的最大工具调用次数后停止继续调用工具，并返回最终回复或明确降级结果

#### Scenario: Handle unknown tool
- **WHEN** 模型请求未注册工具
- **THEN** 系统 SHALL 返回可审计的 tool-not-found 结果，而不是执行任意方法

### Requirement: Stream event contract
AI 基础设施 SHALL 定义平台中立的流式事件契约，至少支持 token、done 和 error 事件；传输层 MAY 在后续 change 中映射到 Cosmic WebAPI、SSE、轮询或其他平台能力。

#### Scenario: Emit token events
- **WHEN** provider 支持增量输出
- **THEN** gateway SHALL 将增量内容转换为有序 token 事件

#### Scenario: Complete stream
- **WHEN** provider 完成输出
- **THEN** gateway SHALL 发送 done 事件并结束流

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

### Requirement: Migration boundaries
AI 基础设施迁移 SHALL NOT 直接迁移 Spring Boot Controller、Spring `SseEmitter`、Java 17 `HttpClient`、JPA/Flyway、DashScope SDK 专有对象、语音 ASR/TTS、Vue/uni-app 页面或生产密钥配置。

#### Scenario: Keep JDK 8 and Cosmic compatibility
- **WHEN** apply 阶段新增 AI 基础设施代码
- **THEN** 代码 SHALL 兼容 JDK 1.8，并 SHALL 使用仓库内 `gradlew.bat` 验证

#### Scenario: Document excluded items
- **WHEN** 迁移地图更新
- **THEN** 文档 SHALL 记录暂不迁移项和后续真实 provider/流式/语音/页面适配项

