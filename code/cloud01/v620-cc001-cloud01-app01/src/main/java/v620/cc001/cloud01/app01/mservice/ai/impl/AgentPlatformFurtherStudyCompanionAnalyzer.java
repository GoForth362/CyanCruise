package v620.cc001.cloud01.app01.mservice.ai.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import v620.cc001.base.common.dto.ai.AgentTaskFlowRequestDto;
import v620.cc001.base.common.dto.ai.AgentTaskFlowResponseDto;
import v620.cc001.cloud01.app01.mservice.ai.AgentPlatformTaskFlowClient;
import v620.cc001.cloud01.app01.mservice.ai.AgentPlatformTaskFlowConfig;
import v620.cc001.cloud01.app01.mservice.ai.FurtherStudyCompanionAnalyzer;
import v620.cc001.cloud01.app01.mservice.furtherstudy.FurtherStudyCompanionStorage;

/** Calls the published further-study companion Agent and validates its structured response. */
public class AgentPlatformFurtherStudyCompanionAnalyzer implements FurtherStudyCompanionAnalyzer {

    public static final String CONFIG_PREFIX = "cc001.agent.platform.study.companion";
    private static final String MODE = "FURTHER_STUDY_ANALYSIS";

    private final AgentPlatformTaskFlowClient client;
    private final AgentPlatformTaskFlowClient taskFlowFallbackClient;
    private final AgentPlatformTaskFlowConfig config;
    private final ObjectMapper mapper;

    public AgentPlatformFurtherStudyCompanionAnalyzer(AgentPlatformTaskFlowClient client,
                                                       AgentPlatformTaskFlowConfig config) {
        this(client, null, config);
    }

    AgentPlatformFurtherStudyCompanionAnalyzer(AgentPlatformTaskFlowClient client,
                                                AgentPlatformTaskFlowClient taskFlowFallbackClient,
                                                AgentPlatformTaskFlowConfig config) {
        this(client, taskFlowFallbackClient, config, null);
    }

    AgentPlatformFurtherStudyCompanionAnalyzer(AgentPlatformTaskFlowClient client,
                                                AgentPlatformTaskFlowClient taskFlowFallbackClient,
                                                AgentPlatformTaskFlowConfig config,
                                                FurtherStudyCompanionStorage storage) {
        this.client = client;
        this.taskFlowFallbackClient = taskFlowFallbackClient;
        this.config = config == null ? new AgentPlatformTaskFlowConfig() : config;
        this.mapper = new ObjectMapper();
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public static AgentPlatformFurtherStudyCompanionAnalyzer fromSystemProperties() {
        return fromSystemProperties(null);
    }

    /**
     * Retained for constructor compatibility. Analysis intentionally uses only
     * the current page submission, never persisted records or user profiles.
     */
    public static AgentPlatformFurtherStudyCompanionAnalyzer fromSystemProperties(
            FurtherStudyCompanionStorage storage) {
        AgentPlatformTaskFlowConfig config = AgentPlatformTaskFlowConfig.fromSystemProperties(CONFIG_PREFIX);
        String taskFlowCode = config.getTaskFlowCode();
        config.setTaskFlowCode(null);
        config.setJsonEncodeAgentQuery(false);
        AgentPlatformTaskFlowClient client = config.isEnabled() && hasText(config.getAgentNumber())
                ? new KingdeeAgentSdkTaskFlowClient(config) : null;
        AgentPlatformTaskFlowClient taskFlowFallbackClient = null;
        if (config.isEnabled() && hasText(taskFlowCode)) {
            AgentPlatformTaskFlowConfig fallbackConfig =
                    AgentPlatformTaskFlowConfig.fromSystemProperties(CONFIG_PREFIX);
            fallbackConfig.setTaskFlowCode(taskFlowCode);
            taskFlowFallbackClient = new KingdeeAgentSdkTaskFlowClient(fallbackConfig);
        }
        return new AgentPlatformFurtherStudyCompanionAnalyzer(
                client, taskFlowFallbackClient, config, storage);
    }

    @Override
    public <T> T analyze(String userId, String taskType, Object payload, Class<T> resultType) {
        String safeUserId = requireText(userId, "请先确认当前登录身份，再使用升学陪伴功能。");
        String safeTaskType = requireText(taskType, "升学分析任务类型不能为空。");
        if (resultType == null) {
            throw new IllegalArgumentException("升学分析结果类型不能为空。");
        }
        ensureAvailable();
        AgentTaskFlowRequestDto request = new AgentTaskFlowRequestDto();
        request.setTaskFlowCode(null);
        request.putInput("question", question(safeUserId, safeTaskType, payload));
        AgentTaskFlowResponseDto response = client.execute(request);
        if (response == null || !response.isSuccess() || !hasText(response.getAnswer())) {
            return executeFallback(request, safeTaskType, resultType,
                    "升学分析智能服务暂时不可用，请稍后重试。");
        }
        try {
            return parse(response.getAnswer(), safeTaskType, resultType);
        } catch (IllegalStateException error) {
            return executeFallback(request, safeTaskType, resultType, error.getMessage());
        }
    }

    private <T> T executeFallback(AgentTaskFlowRequestDto request,
                                  String taskType,
                                  Class<T> resultType,
                                  String primaryMessage) {
        if (taskFlowFallbackClient == null) {
            throw new IllegalStateException(primaryMessage);
        }
        AgentTaskFlowResponseDto fallback = taskFlowFallbackClient.execute(request);
        if (fallback == null || !fallback.isSuccess() || !hasText(fallback.getAnswer())) {
            throw new IllegalStateException(primaryMessage);
        }
        return parse(fallback.getAnswer(), taskType, resultType);
    }

    String question(String userId, String taskType, Object payload) {
        try {
            Map<String, Object> request = new LinkedHashMap<String, Object>();
            request.put("mode", MODE);
            request.put("taskType", taskType);
            request.put("currentDate", LocalDate.now().toString());
            Map<String, Object> payloadValues = payloadValues(payload);
            request.put("payload", payloadValues);
            copyPayloadToEnvelope(request, payloadValues);
        request.put("profileContext", new LinkedHashMap<String, Object>());
        request.put("userMaterials", new java.util.ArrayList<Object>());
            return mapper.writeValueAsString(request);
        } catch (Exception error) {
            throw new IllegalStateException("升学分析资料暂时无法整理，请稍后重试。", error);
        }
    }

    private String trackOf(String taskType) {
        if (taskType != null && taskType.startsWith("POSTGRADUATE_")) return "POSTGRADUATE";
        if (taskType != null && taskType.startsWith("RECOMMENDATION_")) return "RECOMMENDATION";
        return "STUDY_ABROAD";
    }

    private Map<String, Object> payloadValues(Object payload) {
        if (payload == null) {
            return new LinkedHashMap<String, Object>();
        }
        try {
            return mapper.convertValue(payload, LinkedHashMap.class);
        } catch (IllegalArgumentException error) {
            throw new IllegalStateException("升学分析表单暂时无法整理，请稍后重试。", error);
        }
    }

    /**
     * The current contract keeps page data under payload. Some already-published
     * task flows still read their fields from the envelope root, so expose the
     * same trusted values there without allowing them to replace envelope keys.
     */
    private void copyPayloadToEnvelope(Map<String, Object> request, Map<String, Object> payload) {
        if (payload == null) {
            return;
        }
        for (Map.Entry<String, Object> entry : payload.entrySet()) {
            String key = entry.getKey();
            if (hasText(key) && !request.containsKey(key)) {
                request.put(key, entry.getValue());
            }
        }
    }

    private <T> T parse(String answer, String expectedTaskType, Class<T> resultType) {
        try {
            JsonNode root = envelope(answer);
            String actualTaskType = text(root, "taskType");
            if (!expectedTaskType.equals(actualTaskType)) {
                throw new IllegalStateException("升学分析返回了不匹配的任务结果，请稍后重试。");
            }
            requireOk(root, root);
            JsonNode result = root.get("result");
            if (result == null || !result.isObject()) {
                throw new IllegalStateException("升学分析结果格式不完整，请稍后重试。");
            }
            requireOk(result, root);
            if (result.size() <= 1) {
                throw new IllegalStateException("升学分析结果内容不完整，请补充信息后重试。");
            }
            T mapped = mapper.treeToValue(result, resultType);
            if (mapped == null) {
                throw new IllegalStateException("升学分析结果格式不完整，请稍后重试。");
            }
            return mapped;
        } catch (IllegalArgumentException error) {
            throw error;
        } catch (IllegalStateException error) {
            throw error;
        } catch (Exception error) {
            throw new IllegalStateException("升学分析结果格式不完整，请稍后重试。", error);
        }
    }

    private JsonNode envelope(String answer) throws Exception {
        String value = answer == null ? "" : answer.trim();
        if (value.startsWith("```")) {
            int firstLine = value.indexOf('\n');
            int closing = value.lastIndexOf("```");
            if (firstLine >= 0 && closing > firstLine) {
                value = value.substring(firstLine + 1, closing).trim();
            }
        }
        JsonNode node = readNode(value);
        for (int depth = 0; depth < 3; depth++) {
            if (node != null && node.isTextual()) {
                node = readNode(node.asText());
                continue;
            }
            if (node != null && node.isObject() && !node.has("taskType")) {
                JsonNode nested = first(node, "answer", "content", "data");
                if (nested != null) {
                    node = nested;
                    continue;
                }
            }
            break;
        }
        if (node == null || !node.isObject()) {
            throw new IllegalStateException("升学分析结果格式不完整，请稍后重试。");
        }
        return node;
    }

    private JsonNode readNode(String value) throws Exception {
        try {
            return mapper.readTree(value);
        } catch (Exception firstFailure) {
            int start = value.indexOf('{');
            int end = value.lastIndexOf('}');
            if (start >= 0 && end > start) {
                return mapper.readTree(value.substring(start, end + 1));
            }
            throw firstFailure;
        }
    }

    private JsonNode first(JsonNode node, String... names) {
        for (String name : names) {
            JsonNode value = node.get(name);
            if (value != null && !value.isNull()) {
                return value;
            }
        }
        return null;
    }

    private void requireOk(JsonNode statusNode, JsonNode root) {
        String status = text(statusNode, "status");
        if ("OK".equals(status)) {
            return;
        }
        String message = text(statusNode, "message");
        JsonNode nestedResult = root == null ? null : root.get("result");
        if (!hasText(message) && nestedResult != null && nestedResult.isObject()) {
            message = text(nestedResult, "message");
        }
        if (!hasText(message) && root != statusNode) {
            message = text(root, "message");
        }
        if ("NEED_MORE_INFO".equals(status) && !hasText(message)) {
            message = "\u667a\u80fd\u4f53\u672a\u8fd4\u56de\u5177\u4f53\u7684\u7f3a\u5931\u4fe1\u606f\u3002\u8bf7\u68c0\u67e5\u5e73\u53f0\u4efb\u52a1\u6d41\u7cfb\u7edf\u63d0\u793a\u8bcd\u662f\u5426\u8981\u6c42\u5728 message \u4e2d\u5217\u51fa\u7f3a\u5c11\u5b57\u6bb5\u3002";
        }
        if ("NEED_MORE_INFO".equals(status)) {
            throw new IllegalArgumentException(hasText(message)
                    ? message : "请补充必要的升学信息后重试。");
        }
        throw new IllegalStateException(hasText(message)
                ? message : "升学分析智能服务暂时不可用，请稍后重试。");
    }

    private void ensureAvailable() {
        if (!config.isEnabled() || !hasText(config.getAgentNumber()) || client == null) {
            throw new IllegalStateException("升学陪伴智能服务尚未配置，请稍后重试。");
        }
    }

    private String text(JsonNode node, String field) {
        JsonNode value = node == null ? null : node.get(field);
        return value != null && value.isTextual() ? value.asText().trim() : null;
    }

    private String requireText(String value, String message) {
        if (!hasText(value)) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

    private static boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
