package v620.cc001.base.common.dto.career;

import java.io.Serializable;
import java.time.LocalDateTime;

public class AdminInterviewSummaryDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String interviewId;
    private String userId;
    private Integer finalScore;
    private String reportJson;
    private LocalDateTime startedAt;

    public String getInterviewId() { return interviewId; }
    public void setInterviewId(String interviewId) { this.interviewId = interviewId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public Integer getFinalScore() { return finalScore; }
    public void setFinalScore(Integer finalScore) { this.finalScore = finalScore; }
    public String getReportJson() { return reportJson; }
    public void setReportJson(String reportJson) { this.reportJson = reportJson; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
}
