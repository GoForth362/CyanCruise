package v620.cc001.base.common.dto.furtherstudy;

import java.util.ArrayList;
import java.util.List;

/** Request for postgraduate re-exam preparation guidance. */
public class PostgraduateReexamPrepareRequest {
    private String targetSchool;
    private String targetMajor;
    private String preliminaryStatus;
    private List<String> materials = new ArrayList<String>();
    private String researchExperience;

    public String getTargetSchool() { return targetSchool; }
    public void setTargetSchool(String targetSchool) { this.targetSchool = targetSchool; }
    /** Compatibility names used by earlier published further-study task flows. */
    public String getTargetUniversity() { return targetSchool; }
    public String getSchool() { return targetSchool; }
    public String getTargetMajor() { return targetMajor; }
    public void setTargetMajor(String targetMajor) { this.targetMajor = targetMajor; }
    public String getMajor() { return targetMajor; }
    public String getPreliminaryStatus() { return preliminaryStatus; }
    public void setPreliminaryStatus(String preliminaryStatus) { this.preliminaryStatus = preliminaryStatus; }
    public String getExamStatus() { return preliminaryStatus; }
    public String getPreliminaryExamStatus() { return preliminaryStatus; }
    public String getPreliminaryPassed() { return preliminaryStatus; }
    public List<String> getMaterials() { return materials; }
    public void setMaterials(List<String> materials) { this.materials = materials == null ? new ArrayList<String>() : materials; }
    public String getResearchExperience() { return researchExperience; }
    public void setResearchExperience(String researchExperience) { this.researchExperience = researchExperience; }
}
