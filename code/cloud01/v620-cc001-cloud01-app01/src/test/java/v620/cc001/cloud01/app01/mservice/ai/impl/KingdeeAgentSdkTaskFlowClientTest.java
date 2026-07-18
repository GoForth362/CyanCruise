package v620.cc001.cloud01.app01.mservice.ai.impl;

import kd.ai.sdk.model.agent.AgentMessage;
import org.junit.jupiter.api.Test;
import v620.cc001.base.common.dto.ai.AgentTaskFlowRequestDto;
import v620.cc001.base.common.dto.ai.AgentTaskFlowResponseDto;
import v620.cc001.cloud01.app01.mservice.ai.AgentPlatformTaskFlowConfig;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KingdeeAgentSdkTaskFlowClientTest {

    @Test
    void sendsQuestionJsonToConfiguredAgentAndCollectsDiagnosisJson() {
        AgentPlatformTaskFlowConfig config = sdkConfig();
        RecordingRunner runner = new RecordingRunner(Stream.of(
                AgentMessage.chat("message-1", "诊断结果：", true),
                AgentMessage.chat("message-2", "{\"overallScore\":80}", false)));
        KingdeeAgentSdkTaskFlowClient client = new KingdeeAgentSdkTaskFlowClient(config, runner);
        AgentTaskFlowRequestDto request = new AgentTaskFlowRequestDto();
        request.putInput("question", "{\"resumeText\":\"Java 项目\"}");

        AgentTaskFlowResponseDto response = client.execute(request);

        assertTrue(response.isSuccess());
        assertEquals("agent-resume-diagnosis", runner.agentNumber);
        assertEquals("\"{\\\"resumeText\\\":\\\"Java 项目\\\"}\"", runner.query);
        assertEquals("{\"overallScore\":80}", response.getAnswer());
    }

    @Test
    void encodesObjectJsonAsStringForStringTypedTaskFlowInput() throws Exception {
        RecordingRunner runner = new RecordingRunner(Stream.of(
                AgentMessage.chat("message-1", "{\"summary\":\"available\"}", false)));
        KingdeeAgentSdkTaskFlowClient client = new KingdeeAgentSdkTaskFlowClient(sdkConfig(), runner);
        AgentTaskFlowRequestDto request = new AgentTaskFlowRequestDto();
        String question = "{\"mode\":\"DEEP_PROFILE\",\"profileFacts\":{\"targetRole\":\"Java\"}}";
        request.putInput("question", question);

        AgentTaskFlowResponseDto response = client.execute(request);

        assertTrue(response.isSuccess());
        assertTrue(runner.query.startsWith("\"") && runner.query.endsWith("\""));
        assertEquals(question, new com.fasterxml.jackson.databind.ObjectMapper()
                .readValue(runner.query, String.class));
    }

    @Test
    void sendsRawJsonWhenAgentConfigurationDisablesSecondEncoding() {
        AgentPlatformTaskFlowConfig config = sdkConfig();
        config.setJsonEncodeAgentQuery(false);
        RecordingRunner runner = new RecordingRunner(Stream.of(
                AgentMessage.chat("message-1", "{\"summary\":\"available\"}", false)));
        KingdeeAgentSdkTaskFlowClient client = new KingdeeAgentSdkTaskFlowClient(config, runner);
        AgentTaskFlowRequestDto request = new AgentTaskFlowRequestDto();
        String question = "{\"mode\":\"STUDY_PLANNING\",\"direction\":\"STUDY_ABROAD\"}";
        request.putInput("question", question);

        AgentTaskFlowResponseDto response = client.execute(request);

        assertTrue(response.isSuccess());
        assertEquals(question, runner.query);
    }

    @Test
    void directlyRunsConfiguredTaskFlowWithStringParams() {
        AgentPlatformTaskFlowConfig config = new AgentPlatformTaskFlowConfig();
        config.setEnabled(true);
        config.setTaskFlowCode("process-profile");
        RecordingRunner agentRunner = new RecordingRunner(Stream.of(
                AgentMessage.chat("message-1", "should not run", false)));
        Map<String, String> output = new LinkedHashMap<String, String>();
        output.put("answer", "{\"summary\":\"偏好先理解整体再补充细节\"}");
        AgentMessage.EndOutputData data = new AgentMessage.EndOutputData();
        data.setOutput(output);
        RecordingFlowRunner flowRunner = new RecordingFlowRunner(Stream.of(
                new AgentMessage(AgentMessage.MessageType.END_OUTPUT, data)));
        KingdeeAgentSdkTaskFlowClient client = new KingdeeAgentSdkTaskFlowClient(
                config, agentRunner, flowRunner);
        AgentTaskFlowRequestDto request = new AgentTaskFlowRequestDto();
        String question = "{\"mode\":\"ASSESSMENT_INTERPRETATION\"}";
        request.putInput("question", question);

        AgentTaskFlowResponseDto response = client.execute(request);

        assertTrue(response.isSuccess());
        assertEquals("process-profile", flowRunner.flowNumber);
        assertEquals(question, flowRunner.query);
        assertEquals(question, flowRunner.params.get("input"));
        assertEquals(question, flowRunner.params.get("question"));
        assertEquals(null, agentRunner.query);
        assertEquals("{\"summary\":\"偏好先理解整体再补充细节\"}", response.getAnswer());
    }

    @Test
    void returnsUnavailableWhenAgentReportsAnError() {
        KingdeeAgentSdkTaskFlowClient client = new KingdeeAgentSdkTaskFlowClient(sdkConfig(),
                (agentNumber, query) -> Stream.of(AgentMessage.error("task-1", "temporary failure")));
        AgentTaskFlowRequestDto request = new AgentTaskFlowRequestDto();
        request.putInput("question", "{\"resumeText\":\"Java 项目\"}");

        AgentTaskFlowResponseDto response = client.execute(request);

        assertFalse(response.isSuccess());
        assertEquals("SDK_ERROR", response.getErrorCode());
    }

    @Test
    void retriesOnceWhenFirstAgentRunHasNoUsableAnswer() {
        RetryingRunner runner = new RetryingRunner();
        KingdeeAgentSdkTaskFlowClient client = new KingdeeAgentSdkTaskFlowClient(sdkConfig(), runner);
        AgentTaskFlowRequestDto request = new AgentTaskFlowRequestDto();
        request.putInput("question", "test");

        AgentTaskFlowResponseDto response = client.execute(request);

        assertTrue(response.isSuccess());
        assertEquals(2, runner.invocations);
        assertEquals("{\"summary\":\"available after retry\"}", response.getAnswer());
    }

    @Test
    void usesLastCompleteJsonObjectWhenAgentReturnsAnalysisAndPlanObjects() {
        RecordingRunner runner = new RecordingRunner(Stream.of(AgentMessage.chat("message-1",
                "用户情况：{\"targetRole\":\"Java 后端\"}\n规划结果："
                        + "{\"targetRole\":\"Java 后端\",\"weeklyPlan\":{\"actions\":[\"整理项目\"]}}",
                false)));
        KingdeeAgentSdkTaskFlowClient client = new KingdeeAgentSdkTaskFlowClient(sdkConfig(), runner);
        AgentTaskFlowRequestDto request = new AgentTaskFlowRequestDto();
        request.putInput("question", "生成就业路线图");

        AgentTaskFlowResponseDto response = client.execute(request);

        assertTrue(response.isSuccess());
        assertEquals("{\"targetRole\":\"Java 后端\",\"weeklyPlan\":{\"actions\":[\"整理项目\"]}}",
                response.getAnswer());
    }

    @Test
    void usesPlanningObjectForEmploymentAndPostgraduateWhenDebugJsonFollowsIt() {
        String plan = "{\"targetRole\":\"Java 后端\","
                + "\"phases\":[{\"title\":\"准备阶段\"}],"
                + "\"weeklyPlan\":{\"actions\":[\"整理项目\"]}}";
        RecordingRunner runner = new RecordingRunner(Stream.of(AgentMessage.chat("message-1",
                "用户情况：{\"direction\":\"POSTGRADUATE_EXAM\"}\n规划结果："
                        + plan + "\n调试：{\"trace\":\"done\"}", false)));
        KingdeeAgentSdkTaskFlowClient client = new KingdeeAgentSdkTaskFlowClient(sdkConfig(), runner);
        AgentTaskFlowRequestDto request = new AgentTaskFlowRequestDto();
        request.putInput("question", "生成规划路线图");

        AgentTaskFlowResponseDto response = client.execute(request);

        assertTrue(response.isSuccess());
        assertEquals(plan, response.getAnswer());
    }

    @Test
    void readsStructuredTaskFlowOutputFromEndOutputMessage() {
        Map<String, String> output = new LinkedHashMap<String, String>();
        output.put("prompt_output", "{\"summary\":\"偏好按计划推进任务\"}");
        AgentMessage.EndOutputData data = new AgentMessage.EndOutputData();
        data.setOutput(output);
        AgentMessage endOutput = new AgentMessage(AgentMessage.MessageType.END_OUTPUT, data);
        RecordingRunner runner = new RecordingRunner(Stream.of(endOutput));
        KingdeeAgentSdkTaskFlowClient client = new KingdeeAgentSdkTaskFlowClient(sdkConfig(), runner);
        AgentTaskFlowRequestDto request = new AgentTaskFlowRequestDto();
        request.putInput("question", "{\"mode\":\"ASSESSMENT_INTERPRETATION\"}");

        AgentTaskFlowResponseDto response = client.execute(request);

        assertTrue(response.isSuccess());
        assertEquals("{\"summary\":\"偏好按计划推进任务\"}", response.getAnswer());
    }

    @Test
    void prefersAnswerFieldWhenEndOutputContainsSeveralValues() {
        Map<String, String> output = new LinkedHashMap<String, String>();
        output.put("debug", "ignored");
        output.put("answer", "{\"summary\":\"使用最终输出\"}");
        AgentMessage.EndOutputData data = new AgentMessage.EndOutputData();
        data.setOutput(output);
        AgentMessage endOutput = new AgentMessage(AgentMessage.MessageType.END_OUTPUT, data);
        KingdeeAgentSdkTaskFlowClient client = new KingdeeAgentSdkTaskFlowClient(sdkConfig(),
                new RecordingRunner(Stream.of(endOutput)));
        AgentTaskFlowRequestDto request = new AgentTaskFlowRequestDto();
        request.putInput("question", "test");

        AgentTaskFlowResponseDto response = client.execute(request);

        assertTrue(response.isSuccess());
        assertEquals("{\"summary\":\"使用最终输出\"}", response.getAnswer());
    }

    @Test
    void keepsReadingWhenAnErrorMessageIsFollowedByTaskFlowOutput() {
        Map<String, String> output = new LinkedHashMap<String, String>();
        output.put("promptOutput", "{\"summary\":\"available\"}");
        AgentMessage.EndOutputData data = new AgentMessage.EndOutputData();
        data.setOutput(output);
        AgentMessage endOutput = new AgentMessage(AgentMessage.MessageType.END_OUTPUT, data);
        KingdeeAgentSdkTaskFlowClient client = new KingdeeAgentSdkTaskFlowClient(sdkConfig(),
                new RecordingRunner(Stream.of(
                        AgentMessage.error("task-1", "intermediate failure"), endOutput)));
        AgentTaskFlowRequestDto request = new AgentTaskFlowRequestDto();
        request.putInput("question", "test");

        AgentTaskFlowResponseDto response = client.execute(request);

        assertTrue(response.isSuccess());
        assertEquals("{\"summary\":\"available\"}", response.getAnswer());
    }

    @Test
    void selectsJsonValueFromArbitrarilyNamedTaskFlowOutput() {
        Map<String, String> output = new LinkedHashMap<String, String>();
        output.put("traceId", "trace-123");
        output.put("profile_result", "{\"summary\":\"available\"}");
        AgentMessage.EndOutputData data = new AgentMessage.EndOutputData();
        data.setOutput(output);
        AgentMessage endOutput = new AgentMessage(AgentMessage.MessageType.END_OUTPUT, data);
        KingdeeAgentSdkTaskFlowClient client = new KingdeeAgentSdkTaskFlowClient(sdkConfig(),
                new RecordingRunner(Stream.of(endOutput)));
        AgentTaskFlowRequestDto request = new AgentTaskFlowRequestDto();
        request.putInput("question", "test");

        AgentTaskFlowResponseDto response = client.execute(request);

        assertTrue(response.isSuccess());
        assertEquals("{\"summary\":\"available\"}", response.getAnswer());
    }

    @Test
    void readsJsonFromMultiMessageElements() {
        AgentMessage.MessageElement label = new AgentMessage.MessageElement("text", "analysis: ");
        AgentMessage.MessageElement result = new AgentMessage.MessageElement(
                "text", "{\"summary\":\"available\"}");
        AgentMessage multi = AgentMessage.multiMsg(Arrays.asList(label, result), "message-1");
        KingdeeAgentSdkTaskFlowClient client = new KingdeeAgentSdkTaskFlowClient(sdkConfig(),
                new RecordingRunner(Stream.of(multi)));
        AgentTaskFlowRequestDto request = new AgentTaskFlowRequestDto();
        request.putInput("question", "test");

        AgentTaskFlowResponseDto response = client.execute(request);

        assertTrue(response.isSuccess());
        assertEquals("{\"summary\":\"available\"}", response.getAnswer());
    }

    @Test
    void readsJsonFromUnknownSdkMessage() {
        AgentMessage unknown = new AgentMessage(AgentMessage.MessageType.UNKNOWN,
                new AgentMessage.UnknownData("{\"summary\":\"available\"}"));
        KingdeeAgentSdkTaskFlowClient client = new KingdeeAgentSdkTaskFlowClient(sdkConfig(),
                new RecordingRunner(Stream.of(unknown)));
        AgentTaskFlowRequestDto request = new AgentTaskFlowRequestDto();
        request.putInput("question", "test");

        AgentTaskFlowResponseDto response = client.execute(request);

        assertTrue(response.isSuccess());
        assertEquals("{\"summary\":\"available\"}", response.getAnswer());
    }

    @Test
    void readsJsonFromConfirmCardContent() {
        AgentMessage confirmCard = AgentMessage.confirmCard(
                "message-1", "{\"summary\":\"available from card\"}", null);
        KingdeeAgentSdkTaskFlowClient client = new KingdeeAgentSdkTaskFlowClient(sdkConfig(),
                new RecordingRunner(Stream.of(confirmCard)));
        AgentTaskFlowRequestDto request = new AgentTaskFlowRequestDto();
        request.putInput("question", "test");

        AgentTaskFlowResponseDto response = client.execute(request);

        assertTrue(response.isSuccess());
        assertEquals("{\"summary\":\"available from card\"}", response.getAnswer());
    }

    @Test
    void unwrapsJsonEncodedStringFromTaskFlowOutput() {
        Map<String, String> output = new LinkedHashMap<String, String>();
        output.put("answer", "\"{\\\"summary\\\":\\\"available\\\"}\"");
        AgentMessage.EndOutputData data = new AgentMessage.EndOutputData();
        data.setOutput(output);
        AgentMessage endOutput = new AgentMessage(AgentMessage.MessageType.END_OUTPUT, data);
        KingdeeAgentSdkTaskFlowClient client = new KingdeeAgentSdkTaskFlowClient(sdkConfig(),
                new RecordingRunner(Stream.of(endOutput)));
        AgentTaskFlowRequestDto request = new AgentTaskFlowRequestDto();
        request.putInput("question", "test");

        AgentTaskFlowResponseDto response = client.execute(request);

        assertTrue(response.isSuccess());
        assertEquals("{\"summary\":\"available\"}", response.getAnswer());
    }

    private static AgentPlatformTaskFlowConfig sdkConfig() {
        AgentPlatformTaskFlowConfig config = new AgentPlatformTaskFlowConfig();
        config.setEnabled(true);
        config.setAgentNumber("agent-resume-diagnosis");
        return config;
    }

    private static class RecordingRunner implements KingdeeAgentSdkTaskFlowClient.AgentRunner {
        private final Stream<AgentMessage> result;
        private String agentNumber;
        private String query;

        RecordingRunner(Stream<AgentMessage> result) {
            this.result = result;
        }

        @Override
        public Stream<AgentMessage> run(String agentNumber, String query) {
            this.agentNumber = agentNumber;
            this.query = query;
            return result;
        }
    }

    private static class RetryingRunner implements KingdeeAgentSdkTaskFlowClient.AgentRunner {
        private int invocations;

        @Override
        public Stream<AgentMessage> run(String agentNumber, String query) {
            invocations++;
            if (invocations == 1) {
                return Stream.of(AgentMessage.error("task-1", "temporary failure"));
            }
            return Stream.of(AgentMessage.chat("message-2",
                    "{\"summary\":\"available after retry\"}", false));
        }
    }

    private static class RecordingFlowRunner implements KingdeeAgentSdkTaskFlowClient.FlowRunner {
        private final Stream<AgentMessage> result;
        private String flowNumber;
        private String query;
        private Map<String, String> params;

        private RecordingFlowRunner(Stream<AgentMessage> result) {
            this.result = result;
        }

        @Override
        public Stream<AgentMessage> run(String flowNumber,
                                        String query,
                                        Map<String, String> params) {
            this.flowNumber = flowNumber;
            this.query = query;
            this.params = params;
            return result;
        }
    }
}
