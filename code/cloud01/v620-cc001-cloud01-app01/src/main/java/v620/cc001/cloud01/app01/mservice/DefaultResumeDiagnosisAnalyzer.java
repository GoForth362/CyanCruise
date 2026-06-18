package v620.cc001.cloud01.app01.mservice;

import v620.cc001.base.common.dto.career.ResumeDiagnosisRequest;

/**
 * Deterministic local analyzer used until AI integration is available.
 */
public class DefaultResumeDiagnosisAnalyzer implements ResumeDiagnosisAnalyzer {

    public String analyze(ResumeDiagnosisRequest request, String resumeText) {
        String text = resumeText == null ? "" : resumeText;
        String jd = request == null ? null : request.getJobDescription();
        String targetJob = request == null ? null : request.getTargetJob();
        int score = 62;
        if (containsAny(text, new String[]{"Java", "Spring", "Python", "项目", "实习", "Redis"})) {
            score += 12;
        }
        if (hasText(jd) && overlaps(text, jd)) {
            score += 10;
        }
        if (hasText(targetJob) && overlaps(text, targetJob)) {
            score += 6;
        }
        if (text.length() > 500) {
            score += 6;
        }
        score = Math.max(0, Math.min(100, score));
        return "{\"overallScore\":" + score
                + ",\"strengths\":[\"简历包含可识别的岗位相关经历\"],"
                + "\"weaknesses\":[\"部分成果证据仍可继续量化\"],"
                + "\"suggestions\":[\"围绕目标 JD 补充关键词、项目指标和职责边界\"],"
                + "\"revisionSuggestions\":[{"
                + "\"suggestionId\":\"rev-1\","
                + "\"issueType\":\"CONTENT_EVIDENCE\","
                + "\"priority\":\"HIGH\","
                + "\"resumeSection\":\"projects\","
                + "\"problem\":\"项目经历需要补充与目标岗位直接相关的结果证据\","
                + "\"action\":\"补充目标岗位关键词、项目指标和个人职责边界\","
                + "\"rewriteExample\":\"使用核心技术完成某模块，带来可量化结果，并说明个人贡献。\","
                + "\"evidence\":\"fallback analyzer\","
                + "\"targetKeywords\":[\"JD\",\"项目指标\",\"职责边界\"],"
                + "\"status\":\"TODO\","
                + "\"contextSource\":\"fallback\"}]}";
    }

    private boolean overlaps(String resumeText, String jd) {
        String[] parts = jd.split("[^A-Za-z0-9\\p{IsHan}#.+-]+");
        for (String part : parts) {
            if (part != null && part.trim().length() >= 2 && resumeText.contains(part.trim())) {
                return true;
            }
        }
        return false;
    }

    private boolean containsAny(String text, String[] values) {
        for (String value : values) {
            if (text != null && text.contains(value)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
