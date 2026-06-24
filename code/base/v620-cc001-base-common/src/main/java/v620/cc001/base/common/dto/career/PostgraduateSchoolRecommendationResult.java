package v620.cc001.base.common.dto.career;

import java.util.ArrayList;
import java.util.List;

/** Result of postgraduate school recommendation. */
public class PostgraduateSchoolRecommendationResult {
    private String status;
    private String summary;
    private List<PostgraduateSchoolOptionDto> options = new ArrayList<PostgraduateSchoolOptionDto>();
    private List<String> missingInfo = new ArrayList<String>();
    private List<String> reminders = new ArrayList<String>();

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public List<PostgraduateSchoolOptionDto> getOptions() { return options; }
    public void setOptions(List<PostgraduateSchoolOptionDto> options) { this.options = options == null ? new ArrayList<PostgraduateSchoolOptionDto>() : options; }
    public List<String> getMissingInfo() { return missingInfo; }
    public void setMissingInfo(List<String> missingInfo) { this.missingInfo = missingInfo == null ? new ArrayList<String>() : missingInfo; }
    public List<String> getReminders() { return reminders; }
    public void setReminders(List<String> reminders) { this.reminders = reminders == null ? new ArrayList<String>() : reminders; }
}
