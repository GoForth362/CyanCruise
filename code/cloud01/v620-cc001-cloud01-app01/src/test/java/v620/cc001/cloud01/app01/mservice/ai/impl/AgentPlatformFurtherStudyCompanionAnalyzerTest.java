package v620.cc001.cloud01.app01.mservice.ai.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import v620.cc001.base.common.dto.ai.AgentTaskFlowRequestDto;
import v620.cc001.base.common.dto.ai.AgentTaskFlowResponseDto;
import v620.cc001.base.common.dto.furtherstudy.PostgraduatePlanRequest;
import v620.cc001.base.common.dto.furtherstudy.PostgraduatePlanResult;
import v620.cc001.base.common.dto.furtherstudy.RecommendationDiagnosisResult;
import v620.cc001.base.common.dto.furtherstudy.RecommendationPlanResult;
import v620.cc001.base.common.dto.furtherstudy.RecommendationProfileRequest;
import v620.cc001.base.common.dto.furtherstudy.RecommendationTutorLetterRequest;
import v620.cc001.base.common.dto.furtherstudy.RecommendationTutorLetterResult;
import v620.cc001.base.common.dto.furtherstudy.StudyAbroadStatementOutlineResult;
import v620.cc001.base.common.dto.furtherstudy.StudyAbroadStatementRequest;
import v620.cc001.base.common.dto.furtherstudy.StudyAbroadVisaChecklistRequest;
import v620.cc001.base.common.dto.furtherstudy.StudyAbroadVisaChecklistResult;
import v620.cc001.cloud01.app01.mservice.ai.AgentPlatformTaskFlowClient;
import v620.cc001.cloud01.app01.mservice.ai.AgentPlatformTaskFlowConfig;
import v620.cc001.cloud01.app01.mservice.ai.FurtherStudyCompanionAnalyzer;

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
        assertEquals(4, request.size());
        assertEquals("task-flow-test", client.request.getTaskFlowCode());
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
    void mapsPostgraduatePlanTaskFlowEnvelope() {
        String answer = "{\"taskType\":\"POSTGRADUATE_PLAN_GENERATE\",\"status\":\"OK\","
                + "\"result\":{\"status\":\"OK\",\"summary\":\"三轮复习计划\","
                + "\"target\":\"电子科技大学 软件工程\",\"examDate\":\"2026-12-20\","
                + "\"daysRemaining\":143,\"rounds\":["
                + round("FOUNDATION", "基础夯实阶段") + ","
                + round("IMPROVEMENT", "能力提升阶段") + ","
                + round("SPRINT", "冲刺模考阶段")
                + "],\"dailyHabits\":[\"每日复盘\"]}}";
        CapturingClient client = new CapturingClient(answer);

        PostgraduatePlanResult result = analyzer(client).analyze("u-1",
                FurtherStudyCompanionAnalyzer.POSTGRADUATE_PLAN_GENERATE,
                new PostgraduatePlanRequest(), PostgraduatePlanResult.class);

        assertEquals("三轮复习计划", result.getSummary());
        assertEquals(Integer.valueOf(143), result.getDaysRemaining());
        assertEquals(3, result.getRounds().size());
        assertEquals("FOUNDATION", result.getRounds().get(0).getRoundCode());
        assertEquals("每日复盘", result.getDailyHabits().get(0));
    }

    @Test
    void retriesPostgraduatePlanOnceWhenFirstJsonIsIncomplete() {
        String valid = "{\"taskType\":\"POSTGRADUATE_PLAN_GENERATE\",\"status\":\"OK\","
                + "\"result\":{\"status\":\"OK\",\"summary\":\"三轮复习计划\","
                + "\"target\":\"电子科技大学 软件工程\",\"examDate\":\"2026-12-20\","
                + "\"daysRemaining\":143,\"rounds\":["
                + round("FOUNDATION", "基础夯实阶段") + ","
                + round("IMPROVEMENT", "能力提升阶段") + ","
                + round("SPRINT", "冲刺模考阶段")
                + "],\"dailyHabits\":[\"每日复盘\"]}}";
        SequencedClient client = new SequencedClient("{\"taskType\":\"POSTGRADUATE_PLAN_GENERATE\"",
                valid);
        AgentPlatformTaskFlowConfig config = new AgentPlatformTaskFlowConfig();
        config.setEnabled(true);
        config.setTaskFlowCode("task-flow-test");

        PostgraduatePlanResult result =
                new AgentPlatformFurtherStudyCompanionAnalyzer(client, config).analyze("u-1",
                        FurtherStudyCompanionAnalyzer.POSTGRADUATE_PLAN_GENERATE,
                        new PostgraduatePlanRequest(), PostgraduatePlanResult.class);

        assertEquals("三轮复习计划", result.getSummary());
        assertEquals(2, client.invocations);
    }

    @Test
    void retriesRecommendationPlanOnceWhenFirstJsonIsIncomplete() {
        String valid = "{\"taskType\":\"RECOMMENDATION_PLAN_GENERATE\",\"status\":\"OK\","
                + "\"result\":{\"status\":\"OK\",\"summary\":\"背景提升行动计划\","
                + "\"timeline\":[{\"stage\":\"本月\",\"title\":\"整理背景\","
                + "\"detail\":\"梳理课程、竞赛和项目事实\",\"priority\":\"高\"}],"
                + "\"weeklyFocus\":[\"每周复盘排名变化\"],"
                + "\"targetCampTips\":[\"核验目标院校官方通知\"]}}";
        SequencedClient client = new SequencedClient(
                "{\"taskType\":\"RECOMMENDATION_PLAN_GENERATE\"", valid);
        AgentPlatformTaskFlowConfig config = new AgentPlatformTaskFlowConfig();
        config.setEnabled(true);
        config.setTaskFlowCode("task-flow-test");

        RecommendationPlanResult result =
                new AgentPlatformFurtherStudyCompanionAnalyzer(client, config).analyze("u-1",
                        FurtherStudyCompanionAnalyzer.RECOMMENDATION_PLAN_GENERATE,
                        new RecommendationProfileRequest(), RecommendationPlanResult.class);

        assertEquals("背景提升行动计划", result.getSummary());
        assertEquals("整理背景", result.getTimeline().get(0).getTitle());
        assertEquals(2, client.invocations);
    }

    @Test
    void retriesStatementOutlineOnceWhenFirstJsonIsIncomplete() {
        String valid = "{\"taskType\":\"STUDY_ABROAD_STATEMENT_OUTLINE\",\"status\":\"OK\","
                + "\"result\":{\"status\":\"OK\",\"goldenLine\":\"从软件工程实践走向机器学习应用\","
                + "\"outline\":[{\"section\":\"Introduction\",\"content\":\"项目问题\"},"
                + "{\"section\":\"Academic Foundation\",\"content\":\"学习转折\"}],"
                + "\"storyQuestions\":[\"推荐效果如何评估？\"],\"missingInfo\":[],"
                + "\"writingTips\":[\"使用真实项目细节支撑结论\"]}}";
        SequencedClient client = new SequencedClient(
                "{\"taskType\":\"STUDY_ABROAD_STATEMENT_OUTLINE\"", valid);

        StudyAbroadStatementOutlineResult result = analyzer(client).analyze("u-1",
                FurtherStudyCompanionAnalyzer.STUDY_ABROAD_STATEMENT_OUTLINE,
                new StudyAbroadStatementRequest(), StudyAbroadStatementOutlineResult.class);

        assertEquals("从软件工程实践走向机器学习应用", result.getGoldenLine());
        assertTrue(result.getOutline().contains("Introduction：项目问题"));
        assertTrue(!result.getOutline().contains("\"section\""));
        assertEquals(2, client.invocations);
    }

    @Test
    void retriesVisaChecklistWhenFirstResponseIncorrectlyRequestsFilledFields() {
        String valid = "{\"taskType\":\"STUDY_ABROAD_VISA_CHECKLIST\",\"status\":\"OK\","
                + "\"result\":{\"status\":\"OK\",\"summary\":\"美国 2026 秋季申请清单\","
                + "\"checklist\":[{\"stage\":\"申请准备\",\"title\":\"确认申请材料\","
                + "\"detail\":\"核对学校官网材料要求\",\"priority\":\"高\"}],"
                + "\"risks\":[\"以官方要求为准\"],\"reminders\":[\"预留材料办理时间\"]}}";
        SequencedClient client = new SequencedClient(
                "{\"taskType\":\"STUDY_ABROAD_VISA_CHECKLIST\",\"status\":\"NEED_MORE_INFO\","
                        + "\"result\":{\"status\":\"NEED_MORE_INFO\","
                        + "\"message\":\"请补充：countryOrRegion、applicationSeason、admissionStatus\"}}",
                valid);

        StudyAbroadVisaChecklistResult result = analyzer(client).analyze("u-1",
                FurtherStudyCompanionAnalyzer.STUDY_ABROAD_VISA_CHECKLIST,
                new StudyAbroadVisaChecklistRequest(), StudyAbroadVisaChecklistResult.class);

        assertEquals("美国 2026 秋季申请清单", result.getSummary());
        assertEquals(2, client.invocations);
    }

    @Test
    void sendsCurrentAndTargetSchoolAsSeparateTutorLetterFacts() throws Exception {
        CapturingClient client = new CapturingClient(
                "{\"taskType\":\"RECOMMENDATION_TUTOR_LETTER\",\"status\":\"OK\","
                        + "\"result\":{\"status\":\"OK\",\"subject\":\"咨询推免机会\","
                        + "\"body\":\"本科就读于成都理工大学，希望申请电子科技大学。\","
                        + "\"attachments\":[],\"sendTips\":[],\"missingInfo\":[]}}");
        RecommendationTutorLetterRequest payload = new RecommendationTutorLetterRequest();
        payload.setCurrentSchool("成都理工大学");
        payload.setTargetSchool("电子科技大学");

        RecommendationTutorLetterResult result = analyzer(client).analyze("u-1",
                FurtherStudyCompanionAnalyzer.RECOMMENDATION_TUTOR_LETTER,
                payload, RecommendationTutorLetterResult.class);

        JsonNode request = mapper.readTree(client.request.getInputs().get("question"));
        assertEquals("成都理工大学",
                request.get("payload").get("currentSchool").asText());
        assertEquals("电子科技大学",
                request.get("payload").get("targetSchool").asText());
        assertTrue(result.getBody().contains("成都理工大学"));
        assertTrue(result.getBody().contains("电子科技大学"));
    }

    @Test
    void rejectsInvalidJson() {
        CapturingClient client = new CapturingClient("not-json");
        assertThrows(IllegalStateException.class, () -> analyzer(client).analyze("u-1",
                FurtherStudyCompanionAnalyzer.RECOMMENDATION_DIAGNOSE,
                new RecommendationProfileRequest(), RecommendationDiagnosisResult.class));
    }

    private AgentPlatformFurtherStudyCompanionAnalyzer analyzer(AgentPlatformTaskFlowClient client) {
        AgentPlatformTaskFlowConfig config = new AgentPlatformTaskFlowConfig();
        config.setEnabled(true);
        config.setTaskFlowCode("task-flow-test");
        config.setJsonEncodeAgentQuery(false);
        return new AgentPlatformFurtherStudyCompanionAnalyzer(client, config);
    }

    private String okAnswer(String taskType) {
        return "{\"taskType\":\"" + taskType + "\",\"status\":\"OK\",\"result\":{"
                + "\"status\":\"OK\",\"overallScore\":74,\"summary\":\"真实智能分析\"}}";
    }

    private String round(String roundCode, String roundName) {
        return "{\"roundCode\":\"" + roundCode + "\",\"roundName\":\"" + roundName + "\","
                + "\"dateRange\":\"2026-07-30 至 2026-09-12\",\"goal\":\"完成阶段目标\","
                + "\"subjectFocus\":[\"数学一\"],\"weeklyTasks\":[\"完成周任务\"],"
                + "\"checkPoints\":[\"完成检查点\"],\"stateAdvice\":\"保持复盘\"}";
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

    private static class SequencedClient implements AgentPlatformTaskFlowClient {
        private final String[] answers;
        private int invocations;

        private SequencedClient(String... answers) {
            this.answers = answers;
        }

        @Override
        public AgentTaskFlowResponseDto execute(AgentTaskFlowRequestDto request) {
            String answer = answers[Math.min(invocations, answers.length - 1)];
            invocations++;
            AgentTaskFlowResponseDto response = new AgentTaskFlowResponseDto();
            response.setSuccess(true);
            response.setAnswer(answer);
            return response;
        }
    }
}
