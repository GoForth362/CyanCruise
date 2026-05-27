package v620.cc001.base.common.dto.career;

import java.io.Serializable;
import java.time.LocalDateTime;

public class FilePreviewUrlResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private String status;
    private String message;
    private String objectKey;
    private String previewUrl;
    private Long ttlSeconds;
    private LocalDateTime expiresAt;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getObjectKey() { return objectKey; }
    public void setObjectKey(String objectKey) { this.objectKey = objectKey; }
    public String getPreviewUrl() { return previewUrl; }
    public void setPreviewUrl(String previewUrl) { this.previewUrl = previewUrl; }
    public Long getTtlSeconds() { return ttlSeconds; }
    public void setTtlSeconds(Long ttlSeconds) { this.ttlSeconds = ttlSeconds; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
}
