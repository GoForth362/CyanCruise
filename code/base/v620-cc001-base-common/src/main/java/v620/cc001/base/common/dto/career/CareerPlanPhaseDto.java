package v620.cc001.base.common.dto.career;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Long-term phase in an employment route plan.
 */
public class CareerPlanPhaseDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String phaseId;
    private String horizon;
    private String title;
    private String goal;
    private String description;
    private String status;
    private List<String> skills = new ArrayList<String>();
    private List<String> actions = new ArrayList<String>();
    private List<String> kpis = new ArrayList<String>();
    private List<CareerPlanSubStageDto> subStages = new ArrayList<CareerPlanSubStageDto>();

    public String getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(String phaseId) {
        this.phaseId = phaseId;
    }

    public String getHorizon() {
        return horizon;
    }

    public void setHorizon(String horizon) {
        this.horizon = horizon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public List<String> getActions() {
        return actions;
    }

    public void setActions(List<String> actions) {
        this.actions = actions;
    }

    public List<String> getKpis() {
        return kpis;
    }

    public void setKpis(List<String> kpis) {
        this.kpis = kpis;
    }

    public List<CareerPlanSubStageDto> getSubStages() {
        return subStages;
    }

    public void setSubStages(List<CareerPlanSubStageDto> subStages) {
        this.subStages = subStages;
    }
}
