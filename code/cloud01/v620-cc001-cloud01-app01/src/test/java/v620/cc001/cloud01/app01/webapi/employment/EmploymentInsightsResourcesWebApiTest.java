package v620.cc001.cloud01.app01.webapi.employment;

import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerPlanStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerProfileStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerResourceStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryEmploymentInsightStorage;
import org.junit.jupiter.api.Test;
import v620.base.helper.career.CareerProfileBuildService;
import v620.base.helper.career.CareerProfileSnapshotMergeService;
import v620.base.helper.career.EmploymentInsightsResourcesService;
import v620.cc001.base.common.dto.career.CareerProfileOnboardingRequest;
import v620.cc001.base.common.dto.career.CareerResourceFeedDto;
import v620.cc001.base.common.dto.career.EmploymentInsightDto;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;
import v620.cc001.cloud01.app01.mservice.application.CareerProfileApplicationService;
import v620.cc001.cloud01.app01.mservice.application.EmploymentInsightsResourcesApplicationService;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerPlanStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerProfileStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerResourceStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryEmploymentInsightStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmploymentInsightsResourcesWebApiTest {

    @Test
    void webApiReturnsExplicitEmptyStatesWithoutSeededData() {
        CareerProfileApplicationService profileService = new CareerProfileApplicationService(
                new InMemoryCareerProfileStorage(), new InMemoryCareerPlanStorage(),
                new CareerProfileSnapshotMergeService(), new CareerProfileBuildService());
        CareerProfileOnboardingRequest request = new CareerProfileOnboardingRequest();
        UserProfileSnapshot.EducationBlock education = new UserProfileSnapshot.EducationBlock();
        education.setSchool("cdut");
        education.setMajor("Computer Science");
        request.setEducation(education);
        request.setTargetRole("Software Engineer");
        profileService.saveOnboarding("employment-api-user", request);

        EmploymentInsightsResourcesWebApi webApi = new EmploymentInsightsResourcesWebApi(
                new EmploymentInsightsResourcesApplicationService(
                        new InMemoryEmploymentInsightStorage(), new InMemoryCareerResourceStorage(),
                        profileService, new EmploymentInsightsResourcesService()));

        EmploymentInsightDto insight = webApi.insight("employment-api-user");
        CareerResourceFeedDto resources = webApi.resources("employment-api-user");

        assertEquals(EmploymentInsightsResourcesService.STATUS_NO_SOURCES, insight.getStatus());
        assertEquals(EmploymentInsightsResourcesService.STATUS_EMPTY, resources.getStatus());
        assertTrue(resources.getArticles().isEmpty());
        assertTrue(resources.getConsultations().isEmpty());
        assertTrue(resources.getVideos().isEmpty());
    }
}
