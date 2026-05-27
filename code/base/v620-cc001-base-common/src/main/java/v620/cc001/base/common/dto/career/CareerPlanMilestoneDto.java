package v620.cc001.base.common.dto.career;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * One long-term career plan milestone.
 */
public class CareerPlanMilestoneDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String horizon;
    private String title;
    private List<String> skills = new ArrayList<String>();
    private List<String> actions = new ArrayList<String>();
    private List<String> kpis = new ArrayList<String>();

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
}
