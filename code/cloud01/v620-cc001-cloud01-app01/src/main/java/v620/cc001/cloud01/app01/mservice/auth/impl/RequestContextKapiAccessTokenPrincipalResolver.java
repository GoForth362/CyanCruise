package v620.cc001.cloud01.app01.mservice.auth.impl;

import v620.cc001.cloud01.app01.mservice.auth.*;
import kd.bos.context.RequestContext;

import java.lang.reflect.Method;

/**
 * Resolves KAPI token principal from the current Cosmic request context.
 */
public class RequestContextKapiAccessTokenPrincipalResolver implements KapiAccessTokenPrincipalResolver {

    public KapiAccessTokenPrincipal resolve(KapiAccessTokenConfig config) {
        String username = null;
        String accountId = null;
        try {
            RequestContext context = RequestContext.get();
            if (context != null) {
                username = firstText(
                        invoke(context, "getUserNumber"),
                        invoke(context, "getUserNo"),
                        invoke(context, "getUserCode"),
                        invoke(context, "getUserName"),
                        invoke(context, "getUserId"),
                        String.valueOf(context.getUserId()));
                accountId = firstText(
                        invoke(context, "getAccountId"),
                        invoke(context, "getAccountID"),
                        invoke(context, "getAcctId"),
                        invoke(context, "getAcctID"),
                        invoke(context, "getDataCenterId"),
                        invoke(context, "getDataCenterID"),
                        invoke(context, "getDcId"),
                        invoke(context, "getTenantId"),
                        context.getTenantId());
            }
        } catch (Throwable ignored) {
            // Fallback to configured proxy values below.
        }
        username = firstText(username, config == null ? null : config.getUsername());
        accountId = firstText(accountId, config == null ? null : config.getAccountId());
        return new KapiAccessTokenPrincipal(username, accountId,
                hasText(username) && hasText(accountId) ? "cosmic-request-context-or-config" : "missing-principal");
    }

    private String invoke(Object source, String methodName) {
        try {
            Method method = source.getClass().getMethod(methodName);
            if (method.getParameterTypes().length != 0) {
                return null;
            }
            Object value = method.invoke(source);
            return value == null ? null : String.valueOf(value);
        } catch (Exception ignored) {
            return null;
        }
    }

    private String firstText(String... values) {
        for (String value : values) {
            if (hasText(value) && !"0".equals(value.trim())) {
                return value.trim();
            }
        }
        return null;
    }

    private static boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
