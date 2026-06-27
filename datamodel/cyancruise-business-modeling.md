# CyanCruise 金蝶业务对象建模手册

本文用于指导 CyanCruise 在金蝶云苍穹开发平台中逐个建立业务对象。本文以你在设计器中已经建立的对象为基准修正和补齐，不再沿用历史 CareerLoop 命名，也不要求一次性改造 Java 存储适配器。

## 建模约定

- 应用名称：`CyanCruise`
- 用户可见名称使用中文，例如“用户职业画像”“职业测评记录”。
- 创建业务对象时，“对象编码”优先填写 `v620_cc_xxx` 形式，例如 `v620_cc_user_profile`；金蝶设计器限制对象编码不能超过 25 个字符。
- 表名使用 `tk_v620_cc_xxx` 形式，例如 `tk_v620_cc_user_profile`。
- 对象编码和表名都需要控制长度；较长对象使用清晰缩写，例如 `assessment` 缩写为 `assess`，`diagnosis` 缩写为 `diag`，`capability` 缩写为 `cap`，`context` 缩写为 `ctx`，`further_study` 缩写为 `study`。
- 字段标识以平台保存后的 `v620_xxx` 为准，例如 `v620_userid`、`v620_targetrole`、`v620_runid`。
- 金蝶设计器不允许字段标识以 `_id` 结尾；涉及 ID 的字段按平台实际保存结果写成 `v620_userid`、`v620_resumeid`、`v620_recordid`。
- 文档中的“逻辑字段”用于对应现有 Java DTO、PostgreSQL 表或 WebAPI 契约，例如 `user_id`、`target_role`、`result_json`。
- 当前阶段先建立平台业务对象、字段和基础布局；后续再通过 Java adapter 或同步任务把 PostgreSQL 数据接入苍穹业务对象。
- AI 原始输入输出、诊断结果、画像详情等结构变化大的内容使用“大文本”保存 JSON；用户归属、状态、类型、目标岗位、时间等查询字段必须结构化。

## 已建对象

### 001 用户职业画像

- 对象类型：基础资料
- 对象标识：`v620_cc_user_profile`
- 表名：`tk_v620_cc_user_profile`
- 建模状态：已建立字段和基础布局。
- 用途：保存用户长期职业画像汇总，供测评、简历、面试、职业计划、深造陪伴和 Agent 读取。

| 字段标识 | 名称 | 类型 | 必填 | 逻辑字段 | 说明 |
| --- | --- | --- | --- | --- | --- |
| `v620_userid` | 用户标识 | 文本，128 | 是 | `user_id` | 用户归属标识。 |
| `v620_targetrole` | 目标岗位 | 文本，255 | 否 | `target_role` | 用户当前准备的岗位或方向。 |
| `v620_currentstage` | 当前阶段 | 文本，128 | 否 | `current_stage` | 如求职准备、简历优化、面试准备。 |
| `v620_personalizationlevel` | 个性化等级 | 文本，64 | 否 | `personalization_level` | low、medium、high 或中文等级。 |
| `v620_completenessscore` | 完整度分数 | 整数 | 否 | `completeness_score` | 建议 0 到 100。 |
| `v620_profilejson` | 画像内容 | 大文本 | 否 | `profile_json` | 完整画像 JSON。 |
| `v620_readinessjson` | 准备度内容 | 大文本 | 否 | `readiness_json` | 准备度、缺口、风险和建议。 |
| `v620_evidencejson` | 证据内容 | 大文本 | 否 | `evidence_json` | 画像证据和来源。 |
| `v620_agentsummary` | Agent摘要 | 大文本 | 否 | `agent_summary` | 给人和 Agent 快速读取的中文摘要。 |
| `v620_lastagentrunid` | 最近Agent执行ID | 文本，128 | 否 | `last_agent_run_id` | 最近一次刷新画像的 Agent 执行记录。 |

推荐布局：

- 第一行：用户标识、目标岗位、当前阶段
- 第二行：个性化等级、完整度分数、Agent摘要
- 后续：最近Agent执行ID、画像内容、准备度内容、证据内容

### 002 Agent执行记录

- 对象类型：单据
- 对象标识：`v620_cc_agent_run`
- 表名：`tk_v620_cc_agent_run`
- 建模状态：已建立字段，布局可后续补齐。
- 用途：记录每次 Agent 调用的输入、输出、状态、消耗和关联业务对象，支撑排障、审计和运营统计。

| 字段标识 | 名称 | 类型 | 必填 | 逻辑字段 | 说明 |
| --- | --- | --- | --- | --- | --- |
| `v620_runid` | 执行ID | 文本，128 | 是 | `run_id` | 一次 Agent 执行的稳定标识。 |
| `v620_userid` | 用户标识 | 文本，128 | 是 | `user_id` | 调用归属用户。 |
| `v620_agenttype` | Agent类型 | 文本，128 | 否 | `agent_type` | 如职业画像、测评解读、简历诊断、考研、保研、留学。 |
| `v620_businessmodule` | 业务模块 | 文本，128 | 否 | `business_module` | 职业发展、深造陪伴、Agent运行管理等。 |
| `v620_tasktype` | 任务类型 | 文本，128 | 否 | `task_type` | 画像刷新、测评解读、生成计划、诊断简历等。 |
| `v620_modelname` | 使用模型 | 文本，128 | 否 | `model_name` | 实际使用的模型或通道。 |
| `v620_status` | 状态 | 文本，64 | 否 | `status` | pending、running、success、failed、cancelled。 |
| `v620_bizobject` | 关联业务对象 | 文本，128 | 否 | `biz_object` | 写入或读取的主业务对象标识。 |
| `v620_bizid` | 关联业务ID | 文本，128 | 否 | `biz_id` | 关联业务记录 ID。 |
| `v620_inputsummary` | 输入摘要 | 大文本 | 否 | `input_summary` | 输入上下文的中文摘要。 |
| `v620_outputsummary` | 输出摘要 | 大文本 | 否 | `output_summary` | 输出结果的中文摘要。 |
| `v620_inputjson` | 输入内容 | 大文本 | 否 | `input_json` | 请求上下文 JSON，不得保存密钥或 token。 |
| `v620_outputjson` | 输出内容 | 大文本 | 否 | `output_json` | Agent 输出 JSON。 |
| `v620_errormessage` | 错误信息 | 大文本 | 否 | `error_message` | 失败原因，面向排障。 |
| `v620_prompttokens` | 输入Token | 整数 | 否 | `prompt_tokens` | 输入 token 数。 |
| `v620_completiontokens` | 输出Token | 整数 | 否 | `completion_tokens` | 输出 token 数。 |
| `v620_totaltokens` | 总Token | 整数 | 否 | `total_tokens` | 总 token 数。 |
| `v620_startedat` | 开始时间 | 长日期/日期时间 | 否 | `started_at` | 执行开始时间。 |
| `v620_endedat` | 结束时间 | 长日期/日期时间 | 否 | `ended_at` | 执行结束时间。 |

注意：不需要建立“执行步骤”分录，除非后续你明确要追踪工具调用步骤。当前只保留单据头字段即可。

推荐布局：

- 基础信息：执行ID、用户标识、Agent类型、业务模块、任务类型、使用模型、状态
- 关联业务：关联业务对象、关联业务ID
- 消耗与时间：输入Token、输出Token、总Token、开始时间、结束时间
- 输入输出：输入摘要、输出摘要、输入内容、输出内容、错误信息

## 本轮已建对象

### 003 职业测评记录

- 对象类型：单据
- 对象编码：`v620_cc_assess_record`
- 原逻辑对象名：`assessment_record`
- 表名：`tk_v620_cc_assess_record`
- 建模状态：已建立业务对象、字段和基础布局。
- 用途：保存用户每一次职业测评提交、结果、推荐岗位和答案快照。

| 字段标识 | 名称 | 类型 | 必填 | 逻辑字段 | 说明 |
| --- | --- | --- | --- | --- | --- |
| `v620_recordid` | 测评记录ID | 文本，128 | 是 | `record_id` | 测评记录稳定标识。 |
| `v620_userid` | 用户标识 | 文本，128 | 是 | `user_id` | 用户归属。 |
| `v620_scaleid` | 量表ID | 文本，128 | 否 | `scale_id` | 测评量表标识。 |
| `v620_scaletitle` | 量表名称 | 文本，255 | 否 | `scale_title` | 用户可读量表名称。 |
| `v620_status` | 状态 | 文本，64 | 否 | `status` | draft、completed、invalid。 |
| `v620_summary` | 测评摘要 | 大文本 | 否 | `summary` | 测评结果摘要。 |
| `v620_suggestedrolesjson` | 推荐岗位内容 | 大文本 | 否 | `suggested_roles_json` | 推荐岗位 JSON。 |
| `v620_answersjson` | 答案内容 | 大文本 | 否 | `answers_json` | 用户答案快照。 |
| `v620_resultjson` | 结果内容 | 大文本 | 否 | `result_json` | 完整评分结果。 |
| `v620_completedat` | 完成时间 | 长日期/日期时间 | 否 | `completed_at` | 测评完成时间。 |

说明：不需要 `v620_billno`。当前按我们实际操作口径使用 `v620_recordid` 作为测评记录稳定标识。

### 004 简历档案

- 对象类型：单据
- 对象编码：`v620_cc_resume_record`
- 原逻辑对象名：`resume_record`
- 表名：`tk_v620_cc_resume_record`
- 建模状态：已建立业务对象、字段和基础布局。
- 用途：保存简历元数据、目标岗位、文件标识、解析内容和最近诊断分数。

| 字段标识 | 名称 | 类型 | 必填 | 逻辑字段 | 说明 |
| --- | --- | --- | --- | --- | --- |
| `v620_resumeid` | 简历ID | 文本，128 | 是 | `resume_id` | 简历稳定标识。 |
| `v620_userid` | 用户标识 | 文本，128 | 是 | `user_id` | 用户归属。 |
| `v620_title` | 简历标题 | 文本，255 | 否 | `title` | 简历名称。 |
| `v620_targetjob` | 目标岗位 | 文本，255 | 否 | `target_job` | 简历面向岗位。 |
| `v620_filekey` | 文件标识 | 文本，255 | 否 | `file_key` | 文件服务稳定 key，不保存签名 URL。 |
| `v620_status` | 状态 | 文本，64 | 否 | `status` | draft、uploaded、parsed、diagnosed、archived。 |
| `v620_diagnosisscore` | 诊断分数 | 整数 | 否 | `diagnosis_score` | 0 到 100。 |
| `v620_parsedcontent` | 解析内容 | 大文本 | 否 | `parsed_content` | PDF 或文本解析结果。 |
| `v620_keywordjson` | 关键词内容 | 大文本 | 否 | `keyword_json` | 关键词和证据 JSON。 |
| `v620_createdat` | 创建时间 | 长日期/日期时间 | 否 | `created_at` | 创建时间。 |
| `v620_updatedat` | 更新时间 | 长日期/日期时间 | 否 | `updated_at` | 更新时间。 |

### 005 简历诊断

- 对象类型：单据
- 对象编码：`v620_cc_resume_diag`
- 原逻辑对象名：`resume_diagnosis`
- 表名：`tk_v620_cc_resume_diag`
- 建模状态：已建立业务对象、字段和基础布局。
- 用途：保存每次简历诊断的评分、关键词状态和完整诊断结果。

| 字段标识 | 名称 | 类型 | 必填 | 逻辑字段 |
| --- | --- | --- | --- | --- |
| `v620_diagnosisid` | 诊断ID | 文本，128 | 是 | `diagnosis_id` |
| `v620_resumeid` | 简历ID | 文本，128 | 是 | `resume_id` |
| `v620_userid` | 用户标识 | 文本，128 | 是 | `user_id` |
| `v620_score` | 分数 | 整数 | 否 | `score` |
| `v620_diagnosisjson` | 诊断内容 | 大文本 | 否 | `diagnosis_json` |
| `v620_keywordstatusjson` | 关键词状态 | 大文本 | 否 | `keyword_status_json` |
| `v620_createdat` | 创建时间 | 长日期/日期时间 | 否 | `created_at` |
| `v620_updatedat` | 更新时间 | 长日期/日期时间 | 否 | `updated_at` |

### 006 今日任务

- 对象类型：单据
- 对象编码：`v620_cc_career_task`
- 原逻辑对象名：`career_task`
- 表名：`tk_v620_cc_career_task`
- 建模状态：已建立业务对象、字段和基础布局。
- 用途：保存今日行动推荐、任务状态、优先级和父子任务关系。

| 字段标识 | 名称 | 类型 | 必填 | 逻辑字段 |
| --- | --- | --- | --- | --- |
| `v620_taskid` | 任务ID | 文本，128 | 是 | `task_id` |
| `v620_userid` | 用户标识 | 文本，128 | 是 | `user_id` |
| `v620_taskkey` | 任务编码 | 文本，128 | 否 | `task_key` |
| `v620_title` | 任务标题 | 文本，255 | 否 | `title` |
| `v620_description` | 任务说明 | 大文本 | 否 | `description` |
| `v620_duedate` | 到期日期 | 日期 | 否 | `due_date` |
| `v620_status` | 状态 | 文本，64 | 否 | `status` |
| `v620_priority` | 优先级 | 整数 | 否 | `priority` |
| `v620_parenttaskid` | 父任务ID | 文本，128 | 否 | `parent_task_id` |
| `v620_subindex` | 子任务序号 | 整数 | 否 | `sub_index` |
| `v620_updatedat` | 更新时间 | 长日期/日期时间 | 否 | `updated_at` |

### 007 职业计划

- 对象类型：单据
- 对象编码：`v620_cc_career_plan`
- 原逻辑对象名：`career_plan`
- 表名：`tk_v620_cc_career_plan`
- 建模状态：已建立业务对象、字段和基础布局。
- 用途：保存目标岗位、里程碑、本周重点和计划生成信息。

| 字段标识 | 名称 | 类型 | 必填 | 逻辑字段 |
| --- | --- | --- | --- | --- |
| `v620_planid` | 计划ID | 文本，128 | 是 | `plan_id` |
| `v620_userid` | 用户标识 | 文本，128 | 是 | `user_id` |
| `v620_targetrole` | 目标岗位 | 文本，255 | 否 | `target_role` |
| `v620_modelused` | 使用模型 | 文本，128 | 否 | `model_used` |
| `v620_tokensconsumed` | 消耗量 | 整数 | 否 | `tokens_consumed` |
| `v620_startstatejson` | 起点状态 | 大文本 | 否 | `start_state_json` |
| `v620_milestonesjson` | 里程碑内容 | 大文本 | 否 | `milestones_json` |
| `v620_weeklyfocusjson` | 本周重点 | 大文本 | 否 | `weekly_focus_json` |
| `v620_version` | 版本号 | 整数 | 否 | `version` |
| `v620_generatedat` | 生成时间 | 长日期/日期时间 | 否 | `generated_at` |
| `v620_lastupdatedat` | 更新时间 | 长日期/日期时间 | 否 | `last_updated_at` |

### 008 模拟面试

- 对象类型：单据
- 对象编码：`v620_cc_interview`
- 原逻辑对象名：`interview_session`
- 表名：`tk_v620_cc_interview`
- 建模状态：已建立业务对象、字段和基础布局。
- 用途：保存面试会话主记录、岗位、状态、评分和报告。

| 字段标识 | 名称 | 类型 | 必填 | 逻辑字段 |
| --- | --- | --- | --- | --- |
| `v620_interviewid` | 面试ID | 文本，128 | 是 | `interview_id` |
| `v620_userid` | 用户标识 | 文本，128 | 是 | `user_id` |
| `v620_resumeid` | 简历ID | 文本，128 | 否 | `resume_id` |
| `v620_positionname` | 面试岗位 | 文本，255 | 否 | `position_name` |
| `v620_difficulty` | 难度 | 文本，64 | 否 | `difficulty` |
| `v620_status` | 状态 | 文本，64 | 否 | `status` |
| `v620_mode` | 面试模式 | 文本，64 | 否 | `mode` |
| `v620_finalscore` | 最终分数 | 整数 | 否 | `final_score` |
| `v620_durationseconds` | 持续秒数 | 整数 | 否 | `duration_seconds` |
| `v620_reportjson` | 报告内容 | 大文本 | 否 | `report_json` |
| `v620_startedat` | 开始时间 | 长日期/日期时间 | 否 | `started_at` |
| `v620_endedat` | 结束时间 | 长日期/日期时间 | 否 | `ended_at` |

### 009 Agent能力定义

- 对象类型：基础资料
- 对象编码：`v620_cc_agent_cap`
- 原逻辑对象名：`agent_capability`
- 表名：`tk_v620_cc_agent_cap`
- 建模状态：已建立业务对象、字段和基础布局；预置 Agent 记录可后续按运营需要录入。
- 用途：登记 CyanCruise 中可被主调度 Agent 调用的专项 Agent 能力，不是通用 AI 对话框。具体执行过程仍写入“Agent执行记录”，业务结果写回职业测评记录、简历诊断、职业计划、深造记录等业务对象。

| 字段标识 | 名称 | 类型 | 必填 | 逻辑字段 |
| --- | --- | --- | --- | --- |
| `v620_agentid` | Agent ID | 文本，128 | 是 | `agent_id` |
| `v620_agentcode` | Agent编码 | 文本，128 | 是 | `agent_code` |
| `v620_agentname` | Agent名称 | 文本，128 | 是 | `agent_name` |
| `v620_agentrole` | Agent职责 | 大文本 | 否 | `agent_role` |
| `v620_businessmodule` | 业务模块 | 文本，128 | 否 | `business_module` |
| `v620_thinkingflow` | 思维链路 | 大文本 | 否 | `thinking_flow` |
| `v620_mcptoolsjson` | 关键工具 | 大文本 | 否 | `mcp_tools_json` |
| `v620_inputschemajson` | 输入契约 | 大文本 | 否 | `input_schema_json` |
| `v620_outputschemajson` | 输出契约 | 大文本 | 否 | `output_schema_json` |
| `v620_enabled` | 是否启用 | 开关/布尔 | 否 | `enabled` |
| `v620_sortorder` | 排序号 | 整数 | 否 | `sort_order` |
| `v620_createdat` | 创建时间 | 长日期/日期时间 | 否 | `created_at` |
| `v620_updatedat` | 更新时间 | 长日期/日期时间 | 否 | `updated_at` |

建议预置 Agent：

| Agent名称 | 建议编码 | 业务模块 | 职责 |
| --- | --- | --- | --- |
| 主调度Agent | `orchestrator` | Agent运行管理 | 识别用户意图，路由到专项 Agent，聚合结果返回。 |
| 用户画像Agent | `profile` | 职业发展 | 维护专业、年级、GPA、兴趣、目标等用户档案，供其他 Agent 调用。 |
| 信息检索Agent | `retrieval` | Agent运行管理 | 搜索政策、分数线、行业数据、院校信息等实时信息。 |
| 学业规划Agent | `academic_planning` | 职业发展 | 根据 GPA、学分、排名等给出选课和提分建议。 |
| 就业规划Agent | `career_planning` | 职业发展 | 分析专业、兴趣、市场，推荐岗位方向和薪资城市信息。 |
| 简历优化Agent | `resume_optimization` | 职业发展 | 解析经历，诊断简历问题，按经历讲述框架改写并做岗位针对性优化。 |
| 面试辅导Agent | `interview_coach` | 职业发展 | 识别面试类型，生成问题，模拟问答并给出改进建议。 |
| 考研规划Agent | `postgraduate_exam` | 深造陪伴 | 评估初试能力，匹配院校档次，制定备考时间线和科目复习策略。 |
| 保研规划Agent | `postgraduate_recommendation` | 深造陪伴 | 计算综合排名和加分项，判断保研资格，推荐目标院校和夏令营策略。 |
| 留学规划Agent | `study_abroad` | 深造陪伴 | 评估语言、GPA、背景，匹配院校档次，规划申请时间线和文书方向。 |

### 010 深造目标

- 对象类型：基础资料或单据。若用户目标会反复调整且需要历史，建议单据；若只维护当前目标，建议基础资料。
- 对象编码：`v620_cc_study_target`
- 原逻辑对象名：`further_study_target`
- 表名：`tk_v620_cc_study_target`
- 建模状态：已建立业务对象、字段和基础布局。
- 用途：统一承载考研、保研、留学目标。

| 字段标识 | 名称 | 类型 | 必填 | 逻辑字段 |
| --- | --- | --- | --- | --- |
| `v620_targetid` | 目标ID | 文本，128 | 是 | `target_id` |
| `v620_userid` | 用户标识 | 文本，128 | 是 | `user_id` |
| `v620_track` | 深造方向 | 文本，64 | 是 | `track` |
| `v620_targetschool` | 目标学校/项目 | 文本，255 | 否 | `target_school` |
| `v620_targetmajor` | 目标专业/方向 | 文本，255 | 否 | `target_major` |
| `v620_targetregion` | 目标地区 | 文本，255 | 否 | `target_region` |
| `v620_targetstage` | 当前阶段 | 文本，128 | 否 | `target_stage` |
| `v620_status` | 状态 | 文本，64 | 否 | `status` |
| `v620_targetjson` | 目标详情 | 大文本 | 否 | `target_json` |
| `v620_createdat` | 创建时间 | 长日期/日期时间 | 否 | `created_at` |
| `v620_updatedat` | 更新时间 | 长日期/日期时间 | 否 | `updated_at` |

`v620_track` 建议值：考研、保研、留学。代码侧可映射为 `postgraduate_exam`、`postgraduate_recommendation`、`study_abroad`。

### 011 深造记录

- 对象类型：单据
- 对象编码：`v620_cc_study_record`
- 原逻辑对象名：`further_study_record`
- 表名：`tk_v620_cc_study_record`
- 建模状态：已建立业务对象、字段和基础布局。
- 用途：保存考研规划、保研诊断、留学选校等每次生成结果。

| 字段标识 | 名称 | 类型 | 必填 | 逻辑字段 |
| --- | --- | --- | --- | --- |
| `v620_recordid` | 记录ID | 文本，128 | 是 | `record_id` |
| `v620_userid` | 用户标识 | 文本，128 | 是 | `user_id` |
| `v620_track` | 深造方向 | 文本，64 | 是 | `track` |
| `v620_recordtype` | 记录类型 | 文本，128 | 是 | `record_type` |
| `v620_targetid` | 关联目标ID | 文本，128 | 否 | `target_id` |
| `v620_title` | 标题 | 文本，255 | 否 | `title` |
| `v620_status` | 状态 | 文本，64 | 否 | `status` |
| `v620_targetschool` | 目标学校/项目 | 文本，255 | 否 | `target_school` |
| `v620_targetmajor` | 目标专业/方向 | 文本，255 | 否 | `target_major` |
| `v620_targetregion` | 目标地区 | 文本，255 | 否 | `target_region` |
| `v620_examordeadlinedate` | 考试或截止日期 | 日期 | 否 | `exam_or_deadline_date` |
| `v620_requestjson` | 请求内容 | 大文本 | 否 | `request_json` |
| `v620_resultjson` | 结果内容 | 大文本 | 否 | `result_json` |
| `v620_createdat` | 创建时间 | 长日期/日期时间 | 否 | `created_at` |
| `v620_updatedat` | 更新时间 | 长日期/日期时间 | 否 | `updated_at` |

### 012 深造材料

- 对象类型：单据
- 对象编码：`v620_cc_study_material`
- 原逻辑对象名：`further_study_material`
- 表名：`tk_v620_cc_study_material`
- 建模状态：已建立业务对象、字段和基础布局。
- 用途：保存保研文书、导师联系材料、留学个人陈述、签证材料等可持续推进的材料记录。

| 字段标识 | 名称 | 类型 | 必填 | 逻辑字段 |
| --- | --- | --- | --- | --- |
| `v620_materialid` | 材料ID | 文本，128 | 是 | `material_id` |
| `v620_userid` | 用户标识 | 文本，128 | 是 | `user_id` |
| `v620_track` | 深造方向 | 文本，64 | 是 | `track` |
| `v620_recordid` | 来源记录ID | 文本，128 | 否 | `record_id` |
| `v620_materialtype` | 材料类型 | 文本，128 | 是 | `material_type` |
| `v620_title` | 标题 | 文本，255 | 否 | `title` |
| `v620_status` | 状态 | 文本，64 | 否 | `status` |
| `v620_filekey` | 文件标识 | 文本，255 | 否 | `file_key` |
| `v620_contentjson` | 内容详情 | 大文本 | 否 | `content_json` |
| `v620_createdat` | 创建时间 | 长日期/日期时间 | 否 | `created_at` |
| `v620_updatedat` | 更新时间 | 长日期/日期时间 | 否 | `updated_at` |

### 013 深造进展记录

- 对象类型：单据
- 对象编码：`v620_cc_study_event`
- 原逻辑对象名：`further_study_event`
- 表名：`tk_v620_cc_study_event`
- 建模状态：已建立业务对象、字段和基础布局。
- 用途：保存深造目标、记录和材料的状态变化历史。

| 字段标识 | 名称 | 类型 | 必填 | 逻辑字段 |
| --- | --- | --- | --- | --- |
| `v620_eventid` | 事件ID | 文本，128 | 是 | `event_id` |
| `v620_userid` | 用户标识 | 文本，128 | 是 | `user_id` |
| `v620_track` | 深造方向 | 文本，64 | 是 | `track` |
| `v620_recordid` | 来源记录ID | 文本，128 | 否 | `record_id` |
| `v620_eventtype` | 事件类型 | 文本，128 | 是 | `event_type` |
| `v620_summary` | 事件摘要 | 文本，500 | 否 | `summary` |
| `v620_eventjson` | 事件详情 | 大文本 | 否 | `event_json` |
| `v620_createdat` | 创建时间 | 长日期/日期时间 | 否 | `created_at` |

### 014 Agent上下文引用

- 对象类型：单据
- 对象编码：`v620_cc_agent_ctx_ref`
- 原逻辑对象名：`agent_context_ref`
- 表名：`tk_v620_cc_agent_ctx_ref`
- 建模状态：已建立业务对象、字段和基础布局。
- 用途：记录一次 Agent 执行读取了哪些业务对象，为排障和可解释性服务。

| 字段标识 | 名称 | 类型 | 必填 | 逻辑字段 |
| --- | --- | --- | --- | --- |
| `v620_refid` | 引用ID | 文本，128 | 是 | `ref_id` |
| `v620_runid` | 执行ID | 文本，128 | 是 | `run_id` |
| `v620_userid` | 用户标识 | 文本，128 | 是 | `user_id` |
| `v620_sourceobject` | 来源对象 | 文本，128 | 否 | `source_object` |
| `v620_sourceid` | 来源ID | 文本，128 | 否 | `source_id` |
| `v620_sourcesummary` | 来源摘要 | 大文本 | 否 | `source_summary` |
| `v620_writeback` | 是否写回 | 开关/布尔 | 否 | `write_back` |
| `v620_createdat` | 创建时间 | 长日期/日期时间 | 否 | `created_at` |

## 已建立顺序

1. 用户职业画像（已建）
2. Agent执行记录（已建）
3. 职业测评记录（已建）
4. 简历档案（已建）
5. 简历诊断（已建）
6. 今日任务（已建）
7. 职业计划（已建）
8. 模拟面试（已建）
9. Agent能力定义（已建）
10. 深造目标（已建）
11. 深造记录（已建）
12. 深造材料（已建）
13. 深造进展记录（已建）
14. Agent上下文引用（已建）

## 与现有代码的关系

- 当前运行时仍可继续使用 PostgreSQL 存储，不要求立即切换。
- 上述对象字段用于未来 `Cosmic datamodel adapter` 或数据同步映射。
- 代码侧 snake_case 逻辑字段和平台侧 `v620_xxx` 字段需要在 adapter 中显式映射。
- 外部前端链接不受这些布局影响；平台布局主要用于管理员维护、排障、权限和报表。
