package v620.cc001.cloud01.app01.mservice.storage;

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
    public static final String COSMIC_ENABLED_PROPERTY = "cc001.storage.cosmic.enabled";
    public static final String COSMIC_MODULES_PROPERTY = "cc001.storage.cosmic.modules";
    public static final String COSMIC_CLIENT_CLASS_PROPERTY = "cc001.storage.cosmic.clientClass";
    public static final String BACKEND_ENV = "CC001_STORAGE_BACKEND";
    public static final String URL_ENV = "CC001_STORAGE_POSTGRESQL_URL";
    public static final String USERNAME_ENV = "CC001_STORAGE_POSTGRESQL_USERNAME";
    public static final String PASSWORD_ENV = "CC001_STORAGE_POSTGRESQL_PASSWORD";
    public static final String SCHEMA_ENV = "CC001_STORAGE_POSTGRESQL_SCHEMA";
    public static final String INITIALIZE_ENV = "CC001_STORAGE_POSTGRESQL_INITIALIZE";
    public static final String COSMIC_ENABLED_ENV = "CC001_STORAGE_COSMIC_ENABLED";
    public static final String COSMIC_MODULES_ENV = "CC001_STORAGE_COSMIC_MODULES";
    public static final String COSMIC_CLIENT_CLASS_ENV = "CC001_STORAGE_COSMIC_CLIENT_CLASS";

    private String backend;
    private String url;
    private String username;
    private String password;
    private String schema = "public";
    private boolean initialize;
    private boolean cosmicEnabled;
    private String cosmicModules;
    private String cosmicClientClass;

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
        config.setCosmicEnabled(Boolean.parseBoolean(configuredValue(COSMIC_ENABLED_PROPERTY, COSMIC_ENABLED_ENV,
                "cosmic".equalsIgnoreCase(config.getBackend()) ? "true" : "false")));
        config.setCosmicModules(configuredValue(COSMIC_MODULES_PROPERTY, COSMIC_MODULES_ENV, ""));
        config.setCosmicClientClass(configuredValue(COSMIC_CLIENT_CLASS_PROPERTY, COSMIC_CLIENT_CLASS_ENV, ""));
        return config;
    }

    public boolean isPostgresqlBackend() {
        return "postgresql".equalsIgnoreCase(trim(backend));
    }

    public boolean isCosmicBackend() {
        return "cosmic".equalsIgnoreCase(trim(backend));
    }

    public boolean isCosmicEnabled() {
        return cosmicEnabled || isCosmicBackend();
    }

    public boolean isCosmicModuleEnabled(String moduleName) {
        if (!isCosmicEnabled() || !hasText(moduleName)) {
            return false;
        }
        if (!hasText(cosmicModules)) {
            return isCosmicBackend();
        }
        String[] modules = cosmicModules.split(",");
        for (int i = 0; i < modules.length; i++) {
            String module = trim(modules[i]);
            if ("*".equals(module) || moduleName.equalsIgnoreCase(module)) {
                return true;
            }
        }
        return false;
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

    public boolean getCosmicEnabled() {
        return cosmicEnabled;
    }

    public void setCosmicEnabled(boolean cosmicEnabled) {
        this.cosmicEnabled = cosmicEnabled;
    }

    public String getCosmicModules() {
        return cosmicModules;
    }

    public void setCosmicModules(String cosmicModules) {
        this.cosmicModules = trim(cosmicModules);
    }

    public String getCosmicClientClass() {
        return cosmicClientClass;
    }

    public void setCosmicClientClass(String cosmicClientClass) {
        this.cosmicClientClass = trim(cosmicClientClass);
    }

    public static boolean hasText(String value) {
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
