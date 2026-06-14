package v620.cc001.cloud01.app01.mservice;

/**
 * Selects the CyanCruise profile storage adapter from explicit configuration.
 */
public class CareerProfileStorageFactory {

    public static CareerProfileStorage fromSystemProperties() {
        return fromConfig(PostgresqlProfileStorageConfig.fromSystemProperties(), new FileCareerProfileStorage());
    }

    public static CareerProfileStorage fromConfig(PostgresqlProfileStorageConfig config, CareerProfileStorage fallback) {
        CareerProfileStorage safeFallback = fallback == null ? new FileCareerProfileStorage() : fallback;
        PostgresqlProfileStorageConfig safeConfig = config == null ? new PostgresqlProfileStorageConfig() : config;
        if (!safeConfig.isComplete()) {
            return safeFallback;
        }
        return new PostgresqlCareerProfileStorage(safeConfig);
    }
}
