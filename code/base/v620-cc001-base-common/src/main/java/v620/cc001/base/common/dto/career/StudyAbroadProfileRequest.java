package v620.cc001.base.common.dto.career;

/** Student background input for study abroad diagnosis. */
public class StudyAbroadProfileRequest {
    private String countryOrRegion;
    private String targetDegree;
    private String targetMajor;
    private String school;
    private String major;
    private String gpa;
    private String languageScore;
    private String budget;
    private String background;
    private String preference;

    public String getCountryOrRegion() { return countryOrRegion; }
    public void setCountryOrRegion(String countryOrRegion) { this.countryOrRegion = countryOrRegion; }
    public String getTargetDegree() { return targetDegree; }
    public void setTargetDegree(String targetDegree) { this.targetDegree = targetDegree; }
    public String getTargetMajor() { return targetMajor; }
    public void setTargetMajor(String targetMajor) { this.targetMajor = targetMajor; }
    public String getSchool() { return school; }
    public void setSchool(String school) { this.school = school; }
    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }
    public String getGpa() { return gpa; }
    public void setGpa(String gpa) { this.gpa = gpa; }
    public String getLanguageScore() { return languageScore; }
    public void setLanguageScore(String languageScore) { this.languageScore = languageScore; }
    public String getBudget() { return budget; }
    public void setBudget(String budget) { this.budget = budget; }
    public String getBackground() { return background; }
    public void setBackground(String background) { this.background = background; }
    public String getPreference() { return preference; }
    public void setPreference(String preference) { this.preference = preference; }
}
