package v620.cc001.base.common.dto.career;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Cross-tool user portrait copied from IPD without Spring, Jackson, JPA, or Lombok coupling.
 */
public class UserProfileSnapshot implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer version = 1;
    private LocalDateTime updatedAt;
    private AssessmentBlock assessment;
    private ResumeBlock resume;
    private InterviewBlock interview;
    private PreferencesBlock preferences;
    private OnboardingBlock onboarding;

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public AssessmentBlock getAssessment() {
        return assessment;
    }

    public void setAssessment(AssessmentBlock assessment) {
        this.assessment = assessment;
    }

    public ResumeBlock getResume() {
        return resume;
    }

    public void setResume(ResumeBlock resume) {
        this.resume = resume;
    }

    public InterviewBlock getInterview() {
        return interview;
    }

    public void setInterview(InterviewBlock interview) {
        this.interview = interview;
    }

    public PreferencesBlock getPreferences() {
        return preferences;
    }

    public void setPreferences(PreferencesBlock preferences) {
        this.preferences = preferences;
    }

    public OnboardingBlock getOnboarding() {
        return onboarding;
    }

    public void setOnboarding(OnboardingBlock onboarding) {
        this.onboarding = onboarding;
    }

    public static class AssessmentBlock implements Serializable {
        private static final long serialVersionUID = 1L;
        private Long lastRecordId;
        private Long scaleId;
        private String scaleTitle;
        private String summary;
        private List<String> suggestedRoles;
        private LocalDateTime completedAt;

        public Long getLastRecordId() {
            return lastRecordId;
        }

        public void setLastRecordId(Long lastRecordId) {
            this.lastRecordId = lastRecordId;
        }

        public Long getScaleId() {
            return scaleId;
        }

        public void setScaleId(Long scaleId) {
            this.scaleId = scaleId;
        }

        public String getScaleTitle() {
            return scaleTitle;
        }

        public void setScaleTitle(String scaleTitle) {
            this.scaleTitle = scaleTitle;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public List<String> getSuggestedRoles() {
            return suggestedRoles;
        }

        public void setSuggestedRoles(List<String> suggestedRoles) {
            this.suggestedRoles = suggestedRoles;
        }

        public LocalDateTime getCompletedAt() {
            return completedAt;
        }

        public void setCompletedAt(LocalDateTime completedAt) {
            this.completedAt = completedAt;
        }
    }

    public static class ResumeBlock implements Serializable {
        private static final long serialVersionUID = 1L;
        private Long lastResumeId;
        private String lastResumeKey;
        private String title;
        private String targetJob;
        private Integer diagnosisScore;
        private LocalDateTime updatedAt;

        public Long getLastResumeId() {
            return lastResumeId;
        }

        public void setLastResumeId(Long lastResumeId) {
            this.lastResumeId = lastResumeId;
        }

        public String getLastResumeKey() {
            return lastResumeKey;
        }

        public void setLastResumeKey(String lastResumeKey) {
            this.lastResumeKey = lastResumeKey;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTargetJob() {
            return targetJob;
        }

        public void setTargetJob(String targetJob) {
            this.targetJob = targetJob;
        }

        public Integer getDiagnosisScore() {
            return diagnosisScore;
        }

        public void setDiagnosisScore(Integer diagnosisScore) {
            this.diagnosisScore = diagnosisScore;
        }

        public LocalDateTime getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
        }
    }

    public static class InterviewBlock implements Serializable {
        private static final long serialVersionUID = 1L;
        private Long lastInterviewId;
        private String positionName;
        private String difficulty;
        private Integer lastScore;
        private List<String> weakDimensions;
        private List<String> strongDimensions;
        private LocalDateTime completedAt;

        public Long getLastInterviewId() {
            return lastInterviewId;
        }

        public void setLastInterviewId(Long lastInterviewId) {
            this.lastInterviewId = lastInterviewId;
        }

        public String getPositionName() {
            return positionName;
        }

        public void setPositionName(String positionName) {
            this.positionName = positionName;
        }

        public String getDifficulty() {
            return difficulty;
        }

        public void setDifficulty(String difficulty) {
            this.difficulty = difficulty;
        }

        public Integer getLastScore() {
            return lastScore;
        }

        public void setLastScore(Integer lastScore) {
            this.lastScore = lastScore;
        }

        public List<String> getWeakDimensions() {
            return weakDimensions;
        }

        public void setWeakDimensions(List<String> weakDimensions) {
            this.weakDimensions = weakDimensions;
        }

        public List<String> getStrongDimensions() {
            return strongDimensions;
        }

        public void setStrongDimensions(List<String> strongDimensions) {
            this.strongDimensions = strongDimensions;
        }

        public LocalDateTime getCompletedAt() {
            return completedAt;
        }

        public void setCompletedAt(LocalDateTime completedAt) {
            this.completedAt = completedAt;
        }
    }

    public static class PreferencesBlock implements Serializable {
        private static final long serialVersionUID = 1L;
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

    public static class OnboardingBlock implements Serializable {
        private static final long serialVersionUID = 1L;
        private String identityType;
        private String stage;
        private String painPoint;
        private String hasResume;
        private String resumeStatus;
        private String experience;
        private String timeline;
        private EducationBlock education;
        private String weeklyAvailability;
        private String priorityHelp;
        private String recommendedEntry;
        private String onboardingCompletedAt;

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

        public String getExperience() {
            return experience;
        }

        public void setExperience(String experience) {
            this.experience = experience;
        }

        public String getTimeline() {
            return timeline;
        }

        public void setTimeline(String timeline) {
            this.timeline = timeline;
        }

        public EducationBlock getEducation() {
            return education;
        }

        public void setEducation(EducationBlock education) {
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
    }

    public static class EducationBlock implements Serializable {
        private static final long serialVersionUID = 1L;
        private String school;
        private String major;
        private String degree;
        private String graduationYear;

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

        public String getDegree() {
            return degree;
        }

        public void setDegree(String degree) {
            this.degree = degree;
        }

        public String getGraduationYear() {
            return graduationYear;
        }

        public void setGraduationYear(String graduationYear) {
            this.graduationYear = graduationYear;
        }
    }
}
