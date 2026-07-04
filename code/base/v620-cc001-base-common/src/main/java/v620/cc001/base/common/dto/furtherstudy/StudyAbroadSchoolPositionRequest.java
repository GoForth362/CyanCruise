package v620.cc001.base.common.dto.furtherstudy;

/** Input for school positioning. */
public class StudyAbroadSchoolPositionRequest {
    private String countryOrRegion;
    private String targetMajor;
    private String gpa;
    private String languageScore;
    private String budget;
    private String background;
    private String preference;

    public String getCountryOrRegion() { return countryOrRegion; }
    public void setCountryOrRegion(String countryOrRegion) { this.countryOrRegion = countryOrRegion; }
    public String getTargetMajor() { return targetMajor; }
    public void setTargetMajor(String targetMajor) { this.targetMajor = targetMajor; }
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
