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

## 功能分组建议

如果业务对象已经全部建在“首页”下，不需要删除重建；在金蝶开发平台中使用“移动页面”把页面移动到下面的功能分组即可。功能分组只影响平台里的功能树、菜单整理和后续授权维护，不改变对象编码、表名和字段。

建议先建立这些一级或二级功能分组：

- 首页
- 职业发展
- 简历与面试
- 深造陪伴
- Agent运行管理
- 管理后台

对象移动建议如下：

| 对象编号 | 对象名称 | 对象编码 | 建议功能分组 | 原因 |
| --- | --- | --- | --- | --- |
| 001 | 用户职业画像 | `v620_cc_user_profile` | 首页 | 这是用户主画像，首页、测评、简历、面试、深造都会读取。 |
| 002 | Agent执行记录 | `v620_cc_agent_run` | Agent运行管理 | 这是排障和运行追踪对象，不属于普通用户菜单。 |
| 003 | 职业测评记录 | `v620_cc_assess_record` | 职业发展 | 对应职业测评和岗位方向分析。 |
| 004 | 简历档案 | `v620_cc_resume_record` | 简历与面试 | 对应简历制作、上传、解析。 |
| 005 | 简历诊断 | `v620_cc_resume_diag` | 简历与面试 | 对应 AI 简历诊断结果。 |
| 006 | 今日任务 | `v620_cc_career_task` | 首页 | 首页会展示下一步行动，放首页最容易理解。 |
| 007 | 职业计划 | `v620_cc_career_plan` | 职业发展 | 对应 AI 路径规划和长期职业计划。 |
| 008 | 模拟面试 | `v620_cc_interview` | 简历与面试 | 对应 AI 模拟面试和全景仿真面试记录。 |
| 009 | Agent能力定义 | `v620_cc_agent_cap` | Agent运行管理 | 这是后台配置型对象，不给普通用户直接使用。 |
| 010 | 深造目标 | `v620_cc_study_target` | 深造陪伴 | 考研、保研、留学共用目标对象。 |
| 011 | 深造记录 | `v620_cc_study_record` | 深造陪伴 | 保存深造规划、诊断、选校等生成结果。 |
| 012 | 深造材料 | `v620_cc_study_material` | 深造陪伴 | 保存文书、导师联系、签证等材料。 |
| 013 | 深造进展记录 | `v620_cc_study_event` | 深造陪伴 | 保存深造过程事件和状态变化。 |
| 014 | Agent上下文引用 | `v620_cc_agent_ctx_ref` | Agent运行管理 | 用于解释 Agent 读取了哪些上下文。 |
| 015 | 用户账号治理 | `v620_cc_user_account` | 管理后台 | 对应管理端“用户管理”。 |
| 016 | 题库题目 | `v620_cc_question` | 管理后台 | 对应管理端“题库审核”。 |
| 017 | 内容资源 | `v620_cc_content` | 管理后台 | 对应管理端“内容管理”。 |
| 018 | 通知公告 | `v620_cc_notice` | 管理后台 | 对应管理端“通知公告”。 |
| 019 | 管理审计日志 | `v620_cc_admin_audit` | 管理后台 | 对应管理端“操作记录”。 |

如果你暂时不想建太多分组，也可以用最小分组法：

| 功能分组 | 放入对象 |
| --- | --- |
| 首页 | 用户职业画像、今日任务 |
| 职业发展 | 职业测评记录、职业计划、简历档案、简历诊断、模拟面试 |
| 深造陪伴 | 深造目标、深造记录、深造材料、深造进展记录 |
| Agent运行管理 | Agent执行记录、Agent能力定义、Agent上下文引用 |
| 管理后台 | 用户账号治理、题库题目、内容资源、通知公告、管理审计日志 |

移动完成后，建议重新检查每个对象的“功能分组”和“页面菜单”：

1. 普通用户能看到的页面只保留首页、职业发展、简历与面试、深造陪伴相关入口。
2. 管理后台对象不要放到普通用户分组下，后续授权时统一给管理员分组。
3. Agent运行管理对象默认只给开发者或管理员查看，不作为学生端功能入口。
4. 如果移动后菜单不刷新，先保存并发布元数据，再重新进入应用菜单查看。

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

## 管理端业务对象建模补充

管理端是否需要建模，取决于你要做到哪一层：

- 只判断“谁能进入管理端”：不需要新增业务对象，管理员身份来自金蝶安全管理里的管理员/管理员分组，CyanCruise 后端通过平台身份上下文识别。
- 只做管理端页面原型：不需要新增业务对象，可以继续读取现有 PostgreSQL 或降级数据。
- 要实现可用的用户管理、内容管理、题库审核、通知公告和审计日志：需要补充下面的管理端业务对象，否则禁用用户、审核题目、置顶内容、发送公告和查看操作记录都没有稳定落库位置。

管理端对象建议从“最小闭环”开始建 5 个：

1. 用户账号治理
2. 题库题目
3. 内容资源
4. 通知公告
5. 管理审计日志

“管理员是谁”不要在这些业务对象里维护。管理员权限仍然在金蝶“安全管理 / 管理员列表 / 管理员分组”中维护；业务对象只记录管理员做了什么、影响了什么数据。

### 管理端建模操作步骤

每个管理端对象都按下面步骤在金蝶云苍穹开发平台中创建：

1. 进入“开发平台 / 应用管理 / CyanCruise / 数据模型”。
2. 新增业务对象，选择对象类型。用户账号治理可以用“基础资料”；题库题目、内容资源、通知公告、管理审计日志建议用“单据”。
3. 填写对象名称、对象编码和表名。对象编码使用 `v620_cc_xxx`，表名使用 `tk_v620_cc_xxx`。
4. 按本文字段表新增字段。字段标识以平台保存后的 `v620_xxx` 为准，不使用 `_id` 结尾。
5. 保存后进入布局设计，把常用查询字段放在前面，把 JSON、大文本、快照字段放在后面或独立分组。
6. 发布元数据。
7. 回到本文把“建模状态”从“待建立”改为“已建立业务对象、字段和基础布局”。
8. 后续再由 Java adapter 或同步任务把 PostgreSQL/接口数据映射到这些平台对象。

### 015 用户账号治理

- 对象类型：基础资料
- 对象编码：`v620_cc_user_account`
- 原逻辑对象名：`admin_user_governance`
- 表名：`tk_v620_cc_user_account`
- 建模状态：待建立。
- 用途：支撑管理端“用户管理”，保存用户状态、禁用原因、组织和最近活动信息。它不是金蝶登录账号表，也不负责授予管理员权限。

| 字段标识 | 名称 | 类型 | 必填 | 逻辑字段 | 说明 |
| --- | --- | --- | --- | --- | --- |
| `v620_userid` | 用户标识 | 文本，128 | 是 | `user_id` | CyanCruise 用户归属标识。 |
| `v620_username` | 用户名称 | 文本，128 | 否 | `user_name` | 平台姓名或应用昵称。 |
| `v620_nickname` | 昵称 | 文本，128 | 否 | `nickname` | 用户端展示名称。 |
| `v620_orgid` | 组织标识 | 文本，128 | 否 | `org_id` | 所属组织或学校组织。 |
| `v620_school` | 学校 | 文本，255 | 否 | `school` | 用户学校。 |
| `v620_major` | 专业 | 文本，255 | 否 | `major` | 用户专业。 |
| `v620_status` | 账号状态 | 文本，64 | 是 | `status` | active、banned、deleted 或中文状态。 |
| `v620_bannedreason` | 禁用原因 | 大文本 | 否 | `banned_reason` | 管理员禁用说明。 |
| `v620_lastactiveat` | 最近活跃时间 | 长日期/日期时间 | 否 | `last_active_at` | 最近一次使用时间。 |
| `v620_createdat` | 创建时间 | 长日期/日期时间 | 否 | `created_at` | 首次纳入治理时间。 |
| `v620_updatedat` | 更新时间 | 长日期/日期时间 | 否 | `updated_at` | 最近更新账号状态的时间。 |

推荐布局：

- 基础信息：用户标识、用户名称、昵称、组织标识、学校、专业
- 治理状态：账号状态、禁用原因、最近活跃时间
- 系统字段：创建时间、更新时间

### 016 题库题目

- 对象类型：单据
- 对象编码：`v620_cc_question`
- 原逻辑对象名：`question_bank_item`
- 表名：`tk_v620_cc_question`
- 建模状态：待建立。
- 用途：支撑管理端“题库审核”，保存用户贡献题、AI 生成题、审核状态和答案内容。

| 字段标识 | 名称 | 类型 | 必填 | 逻辑字段 | 说明 |
| --- | --- | --- | --- | --- | --- |
| `v620_questionid` | 题目ID | 文本，128 | 是 | `question_id` | 题目稳定标识。 |
| `v620_content` | 题目内容 | 大文本 | 是 | `content` | 面试题或练习题正文。 |
| `v620_summary` | 题目摘要 | 文本，500 | 否 | `summary` | 管理列表展示摘要。 |
| `v620_position` | 适用岗位 | 文本，255 | 否 | `position` | 如后端开发、产品经理。 |
| `v620_difficulty` | 难度 | 文本，64 | 否 | `difficulty` | easy、normal、hard 或中文难度。 |
| `v620_source` | 来源 | 文本，64 | 是 | `source` | user、ai、system。 |
| `v620_reviewstatus` | 审核状态 | 文本，64 | 是 | `review_status` | pending、published、rejected。 |
| `v620_status` | 展示状态 | 文本，64 | 否 | `status` | visible、hidden、deleted。 |
| `v620_answerjson` | 答案内容 | 大文本 | 否 | `answer_json` | 标准答案、评分点或解析 JSON。 |
| `v620_contributorhash` | 贡献者标识 | 文本，128 | 否 | `contributor_hash` | 匿名化贡献者标识，不保存敏感身份。 |
| `v620_createdat` | 创建时间 | 长日期/日期时间 | 否 | `created_at` | 创建时间。 |
| `v620_reviewedat` | 审核时间 | 长日期/日期时间 | 否 | `reviewed_at` | 最近审核时间。 |
| `v620_updatedat` | 更新时间 | 长日期/日期时间 | 否 | `updated_at` | 最近更新时间。 |

推荐布局：

- 题目基础：题目ID、适用岗位、难度、来源、审核状态、展示状态
- 题目内容：题目摘要、题目内容、答案内容
- 审核信息：贡献者标识、审核时间、创建时间、更新时间

### 017 内容资源

- 对象类型：单据
- 对象编码：`v620_cc_content`
- 原逻辑对象名：`admin_content_item`
- 表名：`tk_v620_cc_content`
- 建模状态：待建立。
- 用途：支撑管理端“内容管理”，管理首页文章、视频、资源链接、置顶和隐藏状态。

| 字段标识 | 名称 | 类型 | 必填 | 逻辑字段 | 说明 |
| --- | --- | --- | --- | --- | --- |
| `v620_contentid` | 内容ID | 文本，128 | 是 | `content_id` | 内容稳定标识。 |
| `v620_title` | 标题 | 文本，255 | 是 | `title` | 内容标题。 |
| `v620_contenttype` | 内容类型 | 文本，64 | 是 | `content_type` | article、video、resource。 |
| `v620_summary` | 摘要 | 文本，500 | 否 | `summary` | 列表摘要。 |
| `v620_linkurl` | 链接地址 | 文本，1000 | 否 | `link_url` | 外部或内部资源链接。 |
| `v620_coverfilekey` | 封面文件标识 | 文本，255 | 否 | `cover_file_key` | BOS 文件 key，不保存签名 URL。 |
| `v620_pinned` | 是否置顶 | 开关/布尔 | 否 | `pinned` | 管理端置顶开关。 |
| `v620_hidden` | 是否隐藏 | 开关/布尔 | 否 | `hidden` | 用户端是否隐藏。 |
| `v620_sortorder` | 排序号 | 整数 | 否 | `sort_order` | 同组内容排序。 |
| `v620_contentjson` | 内容详情 | 大文本 | 否 | `content_json` | 正文、标签、来源等扩展内容。 |
| `v620_publishedat` | 发布时间 | 长日期/日期时间 | 否 | `published_at` | 对用户展示的发布时间。 |
| `v620_updatedat` | 更新时间 | 长日期/日期时间 | 否 | `updated_at` | 最近更新时间。 |

推荐布局：

- 基础信息：内容ID、标题、内容类型、摘要
- 展示控制：是否置顶、是否隐藏、排序号、发布时间
- 资源信息：链接地址、封面文件标识、内容详情、更新时间

### 018 通知公告

- 对象类型：单据
- 对象编码：`v620_cc_notice`
- 原逻辑对象名：`notification_record`
- 表名：`tk_v620_cc_notice`
- 建模状态：待建立。
- 用途：支撑管理端“通知公告”，保存管理员给单个用户或所有用户发送的公告记录。用户端消息也可复用该对象。

| 字段标识 | 名称 | 类型 | 必填 | 逻辑字段 | 说明 |
| --- | --- | --- | --- | --- | --- |
| `v620_noticeid` | 公告ID | 文本，128 | 是 | `notice_id` | 公告稳定标识。 |
| `v620_userid` | 接收用户标识 | 文本，128 | 否 | `user_id` | 为空表示面向所有正常用户。 |
| `v620_noticetype` | 公告类型 | 文本，64 | 是 | `notice_type` | admin_broadcast、system、reminder。 |
| `v620_title` | 标题 | 文本，255 | 是 | `title` | 公告标题。 |
| `v620_content` | 内容 | 大文本 | 是 | `content` | 公告正文。 |
| `v620_linkroute` | 跳转页面 | 文本，255 | 否 | `link_route` | 如 resume、interview-home。 |
| `v620_status` | 状态 | 文本，64 | 否 | `status` | sent、failed、read、archived。 |
| `v620_adminid` | 管理员标识 | 文本，128 | 否 | `admin_id` | 发送公告的管理员。 |
| `v620_createdat` | 创建时间 | 长日期/日期时间 | 否 | `created_at` | 发送时间。 |
| `v620_readat` | 阅读时间 | 长日期/日期时间 | 否 | `read_at` | 用户已读时间。 |

推荐布局：

- 公告信息：公告ID、公告类型、标题、内容、跳转页面
- 投递信息：接收用户标识、状态、管理员标识、创建时间、阅读时间

### 019 管理审计日志

- 对象类型：单据
- 对象编码：`v620_cc_admin_audit`
- 原逻辑对象名：`admin_audit_log`
- 表名：`tk_v620_cc_admin_audit`
- 建模状态：待建立。
- 用途：支撑管理端“操作记录”，保存管理员所有状态变更操作，便于排障、追责和运营复盘。

| 字段标识 | 名称 | 类型 | 必填 | 逻辑字段 | 说明 |
| --- | --- | --- | --- | --- | --- |
| `v620_auditid` | 审计ID | 文本，128 | 是 | `audit_id` | 审计记录稳定标识。 |
| `v620_adminid` | 管理员标识 | 文本，128 | 是 | `admin_id` | 执行操作的管理员。 |
| `v620_action` | 操作类型 | 文本，128 | 是 | `action` | ban_user、approve_question、pin_content 等。 |
| `v620_targettype` | 目标类型 | 文本，128 | 否 | `target_type` | user、question、content、notice。 |
| `v620_targetid` | 目标ID | 文本，128 | 否 | `target_id` | 被操作记录的稳定标识。 |
| `v620_beforejson` | 变更前内容 | 大文本 | 否 | `before_json` | 变更前快照，需脱敏。 |
| `v620_afterjson` | 变更后内容 | 大文本 | 否 | `after_json` | 变更后快照，需脱敏。 |
| `v620_ip` | IP地址 | 文本，128 | 否 | `ip` | 操作来源 IP。 |
| `v620_useragent` | 浏览器信息 | 文本，1000 | 否 | `user_agent` | 操作来源 UA。 |
| `v620_createdat` | 创建时间 | 长日期/日期时间 | 是 | `created_at` | 操作发生时间。 |

推荐布局：

- 操作信息：审计ID、管理员标识、操作类型、目标类型、目标ID、创建时间
- 来源信息：IP地址、浏览器信息
- 快照内容：变更前内容、变更后内容

### 管理端对象建立顺序

建议按下面顺序建立，先保证最小可用后台闭环：

15. 用户账号治理（待建）
16. 管理审计日志（待建）
17. 题库题目（待建）
18. 内容资源（待建）
19. 通知公告（待建）

原因：

- 用户管理依赖“用户账号治理”。
- 所有管理写操作都应写“管理审计日志”，所以审计日志应尽早建立。
- 题库、内容、公告是管理端三个主要业务操作区，可以分批接入。

### 管理端对象与页面功能对应关系

| 管理端页签 | 必需对象 | 可复用对象 | 说明 |
| --- | --- | --- | --- |
| 总览 | 暂不强制新增 | 用户账号治理、职业测评记录、模拟面试、管理审计日志 | 总览可以先聚合已有记录；后续如需事件明细，再补使用事件对象。 |
| 用户管理 | 用户账号治理 | 用户职业画像 | 禁用、恢复、搜索用户建议落在用户账号治理。 |
| 内容管理 | 内容资源 | 无 | 文章、视频、资源的置顶和隐藏状态落在内容资源。 |
| 题库审核 | 题库题目 | 模拟面试 | 用户贡献题、AI 生成题和审核状态落在题库题目。 |
| 通知公告 | 通知公告 | 用户账号治理 | 群发时根据用户账号治理筛选正常用户。 |
| 操作记录 | 管理审计日志 | 所有被操作对象 | 每次管理写操作都追加审计日志。 |

### 暂不建议建模的内容

- 管理员、管理员分组、管理员权限：使用金蝶“安全管理”维护，不在 CyanCruise 业务对象中重复建表。
- 管理端菜单：使用金蝶“应用菜单 / 外部链接”配置，不建业务对象。
- OpenAPI 路由：使用开发平台 OpenAPI 配置，不建业务对象。
- KAPI token、clientSecret、access_token：属于系统配置或运行时凭证，禁止保存到业务对象。
- 临时页面状态、前端 tab 选中状态：前端状态，不建业务对象。

## 与现有代码的关系

- 当前运行时仍可继续使用 PostgreSQL 存储，不要求立即切换。
- 上述对象字段用于未来 `Cosmic datamodel adapter` 或数据同步映射。
- 代码侧 snake_case 逻辑字段和平台侧 `v620_xxx` 字段需要在 adapter 中显式映射。
- 外部前端链接不受这些布局影响；平台布局主要用于管理员维护、排障、权限和报表。

## 与前后端代码对应性确认

结论：当前业务建模和 CyanCruise 的前后端功能边界是对应的，可以作为后续接入金蝶业务对象的数据结构基础；但它还不是运行时的直接数据源。现有前端主要通过 WebAPI 路由访问后端，后端主要使用 DTO、应用服务和当前存储实现承载业务数据，后续需要通过 adapter 或同步任务把这些 DTO 字段映射到平台侧 `v620_xxx` 字段。

### 用户端对象对应关系

| 业务对象 | 主要前端入口或接口 | 主要后端契约 | 当前状态 |
| --- | --- | --- | --- |
| 用户职业画像 `v620_cc_user_profile` | 首页、用户画像、`/cc001/career-profile/*` | `CareerUserProfileDto`、`CareerProfileDraftDto`、`UserProfileSnapshot` | 语义对应；平台字段需要由 adapter 映射。 |
| Agent执行记录 `v620_cc_agent_run` | 今日行动、Agent 调用链路 | Agent 调用、AI 使用记录相关服务 | 部分对应；适合作为后续运行记录落库对象。 |
| 职业测评记录 `v620_cc_assess_record` | 测评页面、`/cc001/assessment/*` | `AssessmentScaleDto`、`AssessmentQuestionDto`、测评提交与记录 DTO | 对应。 |
| 简历档案 `v620_cc_resume_record` | 简历制作、`/cc001/resume/*` | `ResumeRecordDto`、`ResumeCreateRequest` | 对应。 |
| 简历诊断 `v620_cc_resume_diag` | 简历诊断、`/cc001/resume-diagnosis/*` | `ResumeDiagnosisRequest`、`ResumeDiagnosisResultDto` | 对应。 |
| 今日任务 `v620_cc_career_task` | 首页今日行动、`/cc001/career-agent/today/get` | `CareerAgentTodayDto` | 语义对应；当前更偏生成型结果，后续可落为任务记录。 |
| 职业计划 `v620_cc_career_plan` | 职业规划、`/cc001/career-plan/*` | `CareerPlanSummaryDto`、`CareerPlanRecordDto`、计划阶段 DTO | 对应。 |
| 模拟面试 `v620_cc_interview` | 面试中心、`/cc001/interview/*` | `InterviewSessionDto`、`InterviewMessageDto`、`InterviewReportDto` | 对应。 |
| Agent能力定义 `v620_cc_agent_cap` | 暂无独立用户端页面 | Agent 能力和配置类逻辑 | 预留对象；用于后续把 Agent 能力配置平台化。 |
| 深造目标 `v620_cc_study_target` | 深造护航、`/cc001/further-study/*` | `FurtherStudyTargetDto`、深造目标保存请求 | 对应。 |
| 深造记录 `v620_cc_study_record` | 深造护航记录、`/cc001/further-study/records/*` | `FurtherStudyRecordSummaryDto`、`FurtherStudyRecordDetailDto` | 对应。 |
| 深造材料 `v620_cc_study_material` | 深造材料、`/cc001/further-study/materials/*` | `FurtherStudyMaterialDto` | 对应。 |
| 深造进展记录 `v620_cc_study_event` | 深造事件、`/cc001/further-study/records/events` | `FurtherStudyEventDto` | 对应。 |
| Agent上下文引用 `v620_cc_agent_ctx_ref` | 暂无独立页面 | Agent 上下文、解释与追踪类逻辑 | 预留对象；用于后续记录生成依据和上下文来源。 |

### 管理端对象对应关系

| 业务对象 | 主要管理端页面或接口 | 主要后端契约 | 当前状态 |
| --- | --- | --- | --- |
| 用户账号治理 `v620_cc_user_account` | 用户管理、`/cc001/admin/users/*` | `AdminUserDto` | 页面和接口语义对应；后续需要接平台对象存储。 |
| 题库题目 `v620_cc_question` | 题库审核、`/cc001/admin/questions/*` | `AdminQuestionDto` | 页面和接口语义对应；后续需要接平台对象存储。 |
| 内容资源 `v620_cc_content` | 内容管理、`/cc001/admin/content/*` | `AdminContentItemDto` | 页面和接口语义对应；后续需要接平台对象存储。 |
| 通知公告 `v620_cc_notice` | 通知公告、管理广播接口 | `AdminBroadcastRequest`、通知记录 DTO | 页面和接口语义对应；后续需要接平台对象存储。 |
| 管理审计日志 `v620_cc_admin_audit` | 操作记录、`/cc001/admin/audit-log/list` | `AdminAuditLogDto` | 页面和接口语义对应；后续所有管理写操作应统一追加审计。 |

### 需要注意的不对应点

- 前端不直接认识 `v620_cc_*` 对象编码；前端只认识页面路由和 WebAPI 路由。
- 后端 DTO 字段不要求和平台字段逐字同名；以逻辑字段为准，通过 adapter 映射到 `v620_xxx`。
- 管理员身份、管理员分组和管理员权限来自金蝶安全管理，不属于 CyanCruise 业务对象。
- 如果希望平台业务对象成为真实数据源，下一步要实现 `Cosmic datamodel adapter` 或数据同步服务；只在金蝶平台建模不会自动替换现有后端存储。
