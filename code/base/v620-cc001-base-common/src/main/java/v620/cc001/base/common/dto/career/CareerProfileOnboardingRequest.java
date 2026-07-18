package v620.cc001.base.common.dto.career;

/**
 * Partial update request for first-run career onboarding.
 */
public class CareerProfileOnboardingRequest {

    private String identityType;
    private String stage;
    private String painPoint;
    private String hasResume;
    private String resumeStatus;
    private Long selectedResumeId;
    private String experience;
    private String selfProfileSupplement;
    private String timeline;
    private UserProfileSnapshot.EducationBlock education;
    private String weeklyAvailability;
    private String priorityHelp;
    private String recommendedEntry;
    private String onboardingCompletedAt;
    private String targetRole;
    private String targetSchool;
    private String routeGoal;

    public String getIdentityType() {
        return identityType;
    }

    public void setIdentityType(String identityType) {
        this.identityType = identityType;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getPainPoint() {
        return painPoint;
    }

    public void setPainPoint(String painPoint) {
        this.painPoint = painPoint;
    }

    public String getHasResume() {
        return hasResume;
    }

    public void setHasResume(String hasResume) {
        this.hasResume = hasResume;
    }

    public String getResumeStatus() {
        return resumeStatus;
    }

    public void setResumeStatus(String resumeStatus) {
        this.resumeStatus = resumeStatus;
    }

    public Long getSelectedResumeId() {
        return selectedResumeId;
    }

    public void setSelectedResumeId(Long selectedResumeId) {
        this.selectedResumeId = selectedResumeId;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getSelfProfileSupplement() {
        return selfProfileSupplement;
    }

    public void setSelfProfileSupplement(String selfProfileSupplement) {
        this.selfProfileSupplement = selfProfileSupplement;
    }

    public String getTimeline() {
        return timeline;
    }

    public void setTimeline(String timeline) {
        this.timeline = timeline;
    }

    public UserProfileSnapshot.EducationBlock getEducation() {
        return education;
    }

    public void setEducation(UserProfileSnapshot.EducationBlock education) {
        this.education = education;
    }

    public String getWeeklyAvailability() {
        return weeklyAvailability;
    }

    public void setWeeklyAvailability(String weeklyAvailability) {
        this.weeklyAvailability = weeklyAvailability;
    }

    public String getPriorityHelp() {
        return priorityHelp;
    }

    public void setPriorityHelp(String priorityHelp) {
        this.priorityHelp = priorityHelp;
    }

    public String getRecommendedEntry() {
        return recommendedEntry;
    }

    public void setRecommendedEntry(String recommendedEntry) {
        this.recommendedEntry = recommendedEntry;
    }

    public String getOnboardingCompletedAt() {
        return onboardingCompletedAt;
    }

    public void setOnboardingCompletedAt(String onboardingCompletedAt) {
        this.onboardingCompletedAt = onboardingCompletedAt;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public String getTargetSchool() {
        return targetSchool;
    }

    public void setTargetSchool(String targetSchool) {
        this.targetSchool = targetSchool;
    }

    public String getRouteGoal() { return routeGoal; }
    public void setRouteGoal(String routeGoal) { this.routeGoal = routeGoal; }
}
