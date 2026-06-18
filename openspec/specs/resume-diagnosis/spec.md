# 简历诊断规格

## Purpose
定义 CyanCruise 如何基于简历文本、目标岗位要求 和已保存简历记录生成诊断结果、抽取简历关键词并维护关键词状态，为今日行动、职业计划和后续 AI 适配提供稳定后端契约。
## Requirements
### Requirement: 发起简历诊断
系统 SHALL 支持用户基于 resumeId 或直接简历文本发起简历诊断。诊断请求 SHALL 包含用户 ID、可选 resumeId、可选 resumeText、可选目标岗位要求 和可选画像上下文。若未提供 resumeText 且提供 resumeId，系统 SHALL 读取该用户拥有的简历记录并使用其 parsedContent 作为诊断文本。

#### Scenario: 使用简历文本诊断
- **WHEN** 用户提交非空 resumeText 和可选目标岗位要求
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

### Requirement: PostgreSQL 持久化简历诊断与关键词状态
CyanCruise 简历诊断结果和关键词状态 SHALL 在运行时通过 PostgreSQL 持久化，而不是默认写入 `filestorage/resume-diagnosis`。

#### Scenario: 诊断结果跨实例读取
- **WHEN** 用户完成简历诊断并创建新的应用服务实例
- **THEN** 新实例 SHALL 从 PostgreSQL 读取同一 resumeId 的诊断结果

#### Scenario: 关键词状态跨实例读取
- **WHEN** 用户触发关键词抽取并保存 READY、EMPTY、FAILED 或其他状态
- **THEN** 后续实例 SHALL 从 PostgreSQL 读取该状态和关键词载荷

#### Scenario: 诊断仍遵守简历所有权
- **WHEN** 用户尝试读取、触发或回写不属于自己的 resumeId
- **THEN** 系统 SHALL 拒绝该操作，并且 PostgreSQL 中其他用户数据保持不变



### Requirement: 输出简历诊断建议
简历诊断结果 SHALL 在保留 `overallScore`、`strengths`、`weaknesses`、`suggestions` 和 `rawAnalysis` 的基础上，支持返回结构化简历诊断建议和优化计划摘要。旧调用方未消费新字段时，既有字段语义 SHALL 保持兼容。

#### Scenario: AI 返回结构化诊断建议
- **WHEN** AI 诊断响应包含 revision suggestions JSON
- **THEN** 系统解析为结构化建议列表，并同步保留普通 suggestions 以兼容旧页面

#### Scenario: AI 只返回普通文本
- **WHEN** AI 或 fallback analyzer 只返回普通建议文本
- **THEN** 系统从普通文本生成至少一条可展示的诊断建议，或返回空建议列表和明确的 fallback 状态

#### Scenario: 旧诊断记录可读取
- **WHEN** PostgreSQL 中已有旧版诊断 payload 没有结构化建议字段
- **THEN** 系统仍能读取诊断结果，并让页面退回展示 `suggestions`

### Requirement: 保存最新诊断建议
按 `resumeId` 完成的简历诊断 SHALL 将最新结构化诊断建议随诊断结果保存到现有简历诊断存储边界，并 SHALL 将 `overallScore` 和关键建议摘要同步到简历记录及用户画像 resume block。

#### Scenario: 保存诊断建议
- **WHEN** 用户对自己拥有的简历完成诊断
- **THEN** 系统保存诊断分数、优缺点、普通建议和结构化诊断建议

#### Scenario: 同步画像摘要
- **WHEN** 诊断结果包含高优先级诊断建议
- **THEN** 系统将最新诊断分数和关键建议摘要用于刷新用户画像中的简历状态

### Requirement: 强化诊断上下文组装
简历诊断 SHALL 在可用时组装目标岗位、目标岗位要求、用户画像摘要、测评结果、简历关键词和文件文本提取结果作为上下文。上下文缺失 SHALL 降级为明确提示或 fallback 规则，不得伪造不存在的画像、文件或 AI 能力。

#### Scenario: 画像和岗位要求同时可用
- **WHEN** 用户画像存在目标岗位且请求包含目标岗位要求
- **THEN** 诊断上下文同时包含岗位、岗位要求和画像摘要，并在结果中保留上下文来源

#### Scenario: AI provider 不可用
- **WHEN** 真实 AI provider 未启用或返回 unavailable
- **THEN** 系统使用确定性 fallback analyzer 生成基础诊断和诊断建议，并返回明确 fallback 状态

### Requirement: 诊断前补齐 PDF 正文
按 `resumeId` 发起诊断时，系统 SHALL 在所有权校验后优先读取已有 `parsedContent`；仅当正文为空且存在 `fileKey` 时，系统 SHALL 尝试通过文件文本提取边界读取 PDF 正文，成功后回写简历记录并继续既有诊断流程。

#### Scenario: 缺少正文但 PDF 可提取
- **WHEN** 用户自己的简历正文为空、存在 PDF `fileKey` 且提取成功
- **THEN** 系统保存提取正文并基于该正文生成诊断结果

#### Scenario: 扫描版 PDF 无正文
- **WHEN** 简历 PDF 只有图片且未提供手工简历正文
- **THEN** 系统不生成伪造诊断，并提示用户当前需要粘贴正文或等待图片文字识别能力

#### Scenario: 已有正文不重复提取
- **WHEN** 简历记录已有非空 `parsedContent`
- **THEN** 系统直接使用已有正文诊断，不重新下载或解析 PDF

### Requirement: 返回可解释评分明细
简历诊断 SHALL 返回总分及分项评分。基础规则诊断 SHALL 使用内容完整度、目标岗位匹配、经历证据和表达清晰度四项标准，各项 SHALL 包含得分、满分和本次评分依据，满分合计 SHALL 为 100。

#### Scenario: 基础规则诊断完成
- **WHEN** 系统基于简历正文和岗位上下文完成规则诊断
- **THEN** 结果返回四项评分明细，且各项得分之和等于总分

#### Scenario: 旧结果没有评分明细
- **WHEN** 系统读取只包含 `overallScore` 的旧诊断结果
- **THEN** 旧结果仍可读取，页面显示总分并说明暂无分项依据

### Requirement: 建议对应真实缺失信号
基础规则诊断 SHALL 根据简历中是否识别到岗位相关词、项目或工作经历、量化成果、动作和结果表达生成对应建议。系统 SHALL NOT 在未检查相关信号时输出固定建议。

#### Scenario: 缺少量化成果
- **WHEN** 简历正文未识别到数字、百分比或数量结果
- **THEN** 系统生成补充量化成果的建议、修改动作和具体示例

#### Scenario: 岗位要求匹配不足
- **WHEN** 已提供岗位要求但简历正文未识别到重合关键词
- **THEN** 系统提示对照岗位要求补充真实经历证据，不使用行业缩写

### Requirement: 目标岗位影响诊断结果
按已有简历发起诊断时，系统 SHALL 使用该简历记录保存的目标岗位。基础规则 SHALL 根据前端、后端、数据、产品等岗位方向识别对应能力词，使不同目标岗位可以影响岗位匹配得分和建议。

#### Scenario: 前端岗位简历包含前端能力
- **WHEN** 所选简历目标岗位为前端方向且正文包含前端相关技术和经历
- **THEN** 目标岗位匹配项识别到岗位证据并提高对应得分

#### Scenario: 岗位方向与简历内容不匹配
- **WHEN** 所选简历目标岗位为后端方向但正文只包含前端相关能力
- **THEN** 系统降低目标岗位匹配得分并提示补充后端岗位的真实能力证据
