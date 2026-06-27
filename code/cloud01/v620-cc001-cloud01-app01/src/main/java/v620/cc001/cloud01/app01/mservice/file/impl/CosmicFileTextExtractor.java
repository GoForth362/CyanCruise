package v620.cc001.cloud01.app01.mservice.file.impl;

import v620.cc001.cloud01.app01.mservice.file.*;
import v620.cc001.base.common.dto.career.FileConstants;
import v620.cc001.base.common.dto.career.FileTextExtractionResult;

public class CosmicFileTextExtractor implements FileTextExtractor {

    private final CosmicCareerFileServiceProvider provider;
    private final CosmicFileAdapterConfig config;
    private String lastProviderName = FileConstants.PROVIDER_UNAVAILABLE;

    public CosmicFileTextExtractor(CosmicCareerFileServiceProvider provider, CosmicFileAdapterConfig config) {
        this.provider = provider == null ? new UnavailableCosmicCareerFileServiceProvider() : provider;
        this.config = config == null ? CosmicFileAdapterConfig.disabled() : config;
    }

    public String extract(byte[] bytes, String objectKey) {
        if (!available()) {
            throw new FileAdapterUnavailableException(FileConstants.STATUS_UNAVAILABLE,
                    "cosmic text extractor unavailable");
        }
        FileTextExtractionResult result = provider.extractText(objectKey, bytes);
        if (result == null || !FileConstants.STATUS_OK.equals(result.getStatus())) {
            throw new FileAdapterUnavailableException(result == null ? FileConstants.STATUS_UNAVAILABLE : result.getStatus(),
                    result == null ? "cosmic text extraction unavailable" : result.getMessage());
        }
        lastProviderName = result.getProvider() == null ? provider.providerName() : result.getProvider();
        return result.getText();
    }

    public boolean available() {
        return config.isEnabled() && provider.available() && provider.textExtractionAvailable();
    }

    public String providerName() {
        return lastProviderName == null ? provider.providerName() : lastProviderName;
    }
}
