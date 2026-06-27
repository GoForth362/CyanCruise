package v620.cc001.cloud01.app01.mservice.auth;

import java.util.Locale;

/**
 * Runtime configuration for server-managed Cosmic KAPI AccessToken acquisition.
 */
public class KapiAccessTokenConfig {

    public static final String ENABLED_PROPERTY = "cc001.kapi.token.enabled";
    public static final String ENDPOINT_PROPERTY = "cc001.kapi.token.endpoint";
    public static final String CLIENT_ID_PROPERTY = "cc001.kapi.token.clientId";
    public static final String CLIENT_SECRET_PROPERTY = "cc001.kapi.token.clientSecret";
    public static final String USERNAME_PROPERTY = "cc001.kapi.token.username";
    public static final String ACCOUNT_ID_PROPERTY = "cc001.kapi.token.accountId";
    public static final String LANGUAGE_PROPERTY = "cc001.kapi.token.language";
    public static final String TIMEOUT_SECONDS_PROPERTY = "cc001.kapi.token.timeoutSeconds";
    public static final String CACHE_SECONDS_PROPERTY = "cc001.kapi.token.cacheSeconds";
    public static final String EXPIRY_SKEW_SECONDS_PROPERTY = "cc001.kapi.token.expirySkewSeconds";

    private boolean enabled = true;
    private String endpoint;
    private String clientId;
    private String clientSecret;
    private String username;
    private String accountId;
    private String language = "zh_CN";
    private int timeoutSeconds = 10;
    private int cacheSeconds = 3300;
    private int expirySkewSeconds = 60;

    public static KapiAccessTokenConfig fromSystemProperties() {
        KapiAccessTokenConfig config = new KapiAccessTokenConfig();
        config.setEnabled(Boolean.parseBoolean(configuredValue(ENABLED_PROPERTY, "CC001_KAPI_TOKEN_ENABLED", "true")));
        config.setEndpoint(configuredValue(ENDPOINT_PROPERTY, "CC001_KAPI_TOKEN_ENDPOINT", null));
        config.setClientId(configuredValue(CLIENT_ID_PROPERTY, "CC001_KAPI_TOKEN_CLIENT_ID", null));
        config.setClientSecret(configuredValue(CLIENT_SECRET_PROPERTY, "CC001_KAPI_TOKEN_CLIENT_SECRET", null));
        config.setUsername(configuredValue(USERNAME_PROPERTY, "CC001_KAPI_TOKEN_USERNAME", null));
        config.setAccountId(configuredValue(ACCOUNT_ID_PROPERTY, "CC001_KAPI_TOKEN_ACCOUNT_ID", null));
        config.setLanguage(configuredValue(LANGUAGE_PROPERTY, "CC001_KAPI_TOKEN_LANGUAGE", config.getLanguage()));
        config.setTimeoutSeconds(parseInt(configuredValue(TIMEOUT_SECONDS_PROPERTY,
                "CC001_KAPI_TOKEN_TIMEOUT_SECONDS", null), config.getTimeoutSeconds()));
        config.setCacheSeconds(parseInt(configuredValue(CACHE_SECONDS_PROPERTY,
                "CC001_KAPI_TOKEN_CACHE_SECONDS", null), config.getCacheSeconds()));
        config.setExpirySkewSeconds(parseInt(configuredValue(EXPIRY_SKEW_SECONDS_PROPERTY,
                "CC001_KAPI_TOKEN_EXPIRY_SKEW_SECONDS", null), config.getExpirySkewSeconds()));
        return config;
    }

    public boolean isAvailable() {
        return enabled
                && hasText(endpoint)
                && hasText(clientId)
                && hasText(clientSecret);
    }

    public String missingReason() {
        if (!enabled) {
            return "server-managed KAPI token is disabled";
        }
        StringBuilder missing = new StringBuilder();
        appendMissing(missing, endpoint, "endpoint");
        appendMissing(missing, clientId, "clientId");
        appendMissing(missing, clientSecret, "clientSecret");
        return missing.length() == 0 ? "" : "missing " + missing;
    }

    private static void appendMissing(StringBuilder out, String value, String name) {
        if (hasText(value)) {
            return;
        }
        if (out.length() > 0) {
            out.append(',');
        }
        out.append(name);
    }

    private static String configuredValue(String property, String env, String fallback) {
        String value = System.getProperty(property);
        if (!hasText(value)) {
            value = System.getenv(env);
        }
        return hasText(value) ? value.trim() : fallback;
    }

    private static int parseInt(String value, int fallback) {
        if (!hasText(value)) {
            return fallback;
        }
        try {
            return Math.max(1, Integer.parseInt(value.trim()));
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }

    private static boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = trimToNull(endpoint);
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = trimToNull(clientId);
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = trimToNull(clientSecret);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = trimToNull(username);
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = trimToNull(accountId);
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        String safe = trimToNull(language);
        this.language = safe == null ? "zh_CN" : safe;
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = Math.max(1, timeoutSeconds);
    }

    public int getCacheSeconds() {
        return cacheSeconds;
    }

    public void setCacheSeconds(int cacheSeconds) {
        this.cacheSeconds = Math.max(1, cacheSeconds);
    }

    public int getExpirySkewSeconds() {
        return expirySkewSeconds;
    }

    public void setExpirySkewSeconds(int expirySkewSeconds) {
        this.expirySkewSeconds = Math.max(0, expirySkewSeconds);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.length() == 0 || "null".equals(trimmed.toLowerCase(Locale.ROOT)) ? null : trimmed;
    }
}
