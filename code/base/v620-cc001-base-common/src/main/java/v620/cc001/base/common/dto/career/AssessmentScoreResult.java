package v620.cc001.base.common.dto.career;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Result of one assessment scoring run.
 */
public class AssessmentScoreResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long recordId;
    private String userId;
    private Long scaleId;
    private String scaleTitle;
    private String status = "COMPLETED";
    private String resultSummary;
    private Map<String, Integer> dimensionCounts = new LinkedHashMap<String, Integer>();
    private List<AssessmentAnswerSnapshot> answers = new ArrayList<AssessmentAnswerSnapshot>();
    private List<String> suggestedRoles = new ArrayList<String>();
    private AiInterpretation aiInterpretation;
    private LocalDateTime createdAt;

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getScaleId() {
        return scaleId;
    }

    public void setScaleId(Long scaleId) {
        this.scaleId = scaleId;
    }

    public String getScaleTitle() {
        return scaleTitle;
    }

    public void setScaleTitle(String scaleTitle) {
        this.scaleTitle = scaleTitle;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResultSummary() {
        return resultSummary;
    }

    public void setResultSummary(String resultSummary) {
        this.resultSummary = resultSummary;
    }

    public Map<String, Integer> getDimensionCounts() {
        return dimensionCounts;
    }

    public void setDimensionCounts(Map<String, Integer> dimensionCounts) {
        this.dimensionCounts = dimensionCounts;
    }

    public List<AssessmentAnswerSnapshot> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AssessmentAnswerSnapshot> answers) {
        this.answers = answers;
    }

    public List<String> getSuggestedRoles() {
        return suggestedRoles;
    }

    public void setSuggestedRoles(List<String> suggestedRoles) {
        this.suggestedRoles = suggestedRoles;
    }

    public AiInterpretation getAiInterpretation() {
        return aiInterpretation;
    }

    public void setAiInterpretation(AiInterpretation aiInterpretation) {
        this.aiInterpretation = aiInterpretation;
    }

    public static class AiInterpretation implements Serializable {
        private static final long serialVersionUID = 1L;
        private String summary;
        private List<String> insights = new ArrayList<String>();
        private List<String> suggestions = new ArrayList<String>();
        private List<String> dataGaps = new ArrayList<String>();
        private LocalDateTime generatedAt;
        private String source;

        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
        public List<String> getInsights() { return insights; }
        public void setInsights(List<String> insights) { this.insights = insights; }
        public List<String> getSuggestions() { return suggestions; }
        public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }
        public List<String> getDataGaps() { return dataGaps; }
        public void setDataGaps(List<String> dataGaps) { this.dataGaps = dataGaps; }
        public LocalDateTime getGeneratedAt() { return generatedAt; }
        public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
