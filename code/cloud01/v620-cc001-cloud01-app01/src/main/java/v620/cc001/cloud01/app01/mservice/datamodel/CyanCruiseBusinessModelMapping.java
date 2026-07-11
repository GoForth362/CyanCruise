package v620.cc001.cloud01.app01.mservice.datamodel;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Maps CyanCruise logical datamodel names to Kingdee business object names.
 */
public final class CyanCruiseBusinessModelMapping {

    private static final Map<String, String> OBJECT_TO_PLATFORM;
    private static final Map<String, String> OBJECT_TO_LOGICAL;
    private static final Map<String, String> FIELD_TO_PLATFORM;
    private static final Map<String, String> FIELD_TO_LOGICAL;

    static {
        Map<String, String> objects = new LinkedHashMap<String, String>();
        objects.put(CyanCruiseDatamodelObjects.USER_PROFILE, "v620_cc_user_profile");
        objects.put(CyanCruiseDatamodelObjects.ASSESSMENT_RECORD, "v620_cc_assess_record");
        objects.put(CyanCruiseDatamodelObjects.RESUME, "v620_cc_resume_record");
        objects.put(CyanCruiseDatamodelObjects.RESUME_DIAGNOSIS, "v620_cc_resume_diag");
        objects.put(CyanCruiseDatamodelObjects.TASK, "v620_cc_career_task");
        objects.put(CyanCruiseDatamodelObjects.CAREER_PLAN, "v620_cc_career_plan");
        objects.put(CyanCruiseDatamodelObjects.INTERVIEW, "v620_cc_interview");
        objects.put(CyanCruiseDatamodelObjects.AGENT_RUN, "v620_cc_agent_run");
        objects.put(CyanCruiseDatamodelObjects.AGENT_CAPABILITY, "v620_cc_agent_cap");
        objects.put(CyanCruiseDatamodelObjects.STUDY_TARGET, "v620_cc_study_target");
        objects.put(CyanCruiseDatamodelObjects.STUDY_RECORD, "v620_cc_study_record");
        objects.put(CyanCruiseDatamodelObjects.STUDY_MATERIAL, "v620_cc_study_material");
        objects.put(CyanCruiseDatamodelObjects.STUDY_EVENT, "v620_cc_study_event");
        objects.put(CyanCruiseDatamodelObjects.AGENT_CONTEXT_REF, "v620_cc_agent_ctx_ref");
        objects.put(CyanCruiseDatamodelObjects.NOTICE, "v620_cc_notice");
        objects.put(CyanCruiseDatamodelObjects.USER_ACCOUNT, "v620_cc_user_account");
        objects.put(CyanCruiseDatamodelObjects.QUESTION, "v620_cc_question");
        objects.put(CyanCruiseDatamodelObjects.CONTENT, "v620_cc_content");
        objects.put(CyanCruiseDatamodelObjects.ADMIN_AUDIT, "v620_cc_admin_audit");
        OBJECT_TO_PLATFORM = Collections.unmodifiableMap(objects);
        OBJECT_TO_LOGICAL = Collections.unmodifiableMap(reverse(objects));

        Map<String, String> fields = new LinkedHashMap<String, String>();
        putCommon(fields);
        putCareerProfile(fields);
        putAssessment(fields);
        putResume(fields);
        putTaskAndPlan(fields);
        putInterview(fields);
        putAgent(fields);
        putFurtherStudy(fields);
        putNoticeAndAdmin(fields);
        FIELD_TO_PLATFORM = Collections.unmodifiableMap(toPlatformTableFields(fields));
        FIELD_TO_LOGICAL = Collections.unmodifiableMap(reverse(FIELD_TO_PLATFORM));
    }

    private CyanCruiseBusinessModelMapping() {
    }

    public static String toPlatformObject(String objectName) {
        String mapped = OBJECT_TO_PLATFORM.get(objectName);
        return mapped == null ? objectName : mapped;
    }

    public static String toLogicalObject(String objectName) {
        String mapped = OBJECT_TO_LOGICAL.get(objectName);
        return mapped == null ? objectName : mapped;
    }

    public static String toPlatformField(String fieldName) {
        String mapped = FIELD_TO_PLATFORM.get(fieldName);
        return mapped == null ? fieldName : mapped;
    }

    public static String toLogicalField(String fieldName) {
        String mapped = FIELD_TO_LOGICAL.get(fieldName);
        return mapped == null ? fieldName : mapped;
    }

    public static List<String> platformFieldsForObject(String objectName) {
        String logicalObject = toLogicalObject(objectName);
        List<String> fields = new ArrayList<String>();
        fields.add(CyanCruiseDatamodelObjects.ID);
        fields.add(toPlatformField(CyanCruiseDatamodelObjects.USER_ID));
        if (CyanCruiseDatamodelObjects.USER_PROFILE.equals(logicalObject)) {
            add(fields, "target_role", "current_stage", "personalization_level", "completeness_score",
                    "profile_json", "readiness_json", "evidence_json", "agent_summary", "last_agent_run_id");
        } else if (CyanCruiseDatamodelObjects.RESUME.equals(logicalObject)) {
            fields.add(toPlatformField(CyanCruiseDatamodelObjects.CREATED_AT));
            fields.add(toPlatformField(CyanCruiseDatamodelObjects.UPDATED_AT));
            add(fields, "resume_id", "title", "target_job", "file_key", "version", "status",
                    "parsed_content", "diagnosis_score", "keyword_json");
        } else if (CyanCruiseDatamodelObjects.RESUME_DIAGNOSIS.equals(logicalObject)) {
            fields.add(toPlatformField(CyanCruiseDatamodelObjects.CREATED_AT));
            fields.add(toPlatformField(CyanCruiseDatamodelObjects.UPDATED_AT));
            add(fields, "diagnosis_id", "resume_id", "score", "diagnosis_json", "keyword_status_json");
        } else if (CyanCruiseDatamodelObjects.ASSESSMENT_RECORD.equals(logicalObject)) {
            fields.add(toPlatformField(CyanCruiseDatamodelObjects.CREATED_AT));
            fields.add(toPlatformField(CyanCruiseDatamodelObjects.UPDATED_AT));
            add(fields, "record_id", "scale_id", "scale_title", "status", "summary",
                    "suggested_roles_json", "answers_json", "result_json", "completed_at");
        } else if (CyanCruiseDatamodelObjects.INTERVIEW.equals(logicalObject)) {
            fields.add(toPlatformField(CyanCruiseDatamodelObjects.CREATED_AT));
            fields.add(toPlatformField(CyanCruiseDatamodelObjects.UPDATED_AT));
            add(fields, "interview_id", "resume_id", "position_name", "difficulty", "status", "mode",
                    "final_score", "duration_seconds", "report_json", "started_at", "ended_at");
        } else if (CyanCruiseDatamodelObjects.TASK.equals(logicalObject)) {
            fields.add(toPlatformField(CyanCruiseDatamodelObjects.CREATED_AT));
            fields.add(toPlatformField(CyanCruiseDatamodelObjects.UPDATED_AT));
            add(fields, "task_id", "task_key", "title", "description", "due_date", "priority",
                    "parent_task_id", "sub_index", "status");
        } else if (CyanCruiseDatamodelObjects.CAREER_PLAN.equals(logicalObject)) {
            fields.add(toPlatformField(CyanCruiseDatamodelObjects.CREATED_AT));
            fields.add(toPlatformField(CyanCruiseDatamodelObjects.UPDATED_AT));
            add(fields, "plan_id", "target_role", "model_used", "tokens_consumed", "start_state_json",
                    "milestones_json", "weekly_focus_json", "version", "generated_at", "last_updated_at", "plan_json");
        } else if (CyanCruiseDatamodelObjects.NOTICE.equals(logicalObject)) {
            fields.add(toPlatformField(CyanCruiseDatamodelObjects.CREATED_AT));
            fields.add(toPlatformField(CyanCruiseDatamodelObjects.UPDATED_AT));
            add(fields, "notice_id", "notice_type", "title", "content", "link_route", "status", "admin_id", "read_at");
        }
        return fields;
    }

    public static CosmicDatamodelRecord toPlatformRecord(CosmicDatamodelRecord record) {
        return mapRecord(record, true);
    }

    public static CosmicDatamodelRecord toLogicalRecord(CosmicDatamodelRecord record) {
        return mapRecord(record, false);
    }

    private static CosmicDatamodelRecord mapRecord(CosmicDatamodelRecord record, boolean platform) {
        if (record == null) {
            return null;
        }
        String objectName = platform ? toPlatformObject(record.getObjectName()) : toLogicalObject(record.getObjectName());
        CosmicDatamodelRecord mapped = new CosmicDatamodelRecord(objectName);
        for (Map.Entry<String, Object> entry : record.getFields().entrySet()) {
            String fieldName = platform ? toPlatformField(entry.getKey()) : toLogicalField(entry.getKey());
            mapped.set(fieldName, entry.getValue());
        }
        return mapped;
    }

    private static Map<String, String> reverse(Map<String, String> source) {
        Map<String, String> reversed = new LinkedHashMap<String, String>();
        for (Map.Entry<String, String> entry : source.entrySet()) {
            reversed.put(entry.getValue(), entry.getKey());
        }
        return reversed;
    }

    private static Map<String, String> toPlatformTableFields(Map<String, String> source) {
        Map<String, String> mapped = new LinkedHashMap<String, String>();
        for (Map.Entry<String, String> entry : source.entrySet()) {
            String value = entry.getValue();
            mapped.put(entry.getKey(), value != null && value.startsWith("v620_") ? "fk_" + value : value);
        }
        return mapped;
    }

    private static void putCommon(Map<String, String> fields) {
        fields.put(CyanCruiseDatamodelObjects.USER_ID, "v620_userid");
        fields.put(CyanCruiseDatamodelObjects.CREATED_AT, "v620_createdat");
        fields.put(CyanCruiseDatamodelObjects.UPDATED_AT, "v620_updatedat");
        fields.put("status", "v620_status");
        fields.put("summary", "v620_summary");
        fields.put("title", "v620_title");
        fields.put("version", "v620_version");
    }

    private static void putCareerProfile(Map<String, String> fields) {
        fields.put("target_role", "v620_targetrole");
        fields.put("current_stage", "v620_currentstage");
        fields.put("personalization_level", "v620_personalizationlevel");
        fields.put("completeness_score", "v620_completenessscore");
        fields.put("profile_json", "v620_profilejson");
        fields.put("readiness_json", "v620_readinessjson");
        fields.put("evidence_json", "v620_evidencejson");
        fields.put("agent_summary", "v620_agentsummary");
        fields.put("last_agent_run_id", "v620_lastagentrunid");
    }

    private static void putAssessment(Map<String, String> fields) {
        fields.put("record_id", "v620_recordid");
        fields.put("scale_id", "v620_scaleid");
        fields.put("scale_title", "v620_scaletitle");
        fields.put("answers_json", "v620_answersjson");
        fields.put("result_json", "v620_resultjson");
        fields.put("suggested_roles_json", "v620_suggestedrolesjson");
        fields.put("completed_at", "v620_completedat");
    }

    private static void putResume(Map<String, String> fields) {
        fields.put("resume_id", "v620_resumeid");
        fields.put("target_job", "v620_targetjob");
        fields.put("file_key", "v620_filekey");
        fields.put("diagnosis_id", "v620_diagnosisid");
        fields.put("diagnosis_score", "v620_diagnosisscore");
        fields.put("parsed_content", "v620_parsedcontent");
        fields.put("keyword_json", "v620_keywordjson");
        fields.put("keyword_status_json", "v620_keywordstatusjson");
        fields.put("diagnosis_json", "v620_diagnosisjson");
        fields.put("score", "v620_score");
    }

    private static void putTaskAndPlan(Map<String, String> fields) {
        fields.put("task_id", "v620_taskid");
        fields.put("task_key", "v620_taskkey");
        fields.put("description", "v620_description");
        fields.put("due_date", "v620_duedate");
        fields.put("priority", "v620_priority");
        fields.put("parent_task_id", "v620_parenttaskid");
        fields.put("sub_index", "v620_subindex");
        fields.put("plan_id", "v620_planid");
        fields.put("model_used", "v620_modelused");
        fields.put("tokens_consumed", "v620_tokensconsumed");
        fields.put("start_state_json", "v620_startstatejson");
        fields.put("milestones_json", "v620_milestonesjson");
        fields.put("weekly_focus_json", "v620_weeklyfocusjson");
        fields.put("generated_at", "v620_generatedat");
        fields.put("last_updated_at", "v620_lastupdatedat");
    }

    private static void putInterview(Map<String, String> fields) {
        fields.put("interview_id", "v620_interviewid");
        fields.put("position_name", "v620_positionname");
        fields.put("difficulty", "v620_difficulty");
        fields.put("mode", "v620_mode");
        fields.put("final_score", "v620_finalscore");
        fields.put("duration_seconds", "v620_durationseconds");
        fields.put("report_json", "v620_reportjson");
        fields.put("started_at", "v620_startedat");
        fields.put("ended_at", "v620_endedat");
    }

    private static void putAgent(Map<String, String> fields) {
        fields.put("run_id", "v620_runid");
        fields.put("agent_type", "v620_agenttype");
        fields.put("business_module", "v620_businessmodule");
        fields.put("task_type", "v620_tasktype");
        fields.put("model_name", "v620_modelname");
        fields.put("biz_object", "v620_bizobject");
        fields.put("biz_id", "v620_bizid");
        fields.put("input_summary", "v620_inputsummary");
        fields.put("output_summary", "v620_outputsummary");
        fields.put("input_json", "v620_inputjson");
        fields.put("output_json", "v620_outputjson");
        fields.put("error_message", "v620_errormessage");
        fields.put("prompt_tokens", "v620_prompttokens");
        fields.put("completion_tokens", "v620_completiontokens");
        fields.put("total_tokens", "v620_totaltokens");
        fields.put("agent_id", "v620_agentid");
        fields.put("agent_code", "v620_agentcode");
        fields.put("agent_name", "v620_agentname");
        fields.put("agent_role", "v620_agentrole");
        fields.put("thinking_flow", "v620_thinkingflow");
        fields.put("mcp_tools_json", "v620_mcptoolsjson");
        fields.put("input_schema_json", "v620_inputschemajson");
        fields.put("output_schema_json", "v620_outputschemajson");
        fields.put("enabled", "v620_enabled");
        fields.put("sort_order", "v620_sortorder");
    }

    private static void putFurtherStudy(Map<String, String> fields) {
        fields.put("target_id", "v620_targetid");
        fields.put("track", "v620_track");
        fields.put("target_school", "v620_targetschool");
        fields.put("target_major", "v620_targetmajor");
        fields.put("target_region", "v620_targetregion");
        fields.put("target_stage", "v620_targetstage");
        fields.put("target_json", "v620_targetjson");
        fields.put("record_type", "v620_recordtype");
        fields.put("exam_or_deadline_date", "v620_examordeadlinedate");
        fields.put("request_json", "v620_requestjson");
        fields.put("material_id", "v620_materialid");
        fields.put("material_type", "v620_materialtype");
        fields.put("content_json", "v620_contentjson");
        fields.put("event_id", "v620_eventid");
        fields.put("event_type", "v620_eventtype");
        fields.put("event_json", "v620_eventjson");
        fields.put("ref_id", "v620_refid");
        fields.put("source_object", "v620_sourceobject");
        fields.put("source_id", "v620_sourceid");
        fields.put("source_summary", "v620_sourcesummary");
        fields.put("write_back", "v620_writeback");
    }

    private static void putNoticeAndAdmin(Map<String, String> fields) {
        fields.put("notice_id", "v620_noticeid");
        fields.put("notice_type", "v620_noticetype");
        fields.put("link_route", "v620_linkroute");
        fields.put("admin_id", "v620_adminid");
        fields.put("read_at", "v620_readat");
        fields.put("username", "v620_username");
        fields.put("nickname", "v620_nickname");
        fields.put("org_id", "v620_orgid");
        fields.put("school", "v620_school");
        fields.put("major", "v620_major");
        fields.put("banned_reason", "v620_bannedreason");
        fields.put("last_active_at", "v620_lastactiveat");
        fields.put("question_id", "v620_questionid");
        fields.put("content", "v620_content");
        fields.put("position", "v620_position");
        fields.put("source", "v620_source");
        fields.put("review_status", "v620_reviewstatus");
        fields.put("answer_json", "v620_answerjson");
        fields.put("contributor_hash", "v620_contributorhash");
        fields.put("reviewed_at", "v620_reviewedat");
        fields.put("content_id", "v620_contentid");
        fields.put("content_type", "v620_contenttype");
        fields.put("link_url", "v620_linkurl");
        fields.put("cover_file_key", "v620_coverfilekey");
        fields.put("pinned", "v620_pinned");
        fields.put("hidden", "v620_hidden");
        fields.put("published_at", "v620_publishedat");
        fields.put("audit_id", "v620_auditid");
        fields.put("action", "v620_action");
        fields.put("target_type", "v620_targettype");
        fields.put("before_json", "v620_beforejson");
        fields.put("after_json", "v620_afterjson");
        fields.put("ip", "v620_ip");
        fields.put("user_agent", "v620_useragent");
    }

    private static void add(List<String> fields, String... logicalFields) {
        for (int i = 0; i < logicalFields.length; i++) {
            String platformField = toPlatformField(logicalFields[i]);
            if (!fields.contains(platformField)) {
                fields.add(platformField);
            }
        }
    }
}
