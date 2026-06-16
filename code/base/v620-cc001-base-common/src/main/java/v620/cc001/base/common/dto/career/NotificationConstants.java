package v620.cc001.base.common.dto.career;

/**
 * Canonical CyanCruise notification types and grouping keys.
 */
public final class NotificationConstants {

    public static final String TYPE_SYSTEM = "SYSTEM";
    public static final String TYPE_WEEKLY_REPORT = "WEEKLY_REPORT";
    public static final String TYPE_INTERVIEW_REPORT = "INTERVIEW_REPORT";
    public static final String TYPE_ASSESSMENT_RESULT = "ASSESSMENT_RESULT";
    public static final String TYPE_RESUME_DIAGNOSIS = "RESUME_DIAGNOSIS";
    public static final String TYPE_STREAK_WARNING = "STREAK_WARNING";
    public static final String TYPE_MARKET_LIKE = "MARKET_LIKE";
    public static final String TYPE_AI_PROACTIVE = "AI_PROACTIVE";
    public static final String TYPE_ADMIN_BROADCAST = "ADMIN_BROADCAST";

    public static final String GROUP_CAREER = "CAREER";
    public static final String GROUP_SYSTEM = "SYSTEM";
    public static final String GROUP_AI = "AI";

    public static final String RESULT_OK = "OK";
    public static final String RESULT_SKIPPED = "SKIPPED";
    public static final String RESULT_FAILED = "FAILED";
    public static final String RESULT_UNAVAILABLE = "UNAVAILABLE";

    private NotificationConstants() {
    }
}
