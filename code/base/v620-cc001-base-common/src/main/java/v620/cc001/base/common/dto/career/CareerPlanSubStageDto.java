package v620.cc001.base.common.dto.career;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Weekly or short-cycle target under a long-term career plan phase.
 */
public class CareerPlanSubStageDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String period;
    private String title;
    private String goal;
    private List<String> actions = new ArrayList<String>();
    private List<String> deliverables = new ArrayList<String>();
    private List<String> dailySuggestions = new ArrayList<String>();

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
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

    public List<String> getActions() {
        return actions;
    }

    public void setActions(List<String> actions) {
        this.actions = actions;
    }

    public List<String> getDeliverables() {
        return deliverables;
    }

    public void setDeliverables(List<String> deliverables) {
        this.deliverables = deliverables;
    }

    public List<String> getDailySuggestions() {
        return dailySuggestions;
    }

    public void setDailySuggestions(List<String> dailySuggestions) {
        this.dailySuggestions = dailySuggestions;
    }
}
