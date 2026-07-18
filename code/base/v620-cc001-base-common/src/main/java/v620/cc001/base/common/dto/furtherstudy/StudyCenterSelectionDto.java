package v620.cc001.base.common.dto.furtherstudy;

import java.io.Serializable;
import java.time.LocalDateTime;

/** 用户在升学中心保存的主方向。 */
public class StudyCenterSelectionDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String userId;
    private String direction;
    private String targetSchool;
    private LocalDateTime updatedAt;
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }
    public String getTargetSchool() { return targetSchool; }
    public void setTargetSchool(String targetSchool) { this.targetSchool = targetSchool; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
