package v620.cc001.cloud01.app01.mservice;

import v620.cc001.base.common.dto.career.CosmicIdentityConstants;
import v620.cc001.base.common.dto.career.CosmicIdentityContextDto;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Production adapter that resolves identity from a provider-supplied platform context map.
 */
public class ConfigurableCosmicIdentityResolver implements CareerLoopIdentityResolver {

    private final CosmicIdentityContextProvider provider;
    private final CosmicIdentityAdapterConfig config;

    public ConfigurableCosmicIdentityResolver(CosmicIdentityContextProvider provider,
                                              CosmicIdentityAdapterConfig config) {
        this.provider = provider == null ? new EmptyCosmicIdentityContextProvider() : provider;
        this.config = config == null ? CosmicIdentityAdapterConfig.disabled() : config;
    }

    public CosmicIdentityContextDto resolve() {
        if (!config.isEnabled()) {
            return CosmicIdentityContextDto.identityRequired("Cosmic identity adapter is disabled");
        }
        Map<String, Object> context = provider.currentContext();
        if (context == null || context.isEmpty()) {
            return CosmicIdentityContextDto.identityRequired("Cosmic platform context is empty");
        }
        String userId = firstText(context, config.getUserIdFields());
        String adminId = firstText(context, config.getAdminIdFields());
        if (!hasText(userId) && !hasText(adminId)) {
            return CosmicIdentityContextDto.identityRequired("Cosmic platform context has no configured identity fields");
        }
        CosmicIdentityContextDto identity = new CosmicIdentityContextDto();
        identity.setUserId(userId);
        identity.setAdminId(adminId);
        identity.setOrgId(firstText(context, config.getOrgIdFields()));
        identity.setRoles(resolveRoles(context));
        identity.setIp(text(context.get("ip")));
        identity.setUserAgent(firstText(context, java.util.Arrays.asList("userAgent", "ua")));
        identity.setSource(CosmicIdentityConstants.SOURCE_COSMIC_PLATFORM_CONTEXT);
        identity.setEnvironment(CosmicIdentityConstants.ENV_PRODUCTION);
        identity.setStatus(CosmicIdentityConstants.STATUS_OK);
        if (config.isDiagnosticsEnabled()) {
            identity.setMessage("Cosmic identity adapter resolved identity from configured platform context");
        }
        return identity;
    }

    private List<String> resolveRoles(Map<String, Object> context) {
        List<String> roles = new ArrayList<String>();
        for (String field : config.getRoleFields()) {
            Object value = context.get(field);
            appendRoles(roles, value);
        }
        return roles;
    }

    private String firstText(Map<String, Object> context, List<String> fields) {
        if (fields == null) {
            return "";
        }
        for (String field : fields) {
            String value = text(context.get(field));
            if (hasText(value)) {
                return value;
            }
        }
        return "";
    }

    private void appendRoles(List<String> roles, Object value) {
        if (value == null) {
            return;
        }
        if (value instanceof Collection) {
            for (Object item : (Collection<?>) value) {
                appendRole(roles, text(item));
            }
            return;
        }
        if (value.getClass().isArray()) {
            int length = Array.getLength(value);
            for (int i = 0; i < length; i += 1) {
                appendRole(roles, text(Array.get(value, i)));
            }
            return;
        }
        String raw = text(value);
        if (raw.indexOf(',') >= 0 || raw.indexOf(';') >= 0) {
            String[] parts = raw.split("[,;]");
            for (String part : parts) {
                appendRole(roles, part);
            }
        } else {
            appendRole(roles, raw);
        }
    }

    private void appendRole(List<String> roles, String role) {
        String safe = role == null ? "" : role.trim();
        if (safe.length() > 0 && !roles.contains(safe)) {
            roles.add(safe);
        }
        if (isAdminAlias(safe) && !roles.contains(CosmicIdentityConstants.ROLE_ADMIN)) {
            roles.add(CosmicIdentityConstants.ROLE_ADMIN);
        }
    }

    private String text(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }

    private boolean isAdminAlias(String role) {
        String normalizedRole = normalizeRole(role);
        if (!hasText(normalizedRole)) {
            return false;
        }
        for (String alias : config.getAdminRoleAliases()) {
            if (normalizedRole.equals(normalizeRole(alias))) {
                return true;
            }
        }
        return false;
    }

    private String normalizeRole(String role) {
        if (!hasText(role)) {
            return "";
        }
        return role.trim().replace('-', '_').toUpperCase(Locale.ENGLISH);
    }
}
