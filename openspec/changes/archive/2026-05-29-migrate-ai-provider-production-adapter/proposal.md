## Why

现有 `ai-infrastructure` 已经固化了 provider-neutral gateway、AI DTO、结构化输出、工具调用和场景 adapter 边界，但生产环境仍停留在 unavailable/fake provider 或测试替身，CareerLoop 的助手、面试、任务拆解等能力无法在苍穹租户中调用真实大模型。

本 change 迁移 IPD 中 DashScope/OpenAI-compatible 调用的业务语义，落到 CyanCruise 的 JDK 8/Cosmic 可配置生产 adapter 上，补齐超时、重试、错误分类、用量审计、密钥脱敏和禁用降级规则。

## What Changes

- 新增生产 AI provider adapter 规格，定义 OpenAI-compatible chat/completions 请求、响应、错误、usage、tool calls 和 stream chunk 的平台中立映射。
- 新增 JDK 8 兼容的 provider 配置与工厂规则，通过系统属性或 Cosmic 配置启用真实 provider；默认保持 disabled-safe，不伪造真实 AI 回复。
- 将 IPD `AiServiceImpl` 的模型选择、默认 system prompt、timeout、一次 5xx 重试、choices/usage 解析和 fallback 语义迁移为 CyanCruise gateway adapter 规则。
- 将 IPD `FunctionCallingServiceImpl` 的工具调用 loop cap、服务端 userId 注入和 unknown tool 处理纳入生产 adapter 验证范围。
- 记录 IPD 来源路径、CyanCruise 目标模块、数据/接口映射、暂不迁移项和租户验证方式。
- 不新增第三方 SDK 依赖；如实现阶段需要 HTTP/JSON 依赖，必须复用仓库已有能力或说明不破坏 Cosmic/KDDT 模板约束。

## Capabilities

### New Capabilities

- `ai-provider-production-adapter`: 真实 AI provider 生产 adapter 的启用、配置、请求/响应映射、错误降级、审计、密钥脱敏、工具调用安全和租户验证契约。

### Modified Capabilities

- `ai-infrastructure`: 将“配置完成后可调用真实 provider”的抽象要求细化为可启用的生产 adapter、provider 诊断和禁用安全行为。

## Impact

- IPD 来源：
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\AiService.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\impl\AiServiceImpl.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\ai\FunctionCallingService.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\ai\FunctionCallingServiceImpl.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\service\ai\tools\`
  - `F:\Project\IPD\frontend\src\api\ai.ts`
- CyanCruise 目标：
  - `code/base/v620-cc001-base-common` 的 AI DTO/常量补充。
  - `code/base/v620-cc001-base-helper` 的 AI 请求构建、响应解析、脱敏和诊断 helper。
  - `code/cloud01/v620-cc001-cloud01-app01` 的 `mservice.ai` gateway/provider adapter、工厂、配置和测试。
  - `webapp/isv/v620/careerloop/careerloop-routes.json` 的真实 AI provider 状态/待迁移项元数据。
  - `docs/ipd-to-cyancruise-migration-map.md` 的迁移地图回填。
- 验证：
  - `openspec validate migrate-ai-provider-production-adapter --strict`
  - AI provider adapter/helper focused tests
  - `openspec validate --all --strict`
  - JDK 8 `.\gradlew.bat clean build`
