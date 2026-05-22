package v620.cc001.base.common.dto.career;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Result of one assessment scoring run.
 */
public class AssessmentScoreResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long scaleId;
    private String scaleTitle;
    private String status = "COMPLETED";
    private String resultSummary;
    private Map<String, Integer> dimensionCounts = new LinkedHashMap<String, Integer>();
    private List<AssessmentAnswerSnapshot> answers = new ArrayList<AssessmentAnswerSnapshot>();

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
}
