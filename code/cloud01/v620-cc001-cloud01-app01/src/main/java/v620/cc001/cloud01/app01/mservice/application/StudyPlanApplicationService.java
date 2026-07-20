package v620.cc001.cloud01.app01.mservice.application;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import v620.base.helper.career.StudyPlanSummaryService;
import v620.cc001.base.common.dto.career.CareerDailyTaskDto;
import v620.cc001.base.common.dto.career.CareerDailyPlanDto;
import v620.cc001.base.common.dto.career.CareerDailyTaskUpdateRequest;
import v620.cc001.base.common.dto.career.CareerPlanPhaseDto;
import v620.cc001.base.common.dto.career.CareerPlanRecordDto;
import v620.cc001.base.common.dto.career.CareerPlanSummaryDto;
import v620.cc001.base.common.dto.career.CareerRouteContext;
import v620.cc001.base.common.dto.career.CareerUserProfileDto;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;
import v620.cc001.base.common.dto.furtherstudy.StudyCenterSelectionDto;
import v620.cc001.base.common.dto.furtherstudy.StudyPlanningMaterialDto;
import v620.cc001.base.common.dto.career.FileConstants;
import java.util.ArrayList;
import v620.cc001.cloud01.app01.mservice.ai.AgentPlatformTaskFlowClient;
import v620.cc001.cloud01.app01.mservice.ai.AgentPlatformTaskFlowConfig;
import v620.cc001.cloud01.app01.mservice.ai.StudyPlanAiGenerator;
import v620.cc001.cloud01.app01.mservice.ai.impl.AgentPlatformStudyPlanGenerator;
import v620.cc001.cloud01.app01.mservice.ai.impl.DefaultAgentPlatformTaskFlowClient;
import v620.cc001.cloud01.app01.mservice.ai.impl.KingdeeAgentSdkTaskFlowClient;
import v620.cc001.cloud01.app01.mservice.ai.PostgraduateRouteCoverage;
import v620.cc001.cloud01.app01.mservice.storage.CyanCruiseStorageFactory;
import v620.cc001.cloud01.app01.mservice.storage.StudyCenterStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.StudyDailyTaskStorageAdapter;
import v620.cc001.cloud01.app01.mservice.storage.impl.StudyPlanStorageAdapter;

/** Independent study planning boundary. It never reads or writes the employment plan slot. */
public class StudyPlanApplicationService {
    private static final Logger LOGGER = Logger.getLogger(StudyPlanApplicationService.class.getName());
    private static final int COMPLETE_ROUTE_PHASE_COUNT = 3;

    private final StudyCenterStorage storage;
    private final CareerProfileApplicationService profileService;
    private final StudyPlanSummaryService summaryService;
    private final StudyPlanAiGenerator aiGenerator;
    private final Clock clock;

    public StudyPlanApplicationService() {
        this(CyanCruiseStorageFactory.studyCenterStorage(), new CareerProfileApplicationService(),
                new StudyPlanSummaryService(), defaultAiGenerator(), Clock.systemDefaultZone());
    }

    public StudyPlanApplicationService(StudyCenterStorage storage,
                                       CareerProfileApplicationService profileService,
                                       StudyPlanSummaryService summaryService,
                                       StudyPlanAiGenerator aiGenerator,
                                       Clock clock) {
        this.storage = storage;
        this.profileService = profileService;
        this.summaryService = summaryService;
        this.aiGenerator = aiGenerator;
        this.clock = clock;
    }

    public CareerPlanSummaryDto getSummary(String userId) {
        String safeUserId = requireUserId(userId);
        StudyCenterSelectionDto selection = selection(safeUserId);
        CareerPlanRecordDto plan = loadVerifiedPlan(safeUserId, selection);
        CareerPlanSummaryDto result = summaryService.summarize(plan, LocalDateTime.now());
        result.setRouteType(CareerRouteContext.STUDY);
        result.setStudyDirection(selection == null ? null : selection.getDirection());
        result.setTargetSchool(resolveTargetSchool(safeUserId, selection));
        if (!hasDirection(selection)) {
            result.setPlanHealth("DIRECTION_REQUIRED");
            result.setAdjustmentReason("请先在升学中心选择考研、保研或留学方向。");
        } else if (plan != null && !selection.getDirection().equals(plan.getStudyDirection())) {
            result.setPlanHealth("NEEDS_REFRESH");
            result.setAdjustmentReason("升学方向已切换，请生成新方向的规划；原规划在生成成功前继续保留。");
        }
        return result;
    }

    public CareerPlanSummaryDto ensurePlan(String userId) {
        String safeUserId = requireUserId(userId);
        requireSelection(safeUserId);
        return getSummary(safeUserId);
    }

    public CareerPlanSummaryDto generateAgentPlan(String userId) {
        return generateAgentPlan(userId, null);
    }

    public CareerPlanSummaryDto generateAgentPlan(String userId, List<String> startedPhaseIds) {
        String safeUserId = requireUserId(userId);
        StudyCenterSelectionDto selection = requireSelection(safeUserId);
        if (aiGenerator == null) throw new IllegalStateException("当前升学方向的智能规划服务尚未配置，请稍后重试。");
        CareerPlanRecordDto existing = loadVerifiedPlan(safeUserId, selection);
        boolean sameDirection = existing != null && selection.getDirection().equals(existing.getStudyDirection());
        Set<String> protectedPhaseIds = sameDirection
                ? protectedPhaseIds(safeUserId, selection.getDirection(), existing, startedPhaseIds)
                : new HashSet<String>();
        if (sameDirection && hasCompleteRoute(existing) && !hasRefreshablePhase(existing, protectedPhaseIds)) {
            throw new IllegalStateException("当前升学路线图的所有阶段都已开始或完成，不能重新生成。");
        }
        CareerUserProfileDto profile = profileService.getProfile(safeUserId);
        UserProfileSnapshot snapshot = profileService.getSnapshot(safeUserId);
        LocalDateTime now = LocalDateTime.now();
        CareerPlanRecordDto generated;
        try {
            List<StudyPlanningMaterialDto> materials = usableMaterials(safeUserId, selection.getDirection());
            generated = aiGenerator.generate(safeUserId, selection.getDirection(),
                    resolveTargetSchool(safeUserId, selection), profile, snapshot, existing, materials);
        } catch (RuntimeException ex) {
            LOGGER.log(Level.WARNING, "Study plan agent generation failed; direction="
                    + selection.getDirection() + ", hasExistingPlan=" + (existing != null)
                    + ", errorType=" + ex.getClass().getSimpleName()
                    + ", message=" + safeMessage(ex.getMessage()), ex);
            throw new IllegalStateException("升学规划智能服务暂不可用，原有升学路线图已保留，请稍后重试。");
        }
        if (generated == null) throw new IllegalStateException("升学规划智能服务未返回有效路线图，原有规划已保留。");
        normalizeGeneratedPhaseStatuses(generated);
        generated.setUserId(safeUserId);
        generated.setStudyDirection(selection.getDirection());
        generated.setTargetSchool(resolveTargetSchool(safeUserId, selection));
        generated.setGeneratedAt(now);
        generated.setLastUpdatedAt(now);
        generated.setPlanningMode("AGENT");
        generated.setAgentStatus("AGENT_GENERATED");
        generated.setVersion(Integer.valueOf(existing == null || existing.getVersion() == null
                ? 1 : existing.getVersion().intValue() + 1));
        summaryService.enrich(generated, selection.getDirection(), generated.getTargetSchool(), profile, now);
        if (sameDirection && hasProtectedPhase(existing, protectedPhaseIds)) {
            mergeProtectedPhases(existing, generated, protectedPhaseIds);
            generated.setGeneratedAt(existing.getGeneratedAt() == null ? now : existing.getGeneratedAt());
            generated.setVersion(existing.getVersion() == null ? Integer.valueOf(1) : existing.getVersion());
        }
        storage.savePlan(safeUserId, selection.getDirection(), generated);
        return summaryService.summarize(generated, now);
    }

    public CareerDailyPlanDto getToday(String userId) {
        String safeUserId = requireUserId(userId);
        StudyCenterSelectionDto selection = requireSelection(safeUserId);
        loadVerifiedPlan(safeUserId, selection);
        return dailyService(selection.getDirection()).getToday(safeUserId);
    }

    public CareerDailyPlanDto updateToday(String userId, CareerDailyTaskUpdateRequest request) {
        String safeUserId = requireUserId(userId);
        StudyCenterSelectionDto selection = requireSelection(safeUserId);
        loadVerifiedPlan(safeUserId, selection);
        return dailyService(selection.getDirection()).update(safeUserId, request);
    }

    private StudyCenterSelectionDto selection(String userId) { return storage.loadSelection(userId); }
    private List<StudyPlanningMaterialDto> usableMaterials(String userId, String direction) {
        List<StudyPlanningMaterialDto> usable = new ArrayList<StudyPlanningMaterialDto>();
        if (!CareerRouteContext.isStudyDirection(direction)) return usable;
        List<StudyPlanningMaterialDto> stored = storage.listMaterials(userId, direction);
        if (stored == null) return usable;
        for (StudyPlanningMaterialDto material : stored) {
            if (material != null && FileConstants.STATUS_OK.equals(material.getExtractionStatus())
                    && hasText(material.getExtractedText())) {
                usable.add(material);
            }
        }
        return usable;
    }
    private StudyCenterSelectionDto requireSelection(String userId) {
        StudyCenterSelectionDto value = selection(userId);
        if (!hasDirection(value)) throw new IllegalArgumentException("请先在升学中心选择考研、保研或留学方向。");
        return value;
    }
    private boolean hasDirection(StudyCenterSelectionDto selection) {
        return selection != null && CareerRouteContext.isStudyDirection(selection.getDirection());
    }
    private boolean isRuleFallback(CareerPlanRecordDto plan) {
        if (plan == null) return false;
        return "RULE_FALLBACK".equalsIgnoreCase(text(plan.getPlanningMode()))
                || "FALLBACK_READY".equalsIgnoreCase(text(plan.getAgentStatus()))
                || "study-rule-fallback".equalsIgnoreCase(text(plan.getModelUsed()));
    }
    private CareerPlanRecordDto loadVerifiedPlan(String userId, StudyCenterSelectionDto selection) {
        String direction = selection == null ? null : selection.getDirection();
        if (!CareerRouteContext.isStudyDirection(direction)) return null;
        CareerPlanRecordDto plan = storage.loadPlan(userId, direction);
        if (isInvalidHistoricalPlan(plan, selection)) {
            storage.deleteDailyTasks(userId, direction);
            storage.deletePlan(userId, direction);
            return null;
        }
        return plan;
    }
    private boolean isInvalidHistoricalPlan(CareerPlanRecordDto plan, StudyCenterSelectionDto selection) {
        if (plan == null) return false;
        if (isRuleFallback(plan)) return true;
        boolean postgraduatePlan = CareerRouteContext.POSTGRADUATE.equals(plan.getStudyDirection());
        boolean recommendationPlan = CareerRouteContext.RECOMMENDATION.equals(plan.getStudyDirection());
        boolean studyAbroadPlan = CareerRouteContext.STUDY_ABROAD.equals(plan.getStudyDirection());
        boolean untypedPostgraduatePlan = !hasText(plan.getStudyDirection()) && selection != null
                && CareerRouteContext.POSTGRADUATE.equals(selection.getDirection());
        if (!postgraduatePlan && !recommendationPlan && !studyAbroadPlan
                && !untypedPostgraduatePlan) return false;
        return !hasCompleteRoute(plan)
                || !PostgraduateRouteCoverage.coversTwelveContinuousMonths(plan.getPhases())
                || !"AGENT".equalsIgnoreCase(text(plan.getPlanningMode()))
                || !"AGENT_GENERATED".equalsIgnoreCase(text(plan.getAgentStatus()));
    }
    private String text(String value) { return value == null ? "" : value.trim(); }
    private String resolveTargetSchool(String userId, StudyCenterSelectionDto selection) {
        UserProfileSnapshot snapshot = profileService.getSnapshot(userId);
        UserProfileSnapshot.OnboardingBlock onboarding = snapshot == null ? null : snapshot.getOnboarding();
        return onboarding != null && hasText(onboarding.getTargetSchool()) ? onboarding.getTargetSchool().trim() : null;
    }
    private void mergeProtectedPhases(CareerPlanRecordDto existing, CareerPlanRecordDto generated,
                                      Set<String> protectedPhaseIds) {
        List<CareerPlanPhaseDto> current = existing.getPhases();
        List<CareerPlanPhaseDto> incoming = generated.getPhases();
        for (int i = 0; current != null && i < current.size(); i += 1) {
            CareerPlanPhaseDto phase = current.get(i);
            if (isProtected(phase, protectedPhaseIds)) {
                if (i < incoming.size()) incoming.set(i, phase); else incoming.add(phase);
            }
        }
        if (hasStartedButIncompletePhase(existing, protectedPhaseIds)) {
            generated.setWeeklyPlan(existing.getWeeklyPlan());
            generated.setWeeklyFocus(existing.getWeeklyFocus());
            generated.setDailySuggestions(existing.getDailySuggestions());
        }
    }
    private boolean hasPhases(CareerPlanRecordDto plan) { return plan != null && plan.getPhases() != null && !plan.getPhases().isEmpty(); }
    private boolean hasCompleteRoute(CareerPlanRecordDto plan) {
        return hasPhases(plan) && plan.getPhases().size() >= COMPLETE_ROUTE_PHASE_COUNT;
    }
    private boolean hasProtectedPhase(CareerPlanRecordDto plan, Set<String> protectedPhaseIds) {
        if (!hasPhases(plan)) return false;
        for (CareerPlanPhaseDto phase : plan.getPhases()) {
            if (isProtected(phase, protectedPhaseIds)) return true;
        }
        return false;
    }
    private boolean hasRefreshablePhase(CareerPlanRecordDto plan, Set<String> protectedPhaseIds) {
        if (!hasPhases(plan)) return true;
        for (CareerPlanPhaseDto phase : plan.getPhases()) {
            if (!isProtected(phase, protectedPhaseIds)) return true;
        }
        return false;
    }
    private boolean hasStartedButIncompletePhase(CareerPlanRecordDto plan, Set<String> protectedPhaseIds) {
        if (!hasPhases(plan)) return false;
        for (CareerPlanPhaseDto phase : plan.getPhases()) {
            if (protectedPhaseIds.contains(phaseId(phase)) && !isCompletedStatus(phase)) return true;
        }
        return false;
    }
    private boolean isProtected(CareerPlanPhaseDto phase, Set<String> protectedPhaseIds) {
        return isCompletedStatus(phase) || protectedPhaseIds.contains(phaseId(phase));
    }
    private boolean isCompletedStatus(CareerPlanPhaseDto phase) {
        String value = status(phase);
        return "COMPLETED".equals(value) || "DONE".equals(value) || "FINISHED".equals(value);
    }
    private Set<String> protectedPhaseIds(String userId, String direction, CareerPlanRecordDto plan,
                                          List<String> requestedPhaseIds) {
        Set<String> valid = new HashSet<String>();
        if (hasPhases(plan)) {
            for (CareerPlanPhaseDto phase : plan.getPhases()) valid.add(phaseId(phase));
        }
        Set<String> result = new HashSet<String>();
        if (requestedPhaseIds != null) {
            for (String phaseId : requestedPhaseIds) {
                String normalized = text(phaseId);
                if (valid.contains(normalized)) result.add(normalized);
            }
        }
        List<CareerDailyTaskDto> tasks = storage.listDailyTasks(userId, direction);
        if (tasks != null) {
            for (CareerDailyTaskDto task : tasks) {
                if (task != null && task.isCompleted() && samePlanVersion(plan, task)
                        && valid.contains(text(task.getPhaseId()))) {
                    result.add(text(task.getPhaseId()));
                }
            }
        }
        return result;
    }
    private boolean samePlanVersion(CareerPlanRecordDto plan, CareerDailyTaskDto task) {
        return plan == null || plan.getVersion() == null || task.getPlanVersion() == null
                || plan.getVersion().intValue() == task.getPlanVersion().intValue();
    }
    private void normalizeGeneratedPhaseStatuses(CareerPlanRecordDto generated) {
        if (!hasPhases(generated)) return;
        for (CareerPlanPhaseDto phase : generated.getPhases()) {
            if (phase != null) phase.setStatus("NOT_STARTED");
        }
    }
    private String phaseId(CareerPlanPhaseDto phase) {
        return phase == null ? "" : text(phase.getPhaseId());
    }
    private String status(CareerPlanPhaseDto phase) {
        return phase == null || !hasText(phase.getStatus()) ? "" : phase.getStatus().trim().toUpperCase().replace('-', '_').replace(' ', '_');
    }
    private String requireUserId(String userId) {
        if (!hasText(userId)) throw new IllegalArgumentException("userId is required");
        return userId.trim();
    }
    private boolean hasText(String value) { return value != null && value.trim().length() > 0; }
    private CareerDailyPlanApplicationService dailyService(String direction) {
        return new CareerDailyPlanApplicationService(new StudyPlanStorageAdapter(storage, direction),
                new StudyDailyTaskStorageAdapter(storage, direction), clock, CareerRouteContext.STUDY);
    }
    private String safeMessage(String value) {
        if (!hasText(value)) return "none";
        String sanitized = value.replace('\r', ' ').replace('\n', ' ');
        return sanitized.length() > 200 ? sanitized.substring(0, 200) : sanitized;
    }

    private static StudyPlanAiGenerator defaultAiGenerator() {
        Map<String, AgentPlatformTaskFlowConfig> configs = new LinkedHashMap<String, AgentPlatformTaskFlowConfig>();
        configs.put(CareerRouteContext.POSTGRADUATE, loadStudyAgentConfig(
                AgentPlatformStudyPlanGenerator.POSTGRADUATE_PREFIX));
        configs.put(CareerRouteContext.RECOMMENDATION, loadStudyAgentConfig(
                AgentPlatformStudyPlanGenerator.RECOMMENDATION_PREFIX));
        configs.put(CareerRouteContext.STUDY_ABROAD, loadStudyAgentConfig(
                AgentPlatformStudyPlanGenerator.STUDY_ABROAD_PREFIX));
        Map<String, AgentPlatformTaskFlowClient> clients = new LinkedHashMap<String, AgentPlatformTaskFlowClient>();
        for (Map.Entry<String, AgentPlatformTaskFlowConfig> entry : configs.entrySet()) {
            AgentPlatformTaskFlowConfig config = entry.getValue();
            if (config.isAgentSdkAvailable()) clients.put(entry.getKey(), new KingdeeAgentSdkTaskFlowClient(config));
            else if (config.isAvailable()) clients.put(entry.getKey(), new DefaultAgentPlatformTaskFlowClient(config));
        }
        return clients.isEmpty() ? null : new AgentPlatformStudyPlanGenerator(clients, configs);
    }

    /**
     * Prefer the published task flow when a runtime taskFlowCode is present.
     * Direct flow execution preserves its structured END_OUTPUT; agentNumber
     * remains a compatibility fallback for tenants that only publish an Agent.
     */
    static AgentPlatformTaskFlowConfig loadStudyAgentConfig(String prefix) {
        AgentPlatformTaskFlowConfig config = AgentPlatformTaskFlowConfig.fromSystemProperties(prefix);
        config.setJsonEncodeAgentQuery(true);
        return config;
    }
}
