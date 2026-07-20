package v620.cc001.base.common.dto.career;

/**
 * Constants for migrated resume diagnosis contracts.
 */
public final class ResumeDiagnosisConstants {

    public static final int MAX_TEXT_CHARS = 20000;
    public static final int MAX_KEYWORDS = 24;

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_PROCESSING = "PROCESSING";
    public static final String STATUS_READY = "READY";
    public static final String STATUS_EMPTY = "EMPTY";
    public static final String STATUS_FAILED = "FAILED";

    public static final String CATEGORY_GOAL = "GOAL";
    public static final String CATEGORY_SKILL = "SKILL";
    public static final String CATEGORY_BACKGROUND = "BACKGROUND";
    public static final String CATEGORY_GROWTH = "GROWTH";

    private ResumeDiagnosisConstants() {
    }
}
