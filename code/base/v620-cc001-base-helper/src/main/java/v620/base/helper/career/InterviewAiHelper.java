package v620.base.helper.career;

import v620.cc001.base.common.dto.career.InterviewAdviceItemDto;
import v620.cc001.base.common.dto.career.InterviewRadarScoreDto;
import v620.cc001.base.common.dto.career.InterviewReportDto;
import v620.cc001.base.common.dto.career.InterviewSessionDto;

import java.util.ArrayList;
import java.util.List;

/** Pure Java prompt and fallback rules for text mock interviews. */
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

    public String reportPrompt(InterviewSessionDto session, String transcript, int answerCount) {
        return "请只根据面试记录生成严格 JSON，不要 Markdown。所有文字使用简体中文。"
                + "字段为 overallScore,totalQuestions,radarScore(expression,logic,technical,pressureResistance,communication),"
                + "strengths(title,detail 数组),improvements(title,detail 数组),textSummary。分数必须为0到100。"
                + "每条建议引用候选人的实际表达，不得编造经历。"
                + "\n目标岗位：" + safe(session == null ? null : session.getPositionName(), "待确认岗位")
                + "\n有效回答数：" + answerCount + "\n面试记录：" + limit(transcript, MAX_TRANSCRIPT);
    }

    public String fallbackQuestion(String position, int answerCount) {
        String role = safe(position, "目标岗位");
        if (answerCount <= 0) return "请介绍一段最能说明你适合“" + role + "”的学习、项目或实践经历。";
        if (answerCount == 1) return "在刚才的经历中，你遇到的最大困难是什么？你具体采取了哪些行动？";
        if (answerCount == 2) return "如果重新处理刚才的问题，你会做出哪些调整？为什么？";
        return "请结合“" + role + "”的实际工作，说明你接下来最需要提升的一项能力和具体计划。";
    }

    public InterviewReportDto fallbackReport(InterviewSessionDto session, int answerCount) {
        int score = Math.min(78, 52 + Math.max(1, answerCount) * 6);
        InterviewReportDto report = new InterviewReportDto();
        report.setOverallScore(Integer.valueOf(score));
        report.setTotalQuestions(Integer.valueOf(answerCount));
        InterviewRadarScoreDto radar = new InterviewRadarScoreDto();
        radar.setExpression(Integer.valueOf(score));
        radar.setLogic(Integer.valueOf(Math.max(0, score - 2)));
        radar.setTechnical(Integer.valueOf(Math.max(0, score - 4)));
        radar.setPressureResistance(Integer.valueOf(score));
        radar.setCommunication(Integer.valueOf(Math.min(100, score + 2)));
        report.setRadarScore(radar);
        List<InterviewAdviceItemDto> strengths = new ArrayList<InterviewAdviceItemDto>();
        strengths.add(advice("完成了有效练习", "你围绕问题完成了 " + answerCount + " 次回答，已经形成可复盘的表达材料。"));
        report.setStrengths(strengths);
        List<InterviewAdviceItemDto> improvements = new ArrayList<InterviewAdviceItemDto>();
        improvements.add(advice("补充具体证据", "回答时说明当时的情境、你的具体行动和最终结果，会更容易让面试官理解你的贡献。"));
        report.setImprovements(improvements);
        report.setTextSummary("这是根据本次练习记录生成的基础复盘。建议结合目标岗位继续补充量化结果和个人贡献，再完成一次针对性练习。AI 服务可用后可获得更细致的逐项分析。");
        return report;
    }

    public int clampScore(int value) { return Math.max(0, Math.min(100, value)); }

    private InterviewAdviceItemDto advice(String title, String detail) {
        InterviewAdviceItemDto item = new InterviewAdviceItemDto();
        item.setTitle(title); item.setDetail(detail); return item;
    }
    private String safe(String value, String fallback) { return value == null || value.trim().length() == 0 ? fallback : value.trim(); }
    private String limit(String value, int max) { String safe = value == null ? "" : value.trim(); return safe.length() <= max ? safe : safe.substring(0, max); }
}
