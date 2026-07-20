package v620.cc001.cloud01.app01.mservice.application;

import v620.cc001.cloud01.app01.mservice.ai.CareerPlanAiGenerator;
import v620.cc001.cloud01.app01.mservice.ai.AgentPlatformTaskFlowConfig;
import v620.cc001.cloud01.app01.mservice.ai.impl.AgentPlatformCareerPlanGenerator;
import v620.cc001.cloud01.app01.mservice.ai.impl.DefaultAgentPlatformTaskFlowClient;
import v620.cc001.cloud01.app01.mservice.ai.impl.KingdeeAgentSdkTaskFlowClient;
import v620.cc001.cloud01.app01.mservice.storage.CareerPlanStorage;
import v620.cc001.cloud01.app01.mservice.storage.CyanCruiseStorageFactory;
import v620.base.helper.career.CareerPlanSummaryService;
import v620.cc001.base.common.dto.career.CareerPlanRecordDto;
import v620.cc001.base.common.dto.career.CareerPlanPhaseDto;
import v620.cc001.base.common.dto.career.CareerPlanSaveRequest;
import v620.cc001.base.common.dto.career.CareerPlanSummaryDto;
import v620.cc001.base.common.dto.career.CareerUserProfileDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Application boundary for current career plan summary and explicit generation.
 */
public class CareerPlanApplicationService {

    private final CareerPlanStorage storage;
    private final CareerProfileApplicationService profileApplicationService;
    private final CareerPlanSummaryService summaryService;
    private final CareerPlanAiGenerator aiGenerator;

    public CareerPlanApplicationService() {
        this(CyanCruiseStorageFactory.careerPlanStorage(), new CareerProfileApplicationService(),
                new CareerPlanSummaryService(), defaultAiGenerator());
    }

    public CareerPlanApplicationService(CareerPlanStorage storage,
                                        CareerProfileApplicationService profileApplicationService,
                                        CareerPlanSummaryService summaryService) {
        this(storage, profileApplicationService, summaryService, null);
    }

    public CareerPlanApplicationService(CareerPlanStorage storage,
                                        CareerProfileApplicationService profileApplicationService,
                                        CareerPlanSummaryService summaryService,
                                        CareerPlanAiGenerator aiGenerator) {
        this.storage = storage;
        this.profileApplicationService = profileApplicationService;
        this.summaryService = summaryService;
        this.aiGenerator = aiGenerator;
    }

    public CareerPlanSummaryDto getSummary(String userId) {
        String safeUserId = requireUserId(userId);
        CareerUserProfileDto profile = resolveProfile(safeUserId);
        CareerPlanRecordDto existing = storage.load(safeUserId);
        if (existing == null || isLegacyFabricatedPlan(existing)) {
            return summarizeWithProfile(null, profile, LocalDateTime.now());
        }
        return summarizeWithProfile(existing, profile, LocalDateTime.now());
    }

    public CareerPlanSummaryDto ensurePlan(String userId) {
        String safeUserId = requireUserId(userId);
        CareerPlanRecordDto existing = storage.load(safeUserId);
        CareerUserProfileDto profile = resolveProfile(safeUserId);
        if (existing != null && !isLegacyFabricatedPlan(existing)) {
            return summarizeWithProfile(existing, profile, LocalDateTime.now());
        }
        return summarizeWithProfile(null, profile, LocalDateTime.now());
    }

    public CareerPlanSummaryDto savePlan(String userId, CareerPlanSaveRequest request) {
        String safeUserId = requireUserId(userId);
        CareerPlanRecordDto existing = storage.load(safeUserId);
        if (isLegacyFabricatedPlan(existing)) existing = null;
        LocalDateTime now = LocalDateTime.now();
        CareerPlanRecordDto plan = new CareerPlanRecordDto();
        plan.setUserId(safeUserId);
        CareerUserProfileDto profile = resolveProfile(safeUserId);
        plan.setTargetRole(firstText(request == null ? null : request.getTargetRole(), resolveTargetRole(profile)));
        plan.setStartStateSummary(request == null ? null : request.getStartStateSummary());
        plan.setPlanningMode(request == null ? null : request.getPlanningMode());
        plan.setHorizonYears(request == null ? null : request.getHorizonYears());
        plan.setAgentStatus(request == null ? null : request.getAgentStatus());
        plan.setPhases(request == null ? null : request.getPhases());
        plan.setWeeklyPlan(request == null ? null : request.getWeeklyPlan());
        plan.setDailySuggestions(request == null ? null : request.getDailySuggestions());
        plan.setMilestones(request == null ? null : request.getMilestones());
        plan.setWeeklyFocus(request == null ? null : request.getWeeklyFocus());
        plan.setModelUsed(firstText(request == null ? null : request.getModelUsed(), "manual"));
        plan.setTokensConsumed(Integer.valueOf(0));
        plan.setGeneratedAt(existing != null && existing.getGeneratedAt() != null ? existing.getGeneratedAt() : now);
        plan.setLastUpdatedAt(now);
        plan.setVersion(Integer.valueOf(existing == null || existing.getVersion() == null
                ? 1
                : existing.getVersion().intValue() + 1));
        storage.save(safeUserId, plan);
        return summarizeWithProfile(plan, profile, now);
    }

    public boolean hasPlan(String userId) {
        CareerPlanRecordDto plan = storage.load(requireUserId(userId));
        return plan != null && !isLegacyFabricatedPlan(plan);
    }

    private boolean isLegacyFabricatedPlan(CareerPlanRecordDto plan) {
        if (plan == null) return false;
        return "RULE_FALLBACK".equalsIgnoreCase(value(plan.getPlanningMode()))
                || "FALLBACK_READY".equalsIgnoreCase(value(plan.getAgentStatus()))
                || "study-rule-fallback".equalsIgnoreCase(value(plan.getModelUsed()));
    }

    private String value(String text) {
        return text == null ? "" : text.trim();
    }

    private CareerUserProfileDto resolveProfile(String userId) {
        return profileApplicationService.getProfile(userId);
    }

    private String resolveTargetRole(CareerUserProfileDto profile) {
        if (profile != null && profile.getTarget() != null && hasText(profile.getTarget().getRole())) {
            return profile.getTarget().getRole();
        }
        return null;
    }

    private String requireUserId(String userId) {
        if (!hasText(userId)) {
            throw new IllegalArgumentException("userId is required");
        }
        return userId.trim();
    }

    private String firstText(String first, String second) {
        return hasText(first) ? first.trim() : second;
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }

    private CareerPlanSummaryDto summarizeWithProfile(CareerPlanRecordDto plan, CareerUserProfileDto profile,
                                                       LocalDateTime now) {
        CareerPlanSummaryDto summary = summaryService.summarize(plan, now);
        if (profile == null) {
            summary.setMissingSignals(new ArrayList<CareerUserProfileDto.MissingSignal>());
            return summary;
        }
        summary.setProfileCompletenessScore(profile.getCompletenessScore());
        summary.setCurrentStage(profile.getCurrentStage());
        summary.setMissingSignals(profile.getMissingSignals() == null
                ? new ArrayList<CareerUserProfileDto.MissingSignal>()
                : profile.getMissingSignals());
        return summary;
    }

    /**
     * Explicit user-triggered Agent generation. Existing plans remain untouched on any failure.
     */
    public CareerPlanSummaryDto generateAgentPlan(String userId) {
        String safeUserId = requireUserId(userId);
        if (aiGenerator == null) {
            throw new IllegalStateException("就业规划智能服务尚未配置，请稍后重试。");
        }
        CareerPlanRecordDto existing = storage.load(safeUserId);
        if (hasPhases(existing) && !hasRefreshablePhase(existing)) {
            throw new IllegalStateException("当前路线图的所有阶段都已开始或完成，不能重新生成。请继续完成现有计划。");
        }
        CareerUserProfileDto profile = resolveProfile(safeUserId);
        LocalDateTime now = LocalDateTime.now();
        CareerPlanRecordDto generated;
        try {
            generated = aiGenerator.generate(safeUserId, resolveTargetRole(profile), profile);
        } catch (RuntimeException ex) {
            throw new IllegalStateException("就业规划智能服务暂不可用，原有路线图已保留，请稍后重试。");
        }
        if (generated == null) {
            throw new IllegalStateException("就业规划智能服务暂不可用，原有路线图已保留，请稍后重试。");
        }
        if (!hasPhases(generated)) {
            throw new IllegalStateException("就业规划智能服务未返回完整路线，原有路线图已保留，请稍后重试。");
        }
        generated.setUserId(safeUserId);
        generated.setTargetRole(firstText(generated.getTargetRole(), resolveTargetRole(profile)));
        generated.setAgentTraceId(resumeReferenceTrace(profile));
        generated.setLastUpdatedAt(now);
        if (hasProtectedPhase(existing)) {
            mergeProtectedPhases(existing, generated);
            generated.setGeneratedAt(existing.getGeneratedAt() == null ? now : existing.getGeneratedAt());
            generated.setVersion(existing.getVersion() == null ? Integer.valueOf(1) : existing.getVersion());
        } else {
            generated.setGeneratedAt(now);
            generated.setVersion(Integer.valueOf(existing == null || existing.getVersion() == null
                    ? 1 : existing.getVersion().intValue() + 1));
        }
        storage.save(safeUserId, generated);
        return summarizeWithProfile(generated, profile, now);
    }

    private void mergeProtectedPhases(CareerPlanRecordDto existing, CareerPlanRecordDto generated) {
        List<CareerPlanPhaseDto> current = existing.getPhases();
        List<CareerPlanPhaseDto> incoming = generated.getPhases();
        List<CareerPlanPhaseDto> merged = new ArrayList<CareerPlanPhaseDto>();
        int size = Math.max(current == null ? 0 : current.size(), incoming == null ? 0 : incoming.size());
        for (int index = 0; index < size; index++) {
            CareerPlanPhaseDto currentPhase = current != null && index < current.size() ? current.get(index) : null;
            CareerPlanPhaseDto incomingPhase = incoming != null && index < incoming.size() ? incoming.get(index) : null;
            if (isProtectedPhase(currentPhase)) {
                merged.add(currentPhase);
            } else if (incomingPhase != null) {
                merged.add(incomingPhase);
            }
        }
        generated.setPhases(merged);
        if (hasInProgressPhase(existing)) {
            generated.setWeeklyPlan(existing.getWeeklyPlan());
            generated.setDailySuggestions(existing.getDailySuggestions());
            generated.setWeeklyFocus(existing.getWeeklyFocus());
        }
    }

    private boolean hasPhases(CareerPlanRecordDto plan) {
        return plan != null && plan.getPhases() != null && !plan.getPhases().isEmpty();
    }

    private boolean hasProtectedPhase(CareerPlanRecordDto plan) {
        if (!hasPhases(plan)) return false;
        for (CareerPlanPhaseDto phase : plan.getPhases()) {
            if (isProtectedPhase(phase)) return true;
        }
        return false;
    }

    private boolean hasRefreshablePhase(CareerPlanRecordDto plan) {
        if (!hasPhases(plan)) return true;
        for (CareerPlanPhaseDto phase : plan.getPhases()) {
            if (!isProtectedPhase(phase)) return true;
        }
        return false;
    }

    private boolean hasInProgressPhase(CareerPlanRecordDto plan) {
        if (!hasPhases(plan)) return false;
        for (CareerPlanPhaseDto phase : plan.getPhases()) {
            if (isInProgressStatus(phase == null ? null : phase.getStatus())) return true;
        }
        return false;
    }

    private boolean isProtectedPhase(CareerPlanPhaseDto phase) {
        String status = phase == null ? null : phase.getStatus();
        return isInProgressStatus(status) || isCompletedStatus(status);
    }

    private boolean isInProgressStatus(String status) {
        if (!hasText(status)) return false;
        String normalized = normalizeStatus(status);
        return "IN_PROGRESS".equals(normalized) || "STARTED".equals(normalized)
                || "执行中".equals(status.trim()) || "进行中".equals(status.trim())
                || "已开始".equals(status.trim());
    }

    private boolean isCompletedStatus(String status) {
        if (!hasText(status)) return false;
        String normalized = normalizeStatus(status);
        return "COMPLETED".equals(normalized) || "DONE".equals(normalized)
                || "FINISHED".equals(normalized) || "已完成".equals(status.trim());
    }

    private String normalizeStatus(String status) {
        return status.trim().toUpperCase().replace('-', '_').replace(' ', '_');
    }

    private String resumeReferenceTrace(CareerUserProfileDto profile) {
        if (profile == null || profile.getEvidence() == null) {
            return null;
        }
        String resumeId = profile.getEvidence().get("selected_resume_id");
        return hasText(resumeId) ? "selected-resume:" + resumeId.trim() : null;
    }

    private static CareerPlanAiGenerator defaultAiGenerator() {
        AgentPlatformTaskFlowConfig config = AgentPlatformTaskFlowConfig.fromSystemProperties(
                AgentPlatformCareerPlanGenerator.CONFIG_PREFIX);
        if (config.isAgentSdkAvailable()) {
            return new AgentPlatformCareerPlanGenerator(new KingdeeAgentSdkTaskFlowClient(config), config);
        }
        if (config.isAvailable()) {
            return new AgentPlatformCareerPlanGenerator(new DefaultAgentPlatformTaskFlowClient(config), config);
        }
        return null;
    }
}
