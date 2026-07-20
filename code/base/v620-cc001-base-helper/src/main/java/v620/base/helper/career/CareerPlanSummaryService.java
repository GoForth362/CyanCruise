package v620.base.helper.career;

import v620.cc001.base.common.dto.career.CareerPlanHealth;
import v620.cc001.base.common.dto.career.CareerPlanMilestoneDto;
import v620.cc001.base.common.dto.career.CareerPlanPhaseDto;
import v620.cc001.base.common.dto.career.CareerPlanRecordDto;
import v620.cc001.base.common.dto.career.CareerPlanSummaryDto;
import v620.cc001.base.common.dto.career.CareerRouteContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Converts a saved career plan into a summary without generating missing business data. */
public class CareerPlanSummaryService {

    public static final int CURRENT_PLAN_VERSION_BASELINE = 2;

    public CareerPlanSummaryDto summarize(CareerPlanRecordDto plan, LocalDateTime now) {
        if (plan == null) {
            CareerPlanSummaryDto missing = new CareerPlanSummaryDto();
            missing.setHasPlan(Boolean.FALSE);
            missing.setPlanHealth(CareerPlanHealth.MISSING);
            missing.setAdjustmentReason("尚未生成真实职业计划。");
            missing.setWeeklyFocus(new ArrayList<String>());
            missing.setDailySuggestions(new ArrayList<String>());
            missing.setPhases(new ArrayList<CareerPlanPhaseDto>());
            return missing;
        }

        List<String> weeklyFocus = clean(plan.getWeeklyFocus());
        if (weeklyFocus.isEmpty() && plan.getWeeklyPlan() != null) {
            weeklyFocus = clean(plan.getWeeklyPlan().getActions());
        }
        CareerPlanMilestoneDto firstMilestone = firstMilestone(plan.getMilestones());
        LocalDateTime reference = now == null ? LocalDateTime.now() : now;
        CareerPlanSummaryDto summary = new CareerPlanSummaryDto();
        summary.setHasPlan(Boolean.TRUE);
        summary.setTargetRole(plan.getTargetRole());
        summary.setRouteType(plan.getRouteType() == null ? CareerRouteContext.EMPLOYMENT : plan.getRouteType());
        summary.setStudyDirection(plan.getStudyDirection());
        summary.setTargetSchool(plan.getTargetSchool());
        summary.setStartStateSummary(plan.getStartStateSummary());
        summary.setPlanningMode(plan.getPlanningMode());
        summary.setHorizonYears(plan.getHorizonYears());
        summary.setAgentStatus(plan.getAgentStatus());
        summary.setModelUsed(plan.getModelUsed());
        summary.setPlanHealth(planHealth(plan, weeklyFocus, reference));
        summary.setAdjustmentReason(planAdjustmentReason(plan, weeklyFocus, reference));
        summary.setNextMilestoneHorizon(firstMilestone == null ? "" : value(firstMilestone.getHorizon()));
        summary.setNextMilestoneTitle(firstMilestone == null ? "" : value(firstMilestone.getTitle()));
        summary.setPhases(plan.getPhases() == null ? new ArrayList<CareerPlanPhaseDto>() : plan.getPhases());
        summary.setWeeklyPlan(plan.getWeeklyPlan());
        summary.setDailySuggestions(clean(plan.getDailySuggestions()));
        summary.setWeeklyFocus(weeklyFocus);
        summary.setGeneratedAt(plan.getGeneratedAt() == null ? null : plan.getGeneratedAt().toString());
        summary.setLastUpdatedAt(plan.getLastUpdatedAt() == null ? null : plan.getLastUpdatedAt().toString());
        summary.setVersion(plan.getVersion());
        return summary;
    }

    private String planHealth(CareerPlanRecordDto plan, List<String> weeklyFocus, LocalDateTime now) {
        if (plan.getLastUpdatedAt() == null || plan.getLastUpdatedAt().isBefore(now.minusDays(14))
                || weeklyFocus.isEmpty()
                || (plan.getVersion() != null && plan.getVersion().intValue() < CURRENT_PLAN_VERSION_BASELINE)) {
            return CareerPlanHealth.NEEDS_REFRESH;
        }
        return CareerPlanHealth.ON_TRACK;
    }

    private String planAdjustmentReason(CareerPlanRecordDto plan, List<String> weeklyFocus, LocalDateTime now) {
        if (plan.getLastUpdatedAt() == null) return "规划缺少更新时间，请重新生成真实计划。";
        if (plan.getLastUpdatedAt().isBefore(now.minusDays(14))) return "规划超过 14 天未更新，建议重新生成。";
        if (weeklyFocus.isEmpty()) return "本周重点缺失，今日任务无法与长期路径对齐。";
        if (plan.getVersion() != null && plan.getVersion().intValue() < CURRENT_PLAN_VERSION_BASELINE) {
            return "规划版本过旧，建议刷新。";
        }
        return "长期规划已就绪，今日行动可与其保持对齐。";
    }

    private CareerPlanMilestoneDto firstMilestone(List<CareerPlanMilestoneDto> milestones) {
        if (milestones == null) return null;
        for (CareerPlanMilestoneDto milestone : milestones) {
            if (milestone != null) return milestone;
        }
        return null;
    }

    private List<String> clean(List<String> values) {
        List<String> cleaned = new ArrayList<String>();
        if (values == null) return cleaned;
        for (String item : values) {
            if (item != null && item.trim().length() > 0) cleaned.add(item.trim());
        }
        return cleaned;
    }

    private String value(String text) {
        return text == null ? "" : text;
    }
}
