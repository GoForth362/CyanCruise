package v620.cc001.cloud01.app01.mservice;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PostgresqlProfileStorageConfigTest {

    @Test
    void incompletePostgresqlConfigFallsBackWithoutConnecting() {
        PostgresqlProfileStorageConfig config = new PostgresqlProfileStorageConfig();
        config.setAdapter("postgresql");
        config.setUrl("jdbc:postgresql://10.0.0.8:5432/cyancruise");
        config.setUsername("cyancruise_app");

        CareerProfileStorage fallback = new InMemoryCareerProfileStorage();

        assertFalse(config.isComplete());
        assertSame(fallback, CareerProfileStorageFactory.fromConfig(config, fallback));
    }

    @Test
    void completePostgresqlConfigSelectsPostgresqlAdapterWithoutInitialization() {
        PostgresqlProfileStorageConfig config = new PostgresqlProfileStorageConfig();
        config.setAdapter("postgresql");
        config.setUrl("jdbc:postgresql://10.0.0.8:5432/cyancruise");
        config.setUsername("cyancruise_app");
        config.setPassword("placeholder");
        config.setSchema("public");
        config.setInitialize(false);

        CareerProfileStorage storage = CareerProfileStorageFactory.fromConfig(config, new InMemoryCareerProfileStorage());

        assertTrue(config.isComplete());
        assertTrue(storage instanceof PostgresqlCareerProfileStorage);
    }

    @Test
    void nonPostgresqlAdapterKeepsFallback() {
        PostgresqlProfileStorageConfig config = new PostgresqlProfileStorageConfig();
        config.setAdapter("file");
        config.setUrl("jdbc:postgresql://10.0.0.8:5432/cyancruise");
        config.setUsername("cyancruise_app");
        config.setPassword("placeholder");
        config.setSchema("public");
        CareerProfileStorage fallback = new InMemoryCareerProfileStorage();

        assertSame(fallback, CareerProfileStorageFactory.fromConfig(config, fallback));
    }
}
