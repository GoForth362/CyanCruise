package v620.cc001.base.common.dto.career;

import java.util.ArrayList;
import java.util.List;

/**
 * Request for saving or updating the current career plan.
 */
public class CareerPlanSaveRequest {

    private String targetRole;
    private String startStateSummary;
    private List<CareerPlanMilestoneDto> milestones = new ArrayList<CareerPlanMilestoneDto>();
    private List<String> weeklyFocus = new ArrayList<String>();
    private String modelUsed;

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
}
