package v620.cc001.cloud01.app01.mservice.ai.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import v620.cc001.base.common.dto.ai.AgentTaskFlowRequestDto;
import v620.cc001.base.common.dto.ai.AgentTaskFlowResponseDto;
import v620.cc001.base.common.dto.furtherstudy.RecommendationDiagnosisResult;
import v620.cc001.base.common.dto.furtherstudy.RecommendationProfileRequest;
import v620.cc001.cloud01.app01.mservice.ai.AgentPlatformTaskFlowClient;
import v620.cc001.cloud01.app01.mservice.ai.AgentPlatformTaskFlowConfig;
import v620.cc001.cloud01.app01.mservice.ai.FurtherStudyCompanionAnalyzer;
import v620.cc001.cloud01.app01.mservice.furtherstudy.impl.InMemoryFurtherStudyCompanionStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AgentPlatformFurtherStudyCompanionAnalyzerTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void sendsCompleteUnencodedQuestionAndMapsResult() throws Exception {
        CapturingClient client = new CapturingClient(okAnswer(
                FurtherStudyCompanionAnalyzer.RECOMMENDATION_DIAGNOSE));
        AgentPlatformFurtherStudyCompanionAnalyzer analyzer = analyzer(client);

        RecommendationProfileRequest payload = new RecommendationProfileRequest();
        payload.setGrade("大三");
        RecommendationDiagnosisResult result = analyzer.analyze("u-1",
                FurtherStudyCompanionAnalyzer.RECOMMENDATION_DIAGNOSE,
                payload, RecommendationDiagnosisResult.class);

        assertEquals(Integer.valueOf(74), result.getOverallScore());
        assertEquals("真实智能分析", result.getSummary());
        String question = client.request.getInputs().get("question");
        assertTrue(question.startsWith("{"));
        assertFalse(question.startsWith("\""));
        JsonNode request = mapper.readTree(question);
        assertEquals("FURTHER_STUDY_ANALYSIS", request.get("mode").asText());
        assertEquals(FurtherStudyCompanionAnalyzer.RECOMMENDATION_DIAGNOSE,
                request.get("taskType").asText());
        assertEquals("大三", request.get("payload").get("grade").asText());
        assertEquals("大三", request.get("grade").asText());
        assertTrue(request.get("profileContext").isObject());
        assertEquals(0, request.get("profileContext").size());
        assertTrue(request.get("userMaterials").isArray());
    }

    @Test
    void rejectsUnavailableConfiguration() {
        AgentPlatformTaskFlowConfig config = new AgentPlatformTaskFlowConfig();
        AgentPlatformFurtherStudyCompanionAnalyzer analyzer =
                new AgentPlatformFurtherStudyCompanionAnalyzer(null, config);
        assertThrows(IllegalStateException.class, () -> analyzer.analyze("u-1",
                FurtherStudyCompanionAnalyzer.RECOMMENDATION_DIAGNOSE,
                new RecommendationProfileRequest(), RecommendationDiagnosisResult.class));
    }

    @Test
    void exposesNeedMoreInformationMessage() {
        CapturingClient client = new CapturingClient("{\"taskType\":\"RECOMMENDATION_DIAGNOSE\","
                + "\"status\":\"NEED_MORE_INFO\",\"result\":{\"status\":\"NEED_MORE_INFO\","
                + "\"message\":\"请补充专业排名和英语成绩。\"}}");
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> analyzer(client).analyze("u-1",
                        FurtherStudyCompanionAnalyzer.RECOMMENDATION_DIAGNOSE,
                        new RecommendationProfileRequest(), RecommendationDiagnosisResult.class));
        assertEquals("请补充专业排名和英语成绩。", error.getMessage());
    }

    @Test
    void explainsWhenAgentOmitsNeedMoreInformationDetails() {
        CapturingClient client = new CapturingClient("{\"taskType\":\"RECOMMENDATION_DIAGNOSE\","
                + "\"status\":\"NEED_MORE_INFO\",\"result\":{\"status\":\"NEED_MORE_INFO\"}}");
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> analyzer(client).analyze("u-1",
                        FurtherStudyCompanionAnalyzer.RECOMMENDATION_DIAGNOSE,
                        new RecommendationProfileRequest(), RecommendationDiagnosisResult.class));
        assertTrue(error.getMessage().contains("\u667a\u80fd\u4f53\u672a\u8fd4\u56de\u5177\u4f53\u7684\u7f3a\u5931\u4fe1\u606f"));
    }

    @Test
    void rejectsMismatchedTaskType() {
        CapturingClient client = new CapturingClient(okAnswer(
                FurtherStudyCompanionAnalyzer.POSTGRADUATE_PLAN_GENERATE));
        assertThrows(IllegalStateException.class, () -> analyzer(client).analyze("u-1",
                FurtherStudyCompanionAnalyzer.RECOMMENDATION_DIAGNOSE,
                new RecommendationProfileRequest(), RecommendationDiagnosisResult.class));
    }

    @Test
    void fallsBackToPublishedTaskFlowWhenAgentRoutesToWrongTask() {
        CapturingClient agent = new CapturingClient(okAnswer(
                FurtherStudyCompanionAnalyzer.POSTGRADUATE_SCHOOL_RECOMMEND));
        CapturingClient taskFlow = new CapturingClient(okAnswer(
                FurtherStudyCompanionAnalyzer.RECOMMENDATION_DIAGNOSE));
        AgentPlatformTaskFlowConfig config = new AgentPlatformTaskFlowConfig();
        config.setEnabled(true);
        config.setAgentNumber("agent-test");
        AgentPlatformFurtherStudyCompanionAnalyzer analyzer =
                new AgentPlatformFurtherStudyCompanionAnalyzer(agent, taskFlow, config,
                        new InMemoryFurtherStudyCompanionStorage());

        RecommendationDiagnosisResult result = analyzer.analyze("u-1",
                FurtherStudyCompanionAnalyzer.RECOMMENDATION_DIAGNOSE,
                new RecommendationProfileRequest(), RecommendationDiagnosisResult.class);

        assertEquals(Integer.valueOf(74), result.getOverallScore());
        assertTrue(agent.request.getInputs().get("question").contains(
                FurtherStudyCompanionAnalyzer.RECOMMENDATION_DIAGNOSE));
        assertEquals(agent.request, taskFlow.request);
    }

    @Test
    void rejectsInvalidJson() {
        CapturingClient client = new CapturingClient("not-json");
        assertThrows(IllegalStateException.class, () -> analyzer(client).analyze("u-1",
                FurtherStudyCompanionAnalyzer.RECOMMENDATION_DIAGNOSE,
                new RecommendationProfileRequest(), RecommendationDiagnosisResult.class));
    }

    private AgentPlatformFurtherStudyCompanionAnalyzer analyzer(CapturingClient client) {
        AgentPlatformTaskFlowConfig config = new AgentPlatformTaskFlowConfig();
        config.setEnabled(true);
        config.setAgentNumber("agent-test");
        config.setJsonEncodeAgentQuery(false);
        return new AgentPlatformFurtherStudyCompanionAnalyzer(client, null, config,
                new InMemoryFurtherStudyCompanionStorage());
    }

    private String okAnswer(String taskType) {
        return "{\"taskType\":\"" + taskType + "\",\"status\":\"OK\",\"result\":{"
                + "\"status\":\"OK\",\"overallScore\":74,\"summary\":\"真实智能分析\"}}";
    }

    private static class CapturingClient implements AgentPlatformTaskFlowClient {
        private final String answer;
        private AgentTaskFlowRequestDto request;

        private CapturingClient(String answer) {
            this.answer = answer;
        }

        @Override
        public AgentTaskFlowResponseDto execute(AgentTaskFlowRequestDto request) {
            this.request = request;
            AgentTaskFlowResponseDto response = new AgentTaskFlowResponseDto();
            response.setSuccess(true);
            response.setAnswer(answer);
            return response;
        }
    }
}
