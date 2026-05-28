package v620.cc001.base.common.dto.career;

/**
 * Constants for CareerLoop file upload, preview and extraction contracts.
 */
public final class FileConstants {

    public static final String STATUS_OK = "OK";
    public static final String STATUS_FAILED = "FAILED";
    public static final String STATUS_SKIPPED = "SKIPPED";
    public static final String STATUS_UNAVAILABLE = "UNAVAILABLE";
    public static final String STATUS_FILE_EMPTY = "FILE_EMPTY";
    public static final String STATUS_MALFORMED_REFERENCE = "MALFORMED_REFERENCE";

    public static final String PROVIDER_LOCAL = "local-memory";
    public static final String PROVIDER_COSMIC = "cosmic-file-service";
    public static final String PROVIDER_UNAVAILABLE = "unavailable";

    public static final String DEFAULT_FOLDER = "others";
    public static final long MIN_PREVIEW_TTL_SECONDS = 60L;
    public static final long MAX_PREVIEW_TTL_SECONDS = 86400L;
    public static final int MAX_EXTRACTED_TEXT_CHARS = 20000;

    private FileConstants() {
    }
}
