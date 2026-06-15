package v620.cc001.cloud01.app01.mservice;

/**
 * Shared PostgreSQL configuration for CyanCruise business-state storage.
 */
public class PostgresqlStorageConfig {

    public static final String BACKEND_PROPERTY = "cc001.storage.backend";
    public static final String URL_PROPERTY = "cc001.storage.postgresql.url";
    public static final String USERNAME_PROPERTY = "cc001.storage.postgresql.username";
    public static final String PASSWORD_PROPERTY = "cc001.storage.postgresql.password";
    public static final String SCHEMA_PROPERTY = "cc001.storage.postgresql.schema";
    public static final String INITIALIZE_PROPERTY = "cc001.storage.postgresql.initialize";
    public static final String BACKEND_ENV = "CC001_STORAGE_BACKEND";
    public static final String URL_ENV = "CC001_STORAGE_POSTGRESQL_URL";
    public static final String USERNAME_ENV = "CC001_STORAGE_POSTGRESQL_USERNAME";
    public static final String PASSWORD_ENV = "CC001_STORAGE_POSTGRESQL_PASSWORD";
    public static final String SCHEMA_ENV = "CC001_STORAGE_POSTGRESQL_SCHEMA";
    public static final String INITIALIZE_ENV = "CC001_STORAGE_POSTGRESQL_INITIALIZE";

    private String backend;
    private String url;
    private String username;
    private String password;
    private String schema = "public";
    private boolean initialize;

    public static PostgresqlStorageConfig fromSystemProperties() {
        PostgresqlStorageConfig config = new PostgresqlStorageConfig();
        config.setBackend(configuredValue(BACKEND_PROPERTY, BACKEND_ENV,
                configuredValue(PostgresqlProfileStorageConfig.ADAPTER_PROPERTY,
                        PostgresqlProfileStorageConfig.ADAPTER_ENV, null)));
        config.setUrl(configuredValue(URL_PROPERTY, URL_ENV,
                configuredValue(PostgresqlProfileStorageConfig.URL_PROPERTY,
                        PostgresqlProfileStorageConfig.URL_ENV, null)));
        config.setUsername(configuredValue(USERNAME_PROPERTY, USERNAME_ENV,
                configuredValue(PostgresqlProfileStorageConfig.USERNAME_PROPERTY,
                        PostgresqlProfileStorageConfig.USERNAME_ENV, null)));
        config.setPassword(configuredValue(PASSWORD_PROPERTY, PASSWORD_ENV,
                configuredValue(PostgresqlProfileStorageConfig.PASSWORD_PROPERTY,
                        PostgresqlProfileStorageConfig.PASSWORD_ENV, null)));
        config.setSchema(configuredValue(SCHEMA_PROPERTY, SCHEMA_ENV,
                configuredValue(PostgresqlProfileStorageConfig.SCHEMA_PROPERTY,
                        PostgresqlProfileStorageConfig.SCHEMA_ENV, "public")));
        config.setInitialize(Boolean.parseBoolean(configuredValue(INITIALIZE_PROPERTY, INITIALIZE_ENV,
                configuredValue(PostgresqlProfileStorageConfig.INITIALIZE_PROPERTY,
                        PostgresqlProfileStorageConfig.INITIALIZE_ENV, "false"))));
        return config;
    }

    public boolean isPostgresqlBackend() {
        return "postgresql".equalsIgnoreCase(trim(backend));
    }

    public boolean isComplete() {
        return isPostgresqlBackend()
                && hasText(url)
                && hasText(username)
                && hasText(password)
                && hasText(schema);
    }

    public void requireComplete(String storageName) {
        if (!isComplete()) {
            throw new IllegalStateException("Complete PostgreSQL configuration is required for "
                    + storageName + ". Set cc001.storage.backend=postgresql, url, username, password and schema.");
        }
    }

    public PostgresqlProfileStorageConfig toProfileConfig() {
        PostgresqlProfileStorageConfig profile = new PostgresqlProfileStorageConfig();
        profile.setAdapter("postgresql");
        profile.setUrl(url);
        profile.setUsername(username);
        profile.setPassword(password);
        profile.setSchema(schema);
        profile.setInitialize(initialize);
        return profile;
    }

    public String getBackend() {
        return backend;
    }

    public void setBackend(String backend) {
        this.backend = trim(backend);
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

    static boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }

    static String trim(String value) {
        return value == null ? null : value.trim();
    }

    static String configuredValue(String propertyName, String envName, String defaultValue) {
        String propertyValue = System.getProperty(propertyName);
        if (hasText(propertyValue)) {
            return propertyValue;
        }
        String envValue = System.getenv(envName);
        return hasText(envValue) ? envValue : defaultValue;
    }
}
