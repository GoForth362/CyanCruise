package v620.cc001.base.common.dto.furtherstudy;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** 面向用户展示的升学准备洞察。 */
public class StudyCenterInsightDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String status;
    private String direction;
    private String directionLabel;
    private String school;
    private String major;
    private String targetSchool;
    private String summary;
    private Integer sourceCount;
    private LocalDateTime updatedAt;
    private List<String> focusItems = new ArrayList<String>();
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }
    public String getDirectionLabel() { return directionLabel; }
    public void setDirectionLabel(String directionLabel) { this.directionLabel = directionLabel; }
    public String getSchool() { return school; }
    public void setSchool(String school) { this.school = school; }
    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }
    public String getTargetSchool() { return targetSchool; }
    public void setTargetSchool(String targetSchool) { this.targetSchool = targetSchool; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public Integer getSourceCount() { return sourceCount; }
    public void setSourceCount(Integer sourceCount) { this.sourceCount = sourceCount; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public List<String> getFocusItems() { return focusItems; }
    public void setFocusItems(List<String> focusItems) { this.focusItems = focusItems; }
}
