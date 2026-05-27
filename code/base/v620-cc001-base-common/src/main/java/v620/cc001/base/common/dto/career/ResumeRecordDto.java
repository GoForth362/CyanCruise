package v620.cc001.base.common.dto.career;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * JDK 8 compatible resume record contract migrated from IPD.
 */
public class ResumeRecordDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long resumeId;
    private String userId;
    private String title;
    private String targetJob;
    private String fileKey;
    private String version = "v1.0";
    private String status = "UPLOADED";
    private String parsedContent;
    private Integer diagnosisScore = Integer.valueOf(0);
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getResumeId() {
        return resumeId;
    }

    public void setResumeId(Long resumeId) {
        this.resumeId = resumeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

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

    public String getParsedContent() {
        return parsedContent;
    }

    public void setParsedContent(String parsedContent) {
        this.parsedContent = parsedContent;
    }

    public Integer getDiagnosisScore() {
        return diagnosisScore;
    }

    public void setDiagnosisScore(Integer diagnosisScore) {
        this.diagnosisScore = diagnosisScore;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
