package v620.cc001.cloud01.app01.mservice.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import v620.cc001.base.common.dto.ai.AgentTaskFlowRequestDto;
import v620.cc001.base.common.dto.ai.AgentTaskFlowResponseDto;
import v620.cc001.base.common.dto.career.AssessmentAnswerSnapshot;
import v620.cc001.base.common.dto.career.AssessmentScoreResult;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;
import v620.cc001.cloud01.app01.mservice.ai.impl.AgentPlatformDeepProfileAnalyzer;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AgentPlatformDeepProfileAnalyzerTest {

    @Test
    void sendsCompletedAssessmentAnswersAndParsesDeepProfile() throws Exception {
        CapturingClient client = new CapturingClient(validAnswer());
        AssessmentScoreResult result = new AssessmentScoreResult();
        result.setScaleTitle("职业兴趣测评");
        result.setResultSummary("偏好技术问题解决");
        AssessmentAnswerSnapshot answer = new AssessmentAnswerSnapshot();
        answer.setQuestionId(Long.valueOf(11));
        answer.setOptionId(Long.valueOf(22));
        answer.setDimensionCode("INVESTIGATIVE");
        answer.setScoreSnapshot(new BigDecimal("2"));
        answer.setValidOption(true);
        result.setAnswers(Arrays.asList(answer));

        UserProfileSnapshot snapshot = new UserProfileSnapshot();
        UserProfileSnapshot.OnboardingBlock onboarding = new UserProfileSnapshot.OnboardingBlock();
        onboarding.setSelfProfileSupplement("负责课程项目的接口联调，每周可投入 12 小时");
        snapshot.setOnboarding(onboarding);

        UserProfileSnapshot.AiDeepProfileBlock profile = new AgentPlatformDeepProfileAnalyzer(client)
                .analyze(Arrays.asList(result), snapshot);

        JsonNode question = new ObjectMapper().readTree(client.request.getInputs().get("question"));
        assertEquals("职业兴趣测评", question.path("assessmentResults").get(0).path("scaleTitle").asText());
        assertEquals("INVESTIGATIVE", question.path("assessmentResults").get(0)
                .path("answers").get(0).path("dimensionCode").asText());
        assertEquals("负责课程项目的接口联调，每周可投入 12 小时",
                question.path("profileFacts").path("selfProfileSupplement").asText());
        assertTrue(question.path("instructions").asText().contains("不得把其中已经明确提供的信息再次列入 dataGaps"));
        assertEquals("偏好逻辑分析", profile.getProfileTags().get(0));
        assertNotNull(profile.getGeneratedAt());
    }

    @Test
    void keepsTaskFlowFailureReasonForTheUser() {
        AgentTaskFlowResponseDto unavailable = AgentTaskFlowResponseDto.unavailable(
                "UNAVAILABLE", "Agent platform task flow code is not configured");

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> new AgentPlatformDeepProfileAnalyzer(request -> unavailable)
                        .analyze(Arrays.<AssessmentScoreResult>asList(), new UserProfileSnapshot()));

        assertEquals("深度画像 AI 不可用：Agent platform task flow code is not configured", error.getMessage());
    }

    private static String validAnswer() {
        return "{\"profileSummary\":\"偏好逻辑分析与实践验证\","
                + "\"profileTags\":[\"偏好逻辑分析\"],\"strengths\":[\"善于拆解问题\"],"
                + "\"workPreferences\":{\"collaborationStyle\":\"目标清晰\",\"workEnvironment\":\"技术氛围\","
                + "\"decisionStyle\":\"依据事实\",\"motivation\":[\"技术成长\"]},"
                + "\"studyPreferences\":[\"动手实践\"],\"careerInclinations\":[\"技术研发\"],"
                + "\"developmentSuggestions\":[\"完成一个真实项目\"],"
                + "\"evidence\":[{\"conclusion\":\"偏好分析\",\"basis\":\"测评结果\",\"confidence\":\"HIGH\"}],"
                + "\"dataGaps\":[\"实习经历\"]}";
    }

    private static class CapturingClient implements AgentPlatformTaskFlowClient {
        private final String answer;
        private AgentTaskFlowRequestDto request;

        CapturingClient(String answer) {
            this.answer = answer;
        }

        public AgentTaskFlowResponseDto execute(AgentTaskFlowRequestDto request) {
            this.request = request;
            AgentTaskFlowResponseDto response = new AgentTaskFlowResponseDto();
            response.setSuccess(true);
            response.setAnswer(answer);
            return response;
        }
    }
}
