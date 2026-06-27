package v620.cc001.base.common.dto.furtherstudy;

/**
 * Shared constants for long-running further-study companion records.
 */
public final class FurtherStudyConstants {

    public static final String TRACK_POSTGRADUATE_EXAM = "postgraduate_exam";
    public static final String TRACK_POSTGRADUATE_RECOMMENDATION = "postgraduate_recommendation";
    public static final String TRACK_STUDY_ABROAD = "study_abroad";

    public static final String RECORD_SCHOOL_RECOMMENDATION = "school_recommendation";
    public static final String RECORD_REVIEW_PLAN = "review_plan";
    public static final String RECORD_MISTAKE_ANALYSIS = "mistake_analysis";
    public static final String RECORD_REEXAM_PREPARATION = "reexam_preparation";
    public static final String RECORD_COMPETITIVENESS_DIAGNOSIS = "competitiveness_diagnosis";
    public static final String RECORD_ACTION_PLAN = "action_plan";
    public static final String RECORD_DOCUMENT_POLISH = "document_polish";
    public static final String RECORD_TUTOR_LETTER = "tutor_letter";
    public static final String RECORD_PROFILE_DIAGNOSIS = "profile_diagnosis";
    public static final String RECORD_LANGUAGE_PLAN = "language_plan";
    public static final String RECORD_SCHOOL_POSITION = "school_position";
    public static final String RECORD_STATEMENT_OUTLINE = "statement_outline";
    public static final String RECORD_VISA_CHECKLIST = "visa_checklist";

    public static final String STATUS_DRAFT = "draft";
    public static final String STATUS_IN_PROGRESS = "in_progress";
    public static final String STATUS_DONE = "done";
    public static final String STATUS_ARCHIVED = "archived";
    public static final String STATUS_PENDING_REVIEW = "pending_review";
    public static final String STATUS_TO_REVIEW = "to_review";
    public static final String STATUS_TO_SEND = "to_send";
    public static final String STATUS_SENT = "sent";
    public static final String STATUS_REPLIED = "replied";

    public static final String MATERIAL_RECOMMENDATION_STATEMENT = "recommendation_statement";
    public static final String MATERIAL_TUTOR_CONTACT = "tutor_contact";
    public static final String MATERIAL_STUDY_ABROAD_STATEMENT = "study_abroad_statement";
    public static final String MATERIAL_RECOMMENDATION_LETTER = "recommendation_letter";
    public static final String MATERIAL_VISA = "visa";
    public static final String MATERIAL_REEXAM = "reexam";

    public static final String EVENT_RECORD_CREATED = "record_created";
    public static final String EVENT_STATUS_UPDATED = "status_updated";
    public static final String EVENT_MATERIAL_SAVED = "material_saved";
    public static final String EVENT_TARGET_SAVED = "target_saved";

    private FurtherStudyConstants() {
    }

    public static String trackLabel(String track) {
        if (TRACK_POSTGRADUATE_EXAM.equals(track)) {
            return "考研";
        }
        if (TRACK_POSTGRADUATE_RECOMMENDATION.equals(track)) {
            return "保研";
        }
        if (TRACK_STUDY_ABROAD.equals(track)) {
            return "留学";
        }
        return "深造";
    }

    public static String statusLabel(String status) {
        if (STATUS_DRAFT.equals(status)) {
            return "草稿";
        }
        if (STATUS_IN_PROGRESS.equals(status)) {
            return "进行中";
        }
        if (STATUS_DONE.equals(status)) {
            return "已完成";
        }
        if (STATUS_ARCHIVED.equals(status)) {
            return "已归档";
        }
        if (STATUS_PENDING_REVIEW.equals(status)) {
            return "待确认";
        }
        if (STATUS_TO_REVIEW.equals(status)) {
            return "待复习";
        }
        if (STATUS_TO_SEND.equals(status)) {
            return "待发送";
        }
        if (STATUS_SENT.equals(status)) {
            return "已发送";
        }
        if (STATUS_REPLIED.equals(status)) {
            return "已回复";
        }
        return "未设置";
    }
}
