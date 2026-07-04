package v620.cc001.cloud01.app01.mservice;

import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerPlanStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerProfileStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryAdminGovernanceStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.AdminContentCareerResourceStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerResourceStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryEmploymentInsightStorage;
import v620.cc001.cloud01.app01.mservice.application.CareerProfileApplicationService;
import v620.cc001.cloud01.app01.mservice.application.EmploymentInsightsResourcesApplicationService;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerPlanStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerProfileStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerResourceStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryEmploymentInsightStorage;
import org.junit.jupiter.api.Test;
import v620.base.helper.career.CareerProfileBuildService;
import v620.base.helper.career.CareerProfileSnapshotMergeService;
import v620.base.helper.career.EmploymentInsightsResourcesService;
import v620.cc001.base.common.dto.career.AdminContentItemDto;
import v620.cc001.base.common.dto.career.CareerProfileOnboardingRequest;
import v620.cc001.base.common.dto.career.CareerResourceFeedDto;
import v620.cc001.base.common.dto.career.CareerResourceCardDto;
import v620.cc001.base.common.dto.career.EmploymentInsightDto;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;

import java.util.Collections;

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

    @Test
    void resourcesIncludeVisibleAdminContentAndSkipHiddenContent() {
        InMemoryAdminGovernanceStorage adminStorage = new InMemoryAdminGovernanceStorage();
        AdminContentItemDto visible = new AdminContentItemDto();
        visible.setType("ARTICLE");
        visible.setTitle("管理员发布的求职文章");
        visible.setSummary("这是一条后台配置的文章。");
        visible.setSourceUrl("https://example.com/article");
        visible.setPinned(Boolean.TRUE);
        adminStorage.saveContent(visible);
        AdminContentItemDto hidden = new AdminContentItemDto();
        hidden.setType("VIDEO");
        hidden.setTitle("隐藏视频");
        hidden.setSourceUrl("https://example.com/hidden-video");
        hidden.setHidden(Boolean.TRUE);
        adminStorage.saveContent(hidden);

        EmploymentInsightsResourcesApplicationService service = new EmploymentInsightsResourcesApplicationService(
                new InMemoryEmploymentInsightStorage(),
                new AdminContentCareerResourceStorage(adminStorage,
                        new InMemoryCareerResourceStorage(Collections.<CareerResourceCardDto>emptyList())),
                new CareerProfileApplicationService(), new EmploymentInsightsResourcesService());

        CareerResourceFeedDto feed = service.getResources(null);

        assertEquals(EmploymentInsightsResourcesService.STATUS_AVAILABLE, feed.getStatus());
        assertContainsSource(feed, "https://example.com/article");
        assertMissingSource(feed, "https://example.com/hidden-video");
    }

    @Test
    void hiddenAdminContentSuppressesFallbackResourceWithSameId() {
        InMemoryAdminGovernanceStorage adminStorage = new InMemoryAdminGovernanceStorage();
        AdminContentItemDto hidden = new AdminContentItemDto();
        hidden.setContentId("fallback-resource-1");
        hidden.setType("ARTICLE");
        hidden.setTitle("Hidden fallback resource");
        hidden.setHidden(Boolean.TRUE);
        adminStorage.saveContent(hidden);
        CareerResourceCardDto fallback = new CareerResourceCardDto();
        fallback.setId("fallback-resource-1");
        fallback.setType("article");
        fallback.setTitle("Fallback article");
        fallback.setSourceUrl("https://example.com/fallback");

        EmploymentInsightsResourcesApplicationService service = new EmploymentInsightsResourcesApplicationService(
                new InMemoryEmploymentInsightStorage(),
                new AdminContentCareerResourceStorage(adminStorage,
                        new InMemoryCareerResourceStorage(Collections.singletonList(fallback))),
                new CareerProfileApplicationService(), new EmploymentInsightsResourcesService());

        CareerResourceFeedDto feed = service.getResources(null);

        assertMissingSource(feed, "https://example.com/fallback");
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

    private void assertMissingSource(CareerResourceFeedDto feed, String sourceUrl) {
        try {
            assertContainsSource(feed, sourceUrl);
        } catch (AssertionError expected) {
            return;
        }
        throw new AssertionError("unexpected sourceUrl: " + sourceUrl);
    }
}
