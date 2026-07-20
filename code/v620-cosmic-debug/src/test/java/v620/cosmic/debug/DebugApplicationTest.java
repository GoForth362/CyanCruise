package v620.cosmic.debug;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DebugApplicationTest {

    @TempDir
    Path tempDir;

    private static final String[] STORAGE_PROPERTIES = {
            "cc001.storage.backend",
            "cc001.storage.postgresql.url",
            "cc001.storage.postgresql.username",
            "cc001.storage.postgresql.password",
            "cc001.storage.postgresql.schema"
    };

    @Test
    void sharedRuntimeRequiresCompleteStorageConfiguration() {
        PropertyState state = new PropertyState(STORAGE_PROPERTIES);
        try {
            System.setProperty("cc001.storage.backend", "postgresql");
            assertThrows(IllegalStateException.class, new org.junit.jupiter.api.function.Executable() {
                public void execute() {
                    DebugApplication.validateSharedStorageConfiguration();
                }
            });
        } finally {
            state.restore();
        }
    }

    @Test
    void completeSharedRuntimeStorageConfigurationIsAccepted() {
        PropertyState state = new PropertyState(STORAGE_PROPERTIES);
        try {
            System.setProperty("cc001.storage.backend", "postgresql");
            System.setProperty("cc001.storage.postgresql.url", "jdbc:postgresql://db.example:5432/cyancruise");
            System.setProperty("cc001.storage.postgresql.username", "cyancruise_app");
            System.setProperty("cc001.storage.postgresql.password", "placeholder");
            System.setProperty("cc001.storage.postgresql.schema", "public");

            assertDoesNotThrow(new org.junit.jupiter.api.function.Executable() {
                public void execute() {
                    DebugApplication.validateSharedStorageConfiguration();
                }
            });
        } finally {
            state.restore();
        }
    }

    @Test
    void localConfigCanBeFoundFromNestedLauncherDirectory() throws Exception {
        Path config = tempDir.resolve("debug-local.properties");
        Files.write(config, "cc001.test=true\n".getBytes("ISO-8859-1"));
        Path launcherDirectory = tempDir.resolve("environment/runtime/bin");
        Files.createDirectories(launcherDirectory);

        File resolved = DebugApplication.findLocalConfigUpwards(launcherDirectory.toFile());

        assertEquals(config.toFile().getCanonicalFile(), resolved.getCanonicalFile());
    }

    private static class PropertyState {
        private final String[] keys;
        private final String[] values;

        PropertyState(String[] keys) {
            this.keys = keys;
            this.values = new String[keys.length];
            for (int i = 0; i < keys.length; i++) {
                values[i] = System.getProperty(keys[i]);
                System.clearProperty(keys[i]);
            }
        }

        void restore() {
            for (int i = 0; i < keys.length; i++) {
                if (values[i] == null) {
                    System.clearProperty(keys[i]);
                } else {
                    System.setProperty(keys[i], values[i]);
                }
            }
        }
    }
}
