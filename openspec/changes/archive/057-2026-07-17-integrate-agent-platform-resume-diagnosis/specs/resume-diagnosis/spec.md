## ADDED Requirements

### Requirement: 简历诊断优先使用平台任务流
当 Agent 平台简历诊断任务流已配置且调用成功时，简历诊断服务 SHALL 使用任务流返回的诊断 JSON 生成既有 `ResumeDiagnosisResultDto`。页面返回字段、诊断保存和用户所有权校验 SHALL 保持兼容。

#### Scenario: 页面展示平台诊断结果
- **WHEN** 用户点击“生成诊断建议”且平台任务流返回有效 JSON
- **THEN** 系统 SHALL 返回总分、四项评分、优点、问题、普通建议和结构化修改建议，并 SHALL 标记结果来源为 `AGENT_AI`，以供现有简历诊断页面展示

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

## MODIFIED Requirements

### Requirement: 解析诊断结果
系统 SHALL 仅将通过校验的智能体结构化 JSON 解析为诊断结果。结果 SHALL 包含 resumeId、overallScore、strengths、weaknesses、suggestions 和 rawAnalysis。overallScore SHALL 保持在 0 到 100 之间；无效响应 SHALL 返回可重试失败且不得保存。

#### Scenario: 解析结构化 JSON
- **WHEN** 分析响应包含 JSON 对象且包含 overallScore、strengths、weaknesses 或 suggestions
- **THEN** 系统提取这些字段并返回结构化诊断结果

#### Scenario: 拒绝非结构化或不完整响应
- **WHEN** 智能体响应不是合法 JSON、缺少必要字段或包含无效分数
- **THEN** 系统返回可供用户重试的失败信息
- **AND THEN** 系统不保存诊断记录，也不更新简历诊断分数

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

### Requirement: 强化诊断上下文组装
简历诊断 SHALL 在可用时组装目标岗位、目标岗位要求、用户画像摘要、测评结果、简历关键词和文件文本提取结果作为上下文。上下文缺失 SHALL 明确标记缺失信息，不得伪造不存在的画像、文件或 AI 能力，也不得改用基础规则生成诊断。

#### Scenario: 画像和岗位要求同时可用
- **WHEN** 用户画像存在目标岗位且请求包含目标岗位要求
- **THEN** 诊断上下文同时包含岗位、岗位要求和画像摘要，并在结果中保留上下文来源

#### Scenario: AI provider 不可用
- **WHEN** 真实 AI provider 未启用或返回 unavailable
- **THEN** 系统返回可供用户重试的失败信息，且不保存诊断记录或更新简历诊断分数

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
