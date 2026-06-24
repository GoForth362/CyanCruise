package v620.cc001.base.common.dto.career;

import java.util.ArrayList;
import java.util.List;

/** Diagnosis result for postgraduate recommendation background. */
public class RecommendationDiagnosisResult {
    private String status;
    private Integer overallScore;
    private String summary;
    private List<RecommendationScoreItemDto> scoreItems = new ArrayList<RecommendationScoreItemDto>();
    private List<String> strengths = new ArrayList<String>();
    private List<String> weaknesses = new ArrayList<String>();
    private List<RecommendationActionItemDto> actions = new ArrayList<RecommendationActionItemDto>();
    private List<String> reminders = new ArrayList<String>();

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getOverallScore() { return overallScore; }
    public void setOverallScore(Integer overallScore) { this.overallScore = overallScore; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public List<RecommendationScoreItemDto> getScoreItems() { return scoreItems; }
    public void setScoreItems(List<RecommendationScoreItemDto> scoreItems) { this.scoreItems = scoreItems == null ? new ArrayList<RecommendationScoreItemDto>() : scoreItems; }
    public List<String> getStrengths() { return strengths; }
    public void setStrengths(List<String> strengths) { this.strengths = strengths == null ? new ArrayList<String>() : strengths; }
    public List<String> getWeaknesses() { return weaknesses; }
    public void setWeaknesses(List<String> weaknesses) { this.weaknesses = weaknesses == null ? new ArrayList<String>() : weaknesses; }
    public List<RecommendationActionItemDto> getActions() { return actions; }
    public void setActions(List<RecommendationActionItemDto> actions) { this.actions = actions == null ? new ArrayList<RecommendationActionItemDto>() : actions; }
    public List<String> getReminders() { return reminders; }
    public void setReminders(List<String> reminders) { this.reminders = reminders == null ? new ArrayList<String>() : reminders; }
}
