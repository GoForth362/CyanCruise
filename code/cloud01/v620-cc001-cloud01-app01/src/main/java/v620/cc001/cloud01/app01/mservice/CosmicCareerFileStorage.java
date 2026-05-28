package v620.cc001.cloud01.app01.mservice;

import v620.base.helper.career.FileUploadPreviewService;
import v620.cc001.base.common.dto.career.FileConstants;
import v620.cc001.base.common.dto.career.FileDeleteResult;
import v620.cc001.base.common.dto.career.FileDownloadResult;
import v620.cc001.base.common.dto.career.FilePreviewUrlResult;
import v620.cc001.base.common.dto.career.FileReferenceDto;

public class CosmicCareerFileStorage implements CareerFileStorage {

    private final CosmicCareerFileServiceProvider provider;
    private final CosmicFileAdapterConfig config;
    private final FileUploadPreviewService helper;

    public CosmicCareerFileStorage(CosmicCareerFileServiceProvider provider,
                                   CosmicFileAdapterConfig config,
                                   FileUploadPreviewService helper) {
        this.provider = provider == null ? new UnavailableCosmicCareerFileServiceProvider() : provider;
        this.config = config == null ? CosmicFileAdapterConfig.disabled() : config;
        this.helper = helper == null ? new FileUploadPreviewService() : helper;
    }

    public FileReferenceDto put(FileReferenceDto reference, byte[] bytes) {
        ensureAvailable();
        FileReferenceDto uploaded = provider.upload(reference, bytes);
        if (uploaded == null || !hasText(uploaded.getObjectKey())) {
            throw new FileAdapterUnavailableException(FileConstants.STATUS_UNAVAILABLE,
                    "cosmic file upload returned no object key");
        }
        uploaded.setObjectKey(helper.normalizeObjectKey(uploaded.getObjectKey()));
        uploaded.setProvider(providerName(uploaded.getProvider()));
        return uploaded;
    }

    public byte[] get(String objectKey) {
        ensureAvailable();
        FileDownloadResult result = provider.download(helper.normalizeObjectKey(objectKey));
        if (result == null || !FileConstants.STATUS_OK.equals(result.getStatus())) {
            throw unavailableFrom(result, "cosmic file download unavailable");
        }
        return result.getBytes();
    }

    public String presign(String objectKey, long ttlSeconds) {
        ensureAvailable();
        long ttl = Math.min(helper.clampTtl(ttlSeconds), config.getMaxPreviewTtlSeconds());
        FilePreviewUrlResult result = provider.previewUrl(helper.normalizeObjectKey(objectKey), ttl);
        if (result == null || !FileConstants.STATUS_OK.equals(result.getStatus())) {
            throw unavailableFrom(result, "cosmic preview url unavailable");
        }
        return result.getPreviewUrl();
    }

    public boolean delete(String objectKey) {
        ensureAvailable();
        FileDeleteResult result = provider.delete(helper.normalizeObjectKey(objectKey));
        if (result == null) {
            throw new FileAdapterUnavailableException(FileConstants.STATUS_UNAVAILABLE,
                    "cosmic delete result unavailable");
        }
        if (FileConstants.STATUS_OK.equals(result.getStatus()) || FileConstants.STATUS_SKIPPED.equals(result.getStatus())) {
            return Boolean.TRUE.equals(result.getDeleted());
        }
        throw new FileAdapterUnavailableException(result.getStatus(), sanitizeMessage(result.getMessage()));
    }

    public boolean previewAvailable() {
        return config.isEnabled() && provider.available();
    }

    public String providerName() {
        return provider.providerName();
    }

    private void ensureAvailable() {
        if (!config.isEnabled()) {
            throw new FileAdapterUnavailableException(FileConstants.STATUS_UNAVAILABLE,
                    "cosmic file adapter disabled");
        }
        if (!provider.available()) {
            throw new FileAdapterUnavailableException(FileConstants.STATUS_UNAVAILABLE,
                    "cosmic file provider unavailable");
        }
    }

    private FileAdapterUnavailableException unavailableFrom(FilePreviewUrlResult result, String fallback) {
        if (result == null) {
            return new FileAdapterUnavailableException(FileConstants.STATUS_UNAVAILABLE, fallback);
        }
        return new FileAdapterUnavailableException(valueOrDefault(result.getStatus(), FileConstants.STATUS_UNAVAILABLE),
                sanitizeMessage(result.getMessage()));
    }

    private FileAdapterUnavailableException unavailableFrom(FileDownloadResult result, String fallback) {
        if (result == null) {
            return new FileAdapterUnavailableException(FileConstants.STATUS_UNAVAILABLE, fallback);
        }
        return new FileAdapterUnavailableException(valueOrDefault(result.getStatus(), FileConstants.STATUS_UNAVAILABLE),
                sanitizeMessage(result.getMessage()));
    }

    private String providerName(String providerName) {
        return valueOrDefault(providerName, provider.providerName());
    }

    private String sanitizeMessage(String message) {
        String safe = valueOrDefault(message, "cosmic file service unavailable");
        int query = safe.indexOf('?');
        return query >= 0 ? safe.substring(0, query) : safe;
    }

    private String valueOrDefault(String value, String fallback) {
        return hasText(value) ? value.trim() : fallback;
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
