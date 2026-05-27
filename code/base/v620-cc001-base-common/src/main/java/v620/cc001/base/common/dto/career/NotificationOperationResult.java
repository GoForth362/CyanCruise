package v620.cc001.base.common.dto.career;

import java.io.Serializable;

public class NotificationOperationResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private String status;
    private String message;
    private Integer updated;
    private NotificationRecordDto notification;

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

    public Integer getUpdated() {
        return updated;
    }

    public void setUpdated(Integer updated) {
        this.updated = updated;
    }

    public NotificationRecordDto getNotification() {
        return notification;
    }

    public void setNotification(NotificationRecordDto notification) {
        this.notification = notification;
    }
}
