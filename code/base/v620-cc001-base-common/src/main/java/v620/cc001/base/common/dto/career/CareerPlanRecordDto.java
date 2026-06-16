package v620.cc001.base.common.dto.career;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Dependency-free career plan record migrated from IPD UserCareerPlan semantics.
 */
public class CareerPlanRecordDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;
    private String targetRole;
    private String startStateSummary;
    private String planningMode;
    private Integer horizonYears;
    private String agentStatus;
    private String agentTraceId;
    private List<CareerPlanPhaseDto> phases = new ArrayList<CareerPlanPhaseDto>();
    private CareerPlanWeeklyPlanDto weeklyPlan;
    private List<String> dailySuggestions = new ArrayList<String>();
    private List<CareerPlanMilestoneDto> milestones = new ArrayList<CareerPlanMilestoneDto>();
    private List<String> weeklyFocus = new ArrayList<String>();
    private String modelUsed;
    private Integer tokensConsumed = Integer.valueOf(0);
    private LocalDateTime generatedAt;
    private LocalDateTime lastUpdatedAt;
    private Integer version = Integer.valueOf(1);

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public String getStartStateSummary() {
        return startStateSummary;
    }

    public void setStartStateSummary(String startStateSummary) {
        this.startStateSummary = startStateSummary;
    }

    public String getPlanningMode() {
        return planningMode;
    }

    public void setPlanningMode(String planningMode) {
        this.planningMode = planningMode;
    }

    public Integer getHorizonYears() {
        return horizonYears;
    }

    public void setHorizonYears(Integer horizonYears) {
        this.horizonYears = horizonYears;
    }

    public String getAgentStatus() {
        return agentStatus;
    }

    public void setAgentStatus(String agentStatus) {
        this.agentStatus = agentStatus;
    }

    public String getAgentTraceId() {
        return agentTraceId;
    }

    public void setAgentTraceId(String agentTraceId) {
        this.agentTraceId = agentTraceId;
    }

    public List<CareerPlanPhaseDto> getPhases() {
        return phases;
    }

    public void setPhases(List<CareerPlanPhaseDto> phases) {
        this.phases = phases;
    }

    public CareerPlanWeeklyPlanDto getWeeklyPlan() {
        return weeklyPlan;
    }

    public void setWeeklyPlan(CareerPlanWeeklyPlanDto weeklyPlan) {
        this.weeklyPlan = weeklyPlan;
    }

    public List<String> getDailySuggestions() {
        return dailySuggestions;
    }

    public void setDailySuggestions(List<String> dailySuggestions) {
        this.dailySuggestions = dailySuggestions;
    }

    public List<CareerPlanMilestoneDto> getMilestones() {
        return milestones;
    }

    public void setMilestones(List<CareerPlanMilestoneDto> milestones) {
        this.milestones = milestones;
    }

    public List<String> getWeeklyFocus() {
        return weeklyFocus;
    }

    public void setWeeklyFocus(List<String> weeklyFocus) {
        this.weeklyFocus = weeklyFocus;
    }

    public String getModelUsed() {
        return modelUsed;
    }

    public void setModelUsed(String modelUsed) {
        this.modelUsed = modelUsed;
    }

    public Integer getTokensConsumed() {
        return tokensConsumed;
    }

    public void setTokensConsumed(Integer tokensConsumed) {
        this.tokensConsumed = tokensConsumed;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public LocalDateTime getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(LocalDateTime lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
