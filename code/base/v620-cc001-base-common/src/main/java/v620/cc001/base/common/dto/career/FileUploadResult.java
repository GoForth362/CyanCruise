package v620.cc001.base.common.dto.career;

import java.io.Serializable;

public class FileUploadResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private String status;
    private String message;
    private FileReferenceDto file;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public FileReferenceDto getFile() { return file; }
    public void setFile(FileReferenceDto file) { this.file = file; }
}
