package v620.cc001.cloud01.app01.mservice;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PostgresqlProfileStorageConfigTest {

    @Test
    void incompletePostgresqlConfigFailsFastWithoutConnecting() {
        PostgresqlProfileStorageConfig config = new PostgresqlProfileStorageConfig();
        config.setAdapter("postgresql");
        config.setUrl("jdbc:postgresql://10.0.0.8:5432/cyancruise");
        config.setUsername("cyancruise_app");

        assertFalse(config.isComplete());
        assertThrows(IllegalStateException.class,
                new org.junit.jupiter.api.function.Executable() {
                    public void execute() {
                        CareerProfileStorageFactory.fromConfig(config, new InMemoryCareerProfileStorage());
                    }
                });
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
    void nonPostgresqlAdapterFailsFast() {
        PostgresqlProfileStorageConfig config = new PostgresqlProfileStorageConfig();
        config.setAdapter("file");
        config.setUrl("jdbc:postgresql://10.0.0.8:5432/cyancruise");
        config.setUsername("cyancruise_app");
        config.setPassword("placeholder");
        config.setSchema("public");

        assertThrows(IllegalStateException.class,
                new org.junit.jupiter.api.function.Executable() {
                    public void execute() {
                        CareerProfileStorageFactory.fromConfig(config, new InMemoryCareerProfileStorage());
                    }
                });
    }
}
