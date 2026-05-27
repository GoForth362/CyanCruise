## Context

CyanCruise 当前主循环能力已经具备稳定 DTO、helper、WebAPI、storage boundary 和 datamodel adapter。为了避免在没有平台密钥、网关策略和审计策略时伪造 AI，前期实现保留了 `UnavailableAssistantChatGenerator`、规则型简历诊断、默认职业计划和今日行动规则。IPD 则使用 DashScope OpenAI-compatible endpoint、Spring `SseEmitter`、function calling、异步摘要、职业计划 JSON 生成和任务拆解。

本次迁移应抽取“AI 基础设施契约”：消息、模型选择、请求超时、失败降级、结构化 JSON、工具调用安全、流式事件和用量统计。具体 provider 可以是 DashScope、Cosmic 平台 AI、内部网关或测试 fake provider；业务模块不应直接知道 provider 实现。

## Goals / Non-Goals

**Goals:**

- 定义 JDK 8 兼容的 AI 请求/响应 DTO，覆盖 messages、model、temperature、stream、tool schemas、usage 和 error。
- 提供 app01 内部 AI gateway 与默认 unavailable/fake provider，后续真实 provider 可替换。
- 提供结构化 JSON 生成与解析规则，用于职业计划、任务拆解、简历诊断、面试报告等场景。
- 提供 function calling loop 规则：工具 schema、工具执行、服务端 userId 注入、调用上限、超时和未知工具降级。
- 提供流式输出的领域事件契约，不直接依赖 Spring SSE。
- 保持现有 WebAPI、DTO、helper 和 storage 边界兼容。

**Non-Goals:**

- 不在 propose 阶段实现代码。
- 不直接迁移 DashScope SDK、Spring `SseEmitter`、Java 17 `HttpClient`、Spring `@Async` 或 JPA repository。
- 不迁移语音 ASR/TTS、数字人身体语言、前端流式页面或生产密钥配置。
- 不要求本 change 同时完成所有业务模块的 AI 质量提升；先建立基础设施，再按场景替换。
- 不新增外部依赖，除非 apply 阶段证明 Cosmic/JDK 8 现有能力无法满足且明确说明必要性。

## Decisions

### 1. AI gateway 放在 app01，公共模块只放 DTO 和纯规则

`base-common` SHALL 定义消息、请求、响应、工具 schema 和 usage DTO；`base-helper` SHALL 定义 JSON 提取、工具循环和提示词组装的纯 Java 规则；真实调用 gateway 和 provider adapter SHALL 放在 app01。

原因：AI 调用涉及密钥、网络、审计、超时和运行环境，不能污染公共 DTO/helper。这样测试可以继续在无外部 AI 的环境下运行。

替代方案是在每个业务服务里各自实现 HTTP 调用；这会产生重复超时、错误、用量和安全处理，不采用。

### 2. Provider-neutral 契约先于 DashScope 具体实现

AI 请求 SHALL 表达为统一 `AiChatRequest`，响应 SHALL 表达为 `AiChatResponse`，provider adapter 负责把它转换为 DashScope、Cosmic AI 或内部网关格式。模型名、默认模型、fallback mode、timeout SHALL 由 app01 adapter 配置读取。

原因：IPD 的 DashScope OpenAI-compatible API 是重要来源，但 CyanCruise 可能最终走 Cosmic 平台能力或内网网关。契约先行可以避免锁定某一家 provider。

替代方案是直接复制 IPD `AiServiceImpl`；该实现依赖 Spring、Java 17 HTTP、SSE 和 DashScope URL，不符合 JDK 8/Cosmic 约束。

### 3. 结构化输出必须有解析和降级策略

职业计划、任务拆解、简历诊断、面试报告等 JSON 场景 SHALL 使用 helper 提取外层 JSON、校验必需字段，并在解析失败时走规则兜底或返回明确错误。

原因：IPD 多处要求模型“只返回 JSON”，但真实模型仍可能返回 markdown fence、解释文本或不完整字段。把解析规则集中后，业务服务更容易测试。

替代方案是在每个业务服务中手写解析；这会导致不一致错误处理，不采用。

### 4. Function calling 的用户归属由服务端注入

工具 schema SHALL 不暴露 `user_id` 参数；工具执行 SHALL 接收服务端认证 userId；每轮最多执行固定次数工具调用，并对未知工具、参数解析失败和工具异常返回可审计结果。

原因：这是 IPD `FunctionCallingServiceImpl` 的关键安全不变量，也是迁移到 Cosmic 后必须保留的业务安全规则。

替代方案是允许模型传 userId；这会造成跨用户数据访问风险，不采用。

### 5. 流式输出先定义事件契约，不绑定 SSE

AI gateway SHALL 能表达 `token`、`done`、`error` 等事件；具体是否通过 Cosmic WebAPI、SSE、轮询或 websocket 输出，由后续 webapp/平台适配 change 决定。

原因：Spring `SseEmitter` 不适合直接迁移，且 Cosmic 的最佳流式方案需要单独验证。

替代方案是本 change 直接实现 SSE；会把平台能力评估和 AI 基础设施混在一起，不采用。

## Risks / Trade-offs

- [Risk] Provider-neutral 抽象首轮可能比直接 DashScope 调用多一层代码 -> Mitigation：先用小 DTO 和单一 gateway 接口，避免过度抽象。
- [Risk] 未接真实密钥时业务体验仍是不可用或规则兜底 -> Mitigation：明确 unavailable/fake provider 语义，不伪造真实 AI 能力。
- [Risk] 结构化 JSON 解析可能无法覆盖所有模型输出 -> Mitigation：集中 helper，聚焦测试 markdown fence、前后缀文本、空响应和缺字段。
- [Risk] function calling 工具过多会扩大权限面 -> Mitigation：工具 registry 白名单、服务端 userId 注入、调用上限和审计记录。
- [Risk] 流式输出平台方案不确定 -> Mitigation：先定义领域事件，不在本 change 绑定传输协议。

## Migration Plan

1. 新增 AI DTO、gateway、provider 配置和 unavailable/fake provider。
2. 新增 AI helper：消息标准化、默认 system prompt 注入、JSON 提取、结构化输出校验、工具调用循环控制。
3. 接入助手聊天生成边界，验证真实 provider 可替换默认 unavailable generator。
4. 接入职业计划、任务拆解、简历诊断和面试报告的结构化生成边界，保留规则兜底。
5. 定义工具 registry 和服务端 userId 注入的工具调用执行边界。
6. 更新规格和迁移地图，运行 OpenSpec 与 JDK 8 Gradle 验证。

Rollback：如果真实 provider 配置不可用，默认接线 SHALL 回退到 unavailable/fake provider 或现有规则实现；WebAPI 契约不变。

## Open Questions

- 真实 provider 最终使用 Cosmic 平台 AI、内部网关还是 DashScope compatible endpoint，需要 apply 前确认运行环境。
- 密钥和模型配置放在 `gradle.properties`、Cosmic 环境变量还是平台参数中心，需要与部署约束对齐。
- 流式输出是否由 Cosmic WebAPI 支持，还是在 webapp 阶段改用轮询/任务状态。
