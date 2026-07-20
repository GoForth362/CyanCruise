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
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmploymentInsightsResourcesApplicationServiceTest {

    @Test
    void emptyStorageDoesNotInventSchoolMetrics() {
        EmploymentInsightsResourcesApplicationService service = serviceWithProfile("employment-user-1",
                "cdut", "Computer Science", "Software Engineer");

        EmploymentInsightDto insight = service.getInsight("employment-user-1");

        assertEquals(EmploymentInsightsResourcesService.STATUS_NO_SOURCES, insight.getStatus());
        assertEquals("Computer Science", insight.getMajor());
        assertEquals("Software Engineer", insight.getTargetRole());
        assertTrue(insight.getSources().isEmpty());
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
    void emptyResourcesCanBeReadWithoutUserId() {
        EmploymentInsightsResourcesApplicationService service = new EmploymentInsightsResourcesApplicationService(
                new InMemoryEmploymentInsightStorage(), new InMemoryCareerResourceStorage(),
                new CareerProfileApplicationService(new InMemoryCareerProfileStorage(), new InMemoryCareerPlanStorage(),
                        new CareerProfileSnapshotMergeService(), new CareerProfileBuildService()),
                new EmploymentInsightsResourcesService());

        CareerResourceFeedDto feed = service.getResources(null);

        assertEquals(EmploymentInsightsResourcesService.STATUS_EMPTY, feed.getStatus());
        assertTrue(feed.getArticles().isEmpty());
        assertTrue(feed.getVideos().isEmpty());
        assertTrue(feed.getConsultations().isEmpty());
    }

    @Test
    void resourcesIncludeVisibleAdminContentAndSkipHiddenContent() {
        InMemoryAdminGovernanceStorage adminStorage = new InMemoryAdminGovernanceStorage();
        AdminContentItemDto visible = new AdminContentItemDto();
        visible.setContentId("z");
        visible.setType("ARTICLE");
        visible.setTitle("管理员发布的求职文章");
        visible.setSummary("这是一条后台配置的文章。");
        visible.setSourceUrl("https://example.com/article");
        visible.setPinned(Boolean.TRUE);
        adminStorage.saveContent(visible);
        AdminContentItemDto unpinned = new AdminContentItemDto();
        unpinned.setContentId("a");
        unpinned.setType("ARTICLE");
        unpinned.setTitle("Unpinned article");
        unpinned.setSourceUrl("https://example.com/unpinned-article");
        adminStorage.saveContent(unpinned);
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
        assertEquals(visible.getTitle(), feed.getArticles().get(0).getTitle());
        assertTrue(Boolean.TRUE.equals(feed.getArticles().get(0).getPinned()));
        assertContainsSource(feed, "https://example.com/article");
        assertMissingSource(feed, "https://example.com/hidden-video");
    }

    @Test
    void visibleAdminContentIsPublishedEvenWhenItUsesAHistoricalSeedId() {
        InMemoryAdminGovernanceStorage adminStorage = new InMemoryAdminGovernanceStorage();
        AdminContentItemDto visible = new AdminContentItemDto();
        visible.setContentId("service-jobonline-001");
        visible.setType("RESOURCE");
        visible.setTitle("就业在线");
        visible.setSourceUrl("https://www.jobonline.cn/");
        visible.setHidden(Boolean.FALSE);
        adminStorage.saveContent(visible);

        EmploymentInsightsResourcesApplicationService service = new EmploymentInsightsResourcesApplicationService(
                new InMemoryEmploymentInsightStorage(),
                new AdminContentCareerResourceStorage(adminStorage, null),
                new CareerProfileApplicationService(), new EmploymentInsightsResourcesService());

        CareerResourceFeedDto feed = service.getResources(null);

        assertEquals(EmploymentInsightsResourcesService.STATUS_AVAILABLE, feed.getStatus());
        assertContainsSource(feed, "https://www.jobonline.cn/");
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
