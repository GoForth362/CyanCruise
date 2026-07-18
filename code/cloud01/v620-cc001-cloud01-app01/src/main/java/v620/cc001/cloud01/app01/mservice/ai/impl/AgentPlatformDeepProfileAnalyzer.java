package v620.cc001.cloud01.app01.mservice.ai.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import v620.cc001.base.common.dto.ai.AgentTaskFlowRequestDto;
import v620.cc001.base.common.dto.ai.AgentTaskFlowResponseDto;
import v620.cc001.base.common.dto.career.AssessmentScoreResult;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;
import v620.cc001.cloud01.app01.mservice.ai.AgentPlatformTaskFlowClient;
import v620.cc001.cloud01.app01.mservice.ai.AgentPlatformTaskFlowConfig;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Converts completed assessments into the configured Agent platform deep profile contract. */
public class AgentPlatformDeepProfileAnalyzer {

    public static final String SOURCE_AI_ASSESSMENT = "AI_ASSESSMENT";
    private final AgentPlatformTaskFlowClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public AgentPlatformDeepProfileAnalyzer(AgentPlatformTaskFlowClient client) {
        this.client = client;
    }

    public UserProfileSnapshot.AiDeepProfileBlock analyze(List<AssessmentScoreResult> results,
                                                           UserProfileSnapshot snapshot) {
        if (client == null) {
            throw new IllegalStateException("深度画像 AI 暂未配置，请稍后重试");
        }
        try {
            AgentTaskFlowResponseDto response = client.execute(request(results, snapshot));
            if (response == null || !response.isSuccess()) {
                throw new IllegalStateException(unavailableMessage(response));
            }
            return parse(response.getAnswer());
        } catch (IllegalStateException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalStateException("深度画像 AI 暂时不可用，请稍后重试", ex);
        }
    }

    private AgentTaskFlowRequestDto request(List<AssessmentScoreResult> results,
                                            UserProfileSnapshot snapshot) throws Exception {
        Map<String, Object> question = new LinkedHashMap<String, Object>();
        question.put("mode", "DEEP_PROFILE");
        question.put("assessmentResults", assessmentPayload(results));
        question.put("profileFacts", profileFacts(snapshot));
        question.put("instructions", "仅基于 assessmentResults 和 profileFacts 的明确内容进行分析。"
                + "不得把测评倾向写成确定能力、经历或职业结论。每一项结论必须在 evidence 中给出 basis 和 HIGH/MEDIUM/LOW 可信度。"
                + "profileFacts.selfProfileSupplement 是用户主动补充的真实事实，必须优先参考；不得把其中已经明确提供的信息再次列入 dataGaps 或重复建议用户补充。"
                + "不得编造经历、技术、分数、岗位、偏好或测试结果。只返回系统约定的 JSON 对象。");
        AgentTaskFlowRequestDto request = new AgentTaskFlowRequestDto();
        request.putInput("question", mapper.writeValueAsString(question));
        return request;
    }

    private String unavailableMessage(AgentTaskFlowResponseDto response) {
        String detail = response == null ? null : response.getErrorMessage();
        if (detail == null || detail.trim().length() == 0) return "深度画像 AI 暂时不可用，请稍后重试";
        return "深度画像 AI 不可用：" + detail;
    }

    private List<Map<String, Object>> assessmentPayload(List<AssessmentScoreResult> results) {
        List<Map<String, Object>> payload = new ArrayList<Map<String, Object>>();
        if (results == null) return payload;
        for (AssessmentScoreResult result : results) {
            if (result == null || !"COMPLETED".equalsIgnoreCase(result.getStatus())) continue;
            Map<String, Object> item = new LinkedHashMap<String, Object>();
            item.put("scaleTitle", result.getScaleTitle());
            item.put("resultSummary", result.getResultSummary());
            item.put("dimensionCounts", result.getDimensionCounts());
            item.put("suggestedRoles", result.getSuggestedRoles());
            item.put("answers", result.getAnswers());
            payload.add(item);
        }
        return payload;
    }

    private Map<String, Object> profileFacts(UserProfileSnapshot snapshot) {
        Map<String, Object> facts = new LinkedHashMap<String, Object>();
        if (snapshot == null) return facts;
        if (snapshot.getPreferences() != null) facts.put("targetRole", snapshot.getPreferences().getTargetRole());
        if (snapshot.getOnboarding() != null) {
            facts.put("identityType", snapshot.getOnboarding().getIdentityType());
            facts.put("stage", snapshot.getOnboarding().getStage());
            facts.put("experience", snapshot.getOnboarding().getExperience());
            facts.put("selfProfileSupplement", snapshot.getOnboarding().getSelfProfileSupplement());
            facts.put("weeklyAvailability", snapshot.getOnboarding().getWeeklyAvailability());
            if (snapshot.getOnboarding().getEducation() != null) {
                facts.put("school", snapshot.getOnboarding().getEducation().getSchool());
                facts.put("major", snapshot.getOnboarding().getEducation().getMajor());
                facts.put("degree", snapshot.getOnboarding().getEducation().getDegree());
            }
        }
        return facts;
    }

    private UserProfileSnapshot.AiDeepProfileBlock parse(String answer) throws Exception {
        String json = jsonObject(answer);
        JsonNode root = mapper.readTree(json);
        if (root == null || !root.isObject() || text(root, "profileSummary") == null) {
            throw new IllegalStateException("深度画像 AI 返回格式无效，请重试");
        }
        UserProfileSnapshot.AiDeepProfileBlock block = new UserProfileSnapshot.AiDeepProfileBlock();
        block.setProfileSummary(text(root, "profileSummary"));
        block.setProfileTags(strings(root.path("profileTags")));
        block.setStrengths(strings(root.path("strengths")));
        JsonNode preferences = root.path("workPreferences");
        block.setCollaborationStyle(text(preferences, "collaborationStyle"));
        block.setWorkEnvironment(text(preferences, "workEnvironment"));
        block.setDecisionStyle(text(preferences, "decisionStyle"));
        block.setMotivation(strings(preferences.path("motivation")));
        block.setStudyPreferences(strings(root.path("studyPreferences")));
        block.setCareerInclinations(strings(root.path("careerInclinations")));
        block.setDevelopmentSuggestions(strings(root.path("developmentSuggestions")));
        block.setEvidence(evidence(root.path("evidence")));
        block.setDataGaps(strings(root.path("dataGaps")));
        block.setSource(SOURCE_AI_ASSESSMENT);
        block.setGeneratedAt(LocalDateTime.now());
        return block;
    }

    private List<UserProfileSnapshot.AiProfileEvidence> evidence(JsonNode node) {
        List<UserProfileSnapshot.AiProfileEvidence> values = new ArrayList<UserProfileSnapshot.AiProfileEvidence>();
        if (!node.isArray()) return values;
        for (JsonNode item : node) {
            if (!item.isObject() || text(item, "conclusion") == null || text(item, "basis") == null) continue;
            UserProfileSnapshot.AiProfileEvidence evidence = new UserProfileSnapshot.AiProfileEvidence();
            evidence.setConclusion(text(item, "conclusion"));
            evidence.setBasis(text(item, "basis"));
            String confidence = text(item, "confidence");
            evidence.setConfidence("HIGH".equals(confidence) || "MEDIUM".equals(confidence) || "LOW".equals(confidence)
                    ? confidence : "LOW");
            values.add(evidence);
        }
        return values;
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

    private String text(JsonNode node, String field) {
        if (node == null || node.isMissingNode() || node.isNull()) return null;
        String value = node.path(field).asText("").trim();
        return value.length() == 0 ? null : value;
    }

    private String jsonObject(String answer) {
        if (answer == null) return null;
        int start = answer.indexOf('{');
        int end = answer.lastIndexOf('}');
        return start >= 0 && end > start ? answer.substring(start, end + 1) : answer;
    }
}
