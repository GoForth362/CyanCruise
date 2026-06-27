package v620.cc001.base.common.dto.career;

/** Input for visa and online application checklist. */
public class StudyAbroadVisaChecklistRequest {
    private String countryOrRegion;
    private String applicationSeason;
    private String admissionStatus;
    private String materialStatus;

    public String getCountryOrRegion() { return countryOrRegion; }
    public void setCountryOrRegion(String countryOrRegion) { this.countryOrRegion = countryOrRegion; }
    public String getApplicationSeason() { return applicationSeason; }
    public void setApplicationSeason(String applicationSeason) { this.applicationSeason = applicationSeason; }
    public String getAdmissionStatus() { return admissionStatus; }
    public void setAdmissionStatus(String admissionStatus) { this.admissionStatus = admissionStatus; }
    public String getMaterialStatus() { return materialStatus; }
    public void setMaterialStatus(String materialStatus) { this.materialStatus = materialStatus; }
}
