package v620.cc001.base.common.dto.furtherstudy;

import java.util.ArrayList;
import java.util.List;

/** One recommended postgraduate target option. */
public class PostgraduateSchoolOptionDto {
    private String tier;
    private String tierName;
    private String schoolName;
    private String majorName;
    private String region;
    private String reason;
    private String risk;
    private List<String> actions = new ArrayList<String>();

    public String getTier() { return tier; }
    public void setTier(String tier) { this.tier = tier; }
    public String getTierName() { return tierName; }
    public void setTierName(String tierName) { this.tierName = tierName; }
    public String getSchoolName() { return schoolName; }
    public void setSchoolName(String schoolName) { this.schoolName = schoolName; }
    public String getMajorName() { return majorName; }
    public void setMajorName(String majorName) { this.majorName = majorName; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getRisk() { return risk; }
    public void setRisk(String risk) { this.risk = risk; }
    public List<String> getActions() { return actions; }
    public void setActions(List<String> actions) { this.actions = actions == null ? new ArrayList<String>() : actions; }
}
