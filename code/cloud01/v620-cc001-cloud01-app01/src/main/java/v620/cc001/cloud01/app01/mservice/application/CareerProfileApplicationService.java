package v620.cc001.cloud01.app01.mservice.application;

import v620.cc001.cloud01.app01.mservice.storage.CareerPlanStorage;
import v620.cc001.cloud01.app01.mservice.storage.CareerProfileStorage;
import v620.cc001.cloud01.app01.mservice.storage.CyanCruiseStorageFactory;
import v620.cc001.cloud01.app01.mservice.storage.ResumeStorage;
import v620.base.helper.career.CareerProfileBuildService;
import v620.base.helper.career.CareerProfileSnapshotMergeService;
import v620.cc001.base.common.dto.career.CareerAgentRuleInput;
import v620.cc001.base.common.dto.career.CareerProfileDraftDto;
import v620.cc001.base.common.dto.career.CareerProfileInputsRequest;
import v620.cc001.base.common.dto.career.CareerProfileOnboardingRequest;
import v620.cc001.base.common.dto.career.CareerProfilePreferencesRequest;
import v620.cc001.base.common.dto.career.CareerUserProfileDto;
import v620.cc001.base.common.dto.career.ResumeRecordDto;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Application service for the migrated career profile and onboarding slice.
 */
public class CareerProfileApplicationService {

    private final CareerProfileStorage storage;
    private final CareerPlanStorage planStorage;
    private final CareerProfileSnapshotMergeService mergeService;
    private final CareerProfileBuildService buildService;
    private final ResumeStorage resumeStorage;

    public CareerProfileApplicationService() {
        this(CyanCruiseStorageFactory.profileStorage(), CyanCruiseStorageFactory.careerPlanStorage(),
                new CareerProfileSnapshotMergeService(), new CareerProfileBuildService(),
                CyanCruiseStorageFactory.resumeStorage());
    }

    public CareerProfileApplicationService(CareerProfileStorage storage,
                                           CareerProfileSnapshotMergeService mergeService,
                                           CareerProfileBuildService buildService) {
        this(storage, null, mergeService, buildService, null);
    }

    public CareerProfileApplicationService(CareerProfileStorage storage,
                                           CareerPlanStorage planStorage,
                                           CareerProfileSnapshotMergeService mergeService,
                                           CareerProfileBuildService buildService) {
        this(storage, planStorage, mergeService, buildService, null);
    }

    public CareerProfileApplicationService(CareerProfileStorage storage,
                                           CareerPlanStorage planStorage,
                                           CareerProfileSnapshotMergeService mergeService,
                                           CareerProfileBuildService buildService,
                                           ResumeStorage resumeStorage) {
        this.storage = storage;
        this.planStorage = planStorage;
        this.mergeService = mergeService;
        this.buildService = buildService;
        this.resumeStorage = resumeStorage;
    }

    public UserProfileSnapshot getSnapshot(String userId) {
        String safeUserId = requireUserId(userId);
        UserProfileSnapshot snapshot = mergeService.ensureSnapshot(storage.loadSnapshot(safeUserId));
        if (normalizeLegacyRouteGoal(snapshot)) {
            storage.saveSnapshot(safeUserId, snapshot);
        }
        return snapshot;
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
        normalizeRequestedRouteGoal(request);
        validateSelectedResume(safeUserId, request);
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

    public UserProfileSnapshot saveAiDeepProfile(String userId, UserProfileSnapshot.AiDeepProfileBlock block) {
        String safeUserId = requireUserId(userId);
        UserProfileSnapshot snapshot = mergeService.ensureSnapshot(storage.loadSnapshot(safeUserId));
        ensureLatestDeepProfileInHistory(snapshot);
        snapshot = mergeService.mergeAiDeepProfile(snapshot, block);
        if (block != null) {
            normalizeDeepProfileRecord(block, snapshot.getUpdatedAt());
            List<UserProfileSnapshot.AiDeepProfileBlock> history = snapshot.getAiDeepProfileHistory();
            history = history == null
                    ? new ArrayList<UserProfileSnapshot.AiDeepProfileBlock>()
                    : new ArrayList<UserProfileSnapshot.AiDeepProfileBlock>(history);
            if (!containsDeepProfileRecord(history, block.getRecordId())) {
                history.add(0, block);
            }
            if (history.size() > 20) {
                history = new ArrayList<UserProfileSnapshot.AiDeepProfileBlock>(history.subList(0, 20));
            }
            snapshot.setAiDeepProfileHistory(history);
        }
        storage.saveSnapshot(safeUserId, snapshot);
        refreshProfile(safeUserId);
        return snapshot;
    }

    public List<UserProfileSnapshot.AiDeepProfileBlock> getAiDeepProfileHistory(String userId) {
        String safeUserId = requireUserId(userId);
        UserProfileSnapshot snapshot = mergeService.ensureSnapshot(storage.loadSnapshot(safeUserId));
        if (ensureLatestDeepProfileInHistory(snapshot)) {
            storage.saveSnapshot(safeUserId, snapshot);
        }
        List<UserProfileSnapshot.AiDeepProfileBlock> history = snapshot.getAiDeepProfileHistory();
        return history == null
                ? new ArrayList<UserProfileSnapshot.AiDeepProfileBlock>()
                : new ArrayList<UserProfileSnapshot.AiDeepProfileBlock>(history);
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
        appendSelectedResumeEvidence(safeUserId, snapshot, profile);
        storage.saveProfile(safeUserId, profile);
        return profile;
    }

    private void validateSelectedResume(String userId, CareerProfileOnboardingRequest request) {
        if (request == null || request.getSelectedResumeId() == null || resumeStorage == null) {
            return;
        }
        ResumeRecordDto resume = resumeStorage.load(request.getSelectedResumeId());
        if (resume == null || !userId.equals(resume.getUserId())) {
            throw new IllegalArgumentException("selected resume does not belong to current user");
        }
    }

    private void appendSelectedResumeEvidence(String userId, UserProfileSnapshot snapshot,
                                              CareerUserProfileDto profile) {
        if (resumeStorage == null || snapshot == null || snapshot.getOnboarding() == null
                || snapshot.getOnboarding().getSelectedResumeId() == null || profile == null) {
            return;
        }
        ResumeRecordDto resume = resumeStorage.load(snapshot.getOnboarding().getSelectedResumeId());
        if (resume == null || !userId.equals(resume.getUserId())) {
            return;
        }
        Map<String, String> evidence = profile.getEvidence();
        if (evidence == null) {
            evidence = new java.util.LinkedHashMap<String, String>();
            profile.setEvidence(evidence);
        }
        evidence.put("selected_resume_id", String.valueOf(resume.getResumeId()));
        putEvidence(evidence, "selected_resume_title", resume.getTitle(), 200);
        putEvidence(evidence, "selected_resume_target", resume.getTargetJob(), 300);
        putEvidence(evidence, "selected_resume_content", resume.getParsedContent(), 6000);
    }

    private void putEvidence(Map<String, String> evidence, String key, String value, int maxLength) {
        if (!hasText(value)) {
            return;
        }
        String normalized = value.trim();
        evidence.put(key, normalized.length() > maxLength ? normalized.substring(0, maxLength) : normalized);
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

    private void normalizeRequestedRouteGoal(CareerProfileOnboardingRequest request) {
        if (request == null || !hasText(request.getRouteGoal())) return;
        String routeGoal = request.getRouteGoal().trim().toLowerCase();
        if ("employment".equals(routeGoal) || "study".equals(routeGoal)) {
            request.setRouteGoal(routeGoal);
            return;
        }
        request.setRouteGoal(hasText(request.getTargetSchool()) ? "study" : "employment");
    }

    private boolean normalizeLegacyRouteGoal(UserProfileSnapshot snapshot) {
        UserProfileSnapshot.OnboardingBlock onboarding = snapshot == null ? null : snapshot.getOnboarding();
        if (onboarding == null || !"explore".equalsIgnoreCase(onboarding.getRouteGoal())) return false;
        onboarding.setRouteGoal(hasText(onboarding.getTargetSchool()) ? "study" : "employment");
        return true;
    }

    private boolean ensureLatestDeepProfileInHistory(UserProfileSnapshot snapshot) {
        UserProfileSnapshot.AiDeepProfileBlock latest = snapshot.getAiDeepProfile();
        if (latest == null) {
            return false;
        }
        boolean changed = normalizeDeepProfileRecord(latest, snapshot.getUpdatedAt());
        List<UserProfileSnapshot.AiDeepProfileBlock> history = snapshot.getAiDeepProfileHistory();
        history = history == null
                ? new ArrayList<UserProfileSnapshot.AiDeepProfileBlock>()
                : new ArrayList<UserProfileSnapshot.AiDeepProfileBlock>(history);
        if (!containsDeepProfileRecord(history, latest.getRecordId())) {
            history.add(0, latest);
            if (history.size() > 20) {
                history = new ArrayList<UserProfileSnapshot.AiDeepProfileBlock>(history.subList(0, 20));
            }
            snapshot.setAiDeepProfileHistory(history);
            changed = true;
        }
        return changed;
    }

    private boolean normalizeDeepProfileRecord(UserProfileSnapshot.AiDeepProfileBlock profile,
                                               LocalDateTime fallbackGeneratedAt) {
        boolean changed = false;
        if (!hasText(profile.getRecordId())) {
            profile.setRecordId(UUID.randomUUID().toString());
            changed = true;
        }
        if (profile.getGeneratedAt() == null) {
            profile.setGeneratedAt(fallbackGeneratedAt == null ? LocalDateTime.now() : fallbackGeneratedAt);
            changed = true;
        }
        return changed;
    }

    private boolean containsDeepProfileRecord(List<UserProfileSnapshot.AiDeepProfileBlock> history,
                                              String recordId) {
        if (!hasText(recordId) || history == null) {
            return false;
        }
        for (UserProfileSnapshot.AiDeepProfileBlock candidate : history) {
            if (candidate != null && recordId.equals(candidate.getRecordId())) {
                return true;
            }
        }
        return false;
    }

    private String requireUserId(String userId) {
        if (userId == null || userId.trim().length() == 0) {
            throw new IllegalArgumentException("userId is required");
        }
        return userId.trim();
    }
}
