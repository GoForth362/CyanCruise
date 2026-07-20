## Context

13 项分析已可调用智能体，但只持久化输入草稿。现有 PostgreSQL 深造存储类继承内存实现，服务重启后记录会丢失。

## Goals

- 每次成功分析保存输入、输出、任务类型、方向及中文标题。
- 全部查询、更新和上下文聚合严格以服务器确认的 userId 为边界，并按方向过滤。
- 上下文只传递目标、最多 5 条最新记录摘要和最多 5 份材料摘要，避免跨用户泄露和无限增长。

## Design

`PostgresqlFurtherStudyCompanionStorage` 直接实现存储契约，在 PostgreSQL 中建立 `cc_further_study_target`、`cc_further_study_record`、`cc_further_study_material`、`cc_further_study_event`。记录的 request/result JSONB 保存原始可追溯内容。

三个 ApplicationService 在智能体成功返回后创建记录；调用前建立仅含当前用户、当前方向数据的上下文包装对象，再将包装对象发送给智能体。输出 DTO 不变，WebAPI 返回仍是原有结果。

## Risks

- 智能体提示词需要识别 `context` 与 `request` 包装结构；若平台仅接受裸请求，调用适配层须由系统提示词约定。
- PostgreSQL 表初始化受现有 `cc001.storage.*` 配置控制；未配置时开发环境仍使用内存实现。

## Validation

- 存储测试验证重建实例后能读取记录、材料和事件，且跨用户/跨方向不可读取。
- 应用服务测试验证 13 个任务成功时保存结果，失败时不保存结果，并验证上下文范围。
