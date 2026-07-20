## Context

升学智能体通过后端接收包含 `taskType` 与业务 DTO 的请求。现有草稿表已经能够保存 JSON，但只有 `PostgraduateApplicationService` 使用它，且前端只恢复考研择校表单。

## Goals / Non-Goals

**Goals:** 在 13 个分析入口统一保存、恢复并向智能体传递真实表单数据；隔离不同用户和任务；分析失败后不丢失输入。

**Non-Goals:** 不把草稿内容自动合并为用户画像；不修改智能体编号、凭据和任务流；不保存或伪造智能体分析结果。

## Decisions

1. 以 `taskType` 作为每个表单的唯一草稿键，数据库键为 `userId + taskType`。
2. 后端 ApplicationService 在调用 `FurtherStudyCompanionAnalyzer` 之前写入 DTO JSON；因此智能体调用失败也不会丢失输入。
3. 前端在提交前先将同一 `request` 写入用户隔离的本地缓存，再发起请求；渲染默认值优先使用内存草稿、本地草稿，随后使用服务端草稿覆盖。
4. 草稿恢复 API 保持读取单个 `taskType` 的轻量契约，不把用户所有升学信息返回给页面。

## Risks / Trade-offs

- 草稿存储的是用户主动提交的文本，需遵守现有用户隔离策略；不将其写入普通日志。
- 每个页面首次进入会多一次草稿读取请求；仅在有已确认用户身份时执行。
- 分析结果的格式仍由智能体/任务流决定；草稿持久化不替代结果结构校验。

## Migration Plan

复用现有 `cc_study_analysis_draft` 表及幂等建表逻辑，无数据迁移。发布后首次提交会创建或更新对应任务草稿；回滚只会停止新读写，不删除已有草稿。

## Open Questions

无。
