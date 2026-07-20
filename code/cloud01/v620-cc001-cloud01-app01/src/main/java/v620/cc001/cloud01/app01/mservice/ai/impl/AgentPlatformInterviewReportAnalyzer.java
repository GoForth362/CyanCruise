package v620.cc001.cloud01.app01.mservice.ai.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import v620.cc001.base.common.dto.ai.AgentTaskFlowRequestDto;
import v620.cc001.base.common.dto.ai.AgentTaskFlowResponseDto;
import v620.cc001.base.common.dto.career.InterviewAdviceItemDto;
import v620.cc001.base.common.dto.career.InterviewRadarScoreDto;
import v620.cc001.base.common.dto.career.InterviewReportDto;
import v620.cc001.base.common.dto.career.InterviewSessionDto;
import v620.cc001.cloud01.app01.mservice.ai.AgentPlatformTaskFlowClient;
import v620.cc001.cloud01.app01.mservice.ai.AgentPlatformTaskFlowConfig;
import v620.cc001.cloud01.app01.mservice.ai.InterviewReportAnalyzer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Calls the dedicated interview Agent and accepts only complete evidence-based reports. */
public class AgentPlatformInterviewReportAnalyzer implements InterviewReportAnalyzer {

    public static final String CONFIG_PREFIX = "cc001.agent.platform.interview";
    private static final int MAX_ATTEMPTS = 3;
    private static final long RETRY_DELAY_MILLIS = 250L;
    private static final String[] DIMENSIONS = new String[] {
            "expression", "logic", "technical", "pressureResistance", "communication"
    };

    private final AgentPlatformTaskFlowClient client;
    private final AgentPlatformTaskFlowConfig config;
    private final ObjectMapper mapper;

    public AgentPlatformInterviewReportAnalyzer(AgentPlatformTaskFlowClient client,
                                                AgentPlatformTaskFlowConfig config) {
        this(client, config, new ObjectMapper());
    }

    public AgentPlatformInterviewReportAnalyzer(AgentPlatformTaskFlowClient client,
                                                AgentPlatformTaskFlowConfig config,
                                                ObjectMapper mapper) {
        this.client = client;
        this.config = config == null ? new AgentPlatformTaskFlowConfig() : config;
        this.mapper = mapper == null ? new ObjectMapper() : mapper;
    }

    @Override
    public InterviewReportDto analyze(InterviewSessionDto session, String transcript, int answerCount) {
        if (client == null) {
            throw unavailable("AI 面试分析暂未配置，请稍后重试。", null);
        }
        Exception lastError = null;
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                AgentTaskFlowResponseDto response = client.execute(request(session, transcript, answerCount));
                InterviewReportDto report = response == null || !response.isSuccess()
                        ? null : parse(response.getAnswer(), answerCount);
                if (report != null) {
                    return report;
                }
            } catch (Exception error) {
                lastError = error;
            }
            if (attempt < MAX_ATTEMPTS) {
                waitBeforeRetry();
            }
        }
        throw unavailable("AI 面试分析暂时不可用，请稍后重试。", lastError);
    }

    AgentTaskFlowRequestDto request(InterviewSessionDto session, String transcript, int answerCount) throws Exception {
        Map<String, Object> context = new LinkedHashMap<String, Object>();
        context.put("positionName", value(session == null ? null : session.getPositionName()));
        context.put("difficulty", value(session == null ? null : session.getDifficulty()));
        context.put("mode", value(session == null ? null : session.getMode()));
        context.put("answerCount", Integer.valueOf(answerCount));
        context.put("transcript", limit(transcript, 16000));
        context.put("analysisInstructions", "只根据本次问题和回答评分，不得编造经历。"
                + "返回严格 JSON：overallScore,totalQuestions,radarScore,scoreReasons,strengths,improvements,textSummary。"
                + "radarScore 和 scoreReasons 必须包含 expression、logic、technical、pressureResistance、communication。"
                + "每项分数为0到100整数；每项评分依据、每条优点和改进建议必须引用实际回答或明确指出哪一题证据不足。"
                + "strengths 和 improvements 的每项格式为 title、detail，均不得为空。所有文字使用普通用户能理解的简体中文。");
        AgentTaskFlowRequestDto request = new AgentTaskFlowRequestDto();
        request.setTaskFlowCode(invocationCode());
        request.putInput("question", mapper.writeValueAsString(context));
        return request;
    }

    InterviewReportDto parse(String raw, int answerCount) {
        try {
            JsonNode root = mapper.readTree(jsonObject(raw));
            if (root == null || !root.isObject() || !validInteger(root.get("overallScore"), 0, 100)
                    || !validInteger(root.get("totalQuestions"), answerCount, answerCount)) {
                return null;
            }
            JsonNode radarNode = root.get("radarScore");
            JsonNode reasonsNode = root.get("scoreReasons");
            if (radarNode == null || !radarNode.isObject() || reasonsNode == null || !reasonsNode.isObject()) {
                return null;
            }
            Map<String, String> reasons = new LinkedHashMap<String, String>();
            for (String dimension : DIMENSIONS) {
                if (!validInteger(radarNode.get(dimension), 0, 100)
                        || !hasText(reasonsNode.path(dimension).asText(null))) {
                    return null;
                }
                reasons.put(dimension, reasonsNode.path(dimension).asText().trim());
            }
            List<InterviewAdviceItemDto> strengths = advice(root.get("strengths"));
            List<InterviewAdviceItemDto> improvements = advice(root.get("improvements"));
            String summary = root.path("textSummary").asText("").trim();
            if (strengths.isEmpty() || improvements.isEmpty() || !hasText(summary)) {
                return null;
            }
            InterviewRadarScoreDto radar = new InterviewRadarScoreDto();
            radar.setExpression(Integer.valueOf(radarNode.path("expression").asInt()));
            radar.setLogic(Integer.valueOf(radarNode.path("logic").asInt()));
            radar.setTechnical(Integer.valueOf(radarNode.path("technical").asInt()));
            radar.setPressureResistance(Integer.valueOf(radarNode.path("pressureResistance").asInt()));
            radar.setCommunication(Integer.valueOf(radarNode.path("communication").asInt()));
            InterviewReportDto report = new InterviewReportDto();
            report.setOverallScore(Integer.valueOf(root.path("overallScore").asInt()));
            report.setTotalQuestions(Integer.valueOf(answerCount));
            report.setRadarScore(radar);
            report.setScoreReasons(reasons);
            report.setStrengths(strengths);
            report.setImprovements(improvements);
            report.setTextSummary(summary);
            return report;
        } catch (Exception ignored) {
            return null;
        }
    }

    private List<InterviewAdviceItemDto> advice(JsonNode array) {
        List<InterviewAdviceItemDto> result = new ArrayList<InterviewAdviceItemDto>();
        if (array == null || !array.isArray()) return result;
        for (JsonNode node : array) {
            String title = node.path("title").asText("").trim();
            String detail = node.path("detail").asText("").trim();
            if (!hasText(title) || !hasText(detail)) return new ArrayList<InterviewAdviceItemDto>();
            InterviewAdviceItemDto item = new InterviewAdviceItemDto();
            item.setTitle(title);
            item.setDetail(detail);
            result.add(item);
        }
        return result;
    }

    private boolean validInteger(JsonNode node, int min, int max) {
        return node != null && node.isIntegralNumber() && node.asInt() >= min && node.asInt() <= max;
    }

    private String jsonObject(String raw) {
        if (!hasText(raw)) return null;
        int start = raw.indexOf('{');
        int end = raw.lastIndexOf('}');
        return start >= 0 && end > start ? raw.substring(start, end + 1) : raw.trim();
    }

    private String invocationCode() {
        return hasText(config.getAgentNumber()) ? config.getAgentNumber() : config.getTaskFlowCode();
    }

    private String value(String text) { return text == null ? "" : text; }
    private String limit(String text, int max) {
        String safe = text == null ? "" : text.trim();
        return safe.length() <= max ? safe : safe.substring(safe.length() - max);
    }
    private boolean hasText(String text) { return text != null && text.trim().length() > 0; }
    private IllegalStateException unavailable(String message, Exception cause) {
        return cause == null ? new IllegalStateException(message) : new IllegalStateException(message, cause);
    }
    private void waitBeforeRetry() {
        try { Thread.sleep(RETRY_DELAY_MILLIS); }
        catch (InterruptedException error) { Thread.currentThread().interrupt(); }
    }
}
