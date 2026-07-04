package v620.cc001.cloud01.app01.mservice.auth;

public class KapiAccessTokenPrincipal {

    private final String username;
    private final String accountId;
    private final String source;

    public KapiAccessTokenPrincipal(String username, String accountId, String source) {
        this.username = trim(username);
        this.accountId = trim(accountId);
        this.source = trim(source);
    }

    public boolean isAvailable() {
        return hasText(username) && hasText(accountId);
    }

    public String cacheKey() {
        return username + "@" + accountId;
    }

    public String getUsername() {
        return username;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getSource() {
        return source;
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }

    private static boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
