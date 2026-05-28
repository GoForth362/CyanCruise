package v620.cc001.cloud01.app01.mservice;

import org.junit.jupiter.api.Test;
import v620.base.helper.career.CosmicIdentityContextHelper;
import v620.cc001.base.common.dto.career.CosmicIdentityConstants;
import v620.cc001.base.common.dto.career.CosmicIdentityContextDto;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigurableCosmicIdentityResolverTest {

    private final CosmicIdentityContextHelper helper = new CosmicIdentityContextHelper();

    @Test
    void disabledAdapterReturnsIdentityRequired() {
        CosmicIdentityContextDto context = new ConfigurableCosmicIdentityResolver(provider(map("userId", "u1")),
                CosmicIdentityAdapterConfig.disabled()).resolve();

        assertEquals(CosmicIdentityConstants.STATUS_IDENTITY_REQUIRED, context.getStatus());
        assertTrue(context.getMessage().contains("disabled"));
    }

    @Test
    void resolvesDefaultCandidatesAndDiagnostics() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("personId", "p1");
        map.put("operatorId", "op1");
        map.put("organizationId", "org1");
        map.put("roles", Arrays.asList("USER", "ADMIN"));
        map.put("ip", "127.0.0.1");
        map.put("userAgent", "JUnit");

        CosmicIdentityContextDto context = new ConfigurableCosmicIdentityResolver(provider(map), enabled()).resolve();

        assertEquals(CosmicIdentityConstants.STATUS_OK, context.getStatus());
        assertEquals("p1", context.getUserId());
        assertEquals("op1", context.getAdminId());
        assertEquals("org1", context.getOrgId());
        assertEquals("127.0.0.1", context.getIp());
        assertEquals("JUnit", context.getUserAgent());
        assertTrue(helper.hasAdminRole(context));
        assertEquals(CosmicIdentityConstants.ENV_PRODUCTION, context.getEnvironment());
    }

    @Test
    void resolvesOperatorWhenUserCandidateMissing() {
        CosmicIdentityContextDto context = new ConfigurableCosmicIdentityResolver(
                provider(map("operatorId", "op-only")), enabled()).resolve();

        assertEquals("op-only", context.getUserId());
        assertEquals("op-only", context.getAdminId());
    }

    @Test
    void parsesRolesFromArrayAndDelimitedText() {
        Map<String, Object> map = map("userId", "u1");
        map.put("roles", "reader;cosmic-admin");
        map.put("roleCodes", new String[]{"tenant-admin", "auditor"});
        CosmicIdentityAdapterConfig config = enabled();
        config.setAdminRoleAliases(Arrays.asList("TENANT_ADMIN"));

        CosmicIdentityContextDto context = new ConfigurableCosmicIdentityResolver(provider(map), config).resolve();

        assertTrue(context.getRoles().contains("cosmic-admin"));
        assertTrue(context.getRoles().contains("tenant-admin"));
        assertTrue(helper.hasAdminRole(context));
    }

    @Test
    void missingContextFailsSafely() {
        CosmicIdentityContextDto context = new ConfigurableCosmicIdentityResolver(provider(Collections.<String, Object>emptyMap()),
                enabled()).resolve();

        assertEquals(CosmicIdentityConstants.STATUS_IDENTITY_REQUIRED, context.getStatus());
        assertFalse(helper.isDevelopmentFallback(context));
    }

    @Test
    void factoryKeepsProductionUnavailableUnlessEnabled() {
        assertTrue(CareerLoopIdentityResolverFactory.production(provider(map("userId", "u1")),
                CosmicIdentityAdapterConfig.disabled()) instanceof UnavailableCosmicIdentityResolver);
        assertTrue(CareerLoopIdentityResolverFactory.production(provider(map("userId", "u1")),
                enabled()) instanceof ConfigurableCosmicIdentityResolver);
    }

    @Test
    void readsDiagnosticsToggleFromSystemProperties() {
        System.setProperty(CosmicIdentityAdapterConfig.DIAGNOSTICS_ENABLED_PROPERTY, "false");
        try {
            CosmicIdentityAdapterConfig config = CosmicIdentityAdapterConfig.fromSystemProperties();

            assertFalse(config.isDiagnosticsEnabled());
        } finally {
            System.clearProperty(CosmicIdentityAdapterConfig.DIAGNOSTICS_ENABLED_PROPERTY);
        }
    }

    private CosmicIdentityAdapterConfig enabled() {
        CosmicIdentityAdapterConfig config = new CosmicIdentityAdapterConfig();
        config.setEnabled(true);
        return config;
    }

    private CosmicIdentityContextProvider provider(final Map<String, Object> map) {
        return new CosmicIdentityContextProvider() {
            public Map<String, Object> currentContext() {
                return map;
            }
        };
    }

    private Map<String, Object> map(String key, Object value) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(key, value);
        return map;
    }
}
