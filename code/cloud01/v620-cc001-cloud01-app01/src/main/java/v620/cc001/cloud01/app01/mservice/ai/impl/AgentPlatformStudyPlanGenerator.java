package v620.cc001.cloud01.app01.mservice.ai.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import v620.cc001.base.common.dto.ai.AgentTaskFlowRequestDto;
import v620.cc001.base.common.dto.ai.AgentTaskFlowResponseDto;
import v620.cc001.base.common.dto.career.CareerPlanRecordDto;
import v620.cc001.base.common.dto.career.CareerRouteContext;
import v620.cc001.base.common.dto.career.CareerUserProfileDto;
import v620.cc001.base.common.dto.career.CareerPlanPhaseDto;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;
import v620.cc001.base.common.dto.furtherstudy.StudyPlanningMaterialDto;
import v620.cc001.cloud01.app01.mservice.ai.AgentPlatformTaskFlowClient;
import v620.cc001.cloud01.app01.mservice.ai.AgentPlatformTaskFlowConfig;
import v620.cc001.cloud01.app01.mservice.ai.PostgraduateRouteCoverage;
import v620.cc001.cloud01.app01.mservice.ai.StudyPlanAiGenerator;

/** Selects an independent Agent configuration for postgraduate, recommendation or overseas study. */
public class AgentPlatformStudyPlanGenerator implements StudyPlanAiGenerator {
    public static final String POSTGRADUATE_PREFIX = "cc001.agent.platform.study.postgraduate";
    public static final String RECOMMENDATION_PREFIX = "cc001.agent.platform.study.recommendation";
    public static final String STUDY_ABROAD_PREFIX = "cc001.agent.platform.study.abroad";
    static final int MAX_MATERIAL_CHARS = 40000;
    static final int MAX_SINGLE_MATERIAL_CHARS = 12000;
    static final int POSTGRADUATE_HORIZON_MONTHS = 12;
    static final int POSTGRADUATE_MIN_PHASES = 3;

    private final Map<String, AgentPlatformTaskFlowClient> clients;
    private final Map<String, AgentPlatformTaskFlowConfig> configs;
    private final ObjectMapper mapper;

    public AgentPlatformStudyPlanGenerator(Map<String, AgentPlatformTaskFlowClient> clients,
                                           Map<String, AgentPlatformTaskFlowConfig> configs) {
        this.clients = clients == null ? new LinkedHashMap<String, AgentPlatformTaskFlowClient>() : clients;
        this.configs = configs == null ? new LinkedHashMap<String, AgentPlatformTaskFlowConfig>() : configs;
        this.mapper = new ObjectMapper();
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public CareerPlanRecordDto generate(String userId, String direction, String targetSchool,
                                        CareerUserProfileDto profile) {
        return generate(userId, direction, targetSchool, profile, null,
                new ArrayList<StudyPlanningMaterialDto>());
    }

    @Override
    public CareerPlanRecordDto generate(String userId, String direction, String targetSchool,
                                        CareerUserProfileDto profile,
                                        CareerPlanRecordDto existingPlan,
                                        List<StudyPlanningMaterialDto> materials) {
        return generate(userId, direction, targetSchool, profile, null, existingPlan, materials);
    }

    @Override
    public CareerPlanRecordDto generate(String userId, String direction, String targetSchool,
                                        CareerUserProfileDto profile,
                                        UserProfileSnapshot snapshot,
                                        CareerPlanRecordDto existingPlan,
                                        List<StudyPlanningMaterialDto> materials) {
        String safeDirection = requireDirection(direction);
        AgentPlatformTaskFlowClient client = clients.get(safeDirection);
        AgentPlatformTaskFlowConfig config = configs.get(safeDirection);
        if (client == null || config == null) {
            throw new IllegalStateException("当前升学方向的智能规划服务尚未配置，请稍后重试。");
        }
        AgentTaskFlowRequestDto request = new AgentTaskFlowRequestDto();
        request.setTaskFlowCode(config.getTaskFlowCode());
        request.putInput("question", question(safeDirection, targetSchool, profile, snapshot,
                existingPlan, materials, false));
        AgentTaskFlowResponseDto response = client.execute(request);
        if (response == null || !response.isSuccess() || !hasText(response.getAnswer())) {
            throw new IllegalStateException("升学规划智能服务暂不可用，请稍后重试。");
        }
        CareerPlanRecordDto plan = parse(response.getAnswer());
        try {
            validate(plan, safeDirection);
        } catch (IllegalStateException firstFailure) {
            if (!CareerRouteContext.POSTGRADUATE.equals(safeDirection)) throw firstFailure;
            request.putInput("question", question(safeDirection, targetSchool, profile, snapshot,
                    existingPlan, materials, true));
            response = client.execute(request);
            if (response == null || !response.isSuccess() || !hasText(response.getAnswer())) {
                throw firstFailure;
            }
            plan = parse(response.getAnswer());
            validate(plan, safeDirection);
        }
        plan.setRouteType(CareerRouteContext.STUDY);
        plan.setStudyDirection(safeDirection);
        plan.setTargetSchool(targetSchool);
        plan.setPlanningMode("AGENT");
        plan.setAgentStatus("AGENT_GENERATED");
        plan.setModelUsed("kingdee-agent-sdk:" + safeDirection.toLowerCase());
        plan.setTokensConsumed(Integer.valueOf(0));
        return plan;
    }

    private String question(String direction, String targetSchool, CareerUserProfileDto profile,
                            UserProfileSnapshot snapshot,
                            CareerPlanRecordDto existingPlan,
                            List<StudyPlanningMaterialDto> materials,
                            boolean correctionRequired) {
        try {
            Map<String, Object> payload = new LinkedHashMap<String, Object>();
            payload.put("mode", "STUDY_PLANNING");
            payload.put("studyDirection", direction);
            payload.put("direction", flowDirection(direction));
            payload.put("currentDate", LocalDate.now().toString());
            payload.put("targetSchool", targetSchool);
            appendStudyProfile(payload, profile, snapshot);
            payload.put("profileSummary", profile);
            payload.put("profileEvidence", profile == null ? null : profile.getEvidence());
            payload.put("existingProgress", progress(existingPlan));
            payload.put("userMaterials", materialEvidence(materials));
            payload.put("planningHorizonMonths", Integer.valueOf(POSTGRADUATE_HORIZON_MONTHS));
            payload.put("minimumPhaseCount", Integer.valueOf(POSTGRADUATE_MIN_PHASES));
            payload.put("requiredPhaseHorizons", requiredPhaseHorizons());
            payload.put("fullYearRoadmapRequired", Boolean.TRUE);
            if (correctionRequired) {
                payload.put("regenerationReason",
                        "The previous result was incomplete. Return the complete 12-month roadmap, not only the current month.");
                payload.put("mustRegenerateAllPhases", Boolean.TRUE);
            }
            payload.put("materialUsageRule", "用户资料只作为当前用户的规划依据；若资料缺少明确日期或条件，不得补造具体招生事实。");
            payload.put("informationPolicy", "除目标院校外的信息均允许暂缺。信息不足时仍须生成可执行的暂行路线，"
                    + "将待确认内容写入 missingInfo，并把核验动作安排到第一阶段；不得拒绝生成。");
            payload.put("outputContract", "只输出一个 camelCase JSON 对象，必须包含 targetRole、"
                    + "startStateSummary、horizonYears、phases、weeklyPlan、dailySuggestions、weeklyFocus；"
                    + "startStateSummary 必须使用中文分号分隔 4 至 6 条事实，依次覆盖当前阶段、已有基础、"
                    + "准备差距、考研目标和待确认信息，不能写成一整段；"
                    + "不得输出 Markdown、解释文字或向用户提问。");
            payload.put("userQuestion", CareerRouteContext.POSTGRADUATE.equals(direction)
                    ? "请结合目标院校、用户画像和上传资料，生成从当前日期起未来一年的考研路线图、"
                    + "每周计划和每日行动。缺失项作为待核验任务，不得因此拒绝生成路线。"
                    : "请按所选升学方向生成未来一年的可执行路线图、每周计划和每日行动。");
            return mapper.writeValueAsString(payload);
        } catch (Exception ex) {
            throw new IllegalStateException("升学规划资料暂时无法整理，请稍后重试。", ex);
        }
    }

    private List<String> requiredPhaseHorizons() {
        List<String> horizons = new ArrayList<String>();
        horizons.add("months 1-2");
        horizons.add("months 3-6");
        horizons.add("months 7-12");
        return horizons;
    }

    private void appendStudyProfile(Map<String, Object> payload,
                                    CareerUserProfileDto profile,
                                    UserProfileSnapshot snapshot) {
        UserProfileSnapshot.OnboardingBlock onboarding = snapshot == null ? null : snapshot.getOnboarding();
        UserProfileSnapshot.EducationBlock education = onboarding == null ? null : onboarding.getEducation();
        payload.put("currentStage", firstText(onboarding == null ? null : onboarding.getStage(),
                profile == null ? null : profile.getCurrentStage()));
        payload.put("school", education == null ? null : education.getSchool());
        payload.put("major", education == null ? null : education.getMajor());
        payload.put("degree", education == null ? null : education.getDegree());
        payload.put("graduationYear", education == null ? null : education.getGraduationYear());
        payload.put("targetMajor", onboarding == null ? null : onboarding.getRouteGoal());
        payload.put("weeklyHours", onboarding == null ? null : onboarding.getWeeklyAvailability());
        payload.put("timeline", onboarding == null ? null : onboarding.getTimeline());
        payload.put("experience", onboarding == null ? null : onboarding.getExperience());
        payload.put("selfProfileSupplement",
                onboarding == null ? null : onboarding.getSelfProfileSupplement());
        payload.put("examDate", null);
        payload.put("subjects", new ArrayList<String>());
        payload.put("studyProfileEvidence", studyProfileEvidence(snapshot));
    }

    /**
     * Do not serialize the whole snapshot here. It contains LocalDateTime fields and the
     * Cosmic runtime ObjectMapper does not install the Java Time module. More importantly,
     * the planning flow only needs stable conclusions, not profile record timestamps.
     */
    private Map<String, Object> studyProfileEvidence(UserProfileSnapshot snapshot) {
        Map<String, Object> evidence = new LinkedHashMap<String, Object>();
        if (snapshot == null) return evidence;
        UserProfileSnapshot.AssessmentBlock assessment = snapshot.getAssessment();
        if (assessment != null) {
            evidence.put("assessmentSummary", assessment.getSummary());
            evidence.put("suggestedRoles", assessment.getSuggestedRoles());
        }
        UserProfileSnapshot.AiDeepProfileBlock deepProfile = snapshot.getAiDeepProfile();
        if (deepProfile != null) {
            evidence.put("profileSummary", deepProfile.getProfileSummary());
            evidence.put("strengths", deepProfile.getStrengths());
            evidence.put("studyPreferences", deepProfile.getStudyPreferences());
            evidence.put("developmentSuggestions", deepProfile.getDevelopmentSuggestions());
            evidence.put("dataGaps", deepProfile.getDataGaps());
        }
        UserProfileSnapshot.PreferencesBlock preferences = snapshot.getPreferences();
        if (preferences != null) {
            evidence.put("targetRolePreference", preferences.getTargetRole());
        }
        return evidence;
    }

    private String flowDirection(String direction) {
        return CareerRouteContext.POSTGRADUATE.equals(direction)
                ? "POSTGRADUATE_EXAM" : direction;
    }

    private String firstText(String first, String second) {
        return hasText(first) ? first.trim() : (hasText(second) ? second.trim() : null);
    }

    private CareerPlanRecordDto parse(String answer) {
        AgentPlatformPlanJsonParser.ParsedPlan parsed = AgentPlatformPlanJsonParser.parse(
                mapper, answer, "升学规划结果格式不完整，请稍后重试。");
        normalizeTaskFlowPlan(parsed.getRoot(), parsed.getPlan());
        return parsed.getPlan();
    }

    /**
     * The postgraduate task flow exposes a richer JSON contract than the shared route DTO.
     * Keep the shared persistence model, while adapting equivalent task-flow fields instead
     * of rejecting an otherwise usable AI plan.
     */
    private void normalizeTaskFlowPlan(JsonNode root, CareerPlanRecordDto plan) {
        if (root == null || plan == null) return;
        if (!hasText(plan.getTargetRole())) {
            plan.setTargetRole(text(root, "targetSummary"));
        }
        if (plan.getHorizonYears() == null) {
            int horizonMonths = integer(root, "horizonMonths");
            plan.setHorizonYears(Integer.valueOf(horizonMonths > 0
                    ? Math.max(1, (horizonMonths + 11) / 12) : 1));
        }
        if ((plan.getDailySuggestions() == null || plan.getDailySuggestions().isEmpty())
                && plan.getWeeklyPlan() != null) {
            List<String> suggestions = plan.getWeeklyPlan().getDailySuggestions();
            if (suggestions == null || suggestions.isEmpty()) {
                suggestions = plan.getWeeklyPlan().getActions();
            }
            plan.setDailySuggestions(copyTexts(suggestions));
        }
    }

    private String text(JsonNode root, String field) {
        JsonNode value = root.get(field);
        return value != null && value.isTextual() ? value.asText() : null;
    }

    private int integer(JsonNode root, String field) {
        JsonNode value = root.get(field);
        return value != null && value.canConvertToInt() ? value.asInt() : 0;
    }

    private List<String> copyTexts(List<String> values) {
        return values == null ? new ArrayList<String>() : new ArrayList<String>(values);
    }

    private void validate(CareerPlanRecordDto plan, String direction) {
        if (plan == null || !hasText(plan.getTargetRole()) || !hasText(plan.getStartStateSummary())
                || plan.getPhases() == null || plan.getPhases().isEmpty()
                || plan.getWeeklyPlan() == null || plan.getDailySuggestions() == null
                || plan.getDailySuggestions().isEmpty() || plan.getWeeklyFocus() == null
                || plan.getWeeklyFocus().isEmpty()) {
            throw new IllegalStateException("升学规划结果格式不完整，请稍后重试。");
        }
        for (CareerPlanPhaseDto phase : plan.getPhases()) {
            if (phase == null || !hasText(phase.getTitle()) || !hasText(phase.getHorizon())
                    || phase.getActions() == null || phase.getActions().isEmpty()
                    || phase.getKpis() == null || phase.getKpis().isEmpty()) {
                throw new IllegalStateException("升学规划结果格式不完整，请稍后重试。");
            }
        }
        if (CareerRouteContext.POSTGRADUATE.equals(direction)
                && (plan.getPhases().size() < POSTGRADUATE_MIN_PHASES
                || !coversFullYear(plan.getPhases()))) {
            throw new IllegalStateException(
                    "Postgraduate roadmap must cover all 12 months in at least three phases.");
        }
    }

    private boolean coversFullYear(List<CareerPlanPhaseDto> phases) {
        return PostgraduateRouteCoverage.coversTwelveContinuousMonths(phases);
    }

    private Map<String, Object> progress(CareerPlanRecordDto plan) {
        Map<String, Object> out = new LinkedHashMap<String, Object>();
        if (plan == null) {
            out.put("hasExistingPlan", Boolean.FALSE);
            return out;
        }
        out.put("hasExistingPlan", Boolean.TRUE);
        out.put("version", plan.getVersion());
        List<Map<String, Object>> phases = new ArrayList<Map<String, Object>>();
        if (plan.getPhases() != null) {
            for (CareerPlanPhaseDto phase : plan.getPhases()) {
                Map<String, Object> item = new LinkedHashMap<String, Object>();
                item.put("phaseId", phase == null ? null : phase.getPhaseId());
                item.put("title", phase == null ? null : phase.getTitle());
                item.put("status", phase == null ? null : phase.getStatus());
                phases.add(item);
            }
        }
        out.put("phases", phases);
        return out;
    }

    private List<Map<String, Object>> materialEvidence(List<StudyPlanningMaterialDto> materials) {
        List<Map<String, Object>> out = new ArrayList<Map<String, Object>>();
        int remaining = MAX_MATERIAL_CHARS;
        if (materials == null) return out;
        for (StudyPlanningMaterialDto material : materials) {
            if (material == null || remaining <= 0 || !hasText(material.getExtractedText())) continue;
            String text = material.getExtractedText().trim();
            int allowed = Math.min(remaining, Math.min(MAX_SINGLE_MATERIAL_CHARS, text.length()));
            Map<String, Object> item = new LinkedHashMap<String, Object>();
            item.put("materialId", material.getMaterialId());
            item.put("title", material.getTitle());
            item.put("materialType", material.getMaterialType());
            item.put("originalFilename", material.getOriginalFilename());
            item.put("content", text.substring(0, allowed));
            item.put("truncated", Boolean.valueOf(allowed < text.length()
                    || Boolean.TRUE.equals(material.getTruncated())));
            out.add(item);
            remaining -= allowed;
        }
        return out;
    }

    private String requireDirection(String value) {
        if (!CareerRouteContext.isStudyDirection(value)) throw new IllegalArgumentException("请先选择考研、保研或留学方向。");
        return value.trim();
    }
    private boolean hasText(String value) { return value != null && value.trim().length() > 0; }
}
