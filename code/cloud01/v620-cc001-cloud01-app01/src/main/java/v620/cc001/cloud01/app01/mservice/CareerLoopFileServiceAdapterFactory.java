package v620.cc001.cloud01.app01.mservice;

import v620.base.helper.career.FileUploadPreviewService;

public final class CareerLoopFileServiceAdapterFactory {

    private CareerLoopFileServiceAdapterFactory() {
    }

    public static FileUploadPreviewApplicationService production() {
        CosmicFileAdapterConfig config = CosmicFileAdapterConfig.fromSystemProperties();
        CosmicCareerFileServiceProvider provider = config.isEnabled()
                ? new BosAttachmentFileServiceProvider()
                : new UnavailableCosmicCareerFileServiceProvider();
        return production(provider, config, new FileUploadPreviewService());
    }

    public static FileUploadPreviewApplicationService production(CosmicCareerFileServiceProvider provider,
                                                                 CosmicFileAdapterConfig config,
                                                                 FileUploadPreviewService helper) {
        CosmicCareerFileStorage storage = new CosmicCareerFileStorage(provider, config, helper);
        PdfBoxFileTextExtractor extractor = new PdfBoxFileTextExtractor();
        return new FileUploadPreviewApplicationService(storage, extractor, helper);
    }

    public static FileUploadPreviewApplicationService localInMemory() {
        return new FileUploadPreviewApplicationService(new InMemoryCareerFileStorage(),
                new PdfBoxFileTextExtractor(),
                new FileUploadPreviewService());
    }
}
