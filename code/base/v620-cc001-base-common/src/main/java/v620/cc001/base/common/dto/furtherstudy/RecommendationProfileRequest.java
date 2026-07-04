package v620.cc001.base.common.dto.furtherstudy;

/** Background input for postgraduate recommendation diagnosis and plan. */
public class RecommendationProfileRequest {
    private String grade;
    private String school;
    private String major;
    private String gpa;
    private String rank;
    private String englishLevel;
    private String awards;
    private String research;
    private String papers;
    private String patentsOrCopyrights;
    private String targetSchools;
    private String targetMajor;

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    public String getSchool() { return school; }
    public void setSchool(String school) { this.school = school; }
    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }
    public String getGpa() { return gpa; }
    public void setGpa(String gpa) { this.gpa = gpa; }
    public String getRank() { return rank; }
    public void setRank(String rank) { this.rank = rank; }
    public String getEnglishLevel() { return englishLevel; }
    public void setEnglishLevel(String englishLevel) { this.englishLevel = englishLevel; }
    public String getAwards() { return awards; }
    public void setAwards(String awards) { this.awards = awards; }
    public String getResearch() { return research; }
    public void setResearch(String research) { this.research = research; }
    public String getPapers() { return papers; }
    public void setPapers(String papers) { this.papers = papers; }
    public String getPatentsOrCopyrights() { return patentsOrCopyrights; }
    public void setPatentsOrCopyrights(String patentsOrCopyrights) { this.patentsOrCopyrights = patentsOrCopyrights; }
    public String getTargetSchools() { return targetSchools; }
    public void setTargetSchools(String targetSchools) { this.targetSchools = targetSchools; }
    public String getTargetMajor() { return targetMajor; }
    public void setTargetMajor(String targetMajor) { this.targetMajor = targetMajor; }
}
