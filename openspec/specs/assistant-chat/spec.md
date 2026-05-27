# 助手聊天规格

## Purpose
定义 CyanCruise 助手聊天的多角色 persona、提示词上下文、会话历史、消息持久化、所有权校验和应用边界契约，为后续真实 AI、长期记忆、SSE、webapp 页面和 Cosmic datamodel 适配提供稳定业务语义。

## Requirements
### Requirement: 管理助手 persona
系统 SHALL 支持 `MENTOR`、`CHALLENGER` 和 `INTERVIEWER` 三种助手 persona。persona SHALL 决定系统提示词的语气、求职场景聚焦和历史会话归属。未提供 persona、空白 persona 或未知 persona SHALL 回退为 `MENTOR`。

#### Scenario: 使用默认求职教练
- **WHEN** 调用方未提供 persona
- **THEN** 系统使用 `MENTOR` 的求职教练提示词

#### Scenario: 使用严格反馈教练
- **WHEN** 调用方提供 persona 为 `CHALLENGER`
- **THEN** 系统使用直接、公平、追问证据和下一步行动的严格反馈提示词

#### Scenario: 使用面试练习角色
- **WHEN** 调用方提供 persona 为 `INTERVIEWER`
- **THEN** 系统使用一次只问一个问题、先反馈再追问的模拟面试提示词

#### Scenario: 未知角色回退
- **WHEN** 调用方提供未知 persona
- **THEN** 系统回退为 `MENTOR`，且不会中断聊天请求

### Requirement: 组装助手聊天上下文
系统 SHALL 为每次助手聊天组装有序消息上下文。上下文第一条 SHALL 是 `system` 消息，内容由 persona 基础提示词、可用职业画像提示片段、可选长期记忆摘要和可选用户事实片段组成；随后 SHALL 保留调用方提供的历史消息顺序，并在末尾追加本次用户消息。

#### Scenario: 组装基础上下文
- **WHEN** 用户发送消息且没有可用画像、记忆或事实片段
- **THEN** 系统生成 persona system 消息，并在末尾追加本次 user 消息

#### Scenario: 注入职业画像
- **WHEN** 用户发送消息且画像上下文源返回非空提示片段
- **THEN** system 消息包含 persona 提示词和画像提示片段

#### Scenario: 注入长期记忆
- **WHEN** 用户发送消息且该用户该 persona 存在长期记忆摘要
- **THEN** system 消息包含长期记忆摘要，并按 persona 隔离

#### Scenario: 保留历史顺序
- **WHEN** 调用方提交多条历史消息
- **THEN** 系统在 system 消息之后按原顺序保留这些消息，再追加本次 user 消息

### Requirement: 发送助手聊天消息
系统 SHALL 支持用户发送助手聊天消息。请求 SHALL 包含用户 ID、message、可选 history、可选 persona 和可选 sessionId。系统 SHALL 校验 message 非空，调用可替换聊天生成边界，并返回助手回复。

#### Scenario: 发送有效消息
- **WHEN** 用户提交非空 message
- **THEN** 系统基于组装后的上下文生成并返回 assistant reply

#### Scenario: 拒绝空消息
- **WHEN** 用户提交空白 message
- **THEN** 系统拒绝请求并返回明确错误

#### Scenario: 聊天生成边界不可用
- **WHEN** 未配置真实 AI 或聊天生成边界返回不可用
- **THEN** 系统返回明确的未配置或生成失败错误，而不是伪造长期 AI 能力

### Requirement: 管理助手会话
系统 SHALL 支持创建、查询和删除助手会话。会话 SHALL 包含 sessionId、userId、title、modelName、persona、createdAt 和 updatedAt。创建会话时 SHALL 使用当前用户 ID 或显式用户 ID，title 为空时 SHALL 使用默认标题，persona 为空时 SHALL 使用 `MENTOR`。

#### Scenario: 创建新会话
- **WHEN** 用户创建助手会话并提交 title 和 persona
- **THEN** 系统保存该用户的会话，并返回 sessionId、title 和 persona

#### Scenario: 默认标题和角色
- **WHEN** 用户创建会话但未提交 title 或 persona
- **THEN** 系统使用默认标题和 `MENTOR`

#### Scenario: 查询用户会话
- **WHEN** 用户请求自己的助手会话列表
- **THEN** 系统按 updatedAt 倒序返回该用户会话

#### Scenario: 删除会话
- **WHEN** 用户删除自己的助手会话
- **THEN** 系统删除该会话及其消息历史

### Requirement: 维护助手消息历史
系统 SHALL 支持按会话追加和读取助手消息历史。消息 SHALL 包含 msgId、sessionId、role、content、promptTokens、completionTokens、totalTokens、costMicros 和 createdAt。role SHALL 至少支持 `user`、`assistant` 和 `system`。消息历史 SHALL 按 createdAt 或追加顺序升序返回。

#### Scenario: 追加用户和助手消息对
- **WHEN** 用户向自己的会话追加一轮 userMessage 和 assistantReply
- **THEN** 系统依次保存 `user` 消息和 `assistant` 消息

#### Scenario: 自动派生会话标题
- **WHEN** 会话标题仍为默认标题且追加的 userMessage 非空
- **THEN** 系统使用首条用户消息派生简短标题

#### Scenario: 读取会话消息
- **WHEN** 用户请求自己的会话消息
- **THEN** 系统按发送顺序返回该会话全部消息

#### Scenario: 保留 token 与成本字段
- **WHEN** 聊天适配器提供 token 或成本统计
- **THEN** 系统保存 promptTokens、completionTokens、totalTokens 和 costMicros；未提供时使用零值

### Requirement: 强制助手会话所有权校验
系统 SHALL 对所有按 sessionId 进行的读取、追加、删除、摘要触发和事实抽取触发执行用户所有权校验。调用方 SHALL NOT 能读取、修改或删除其他用户的助手会话与消息。

#### Scenario: 拒绝跨用户读取消息
- **WHEN** 用户请求不属于自己的 sessionId 消息
- **THEN** 系统拒绝该请求

#### Scenario: 拒绝跨用户追加消息
- **WHEN** 用户向不属于自己的 sessionId 追加消息
- **THEN** 系统拒绝该请求，且不保存任何消息

#### Scenario: 拒绝跨用户删除会话
- **WHEN** 用户删除不属于自己的 sessionId
- **THEN** 系统拒绝该请求，且保留会话与消息

### Requirement: 暴露助手聊天 Cosmic WebAPI
系统 SHALL 通过 Cosmic WebAPI 暴露助手聊天能力。WebAPI SHALL 支持发送聊天消息、创建会话、查询用户会话、读取会话消息、追加消息对和删除会话，并保持与已迁移 CareerLoop WebAPI 的显式 userId 风格兼容。

#### Scenario: WebAPI 发送聊天
- **WHEN** 调用方提交 userId 和聊天请求
- **THEN** WebAPI 返回助手回复

#### Scenario: WebAPI 管理历史
- **WHEN** 调用方提交 userId、sessionId 和历史操作请求
- **THEN** WebAPI 执行所有权校验后返回会话或消息结果

#### Scenario: WebAPI 删除会话
- **WHEN** 调用方提交 userId 和自己的 sessionId
- **THEN** WebAPI 删除该会话及其消息并返回成功结果

### Requirement: 保持可替换的助手边界
系统 SHALL 通过可替换边界完成聊天生成、会话存储、消息存储、画像上下文、长期记忆摘要和用户事实渲染。默认实现 SHALL 可在没有 Spring Boot、JPA、DashScope/Qwen、SSE、Spring 异步任务或 Cosmic datamodel 的情况下通过测试；未来平台 AI、SSE 和 Cosmic datamodel 适配 SHALL 能替换这些边界，而无需修改 DTO、helper 或 WebAPI 契约。

#### Scenario: 默认实现可测试
- **WHEN** 本地测试运行且没有外部 AI 或 Cosmic datamodel
- **THEN** 系统仍可验证 persona 选择、上下文组装、会话历史、所有权校验和 WebAPI 契约

#### Scenario: 替换为真实 AI 适配器
- **WHEN** 后续真实 AI 适配器实现完成
- **THEN** 它可以通过同一聊天生成边界替换默认实现

#### Scenario: 替换为 Cosmic 存储
- **WHEN** 后续 Cosmic datamodel 适配器实现完成
- **THEN** 它可以通过同一会话和消息存储边界替换默认存储
