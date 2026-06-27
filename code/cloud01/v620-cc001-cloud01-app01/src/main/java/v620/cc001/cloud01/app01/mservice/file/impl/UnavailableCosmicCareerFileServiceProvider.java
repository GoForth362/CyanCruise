package v620.cc001.cloud01.app01.mservice.file.impl;

import v620.cc001.cloud01.app01.mservice.file.*;
import v620.cc001.base.common.dto.career.FileConstants;
import v620.cc001.base.common.dto.career.FileDeleteResult;
import v620.cc001.base.common.dto.career.FileDownloadResult;
import v620.cc001.base.common.dto.career.FilePreviewUrlResult;
import v620.cc001.base.common.dto.career.FileReferenceDto;
import v620.cc001.base.common.dto.career.FileTextExtractionResult;

public class UnavailableCosmicCareerFileServiceProvider implements CosmicCareerFileServiceProvider {

    private final String providerName;
    private final String reason;

    public UnavailableCosmicCareerFileServiceProvider() {
        this(FileConstants.PROVIDER_UNAVAILABLE, "cosmic file provider unavailable");
    }

    public UnavailableCosmicCareerFileServiceProvider(String providerName, String reason) {
        this.providerName = providerName;
        this.reason = reason;
    }

    public String providerName() {
        return providerName;
    }

    public boolean available() {
        return false;
    }

    public FileReferenceDto upload(FileReferenceDto requestedReference, byte[] bytes) {
        throw unavailable();
    }

    public FilePreviewUrlResult previewUrl(String objectKey, long ttlSeconds) {
        FilePreviewUrlResult result = new FilePreviewUrlResult();
        result.setStatus(FileConstants.STATUS_UNAVAILABLE);
        result.setMessage(reason);
        result.setObjectKey(objectKey);
        result.setTtlSeconds(Long.valueOf(ttlSeconds));
        result.setProvider(providerName);
        return result;
    }

    public FileDownloadResult download(String objectKey) {
        FileDownloadResult result = new FileDownloadResult();
        result.setStatus(FileConstants.STATUS_UNAVAILABLE);
        result.setMessage(reason);
        result.setObjectKey(objectKey);
        result.setProvider(providerName);
        return result;
    }

    public FileDeleteResult delete(String objectKey) {
        FileDeleteResult result = new FileDeleteResult();
        result.setStatus(FileConstants.STATUS_UNAVAILABLE);
        result.setMessage(reason);
        result.setObjectKey(objectKey);
        result.setDeleted(Boolean.FALSE);
        result.setProvider(providerName);
        return result;
    }

    public boolean textExtractionAvailable() {
        return false;
    }

    public FileTextExtractionResult extractText(String objectKey, byte[] bytes) {
        FileTextExtractionResult result = new FileTextExtractionResult();
        result.setStatus(FileConstants.STATUS_UNAVAILABLE);
        result.setMessage(reason);
        result.setObjectKey(objectKey);
        result.setText("");
        result.setCharCount(Integer.valueOf(0));
        result.setTruncated(Boolean.FALSE);
        result.setProvider(providerName);
        return result;
    }

    private FileAdapterUnavailableException unavailable() {
        return new FileAdapterUnavailableException(FileConstants.STATUS_UNAVAILABLE, reason);
    }
}
