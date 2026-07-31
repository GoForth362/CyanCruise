package v620.cc001.cloud01.app01.mservice.ai.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import v620.cc001.base.common.dto.ai.AgentTaskFlowRequestDto;
import v620.cc001.base.common.dto.ai.AgentTaskFlowResponseDto;
import v620.cc001.cloud01.app01.mservice.ai.AgentPlatformTaskFlowClient;
import v620.cc001.cloud01.app01.mservice.ai.AgentPlatformTaskFlowConfig;
import v620.cc001.cloud01.app01.mservice.ai.FurtherStudyCompanionAnalyzer;
import v620.cc001.cloud01.app01.mservice.storage.FurtherStudyTaskInputStorage;

/** Calls the published further-study companion task flow and validates its structured response. */
public class AgentPlatformFurtherStudyCompanionAnalyzer implements FurtherStudyCompanionAnalyzer {

    public static final String CONFIG_PREFIX = "cc001.agent.platform.study.companion";
    private static final String MODE = "FURTHER_STUDY_ANALYSIS";
    private static final Logger LOGGER =
            Logger.getLogger(AgentPlatformFurtherStudyCompanionAnalyzer.class.getName());

    private final AgentPlatformTaskFlowClient client;
    private final AgentPlatformTaskFlowConfig config;
    private final ObjectMapper mapper;

    public AgentPlatformFurtherStudyCompanionAnalyzer(AgentPlatformTaskFlowClient client,
                                                       AgentPlatformTaskFlowConfig config) {
        this.client = client;
        this.config = config == null ? new AgentPlatformTaskFlowConfig() : config;
        this.mapper = new ObjectMapper();
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public static AgentPlatformFurtherStudyCompanionAnalyzer fromSystemProperties() {
        AgentPlatformTaskFlowConfig config = AgentPlatformTaskFlowConfig.fromSystemProperties(CONFIG_PREFIX);
        config.setJsonEncodeAgentQuery(false);
        AgentPlatformTaskFlowClient client = config.isEnabled() && hasText(config.getTaskFlowCode())
                ? new KingdeeAgentSdkTaskFlowClient(config) : null;
        return new AgentPlatformFurtherStudyCompanionAnalyzer(client, config);
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
        request.setTaskFlowCode(config.getTaskFlowCode());
        request.putInput("question", question(safeUserId, safeTaskType, payload));
        AgentTaskFlowResponseDto response = client.execute(request);
        if (response == null || !response.isSuccess() || !hasText(response.getAnswer())) {
            throw new IllegalStateException("升学分析任务流暂时不可用，请稍后重试。");
        }
        try {
            return parse(response.getAnswer(), safeTaskType, resultType);
        } catch (RuntimeException firstError) {
            if (!isRetryablePlanTask(safeTaskType)) {
                throw firstError;
            }
            LOGGER.log(Level.WARNING, "Further-study plan result was invalid; retrying the same task flow"
                    + ", taskType=" + safeTaskType
                    + ", answerLength=" + response.getAnswer().length(), firstError);
            AgentTaskFlowResponseDto retry = client.execute(request);
            if (retry == null || !retry.isSuccess() || !hasText(retry.getAnswer())) {
                throw firstError;
            }
            return parse(retry.getAnswer(), safeTaskType, resultType);
        }
    }

    private boolean isRetryablePlanTask(String taskType) {
        return POSTGRADUATE_PLAN_GENERATE.equals(taskType)
                || RECOMMENDATION_PLAN_GENERATE.equals(taskType)
                || STUDY_ABROAD_STATEMENT_OUTLINE.equals(taskType)
                || STUDY_ABROAD_VISA_CHECKLIST.equals(taskType);
    }

    String question(String userId, String taskType, Object payload) {
        try {
            Map<String, Object> request = new LinkedHashMap<String, Object>();
            request.put("mode", MODE);
            request.put("taskType", taskType);
            request.put("currentDate", LocalDate.now().toString());
            Map<String, Object> payloadValues = payloadValues(payload);
            payloadValues = FurtherStudyTaskInputStorage.saveAndLoad(userId, taskType, payloadValues);
            request.put("payload", payloadValues);
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
            T mapped = mapper.treeToValue(normalizeResult(result, expectedTaskType), resultType);
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
        JsonNode node = mapper.readTree(value);
        if (node == null || !node.isObject()) {
            throw new IllegalStateException("升学分析结果格式不完整，请稍后重试。");
        }
        return node;
    }

    private JsonNode normalizeResult(JsonNode result, String taskType) throws Exception {
        if (!STUDY_ABROAD_STATEMENT_OUTLINE.equals(taskType) || !result.isObject()) {
            return result;
        }
        ObjectNode normalized = ((ObjectNode) result).deepCopy();
        normalizeTextField(normalized, "goldenLine");
        normalizeTextField(normalized, "outline");
        return normalized;
    }

    private void normalizeTextField(ObjectNode result, String field) throws Exception {
        JsonNode value = result.get(field);
        if (value == null || value.isNull() || value.isTextual()) {
            return;
        }
        result.put(field, readableText(value));
    }

    private String readableText(JsonNode value) throws Exception {
        if (value == null || value.isNull()) {
            return "";
        }
        if (value.isTextual() || value.isNumber() || value.isBoolean()) {
            return value.asText();
        }
        if (value.isArray()) {
            StringBuilder out = new StringBuilder();
            for (JsonNode item : value) {
                appendParagraph(out, readableText(item));
            }
            return out.toString();
        }
        if (value.isObject()) {
            String title = firstText(value, "section", "title", "name");
            String content = firstText(value, "content", "detail", "text", "description");
            if (hasText(title) || hasText(content)) {
                return hasText(title) && hasText(content) ? title + "：" + content : firstNonEmpty(title, content);
            }
        }
        return mapper.writeValueAsString(value);
    }

    private void appendParagraph(StringBuilder out, String text) {
        if (!hasText(text)) {
            return;
        }
        if (out.length() > 0) {
            out.append("\n\n");
        }
        out.append(text.trim());
    }

    private String firstText(JsonNode node, String... fields) {
        for (String field : fields) {
            String value = text(node, field);
            if (hasText(value)) {
                return value;
            }
        }
        return "";
    }

    private String firstNonEmpty(String first, String second) {
        return hasText(first) ? first : second;
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
        if (!config.isEnabled() || !hasText(config.getTaskFlowCode()) || client == null) {
            throw new IllegalStateException("升学陪伴任务流尚未配置，请稍后重试。");
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
