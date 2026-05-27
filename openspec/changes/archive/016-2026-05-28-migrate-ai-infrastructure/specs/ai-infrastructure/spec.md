## ADDED Requirements

### Requirement: Provider-neutral AI gateway
系统 SHALL 提供 provider-neutral AI gateway，用于同步聊天、指定模型调用、结构化生成、工具调用和流式事件输出；业务服务 SHALL 依赖 gateway 契约，而不是直接依赖 DashScope、Spring SSE、JPA 或具体 HTTP 实现。

#### Scenario: Call configured provider
- **WHEN** 业务服务提交包含 messages 和 model 的 AI 请求
- **THEN** gateway SHALL 调用已配置 provider，并返回统一 AI 响应对象

#### Scenario: Provider unavailable
- **WHEN** 未配置真实 provider、密钥缺失或 provider 显式不可用
- **THEN** gateway SHALL 返回明确不可用状态或抛出明确未配置错误，不得伪造真实 AI 回复

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
系统 SHALL 为需要 JSON 的 AI 场景提供结构化生成契约，支持提取 markdown fence 中的 JSON、定位最外层 JSON object/array、校验必需字段，并在解析失败时执行降级策略。

#### Scenario: Extract JSON from fenced response
- **WHEN** 模型返回包含 markdown 代码块的 JSON
- **THEN** helper SHALL 提取可解析 JSON 内容供业务场景使用

#### Scenario: Fall back on invalid JSON
- **WHEN** 职业计划、任务拆解、简历诊断或面试报告收到不可解析 JSON
- **THEN** 对应场景 SHALL 走规则兜底或返回明确失败状态，而不是保存不完整结构

### Requirement: CareerLoop AI scenario adapters
真实 AI 基础设施 SHALL 能替换现有 CareerLoop 可替换边界，包括助手聊天生成、职业计划生成、简历诊断分析、模拟面试追问/报告、今日任务拆解和长期记忆摘要。

#### Scenario: Replace assistant generator
- **WHEN** 真实 AI provider 配置完成
- **THEN** `AssistantChatGenerator` SHALL 可通过同一边界调用真实 AI，而无需修改助手聊天 DTO 或 WebAPI 契约

#### Scenario: Keep deterministic fallback
- **WHEN** 真实 AI provider 调用失败
- **THEN** 职业计划、任务拆解、简历诊断和面试报告 SHALL 保留规则兜底或明确错误，不中断已有核心流程

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
AI provider 调用 SHALL 有明确超时、可选一次重试、错误分类和最小审计信息，避免请求无限挂起或吞掉失败原因。

#### Scenario: Timeout
- **WHEN** provider 在配置超时时间内没有返回
- **THEN** gateway SHALL 终止等待并返回 timeout 错误或降级结果

#### Scenario: Retry server error
- **WHEN** provider 返回可重试的 5xx 错误
- **THEN** gateway MAY 重试一次，并 SHALL 记录模型名、状态码和耗时

### Requirement: Migration boundaries
AI 基础设施迁移 SHALL NOT 直接迁移 Spring Boot Controller、Spring `SseEmitter`、Java 17 `HttpClient`、JPA/Flyway、DashScope SDK 专有对象、语音 ASR/TTS、Vue/uni-app 页面或生产密钥配置。

#### Scenario: Keep JDK 8 and Cosmic compatibility
- **WHEN** apply 阶段新增 AI 基础设施代码
- **THEN** 代码 SHALL 兼容 JDK 1.8，并 SHALL 使用仓库内 `gradlew.bat` 验证

#### Scenario: Document excluded items
- **WHEN** 迁移地图更新
- **THEN** 文档 SHALL 记录暂不迁移项和后续真实 provider/流式/语音/页面适配项
