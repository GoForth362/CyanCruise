package v620.cc001.base.common.dto.career;

import java.util.ArrayList;
import java.util.List;

/** Visa and online application checklist result. */
public class StudyAbroadVisaChecklistResult {
    private String status;
    private String summary;
    private final List<StudyAbroadChecklistItemDto> checklist = new ArrayList<StudyAbroadChecklistItemDto>();
    private final List<String> risks = new ArrayList<String>();
    private final List<String> reminders = new ArrayList<String>();

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public List<StudyAbroadChecklistItemDto> getChecklist() { return checklist; }
    public List<String> getRisks() { return risks; }
    public List<String> getReminders() { return reminders; }
}
