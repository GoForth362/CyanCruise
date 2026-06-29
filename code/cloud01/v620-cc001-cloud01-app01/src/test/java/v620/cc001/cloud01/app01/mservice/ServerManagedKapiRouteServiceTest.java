package v620.cc001.cloud01.app01.mservice.auth.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kd.bos.entity.api.ApiResult;
import org.junit.jupiter.api.Test;
import v620.cc001.base.common.dto.career.CosmicIdentityConstants;
import v620.cc001.base.common.dto.career.CosmicIdentityContextDto;
import v620.cc001.cloud01.app01.mservice.auth.KapiAccessTokenConfig;
import v620.cc001.cloud01.app01.mservice.auth.KapiAccessTokenPrincipal;
import v620.cc001.cloud01.app01.mservice.auth.KapiAccessTokenPrincipalResolver;
import v620.cc001.cloud01.app01.mservice.auth.CyanCruiseIdentityResolver;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServerManagedKapiRouteServiceTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void identityCurrentDoesNotRequestKapiToken() {
        CountingTokenTransport tokenTransport = new CountingTokenTransport(
                "{\"status\":true,\"data\":{\"access_token\":\"TOKEN\",\"expires_in\":120}}");
        CountingRouteTransport routeTransport = new CountingRouteTransport("{\"success\":true,\"data\":{}}");
        ServerManagedKapiRouteService service = service(okIdentity(), tokenTransport, routeTransport);

        ApiResult result = service.route(params("/cc001/identity/current"));

        assertTrue(result.getSuccess());
        assertEquals("current-user", ((CosmicIdentityContextDto) result.getData()).getUserId());
        assertEquals(0, tokenTransport.calls);
        assertEquals(0, routeTransport.calls);
    }

    @Test
    void protectedRouteUsesServerTokenAndRequestContextIdentity() throws Exception {
        CountingTokenTransport tokenTransport = new CountingTokenTransport(
                "{\"status\":true,\"data\":{\"access_token\":\"TOKEN-001\",\"expires_in\":120}}");
        CountingRouteTransport routeTransport = new CountingRouteTransport(
                "{\"success\":true,\"data\":{\"status\":\"OK\"}}");
        ServerManagedKapiRouteService service = service(okIdentity(), tokenTransport, routeTransport);

        ApiResult result = service.route(params("/cc001/career-profile/snapshot/get"));

        assertTrue(result.getSuccess());
        assertTrue(routeTransport.lastEndpoint.contains("access_token=TOKEN-001"));
        JsonNode request = mapper.readTree(routeTransport.lastBody);
        assertEquals("/cc001/career-profile/snapshot/get", request.path("path").asText());
        assertEquals("current-user", request.path("platformIdentity").path("userId").asText());
        assertEquals("server-managed-request-context", request.path("platformIdentity").path("source").asText());
    }

    @Test
    void missingIdentityRejectsBeforeTokenRequest() {
        CountingTokenTransport tokenTransport = new CountingTokenTransport(
                "{\"status\":true,\"data\":{\"access_token\":\"TOKEN\",\"expires_in\":120}}");
        CountingRouteTransport routeTransport = new CountingRouteTransport("{\"success\":true,\"data\":{}}");
        CosmicIdentityContextDto identity = CosmicIdentityContextDto.identityRequired("missing login");
        ServerManagedKapiRouteService service = service(identity, tokenTransport, routeTransport);

        ApiResult result = service.route(params("/cc001/career-profile/snapshot/get"));

        assertFalse(result.getSuccess());
        assertEquals("IDENTITY_REQUIRED", result.getErrorCode());
        assertEquals(0, tokenTransport.calls);
        assertEquals(0, routeTransport.calls);
    }

    private ServerManagedKapiRouteService service(final CosmicIdentityContextDto identity,
                                                  CountingTokenTransport tokenTransport,
                                                  CountingRouteTransport routeTransport) {
        KapiAccessTokenConfig config = new KapiAccessTokenConfig();
        config.setEndpoint("http://127.0.0.1:8080/ierp/kapi/oauth2/getToken");
        config.setClientId("cc001");
        config.setClientSecret("secret-value");
        KapiAccessTokenService tokenService = new KapiAccessTokenService(config, tokenTransport,
                new FixedClock(1000L), mapper, new KapiAccessTokenPrincipalResolver() {
            public KapiAccessTokenPrincipal resolve(KapiAccessTokenConfig ignored) {
                return new KapiAccessTokenPrincipal("proxy-user", "account", "test");
            }
        });
        IdentityAwareCyanCruiseWebApiBoundary boundary = new IdentityAwareCyanCruiseWebApiBoundary(
                new CyanCruiseIdentityResolver() {
                    public CosmicIdentityContextDto resolve() {
                        return identity;
                    }
                });
        return new ServerManagedKapiRouteService(tokenService, boundary, routeTransport, mapper,
                "http://127.0.0.1:8080/ierp/kapi/v2/v620/v620_cc001/cc001/cyancruise/route", 10);
    }

    private Map<String, Object> params(String path) {
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("path", path);
        params.put("body", new LinkedHashMap<String, Object>());
        return params;
    }

    private CosmicIdentityContextDto okIdentity() {
        CosmicIdentityContextDto identity = new CosmicIdentityContextDto();
        identity.setUserId("current-user");
        identity.setAdminId("current-user");
        identity.setStatus(CosmicIdentityConstants.STATUS_OK);
        identity.setSource("request-context");
        identity.setEnvironment(CosmicIdentityConstants.ENV_PRODUCTION);
        return identity;
    }

    private static class CountingTokenTransport implements KapiAccessTokenService.Transport {
        private final String response;
        private int calls;

        CountingTokenTransport(String response) {
            this.response = response;
        }

        public KapiAccessTokenService.HttpResult post(String endpoint, String body, int timeoutSeconds) {
            calls += 1;
            return new KapiAccessTokenService.HttpResult(200, response);
        }
    }

    private static class CountingRouteTransport implements ServerManagedKapiRouteService.Transport {
        private final String response;
        private int calls;
        private String lastEndpoint;
        private String lastBody;

        CountingRouteTransport(String response) {
            this.response = response;
        }

        public ServerManagedKapiRouteService.HttpResult post(String endpoint, String body, int timeoutSeconds) {
            calls += 1;
            lastEndpoint = endpoint;
            lastBody = body;
            return new ServerManagedKapiRouteService.HttpResult(200, response);
        }
    }

    private static class FixedClock implements KapiAccessTokenService.Clock {
        private final long now;

        FixedClock(long now) {
            this.now = now;
        }

        public long nowMillis() {
            return now;
        }
    }
}
