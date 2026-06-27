package v620.cc001.base.common.dto.furtherstudy;

import java.io.Serializable;
import java.time.LocalDateTime;

public class FurtherStudyTargetDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String targetId;
    private String userId;
    private String track;
    private String targetSchool;
    private String targetMajor;
    private String targetRegion;
    private String targetStage;
    private String status;
    private String targetJson;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
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

    public String getTargetStage() {
        return targetStage;
    }

    public void setTargetStage(String targetStage) {
        this.targetStage = targetStage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTargetJson() {
        return targetJson;
    }

    public void setTargetJson(String targetJson) {
        this.targetJson = targetJson;
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
