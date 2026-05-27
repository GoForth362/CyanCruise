package v620.base.helper.career;

import v620.cc001.base.common.dto.career.FileConstants;
import v620.cc001.base.common.dto.career.FileReferenceDto;
import v620.cc001.base.common.dto.career.FileTextExtractionResult;

import java.util.Locale;
import java.util.UUID;

/**
 * Pure Java rules for file references, preview URLs and extraction limits.
 */
public class FileUploadPreviewService {

    public String normalizeFolder(String folder) {
        if (!hasText(folder)) {
            return FileConstants.DEFAULT_FOLDER;
        }
        String cleaned = folder.trim().replace('\\', '/');
        while (cleaned.startsWith("/")) {
            cleaned = cleaned.substring(1);
        }
        while (cleaned.endsWith("/")) {
            cleaned = cleaned.substring(0, cleaned.length() - 1);
        }
        cleaned = cleaned.replaceAll("[^A-Za-z0-9_./-]", "-");
        return cleaned.length() == 0 ? FileConstants.DEFAULT_FOLDER : cleaned;
    }

    public String extensionOf(String filename) {
        if (!hasText(filename)) {
            return "";
        }
        String name = filename.trim();
        int slash = Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\'));
        if (slash >= 0) {
            name = name.substring(slash + 1);
        }
        int dot = name.lastIndexOf('.');
        if (dot < 0 || dot == name.length() - 1 || dot == 0) {
            return "";
        }
        return name.substring(dot).toLowerCase(Locale.ROOT);
    }

    public FileReferenceDto buildReference(String folder, String originalFilename, byte[] bytes) {
        validateUpload(bytes);
        String safeFolder = normalizeFolder(folder);
        String extension = extensionOf(originalFilename);
        FileReferenceDto reference = new FileReferenceDto();
        reference.setFolder(safeFolder);
        reference.setOriginalFilename(originalFilename);
        reference.setExtension(extension);
        reference.setSizeBytes(Long.valueOf(bytes.length));
        reference.setObjectKey(safeFolder + "/" + UUID.randomUUID().toString() + extension);
        return reference;
    }

    public void validateUpload(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException(FileConstants.STATUS_FILE_EMPTY);
        }
    }

    public String normalizeObjectKey(String fileUrlOrKey) {
        if (!hasText(fileUrlOrKey)) {
            return null;
        }
        String value = fileUrlOrKey.trim();
        int scheme = value.indexOf("://");
        if (scheme < 0) {
            return stripQuery(stripLeadingSlash(value));
        }
        int slash = value.indexOf('/', scheme + 3);
        if (slash < 0 || slash + 1 >= value.length()) {
            throw new IllegalArgumentException(FileConstants.STATUS_MALFORMED_REFERENCE);
        }
        return stripQuery(stripLeadingSlash(value.substring(slash + 1)));
    }

    public long clampTtl(long ttlSeconds) {
        return Math.max(FileConstants.MIN_PREVIEW_TTL_SECONDS,
                Math.min(ttlSeconds, FileConstants.MAX_PREVIEW_TTL_SECONDS));
    }

    public String fileTypeHint(String objectKeyOrFilename) {
        String extension = extensionOf(objectKeyOrFilename);
        if (".pdf".equals(extension)) return "pdf";
        if (".doc".equals(extension) || ".docx".equals(extension)) return "word";
        if (".png".equals(extension) || ".jpg".equals(extension) || ".jpeg".equals(extension)) return "image";
        if (".txt".equals(extension) || ".md".equals(extension)) return "text";
        return "binary";
    }

    public FileTextExtractionResult textResult(String objectKey, String text, String status, String message) {
        FileTextExtractionResult result = new FileTextExtractionResult();
        result.setObjectKey(objectKey);
        result.setStatus(status);
        result.setMessage(message);
        String safeText = text == null ? "" : text;
        boolean truncated = safeText.length() > FileConstants.MAX_EXTRACTED_TEXT_CHARS;
        if (truncated) {
            safeText = safeText.substring(0, FileConstants.MAX_EXTRACTED_TEXT_CHARS);
        }
        result.setText(safeText);
        result.setCharCount(Integer.valueOf(safeText.length()));
        result.setTruncated(Boolean.valueOf(truncated));
        return result;
    }

    private String stripLeadingSlash(String value) {
        String out = value;
        while (out.startsWith("/")) {
            out = out.substring(1);
        }
        return out;
    }

    private String stripQuery(String value) {
        int query = value.indexOf('?');
        return query >= 0 ? value.substring(0, query) : value;
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
