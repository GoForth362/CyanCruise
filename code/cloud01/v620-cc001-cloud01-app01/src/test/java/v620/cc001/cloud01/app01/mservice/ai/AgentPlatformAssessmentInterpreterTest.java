package v620.cc001.cloud01.app01.mservice.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import v620.cc001.base.common.dto.ai.AgentTaskFlowRequestDto;
import v620.cc001.base.common.dto.ai.AgentTaskFlowResponseDto;
import v620.cc001.base.common.dto.career.AssessmentAnswerSnapshot;
import v620.cc001.base.common.dto.career.AssessmentScoreResult;
import v620.cc001.cloud01.app01.mservice.ai.impl.AgentPlatformAssessmentInterpreter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AgentPlatformAssessmentInterpreterTest {

    @Test
    void sendsAnswerSnapshotAndParsesInterpretation() throws Exception {
        CapturingClient client = new CapturingClient("{\"summary\":\"偏好按计划推进任务\","
                + "\"insights\":[\"本次选择体现了对结构化安排的偏好\"],"
                + "\"suggestions\":[\"在项目中记录任务拆分过程\"],"
                + "\"dataGaps\":[\"缺少真实项目协作记录\"]}");
        AssessmentScoreResult result = new AssessmentScoreResult();
        result.setScaleTitle("性格倾向测评");
        result.setResultSummary("ESTJ");
        result.setCreatedAt(LocalDateTime.of(2026, 7, 16, 20, 30));
        AssessmentAnswerSnapshot answer = new AssessmentAnswerSnapshot();
        answer.setQuestionId(Long.valueOf(1));
        answer.setQuestionText("面对团队任务时你的倾向是？");
        answer.setOptionId(Long.valueOf(2));
        answer.setOptionText("先明确目标和分工");
        answer.setDimensionCode("J");
        answer.setScoreSnapshot(new BigDecimal("2"));
        answer.setValidOption(true);
        result.setAnswers(Arrays.asList(answer));

        AssessmentScoreResult.AiInterpretation interpretation = new AgentPlatformAssessmentInterpreter(client)
                .interpret(result);

        JsonNode question = new ObjectMapper().readTree(client.request.getInputs().get("question"));
        assertEquals("ASSESSMENT_INTERPRETATION", question.path("mode").asText());
        assertFalse(question.path("assessmentResult").has("createdAt"));
        assertEquals("面对团队任务时你的倾向是？", question.path("assessmentResult")
                .path("answers").get(0).path("questionText").asText());
        assertEquals("先明确目标和分工", question.path("assessmentResult")
                .path("answers").get(0).path("optionText").asText());
        assertEquals("偏好按计划推进任务", interpretation.getSummary());
        assertEquals("AI_ASSESSMENT_INTERPRETATION", interpretation.getSource());
        assertNotNull(interpretation.getGeneratedAt());
    }

    @Test
    void hidesPlatformFailureDetailsFromTheUser() {
        AgentTaskFlowResponseDto unavailable = AgentTaskFlowResponseDto.unavailable(
                "UNAVAILABLE", "Agent platform task flow code is not configured");

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> new AgentPlatformAssessmentInterpreter(request -> unavailable).interpret(new AssessmentScoreResult()));

        assertEquals("测评 AI 解读生成失败，请稍后重试", error.getMessage());
    }

    @Test
    void rejectsPlainTextAnswerFromPlatformAgent() {
        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> new AgentPlatformAssessmentInterpreter(new CapturingClient(
                        "Your assessment suggests a structured working style."))
                        .interpret(new AssessmentScoreResult()));

        assertEquals("AI 服务没有返回可用分析，请稍后重试", error.getMessage());
    }

    @Test
    void rejectsAgentToolInstructionEnvelope() {
        String answer = "{\"Thought\":\"输入为合法 JSON\",\"Action\":\"画像补全任务流\","
                + "\"Action_input\":{\"input\":\"{\\\"mode\\\":\\\"ASSESSMENT_INTERPRETATION\\\"}\"}}";

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> new AgentPlatformAssessmentInterpreter(new CapturingClient(answer))
                        .interpret(new AssessmentScoreResult()));

        assertEquals("AI 服务没有返回可用分析，请稍后重试", error.getMessage());
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
