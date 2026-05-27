package v620.cc001.base.common.dto.career;

import java.io.Serializable;

/**
 * Partial update request for resume metadata.
 */
public class ResumeUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;
    private String targetJob;
    private String fileKey;
    private String parsedContent;
    private String version;
    private String status;
    private Integer diagnosisScore;

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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getDiagnosisScore() {
        return diagnosisScore;
    }

    public void setDiagnosisScore(Integer diagnosisScore) {
        this.diagnosisScore = diagnosisScore;
    }
}
