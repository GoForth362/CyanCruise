package v620.cc001.cloud01.app01.mservice;

/**
 * Selects the CyanCruise profile storage adapter from explicit configuration.
 */
public class CareerProfileStorageFactory {

    public static CareerProfileStorage fromSystemProperties() {
        return fromConfig(PostgresqlStorageConfig.fromSystemProperties());
    }

    public static CareerProfileStorage fromConfig(PostgresqlStorageConfig config) {
        PostgresqlStorageConfig safeConfig = config == null ? new PostgresqlStorageConfig() : config;
        safeConfig.requireComplete("profile storage");
        return new PostgresqlCareerProfileStorage(safeConfig.toProfileConfig());
    }

    public static CareerProfileStorage fromConfig(PostgresqlProfileStorageConfig config, CareerProfileStorage fallback) {
        PostgresqlProfileStorageConfig safeConfig = config == null ? new PostgresqlProfileStorageConfig() : config;
        if (!safeConfig.isComplete()) {
            throw new IllegalStateException("Complete PostgreSQL profile storage configuration is required");
        }
        return new PostgresqlCareerProfileStorage(safeConfig);
    }
}
