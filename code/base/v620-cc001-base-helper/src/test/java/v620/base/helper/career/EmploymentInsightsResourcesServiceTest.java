package v620.base.helper.career;

import org.junit.jupiter.api.Test;
import v620.cc001.base.common.dto.career.CareerResourceCardDto;
import v620.cc001.base.common.dto.career.CareerResourceFeedDto;
import v620.cc001.base.common.dto.career.EmploymentInsightDto;
import v620.cc001.base.common.dto.career.EmploymentInsightProfileContext;
import v620.cc001.base.common.dto.career.EmploymentInsightRecordDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmploymentInsightsResourcesServiceTest {

    private final EmploymentInsightsResourcesService service = new EmploymentInsightsResourcesService();
    private final LocalDateTime now = LocalDateTime.of(2026, 5, 28, 12, 0);

    @Test
    void supportedSchoolAliasIsNormalizedAndMetricsRemainSourceBacked() {
        EmploymentInsightDto insight = service.buildInsight(context("cdut", "Computer Science", "Software Engineer"),
                Arrays.asList(record("r1", "Chengdu University of Technology", 2025,
                        "CDUT official report", "CDUT_OFFICIAL_PDF", "Computer", "Software",
                        new BigDecimal("91.20"), new BigDecimal("23.40"), "Software and engineering roles are visible.")), now);

        assertEquals(EmploymentInsightsResourcesService.STATUS_AVAILABLE, insight.getStatus());
        assertEquals("Chengdu University of Technology", insight.getSchool());
        assertEquals(new BigDecimal("91.20"), insight.getLatestEmploymentRate());
        assertEquals(1, insight.getTrend().size());
        assertEquals(EmploymentInsightsResourcesService.COVERAGE_VERIFIED_FULL, insight.getCoverage().get(4).getStatus());
        assertEquals("r1", insight.getSources().get(0).getId());
    }

    @Test
    void chineseSchoolNameIsNormalizedToSupportedSource() {
        EmploymentInsightDto insight = service.buildInsight(context("成都理工大学", "软件工程", "前端开发"),
                Arrays.asList(record("r1", "Chengdu University of Technology", 2025,
                        "CDUT official report", "CDUT_OFFICIAL_PDF", "软件", "前端",
                        new BigDecimal("91.20"), null, "软件和前端岗位去向可见。")), now);

        assertEquals(EmploymentInsightsResourcesService.STATUS_AVAILABLE, insight.getStatus());
        assertEquals("Chengdu University of Technology", insight.getSchool());
        assertEquals(Integer.valueOf(1), insight.getSourceCount());
    }

    @Test
    void missingSchoolDoesNotBorrowOtherSchoolMetrics() {
        EmploymentInsightDto insight = service.buildInsight(context(null, "Computer Science", "Software Engineer"),
                Arrays.asList(record("r1", "Chengdu University of Technology", 2025,
                        "CDUT official report", "CDUT_OFFICIAL_PDF", "Computer", "Software",
                        new BigDecimal("91.20"), null, "Software roles.")), now);

        assertEquals(EmploymentInsightsResourcesService.STATUS_MISSING_SCHOOL, insight.getStatus());
        assertNull(insight.getLatestEmploymentRate());
        assertTrue(insight.getSources().isEmpty());
    }

    @Test
    void unsupportedSchoolDoesNotUseSupportedSchoolRecords() {
        EmploymentInsightDto insight = service.buildInsight(context("Unknown University", "Computer Science", "Software Engineer"),
                Arrays.asList(record("r1", "Chengdu University of Technology", 2025,
                        "CDUT official report", "CDUT_OFFICIAL_PDF", "Computer", "Software",
                        new BigDecimal("91.20"), null, "Software roles.")), now);

        assertEquals(EmploymentInsightsResourcesService.STATUS_UNSUPPORTED_SCHOOL, insight.getStatus());
        assertNull(insight.getLatestEmploymentRate());
        assertEquals(Integer.valueOf(0), insight.getSourceCount());
    }

    @Test
    void absentMetricsAreNotFabricated() {
        EmploymentInsightDto insight = service.buildInsight(context("Chengdu University of Technology", "Design", "Product Manager"),
                Arrays.asList(record("r1", "Chengdu University of Technology", 2025,
                        "Public summary", "PUBLIC_SUMMARY", "Design", "Product", null, null,
                        "Public source has destination text but no numeric rate.")), now);

        assertEquals(EmploymentInsightsResourcesService.STATUS_AVAILABLE, insight.getStatus());
        assertNull(insight.getLatestEmploymentRate());
        assertNull(insight.getLatestPostgraduateRate());
        assertTrue(insight.getSummary().contains("暂未提供量化指标"));
        assertEquals(EmploymentInsightsResourcesService.COVERAGE_NEEDS_REVIEW, insight.getCoverage().get(4).getStatus());
    }

    @Test
    void missingTargetRoleReturnsSchoolLevelInsightOnly() {
        EmploymentInsightDto insight = service.buildInsight(context("Chengdu University of Technology", "Computer Science", null),
                Arrays.asList(record("r1", "Chengdu University of Technology", 2025,
                        "CDUT official report", "CDUT_OFFICIAL_PDF", "Computer", "Software",
                        new BigDecimal("91.20"), null, "Software roles.")), now);

        assertEquals(EmploymentInsightsResourcesService.STATUS_MISSING_TARGET_ROLE, insight.getStatus());
        assertTrue(insight.getSummary().contains("补充目标岗位"));
    }

    @Test
    void resourceFeedReturnsEmptyStateAndTypedCards() {
        CareerResourceFeedDto empty = service.buildResourceFeed(Collections.<CareerResourceCardDto>emptyList(), null, now);
        CareerResourceCardDto article = card("a1", "article");
        CareerResourceCardDto video = card("v1", "video");
        CareerResourceFeedDto feed = service.buildResourceFeed(Arrays.asList(video, article), "user-1", now);

        assertEquals(EmploymentInsightsResourcesService.STATUS_EMPTY, empty.getStatus());
        assertEquals(EmploymentInsightsResourcesService.STATUS_AVAILABLE, feed.getStatus());
        assertEquals(1, feed.getArticles().size());
        assertEquals(1, feed.getVideos().size());
    }

    private EmploymentInsightProfileContext context(String school, String major, String targetRole) {
        EmploymentInsightProfileContext context = new EmploymentInsightProfileContext();
        context.setUserId("u1");
        context.setSchool(school);
        context.setMajor(major);
        context.setTargetRole(targetRole);
        return context;
    }

    private EmploymentInsightRecordDto record(String id, String school, int year, String title, String sourceType,
                                              String majorKeyword, String careerKeyword, BigDecimal employmentRate,
                                              BigDecimal postgraduateRate, String summary) {
        EmploymentInsightRecordDto record = new EmploymentInsightRecordDto();
        record.setId(id);
        record.setSchool(school);
        record.setYear(Integer.valueOf(year));
        record.setSourceTitle(title);
        record.setSourceUrl("https://example.test/" + id);
        record.setSourceType(sourceType);
        record.setMajorKeyword(majorKeyword);
        record.setCareerKeyword(careerKeyword);
        record.setEmploymentRate(employmentRate);
        record.setPostgraduateRate(postgraduateRate);
        record.setDestinationSummary(summary);
        record.setFetchedAt(now.minusDays(1));
        return record;
    }

    private CareerResourceCardDto card(String id, String type) {
        CareerResourceCardDto card = new CareerResourceCardDto();
        card.setId(id);
        card.setType(type);
        card.setTitle(type + " title");
        return card;
    }
}
