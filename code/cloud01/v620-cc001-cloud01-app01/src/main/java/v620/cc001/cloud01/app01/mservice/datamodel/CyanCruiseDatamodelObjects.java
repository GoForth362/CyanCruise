package v620.cc001.cloud01.app01.mservice.datamodel;

/**
 * Logical object and field names used by CyanCruise application code.
 *
 * <p>The Kingdee business object codes are defined in CyanCruiseBusinessModelMapping.
 * Keep these logical names stable so existing storage and WebAPI behavior does not
 * change when the platform object names are adjusted.</p>
 */
public final class CyanCruiseDatamodelObjects {

    public static final String PROFILE_SNAPSHOT = "cc_cl_profile_snapshot";
    public static final String PROFILE_DRAFT = "cc_cl_profile_draft";
    public static final String PROFILE_FACT = "cc_cl_profile_fact";
    public static final String USER_PROFILE = "cc_cl_user_profile";
    public static final String ASSESSMENT_RECORD = "cc_cl_assessment_record";
    public static final String RESUME = "cc_cl_resume";
    public static final String TASK = "cc_cl_task";
    public static final String CAREER_PLAN = "cc_cl_career_plan";
    public static final String INTERVIEW = "cc_cl_interview";
    public static final String INTERVIEW_MESSAGE = "cc_cl_interview_message";
    public static final String RESUME_DIAGNOSIS = "cc_cl_resume_diagnosis";
    public static final String ASSISTANT_SESSION = "cc_cl_assistant_session";
    public static final String ASSISTANT_MESSAGE = "cc_cl_assistant_message";
    public static final String AGENT_RUN = "cc_agent_run";
    public static final String AGENT_CAPABILITY = "cc_agent_capability";
    public static final String STUDY_TARGET = "cc_study_target";
    public static final String STUDY_RECORD = "cc_study_record";
    public static final String STUDY_MATERIAL = "cc_study_material";
    public static final String STUDY_EVENT = "cc_study_event";
    public static final String AGENT_CONTEXT_REF = "cc_agent_context_ref";
    public static final String NOTICE = "cc_notice";
    public static final String USER_ACCOUNT = "cc_user_account";
    public static final String QUESTION = "cc_question";
    public static final String CONTENT = "cc_content";
    public static final String ADMIN_AUDIT = "cc_admin_audit";

    public static final String ID = "id";
    public static final String USER_ID = "user_id";
    public static final String UPDATED_AT = "updated_at";
    public static final String CREATED_AT = "created_at";

    private CyanCruiseDatamodelObjects() {
    }
}
