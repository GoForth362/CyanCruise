package v620.base.helper.career;

import v620.cc001.base.common.dto.career.ResumeDiagnosisConstants;
import v620.cc001.base.common.dto.career.ResumeDiagnosisResultDto;
import v620.cc001.base.common.dto.career.ResumeDiagnosisScoreItemDto;
import v620.cc001.base.common.dto.career.ResumeKeywordDto;
import v620.cc001.base.common.dto.career.ResumeKeywordStatusDto;
import v620.cc001.base.common.dto.career.ResumeRecordDto;
import v620.cc001.base.common.dto.career.ResumeRevisionPlanDto;
import v620.cc001.base.common.dto.career.ResumeRevisionSuggestionDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Pure Java rules for resume diagnosis parsing and keyword extraction.
 */
public class ResumeDiagnosisService {

    private static final Set<String> CONTACT_TOKENS = setOf(new String[]{
            "email", "mail", "phone", "tel", "mobile", "wechat", "weixin", "qq", "github", "linkedin"
    });
    private static final Set<String> NON_SKILL_TOKENS = setOf(new String[]{
            "本科", "课程", "核心课程", "项目经历", "专业排名前"
    });
    private static final Set<String> STOPWORDS = setOf(new String[]{
            "用户", "目标", "岗位", "状态", "未知", "暂无", "简历状态", "简历匹配",
            "公司", "负责", "参与", "完成", "熟悉", "掌握", "了解", "使用", "进行", "相关",
            "basic", "profile", "target", "role", "state", "status", "null", "none", "no", "yes",
            "company", "responsible", "use", "used", "using", "related"
    });
    private static final List<String> KNOWN_TERMS = Arrays.asList(
            "前端", "后端", "全栈", "算法", "数据", "产品", "运营", "设计", "测试", "开发", "工程师",
            "Java", "Python", "JavaScript", "TypeScript", "Vue", "React", "Spring", "SpringBoot",
            "Node", "Express", "UniApp", "微信小程序", "小程序", "HTML", "CSS", "Sass", "Less",
            "SQL", "MySQL", "PostgreSQL", "MongoDB", "Redis", "Docker", "Kubernetes", "Git", "Linux",
            "AI", "AIGC", "LLM", "NLP", "UI", "UX",
            "沟通", "表达", "项目", "实习", "面试", "校招", "求职", "匹配", "学习", "探索",
            "应届", "学生", "转行", "城市", "远程", "作品集", "英语", "技术", "管理", "研究",
            "本科", "硕士", "博士", "学士", "课程", "竞赛", "奖学金", "论文", "证书",
            "后端工程师", "前端工程师", "产品经理", "数据分析师", "算法工程师"
    );

    public ResumeDiagnosisResultDto parseAnalysis(String analysis) {
        ResumeDiagnosisResultDto result = new ResumeDiagnosisResultDto();
        result.setRawAnalysis(analysis);
        if (!hasText(analysis)) {
            result.setOverallScore(Integer.valueOf(ResumeDiagnosisConstants.DEFAULT_SCORE));
            applyRevisionFallback(result, null);
            return result;
        }
        String json = objectSlice(analysis);
        if (hasText(json)) {
            Integer score = extractJsonInt(json, "overallScore");
            if (score != null) {
                result.setOverallScore(Integer.valueOf(clamp(score.intValue(), 0, 100)));
                result.setStrengths(extractJsonArray(json, "strengths"));
                result.setWeaknesses(extractJsonArray(json, "weaknesses"));
                result.setSuggestions(extractJsonArray(json, "suggestions"));
                result.setScoreBreakdown(extractScoreBreakdown(json));
                result.setRevisionSuggestions(extractRevisionSuggestions(json));
                applyRevisionFallback(result, analysis);
                return result;
            }
        }
        result.setOverallScore(Integer.valueOf(extractFirstScore(analysis, ResumeDiagnosisConstants.DEFAULT_SCORE)));
        List<String> suggestions = new ArrayList<String>();
        suggestions.add(analysis);
        result.setSuggestions(suggestions);
        applyRevisionFallback(result, analysis);
        return result;
    }

    public ResumeRevisionPlanDto buildRevisionPlan(List<ResumeRevisionSuggestionDto> suggestions, List<String> contextSources) {
        List<ResumeRevisionSuggestionDto> safeSuggestions =
                suggestions == null ? new ArrayList<ResumeRevisionSuggestionDto>() : suggestions;
        ResumeRevisionPlanDto plan = new ResumeRevisionPlanDto();
        int high = 0;
        for (ResumeRevisionSuggestionDto suggestion : safeSuggestions) {
            if (suggestion != null && "HIGH".equalsIgnoreCase(suggestion.getPriority())) {
                high += 1;
            }
        }
        plan.setTotalSuggestions(Integer.valueOf(safeSuggestions.size()));
        plan.setHighPrioritySuggestions(Integer.valueOf(high));
        plan.setOverallPriority(high > 0 ? "HIGH" : safeSuggestions.isEmpty() ? "NONE" : "MEDIUM");
        plan.setNextAction(safeSuggestions.isEmpty()
                ? "补充简历文本或目标岗位后再次诊断"
                : "优先处理高优先级建议，修改后再次诊断验证分数变化");
        plan.setContextSources(contextSources);
        plan.setContextSummary(join(contextSources, " / "));
        return plan;
    }

    public int extractFirstScore(String text, int fallback) {
        if (!hasText(text)) {
            return clamp(fallback, 0, 100);
        }
        Matcher matcher = Pattern.compile("\\d+").matcher(text);
        while (matcher.find()) {
            try {
                int value = Integer.parseInt(matcher.group());
                if (value >= 0 && value <= 100) {
                    return value;
                }
            } catch (Exception ignored) {
            }
        }
        return clamp(fallback, 0, 100);
    }

    public ResumeKeywordStatusDto extractKeywordStatus(ResumeRecordDto resume) {
        ResumeKeywordStatusDto status = new ResumeKeywordStatusDto();
        status.setResumeId(resume == null ? null : resume.getResumeId());
        List<ResumeKeywordDto> keywords = extractKeywords(resume);
        status.setKeywords(keywords);
        status.setStatus(keywords.isEmpty()
                ? ResumeDiagnosisConstants.STATUS_EMPTY
                : ResumeDiagnosisConstants.STATUS_READY);
        return status;
    }

    public List<ResumeKeywordDto> extractKeywords(ResumeRecordDto resume) {
        List<KeywordSpec> out = new ArrayList<KeywordSpec>();
        if (resume == null) {
            return new ArrayList<ResumeKeywordDto>();
        }
        addTokens(out, ResumeDiagnosisConstants.CATEGORY_GOAL, resume.getTargetJob(), 76, "target job");
        addTokens(out, ResumeDiagnosisConstants.CATEGORY_BACKGROUND, resume.getTitle(), 48, "title");
        if (resume.getUpdatedAt() != null) {
            add(out, ResumeDiagnosisConstants.CATEGORY_GROWTH, String.valueOf(resume.getUpdatedAt().getYear()), 54, "updated year");
        }
        if (hasText(resume.getParsedContent())) {
            String parsed = resume.getParsedContent();
            addJsonFieldText(out, ResumeDiagnosisConstants.CATEGORY_SKILL, parsed, "skills", 82);
            addJsonFieldText(out, ResumeDiagnosisConstants.CATEGORY_BACKGROUND, parsed, "education", 66);
            addJsonFieldText(out, ResumeDiagnosisConstants.CATEGORY_SKILL, parsed, "projects", 72);
            addJsonFieldText(out, ResumeDiagnosisConstants.CATEGORY_GROWTH, parsed, "experience", 70);
            addJsonFieldText(out, ResumeDiagnosisConstants.CATEGORY_GROWTH, parsed, "work", 70);
            addJsonFieldText(out, ResumeDiagnosisConstants.CATEGORY_SKILL, parsed, "rawContent", 62);
            if (out.isEmpty()) {
                addTokens(out, ResumeDiagnosisConstants.CATEGORY_SKILL, parsed, 54, "parsed content");
            }
        }
        List<KeywordSpec> merged = merge(out);
        List<ResumeKeywordDto> result = new ArrayList<ResumeKeywordDto>();
        for (int i = 0; i < merged.size() && i < ResumeDiagnosisConstants.MAX_KEYWORDS; i++) {
            KeywordSpec spec = merged.get(i);
            ResumeKeywordDto dto = new ResumeKeywordDto();
            dto.setCategory(spec.category);
            dto.setLabel(spec.label);
            dto.setWeight(Integer.valueOf(spec.weight));
            dto.setEvidence(spec.evidence);
            result.add(dto);
        }
        return result;
    }

    private void addJsonFieldText(List<KeywordSpec> out, String category, String json, String field, int weight) {
        List<String> values = extractJsonValues(json, field);
        for (String value : values) {
            addTokens(out, category, value, weight, field);
        }
    }

    private List<String> extractJsonValues(String json, String field) {
        List<String> values = new ArrayList<String>();
        Pattern pattern = Pattern.compile("\"" + Pattern.quote(field) + "\"\\s*:\\s*(\\[[\\s\\S]*?\\]|\"(?:\\\\.|[^\"])*\"|\\{[\\s\\S]*?\\})");
        Matcher matcher = pattern.matcher(json == null ? "" : json);
        while (matcher.find()) {
            String raw = matcher.group(1);
            if (raw.startsWith("[")) {
                values.addAll(extractStringValues(raw));
            } else if (raw.startsWith("\"")) {
                values.add(unquote(raw));
            } else {
                values.add(raw);
            }
        }
        return values;
    }

    private String objectSlice(String text) {
        if (text == null) {
            return null;
        }
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return text.substring(start, end + 1);
        }
        return null;
    }

    private Integer extractJsonInt(String json, String field) {
        Matcher matcher = Pattern.compile("\"" + Pattern.quote(field) + "\"\\s*:\\s*(-?\\d+)").matcher(json == null ? "" : json);
        if (matcher.find()) {
            try {
                return Integer.valueOf(Integer.parseInt(matcher.group(1)));
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private List<String> extractJsonArray(String json, String field) {
        Matcher matcher = Pattern.compile("\"" + Pattern.quote(field) + "\"\\s*:\\s*\\[([\\s\\S]*?)\\]").matcher(json == null ? "" : json);
        if (!matcher.find()) {
            return new ArrayList<String>();
        }
        return extractStringValues(matcher.group(1));
    }

    private void applyRevisionFallback(ResumeDiagnosisResultDto result, String analysis) {
        if (result.getRevisionSuggestions().isEmpty()) {
            List<ResumeRevisionSuggestionDto> suggestions = new ArrayList<ResumeRevisionSuggestionDto>();
            List<String> plainSuggestions = result.getSuggestions();
            if (plainSuggestions != null && !plainSuggestions.isEmpty()) {
                for (int i = 0; i < plainSuggestions.size(); i += 1) {
                    String text = plainSuggestions.get(i);
                    if (hasText(text)) {
                        suggestions.add(fallbackSuggestion("rev-" + (i + 1), text, i == 0 ? "HIGH" : "MEDIUM"));
                    }
                }
            } else if (hasText(analysis)) {
                suggestions.add(fallbackSuggestion("rev-1", analysis, "MEDIUM"));
            }
            result.setRevisionSuggestions(suggestions);
        }
        if (result.getRevisionPlan() == null) {
            result.setRevisionPlan(buildRevisionPlan(result.getRevisionSuggestions(), result.getContextSources()));
        }
    }

    private ResumeRevisionSuggestionDto fallbackSuggestion(String id, String text, String priority) {
        ResumeRevisionSuggestionDto suggestion = new ResumeRevisionSuggestionDto();
        suggestion.setSuggestionId(id);
        suggestion.setIssueType("CONTENT_EVIDENCE");
        suggestion.setPriority(priority);
        suggestion.setResumeSection("experience");
        suggestion.setProblem("简历内容仍需与目标岗位证据对齐");
        suggestion.setAction(trim(text, 300));
        suggestion.setRewriteExample("用“动作 + 技术/方法 + 指标结果 + 个人贡献”重写一条经历。");
        suggestion.setEvidence(trim(text, 160));
        suggestion.setStatus("TODO");
        suggestion.setContextSource("fallback");
        return suggestion;
    }

    private List<ResumeRevisionSuggestionDto> extractRevisionSuggestions(String json) {
        String array = extractJsonArrayBody(json, "revisionSuggestions");
        if (!hasText(array)) {
            array = extractJsonArrayBody(json, "revision_suggestions");
        }
        List<ResumeRevisionSuggestionDto> suggestions = new ArrayList<ResumeRevisionSuggestionDto>();
        if (!hasText(array)) {
            return suggestions;
        }
        List<String> objects = extractJsonObjects(array);
        for (int i = 0; i < objects.size(); i += 1) {
            ResumeRevisionSuggestionDto suggestion = parseRevisionSuggestion(objects.get(i), i + 1);
            if (suggestion != null) {
                suggestions.add(suggestion);
            }
        }
        return suggestions;
    }

    private List<ResumeDiagnosisScoreItemDto> extractScoreBreakdown(String json) {
        String array = extractJsonArrayBody(json, "scoreBreakdown");
        List<ResumeDiagnosisScoreItemDto> items = new ArrayList<ResumeDiagnosisScoreItemDto>();
        for (String object : extractJsonObjects(array)) {
            String name = extractJsonString(object, "name");
            Integer score = extractJsonInt(object, "score");
            Integer maxScore = extractJsonInt(object, "maxScore");
            if (!hasText(name) || score == null || maxScore == null) {
                continue;
            }
            ResumeDiagnosisScoreItemDto item = new ResumeDiagnosisScoreItemDto();
            item.setName(name);
            item.setScore(Integer.valueOf(clamp(score.intValue(), 0, maxScore.intValue())));
            item.setMaxScore(Integer.valueOf(Math.max(0, maxScore.intValue())));
            item.setReason(extractJsonString(object, "reason"));
            items.add(item);
        }
        return items;
    }

    private ResumeRevisionSuggestionDto parseRevisionSuggestion(String json, int index) {
        String action = firstText(
                extractJsonString(json, "action"),
                extractJsonString(json, "suggestion"),
                extractJsonString(json, "text"));
        String problem = firstText(extractJsonString(json, "problem"), extractJsonString(json, "issue"));
        String rewrite = firstText(extractJsonString(json, "rewriteExample"), extractJsonString(json, "rewrite_example"));
        if (!hasText(action) && !hasText(problem) && !hasText(rewrite)) {
            return null;
        }
        ResumeRevisionSuggestionDto suggestion = new ResumeRevisionSuggestionDto();
        suggestion.setSuggestionId(firstText(extractJsonString(json, "suggestionId"), extractJsonString(json, "id"), "rev-" + index));
        suggestion.setIssueType(firstText(extractJsonString(json, "issueType"), extractJsonString(json, "issue_type"), "CONTENT_EVIDENCE"));
        suggestion.setPriority(normalizePriority(extractJsonString(json, "priority"), index));
        suggestion.setResumeSection(firstText(extractJsonString(json, "resumeSection"), extractJsonString(json, "section"), "experience"));
        suggestion.setProblem(problem);
        suggestion.setAction(action);
        suggestion.setRewriteExample(rewrite);
        suggestion.setEvidence(extractJsonString(json, "evidence"));
        suggestion.setTargetKeywords(extractJsonArray(json, "targetKeywords"));
        if (suggestion.getTargetKeywords().isEmpty()) {
            suggestion.setTargetKeywords(extractJsonArray(json, "target_keywords"));
        }
        suggestion.setStatus(firstText(extractJsonString(json, "status"), "TODO"));
        suggestion.setContextSource(firstText(extractJsonString(json, "contextSource"), extractJsonString(json, "context_source")));
        suggestion.setUpdatedAt(extractJsonString(json, "updatedAt"));
        return suggestion;
    }

    private String extractJsonArrayBody(String json, String field) {
        Matcher matcher = Pattern.compile("\"" + Pattern.quote(field) + "\"\\s*:\\s*\\[")
                .matcher(json == null ? "" : json);
        if (!matcher.find()) {
            return null;
        }
        int start = matcher.end();
        int depth = 1;
        boolean inString = false;
        boolean escaped = false;
        for (int i = start; i < json.length(); i += 1) {
            char ch = json.charAt(i);
            if (inString) {
                if (escaped) {
                    escaped = false;
                } else if (ch == '\\') {
                    escaped = true;
                } else if (ch == '"') {
                    inString = false;
                }
            } else if (ch == '"') {
                inString = true;
            } else if (ch == '[') {
                depth += 1;
            } else if (ch == ']') {
                depth -= 1;
                if (depth == 0) {
                    return json.substring(start, i);
                }
            }
        }
        return null;
    }

    private List<String> extractJsonObjects(String text) {
        List<String> objects = new ArrayList<String>();
        if (!hasText(text)) {
            return objects;
        }
        int depth = 0;
        int start = -1;
        boolean inString = false;
        boolean escaped = false;
        for (int i = 0; i < text.length(); i += 1) {
            char ch = text.charAt(i);
            if (inString) {
                if (escaped) {
                    escaped = false;
                } else if (ch == '\\') {
                    escaped = true;
                } else if (ch == '"') {
                    inString = false;
                }
            } else if (ch == '"') {
                inString = true;
            } else if (ch == '{') {
                if (depth == 0) {
                    start = i;
                }
                depth += 1;
            } else if (ch == '}') {
                depth -= 1;
                if (depth == 0 && start >= 0) {
                    objects.add(text.substring(start, i + 1));
                    start = -1;
                }
            }
        }
        return objects;
    }

    private String extractJsonString(String json, String field) {
        Matcher matcher = Pattern.compile("\"" + Pattern.quote(field) + "\"\\s*:\\s*\"((?:\\\\.|[^\"])*)\"")
                .matcher(json == null ? "" : json);
        if (matcher.find()) {
            return unescape(matcher.group(1));
        }
        return null;
    }

    private String normalizePriority(String value, int index) {
        String normalized = value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
        if ("HIGH".equals(normalized) || "MEDIUM".equals(normalized) || "LOW".equals(normalized)) {
            return normalized;
        }
        return index == 1 ? "HIGH" : "MEDIUM";
    }

    private String firstText(String first, String second) {
        return hasText(first) ? first : second;
    }

    private String firstText(String first, String second, String third) {
        if (hasText(first)) return first;
        if (hasText(second)) return second;
        return third;
    }

    private String trim(String value, int max) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.length() <= max ? trimmed : trimmed.substring(0, max);
    }

    private String join(List<String> values, String delimiter) {
        if (values == null || values.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (String value : values) {
            if (!hasText(value)) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append(delimiter);
            }
            builder.append(value.trim());
        }
        return builder.toString();
    }

    private List<String> extractStringValues(String text) {
        List<String> values = new ArrayList<String>();
        Matcher matcher = Pattern.compile("\"((?:\\\\.|[^\"])*)\"").matcher(text == null ? "" : text);
        while (matcher.find()) {
            String value = unescape(matcher.group(1));
            if (hasText(value)) {
                values.add(value.trim());
            }
        }
        return values;
    }

    private void addTokens(List<KeywordSpec> out, String category, String text, int weight, String evidence) {
        if (!hasText(text)) {
            return;
        }
        boolean includeTime = !ResumeDiagnosisConstants.CATEGORY_SKILL.equals(category);
        for (String token : tokenize(text, includeTime)) {
            int safeWeight = token.equalsIgnoreCase(text.trim()) ? weight : Math.max(35, weight - 8);
            add(out, category, token, safeWeight, evidence);
        }
    }

    private void add(List<KeywordSpec> out, String category, String label, int weight, String evidence) {
        String clean = normalizeLabel(label);
        if (!isUsefulToken(clean)) {
            return;
        }
        if (ResumeDiagnosisConstants.CATEGORY_SKILL.equals(category)
                && (isTimeToken(clean) || NON_SKILL_TOKENS.contains(clean))) {
            return;
        }
        out.add(new KeywordSpec(category, clean, clamp(weight, 1, 100), evidence));
    }

    private List<KeywordSpec> merge(List<KeywordSpec> specs) {
        Map<String, KeywordSpec> merged = new LinkedHashMap<String, KeywordSpec>();
        for (KeywordSpec spec : specs) {
            String key = spec.category + "::" + spec.label.toLowerCase(Locale.ROOT);
            KeywordSpec current = merged.get(key);
            if (current == null || spec.weight > current.weight) {
                merged.put(key, spec);
            }
        }
        List<KeywordSpec> result = new ArrayList<KeywordSpec>(merged.values());
        Collections.sort(result, new Comparator<KeywordSpec>() {
            public int compare(KeywordSpec left, KeywordSpec right) {
                return right.weight - left.weight;
            }
        });
        return result;
    }

    private List<String> tokenize(String text, boolean includeTime) {
        LinkedHashSet<String> out = new LinkedHashSet<String>();
        String normalized = normalizeLabel(text)
                .replaceAll("[，。；、,;|/\\\\()（）\\[\\]【】{}:：&]+", " ")
                .replaceAll("\\s+", " ")
                .trim();
        if (includeTime) {
            addTimeTokens(text, out);
        }
        String[] parts = normalized.split(" ");
        for (String part : parts) {
            String p = part == null ? "" : part.trim();
            if (!hasText(p) || isContactLike(p) || (!includeTime && isTimeToken(p))) {
                continue;
            }
            for (String term : KNOWN_TERMS) {
                if (containsIgnoreCase(p, term)) {
                    out.add(term);
                }
            }
            if (p.matches("[A-Za-z][A-Za-z0-9#.+-]{1,23}")) {
                out.add(p);
            } else if (p.matches("[A-Za-z0-9#.+-]+")) {
                String[] tokens = p.split("(?=[A-Z])|[-_]+");
                for (String token : tokens) {
                    if (isUsefulToken(token)) {
                        out.add(token);
                    }
                }
            } else {
                addMeaningfulChineseTokens(p, out);
            }
        }
        List<String> result = new ArrayList<String>();
        for (String token : out) {
            if (isUsefulToken(token)) {
                result.add(token);
            }
        }
        return result;
    }

    private void addMeaningfulChineseTokens(String text, Set<String> out) {
        String zh = text.replaceAll("[^\\p{IsHan}A-Za-z0-9#.+-]", "");
        if (!hasText(zh)) {
            return;
        }
        if (zh.length() <= 6 && looksLikeCareerToken(zh)) {
            out.add(zh);
        }
    }

    private void addTimeTokens(String label, Set<String> out) {
        Matcher matcher = Pattern.compile("(?<!\\d)((?:19|20)\\d{2})(?:[./年]?(0?[1-9]|1[0-2])月?)?")
                .matcher(label == null ? "" : label);
        while (matcher.find()) {
            String year = matcher.group(1);
            String month = matcher.group(2);
            out.add(month == null ? year : year + "." + month.replaceFirst("^0", ""));
        }
    }

    private boolean looksLikeCareerToken(String token) {
        if (!isUsefulToken(token)) {
            return false;
        }
        for (String term : KNOWN_TERMS) {
            if (containsIgnoreCase(token, term)) {
                return true;
            }
        }
        return token.matches(".*(工程师|开发|算法|数据|产品|运营|设计|实习|项目|课程|竞赛|证书|本科|硕士|博士|学校|大学|学院|专业|面试|校招|转行|英语).*");
    }

    private boolean isUsefulToken(String token) {
        String clean = normalizeLabel(token);
        if (!hasText(clean)) return false;
        if (clean.length() < 2 && !clean.matches("AI|UI|UX")) return false;
        if (clean.length() > 24) return false;
        if (isContactLike(clean)) return false;
        if (clean.matches("(?i).*(email|mail|phone|mobile|wechat|weixin|github|linkedin).*")) return false;
        if (clean.matches("\\+?\\d{2,}[-\\d\\s]{4,}")) return false;
        if (clean.matches("\\d+(?:\\.\\d+)+") && !clean.matches("(?:19|20)\\d{2}(?:\\.\\d{1,2})?")) return false;
        if (clean.matches("\\d+") && !clean.matches("(19|20)\\d{2}")) return false;
        return !STOPWORDS.contains(clean.toLowerCase(Locale.ROOT));
    }

    private String normalizeLabel(String value) {
        return value == null ? "" : value
                .replace("：", ":")
                .replaceAll("\\s+", " ")
                .replaceAll("^[,.;:!?，。；：！？]+|[,.;:!?，。；：！？]+$", "")
                .trim();
    }

    private boolean isContactLike(String value) {
        if (!hasText(value)) return false;
        String clean = normalizeLabel(value);
        String lower = clean.toLowerCase(Locale.ROOT);
        if (CONTACT_TOKENS.contains(lower)) return true;
        if (lower.contains("@")) return true;
        if (lower.matches("https?://.*|www\\..*")) return true;
        if (lower.matches("\\+?86")) return true;
        return lower.matches("\\+?\\d[\\d\\s-]{6,}\\d");
    }

    private boolean isTimeToken(String value) {
        if (!hasText(value)) return false;
        return normalizeLabel(value).matches("(?:19|20)\\d{2}(?:\\.\\d{1,2})?");
    }

    private boolean containsIgnoreCase(String text, String term) {
        return text != null && term != null && text.toLowerCase(Locale.ROOT).contains(term.toLowerCase(Locale.ROOT));
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private String unquote(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.length() >= 2 && trimmed.startsWith("\"") && trimmed.endsWith("\"")) {
            return unescape(trimmed.substring(1, trimmed.length() - 1));
        }
        return trimmed;
    }

    private String unescape(String value) {
        return value == null ? null : value.replace("\\\"", "\"").replace("\\\\", "\\");
    }

    private static Set<String> setOf(String[] values) {
        return new LinkedHashSet<String>(Arrays.asList(values));
    }

    private static class KeywordSpec {
        private final String category;
        private final String label;
        private final int weight;
        private final String evidence;

        private KeywordSpec(String category, String label, int weight, String evidence) {
            this.category = category;
            this.label = label;
            this.weight = weight;
            this.evidence = evidence;
        }
    }
}
