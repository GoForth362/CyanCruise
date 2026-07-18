package v620.cc001.base.common.dto.furtherstudy;

import java.io.Serializable;
import java.time.LocalDateTime;

/** User-owned document that can be used as evidence for study route planning. */
public class StudyPlanningMaterialDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String materialId;
    private String userId;
    private String direction;
    private String materialType;
    private String title;
    private String originalFilename;
    private String objectKey;
    private String mediaType;
    private Long sizeBytes;
    private String extractionStatus;
    private String extractionMessage;
    private String extractedText;
    private Integer extractedCharCount;
    private Boolean truncated;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public String getMaterialId() { return materialId; }
    public void setMaterialId(String materialId) { this.materialId = materialId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }
    public String getMaterialType() { return materialType; }
    public void setMaterialType(String materialType) { this.materialType = materialType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }
    public String getObjectKey() { return objectKey; }
    public void setObjectKey(String objectKey) { this.objectKey = objectKey; }
    public String getMediaType() { return mediaType; }
    public void setMediaType(String mediaType) { this.mediaType = mediaType; }
    public Long getSizeBytes() { return sizeBytes; }
    public void setSizeBytes(Long sizeBytes) { this.sizeBytes = sizeBytes; }
    public String getExtractionStatus() { return extractionStatus; }
    public void setExtractionStatus(String extractionStatus) { this.extractionStatus = extractionStatus; }
    public String getExtractionMessage() { return extractionMessage; }
    public void setExtractionMessage(String extractionMessage) { this.extractionMessage = extractionMessage; }
    public String getExtractedText() { return extractedText; }
    public void setExtractedText(String extractedText) { this.extractedText = extractedText; }
    public Integer getExtractedCharCount() { return extractedCharCount; }
    public void setExtractedCharCount(Integer extractedCharCount) { this.extractedCharCount = extractedCharCount; }
    public Boolean getTruncated() { return truncated; }
    public void setTruncated(Boolean truncated) { this.truncated = truncated; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
