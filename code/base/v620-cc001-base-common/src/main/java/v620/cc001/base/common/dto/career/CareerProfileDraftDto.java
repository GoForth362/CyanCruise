package v620.cc001.base.common.dto.career;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Partial route-entry profile data saved before completed onboarding.
 */
public class CareerProfileDraftDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String identityType;
    private String educationStage;
    private String schoolMajor;
    private String resumeStatus;
    private String targetRole;
    private String preference;
    private String experience;
    private String routeIntent;
    private LocalDateTime updatedAt;

    public String getIdentityType() {
        return identityType;
    }

    public void setIdentityType(String identityType) {
        this.identityType = identityType;
    }

    public String getEducationStage() {
        return educationStage;
    }

    public void setEducationStage(String educationStage) {
        this.educationStage = educationStage;
    }

    public String getSchoolMajor() {
        return schoolMajor;
    }

    public void setSchoolMajor(String schoolMajor) {
        this.schoolMajor = schoolMajor;
    }

    public String getResumeStatus() {
        return resumeStatus;
    }

    public void setResumeStatus(String resumeStatus) {
        this.resumeStatus = resumeStatus;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public String getPreference() {
        return preference;
    }

    public void setPreference(String preference) {
        this.preference = preference;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getRouteIntent() {
        return routeIntent;
    }

    public void setRouteIntent(String routeIntent) {
        this.routeIntent = routeIntent;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
