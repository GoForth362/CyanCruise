package v620.cc001.cloud01.app01.mservice;

import v620.cc001.cloud01.app01.mservice.ai.impl.DefaultResumeDiagnosisAnalyzer;
import v620.cc001.cloud01.app01.mservice.ai.impl.DefaultResumeDiagnosisAnalyzer;
import org.junit.jupiter.api.Test;
import v620.base.helper.career.ResumeDiagnosisService;
import v620.cc001.base.common.dto.career.ResumeDiagnosisRequest;
import v620.cc001.base.common.dto.career.ResumeDiagnosisResultDto;
import v620.cc001.base.common.dto.career.ResumeDiagnosisScoreItemDto;
import v620.cc001.base.common.dto.career.ResumeRevisionSuggestionDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultResumeDiagnosisAnalyzerTest {

    @Test
    void returnsExplainableFourPartScore() {
        ResumeDiagnosisRequest request = new ResumeDiagnosisRequest();
        request.setTargetJob("后端开发");
        request.setJobDescription("Java Spring Redis 项目经验");
        String text = "教育背景 本科。技能 Java Spring Redis。项目经历：负责开发接口，优化查询并上线交付，性能提升 40%。";

        String analysis = new DefaultResumeDiagnosisAnalyzer().analyze(request, text);
        ResumeDiagnosisResultDto result = new ResumeDiagnosisService().parseAnalysis(analysis);

        int scoreTotal = 0;
        int maxTotal = 0;
        for (ResumeDiagnosisScoreItemDto item : result.getScoreBreakdown()) {
            scoreTotal += item.getScore().intValue();
            maxTotal += item.getMaxScore().intValue();
            assertTrue(item.getReason().length() > 0);
        }
        assertEquals(4, result.getScoreBreakdown().size());
        assertEquals(100, maxTotal);
        assertEquals(result.getOverallScore().intValue(), scoreTotal);
    }

    @Test
    void missingEvidenceProducesSpecificChineseSuggestions() {
        ResumeDiagnosisRequest request = new ResumeDiagnosisRequest();
        request.setTargetJob("后端开发");
        request.setJobDescription("Java Redis 高并发项目经验");

        String analysis = new DefaultResumeDiagnosisAnalyzer().analyze(request, "学习计算机知识，认真负责。 ");
        ResumeDiagnosisResultDto result = new ResumeDiagnosisService().parseAnalysis(analysis);

        assertTrue(result.getWeaknesses().toString().contains("数字"));
        assertTrue(result.getSuggestions().toString().contains("数量"));
        assertTrue(result.getRevisionSuggestions().size() >= 3);
        assertFalse(analysis.contains("目标 JD"));
        assertFalse(result.getRevisionSuggestions().toString().contains("JD"));
    }

    @Test
    void selectedResumeTargetRoleChangesMatchingScoreAndAdvice() {
        String frontendResume = "项目经历：使用 Vue、TypeScript、HTML 和 CSS 开发前端页面，完成组件优化并上线。";
        ResumeDiagnosisRequest frontend = new ResumeDiagnosisRequest();
        frontend.setTargetJob("前端开发");
        ResumeDiagnosisRequest backend = new ResumeDiagnosisRequest();
        backend.setTargetJob("后端开发");
        ResumeDiagnosisService helper = new ResumeDiagnosisService();

        ResumeDiagnosisResultDto frontendResult = helper.parseAnalysis(
                new DefaultResumeDiagnosisAnalyzer().analyze(frontend, frontendResume));
        ResumeDiagnosisResultDto backendResult = helper.parseAnalysis(
                new DefaultResumeDiagnosisAnalyzer().analyze(backend, frontendResume));

        assertTrue(frontendResult.getOverallScore().intValue() > backendResult.getOverallScore().intValue());
        boolean mentionsBackendRole = false;
        for (ResumeRevisionSuggestionDto suggestion : backendResult.getRevisionSuggestions()) {
            if ((suggestion.getAction() != null && suggestion.getAction().contains("后端开发"))
                    || (suggestion.getRewriteExample() != null && suggestion.getRewriteExample().contains("后端开发"))) {
                mentionsBackendRole = true;
            }
        }
        assertTrue(mentionsBackendRole);
    }
}
