package v620.cc001.base.common.dto.career;

import java.util.ArrayList;
import java.util.List;

/** Action plan for postgraduate recommendation preparation. */
public class RecommendationPlanResult {
    private String status;
    private String summary;
    private List<RecommendationActionItemDto> timeline = new ArrayList<RecommendationActionItemDto>();
    private List<String> weeklyFocus = new ArrayList<String>();
    private List<String> targetCampTips = new ArrayList<String>();

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public List<RecommendationActionItemDto> getTimeline() { return timeline; }
    public void setTimeline(List<RecommendationActionItemDto> timeline) { this.timeline = timeline == null ? new ArrayList<RecommendationActionItemDto>() : timeline; }
    public List<String> getWeeklyFocus() { return weeklyFocus; }
    public void setWeeklyFocus(List<String> weeklyFocus) { this.weeklyFocus = weeklyFocus == null ? new ArrayList<String>() : weeklyFocus; }
    public List<String> getTargetCampTips() { return targetCampTips; }
    public void setTargetCampTips(List<String> targetCampTips) { this.targetCampTips = targetCampTips == null ? new ArrayList<String>() : targetCampTips; }
}
