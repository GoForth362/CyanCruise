package v620.base.helper.career;

import v620.cc001.base.common.dto.career.ResumeDiagnosisConstants;
import v620.cc001.base.common.dto.career.ResumeDiagnosisResultDto;
import v620.cc001.base.common.dto.career.ResumeKeywordDto;
import v620.cc001.base.common.dto.career.ResumeKeywordStatusDto;
import v620.cc001.base.common.dto.career.ResumeRecordDto;

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
                return result;
            }
        }
        result.setOverallScore(Integer.valueOf(extractFirstScore(analysis, ResumeDiagnosisConstants.DEFAULT_SCORE)));
        List<String> suggestions = new ArrayList<String>();
        suggestions.add(analysis);
        result.setSuggestions(suggestions);
        return result;
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
