package v620.cc001.cloud01.app01.mservice.ai.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import v620.cc001.base.common.dto.ai.AgentTaskFlowRequestDto;
import v620.cc001.base.common.dto.ai.AgentTaskFlowResponseDto;
import v620.cc001.cloud01.app01.mservice.ai.AgentPlatformTaskFlowClient;
import v620.cc001.cloud01.app01.mservice.ai.AgentPlatformTaskFlowConfig;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Calls a published Agent task flow without exposing its endpoint or token to the browser.
 */
public class DefaultAgentPlatformTaskFlowClient implements AgentPlatformTaskFlowClient {

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private final AgentPlatformTaskFlowConfig config;
    private final Transport transport;
    private final ObjectMapper mapper;

    public DefaultAgentPlatformTaskFlowClient(AgentPlatformTaskFlowConfig config) {
        this(config, new UrlConnectionTransport(), new ObjectMapper());
    }

    public DefaultAgentPlatformTaskFlowClient(AgentPlatformTaskFlowConfig config,
                                              Transport transport,
                                              ObjectMapper mapper) {
        this.config = config == null ? new AgentPlatformTaskFlowConfig() : config;
        this.transport = transport == null ? new UrlConnectionTransport() : transport;
        this.mapper = mapper == null ? new ObjectMapper() : mapper;
    }

    public AgentTaskFlowResponseDto execute(AgentTaskFlowRequestDto request) {
        if (!config.isAvailable()) {
            return AgentTaskFlowResponseDto.unavailable("UNAVAILABLE", config.missingReason());
        }
        try {
            String body = mapper.writeValueAsString(requestBody(request));
            HttpResult result = transport.post(config.getEndpoint(), config.getAuthorizationHeader(),
                    config.getAuthorizationPrefix() + config.getAccessToken(), body, config.getTimeoutSeconds());
            if (result.statusCode < 200 || result.statusCode >= 300) {
                return failure("HTTP_" + result.statusCode, "Agent platform task flow request failed", result.statusCode);
            }
            String answer = extractAnswer(result.body);
            if (!hasText(answer)) {
                return failure("INVALID_RESPONSE", "Agent platform response has no task flow answer", result.statusCode);
            }
            AgentTaskFlowResponseDto response = new AgentTaskFlowResponseDto();
            response.setSuccess(true);
            response.setAnswer(answer);
            response.setStatusCode(Integer.valueOf(result.statusCode));
            return response;
        } catch (SocketTimeoutException ex) {
            return AgentTaskFlowResponseDto.unavailable("TIMEOUT", "Agent platform task flow request timed out");
        } catch (Exception ex) {
            return AgentTaskFlowResponseDto.unavailable("NETWORK", "Agent platform task flow request failed");
        }
    }

    private Map<String, Object> requestBody(AgentTaskFlowRequestDto request) {
        AgentTaskFlowRequestDto safe = request == null ? new AgentTaskFlowRequestDto() : request;
        Map<String, Object> body = new LinkedHashMap<String, Object>();
        String flowCode = hasText(safe.getTaskFlowCode()) ? safe.getTaskFlowCode() : config.getTaskFlowCode();
        body.put(config.getFlowCodeField(), flowCode);
        body.put(config.getInputField(), safe.getInputs());
        return body;
    }

    private String extractAnswer(String responseBody) throws Exception {
        JsonNode root = mapper.readTree(responseBody == null ? "{}" : responseBody);
        JsonNode answer = path(root, config.getAnswerPath());
        if (answer == null || answer.isMissingNode() || answer.isNull()) {
            return null;
        }
        return answer.isValueNode() ? answer.asText() : mapper.writeValueAsString(answer);
    }

    private JsonNode path(JsonNode root, String configuredPath) {
        JsonNode current = root;
        String[] names = configuredPath == null ? new String[0] : configuredPath.split("\\.");
        for (String name : names) {
            if (current == null || !hasText(name)) {
                return null;
            }
            current = current.path(name.trim());
        }
        return current;
    }

    private AgentTaskFlowResponseDto failure(String code, String message, int statusCode) {
        AgentTaskFlowResponseDto response = AgentTaskFlowResponseDto.unavailable(code, message);
        response.setStatusCode(Integer.valueOf(statusCode));
        return response;
    }

    private static boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }

    public interface Transport {
        HttpResult post(String endpoint, String authorizationHeader, String authorizationValue,
                        String body, int timeoutSeconds) throws Exception;
    }

    public static class HttpResult {
        public final int statusCode;
        public final String body;

        public HttpResult(int statusCode, String body) {
            this.statusCode = statusCode;
            this.body = body == null ? "" : body;
        }
    }

    private static class UrlConnectionTransport implements Transport {
        public HttpResult post(String endpoint, String authorizationHeader, String authorizationValue,
                               String body, int timeoutSeconds) throws Exception {
            HttpURLConnection connection = (HttpURLConnection) new URL(endpoint).openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(timeoutSeconds * 1000);
            connection.setReadTimeout(timeoutSeconds * 1000);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            if (hasText(authorizationHeader) && hasText(authorizationValue)) {
                connection.setRequestProperty(authorizationHeader, authorizationValue);
            }
            OutputStream output = connection.getOutputStream();
            output.write((body == null ? "" : body).getBytes(UTF_8));
            output.close();
            int status = connection.getResponseCode();
            InputStream input = status >= 400 ? connection.getErrorStream() : connection.getInputStream();
            return new HttpResult(status, readAll(input));
        }

        private String readAll(InputStream input) throws Exception {
            if (input == null) return "";
            BufferedReader reader = new BufferedReader(new InputStreamReader(input, UTF_8));
            StringBuilder out = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (out.length() > 0) out.append('\n');
                out.append(line);
            }
            reader.close();
            return out.toString();
        }
    }
}
