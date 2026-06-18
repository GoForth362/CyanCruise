package v620.cc001.cloud01.app01.mservice;

import v620.base.helper.career.FileUploadPreviewService;
import v620.cc001.base.common.dto.career.FileConstants;
import v620.cc001.base.common.dto.career.FileDeleteResult;
import v620.cc001.base.common.dto.career.FileDownloadResult;
import v620.cc001.base.common.dto.career.FilePreviewUrlResult;
import v620.cc001.base.common.dto.career.FileReferenceDto;
import v620.cc001.base.common.dto.career.FileTextExtractionResult;
import v620.cc001.base.common.dto.career.FileUploadRequest;
import v620.cc001.base.common.dto.career.FileUploadResult;

import java.time.LocalDateTime;

/**
 * Application boundary for CyanCruise file upload, preview and text extraction.
 */
public class FileUploadPreviewApplicationService {

    private final CareerFileStorage storage;
    private final FileTextExtractor textExtractor;
    private final FileUploadPreviewService helper;

    public FileUploadPreviewApplicationService() {
        this(CareerLoopFileServiceAdapterFactory.production());
    }

    private FileUploadPreviewApplicationService(FileUploadPreviewApplicationService source) {
        this(source.storage, source.textExtractor, source.helper);
    }

    public FileUploadPreviewApplicationService(CareerFileStorage storage,
                                                FileTextExtractor textExtractor,
                                                FileUploadPreviewService helper) {
        this.storage = storage;
        this.textExtractor = textExtractor;
        this.helper = helper;
    }

    public FileUploadResult upload(FileUploadRequest request) {
        if (request == null) {
            return uploadFailure(FileConstants.STATUS_FILE_EMPTY, "file request is required");
        }
        try {
            FileReferenceDto reference = helper.buildReference(request.getFolder(), request.getOriginalFilename(), request.getBytes());
            reference = storage.put(reference, request.getBytes());
            FileUploadResult result = new FileUploadResult();
            result.setStatus(FileConstants.STATUS_OK);
            result.setMessage("uploaded");
            result.setFile(reference);
            return result;
        } catch (IllegalArgumentException ex) {
            return uploadFailure(ex.getMessage(), ex.getMessage());
        } catch (FileAdapterUnavailableException ex) {
            return uploadFailure(ex.getStatus(), ex.getMessage());
        } catch (Exception ex) {
            return uploadFailure(FileConstants.STATUS_FAILED, ex.toString());
        }
    }

    public FilePreviewUrlResult previewUrl(String fileUrlOrKey, long ttlSeconds) {
        FilePreviewUrlResult result = new FilePreviewUrlResult();
        if (fileUrlOrKey == null || fileUrlOrKey.trim().length() == 0) {
            result.setStatus(FileConstants.STATUS_SKIPPED);
            result.setMessage("blank file reference");
            return result;
        }
        try {
            String key = helper.normalizeObjectKey(fileUrlOrKey);
            long ttl = helper.clampTtl(ttlSeconds);
            result.setObjectKey(key);
            result.setTtlSeconds(Long.valueOf(ttl));
            result.setProvider(storage.providerName());
            String url = storage.presign(key, ttl);
            if (url == null || url.trim().length() == 0) {
                result.setStatus(FileConstants.STATUS_UNAVAILABLE);
                result.setMessage("preview url unavailable");
                return result;
            }
            result.setStatus(FileConstants.STATUS_OK);
            result.setMessage("preview ready");
            result.setPreviewUrl(url);
            result.setExpiresAt(LocalDateTime.now().plusSeconds(ttl));
            return result;
        } catch (IllegalArgumentException ex) {
            result.setStatus(FileConstants.STATUS_MALFORMED_REFERENCE);
            result.setMessage(ex.getMessage());
            return result;
        } catch (FileAdapterUnavailableException ex) {
            result.setStatus(ex.getStatus());
            result.setMessage(ex.getMessage());
            result.setProvider(storage.providerName());
            return result;
        }
    }

    public FileDownloadResult download(String fileUrlOrKey) {
        FileDownloadResult result = new FileDownloadResult();
        try {
            String key = helper.normalizeObjectKey(fileUrlOrKey);
            if (key == null) {
                result.setStatus(FileConstants.STATUS_SKIPPED);
                result.setMessage("blank file reference");
                return result;
            }
            byte[] bytes = storage.get(key);
            if (bytes == null) {
                result.setStatus(FileConstants.STATUS_FAILED);
                result.setMessage("file not found");
                result.setObjectKey(key);
                return result;
            }
            result.setStatus(FileConstants.STATUS_OK);
            result.setMessage("downloaded");
            result.setObjectKey(key);
            result.setBytes(bytes);
            result.setSizeBytes(Long.valueOf(bytes.length));
            result.setProvider(storage.providerName());
            return result;
        } catch (IllegalArgumentException ex) {
            result.setStatus(FileConstants.STATUS_MALFORMED_REFERENCE);
            result.setMessage(ex.getMessage());
            return result;
        } catch (FileAdapterUnavailableException ex) {
            result.setStatus(ex.getStatus());
            result.setMessage(ex.getMessage());
            result.setProvider(storage.providerName());
            return result;
        }
    }

    public FileDeleteResult delete(String fileUrlOrKey) {
        FileDeleteResult result = new FileDeleteResult();
        try {
            String key = helper.normalizeObjectKey(fileUrlOrKey);
            if (key == null) {
                result.setStatus(FileConstants.STATUS_SKIPPED);
                result.setMessage("blank file reference");
                result.setDeleted(Boolean.FALSE);
                return result;
            }
            boolean deleted = storage.delete(key);
            result.setStatus(FileConstants.STATUS_OK);
            result.setMessage(deleted ? "deleted" : "already absent");
            result.setObjectKey(key);
            result.setDeleted(Boolean.valueOf(deleted));
            result.setProvider(storage.providerName());
            return result;
        } catch (IllegalArgumentException ex) {
            result.setStatus(FileConstants.STATUS_MALFORMED_REFERENCE);
            result.setMessage(ex.getMessage());
            result.setDeleted(Boolean.FALSE);
            return result;
        } catch (FileAdapterUnavailableException ex) {
            result.setStatus(ex.getStatus());
            result.setMessage(ex.getMessage());
            result.setDeleted(Boolean.FALSE);
            result.setProvider(storage.providerName());
            return result;
        }
    }

    public FileTextExtractionResult extractText(String fileUrlOrKey) {
        FileDownloadResult download = download(fileUrlOrKey);
        if (!FileConstants.STATUS_OK.equals(download.getStatus())) {
            return helper.textResult(download.getObjectKey(), "", download.getStatus(), download.getMessage());
        }
        if (!textExtractor.available()) {
            return helper.textResult(download.getObjectKey(), "", FileConstants.STATUS_UNAVAILABLE, "text extractor unavailable");
        }
        try {
            String text = textExtractor.extract(download.getBytes(), download.getObjectKey());
            if (text == null || text.trim().length() == 0) {
                String status = isPdf(download.getObjectKey())
                        ? FileConstants.STATUS_TEXT_EMPTY
                        : FileConstants.STATUS_UNSUPPORTED;
                String message = isPdf(download.getObjectKey())
                        ? "PDF 中没有可读取的文字，可能是扫描件或纯图片文件"
                        : "当前文件类型不支持正文提取";
                return helper.textResult(download.getObjectKey(), "", status, message);
            }
            FileTextExtractionResult result = helper.textResult(download.getObjectKey(), text, FileConstants.STATUS_OK, "extracted");
            result.setProvider(download.getProvider());
            if (textExtractor instanceof CosmicFileTextExtractor) {
                result.setProvider(((CosmicFileTextExtractor) textExtractor).providerName());
            }
            return result;
        } catch (FileTextExtractionException ex) {
            return helper.textResult(download.getObjectKey(), "", ex.getStatus(), ex.getMessage());
        } catch (FileAdapterUnavailableException ex) {
            return helper.textResult(download.getObjectKey(), "", ex.getStatus(), ex.getMessage());
        } catch (Exception ex) {
            return helper.textResult(download.getObjectKey(), "", FileConstants.STATUS_FAILED, ex.toString());
        }
    }

    private boolean isPdf(String objectKey) {
        return objectKey != null && objectKey.toLowerCase().endsWith(".pdf");
    }

    private FileUploadResult uploadFailure(String status, String message) {
        FileUploadResult result = new FileUploadResult();
        result.setStatus(status);
        result.setMessage(message);
        return result;
    }
}
