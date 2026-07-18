package v620.cc001.cloud01.app01.mservice.ai.impl;

import kd.ai.sdk.SDKClient;
import kd.ai.sdk.model.agent.AgentMessage;
import kd.ai.sdk.model.agent.RunAgentRequest;
import kd.ai.sdk.model.agent.RunFlowRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import v620.cc001.base.common.dto.ai.AgentTaskFlowRequestDto;
import v620.cc001.base.common.dto.ai.AgentTaskFlowResponseDto;
import v620.cc001.cloud01.app01.mservice.ai.AgentPlatformTaskFlowClient;
import v620.cc001.cloud01.app01.mservice.ai.AgentPlatformTaskFlowConfig;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Invokes the configured Agent platform agent through the official Cosmic SDK.
 * All platform credentials and the current user context remain inside Cosmic.
 */
public class KingdeeAgentSdkTaskFlowClient implements AgentPlatformTaskFlowClient {

    private static final Logger LOGGER = Logger.getLogger(KingdeeAgentSdkTaskFlowClient.class.getName());
    private static final int MAX_ATTEMPTS = 2;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final AgentPlatformTaskFlowConfig config;
    private final AgentRunner runner;
    private final FlowRunner flowRunner;

    public KingdeeAgentSdkTaskFlowClient(AgentPlatformTaskFlowConfig config) {
        this(config, new CosmicAgentRunner(), new CosmicFlowRunner());
    }

    KingdeeAgentSdkTaskFlowClient(AgentPlatformTaskFlowConfig config, AgentRunner runner) {
        this(config, runner, new CosmicFlowRunner());
    }

    KingdeeAgentSdkTaskFlowClient(AgentPlatformTaskFlowConfig config,
                                 AgentRunner runner,
                                 FlowRunner flowRunner) {
        this.config = config == null ? new AgentPlatformTaskFlowConfig() : config;
        this.runner = runner == null ? new CosmicAgentRunner() : runner;
        this.flowRunner = flowRunner == null ? new CosmicFlowRunner() : flowRunner;
    }

    @Override
    public AgentTaskFlowResponseDto execute(AgentTaskFlowRequestDto request) {
        if (!config.isAgentSdkAvailable()) {
            return AgentTaskFlowResponseDto.unavailable("UNAVAILABLE", config.missingAgentSdkReason());
        }
        String query = question(request);
        if (!hasText(query)) {
            return AgentTaskFlowResponseDto.unavailable("INVALID_REQUEST", "Agent task flow request has no question input");
        }
        Exception lastError = null;
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            long startedAt = System.currentTimeMillis();
            try (Stream<AgentMessage> messages = run(query)) {
                AnswerCollection collected = collectAnswer(messages);
                if (hasText(collected.answer)) {
                    AgentTaskFlowResponseDto response = new AgentTaskFlowResponseDto();
                    response.setSuccess(true);
                    response.setAnswer(collected.answer);
                    logSuccess(attempt, query.length(), startedAt, collected);
                    return response;
                }
                if (hasText(collected.errorCode)) {
                    lastError = new IllegalStateException("Agent SDK error event: " + collected.errorCode);
                }
                LOGGER.warning("Agent SDK returned no usable task flow answer; target="
                        + safeTarget() + ", attempt=" + attempt + ", queryLength=" + query.length()
                        + ", eventTypes=" + collected.eventTypes + ", errorCode=" + safeCode(collected.errorCode)
                        + ", answerSources=" + collected.answerSources + ", outputKeys=" + collected.outputKeys
                        + ", elapsedMs=" + (System.currentTimeMillis() - startedAt));
                diagnostic("no-answer", attempt, query.length(), startedAt,
                        collected.eventTypes, collected.errorCode, collected.answerSources, collected.outputKeys);
            } catch (Exception error) {
                lastError = error;
                LOGGER.log(Level.WARNING, "Agent SDK task flow call failed; target="
                        + safeTarget() + ", attempt=" + attempt + ", queryLength=" + query.length()
                        + ", elapsedMs=" + (System.currentTimeMillis() - startedAt), error);
            }
        }
        return lastError == null
                ? AgentTaskFlowResponseDto.unavailable("INVALID_RESPONSE", "智能体未返回可用结果，请稍后重试")
                : AgentTaskFlowResponseDto.unavailable("SDK_ERROR", "智能体服务暂时不可用，请稍后重试");
    }

    /**
     * The task-flow input is declared as STRING. A JSON string value prevents the agent from
     * emitting an object-valued Action_input while preserving the original JSON request text.
     */
    private String agentQuery(String query) throws Exception {
        return MAPPER.writeValueAsString(query);
    }

    private Stream<AgentMessage> run(String query) throws Exception {
        if (hasText(config.getTaskFlowCode())) {
            Map<String, String> params = new LinkedHashMap<String, String>();
            params.put("input", query);
            params.put("question", query);
            return flowRunner.run(config.getTaskFlowCode(), query, params);
        }
        return runner.run(config.getAgentNumber(), config.isJsonEncodeAgentQuery()
                ? agentQuery(query) : query);
    }

    private String question(AgentTaskFlowRequestDto request) {
        if (request == null || request.getInputs() == null) {
            return null;
        }
        return request.getInputs().get("question");
    }

    private AnswerCollection collectAnswer(Stream<AgentMessage> messages) {
        AnswerCollection collected = new AnswerCollection();
        if (messages == null) {
            return collected;
        }
        StringBuilder content = new StringBuilder();
        List<String> candidates = new ArrayList<String>();
        String taskFlowOutput = null;
        java.util.Iterator<AgentMessage> iterator = messages.iterator();
        while (iterator.hasNext()) {
            AgentMessage message = iterator.next();
            if (message == null) {
                continue;
            }
            collected.appendType(message.getType());
            if (message.isError() && message.getErrorData() != null) {
                collected.errorCode = message.getErrorData().getCode();
            }
            if (message.isChat() && message.getChatData() != null
                    && hasText(message.getChatData().getMessage())) {
                String value = message.getChatData().getMessage();
                content.append(value);
                candidates.add(value);
                collected.appendSource("CHAT");
            }
            if (message.isEndOutput() && message.getEndOutputData() != null) {
                Map<String, String> outputMap = message.getEndOutputData().getOutput();
                collected.appendOutputKeys(outputMap);
                String output = taskFlowOutput(outputMap);
                if (hasText(output)) {
                    taskFlowOutput = output;
                    collected.appendSource("END_OUTPUT");
                }
            }
            if (message.isMultiMsg() && message.getMultiMsgData() != null) {
                appendElements(content, message.getMultiMsgData().getMsgList());
                appendElementCandidates(candidates, message.getMultiMsgData().getMsgList());
                collected.appendSource("MULTI_MSG");
            }
            if (message.isAskUser() && message.getAskUserData() != null) {
                appendElements(content, message.getAskUserData().getMessageElements());
                appendElementCandidates(candidates, message.getAskUserData().getMessageElements());
                collected.appendSource("ASK_USER");
            }
            if (message.isConfirmCard() && message.getConfirmCardData() != null
                    && hasText(message.getConfirmCardData().getContent())) {
                String value = message.getConfirmCardData().getContent();
                content.append(value);
                candidates.add(value);
                collected.appendSource("CONFIRM_CARD");
            }
            if (message.getType() == AgentMessage.MessageType.UNKNOWN
                    && message.getUnknownData() != null
                    && hasText(message.getUnknownData().getData())) {
                String value = message.getUnknownData().getData();
                content.append(value);
                candidates.add(value);
                collected.appendSource("UNKNOWN");
            }
        }
        if (hasText(taskFlowOutput)) {
            collected.answer = normalizedAnswer(taskFlowOutput);
            return collected;
        }
        candidates.add(content.toString());
        collected.answer = bestAnswer(candidates);
        return collected;
    }

    private String bestAnswer(List<String> candidates) {
        String longestJson = null;
        String longestText = null;
        if (candidates == null) {
            return null;
        }
        for (String candidate : candidates) {
            String normalized = normalizedAnswer(candidate);
            if (!hasText(normalized)) {
                continue;
            }
            if (containsCompleteJsonObject(normalized)) {
                String object = jsonObject(normalized);
                if (longestJson == null || object.length() > longestJson.length()) {
                    longestJson = object;
                }
            }
            if (longestText == null || normalized.length() > longestText.length()) {
                longestText = normalized;
            }
        }
        return hasText(longestJson) ? longestJson : longestText;
    }

    private String normalizedAnswer(String value) {
        if (!hasText(value)) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.length() >= 2 && trimmed.charAt(0) == '"'
                && trimmed.charAt(trimmed.length() - 1) == '"') {
            try {
                trimmed = new com.fasterxml.jackson.databind.ObjectMapper().readValue(trimmed, String.class);
            } catch (Exception ignored) {
                // Keep the original value when it is not a JSON-encoded string.
            }
        }
        return jsonObject(trimmed);
    }

    private String taskFlowOutput(Map<String, String> output) {
        if (output == null || output.isEmpty()) {
            return null;
        }
        String preferred = keyedValue(output, new String[] {"answer", "promptoutput", "output"});
        if (hasText(preferred)) {
            return preferred;
        }
        String jsonValue = null;
        String longestValue = null;
        for (String value : output.values()) {
            if (!hasText(value)) {
                continue;
            }
            if (containsCompleteJsonObject(value)
                    && (jsonValue == null || value.length() > jsonValue.length())) {
                jsonValue = value;
            }
            if (longestValue == null || value.length() > longestValue.length()) {
                longestValue = value;
            }
        }
        if (jsonValue != null) {
            return jsonValue;
        }
        String secondary = keyedValue(output, new String[] {"result", "content", "message"});
        return hasText(secondary) ? secondary : longestValue;
    }

    private String keyedValue(Map<String, String> output, String[] keys) {
        for (String key : keys) {
            for (Map.Entry<String, String> entry : output.entrySet()) {
                if (key.equals(normalizeKey(entry.getKey())) && hasText(entry.getValue())) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    private void appendElements(StringBuilder content, List<AgentMessage.MessageElement> elements) {
        if (elements == null) {
            return;
        }
        for (AgentMessage.MessageElement element : elements) {
            if (element != null && hasText(element.getValue())) {
                content.append(element.getValue());
            }
        }
    }

    private void appendElementCandidates(List<String> candidates,
                                         List<AgentMessage.MessageElement> elements) {
        if (elements == null) {
            return;
        }
        for (AgentMessage.MessageElement element : elements) {
            if (element != null && hasText(element.getValue())) {
                candidates.add(element.getValue());
            }
        }
    }

    private String normalizeKey(String key) {
        if (!hasText(key)) {
            return "";
        }
        return key.trim().toLowerCase(Locale.ROOT).replace("-", "").replace("_", "");
    }

    private boolean containsCompleteJsonObject(String value) {
        if (!hasText(value)) {
            return false;
        }
        String object = jsonObject(value);
        return hasText(object) && object.startsWith("{") && object.endsWith("}");
    }

    private String jsonObject(String value) {
        if (!hasText(value)) {
            return null;
        }
        String lastObject = null;
        String lastPlanningObject = null;
        int start = -1;
        int depth = 0;
        boolean inString = false;
        boolean escaped = false;
        for (int index = 0; index < value.length(); index++) {
            char current = value.charAt(index);
            if (inString) {
                if (escaped) {
                    escaped = false;
                } else if (current == '\\') {
                    escaped = true;
                } else if (current == '"') {
                    inString = false;
                }
                continue;
            }
            if (current == '"') {
                inString = true;
            } else if (current == '{') {
                if (depth == 0) {
                    start = index;
                }
                depth++;
            } else if (current == '}' && depth > 0) {
                depth--;
                if (depth == 0 && start >= 0) {
                    lastObject = value.substring(start, index + 1).trim();
                    if (isPlanningJson(lastObject)) {
                        lastPlanningObject = lastObject;
                    }
                    start = -1;
                }
            }
        }
        if (hasText(lastPlanningObject)) {
            return lastPlanningObject;
        }
        return hasText(lastObject) ? lastObject : value.trim();
    }

    private boolean isPlanningJson(String candidate) {
        try {
            com.fasterxml.jackson.databind.JsonNode root = MAPPER.readTree(candidate);
            if (root != null && root.has("data") && root.get("data").isObject()) {
                root = root.get("data");
            }
            if (root == null || !root.isObject()) {
                return false;
            }
            boolean hasTarget = textual(root, "targetRole") || textual(root, "targetSummary");
            return hasTarget && root.has("phases") && root.get("phases").isArray()
                    && root.has("weeklyPlan") && root.get("weeklyPlan").isObject();
        } catch (Exception ignored) {
            return false;
        }
    }

    private boolean textual(com.fasterxml.jackson.databind.JsonNode root, String field) {
        com.fasterxml.jackson.databind.JsonNode value = root.get(field);
        return value != null && value.isTextual() && hasText(value.asText());
    }

    private static boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }

    private void logSuccess(int attempt, int queryLength, long startedAt, AnswerCollection collected) {
        LOGGER.info("Agent SDK task flow completed; target=" + safeTarget()
                + ", attempt=" + attempt + ", queryLength=" + queryLength
                + ", elapsedMs=" + (System.currentTimeMillis() - startedAt));
        diagnostic("completed", attempt, queryLength, startedAt, collected.eventTypes,
                null, collected.answerSources, collected.outputKeys);
    }

    private void diagnostic(String status,
                            int attempt,
                            int queryLength,
                            long startedAt,
                            String eventTypes,
                            String errorCode,
                            String answerSources,
                            String outputKeys) {
        StringBuilder line = new StringBuilder("[CyanCruise AI] status=").append(status)
                .append(", target=").append(safeTarget())
                .append(", attempt=").append(attempt)
                .append(", queryLength=").append(queryLength)
                .append(", elapsedMs=").append(System.currentTimeMillis() - startedAt);
        if (hasText(eventTypes)) {
            line.append(", eventTypes=").append(eventTypes);
        }
        if (hasText(errorCode)) {
            line.append(", errorCode=").append(safeCode(errorCode));
        }
        if (hasText(answerSources)) {
            line.append(", answerSources=").append(answerSources);
        }
        if (hasText(outputKeys)) {
            line.append(", outputKeys=").append(outputKeys);
        }
        System.out.println(line.toString());
    }

    private String safeCode(String value) {
        if (!hasText(value)) {
            return "none";
        }
        return value.replaceAll("[^A-Za-z0-9_.-]", "");
    }

    private String safeTarget() {
        String value = hasText(config.getTaskFlowCode())
                ? config.getTaskFlowCode() : config.getAgentNumber();
        if (!hasText(value) || value.length() <= 6) {
            return "configured";
        }
        return "..." + value.substring(value.length() - 6);
    }

    interface AgentRunner {
        Stream<AgentMessage> run(String agentNumber, String query);
    }

    interface FlowRunner {
        Stream<AgentMessage> run(String flowNumber, String query, Map<String, String> params);
    }

    private static class AnswerCollection {
        private String answer;
        private String errorCode;
        private String eventTypes;
        private String answerSources;
        private String outputKeys;

        private void appendType(AgentMessage.MessageType type) {
            String value = type == null ? "null" : type.name();
            eventTypes = hasText(eventTypes) ? eventTypes + "|" + value : value;
        }

        private void appendSource(String source) {
            if (!hasText(source)) {
                return;
            }
            answerSources = hasText(answerSources) ? answerSources + "|" + source : source;
        }

        private void appendOutputKeys(Map<String, String> output) {
            if (output == null || output.isEmpty()) {
                return;
            }
            Set<String> keys = new LinkedHashSet<String>();
            for (String key : output.keySet()) {
                if (hasText(key)) {
                    keys.add(key.replaceAll("[^A-Za-z0-9_.-]", ""));
                }
            }
            if (!keys.isEmpty()) {
                outputKeys = keys.toString();
            }
        }
    }

    private static class CosmicAgentRunner implements AgentRunner {
        @Override
        public Stream<AgentMessage> run(String agentNumber, String query) {
            RunAgentRequest request = RunAgentRequest.builder()
                    .agentNumber(agentNumber)
                    .query(query)
                    .build();
            return SDKClient.create().getAgentService().runAgent(request);
        }
    }

    private static class CosmicFlowRunner implements FlowRunner {
        @Override
        public Stream<AgentMessage> run(String flowNumber,
                                        String query,
                                        Map<String, String> params) {
            RunFlowRequest request = RunFlowRequest.builder()
                    .flowNumber(flowNumber)
                    .query(query)
                    .params(params)
                    .build();
            return SDKClient.create().getAgentService().runFlow(request);
        }
    }
}
