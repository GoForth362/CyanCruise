package v620.cc001.cloud01.app01.mservice.auth;

/**
 * Sanitized result for server-managed KAPI AccessToken acquisition.
 */
public class KapiAccessTokenResult {

    private final boolean success;
    private final String accessToken;
    private final long expiresAtMillis;
    private final String message;

    private KapiAccessTokenResult(boolean success, String accessToken, long expiresAtMillis, String message) {
        this.success = success;
        this.accessToken = accessToken;
        this.expiresAtMillis = expiresAtMillis;
        this.message = message;
    }

    public static KapiAccessTokenResult success(String accessToken, long expiresAtMillis) {
        return new KapiAccessTokenResult(true, accessToken, expiresAtMillis, "OK");
    }

    public static KapiAccessTokenResult unavailable(String message) {
        return new KapiAccessTokenResult(false, null, 0L, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public long getExpiresAtMillis() {
        return expiresAtMillis;
    }

    public String getMessage() {
        return message;
    }
}
