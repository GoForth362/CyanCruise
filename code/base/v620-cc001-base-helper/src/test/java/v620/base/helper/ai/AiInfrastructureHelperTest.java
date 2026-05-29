package v620.base.helper.ai;

import org.junit.jupiter.api.Test;
import v620.cc001.base.common.dto.ai.AiChatResponseDto;
import v620.cc001.base.common.dto.ai.AiConstants;
import v620.cc001.base.common.dto.ai.AiMessageDto;
import v620.cc001.base.common.dto.ai.AiStreamEventDto;
import v620.cc001.base.common.dto.ai.AiToolCallDto;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AiInfrastructureHelperTest {

    @Test
    void injectsDefaultSystemOnlyWhenMissing() {
        AiMessageHelper helper = new AiMessageHelper();
        List<AiMessageDto> injected = helper.withDefaultSystemPrompt(
                Collections.singletonList(new AiMessageDto("user", "hello")), "system prompt");
        List<AiMessageDto> preserved = helper.withDefaultSystemPrompt(
                Arrays.asList(new AiMessageDto("system", "custom"), new AiMessageDto("user", "hello")), "system prompt");

        assertEquals(AiConstants.ROLE_SYSTEM, injected.get(0).getRole());
        assertEquals("system prompt", injected.get(0).getContent());
        assertEquals("custom", preserved.get(0).getContent());
        assertEquals(2, preserved.size());
    }

    @Test
    void extractsJsonFromFenceAndSurroundingText() {
        AiJsonHelper helper = new AiJsonHelper();

        assertEquals("{\"a\":1}", helper.extractJsonObject("prefix ```json\n{\"a\":1}\n``` suffix"));
        assertEquals("[{\"a\":1}]", helper.extractJsonArray("noise [{\"a\":1}] tail"));
        assertTrue(helper.containsRequiredFields("{\"target_role\":\"Java\",\"weekly_focus\":[]}",
                Arrays.asList("target_role", "weekly_focus")));
        assertFalse(helper.containsRequiredFields("{\"target_role\":\"Java\"}",
                Arrays.asList("target_role", "weekly_focus")));
    }

    @Test
    void functionCallingUsesServerUserAndHandlesFailures() {
        AiFunctionCallingLoopHelper helper = new AiFunctionCallingLoopHelper();
        AiToolCallDto call = new AiToolCallDto();
        call.setName("profile");
        call.setArgumentsJson("{\"userId\":\"bad\"}");

        String result = helper.resolveToolResult(call, new AiFunctionCallingLoopHelper.ToolExecutor() {
            public boolean supports(String toolName) {
                return "profile".equals(toolName);
            }

            public String execute(String toolName, String argumentsJson, String serverUserId) {
                return "user=" + serverUserId + ", args=" + argumentsJson;
            }
        }, "server-user");

        assertTrue(result.contains("user=server-user"));
        assertTrue(helper.resolveToolResult(call, null, "server-user").contains(AiConstants.ERROR_TOOL_NOT_FOUND));

        AiChatResponseDto response = new AiChatResponseDto();
        response.setFinishReason(AiConstants.FINISH_TOOL_CALLS);
        response.setToolCalls(Collections.singletonList(call));
        assertTrue(helper.shouldContinue(0, 5, response));
        assertFalse(helper.shouldContinue(5, 5, response));
        assertTrue(helper.finalReplyOrFallback("", 5, 5).contains(AiConstants.ERROR_TOOL_LIMIT));
    }

    @Test
    void parsesOpenAiCompatibleResponseAndRedactsSecrets() {
        OpenAiCompatibleProviderHelper helper = new OpenAiCompatibleProviderHelper();
        String body = "{\"model\":\"qwen-max\",\"choices\":[{\"finish_reason\":\"tool_calls\",\"message\":{\"content\":\"hello\",\"tool_calls\":[{\"id\":\"call-1\",\"type\":\"function\",\"function\":{\"name\":\"profile\",\"arguments\":\"{\\\"userId\\\":\\\"bad\\\"}\"}}]}}],\"usage\":{\"prompt_tokens\":2,\"completion_tokens\":3,\"total_tokens\":5}}";

        AiChatResponseDto response = helper.parseChatResponse(body, "provider", 12L, 1);

        assertEquals("hello", response.getContent());
        assertEquals("qwen-max", response.getModelName());
        assertEquals(Integer.valueOf(2), response.getUsage().getPromptTokens());
        assertEquals(Integer.valueOf(3), response.getUsage().getCompletionTokens());
        assertEquals(Integer.valueOf(5), response.getUsage().getTotalTokens());
        assertEquals(1, response.getToolCalls().size());
        assertEquals("profile", response.getToolCalls().get(0).getName());
        assertTrue(response.getToolCalls().get(0).getArgumentsJson().contains("bad"));

        AiChatResponseDto invalid = helper.parseChatResponse("{\"choices\":[]}", "provider", 0L, 0);
        assertEquals(AiConstants.ERROR_INVALID_RESPONSE, invalid.getErrorCode());

        String redacted = helper.redactSecrets("Authorization: Bearer sk-secret apiKey=sk-secret", "sk-secret");
        assertFalse(redacted.contains("sk-secret"));
        assertTrue(redacted.contains("***"));
    }

    @Test
    void normalizesOpenAiCompatibleStreamChunks() {
        OpenAiCompatibleProviderHelper helper = new OpenAiCompatibleProviderHelper();

        List<AiStreamEventDto> events = helper.parseStreamLines(Arrays.asList(
                "data: {\"choices\":[{\"delta\":{\"content\":\"你\"}}]}",
                "data: {\"choices\":[{\"delta\":{\"content\":\"好\"}}]}",
                "data: [DONE]"));

        assertEquals(AiConstants.STREAM_TOKEN, events.get(0).getType());
        assertEquals("你", events.get(0).getData());
        assertEquals(Integer.valueOf(1), events.get(1).getIndex());
        assertEquals(AiConstants.STREAM_DONE, events.get(2).getType());
    }
}
