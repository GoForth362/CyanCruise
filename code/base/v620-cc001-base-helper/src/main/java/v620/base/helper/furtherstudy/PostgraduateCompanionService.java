package v620.base.helper.furtherstudy;

import v620.cc001.base.common.dto.furtherstudy.PostgraduateChecklistItemDto;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateConstants;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateDerivedQuestionDto;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateKnowledgeNodeDto;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateMistakeAnalysisResult;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateMistakeAnalyzeRequest;
import v620.cc001.base.common.dto.furtherstudy.PostgraduatePlanRequest;
import v620.cc001.base.common.dto.furtherstudy.PostgraduatePlanResult;
import v620.cc001.base.common.dto.furtherstudy.PostgraduatePlanRoundDto;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateReexamPreparationResult;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateReexamPrepareRequest;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateSchoolOptionDto;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateSchoolRecommendRequest;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateSchoolRecommendationResult;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/** Pure Java rule service for postgraduate exam companion. */
public class PostgraduateCompanionService {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ISO_LOCAL_DATE;

    public PostgraduateSchoolRecommendationResult recommendSchools(PostgraduateSchoolRecommendRequest request) {
        PostgraduateSchoolRecommendRequest safe = request == null ? new PostgraduateSchoolRecommendRequest() : request;
        PostgraduateSchoolRecommendationResult result = new PostgraduateSchoolRecommendationResult();
        List<String> missing = missingSchoolFields(safe);
        result.setMissingInfo(missing);
        result.setStatus(missing.isEmpty() ? PostgraduateConstants.STATUS_OK : PostgraduateConstants.STATUS_NEED_MORE_INFO);

        String region = firstText(safe.getPreferredRegion(), "目标地区");
        String major = firstText(safe.getTargetMajor(), safe.getMajor(), "目标专业");
        int score = competitivenessScore(safe);
        result.setSummary("根据当前画像，建议用 1 所冲刺、2 所稳妥、1 所保底的方式搭配目标，并在报名前年份核对招生简章、国家线和报录比。");
        result.getOptions().add(option(PostgraduateConstants.LEVEL_SAFE, "保底", safeSchool(region, score), major,
                region, "录取难度相对可控，适合作为稳定上岸选择。",
                "如果专业课范围差异较大，仍需提前确认参考书和真题风格。",
                list("核对近三年复试线", "确认是否保护一志愿", "整理该校专业课真题目录")));
        result.getOptions().add(option(PostgraduateConstants.LEVEL_MATCH, "稳妥", matchSchool(region, score), major,
                region, "与你的本科背景、成绩和英语水平较匹配，适合作为主目标。",
                "需要持续保持公共课分数，避免只依赖专业课拉分。",
                list("把该校考试科目作为主计划", "建立月度模考分数表", "关注学院复试名单和调剂情况")));
        result.getOptions().add(option(PostgraduateConstants.LEVEL_STRETCH, "冲刺", stretchSchool(region, score), major,
                region, "学校平台或专业热度更高，可作为提高上限的冲刺目标。",
                "竞争强度较高，建议只有在基础轮完成较好时继续投入。",
                list("对比专业课压分情况", "准备替代院校", "用真题正确率决定是否坚持冲刺")));
        result.getReminders().add("本建议不等同录取承诺，请核对当年招生简章、国家线、复试线、招生人数和报录比。");
        result.getReminders().add("如果目标专业跨度较大，请额外评估专业课学习成本和复试背景要求。");
        return result;
    }

    public PostgraduatePlanResult generatePlan(PostgraduatePlanRequest request) {
        PostgraduatePlanRequest safe = request == null ? new PostgraduatePlanRequest() : request;
        LocalDate start = parseDate(safe.getStartDate(), LocalDate.now());
        LocalDate exam = parseDate(safe.getExamDate(), start.plusDays(180));
        if (!exam.isAfter(start)) {
            exam = start.plusDays(90);
        }
        int days = (int) ChronoUnit.DAYS.between(start, exam);
        List<String> subjects = safe.getSubjects() == null || safe.getSubjects().isEmpty()
                ? list("政治", "英语", "数学或专业基础", "专业课")
                : safe.getSubjects();

        PostgraduatePlanResult result = new PostgraduatePlanResult();
        result.setStatus(PostgraduateConstants.STATUS_OK);
        result.setTarget(firstText(safe.getTargetSchool(), "目标院校") + " " + firstText(safe.getTargetMajor(), "目标专业"));
        result.setExamDate(DATE.format(exam));
        result.setDaysRemaining(Integer.valueOf(days));
        result.setSummary(days < 90
                ? "距离初试较近，计划将压缩基础补缺，把真题复盘和高频考点放在优先级最高的位置。"
                : "计划按基础、提高、冲刺三轮推进，每周同时保留复盘和状态调整时间。");
        result.setRounds(buildRounds(start, exam, subjects, days));
        result.setDailyHabits(list("每天固定 20 分钟复盘错题", "每周至少一次完整计时训练", "每晚记录睡眠、情绪和完成度", "每两周根据正确率调整科目投入"));
        return result;
    }

    public PostgraduateMistakeAnalysisResult analyzeMistake(PostgraduateMistakeAnalyzeRequest request) {
        PostgraduateMistakeAnalyzeRequest safe = request == null ? new PostgraduateMistakeAnalyzeRequest() : request;
        String question = trim(safe.getQuestionText());
        if (question.length() == 0) {
            throw new IllegalArgumentException("请先粘贴或输入错题题目，再进行解析。");
        }
        String subject = firstText(safe.getSubject(), inferSubject(question));
        List<String> points = inferKnowledgePoints(subject, question);
        PostgraduateMistakeAnalysisResult result = new PostgraduateMistakeAnalysisResult();
        result.setStatus(PostgraduateConstants.STATUS_OK);
        result.setSubject(subject);
        result.setAnswer("先定位题目考查的核心条件，再按知识点公式、概念或答题模板逐步推导；如果是主观题，先列关键词，再组织完整表述。");
        result.setExplanation("这道题的关键不是死记答案，而是识别题干中的限定条件，并把它映射到对应知识点。建议把本题订正为“条件-考点-步骤-结论”四行笔记。");
        result.setKnowledgeTree(buildKnowledgeTree(subject, points));
        result.setErrorReasons(list(
                hasText(safe.getWrongAnswer()) ? "原答案可能没有完整回应题干条件：" + trimTo(safe.getWrongAnswer(), 80) : "没有记录自己的错误答案，暂时无法精确定位偏差。",
                "对核心考点的触发词不够敏感，容易把题目归到相邻知识点。",
                "订正时如果只抄答案，下一次遇到变式仍容易出错。"));
        result.setCorrectionSteps(list("重读题干并圈出限定词", "写出本题对应的知识树节点", "不看答案复述一遍解题步骤", "隔 2 天做一道同类变式题"));
        result.setDerivedQuestions(buildDerivedQuestions(subject, points));
        return result;
    }

    public PostgraduateReexamPreparationResult prepareReexam(PostgraduateReexamPrepareRequest request) {
        PostgraduateReexamPrepareRequest safe = request == null ? new PostgraduateReexamPrepareRequest() : request;
        boolean beforeExam = containsAny(safe.getPreliminaryStatus(), new String[]{"未初试", "备考", "before", "preparing"});
        PostgraduateReexamPreparationResult result = new PostgraduateReexamPreparationResult();
        result.setStatus(PostgraduateConstants.STATUS_OK);
        result.setSummary(beforeExam
                ? "当前重点仍是初试，但可以轻量准备复试材料，避免初试后从零开始。"
                : "可以进入复试准备节奏：先补材料，再联系导师，最后集中模拟表达。");
        result.getChecklist().add(item("材料", "整理个人简历", "用一页纸呈现教育背景、项目经历、科研或课程成果，突出与目标专业相关的证据。", "高"));
        result.getChecklist().add(item("导师", "筛选导师方向", "阅读学院导师主页和近三年论文，记录研究方向、代表成果和可提问点。", "高"));
        result.getChecklist().add(item("专业", "复盘专业课", "按初试错题和复试参考书整理 20 个高频问答。", "中"));
        result.getChecklist().add(item("表达", "准备中英文自我介绍", "中文 2 分钟、英文 1 分钟，均包含背景、经历、目标方向和读研动机。", "中"));
        result.getChecklist().add(item("模拟", "进行复试模拟", "每周至少一次问答模拟，重点练习追问、压力问题和项目细节。", beforeExam ? "低" : "高"));
        result.setTutorContactTips(list("邮件标题写清楚报考专业、姓名和来意", "正文保持简洁，不群发痕迹明显的模板", "附上简历和成绩亮点，但不要催促老师承诺名额"));
        result.setResumeTips(list("把课程、项目、竞赛和论文按目标专业相关性排序", "每段经历补充你负责了什么、使用了什么方法、得到什么结果", "避免堆砌术语，确保面试时能解释每个细节"));
        result.setMockInterviewTips(list("准备为什么选择本专业、本校和导师方向", "准备本科项目或课程设计的追问", "练习英语口语自我介绍和专业词汇解释"));
        return result;
    }

    private List<PostgraduatePlanRoundDto> buildRounds(LocalDate start, LocalDate exam, List<String> subjects, int days) {
        List<PostgraduatePlanRoundDto> rounds = new ArrayList<PostgraduatePlanRoundDto>();
        if (days < 90) {
            LocalDate middle = start.plusDays(Math.max(20, days / 2));
            rounds.add(round(PostgraduateConstants.ROUND_FOUNDATION, "补缺轮", start, middle, subjects,
                    "快速补齐最薄弱章节，建立错题清单。",
                    list("每天完成一个高频考点", "公共课保持连续输入", "专业课优先真题涉及章节"),
                    list("完成近三年真题粗刷", "列出前 20 个薄弱点"), "短周期备考容易焦虑，请用每日完成度替代情绪判断。"));
            rounds.add(round(PostgraduateConstants.ROUND_SPRINT, "冲刺轮", middle.plusDays(1), exam, subjects,
                    "以真题、模考和背诵收口为主。",
                    list("每周至少两套计时训练", "错题只保留高频和反复错题", "政治和英语作文模板开始定稿"),
                    list("形成考前背诵清单", "模拟分数达到目标区间"), "保证睡眠，不要在考前一周大幅改变作息。"));
            return rounds;
        }
        LocalDate firstEnd = start.plusDays(days * 45L / 100L);
        LocalDate secondEnd = start.plusDays(days * 78L / 100L);
        rounds.add(round(PostgraduateConstants.ROUND_FOUNDATION, "基础轮", start, firstEnd, subjects,
                "建立完整知识框架，把教材、基础课和入门题打通。",
                list("每周固定完成教材章节和基础题", "英语保持单词和长难句", "数学或专业课建立公式/概念卡片"),
                list("完成第一遍知识框架", "整理第一版错题本"), "基础轮不要急于比较进度，先保证连续性。"));
        rounds.add(round(PostgraduateConstants.ROUND_IMPROVEMENT, "提高轮", firstEnd.plusDays(1), secondEnd, subjects,
                "进入真题和专题训练，提升速度、准确率和综合题处理能力。",
                list("按专题刷真题", "每周复盘错题知识树", "公共课开始限时训练"),
                list("真题正确率稳定提升", "形成二轮重点清单"), "如果某科连续两周低效，主动降低任务量并补基础。"));
        rounds.add(round(PostgraduateConstants.ROUND_SPRINT, "冲刺轮", secondEnd.plusDays(1), exam, subjects,
                "围绕真题套卷、背诵、模考和状态管理收口。",
                list("每周完整模考", "整理考前背诵页", "复盘错题但不再无限扩展新资料"),
                list("完成考前模拟", "确定考场时间分配"), "冲刺轮重点是稳定输出，不用追求资料数量。"));
        return rounds;
    }

    private PostgraduatePlanRoundDto round(String code, String name, LocalDate start, LocalDate end, List<String> subjects,
                                           String goal, List<String> weeklyTasks, List<String> checkPoints, String advice) {
        PostgraduatePlanRoundDto dto = new PostgraduatePlanRoundDto();
        dto.setRoundCode(code);
        dto.setRoundName(name);
        dto.setDateRange(DATE.format(start) + " 至 " + DATE.format(end));
        dto.setGoal(goal);
        List<String> focus = new ArrayList<String>();
        for (String subject : subjects) {
            if (hasText(subject)) {
                focus.add(subject + "：按本轮目标安排基础、专题或套卷任务");
            }
        }
        dto.setSubjectFocus(focus);
        dto.setWeeklyTasks(weeklyTasks);
        dto.setCheckPoints(checkPoints);
        dto.setStateAdvice(advice);
        return dto;
    }

    private List<PostgraduateKnowledgeNodeDto> buildKnowledgeTree(String subject, List<String> points) {
        List<PostgraduateKnowledgeNodeDto> nodes = new ArrayList<PostgraduateKnowledgeNodeDto>();
        PostgraduateKnowledgeNodeDto root = new PostgraduateKnowledgeNodeDto();
        root.setName(subject + "核心考点");
        root.setChildren(points);
        nodes.add(root);
        PostgraduateKnowledgeNodeDto method = new PostgraduateKnowledgeNodeDto();
        method.setName("订正方法");
        method.setChildren(list("题干条件", "公式或概念", "推导步骤", "复盘变式"));
        nodes.add(method);
        return nodes;
    }

    private List<PostgraduateDerivedQuestionDto> buildDerivedQuestions(String subject, List<String> points) {
        List<PostgraduateDerivedQuestionDto> out = new ArrayList<PostgraduateDerivedQuestionDto>();
        String point = points.isEmpty() ? "本题考点" : points.get(0);
        out.add(derived("同类基础题", "围绕“" + point + "”重新设计一个只改变数值或材料的题目。", "先写出考点，再套用标准步骤。"));
        out.add(derived("同类提高题", "把“" + point + "”与另一个章节知识点组合，训练综合判断。", "先拆分条件，再分别处理。"));
        out.add(derived("表达复盘题", "用 3 句话向同学讲清楚这道题为什么会错。", "包含错因、正确路径和下次提醒。"));
        return out;
    }

    private List<String> inferKnowledgePoints(String subject, String question) {
        String text = (subject + " " + question).toLowerCase(Locale.ROOT);
        if (containsAny(text, new String[]{"408", "数据结构", "操作系统", "计算机网络", "组成原理"})) {
            return list("数据结构与算法", "操作系统进程/内存", "计算机网络协议", "计算机组成原理");
        }
        if (containsAny(text, new String[]{"数学", "极限", "导数", "积分", "线性代数", "概率"})) {
            return list("函数极限与连续", "导数和积分", "线性代数基础", "概率统计模型");
        }
        if (containsAny(text, new String[]{"英语", "阅读", "翻译", "作文"})) {
            return list("长难句结构", "段落主旨", "选项干扰", "写作表达");
        }
        if (containsAny(text, new String[]{"政治", "马原", "毛中特", "史纲"})) {
            return list("概念辨析", "材料关键词", "理论与材料对应", "主观题层次");
        }
        return list("核心概念", "题干条件", "解题步骤", "复盘变式");
    }

    private String inferSubject(String question) {
        if (containsAny(question, new String[]{"408", "数据结构", "操作系统", "网络"})) return "408 计算机专业课";
        if (containsAny(question, new String[]{"极限", "导数", "矩阵", "概率"})) return "数学";
        if (containsAny(question, new String[]{"阅读", "翻译", "作文", "cloze"})) return "英语";
        if (containsAny(question, new String[]{"马原", "史纲", "中特"})) return "政治";
        return "专业课";
    }

    private int competitivenessScore(PostgraduateSchoolRecommendRequest request) {
        int score = 50;
        String level = lower(request.getUndergraduateLevel());
        if (level.contains("985")) score += 18;
        else if (level.contains("211") || level.contains("双一流")) score += 12;
        else if (level.contains("一本")) score += 6;
        Double gpa = parseNumber(request.getGpa());
        if (gpa != null) {
            if (gpa.doubleValue() >= 3.7 || gpa.doubleValue() >= 85) score += 12;
            else if (gpa.doubleValue() >= 3.3 || gpa.doubleValue() >= 80) score += 8;
            else if (gpa.doubleValue() < 2.8 && gpa.doubleValue() < 75) score -= 8;
        }
        String english = lower(request.getEnglishLevel());
        if (english.contains("六级") || english.contains("雅思") || english.contains("托福") || english.contains("cet6")) score += 8;
        else if (english.contains("四级") || english.contains("cet4")) score += 4;
        if (hasText(request.getPreference())) score += 2;
        return Math.max(20, Math.min(90, score));
    }

    private String safeSchool(String region, int score) {
        if (score >= 70) return region + "重点行业特色高校";
        return region + "省属重点高校";
    }

    private String matchSchool(String region, int score) {
        if (score >= 70) return region + "双一流或强势学科高校";
        if (score >= 55) return region + "区域重点高校";
        return region + "招生规模稳定高校";
    }

    private String stretchSchool(String region, int score) {
        if (score >= 75) return region + "头部 985 高校";
        if (score >= 60) return region + "热门 211 高校";
        return region + "专业排名靠前高校";
    }

    private List<String> missingSchoolFields(PostgraduateSchoolRecommendRequest request) {
        List<String> missing = new ArrayList<String>();
        if (!hasText(request.getUndergraduateLevel()) && !hasText(request.getUndergraduateSchool())) missing.add("本科学校或学校层次");
        if (!hasText(request.getGpa())) missing.add("绩点或平均分");
        if (!hasText(request.getEnglishLevel())) missing.add("英语水平");
        if (!hasText(request.getPreferredRegion())) missing.add("期望地区");
        if (!hasText(request.getTargetMajor()) && !hasText(request.getMajor())) missing.add("目标专业");
        return missing;
    }

    private PostgraduateSchoolOptionDto option(String tier, String tierName, String school, String major, String region,
                                               String reason, String risk, List<String> actions) {
        PostgraduateSchoolOptionDto dto = new PostgraduateSchoolOptionDto();
        dto.setTier(tier);
        dto.setTierName(tierName);
        dto.setSchoolName(school);
        dto.setMajorName(major);
        dto.setRegion(region);
        dto.setReason(reason);
        dto.setRisk(risk);
        dto.setActions(actions);
        return dto;
    }

    private PostgraduateChecklistItemDto item(String stage, String title, String detail, String priority) {
        PostgraduateChecklistItemDto dto = new PostgraduateChecklistItemDto();
        dto.setStage(stage);
        dto.setTitle(title);
        dto.setDetail(detail);
        dto.setPriority(priority);
        return dto;
    }

    private PostgraduateDerivedQuestionDto derived(String title, String hint, String answerOutline) {
        PostgraduateDerivedQuestionDto dto = new PostgraduateDerivedQuestionDto();
        dto.setTitle(title);
        dto.setHint(hint);
        dto.setAnswerOutline(answerOutline);
        return dto;
    }

    private LocalDate parseDate(String value, LocalDate fallback) {
        if (!hasText(value)) return fallback;
        try {
            return LocalDate.parse(value.trim(), DATE);
        } catch (DateTimeParseException ex) {
            return fallback;
        }
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
        String text = value == null ? "" : value.toLowerCase(Locale.ROOT);
        for (String term : terms) {
            if (text.contains(term.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

    private String lower(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT);
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private String trimTo(String value, int max) {
        String text = trim(value);
        return text.length() <= max ? text : text.substring(0, max);
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }

    private String firstText(String first, String second) {
        return hasText(first) ? first.trim() : second;
    }

    private String firstText(String first, String second, String third) {
        if (hasText(first)) return first.trim();
        if (hasText(second)) return second.trim();
        return third;
    }

    private List<String> list(String... values) {
        return new ArrayList<String>(Arrays.asList(values));
    }
}
