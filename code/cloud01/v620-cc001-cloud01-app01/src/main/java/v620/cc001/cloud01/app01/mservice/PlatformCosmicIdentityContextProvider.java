package v620.cc001.cloud01.app01.mservice;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PlatformCosmicIdentityContextProvider implements CosmicIdentityContextProvider {

    public static final String DIAGNOSTIC_FIELD = "_diagnostic";
    public static final String PROVIDER_FIELD = "_provider";

    private final CosmicLoginContextBridge bridge;
    private final CosmicLoginContextProviderConfig config;
    private String lastDiagnostic = "not invoked";

    public PlatformCosmicIdentityContextProvider(CosmicLoginContextBridge bridge,
                                                 CosmicLoginContextProviderConfig config) {
        this.bridge = bridge == null ? new UnavailableCosmicLoginContextBridge() : bridge;
        this.config = config == null ? CosmicLoginContextProviderConfig.disabled() : config;
    }

    public Map<String, Object> currentContext() {
        if (!config.isEnabled()) {
            return diagnostic("Cosmic login context provider disabled");
        }
        try {
            Map<String, Object> raw = bridge.currentLoginContext();
            if (raw == null || raw.isEmpty()) {
                return diagnostic("Cosmic login context bridge returned empty context");
            }
            Map<String, Object> normalized = normalize(raw);
            if (normalized.isEmpty()) {
                return diagnostic("Cosmic login context has no safe fields");
            }
            if (config.isDiagnosticsEnabled()) {
                normalized.put(DIAGNOSTIC_FIELD, "Cosmic login context provider resolved safe platform fields");
                normalized.put(PROVIDER_FIELD, config.getProviderName());
            }
            lastDiagnostic = "resolved";
            return normalized;
        } catch (Exception ex) {
            return diagnostic("Cosmic login context bridge failed: " + sanitize(ex.getClass().getSimpleName()));
        }
    }

    public String lastDiagnostic() {
        return lastDiagnostic;
    }

    private Map<String, Object> normalize(Map<String, Object> raw) {
        Map<String, Object> out = new LinkedHashMap<String, Object>();
        for (Map.Entry<String, Object> entry : raw.entrySet()) {
            String key = safeKey(entry.getKey());
            if (key == null || sensitive(key)) {
                continue;
            }
            Object value = normalizeValue(entry.getValue());
            if (value != null) {
                out.put(key, value);
            }
        }
        return out;
    }

    private Object normalizeValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Map) {
            Object role = roleValue((Map<?, ?>) value);
            return role == null ? null : role;
        }
        if (value instanceof Collection) {
            List<Object> out = new ArrayList<Object>();
            for (Object item : (Collection<?>) value) {
                Object normalized = normalizeCollectionItem(item);
                if (normalized != null) {
                    out.add(normalized);
                }
            }
            return out;
        }
        if (value.getClass().isArray()) {
            List<Object> out = new ArrayList<Object>();
            int length = java.lang.reflect.Array.getLength(value);
            for (int i = 0; i < length; i += 1) {
                Object normalized = normalizeCollectionItem(java.lang.reflect.Array.get(value, i));
                if (normalized != null) {
                    out.add(normalized);
                }
            }
            return out;
        }
        if (value instanceof CharSequence || value instanceof Number || value instanceof Boolean) {
            return String.valueOf(value).trim();
        }
        Object role = roleValue(value);
        return role == null ? null : role;
    }

    private Object normalizeCollectionItem(Object item) {
        if (item instanceof Map) {
            return roleValue((Map<?, ?>) item);
        }
        Object role = roleValue(item);
        if (role != null) {
            return role;
        }
        if (item instanceof CharSequence || item instanceof Number || item instanceof Boolean) {
            return String.valueOf(item).trim();
        }
        return null;
    }

    private Object roleValue(Map<?, ?> map) {
        for (String key : new String[]{"code", "roleCode", "number", "name", "id"}) {
            Object value = map.get(key);
            if (value != null && String.valueOf(value).trim().length() > 0) {
                return String.valueOf(value).trim();
            }
        }
        return null;
    }

    private Object roleValue(Object object) {
        if (object == null || object instanceof CharSequence || object instanceof Number || object instanceof Boolean) {
            return null;
        }
        for (String methodName : new String[]{"getCode", "getRoleCode", "getNumber", "getName", "getId"}) {
            try {
                Method method = object.getClass().getMethod(methodName);
                Object value = method.invoke(object);
                if (value != null && String.valueOf(value).trim().length() > 0) {
                    return String.valueOf(value).trim();
                }
            } catch (Exception ignored) {
                // Try the next conventional role accessor.
            }
        }
        return null;
    }

    private Map<String, Object> diagnostic(String message) {
        lastDiagnostic = sanitize(message);
        if (!config.isDiagnosticsEnabled()) {
            return Collections.emptyMap();
        }
        Map<String, Object> out = new LinkedHashMap<String, Object>();
        out.put(DIAGNOSTIC_FIELD, lastDiagnostic);
        out.put(PROVIDER_FIELD, config.getProviderName());
        return out;
    }

    private String safeKey(String key) {
        if (key == null) {
            return null;
        }
        String safe = key.trim();
        return safe.length() == 0 ? null : safe;
    }

    private boolean sensitive(String key) {
        String lower = key.toLowerCase(java.util.Locale.ENGLISH);
        return lower.contains("token")
                || lower.contains("secret")
                || lower.contains("password")
                || lower.contains("cookie")
                || lower.contains("session")
                || lower.contains("mobile")
                || lower.contains("phone")
                || lower.contains("email");
    }

    private String sanitize(String message) {
        if (message == null || message.trim().length() == 0) {
            return "Cosmic login context unavailable";
        }
        String safe = message.replaceAll("[\\r\\n\\t]+", " ").trim();
        safe = safe.replaceAll("(?i)(token|secret|password|cookie|session|mobile|phone|email)=[^\\s,;]+", "$1=<redacted>");
        return safe.length() > 160 ? safe.substring(0, 160) : safe;
    }
}
