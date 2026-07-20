package v620.cc001.cloud01.app01.mservice.ai.impl;

import org.junit.jupiter.api.Test;
import v620.cc001.base.common.dto.ai.AgentTaskFlowRequestDto;
import v620.cc001.base.common.dto.ai.AgentTaskFlowResponseDto;
import v620.cc001.base.common.dto.career.InterviewReportDto;
import v620.cc001.base.common.dto.career.InterviewSessionDto;
import v620.cc001.cloud01.app01.mservice.ai.AgentPlatformTaskFlowClient;
import v620.cc001.cloud01.app01.mservice.ai.AgentPlatformTaskFlowConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AgentPlatformInterviewReportAnalyzerTest {

    @Test
    void mapsTrustedSessionContextAndParsesCompleteEvidenceReport() throws Exception {
        AgentPlatformTaskFlowConfig config = config();
        CapturingClient client = new CapturingClient(validReport(2));
        AgentPlatformInterviewReportAnalyzer analyzer = new AgentPlatformInterviewReportAnalyzer(client, config);
        InterviewSessionDto session = session();

        InterviewReportDto report = analyzer.analyze(session,
                "[面试官] 请介绍项目\n[候选人] 我负责接口并将耗时降低30%", 2);

        assertEquals(Integer.valueOf(82), report.getOverallScore());
        assertEquals(Integer.valueOf(2), report.getTotalQuestions());
        assertEquals("回答引用了接口耗时降低30%的结果。", report.getScoreReasons().get("technical"));
        assertTrue(client.request.getInputs().get("question").contains("后端开发工程师"));
        assertTrue(client.request.getInputs().get("question").contains("耗时降低30%"));
        assertNotNull(report.getStrengths().get(0).getDetail());
    }

    @Test
    void rejectsMissingReasonsInsteadOfSupplyingDefaultScores() {
        String invalid = validReport(1).replace("回答能回应问题。", "");
        AgentPlatformInterviewReportAnalyzer analyzer = new AgentPlatformInterviewReportAnalyzer(
                new CapturingClient(invalid), config());

        assertThrows(IllegalStateException.class, () -> analyzer.analyze(session(), "真实问答", 1));
    }

    @Test
    void rejectsMismatchedAnswerCount() {
        AgentPlatformInterviewReportAnalyzer analyzer = new AgentPlatformInterviewReportAnalyzer(
                new CapturingClient(validReport(7)), config());

        assertThrows(IllegalStateException.class, () -> analyzer.analyze(session(), "只有两次回答", 2));
    }

    @Test
    void reportsUnconfiguredAgentClearly() {
        AgentPlatformInterviewReportAnalyzer analyzer = new AgentPlatformInterviewReportAnalyzer(null, config());
        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> analyzer.analyze(session(), "真实问答", 1));
        assertTrue(error.getMessage().contains("暂未配置"));
    }

    private AgentPlatformTaskFlowConfig config() {
        AgentPlatformTaskFlowConfig config = new AgentPlatformTaskFlowConfig();
        config.setEnabled(true);
        config.setTaskFlowCode("interview-analysis-flow");
        return config;
    }

    private InterviewSessionDto session() {
        InterviewSessionDto session = new InterviewSessionDto();
        session.setPositionName("后端开发工程师");
        session.setDifficulty("Hard");
        session.setMode("TEXT");
        return session;
    }

    private String validReport(int count) {
        return "{\"overallScore\":82,\"totalQuestions\":" + count + ","
                + "\"radarScore\":{\"expression\":80,\"logic\":84,\"technical\":86,\"pressureResistance\":78,\"communication\":82},"
                + "\"scoreReasons\":{\"expression\":\"回答使用了清楚的项目描述。\",\"logic\":\"回答按问题、行动和结果展开。\","
                + "\"technical\":\"回答引用了接口耗时降低30%的结果。\",\"pressureResistance\":\"回答说明了问题处理过程。\",\"communication\":\"回答能回应问题。\"},"
                + "\"strengths\":[{\"title\":\"结果具体\",\"detail\":\"回答中明确提到将接口耗时降低30%。\"}],"
                + "\"improvements\":[{\"title\":\"补充个人决策\",\"detail\":\"第一题说明了结果，但没有说明为什么选择该方案。\"}],"
                + "\"textSummary\":\"回答有具体结果，下一步需要补充关键决策依据。\"}";
    }

    private static class CapturingClient implements AgentPlatformTaskFlowClient {
        private final String answer;
        private AgentTaskFlowRequestDto request;

        CapturingClient(String answer) { this.answer = answer; }

        public AgentTaskFlowResponseDto execute(AgentTaskFlowRequestDto request) {
            this.request = request;
            AgentTaskFlowResponseDto response = new AgentTaskFlowResponseDto();
            response.setSuccess(true);
            response.setAnswer(answer);
            return response;
        }
    }
}
