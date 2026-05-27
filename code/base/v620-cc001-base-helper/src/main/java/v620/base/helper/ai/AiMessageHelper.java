package v620.base.helper.ai;

import v620.cc001.base.common.dto.ai.AiConstants;
import v620.cc001.base.common.dto.ai.AiMessageDto;

import java.util.ArrayList;
import java.util.List;

public class AiMessageHelper {

    private static final String DEFAULT_SYSTEM_PROMPT =
            "You are a professional career assistant. Reply concisely and stay on topic.";

    public List<AiMessageDto> withDefaultSystemPrompt(List<AiMessageDto> messages) {
        return withDefaultSystemPrompt(messages, DEFAULT_SYSTEM_PROMPT);
    }

    public List<AiMessageDto> withDefaultSystemPrompt(List<AiMessageDto> messages, String defaultPrompt) {
        List<AiMessageDto> result = copy(messages);
        if (!hasSystemFirst(result) && hasText(defaultPrompt)) {
            result.add(0, new AiMessageDto(AiConstants.ROLE_SYSTEM, defaultPrompt.trim()));
        }
        return result;
    }

    public boolean hasSystemFirst(List<AiMessageDto> messages) {
        return messages != null
                && !messages.isEmpty()
                && AiConstants.ROLE_SYSTEM.equalsIgnoreCase(messages.get(0).getRole());
    }

    public List<AiMessageDto> copy(List<AiMessageDto> messages) {
        List<AiMessageDto> result = new ArrayList<AiMessageDto>();
        if (messages == null) {
            return result;
        }
        for (AiMessageDto message : messages) {
            AiMessageDto copy = new AiMessageDto();
            copy.setRole(normalizeRole(message == null ? null : message.getRole()));
            copy.setContent(message == null ? null : message.getContent());
            copy.setToolCallId(message == null ? null : message.getToolCallId());
            copy.setToolCalls(message == null ? null : message.getToolCalls());
            result.add(copy);
        }
        return result;
    }

    public String normalizeRole(String role) {
        if (!hasText(role)) {
            return AiConstants.ROLE_USER;
        }
        String value = role.trim().toLowerCase();
        if (AiConstants.ROLE_SYSTEM.equals(value)
                || AiConstants.ROLE_ASSISTANT.equals(value)
                || AiConstants.ROLE_TOOL.equals(value)) {
            return value;
        }
        return AiConstants.ROLE_USER;
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
