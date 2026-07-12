package v620.cc001.cloud01.app01.mservice.application;

import v620.cc001.cloud01.app01.mservice.storage.CareerPlanStorage;
import v620.cc001.cloud01.app01.mservice.storage.CareerProfileStorage;
import v620.cc001.cloud01.app01.mservice.storage.CyanCruiseStorageFactory;
import v620.base.helper.career.CareerProfileBuildService;
import v620.base.helper.career.CareerProfileSnapshotMergeService;
import v620.cc001.base.common.dto.career.CareerAgentRuleInput;
import v620.cc001.base.common.dto.career.CareerProfileDraftDto;
import v620.cc001.base.common.dto.career.CareerProfileInputsRequest;
import v620.cc001.base.common.dto.career.CareerProfileOnboardingRequest;
import v620.cc001.base.common.dto.career.CareerProfilePreferencesRequest;
import v620.cc001.base.common.dto.career.CareerUserProfileDto;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Application service for the migrated career profile and onboarding slice.
 */
public class CareerProfileApplicationService {

    private final CareerProfileStorage storage;
    private final CareerPlanStorage planStorage;
    private final CareerProfileSnapshotMergeService mergeService;
    private final CareerProfileBuildService buildService;

    public CareerProfileApplicationService() {
        this(CyanCruiseStorageFactory.profileStorage(), CyanCruiseStorageFactory.careerPlanStorage(),
                new CareerProfileSnapshotMergeService(), new CareerProfileBuildService());
    }

    public CareerProfileApplicationService(CareerProfileStorage storage,
                                           CareerProfileSnapshotMergeService mergeService,
                                           CareerProfileBuildService buildService) {
        this(storage, null, mergeService, buildService);
    }

    public CareerProfileApplicationService(CareerProfileStorage storage,
                                           CareerPlanStorage planStorage,
                                           CareerProfileSnapshotMergeService mergeService,
                                           CareerProfileBuildService buildService) {
        this.storage = storage;
        this.planStorage = planStorage;
        this.mergeService = mergeService;
        this.buildService = buildService;
    }

    public UserProfileSnapshot getSnapshot(String userId) {
        return mergeService.ensureSnapshot(storage.loadSnapshot(requireUserId(userId)));
    }

    public CareerProfileDraftDto getDraft(String userId) {
        return ensureDraft(storage.loadDraft(requireUserId(userId)));
    }

    public CareerProfileDraftDto saveDraft(String userId, CareerProfileDraftDto request) {
        String safeUserId = requireUserId(userId);
        CareerProfileDraftDto draft = mergeDraft(storage.loadDraft(safeUserId), request);
        draft.setUpdatedAt(LocalDateTime.now());
        storage.saveDraft(safeUserId, draft);
        return draft;
    }

    public CareerProfileDraftDto clearDraft(String userId) {
        String safeUserId = requireUserId(userId);
        storage.clearDraft(safeUserId);
        return new CareerProfileDraftDto();
    }

    public UserProfileSnapshot savePreferences(String userId, CareerProfilePreferencesRequest request) {
        String safeUserId = requireUserId(userId);
        UserProfileSnapshot snapshot = mergeService.mergePreferences(storage.loadSnapshot(safeUserId), request);
        storage.saveSnapshot(safeUserId, snapshot);
        refreshProfile(safeUserId);
        return snapshot;
    }

    public UserProfileSnapshot saveOnboarding(String userId, CareerProfileOnboardingRequest request) {
        String safeUserId = requireUserId(userId);
        UserProfileSnapshot snapshot = mergeService.mergeOnboarding(storage.loadSnapshot(safeUserId), request);
        storage.saveSnapshot(safeUserId, snapshot);
        refreshProfile(safeUserId);
        return snapshot;
    }

    public UserProfileSnapshot saveAssessment(String userId, UserProfileSnapshot.AssessmentBlock block) {
        String safeUserId = requireUserId(userId);
        UserProfileSnapshot snapshot = mergeService.mergeAssessment(storage.loadSnapshot(safeUserId), block);
        storage.saveSnapshot(safeUserId, snapshot);
        refreshProfile(safeUserId);
        return snapshot;
    }

    public UserProfileSnapshot saveResume(String userId, UserProfileSnapshot.ResumeBlock block) {
        String safeUserId = requireUserId(userId);
        UserProfileSnapshot snapshot = mergeService.mergeResume(storage.loadSnapshot(safeUserId), block);
        storage.saveSnapshot(safeUserId, snapshot);
        refreshProfile(safeUserId);
        return snapshot;
    }

    public UserProfileSnapshot clearResume(String userId) {
        String safeUserId = requireUserId(userId);
        UserProfileSnapshot snapshot = mergeService.clearResume(storage.loadSnapshot(safeUserId));
        storage.saveSnapshot(safeUserId, snapshot);
        refreshProfile(safeUserId);
        return snapshot;
    }

    public UserProfileSnapshot saveInterview(String userId, UserProfileSnapshot.InterviewBlock block) {
        String safeUserId = requireUserId(userId);
        UserProfileSnapshot snapshot = mergeService.mergeInterview(storage.loadSnapshot(safeUserId), block);
        storage.saveSnapshot(safeUserId, snapshot);
        refreshProfile(safeUserId);
        return snapshot;
    }

    public CareerUserProfileDto saveProfileInputs(String userId, CareerProfileInputsRequest request) {
        String safeUserId = requireUserId(userId);
        if (request != null) {
            saveFact(safeUserId, "target_city", request.getTargetCity());
            saveFact(safeUserId, "target_industry", request.getTargetIndustry());
            saveFact(safeUserId, "timeline", request.getTimeline());
            saveFact(safeUserId, "weekly_hours", request.getWeeklyHours());
            saveFact(safeUserId, "preferred_task_difficulty", request.getPreferredDifficulty());
            if (request.getConsiderGradSchool() != null) {
                saveFact(safeUserId, "consider_grad_school", request.getConsiderGradSchool().booleanValue() ? "true" : "false");
            }
            if (request.getConsiderStudyAbroad() != null) {
                saveFact(safeUserId, "consider_study_abroad", request.getConsiderStudyAbroad().booleanValue() ? "true" : "false");
            }
            saveFact(safeUserId, "career_goal_note", request.getCareerGoalNote());
        }
        return refreshProfile(safeUserId);
    }

    public CareerUserProfileDto getProfile(String userId) {
        String safeUserId = requireUserId(userId);
        CareerUserProfileDto profile = storage.loadProfile(safeUserId);
        return profile == null ? refreshProfile(safeUserId) : profile;
    }

    public CareerUserProfileDto refreshProfile(String userId) {
        String safeUserId = requireUserId(userId);
        UserProfileSnapshot snapshot = mergeService.ensureSnapshot(storage.loadSnapshot(safeUserId));
        Map<String, String> facts = storage.loadFacts(safeUserId);
        boolean hasPlan = planStorage != null && planStorage.exists(safeUserId);
        CareerUserProfileDto profile = buildService.build(snapshot, facts, new CareerAgentRuleInput.CheckInStatus(), hasPlan);
        storage.saveProfile(safeUserId, profile);
        return profile;
    }

    private void saveFact(String userId, String key, String value) {
        if (value != null && value.trim().length() > 0) {
            storage.saveFact(userId, key, value);
        }
    }

    private CareerProfileDraftDto mergeDraft(CareerProfileDraftDto existing, CareerProfileDraftDto request) {
        CareerProfileDraftDto draft = ensureDraft(existing);
        if (request == null) {
            return draft;
        }
        if (hasText(request.getIdentityType())) {
            draft.setIdentityType(request.getIdentityType().trim());
        }
        if (hasText(request.getEducationStage())) {
            draft.setEducationStage(request.getEducationStage().trim());
        }
        if (hasText(request.getSchool())) {
            draft.setSchool(request.getSchool().trim());
        }
        if (hasText(request.getMajor())) {
            draft.setMajor(request.getMajor().trim());
        }
        if (hasText(request.getSchoolMajor())) {
            draft.setSchoolMajor(request.getSchoolMajor().trim());
            if (!hasText(draft.getMajor())) {
                draft.setMajor(request.getSchoolMajor().trim());
            }
        }
        if (hasText(request.getResumeStatus())) {
            draft.setResumeStatus(request.getResumeStatus().trim());
        }
        if (hasText(request.getTargetRole())) {
            draft.setTargetRole(request.getTargetRole().trim());
        }
        if (hasText(request.getPreference())) {
            draft.setPreference(request.getPreference().trim());
        }
        if (hasText(request.getExperience())) {
            draft.setExperience(request.getExperience().trim());
        }
        if (hasText(request.getRouteIntent())) {
            draft.setRouteIntent(request.getRouteIntent().trim());
        }
        return draft;
    }

    private CareerProfileDraftDto ensureDraft(CareerProfileDraftDto draft) {
        return draft == null ? new CareerProfileDraftDto() : draft;
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }

    private String requireUserId(String userId) {
        if (userId == null || userId.trim().length() == 0) {
            throw new IllegalArgumentException("userId is required");
        }
        return userId.trim();
    }
}
