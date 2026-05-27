package v620.cc001.base.common.dto.career;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

public class AdminAnalyticsSummaryDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer totalUsers;
    private Integer totalInterviews;
    private Integer totalAssessments;
    private Integer totalCheckIns;
    private LocalDateTime since;
    private Map<String, Integer> eventBreakdown30d = new LinkedHashMap<String, Integer>();
    public Integer getTotalUsers() { return totalUsers; }
    public void setTotalUsers(Integer totalUsers) { this.totalUsers = totalUsers; }
    public Integer getTotalInterviews() { return totalInterviews; }
    public void setTotalInterviews(Integer totalInterviews) { this.totalInterviews = totalInterviews; }
    public Integer getTotalAssessments() { return totalAssessments; }
    public void setTotalAssessments(Integer totalAssessments) { this.totalAssessments = totalAssessments; }
    public Integer getTotalCheckIns() { return totalCheckIns; }
    public void setTotalCheckIns(Integer totalCheckIns) { this.totalCheckIns = totalCheckIns; }
    public LocalDateTime getSince() { return since; }
    public void setSince(LocalDateTime since) { this.since = since; }
    public Map<String, Integer> getEventBreakdown30d() { return eventBreakdown30d; }
    public void setEventBreakdown30d(Map<String, Integer> eventBreakdown30d) { this.eventBreakdown30d = eventBreakdown30d; }
}
