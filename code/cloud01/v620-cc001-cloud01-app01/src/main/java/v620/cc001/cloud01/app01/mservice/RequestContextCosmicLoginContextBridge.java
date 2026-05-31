package v620.cc001.cloud01.app01.mservice;

import kd.bos.context.RequestContext;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Reads the current Cosmic request context for WebAPI calls opened from the platform portal.
 */
public class RequestContextCosmicLoginContextBridge implements CosmicLoginContextBridge {

    public Map<String, Object> currentLoginContext() {
        Map<String, Object> context = new LinkedHashMap<String, Object>();
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
        } catch (Throwable ex) {
            context.put(PlatformCosmicIdentityContextProvider.DIAGNOSTIC_FIELD,
                    "RequestContext bridge failed: " + ex.getClass().getName());
        }
        return context;
    }

    private void putLong(Map<String, Object> context, String key, long value) {
        if (value > 0 && !context.containsKey(key)) {
            context.put(key, String.valueOf(value));
        }
    }

    private void put(Map<String, Object> context, String key, Object value) {
        if (value != null && String.valueOf(value).trim().length() > 0) {
            context.put(key, value);
        }
    }
}
