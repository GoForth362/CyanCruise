package v620.cc001.base.common.dto.career;

/** Request for postgraduate school recommendation. */
public class PostgraduateSchoolRecommendRequest {
    private String undergraduateSchool;
    private String undergraduateLevel;
    private String major;
    private String targetMajor;
    private String gpa;
    private String englishLevel;
    private String preferredRegion;
    private String preference;

    public String getUndergraduateSchool() { return undergraduateSchool; }
    public void setUndergraduateSchool(String undergraduateSchool) { this.undergraduateSchool = undergraduateSchool; }
    public String getUndergraduateLevel() { return undergraduateLevel; }
    public void setUndergraduateLevel(String undergraduateLevel) { this.undergraduateLevel = undergraduateLevel; }
    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }
    public String getTargetMajor() { return targetMajor; }
    public void setTargetMajor(String targetMajor) { this.targetMajor = targetMajor; }
    public String getGpa() { return gpa; }
    public void setGpa(String gpa) { this.gpa = gpa; }
    public String getEnglishLevel() { return englishLevel; }
    public void setEnglishLevel(String englishLevel) { this.englishLevel = englishLevel; }
    public String getPreferredRegion() { return preferredRegion; }
    public void setPreferredRegion(String preferredRegion) { this.preferredRegion = preferredRegion; }
    public String getPreference() { return preference; }
    public void setPreference(String preference) { this.preference = preference; }
}
