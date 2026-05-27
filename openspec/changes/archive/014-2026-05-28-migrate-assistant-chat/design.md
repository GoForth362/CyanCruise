## Context

CyanCruise 已有职业画像、今日行动、简历、职业计划、模拟面试和简历诊断后端基础能力，其中今日行动已经将部分行动入口指向 `/pages/assistant/index`。IPD 的助手聊天由 Spring Boot Controller、JPA 会话/消息实体、DashScope/Qwen 服务、function calling、SSE、长期摘要、事实抽取和 uni-app 页面组合而成；这些技术实现不能直接迁移到 Kingdee Cosmic/JDK 8 二开工程。

本次迁移只抽取稳定业务语义：多 persona 求职助手、聊天上下文组装、会话/消息历史、所有权校验和 WebAPI 契约。真实 AI 生成、SSE 流式输出、长期记忆摘要生成、用户事实抽取、最终 Cosmic datamodel 和 webapp 页面在本 change 中只保留可替换边界或后续项。

## Goals / Non-Goals

**Goals:**

- 定义并实现 JDK 8 兼容的助手聊天 DTO、persona 常量、会话和消息契约。
- 提供纯 Java helper：persona 标准化、系统提示词选择、上下文消息组装、空消息校验、默认标题和首条消息标题派生。
- 提供应用服务边界：发送聊天、创建会话、查询会话、读取消息、追加消息对、删除会话。
- 对所有 sessionId 操作执行用户所有权校验。
- 提供可替换边界：聊天生成、会话/消息存储、画像上下文渲染、长期记忆渲染、用户事实渲染。
- 暴露 Cosmic WebAPI 风格入口，保持与当前迁移能力的显式 userId 参数风格一致。
- 增加聚焦测试，覆盖 persona、上下文组装、历史顺序、默认存储读回、标题派生、所有权拒绝和 WebAPI 契约。

**Non-Goals:**

- 不迁移 Spring Boot、JPA、Flyway、Lombok、Jackson Controller 注解或 uni-app/Vue 页面。
- 不实现 DashScope/Qwen function calling、真实 AI 联网调用或外部 AI SDK。
- 不实现 SSE 流式输出；仅保留后续流式适配的边界语义。
- 不实现 Spring 异步长期摘要生成、事实抽取任务、画像标签管理或微信订阅通知。
- 不新增外部依赖，不修改 Cosmic/KDDT 模板约束。
- 不在本次实现最终 Cosmic datamodel；默认存储作为可替换适配。

## Decisions

### 1. 将 persona 作为稳定公共契约

`MENTOR`、`CHALLENGER`、`INTERVIEWER` SHALL 放在 base-common 的常量或 DTO 旁，helper 只依赖这些字符串常量并处理未知值回退。

原因：IPD 的 persona 是用户可见的业务模式，不是某个 AI SDK 的参数。把它放入公共契约后，WebAPI、webapp 和后续 AI 适配器可以共享同一枚举语义。

替代方案是只在应用服务中硬编码 persona；这会让前端、测试和后续 AI 适配重复定义角色。

### 2. 将提示词组装放在 helper 模块

提示词组装 SHALL 是纯 Java 规则：先放 system message，再追加 history，最后追加本次 user message。画像、长期记忆和用户事实通过字符串提供方注入，helper 只负责拼接非空片段和保序。

原因：提示词组装是可测试的业务规则，不应该绑在 Controller 或 AI 客户端里。这样没有真实 AI 时也能验证“给模型看到什么上下文”。

替代方案是由 AI 适配器自行组装上下文；这会让不同适配器出现不一致提示词，且更难做聚焦测试。

### 3. 发送聊天与追加历史分离，但共享会话校验

发送聊天 SHALL 接收历史并返回 reply；追加历史 SHALL 明确保存 user/assistant 消息对。若请求携带 sessionId，应用服务在触发后续摘要/事实边界前 SHALL 校验 session 所有权。

原因：IPD 前端先调用 `/send` 获得回复，再调用 `/history/session/{id}/append` 保存消息。本次迁移保留这个契约语义，同时允许后续 WebAPI 在一次事务中组合发送和保存。

替代方案是发送时强制保存历史；这会改变现有页面契约，也会让无 session 的临时问答更难支持。

### 4. 默认聊天生成器不伪造真实 AI

默认 `AssistantChatGenerator` SHALL 是可替换边界。实现阶段可以提供测试用确定性实现或明确未配置错误，但不能宣称已完成真实 AI function calling。

原因：用户价值在于稳定契约和集成点；真实 AI 接入涉及平台网关、密钥、审计、成本和流式协议，应该单独 change 评审。

替代方案是引入外部 SDK 或直接复用 IPD DashScope 代码；这会破坏 JDK 8/Cosmic 模板约束并扩大迁移风险。

### 5. 默认存储先服务测试和迁移闭环

会话/消息存储 SHALL 定义接口，默认实现可采用内存或文件型持久化适配，测试 SHALL 验证跨新 service/storage 实例读回。后续 Cosmic datamodel 只需替换存储边界。

原因：当前多个已迁移模块都采用“可替换边界 + 默认可测试实现”的路径，能避免在 datamodel 未定型时阻塞业务规则迁移。

替代方案是本次直接设计 Cosmic 数据模型；这会扩大 schema 评审和 KDDT 适配范围，不适合先 propose 的助手聊天基础设施。

## Risks / Trade-offs

- [Risk] 默认实现不提供真实 AI 质量 -> Mitigation：明确把真实 AI、function calling 和 SSE 列为后续 change；本次只承诺契约、上下文和历史。
- [Risk] 长期记忆和用户事实暂未真正生成 -> Mitigation：保留渲染边界和触发点，先允许未来摘要/事实服务接入。
- [Risk] 会话历史与发送聊天分离可能导致调用方忘记保存 -> Mitigation：WebAPI 文档和任务中保留追加消息对入口，后续可增加组合发送保存入口。
- [Risk] 文件型或内存型默认存储不等同最终生产数据模型 -> Mitigation：所有业务逻辑依赖存储接口，后续 Cosmic datamodel 替换时不修改 helper 和 DTO。
- [Risk] persona 提示词后续需要产品调整 -> Mitigation：集中在 helper/常量中维护，并用测试锁定默认回退和上下文顺序。

## Migration Plan

1. 新增助手聊天 DTO、persona 常量、会话 DTO、消息 DTO 和请求/响应对象，保持 JDK 8 兼容。
2. 新增 helper，覆盖 persona 标准化、提示词选择、上下文组装、消息校验和标题派生。
3. 新增聊天生成、存储、画像上下文、长期记忆和用户事实渲染边界；默认实现不引入外部 SDK。
4. 新增应用服务，完成发送、会话管理、消息管理和所有权校验。
5. 新增 Cosmic WebAPI 和聚焦测试。
6. 更新主规格、迁移地图和归档 change；运行 OpenSpec 与 JDK 8 Gradle 验证。

## Open Questions

- 真实 AI 适配使用 Cosmic 平台能力、内部网关还是外部 DashScope，需要在 AI 接入 change 中确认。
- SSE 或增量流式输出是否由 Cosmic WebAPI 支持，需要在前端页面迁移前确认。
- 长期摘要、用户事实和画像标签是否进入统一画像 datamodel，需要在画像记忆或 AI 基础设施 change 中细化。
