## 1. Shared Contracts

- [x] 1.1 新增助手聊天请求、响应、persona 常量、会话 DTO 和消息 DTO，保持 JDK 8 兼容。
- [x] 1.2 DTO SHALL 不依赖 Spring、JPA、Lombok、Jackson Controller 注解、DashScope/Qwen SDK 或外部 AI SDK。
- [x] 1.3 明确 persona、消息 role、token/cost 零值、默认标题和错误状态常量。

## 2. Helper Rules

- [x] 2.1 新增助手聊天 helper，标准化 `MENTOR`、`CHALLENGER`、`INTERVIEWER` persona，并对空白或未知 persona 回退为 `MENTOR`。
- [x] 2.2 实现三种 persona 的简体中文系统提示词选择规则。
- [x] 2.3 实现上下文组装规则：system 消息、历史消息、本次 user 消息按顺序输出。
- [x] 2.4 实现画像上下文、长期记忆摘要和用户事实片段的非空拼接规则。
- [x] 2.5 实现空消息校验、消息 role 标准化和首条用户消息派生标题规则。
- [x] 2.6 增加 helper 聚焦测试，覆盖 persona 回退、三种提示词、上下文顺序、片段拼接和标题派生。

## 3. Storage And Application Boundary

- [x] 3.1 新增 `AssistantChatGenerator` 或等价聊天生成边界，默认实现不调用真实 AI。
- [x] 3.2 新增会话与消息存储边界，支持创建、按用户倒序查询、读取消息、追加消息、删除会话和所有权判断。
- [x] 3.3 新增默认存储实现，允许测试跨新的 storage 或 application service 实例读回会话与消息。
- [x] 3.4 新增画像上下文、长期记忆摘要和用户事实渲染边界，默认实现可返回空片段。
- [x] 3.5 新增 `AssistantChatApplicationService`，支持发送聊天、创建会话、查询会话、读取消息、追加消息对和删除会话。
- [x] 3.6 应用服务 SHALL 对所有 sessionId 操作执行用户所有权校验。
- [x] 3.7 追加消息对后 SHALL 更新会话 updatedAt，并在默认标题场景下派生简短标题。

## 4. WebAPI

- [x] 4.1 新增 Cosmic WebAPI 发送助手聊天入口。
- [x] 4.2 新增 Cosmic WebAPI 创建会话和查询用户会话入口。
- [x] 4.3 新增 Cosmic WebAPI 读取会话消息和追加消息对入口。
- [x] 4.4 新增 Cosmic WebAPI 删除会话入口。
- [x] 4.5 增加 WebAPI 聚焦测试，覆盖发送聊天、历史查询、追加消息、删除会话和跨用户拒绝。

## 5. Migration Documents

- [x] 5.1 更新 `docs/ipd-to-cyancruise-migration-map.md` 中助手聊天状态和后续项。
- [x] 5.2 记录真实 AI、function calling、SSE、长期记忆摘要生成、事实抽取、webapp 页面和最终 Cosmic datamodel 仍为后续迁移项。
- [x] 5.3 将 delta spec 同步到 `openspec/specs/assistant-chat/`。

## 6. Validation

- [x] 6.1 运行 `openspec validate migrate-assistant-chat --strict`。
- [x] 6.2 运行 `openspec validate --all --strict`。
- [x] 6.3 设置 JDK 8 后运行相关 Gradle 测试。
- [x] 6.4 设置 JDK 8 后运行 `.\gradlew.bat clean build`。
