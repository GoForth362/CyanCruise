package v620.cc001.base.common.dto.career;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Stable daily execution slice derived from the persisted career plan.
 */
public class CareerDailyPlanDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private LocalDate planDate;
    private String routeType;
    private String studyDirection;
    private String phaseId;
    private String phaseTitle;
    private String summary;
    private Integer planVersion;
    private Integer completedCount;
    private Integer totalCount;
    private Boolean allCompleted;
    private List<String> completedSourceTaskIds = new ArrayList<String>();
    private List<CareerDailyTaskDto> items = new ArrayList<CareerDailyTaskDto>();

    public LocalDate getPlanDate() { return planDate; }
    public void setPlanDate(LocalDate planDate) { this.planDate = planDate; }
    public String getRouteType() { return routeType; }
    public void setRouteType(String routeType) { this.routeType = routeType; }
    public String getStudyDirection() { return studyDirection; }
    public void setStudyDirection(String studyDirection) { this.studyDirection = studyDirection; }
    public String getPhaseId() { return phaseId; }
    public void setPhaseId(String phaseId) { this.phaseId = phaseId; }
    public String getPhaseTitle() { return phaseTitle; }
    public void setPhaseTitle(String phaseTitle) { this.phaseTitle = phaseTitle; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public Integer getPlanVersion() { return planVersion; }
    public void setPlanVersion(Integer planVersion) { this.planVersion = planVersion; }
    public Integer getCompletedCount() { return completedCount; }
    public void setCompletedCount(Integer completedCount) { this.completedCount = completedCount; }
    public Integer getTotalCount() { return totalCount; }
    public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }
    public Boolean getAllCompleted() { return allCompleted; }
    public void setAllCompleted(Boolean allCompleted) { this.allCompleted = allCompleted; }
    public List<String> getCompletedSourceTaskIds() { return completedSourceTaskIds; }
    public void setCompletedSourceTaskIds(List<String> value) { this.completedSourceTaskIds = value; }
    public List<CareerDailyTaskDto> getItems() { return items; }
    public void setItems(List<CareerDailyTaskDto> items) { this.items = items; }
}
