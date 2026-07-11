package v620.cc001.cloud01.app01.mservice.storage;


import v620.cc001.cloud01.app01.mservice.datamodel.CosmicBusinessObjectDatamodelGateway;
import v620.cc001.cloud01.app01.mservice.datamodel.MappedCosmicDatamodelGateway;
import v620.cc001.cloud01.app01.mservice.storage.impl.CosmicCareerProfileStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerProfileStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.PostgresqlCareerProfileStorage;
/**
 * Selects the CyanCruise profile storage adapter from explicit configuration.
 */
public class CareerProfileStorageFactory {

    public static CareerProfileStorage fromSystemProperties() {
        return fromConfig(PostgresqlStorageConfig.fromSystemProperties());
    }

    public static CareerProfileStorage fromConfig(PostgresqlStorageConfig config) {
        PostgresqlStorageConfig safeConfig = config == null ? new PostgresqlStorageConfig() : config;
        if (safeConfig.isCosmicModuleEnabled("profile")) {
            return new CosmicCareerProfileStorage(new MappedCosmicDatamodelGateway(
                    CosmicBusinessObjectDatamodelGateway.fromConfig(safeConfig)));
        }
        if (safeConfig.isCosmicEnabled()) {
            return new InMemoryCareerProfileStorage();
        }
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
