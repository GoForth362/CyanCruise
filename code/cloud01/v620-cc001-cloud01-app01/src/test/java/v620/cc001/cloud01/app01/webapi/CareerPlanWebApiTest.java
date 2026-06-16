package v620.cc001.cloud01.app01.webapi;

import org.junit.jupiter.api.Test;
import v620.base.helper.career.CareerPlanSummaryService;
import v620.base.helper.career.CareerProfileBuildService;
import v620.base.helper.career.CareerProfileSnapshotMergeService;
import v620.cc001.base.common.dto.career.CareerPlanSaveRequest;
import v620.cc001.base.common.dto.career.CareerPlanSummaryDto;
import v620.cc001.base.common.dto.career.CareerProfilePreferencesRequest;
import v620.cc001.cloud01.app01.mservice.CareerPlanApplicationService;
import v620.cc001.cloud01.app01.mservice.CareerPlanStorage;
import v620.cc001.cloud01.app01.mservice.CareerProfileApplicationService;
import v620.cc001.cloud01.app01.mservice.InMemoryCareerPlanStorage;
import v620.cc001.cloud01.app01.mservice.InMemoryCareerProfileStorage;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CareerPlanWebApiTest {

    @Test
    void summaryEnsureAndSaveUseApplicationBoundary() {
        CareerPlanStorage planStorage = new InMemoryCareerPlanStorage();
        CareerProfileApplicationService profileService = profileService(planStorage);
        CareerProfilePreferencesRequest preferences = new CareerProfilePreferencesRequest();
        preferences.setTargetRole("Data Analyst");
        profileService.savePreferences("api-plan-user", preferences);
        CareerPlanWebApi webApi = new CareerPlanWebApi(new CareerPlanApplicationService(
                planStorage, profileService, new CareerPlanSummaryService()));

        CareerPlanSummaryDto missing = webApi.summary("api-plan-user");
        CareerPlanSummaryDto ensured = webApi.ensure("api-plan-user");
        CareerPlanSaveRequest request = new CareerPlanSaveRequest();
        request.setTargetRole("Data Analyst");
        request.setWeeklyFocus(Arrays.asList("分析一个招聘 JD"));
        CareerPlanSummaryDto saved = webApi.save("api-plan-user", request);

        assertEquals(Boolean.FALSE, missing.getHasPlan());
        assertEquals(Boolean.TRUE, ensured.getHasPlan());
        assertEquals("Data Analyst", ensured.getTargetRole());
        assertEquals("RULE_FALLBACK", ensured.getPlanningMode());
        assertTrue(ensured.getPhases().size() >= 3);
        assertEquals("分析一个招聘 JD", saved.getWeeklyFocus().get(0));
    }

    private CareerProfileApplicationService profileService(CareerPlanStorage planStorage) {
        return new CareerProfileApplicationService(new InMemoryCareerProfileStorage(), planStorage,
                new CareerProfileSnapshotMergeService(), new CareerProfileBuildService());
    }
}
