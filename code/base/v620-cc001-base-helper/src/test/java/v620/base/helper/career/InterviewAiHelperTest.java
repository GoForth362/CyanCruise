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
    void fallbackQuestionChangesWithAnswerCount() {
        assertTrue(helper.fallbackQuestion("产品经理", 0).contains("产品经理"));
        assertTrue(helper.fallbackQuestion("产品经理", 2).contains("重新处理"));
    }

    @Test
    void fallbackReportHasBoundedScoresAndAdvice() {
        InterviewSessionDto session = new InterviewSessionDto(); session.setPositionName("数据分析师");
        InterviewReportDto report = helper.fallbackReport(session, 20);
        assertEquals(Integer.valueOf(78), report.getOverallScore());
        assertEquals(Integer.valueOf(20), report.getTotalQuestions());
        assertTrue(!report.getImprovements().isEmpty());
        assertEquals(100, helper.clampScore(120));
    }
}
