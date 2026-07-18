## Context

简历诊断页面已经通过 `ResumeDiagnosisApplicationService` 读取当前用户的简历正文、目标岗位、岗位要求和画像上下文，并消费 `ResumeDiagnosisResultDto` 中的总分、分项评分和结构化修改建议。当前 `AiGatewayResumeDiagnosisAnalyzer` 使用通用 AI 网关生成 JSON，尚未调用金蝶 Agent 平台任务流。

平台侧已准备“CyanCruise 简历优化任务流”，其正式用途是由服务端传入诊断上下文，并返回与现有 DTO 兼容的诊断 JSON。当前阶段必须兼容 JDK 8 和 Cosmic 模块边界，且不得把平台凭据或用户身份放到浏览器；只有合法的智能体结果才能形成诊断记录。

## Goals / Non-Goals

**Goals:**

- 让现有简历诊断服务能够以可替换方式调用 Agent 平台任务流。
- 由服务端组装 `resumeText`、`targetJob`、`jobDescription` 和 `profileContext`，并保留既有简历所有权校验和 PDF 文本提取。
- 校验平台返回的 JSON 后复用现有诊断解析、保存和页面展示链路。
- 平台调用短暂失败时执行有限重试；重试仍失败则返回用户可理解的重试提示，不生成或保存规则版诊断。

**Non-Goals:**

- 不让浏览器直接调用 Agent 平台，也不暴露平台凭据或任务流编码。
- 不将用户简历写入通用知识库或平台长期知识库。
- 不重写简历诊断页面、数据模型或既有 PostgreSQL 保存结构。
- 不在本 change 中实现考研、就业等其他任务流的后端接入。

## Decisions

### 决策：新增 `ResumeDiagnosisAnalyzer` 的平台任务流实现

新增 `AgentPlatformResumeDiagnosisAnalyzer`，实现既有 `ResumeDiagnosisAnalyzer` 接口。`ResumeDiagnosisApplicationService` 继续只依赖该接口，因此页面、WebAPI、所有权校验、文件文本提取和结果保存均不感知平台调用细节。

相比修改页面直接调用平台，此方式复用已有 DTO 和 JSON 解析能力，也能让测试通过注入替身适配器覆盖成功和失败场景。

### 决策：通过官方 SDK 在服务端调用简历诊断智能体

在应用层新增基于 `bos-ai-sdk` 的 Agent 平台调用客户端，使用 `SDKClient.create().getAgentService().runAgent(...)` 调用已配置的简历诊断智能体。客户端读取服务端配置中的 `agentNumber`，将规范化诊断上下文作为 `query` 传入，并汇总 SDK 返回的聊天消息中的 JSON。

Cosmic 本地安装已提供 `mservice-cosmic/lib/bos/bos-ai-sdk-8.0.jar`，并由 `base-common` 的 `bos` 依赖目录传递给业务模块；因此无需新增外部 Maven 依赖，也无需向浏览器或业务代码写入访问令牌。

浏览器仅调用已有 CyanCruise 简历诊断 WebAPI。用户身份仍由 Cosmic 服务端上下文和既有简历所有权校验确认。

### 决策：任务流使用单一 JSON 请求与 JSON 响应契约

适配器将 `resumeText`、`targetJob`、`jobDescription` 和 `profileContext` 序列化为任务流 `question` 输入。任务流 `answer` 必须只返回诊断 JSON，字段与 `ResumeDiagnosisResultDto` 和 `ResumeRevisionSuggestionDto` 对齐。

在正式 API 是否支持命名任务流输入尚未确认前，单一 JSON 请求可避免依赖未验证的多参数调用方式；后续确认支持后可在适配器内部演进，不改变上层诊断接口。

### 决策：相同诊断输入在短时内复用 AI 结果

简历诊断输入由简历正文、目标岗位、岗位要求和用户画像组成。服务端对这些字段和用户标识生成摘要键，并在 30 分钟内复用同一次已校验的 AI 结果；任一字段发生变化时必须重新调用智能体。这样既保留岗位要求变化带来的重新分析，也避免用户在没有修改输入时反复点击得到不同分数。

服务端始终合并已保存的用户画像摘要，即使浏览器已提交局部画像文本也不得遗漏；页面参考信息必须明确标记“用户画像”。

### 运行时配置与默认报文

后端通过以下 JVM 系统属性启用任务流调用；地址、令牌和任务流编码均不得写入前端资源或业务代码：

```properties
cc001.agent.platform.resume.enabled=true
cc001.agent.platform.resume.agentNumber=<简历诊断智能体编码>
```

同名环境变量也可用于运行环境配置，例如 `CC001_AGENT_PLATFORM_RESUME_ENABLED=true` 和 `CC001_AGENT_PLATFORM_RESUME_AGENT_NUMBER=<简历诊断智能体编码>`。系统属性优先于环境变量。

SDK 的默认请求为：

```json
{
  "agentNumber": "<简历诊断智能体编码>",
  "query": "{\"resumeText\":\"...\",\"targetJob\":\"...\",\"jobDescription\":\"...\",\"profileContext\":\"...\"}"
}
```

智能体负责调用其关联的“CyanCruise 简历优化任务流”，并只返回诊断 JSON。SDK 客户端从流式聊天消息中汇总 JSON；页面和 WebAPI 均无需感知 SDK 细节。

### 决策：平台失败采用可观测的有限重试

平台配置缺失、调用超时、HTTP 失败、响应为空或 JSON 不合法时，适配器对可恢复问题执行有限重试。重试仍失败时，`ResumeDiagnosisApplicationService` 返回可重试失败，不创建诊断记录，也不更新简历诊断分数。日志只记录调用状态和摘要，不记录完整简历正文、凭据或敏感响应。

该策略避免把基础规则结果伪装为智能诊断，同时通过普通中文重试提示保持用户体验；状态记录用于排查平台配置和响应格式问题。

## Risks / Trade-offs

- [智能体编码未配置] → 服务端返回配置未完成的可重试失败，不生成诊断记录；配置编码后即可启用真实 AI 调用。
- [模型返回 JSON 字段缺失或分数不一致] → 服务端复用既有解析与分数校验，并在有限重试后返回失败且不保存无效结果。
- [简历正文包含个人数据] → 仅在单次服务端调用中传递，日志不记录正文，浏览器不持有平台凭据。
- [平台调用变慢] → 使用可配置超时、有限重试和明确的用户提示，避免请求无限等待。

## Migration Plan

1. 保留可替换的 `ResumeDiagnosisAnalyzer` 接口，但正式诊断只接受智能体返回的合法结构化结果。
2. 新增平台任务流客户端和分析器，通过服务端配置启用。
3. 在测试环境配置简历诊断智能体编码，验证智能体关联任务流后返回诊断 JSON。
4. 启用平台分析器，验证失败时页面提示重试且数据库不新增诊断记录。
5. 若需停止服务，关闭平台配置并让页面显示暂不可用提示，不恢复规则版诊断。

## Open Questions

- 运行中 Cosmic 环境的 JVM 系统属性由哪个部署入口管理，以便配置简历诊断智能体编码？
