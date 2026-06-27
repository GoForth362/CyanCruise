package v620.cc001.base.common.dto.furtherstudy;

import java.util.ArrayList;
import java.util.List;

/** Request for postgraduate exam revision plan generation. */
public class PostgraduatePlanRequest {
    private String targetSchool;
    private String targetMajor;
    private String examDate;
    private String startDate;
    private String weeklyHours;
    private List<String> subjects = new ArrayList<String>();
    private String currentStage;

    public String getTargetSchool() { return targetSchool; }
    public void setTargetSchool(String targetSchool) { this.targetSchool = targetSchool; }
    public String getTargetMajor() { return targetMajor; }
    public void setTargetMajor(String targetMajor) { this.targetMajor = targetMajor; }
    public String getExamDate() { return examDate; }
    public void setExamDate(String examDate) { this.examDate = examDate; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getWeeklyHours() { return weeklyHours; }
    public void setWeeklyHours(String weeklyHours) { this.weeklyHours = weeklyHours; }
    public List<String> getSubjects() { return subjects; }
    public void setSubjects(List<String> subjects) { this.subjects = subjects == null ? new ArrayList<String>() : subjects; }
    public String getCurrentStage() { return currentStage; }
    public void setCurrentStage(String currentStage) { this.currentStage = currentStage; }
}
