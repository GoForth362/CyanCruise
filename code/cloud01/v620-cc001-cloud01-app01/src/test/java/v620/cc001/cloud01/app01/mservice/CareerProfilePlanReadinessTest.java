package v620.cc001.cloud01.app01.mservice;

import org.junit.jupiter.api.Test;
import v620.base.helper.career.CareerPlanSummaryService;
import v620.base.helper.career.CareerProfileBuildService;
import v620.base.helper.career.CareerProfileSnapshotMergeService;
import v620.cc001.base.common.dto.career.CareerPlanSaveRequest;
import v620.cc001.base.common.dto.career.CareerProfilePreferencesRequest;
import v620.cc001.base.common.dto.career.CareerUserProfileDto;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
