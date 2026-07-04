package v620.base.helper.furtherstudy;

import v620.cc001.base.common.dto.furtherstudy.RecommendationActionItemDto;
import v620.cc001.base.common.dto.furtherstudy.RecommendationConstants;
import v620.cc001.base.common.dto.furtherstudy.RecommendationDiagnosisResult;
import v620.cc001.base.common.dto.furtherstudy.RecommendationDocumentPolishRequest;
import v620.cc001.base.common.dto.furtherstudy.RecommendationDocumentPolishResult;
import v620.cc001.base.common.dto.furtherstudy.RecommendationPlanResult;
import v620.cc001.base.common.dto.furtherstudy.RecommendationProfileRequest;
import v620.cc001.base.common.dto.furtherstudy.RecommendationScoreItemDto;
import v620.cc001.base.common.dto.furtherstudy.RecommendationTutorLetterRequest;
import v620.cc001.base.common.dto.furtherstudy.RecommendationTutorLetterResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/** Pure Java rule service for postgraduate recommendation companion. */
public class RecommendationCompanionService {

    public RecommendationDiagnosisResult diagnose(RecommendationProfileRequest request) {
        RecommendationProfileRequest safe = request == null ? new RecommendationProfileRequest() : request;
        int rankScore = rankScore(safe);
        int awardScore = evidenceScore(safe.getAwards(), 18, "国", "省", "数学建模", "竞赛");
        int researchScore = evidenceScore(join(safe.getResearch(), safe.getPapers(), safe.getPatentsOrCopyrights()), 22, "论文", "课题", "导师", "软著", "专利", "实验");
        int englishScore = englishScore(safe.getEnglishLevel());
        int materialScore = materialScore(safe);
        int overall = clamp(rankScore + awardScore + researchScore + englishScore + materialScore, 0, 100);

        RecommendationDiagnosisResult result = new RecommendationDiagnosisResult();
        result.setStatus(RecommendationConstants.STATUS_OK);
        result.setOverallScore(Integer.valueOf(overall));
        result.setSummary(overall >= 78
                ? "你的保研竞争力较强，当前重点是锁定目标营校并把材料打磨得更像研究型申请。"
                : overall >= 62
                ? "你的保研基础具备可塑性，建议优先稳住排名，并补齐科研或材料表达短板。"
                : "当前背景还需要明显补强，建议先确认本校推免资格边界，再选择更稳妥的营校梯度。");
        result.getScoreItems().add(score("绩点与排名", rankScore, 30, rankScore >= 24 ? "排名优势明显" : "需要持续监控排名变化"));
        result.getScoreItems().add(score("竞赛与奖项", awardScore, 18, awardScore >= 12 ? "竞赛经历有辨识度" : "奖项证据还可以继续补充"));
        result.getScoreItems().add(score("科研与成果", researchScore, 22, researchScore >= 14 ? "已有科研或成果支撑" : "科研论文或课题经历偏弱"));
        result.getScoreItems().add(score("英语与表达", englishScore, 12, englishScore >= 8 ? "英语材料可作为加分项" : "英语证明和面试表达需要准备"));
        result.getScoreItems().add(score("目标匹配", materialScore, 18, materialScore >= 12 ? "目标方向较清晰" : "目标营校和专业方向需要收敛"));
        fillStrengthsAndWeaknesses(result, safe, rankScore, awardScore, researchScore, englishScore, materialScore);
        result.getActions().addAll(coreActions(safe, researchScore, materialScore));
        result.getReminders().add("保研结果以本校推免政策、学院排名认定和目标院校通知为准，系统建议只作为准备辅助。");
        result.getReminders().add("如果目标导师或营校方向发生变化，请重新生成材料和意向信。");
        return result;
    }

    public RecommendationPlanResult generatePlan(RecommendationProfileRequest request) {
        RecommendationDiagnosisResult diagnosis = diagnose(request);
        RecommendationPlanResult result = new RecommendationPlanResult();
        result.setStatus(RecommendationConstants.STATUS_OK);
        result.setSummary("按“资格稳定、背景补强、营校投递、面试表达”四条线推进，避免只盯通知而忽略材料质量。");
        result.getTimeline().add(action("绩点排名", "每两周更新排名风险", "记录已出课程、待出课程和可能影响排名的课程，估算是否仍在推免边界内。", RecommendationConstants.PRIORITY_HIGH));
        result.getTimeline().add(action("背景提升", "补齐科研证据", "优先联系课程老师或实验室导师，把课程项目整理成可讲清楚的问题、方法和结果。", RecommendationConstants.PRIORITY_HIGH));
        result.getTimeline().add(action("信息战", "建立夏令营/预推免清单", "按冲刺、匹配、保底三档记录院校、专业、材料截止时间、考核形式和往年要求。", RecommendationConstants.PRIORITY_HIGH));
        result.getTimeline().add(action("材料", "准备一套主文书", "简历、自述信、成绩单、获奖证明、科研材料先形成主版本，再按营校方向微调。", RecommendationConstants.PRIORITY_MEDIUM));
        result.getTimeline().add(action("面试", "每周模拟一次专业问答", "围绕项目、论文、竞赛、专业基础和英语自我介绍进行追问训练。", RecommendationConstants.PRIORITY_MEDIUM));
        result.getTimeline().addAll(diagnosis.getActions());
        result.setWeeklyFocus(list("更新排名和营校清单", "补一段可量化经历材料", "精修一版自述信或导师邮件", "进行一次 20 分钟面试模拟"));
        result.setTargetCampTips(list("冲刺营校用于提高上限，匹配营校用于主投递，保底营校用于稳定结果", "不要只看学校名气，也要看专业方向、导师匹配和材料截止时间", "每个目标至少准备一条为什么适合该方向的证据"));
        return result;
    }

    public RecommendationDocumentPolishResult polishDocument(RecommendationDocumentPolishRequest request) {
        RecommendationDocumentPolishRequest safe = request == null ? new RecommendationDocumentPolishRequest() : request;
        String draft = trim(safe.getDraft());
        if (draft.length() == 0) {
            throw new IllegalArgumentException("请先粘贴文书初稿，再进行润色。");
        }
        String type = firstText(safe.getDocumentType(), "个人自述");
        String target = firstText(safe.getTargetMajor(), "目标专业");
        String highlight = firstText(safe.getHighlights(), "课程学习、项目经历或竞赛成果");
        RecommendationDocumentPolishResult result = new RecommendationDocumentPolishResult();
        result.setStatus(RecommendationConstants.STATUS_OK);
        result.setPolishedText("【" + type + "润色稿】\n"
                + "我希望申请" + target + "方向，并不是因为简单地罗列经历，而是因为过往学习和实践让我逐步形成了清晰的学术兴趣。\n\n"
                + "在背景方面，我围绕" + highlight + "持续积累基础能力。原稿中提到的经历可以进一步表达为：我面对具体问题时，先明确目标和约束，再通过资料检索、方案设计和反复验证推进任务，最终沉淀出可复用的方法和结果。\n\n"
                + "这段经历体现了我的主动学习、问题拆解和学术表达潜力。进入研究生阶段后，我希望继续在" + target + "相关方向深入训练，将已有基础转化为更系统的研究能力。\n\n"
                + "【原稿保留信息】\n" + trimTo(draft, 420));
        result.setRewriteReasons(list("把流水账改成“背景、行动、结果、学术潜力”的经历讲述框架", "减少空泛形容词，突出你做了什么、怎么做、结果是什么", "把经历和目标专业建立连接，让材料更像研究型申请"));
        result.setRetainedHighlights(list(highlight, "原稿中的真实经历和个人动机"));
        if (!containsAny(draft, new String[]{"结果", "提升", "获奖", "论文", "项目", "数据"})) {
            result.getMissingInfo().add("建议补充可验证结果，例如排名、奖项、项目指标、论文或课程成绩。");
        }
        if (!containsAny(draft, new String[]{"方向", "导师", "研究", "专业"})) {
            result.getMissingInfo().add("建议补充目标专业方向，以及你为什么适合这个方向。");
        }
        return result;
    }

    public RecommendationTutorLetterResult generateTutorLetter(RecommendationTutorLetterRequest request) {
        RecommendationTutorLetterRequest safe = request == null ? new RecommendationTutorLetterRequest() : request;
        String tutor = firstText(safe.getTutorName(), "老师");
        String school = firstText(safe.getTargetSchool(), "贵校");
        String major = firstText(safe.getTargetMajor(), "相关专业");
        String direction = trim(safe.getResearchDirection());
        String background = firstText(safe.getPersonalBackground(), "我已准备好个人简历、成绩单和相关经历材料");
        RecommendationTutorLetterResult result = new RecommendationTutorLetterResult();
        result.setStatus(direction.length() == 0 ? RecommendationConstants.STATUS_NEED_MORE_INFO : RecommendationConstants.STATUS_OK);
        result.setSubject("保研咨询：" + major + "方向推免申请 - 学生姓名");
        result.setBody(tutor + "您好：\n\n"
                + "冒昧来信打扰。我正在准备" + school + major + "方向的推免申请，了解到您"
                + (direction.length() == 0 ? "长期从事该领域相关研究" : "的研究方向包括“" + direction + "”")
                + "，与我的学习兴趣较为契合，因此希望向您请教是否有机会进一步了解课题组和推免要求。\n\n"
                + "我的基本背景是：" + background + "。在过往学习和实践中，我比较关注问题拆解、资料检索和结果复盘，也希望在研究生阶段接受更系统的科研训练。\n\n"
                + "如果方便，我想请教：课题组今年是否考虑接收推免生，以及您更看重申请者哪些课程基础、科研经历或能力准备。我随信附上简历和成绩材料，恳请老师批评指正。\n\n"
                + "感谢老师拨冗阅读，祝工作顺利！\n学生姓名\n联系方式");
        result.setAttachments(list("个人简历 PDF", "成绩单或排名证明", "竞赛/论文/项目材料合并 PDF"));
        result.setSendTips(list("发送前核对导师真实研究方向，不要编造论文信息", "邮件控制在 400-700 字，附件命名清楚", "三到五天未回复可以礼貌跟进一次，不要频繁催促"));
        if (direction.length() == 0) {
            result.getMissingInfo().add("请补充导师研究方向或论文关键词，发送前不要使用泛化描述替代事实。");
        }
        return result;
    }

    private void fillStrengthsAndWeaknesses(RecommendationDiagnosisResult result, RecommendationProfileRequest request,
                                            int rankScore, int awardScore, int researchScore, int englishScore, int materialScore) {
        if (rankScore >= 24) result.getStrengths().add("绩点和排名是当前最稳定的保研基础。");
        else result.getWeaknesses().add("绩点或排名仍有不确定性，需要优先监控推免资格边界。");
        if (awardScore >= 12) result.getStrengths().add("竞赛或奖项经历有辨识度，可以作为材料亮点。");
        else result.getWeaknesses().add("竞赛奖项证据偏少，建议用课程项目或科研经历补足。");
        if (researchScore >= 14) result.getStrengths().add("已有科研、论文、软著或课题经历，可向学术潜力表达延展。");
        else result.getWeaknesses().add("科研论文或软著为空白，是当前最值得补强的方向。");
        if (englishScore < 8) result.getWeaknesses().add("英语证明和面试表达需要提前准备。");
        if (materialScore < 12) result.getWeaknesses().add("目标院校和专业方向还不够收敛，容易导致材料泛化。");
        if (containsAny(request.getAwards(), new String[]{"数学建模", "国二", "国家二等奖"})) {
            result.getStrengths().add("数学建模奖项可转化为建模能力、协作能力和问题求解能力证据。");
        }
    }

    private List<RecommendationActionItemDto> coreActions(RecommendationProfileRequest request, int researchScore, int materialScore) {
        List<RecommendationActionItemDto> actions = new ArrayList<RecommendationActionItemDto>();
        if (researchScore < 14) {
            actions.add(action("背景提升", "补科研空白", "大三下优先联系任课老师或实验室，争取参与课题、复现实验或整理课程项目为研究经历。", RecommendationConstants.PRIORITY_HIGH));
        }
        if (materialScore < 12) {
            actions.add(action("目标锁定", "建立营校梯度", "把夏令营和预推免目标拆成冲刺、匹配、保底三档，每档至少 3 个项目。", RecommendationConstants.PRIORITY_HIGH));
        }
        actions.add(action("材料精修", "准备主版本文书", "把每段经历写成背景、行动、结果、学术潜力，避免流水账。", RecommendationConstants.PRIORITY_MEDIUM));
        actions.add(action("面试模拟", "准备三类追问", "专业基础、项目细节、为什么选择该方向，每类至少准备 5 个问答。", RecommendationConstants.PRIORITY_MEDIUM));
        return actions;
    }

    private RecommendationScoreItemDto score(String name, int score, int max, String comment) {
        RecommendationScoreItemDto dto = new RecommendationScoreItemDto();
        dto.setName(name);
        dto.setScore(Integer.valueOf(score));
        dto.setMaxScore(Integer.valueOf(max));
        dto.setComment(comment);
        return dto;
    }

    private RecommendationActionItemDto action(String stage, String title, String detail, String priority) {
        RecommendationActionItemDto dto = new RecommendationActionItemDto();
        dto.setStage(stage);
        dto.setTitle(title);
        dto.setDetail(detail);
        dto.setPriority(priority);
        return dto;
    }

    private int rankScore(RecommendationProfileRequest request) {
        int score = 12;
        Double gpa = parseNumber(request.getGpa());
        if (gpa != null) {
            if (gpa.doubleValue() >= 3.8 || gpa.doubleValue() >= 88) score += 8;
            else if (gpa.doubleValue() >= 3.5 || gpa.doubleValue() >= 84) score += 5;
            else if (gpa.doubleValue() < 3.0 && gpa.doubleValue() < 78) score -= 4;
        }
        Double rank = parseNumber(request.getRank());
        String rankText = lower(request.getRank());
        if (rankText.contains("前5") || rankText.contains("5%") || (rank != null && rank.doubleValue() <= 5)) score += 10;
        else if (rankText.contains("前10") || rankText.contains("10%") || (rank != null && rank.doubleValue() <= 10)) score += 7;
        else if (rankText.contains("前20") || rankText.contains("20%") || (rank != null && rank.doubleValue() <= 20)) score += 3;
        return clamp(score, 0, 30);
    }

    private int evidenceScore(String text, int max, String... strongTerms) {
        if (!hasText(text)) return 0;
        int score = Math.min(8, text.length() / 12 + 4);
        for (String term : strongTerms) {
            if (containsAny(text, new String[]{term})) {
                score += 4;
            }
        }
        return clamp(score, 0, max);
    }

    private int englishScore(String english) {
        String text = lower(english);
        if (!hasText(text)) return 3;
        if (text.contains("六级") || text.contains("cet6") || text.contains("雅思") || text.contains("托福")) return 10;
        if (text.contains("四级") || text.contains("cet4")) return 7;
        return 5;
    }

    private int materialScore(RecommendationProfileRequest request) {
        int score = 4;
        if (hasText(request.getTargetSchools())) score += 6;
        if (hasText(request.getTargetMajor())) score += 5;
        if (hasText(request.getMajor()) && hasText(request.getTargetMajor())) score += 3;
        return clamp(score, 0, 18);
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
            if (text.contains(lower(term))) {
                return true;
            }
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

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private String trimTo(String value, int max) {
        String text = trim(value);
        return text.length() <= max ? text : text.substring(0, max);
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
