package v620.cc001.base.common.dto.furtherstudy;

import java.io.Serializable;

public class FurtherStudyRecordStatusUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String recordId;
    private String status;
    private String eventSummary;
    private String eventJson;

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEventSummary() {
        return eventSummary;
    }

    public void setEventSummary(String eventSummary) {
        this.eventSummary = eventSummary;
    }

    public String getEventJson() {
        return eventJson;
    }

    public void setEventJson(String eventJson) {
        this.eventJson = eventJson;
    }
}
