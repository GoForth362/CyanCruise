package v620.cc001.base.common.dto.career;

import java.util.ArrayList;
import java.util.List;

/** Language exam plan result. */
public class StudyAbroadLanguagePlanResult {
    private String status;
    private String summary;
    private final List<StudyAbroadChecklistItemDto> rounds = new ArrayList<StudyAbroadChecklistItemDto>();
    private final List<String> weeklyRoutine = new ArrayList<String>();
    private final List<String> examinerTips = new ArrayList<String>();

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public List<StudyAbroadChecklistItemDto> getRounds() { return rounds; }
    public List<String> getWeeklyRoutine() { return weeklyRoutine; }
    public List<String> getExaminerTips() { return examinerTips; }
}
