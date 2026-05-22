package v620.cc001.base.common.dto.career;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Unified career profile derived from snapshot blocks and user-supplied facts.
 */
public class CareerUserProfileDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String personalizationLevel;
    private Integer completenessScore;
    private String currentStage;
    private TargetRole target;
    private Readiness readiness;
    private Behavior behavior;
    private List<MissingSignal> missingSignals = new ArrayList<MissingSignal>();
    private Map<String, String> evidence = new LinkedHashMap<String, String>();

    public String getPersonalizationLevel() {
        return personalizationLevel;
    }

    public void setPersonalizationLevel(String personalizationLevel) {
        this.personalizationLevel = personalizationLevel;
    }

    public Integer getCompletenessScore() {
        return completenessScore;
    }

    public void setCompletenessScore(Integer completenessScore) {
        this.completenessScore = completenessScore;
    }

    public String getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(String currentStage) {
        this.currentStage = currentStage;
    }

    public TargetRole getTarget() {
        return target;
    }

    public void setTarget(TargetRole target) {
        this.target = target;
    }

    public Readiness getReadiness() {
        return readiness;
    }

    public void setReadiness(Readiness readiness) {
        this.readiness = readiness;
    }

    public Behavior getBehavior() {
        return behavior;
    }

    public void setBehavior(Behavior behavior) {
        this.behavior = behavior;
    }

    public List<MissingSignal> getMissingSignals() {
        return missingSignals;
    }

    public void setMissingSignals(List<MissingSignal> missingSignals) {
        this.missingSignals = missingSignals;
    }

    public Map<String, String> getEvidence() {
        return evidence;
    }

    public void setEvidence(Map<String, String> evidence) {
        this.evidence = evidence;
    }

    public static class TargetRole implements Serializable {
        private static final long serialVersionUID = 1L;
        private String role;
        private String source;
        private BigDecimal confidence;

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public BigDecimal getConfidence() {
            return confidence;
        }

        public void setConfidence(BigDecimal confidence) {
            this.confidence = confidence;
        }
    }

    public static class Readiness implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer overallPercent;
        private Integer directionClarityPercent;
        private Integer resumeReadinessPercent;
        private Integer interviewReadinessPercent;
        private Integer actionContinuityPercent;
        private Integer resumeScore;
        private Integer interviewScore;
        private Boolean hasAssessment;
        private Boolean hasResume;
        private Boolean hasInterview;
        private Boolean hasPlan;

        public Integer getOverallPercent() {
            return overallPercent;
        }

        public void setOverallPercent(Integer overallPercent) {
            this.overallPercent = overallPercent;
        }

        public Integer getDirectionClarityPercent() {
            return directionClarityPercent;
        }

        public void setDirectionClarityPercent(Integer directionClarityPercent) {
            this.directionClarityPercent = directionClarityPercent;
        }

        public Integer getResumeReadinessPercent() {
            return resumeReadinessPercent;
        }

        public void setResumeReadinessPercent(Integer resumeReadinessPercent) {
            this.resumeReadinessPercent = resumeReadinessPercent;
        }

        public Integer getInterviewReadinessPercent() {
            return interviewReadinessPercent;
        }

        public void setInterviewReadinessPercent(Integer interviewReadinessPercent) {
            this.interviewReadinessPercent = interviewReadinessPercent;
        }

        public Integer getActionContinuityPercent() {
            return actionContinuityPercent;
        }

        public void setActionContinuityPercent(Integer actionContinuityPercent) {
            this.actionContinuityPercent = actionContinuityPercent;
        }

        public Integer getResumeScore() {
            return resumeScore;
        }

        public void setResumeScore(Integer resumeScore) {
            this.resumeScore = resumeScore;
        }

        public Integer getInterviewScore() {
            return interviewScore;
        }

        public void setInterviewScore(Integer interviewScore) {
            this.interviewScore = interviewScore;
        }

        public Boolean getHasAssessment() {
            return hasAssessment;
        }

        public void setHasAssessment(Boolean hasAssessment) {
            this.hasAssessment = hasAssessment;
        }

        public Boolean getHasResume() {
            return hasResume;
        }

        public void setHasResume(Boolean hasResume) {
            this.hasResume = hasResume;
        }

        public Boolean getHasInterview() {
            return hasInterview;
        }

        public void setHasInterview(Boolean hasInterview) {
            this.hasInterview = hasInterview;
        }

        public Boolean getHasPlan() {
            return hasPlan;
        }

        public void setHasPlan(Boolean hasPlan) {
            this.hasPlan = hasPlan;
        }
    }

    public static class Behavior implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer streakDays;
        private Integer weeklyDays;
        private Integer todayCompleted;
        private Integer todayTotal;
        private String preferredDifficulty;

        public Integer getStreakDays() {
            return streakDays;
        }

        public void setStreakDays(Integer streakDays) {
            this.streakDays = streakDays;
        }

        public Integer getWeeklyDays() {
            return weeklyDays;
        }

        public void setWeeklyDays(Integer weeklyDays) {
            this.weeklyDays = weeklyDays;
        }

        public Integer getTodayCompleted() {
            return todayCompleted;
        }

        public void setTodayCompleted(Integer todayCompleted) {
            this.todayCompleted = todayCompleted;
        }

        public Integer getTodayTotal() {
            return todayTotal;
        }

        public void setTodayTotal(Integer todayTotal) {
            this.todayTotal = todayTotal;
        }

        public String getPreferredDifficulty() {
            return preferredDifficulty;
        }

        public void setPreferredDifficulty(String preferredDifficulty) {
            this.preferredDifficulty = preferredDifficulty;
        }
    }

    public static class MissingSignal implements Serializable {
        private static final long serialVersionUID = 1L;
        private String key;
        private String label;
        private String priority;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getPriority() {
            return priority;
        }

        public void setPriority(String priority) {
            this.priority = priority;
        }
    }
}
