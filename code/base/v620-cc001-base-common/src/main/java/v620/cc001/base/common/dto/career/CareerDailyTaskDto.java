package v620.cc001.base.common.dto.career;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * One persisted daily action linked to a source item in the career plan.
 */
public class CareerDailyTaskDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String taskId;
    private String routeType;
    private String sourceTaskId;
    private String phaseId;
    private String text;
    private LocalDate planDate;
    private String status;
    private Integer sequence;
    private Integer planVersion;
    private Boolean carriedOver;
    private LocalDateTime completedAt;
    private LocalDateTime updatedAt;

    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }
    public String getRouteType() { return routeType; }
    public void setRouteType(String routeType) { this.routeType = routeType; }
    public String getSourceTaskId() { return sourceTaskId; }
    public void setSourceTaskId(String sourceTaskId) { this.sourceTaskId = sourceTaskId; }
    public String getPhaseId() { return phaseId; }
    public void setPhaseId(String phaseId) { this.phaseId = phaseId; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public LocalDate getPlanDate() { return planDate; }
    public void setPlanDate(LocalDate planDate) { this.planDate = planDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getSequence() { return sequence; }
    public void setSequence(Integer sequence) { this.sequence = sequence; }
    public Integer getPlanVersion() { return planVersion; }
    public void setPlanVersion(Integer planVersion) { this.planVersion = planVersion; }
    public Boolean getCarriedOver() { return carriedOver; }
    public void setCarriedOver(Boolean carriedOver) { this.carriedOver = carriedOver; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public boolean isCompleted() {
        return "COMPLETED".equalsIgnoreCase(status);
    }
}
