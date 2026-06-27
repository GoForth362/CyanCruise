package v620.cc001.base.common.dto.career;

/** Request for tutor contact letter generation. */
public class RecommendationTutorLetterRequest {
    private String tutorName;
    private String targetSchool;
    private String targetMajor;
    private String researchDirection;
    private String personalBackground;
    private String purpose;

    public String getTutorName() { return tutorName; }
    public void setTutorName(String tutorName) { this.tutorName = tutorName; }
    public String getTargetSchool() { return targetSchool; }
    public void setTargetSchool(String targetSchool) { this.targetSchool = targetSchool; }
    public String getTargetMajor() { return targetMajor; }
    public void setTargetMajor(String targetMajor) { this.targetMajor = targetMajor; }
    public String getResearchDirection() { return researchDirection; }
    public void setResearchDirection(String researchDirection) { this.researchDirection = researchDirection; }
    public String getPersonalBackground() { return personalBackground; }
    public void setPersonalBackground(String personalBackground) { this.personalBackground = personalBackground; }
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
}
