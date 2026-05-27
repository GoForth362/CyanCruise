package v620.cc001.base.common.dto.career;

import java.io.Serializable;

public class SubscriptionSendResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private String status;
    private String reason;
    private String templateId;
    private String userId;
    private Integer remainingAfterSend;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getRemainingAfterSend() {
        return remainingAfterSend;
    }

    public void setRemainingAfterSend(Integer remainingAfterSend) {
        this.remainingAfterSend = remainingAfterSend;
    }
}
