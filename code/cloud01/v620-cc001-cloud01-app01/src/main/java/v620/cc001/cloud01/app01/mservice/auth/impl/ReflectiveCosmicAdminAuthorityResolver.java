package v620.cc001.cloud01.app01.mservice.auth.impl;

import v620.cc001.cloud01.app01.mservice.auth.CosmicAdminAuthorityResolver;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Uses Cosmic permission APIs reflectively so CyanCruise can run across
 * slightly different platform library layouts.
 */
public class ReflectiveCosmicAdminAuthorityResolver implements CosmicAdminAuthorityResolver {

    private static final String PERMISSION_SERVICE_FACTORY = "kd.bos.permission.factory.PermServiceFactory";
    private static final String PERMISSION_SERVICE = "kd.bos.permission.api.PermissionService";
    private static final String ADMIN_GROUP_SERVICE = "kd.bos.permission.service.AdminGroupService";

    public boolean isAdmin(String userId, String adminId) {
        for (Long candidate : candidateIds(userId, adminId)) {
            if (isAdminByPermissionService(candidate.longValue()) || isAdminByAdminGroup(candidate)) {
                return true;
            }
        }
        return false;
    }

    private boolean isAdminByPermissionService(long userId) {
        try {
            Class<?> factoryClass = Class.forName(PERMISSION_SERVICE_FACTORY);
            Class<?> serviceClass = Class.forName(PERMISSION_SERVICE);
            Method getService = factoryClass.getMethod("getService", Class.class);
            Object service = getService.invoke(null, serviceClass);
            if (service == null) {
                return false;
            }
            Method isAdminUser = serviceClass.getMethod("isAdminUser", long.class);
            Object value = isAdminUser.invoke(service, Long.valueOf(userId));
            return Boolean.TRUE.equals(value);
        } catch (Throwable ignored) {
            return false;
        }
    }

    private boolean isAdminByAdminGroup(Long userId) {
        try {
            Class<?> serviceClass = Class.forName(ADMIN_GROUP_SERVICE);
            Method method = serviceClass.getMethod("getAdminGrpIdsByUser", Long.class);
            Object value = method.invoke(null, userId);
            return value instanceof Collection && !((Collection<?>) value).isEmpty();
        } catch (Throwable ignored) {
            return false;
        }
    }

    private Set<Long> candidateIds(String userId, String adminId) {
        Set<Long> ids = new LinkedHashSet<Long>();
        addId(ids, adminId);
        addId(ids, userId);
        return ids;
    }

    private void addId(Set<Long> ids, String value) {
        if (value == null) {
            return;
        }
        String safe = value.trim();
        if (safe.length() == 0) {
            return;
        }
        try {
            ids.add(Long.valueOf(Long.parseLong(safe)));
        } catch (NumberFormatException ignored) {
            // Platform permission APIs use long user ids; non-numeric ids cannot be checked here.
        }
    }
}
