package v620.cc001.base.common.dto.career;

import java.util.ArrayList;
import java.util.List;

/** School positioning result. */
public class StudyAbroadSchoolPositionResult {
    private String status;
    private String summary;
    private final List<StudyAbroadSchoolOptionDto> options = new ArrayList<StudyAbroadSchoolOptionDto>();
    private final List<String> cautions = new ArrayList<String>();

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public List<StudyAbroadSchoolOptionDto> getOptions() { return options; }
    public List<String> getCautions() { return cautions; }
}
