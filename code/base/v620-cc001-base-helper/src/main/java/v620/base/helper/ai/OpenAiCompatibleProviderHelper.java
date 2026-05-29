package v620.base.helper.ai;

import v620.cc001.base.common.dto.ai.AiChatRequestDto;
import v620.cc001.base.common.dto.ai.AiChatResponseDto;
import v620.cc001.base.common.dto.ai.AiConstants;
import v620.cc001.base.common.dto.ai.AiMessageDto;
import v620.cc001.base.common.dto.ai.AiStreamEventDto;
import v620.cc001.base.common.dto.ai.AiToolCallDto;
import v620.cc001.base.common.dto.ai.AiToolSchemaDto;
import v620.cc001.base.common.dto.ai.AiUsageDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OpenAiCompatibleProviderHelper {

    private static final Pattern STRING_FIELD_TEMPLATE = Pattern.compile("\"%s\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"");
    private static final Pattern INT_FIELD_TEMPLATE = Pattern.compile("\"%s\"\\s*:\\s*(\\d+)");
    private static final Pattern CHOICES_PATTERN = Pattern.compile("\"choices\"\\s*:\\s*\\[");
    private static final Pattern TOOL_CALL_PATTERN = Pattern.compile("\\{\\s*\"id\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"[\\s\\S]*?\"function\"\\s*:\\s*\\{[\\s\\S]*?\"name\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"[\\s\\S]*?\"arguments\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"");

    public String buildChatRequestJson(AiChatRequestDto request) {
        StringBuilder out = new StringBuilder();
        out.append('{');
        appendField(out, "model", request == null ? null : request.getModelName());
        out.append(",\"messages\":[");
        List<AiMessageDto> messages = request == null ? null : request.getMessages();
        if (messages != null) {
            for (int i = 0; i < messages.size(); i++) {
                if (i > 0) out.append(',');
                appendMessage(out, messages.get(i));
            }
        }
        out.append(']');
        if (request != null && request.getTools() != null && !request.getTools().isEmpty()) {
            out.append(",\"tools\":[");
            for (int i = 0; i < request.getTools().size(); i++) {
                if (i > 0) out.append(',');
                appendTool(out, request.getTools().get(i));
            }
            out.append("],\"tool_choice\":\"auto\"");
        }
        if (request != null && request.isStream()) {
            out.append(",\"stream\":true");
        }
        out.append('}');
        return out.toString();
    }

    public AiChatResponseDto parseChatResponse(String body, String providerName, long elapsedMillis, int retryCount) {
        AiChatResponseDto response = new AiChatResponseDto();
        response.setProviderName(providerName);
        response.setElapsedMillis(Long.valueOf(elapsedMillis));
        response.setRetryCount(Integer.valueOf(retryCount));
        if (!hasText(body) || !CHOICES_PATTERN.matcher(body).find()) {
            return error(response, AiConstants.ERROR_INVALID_RESPONSE, "provider response missing choices");
        }
        String model = stringField(body, "model");
        if (hasText(model)) {
            response.setModelName(model);
        }
        String content = nestedStringField(body, "message", "content");
        response.setContent(content);
        String finishReason = stringField(body, "finish_reason");
        response.setFinishReason(hasText(finishReason) ? finishReason : AiConstants.FINISH_STOP);
        response.setUsage(parseUsage(body));
        response.setToolCalls(parseToolCalls(body));
        response.setFallback(false);
        if (!hasText(content) && response.getToolCalls().isEmpty()) {
            return error(response, AiConstants.ERROR_INVALID_RESPONSE, "provider response missing content and tool calls");
        }
        return response;
    }

    public AiChatResponseDto parseErrorResponse(int statusCode,
                                                String body,
                                                String providerName,
                                                long elapsedMillis,
                                                int retryCount) {
        AiChatResponseDto response = new AiChatResponseDto();
        response.setProviderName(providerName);
        response.setStatusCode(Integer.valueOf(statusCode));
        response.setElapsedMillis(Long.valueOf(elapsedMillis));
        response.setRetryCount(Integer.valueOf(retryCount));
        String code;
        if (statusCode == 401 || statusCode == 403) {
            code = AiConstants.ERROR_AUTHENTICATION;
        } else if (statusCode >= 400 && statusCode < 500) {
            code = AiConstants.ERROR_BAD_REQUEST;
        } else {
            code = AiConstants.ERROR_PROVIDER;
        }
        String message = stringField(body, "message");
        return error(response, code, hasText(message) ? message : "provider returned status " + statusCode);
    }

    public List<AiStreamEventDto> parseStreamLines(List<String> lines) {
        List<AiStreamEventDto> events = new ArrayList<AiStreamEventDto>();
        if (lines == null) {
            events.add(AiStreamEventDto.done(0));
            return events;
        }
        int index = 0;
        for (String line : lines) {
            if (!hasText(line)) {
                continue;
            }
            String payload = line.trim();
            if (payload.startsWith("data:")) {
                payload = payload.substring(5).trim();
            }
            if ("[DONE]".equals(payload)) {
                events.add(AiStreamEventDto.done(index));
                return events;
            }
            String token = nestedStringField(payload, "delta", "content");
            if (hasText(token)) {
                events.add(AiStreamEventDto.token(token, index++));
            }
        }
        events.add(AiStreamEventDto.done(index));
        return events;
    }

    public String redactSecrets(String text, String apiKey) {
        if (text == null) {
            return null;
        }
        String out = text;
        if (hasText(apiKey)) {
            out = out.replace(apiKey, "***");
        }
        out = out.replaceAll("(?i)Bearer\\s+[A-Za-z0-9._\\-+/=]+", "Bearer ***");
        out = out.replaceAll("(?i)(Authorization\\s*[:=]\\s*)[^\\s,;}]+", "$1***");
        out = out.replaceAll("(?i)(api[_-]?key\\s*[:=]\\s*)[^\\s,;}]+", "$1***");
        return out;
    }

    private void appendMessage(StringBuilder out, AiMessageDto message) {
        out.append('{');
        appendField(out, "role", message == null ? null : message.getRole());
        out.append(',');
        appendField(out, "content", message == null ? null : message.getContent());
        if (message != null && hasText(message.getToolCallId())) {
            out.append(',');
            appendField(out, "tool_call_id", message.getToolCallId());
        }
        if (message != null && message.getToolCalls() != null && !message.getToolCalls().isEmpty()) {
            out.append(",\"tool_calls\":[");
            for (int i = 0; i < message.getToolCalls().size(); i++) {
                if (i > 0) out.append(',');
                appendToolCall(out, message.getToolCalls().get(i));
            }
            out.append(']');
        }
        out.append('}');
    }

    private void appendTool(StringBuilder out, AiToolSchemaDto schema) {
        out.append("{\"type\":\"function\",\"function\":{");
        appendField(out, "name", schema == null ? null : schema.getName());
        out.append(',');
        appendField(out, "description", schema == null ? null : schema.getDescription());
        out.append(",\"parameters\":{\"type\":\"object\",\"properties\":{");
        if (schema != null && schema.getParameters() != null) {
            int i = 0;
            for (Map.Entry<String, String> entry : schema.getParameters().entrySet()) {
                if (i++ > 0) out.append(',');
                appendField(out, entry.getKey(), entry.getValue());
            }
        }
        out.append("}}}}");
    }

    private void appendToolCall(StringBuilder out, AiToolCallDto call) {
        out.append('{');
        appendField(out, "id", call == null ? null : call.getId());
        out.append(",\"type\":\"function\",\"function\":{");
        appendField(out, "name", call == null ? null : call.getName());
        out.append(',');
        appendField(out, "arguments", call == null ? null : call.getArgumentsJson());
        out.append("}}");
    }

    private void appendField(StringBuilder out, String key, String value) {
        out.append('"').append(escape(key)).append("\":");
        if (value == null) {
            out.append("null");
        } else {
            out.append('"').append(escape(value)).append('"');
        }
    }

    private AiUsageDto parseUsage(String body) {
        AiUsageDto usage = new AiUsageDto();
        usage.setPromptTokens(Integer.valueOf(intField(body, "prompt_tokens", 0)));
        usage.setCompletionTokens(Integer.valueOf(intField(body, "completion_tokens", 0)));
        usage.setTotalTokens(Integer.valueOf(intField(body, "total_tokens", 0)));
        return usage;
    }

    private List<AiToolCallDto> parseToolCalls(String body) {
        List<AiToolCallDto> calls = new ArrayList<AiToolCallDto>();
        Matcher matcher = TOOL_CALL_PATTERN.matcher(body == null ? "" : body);
        while (matcher.find()) {
            AiToolCallDto call = new AiToolCallDto();
            call.setId(unescape(matcher.group(1)));
            call.setName(unescape(matcher.group(2)));
            call.setArgumentsJson(unescape(matcher.group(3)));
            calls.add(call);
        }
        return calls;
    }

    private AiChatResponseDto error(AiChatResponseDto response, String code, String message) {
        response.setFallback(true);
        response.setFinishReason(AiConstants.FINISH_ERROR);
        response.setErrorCode(code);
        response.setErrorMessage(message);
        response.setDiagnostics(message);
        return response;
    }

    private String nestedStringField(String body, String parent, String field) {
        int parentIndex = body == null ? -1 : body.indexOf("\"" + parent + "\"");
        if (parentIndex < 0) {
            return null;
        }
        return stringField(body.substring(parentIndex), field);
    }

    private String stringField(String body, String field) {
        Matcher matcher = Pattern.compile(String.format(STRING_FIELD_TEMPLATE.pattern(), Pattern.quote(field))).matcher(body == null ? "" : body);
        return matcher.find() ? unescape(matcher.group(1)) : null;
    }

    private int intField(String body, String field, int defaultValue) {
        Matcher matcher = Pattern.compile(String.format(INT_FIELD_TEMPLATE.pattern(), Pattern.quote(field))).matcher(body == null ? "" : body);
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : defaultValue;
    }

    private String escape(String text) {
        return text == null ? "" : text.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

    private String unescape(String text) {
        if (text == null) return null;
        return text.replace("\\n", "\n").replace("\\r", "\r").replace("\\t", "\t")
                .replace("\\\"", "\"").replace("\\\\", "\\");
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
