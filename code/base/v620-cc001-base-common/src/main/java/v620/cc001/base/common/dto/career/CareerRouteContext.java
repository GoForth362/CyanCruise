package v620.cc001.base.common.dto.career;

/** Shared route and study-direction values used by persisted plans. */
public final class CareerRouteContext {
    public static final String EMPLOYMENT = "EMPLOYMENT";
    public static final String STUDY = "STUDY";
    public static final String GOAL_EMPLOYMENT = "employment";
    public static final String GOAL_STUDY = "study";
    public static final String POSTGRADUATE = "POSTGRADUATE";
    public static final String RECOMMENDATION = "RECOMMENDATION";
    public static final String STUDY_ABROAD = "STUDY_ABROAD";

    private CareerRouteContext() { }

    public static String normalizeGoal(String value) {
        return GOAL_STUDY.equalsIgnoreCase(trim(value)) ? GOAL_STUDY : GOAL_EMPLOYMENT;
    }

    public static boolean isStudyDirection(String value) {
        String safe = trim(value);
        return POSTGRADUATE.equals(safe) || RECOMMENDATION.equals(safe) || STUDY_ABROAD.equals(safe);
    }

    private static String trim(String value) { return value == null ? "" : value.trim(); }
}
