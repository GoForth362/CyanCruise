package v620.cc001.base.common.dto.career;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User-facing summary for the long-term career plan.
 */
public class CareerPlanSummaryDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean hasPlan;
    private String targetRole;
    private String planHealth;
    private String adjustmentReason;
    private String nextMilestoneHorizon;
    private String nextMilestoneTitle;
    private List<String> weeklyFocus = new ArrayList<String>();
    private String generatedAt;
    private String lastUpdatedAt;
    private Integer version;

    public Boolean getHasPlan() {
        return hasPlan;
    }

    public void setHasPlan(Boolean hasPlan) {
        this.hasPlan = hasPlan;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public String getPlanHealth() {
        return planHealth;
    }

    public void setPlanHealth(String planHealth) {
        this.planHealth = planHealth;
    }

    public String getAdjustmentReason() {
        return adjustmentReason;
    }

    public void setAdjustmentReason(String adjustmentReason) {
        this.adjustmentReason = adjustmentReason;
    }

    public String getNextMilestoneHorizon() {
        return nextMilestoneHorizon;
    }

    public void setNextMilestoneHorizon(String nextMilestoneHorizon) {
        this.nextMilestoneHorizon = nextMilestoneHorizon;
    }

    public String getNextMilestoneTitle() {
        return nextMilestoneTitle;
    }

    public void setNextMilestoneTitle(String nextMilestoneTitle) {
        this.nextMilestoneTitle = nextMilestoneTitle;
    }

    public List<String> getWeeklyFocus() {
        return weeklyFocus;
    }

    public void setWeeklyFocus(List<String> weeklyFocus) {
        this.weeklyFocus = weeklyFocus;
    }

    public String getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(String generatedAt) {
        this.generatedAt = generatedAt;
    }

    public String getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(String lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
