package v620.cc001.cloud01.app01.mservice;

import org.junit.jupiter.api.Test;
import v620.base.helper.career.CosmicIdentityContextHelper;
import v620.cc001.base.common.dto.career.CosmicIdentityConstants;
import v620.cc001.base.common.dto.career.CosmicIdentityContextDto;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CosmicLoginContextProviderTest {

    private final CosmicIdentityContextHelper helper = new CosmicIdentityContextHelper();

    @Test
    void bridgeContextFeedsConfigurableResolver() {
        Map<String, Object> raw = new LinkedHashMap<String, Object>();
        raw.put("personId", "u100");
        raw.put("operatorId", "admin100");
        raw.put("deptId", "org100");
        raw.put("roles", Arrays.asList(role("COSMIC_ADMIN"), role("reader")));
        raw.put("token", "secret-token");
        raw.put("email", "a@example.com");
        raw.put("ip", "10.0.0.1");
        raw.put("userAgent", "JUnit");

        CosmicIdentityContextDto identity = new ConfigurableCosmicIdentityResolver(
                provider(raw, enabledProvider()), enabledAdapter()).resolve();

        assertEquals(CosmicIdentityConstants.STATUS_OK, identity.getStatus());
        assertEquals("u100", identity.getUserId());
        assertEquals("admin100", identity.getAdminId());
        assertEquals("org100", identity.getOrgId());
        assertTrue(identity.getRoles().contains("COSMIC_ADMIN"));
        assertTrue(helper.hasAdminRole(identity));
        assertEquals("10.0.0.1", identity.getIp());
        assertEquals("JUnit", identity.getUserAgent());
        assertFalse(String.valueOf(identity.getMessage()).contains("secret-token"));
        assertFalse(String.valueOf(identity.getMessage()).contains("example.com"));
    }

    @Test
    void disabledProviderReturnsOnlySafeDiagnostics() {
        Map<String, Object> context = provider(map("userId", "u1"), CosmicLoginContextProviderConfig.disabled()).currentContext();

        assertTrue(context.containsKey(PlatformCosmicIdentityContextProvider.DIAGNOSTIC_FIELD));
        assertFalse(context.containsKey("userId"));
    }

    @Test
    void bridgeExceptionDoesNotTrustIdentity() {
        CosmicLoginContextProviderConfig config = enabledProvider();
        CosmicIdentityContextProvider provider = CosmicLoginContextProviderFactory.production(new CosmicLoginContextBridge() {
            public Map<String, Object> currentLoginContext() {
                throw new IllegalStateException("boom token=secret");
            }
        }, config);

        CosmicIdentityContextDto identity = new ConfigurableCosmicIdentityResolver(provider, enabledAdapter()).resolve();

        assertEquals(CosmicIdentityConstants.STATUS_IDENTITY_REQUIRED, identity.getStatus());
        assertTrue(identity.getMessage().contains("bridge failed"));
        assertFalse(identity.getMessage().contains("secret"));
    }

    @Test
    void factoryKeepsAdapterDisabledUnavailable() {
        assertTrue(CareerLoopIdentityResolverFactory.production(provider(map("userId", "u1"), enabledProvider()),
                CosmicIdentityAdapterConfig.disabled()) instanceof UnavailableCosmicIdentityResolver);
    }

    @Test
    void webApiBoundaryRejectsBodyMismatchAndAllowsCurrentUser() {
        CosmicIdentityContextProvider provider = provider(map("userId", "u1"), enabledProvider());
        IdentityAwareCareerLoopWebApiBoundary boundary = new IdentityAwareCareerLoopWebApiBoundary(
                new ConfigurableCosmicIdentityResolver(provider, enabledAdapter()));

        assertEquals("u1", boundary.requireUser(null));
        assertEquals("u1", boundary.requireUser("u1"));
        try {
            boundary.requireUser("u2");
        } catch (IdentityBoundaryException ex) {
            assertEquals(CosmicIdentityConstants.STATUS_IDENTITY_MISMATCH, ex.getStatus());
        }
    }

    @Test
    void developmentResolverRemainsSeparate() {
        CosmicIdentityContextDto identity = new DevelopmentCareerLoopIdentityResolver("dev", null,
                java.util.Collections.<String>emptyList()).resolve();

        assertTrue(helper.isDevelopmentFallback(identity));
        assertEquals(CosmicIdentityConstants.ENV_DEVELOPMENT, identity.getEnvironment());
    }

    private CosmicIdentityContextProvider provider(final Map<String, Object> map,
                                                   CosmicLoginContextProviderConfig config) {
        return CosmicLoginContextProviderFactory.production(new CosmicLoginContextBridge() {
            public Map<String, Object> currentLoginContext() {
                return map;
            }
        }, config);
    }

    private CosmicLoginContextProviderConfig enabledProvider() {
        CosmicLoginContextProviderConfig config = new CosmicLoginContextProviderConfig();
        config.setEnabled(true);
        return config;
    }

    private CosmicIdentityAdapterConfig enabledAdapter() {
        CosmicIdentityAdapterConfig config = new CosmicIdentityAdapterConfig();
        config.setEnabled(true);
        return config;
    }

    private Map<String, Object> map(String key, Object value) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put(key, value);
        return map;
    }

    private Map<String, Object> role(String code) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("code", code);
        map.put("name", "ignored");
        return map;
    }
}
