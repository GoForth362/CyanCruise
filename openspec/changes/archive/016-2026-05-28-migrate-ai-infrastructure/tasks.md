## 1. Shared AI Contracts

- [x] 1.1 新增 JDK 8 兼容 AI message、request、response、usage、tool schema、tool call 和 stream event DTO。
- [x] 1.2 明确 role、finishReason、errorCode、fallback 状态、token/cost 零值和 modelName 默认值。
- [x] 1.3 确保 DTO 不依赖 Spring、DashScope SDK、Jackson 注解、JPA、Lombok 或 Java 17 API。

## 2. Helper Rules

- [x] 2.1 新增 AI helper，支持消息顺序保留、默认 system prompt 注入和已有 system prompt 检测。
- [x] 2.2 新增 JSON 提取与校验 helper，覆盖 markdown fence、前后缀文本、object/array 提取、空响应和缺字段。
- [x] 2.3 新增 function calling loop 纯规则 helper，覆盖最大工具调用次数、未知工具、工具异常和最终回复降级。
- [x] 2.4 增加 helper 聚焦测试。

## 3. Gateway And Providers

- [x] 3.1 在 app01 新增 `AiGateway` 或等价 provider-neutral 边界。
- [x] 3.2 新增 unavailable/fake provider，用于未配置真实 AI 时返回明确不可用或测试响应。
- [x] 3.3 新增 JDK 8/Cosmic 兼容 provider adapter 接口，预留 DashScope compatible endpoint 或 Cosmic 平台 AI 接入。
- [x] 3.4 新增超时、可选一次重试、错误分类和最小审计字段。

## 4. CareerLoop Scenario Wiring

- [x] 4.1 将助手聊天生成边界接入 AI gateway，同时保留未配置时的明确错误。
- [x] 4.2 为职业计划生成新增结构化 AI 适配边界，解析失败时保留默认计划兜底。
- [x] 4.3 为简历诊断新增 AI analyzer 适配边界，失败时保留规则型诊断。
- [x] 4.4 为今日任务拆解新增 AI decomposition 边界，失败时保留规则拆解。
- [x] 4.5 为模拟面试追问/报告和长期记忆摘要预留 gateway 接线点和测试 fake。

## 5. Function Calling And Tools

- [x] 5.1 新增工具 registry 契约，工具 schema SHALL 不暴露 userId 参数。
- [x] 5.2 新增工具执行边界，执行时 SHALL 使用服务端认证 userId。
- [x] 5.3 增加 function calling 聚焦测试，覆盖服务端 userId 注入、未知工具、调用上限和工具异常。

## 6. Stream Events

- [x] 6.1 新增平台中立 stream event DTO，覆盖 token、done、error。
- [x] 6.2 新增 provider stream 转换边界，不直接依赖 Spring `SseEmitter`。
- [x] 6.3 增加 stream event 顺序和错误事件测试。

## 7. Documentation

- [x] 7.1 更新 `docs/ipd-to-cyancruise-migration-map.md`，记录 AI 基础设施迁移状态、IPD 来源路径、目标模块和暂不迁移项。
- [x] 7.2 将 delta spec 同步到 `openspec/specs/ai-infrastructure/`。
- [x] 7.3 记录真实 provider、密钥配置、流式传输、语音和页面适配仍为后续项。

## 8. Validation And Delivery

- [x] 8.1 运行 `openspec validate migrate-ai-infrastructure --strict`。
- [x] 8.2 运行 `openspec validate --all --strict`。
- [x] 8.3 设置 JDK 8 后运行相关 Gradle 测试。
- [x] 8.4 设置 JDK 8 后运行 `.\gradlew.bat clean build`。
- [x] 8.5 verify 通过后归档 change，按下一个顺序编号命名归档目录。
- [x] 8.6 本地 commit 并推送当前分支 `codex/migrate-ai-infrastructure`。
