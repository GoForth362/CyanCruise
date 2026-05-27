# 简历诊断规格

## Purpose
定义 CyanCruise 如何基于简历文本、目标 JD 和已保存简历记录生成诊断结果、抽取简历关键词并维护关键词状态，为今日行动、职业计划和后续 AI 适配提供稳定后端契约。

## Requirements
### Requirement: 发起简历诊断
系统 SHALL 支持用户基于 resumeId 或直接简历文本发起简历诊断。诊断请求 SHALL 包含用户 ID、可选 resumeId、可选 resumeText、可选目标 JD 和可选画像上下文。若未提供 resumeText 且提供 resumeId，系统 SHALL 读取该用户拥有的简历记录并使用其 parsedContent 作为诊断文本。

#### Scenario: 使用简历文本诊断
- **WHEN** 用户提交非空 resumeText 和可选目标 JD
- **THEN** 系统基于该文本生成诊断结果，而不要求 resumeId

#### Scenario: 使用 resumeId 诊断
- **WHEN** 用户提交自己拥有的 resumeId 且未提交 resumeText
- **THEN** 系统读取该简历记录的 parsedContent 作为诊断文本

#### Scenario: 拒绝空简历内容
- **WHEN** 请求没有可用 resumeText，且 resumeId 对应简历也没有 parsedContent
- **THEN** 系统拒绝诊断并返回明确错误

### Requirement: 解析诊断结果
系统 SHALL 将诊断分析解析为结构化结果。结果 SHALL 包含 resumeId、overallScore、strengths、weaknesses、suggestions 和 rawAnalysis。overallScore SHALL 保持在 0 到 100 之间。

#### Scenario: 解析结构化 JSON
- **WHEN** 分析响应包含 JSON 对象且包含 overallScore、strengths、weaknesses 或 suggestions
- **THEN** 系统提取这些字段并返回结构化诊断结果

#### Scenario: 解析非结构化文本
- **WHEN** 分析响应不是可解析 JSON 但包含 0 到 100 的数字
- **THEN** 系统将第一个合法数字作为 overallScore，并将原文作为建议或原始分析

#### Scenario: 使用兜底分数
- **WHEN** 分析响应为空或无法提取合法分数
- **THEN** 系统返回明确兜底分数和空列表，且不抛出解析异常

### Requirement: 回写诊断分数
系统 SHALL 在诊断成功后将 overallScore 写回对应简历记录的 diagnosisScore，并通过简历基础能力同步职业画像 resume block。若诊断请求没有 resumeId，系统 SHALL 返回诊断结果但不更新任何简历记录。

#### Scenario: 诊断成功后更新简历分数
- **WHEN** 用户对自己拥有的 resumeId 完成诊断且结果包含 overallScore
- **THEN** 系统更新该简历记录 diagnosisScore，并同步画像 resume block

#### Scenario: 直接文本诊断不回写
- **WHEN** 用户只提交 resumeText 而没有 resumeId
- **THEN** 系统返回诊断结果，且不修改任何已保存简历记录

### Requirement: 抽取简历关键词
系统 SHALL 支持从简历目标岗位、标题、解析内容和更新时间抽取职业关键词。关键词 SHALL 包含 category、label、weight 和 evidence，并按权重倒序返回。系统 SHALL 过滤联系方式、常见停用词、无意义数字、过长 token 和非技能时间 token。

#### Scenario: 从目标岗位和标题抽取关键词
- **WHEN** 简历记录包含目标岗位或标题
- **THEN** 系统抽取岗位、背景或技能关键词并保留来源证据

#### Scenario: 从解析内容抽取关键词
- **WHEN** 简历 parsedContent 包含 skills、education、projects、experience、work 或 rawContent 字段
- **THEN** 系统按字段语义抽取对应类别关键词

#### Scenario: 合并重复关键词
- **WHEN** 多个来源产生相同 category 和 label 的关键词
- **THEN** 系统只保留权重最高的一条

#### Scenario: 关键词为空
- **WHEN** 简历没有可读文本或没有可用关键词
- **THEN** 系统返回 EMPTY 状态和空关键词列表

### Requirement: 维护关键词抽取状态
系统 SHALL 为每个用户简历维护关键词抽取状态。状态 SHALL 至少支持 `PENDING`、`PROCESSING`、`READY`、`EMPTY` 和 `FAILED`。非强制触发时，如果已有 `READY`、`EMPTY`、`FAILED` 或未过期的忙碌状态，系统 SHALL 返回当前状态而不重复抽取。

#### Scenario: 读取关键词状态
- **WHEN** 用户请求自己简历的关键词状态
- **THEN** 系统返回该简历的状态、错误信息和关键词列表

#### Scenario: 非强制触发复用结果
- **WHEN** 简历已有 READY 状态且用户未指定 force
- **THEN** 系统返回已有关键词状态，而不重新抽取

#### Scenario: 强制重算关键词
- **WHEN** 用户指定 force 触发关键词抽取
- **THEN** 系统重新抽取关键词并覆盖旧关键词状态

### Requirement: 强制简历诊断所有权校验
系统 SHALL 对所有按 resumeId 进行的诊断、关键词状态、关键词抽取和回写操作执行用户所有权校验。调用方 SHALL NOT 能读取、诊断或修改其他用户的简历。

#### Scenario: 拒绝跨用户诊断
- **WHEN** 用户对不属于自己的 resumeId 发起诊断
- **THEN** 系统拒绝该请求

#### Scenario: 拒绝跨用户关键词读取
- **WHEN** 用户请求不属于自己的 resumeId 的关键词状态
- **THEN** 系统拒绝该请求

### Requirement: 暴露简历诊断 Cosmic WebAPI
系统 SHALL 通过 Cosmic WebAPI 暴露简历诊断能力。WebAPI SHALL 支持触发诊断、读取关键词状态、触发关键词抽取和强制重算关键词。

#### Scenario: WebAPI 触发诊断
- **WHEN** 调用方提交用户 ID 和诊断请求
- **THEN** WebAPI 返回结构化诊断结果

#### Scenario: WebAPI 读取关键词状态
- **WHEN** 调用方提交用户 ID 和 resumeId
- **THEN** WebAPI 返回该用户该简历的关键词状态

#### Scenario: WebAPI 强制重算关键词
- **WHEN** 调用方提交用户 ID、resumeId 和 force=true
- **THEN** WebAPI 触发关键词重算并返回最新状态

### Requirement: 保持可替换的诊断与关键词边界
系统 SHALL 通过可替换边界完成诊断分析、文本来源解析和关键词持久化。默认实现 SHALL 可在没有 AI SDK、PDF 解析库、OSS SDK 或 Cosmic datamodel 的情况下通过测试；未来 AI、文件和 datamodel 适配 SHALL 能替换这些边界，而无需修改 DTO、helper 或 WebAPI 契约。

#### Scenario: 默认实现可测试
- **WHEN** 本地测试运行且没有外部 AI、PDF 或 OSS 能力
- **THEN** 系统仍可通过确定性规则完成诊断解析、关键词抽取和状态读写

#### Scenario: 替换为 AI 诊断适配器
- **WHEN** 后续 AI 诊断适配器实现完成
- **THEN** 它可以通过同一诊断分析边界替换默认实现

#### Scenario: 替换为 Cosmic 存储
- **WHEN** Cosmic datamodel 关键词或诊断记录适配器实现完成
- **THEN** 它可以通过同一存储边界替换默认存储
