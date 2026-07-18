package v620.cc001.base.common.dto.furtherstudy;

import java.io.Serializable;
import v620.cc001.base.common.dto.career.FileUploadRequest;

public class StudyPlanningMaterialUploadRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String direction;
    private String materialType;
    private String title;
    private String mediaType;
    private FileUploadRequest file;

    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }
    public String getMaterialType() { return materialType; }
    public void setMaterialType(String materialType) { this.materialType = materialType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMediaType() { return mediaType; }
    public void setMediaType(String mediaType) { this.mediaType = mediaType; }
    public FileUploadRequest getFile() { return file; }
    public void setFile(FileUploadRequest file) { this.file = file; }
}
