package v620.cc001.base.common.dto.career;

import java.io.Serializable;

public class AdminOperationResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private String status;
    private String message;
    private String targetId;
    private Integer updated;
    private Boolean auditRecorded;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public Integer getUpdated() {
        return updated;
    }

    public void setUpdated(Integer updated) {
        this.updated = updated;
    }

    public Boolean getAuditRecorded() {
        return auditRecorded;
    }

    public void setAuditRecorded(Boolean auditRecorded) {
        this.auditRecorded = auditRecorded;
    }
}
