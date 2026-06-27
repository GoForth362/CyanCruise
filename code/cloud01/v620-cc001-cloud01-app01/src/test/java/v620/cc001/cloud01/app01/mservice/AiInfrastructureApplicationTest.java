package v620.cc001.cloud01.app01.mservice;

import v620.cc001.cloud01.app01.mservice.ai.impl.AiGatewayAssistantChatGenerator;
import v620.cc001.cloud01.app01.mservice.ai.impl.AiGatewayCareerPlanGenerator;
import v620.cc001.cloud01.app01.mservice.ai.impl.AiGatewayResumeDiagnosisAnalyzer;
import v620.cc001.cloud01.app01.mservice.ai.impl.CompatibleEndpointAiProviderAdapter;
import v620.cc001.cloud01.app01.mservice.ai.impl.DefaultAiGateway;
import v620.cc001.cloud01.app01.mservice.ai.impl.DefaultResumeDiagnosisAnalyzer;
import v620.cc001.cloud01.app01.mservice.ai.impl.FakeAiProviderAdapter;
import v620.cc001.cloud01.app01.mservice.ai.impl.UnavailableAiProviderAdapter;
import v620.cc001.cloud01.app01.mservice.ai.impl.AiGatewayAssistantChatGenerator;
import v620.cc001.cloud01.app01.mservice.ai.impl.AiGatewayCareerPlanGenerator;
import v620.cc001.cloud01.app01.mservice.ai.impl.AiGatewayResumeDiagnosisAnalyzer;
import v620.cc001.cloud01.app01.mservice.ai.impl.DefaultResumeDiagnosisAnalyzer;
import v620.cc001.cloud01.app01.mservice.ai.TaskDecompositionAiService;
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
import v620.cc001.cloud01.app01.mservice.ai.AiProviderAdapterFactory;
import v620.cc001.cloud01.app01.mservice.ai.AiProviderConfig;
import v620.cc001.cloud01.app01.mservice.ai.AiTool;
import v620.cc001.cloud01.app01.mservice.ai.AiToolRegistry;
import v620.cc001.cloud01.app01.mservice.ai.impl.CompatibleEndpointAiProviderAdapter;
import v620.cc001.cloud01.app01.mservice.ai.impl.DefaultAiGateway;
import v620.cc001.cloud01.app01.mservice.ai.impl.FakeAiProviderAdapter;
import v620.cc001.cloud01.app01.mservice.ai.impl.UnavailableAiProviderAdapter;

import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
                        "```json\n{\"target_role\":\"Java\",\"phases\":[{\"title\":\"prepare\",\"goal\":\"ready\",\"actions\":[\"resume\"],\"kpis\":[\"profile\"],\"sub_stages\":[]}],\"weekly_plan\":{\"week_title\":\"start\",\"actions\":[\"resume\"],\"deliverables\":[\"profile\"]},\"daily_suggestions\":[\"read one JD\"]}\n```", "plan-model")));

        CareerPlanRecordDto plan = generator.generate("u2", "Java", null);

        assertEquals("u2", plan.getUserId());
        assertEquals("Java", plan.getTargetRole());
        assertEquals("plan-model", plan.getModelUsed());
        assertTrue(plan.getStartStateSummary().contains("weekly_plan"));
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

    @Test
    void providerFactoryIsDisabledSafeByDefault() {
        AiProviderConfig config = new AiProviderConfig();
        config.setEnabled(true);
        config.setEndpoint("https://example.test/v1/chat/completions");

        AiChatResponseDto response = AiProviderAdapterFactory.fromConfig(config).chat(new AiChatRequestDto());

        assertEquals(AiConstants.ERROR_UNAVAILABLE, response.getErrorCode());
        assertTrue(response.isFallback());
    }

    @Test
    void compatibleProviderMapsSuccessAndDiagnostics() {
        AiProviderConfig config = enabledProvider();
        config.setDiagnosticsEnabled(true);
        RecordingTransport transport = new RecordingTransport(
                new CompatibleEndpointAiProviderAdapter.HttpResult(200,
                        "{\"model\":\"qwen-max\",\"choices\":[{\"finish_reason\":\"stop\",\"message\":{\"content\":\"ok\"}}],\"usage\":{\"prompt_tokens\":1,\"completion_tokens\":2,\"total_tokens\":3}}"));
        CompatibleEndpointAiProviderAdapter adapter = new CompatibleEndpointAiProviderAdapter(config, null, transport);
        AiChatRequestDto request = new AiChatRequestDto();
        request.setMessages(Collections.singletonList(new AiMessageDto("user", "hello")));

        AiChatResponseDto response = adapter.chat(request);

        assertEquals("ok", response.getContent());
        assertEquals("qwen-max", response.getModelName());
        assertEquals(Integer.valueOf(3), response.getUsage().getTotalTokens());
        assertEquals("Bearer sk-test", transport.authorization);
        assertTrue(transport.body.contains("\"model\":\"qwen-max\""));
        assertTrue(response.getDiagnostics().contains("messageCount=1"));
        assertTrue(response.getDiagnostics().contains("retryCount=0"));
    }

    @Test
    void compatibleProviderRetries5xxOnceAndDoesNotRetry401() {
        AiProviderConfig config = enabledProvider();
        RecordingTransport serverError = new RecordingTransport(
                new CompatibleEndpointAiProviderAdapter.HttpResult(500, "{\"error\":{\"message\":\"temporary\"}}"),
                new CompatibleEndpointAiProviderAdapter.HttpResult(200, "{\"choices\":[{\"message\":{\"content\":\"after retry\"}}]}"));
        AiChatResponseDto retried = new CompatibleEndpointAiProviderAdapter(config, null, serverError).chat(new AiChatRequestDto());

        assertEquals("after retry", retried.getContent());
        assertEquals(Integer.valueOf(1), retried.getRetryCount());
        assertEquals(2, serverError.calls);

        RecordingTransport auth = new RecordingTransport(
                new CompatibleEndpointAiProviderAdapter.HttpResult(401, "{\"error\":{\"message\":\"bad key\"}}"));
        AiChatResponseDto rejected = new CompatibleEndpointAiProviderAdapter(config, null, auth).chat(new AiChatRequestDto());

        assertEquals(AiConstants.ERROR_AUTHENTICATION, rejected.getErrorCode());
        assertEquals(1, auth.calls);
    }

    @Test
    void compatibleProviderClassifiesTimeoutAndRedactsSecret() {
        AiProviderConfig config = enabledProvider();
        RecordingTransport timeout = new RecordingTransport(new SocketTimeoutException("Bearer sk-test timed out"));

        AiChatResponseDto response = new CompatibleEndpointAiProviderAdapter(config, null, timeout).chat(new AiChatRequestDto());

        assertEquals(AiConstants.ERROR_TIMEOUT, response.getErrorCode());
        assertFalse(response.getErrorMessage().contains("sk-test"));
        assertFalse(response.getDiagnostics().contains("sk-test"));
    }

    @Test
    void compatibleProviderNormalizesStreamChunks() {
        AiProviderConfig config = enabledProvider();
        RecordingTransport stream = new RecordingTransport(new CompatibleEndpointAiProviderAdapter.HttpResult(200,
                "data: {\"choices\":[{\"delta\":{\"content\":\"A\"}}]}\n"
                        + "data: {\"choices\":[{\"delta\":{\"content\":\"I\"}}]}\n"
                        + "data: [DONE]"));

        List<AiStreamEventDto> events = new CompatibleEndpointAiProviderAdapter(config, null, stream).stream(new AiChatRequestDto());

        assertEquals(AiConstants.STREAM_TOKEN, events.get(0).getType());
        assertEquals("A", events.get(0).getData());
        assertEquals(AiConstants.STREAM_DONE, events.get(2).getType());
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

    private AiProviderConfig enabledProvider() {
        AiProviderConfig config = new AiProviderConfig();
        config.setEnabled(true);
        config.setEndpoint("https://example.test/v1/chat/completions");
        config.setApiKey("sk-test");
        config.setModelName("qwen-max");
        config.setTimeoutSeconds(3);
        config.setRetryOn5xx(true);
        return config;
    }

    private static class RecordingTransport implements CompatibleEndpointAiProviderAdapter.Transport {
        private final CompatibleEndpointAiProviderAdapter.HttpResult[] results;
        private final Exception failure;
        private int calls;
        private String body;
        private String authorization;

        RecordingTransport(CompatibleEndpointAiProviderAdapter.HttpResult... results) {
            this.results = results;
            this.failure = null;
        }

        RecordingTransport(Exception failure) {
            this.results = new CompatibleEndpointAiProviderAdapter.HttpResult[0];
            this.failure = failure;
        }

        public CompatibleEndpointAiProviderAdapter.HttpResult post(String endpoint, String apiKey, String body, int timeoutSeconds) throws Exception {
            this.calls++;
            this.body = body;
            this.authorization = "Bearer " + apiKey;
            if (failure != null) {
                throw failure;
            }
            int index = Math.min(calls - 1, results.length - 1);
            return results[index];
        }
    }
}
