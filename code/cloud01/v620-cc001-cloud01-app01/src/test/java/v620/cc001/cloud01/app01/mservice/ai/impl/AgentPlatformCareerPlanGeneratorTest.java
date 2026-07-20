package v620.cc001.cloud01.app01.mservice.ai.impl;

import org.junit.jupiter.api.Test;
import v620.cc001.base.common.dto.ai.AgentTaskFlowRequestDto;
import v620.cc001.base.common.dto.ai.AgentTaskFlowResponseDto;
import v620.cc001.base.common.dto.career.CareerPlanRecordDto;
import v620.cc001.base.common.dto.career.CareerUserProfileDto;
import v620.cc001.cloud01.app01.mservice.ai.AgentPlatformTaskFlowClient;
import v620.cc001.cloud01.app01.mservice.ai.AgentPlatformTaskFlowConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AgentPlatformCareerPlanGeneratorTest {

    @Test
    void sendsEmploymentContextAndParsesStructuredPlan() {
        RecordingClient client = new RecordingClient(success(planJson()));
        AgentPlatformTaskFlowConfig config = new AgentPlatformTaskFlowConfig();
        config.setTaskFlowCode("employment-flow");
        AgentPlatformCareerPlanGenerator generator = new AgentPlatformCareerPlanGenerator(client, config);
        CareerUserProfileDto profile = new CareerUserProfileDto();
        profile.setCurrentStage("大三");

        CareerPlanRecordDto plan = generator.generate("user-1", "Java 开发工程师", profile);

        assertEquals("employment-flow", client.request.getTaskFlowCode());
        assertTrue(client.request.getInputs().get("question").contains("EMPLOYMENT_PLANNING"));
        assertTrue(client.request.getInputs().get("question").contains("currentDate"));
        assertTrue(client.request.getInputs().get("question").contains("Java 开发工程师"));
        assertEquals("Java 开发工程师", plan.getTargetRole());
        assertEquals("AGENT", plan.getPlanningMode());
        assertEquals(1, plan.getPhases().size());
        assertEquals("整理项目证据", plan.getWeeklyPlan().getActions().get(0));
    }

    @Test
    void rejectsIncompleteTaskFlowOutput() {
        RecordingClient client = new RecordingClient(success("{\"targetRole\":\"Java 开发工程师\"}"));
        AgentPlatformCareerPlanGenerator generator = new AgentPlatformCareerPlanGenerator(
                client, new AgentPlatformTaskFlowConfig());

        assertThrows(IllegalStateException.class,
                () -> generator.generate("user-1", "Java 开发工程师", new CareerUserProfileDto()));
    }

    @Test
    void parsesStringEncodedResultAndIgnoresToolInputEcho() throws Exception {
        String encoded = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(planJson());
        String answer = "{\"Thought\":\"准备调用任务流\","
                + "\"Action_input\":{\"question\":\"{\\\"mode\\\":\\\"EMPLOYMENT_PLANNING\\\"}\"},"
                + "\"result\":{\"answer\":" + encoded + "}}";
        RecordingClient client = new RecordingClient(success(answer));
        AgentPlatformCareerPlanGenerator generator = new AgentPlatformCareerPlanGenerator(
                client, new AgentPlatformTaskFlowConfig());

        CareerPlanRecordDto plan = generator.generate(
                "user-1", "Java 开发工程师", new CareerUserProfileDto());

        assertEquals("Java 开发工程师", plan.getTargetRole());
        assertEquals(1, plan.getPhases().size());
    }

    private static AgentTaskFlowResponseDto success(String answer) {
        AgentTaskFlowResponseDto response = new AgentTaskFlowResponseDto();
        response.setSuccess(true);
        response.setAnswer(answer);
        return response;
    }

    private static String planJson() {
        return "{\"targetRole\":\"Java 开发工程师\","
                + "\"startStateSummary\":\"已有项目基础，需要补充量化证据。\","
                + "\"horizonYears\":3,"
                + "\"phases\":[{\"phaseId\":\"year-1\",\"horizon\":\"1年\","
                + "\"title\":\"形成可投递能力\",\"goal\":\"完成求职准备\","
                + "\"actions\":[\"完善项目\"],\"kpis\":[\"完成两次投递\"]}],"
                + "\"weeklyPlan\":{\"weekTitle\":\"本周重点\",\"weekGoal\":\"补齐证据\","
                + "\"actions\":[\"整理项目证据\"],\"deliverables\":[\"项目清单\"]},"
                + "\"dailySuggestions\":[\"今天整理一个项目\"],"
                + "\"weeklyFocus\":[\"整理项目证据\"]}";
    }

    private static class RecordingClient implements AgentPlatformTaskFlowClient {
        private final AgentTaskFlowResponseDto response;
        private AgentTaskFlowRequestDto request;

        RecordingClient(AgentTaskFlowResponseDto response) {
            this.response = response;
        }

        @Override
        public AgentTaskFlowResponseDto execute(AgentTaskFlowRequestDto request) {
            this.request = request;
            return response;
        }
    }
}
