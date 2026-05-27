package v620.cc001.base.common.dto.career;

import java.io.Serializable;

public class AdminOrganizationDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orgId;
    private String code;
    private String name;
    private String description;
    private String contactName;
    private String contactEmail;
    private Boolean active;

    public String getOrgId() { return orgId; }
    public void setOrgId(String orgId) { this.orgId = orgId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
