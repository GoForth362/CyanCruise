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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
