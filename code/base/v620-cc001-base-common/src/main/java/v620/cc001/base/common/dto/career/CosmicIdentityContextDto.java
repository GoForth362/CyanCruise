package v620.cc001.base.common.dto.career;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Platform identity context used before invoking user-owned or admin-owned APIs.
 */
public class CosmicIdentityContextDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;
    private String adminId;
    private List<String> roles = new ArrayList<String>();
    private String orgId;
    private String source;
    private String environment;
    private String status;
    private String ip;
    private String userAgent;
    private String message;

    public static CosmicIdentityContextDto identityRequired(String message) {
        CosmicIdentityContextDto context = new CosmicIdentityContextDto();
        context.setStatus(CosmicIdentityConstants.STATUS_IDENTITY_REQUIRED);
        context.setSource(CosmicIdentityConstants.SOURCE_UNAVAILABLE);
        context.setEnvironment(CosmicIdentityConstants.ENV_PRODUCTION);
        context.setMessage(message);
        return context;
    }

    public static CosmicIdentityContextDto development(String userId, String adminId, List<String> roles) {
        CosmicIdentityContextDto context = new CosmicIdentityContextDto();
        context.setUserId(userId);
        context.setAdminId(adminId);
        context.setRoles(roles);
        context.setSource(CosmicIdentityConstants.SOURCE_DEVELOPMENT_FALLBACK);
        context.setEnvironment(CosmicIdentityConstants.ENV_DEVELOPMENT);
        context.setStatus(hasText(userId) || hasText(adminId) ? CosmicIdentityConstants.STATUS_OK
                : CosmicIdentityConstants.STATUS_IDENTITY_REQUIRED);
        return context;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles == null ? new ArrayList<String>() : new ArrayList<String>(roles);
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private static boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
