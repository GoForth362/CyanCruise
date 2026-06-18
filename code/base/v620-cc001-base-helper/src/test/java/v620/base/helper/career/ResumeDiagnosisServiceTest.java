package v620.base.helper.career;

import org.junit.jupiter.api.Test;
import v620.cc001.base.common.dto.career.ResumeDiagnosisConstants;
import v620.cc001.base.common.dto.career.ResumeDiagnosisResultDto;
import v620.cc001.base.common.dto.career.ResumeKeywordDto;
import v620.cc001.base.common.dto.career.ResumeKeywordStatusDto;
import v620.cc001.base.common.dto.career.ResumeRecordDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResumeDiagnosisServiceTest {

    private final ResumeDiagnosisService service = new ResumeDiagnosisService();

    @Test
    void parsesStructuredDiagnosisJson() {
        ResumeDiagnosisResultDto result = service.parseAnalysis("{\"overallScore\":88,\"strengths\":[\"项目清晰\"],\"weaknesses\":[\"量化不足\"],\"suggestions\":[\"补充指标\"],\"revisionSuggestions\":[{\"suggestionId\":\"rev-a\",\"issueType\":\"METRIC\",\"priority\":\"HIGH\",\"resumeSection\":\"projects\",\"problem\":\"缺少结果\",\"action\":\"补充转化率\",\"rewriteExample\":\"将结果补成百分比\",\"evidence\":\"项目 A\",\"targetKeywords\":[\"Java\",\"Redis\"],\"status\":\"TODO\"}]}");

        assertEquals(Integer.valueOf(88), result.getOverallScore());
        assertEquals("项目清晰", result.getStrengths().get(0));
        assertEquals("量化不足", result.getWeaknesses().get(0));
        assertEquals("补充指标", result.getSuggestions().get(0));
        assertEquals("rev-a", result.getRevisionSuggestions().get(0).getSuggestionId());
        assertEquals("HIGH", result.getRevisionSuggestions().get(0).getPriority());
        assertEquals("Redis", result.getRevisionSuggestions().get(0).getTargetKeywords().get(1));
        assertEquals(Integer.valueOf(1), result.getRevisionPlan().getTotalSuggestions());
        assertEquals(Integer.valueOf(1), result.getRevisionPlan().getHighPrioritySuggestions());
    }

    @Test
    void fallsBackToFirstValidScoreFromText() {
        ResumeDiagnosisResultDto result = service.parseAnalysis("整体匹配分 91/100，建议补充 Spring 项目。");

        assertEquals(Integer.valueOf(91), result.getOverallScore());
        assertTrue(result.getSuggestions().get(0).contains("Spring"));
        assertEquals(1, result.getRevisionSuggestions().size());
        assertTrue(result.getRevisionSuggestions().get(0).getAction().contains("Spring"));
    }

    @Test
    void usesDefaultScoreForEmptyOrInvalidAnalysis() {
        assertEquals(Integer.valueOf(ResumeDiagnosisConstants.DEFAULT_SCORE), service.parseAnalysis(null).getOverallScore());
        assertEquals(Integer.valueOf(ResumeDiagnosisConstants.DEFAULT_SCORE), service.parseAnalysis("没有分数").getOverallScore());
        assertEquals(Integer.valueOf(0), service.parseAnalysis(null).getRevisionPlan().getTotalSuggestions());
    }

    @Test
    void extractsFiltersAndMergesKeywords() {
        ResumeRecordDto resume = new ResumeRecordDto();
        resume.setResumeId(Long.valueOf(3L));
        resume.setTitle("Java 后端工程师 resume");
        resume.setTargetJob("Java 后端工程师");
        resume.setParsedContent("{\"skills\":[\"Java\",\"SpringBoot\",\"13800138000\",\"email\"],\"projects\":\"Redis 项目\",\"education\":\"本科 软件工程\",\"rawContent\":\"Git Linux Java\"}");
        resume.setUpdatedAt(LocalDateTime.of(2026, 5, 27, 10, 0));

        List<ResumeKeywordDto> keywords = service.extractKeywords(resume);

        assertTrue(containsLabel(keywords, "Java"));
        assertTrue(containsLabel(keywords, "SpringBoot"));
        assertTrue(containsLabel(keywords, "Redis"));
        assertFalse(containsLabel(keywords, "13800138000"));
        assertFalse(containsLabel(keywords, "email"));
        assertEquals(1, countCategoryLabel(keywords, ResumeDiagnosisConstants.CATEGORY_SKILL, "Java"));
    }

    @Test
    void returnsEmptyKeywordStatusWhenNoUsefulText() {
        ResumeRecordDto resume = new ResumeRecordDto();
        resume.setResumeId(Long.valueOf(5L));

        ResumeKeywordStatusDto status = service.extractKeywordStatus(resume);

        assertEquals(ResumeDiagnosisConstants.STATUS_EMPTY, status.getStatus());
        assertEquals(0, status.getKeywords().size());
    }

    private boolean containsLabel(List<ResumeKeywordDto> keywords, String label) {
        return countLabel(keywords, label) > 0;
    }

    private int countLabel(List<ResumeKeywordDto> keywords, String label) {
        int count = 0;
        for (ResumeKeywordDto keyword : keywords) {
            if (label.equals(keyword.getLabel())) {
                count++;
            }
        }
        return count;
    }

    private int countCategoryLabel(List<ResumeKeywordDto> keywords, String category, String label) {
        int count = 0;
        for (ResumeKeywordDto keyword : keywords) {
            if (category.equals(keyword.getCategory()) && label.equals(keyword.getLabel())) {
                count++;
            }
        }
        return count;
    }
}
