package v620.cc001.base.common.dto.career;

import java.io.Serializable;

public class FileTextExtractionResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private String status;
    private String message;
    private String objectKey;
    private String text;
    private Integer charCount;
    private Boolean truncated;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getObjectKey() { return objectKey; }
    public void setObjectKey(String objectKey) { this.objectKey = objectKey; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public Integer getCharCount() { return charCount; }
    public void setCharCount(Integer charCount) { this.charCount = charCount; }
    public Boolean getTruncated() { return truncated; }
    public void setTruncated(Boolean truncated) { this.truncated = truncated; }
}
