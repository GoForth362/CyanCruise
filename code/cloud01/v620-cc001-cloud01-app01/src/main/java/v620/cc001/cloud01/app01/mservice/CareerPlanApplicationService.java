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

    public CareerPlanApplicationService() {
        this(CyanCruiseStorageFactory.careerPlanStorage(), new CareerProfileApplicationService(), new CareerPlanSummaryService());
    }

    public CareerPlanApplicationService(CareerPlanStorage storage,
                                        CareerProfileApplicationService profileApplicationService,
                                        CareerPlanSummaryService summaryService) {
        this.storage = storage;
        this.profileApplicationService = profileApplicationService;
        this.summaryService = summaryService;
    }

    public CareerPlanSummaryDto getSummary(String userId) {
        return summaryService.summarize(storage.load(requireUserId(userId)), LocalDateTime.now());
    }

    public CareerPlanSummaryDto ensurePlan(String userId) {
        String safeUserId = requireUserId(userId);
        CareerPlanRecordDto existing = storage.load(safeUserId);
        if (existing != null) {
            return summaryService.summarize(existing, LocalDateTime.now());
        }
        CareerPlanRecordDto created = summaryService.defaultPlan(safeUserId, resolveTargetRole(safeUserId), LocalDateTime.now());
        storage.save(safeUserId, created);
        return summaryService.summarize(created, LocalDateTime.now());
    }

    public CareerPlanSummaryDto savePlan(String userId, CareerPlanSaveRequest request) {
        String safeUserId = requireUserId(userId);
        CareerPlanRecordDto existing = storage.load(safeUserId);
        LocalDateTime now = LocalDateTime.now();
        CareerPlanRecordDto plan = new CareerPlanRecordDto();
        plan.setUserId(safeUserId);
        plan.setTargetRole(firstText(request == null ? null : request.getTargetRole(), resolveTargetRole(safeUserId)));
        plan.setStartStateSummary(request == null ? null : request.getStartStateSummary());
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
        return summaryService.summarize(plan, now);
    }

    public boolean hasPlan(String userId) {
        return storage.exists(requireUserId(userId));
    }

    private String resolveTargetRole(String userId) {
        CareerUserProfileDto profile = profileApplicationService.getProfile(userId);
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
