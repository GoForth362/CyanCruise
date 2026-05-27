package v620.base.helper.career;

import v620.cc001.base.common.dto.career.AssistantChatConstants;
import v620.cc001.base.common.dto.career.AssistantChatMessageDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Pure Java helper rules for assistant chat persona and prompt assembly.
 */
public class AssistantChatHelper {

    private static final int TITLE_LIMIT = 20;

    private static final String PROMPT_MENTOR =
            "你是“小职”，面向中国大学生和应届生的 AI 求职教练。你的任务是帮助用户完成职业规划、简历优化、面试准备和技能提升。"
                    + "请始终使用简体中文回答，除非用户明确要求翻译英文原文。回答要温和、具体、可执行，不要空泛鼓励。"
                    + "如果用户的问题和求职无关，请自然地拉回到求职准备。";

    private static final String PROMPT_CHALLENGER =
            "你是“小严”，面向中国大学生和应届生的严格求职反馈教练。你的任务是指出目标、简历、面试回答和行动计划中的薄弱处。"
                    + "请始终使用简体中文回答，语气直接但公平，不做空泛鼓励。遇到含糊表达时，要追问证据、成果和下一步行动。";

    private static final String PROMPT_INTERVIEWER =
            "你是“小面”，负责模拟真实 HR、技术面和岗位面试。你的任务是一次只问一个问题，听完回答后给出简短专业反馈，再继续追问。"
                    + "请始终使用简体中文回答，语气正式、中立，像真实面试官。不要在没有反馈的情况下直接跳到下一题。";

    public String normalizePersona(String persona) {
        if (!hasText(persona)) {
            return AssistantChatConstants.PERSONA_MENTOR;
        }
        String normalized = persona.trim().toUpperCase();
        if (AssistantChatConstants.PERSONA_CHALLENGER.equals(normalized)) {
            return AssistantChatConstants.PERSONA_CHALLENGER;
        }
        if (AssistantChatConstants.PERSONA_INTERVIEWER.equals(normalized)) {
            return AssistantChatConstants.PERSONA_INTERVIEWER;
        }
        return AssistantChatConstants.PERSONA_MENTOR;
    }

    public String systemPromptFor(String persona) {
        String normalized = normalizePersona(persona);
        if (AssistantChatConstants.PERSONA_CHALLENGER.equals(normalized)) {
            return PROMPT_CHALLENGER;
        }
        if (AssistantChatConstants.PERSONA_INTERVIEWER.equals(normalized)) {
            return PROMPT_INTERVIEWER;
        }
        return PROMPT_MENTOR;
    }

    public String buildSystemPrompt(String persona,
                                    String profileSnippet,
                                    String memorySummary,
                                    String userFactsSnippet) {
        StringBuilder prompt = new StringBuilder(systemPromptFor(persona));
        appendSection(prompt, profileSnippet);
        appendSection(prompt, memorySummary);
        appendSection(prompt, userFactsSnippet);
        return prompt.toString();
    }

    public List<AssistantChatMessageDto> buildMessages(List<AssistantChatMessageDto> history,
                                                       String userMessage,
                                                       String persona,
                                                       String profileSnippet,
                                                       String memorySummary,
                                                       String userFactsSnippet) {
        requireMessage(userMessage, "message is required");
        List<AssistantChatMessageDto> messages = new ArrayList<AssistantChatMessageDto>();
        messages.add(message(AssistantChatConstants.ROLE_SYSTEM,
                buildSystemPrompt(persona, profileSnippet, memorySummary, userFactsSnippet)));
        if (history != null) {
            for (AssistantChatMessageDto item : history) {
                if (item == null || !hasText(item.getContent())) {
                    continue;
                }
                messages.add(message(normalizeRole(item.getRole()), item.getContent().trim()));
            }
        }
        messages.add(message(AssistantChatConstants.ROLE_USER, userMessage.trim()));
        return messages;
    }

    public String normalizeRole(String role) {
        if (AssistantChatConstants.ROLE_SYSTEM.equalsIgnoreCase(role)) {
            return AssistantChatConstants.ROLE_SYSTEM;
        }
        if (AssistantChatConstants.ROLE_ASSISTANT.equalsIgnoreCase(role)) {
            return AssistantChatConstants.ROLE_ASSISTANT;
        }
        return AssistantChatConstants.ROLE_USER;
    }

    public void requireMessage(String message, String errorMessage) {
        if (!hasText(message)) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public String defaultTitle(String title) {
        return hasText(title) ? title.trim() : AssistantChatConstants.DEFAULT_TITLE;
    }

    public boolean isDefaultTitle(String title) {
        if (!hasText(title)) {
            return true;
        }
        String value = title.trim();
        return AssistantChatConstants.DEFAULT_TITLE.equals(value) || "New Conversation".equals(value);
    }

    public String titleFromFirstMessage(String userMessage) {
        if (!hasText(userMessage)) {
            return AssistantChatConstants.DEFAULT_TITLE;
        }
        String trimmed = userMessage.trim();
        if (trimmed.length() <= TITLE_LIMIT) {
            return trimmed;
        }
        return trimmed.substring(0, TITLE_LIMIT) + "...";
    }

    private AssistantChatMessageDto message(String role, String content) {
        AssistantChatMessageDto message = new AssistantChatMessageDto();
        message.setRole(role);
        message.setContent(content);
        return message;
    }

    private void appendSection(StringBuilder prompt, String value) {
        if (hasText(value)) {
            prompt.append("\n\n").append(value.trim());
        }
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
