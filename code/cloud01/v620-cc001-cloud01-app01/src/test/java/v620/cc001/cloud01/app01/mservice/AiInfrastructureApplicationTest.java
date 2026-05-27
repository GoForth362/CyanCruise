package v620.cc001.cloud01.app01.mservice;

import org.junit.jupiter.api.Test;
import v620.cc001.base.common.dto.ai.AiChatRequestDto;
import v620.cc001.base.common.dto.ai.AiChatResponseDto;
import v620.cc001.base.common.dto.ai.AiConstants;
import v620.cc001.base.common.dto.ai.AiMessageDto;
import v620.cc001.base.common.dto.ai.AiStreamEventDto;
import v620.cc001.base.common.dto.ai.AiToolCallDto;
import v620.cc001.base.common.dto.ai.AiToolSchemaDto;
import v620.cc001.base.common.dto.career.AssistantChatMessageDto;
import v620.cc001.base.common.dto.career.AssistantChatResponse;
import v620.cc001.base.common.dto.career.CareerAgentTodayDto;
import v620.cc001.base.common.dto.career.CareerPlanRecordDto;
import v620.cc001.base.common.dto.career.ResumeDiagnosisRequest;
import v620.cc001.cloud01.app01.mservice.ai.AiFunctionCallingService;
import v620.cc001.cloud01.app01.mservice.ai.AiGateway;
import v620.cc001.cloud01.app01.mservice.ai.AiTool;
import v620.cc001.cloud01.app01.mservice.ai.AiToolRegistry;
import v620.cc001.cloud01.app01.mservice.ai.DefaultAiGateway;
import v620.cc001.cloud01.app01.mservice.ai.FakeAiProviderAdapter;
import v620.cc001.cloud01.app01.mservice.ai.UnavailableAiProviderAdapter;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AiInfrastructureApplicationTest {

    @Test
    void defaultGatewayReturnsUnavailableWhenProviderMissing() {
        AiGateway gateway = new DefaultAiGateway(new UnavailableAiProviderAdapter());

        AiChatResponseDto response = gateway.chat(new AiChatRequestDto());

        assertEquals(AiConstants.ERROR_UNAVAILABLE, response.getErrorCode());
        assertTrue(response.isFallback());
        assertEquals(AiConstants.STREAM_ERROR, gateway.stream(new AiChatRequestDto()).get(0).getType());
    }

    @Test
    void assistantGeneratorUsesGatewayAndUsage() {
        AiGatewayAssistantChatGenerator generator = new AiGatewayAssistantChatGenerator(
                new DefaultAiGateway(new FakeAiProviderAdapter("AI reply", "fake-model")));
        AssistantChatMessageDto message = new AssistantChatMessageDto();
        message.setRole("user");
        message.setContent("hello");

        AssistantChatResponse response = generator.chat("u1", "MENTOR", Collections.singletonList(message));

        assertEquals("AI reply", response.getReply());
        assertEquals("fake-model", response.getModelName());
        assertEquals(Integer.valueOf(1), response.getCompletionTokens());
    }

    @Test
    void resumeAnalyzerFallsBackWhenGatewayUnavailable() {
        AiGatewayResumeDiagnosisAnalyzer analyzer = new AiGatewayResumeDiagnosisAnalyzer(
                new DefaultAiGateway(new UnavailableAiProviderAdapter()), new DefaultResumeDiagnosisAnalyzer());
        ResumeDiagnosisRequest request = new ResumeDiagnosisRequest();
        request.setJobDescription("Java Spring");

        String raw = analyzer.analyze(request, "Java Spring project");

        assertTrue(raw.contains("overallScore"));
    }

    @Test
    void structuredCareerPlanRequiresJsonFields() {
        AiGatewayCareerPlanGenerator generator = new AiGatewayCareerPlanGenerator(
                new DefaultAiGateway(new FakeAiProviderAdapter(
                        "```json\n{\"target_role\":\"Java\",\"weekly_focus\":[\"resume\"]}\n```", "plan-model")));

        CareerPlanRecordDto plan = generator.generate("u2", "Java", null);

        assertEquals("u2", plan.getUserId());
        assertEquals("Java", plan.getTargetRole());
        assertEquals("plan-model", plan.getModelUsed());
        assertTrue(plan.getStartStateSummary().contains("weekly_focus"));
    }

    @Test
    void taskDecompositionReturnsJsonArrayOrNullFallback() {
        TaskDecompositionAiService service = new TaskDecompositionAiService(
                new DefaultAiGateway(new FakeAiProviderAdapter("[{\"title\":\"step\"}]", "task-model")));
        CareerAgentTodayDto.Action action = new CareerAgentTodayDto.Action();
        action.setLabel("prepare resume");

        assertEquals("[{\"title\":\"step\"}]", service.decompose(action));
        assertNull(new TaskDecompositionAiService(new DefaultAiGateway(new UnavailableAiProviderAdapter())).decompose(action));
    }

    @Test
    void functionCallingInjectsServerUserAndRejectsUserIdSchema() {
        AiToolRegistry registry = new AiToolRegistry();
        registry.register(new AiTool() {
            public AiToolSchemaDto schema() {
                AiToolSchemaDto schema = new AiToolSchemaDto();
                schema.setName("profile");
                schema.getParameters().put("topic", "string");
                return schema;
            }

            public String execute(String argumentsJson, String serverUserId) {
                return "serverUser=" + serverUserId + ", args=" + argumentsJson;
            }
        });
        AiFunctionCallingService service = new AiFunctionCallingService(new ToolCallingGateway(), registry);

        String reply = service.chat(Collections.singletonList(new AiMessageDto("user", "use tool")), "safe-user");

        assertTrue(reply.contains("serverUser=safe-user"));

        AiToolRegistry bad = new AiToolRegistry();
        bad.register(new AiTool() {
            public AiToolSchemaDto schema() {
                AiToolSchemaDto schema = new AiToolSchemaDto();
                schema.setName("bad");
                schema.getParameters().put("userId", "string");
                return schema;
            }

            public String execute(String argumentsJson, String serverUserId) {
                return "";
            }
        });
        assertThrows(IllegalStateException.class, new ThrowingRunnableAdapter(new Runnable() {
            public void run() {
                bad.schemas();
            }
        }));
    }

    @Test
    void streamEventsAreOrderedAndComplete() {
        List<AiStreamEventDto> events = new DefaultAiGateway(new FakeAiProviderAdapter("好", "stream-model"))
                .stream(new AiChatRequestDto());

        assertEquals(AiConstants.STREAM_TOKEN, events.get(0).getType());
        assertEquals(Integer.valueOf(0), events.get(0).getIndex());
        assertEquals(AiConstants.STREAM_DONE, events.get(events.size() - 1).getType());
    }

    private static class ToolCallingGateway implements AiGateway {
        private int calls;

        public AiChatResponseDto chat(AiChatRequestDto request) {
            calls++;
            AiChatResponseDto response = new AiChatResponseDto();
            if (calls == 1) {
                response.setFinishReason(AiConstants.FINISH_TOOL_CALLS);
                AiToolCallDto call = new AiToolCallDto();
                call.setId("call-1");
                call.setName("profile");
                call.setArgumentsJson("{\"userId\":\"bad\"}");
                response.setToolCalls(Collections.singletonList(call));
                return response;
            }
            response.setContent(request.getMessages().get(request.getMessages().size() - 1).getContent());
            return response;
        }

        public List<AiStreamEventDto> stream(AiChatRequestDto request) {
            return Collections.emptyList();
        }
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
