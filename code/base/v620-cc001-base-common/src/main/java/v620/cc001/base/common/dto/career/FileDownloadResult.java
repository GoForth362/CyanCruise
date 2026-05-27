package v620.cc001.base.common.dto.career;

import java.io.Serializable;

public class FileDownloadResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private String status;
    private String message;
    private String objectKey;
    private byte[] bytes;
    private Long sizeBytes;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getObjectKey() { return objectKey; }
    public void setObjectKey(String objectKey) { this.objectKey = objectKey; }
    public byte[] getBytes() { return bytes; }
    public void setBytes(byte[] bytes) { this.bytes = bytes; }
    public Long getSizeBytes() { return sizeBytes; }
    public void setSizeBytes(Long sizeBytes) { this.sizeBytes = sizeBytes; }
}
