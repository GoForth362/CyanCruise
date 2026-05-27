package v620.base.helper.ai;

import org.junit.jupiter.api.Test;
import v620.cc001.base.common.dto.ai.AiChatResponseDto;
import v620.cc001.base.common.dto.ai.AiConstants;
import v620.cc001.base.common.dto.ai.AiMessageDto;
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
}
