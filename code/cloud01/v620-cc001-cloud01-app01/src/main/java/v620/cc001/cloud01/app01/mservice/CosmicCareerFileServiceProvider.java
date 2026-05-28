package v620.cc001.cloud01.app01.mservice;

import v620.cc001.base.common.dto.career.FileDeleteResult;
import v620.cc001.base.common.dto.career.FileDownloadResult;
import v620.cc001.base.common.dto.career.FilePreviewUrlResult;
import v620.cc001.base.common.dto.career.FileReferenceDto;
import v620.cc001.base.common.dto.career.FileTextExtractionResult;

public interface CosmicCareerFileServiceProvider {

    String providerName();

    boolean available();

    FileReferenceDto upload(FileReferenceDto requestedReference, byte[] bytes);

    FilePreviewUrlResult previewUrl(String objectKey, long ttlSeconds);

    FileDownloadResult download(String objectKey);

    FileDeleteResult delete(String objectKey);

    boolean textExtractionAvailable();

    FileTextExtractionResult extractText(String objectKey, byte[] bytes);
}
