package v620.cc001.cloud01.app01.webapi.career;

import org.junit.jupiter.api.Test;
import v620.base.helper.career.CareerPlanSummaryService;
import v620.base.helper.career.CareerProfileBuildService;
import v620.base.helper.career.CareerProfileSnapshotMergeService;
import v620.cc001.base.common.dto.career.CareerPlanSaveRequest;
import v620.cc001.base.common.dto.career.CareerPlanSummaryDto;
import v620.cc001.base.common.dto.career.CareerProfilePreferencesRequest;
import v620.cc001.cloud01.app01.mservice.application.CareerPlanApplicationService;
import v620.cc001.cloud01.app01.mservice.application.CareerProfileApplicationService;
import v620.cc001.cloud01.app01.mservice.storage.CareerPlanStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerPlanStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerProfileStorage;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CareerPlanWebApiTest {

    @Test
    void ensureDoesNotCreatePlanAndSaveUsesApplicationBoundary() {
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
        request.setWeeklyFocus(Arrays.asList("分析一个岗位要求"));
        CareerPlanSummaryDto saved = webApi.save("api-plan-user", request);

        assertEquals(Boolean.FALSE, missing.getHasPlan());
        assertEquals(Boolean.FALSE, ensured.getHasPlan());
        assertEquals(Boolean.TRUE, saved.getHasPlan());
        assertEquals("分析一个岗位要求", saved.getWeeklyFocus().get(0));
    }

    private CareerProfileApplicationService profileService(CareerPlanStorage planStorage) {
        return new CareerProfileApplicationService(new InMemoryCareerProfileStorage(), planStorage,
                new CareerProfileSnapshotMergeService(), new CareerProfileBuildService());
    }
}
