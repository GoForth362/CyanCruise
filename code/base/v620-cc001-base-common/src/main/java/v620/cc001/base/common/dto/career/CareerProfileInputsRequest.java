package v620.cc001.base.common.dto.career;

/**
 * Optional user-supplied career facts that supplement inferred profile data.
 */
public class CareerProfileInputsRequest {

    private String targetCity;
    private String targetIndustry;
    private String timeline;
    private String weeklyHours;
    private String preferredDifficulty;
    private Boolean considerGradSchool;
    private Boolean considerStudyAbroad;
    private String careerGoalNote;

    public String getTargetCity() {
        return targetCity;
    }

    public void setTargetCity(String targetCity) {
        this.targetCity = targetCity;
    }

    public String getTargetIndustry() {
        return targetIndustry;
    }

    public void setTargetIndustry(String targetIndustry) {
        this.targetIndustry = targetIndustry;
    }

    public String getTimeline() {
        return timeline;
    }

    public void setTimeline(String timeline) {
        this.timeline = timeline;
    }

    public String getWeeklyHours() {
        return weeklyHours;
    }

    public void setWeeklyHours(String weeklyHours) {
        this.weeklyHours = weeklyHours;
    }

    public String getPreferredDifficulty() {
        return preferredDifficulty;
    }

    public void setPreferredDifficulty(String preferredDifficulty) {
        this.preferredDifficulty = preferredDifficulty;
    }

    public Boolean getConsiderGradSchool() {
        return considerGradSchool;
    }

    public void setConsiderGradSchool(Boolean considerGradSchool) {
        this.considerGradSchool = considerGradSchool;
    }

    public Boolean getConsiderStudyAbroad() {
        return considerStudyAbroad;
    }

    public void setConsiderStudyAbroad(Boolean considerStudyAbroad) {
        this.considerStudyAbroad = considerStudyAbroad;
    }

    public String getCareerGoalNote() {
        return careerGoalNote;
    }

    public void setCareerGoalNote(String careerGoalNote) {
        this.careerGoalNote = careerGoalNote;
    }
}
