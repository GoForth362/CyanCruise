package v620.cc001.cloud01.app01.mservice.auth;

import v620.cc001.base.common.dto.career.CosmicIdentityConstants;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration for resolving CyanCruise identity from Cosmic platform context.
 */
public class CosmicIdentityAdapterConfig {

    public static final String ENABLED_PROPERTY = "cc001.identity.adapter.enabled";
    public static final String USER_FIELDS_PROPERTY = "cc001.identity.adapter.user.fields";
    public static final String ADMIN_FIELDS_PROPERTY = "cc001.identity.adapter.admin.fields";
    public static final String ORG_FIELDS_PROPERTY = "cc001.identity.adapter.org.fields";
    public static final String ROLE_FIELDS_PROPERTY = "cc001.identity.adapter.role.fields";
    public static final String ADMIN_ALIASES_PROPERTY = "cc001.identity.adapter.admin.aliases";
    public static final String PLATFORM_ADMIN_ENABLED_PROPERTY = "cc001.identity.adapter.platformAdmin.enabled";
    public static final String DIAGNOSTICS_ENABLED_PROPERTY = "cc001.identity.adapter.diagnostics.enabled";

    private boolean enabled;
    private List<String> userIdFields = list("userId", "currentUserId", "currUserId", "loginUserId",
            "personId", "operatorId", "uid", "userNumber");
    private List<String> adminIdFields = list("adminId", "userId", "currentUserId", "currUserId",
            "loginUserId", "operatorId");
    private List<String> orgIdFields = list("orgId", "organizationId", "deptId", "departmentId");
    private List<String> roleFields = list("roles", "roleCodes", "role", "permissionCodes");
    private List<String> adminRoleAliases = list(CosmicIdentityConstants.ROLE_ADMIN,
            CosmicIdentityConstants.ROLE_COSMIC_ADMIN,
            CosmicIdentityConstants.ROLE_PLATFORM_ADMIN);
    private boolean platformAdminEnabled = true;
    private boolean diagnosticsEnabled = true;

    public static CosmicIdentityAdapterConfig disabled() {
        return new CosmicIdentityAdapterConfig();
    }

    public static CosmicIdentityAdapterConfig fromSystemProperties() {
        CosmicIdentityAdapterConfig config = new CosmicIdentityAdapterConfig();
        config.setEnabled(Boolean.parseBoolean(System.getProperty(ENABLED_PROPERTY, "false")));
        config.setUserIdFields(split(System.getProperty(USER_FIELDS_PROPERTY), config.getUserIdFields()));
        config.setAdminIdFields(split(System.getProperty(ADMIN_FIELDS_PROPERTY), config.getAdminIdFields()));
        config.setOrgIdFields(split(System.getProperty(ORG_FIELDS_PROPERTY), config.getOrgIdFields()));
        config.setRoleFields(split(System.getProperty(ROLE_FIELDS_PROPERTY), config.getRoleFields()));
        config.setAdminRoleAliases(split(System.getProperty(ADMIN_ALIASES_PROPERTY), config.getAdminRoleAliases()));
        config.setPlatformAdminEnabled(Boolean.parseBoolean(System.getProperty(PLATFORM_ADMIN_ENABLED_PROPERTY,
                String.valueOf(config.isPlatformAdminEnabled()))));
        config.setDiagnosticsEnabled(Boolean.parseBoolean(
                System.getProperty(DIAGNOSTICS_ENABLED_PROPERTY, String.valueOf(config.isDiagnosticsEnabled()))));
        return config;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getUserIdFields() {
        return userIdFields;
    }

    public void setUserIdFields(List<String> userIdFields) {
        this.userIdFields = userIdFields;
    }

    public List<String> getAdminIdFields() {
        return adminIdFields;
    }

    public void setAdminIdFields(List<String> adminIdFields) {
        this.adminIdFields = adminIdFields;
    }

    public List<String> getOrgIdFields() {
        return orgIdFields;
    }

    public void setOrgIdFields(List<String> orgIdFields) {
        this.orgIdFields = orgIdFields;
    }

    public List<String> getRoleFields() {
        return roleFields;
    }

    public void setRoleFields(List<String> roleFields) {
        this.roleFields = roleFields;
    }

    public List<String> getAdminRoleAliases() {
        return adminRoleAliases;
    }

    public void setAdminRoleAliases(List<String> adminRoleAliases) {
        this.adminRoleAliases = adminRoleAliases;
    }

    public boolean isPlatformAdminEnabled() {
        return platformAdminEnabled;
    }

    public void setPlatformAdminEnabled(boolean platformAdminEnabled) {
        this.platformAdminEnabled = platformAdminEnabled;
    }

    public boolean isDiagnosticsEnabled() {
        return diagnosticsEnabled;
    }

    public void setDiagnosticsEnabled(boolean diagnosticsEnabled) {
        this.diagnosticsEnabled = diagnosticsEnabled;
    }

    private static List<String> list(String... values) {
        return Arrays.asList(values);
    }

    private static List<String> split(String value, List<String> fallback) {
        if (value == null || value.trim().length() == 0) {
            return fallback;
        }
        return Arrays.asList(value.split("\\s*[,;]\\s*"));
    }
}
