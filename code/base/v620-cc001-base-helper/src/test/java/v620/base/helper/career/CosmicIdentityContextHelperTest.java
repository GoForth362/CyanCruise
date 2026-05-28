package v620.base.helper.career;

import org.junit.jupiter.api.Test;
import v620.cc001.base.common.dto.career.CosmicIdentityConstants;
import v620.cc001.base.common.dto.career.CosmicIdentityContextDto;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CosmicIdentityContextHelperTest {

    private final CosmicIdentityContextHelper helper = new CosmicIdentityContextHelper();

    @Test
    void marksMissingIdentityAsRequired() {
        CosmicIdentityContextDto context = CosmicIdentityContextDto.identityRequired("missing");

        assertEquals(CosmicIdentityConstants.STATUS_IDENTITY_REQUIRED, helper.userStatus(context, "u1"));
    }

    @Test
    void keepsDevelopmentFallbackVisible() {
        CosmicIdentityContextDto context = CosmicIdentityContextDto.development("u1", null, null);

        assertTrue(helper.isDevelopmentFallback(context));
        assertEquals(CosmicIdentityConstants.STATUS_OK, helper.userStatus(context, "u1"));
    }

    @Test
    void detectsUserMismatch() {
        CosmicIdentityContextDto context = user("u1");

        assertEquals(CosmicIdentityConstants.STATUS_OK, helper.userStatus(context, "u1"));
        assertEquals(CosmicIdentityConstants.STATUS_IDENTITY_MISMATCH, helper.userStatus(context, "u2"));
    }

    @Test
    void normalizesAdminRoles() {
        CosmicIdentityContextDto context = user("admin");
        context.setAdminId("admin");
        context.setRoles(Arrays.asList("cosmic-admin"));

        assertTrue(helper.hasAdminRole(context));
        assertEquals(CosmicIdentityConstants.STATUS_OK, helper.adminStatus(context, "admin"));
    }

    @Test
    void rejectsCallerWithoutAdminRole() {
        CosmicIdentityContextDto context = user("u1");

        assertFalse(helper.hasAdminRole(context));
        assertEquals(CosmicIdentityConstants.STATUS_FORBIDDEN, helper.adminStatus(context, "u1"));
    }

    private CosmicIdentityContextDto user(String userId) {
        CosmicIdentityContextDto context = new CosmicIdentityContextDto();
        context.setUserId(userId);
        context.setSource(CosmicIdentityConstants.SOURCE_COSMIC_PLATFORM_CONTEXT);
        context.setEnvironment(CosmicIdentityConstants.ENV_PRODUCTION);
        context.setStatus(CosmicIdentityConstants.STATUS_OK);
        return context;
    }
}
