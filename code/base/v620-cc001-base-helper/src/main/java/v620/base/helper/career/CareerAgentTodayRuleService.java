package v620.base.helper.career;

import v620.cc001.base.common.dto.career.CareerAgentRuleInput;
import v620.cc001.base.common.dto.career.CareerAgentTodayDto;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Migrated pure-Java version of IPD CareerAgentServiceImpl#getToday decision rules.
 */
public class CareerAgentTodayRuleService {

    private static final int RESUME_SCORE_THRESHOLD = 70;
    private static final int INTERVIEW_SCORE_THRESHOLD = 70;
    private static final int MIN_CHECKIN_DAYS_PER_WEEK = 3;

    public CareerAgentTodayDto recommend(CareerAgentRuleInput input) {
        CareerAgentRuleInput safeInput = input == null ? new CareerAgentRuleInput() : input;
        UserProfileSnapshot snapshot = safeInput.getSnapshot() == null ? new UserProfileSnapshot() : safeInput.getSnapshot();
        CareerAgentRuleInput.CheckInStatus checkIn = safeInput.getCheckInStatus() == null
                ? new CareerAgentRuleInput.CheckInStatus()
                : safeInput.getCheckInStatus();

        UserProfileSnapshot.AssessmentBlock assessment = snapshot.getAssessment();
        UserProfileSnapshot.ResumeBlock resume = snapshot.getResume();
        UserProfileSnapshot.InterviewBlock interview = snapshot.getInterview();
        UserProfileSnapshot.PreferencesBlock preferences = snapshot.getPreferences();
        UserProfileSnapshot.OnboardingBlock onboarding = snapshot.getOnboarding();

        String targetRole = firstText(
                preferences != null ? preferences.getTargetRole() : null,
                resume != null ? resume.getTargetJob() : null,
                interview != null ? interview.getPositionName() : null
        );
        String identityType = onboarding != null ? onboarding.getIdentityType() : null;
        String selfReportedResume = onboarding != null ? onboarding.getHasResume() : null;
        boolean selfReportedHasResume = "yes".equalsIgnoreCase(selfReportedResume);

        List<String> risks = new ArrayList<String>();
        List<String> riskKeys = new ArrayList<String>();
        List<CareerAgentTodayDto.Action> actions = new ArrayList<CareerAgentTodayDto.Action>();

        String stage;
        String headline;
        String focus;
        String reason;

        if (!hasText(targetRole)) {
            stage = "TARGET_ROLE_SELECTION";
            headline = "先选择一个目标岗位方向";
            focus = "把求职准备收敛到一个具体岗位或方向";
            reason = "目标岗位还没有设定。先确定方向，后续简历、测评和面试任务才会更准确。";
            risks.add("目标岗位尚未设置");
            riskKeys.add("agent.risk.reason.NO_TARGET_ROLE");
            if (assessment == null) {
                risks.add("测评基线尚未建立");
                riskKeys.add("agent.risk.reason.NO_ASSESSMENT");
            }
            actions.add(action("选择目标岗位", "/pages/map/index", "TARGET_ROLE", "HIGH"));
            actions.add(action("完成职业测评", "/pages/assessment/index", "ASSESSMENT", "MEDIUM"));
        } else if ("career_switcher".equals(identityType)) {
            stage = "CAREER_SWITCH_POSITIONING";
            headline = "整理转岗理由和目标岗位证据";
            focus = "把过往经历改写成「" + targetRole + "」需要的能力证据";
            reason = "你在 onboarding 中选择了转方向。今天最重要的是先解释为什么转、凭什么能转，再进入简历细节。";
            risks.add("转岗叙事尚未沉淀");
            riskKeys.add("agent.risk.reason.PROFILE_MISSING");
            actions.add(action("整理转岗证据", "/pages/assistant/index", "CAREER_SWITCH", "HIGH"));
            actions.add(action("查看职业路线", "/pages/map/index", "LEARNING", "MEDIUM"));
        } else if ("internship_seeker".equals(identityType) && !selfReportedHasResume && resume == null) {
            stage = "INTERNSHIP_RESUME_BOOTSTRAP";
            headline = "建立第一版实习简历素材";
            focus = "先整理课程、项目和校园经历，形成可上传的实习简历草稿";
            reason = "你在 onboarding 中选择了找实习，且还没有可投递简历。今天应先把经历素材整理出来。";
            risks.add("实习简历素材尚未建立");
            riskKeys.add("agent.risk.reason.NO_RESUME");
            actions.add(action("整理实习简历素材", "/pages/resume/index", "RESUME", "HIGH"));
            actions.add(action("完成职业测评", "/pages/assessment/index", "ASSESSMENT", "MEDIUM"));
        } else if ("new_graduate".equals(identityType) && selfReportedHasResume && resume == null) {
            stage = "GRADUATE_RESUME_UPLOAD";
            headline = "上传简历并匹配一个目标 JD";
            focus = "用已有简历完成一次针对「" + targetRole + "」的 AI 诊断";
            reason = "你在 onboarding 中选择了应届/准应届且已有简历。下一步不是继续浏览功能，而是把简历上传并对齐目标岗位。";
            risks.add("已有简历尚未进入系统诊断");
            riskKeys.add("agent.risk.reason.NO_RESUME_HAS_ROLE");
            actions.add(action("上传简历做 JD 匹配", "/pages/resume/index", "RESUME", "HIGH"));
            actions.add(action("打开 AI 简历诊断", "/pages/resume-ai/index", "RESUME", "MEDIUM"));
        } else if (assessment == null) {
            stage = "ASSESSMENT_BASELINE";
            headline = "完成 5 分钟职业测评";
            focus = "为「" + targetRole + "」建立第一份能力和偏好基线";
            reason = "目标岗位已经明确，但还缺少测评画像。先补齐这个基线，后续简历和面试建议会更准。";
            risks.add("测评基线尚未建立");
            riskKeys.add("agent.risk.reason.NO_ASSESSMENT");
            actions.add(action("完成 5 分钟测评", "/pages/assessment/index", "ASSESSMENT", "HIGH"));
            actions.add(action("查看职业路线", "/pages/map/index", "LEARNING", "MEDIUM"));
        } else if (resume == null) {
            stage = "RESUME_BOOTSTRAP";
            headline = selfReportedHasResume ? "上传已有简历并做一次诊断" : "为「" + targetRole + "」准备一份简历";
            focus = selfReportedHasResume ? "把已有简历上传后匹配一个目标 JD" : "创建或上传你的第一份简历草稿";
            reason = selfReportedHasResume
                    ? "你自报已有简历，但系统还没有简历记录。先上传并诊断，准备度才会变成可评估状态。"
                    : "目标方向已明确，但在简历存在之前，我无法评估你的求职准备度。";
            risks.add("目标岗位暂无简历数据");
            riskKeys.add("agent.risk.reason.NO_RESUME");
            actions.add(action(selfReportedHasResume ? "上传简历做 JD 匹配" : "生成简历草稿", selfReportedHasResume ? "/pages/resume/index" : "/pages/resume-ai/index", "RESUME", "HIGH"));
            actions.add(action("打开简历模块", "/pages/resume/index", "RESUME", "MEDIUM"));
        } else if (resume.getDiagnosisScore() != null && resume.getDiagnosisScore().intValue() < RESUME_SCORE_THRESHOLD) {
            stage = "RESUME_IMPROVEMENT";
            headline = "投递前先优化你的简历";
            focus = "将简历诊断分数提升至 70 分以上";
            reason = "目标岗位明确，但当前简历分数偏低，可能影响面试邀约率。";
            risks.add("简历诊断分数低于推荐阈值");
            riskKeys.add("agent.risk.reason.RESUME_LOW_SCORE");
            actions.add(action("优化简历", "/pages/resume-ai/index", "RESUME", "HIGH"));
            actions.add(action("检查简历薄弱项", "/pages/assistant/index", "CHAT", "MEDIUM"));
        } else if (interview == null) {
            stage = "INTERVIEW_BOOTSTRAP";
            headline = "开始「" + targetRole + "」的面试练习";
            focus = "本周完成一次模拟面试";
            reason = "方向和简历已有足够信号。下一步风险是：你能否在面试压力下清晰表达经历。";
            risks.add("暂无模拟面试记录");
            riskKeys.add("agent.risk.reason.NO_INTERVIEW");
            actions.add(action("开始模拟面试", "/pages/interview/start", "INTERVIEW", "HIGH"));
            actions.add(action("用求职教练练习", "/pages/assistant/index", "CHAT", "MEDIUM"));
        } else if (interview.getLastScore() != null && interview.getLastScore().intValue() < INTERVIEW_SCORE_THRESHOLD) {
            stage = "INTERVIEW_IMPROVEMENT";
            headline = "针对薄弱维度专项练习";
            focus = "重点练习最薄弱的面试维度一次";
            reason = "面试分数低于就绪阈值，今天应专注针对性练习，而不是泛泛学习。";
            risks.add("上次面试分数低于推荐阈值");
            riskKeys.add("agent.risk.reason.INTERVIEW_LOW_SCORE");
            if (interview.getWeakDimensions() != null && !interview.getWeakDimensions().isEmpty()) {
                risks.add("薄弱维度：" + join(interview.getWeakDimensions(), "、"));
                riskKeys.add("agent.risk.reason.WEAK_DIMENSIONS");
            }
            actions.add(action("练习面试", "/pages/interview/start", "INTERVIEW", "HIGH"));
            actions.add(action("复盘面试回答", "/pages/assistant/index", "CHAT", "MEDIUM"));
        } else if (checkIn.getWeeklyDays() < MIN_CHECKIN_DAYS_PER_WEEK) {
            stage = "EXECUTION_RHYTHM";
            headline = "重建你的每周执行节奏";
            focus = "今天完成一项核心职业行动";
            reason = "你的档案可用，但近期行动连续性偏低。现在更适合先完成一个小任务，而不是继续增加计划。";
            risks.add("本周打卡天数不足 3 天");
            riskKeys.add("agent.risk.reason.LOW_CHECKIN");
            actions.add(action("查看打卡计划", "/pages/checkin/index", "CHECKIN", "HIGH"));
            actions.add(action("更新今日行动", "/pages/agent/index", "PLAN", "MEDIUM"));
        } else {
            stage = "CAREER_MOMENTUM";
            headline = "保持冲向「" + targetRole + "」的势头";
            focus = "完成一项专项提升任务并保持连续打卡";
            reason = "方向、简历和面试信号均已具备。最佳下一步是持续改进和跟踪进展。";
            actions.add(action("复盘求职计划", "/pages/assistant/index", "CHAT", "MEDIUM"));
            actions.add(action("打开求职路线", "/pages/map/index", "LEARNING", "MEDIUM"));
        }

        if (checkIn.getTodayCompleted() < checkIn.getTodayTotal()) {
            risks.add("今日核心任务尚未完成");
            riskKeys.add("agent.risk.reason.TODAY_INCOMPLETE");
        }
        appendWeeklyFocusActions(safeInput.getWeeklyFocusItems(), actions);
        if (safeInput.getWeeklyFocusItems() != null && !safeInput.getWeeklyFocusItems().isEmpty()) {
            reason = reason + " 已找到你的长期规划，今日任务中已加入本周重点行动。";
        }

        CareerAgentTodayDto result = new CareerAgentTodayDto();
        result.setStage(stage);
        result.setRiskLevel(riskLevel(risks.size()));
        result.setHeadline(headline);
        result.setHeadlineKey("agent.today." + stage + ".headline");
        result.setReason(personalize(reason, safeInput));
        result.setReasonKey("agent.today." + stage + ".reason");
        result.setTodayFocus(focus);
        result.setFocusKey("agent.today." + stage + ".focus");
        result.setProgressPercent(Integer.valueOf(progress(assessment, resume, interview, targetRole, checkIn)));
        result.setRiskReasons(risks);
        result.setRiskReasonKeys(riskKeys);
        result.setActions(actions);
        return result;
    }

    private void appendWeeklyFocusActions(List<String> weeklyFocusItems, List<CareerAgentTodayDto.Action> actions) {
        if (weeklyFocusItems == null) {
            return;
        }
        int count = 0;
        for (String item : weeklyFocusItems) {
            if (!hasText(item)) {
                continue;
            }
            CareerAgentTodayDto.Action action = new CareerAgentTodayDto.Action();
            action.setLabel(shortTaskLabel(item));
            action.setLabelKey("agent.action.PLAN");
            action.setTarget("/pages/map/index?tab=plan");
            action.setType("PLAN");
            action.setPriority("HIGH");
            action.setSource("PLAN_WEEKLY");
            actions.add(action);
            count++;
            if (count >= 2) {
                break;
            }
        }
    }

    private int progress(UserProfileSnapshot.AssessmentBlock assessment,
                         UserProfileSnapshot.ResumeBlock resume,
                         UserProfileSnapshot.InterviewBlock interview,
                         String targetRole,
                         CareerAgentRuleInput.CheckInStatus checkIn) {
        int score = 0;
        if (assessment != null) score += 20;
        if (hasText(targetRole)) score += 20;
        if (resume != null) score += 25;
        if (interview != null) score += 25;
        if (checkIn.getWeeklyDays() >= 3) score += 10;
        return Math.min(score, 100);
    }

    private CareerAgentTodayDto.Action action(String label, String target, String type, String priority) {
        CareerAgentTodayDto.Action action = new CareerAgentTodayDto.Action();
        action.setLabel(label);
        action.setLabelKey("agent.action." + type);
        action.setTarget(target);
        action.setType(type);
        action.setPriority(priority);
        action.setSource("DAILY_AGENT");
        return action;
    }

    private String riskLevel(int riskCount) {
        if (riskCount >= 3) return "HIGH";
        if (riskCount >= 1) return "MEDIUM";
        return "LOW";
    }

    private String personalize(String reason, CareerAgentRuleInput input) {
        List<String> hints = new ArrayList<String>();
        if (hasText(input.getUserMajor())) hints.add("专业：" + input.getUserMajor());
        if (hasText(input.getUserSchool())) hints.add("学校：" + input.getUserSchool());
        if (hints.isEmpty()) return reason;
        return reason + "（" + join(hints, "，") + "）";
    }

    private String shortTaskLabel(String text) {
        String value = text == null ? "" : text.trim();
        if (value.length() <= 24) return value;
        return value.substring(0, 24) + "...";
    }

    private String firstText(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (hasText(value)) {
                return value;
            }
        }
        return null;
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }

    private String join(List<String> values, String delimiter) {
        StringBuilder builder = new StringBuilder();
        for (String value : values) {
            if (builder.length() > 0) {
                builder.append(delimiter);
            }
            builder.append(value);
        }
        return builder.toString();
    }
}
