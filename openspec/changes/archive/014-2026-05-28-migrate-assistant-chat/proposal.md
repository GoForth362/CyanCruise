## Why

IPD 的 CareerLoop 已经把“助手聊天”作为今日行动、简历优化、面试准备和职业计划之间的共用 AI 入口；CyanCruise 当前已有多个能力会跳转到 `/pages/assistant/index`，但后端还缺少统一的多角色聊天、会话历史和提示词组装契约。迁移助手聊天后，后续 AI 接入、长期记忆和 webapp 页面可以围绕稳定业务语义继续演进。

## What Changes

- 新增助手聊天业务契约：支持用户发送消息、携带有序历史、选择 persona，并返回助手回复。
- 新增多角色 persona 契约：迁移 `MENTOR`、`CHALLENGER`、`INTERVIEWER` 三种角色及其简体中文求职场景提示词语义，未知角色 SHALL 回退到 `MENTOR`。
- 新增聊天上下文组装规则：系统消息 SHALL 由 persona 基础提示词、可用职业画像提示片段、可选长期记忆摘要和可选用户事实片段组成，且 SHALL 保持有序消息历史。
- 新增会话与消息历史契约：支持创建会话、按用户查询会话、读取会话消息、追加用户/助手消息对、删除会话及其消息，并强制用户所有权校验。
- 新增可替换 AI 与存储边界：默认实现 SHALL 可在没有 Spring Boot、JPA、DashScope/Qwen、SSE 或 Cosmic datamodel 的情况下通过测试；后续平台 AI、流式输出和 datamodel 适配可替换边界。
- 暂不迁移 Spring Boot Controller/JPA/Flyway、Lombok、DashScope/Qwen function calling、SSE 实现、Spring 异步摘要/事实抽取、Vue/uni-app 页面和微信订阅通知。

## Capabilities

### New Capabilities

- `assistant-chat`: 定义多角色助手聊天、提示词组装、会话历史、消息持久化、所有权校验和 Cosmic WebAPI 入口契约。

### Modified Capabilities

- 无。本次不改变今日行动、画像、简历、面试或职业计划既有 SHALL；助手聊天作为新的共用入口消费这些能力已暴露的摘要信号。

## Impact

- IPD 来源路径：
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\controller\ChatController.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\controller\ChatHistoryController.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\config\AiPersonas.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\model\entity\AssistantSession.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\model\entity\AssistantMessage.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\repository\AssistantSessionRepository.java`
  - `F:\Project\IPD\backend\src\main\java\com\group1\career\repository\AssistantMessageRepository.java`
  - 参考页面契约：`F:\Project\IPD\frontend\src\pages\assistant\index.vue`、`F:\Project\IPD\frontend\src\pages\assistant\history.vue`
- CyanCruise 目标模块：
  - `code/base/v620-cc001-base-common/`：JDK 8 DTO、persona 常量、请求/响应和会话/消息契约。
  - `code/base/v620-cc001-base-helper/`：纯 Java persona 选择、提示词组装、消息校验和标题派生规则。
  - `code/cloud01/v620-cc001-cloud01-app01/`：应用服务、可替换聊天/存储/画像上下文边界、Cosmic WebAPI 和聚焦测试。
  - `openspec/specs/`、`docs/ipd-to-cyancruise-migration-map.md`：规格和迁移地图同步。
- 不新增外部依赖；真实 AI、SSE、长期记忆摘要、事实抽取、webapp 页面和最终 Cosmic datamodel 适配保留为后续 change。
