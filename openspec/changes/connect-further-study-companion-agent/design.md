## Context

三个升学 ApplicationService 已保留现有 WebAPI 与 DTO，但当前主动拒绝生成，以避免过去的规则模板被误认为真实 AI。金蝶 Agent SDK 调用边界、运行时配置和结果收集能力已经存在；平台侧“升学陪伴智能体”也已发布并在预览中验证，其内部通过一个 `question:String` 参数调用统一任务流。

本次需要在不改页面接口、不暴露平台凭据、不覆盖用户现有存储改动的前提下，把 13 个页面操作统一接到一个智能体编号，并严格校验结构化结果。

## Goals / Non-Goals

**Goals:**

- 一个服务端适配器覆盖 13 个固定 `taskType`，避免三个业务服务各自重复 Agent 调用代码。
- 将现有请求 DTO 原样放入 `payload`，同时写入固定 `mode`、服务端当前日期和最小用户上下文。
- 仅接受与请求任务类型一致、外层和内层状态均为 `OK` 的结果。
- 将 `result` 映射为现有结果 DTO，保留 WebAPI 与页面展示契约。
- 配置缺失、平台失败、补充资料状态或结构错误均明确失败，不生成规则兜底结果。

**Non-Goals:**

- 不在本次修改平台智能体、任务流或知识库内容。
- 不新增数据库表，也不改变现有进一步升学记录持久化实现。
- 不让浏览器直接调用 Agent SDK，不在源码中硬编码 `agentNumber`。
- 不在服务端重新评分或修正模型给出的业务结论；只做契约和安全校验。

## Decisions

### 使用一个共享配置和一个智能体入口

新增 `cc001.agent.platform.study.companion.*` 配置，三个方向的 13 个具体功能都通过同一 `agentNumber` 调用已发布智能体。现有 `study.postgraduate`、`study.recommendation`、`study.abroad` 配置继续服务于完整年度路线生成，避免改变已有路线能力。

备选方案是页面功能复用三个路线智能体，但它们的输出契约只覆盖年度路线，无法稳定承载错题、文书、意向信等不同结果，因此不采用。

### 统一请求信封并传递原始 JSON 文本

适配器生成以下信封：`mode=FURTHER_STUDY_ANALYSIS`、固定 `taskType`、服务端 `currentDate`、请求 DTO 对应的 `payload`、包含服务端确认 `userId` 的 `profileContext`、空的 `userMaterials`。序列化结果作为 Agent SDK 的原始 query 传入，不再额外 JSON 字符串编码，确保智能体能读取顶层 `taskType`。

用户资料暂只来自当前页面请求；后续如需关联资料库，可在服务端读取当前用户资料后填充 `userMaterials`，不接受浏览器指定其他用户资料。

### 严格解析外层状态后映射现有 DTO

适配器从 Agent 返回文本中提取 JSON，允许 Markdown fence、一次字符串编码或常见 `answer/content/data` 包装。最终对象必须包含与请求一致的 `taskType`、`status=OK`，且 `result.status=OK`。随后使用忽略未知字段的 Jackson 映射到调用方指定结果 DTO，并要求结果非空。

`NEED_MORE_INFO` 的可读 message 会作为普通中文异常返回页面；没有 message 时使用统一的“请补充必要信息后重试”。这保留当前同步 WebAPI 契约，不引入新的联合返回类型。

### 默认构造器装配真实 Agent，测试保留依赖注入

三个 ApplicationService 默认构造器从 `cc001.agent.platform.study.companion` 加载配置：SDK 可用时创建 `KingdeeAgentSdkTaskFlowClient`，否则创建不可用适配器并在调用时明确报错。新增可注入适配器的构造器供单元测试使用，已有 Helper 构造器保留兼容但不再产生业务结果。

## Risks / Trade-offs

- [智能体输出字段与 DTO 不一致] → 严格解析并通过 13 个契约测试覆盖，失败时不返回半成品。
- [Agent 对 query 再次编码后无法识别 taskType] → 该配置显式关闭 `jsonEncodeAgentQuery`，直接传递完整 JSON 原文。
- [模型返回 NEED_MORE_INFO 但页面只支持结果 DTO] → 当前返回清晰中文异常；后续可独立扩展 WebAPI 状态契约。
- [同一智能体承载 13 个任务导致提示词漂移] → 服务端固定 taskType 枚举并校验响应任务类型，拒绝串台结果。
- [已有脏工作区发生冲突] → 只编辑目标服务和新增适配器/测试，保留 Helper 与其他业务改动。

## Migration Plan

1. 部署代码但保持 `cc001.agent.platform.study.companion.enabled=false`，页面继续明确提示服务未配置。
2. 在运行环境配置 `enabled=true` 与已发布智能体编号 `agent-C4255782`；编号只进入本地/部署配置，不提交为业务常量。
3. 依次联调考研、保研、留学 13 项任务并检查服务端日志中的脱敏目标与事件类型。
4. 若出现异常，关闭 `enabled` 即可回滚到明确不可用状态，不会恢复虚假规则结果。

## Open Questions

- 当前平台智能体编号由截图确认为 `agent-C4255782`，生产租户发布或迁移后是否保持不变，由部署管理员在启用前确认。
- 用户上传的升学资料何时并入 13 项即时分析，留给后续资料聚合 change；当前知识库检索仍由智能体内部任务流完成。
