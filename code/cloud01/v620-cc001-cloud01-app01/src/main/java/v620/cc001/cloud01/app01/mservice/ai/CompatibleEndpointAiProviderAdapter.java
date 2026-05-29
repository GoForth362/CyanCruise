package v620.cc001.cloud01.app01.mservice.ai;

import v620.base.helper.ai.OpenAiCompatibleProviderHelper;
import v620.cc001.base.common.dto.ai.AiChatRequestDto;
import v620.cc001.base.common.dto.ai.AiChatResponseDto;
import v620.cc001.base.common.dto.ai.AiConstants;
import v620.cc001.base.common.dto.ai.AiStreamEventDto;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

public class CompatibleEndpointAiProviderAdapter implements AiProviderAdapter {

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private final AiProviderConfig config;
    private final OpenAiCompatibleProviderHelper helper;
    private final Transport transport;

    public CompatibleEndpointAiProviderAdapter(String endpoint, String apiKey) {
        this(config(endpoint, apiKey));
    }

    public CompatibleEndpointAiProviderAdapter(AiProviderConfig config) {
        this(config, new OpenAiCompatibleProviderHelper(), new UrlConnectionTransport());
    }

    public CompatibleEndpointAiProviderAdapter(AiProviderConfig config,
                                               OpenAiCompatibleProviderHelper helper,
                                               Transport transport) {
        this.config = config == null ? new AiProviderConfig() : config;
        this.helper = helper == null ? new OpenAiCompatibleProviderHelper() : helper;
        this.transport = transport == null ? new UrlConnectionTransport() : transport;
    }

    public boolean isAvailable() {
        return config.isComplete();
    }

    public AiChatResponseDto chat(AiChatRequestDto request) {
        if (!isAvailable()) {
            AiChatResponseDto response = AiChatResponseDto.unavailable("AI provider is disabled or incomplete");
            response.setProviderName(config.getProviderName());
            response.setDiagnostics("enabled=" + config.isEnabled() + ", endpointConfigured="
                    + hasText(config.getEndpoint()) + ", apiKeyConfigured=" + hasText(config.getApiKey()));
            return response;
        }
        AiChatRequestDto safe = normalize(request, false);
        String body = helper.buildChatRequestJson(safe);
        return executeChat(body, safe.isRetryOnce() || config.isRetryOn5xx());
    }

    public List<AiStreamEventDto> stream(AiChatRequestDto request) {
        if (!isAvailable()) {
            return Arrays.asList(AiStreamEventDto.error(AiConstants.ERROR_UNAVAILABLE,
                    "AI provider is disabled or incomplete", 0));
        }
        AiChatRequestDto safe = normalize(request, true);
        String body = helper.buildChatRequestJson(safe);
        try {
            HttpResult result = transport.post(config.getEndpoint(), config.getApiKey(), body, safe.getTimeoutSeconds().intValue());
            if (result.statusCode >= 200 && result.statusCode < 300) {
                return helper.parseStreamLines(Arrays.asList(result.body.split("\\r?\\n")));
            }
            return Arrays.asList(AiStreamEventDto.error(classify(result.statusCode),
                    "provider returned status " + result.statusCode, 0));
        } catch (Exception ex) {
            return Arrays.asList(AiStreamEventDto.error(errorCode(ex), helper.redactSecrets(ex.toString(), config.getApiKey()), 0));
        }
    }

    private AiChatResponseDto executeChat(String body, boolean retryOn5xx) {
        long started = System.currentTimeMillis();
        int retryCount = 0;
        try {
            HttpResult result = transport.post(config.getEndpoint(), config.getApiKey(), body, config.getTimeoutSeconds());
            if (isRetryable(result.statusCode) && retryOn5xx) {
                retryCount = 1;
                result = transport.post(config.getEndpoint(), config.getApiKey(), body, config.getTimeoutSeconds());
            }
            long elapsed = System.currentTimeMillis() - started;
            AiChatResponseDto response = result.statusCode >= 200 && result.statusCode < 300
                    ? helper.parseChatResponse(result.body, config.getProviderName(), elapsed, retryCount)
                    : helper.parseErrorResponse(result.statusCode, helper.redactSecrets(result.body, config.getApiKey()),
                    config.getProviderName(), elapsed, retryCount);
            response.setStatusCode(Integer.valueOf(result.statusCode));
            if (config.isDiagnosticsEnabled()) {
                response.setDiagnostics(diagnostics(response, body));
            }
            return response;
        } catch (Exception ex) {
            AiChatResponseDto response = AiChatResponseDto.unavailable(helper.redactSecrets(ex.toString(), config.getApiKey()));
            response.setProviderName(config.getProviderName());
            response.setErrorCode(errorCode(ex));
            response.setElapsedMillis(Long.valueOf(System.currentTimeMillis() - started));
            response.setRetryCount(Integer.valueOf(retryCount));
            response.setDiagnostics(helper.redactSecrets(ex.toString(), config.getApiKey()));
            return response;
        }
    }

    private AiChatRequestDto normalize(AiChatRequestDto request, boolean stream) {
        AiChatRequestDto safe = request == null ? new AiChatRequestDto() : request;
        if (!hasText(safe.getModelName()) || AiConstants.DEFAULT_MODEL_NAME.equals(safe.getModelName())) {
            safe.setModelName(config.getModelName());
        }
        if (safe.getTimeoutSeconds() == null || safe.getTimeoutSeconds().intValue() <= 0) {
            safe.setTimeoutSeconds(Integer.valueOf(config.getTimeoutSeconds()));
        }
        safe.setRetryOnce(config.isRetryOn5xx());
        safe.setStream(stream);
        return safe;
    }

    private String diagnostics(AiChatResponseDto response, String requestBody) {
        int messageCount = requestBody == null ? 0 : count(requestBody, "\"role\"");
        return "provider=" + config.getProviderName()
                + ", model=" + response.getModelName()
                + ", statusCode=" + response.getStatusCode()
                + ", errorCode=" + response.getErrorCode()
                + ", elapsedMillis=" + response.getElapsedMillis()
                + ", retryCount=" + response.getRetryCount()
                + ", messageCount=" + messageCount;
    }

    private boolean isRetryable(int statusCode) {
        return statusCode >= 500 && statusCode <= 599;
    }

    private String classify(int statusCode) {
        if (statusCode == 401 || statusCode == 403) return AiConstants.ERROR_AUTHENTICATION;
        if (statusCode >= 400 && statusCode < 500) return AiConstants.ERROR_BAD_REQUEST;
        return AiConstants.ERROR_PROVIDER;
    }

    private String errorCode(Exception ex) {
        if (ex instanceof SocketTimeoutException) {
            return AiConstants.ERROR_TIMEOUT;
        }
        return AiConstants.ERROR_NETWORK;
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }

    private int count(String text, String needle) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(needle, index)) >= 0) {
            count++;
            index += needle.length();
        }
        return count;
    }

    private static AiProviderConfig config(String endpoint, String apiKey) {
        AiProviderConfig config = new AiProviderConfig();
        config.setEnabled(true);
        config.setEndpoint(endpoint);
        config.setApiKey(apiKey);
        return config;
    }

    public interface Transport {
        HttpResult post(String endpoint, String apiKey, String body, int timeoutSeconds) throws Exception;
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
        public HttpResult post(String endpoint, String apiKey, String body, int timeoutSeconds) throws Exception {
            HttpURLConnection connection = (HttpURLConnection) new URL(endpoint).openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(timeoutSeconds * 1000);
            connection.setReadTimeout(timeoutSeconds * 1000);
            connection.setDoOutput(true);
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
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
                if (out.length() > 0) out.append('\n');
                out.append(line);
            }
            reader.close();
            return out.toString();
        }
    }
}
