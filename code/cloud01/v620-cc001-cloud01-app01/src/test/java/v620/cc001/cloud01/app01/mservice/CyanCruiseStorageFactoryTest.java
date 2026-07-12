package v620.cc001.cloud01.app01.mservice;

import org.junit.jupiter.api.Test;
import v620.cc001.cloud01.app01.mservice.notification.impl.PostgresqlSubscriptionQuotaStorage;
import v620.cc001.cloud01.app01.mservice.storage.CyanCruiseStorageFactory;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryAdminGovernanceStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.PostgresqlAdminGovernanceStorage;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CyanCruiseStorageFactoryTest {

    private static final String[] PROPERTIES = {
            CyanCruiseStorageFactory.SHARED_RUNTIME_PROPERTY,
            "cc001.storage.backend",
            "cc001.storage.postgresql.url",
            "cc001.storage.postgresql.username",
            "cc001.storage.postgresql.password",
            "cc001.storage.postgresql.schema",
            "cc001.storage.postgresql.initialize",
            "cc001.storage.cosmic.enabled",
            "cc001.profile.storage.adapter",
            "cc001.profile.postgresql.url",
            "cc001.profile.postgresql.username",
            "cc001.profile.postgresql.password",
            "cc001.profile.postgresql.schema",
            "cc001.profile.postgresql.initialize"
    };

    @Test
    void sharedRuntimeRejectsIncompleteStorageInsteadOfUsingMemory() {
        PropertyState state = new PropertyState();
        try {
            System.setProperty(CyanCruiseStorageFactory.SHARED_RUNTIME_PROPERTY, "true");

            assertThrows(IllegalStateException.class, new org.junit.jupiter.api.function.Executable() {
                public void execute() {
                    CyanCruiseStorageFactory.adminGovernanceStorage();
                }
            });
        } finally {
            state.restore();
        }
    }

    @Test
    void sharedRuntimeCreatesPostgresqlGovernanceStorage() {
        PropertyState state = new PropertyState();
        try {
            System.setProperty(CyanCruiseStorageFactory.SHARED_RUNTIME_PROPERTY, "true");
            System.setProperty("cc001.storage.backend", "postgresql");
            System.setProperty("cc001.storage.postgresql.url", "jdbc:postgresql://db.example:5432/cyancruise");
            System.setProperty("cc001.storage.postgresql.username", "cyancruise_app");
            System.setProperty("cc001.storage.postgresql.password", "placeholder");
            System.setProperty("cc001.storage.postgresql.schema", "public");
            System.setProperty("cc001.storage.postgresql.initialize", "false");

            assertTrue(CyanCruiseStorageFactory.adminGovernanceStorage()
                    instanceof PostgresqlAdminGovernanceStorage);
        } finally {
            state.restore();
        }
    }

    @Test
    void sharedRuntimeCreatesPostgresqlSubscriptionQuotaStorage() {
        PropertyState state = new PropertyState();
        try {
            System.setProperty(CyanCruiseStorageFactory.SHARED_RUNTIME_PROPERTY, "true");
            System.setProperty("cc001.storage.backend", "postgresql");
            System.setProperty("cc001.storage.postgresql.url", "jdbc:postgresql://db.example:5432/cyancruise");
            System.setProperty("cc001.storage.postgresql.username", "cyancruise_app");
            System.setProperty("cc001.storage.postgresql.password", "placeholder");
            System.setProperty("cc001.storage.postgresql.schema", "public");
            System.setProperty("cc001.storage.postgresql.initialize", "false");

            assertTrue(CyanCruiseStorageFactory.subscriptionQuotaStorage()
                    instanceof PostgresqlSubscriptionQuotaStorage);
        } finally {
            state.restore();
        }
    }

    @Test
    void explicitLocalDevelopmentCanUseMemoryStorage() {
        PropertyState state = new PropertyState();
        try {
            System.setProperty(CyanCruiseStorageFactory.SHARED_RUNTIME_PROPERTY, "false");

            assertTrue(CyanCruiseStorageFactory.adminGovernanceStorage()
                    instanceof InMemoryAdminGovernanceStorage);
        } finally {
            state.restore();
        }
    }

    private static class PropertyState {
        private final String[] values = new String[PROPERTIES.length];

        PropertyState() {
            for (int i = 0; i < PROPERTIES.length; i++) {
                values[i] = System.getProperty(PROPERTIES[i]);
                System.clearProperty(PROPERTIES[i]);
            }
        }

        void restore() {
            for (int i = 0; i < PROPERTIES.length; i++) {
                if (values[i] == null) {
                    System.clearProperty(PROPERTIES[i]);
                } else {
                    System.setProperty(PROPERTIES[i], values[i]);
                }
            }
        }
    }
}
