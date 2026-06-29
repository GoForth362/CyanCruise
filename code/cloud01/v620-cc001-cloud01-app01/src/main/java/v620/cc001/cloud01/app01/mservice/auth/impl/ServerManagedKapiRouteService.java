package v620.cc001.cloud01.app01.mservice.auth.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kd.bos.entity.api.ApiResult;
import v620.cc001.base.common.dto.career.CosmicIdentityConstants;
import v620.cc001.base.common.dto.career.CosmicIdentityContextDto;
import v620.cc001.cloud01.app01.mservice.auth.KapiAccessTokenConfig;
import v620.cc001.cloud01.app01.mservice.auth.KapiAccessTokenResult;
import v620.cc001.cloud01.app01.webapi.CyanCruiseCustomWebApiPlugin;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Browser-facing backend proxy. The browser never receives KAPI tokens.
 */
public class ServerManagedKapiRouteService {

    public static final String ROUTE_ENDPOINT_PROPERTY = "cc001.kapi.route.endpoint";
    public static final String ROUTE_TIMEOUT_SECONDS_PROPERTY = "cc001.kapi.route.timeoutSeconds";
    private static final String IDENTITY_CURRENT_PATH = "/cc001/identity/current";
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private final KapiAccessTokenService tokenService;
    private final IdentityAwareCyanCruiseWebApiBoundary identityBoundary;
    private final Transport transport;
    private final ObjectMapper mapper;
    private final String routeEndpoint;
    private final int timeoutSeconds;

    public ServerManagedKapiRouteService() {
        this(new KapiAccessTokenService(), new IdentityAwareCyanCruiseWebApiBoundary(),
                new UrlConnectionTransport(), new ObjectMapper(), resolveRouteEndpoint(),
                resolveTimeoutSeconds());
    }

    ServerManagedKapiRouteService(KapiAccessTokenService tokenService,
                                  IdentityAwareCyanCruiseWebApiBoundary identityBoundary,
                                  Transport transport,
                                  ObjectMapper mapper,
                                  String routeEndpoint,
                                  int timeoutSeconds) {
        this.tokenService = tokenService == null ? new KapiAccessTokenService() : tokenService;
        this.identityBoundary = identityBoundary == null
                ? new IdentityAwareCyanCruiseWebApiBoundary()
                : identityBoundary;
        this.transport = transport == null ? new UrlConnectionTransport() : transport;
        this.mapper = mapper == null ? new ObjectMapper() : mapper;
        this.routeEndpoint = trimToNull(routeEndpoint);
        this.timeoutSeconds = Math.max(1, timeoutSeconds);
    }

    public ApiResult route(Map<String, Object> params) {
        if (params == null) {
            return ApiResult.fail("CyanCruise server-managed route params are required");
        }
        String path = normalizePath(text(params.get(CyanCruiseCustomWebApiPlugin.PARAM_PATH)));
        if (!hasText(path)) {
            return ApiResult.fail("CyanCruise server-managed route path is required");
        }
        CosmicIdentityContextDto identity = identityBoundary.currentIdentity();
        if (IDENTITY_CURRENT_PATH.equals(path)) {
            return ApiResult.success(identity);
        }
        if (!isUsableIdentity(identity)) {
            return ApiResult.fail(firstText(identity == null ? null : identity.getMessage(),
                            "CyanCruise requires current Cosmic login context"),
                    "IDENTITY_REQUIRED");
        }
        if (!hasText(routeEndpoint)) {
            return ApiResult.fail("missing " + ROUTE_ENDPOINT_PROPERTY + " or "
                    + KapiAccessTokenConfig.ENDPOINT_PROPERTY, "KAPI_ROUTE_ENDPOINT_MISSING");
        }
        KapiAccessTokenResult token = tokenService.currentToken();
        if (token == null || !token.isSuccess()) {
            return ApiResult.fail(token == null ? "KAPI token unavailable" : token.getMessage(),
                    "KAPI_TOKEN_UNAVAILABLE");
        }
        try {
            Map<String, Object> request = new LinkedHashMap<String, Object>();
            request.put(CyanCruiseCustomWebApiPlugin.PARAM_PATH, path);
            request.put(CyanCruiseCustomWebApiPlugin.PARAM_BODY,
                    params.get(CyanCruiseCustomWebApiPlugin.PARAM_BODY));
            request.put(CyanCruiseCustomWebApiPlugin.PARAM_PLATFORM_IDENTITY, platformIdentity(identity));
            HttpResult result = transport.post(routeUrl(token.getAccessToken()),
                    mapper.writeValueAsString(request), timeoutSeconds);
            if (result.statusCode < 200 || result.statusCode >= 300) {
                return ApiResult.fail("KAPI route returned HTTP " + result.statusCode,
                        "KAPI_ROUTE_HTTP_ERROR");
            }
            return parseKapiResult(result.body);
        } catch (Exception ex) {
            return ApiResult.fail("KAPI route proxy failed: " + ex.getClass().getSimpleName(),
                    "KAPI_ROUTE_PROXY_FAILED");
        }
    }

    private ApiResult parseKapiResult(String body) throws Exception {
        JsonNode root = mapper.readTree(hasText(body) ? body : "{}");
        if (root.has("success")) {
            if (!root.path("success").asBoolean(false)) {
                return ApiResult.fail(firstText(text(root, "message"), "KAPI route rejected request"),
                        firstText(text(root, "errorCode"), "KAPI_ROUTE_FAILED"));
            }
            return ApiResult.success(toObject(root.path("data")));
        }
        if (root.has("status") && root.has("data")) {
            if (!root.path("status").asBoolean(false)) {
                return ApiResult.fail(firstText(text(root, "message"), "KAPI route rejected request"),
                        firstText(text(root, "errorCode"), "KAPI_ROUTE_FAILED"));
            }
            return ApiResult.success(toObject(root.path("data")));
        }
        return ApiResult.success(toObject(root));
    }

    private Object toObject(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return null;
        }
        return mapper.convertValue(node, Object.class);
    }

    private String routeUrl(String accessToken) {
        String separator = routeEndpoint.indexOf('?') >= 0 ? "&" : "?";
        return routeEndpoint + separator + "access_token=" + urlEncode(accessToken);
    }

    private Map<String, Object> platformIdentity(CosmicIdentityContextDto identity) {
        Map<String, Object> context = new LinkedHashMap<String, Object>();
        put(context, "userId", identity.getUserId());
        put(context, "currentUserId", identity.getUserId());
        put(context, "operatorId", identity.getUserId());
        put(context, "adminId", identity.getAdminId());
        put(context, "displayName", identity.getDisplayName());
        put(context, "userName", identity.getUserName());
        put(context, "orgId", identity.getOrgId());
        put(context, "environment", identity.getEnvironment());
        put(context, "ip", identity.getIp());
        put(context, "userAgent", identity.getUserAgent());
        put(context, "source", "server-managed-request-context");
        List<String> roles = identity.getRoles();
        if (roles != null && !roles.isEmpty()) {
            context.put("roles", roles);
        }
        return context;
    }

    private boolean isUsableIdentity(CosmicIdentityContextDto identity) {
        return identity != null
                && CosmicIdentityConstants.STATUS_OK.equals(identity.getStatus())
                && (hasText(identity.getUserId()) || hasText(identity.getAdminId()));
    }

    private static String resolveRouteEndpoint() {
        String configured = configuredValue(ROUTE_ENDPOINT_PROPERTY, "CC001_KAPI_ROUTE_ENDPOINT", null);
        if (hasText(configured)) {
            return configured;
        }
        String tokenEndpoint = configuredValue(KapiAccessTokenConfig.ENDPOINT_PROPERTY,
                "CC001_KAPI_TOKEN_ENDPOINT", null);
        if (!hasText(tokenEndpoint)) {
            return null;
        }
        int marker = tokenEndpoint.indexOf("/kapi/oauth2/");
        if (marker < 0) {
            return null;
        }
        return tokenEndpoint.substring(0, marker) + "/kapi/v2/v620/v620_cc001/cc001/cyancruise/route";
    }

    private static int resolveTimeoutSeconds() {
        String value = configuredValue(ROUTE_TIMEOUT_SECONDS_PROPERTY, "CC001_KAPI_ROUTE_TIMEOUT_SECONDS", null);
        if (!hasText(value)) {
            value = configuredValue(KapiAccessTokenConfig.TIMEOUT_SECONDS_PROPERTY,
                    "CC001_KAPI_TOKEN_TIMEOUT_SECONDS", null);
        }
        try {
            return hasText(value) ? Math.max(1, Integer.parseInt(value.trim())) : 10;
        } catch (NumberFormatException ex) {
            return 10;
        }
    }

    private static String configuredValue(String property, String env, String fallback) {
        String value = System.getProperty(property);
        if (!hasText(value)) {
            value = System.getenv(env);
        }
        return hasText(value) ? value.trim() : fallback;
    }

    private static String normalizePath(String path) {
        String safe = trimToNull(path);
        if (safe == null) {
            return "";
        }
        return safe.charAt(0) == '/' ? safe : "/" + safe;
    }

    private static String text(JsonNode node, String field) {
        if (node == null || node.path(field).isMissingNode() || node.path(field).isNull()) {
            return null;
        }
        String value = node.path(field).asText();
        return hasText(value) ? value : null;
    }

    private static String text(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private static String firstText(String... values) {
        for (String value : values) {
            if (hasText(value)) {
                return value.trim();
            }
        }
        return null;
    }

    private static void put(Map<String, Object> context, String key, Object value) {
        if (hasText(text(value))) {
            context.put(key, value);
        }
    }

    private static boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }

    private static String trimToNull(String value) {
        if (!hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private static String urlEncode(String value) {
        try {
            return java.net.URLEncoder.encode(value == null ? "" : value, "UTF-8");
        } catch (Exception ex) {
            return "";
        }
    }

    interface Transport {
        HttpResult post(String endpoint, String body, int timeoutSeconds) throws Exception;
    }

    static class HttpResult {
        final int statusCode;
        final String body;

        HttpResult(int statusCode, String body) {
            this.statusCode = statusCode;
            this.body = body == null ? "" : body;
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
