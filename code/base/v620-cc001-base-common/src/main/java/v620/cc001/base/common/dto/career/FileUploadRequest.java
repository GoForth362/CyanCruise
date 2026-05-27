package v620.cc001.base.common.dto.career;

import java.io.Serializable;

public class FileUploadRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String folder;
    private String originalFilename;
    private byte[] bytes;

    public String getFolder() { return folder; }
    public void setFolder(String folder) { this.folder = folder; }
    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }
    public byte[] getBytes() { return bytes; }
    public void setBytes(byte[] bytes) { this.bytes = bytes; }
}
