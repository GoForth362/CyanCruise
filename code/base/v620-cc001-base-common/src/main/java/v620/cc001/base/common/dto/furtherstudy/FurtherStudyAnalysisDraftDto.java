package v620.cc001.base.common.dto.furtherstudy;

import java.io.Serializable;
import java.time.LocalDateTime;

/** User-scoped draft for one further-study analysis task. */
public class FurtherStudyAnalysisDraftDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String taskType;
    private String payloadJson;
    private LocalDateTime updatedAt;

    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }
    public String getPayloadJson() { return payloadJson; }
    public void setPayloadJson(String payloadJson) { this.payloadJson = payloadJson; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
