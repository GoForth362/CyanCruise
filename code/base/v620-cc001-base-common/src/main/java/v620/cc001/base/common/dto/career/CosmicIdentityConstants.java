package v620.cc001.base.common.dto.career;

/**
 * Constants for CyanCruise identity context resolution.
 */
public final class CosmicIdentityConstants {

    public static final String STATUS_OK = "OK";
    public static final String STATUS_IDENTITY_REQUIRED = "IDENTITY_REQUIRED";
    public static final String STATUS_FORBIDDEN = "FORBIDDEN";
    public static final String STATUS_IDENTITY_MISMATCH = "IDENTITY_MISMATCH";

    public static final String ENV_PRODUCTION = "production";
    public static final String ENV_DEVELOPMENT = "development";

    public static final String SOURCE_COSMIC_PLATFORM_CONTEXT = "cosmic-platform-context";
    public static final String SOURCE_DEVELOPMENT_FALLBACK = "development-fallback";
    public static final String SOURCE_UNAVAILABLE = "unavailable";

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_COSMIC_ADMIN = "COSMIC_ADMIN";
    public static final String ROLE_PLATFORM_ADMIN = "PLATFORM_ADMIN";

    private CosmicIdentityConstants() {
    }
}
