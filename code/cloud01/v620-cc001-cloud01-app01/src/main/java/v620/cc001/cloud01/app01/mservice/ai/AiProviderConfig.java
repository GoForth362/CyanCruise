package v620.cc001.cloud01.app01.mservice.ai;

import v620.cc001.base.common.dto.ai.AiConstants;

public class AiProviderConfig {

    public static final String PROP_ENABLED = "cc001.ai.provider.enabled";
    public static final String PROP_ENDPOINT = "cc001.ai.provider.endpoint";
    public static final String PROP_API_KEY = "cc001.ai.provider.apiKey";
    public static final String PROP_MODEL = "cc001.ai.provider.model";
    public static final String PROP_TIMEOUT_SECONDS = "cc001.ai.provider.timeoutSeconds";
    public static final String PROP_RETRY_ON_5XX = "cc001.ai.provider.retryOn5xx";
    public static final String PROP_DIAGNOSTICS_ENABLED = "cc001.ai.provider.diagnostics.enabled";
    public static final String PROP_PROVIDER_NAME = "cc001.ai.provider.name";

    private boolean enabled;
    private String endpoint;
    private String apiKey;
    private String modelName = AiConstants.DEFAULT_MODEL_NAME;
    private int timeoutSeconds = 60;
    private boolean retryOn5xx = true;
    private boolean diagnosticsEnabled;
    private String providerName = AiConstants.PROVIDER_OPENAI_COMPATIBLE;

    public static AiProviderConfig fromSystemProperties() {
        AiProviderConfig config = new AiProviderConfig();
        config.setEnabled(Boolean.parseBoolean(System.getProperty(PROP_ENABLED, "false")));
        config.setEndpoint(System.getProperty(PROP_ENDPOINT));
        config.setApiKey(System.getProperty(PROP_API_KEY));
        config.setModelName(System.getProperty(PROP_MODEL, AiConstants.DEFAULT_MODEL_NAME));
        config.setTimeoutSeconds(parseInt(System.getProperty(PROP_TIMEOUT_SECONDS), 60));
        config.setRetryOn5xx(Boolean.parseBoolean(System.getProperty(PROP_RETRY_ON_5XX, "true")));
        config.setDiagnosticsEnabled(Boolean.parseBoolean(System.getProperty(PROP_DIAGNOSTICS_ENABLED, "false")));
        config.setProviderName(System.getProperty(PROP_PROVIDER_NAME, AiConstants.PROVIDER_OPENAI_COMPATIBLE));
        return config;
    }

    public boolean isComplete() {
        return enabled && hasText(endpoint) && hasText(apiKey) && hasText(modelName);
    }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = hasText(modelName) ? modelName : AiConstants.DEFAULT_MODEL_NAME; }
    public int getTimeoutSeconds() { return timeoutSeconds; }
    public void setTimeoutSeconds(int timeoutSeconds) { this.timeoutSeconds = timeoutSeconds <= 0 ? 60 : timeoutSeconds; }
    public boolean isRetryOn5xx() { return retryOn5xx; }
    public void setRetryOn5xx(boolean retryOn5xx) { this.retryOn5xx = retryOn5xx; }
    public boolean isDiagnosticsEnabled() { return diagnosticsEnabled; }
    public void setDiagnosticsEnabled(boolean diagnosticsEnabled) { this.diagnosticsEnabled = diagnosticsEnabled; }
    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = hasText(providerName) ? providerName : AiConstants.PROVIDER_OPENAI_COMPATIBLE; }

    private static int parseInt(String value, int fallback) {
        try {
            return hasText(value) ? Integer.parseInt(value.trim()) : fallback;
        } catch (Exception ex) {
            return fallback;
        }
    }

    private static boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
