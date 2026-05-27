package v620.base.helper.career;

import org.junit.jupiter.api.Test;
import v620.cc001.base.common.dto.career.CareerPlanHealth;
import v620.cc001.base.common.dto.career.CareerPlanMilestoneDto;
import v620.cc001.base.common.dto.career.CareerPlanRecordDto;
import v620.cc001.base.common.dto.career.CareerPlanSummaryDto;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CareerPlanSummaryServiceTest {

    private final CareerPlanSummaryService service = new CareerPlanSummaryService();
    private final LocalDateTime now = LocalDateTime.of(2026, 5, 27, 12, 0);

    @Test
    void missingPlanReturnsMissingSummary() {
        CareerPlanSummaryDto summary = service.summarize(null, now);

        assertEquals(Boolean.FALSE, summary.getHasPlan());
        assertEquals(CareerPlanHealth.MISSING, summary.getPlanHealth());
        assertTrue(summary.getWeeklyFocus().isEmpty());
    }

    @Test
    void planSummaryUsesFirstMilestoneAndCleanWeeklyFocus() {
        CareerPlanSummaryDto summary = service.summarize(plan(now.minusDays(1), Integer.valueOf(2),
                Arrays.asList(" 优化项目经历 ", " ", "投递 3 个岗位")), now);

        assertEquals(Boolean.TRUE, summary.getHasPlan());
        assertEquals(CareerPlanHealth.ON_TRACK, summary.getPlanHealth());
        assertEquals("6m", summary.getNextMilestoneHorizon());
        assertEquals("夯实基础", summary.getNextMilestoneTitle());
        assertEquals(2, summary.getWeeklyFocus().size());
        assertEquals("优化项目经历", summary.getWeeklyFocus().get(0));
    }

    @Test
    void stalePlanNeedsRefresh() {
        CareerPlanSummaryDto summary = service.summarize(plan(now.minusDays(15), Integer.valueOf(2),
                Arrays.asList("优化简历")), now);

        assertEquals(CareerPlanHealth.NEEDS_REFRESH, summary.getPlanHealth());
        assertTrue(summary.getAdjustmentReason().contains("14 天"));
    }

    @Test
    void planWithoutWeeklyFocusNeedsRefresh() {
        CareerPlanSummaryDto summary = service.summarize(plan(now.minusDays(1), Integer.valueOf(2),
                Arrays.asList(" ")), now);

        assertEquals(CareerPlanHealth.NEEDS_REFRESH, summary.getPlanHealth());
        assertTrue(summary.getAdjustmentReason().contains("本周重点缺失"));
    }

    @Test
    void oldVersionNeedsRefresh() {
        CareerPlanSummaryDto summary = service.summarize(plan(now.minusDays(1), Integer.valueOf(1),
                Arrays.asList("优化简历")), now);

        assertEquals(CareerPlanHealth.NEEDS_REFRESH, summary.getPlanHealth());
        assertTrue(summary.getAdjustmentReason().contains("版本过旧"));
    }

    @Test
    void defaultPlanUsesProfileTargetOrFallback() {
        CareerPlanRecordDto withRole = service.defaultPlan("user-1", "Java Engineer", now);
        CareerPlanRecordDto fallback = service.defaultPlan("user-2", " ", now);

        assertEquals("Java Engineer", withRole.getTargetRole());
        assertEquals(Integer.valueOf(2), withRole.getVersion());
        assertFalse(withRole.getMilestones().isEmpty());
        assertFalse(withRole.getWeeklyFocus().isEmpty());
        assertEquals("互联网行业职位", fallback.getTargetRole());
    }

    private CareerPlanRecordDto plan(LocalDateTime lastUpdatedAt, Integer version, java.util.List<String> weeklyFocus) {
        CareerPlanRecordDto plan = new CareerPlanRecordDto();
        plan.setUserId("user-1");
        plan.setTargetRole("Java Engineer");
        plan.setMilestones(Arrays.asList(milestone()));
        plan.setWeeklyFocus(weeklyFocus);
        plan.setGeneratedAt(now.minusDays(2));
        plan.setLastUpdatedAt(lastUpdatedAt);
        plan.setVersion(version);
        return plan;
    }

    private CareerPlanMilestoneDto milestone() {
        CareerPlanMilestoneDto milestone = new CareerPlanMilestoneDto();
        milestone.setHorizon("6m");
        milestone.setTitle("夯实基础");
        return milestone;
    }
}
