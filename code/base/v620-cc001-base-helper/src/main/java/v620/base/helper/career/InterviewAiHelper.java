package v620.base.helper.career;

import v620.cc001.base.common.dto.career.InterviewAdviceItemDto;
import v620.cc001.base.common.dto.career.InterviewRadarScoreDto;
import v620.cc001.base.common.dto.career.InterviewReportDto;
import v620.cc001.base.common.dto.career.InterviewSessionDto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Pure Java prompt builder for text mock interviews. */
public class InterviewAiHelper {
    private static final int MAX_CONTEXT = 6000;
    private static final int MAX_TRANSCRIPT = 12000;

    public String questionPrompt(String position, String difficulty, String resumeText,
                                 String profileSummary, String transcript, boolean opening) {
        return "你是一名耐心、专业的中文面试官。只输出一道面试问题，不要点评，不要编号。"
                + (opening ? "这是开场问题，先让候选人介绍与岗位最相关的经历。" : "根据上一回答自然追问，不要重复已经问过的问题。")
                + "\n目标岗位：" + safe(position, "待确认岗位")
                + "\n练习难度：" + safe(difficulty, "常规")
                + "\n简历摘要：" + limit(resumeText, MAX_CONTEXT)
                + "\n用户情况：" + limit(profileSummary, 2000)
                + "\n已有面试记录：" + limit(transcript, MAX_TRANSCRIPT);
    }

    public String temporaryQuestion(String position, String difficulty, int answerCount, boolean opening) {
        String role = safe(position, "目标岗位");
        String level = difficultyLabel(difficulty);
        if (opening || answerCount <= 0) {
            return "【基础练习题】请用两分钟介绍一下自己，并重点说明你与" + role + "最相关的一段经历。";
        }
        String[] questions = new String[] {
                "请选择一个最能体现你能力的项目，说明你的具体职责、采取的行动和最终结果。",
                "请描述一次你遇到困难或计划变化的经历，你是如何分析并解决问题的？",
                "请举例说明你与他人意见不一致时，如何沟通并推动事情继续进行。",
                "结合" + role + "，请说明你掌握的一项关键能力，以及你在实际任务中如何使用它。",
                "请描述一次时间紧或压力较大的任务，你如何安排优先级并保证交付质量？",
                "回顾前面的回答，你认为自己胜任" + role + "的主要优势是什么，还有哪一点需要继续提升？"
        };
        int index = Math.max(0, Math.min(questions.length - 1, answerCount - 1));
        return "【基础练习题·" + level + "】" + questions[index];
    }

    public InterviewReportDto basicRulesReport(InterviewSessionDto session, String transcript, int answerCount) {
        int safeAnswers = Math.max(1, answerCount);
        int detailBonus = Math.min(8, safe(transcript, "").length() / 500);
        int overall = Math.min(84, 55 + Math.min(21, safeAnswers * 3) + detailBonus);

        InterviewRadarScoreDto radar = new InterviewRadarScoreDto();
        radar.setExpression(Integer.valueOf(overall));
        radar.setLogic(Integer.valueOf(clamp(overall - 2)));
        radar.setTechnical(Integer.valueOf(clamp(overall - 4)));
        radar.setPressureResistance(Integer.valueOf(clamp(overall - 3)));
        radar.setCommunication(Integer.valueOf(clamp(overall - 1)));

        Map<String, String> reasons = new LinkedHashMap<String, String>();
        reasons.put("expression", "基础规则仅根据已保存回答数量和文字完整度估算表达完成度。");
        reasons.put("logic", "基础规则无法理解回答语义，当前分数只反映回答是否持续完成。");
        reasons.put("technical", "基础规则无法判断专业内容是否正确，建议后续结合岗位要求人工复盘。");
        reasons.put("pressureResistance", "基础规则未采集真实压力表现，当前仅记录本次练习完成情况。");
        reasons.put("communication", "基础规则只检查文字回答是否形成，不代表真实沟通能力评价。");

        List<InterviewAdviceItemDto> strengths = new ArrayList<InterviewAdviceItemDto>();
        strengths.add(advice("完成了有效练习", "本次保存了 " + safeAnswers + " 条有效回答，可用于回看自己的表达结构。"));
        List<InterviewAdviceItemDto> improvements = new ArrayList<InterviewAdviceItemDto>();
        improvements.add(advice("补充具体证据", "回看每条回答，补充你做了什么、为什么这样做以及产生了什么结果。"));
        improvements.add(advice("结合岗位要求复盘", "当前为基础规则复盘，建议对照目标岗位要求检查专业内容和案例匹配度。"));

        InterviewReportDto report = new InterviewReportDto();
        report.setAnalysisSource(InterviewReportDto.ANALYSIS_SOURCE_BASIC_RULES);
        report.setOverallScore(Integer.valueOf(overall));
        report.setTotalQuestions(Integer.valueOf(safeAnswers));
        report.setRadarScore(radar);
        report.setScoreReasons(reasons);
        report.setStrengths(strengths);
        report.setImprovements(improvements);
        report.setTextSummary("本结果为基础规则复盘，不是 AI 深度分析。系统仅依据已保存的 "
                + safeAnswers + " 条文字回答和完成度生成，用于保证面试练习可以正常结束。"
                + (session != null && session.getPositionName() != null
                ? "目标岗位：" + session.getPositionName() + "。" : ""));
        return report;
    }

    private InterviewAdviceItemDto advice(String title, String detail) {
        InterviewAdviceItemDto item = new InterviewAdviceItemDto();
        item.setTitle(title);
        item.setDetail(detail);
        return item;
    }

    private String difficultyLabel(String difficulty) {
        if ("Easy".equalsIgnoreCase(safe(difficulty, ""))) return "入门";
        if ("Hard".equalsIgnoreCase(safe(difficulty, ""))) return "进阶";
        return "常规";
    }

    private int clamp(int score) { return Math.max(0, Math.min(100, score)); }

    private String safe(String value, String fallback) { return value == null || value.trim().length() == 0 ? fallback : value.trim(); }
    private String limit(String value, int max) { String safe = value == null ? "" : value.trim(); return safe.length() <= max ? safe : safe.substring(0, max); }
}
