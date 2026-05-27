package v620.cc001.base.common.dto.career;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class SubscriptionSendRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;
    private String templateId;
    private String recipientId;
    private String page;
    private Map<String, String> data = new LinkedHashMap<String, String>();

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}
