package v620.cc001.cloud01.app01.mservice;

import org.junit.jupiter.api.Test;
import v620.base.helper.career.CareerProfileBuildService;
import v620.base.helper.career.CareerProfileSnapshotMergeService;
import v620.base.helper.career.EmploymentInsightsResourcesService;
import v620.cc001.base.common.dto.career.CareerProfileOnboardingRequest;
import v620.cc001.base.common.dto.career.CareerResourceFeedDto;
import v620.cc001.base.common.dto.career.EmploymentInsightDto;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmploymentInsightsResourcesApplicationServiceTest {

    @Test
    void insightUsesProfileSchoolMajorAndTargetRole() {
        EmploymentInsightsResourcesApplicationService service = serviceWithProfile("employment-user-1",
                "cdut", "Computer Science", "Software Engineer");

        EmploymentInsightDto insight = service.getInsight("employment-user-1");

        assertEquals(EmploymentInsightsResourcesService.STATUS_AVAILABLE, insight.getStatus());
        assertEquals("Chengdu University of Technology", insight.getSchool());
        assertEquals("Computer Science", insight.getMajor());
        assertEquals("Software Engineer", insight.getTargetRole());
        assertFalse(insight.getSources().isEmpty());
    }

    @Test
    void missingIdentityIsRejectedForUserOwnedInsight() {
        EmploymentInsightsResourcesApplicationService service = new EmploymentInsightsResourcesApplicationService();

        assertThrows(IllegalArgumentException.class, new org.junit.jupiter.api.function.Executable() {
            public void execute() {
                service.getInsight(" ");
            }
        });
    }

    @Test
    void unsupportedSchoolDoesNotReturnBorrowedMetrics() {
        EmploymentInsightsResourcesApplicationService service = serviceWithProfile("employment-user-2",
                "Unknown University", "Computer Science", "Software Engineer");

        EmploymentInsightDto insight = service.getInsight("employment-user-2");

        assertEquals(EmploymentInsightsResourcesService.STATUS_UNSUPPORTED_SCHOOL, insight.getStatus());
        assertNull(insight.getLatestEmploymentRate());
        assertEquals(Integer.valueOf(0), insight.getSourceCount());
    }

    @Test
    void resourcesCanBeReadWithoutUserId() {
        EmploymentInsightsResourcesApplicationService service = new EmploymentInsightsResourcesApplicationService();

        CareerResourceFeedDto feed = service.getResources(null);

        assertEquals(EmploymentInsightsResourcesService.STATUS_AVAILABLE, feed.getStatus());
        assertFalse(feed.getArticles().isEmpty());
        assertFalse(feed.getVideos().isEmpty());
        assertFalse(feed.getConsultations().isEmpty());
        assertContainsSource(feed, "https://www.ncss.cn/");
        assertContainsSource(feed, "https://www.bilibili.com/video/BV1RN411f7LU/");
    }

    private EmploymentInsightsResourcesApplicationService serviceWithProfile(String userId,
                                                                              String school,
                                                                              String major,
                                                                              String targetRole) {
        CareerProfileApplicationService profileService = new CareerProfileApplicationService(
                new InMemoryCareerProfileStorage(), new InMemoryCareerPlanStorage(),
                new CareerProfileSnapshotMergeService(), new CareerProfileBuildService());
        CareerProfileOnboardingRequest request = new CareerProfileOnboardingRequest();
        UserProfileSnapshot.EducationBlock education = new UserProfileSnapshot.EducationBlock();
        education.setSchool(school);
        education.setMajor(major);
        request.setEducation(education);
        request.setTargetRole(targetRole);
        profileService.saveOnboarding(userId, request);
        return new EmploymentInsightsResourcesApplicationService(
                new InMemoryEmploymentInsightStorage(), new InMemoryCareerResourceStorage(),
                profileService, new EmploymentInsightsResourcesService());
    }

    private void assertContainsSource(CareerResourceFeedDto feed, String sourceUrl) {
        for (int i = 0; i < feed.getArticles().size(); i += 1) {
            if (sourceUrl.equals(feed.getArticles().get(i).getSourceUrl())) {
                return;
            }
        }
        for (int i = 0; i < feed.getConsultations().size(); i += 1) {
            if (sourceUrl.equals(feed.getConsultations().get(i).getSourceUrl())) {
                return;
            }
        }
        for (int i = 0; i < feed.getCareerPaths().size(); i += 1) {
            if (sourceUrl.equals(feed.getCareerPaths().get(i).getSourceUrl())) {
                return;
            }
        }
        for (int i = 0; i < feed.getVideos().size(); i += 1) {
            if (sourceUrl.equals(feed.getVideos().get(i).getSourceUrl())) {
                return;
            }
        }
        throw new AssertionError("missing sourceUrl: " + sourceUrl);
    }
}
