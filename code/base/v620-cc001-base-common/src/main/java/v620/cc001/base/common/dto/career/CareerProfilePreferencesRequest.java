package v620.cc001.base.common.dto.career;

/**
 * Partial update request for career profile preferences.
 */
public class CareerProfilePreferencesRequest {

    private String targetRole;
    private String interviewMode;

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public String getInterviewMode() {
        return interviewMode;
    }

    public void setInterviewMode(String interviewMode) {
        this.interviewMode = interviewMode;
    }
}
