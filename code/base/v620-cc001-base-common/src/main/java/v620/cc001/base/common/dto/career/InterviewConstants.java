package v620.cc001.base.common.dto.career;

/**
 * Stable values for the migrated interview core contract.
 */
public final class InterviewConstants {

    public static final int MAX_AI_INTERVIEW_QUESTIONS = 7;
    public static final int INTERVIEW_HISTORY_PAGE_SIZE = 10;

    public static final String STATUS_ONGOING = "ONGOING";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_CANCELLED = "CANCELLED";

    public static final String MODE_TEXT = "TEXT";
    public static final String MODE_VOICE = "VOICE";

    public static final String ROLE_USER = "USER";
    public static final String ROLE_AI = "AI";

    public static final String DIFFICULTY_EASY = "Easy";
    public static final String DIFFICULTY_NORMAL = "Normal";
    public static final String DIFFICULTY_HARD = "Hard";

    private InterviewConstants() {
    }
}
