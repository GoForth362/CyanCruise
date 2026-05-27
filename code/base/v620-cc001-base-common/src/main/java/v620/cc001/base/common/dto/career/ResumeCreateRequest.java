package v620.cc001.base.common.dto.career;

import java.io.Serializable;

/**
 * Request for creating a resume record from an already uploaded file key.
 */
public class ResumeCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;
    private String targetJob;
    private String fileKey;
    private String parsedContent;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTargetJob() {
        return targetJob;
    }

    public void setTargetJob(String targetJob) {
        this.targetJob = targetJob;
    }

    public String getFileKey() {
        return fileKey;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    public String getParsedContent() {
        return parsedContent;
    }

    public void setParsedContent(String parsedContent) {
        this.parsedContent = parsedContent;
    }
}
