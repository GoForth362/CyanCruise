package v620.base.helper.career;

import v620.cc001.base.common.dto.career.CareerPlanHealth;
import v620.cc001.base.common.dto.career.CareerPlanMilestoneDto;
import v620.cc001.base.common.dto.career.CareerPlanPhaseDto;
import v620.cc001.base.common.dto.career.CareerPlanRecordDto;
import v620.cc001.base.common.dto.career.CareerPlanSubStageDto;
import v620.cc001.base.common.dto.career.CareerPlanSummaryDto;
import v620.cc001.base.common.dto.career.CareerPlanWeeklyPlanDto;
import v620.cc001.base.common.dto.career.CareerUserProfileDto;

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
    private static final String MODE_RULE_FALLBACK = "RULE_FALLBACK";
    private static final String AGENT_STATUS_FALLBACK_READY = "FALLBACK_READY";

    public CareerPlanSummaryDto summarize(CareerPlanRecordDto plan, LocalDateTime now) {
        if (plan == null) {
            CareerPlanSummaryDto missing = new CareerPlanSummaryDto();
            missing.setHasPlan(Boolean.FALSE);
            missing.setPlanHealth(CareerPlanHealth.MISSING);
            missing.setAdjustmentReason("尚未生成长期求职计划。");
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
        CareerPlanSummaryDto summary = new CareerPlanSummaryDto();
        summary.setHasPlan(Boolean.TRUE);
        summary.setTargetRole(plan.getTargetRole());
        summary.setStartStateSummary(plan.getStartStateSummary());
        summary.setPlanningMode(plan.getPlanningMode());
        summary.setHorizonYears(plan.getHorizonYears());
        summary.setAgentStatus(plan.getAgentStatus());
        summary.setModelUsed(plan.getModelUsed());
        summary.setPlanHealth(planHealth(plan, weeklyFocus, now == null ? LocalDateTime.now() : now));
        summary.setAdjustmentReason(planAdjustmentReason(plan, weeklyFocus, now == null ? LocalDateTime.now() : now));
        summary.setNextMilestoneHorizon(firstMilestone == null ? "" : value(firstMilestone.getHorizon()));
        summary.setNextMilestoneTitle(firstMilestone == null ? "" : value(firstMilestone.getTitle()));
        summary.setPhases(copyPhases(plan.getPhases()));
        summary.setWeeklyPlan(plan.getWeeklyPlan());
        summary.setDailySuggestions(clean(plan.getDailySuggestions()));
        summary.setWeeklyFocus(weeklyFocus);
        summary.setGeneratedAt(plan.getGeneratedAt() == null ? null : plan.getGeneratedAt().toString());
        summary.setLastUpdatedAt(plan.getLastUpdatedAt() == null ? null : plan.getLastUpdatedAt().toString());
        summary.setVersion(plan.getVersion());
        return summary;
    }

    public CareerPlanRecordDto defaultPlan(String userId, String targetRole, LocalDateTime now) {
        return defaultPlan(userId, targetRole, null, now);
    }

    public CareerPlanRecordDto defaultPlan(String userId, String targetRole, CareerUserProfileDto profile, LocalDateTime now) {
        LocalDateTime timestamp = now == null ? LocalDateTime.now() : now;
        String target = hasText(targetRole) ? targetRole.trim() : DEFAULT_TARGET_ROLE;
        CareerPlanRecordDto plan = new CareerPlanRecordDto();
        plan.setUserId(userId);
        plan.setTargetRole(target);
        plan.setStartStateSummary(startStateSummary(target, profile));
        plan.setPlanningMode(MODE_RULE_FALLBACK);
        plan.setHorizonYears(Integer.valueOf(3));
        plan.setAgentStatus(AGENT_STATUS_FALLBACK_READY);
        plan.setPhases(defaultPhases(target));
        plan.setWeeklyPlan(defaultWeeklyPlan(target));
        plan.setDailySuggestions(defaultDailySuggestions(target));
        plan.setMilestones(defaultMilestones(target));
        plan.setWeeklyFocus(defaultWeeklyFocus(target));
        plan.setModelUsed("rule-fallback");
        plan.setTokensConsumed(Integer.valueOf(0));
        plan.setGeneratedAt(timestamp);
        plan.setLastUpdatedAt(timestamp);
        plan.setVersion(Integer.valueOf(CURRENT_PLAN_VERSION_BASELINE));
        return plan;
    }

    public CareerPlanRecordDto enrichStructuredPlan(CareerPlanRecordDto plan, CareerUserProfileDto profile, LocalDateTime now) {
        if (plan == null) {
            return null;
        }
        String target = hasText(plan.getTargetRole()) ? plan.getTargetRole().trim() : DEFAULT_TARGET_ROLE;
        if (plan.getPhases() == null || plan.getPhases().isEmpty()) {
            plan.setPhases(defaultPhases(target));
        }
        if (plan.getWeeklyPlan() == null) {
            plan.setWeeklyPlan(defaultWeeklyPlan(target));
        }
        if (plan.getDailySuggestions() == null || plan.getDailySuggestions().isEmpty()) {
            plan.setDailySuggestions(defaultDailySuggestions(target));
        }
        if (plan.getWeeklyFocus() == null || plan.getWeeklyFocus().isEmpty()) {
            plan.setWeeklyFocus(defaultWeeklyFocus(target));
        }
        if (!hasText(plan.getStartStateSummary())) {
            plan.setStartStateSummary(startStateSummary(target, profile));
        }
        if (!hasText(plan.getPlanningMode())) {
            plan.setPlanningMode(MODE_RULE_FALLBACK);
        }
        if (plan.getHorizonYears() == null) {
            plan.setHorizonYears(Integer.valueOf(3));
        }
        if (!hasText(plan.getAgentStatus())) {
            plan.setAgentStatus(AGENT_STATUS_FALLBACK_READY);
        }
        if (!hasText(plan.getModelUsed())) {
            plan.setModelUsed("rule-fallback");
        }
        if (plan.getVersion() == null || plan.getVersion().intValue() < CURRENT_PLAN_VERSION_BASELINE) {
            plan.setVersion(Integer.valueOf(CURRENT_PLAN_VERSION_BASELINE));
        }
        if (plan.getLastUpdatedAt() == null) {
            plan.setLastUpdatedAt(now == null ? LocalDateTime.now() : now);
        }
        if (plan.getGeneratedAt() == null) {
            plan.setGeneratedAt(now == null ? LocalDateTime.now() : now);
        }
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
        milestones.add(milestone("1m", "完成方向定位与求职材料准备",
                Arrays.asList("拆解" + targetRole + "岗位要求", "完成简历和项目证据清单"),
                Arrays.asList("形成 1 版可投递简历")));
        milestones.add(milestone("3m", "补齐核心能力并完成可展示项目",
                Arrays.asList("系统学习目标岗位核心技能", "完成 1-2 个可复盘项目"),
                Arrays.asList("沉淀项目文档和面试讲述稿")));
        milestones.add(milestone("1y", "完成投递、面试和岗位机会转化",
                Arrays.asList("精心准备简历和作品集", "持续投递目标公司并复盘面试"),
                Arrays.asList("拿到至少 1 个满意 offer 或实习机会")));
        milestones.add(milestone("3y", "成长为稳定独立的中级人才",
                Arrays.asList("深耕专业方向", "建立职业人脉"),
                Arrays.asList("能独立负责核心模块或业务专题")));
        return milestones;
    }

    private List<CareerPlanPhaseDto> defaultPhases(String targetRole) {
        List<CareerPlanPhaseDto> phases = new ArrayList<CareerPlanPhaseDto>();
        phases.add(phase("phase-1", "0-1个月", "定位与材料准备",
                "明确" + targetRole + "的岗位画像，完成可投递简历和项目证据清单。",
                "把目标岗位拆成能力关键词、项目证据和投递清单。",
                Arrays.asList("岗位关键词分析", "简历表达", "项目证据整理"),
                Arrays.asList("选定 10 个目标岗位样本", "完成 1 版简历", "准备 2 段项目 STAR 讲述"),
                Arrays.asList("岗位关键词清单", "可投递简历", "项目证据清单"),
                Arrays.asList(
                        subStage("第 1 周", "岗位画像拆解", "完成岗位关键词、能力要求和公司类型梳理",
                                Arrays.asList("收集 10 个目标岗位 JD", "标注高频技能和经验要求"),
                                Arrays.asList("岗位关键词表", "目标公司清单"),
                                Arrays.asList("每天分析 2 个 JD", "每天补充 1 条项目证据")),
                        subStage("第 2-4 周", "材料成型", "让简历、作品和面试素材可以支撑目标岗位",
                                Arrays.asList("重写简历要点", "完成项目复盘稿", "安排一次模拟面试"),
                                Arrays.asList("简历版本 1.0", "项目 STAR 讲述稿"),
                                Arrays.asList("每天优化 1 组简历 bullet", "每天练习 1 个项目问题")))));
        phases.add(phase("phase-2", "1-3个月", "能力补齐与项目验证",
                "围绕" + targetRole + "补齐核心技能，完成可以被简历和面试引用的项目成果。",
                "用项目和练习证明能力，而不是只停留在课程学习。",
                Arrays.asList("核心技能学习", "项目实现", "复盘表达"),
                Arrays.asList("完成核心技能学习计划", "交付 1-2 个项目", "形成项目复盘文档"),
                Arrays.asList("项目仓库或作品链接", "项目复盘文档", "技能短板清单"),
                Arrays.asList(
                        subStage("第 5-8 周", "核心技能集中训练", "完成目标岗位必备技能的基础闭环",
                                Arrays.asList("拆分技能树", "完成每周小练习", "记录问题和复盘"),
                                Arrays.asList("技能学习记录", "练习成果"),
                                Arrays.asList("每天 60-90 分钟技能练习", "每天记录 1 个可讲述问题")),
                        subStage("第 9-12 周", "项目交付与包装", "形成能支撑投递和面试的项目资产",
                                Arrays.asList("完成项目功能", "补充 README 或作品说明", "准备项目面试问答"),
                                Arrays.asList("项目成果", "面试问答卡片"),
                                Arrays.asList("每天推进 1 个项目任务", "每天复盘 1 个技术选择")))));
        phases.add(phase("phase-3", "3-12个月", "投递面试与机会转化",
                "建立稳定投递节奏，通过面试复盘提升 offer 转化率。",
                "把每次投递和面试都变成下一次优化的输入。",
                Arrays.asList("投递策略", "面试表达", "复盘迭代"),
                Arrays.asList("每周投递目标岗位", "每周完成面试题或模拟面试", "根据反馈更新简历"),
                Arrays.asList("稳定投递节奏", "面试复盘记录", "offer 或实习机会"),
                Arrays.asList(
                        subStage("每周循环", "投递与复盘", "持续增加有效机会并修正表达",
                                Arrays.asList("投递 5-10 个目标岗位", "复盘反馈并更新材料", "练习 2 个高频面试题"),
                                Arrays.asList("投递记录", "面试复盘", "更新后的简历"),
                                Arrays.asList("每天查看岗位并投递", "每天练习 1 个面试问题")))));
        phases.add(phase("phase-4", "1-3年", "岗位成长与职业竞争力",
                "从入门执行成长为能独立负责模块或专题的人才。",
                "围绕业务理解、工程质量和协作影响力建立长期竞争力。",
                Arrays.asList("业务理解", "独立交付", "协作沟通"),
                Arrays.asList("沉淀工作方法", "承担更完整的模块", "建立行业信息来源"),
                Arrays.asList("独立负责模块", "形成个人作品或方法论", "明确下一阶段晋升方向"),
                Arrays.asList(
                        subStage("每季度", "能力复盘", "复盘成长曲线并调整下一季度目标",
                                Arrays.asList("复盘项目贡献", "更新技能地图", "确认下一阶段重点"),
                                Arrays.asList("季度复盘", "技能提升计划"),
                                Arrays.asList("每天记录关键工作", "每周沉淀 1 条方法论")))));
        return phases;
    }

    private CareerPlanPhaseDto phase(String phaseId, String horizon, String title, String goal, String description,
                                     List<String> skills, List<String> actions, List<String> kpis,
                                     List<CareerPlanSubStageDto> subStages) {
        CareerPlanPhaseDto phase = new CareerPlanPhaseDto();
        phase.setPhaseId(phaseId);
        phase.setHorizon(horizon);
        phase.setTitle(title);
        phase.setGoal(goal);
        phase.setDescription(description);
        phase.setStatus("待推进");
        phase.setSkills(skills);
        phase.setActions(actions);
        phase.setKpis(kpis);
        phase.setSubStages(subStages);
        return phase;
    }

    private CareerPlanSubStageDto subStage(String period, String title, String goal,
                                           List<String> actions, List<String> deliverables,
                                           List<String> dailySuggestions) {
        CareerPlanSubStageDto subStage = new CareerPlanSubStageDto();
        subStage.setPeriod(period);
        subStage.setTitle(title);
        subStage.setGoal(goal);
        subStage.setActions(actions);
        subStage.setDeliverables(deliverables);
        subStage.setDailySuggestions(dailySuggestions);
        return subStage;
    }

    private CareerPlanWeeklyPlanDto defaultWeeklyPlan(String targetRole) {
        CareerPlanWeeklyPlanDto weeklyPlan = new CareerPlanWeeklyPlanDto();
        weeklyPlan.setWeekTitle("本周启动计划");
        weeklyPlan.setWeekGoal("围绕" + targetRole + "完成岗位拆解、简历证据整理和一次面试练习。");
        weeklyPlan.setActions(defaultWeeklyFocus(targetRole));
        weeklyPlan.setDeliverables(Arrays.asList("目标岗位关键词清单", "简历优化清单", "一次模拟面试复盘"));
        weeklyPlan.setDailySuggestions(defaultDailySuggestions(targetRole));
        return weeklyPlan;
    }

    private List<String> defaultWeeklyFocus(String targetRole) {
        return Arrays.asList(
                "完善个人简历，突出与" + targetRole + "相关的核心优势",
                "每天完成一项" + targetRole + "相关练习或岗位研究",
                "研究 1-2 家目标公司的招聘要求");
    }

    private List<String> defaultDailySuggestions(String targetRole) {
        return Arrays.asList(
                "用 30 分钟阅读 1-2 个" + targetRole + "岗位 JD，记录高频能力词。",
                "用 45 分钟优化一组简历经历，补充结果、数据和个人贡献。",
                "用 60 分钟推进一个项目或技能练习，保留可展示产物。",
                "用 20 分钟整理一个面试问题的 STAR 回答。",
                "当天结束前记录今日完成项、卡点和明天第一步。");
    }

    private String startStateSummary(String targetRole, CareerUserProfileDto profile) {
        if (profile == null) {
            return "已根据当前可用信息生成" + targetRole + "路线图；补充画像、简历和面试记录后可进一步细化。";
        }
        String stage = hasText(profile.getCurrentStage()) ? profile.getCurrentStage() : "当前阶段待补充";
        String score = profile.getCompletenessScore() == null ? "画像完整度待评估" : "画像完整度 " + profile.getCompletenessScore() + "%";
        return "当前阶段：" + stage + "；" + score + "；目标岗位：" + targetRole + "。";
    }

    private List<CareerPlanPhaseDto> copyPhases(List<CareerPlanPhaseDto> phases) {
        return phases == null ? new ArrayList<CareerPlanPhaseDto>() : phases;
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
