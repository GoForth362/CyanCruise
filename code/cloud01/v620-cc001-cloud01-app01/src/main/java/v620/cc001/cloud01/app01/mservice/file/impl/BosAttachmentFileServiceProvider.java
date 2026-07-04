package v620.cc001.cloud01.app01.mservice.file.impl;

import v620.cc001.cloud01.app01.mservice.file.*;
import v620.cc001.base.common.dto.career.FileConstants;
import v620.cc001.base.common.dto.career.FileDeleteResult;
import v620.cc001.base.common.dto.career.FileDownloadResult;
import v620.cc001.base.common.dto.career.FilePreviewUrlResult;
import v620.cc001.base.common.dto.career.FileReferenceDto;
import v620.cc001.base.common.dto.career.FileTextExtractionResult;

import java.net.URLEncoder;
import java.util.Map;

public class BosAttachmentFileServiceProvider implements CosmicCareerFileServiceProvider {

    private final BosFileServiceClient client;
    private final String providerName;

    public BosAttachmentFileServiceProvider() {
        this(new DefaultBosFileServiceClient(), FileConstants.PROVIDER_COSMIC);
    }

    BosAttachmentFileServiceProvider(BosFileServiceClient client, String providerName) {
        this.client = client == null ? new DefaultBosFileServiceClient() : client;
        this.providerName = hasText(providerName) ? providerName.trim() : FileConstants.PROVIDER_COSMIC;
    }

    public String providerName() {
        return providerName;
    }

    public boolean available() {
        try {
            return client.available();
        } catch (Exception ex) {
            return false;
        }
    }

    public FileReferenceDto upload(FileReferenceDto requestedReference, byte[] bytes) {
        if (requestedReference == null || !hasText(requestedReference.getObjectKey())) {
            throw unavailable(FileConstants.STATUS_MALFORMED_REFERENCE, "missing object key");
        }
        try {
            String uploadedKey = client.upload(requestedReference.getObjectKey(),
                    requestedReference.getOriginalFilename(), bytes);
            FileReferenceDto out = new FileReferenceDto();
            out.setObjectKey(hasText(uploadedKey) ? stripQuery(uploadedKey.trim()) : requestedReference.getObjectKey());
            out.setFolder(requestedReference.getFolder());
            out.setOriginalFilename(requestedReference.getOriginalFilename());
            out.setExtension(requestedReference.getExtension());
            out.setSizeBytes(requestedReference.getSizeBytes());
            out.setProvider(providerName());
            return out;
        } catch (FileAdapterUnavailableException ex) {
            throw ex;
        } catch (Exception ex) {
            throw unavailable(FileConstants.STATUS_FAILED, sanitize(ex.toString()));
        }
    }

    public FilePreviewUrlResult previewUrl(String objectKey, long ttlSeconds) {
        FilePreviewUrlResult result = new FilePreviewUrlResult();
        result.setObjectKey(objectKey);
        result.setTtlSeconds(Long.valueOf(ttlSeconds));
        result.setProvider(providerName());
        String fallbackMessage = null;
        try {
            Map<String, Object> preview = client.preview(fileNameOf(objectKey), objectKey);
            result.setPreviewUrl(firstUrl(preview));
        } catch (Exception ex) {
            fallbackMessage = sanitize(ex.toString());
        }
        if (!hasText(result.getPreviewUrl())) {
            result.setPreviewUrl(downloadUrl(objectKey));
        }
        if (!hasText(result.getPreviewUrl())) {
            result.setStatus(FileConstants.STATUS_UNAVAILABLE);
            result.setMessage(hasText(fallbackMessage) ? fallbackMessage : "bos preview url unavailable");
            return result;
        }
        result.setStatus(FileConstants.STATUS_OK);
        result.setMessage(hasText(fallbackMessage) ? "download url ready after preview fallback" : "preview ready");
        return result;
    }

    public FileDownloadResult download(String objectKey) {
        FileDownloadResult result = new FileDownloadResult();
        result.setObjectKey(objectKey);
        result.setProvider(providerName());
        try {
            byte[] bytes = client.download(objectKey);
            if (bytes == null) {
                result.setStatus(FileConstants.STATUS_FAILED);
                result.setMessage("file not found");
                return result;
            }
            result.setStatus(FileConstants.STATUS_OK);
            result.setMessage("downloaded");
            result.setBytes(bytes);
            result.setSizeBytes(Long.valueOf(bytes.length));
            return result;
        } catch (Exception ex) {
            result.setStatus(FileConstants.STATUS_UNAVAILABLE);
            result.setMessage(sanitize(ex.toString()));
            return result;
        }
    }

    public FileDeleteResult delete(String objectKey) {
        FileDeleteResult result = new FileDeleteResult();
        result.setObjectKey(objectKey);
        result.setProvider(providerName());
        try {
            client.delete(objectKey);
            result.setStatus(FileConstants.STATUS_OK);
            result.setMessage("deleted");
            result.setDeleted(Boolean.TRUE);
            return result;
        } catch (Exception ex) {
            result.setStatus(FileConstants.STATUS_UNAVAILABLE);
            result.setMessage(sanitize(ex.toString()));
            result.setDeleted(Boolean.FALSE);
            return result;
        }
    }

    public boolean textExtractionAvailable() {
        return false;
    }

    public FileTextExtractionResult extractText(String objectKey, byte[] bytes) {
        FileTextExtractionResult result = new FileTextExtractionResult();
        result.setStatus(FileConstants.STATUS_UNAVAILABLE);
        result.setMessage("bos text extraction unsupported");
        result.setObjectKey(objectKey);
        result.setText("");
        result.setCharCount(Integer.valueOf(0));
        result.setTruncated(Boolean.FALSE);
        result.setProvider(providerName());
        return result;
    }

    private FileAdapterUnavailableException unavailable(String status, String message) {
        return new FileAdapterUnavailableException(status, message);
    }

    private String firstUrl(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        String direct = firstString(map, "previewUrl", "url", "preview.url", "downloadUrl", "fileDownloadUrl");
        if (hasText(direct)) {
            return direct;
        }
        Object result = map.get("result");
        if (result instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> nested = (Map<String, Object>) result;
            return firstString(nested, "previewUrl", "url", "preview.url", "downloadUrl", "fileDownloadUrl");
        }
        return null;
    }

    private String firstString(Map<String, Object> map, String... keys) {
        for (int i = 0; i < keys.length; i++) {
            Object value = map.get(keys[i]);
            if (value != null && hasText(String.valueOf(value))) {
                return String.valueOf(value);
            }
        }
        return null;
    }

    private String downloadUrl(String objectKey) {
        try {
            String prefix = client.httpUrlPrefix();
            if (!hasText(prefix)) {
                return null;
            }
            String normalized = prefix.endsWith("/") ? prefix.substring(0, prefix.length() - 1) : prefix;
            return normalized + "/file/download.do?path=" + URLEncoder.encode(objectKey, "UTF-8");
        } catch (Exception ex) {
            return null;
        }
    }

    private String fileNameOf(String objectKey) {
        if (!hasText(objectKey)) {
            return "file";
        }
        String normalized = objectKey.replace('\\', '/');
        int slash = normalized.lastIndexOf('/');
        return slash >= 0 && slash + 1 < normalized.length() ? normalized.substring(slash + 1) : normalized;
    }

    private String stripQuery(String value) {
        int query = value.indexOf('?');
        return query >= 0 ? value.substring(0, query) : value;
    }

    private String sanitize(String message) {
        if (!hasText(message)) {
            return "bos file service unavailable";
        }
        String safe = message.trim();
        int query = safe.indexOf('?');
        return query >= 0 ? safe.substring(0, query) : safe;
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
