# ai-provider-production-adapter Specification

## Purpose
TBD - created by archiving change migrate-ai-provider-production-adapter. Update Purpose after archive.
## Requirements
### Requirement: Production provider enablement
系统 SHALL 提供 JDK 8 兼容的生产 AI provider adapter 配置与工厂，只有在显式启用且 endpoint、apiKey、model 等关键配置完整时才将真实 provider 标记为 available；默认状态 SHALL 为 disabled-safe。

#### Scenario: Provider disabled by default
- **WHEN** 未配置 `cc001.ai.provider.enabled=true`
- **THEN** gateway SHALL 使用 unavailable provider，并返回明确 `ERROR_UNAVAILABLE`，不得调用真实网络 endpoint

#### Scenario: Provider enabled with complete configuration
- **WHEN** 已显式启用 provider 且 endpoint、apiKey、model 和 timeout 配置有效
- **THEN** 工厂 SHALL 构造生产 provider adapter，并 SHALL 对业务服务保持 `AiGateway` 契约不变

#### Scenario: Provider enabled with missing secret
- **WHEN** provider 已启用但 apiKey 为空
- **THEN** adapter SHALL 标记为 unavailable，并 SHALL 返回缺少密钥的脱敏诊断，不得输出密钥占位值或尝试网络调用

### Requirement: OpenAI-compatible request mapping
生产 provider adapter SHALL 将 `AiChatRequestDto` 映射为 OpenAI-compatible chat/completions 请求，保留消息顺序、model、tool schemas、tool choice、stream 标记和 timeout。

#### Scenario: Preserve request messages
- **WHEN** 业务服务提交包含 system、user、assistant 或 tool 角色的有序 messages
- **THEN** adapter SHALL 以相同顺序写入 provider 请求，并 SHALL 保留 role/content/tool_call_id 等 provider-compatible 字段

#### Scenario: Include tools when present
- **WHEN** AI 请求包含 tool schemas 或 tool choice
- **THEN** adapter SHALL 将工具定义写入 provider 请求，并 SHALL NOT 添加模型可控制用户归属的工具参数

### Requirement: Provider response parsing
生产 provider adapter SHALL 将 provider 响应解析为统一 `AiChatResponseDto`，包括 content、finishReason、toolCalls、usage、modelName、errorCode、errorMessage 和 fallback 标记。

#### Scenario: Parse successful text response
- **WHEN** provider 返回 2xx 且包含 `choices[0].message.content`
- **THEN** adapter SHALL 返回非 fallback 响应，并 SHALL 设置 content、finishReason、modelName 和 usage 字段

#### Scenario: Parse tool calls
- **WHEN** provider 返回 `finish_reason=tool_calls` 且 message 中包含 tool calls
- **THEN** adapter SHALL 转换为 `AiToolCallDto` 列表，并 SHALL 保留 toolCall id、function name 和 arguments

#### Scenario: Invalid provider response
- **WHEN** provider 返回 2xx 但 JSON 不可解析或缺少 choices
- **THEN** adapter SHALL 返回 `ERROR_INVALID_RESPONSE` 或等价 provider 错误，并 SHALL NOT 保存不完整结构为成功结果

### Requirement: Timeout retry and error classification
生产 provider adapter SHALL 对每次真实调用设置 timeout，针对可重试 5xx 最多重试一次，并将 4xx、认证失败、timeout、网络异常和 provider 响应异常分类为可审计错误。

#### Scenario: Timeout returns explicit error
- **WHEN** provider 在配置 timeout 内未完成响应
- **THEN** adapter SHALL 终止等待并返回 `ERROR_TIMEOUT`，且 SHALL 保留 fallback=true

#### Scenario: Retry server error once
- **WHEN** provider 第一次返回 5xx 且重试开关启用
- **THEN** adapter MAY 重试一次，并 SHALL 在最终响应诊断中记录 retryCount

#### Scenario: Do not retry authentication failure
- **WHEN** provider 返回 401 或 403
- **THEN** adapter SHALL 返回认证/权限类错误，并 SHALL NOT 重试

### Requirement: Audit diagnostics and secret redaction
生产 provider adapter SHALL 记录最小调用诊断信息，并 MUST 对 apiKey、Authorization header、完整用户内容和 provider secret 做脱敏处理。

#### Scenario: Redact provider secret
- **WHEN** adapter 生成 errorMessage、diagnostics、审计文本或测试可见输出
- **THEN** 输出 SHALL NOT 包含原始 apiKey、Authorization header 或 bearer token

#### Scenario: Preserve minimal audit fields
- **WHEN** provider 调用完成、失败或降级
- **THEN** 诊断 SHALL 至少包含 providerName、modelName、statusCode 或 errorCode、elapsedMillis、retryCount 和 messageCount

### Requirement: Stream chunk normalization
生产 provider adapter SHALL 将 OpenAI-compatible streaming chunk 归一为平台中立 `AiStreamEventDto`，至少支持 token、done 和 error 事件。

#### Scenario: Convert token chunk
- **WHEN** provider 返回包含 `choices[0].delta.content` 的 stream chunk
- **THEN** adapter SHALL 生成有序 token event，并 SHALL 保留序号

#### Scenario: Convert done chunk
- **WHEN** provider 返回 `[DONE]` 或等价结束标记
- **THEN** adapter SHALL 生成 done event，并 SHALL 结束本次 stream 结果

### Requirement: Tenant verification boundary
生产 provider adapter 迁移 SHALL 记录租户启用、配置、禁用、回滚和人工验证方式，且 SHALL NOT 将真实密钥写入仓库、OpenSpec、webapp route metadata 或迁移地图。

#### Scenario: Document tenant verification
- **WHEN** 迁移地图回填完成
- **THEN** 文档 SHALL 包含启用属性、验证步骤、回滚方式、暂不迁移项和密钥不入库约束

#### Scenario: Keep route metadata secret-free
- **WHEN** webapp route/API metadata 描述真实 AI provider 状态
- **THEN** metadata SHALL 只记录 adapter 状态、诊断开关和 fallback 行为，不得包含 endpoint secret、apiKey 或 Authorization header

