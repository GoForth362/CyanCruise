package v620.base.helper.career;

import v620.cc001.base.common.dto.career.CosmicIdentityConstants;
import v620.cc001.base.common.dto.career.CosmicIdentityContextDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Pure identity rules shared by web APIs and tests.
 */
public class CosmicIdentityContextHelper {

    public List<String> normalizeRoles(List<String> roles) {
        List<String> normalized = new ArrayList<String>();
        if (roles == null) {
            return normalized;
        }
        for (String role : roles) {
            String safe = normalizeRole(role);
            if (safe.length() > 0 && !normalized.contains(safe)) {
                normalized.add(safe);
            }
        }
        return normalized;
    }

    public boolean isDevelopmentFallback(CosmicIdentityContextDto context) {
        return context != null
                && CosmicIdentityConstants.ENV_DEVELOPMENT.equals(context.getEnvironment())
                && CosmicIdentityConstants.SOURCE_DEVELOPMENT_FALLBACK.equals(context.getSource());
    }

    public boolean isOk(CosmicIdentityContextDto context) {
        return context != null && CosmicIdentityConstants.STATUS_OK.equals(context.getStatus());
    }

    public boolean hasAdminRole(CosmicIdentityContextDto context) {
        if (context == null) {
            return false;
        }
        List<String> roles = normalizeRoles(context.getRoles());
        return roles.contains(CosmicIdentityConstants.ROLE_ADMIN)
                || roles.contains(CosmicIdentityConstants.ROLE_COSMIC_ADMIN)
                || roles.contains(CosmicIdentityConstants.ROLE_PLATFORM_ADMIN);
    }

    public String userStatus(CosmicIdentityContextDto context, String explicitUserId) {
        if (!isOk(context) || !hasText(context.getUserId())) {
            return CosmicIdentityConstants.STATUS_IDENTITY_REQUIRED;
        }
        if (hasText(explicitUserId) && !same(context.getUserId(), explicitUserId)) {
            return CosmicIdentityConstants.STATUS_IDENTITY_MISMATCH;
        }
        return CosmicIdentityConstants.STATUS_OK;
    }

    public String adminStatus(CosmicIdentityContextDto context, String explicitAdminId) {
        if (!isOk(context) || (!hasText(context.getAdminId()) && !hasText(context.getUserId()))) {
            return CosmicIdentityConstants.STATUS_IDENTITY_REQUIRED;
        }
        if (!hasAdminRole(context)) {
            return CosmicIdentityConstants.STATUS_FORBIDDEN;
        }
        String resolved = hasText(context.getAdminId()) ? context.getAdminId() : context.getUserId();
        if (hasText(explicitAdminId) && !same(resolved, explicitAdminId)) {
            return CosmicIdentityConstants.STATUS_IDENTITY_MISMATCH;
        }
        return CosmicIdentityConstants.STATUS_OK;
    }

    public String resolvedUserId(CosmicIdentityContextDto context) {
        return context == null ? null : trim(context.getUserId());
    }

    public String resolvedAdminId(CosmicIdentityContextDto context) {
        if (context == null) {
            return null;
        }
        return hasText(context.getAdminId()) ? trim(context.getAdminId()) : trim(context.getUserId());
    }

    private String normalizeRole(String role) {
        return trim(role).replace('-', '_').toUpperCase(Locale.ENGLISH);
    }

    private boolean same(String left, String right) {
        return trim(left).equals(trim(right));
    }

    private static boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }

    private static String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
