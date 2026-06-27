package v620.cc001.base.common.dto.career;

/** Input for personal statement outline generation. */
public class StudyAbroadStatementRequest {
    private String targetMajor;
    private String professorTopic;
    private String personalStory;
    private String academicExperience;
    private String careerGoal;
    private String language;

    public String getTargetMajor() { return targetMajor; }
    public void setTargetMajor(String targetMajor) { this.targetMajor = targetMajor; }
    public String getProfessorTopic() { return professorTopic; }
    public void setProfessorTopic(String professorTopic) { this.professorTopic = professorTopic; }
    public String getPersonalStory() { return personalStory; }
    public void setPersonalStory(String personalStory) { this.personalStory = personalStory; }
    public String getAcademicExperience() { return academicExperience; }
    public void setAcademicExperience(String academicExperience) { this.academicExperience = academicExperience; }
    public String getCareerGoal() { return careerGoal; }
    public void setCareerGoal(String careerGoal) { this.careerGoal = careerGoal; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
}
