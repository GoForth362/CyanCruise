package v620.base.helper.furtherstudy;

import v620.cc001.base.common.dto.furtherstudy.StudyAbroadChecklistItemDto;
import v620.cc001.base.common.dto.furtherstudy.StudyAbroadConstants;
import v620.cc001.base.common.dto.furtherstudy.StudyAbroadLanguagePlanRequest;
import v620.cc001.base.common.dto.furtherstudy.StudyAbroadLanguagePlanResult;
import v620.cc001.base.common.dto.furtherstudy.StudyAbroadProfileDiagnosisResult;
import v620.cc001.base.common.dto.furtherstudy.StudyAbroadProfileRequest;
import v620.cc001.base.common.dto.furtherstudy.StudyAbroadSchoolOptionDto;
import v620.cc001.base.common.dto.furtherstudy.StudyAbroadSchoolPositionRequest;
import v620.cc001.base.common.dto.furtherstudy.StudyAbroadSchoolPositionResult;
import v620.cc001.base.common.dto.furtherstudy.StudyAbroadStatementOutlineResult;
import v620.cc001.base.common.dto.furtherstudy.StudyAbroadStatementRequest;
import v620.cc001.base.common.dto.furtherstudy.StudyAbroadVisaChecklistRequest;
import v620.cc001.base.common.dto.furtherstudy.StudyAbroadVisaChecklistResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/** Pure Java rule fallback for study abroad companion. */
public class StudyAbroadCompanionService {

    public StudyAbroadProfileDiagnosisResult diagnoseProfile(StudyAbroadProfileRequest request) {
        StudyAbroadProfileRequest safe = request == null ? new StudyAbroadProfileRequest() : request;
        int academic = academicScore(safe.getGpa(), 34);
        int language = languageScore(safe.getLanguageScore(), 24);
        int background = evidenceScore(join(safe.getBackground(), safe.getPreference()), 22,
                "科研", "论文", "竞赛", "实习", "项目", "交换", "志愿");
        int clarity = clarityScore(safe.getCountryOrRegion(), safe.getTargetDegree(), safe.getTargetMajor(), safe.getBudget());
        int total = clamp(academic + language + background + clarity, 0, 100);

        StudyAbroadProfileDiagnosisResult result = new StudyAbroadProfileDiagnosisResult();
        result.setStatus(StudyAbroadConstants.STATUS_OK);
        result.setReadinessScore(Integer.valueOf(total));
        result.setSummary(total >= 78
                ? "你的留学申请准备度较高，当前重点是把院校清单、文书主线和申请批次尽快落成表格。"
                : total >= 60
                ? "你的申请基础已经具备，但语言成绩、经历证据或目标定位仍需要补强，建议先做三档选校和八周行动计划。"
                : "当前信息还偏早期，建议先明确国家地区、预算、目标学位和语言考试，再推进文书与网申。");
        if (academic >= 25) result.getStrengths().add("成绩基础可以支撑主申请，需要把课程优势和目标专业关系讲清楚。");
        else result.getGaps().add("成绩或绩点信息还不够强，需要用课程解释、项目经历和更稳妥的院校梯度降低风险。");
        if (language >= 18) result.getStrengths().add("语言成绩接近可投递状态，可以开始同步准备文书和网申材料。");
        else result.getGaps().add("语言考试仍是关键卡点，建议先锁定考试日期并按听说读写拆分训练。");
        if (background >= 15) result.getStrengths().add("软实力经历有可展开空间，适合提炼成个人陈述中的主线证据。");
        else result.getGaps().add("科研、实习、项目或竞赛证据偏少，需要补一段能说明学习动机和专业能力的经历。");
        result.getNextActions().add(item("目标定位", "建立国家地区和预算边界", "确认目标国家/地区、学位类型、预算上限、是否接受一年制或两年制项目。", StudyAbroadConstants.PRIORITY_HIGH));
        result.getNextActions().add(item("语言考试", "倒排一次正式考试", "把目标分数拆到单项，至少预留一次补考窗口。", StudyAbroadConstants.PRIORITY_HIGH));
        result.getNextActions().add(item("文书主线", "梳理个人故事和专业匹配", "用一个真实问题、一次关键经历和未来目标串起个人陈述。", StudyAbroadConstants.PRIORITY_MEDIUM));
        result.getReminders().add("当前为规则版建议，不会伪装成真实 AI 批改；接入 AI provider 后可替换为多轮对话和写作反馈。");
        result.getReminders().add("院校要求以官网最新项目页、语言政策、签证政策和申请系统为准。");
        return result;
    }

    public StudyAbroadLanguagePlanResult generateLanguagePlan(StudyAbroadLanguagePlanRequest request) {
        StudyAbroadLanguagePlanRequest safe = request == null ? new StudyAbroadLanguagePlanRequest() : request;
        String exam = firstText(safe.getExamType(), "雅思/托福/GRE");
        String current = firstText(safe.getCurrentScore(), "当前成绩未填写");
        String target = firstText(safe.getTargetScore(), "目标分数未填写");
        StudyAbroadLanguagePlanResult result = new StudyAbroadLanguagePlanResult();
        result.setStatus(StudyAbroadConstants.STATUS_OK);
        result.setSummary("围绕" + exam + "从 " + current + " 提升到 " + target + "，建议按诊断、专项、模考三轮推进。");
        result.getRounds().add(item("第 1 轮", "诊断与基础修复", "完成一次完整模考，定位薄弱单项；每天保留词汇、长难句或口语素材输入。", StudyAbroadConstants.PRIORITY_HIGH));
        result.getRounds().add(item("第 2 轮", "单项专项训练", "写作按观点、结构、论证、语言四类批改；口语按题库、追问和复述训练。", StudyAbroadConstants.PRIORITY_HIGH));
        result.getRounds().add(item("第 3 轮", "限时模考与复盘", "每周至少一次限时套题，记录失分原因和下一周专项任务。", StudyAbroadConstants.PRIORITY_MEDIUM));
        result.getWeeklyRoutine().addAll(list("2 次阅读或听力限时训练", "2 篇写作精改或口语录音复盘", "1 次完整模考或半套题复盘", "整理一页错因清单"));
        result.getExaminerTips().add("当前版本不会进行真实 AI 口语对练；可先用录音文本输入，系统按规则给出训练方向。");
        result.getExaminerTips().add(hasText(safe.getWeakParts()) ? "重点薄弱项：" + safe.getWeakParts() : "如果填写薄弱单项，计划会更贴近真实备考节奏。");
        return result;
    }

    public StudyAbroadSchoolPositionResult positionSchools(StudyAbroadSchoolPositionRequest request) {
        StudyAbroadSchoolPositionRequest safe = request == null ? new StudyAbroadSchoolPositionRequest() : request;
        String country = firstText(safe.getCountryOrRegion(), "目标地区");
        String major = firstText(safe.getTargetMajor(), "目标专业");
        StudyAbroadSchoolPositionResult result = new StudyAbroadSchoolPositionResult();
        result.setStatus(StudyAbroadConstants.STATUS_OK);
        result.setSummary("建议把 " + country + " 的 " + major + " 项目拆成冲刺、匹配、稳妥三档，每档保留 2-4 个项目。");
        result.getOptions().add(option("冲刺", country + "高选择性项目", major, "用于拉高上限，适合语言和文书都能在截止前打磨到位的项目。",
                list("补充教授方向匹配证据", "准备更强的项目经历段落", "提前确认推荐信质量")));
        result.getOptions().add(option("匹配", country + "主申请项目", major, "和当前成绩、语言、背景最接近，应作为主要投递池。",
                list("逐项核对先修课", "准备项目定制版个人陈述", "记录申请截止日期")));
        result.getOptions().add(option("稳妥", country + "风险兜底项目", major, "用于降低语言、预算或申请季竞争带来的不确定性。",
                list("确认是否接受后补语言", "核对奖学金和总费用", "保留一套完整网申材料")));
        result.getCautions().add("这里不使用实时排名和录取数据，具体项目必须以院校官网、申请系统和最新录取要求为准。");
        result.getCautions().add("预算、学制、签证时长和毕业后就业政策会显著影响最终选择。");
        return result;
    }

    public StudyAbroadStatementOutlineResult buildStatementOutline(StudyAbroadStatementRequest request) {
        StudyAbroadStatementRequest safe = request == null ? new StudyAbroadStatementRequest() : request;
        String major = firstText(safe.getTargetMajor(), "目标专业");
        String topic = firstText(safe.getProfessorTopic(), "目标课题方向");
        String story = firstText(safe.getPersonalStory(), "一次真实经历");
        String academic = firstText(safe.getAcademicExperience(), "课程、项目或科研经历");
        String goal = firstText(safe.getCareerGoal(), "未来目标");
        StudyAbroadStatementOutlineResult result = new StudyAbroadStatementOutlineResult();
        result.setStatus(hasText(safe.getPersonalStory()) && hasText(safe.getProfessorTopic())
                ? StudyAbroadConstants.STATUS_OK : StudyAbroadConstants.STATUS_NEED_MORE_INFO);
        result.setGoldenLine("我希望申请 " + major + "，不是为了泛泛追求名校，而是因为" + story + "让我持续关注"
                + topic + "，并希望把" + academic + "沉淀成面向" + goal + "的长期能力。");
        result.setOutline("1. 开头：用一个具体问题或场景引出申请动机。\n"
                + "2. 学术准备：说明你学过什么、做过什么、如何证明适合 " + major + "。\n"
                + "3. 项目匹配：连接教授课题、课程设置或实验室方向：" + topic + "。\n"
                + "4. 未来计划：把短期学习计划和长期目标连起来：" + goal + "。\n"
                + "5. 结尾：强调你能为项目带来的视角、方法和持续投入。");
        result.getStoryQuestions().addAll(list("这段经历里你最想解决的具体问题是什么？", "你采取过哪些行动，结果有没有可验证证据？", "为什么这个目标项目比其他项目更适合你？"));
        if (!hasText(safe.getPersonalStory())) result.getMissingInfo().add("请补充一个真实个人故事，避免个人陈述变成简历复述。");
        if (!hasText(safe.getProfessorTopic())) result.getMissingInfo().add("请补充目标教授、课程或课题方向，方便建立项目匹配。");
        result.getWritingTips().add("文书中可以解释个人动机，但不要把篇幅都放在情绪表达上，要落到学术准备和项目匹配。");
        result.getWritingTips().add("当前为规则版提纲，不会引用真实论文；接入 AI 或检索后再生成定制套磁段落。");
        return result;
    }

    public StudyAbroadVisaChecklistResult buildVisaChecklist(StudyAbroadVisaChecklistRequest request) {
        StudyAbroadVisaChecklistRequest safe = request == null ? new StudyAbroadVisaChecklistRequest() : request;
        String country = firstText(safe.getCountryOrRegion(), "目标国家/地区");
        StudyAbroadVisaChecklistResult result = new StudyAbroadVisaChecklistResult();
        result.setStatus(StudyAbroadConstants.STATUS_OK);
        result.setSummary("围绕 " + country + " 申请季，建议把网申、录取确认、签证和行前材料分开管理。");
        result.getChecklist().add(item("网申", "核对账号、项目和截止日期", "每个项目记录申请系统、截止日期、推荐信状态、材料提交状态。", StudyAbroadConstants.PRIORITY_HIGH));
        result.getChecklist().add(item("材料", "准备成绩单、语言成绩和文书", "确认中英文成绩单、在读证明或毕业证明、语言送分、个人陈述、简历和推荐信。", StudyAbroadConstants.PRIORITY_HIGH));
        result.getChecklist().add(item("录取后", "确认押金和签证文件", "收到录取后核对押金、确认表、住宿、保险和签证所需学校文件。", StudyAbroadConstants.PRIORITY_MEDIUM));
        result.getChecklist().add(item("签证", "整理资金和身份材料", "按使领馆或官方签证系统要求准备资金证明、护照、照片、体检或预约材料。", StudyAbroadConstants.PRIORITY_HIGH));
        if (!containsAny(safe.getAdmissionStatus(), new String[]{"录取", "offer", "已拿"})) {
            result.getRisks().add("如果尚未拿到录取，签证材料先做预清单，不要提前使用过期证明。");
        }
        if (!hasText(safe.getMaterialStatus())) {
            result.getRisks().add("材料状态未填写，建议先建立一张按项目和截止日期排序的清单。");
        }
        result.getReminders().add("签证政策变化快，最终材料以官方签证网站和院校国际办公室通知为准。");
        result.getReminders().add("不要在系统中填写护照号、资金账号、签证账号密码等敏感信息。");
        return result;
    }

    private StudyAbroadChecklistItemDto item(String stage, String title, String detail, String priority) {
        StudyAbroadChecklistItemDto dto = new StudyAbroadChecklistItemDto();
        dto.setStage(stage);
        dto.setTitle(title);
        dto.setDetail(detail);
        dto.setPriority(priority);
        return dto;
    }

    private StudyAbroadSchoolOptionDto option(String tier, String schoolName, String program, String reason, List<String> preparation) {
        StudyAbroadSchoolOptionDto dto = new StudyAbroadSchoolOptionDto();
        dto.setTier(tier);
        dto.setSchoolName(schoolName);
        dto.setProgram(program);
        dto.setReason(reason);
        dto.getPreparation().addAll(preparation);
        return dto;
    }

    private int academicScore(String gpa, int max) {
        Double score = parseNumber(gpa);
        if (score == null) return 12;
        if (score.doubleValue() >= 3.7 || score.doubleValue() >= 88) return max;
        if (score.doubleValue() >= 3.3 || score.doubleValue() >= 82) return max - 7;
        if (score.doubleValue() >= 3.0 || score.doubleValue() >= 78) return max - 13;
        return 12;
    }

    private int languageScore(String value, int max) {
        Double score = parseNumber(value);
        String text = lower(value);
        if (score != null) {
            if (score.doubleValue() >= 7 || score.doubleValue() >= 100 || score.doubleValue() >= 320) return max;
            if (score.doubleValue() >= 6.5 || score.doubleValue() >= 90 || score.doubleValue() >= 310) return max - 5;
            if (score.doubleValue() >= 6 || score.doubleValue() >= 80 || score.doubleValue() >= 300) return max - 10;
        }
        if (containsAny(text, new String[]{"雅思", "托福", "gre", "ielts", "toefl"})) return 12;
        return 6;
    }

    private int evidenceScore(String text, int max, String... strongTerms) {
        if (!hasText(text)) return 4;
        int score = Math.min(10, text.length() / 16 + 5);
        for (String term : strongTerms) {
            if (containsAny(text, new String[]{term})) score += 3;
        }
        return clamp(score, 0, max);
    }

    private int clarityScore(String country, String degree, String major, String budget) {
        int score = 0;
        if (hasText(country)) score += 5;
        if (hasText(degree)) score += 4;
        if (hasText(major)) score += 6;
        if (hasText(budget)) score += 5;
        return score;
    }

    private Double parseNumber(String value) {
        if (!hasText(value)) return null;
        String normalized = value.replaceAll("[^0-9.]", "");
        if (!hasText(normalized)) return null;
        try {
            return Double.valueOf(normalized);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private boolean containsAny(String value, String[] terms) {
        String text = lower(value);
        for (String term : terms) {
            if (text.contains(lower(term))) return true;
        }
        return false;
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private String firstText(String first, String fallback) {
        return hasText(first) ? first.trim() : fallback;
    }

    private String join(String... values) {
        StringBuilder builder = new StringBuilder();
        for (String value : values) {
            if (hasText(value)) {
                if (builder.length() > 0) builder.append("；");
                builder.append(value.trim());
            }
        }
        return builder.toString();
    }

    private String lower(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT);
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }

    private List<String> list(String... values) {
        return new ArrayList<String>(Arrays.asList(values));
    }
}
