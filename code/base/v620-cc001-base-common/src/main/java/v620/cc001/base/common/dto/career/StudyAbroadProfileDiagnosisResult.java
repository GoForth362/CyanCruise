package v620.cc001.base.common.dto.career;

import java.util.ArrayList;
import java.util.List;

/** Rule fallback result for study abroad profile diagnosis. */
public class StudyAbroadProfileDiagnosisResult {
    private String status;
    private Integer readinessScore;
    private String summary;
    private final List<String> strengths = new ArrayList<String>();
    private final List<String> gaps = new ArrayList<String>();
    private final List<StudyAbroadChecklistItemDto> nextActions = new ArrayList<StudyAbroadChecklistItemDto>();
    private final List<String> reminders = new ArrayList<String>();

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getReadinessScore() { return readinessScore; }
    public void setReadinessScore(Integer readinessScore) { this.readinessScore = readinessScore; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public List<String> getStrengths() { return strengths; }
    public List<String> getGaps() { return gaps; }
    public List<StudyAbroadChecklistItemDto> getNextActions() { return nextActions; }
    public List<String> getReminders() { return reminders; }
}
