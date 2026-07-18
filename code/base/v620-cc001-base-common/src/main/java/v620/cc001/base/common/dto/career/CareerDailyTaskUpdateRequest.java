package v620.cc001.base.common.dto.career;

import java.io.Serializable;

/** Request for changing a persisted daily task completion state. */
public class CareerDailyTaskUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String taskId;
    private Boolean completed;

    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }
    public Boolean getCompleted() { return completed; }
    public void setCompleted(Boolean completed) { this.completed = completed; }
}
