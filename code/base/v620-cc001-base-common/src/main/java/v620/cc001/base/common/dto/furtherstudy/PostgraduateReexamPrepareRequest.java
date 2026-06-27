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
    public String getTargetMajor() { return targetMajor; }
    public void setTargetMajor(String targetMajor) { this.targetMajor = targetMajor; }
    public String getPreliminaryStatus() { return preliminaryStatus; }
    public void setPreliminaryStatus(String preliminaryStatus) { this.preliminaryStatus = preliminaryStatus; }
    public List<String> getMaterials() { return materials; }
    public void setMaterials(List<String> materials) { this.materials = materials == null ? new ArrayList<String>() : materials; }
    public String getResearchExperience() { return researchExperience; }
    public void setResearchExperience(String researchExperience) { this.researchExperience = researchExperience; }
}
