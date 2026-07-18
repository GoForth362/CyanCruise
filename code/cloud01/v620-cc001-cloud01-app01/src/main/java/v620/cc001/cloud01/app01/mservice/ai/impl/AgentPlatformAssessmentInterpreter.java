package v620.cc001.cloud01.app01.mservice.ai.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import v620.cc001.base.common.dto.ai.AgentTaskFlowRequestDto;
import v620.cc001.base.common.dto.ai.AgentTaskFlowResponseDto;
import v620.cc001.base.common.dto.career.AssessmentAnswerSnapshot;
import v620.cc001.base.common.dto.career.AssessmentScoreResult;
import v620.cc001.cloud01.app01.mservice.ai.AgentPlatformTaskFlowClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Requests a cautious AI explanation for one completed assessment. */
public class AgentPlatformAssessmentInterpreter {

    private final AgentPlatformTaskFlowClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public AgentPlatformAssessmentInterpreter(AgentPlatformTaskFlowClient client) {
        this.client = client;
    }

    public AssessmentScoreResult.AiInterpretation interpret(AssessmentScoreResult result) {
        if (client == null) throw new IllegalStateException("当前没有可用的 AI 服务，请联系管理员完成连接后重试");
        try {
            Map<String, Object> input = new LinkedHashMap<String, Object>();
            input.put("mode", "ASSESSMENT_INTERPRETATION");
            input.put("assessmentResult", assessmentPayload(result));
            input.put("instructions", "仅解释当前测评结果和答题快照；不得把测评倾向写成确定能力或职业结论。只返回约定 JSON。");
            AgentTaskFlowRequestDto request = new AgentTaskFlowRequestDto();
            request.putInput("question", mapper.writeValueAsString(input));
            AgentTaskFlowResponseDto response = client.execute(request);
            if (response == null || !response.isSuccess()) throw new IllegalStateException(unavailableMessage(response));
            return parse(response.getAnswer());
        } catch (IllegalStateException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalStateException("测评 AI 解读暂时不可用，请稍后重试", ex);
        }
    }

    private AssessmentScoreResult.AiInterpretation parse(String answer) throws Exception {
        String rawAnswer = answer == null ? "" : answer.trim();
        if (rawAnswer.length() == 0) {
            throw invalidResponse();
        }
        try {
            return parseStructured(rawAnswer);
        } catch (IllegalStateException ex) {
            throw ex;
        } catch (Exception ex) {
            throw invalidResponse();
        }
    }

    private AssessmentScoreResult.AiInterpretation parseStructured(String answer) throws Exception {
        String json = jsonObject(answer);
        JsonNode root = mapper.readTree(json);
        if (root == null || !root.isObject() || isAgentInstruction(root) || text(root, "summary") == null) {
            throw invalidResponse();
        }
        AssessmentScoreResult.AiInterpretation value = new AssessmentScoreResult.AiInterpretation();
        value.setSummary(text(root, "summary"));
        value.setInsights(strings(root.path("insights")));
        value.setSuggestions(strings(root.path("suggestions")));
        value.setDataGaps(strings(root.path("dataGaps")));
        value.setSource("AI_ASSESSMENT_INTERPRETATION");
        value.setGeneratedAt(LocalDateTime.now());
        return value;
    }

    private boolean isAgentInstruction(JsonNode root) {
        return root.has("Thought") || root.has("Action") || root.has("Action_input")
                || root.has("thought") || root.has("action") || root.has("action_input");
    }

    private IllegalStateException invalidResponse() {
        return new IllegalStateException("AI 服务没有返回可用分析，请稍后重试");
    }

    private Map<String, Object> assessmentPayload(AssessmentScoreResult result) {
        Map<String, Object> payload = new LinkedHashMap<String, Object>();
        if (result == null) return payload;
        payload.put("recordId", result.getRecordId());
        payload.put("scaleId", result.getScaleId());
        payload.put("scaleTitle", result.getScaleTitle());
        payload.put("resultSummary", result.getResultSummary());
        payload.put("dimensionCounts", result.getDimensionCounts());
        payload.put("suggestedRoles", result.getSuggestedRoles());
        payload.put("answers", answerPayload(result.getAnswers()));
        return payload;
    }

    private List<Map<String, Object>> answerPayload(List<AssessmentAnswerSnapshot> answers) {
        List<Map<String, Object>> payload = new ArrayList<Map<String, Object>>();
        if (answers == null) return payload;
        for (AssessmentAnswerSnapshot answer : answers) {
            if (answer == null) continue;
            Map<String, Object> item = new LinkedHashMap<String, Object>();
            item.put("questionId", answer.getQuestionId());
            item.put("questionText", answer.getQuestionText());
            item.put("optionId", answer.getOptionId());
            item.put("optionText", answer.getOptionText());
            item.put("dimensionCode", answer.getDimensionCode());
            item.put("scoreSnapshot", answer.getScoreSnapshot());
            item.put("validOption", Boolean.valueOf(answer.isValidOption()));
            payload.add(item);
        }
        return payload;
    }

    private String jsonObject(String answer) {
        if (answer == null) return null;
        int start = answer.indexOf('{');
        int end = answer.lastIndexOf('}');
        return start >= 0 && end > start ? answer.substring(start, end + 1) : answer;
    }

    private String unavailableMessage(AgentTaskFlowResponseDto response) {
        if (response != null && "SDK_ERROR".equals(response.getErrorCode())) {
            return "AI 服务连接失败，请稍后重试";
        }
        if (response != null && "INVALID_RESPONSE".equals(response.getErrorCode())) {
            return "AI 服务没有返回可用分析，请稍后重试";
        }
        return "测评 AI 解读生成失败，请稍后重试";
    }

    private String text(JsonNode node, String field) {
        String value = node == null ? "" : node.path(field).asText("").trim();
        return value.length() == 0 ? null : value;
    }

    private List<String> strings(JsonNode node) {
        List<String> values = new ArrayList<String>();
        if (!node.isArray()) return values;
        Iterator<JsonNode> iterator = node.elements();
        while (iterator.hasNext()) {
            String value = iterator.next().asText("").trim();
            if (value.length() > 0) values.add(value);
        }
        return values;
    }
}
