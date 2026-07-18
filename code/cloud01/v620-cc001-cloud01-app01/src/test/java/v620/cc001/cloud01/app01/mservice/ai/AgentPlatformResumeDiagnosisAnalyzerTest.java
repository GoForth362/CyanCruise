package v620.cc001.cloud01.app01.mservice.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import v620.cc001.base.common.dto.ai.AgentTaskFlowRequestDto;
import v620.cc001.base.common.dto.ai.AgentTaskFlowResponseDto;
import v620.cc001.base.common.dto.career.ResumeDiagnosisRequest;
import v620.cc001.cloud01.app01.mservice.ai.impl.AgentPlatformResumeDiagnosisAnalyzer;
import v620.cc001.cloud01.app01.mservice.ai.impl.DefaultAgentPlatformTaskFlowClient;
import v620.cc001.cloud01.app01.mservice.ai.impl.DefaultResumeDiagnosisAnalyzer;

import java.net.SocketTimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AgentPlatformResumeDiagnosisAnalyzerTest {

    @Test
    void clientSendsSingleQuestionJsonAndReadsConfiguredAnswerPath() {
        AgentPlatformTaskFlowConfig config = enabledConfig();
        config.setAnswerPath("data.answer");
        RecordingTransport transport = new RecordingTransport(200,
                "{\"data\":{\"answer\":\"{\\\"overallScore\\\":80}\"}}");
        DefaultAgentPlatformTaskFlowClient client = new DefaultAgentPlatformTaskFlowClient(config, transport, new ObjectMapper());
        AgentTaskFlowRequestDto request = new AgentTaskFlowRequestDto();
        request.setTaskFlowCode("process-resume");
        request.putInput("question", "{\"resumeText\":\"Java experience\"}");

        AgentTaskFlowResponseDto response = client.execute(request);

        assertTrue(response.isSuccess());
        assertEquals("{\"overallScore\":80}", response.getAnswer());
        assertEquals("Bearer token-1", transport.authorization);
        assertTrue(transport.body.contains("\"taskFlowCode\":\"process-resume\""));
        assertTrue(transport.body.contains("\"question\":\"{\\\"resumeText\\\":\\\"Java experience\\\"}\""));
    }

    @Test
    void clientNeverReportsConfiguredTokenWhenRequestFails() {
        AgentPlatformTaskFlowConfig config = enabledConfig();
        DefaultAgentPlatformTaskFlowClient client = new DefaultAgentPlatformTaskFlowClient(
                config, new TimeoutTransport(), new ObjectMapper());

        AgentTaskFlowResponseDto response = client.execute(new AgentTaskFlowRequestDto());

        assertFalse(response.isSuccess());
        assertEquals("TIMEOUT", response.getErrorCode());
        assertFalse(response.getErrorMessage().contains("token-1"));
    }

    @Disabled("规则兜底已移除，保留旧用例仅用于历史行为对照")
    @Test
    void analyzerUsesValidTaskFlowJsonAndFallsBackForInvalidScores() {
        ResumeDiagnosisRequest request = new ResumeDiagnosisRequest();
        request.setTargetJob("Java 后端实习");
        request.setJobDescription("接口开发");
        request.setProfileContext("软件工程专业");

        AgentPlatformResumeDiagnosisAnalyzer valid = new AgentPlatformResumeDiagnosisAnalyzer(
                new FixedClient(validDiagnosis()), enabledConfig(), new DefaultResumeDiagnosisAnalyzer());
        String validAnswer = valid.analyze(request, "项目经历：使用 Java 完成接口开发");
        assertEquals(validDiagnosis(), validAnswer);
        assertEquals(AgentPlatformResumeDiagnosisAnalyzer.SOURCE_AGENT_AI, valid.getLastResultSource());

        AgentPlatformResumeDiagnosisAnalyzer invalid = new AgentPlatformResumeDiagnosisAnalyzer(
                new FixedClient("{\"overallScore\":99,\"scoreBreakdown\":[]}"),
                enabledConfig(), new DefaultResumeDiagnosisAnalyzer());
        String fallback = invalid.analyze(request, "项目经历：使用 Java 完成接口开发");
        assertTrue(fallback.contains("\"overallScore\""));
        assertFalse(fallback.contains("\"overallScore\":99"));

        AgentPlatformResumeDiagnosisAnalyzer inconsistentScore = new AgentPlatformResumeDiagnosisAnalyzer(
                new FixedClient(validDiagnosis().replace("\"overallScore\":80", "\"overallScore\":81")),
                enabledConfig(), new DefaultResumeDiagnosisAnalyzer());
        String inconsistentFallback = inconsistentScore.analyze(request, "项目经历：使用 Java 完成接口开发");
        assertFalse(inconsistentFallback.contains("\"overallScore\":81"));
    }

    @Test
    void analyzerRejectsInvalidTaskFlowResponseWithoutRuleFallback() {
        AgentPlatformResumeDiagnosisAnalyzer analyzer = new AgentPlatformResumeDiagnosisAnalyzer(
                new FixedClient("{\"overallScore\":99,\"scoreBreakdown\":[]}"), enabledConfig(),
                new DefaultResumeDiagnosisAnalyzer());

        IllegalStateException error = org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException.class,
                () -> analyzer.analyze(new ResumeDiagnosisRequest(), "resume"));

        assertTrue(error.getMessage().contains("AI"));
    }

    @Test
    void analyzerPackagesOnlyTheConfiguredResumeDiagnosisContext() throws Exception {
        ResumeDiagnosisRequest request = new ResumeDiagnosisRequest();
        request.setTargetJob("Java 后端实习");
        request.setJobDescription("负责接口与数据库开发");
        request.setProfileContext("软件工程专业，大三");
        CapturingClient client = new CapturingClient(validDiagnosis());
        AgentPlatformResumeDiagnosisAnalyzer analyzer = new AgentPlatformResumeDiagnosisAnalyzer(
                client, enabledConfig(), new DefaultResumeDiagnosisAnalyzer());

        analyzer.analyze(request, "项目经历：用户模块开发");

        assertEquals("process-resume", client.request.getTaskFlowCode());
        JsonNode question = new ObjectMapper().readTree(client.request.getInputs().get("question"));
        assertEquals("项目经历：用户模块开发", question.path("resumeText").asText());
        assertEquals("Java 后端实习", question.path("targetJob").asText());
        assertEquals("负责接口与数据库开发", question.path("jobDescription").asText());
        assertEquals("软件工程专业，大三", question.path("profileContext").asText());
        assertTrue(question.path("diagnosisInstructions").asText().contains("逐条阅读岗位要求"));
        assertEquals(5, question.size());
    }

    @Test
    void clientDoesNotInvokeTransportWhenConfigurationIsIncomplete() {
        AgentPlatformTaskFlowConfig config = new AgentPlatformTaskFlowConfig();
        config.setEnabled(true);
        config.setEndpoint("https://agent.example.test/task-flows/run");
        RecordingTransport transport = new RecordingTransport(200, "{\"answer\":\"ignored\"}");

        AgentTaskFlowResponseDto response = new DefaultAgentPlatformTaskFlowClient(config, transport, new ObjectMapper())
                .execute(new AgentTaskFlowRequestDto());

        assertFalse(response.isSuccess());
        assertEquals("UNAVAILABLE", response.getErrorCode());
        assertEquals(null, transport.body);
    }

    private static AgentPlatformTaskFlowConfig enabledConfig() {
        AgentPlatformTaskFlowConfig config = new AgentPlatformTaskFlowConfig();
        config.setEnabled(true);
        config.setEndpoint("https://agent.example.test/task-flows/run");
        config.setAccessToken("token-1");
        config.setTaskFlowCode("process-resume");
        config.setTimeoutSeconds(3);
        return config;
    }

    private static String validDiagnosis() {
        return "{\"overallScore\":80,\"scoreBreakdown\":["
                + "{\"name\":\"内容完整度\",\"score\":20,\"maxScore\":25,\"reason\":\"完整\"},"
                + "{\"name\":\"目标岗位匹配\",\"score\":24,\"maxScore\":30,\"reason\":\"相关\"},"
                + "{\"name\":\"经历证据\",\"score\":24,\"maxScore\":30,\"reason\":\"有证据\"},"
                + "{\"name\":\"表达清晰度\",\"score\":12,\"maxScore\":15,\"reason\":\"清晰\"}],"
                + "\"strengths\":[\"项目经历清楚\"],\"weaknesses\":[\"量化结果不足\"],"
                + "\"suggestions\":[\"补充真实结果\"],\"revisionSuggestions\":[]}";
    }

    private static class RecordingTransport implements DefaultAgentPlatformTaskFlowClient.Transport {
        private final int statusCode;
        private final String responseBody;
        private String body;
        private String authorization;

        RecordingTransport(int statusCode, String responseBody) {
            this.statusCode = statusCode;
            this.responseBody = responseBody;
        }

        public DefaultAgentPlatformTaskFlowClient.HttpResult post(String endpoint, String authorizationHeader,
                                                                   String authorizationValue, String body,
                                                                   int timeoutSeconds) {
            this.body = body;
            this.authorization = authorizationValue;
            return new DefaultAgentPlatformTaskFlowClient.HttpResult(statusCode, responseBody);
        }
    }

    private static class TimeoutTransport implements DefaultAgentPlatformTaskFlowClient.Transport {
        public DefaultAgentPlatformTaskFlowClient.HttpResult post(String endpoint, String authorizationHeader,
                                                                   String authorizationValue, String body,
                                                                   int timeoutSeconds) throws Exception {
            throw new SocketTimeoutException("token-1 timed out");
        }
    }

    private static class FixedClient implements AgentPlatformTaskFlowClient {
        private final String answer;

        FixedClient(String answer) {
            this.answer = answer;
        }

        public AgentTaskFlowResponseDto execute(AgentTaskFlowRequestDto request) {
            AgentTaskFlowResponseDto response = new AgentTaskFlowResponseDto();
            response.setSuccess(true);
            response.setAnswer(answer);
            return response;
        }
    }

    private static class CapturingClient extends FixedClient {
        private AgentTaskFlowRequestDto request;

        CapturingClient(String answer) {
            super(answer);
        }

        public AgentTaskFlowResponseDto execute(AgentTaskFlowRequestDto request) {
            this.request = request;
            return super.execute(request);
        }
    }
}
