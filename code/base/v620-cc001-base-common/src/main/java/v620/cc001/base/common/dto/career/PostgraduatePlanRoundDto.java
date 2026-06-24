package v620.cc001.base.common.dto.career;

import java.util.ArrayList;
import java.util.List;

/** One revision round in postgraduate exam plan. */
public class PostgraduatePlanRoundDto {
    private String roundCode;
    private String roundName;
    private String dateRange;
    private String goal;
    private List<String> subjectFocus = new ArrayList<String>();
    private List<String> weeklyTasks = new ArrayList<String>();
    private List<String> checkPoints = new ArrayList<String>();
    private String stateAdvice;

    public String getRoundCode() { return roundCode; }
    public void setRoundCode(String roundCode) { this.roundCode = roundCode; }
    public String getRoundName() { return roundName; }
    public void setRoundName(String roundName) { this.roundName = roundName; }
    public String getDateRange() { return dateRange; }
    public void setDateRange(String dateRange) { this.dateRange = dateRange; }
    public String getGoal() { return goal; }
    public void setGoal(String goal) { this.goal = goal; }
    public List<String> getSubjectFocus() { return subjectFocus; }
    public void setSubjectFocus(List<String> subjectFocus) { this.subjectFocus = subjectFocus == null ? new ArrayList<String>() : subjectFocus; }
    public List<String> getWeeklyTasks() { return weeklyTasks; }
    public void setWeeklyTasks(List<String> weeklyTasks) { this.weeklyTasks = weeklyTasks == null ? new ArrayList<String>() : weeklyTasks; }
    public List<String> getCheckPoints() { return checkPoints; }
    public void setCheckPoints(List<String> checkPoints) { this.checkPoints = checkPoints == null ? new ArrayList<String>() : checkPoints; }
    public String getStateAdvice() { return stateAdvice; }
    public void setStateAdvice(String stateAdvice) { this.stateAdvice = stateAdvice; }
}
