package v620.base.helper.career;

import org.junit.jupiter.api.Test;
import v620.cc001.base.common.dto.career.InterviewReportDto;
import v620.cc001.base.common.dto.career.InterviewSessionDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InterviewAiHelperTest {
    private final InterviewAiHelper helper = new InterviewAiHelper();

    @Test
    void buildsChinesePromptAndLimitsLongContext() {
        StringBuilder longResume = new StringBuilder();
        for (int i = 0; i < 7000; i++) longResume.append('简');
        String prompt = helper.questionPrompt("后端开发工程师", "Normal", longResume.toString(), "在校生", "", true);
        assertTrue(prompt.contains("目标岗位：后端开发工程师"));
        assertTrue(prompt.length() < 9000);
    }

    @Test
    void createsTransparentTemporaryQuestionAndBasicRulesReport() {
        String question = helper.temporaryQuestion("后端开发工程师", "Hard", 2, false);
        InterviewSessionDto session = new InterviewSessionDto();
        session.setPositionName("后端开发工程师");

        InterviewReportDto report = helper.basicRulesReport(session,
                "[候选人] 我负责接口开发并完成上线。", 1);

        assertTrue(question.startsWith("【基础练习题·进阶】"));
        assertEquals(InterviewReportDto.ANALYSIS_SOURCE_BASIC_RULES, report.getAnalysisSource());
        assertEquals(Integer.valueOf(1), report.getTotalQuestions());
        assertTrue(report.getTextSummary().contains("不是 AI 深度分析"));
    }
}
