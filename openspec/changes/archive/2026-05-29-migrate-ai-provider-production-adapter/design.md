## Context

CyanCruise 已有 `ai-infrastructure` 基线：`AiGateway`、`AiProviderAdapter`、AI DTO、默认 system prompt helper、结构化 JSON helper、function calling loop 和多个 CareerLoop 场景 adapter。当前 `CompatibleEndpointAiProviderAdapter` 仍是占位实现，生产环境缺少真实 provider 请求、响应解析、错误分类、用量审计和密钥脱敏。

IPD 来源 `AiServiceImpl` 使用 DashScope OpenAI-compatible endpoint，包含模型选择、默认 system prompt 注入、`choices[0].message.content` 解析、usage/错误处理、HTTP timeout、一次 5xx retry 和 streaming SSE 语义。IPD `FunctionCallingServiceImpl` 还定义了工具调用上限、unknown tool 处理和服务端 userId 注入安全规则。本迁移只取这些业务语义，不迁移 Spring Boot、Java 17 `HttpClient`、`SseEmitter`、DashScope SDK/URL 硬编码或密钥配置。

## Goals / Non-Goals

**Goals:**

- 提供 JDK 8 兼容的生产 AI provider adapter，可通过配置启用 OpenAI-compatible chat/completions provider。
- 将请求/响应映射到既有 `AiChatRequestDto`、`AiChatResponseDto`、`AiUsageDto`、`AiToolCallDto` 和 `AiStreamEventDto`。
- 默认禁用真实 provider；未启用、缺少 endpoint/apiKey/model 或 provider 不可用时返回明确 unavailable，不伪造 AI 回复。
- 支持 timeout、一次 5xx retry、provider 错误分类、用量字段、耗时审计和密钥脱敏诊断。
- 保持业务应用服务依赖 `AiGateway`/场景 adapter，不直接感知 provider。
- 在迁移地图和 route metadata 中回填真实 AI provider 的生产 adapter 状态、租户验证方式和暂不迁移项。

**Non-Goals:**

- 不引入 DashScope SDK、OpenAI SDK、Spring WebClient、Java 17 `HttpClient` 或新的运行时框架依赖。
- 不迁移 IPD Spring Controller、SSE 传输层、JPA/Flyway、Vue/uni-app 页面、语音 ASR/TTS、数字人面试或生产密钥本身。
- 不改变助手、面试、职业计划、简历诊断、今日任务等现有 WebAPI DTO。
- 不把 fake provider 或内存 mock 作为生产替代。

## Decisions

1. **生产 adapter 实现为 OpenAI-compatible provider binding**

   使用 `AiProviderAdapter` 作为唯一业务入口，新增配置对象与工厂选择真实 adapter、unavailable adapter 或测试 adapter。真实 adapter 使用 JDK 8 可用 HTTP 能力构造 `/chat/completions` JSON 请求，字段保持 OpenAI-compatible：`model`、`messages`、`tools`、`tool_choice`、`stream`。这样可承接 IPD DashScope compatible-mode 语义，也给后续客户环境替换为 Cosmic 平台 AI 服务留下边界。

   备选方案是直接在各场景服务中调用 HTTP provider；放弃，因为会破坏已建立的 provider-neutral gateway。

2. **配置显式启用，默认安全禁用**

   增加类似 `cc001.ai.provider.enabled`、`cc001.ai.provider.endpoint`、`cc001.ai.provider.apiKey`、`cc001.ai.provider.model`、`cc001.ai.provider.timeoutSeconds`、`cc001.ai.provider.retryOn5xx`、`cc001.ai.provider.diagnostics.enabled` 的系统属性读取规则。默认 `enabled=false`，缺少关键配置时 `isAvailable=false`，gateway 返回 `ERROR_UNAVAILABLE`。

   备选方案是沿用 IPD `fallback-mode` 字符串回复；放弃，因为 CyanCruise 规格要求不得伪造真实 AI 回复，业务兜底应由场景 adapter 决定。

3. **响应解析集中在 helper/adapter 层**

   adapter 负责解析 `choices[0].message.content`、`choices[0].message.tool_calls`、`finish_reason`、`usage.prompt_tokens`、`usage.completion_tokens`、`usage.total_tokens`、model 名称和 provider error body。解析失败返回 `ERROR_PROVIDER` 或 `ERROR_INVALID_RESPONSE`，并保留脱敏后的诊断信息。

   备选方案是让业务场景解析 provider JSON；放弃，因为会让 provider 细节泄漏到 CareerLoop 场景代码。

4. **timeout/retry/audit 使用最小生产基线**

   单次请求必须有 timeout；5xx 或明确可重试错误最多重试一次；4xx、认证失败、配置错误不得重试。审计字段至少包含 providerName、modelName、statusCode/errorCode、elapsedMillis、retryCount、request message count 和脱敏 endpoint，不记录 apiKey、Authorization header 或完整用户内容。

   备选方案是实现复杂指数退避和持久化审计；当前阶段先做最小可验证基线，持久化运营观测留给 production readiness。

5. **function calling 安全边界复用现有 loop**

   真实 provider 返回 tool calls 后仍由服务端 `AiFunctionCallingService` 和 registry 执行工具。工具参数中的 userId/adminId/orgId 不得决定数据归属；服务端身份来自已认证 CareerLoop/Cosmic 边界。工具调用次数仍受 loop cap 限制，unknown tool 返回可审计错误。

   备选方案是让 provider 直接调用外部 tool endpoint；放弃，因为不符合当前安全边界。

## Risks / Trade-offs

- 真实客户环境 HTTP/TLS/proxy 与本地测试不同 → 通过 adapter 工厂隔离、保留 unavailable 降级，并在迁移地图中加入租户手工验证清单。
- provider 返回 JSON 与 OpenAI-compatible 细节有差异 → parser 只依赖稳定字段，并对缺失 usage/tool calls 使用空值和明确错误分类。
- 密钥或提示词泄漏风险 → 诊断对象和日志必须脱敏；测试覆盖 Authorization/apiKey 不出现在 errorMessage、diagnostics 和 route metadata 中。
- JDK 8 HTTP 实现限制流式体验 → 本 change 只定义 provider stream chunk 到 `AiStreamEventDto` 的转换，实际 Cosmic/SSE/轮询传输层仍为后续工作。
- 网络构建环境不具备真实 provider → 自动化测试使用本地 fake HTTP/provider stub 验证请求构造、解析、重试和错误分类；真实 provider 只进入租户验证清单。

## Migration Plan

1. 读取 IPD `AiServiceImpl`、`FunctionCallingServiceImpl` 和 AI tools，固化来源、映射、暂不迁移项。
2. 在 common/helper 补充生产 adapter 所需常量、诊断 DTO 或解析 helper。
3. 在 cloud01 `mservice.ai` 中替换占位 compatible adapter 或新增 production adapter、配置和 factory。
4. 为可用、禁用、缺密钥、401/4xx、5xx retry、timeout、invalid JSON、usage、tool calls、stream chunk 和脱敏诊断补充 focused tests。
5. 回填 `docs/ipd-to-cyancruise-migration-map.md` 与 `webapp/isv/v620/careerloop/careerloop-routes.json`。
6. 执行 OpenSpec strict 校验、focused tests、`openspec validate --all --strict` 和 JDK 8 Gradle build。

Rollback 策略：将 `cc001.ai.provider.enabled=false` 或移除关键配置即可回到 unavailable provider，不影响现有规则兜底和业务 WebAPI。

## Open Questions

- 客户租户最终使用 DashScope compatible endpoint、苍穹平台 AI 服务，还是 OpenAI-compatible 私有网关，需要生产部署时确认。
- 是否需要在 production readiness change 中补充持久化调用审计、额度告警、成本报表和租户密钥轮换流程。
