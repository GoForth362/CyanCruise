package v620.cc001.cloud01.app01.mservice;

public class CosmicLoginContextProviderConfig {

    public static final String ENABLED_PROPERTY = "cc001.identity.login.provider.enabled";
    public static final String BRIDGE_CLASS_PROPERTY = "cc001.identity.login.provider.bridgeClass";
    public static final String PROVIDER_NAME_PROPERTY = "cc001.identity.login.provider.name";
    public static final String DIAGNOSTICS_ENABLED_PROPERTY = "cc001.identity.login.provider.diagnostics.enabled";

    private boolean enabled;
    private String bridgeClassName;
    private String providerName = "cosmic-login-context";
    private boolean diagnosticsEnabled = true;

    public static CosmicLoginContextProviderConfig disabled() {
        return new CosmicLoginContextProviderConfig();
    }

    public static CosmicLoginContextProviderConfig fromSystemProperties() {
        CosmicLoginContextProviderConfig config = new CosmicLoginContextProviderConfig();
        config.setEnabled(Boolean.parseBoolean(System.getProperty(ENABLED_PROPERTY, "false")));
        config.setBridgeClassName(System.getProperty(BRIDGE_CLASS_PROPERTY));
        config.setProviderName(System.getProperty(PROVIDER_NAME_PROPERTY, config.getProviderName()));
        config.setDiagnosticsEnabled(Boolean.parseBoolean(System.getProperty(DIAGNOSTICS_ENABLED_PROPERTY,
                String.valueOf(config.isDiagnosticsEnabled()))));
        return config;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getBridgeClassName() {
        return bridgeClassName;
    }

    public void setBridgeClassName(String bridgeClassName) {
        this.bridgeClassName = trimToNull(bridgeClassName);
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        String safe = trimToNull(providerName);
        this.providerName = safe == null ? "cosmic-login-context" : safe;
    }

    public boolean isDiagnosticsEnabled() {
        return diagnosticsEnabled;
    }

    public void setDiagnosticsEnabled(boolean diagnosticsEnabled) {
        this.diagnosticsEnabled = diagnosticsEnabled;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.length() == 0 ? null : trimmed;
    }
}
