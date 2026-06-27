package v620.cc001.cloud01.app01.mservice.auth.impl;

import v620.cc001.cloud01.app01.mservice.auth.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Acquires and caches KAPI AccessToken on the server side.
 */
public class KapiAccessTokenService {

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private final KapiAccessTokenConfig config;
    private final Transport transport;
    private final Clock clock;
    private final ObjectMapper mapper;
    private final KapiAccessTokenPrincipalResolver principalResolver;
    private final Map<String, KapiAccessTokenResult> cachedTokens = new HashMap<String, KapiAccessTokenResult>();

    public KapiAccessTokenService() {
        this(KapiAccessTokenConfig.fromSystemProperties());
    }

    public KapiAccessTokenService(KapiAccessTokenConfig config) {
        this(config, new UrlConnectionTransport(), new SystemClock(), new ObjectMapper(),
                new RequestContextKapiAccessTokenPrincipalResolver());
    }

    KapiAccessTokenService(KapiAccessTokenConfig config, Transport transport, Clock clock, ObjectMapper mapper) {
        this(config, transport, clock, mapper, new RequestContextKapiAccessTokenPrincipalResolver());
    }

    KapiAccessTokenService(KapiAccessTokenConfig config, Transport transport, Clock clock, ObjectMapper mapper,
                           KapiAccessTokenPrincipalResolver principalResolver) {
        this.config = config == null ? new KapiAccessTokenConfig() : config;
        this.transport = transport == null ? new UrlConnectionTransport() : transport;
        this.clock = clock == null ? new SystemClock() : clock;
        this.mapper = mapper == null ? new ObjectMapper() : mapper;
        this.principalResolver = principalResolver == null
                ? new RequestContextKapiAccessTokenPrincipalResolver()
                : principalResolver;
    }

    public synchronized KapiAccessTokenResult currentToken() {
        if (!config.isAvailable()) {
            return KapiAccessTokenResult.unavailable(config.missingReason());
        }
        KapiAccessTokenPrincipal principal = principalResolver.resolve(config);
        if (principal == null || !principal.isAvailable()) {
            return KapiAccessTokenResult.unavailable("missing username/accountId from Cosmic request context");
        }
        KapiAccessTokenResult cached = cachedTokens.get(principal.cacheKey());
        if (cached != null && cached.isSuccess() && cached.getExpiresAtMillis() > clock.nowMillis()) {
            return cached;
        }
        KapiAccessTokenResult acquired = acquireToken(principal);
        if (acquired.isSuccess()) {
            cachedTokens.put(principal.cacheKey(), acquired);
        }
        return acquired;
    }

    public synchronized void clearCache() {
        cachedTokens.clear();
    }

    private KapiAccessTokenResult acquireToken(KapiAccessTokenPrincipal principal) {
        try {
            String request = mapper.writeValueAsString(requestBody(principal));
            HttpResult result = transport.post(config.getEndpoint(), request, config.getTimeoutSeconds());
            if (result.statusCode < 200 || result.statusCode >= 300) {
                return KapiAccessTokenResult.unavailable("KAPI token endpoint returned HTTP " + result.statusCode);
            }
            JsonNode root = mapper.readTree(result.body == null ? "{}" : result.body);
            if (root.has("status") && !root.path("status").asBoolean(false)) {
                return KapiAccessTokenResult.unavailable(sanitizedMessage(root));
            }
            if (root.has("success") && !root.path("success").asBoolean(false)) {
                return KapiAccessTokenResult.unavailable(sanitizedMessage(root));
            }
            String token = firstText(
                    text(root, "access_token"),
                    text(root, "accessToken"),
                    text(root.path("data"), "access_token"),
                    text(root.path("data"), "accessToken"),
                    text(root.path("data"), "token"),
                    text(root, "token"));
            if (!hasText(token)) {
                return KapiAccessTokenResult.unavailable("KAPI token response has no access token");
            }
            int responseExpires = firstPositiveInt(
                    root.path("expires_in").asInt(0),
                    root.path("expiresIn").asInt(0),
                    root.path("data").path("expires_in").asInt(0),
                    root.path("data").path("expiresIn").asInt(0));
            int cacheSeconds = responseExpires > 0 ? responseExpires : config.getCacheSeconds();
            long expiresAt = clock.nowMillis()
                    + Math.max(1, cacheSeconds - config.getExpirySkewSeconds()) * 1000L;
            return KapiAccessTokenResult.success(token, expiresAt);
        } catch (Exception ex) {
            return KapiAccessTokenResult.unavailable("KAPI token acquisition failed: " + ex.getClass().getSimpleName());
        }
    }

    private Map<String, Object> requestBody(KapiAccessTokenPrincipal principal) {
        Map<String, Object> body = new LinkedHashMap<String, Object>();
        body.put("client_id", config.getClientId());
        body.put("client_secret", config.getClientSecret());
        body.put("username", principal.getUsername());
        body.put("accountId", principal.getAccountId());
        body.put("nonce", UUID.randomUUID().toString());
        body.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(clock.nowMillis())));
        body.put("language", config.getLanguage());
        return body;
    }

    private String sanitizedMessage(JsonNode root) {
        String message = firstText(text(root, "message"), text(root, "errorCode"), "KAPI token endpoint rejected request");
        return redact(message);
    }

    private String redact(String value) {
        String safe = value == null ? "" : value;
        if (hasText(config.getClientSecret())) {
            safe = safe.replace(config.getClientSecret(), "<redacted>");
        }
        for (KapiAccessTokenResult cached : cachedTokens.values()) {
            if (cached != null && hasText(cached.getAccessToken())) {
                safe = safe.replace(cached.getAccessToken(), "<redacted>");
            }
        }
        return safe;
    }

    private static String text(JsonNode node, String field) {
        if (node == null || node.isMissingNode() || node.path(field).isMissingNode() || node.path(field).isNull()) {
            return null;
        }
        String value = node.path(field).asText();
        return hasText(value) ? value : null;
    }

    private static String firstText(String... values) {
        for (String value : values) {
            if (hasText(value)) {
                return value.trim();
            }
        }
        return null;
    }

    private static int firstPositiveInt(int... values) {
        for (int value : values) {
            if (value > 0) {
                return value;
            }
        }
        return 0;
    }

    private static boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }

    interface Transport {
        HttpResult post(String endpoint, String body, int timeoutSeconds) throws Exception;
    }

    interface Clock {
        long nowMillis();
    }

    static class HttpResult {
        final int statusCode;
        final String body;

        HttpResult(int statusCode, String body) {
            this.statusCode = statusCode;
            this.body = body == null ? "" : body;
        }
    }

    private static class SystemClock implements Clock {
        public long nowMillis() {
            return System.currentTimeMillis();
        }
    }

    private static class UrlConnectionTransport implements Transport {
        public HttpResult post(String endpoint, String body, int timeoutSeconds) throws Exception {
            HttpURLConnection connection = (HttpURLConnection) new URL(endpoint).openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(timeoutSeconds * 1000);
            connection.setReadTimeout(timeoutSeconds * 1000);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            OutputStream output = connection.getOutputStream();
            output.write((body == null ? "" : body).getBytes(UTF_8));
            output.close();
            int status = connection.getResponseCode();
            InputStream input = status >= 400 ? connection.getErrorStream() : connection.getInputStream();
            return new HttpResult(status, readAll(input));
        }

        private String readAll(InputStream input) throws Exception {
            if (input == null) {
                return "";
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(input, UTF_8));
            StringBuilder out = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (out.length() > 0) {
                    out.append('\n');
                }
                out.append(line);
            }
            reader.close();
            return out.toString();
        }
    }
}
