package v620.cc001.cloud01.app01.mservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import v620.base.helper.career.CareerPlanSummaryService;
import v620.base.helper.career.CareerProfileBuildService;
import v620.base.helper.career.CareerProfileSnapshotMergeService;
import v620.cc001.base.common.dto.career.CareerPlanSaveRequest;
import v620.cc001.base.common.dto.career.CareerPlanSummaryDto;
import v620.cc001.base.common.dto.career.CareerPlanRecordDto;
import v620.cc001.base.common.dto.career.CareerProfilePreferencesRequest;
import v620.cc001.cloud01.app01.mservice.application.CareerPlanApplicationService;
import v620.cc001.cloud01.app01.mservice.application.CareerProfileApplicationService;
import v620.cc001.cloud01.app01.mservice.storage.CareerPlanStorage;
import v620.cc001.cloud01.app01.mservice.storage.CareerProfileStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.FileCareerPlanStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerPlanStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerProfileStorage;

import java.io.File;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CareerPlanApplicationServiceTest {

    @TempDir
    File tempDir;

    @Test
    void fileStorageReloadsPlanFromFreshServiceInstance() {
        CareerProfileApplicationService profileService = profileService(new InMemoryCareerProfileStorage(), null);
        CareerPlanApplicationService first = service(new FileCareerPlanStorage(tempDir), profileService);
        CareerPlanSaveRequest request = new CareerPlanSaveRequest();
        request.setTargetRole("Java Engineer");
        request.setWeeklyFocus(Arrays.asList("优化项目经历"));
        first.savePlan("plan-user-1", request);

        CareerPlanApplicationService second = service(new FileCareerPlanStorage(tempDir), profileService);
        CareerPlanSummaryDto summary = second.getSummary("plan-user-1");

        assertEquals(Boolean.TRUE, summary.getHasPlan());
        assertEquals("Java Engineer", summary.getTargetRole());
        assertEquals("优化项目经历", summary.getWeeklyFocus().get(0));
    }

    @Test
    void ensurePlanDoesNotInventPlanFromProfile() {
        InMemoryCareerPlanStorage planStorage = new InMemoryCareerPlanStorage();
        CareerProfileApplicationService profileService = profileService(new InMemoryCareerProfileStorage(), planStorage);
        CareerProfilePreferencesRequest preferences = new CareerProfilePreferencesRequest();
        preferences.setTargetRole("Data Analyst");
        profileService.savePreferences("plan-user-2", preferences);
        CareerPlanApplicationService service = service(planStorage, profileService);

        CareerPlanSummaryDto summary = service.ensurePlan("plan-user-2");

        assertEquals(Boolean.FALSE, summary.getHasPlan());
        assertTrue(summary.getWeeklyFocus().isEmpty());
        assertTrue(summary.getPhases().isEmpty());
        assertEquals(null, planStorage.load("plan-user-2"));
    }

    @Test
    void legacyFallbackPlanIsNotExposedAsRealPlan() {
        InMemoryCareerPlanStorage storage = new InMemoryCareerPlanStorage();
        CareerPlanRecordDto legacy = new CareerPlanRecordDto();
        legacy.setUserId("legacy-user");
        legacy.setTargetRole("Fallback Role");
        legacy.setPlanningMode("RULE_FALLBACK");
        storage.save("legacy-user", legacy);
        CareerPlanApplicationService service = service(storage,
                profileService(new InMemoryCareerProfileStorage(), storage));

        assertEquals(Boolean.FALSE, service.getSummary("legacy-user").getHasPlan());
        assertEquals(false, service.hasPlan("legacy-user"));
    }

    @Test
    void getSummaryDoesNotRewriteStoredPlanWhenProfileChanges() {
        InMemoryCareerPlanStorage planStorage = new InMemoryCareerPlanStorage();
        CareerProfileApplicationService profileService = profileService(new InMemoryCareerProfileStorage(), planStorage);
        CareerPlanApplicationService service = service(planStorage, profileService);
        CareerPlanSaveRequest oldPlan = new CareerPlanSaveRequest();
        oldPlan.setTargetRole("Frontend Engineer");
        oldPlan.setWeeklyFocus(Arrays.asList("Build UI portfolio"));
        service.savePlan("plan-user-target-refresh", oldPlan);

        CareerProfilePreferencesRequest preferences = new CareerProfilePreferencesRequest();
        preferences.setTargetRole("Backend Engineer");
        profileService.savePreferences("plan-user-target-refresh", preferences);

        CareerPlanSummaryDto summary = service.getSummary("plan-user-target-refresh");

        assertEquals("Frontend Engineer", summary.getTargetRole());
        assertEquals("Build UI portfolio", summary.getWeeklyFocus().get(0));
        assertEquals("Frontend Engineer", planStorage.load("plan-user-target-refresh").getTargetRole());
    }

    @Test
    void savePlanPersistsOnlySubmittedFieldsAndIncrementsVersion() {
        CareerPlanApplicationService service = service(new InMemoryCareerPlanStorage(),
                profileService(new InMemoryCareerProfileStorage(), null));
        CareerPlanSaveRequest first = new CareerPlanSaveRequest();
        first.setTargetRole("Product Manager");
        first.setWeeklyFocus(Arrays.asList("整理产品案例"));
        CareerPlanSummaryDto created = service.savePlan("plan-user-3", first);

        CareerPlanSaveRequest second = new CareerPlanSaveRequest();
        second.setTargetRole("Product Manager");
        second.setWeeklyFocus(Arrays.asList("投递产品岗位"));
        CareerPlanSummaryDto updated = service.savePlan("plan-user-3", second);

        assertEquals(Integer.valueOf(created.getVersion().intValue() + 1), updated.getVersion());
        assertEquals(1, updated.getWeeklyFocus().size());
        assertEquals("投递产品岗位", updated.getWeeklyFocus().get(0));
        assertTrue(updated.getPhases().isEmpty());
        assertTrue(updated.getDailySuggestions().isEmpty());
    }

    private CareerPlanApplicationService service(CareerPlanStorage storage,
                                                 CareerProfileApplicationService profileService) {
        return new CareerPlanApplicationService(storage, profileService, new CareerPlanSummaryService());
    }

    private CareerProfileApplicationService profileService(CareerProfileStorage profileStorage,
                                                           CareerPlanStorage planStorage) {
        return new CareerProfileApplicationService(profileStorage, planStorage,
                new CareerProfileSnapshotMergeService(), new CareerProfileBuildService());
    }
}
