package v620.cc001.cloud01.app01.mservice.auth.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import v620.cc001.cloud01.app01.mservice.auth.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KapiAccessTokenServiceTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void missingConfigDoesNotCallEndpoint() {
        CountingTransport transport = new CountingTransport("{\"status\":true}");
        KapiAccessTokenConfig config = validConfig();
        config.setClientSecret(null);
        KapiAccessTokenService service = service(config, transport, new FixedClock(1000L),
                principal("fengru", "1565321489509515264"));

        KapiAccessTokenResult result = service.currentToken();

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("clientSecret"));
        assertEquals(0, transport.calls);
    }

    @Test
    void successfulTokenIsParsedAndCached() throws Exception {
        CountingTransport transport = new CountingTransport(
                "{\"status\":true,\"data\":{\"access_token\":\"TOKEN-001\",\"expires_in\":120}}");
        FixedClock clock = new FixedClock(100000L);
        KapiAccessTokenService service = service(validConfig(), transport, clock,
                principal("fengru", "1565321489509515264"));

        KapiAccessTokenResult first = service.currentToken();
        KapiAccessTokenResult second = service.currentToken();

        assertTrue(first.isSuccess());
        assertEquals("TOKEN-001", first.getAccessToken());
        assertEquals("TOKEN-001", second.getAccessToken());
        assertEquals(1, transport.calls);

        JsonNode request = mapper.readTree(transport.lastBody);
        assertEquals("cc001", request.path("client_id").asText());
        assertEquals("secret-value", request.path("client_secret").asText());
        assertEquals("fengru", request.path("username").asText());
        assertEquals("1565321489509515264", request.path("accountId").asText());
        assertTrue(request.path("nonce").asText().length() > 10);
        assertTrue(request.path("timestamp").asText().matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"));
    }

    @Test
    void usernameAndAccountIdCanComeFromResolver() throws Exception {
        CountingTransport transport = new CountingTransport(
                "{\"status\":true,\"data\":{\"access_token\":\"TOKEN-CTX\",\"expires_in\":120}}");
        KapiAccessTokenConfig config = validConfig();
        config.setUsername(null);
        config.setAccountId(null);
        KapiAccessTokenService service = service(config, transport, new FixedClock(100000L),
                principal("current-user", "current-account"));

        KapiAccessTokenResult result = service.currentToken();

        assertTrue(result.isSuccess());
        JsonNode request = mapper.readTree(transport.lastBody);
        assertEquals("current-user", request.path("username").asText());
        assertEquals("current-account", request.path("accountId").asText());
    }

    @Test
    void missingDynamicPrincipalDoesNotCallEndpoint() {
        CountingTransport transport = new CountingTransport(
                "{\"status\":true,\"data\":{\"access_token\":\"TOKEN-CTX\",\"expires_in\":120}}");
        KapiAccessTokenConfig config = validConfig();
        config.setUsername(null);
        config.setAccountId(null);
        KapiAccessTokenService service = service(config, transport, new FixedClock(100000L),
                principal("", ""));

        KapiAccessTokenResult result = service.currentToken();

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("username/accountId"));
        assertEquals(0, transport.calls);
    }

    @Test
    void expiredCacheFetchesAgain() {
        CountingTransport transport = new CountingTransport(
                "{\"status\":true,\"data\":{\"access_token\":\"TOKEN-001\",\"expires_in\":61}}",
                "{\"status\":true,\"data\":{\"access_token\":\"TOKEN-002\",\"expires_in\":61}}");
        FixedClock clock = new FixedClock(100000L);
        KapiAccessTokenService service = service(validConfig(), transport, clock,
                principal("fengru", "1565321489509515264"));

        assertEquals("TOKEN-001", service.currentToken().getAccessToken());
        clock.now = 102000L;
        assertEquals("TOKEN-002", service.currentToken().getAccessToken());
        assertEquals(2, transport.calls);
    }

    @Test
    void cacheIsSeparatedByPrincipal() {
        CountingTransport transport = new CountingTransport(
                "{\"status\":true,\"data\":{\"access_token\":\"TOKEN-A\",\"expires_in\":120}}",
                "{\"status\":true,\"data\":{\"access_token\":\"TOKEN-B\",\"expires_in\":120}}");
        MutablePrincipalResolver resolver = new MutablePrincipalResolver("user-a", "account");
        KapiAccessTokenService service = new KapiAccessTokenService(validConfig(), transport,
                new FixedClock(100000L), mapper, resolver);

        assertEquals("TOKEN-A", service.currentToken().getAccessToken());
        resolver.username = "user-b";
        assertEquals("TOKEN-B", service.currentToken().getAccessToken());
        resolver.username = "user-a";
        assertEquals("TOKEN-A", service.currentToken().getAccessToken());
        assertEquals(2, transport.calls);
    }

    @Test
    void failedResponseDoesNotExposeSecret() {
        CountingTransport transport = new CountingTransport(
                "{\"status\":false,\"message\":\"bad secret-value\"}");
        KapiAccessTokenService service = service(validConfig(), transport, new FixedClock(1000L),
                principal("fengru", "1565321489509515264"));

        KapiAccessTokenResult result = service.currentToken();

        assertFalse(result.isSuccess());
        assertFalse(result.getMessage().contains("secret-value"));
        assertTrue(result.getMessage().contains("<redacted>"));
    }

    private KapiAccessTokenConfig validConfig() {
        KapiAccessTokenConfig config = new KapiAccessTokenConfig();
        config.setEndpoint("http://127.0.0.1:8080/ierp/kapi/oauth2/getToken");
        config.setClientId("cc001");
        config.setClientSecret("secret-value");
        config.setUsername("fengru");
        config.setAccountId("1565321489509515264");
        config.setExpirySkewSeconds(60);
        return config;
    }

    private KapiAccessTokenService service(KapiAccessTokenConfig config, CountingTransport transport,
                                           FixedClock clock, final KapiAccessTokenPrincipal principal) {
        return new KapiAccessTokenService(config, transport, clock, mapper, new KapiAccessTokenPrincipalResolver() {
            public KapiAccessTokenPrincipal resolve(KapiAccessTokenConfig ignored) {
                return principal;
            }
        });
    }

    private KapiAccessTokenPrincipal principal(String username, String accountId) {
        return new KapiAccessTokenPrincipal(username, accountId, "test");
    }

    private static class CountingTransport implements KapiAccessTokenService.Transport {
        private final String[] responses;
        private int calls;
        private String lastBody;

        CountingTransport(String... responses) {
            this.responses = responses;
        }

        public KapiAccessTokenService.HttpResult post(String endpoint, String body, int timeoutSeconds) {
            lastBody = body;
            int index = Math.min(calls, responses.length - 1);
            calls += 1;
            return new KapiAccessTokenService.HttpResult(200, responses[index]);
        }
    }

    private static class FixedClock implements KapiAccessTokenService.Clock {
        private long now;

        FixedClock(long now) {
            this.now = now;
        }

        public long nowMillis() {
            return now;
        }
    }

    private static class MutablePrincipalResolver implements KapiAccessTokenPrincipalResolver {
        private String username;
        private final String accountId;

        MutablePrincipalResolver(String username, String accountId) {
            this.username = username;
            this.accountId = accountId;
        }

        public KapiAccessTokenPrincipal resolve(KapiAccessTokenConfig config) {
            return new KapiAccessTokenPrincipal(username, accountId, "test");
        }
    }
}
