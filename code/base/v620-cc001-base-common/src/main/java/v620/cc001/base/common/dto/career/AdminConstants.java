package v620.cc001.base.common.dto.career;

/**
 * Constants for CyanCruise admin governance.
 */
public final class AdminConstants {

    public static final String ROLE_ADMIN = "ADMIN";

    public static final String STATUS_OK = "OK";
    public static final String STATUS_IDENTITY_REQUIRED = "IDENTITY_REQUIRED";
    public static final String STATUS_FORBIDDEN = "FORBIDDEN";
    public static final String STATUS_FAILED = "FAILED";
    public static final String STATUS_SKIPPED = "SKIPPED";

    public static final String USER_STATUS_ACTIVE = "ACTIVE";
    public static final String USER_STATUS_BANNED = "BANNED";

    public static final String QUESTION_REVIEW_PENDING = "PENDING_REVIEW";
    public static final String QUESTION_REVIEW_PUBLISHED = "PUBLISHED";
    public static final String QUESTION_REVIEW_REJECTED = "REJECTED";
    public static final String QUESTION_STATUS_APPROVED = "APPROVED";
    public static final String QUESTION_STATUS_HIDDEN = "HIDDEN";

    public static final String CONTENT_TYPE_ARTICLE = "ARTICLE";
    public static final String CONTENT_TYPE_VIDEO = "VIDEO";
    public static final String CONTENT_TYPE_RESOURCE = "RESOURCE";

    public static final String ACTION_BAN_USER = "BAN_USER";
    public static final String ACTION_UNBAN_USER = "UNBAN_USER";
    public static final String ACTION_BROADCAST = "BROADCAST";
    public static final String ACTION_APPROVE_QUESTION = "APPROVE_QUESTION";
    public static final String ACTION_REJECT_QUESTION = "REJECT_QUESTION";
    public static final String ACTION_UPDATE_QUESTION = "UPDATE_QUESTION";
    public static final String ACTION_DELETE_QUESTION = "DELETE_QUESTION";
    public static final String ACTION_SAVE_CONTENT = "SAVE_CONTENT";
    public static final String ACTION_DELETE_CONTENT = "DELETE_CONTENT";
    public static final String ACTION_TOGGLE_CONTENT = "TOGGLE_CONTENT";
    public static final String ACTION_SAVE_SKILL_MAP = "SAVE_SKILL_MAP";

    private AdminConstants() {
    }
}
