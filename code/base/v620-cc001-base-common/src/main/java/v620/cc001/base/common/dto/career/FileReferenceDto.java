package v620.cc001.base.common.dto.career;

import java.io.Serializable;

public class FileReferenceDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String objectKey;
    private String folder;
    private String originalFilename;
    private String extension;
    private Long sizeBytes;

    public String getObjectKey() { return objectKey; }
    public void setObjectKey(String objectKey) { this.objectKey = objectKey; }
    public String getFolder() { return folder; }
    public void setFolder(String folder) { this.folder = folder; }
    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }
    public String getExtension() { return extension; }
    public void setExtension(String extension) { this.extension = extension; }
    public Long getSizeBytes() { return sizeBytes; }
    public void setSizeBytes(Long sizeBytes) { this.sizeBytes = sizeBytes; }
}
