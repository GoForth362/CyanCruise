package v620.cc001.cloud01.app01.mservice;

import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerPlanStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerProfileStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryResumeStorage;
import v620.cc001.cloud01.app01.mservice.application.CareerPlanApplicationService;
import v620.cc001.cloud01.app01.mservice.application.CareerProfileApplicationService;
import v620.cc001.cloud01.app01.mservice.storage.CareerPlanStorage;
import v620.cc001.cloud01.app01.mservice.storage.CareerProfileStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerPlanStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerProfileStorage;
import org.junit.jupiter.api.Test;
import v620.base.helper.career.CareerPlanSummaryService;
import v620.base.helper.career.CareerProfileBuildService;
import v620.base.helper.career.CareerProfileSnapshotMergeService;
import v620.cc001.base.common.dto.career.CareerPlanSaveRequest;
import v620.cc001.base.common.dto.career.CareerProfilePreferencesRequest;
import v620.cc001.base.common.dto.career.CareerProfileOnboardingRequest;
import v620.cc001.base.common.dto.career.CareerUserProfileDto;
import v620.cc001.base.common.dto.career.ResumeRecordDto;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CareerProfilePlanReadinessTest {

    @Test
    void profileUsesCareerPlanExistenceForReadiness() {
        CareerPlanStorage planStorage = new InMemoryCareerPlanStorage();
        CareerProfileApplicationService profileService = profileService(new InMemoryCareerProfileStorage(), planStorage);
        CareerProfilePreferencesRequest preferences = new CareerProfilePreferencesRequest();
        preferences.setTargetRole("Java Engineer");
        profileService.savePreferences("profile-plan-user", preferences);

        CareerUserProfileDto withoutPlan = profileService.refreshProfile("profile-plan-user");
        new CareerPlanApplicationService(planStorage, profileService, new CareerPlanSummaryService())
                .savePlan("profile-plan-user", saveRequest());
        CareerUserProfileDto withPlan = profileService.refreshProfile("profile-plan-user");

        assertEquals(Boolean.FALSE, withoutPlan.getReadiness().getHasPlan());
        assertTrue(hasMissingSignal(withoutPlan, "career_plan"));
        assertEquals(Boolean.TRUE, withPlan.getReadiness().getHasPlan());
        assertFalse(hasMissingSignal(withPlan, "career_plan"));
    }

    @Test
    void profileTreatsMissingPlanStorageAsNoPlan() {
        CareerProfileApplicationService profileService = profileService(new InMemoryCareerProfileStorage(), null);

        CareerUserProfileDto profile = profileService.refreshProfile("profile-no-storage-user");

        assertEquals(Boolean.FALSE, profile.getReadiness().getHasPlan());
        assertTrue(hasMissingSignal(profile, "career_plan"));
    }

    @Test
    void selectedResumeIsPersistedAndAddedToPlanningEvidence() {
        InMemoryResumeStorage resumeStorage = new InMemoryResumeStorage();
        ResumeRecordDto ownResume = resume("selected-resume-user", "前端求职简历", "前端开发工程师",
                "成都理工大学，软件工程；前端实习；熟悉 JavaScript 和响应式布局。");
        ownResume = resumeStorage.save(ownResume);
        ResumeRecordDto otherResume = resumeStorage.save(resume("another-user", "他人简历", "后端开发", "不得读取"));
        CareerProfileApplicationService profileService = new CareerProfileApplicationService(
                new InMemoryCareerProfileStorage(), new InMemoryCareerPlanStorage(),
                new CareerProfileSnapshotMergeService(), new CareerProfileBuildService(), resumeStorage);

        CareerProfileOnboardingRequest request = new CareerProfileOnboardingRequest();
        request.setSelectedResumeId(ownResume.getResumeId());
        UserProfileSnapshot snapshot = profileService.saveOnboarding("selected-resume-user", request);
        CareerUserProfileDto profile = profileService.getProfile("selected-resume-user");

        assertEquals(ownResume.getResumeId(), snapshot.getOnboarding().getSelectedResumeId());
        assertEquals("前端求职简历", profile.getEvidence().get("selected_resume_title"));
        assertTrue(profile.getEvidence().get("selected_resume_content").contains("JavaScript"));

        request.setSelectedResumeId(otherResume.getResumeId());
        assertThrows(IllegalArgumentException.class,
                () -> profileService.saveOnboarding("selected-resume-user", request));
    }

    private ResumeRecordDto resume(String userId, String title, String targetJob, String content) {
        ResumeRecordDto resume = new ResumeRecordDto();
        resume.setUserId(userId);
        resume.setTitle(title);
        resume.setTargetJob(targetJob);
        resume.setParsedContent(content);
        return resume;
    }

    private CareerProfileApplicationService profileService(CareerProfileStorage profileStorage,
                                                           CareerPlanStorage planStorage) {
        return new CareerProfileApplicationService(profileStorage, planStorage,
                new CareerProfileSnapshotMergeService(), new CareerProfileBuildService());
    }

    private CareerPlanSaveRequest saveRequest() {
        CareerPlanSaveRequest request = new CareerPlanSaveRequest();
        request.setTargetRole("Java Engineer");
        request.setWeeklyFocus(Arrays.asList("优化项目经历"));
        return request;
    }

    private boolean hasMissingSignal(CareerUserProfileDto profile, String key) {
        for (CareerUserProfileDto.MissingSignal signal : profile.getMissingSignals()) {
            if (key.equals(signal.getKey())) {
                return true;
            }
        }
        return false;
    }
}
