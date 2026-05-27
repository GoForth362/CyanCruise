package v620.base.helper.ai;

import v620.cc001.base.common.dto.ai.AiChatResponseDto;
import v620.cc001.base.common.dto.ai.AiConstants;
import v620.cc001.base.common.dto.ai.AiToolCallDto;

import java.util.List;

public class AiFunctionCallingLoopHelper {

    public String resolveToolResult(AiToolCallDto call, ToolExecutor executor, String serverUserId) {
        if (call == null || !hasText(call.getName())) {
            return error(AiConstants.ERROR_TOOL_NOT_FOUND, "Tool not found");
        }
        if (executor == null || !executor.supports(call.getName())) {
            return error(AiConstants.ERROR_TOOL_NOT_FOUND, "Tool not found: " + call.getName());
        }
        try {
            String result = executor.execute(call.getName(), call.getArgumentsJson(), serverUserId);
            return hasText(result) ? result : "(empty result)";
        } catch (Exception e) {
            return error(AiConstants.ERROR_TOOL_FAILED, e.getMessage());
        }
    }

    public boolean shouldContinue(int callCount, int maxCalls, AiChatResponseDto response) {
        return callCount < maxCalls
                && response != null
                && AiConstants.FINISH_TOOL_CALLS.equals(response.getFinishReason())
                && response.getToolCalls() != null
                && !response.getToolCalls().isEmpty();
    }

    public String finalReplyOrFallback(String lastAssistantText, int callCount, int maxCalls) {
        if (hasText(lastAssistantText)) {
            return lastAssistantText;
        }
        if (callCount >= maxCalls) {
            return error(AiConstants.ERROR_TOOL_LIMIT, "Tool call limit reached");
        }
        return "";
    }

    public int countToolCalls(List<AiToolCallDto> calls) {
        return calls == null ? 0 : calls.size();
    }

    private String error(String code, String message) {
        return "{\"errorCode\":\"" + safe(code) + "\",\"message\":\"" + safe(message) + "\"}";
    }

    private String safe(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }

    public interface ToolExecutor {
        boolean supports(String toolName);

        String execute(String toolName, String argumentsJson, String serverUserId);
    }
}
