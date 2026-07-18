package v620.cc001.base.common.dto.career;

import java.io.Serializable;
import java.time.LocalDateTime;

/** Fixed server-side question snapshot for one assessment run. */
public class AssessmentAttemptDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String attemptId;
    private String userId;
    private Long scaleId;
    private String status;
    private AssessmentScaleDto scale;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    public String getAttemptId() { return attemptId; }
    public void setAttemptId(String attemptId) { this.attemptId = attemptId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public Long getScaleId() { return scaleId; }
    public void setScaleId(Long scaleId) { this.scaleId = scaleId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public AssessmentScaleDto getScale() { return scale; }
    public void setScale(AssessmentScaleDto scale) { this.scale = scale; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
