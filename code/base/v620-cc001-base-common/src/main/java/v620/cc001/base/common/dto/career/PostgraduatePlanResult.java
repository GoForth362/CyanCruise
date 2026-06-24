package v620.cc001.base.common.dto.career;

import java.util.ArrayList;
import java.util.List;

/** Result of postgraduate exam revision plan generation. */
public class PostgraduatePlanResult {
    private String status;
    private String summary;
    private String target;
    private String examDate;
    private Integer daysRemaining;
    private List<PostgraduatePlanRoundDto> rounds = new ArrayList<PostgraduatePlanRoundDto>();
    private List<String> dailyHabits = new ArrayList<String>();

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }
    public String getExamDate() { return examDate; }
    public void setExamDate(String examDate) { this.examDate = examDate; }
    public Integer getDaysRemaining() { return daysRemaining; }
    public void setDaysRemaining(Integer daysRemaining) { this.daysRemaining = daysRemaining; }
    public List<PostgraduatePlanRoundDto> getRounds() { return rounds; }
    public void setRounds(List<PostgraduatePlanRoundDto> rounds) { this.rounds = rounds == null ? new ArrayList<PostgraduatePlanRoundDto>() : rounds; }
    public List<String> getDailyHabits() { return dailyHabits; }
    public void setDailyHabits(List<String> dailyHabits) { this.dailyHabits = dailyHabits == null ? new ArrayList<String>() : dailyHabits; }
}
