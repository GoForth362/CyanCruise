package v620.cc001.base.common.dto.career;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Current-week execution slice derived from the long-term route.
 */
public class CareerPlanWeeklyPlanDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String weekTitle;
    private String weekGoal;
    private List<String> actions = new ArrayList<String>();
    private List<String> deliverables = new ArrayList<String>();
    private List<String> dailySuggestions = new ArrayList<String>();

    public String getWeekTitle() {
        return weekTitle;
    }

    public void setWeekTitle(String weekTitle) {
        this.weekTitle = weekTitle;
    }

    public String getWeekGoal() {
        return weekGoal;
    }

    public void setWeekGoal(String weekGoal) {
        this.weekGoal = weekGoal;
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
