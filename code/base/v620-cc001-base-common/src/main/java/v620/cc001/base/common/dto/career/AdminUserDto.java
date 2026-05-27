package v620.cc001.base.common.dto.career;

import java.io.Serializable;
import java.time.LocalDateTime;

public class AdminUserDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;
    private String orgId;
    private String nickname;
    private String school;
    private String major;
    private String status;
    private String bannedReason;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getOrgId() { return orgId; }
    public void setOrgId(String orgId) { this.orgId = orgId; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getSchool() { return school; }
    public void setSchool(String school) { this.school = school; }
    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getBannedReason() { return bannedReason; }
    public void setBannedReason(String bannedReason) { this.bannedReason = bannedReason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}
