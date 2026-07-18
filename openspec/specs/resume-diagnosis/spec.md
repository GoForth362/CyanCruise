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
系统 SHALL 仅将通过校验的智能体结构化 JSON 解析为诊断结果。结果 SHALL 包含 resumeId、overallScore、strengths、weaknesses、suggestions 和 rawAnalysis。overallScore SHALL 保持在 0 到 100 之间；无效响应 SHALL 返回可重试失败且不得保存。

#### Scenario: 解析结构化 JSON
- **WHEN** 分析响应包含 JSON 对象且包含 overallScore、strengths、weaknesses 或 suggestions
- **THEN** 系统提取这些字段并返回结构化诊断结果

#### Scenario: 拒绝非结构化或不完整响应
- **WHEN** 智能体响应不是合法 JSON、缺少必要字段或包含无效分数
- **THEN** 系统返回可供用户重试的失败信息
- **AND THEN** 系统不保存诊断记录，也不更新简历诊断分数

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
系统 SHALL 通过可替换边界完成诊断分析、文本来源解析和关键词持久化。测试 SHALL 可通过注入模拟智能体、文件和存储适配器运行；正式诊断 SHALL 只接受智能体返回的合法结构化结果，不得以确定性规则结果替代智能体诊断。未来 AI、文件和 datamodel 适配 SHALL 能替换这些边界，而无需修改 DTO、helper 或 WebAPI 契约。

#### Scenario: 通过模拟适配器测试
- **WHEN** 本地测试运行且没有外部 AI、PDF 或 OSS 能力
- **THEN** 系统通过注入模拟智能体和存储适配器验证诊断解析、关键词抽取和状态读写

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
- **WHEN** 智能体只返回普通文本而不是符合契约的结构化诊断 JSON
- **THEN** 系统返回可供用户重试的失败信息，且不生成或保存规则版建议

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
简历诊断 SHALL 在可用时组装目标岗位、目标岗位要求、用户画像摘要、测评结果、简历关键词和文件文本提取结果作为上下文。上下文缺失 SHALL 明确标记缺失信息，不得伪造不存在的画像、文件或 AI 能力，也不得改用基础规则生成诊断。

#### Scenario: 画像和岗位要求同时可用
- **WHEN** 用户画像存在目标岗位且请求包含目标岗位要求
- **THEN** 诊断上下文同时包含岗位、岗位要求和画像摘要，并在结果中保留上下文来源

#### Scenario: AI provider 不可用
- **WHEN** 真实 AI provider 未启用或返回 unavailable
- **THEN** 系统返回可供用户重试的失败信息，且不保存诊断记录或更新简历诊断分数

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
简历诊断 SHALL 返回总分及分项评分。智能体诊断 SHALL 使用内容完整度、目标岗位匹配、经历证据和表达清晰度四项标准，各项 SHALL 包含得分、满分和本次评分依据，满分合计 SHALL 为 100。

#### Scenario: 智能体诊断完成
- **WHEN** 系统基于简历正文和岗位上下文获得合法的智能体诊断结果
- **THEN** 结果返回四项评分明细，且各项得分之和等于总分

#### Scenario: 旧结果没有评分明细
- **WHEN** 系统读取只包含 `overallScore` 的旧诊断结果
- **THEN** 旧结果仍可读取，页面显示总分并说明暂无分项依据

### Requirement: 建议对应真实缺失信号
智能体诊断 SHALL 根据简历中是否识别到岗位相关词、项目或工作经历、量化成果、动作和结果表达生成对应建议。系统 SHALL NOT 在未检查相关信号时输出固定建议。

#### Scenario: 缺少量化成果
- **WHEN** 简历正文未识别到数字、百分比或数量结果
- **THEN** 系统生成补充量化成果的建议、修改动作和具体示例

#### Scenario: 岗位要求匹配不足
- **WHEN** 已提供岗位要求但简历正文未识别到重合关键词
- **THEN** 系统提示对照岗位要求补充真实经历证据，不使用行业缩写

### Requirement: 目标岗位影响诊断结果
按已有简历发起诊断时，系统 SHALL 使用该简历记录保存的目标岗位。智能体 SHALL 根据前端、后端、数据、产品等岗位方向识别对应能力词，使不同目标岗位可以影响岗位匹配得分和建议。

#### Scenario: 前端岗位简历包含前端能力
- **WHEN** 所选简历目标岗位为前端方向且正文包含前端相关技术和经历
- **THEN** 目标岗位匹配项识别到岗位证据并提高对应得分

#### Scenario: 岗位方向与简历内容不匹配
- **WHEN** 所选简历目标岗位为后端方向但正文只包含前端相关能力
- **THEN** 系统降低目标岗位匹配得分并提示补充后端岗位的真实能力证据

### Requirement: 简历诊断使用平台任务流
当 Agent 平台简历诊断任务流已配置且调用成功时，简历诊断服务 SHALL 使用任务流返回的诊断 JSON 生成既有 `ResumeDiagnosisResultDto`。页面返回字段、诊断保存和用户所有权校验 SHALL 保持兼容。

#### Scenario: 页面展示平台诊断结果
- **WHEN** 用户点击“生成诊断建议”且平台任务流返回有效 JSON
- **THEN** 系统 SHALL 返回总分、四项评分、优点、问题、普通建议和结构化修改建议，并 SHALL 标记结果来源为 `AGENT_AI`

#### Scenario: 用户诊断自己的已保存简历
- **WHEN** 用户通过 resumeId 发起诊断
- **THEN** 系统 SHALL 在既有所有权校验、简历正文读取和 PDF 文本提取完成后再调用平台任务流

### Requirement: 岗位要求和用户画像必须参与诊断上下文
简历诊断服务 SHALL 将页面提交的岗位要求与已保存的用户画像共同传给平台智能体。页面 SHALL 在参考信息中明确展示“岗位要求”和“用户画像”；当目标岗位、岗位要求或用户画像出现冲突时，诊断结论 SHALL 说明该冲突。

#### Scenario: 用户填写岗位要求
- **WHEN** 用户提交包含岗位要求的诊断请求
- **THEN** 平台诊断 SHALL 在目标岗位匹配评分依据中说明已匹配、未匹配或证据不足的岗位能力点

### Requirement: 相同输入复用已校验的诊断结果
简历诊断服务 SHALL 在短时内复用同一用户、同一简历正文、目标岗位、岗位要求和用户画像的已校验结果；任一输入变化时 SHALL 重新调用平台智能体。

#### Scenario: 用户重复诊断且未改动输入
- **WHEN** 用户在有效期内重复提交完全相同的诊断输入
- **THEN** 系统 SHALL 返回相同的诊断结果，避免大模型随机性导致分数波动

#### Scenario: 用户更新岗位要求
- **WHEN** 用户修改岗位要求后再次诊断
- **THEN** 系统 SHALL 视为新的诊断输入并重新调用平台智能体

### Requirement: 平台不可用时不生成伪诊断
当 Agent 平台任务流不可用、超时或返回无效响应时，简历诊断服务 SHALL 对可恢复问题执行有限重试。重试仍失败时，服务 SHALL 返回可供用户重试的失败信息，并 SHALL NOT 使用基础规则生成或保存诊断结果。

#### Scenario: 平台超时
- **WHEN** 平台任务流在配置超时内未返回有效结果
- **THEN** 系统 SHALL 返回普通中文重试提示，且 SHALL NOT 保存诊断记录或更新简历诊断分数

#### Scenario: 平台返回非 JSON 内容
- **WHEN** 平台任务流 `answer` 不是可解析的诊断 JSON
- **THEN** 系统 SHALL 在有限重试后返回可供用户重试的失败信息
- **AND THEN** 系统 SHALL NOT 保存无效响应或生成规则版诊断

### Requirement: 诊断上下文引用 AI 深度画像
简历诊断 SHALL 在存在最新 AI 深度画像时将其摘要、标签和资料不足项加入分析上下文，并明确其来源为基于测评的 AI 分析。

#### Scenario: 有深度画像时发起诊断
- **WHEN** 用户发起简历诊断且职业画像快照包含最新 AI 深度画像
- **THEN** 诊断请求上下文 SHALL 包含该画像摘要和标签，且不得覆盖用户明确填写的岗位或简历事实

#### Scenario: 自画像包含补充事实
- **WHEN** 用户发起简历诊断且已填写自画像补充
- **THEN** 诊断请求上下文 SHALL 将该内容标记为用户事实，并用于减少与已知情况冲突或重复的建议

