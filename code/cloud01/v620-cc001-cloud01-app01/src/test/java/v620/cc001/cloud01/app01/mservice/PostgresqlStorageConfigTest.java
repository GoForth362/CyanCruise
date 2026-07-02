package v620.cc001.cloud01.app01.mservice;

import v620.cc001.cloud01.app01.mservice.storage.PostgresqlProfileStorageConfig;
import v620.cc001.cloud01.app01.mservice.storage.PostgresqlStorageConfig;
import v620.cc001.cloud01.app01.mservice.notification.impl.PostgresqlNotificationStorage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PostgresqlStorageConfigTest {

    @Test
    void completeSharedConfigIsValid() {
        PostgresqlStorageConfig config = new PostgresqlStorageConfig();
        config.setBackend("postgresql");
        config.setUrl("jdbc:postgresql://10.0.0.8:5432/cyancruise");
        config.setUsername("cyancruise_app");
        config.setPassword("placeholder");
        config.setSchema("public");

        assertTrue(config.isComplete());
        assertEquals("postgresql", config.toProfileConfig().getAdapter());
    }

    @Test
    void incompleteConfigFailsFast() {
        final PostgresqlStorageConfig config = new PostgresqlStorageConfig();
        config.setBackend("postgresql");

        assertThrows(IllegalStateException.class,
                new org.junit.jupiter.api.function.Executable() {
                    public void execute() {
                        config.requireComplete("test storage");
                    }
                });
    }

    @Test
    void notificationStorageRequiresCompleteConfig() {
        final PostgresqlStorageConfig config = new PostgresqlStorageConfig();
        config.setBackend("postgresql");

        assertThrows(IllegalStateException.class,
                new org.junit.jupiter.api.function.Executable() {
                    public void execute() {
                        new PostgresqlNotificationStorage(config);
                    }
                });
    }

    @Test
    void profilePropertiesRemainTemporaryAlias() {
        String previousAdapter = System.getProperty(PostgresqlProfileStorageConfig.ADAPTER_PROPERTY);
        String previousUrl = System.getProperty(PostgresqlProfileStorageConfig.URL_PROPERTY);
        String previousUsername = System.getProperty(PostgresqlProfileStorageConfig.USERNAME_PROPERTY);
        String previousPassword = System.getProperty(PostgresqlProfileStorageConfig.PASSWORD_PROPERTY);
        String previousSchema = System.getProperty(PostgresqlProfileStorageConfig.SCHEMA_PROPERTY);
        try {
            System.setProperty(PostgresqlProfileStorageConfig.ADAPTER_PROPERTY, "postgresql");
            System.setProperty(PostgresqlProfileStorageConfig.URL_PROPERTY, "jdbc:postgresql://10.0.0.8:5432/cyancruise");
            System.setProperty(PostgresqlProfileStorageConfig.USERNAME_PROPERTY, "cyancruise_app");
            System.setProperty(PostgresqlProfileStorageConfig.PASSWORD_PROPERTY, "placeholder");
            System.setProperty(PostgresqlProfileStorageConfig.SCHEMA_PROPERTY, "public");

            PostgresqlStorageConfig config = PostgresqlStorageConfig.fromSystemProperties();

            assertTrue(config.isComplete());
            assertEquals("public", config.getSchema());
        } finally {
            restore(PostgresqlProfileStorageConfig.ADAPTER_PROPERTY, previousAdapter);
            restore(PostgresqlProfileStorageConfig.URL_PROPERTY, previousUrl);
            restore(PostgresqlProfileStorageConfig.USERNAME_PROPERTY, previousUsername);
            restore(PostgresqlProfileStorageConfig.PASSWORD_PROPERTY, previousPassword);
            restore(PostgresqlProfileStorageConfig.SCHEMA_PROPERTY, previousSchema);
        }
    }

    private void restore(String key, String value) {
        if (value == null) {
            System.clearProperty(key);
            return;
        }
        System.setProperty(key, value);
    }
}
