package v620.cc001.base.common.dto.career;

import java.util.ArrayList;
import java.util.List;

/** Result for tutor contact letter generation. */
public class RecommendationTutorLetterResult {
    private String status;
    private String subject;
    private String body;
    private List<String> attachments = new ArrayList<String>();
    private List<String> sendTips = new ArrayList<String>();
    private List<String> missingInfo = new ArrayList<String>();

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public List<String> getAttachments() { return attachments; }
    public void setAttachments(List<String> attachments) { this.attachments = attachments == null ? new ArrayList<String>() : attachments; }
    public List<String> getSendTips() { return sendTips; }
    public void setSendTips(List<String> sendTips) { this.sendTips = sendTips == null ? new ArrayList<String>() : sendTips; }
    public List<String> getMissingInfo() { return missingInfo; }
    public void setMissingInfo(List<String> missingInfo) { this.missingInfo = missingInfo == null ? new ArrayList<String>() : missingInfo; }
}
