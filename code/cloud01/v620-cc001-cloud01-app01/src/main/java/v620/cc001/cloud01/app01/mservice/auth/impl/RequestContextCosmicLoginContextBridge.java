package v620.cc001.cloud01.app01.mservice.auth.impl;

import v620.cc001.cloud01.app01.mservice.auth.*;
import kd.bos.context.RequestContext;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Reads the current Cosmic request context for WebAPI calls opened from the platform portal.
 */
public class RequestContextCosmicLoginContextBridge implements CosmicLoginContextBridge {

    private static final ThreadLocal<Map<String, Object>> PLATFORM_CONTEXT = new ThreadLocal<Map<String, Object>>();

    public static void setPlatformContext(Map<String, Object> context) {
        if (context == null || context.isEmpty()) {
            PLATFORM_CONTEXT.remove();
            return;
        }
        PLATFORM_CONTEXT.set(new LinkedHashMap<String, Object>(context));
    }

    public static void clearPlatformContext() {
        PLATFORM_CONTEXT.remove();
    }

    public Map<String, Object> currentLoginContext() {
        Map<String, Object> context = new LinkedHashMap<String, Object>();
        Map<String, Object> platformContext = PLATFORM_CONTEXT.get();
        if (platformContext != null) {
            context.putAll(platformContext);
        }
        try {
            RequestContext requestContext = RequestContext.get();
            if (requestContext == null) {
                return context;
            }
            put(context, "userId", requestContext.getUserId());
            putLong(context, "userId", requestContext.getCurrUserId());
            putLong(context, "operatorId", requestContext.getCurrUserId());
            put(context, "uid", requestContext.getUid());
            put(context, "userName", requestContext.getUserName());
            put(context, "orgId", requestContext.getOrgId());
            put(context, "tenantId", requestContext.getTenantId());
            put(context, "tenantCode", requestContext.getTenantCode());
            put(context, "ip", requestContext.getLoginIP());
            put(context, "userAgent", requestContext.getUserAgent());
            put(context, "api3rdAppId", requestContext.getApi3rdAppId());
            put(context, "authType", requestContext.getAuthType());
            putReflective(context, requestContext);
        } catch (Throwable ex) {
            context.put(PlatformCosmicIdentityContextProvider.DIAGNOSTIC_FIELD,
                    "RequestContext bridge failed: " + ex.getClass().getName());
        }
        return context;
    }

    private void putLong(Map<String, Object> context, String key, long value) {
        if (value > 0 && !hasUsableValue(context.get(key))) {
            context.put(key, String.valueOf(value));
        }
    }

    private void put(Map<String, Object> context, String key, Object value) {
        if (hasUsableValue(value) && !hasUsableValue(context.get(key))) {
            context.put(key, value);
        }
    }

    private void putReflective(Map<String, Object> context, RequestContext requestContext) {
        putByMethod(context, requestContext, "userId", "getUserId");
        putByMethod(context, requestContext, "currentUserId", "getCurrentUserId");
        putByMethod(context, requestContext, "currentUserId", "getCurrentUserID");
        putByMethod(context, requestContext, "currUserId", "getCurrUserId");
        putByMethod(context, requestContext, "loginUserId", "getLoginUserId");
        putByMethod(context, requestContext, "loginUserId", "getLoginUserID");
        putByMethod(context, requestContext, "operatorId", "getOperatorId");
        putByMethod(context, requestContext, "operatorId", "getOperatorID");
        putByMethod(context, requestContext, "personId", "getPersonId");
        putByMethod(context, requestContext, "personId", "getPersonID");
        putByMethod(context, requestContext, "uid", "getUid");
        putByMethod(context, requestContext, "uid", "getUID");
        putByMethod(context, requestContext, "userNumber", "getUserNumber");
        putByMethod(context, requestContext, "userNumber", "getUserNo");
        putByMethod(context, requestContext, "userName", "getUserName");
        putByMethod(context, requestContext, "displayName", "getDisplayName");
        putByMethod(context, requestContext, "displayName", "getUserDisplayName");
        putByMethod(context, requestContext, "nickName", "getNickName");
        putByMethod(context, requestContext, "personName", "getPersonName");
        putByMethod(context, requestContext, "operatorName", "getOperatorName");
        putByMethod(context, requestContext, "orgId", "getOrgId");
        putByMethod(context, requestContext, "orgId", "getOrgID");
        putByMethod(context, requestContext, "organizationId", "getOrganizationId");
        putByMethod(context, requestContext, "deptId", "getDeptId");
        putByMethod(context, requestContext, "roles", "getRoles");
        putByMethod(context, requestContext, "roleCodes", "getRoleCodes");
    }

    private void putByMethod(Map<String, Object> context, Object source, String key, String methodName) {
        try {
            Method method = source.getClass().getMethod(methodName);
            if (method.getParameterTypes().length != 0) {
                return;
            }
            Object value = method.invoke(source);
            put(context, key, value);
        } catch (Exception ignored) {
            // Different Cosmic builds expose different RequestContext accessors.
        }
    }

    private boolean hasUsableValue(Object value) {
        if (value == null) {
            return false;
        }
        String text = String.valueOf(value).trim();
        return text.length() > 0
                && !"0".equals(text)
                && !"null".equalsIgnoreCase(text)
                && !"undefined".equalsIgnoreCase(text);
    }
}
