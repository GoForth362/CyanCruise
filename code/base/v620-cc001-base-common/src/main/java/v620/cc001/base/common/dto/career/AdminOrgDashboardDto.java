package v620.cc001.base.common.dto.career;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AdminOrgDashboardDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orgId;
    private Integer studentCount;
    private Integer interviewCount;
    private Integer reportCount;
    private Integer skippedReportCount;
    private Map<String, Double> radarAverages = new LinkedHashMap<String, Double>();
    private List<String> weakDimensionsTop3 = new ArrayList<String>();

    public String getOrgId() { return orgId; }
    public void setOrgId(String orgId) { this.orgId = orgId; }
    public Integer getStudentCount() { return studentCount; }
    public void setStudentCount(Integer studentCount) { this.studentCount = studentCount; }
    public Integer getInterviewCount() { return interviewCount; }
    public void setInterviewCount(Integer interviewCount) { this.interviewCount = interviewCount; }
    public Integer getReportCount() { return reportCount; }
    public void setReportCount(Integer reportCount) { this.reportCount = reportCount; }
    public Integer getSkippedReportCount() { return skippedReportCount; }
    public void setSkippedReportCount(Integer skippedReportCount) { this.skippedReportCount = skippedReportCount; }
    public Map<String, Double> getRadarAverages() { return radarAverages; }
    public void setRadarAverages(Map<String, Double> radarAverages) { this.radarAverages = radarAverages; }
    public List<String> getWeakDimensionsTop3() { return weakDimensionsTop3; }
    public void setWeakDimensionsTop3(List<String> weakDimensionsTop3) { this.weakDimensionsTop3 = weakDimensionsTop3; }
}
