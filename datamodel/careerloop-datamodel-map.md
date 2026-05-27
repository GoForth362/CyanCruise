# CareerLoop Cosmic Datamodel Map

本映射表记录 IPD 到 CyanCruise 的正式 Cosmic datamodel 适配路线。它只迁移业务数据语义、状态和接口契约，不迁移 Spring Boot、JPA、Flyway、Lombok、repository、Vue、uni-app、真实 AI SDK 或生产数据补偿脚本。

## Object Groups

| Object | IPD Source | Storage Boundary | Structured Query Fields | Semi-structured Fields |
| --- | --- | --- | --- | --- |
| `cc_cl_profile_snapshot` | `AgentUserProfile`, profile snapshot services | `CareerProfileStorage.loadSnapshot/saveSnapshot` | `user_id`, `version`, `updated_at` | `snapshot_json` |
| `cc_cl_profile_fact` | `UserFact` | `CareerProfileStorage.loadFacts/saveFact` | `user_id`, `fact_key`, `updated_at` | `fact_value` |
| `cc_cl_user_profile` | `AgentUserProfile` | `CareerProfileStorage.loadProfile/saveProfile` | `user_id`, `personalization_level`, `completeness_score`, `current_stage`, `target_role`, `updated_at` | `profile_json`, `readiness_json`, `evidence_json` |
| `cc_cl_assessment_record` | `AssessmentRecord`, `AssessmentAnswer` | `AssessmentResultStorage` | `record_id`, `user_id`, `scale_id`, `status`, `created_at` | `result_json`, `answers_json` |
| `cc_cl_resume` | `Resume`, `ResumeProfileKeyword` | `ResumeStorage` | `resume_id`, `user_id`, `title`, `target_job`, `file_key`, `status`, `diagnosis_score`, `created_at`, `updated_at` | `parsed_content`, `keyword_json` |
| `cc_cl_task` | `AgentTask` | future today-action storage adapter | `task_id`, `user_id`, `task_key`, `due_date`, `status`, `priority`, `parent_task_id`, `sub_index`, `updated_at` | `description` |
| `cc_cl_career_plan` | `UserCareerPlan` | `CareerPlanStorage` | `user_id`, `target_role`, `model_used`, `tokens_consumed`, `generated_at`, `last_updated_at`, `version` | `start_state_json`, `milestones_json`, `weekly_focus_json` |
| `cc_cl_interview` | `Interview` | `InterviewStorage` | `interview_id`, `user_id`, `resume_id`, `position_name`, `difficulty`, `status`, `mode`, `final_score`, `started_at`, `ended_at`, `duration_seconds` | `report_json` |
| `cc_cl_interview_message` | `InterviewMessage` | `InterviewStorage` | `message_id`, `interview_id`, `role`, `created_at` | `content` |
| `cc_cl_resume_diagnosis` | `ResumeKeywordService`, diagnosis result semantics | `ResumeDiagnosisStorage` | `resume_id`, `user_id`, `score`, `created_at`, `updated_at` | `diagnosis_json`, `keyword_status_json` |
| `cc_cl_assistant_session` | `AssistantSession` | `AssistantChatStorage` | `session_id`, `user_id`, `title`, `model_name`, `persona`, `created_at`, `updated_at` | none |
| `cc_cl_assistant_message` | `AssistantMessage` | `AssistantChatStorage` | `msg_id`, `session_id`, `role`, `prompt_tokens`, `completion_tokens`, `total_tokens`, `cost_micros`, `created_at` | `content` |

## Adapter Rules

- App services SHALL continue to depend on storage boundaries, not concrete Cosmic APIs.
- `base-common` SHALL remain DTO-only; `base-helper` SHALL remain pure Java rules.
- User-owned records SHALL include `user_id` directly or validate ownership through their parent object.
- Status, timestamps, target role/job, sort order, parent-child linkage and business ids SHALL be structured fields.
- AI output, evidence, parsed resume content, score dimensions and report bodies MAY remain JSON text until reporting requirements require further normalization.
- File and memory storage implementations MAY remain as fallback or tests while Cosmic datamodel adapters are introduced.
