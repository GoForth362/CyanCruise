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
            if (isAdminByPermissionService(candidate.longValue())
                    || isSuperUserByPermissionService(candidate.longValue())
                    || hasAdminTypeByPermissionService(candidate.longValue())
                    || isAdminByAdminGroup(candidate)
                    || isAdminByAdminGroupMembership(candidate)) {
                return true;
            }
        }
        return false;
    }

    boolean isAdminByPermissionService(long userId) {
        try {
            Class<?> factoryClass = loadClass(PERMISSION_SERVICE_FACTORY);
            Class<?> serviceClass = loadClass(PERMISSION_SERVICE);
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

    boolean isSuperUserByPermissionService(long userId) {
        return invokePermissionBoolean("isSuperUser", userId);
    }

    boolean hasAdminTypeByPermissionService(long userId) {
        try {
            Object service = permissionService();
            if (service == null) {
                return false;
            }
            Method getAdminType = loadClass(PERMISSION_SERVICE).getMethod("getAdminType", long.class);
            Object value = getAdminType.invoke(service, Long.valueOf(userId));
            String type = value == null ? "" : String.valueOf(value).trim();
            return type.length() > 0 && !"NotAdmin".equalsIgnoreCase(type);
        } catch (Throwable ignored) {
            return false;
        }
    }

    boolean isAdminByAdminGroup(Long userId) {
        return hasAdminGroupMembership("getAdminGrpIdsByUser", userId);
    }

    boolean isAdminByAdminGroupMembership(Long userId) {
        return hasAdminGroupMembership("getUserInAdmGrpIdSet", userId);
    }

    private boolean invokePermissionBoolean(String methodName, long userId) {
        try {
            Object service = permissionService();
            if (service == null) {
                return false;
            }
            Method method = loadClass(PERMISSION_SERVICE).getMethod(methodName, long.class);
            return Boolean.TRUE.equals(method.invoke(service, Long.valueOf(userId)));
        } catch (Throwable ignored) {
            return false;
        }
    }

    private Object permissionService() {
        try {
            Class<?> factoryClass = loadClass(PERMISSION_SERVICE_FACTORY);
            Class<?> serviceClass = loadClass(PERMISSION_SERVICE);
            Method getService = factoryClass.getMethod("getService", Class.class);
            return getService.invoke(null, serviceClass);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private boolean hasAdminGroupMembership(String methodName, Long userId) {
        try {
            Class<?> serviceClass = loadClass(ADMIN_GROUP_SERVICE);
            Method method = serviceClass.getMethod(methodName, Long.class);
            Object value = method.invoke(null, userId);
            return value instanceof Collection && !((Collection<?>) value).isEmpty();
        } catch (Throwable ignored) {
            return false;
        }
    }

    private Class<?> loadClass(String className) throws ClassNotFoundException {
        ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        if (contextLoader != null) {
            try {
                return Class.forName(className, true, contextLoader);
            } catch (ClassNotFoundException ignored) {
                // Fall through to the application class loader.
            }
        }
        return Class.forName(className);
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
