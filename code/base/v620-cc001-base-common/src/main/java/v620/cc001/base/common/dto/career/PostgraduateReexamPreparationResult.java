package v620.cc001.base.common.dto.career;

import java.util.ArrayList;
import java.util.List;

/** Result for postgraduate re-exam preparation. */
public class PostgraduateReexamPreparationResult {
    private String status;
    private String summary;
    private List<PostgraduateChecklistItemDto> checklist = new ArrayList<PostgraduateChecklistItemDto>();
    private List<String> tutorContactTips = new ArrayList<String>();
    private List<String> resumeTips = new ArrayList<String>();
    private List<String> mockInterviewTips = new ArrayList<String>();

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public List<PostgraduateChecklistItemDto> getChecklist() { return checklist; }
    public void setChecklist(List<PostgraduateChecklistItemDto> checklist) { this.checklist = checklist == null ? new ArrayList<PostgraduateChecklistItemDto>() : checklist; }
    public List<String> getTutorContactTips() { return tutorContactTips; }
    public void setTutorContactTips(List<String> tutorContactTips) { this.tutorContactTips = tutorContactTips == null ? new ArrayList<String>() : tutorContactTips; }
    public List<String> getResumeTips() { return resumeTips; }
    public void setResumeTips(List<String> resumeTips) { this.resumeTips = resumeTips == null ? new ArrayList<String>() : resumeTips; }
    public List<String> getMockInterviewTips() { return mockInterviewTips; }
    public void setMockInterviewTips(List<String> mockInterviewTips) { this.mockInterviewTips = mockInterviewTips == null ? new ArrayList<String>() : mockInterviewTips; }
}
