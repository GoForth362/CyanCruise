package v620.cc001.base.common.dto.furtherstudy;

import java.util.ArrayList;
import java.util.List;

/** School positioning option. */
public class StudyAbroadSchoolOptionDto {
    private String tier;
    private String schoolName;
    private String program;
    private String reason;
    private final List<String> preparation = new ArrayList<String>();

    public String getTier() { return tier; }
    public void setTier(String tier) { this.tier = tier; }
    public String getSchoolName() { return schoolName; }
    public void setSchoolName(String schoolName) { this.schoolName = schoolName; }
    public String getProgram() { return program; }
    public void setProgram(String program) { this.program = program; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public List<String> getPreparation() { return preparation; }
}
