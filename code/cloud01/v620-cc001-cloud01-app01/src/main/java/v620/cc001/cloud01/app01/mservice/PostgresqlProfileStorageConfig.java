package v620.cc001.cloud01.app01.mservice;

/**
 * Configuration for CyanCruise profile storage backed by PostgreSQL.
 */
public class PostgresqlProfileStorageConfig {

    public static final String ADAPTER_PROPERTY = "cc001.profile.storage.adapter";
    public static final String URL_PROPERTY = "cc001.profile.postgresql.url";
    public static final String USERNAME_PROPERTY = "cc001.profile.postgresql.username";
    public static final String PASSWORD_PROPERTY = "cc001.profile.postgresql.password";
    public static final String SCHEMA_PROPERTY = "cc001.profile.postgresql.schema";
    public static final String INITIALIZE_PROPERTY = "cc001.profile.postgresql.initialize";
    public static final String ADAPTER_ENV = "CC001_PROFILE_STORAGE_ADAPTER";
    public static final String URL_ENV = "CC001_PROFILE_POSTGRESQL_URL";
    public static final String USERNAME_ENV = "CC001_PROFILE_POSTGRESQL_USERNAME";
    public static final String PASSWORD_ENV = "CC001_PROFILE_POSTGRESQL_PASSWORD";
    public static final String SCHEMA_ENV = "CC001_PROFILE_POSTGRESQL_SCHEMA";
    public static final String INITIALIZE_ENV = "CC001_PROFILE_POSTGRESQL_INITIALIZE";

    private String adapter;
    private String url;
    private String username;
    private String password;
    private String schema = "public";
    private boolean initialize;

    public static PostgresqlProfileStorageConfig fromSystemProperties() {
        PostgresqlProfileStorageConfig config = new PostgresqlProfileStorageConfig();
        config.setAdapter(configuredValue(ADAPTER_PROPERTY, ADAPTER_ENV, null));
        config.setUrl(configuredValue(URL_PROPERTY, URL_ENV, null));
        config.setUsername(configuredValue(USERNAME_PROPERTY, USERNAME_ENV, null));
        config.setPassword(configuredValue(PASSWORD_PROPERTY, PASSWORD_ENV, null));
        config.setSchema(configuredValue(SCHEMA_PROPERTY, SCHEMA_ENV, "public"));
        config.setInitialize(Boolean.parseBoolean(configuredValue(INITIALIZE_PROPERTY, INITIALIZE_ENV, "false")));
        return config;
    }

    public boolean isPostgresqlAdapter() {
        return "postgresql".equalsIgnoreCase(trim(adapter));
    }

    public boolean isComplete() {
        return isPostgresqlAdapter()
                && hasText(url)
                && hasText(username)
                && hasText(password)
                && hasText(schema);
    }

    public String getAdapter() {
        return adapter;
    }

    public void setAdapter(String adapter) {
        this.adapter = adapter;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = trim(url);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = trim(username);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = hasText(schema) ? trim(schema) : "public";
    }

    public boolean isInitialize() {
        return initialize;
    }

    public void setInitialize(boolean initialize) {
        this.initialize = initialize;
    }

    private static boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }

    private static String configuredValue(String propertyName, String envName, String defaultValue) {
        String propertyValue = System.getProperty(propertyName);
        if (hasText(propertyValue)) {
            return propertyValue;
        }
        String envValue = System.getenv(envName);
        return hasText(envValue) ? envValue : defaultValue;
    }
}
