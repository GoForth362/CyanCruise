package v620.cc001.cloud01.app01.mservice;

import v620.cc001.base.common.dto.career.ResumeDiagnosisRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Explainable deterministic analyzer used until agent integration is available.
 */
public class DefaultResumeDiagnosisAnalyzer implements ResumeDiagnosisAnalyzer {

    private static final Pattern NUMBER_PATTERN = Pattern.compile(".*(?:\\d|%|％|万|千|百).*", Pattern.DOTALL);

    public String analyze(ResumeDiagnosisRequest request, String resumeText) {
        String text = resumeText == null ? "" : resumeText;
        String jobRequirements = request == null ? null : request.getJobDescription();
        String targetJob = request == null ? null : request.getTargetJob();

        boolean hasExperience = containsAny(text, new String[]{"项目", "实习", "工作经历", "任职", "负责"});
        boolean hasSkills = containsAny(text, new String[]{"Java", "Spring", "Python", "Redis", "SQL", "技能", "技术"});
        boolean hasNumbers = NUMBER_PATTERN.matcher(text).matches();
        boolean hasAction = containsAny(text, new String[]{"完成", "实现", "设计", "开发", "优化", "搭建", "解决"});
        boolean hasResult = containsAny(text, new String[]{"提升", "降低", "减少", "增长", "结果", "上线", "交付"});
        boolean hasContribution = containsAny(text, new String[]{"负责", "主导", "独立", "参与", "协作", "个人贡献"});
        boolean targetMatched = hasText(targetJob) && matchesTargetRole(text, targetJob);
        boolean requirementsMatched = hasText(jobRequirements) && overlaps(text, jobRequirements);

        int completeness = completenessScore(text);
        int matching = matchingScore(targetJob, jobRequirements, targetMatched, requirementsMatched);
        int evidence = (hasExperience ? 10 : 0) + (hasSkills ? 8 : 0) + (hasNumbers ? 12 : 0);
        int clarity = (hasAction ? 5 : 0) + (hasResult ? 5 : 0) + (hasContribution ? 5 : 0);
        int overall = completeness + matching + evidence + clarity;

        List<String> strengths = new ArrayList<String>();
        List<String> weaknesses = new ArrayList<String>();
        List<Suggestion> suggestions = new ArrayList<Suggestion>();
        if (hasExperience) strengths.add("简历中已识别到项目、实习或工作经历");
        if (hasSkills) strengths.add("简历中已识别到岗位相关技能");
        if (hasNumbers) strengths.add("部分经历已经使用数字说明成果");
        if (targetMatched || requirementsMatched) strengths.add("简历内容与目标岗位存在可识别的匹配点");

        if (!hasNumbers) {
            weaknesses.add("未识别到明确的数字或比例成果");
            suggestions.add(new Suggestion("RESULT_EVIDENCE", "HIGH", "experience",
                    "经历描述缺少可验证的成果", "为一条真实经历补充数量、比例、耗时或效率变化",
                    "将“负责接口开发”改为“独立完成 6 个核心接口，将平均响应时间从 800 毫秒降到 300 毫秒”",
                    new String[]{"量化成果", "个人贡献"}));
        }
        if (!hasExperience) {
            weaknesses.add("未识别到完整的项目、实习或工作经历");
            suggestions.add(new Suggestion("EXPERIENCE", "HIGH", "experience",
                    "岗位能力缺少经历证据", "至少补充一段与目标岗位直接相关的项目、实习或工作经历",
                    "按“背景、任务、行动、结果”写清楚你解决了什么问题以及最终产出",
                    new String[]{"经历证据", "项目成果"}));
        }
        if ((hasText(targetJob) || hasText(jobRequirements)) && !targetMatched && !requirementsMatched) {
            weaknesses.add("未识别到与目标岗位要求直接重合的能力词");
            suggestions.add(new Suggestion("ROLE_MATCH", "HIGH", "skills",
                    "简历与目标岗位的匹配表达不足", "对照岗位要求，补充你确实具备且能用经历证明的技能",
                    "围绕“" + targetJob + "”选择 2 至 3 个真实掌握的能力词，并分别放入对应项目或工作经历中",
                    new String[]{"岗位要求", "能力证据"}));
        }
        if (text.length() < 400) {
            weaknesses.add("简历正文较短，关键信息可能不完整");
            suggestions.add(new Suggestion("COMPLETENESS", "MEDIUM", "summary",
                    "简历信息完整度不足", "检查教育背景、技能、项目或工作经历、成果四类信息是否齐全",
                    "每段经历至少写清任务、具体行动和一个可验证结果",
                    new String[]{"内容完整度", "经历结构"}));
        }
        if (!hasAction || !hasResult || !hasContribution) {
            weaknesses.add("部分经历没有同时讲清行动、结果和个人贡献");
            suggestions.add(new Suggestion("CLARITY", "MEDIUM", "experience",
                    "经历表达不够具体", "把职责描述改成“做了什么、怎么做、结果如何、你贡献了什么”",
                    "使用某项技术完成核心模块，解决具体问题，取得可验证结果，并说明个人负责范围",
                    new String[]{"行动", "结果", "个人贡献"}));
        }
        if (suggestions.isEmpty()) {
            suggestions.add(new Suggestion("REFINEMENT", "LOW", "experience",
                    "当前基础规则未发现明显缺口", "优先检查最重要的一段经历是否与目标岗位最相关",
                    "保留最能证明岗位能力的技术、行动、指标和个人贡献，删除无关描述",
                    new String[]{"岗位匹配", "重点经历"}));
        }
        if (strengths.isEmpty()) strengths.add("已成功读取简历正文，可以继续补充岗位相关证据");

        return buildJson(overall, completeness, matching, evidence, clarity, text, targetJob,
                jobRequirements, hasExperience, hasSkills, hasNumbers, hasAction, hasResult,
                hasContribution, targetMatched, requirementsMatched, strengths, weaknesses, suggestions);
    }

    private int completenessScore(String text) {
        int score = text.length() >= 800 ? 15 : text.length() >= 400 ? 10 : text.length() >= 200 ? 6 : 3;
        int sections = countPresent(text, new String[]{"教育", "项目", "实习", "工作", "技能"});
        return score + (sections >= 3 ? 10 : sections == 2 ? 7 : sections == 1 ? 4 : 0);
    }

    private int matchingScore(String targetJob, String requirements, boolean targetMatched, boolean requirementsMatched) {
        if (!hasText(targetJob) && !hasText(requirements)) return 12;
        int score = 0;
        if (hasText(targetJob)) score += targetMatched ? 12 : 3;
        if (hasText(requirements)) score += requirementsMatched ? 18 : 4;
        return score;
    }

    private String buildJson(int overall, int completeness, int matching, int evidence, int clarity,
                             String text, String targetJob, String requirements, boolean hasExperience,
                             boolean hasSkills, boolean hasNumbers, boolean hasAction, boolean hasResult,
                             boolean hasContribution, boolean targetMatched, boolean requirementsMatched,
                             List<String> strengths, List<String> weaknesses, List<Suggestion> suggestions) {
        StringBuilder json = new StringBuilder("{\"overallScore\":").append(overall);
        json.append(",\"scoreBreakdown\":[");
        scoreItem(json, "内容完整度", completeness, 25, text.length() >= 400 ? "正文长度和主要经历结构较完整" : "正文较短或主要经历结构不完整");
        json.append(',');
        scoreItem(json, "目标岗位匹配", matching, 30, targetMatched || requirementsMatched ? "识别到与目标岗位或岗位要求重合的内容" : "未识别到足够的岗位匹配表达");
        json.append(',');
        scoreItem(json, "经历证据", evidence, 30, evidenceReason(hasExperience, hasSkills, hasNumbers));
        json.append(',');
        scoreItem(json, "表达清晰度", clarity, 15, clarityReason(hasAction, hasResult, hasContribution));
        json.append("],\"strengths\":"); stringArray(json, strengths);
        json.append(",\"weaknesses\":"); stringArray(json, weaknesses);
        List<String> simpleSuggestions = new ArrayList<String>();
        for (Suggestion suggestion : suggestions) simpleSuggestions.add(suggestion.action);
        json.append(",\"suggestions\":"); stringArray(json, simpleSuggestions);
        json.append(",\"revisionSuggestions\":[");
        for (int i = 0; i < suggestions.size(); i += 1) {
            if (i > 0) json.append(',');
            suggestions.get(i).appendJson(json, i + 1);
        }
        return json.append("]}").toString();
    }

    private void scoreItem(StringBuilder json, String name, int score, int max, String reason) {
        json.append("{\"name\":"); quote(json, name); json.append(",\"score\":").append(score)
                .append(",\"maxScore\":").append(max).append(",\"reason\":"); quote(json, reason); json.append('}');
    }

    private void stringArray(StringBuilder json, List<String> values) {
        json.append('[');
        for (int i = 0; i < values.size(); i += 1) { if (i > 0) json.append(','); quote(json, values.get(i)); }
        json.append(']');
    }

    private String evidenceReason(boolean experience, boolean skills, boolean numbers) {
        return "经历结构" + yesNo(experience) + "、岗位技能" + yesNo(skills) + "、量化成果" + yesNo(numbers);
    }

    private String clarityReason(boolean action, boolean result, boolean contribution) {
        return "具体行动" + yesNo(action) + "、结果表达" + yesNo(result) + "、个人贡献" + yesNo(contribution);
    }

    private String yesNo(boolean value) { return value ? "已识别" : "未识别"; }
    private int countPresent(String text, String[] values) { int count = 0; for (String value : values) if (text.contains(value)) count++; return count; }
    private boolean overlaps(String text, String context) { for (String part : context.split("[^A-Za-z0-9\\p{IsHan}#.+-]+")) if (part != null && part.trim().length() >= 2 && text.contains(part.trim())) return true; return false; }
    private boolean matchesTargetRole(String text, String targetJob) {
        if (overlaps(text, targetJob)) return true;
        String role = targetJob == null ? "" : targetJob.toLowerCase();
        if (containsAny(role, new String[]{"前端", "frontend", "web"}))
            return containsAny(text, new String[]{"前端", "JavaScript", "TypeScript", "Vue", "React", "HTML", "CSS"});
        if (containsAny(role, new String[]{"后端", "backend", "服务端"}))
            return containsAny(text, new String[]{"后端", "Java", "Spring", "Python", "接口", "数据库", "Redis", "微服务"});
        if (containsAny(role, new String[]{"数据", "data", "分析"}))
            return containsAny(text, new String[]{"数据分析", "SQL", "Python", "Excel", "指标", "可视化", "统计"});
        if (containsAny(role, new String[]{"算法", "人工智能", "ai", "机器学习"}))
            return containsAny(text, new String[]{"算法", "机器学习", "深度学习", "Python", "模型", "NLP", "计算机视觉"});
        if (containsAny(role, new String[]{"产品", "product"}))
            return containsAny(text, new String[]{"产品", "需求", "用户研究", "原型", "竞品", "数据指标"});
        if (containsAny(role, new String[]{"测试", "qa"}))
            return containsAny(text, new String[]{"测试", "自动化", "用例", "质量", "缺陷", "接口测试"});
        return false;
    }
    private boolean containsAny(String text, String[] values) { for (String value : values) if (text.contains(value)) return true; return false; }
    private boolean hasText(String value) { return value != null && value.trim().length() > 0; }
    private void quote(StringBuilder json, String value) { json.append('"').append(value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")).append('"'); }

    private static class Suggestion {
        private final String type, priority, section, problem, action, example;
        private final String[] keywords;
        Suggestion(String type, String priority, String section, String problem, String action, String example, String[] keywords) {
            this.type = type; this.priority = priority; this.section = section; this.problem = problem;
            this.action = action; this.example = example; this.keywords = keywords;
        }
        void appendJson(StringBuilder json, int index) {
            json.append("{\"suggestionId\":\"rule-").append(index).append("\",\"issueType\":\"").append(type)
                    .append("\",\"priority\":\"").append(priority).append("\",\"resumeSection\":\"").append(section)
                    .append("\",\"problem\":"); appendQuoted(json, problem); json.append(",\"action\":"); appendQuoted(json, action);
            json.append(",\"rewriteExample\":"); appendQuoted(json, example); json.append(",\"targetKeywords\":[");
            for (int i = 0; i < keywords.length; i += 1) { if (i > 0) json.append(','); appendQuoted(json, keywords[i]); }
            json.append("],\"status\":\"TODO\",\"contextSource\":\"rule\"}");
        }
        private static void appendQuoted(StringBuilder json, String value) { json.append('"').append(value.replace("\\", "\\\\").replace("\"", "\\\"")).append('"'); }
    }
}
