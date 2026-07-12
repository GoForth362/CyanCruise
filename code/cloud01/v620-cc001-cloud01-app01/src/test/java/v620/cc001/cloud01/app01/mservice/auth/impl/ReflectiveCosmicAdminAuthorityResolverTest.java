package v620.cc001.cloud01.app01.mservice.auth.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReflectiveCosmicAdminAuthorityResolverTest {

    @Test
    void acceptsEverySupportedPlatformAdministratorSignal() {
        for (int signal = 0; signal < 5; signal += 1) {
            assertTrue(new StubResolver(signal).isAdmin("2477190919195983874", ""));
        }
    }

    @Test
    void rejectsRegularAndNonNumericUsers() {
        assertFalse(new StubResolver(-1).isAdmin("2477190919195983874", ""));
        assertFalse(new StubResolver(0).isAdmin("api-user", "api-user"));
    }

    private static final class StubResolver extends ReflectiveCosmicAdminAuthorityResolver {
        private final int activeSignal;

        private StubResolver(int activeSignal) {
            this.activeSignal = activeSignal;
        }

        boolean isAdminByPermissionService(long userId) {
            return activeSignal == 0;
        }

        boolean isSuperUserByPermissionService(long userId) {
            return activeSignal == 1;
        }

        boolean hasAdminTypeByPermissionService(long userId) {
            return activeSignal == 2;
        }

        boolean isAdminByAdminGroup(Long userId) {
            return activeSignal == 3;
        }

        boolean isAdminByAdminGroupMembership(Long userId) {
            return activeSignal == 4;
        }
    }
}
