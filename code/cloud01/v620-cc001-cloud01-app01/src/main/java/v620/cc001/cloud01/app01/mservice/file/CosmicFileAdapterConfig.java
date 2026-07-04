package v620.cc001.cloud01.app01.mservice.file;

import v620.cc001.base.common.dto.career.FileConstants;

/**
 * Configuration for CyanCruise file operations backed by Cosmic file service.
 */
public class CosmicFileAdapterConfig {

    public static final String ENABLED_PROPERTY = "cc001.file.adapter.enabled";
    public static final String PROVIDER_NAME_PROPERTY = "cc001.file.adapter.provider";
    public static final String MAX_PREVIEW_TTL_PROPERTY = "cc001.file.adapter.preview.maxTtlSeconds";
    public static final String TEXT_EXTRACTION_MODE_PROPERTY = "cc001.file.adapter.textExtraction.mode";
    public static final String DIAGNOSTICS_ENABLED_PROPERTY = "cc001.file.adapter.diagnostics.enabled";

    private boolean enabled;
    private String providerName = FileConstants.PROVIDER_COSMIC;
    private long maxPreviewTtlSeconds = FileConstants.MAX_PREVIEW_TTL_SECONDS;
    private String textExtractionMode = "unavailable";
    private boolean diagnosticsEnabled = true;

    public static CosmicFileAdapterConfig disabled() {
        return new CosmicFileAdapterConfig();
    }

    public static CosmicFileAdapterConfig fromSystemProperties() {
        CosmicFileAdapterConfig config = new CosmicFileAdapterConfig();
        config.setEnabled(Boolean.parseBoolean(System.getProperty(ENABLED_PROPERTY, "false")));
        config.setProviderName(valueOrDefault(System.getProperty(PROVIDER_NAME_PROPERTY), config.getProviderName()));
        config.setMaxPreviewTtlSeconds(parseLong(System.getProperty(MAX_PREVIEW_TTL_PROPERTY),
                config.getMaxPreviewTtlSeconds()));
        config.setTextExtractionMode(valueOrDefault(System.getProperty(TEXT_EXTRACTION_MODE_PROPERTY),
                config.getTextExtractionMode()));
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

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = valueOrDefault(providerName, FileConstants.PROVIDER_COSMIC);
    }

    public long getMaxPreviewTtlSeconds() {
        return maxPreviewTtlSeconds;
    }

    public void setMaxPreviewTtlSeconds(long maxPreviewTtlSeconds) {
        this.maxPreviewTtlSeconds = Math.max(FileConstants.MIN_PREVIEW_TTL_SECONDS,
                Math.min(maxPreviewTtlSeconds, FileConstants.MAX_PREVIEW_TTL_SECONDS));
    }

    public String getTextExtractionMode() {
        return textExtractionMode;
    }

    public void setTextExtractionMode(String textExtractionMode) {
        this.textExtractionMode = valueOrDefault(textExtractionMode, "unavailable");
    }

    public boolean isDiagnosticsEnabled() {
        return diagnosticsEnabled;
    }

    public void setDiagnosticsEnabled(boolean diagnosticsEnabled) {
        this.diagnosticsEnabled = diagnosticsEnabled;
    }

    private static String valueOrDefault(String value, String fallback) {
        return value == null || value.trim().length() == 0 ? fallback : value.trim();
    }

    private static long parseLong(String value, long fallback) {
        if (value == null || value.trim().length() == 0) {
            return fallback;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }
}
