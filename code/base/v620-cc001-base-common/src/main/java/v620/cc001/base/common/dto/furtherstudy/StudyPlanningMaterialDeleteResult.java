package v620.cc001.base.common.dto.furtherstudy;

import java.io.Serializable;

public class StudyPlanningMaterialDeleteResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private String materialId;
    private Boolean deleted;
    private String message;

    public String getMaterialId() { return materialId; }
    public void setMaterialId(String materialId) { this.materialId = materialId; }
    public Boolean getDeleted() { return deleted; }
    public void setDeleted(Boolean deleted) { this.deleted = deleted; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
