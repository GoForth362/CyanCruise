package v620.cc001.cloud01.app01.mservice;

import v620.base.helper.career.FileUploadPreviewService;

public final class CareerLoopFileServiceAdapterFactory {

    private CareerLoopFileServiceAdapterFactory() {
    }

    public static FileUploadPreviewApplicationService production() {
        return production(new UnavailableCosmicCareerFileServiceProvider(),
                CosmicFileAdapterConfig.fromSystemProperties(),
                new FileUploadPreviewService());
    }

    public static FileUploadPreviewApplicationService production(CosmicCareerFileServiceProvider provider,
                                                                 CosmicFileAdapterConfig config,
                                                                 FileUploadPreviewService helper) {
        CosmicCareerFileStorage storage = new CosmicCareerFileStorage(provider, config, helper);
        CosmicFileTextExtractor extractor = new CosmicFileTextExtractor(provider, config);
        return new FileUploadPreviewApplicationService(storage, extractor, helper);
    }

    public static FileUploadPreviewApplicationService localInMemory() {
        return new FileUploadPreviewApplicationService(new InMemoryCareerFileStorage(),
                new PlainTextFileTextExtractor(),
                new FileUploadPreviewService());
    }
}
