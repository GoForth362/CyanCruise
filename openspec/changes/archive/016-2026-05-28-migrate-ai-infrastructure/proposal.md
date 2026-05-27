## Why

CareerLoop 后端已经完成画像、测评、简历、今日行动、职业计划、模拟面试、简历诊断、助手聊天和 Cosmic datamodel 适配基础，但多个能力仍停留在确定性规则或 `Unavailable*` 边界。现在需要迁移 IPD 的真实 AI 基础设施语义，建立 CyanCruise 可替换、可审计、JDK 8 兼容的模型调用、结构化生成、工具调用和降级契约。

## What Changes

- 新增 AI 基础设施能力规格：统一定义同步聊天、指定模型调用、结构化 JSON 生成、工具调用循环、流式输出事件、超时/失败降级和用量统计语义。
- 新增平台中立的 AI gateway 边界：应用服务 SHALL 依赖 CyanCruise 内部接口，不直接依赖 DashScope SDK、Spring `SseEmitter`、Java 17 `HttpClient`、JPA 或 IPD Controller。
- 新增 CareerLoop AI 场景适配路线：助手聊天、职业计划生成、简历诊断、模拟面试追问/报告、今日任务拆解和长期记忆摘要 SHALL 通过统一 AI gateway 接入。
- 新增 function calling 安全规则：工具调用 SHALL 使用服务端认证 userId，模型不得通过参数决定用户归属；工具调用次数、超时和未知工具结果 SHALL 有明确上限与降级。
- 新增可测试降级实现：未配置真实 AI 或密钥时 SHALL 返回明确不可用状态或走规则兜底，不伪造真实 AI 能力。
- 暂不直接迁移 IPD 的 DashScope SDK 代码、Spring SSE、异步调度、语音 ASR/TTS、外部密钥配置、生产网关策略或前端页面；本 change 只生成提案文档，等待审阅后再 apply。

## Capabilities

### New Capabilities

- `ai-infrastructure`: 定义 CyanCruise 真实 AI 接入的 provider-neutral gateway、消息/响应契约、结构化输出、工具调用、安全降级、流式事件和 CareerLoop 场景适配要求。

### Modified Capabilities

- 无。本次新增横向 AI 基础设施能力，不改变 `assistant-chat`、`resume-diagnosis`、`career-plan-summary`、`interview-core` 或 `today-action-recommendation` 已归档业务 SHALL；后续 apply 通过既有可替换边界接入真实 AI。

## Impact

- IPD 来源路径：
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\AiService.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\impl\AiServiceImpl.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\ai\FunctionCallingService.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\ai\FunctionCallingServiceImpl.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\ai\tools\`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\impl\ConversationSummaryServiceImpl.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\impl\CareerPlanServiceImpl.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\impl\TaskDecomposerImpl.java`
  - 参考后续项：`InterviewServiceImpl.java`、`VoiceServiceImpl.java`
- CyanCruise 目标模块：
  - `code/base/v620-cc001-base-common/`：AI 消息、模型请求、响应、工具 schema、用量和错误状态 DTO。
  - `code/base/v620-cc001-base-helper/`：提示词片段、JSON 提取/校验、工具调用循环规则和降级解析规则。
  - `code/cloud01/v620-cc001-cloud01-app01/`：AI gateway、provider adapter、CareerLoop 场景服务接线和聚焦测试。
  - `openspec/specs/`、`docs/ipd-to-cyancruise-migration-map.md`：规格与迁移地图同步。
- 依赖影响：默认不新增外部依赖；若 apply 阶段需要 HTTP provider，应优先使用 JDK 8 可用能力或 Cosmic 平台能力，并说明密钥、超时、审计和模板约束。
- API 影响：现有 WebAPI 入参/出参保持兼容；真实 AI 只替换既有 `AssistantChatGenerator`、`ResumeDiagnosisAnalyzer`、计划生成、任务拆解等内部边界。
