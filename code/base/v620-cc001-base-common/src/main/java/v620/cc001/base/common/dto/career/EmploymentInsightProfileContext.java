package v620.cc001.base.common.dto.career;

import java.io.Serializable;

/**
 * User-owned context used to match public employment insight records.
 */
public class EmploymentInsightProfileContext implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;
    private String school;
    private String major;
    private String targetRole;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }
}
