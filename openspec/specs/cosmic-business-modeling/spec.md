# cosmic-business-modeling Specification

## Purpose
定义 CyanCruise 在金蝶苍穹设计器中的业务对象建模口径，约束对象编码、表名、字段标识、深造对象复用和 Agent 运行记录规划，确保数据模型文档与已建平台对象保持一致。

## Requirements

### Requirement: 建模文档 SHALL 以已建对象为基准

系统 SHALL 在 `datamodel/cyancruise-business-modeling.md` 中记录 CyanCruise 苍穹业务对象建模口径，并以已经在金蝶设计器中建立的对象为基准修正文档。

#### Scenario: 记录已建用户职业画像

- **WHEN** 开发者查看建模文档
- **THEN** 文档 SHALL 记录“用户职业画像”为基础资料
- **AND** 对象标识 SHALL 为 `v620_cc_user_profile`
- **AND** 表名 SHALL 为 `tk_v620_cc_user_profile`

#### Scenario: 记录已建 Agent执行记录

- **WHEN** 开发者查看建模文档
- **THEN** 文档 SHALL 记录“Agent执行记录”为单据
- **AND** 对象标识 SHALL 为 `v620_cc_agent_run`
- **AND** 表名 SHALL 为 `tk_v620_cc_agent_run`

### Requirement: 字段标识 SHALL 使用平台保存后的 v620 形式

建模文档 SHALL 使用平台保存后的 `v620_xxx` 字段标识，不得把驼峰输入名作为正式字段标识。

#### Scenario: 展示用户字段

- **WHEN** 文档描述用户归属字段
- **THEN** 字段标识 SHALL 写为 `v620_userid`
- **AND** 逻辑字段 MAY 标注为 `user_id`

#### Scenario: 展示记录字段

- **WHEN** 文档描述测评记录、执行记录、简历记录或深造记录 ID
- **THEN** 字段标识 SHALL 写为 `v620_recordid`、`v620_runid`、`v620_resumeid` 或同类平台字段
- **AND** 文档 SHALL NOT 要求字段标识以 `_id` 结尾

### Requirement: 对象编码和表名 SHALL 控制长度并使用稳定缩写

建模文档 SHALL 为较长对象提供适合金蝶设计器保存的短对象编码和短表名，且缩写 SHALL 保持语义清晰。创建业务对象时填写的对象编码 SHALL 不超过 25 个字符。

#### Scenario: 职业测评记录使用短表名

- **WHEN** 开发者建立“职业测评记录”
- **THEN** 对象编码 SHALL 使用 `v620_cc_assess_record`
- **AND** 表名 SHALL 使用 `tk_v620_cc_assess_record`
- **AND** 文档 SHALL NOT 要求使用过长的 `v620_cc_assessment_record`
- **AND** 文档 SHALL NOT 要求使用过长的 `tk_v620_cc_assessment_record`

#### Scenario: 深造对象使用 study 缩写

- **WHEN** 开发者建立深造目标、深造记录、深造材料或深造进展记录
- **THEN** 对象编码 SHALL 使用 `v620_cc_study_target`、`v620_cc_study_record`、`v620_cc_study_material` 或 `v620_cc_study_event`
- **AND** 表名 SHALL 使用 `tk_v620_cc_study_target`、`tk_v620_cc_study_record`、`tk_v620_cc_study_material` 或 `tk_v620_cc_study_event`
- **AND** 文档 SHALL NOT 要求使用过长的 `v620_cc_further_study_target`、`v620_cc_further_study_record`、`v620_cc_further_study_material` 或 `v620_cc_further_study_event`

#### Scenario: Agent上下文引用使用 ctx 缩写

- **WHEN** 开发者建立“Agent上下文引用”
- **THEN** 对象编码 SHALL 使用 `v620_cc_agent_ctx_ref`
- **AND** 表名 SHALL 使用 `tk_v620_cc_agent_ctx_ref`
- **AND** 文档 SHALL NOT 要求使用过长的 `v620_cc_agent_context_ref`

### Requirement: 当前建模 SHALL 不要求未采用字段和分录

当前建模规划 SHALL 去除未实际采用的 `v620_billno` 要求，并 SHALL 不要求 Agent执行记录建立“执行步骤”分录。

#### Scenario: 职业测评记录不要求单据编号字段

- **WHEN** 开发者建立“职业测评记录”
- **THEN** 文档 SHALL 要求建立 `v620_recordid`
- **AND** 文档 SHALL NOT 要求建立 `v620_billno`

#### Scenario: Agent执行记录不要求步骤分录

- **WHEN** 开发者建立“Agent执行记录”
- **THEN** 文档 SHALL 只要求单据头字段
- **AND** 文档 SHALL NOT 要求建立“执行步骤”分录

### Requirement: 职业发展业务对象 SHALL 覆盖核心主线

建模文档 SHALL 覆盖职业发展主线所需业务对象：职业测评记录、简历档案、简历诊断、今日任务、职业计划和模拟面试。通用 AI 对话框 SHALL NOT 作为当前主业务模型替代任务型 Agent。

#### Scenario: 职业测评记录字段完整

- **WHEN** 开发者建立“职业测评记录”
- **THEN** 文档 SHALL 提供 `v620_recordid`、`v620_userid`、`v620_scaleid`、`v620_scaletitle`、`v620_status`、`v620_summary`、`v620_suggestedrolesjson`、`v620_answersjson`、`v620_resultjson` 和 `v620_completedat`

#### Scenario: 简历对象字段完整

- **WHEN** 开发者建立“简历档案”
- **THEN** 文档 SHALL 提供用户归属、简历 ID、标题、目标岗位、文件标识、状态、诊断分数、解析内容和关键词内容字段

### Requirement: 深造陪伴 SHALL 使用统一对象承载考研保研留学

建模文档 SHALL 使用“深造目标”“深造记录”“深造材料”“深造进展记录”统一承载考研、保研和留学，不为三类方向复制三套对象。

#### Scenario: 使用方向字段区分深造类型

- **WHEN** 开发者建立深造相关对象
- **THEN** 对象 SHALL 包含 `v620_track`
- **AND** `v620_track` SHALL 用于区分考研、保研和留学

#### Scenario: 深造记录保存生成结果

- **WHEN** 用户生成考研规划、保研诊断或留学选校结果
- **THEN** “深造记录” SHALL 保存请求内容、结果内容、记录类型、目标学校、目标专业、目标地区和用户归属

### Requirement: Agent 运行管理 SHALL 支持后续可解释性

建模文档 SHALL 规划“Agent能力定义”、保留“Agent执行记录”，并 SHALL 规划“Agent上下文引用”作为后续对象。

#### Scenario: 记录 Agent 能力定义

- **WHEN** 开发者查看 Agent 建模规划
- **THEN** 文档 SHALL 提供“Agent能力定义”对象
- **AND** 该对象 SHALL 能够登记主调度Agent、用户画像Agent、信息检索Agent、学业规划Agent、就业规划Agent、简历优化Agent、面试辅导Agent、考研规划Agent、保研规划Agent和留学规划Agent
- **AND** 该对象 SHALL 保存 Agent 职责、业务模块、思维链路、关键工具、输入契约和输出契约

#### Scenario: 记录 Agent 执行

- **WHEN** Agent 完成一次调用
- **THEN** “Agent执行记录” SHALL 能够保存用户标识、Agent类型、业务模块、任务类型、模型名称、状态、输入输出、错误信息、token 消耗和执行时间

#### Scenario: 记录 Agent 上下文引用

- **WHEN** 后续需要解释 Agent 结果来源
- **THEN** “Agent上下文引用” SHALL 能够记录执行ID、来源对象、来源ID、来源摘要和是否写回

