package v620.cc001.base.common.dto.furtherstudy;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class FurtherStudyRecordSummaryDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String recordId;
    private String userId;
    private String track;
    private String recordType;
    private String targetId;
    private String title;
    private String status;
    private String targetSchool;
    private String targetMajor;
    private String targetRegion;
    private LocalDate examOrDeadlineDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTargetSchool() {
        return targetSchool;
    }

    public void setTargetSchool(String targetSchool) {
        this.targetSchool = targetSchool;
    }

    public String getTargetMajor() {
        return targetMajor;
    }

    public void setTargetMajor(String targetMajor) {
        this.targetMajor = targetMajor;
    }

    public String getTargetRegion() {
        return targetRegion;
    }

    public void setTargetRegion(String targetRegion) {
        this.targetRegion = targetRegion;
    }

    public LocalDate getExamOrDeadlineDate() {
        return examOrDeadlineDate;
    }

    public void setExamOrDeadlineDate(LocalDate examOrDeadlineDate) {
        this.examOrDeadlineDate = examOrDeadlineDate;
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
