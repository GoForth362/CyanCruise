package v620.cc001.cloud01.app01.mservice;

import v620.base.helper.career.CareerPlanSummaryService;
import v620.cc001.base.common.dto.career.CareerPlanRecordDto;
import v620.cc001.base.common.dto.career.CareerPlanSaveRequest;
import v620.cc001.base.common.dto.career.CareerPlanSummaryDto;
import v620.cc001.base.common.dto.career.CareerUserProfileDto;

import java.time.LocalDateTime;

/**
 * Application boundary for current career plan summary and default generation.
 */
public class CareerPlanApplicationService {

    private final CareerPlanStorage storage;
    private final CareerProfileApplicationService profileApplicationService;
    private final CareerPlanSummaryService summaryService;
    private final CareerPlanAiGenerator aiGenerator;

    public CareerPlanApplicationService() {
        this(CyanCruiseStorageFactory.careerPlanStorage(), new CareerProfileApplicationService(), new CareerPlanSummaryService());
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
        return summaryService.summarize(storage.load(requireUserId(userId)), LocalDateTime.now());
    }

    public CareerPlanSummaryDto ensurePlan(String userId) {
        String safeUserId = requireUserId(userId);
        CareerPlanRecordDto existing = storage.load(safeUserId);
        if (existing != null) {
            if (existing.getPhases() == null || existing.getPhases().isEmpty()) {
                CareerPlanRecordDto enriched = summaryService.enrichStructuredPlan(existing, resolveProfile(safeUserId), LocalDateTime.now());
                storage.save(safeUserId, enriched);
                return summaryService.summarize(enriched, LocalDateTime.now());
            }
            return summaryService.summarize(existing, LocalDateTime.now());
        }
        CareerUserProfileDto profile = resolveProfile(safeUserId);
        String targetRole = resolveTargetRole(profile);
        CareerPlanRecordDto created = createPlanWithGenerator(safeUserId, targetRole, profile, LocalDateTime.now());
        storage.save(safeUserId, created);
        return summaryService.summarize(created, LocalDateTime.now());
    }

    public CareerPlanSummaryDto savePlan(String userId, CareerPlanSaveRequest request) {
        String safeUserId = requireUserId(userId);
        CareerPlanRecordDto existing = storage.load(safeUserId);
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
        summaryService.enrichStructuredPlan(plan, profile, now);
        storage.save(safeUserId, plan);
        return summaryService.summarize(plan, now);
    }

    public boolean hasPlan(String userId) {
        return storage.exists(requireUserId(userId));
    }

    private CareerPlanRecordDto createPlanWithGenerator(String userId, String targetRole,
                                                        CareerUserProfileDto profile, LocalDateTime now) {
        if (aiGenerator != null) {
            try {
                CareerPlanRecordDto generated = aiGenerator.generate(userId, targetRole, profile);
                if (generated != null) {
                    generated.setUserId(userId);
                    generated.setTargetRole(firstText(generated.getTargetRole(), targetRole));
                    generated.setGeneratedAt(generated.getGeneratedAt() == null ? now : generated.getGeneratedAt());
                    generated.setLastUpdatedAt(generated.getLastUpdatedAt() == null ? now : generated.getLastUpdatedAt());
                    return summaryService.enrichStructuredPlan(generated, profile, now);
                }
            } catch (RuntimeException ignored) {
                // Fall back to deterministic rules until the external agent is stable.
            }
        }
        return summaryService.defaultPlan(userId, targetRole, profile, now);
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
}
