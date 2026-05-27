package v620.base.helper.career;

import org.junit.jupiter.api.Test;
import v620.cc001.base.common.dto.career.AssistantChatConstants;
import v620.cc001.base.common.dto.career.AssistantChatMessageDto;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AssistantChatHelperTest {

    private final AssistantChatHelper helper = new AssistantChatHelper();

    @Test
    void normalizesPersonaWithMentorFallback() {
        assertEquals(AssistantChatConstants.PERSONA_MENTOR, helper.normalizePersona(null));
        assertEquals(AssistantChatConstants.PERSONA_MENTOR, helper.normalizePersona("unknown"));
        assertEquals(AssistantChatConstants.PERSONA_CHALLENGER, helper.normalizePersona("challenger"));
        assertEquals(AssistantChatConstants.PERSONA_INTERVIEWER, helper.normalizePersona(" INTERVIEWER "));
    }

    @Test
    void returnsDistinctPersonaPrompts() {
        assertTrue(helper.systemPromptFor("MENTOR").contains("小职"));
        assertTrue(helper.systemPromptFor("CHALLENGER").contains("小严"));
        assertTrue(helper.systemPromptFor("INTERVIEWER").contains("小面"));
    }

    @Test
    void buildsOrderedContextWithOptionalSnippets() {
        List<AssistantChatMessageDto> history = new ArrayList<AssistantChatMessageDto>();
        history.add(message("USER", "第一轮"));
        history.add(message("assistant", "继续"));

        List<AssistantChatMessageDto> messages = helper.buildMessages(history, "本轮问题", "MENTOR",
                "[PROFILE] 目标 Java", "[MEMORY] 上次聊简历", "[FACTS] 会 Spring");

        assertEquals(4, messages.size());
        assertEquals(AssistantChatConstants.ROLE_SYSTEM, messages.get(0).getRole());
        assertTrue(messages.get(0).getContent().contains("[PROFILE] 目标 Java"));
        assertTrue(messages.get(0).getContent().contains("[MEMORY] 上次聊简历"));
        assertTrue(messages.get(0).getContent().contains("[FACTS] 会 Spring"));
        assertEquals("第一轮", messages.get(1).getContent());
        assertEquals(AssistantChatConstants.ROLE_ASSISTANT, messages.get(2).getRole());
        assertEquals("本轮问题", messages.get(3).getContent());
    }

    @Test
    void rejectsBlankMessageAndDerivesTitle() {
        assertThrows(IllegalArgumentException.class, new ThrowingRunnableAdapter(new Runnable() {
            public void run() {
                helper.buildMessages(null, "  ", null, null, null, null);
            }
        }));
        assertEquals("新的求职对话", helper.defaultTitle(" "));
        assertEquals("帮我优化 Java 后端简历并准备面试追...", helper.titleFromFirstMessage("帮我优化 Java 后端简历并准备面试追问和复盘"));
    }

    private AssistantChatMessageDto message(String role, String content) {
        AssistantChatMessageDto message = new AssistantChatMessageDto();
        message.setRole(role);
        message.setContent(content);
        return message;
    }

    private static class ThrowingRunnableAdapter implements org.junit.jupiter.api.function.Executable {
        private final Runnable runnable;

        ThrowingRunnableAdapter(Runnable runnable) {
            this.runnable = runnable;
        }

        public void execute() {
            runnable.run();
        }
    }
}
