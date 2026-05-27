package v620.cc001.base.common.dto.career;

import java.io.Serializable;

public class AdminIdentityDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;
    private Boolean admin;
    private String status;
    private String message;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
