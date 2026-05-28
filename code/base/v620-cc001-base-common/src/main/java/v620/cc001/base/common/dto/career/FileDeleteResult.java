package v620.cc001.base.common.dto.career;

import java.io.Serializable;

public class FileDeleteResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private String status;
    private String message;
    private String objectKey;
    private Boolean deleted;
    private String provider;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getObjectKey() { return objectKey; }
    public void setObjectKey(String objectKey) { this.objectKey = objectKey; }
    public Boolean getDeleted() { return deleted; }
    public void setDeleted(Boolean deleted) { this.deleted = deleted; }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
}
