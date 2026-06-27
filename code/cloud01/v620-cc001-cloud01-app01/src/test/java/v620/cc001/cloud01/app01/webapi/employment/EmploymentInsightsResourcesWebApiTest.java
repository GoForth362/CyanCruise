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
import static org.junit.jupiter.api.Assertions.assertFalse;

class EmploymentInsightsResourcesWebApiTest {

    @Test
    void webApiReturnsInsightAndResources() {
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

        assertEquals(EmploymentInsightsResourcesService.STATUS_AVAILABLE, insight.getStatus());
        assertEquals("Chengdu University of Technology", insight.getSchool());
        assertEquals(EmploymentInsightsResourcesService.STATUS_AVAILABLE, resources.getStatus());
        assertFalse(resources.getArticles().isEmpty());
        assertFalse(resources.getConsultations().isEmpty());
        assertFalse(resources.getVideos().isEmpty());
    }
}
