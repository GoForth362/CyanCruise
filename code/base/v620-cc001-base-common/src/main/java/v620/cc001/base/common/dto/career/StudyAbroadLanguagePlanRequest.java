package v620.cc001.base.common.dto.career;

/** Input for IELTS, TOEFL, GRE or similar language exam planning. */
public class StudyAbroadLanguagePlanRequest {
    private String examType;
    private String currentScore;
    private String targetScore;
    private String examDate;
    private String weeklyHours;
    private String weakParts;

    public String getExamType() { return examType; }
    public void setExamType(String examType) { this.examType = examType; }
    public String getCurrentScore() { return currentScore; }
    public void setCurrentScore(String currentScore) { this.currentScore = currentScore; }
    public String getTargetScore() { return targetScore; }
    public void setTargetScore(String targetScore) { this.targetScore = targetScore; }
    public String getExamDate() { return examDate; }
    public void setExamDate(String examDate) { this.examDate = examDate; }
    public String getWeeklyHours() { return weeklyHours; }
    public void setWeeklyHours(String weeklyHours) { this.weeklyHours = weeklyHours; }
    public String getWeakParts() { return weakParts; }
    public void setWeakParts(String weakParts) { this.weakParts = weakParts; }
}
