# CyanCruise Cosmic Datamodel Map

本映射表记录 IPD 到 CyanCruise 的正式 Cosmic datamodel 适配路线。迁移目标是业务数据语义、状态、流程和接口契约，不迁移 Spring Boot、JPA、Flyway、Lombok、Repository、Vue、uni-app、真实 AI SDK 或生产数据补偿脚本。

## 对象分组

| 对象 | IPD 来源 | 存储边界 | 结构化查询字段 | 半结构化字段 |
| --- | --- | --- | --- | --- |
| `cc_cl_profile_snapshot` | `AgentUserProfile`、画像快照服务 | `CareerProfileStorage.loadSnapshot/saveSnapshot` | `user_id`、`version`、`updated_at` | `snapshot_json` |
| `cc_cl_profile_fact` | `UserFact` | `CareerProfileStorage.loadFacts/saveFact` | `user_id`、`fact_key`、`updated_at` | `fact_value` |
| `cc_cl_user_profile` | `AgentUserProfile` | `CareerProfileStorage.loadProfile/saveProfile` | `user_id`、`personalization_level`、`completeness_score`、`current_stage`、`target_role`、`updated_at` | `profile_json`、`readiness_json`、`evidence_json` |
| `cc_cl_assessment_record` | `AssessmentRecord`、`AssessmentAnswer` | `AssessmentResultStorage` | `record_id`、`user_id`、`scale_id`、`status`、`created_at` | `result_json`、`answers_json` |
| `cc_cl_resume` | `Resume`、`ResumeProfileKeyword` | `ResumeStorage` | `resume_id`、`user_id`、`title`、`target_job`、`file_key`、`status`、`diagnosis_score`、`created_at`、`updated_at` | `parsed_content`、`keyword_json` |
| `cc_cl_task` | `AgentTask` | 后续今日行动存储适配器 | `task_id`、`user_id`、`task_key`、`due_date`、`status`、`priority`、`parent_task_id`、`sub_index`、`updated_at` | `description` |
| `cc_cl_career_plan` | `UserCareerPlan` | `CareerPlanStorage` | `user_id`、`target_role`、`model_used`、`tokens_consumed`、`generated_at`、`last_updated_at`、`version` | `start_state_json`、`milestones_json`、`weekly_focus_json` |
| `cc_cl_interview` | `Interview` | `InterviewStorage` | `interview_id`、`user_id`、`resume_id`、`position_name`、`difficulty`、`status`、`mode`、`final_score`、`started_at`、`ended_at`、`duration_seconds` | `report_json` |
| `cc_cl_interview_message` | `InterviewMessage` | `InterviewStorage` | `message_id`、`interview_id`、`role`、`created_at` | `content` |
| `cc_cl_resume_diagnosis` | `ResumeKeywordService`、简历诊断结果语义 | `ResumeDiagnosisStorage` | `resume_id`、`user_id`、`score`、`created_at`、`updated_at` | `diagnosis_json`、`keyword_status_json` |
| `cc_cl_assistant_session` | `AssistantSession` | `AssistantChatStorage` | `session_id`、`user_id`、`title`、`model_name`、`persona`、`created_at`、`updated_at` | 无 |
| `cc_cl_assistant_message` | `AssistantMessage` | `AssistantChatStorage` | `msg_id`、`session_id`、`role`、`prompt_tokens`、`completion_tokens`、`total_tokens`、`cost_micros`、`created_at` | `content` |

## 适配规则

- 应用服务 SHALL 继续依赖存储边界，而不是直接依赖具体 Cosmic API。
- `base-common` SHALL 保持 DTO-only；`base-helper` SHALL 保持纯 Java 规则。
- 用户归属记录 SHALL 直接包含 `user_id`，或通过父对象校验归属。
- 状态、时间戳、目标岗位、目标职位、排序、父子关系和业务 ID SHALL 使用结构化字段。
- AI 输出、证据、简历解析内容、评分维度和报告正文 MAY 继续使用 JSON 文本，直到报表或检索需求要求进一步结构化。
- 文件存储和内存存储实现 MAY 在引入 Cosmic datamodel adapter 期间继续作为 fallback 或测试替身。

## 与金蝶业务建模的关系

`cyancruise-business-modeling.md` 记录的是金蝶设计器中连续建立业务对象的字段和布局；本文档记录的是后端存储适配层的对象映射。两者可以共享业务语义，但命名粒度不同：

- 金蝶建模对象优先使用 `tk_v620_cc_` 表名前缀，较长对象使用短缩写，例如 `tk_v620_cc_assess_record`、`tk_v620_cc_study_target`。
- 金蝶苍穹对象编码统一使用不超过 25 个字符的 `v620_cc_` 前缀短编码，例如 `v620_cc_assess_record`、`v620_cc_study_target`。
- 金蝶苍穹字段标识统一使用 `v620_` 前缀，且不得以 `_id` 结尾；涉及 ID 语义时使用 `userid`、`runid`、`scaleid`、`resumeid` 等连续写法。
- 存储适配对象沿用 `cc_cl_` 逻辑对象名，便于对应已有 storage boundary。
- 后端 DTO、SQL、PostgreSQL 和存储适配层 MAY 继续使用 `user_id`、`resume_id` 等 snake_case 逻辑字段；金蝶字段标识 SHALL 在建模文档中标明对应逻辑字段。
- 面向用户的页面、按钮、提示和文档说明使用中文；代码标识、表名、字段名和 OpenSpec 关键字保留英文。
