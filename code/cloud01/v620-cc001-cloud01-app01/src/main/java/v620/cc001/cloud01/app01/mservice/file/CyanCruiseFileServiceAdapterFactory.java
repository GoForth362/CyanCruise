package v620.cc001.cloud01.app01.mservice.file;

import v620.cc001.cloud01.app01.mservice.file.impl.BosAttachmentFileServiceProvider;
import v620.cc001.cloud01.app01.mservice.file.impl.CosmicCareerFileServiceProvider;
import v620.cc001.cloud01.app01.mservice.file.impl.CosmicCareerFileStorage;
import v620.cc001.cloud01.app01.mservice.file.impl.PdfBoxFileTextExtractor;
import v620.cc001.cloud01.app01.mservice.file.impl.UnavailableCosmicCareerFileServiceProvider;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerFileStorage;
import v620.cc001.cloud01.app01.mservice.application.FileUploadPreviewApplicationService;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerFileStorage;
import v620.base.helper.career.FileUploadPreviewService;

public final class CyanCruiseFileServiceAdapterFactory {

    private CyanCruiseFileServiceAdapterFactory() {
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
