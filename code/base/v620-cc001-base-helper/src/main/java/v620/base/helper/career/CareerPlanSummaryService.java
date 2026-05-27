package v620.base.helper.career;

import v620.cc001.base.common.dto.career.CareerPlanHealth;
import v620.cc001.base.common.dto.career.CareerPlanMilestoneDto;
import v620.cc001.base.common.dto.career.CareerPlanRecordDto;
import v620.cc001.base.common.dto.career.CareerPlanSummaryDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Pure Java career plan summary and fallback-plan rules.
 */
public class CareerPlanSummaryService {

    public static final int CURRENT_PLAN_VERSION_BASELINE = 2;
    private static final String DEFAULT_TARGET_ROLE = "互联网行业职位";

    public CareerPlanSummaryDto summarize(CareerPlanRecordDto plan, LocalDateTime now) {
        if (plan == null) {
            CareerPlanSummaryDto missing = new CareerPlanSummaryDto();
            missing.setHasPlan(Boolean.FALSE);
            missing.setPlanHealth(CareerPlanHealth.MISSING);
            missing.setAdjustmentReason("尚未生成长期求职计划。");
            missing.setWeeklyFocus(new ArrayList<String>());
            return missing;
        }

        List<String> weeklyFocus = clean(plan.getWeeklyFocus());
        CareerPlanMilestoneDto firstMilestone = firstMilestone(plan.getMilestones());
        CareerPlanSummaryDto summary = new CareerPlanSummaryDto();
        summary.setHasPlan(Boolean.TRUE);
        summary.setTargetRole(plan.getTargetRole());
        summary.setPlanHealth(planHealth(plan, weeklyFocus, now == null ? LocalDateTime.now() : now));
        summary.setAdjustmentReason(planAdjustmentReason(plan, weeklyFocus, now == null ? LocalDateTime.now() : now));
        summary.setNextMilestoneHorizon(firstMilestone == null ? "" : value(firstMilestone.getHorizon()));
        summary.setNextMilestoneTitle(firstMilestone == null ? "" : value(firstMilestone.getTitle()));
        summary.setWeeklyFocus(weeklyFocus);
        summary.setGeneratedAt(plan.getGeneratedAt() == null ? null : plan.getGeneratedAt().toString());
        summary.setLastUpdatedAt(plan.getLastUpdatedAt() == null ? null : plan.getLastUpdatedAt().toString());
        summary.setVersion(plan.getVersion());
        return summary;
    }

    public CareerPlanRecordDto defaultPlan(String userId, String targetRole, LocalDateTime now) {
        LocalDateTime timestamp = now == null ? LocalDateTime.now() : now;
        String target = hasText(targetRole) ? targetRole.trim() : DEFAULT_TARGET_ROLE;
        CareerPlanRecordDto plan = new CareerPlanRecordDto();
        plan.setUserId(userId);
        plan.setTargetRole(target);
        plan.setStartStateSummary("基于当前职业画像生成的默认求职计划。");
        plan.setMilestones(defaultMilestones(target));
        plan.setWeeklyFocus(Arrays.asList(
                "完善个人简历，突出与" + target + "相关的核心优势",
                "每天完成一项" + target + "相关练习或岗位研究",
                "研究 1-2 家目标公司的招聘要求"));
        plan.setModelUsed("default");
        plan.setTokensConsumed(Integer.valueOf(0));
        plan.setGeneratedAt(timestamp);
        plan.setLastUpdatedAt(timestamp);
        plan.setVersion(Integer.valueOf(CURRENT_PLAN_VERSION_BASELINE));
        return plan;
    }

    private String planHealth(CareerPlanRecordDto plan, List<String> weeklyFocus, LocalDateTime now) {
        if (plan.getLastUpdatedAt() == null) {
            return CareerPlanHealth.NEEDS_REFRESH;
        }
        if (plan.getLastUpdatedAt().isBefore(now.minusDays(14))) {
            return CareerPlanHealth.NEEDS_REFRESH;
        }
        if (weeklyFocus == null || weeklyFocus.isEmpty()) {
            return CareerPlanHealth.NEEDS_REFRESH;
        }
        if (plan.getVersion() != null && plan.getVersion().intValue() < CURRENT_PLAN_VERSION_BASELINE) {
            return CareerPlanHealth.NEEDS_REFRESH;
        }
        return CareerPlanHealth.ON_TRACK;
    }

    private String planAdjustmentReason(CareerPlanRecordDto plan, List<String> weeklyFocus, LocalDateTime now) {
        if (plan.getLastUpdatedAt() == null) {
            return "规划尚未生成，无法评估健康度。";
        }
        if (plan.getLastUpdatedAt().isBefore(now.minusDays(14))) {
            return "规划超过 14 天未更新，建议重新生成以对齐最新状态。";
        }
        if (weeklyFocus == null || weeklyFocus.isEmpty()) {
            return "本周重点缺失，今日任务无法与长期路径对齐。";
        }
        if (plan.getVersion() != null && plan.getVersion().intValue() < CURRENT_PLAN_VERSION_BASELINE) {
            return "规划版本过旧，建议刷新以获得更准确的建议。";
        }
        return "长期规划已就绪，今日行动可与其保持对齐。";
    }

    private List<CareerPlanMilestoneDto> defaultMilestones(String targetRole) {
        List<CareerPlanMilestoneDto> milestones = new ArrayList<CareerPlanMilestoneDto>();
        milestones.add(milestone("6m", "夯实" + targetRole + "核心技能基础",
                Arrays.asList("系统学习目标岗位核心技能", "完成 2 个以上实践项目"),
                Arrays.asList("掌握岗位所需技能栈")));
        milestones.add(milestone("1y", "获得相关实习或初级岗位",
                Arrays.asList("精心准备简历和作品集", "积极投递目标公司"),
                Arrays.asList("拿到至少 1 个满意 offer 或实习机会")));
        milestones.add(milestone("3y", "成长为稳定独立的中级人才",
                Arrays.asList("深耕专业方向", "建立职业人脉"),
                Arrays.asList("能独立负责核心模块或业务专题")));
        milestones.add(milestone("5y", "形成高级或专家方向竞争力",
                Arrays.asList("承担更大责任", "沉淀个人方法论"),
                Arrays.asList("担任高级职位或成为领域专家")));
        return milestones;
    }

    private CareerPlanMilestoneDto milestone(String horizon, String title, List<String> actions, List<String> kpis) {
        CareerPlanMilestoneDto milestone = new CareerPlanMilestoneDto();
        milestone.setHorizon(horizon);
        milestone.setTitle(title);
        milestone.setSkills(new ArrayList<String>());
        milestone.setActions(actions);
        milestone.setKpis(kpis);
        return milestone;
    }

    private CareerPlanMilestoneDto firstMilestone(List<CareerPlanMilestoneDto> milestones) {
        if (milestones == null) {
            return null;
        }
        for (CareerPlanMilestoneDto milestone : milestones) {
            if (milestone != null) {
                return milestone;
            }
        }
        return null;
    }

    private List<String> clean(List<String> values) {
        List<String> clean = new ArrayList<String>();
        if (values == null) {
            return clean;
        }
        for (String value : values) {
            if (hasText(value)) {
                clean.add(value.trim());
            }
        }
        return clean;
    }

    private String value(String text) {
        return text == null ? "" : text;
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
