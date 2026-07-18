package v620.base.helper.career;

import org.junit.jupiter.api.Test;
import v620.cc001.base.common.dto.career.AssessmentAnswerSnapshot;
import v620.cc001.base.common.dto.career.AssessmentOptionDto;
import v620.cc001.base.common.dto.career.AssessmentQuestionDto;
import v620.cc001.base.common.dto.career.AssessmentScaleDto;
import v620.cc001.base.common.dto.career.AssessmentScoreResult;
import v620.cc001.base.common.dto.career.AssessmentSubmitRequest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AssessmentScoringServiceTest {

    private final AssessmentScoringService service = new AssessmentScoringService();

    @Test
    void scoresMbtiPortrait() {
        AssessmentScaleDto scale = scale("MBTI 职业性格测评",
                question(1L, option(11L, "E"), option(12L, "I")),
                question(2L, option(21L, "N"), option(22L, "S")),
                question(3L, option(31L, "T"), option(32L, "F")),
                question(4L, option(41L, "P"), option(42L, "J")));

        AssessmentSubmitRequest request = answers(
                answer(1L, 11L),
                answer(2L, 21L),
                answer(3L, 31L),
                answer(4L, 41L));

        AssessmentScoreResult result = service.score(scale, request);

        assertEquals("ENTP", result.getResultSummary());
        assertEquals(Integer.valueOf(1), result.getDimensionCounts().get("E"));
        assertEquals(Integer.valueOf(1), result.getDimensionCounts().get("N"));
        assertEquals("COMPLETED", result.getStatus());
    }

    @Test
    void mbtiTieUsesFirstDimension() {
        AssessmentScaleDto scale = scale("MBTI",
                question(1L, option(11L, "E")),
                question(2L, option(21L, "I")));

        AssessmentSubmitRequest request = answers(answer(1L, 11L), answer(2L, 21L));

        AssessmentScoreResult result = service.score(scale, request);

        assertEquals("ESTJ", result.getResultSummary());
    }

    @Test
    void scoresNonMbtiByTopThreeDimensions() {
        AssessmentScaleDto scale = scale("Holland 职业兴趣",
                question(1L, option(11L, "R")),
                question(2L, option(21L, "R")),
                question(3L, option(31L, "I")),
                question(4L, option(41L, "A")),
                question(5L, option(51L, "A")),
                question(6L, option(61L, "A")),
                question(7L, option(71L, "S")));

        AssessmentSubmitRequest request = answers(
                answer(1L, 11L),
                answer(2L, 21L),
                answer(3L, 31L),
                answer(4L, 41L),
                answer(5L, 51L),
                answer(6L, 61L),
                answer(7L, 71L));

        AssessmentScoreResult result = service.score(scale, request);

        assertEquals("ARI", result.getResultSummary());
    }

    @Test
    void invalidOptionIsSnapshottedButNotCounted() {
        AssessmentScaleDto scale = scale("Holland", question(1L, option(11L, "R")));
        AssessmentSubmitRequest request = answers(answer(1L, 999L));

        AssessmentScoreResult result = service.score(scale, request);
        AssessmentAnswerSnapshot snapshot = result.getAnswers().get(0);

        assertEquals("N/A", result.getResultSummary());
        assertTrue(result.getDimensionCounts().isEmpty());
        assertEquals(Long.valueOf(1L), snapshot.getQuestionId());
        assertEquals(Long.valueOf(999L), snapshot.getOptionId());
        assertFalse(snapshot.isValidOption());
        assertEquals(BigDecimal.ZERO, snapshot.getScoreSnapshot());
    }

    @Test
    void answerSnapshotKeepsDimensionAndScore() {
        AssessmentScaleDto scale = scale("Holland", question(1L, option(11L, "R", new BigDecimal("2.50"))));
        AssessmentSubmitRequest request = answers(answer(1L, 11L));

        AssessmentScoreResult result = service.score(scale, request);
        AssessmentAnswerSnapshot snapshot = result.getAnswers().get(0);

        assertTrue(snapshot.isValidOption());
        assertEquals("R", snapshot.getDimensionCode());
        assertEquals(new BigDecimal("2.50"), snapshot.getScoreSnapshot());
    }

    @Test
    void scoresMultiChoiceAnswers() {
        AssessmentQuestionDto question = question(1L, option(11L, "R"), option(12L, "I"), option(13L, "A"));
        question.setQuestionType("MULTI");
        AssessmentScaleDto scale = scale("Holland", question);

        AssessmentSubmitRequest request = new AssessmentSubmitRequest();
        Map<Long, java.util.List<Long>> answers = new LinkedHashMap<Long, java.util.List<Long>>();
        answers.put(Long.valueOf(1L), Arrays.asList(Long.valueOf(11L), Long.valueOf(12L)));
        request.setAnswerOptionIds(answers);

        AssessmentScoreResult result = service.score(scale, request);

        assertEquals("IR", result.getResultSummary());
        assertEquals(2, result.getAnswers().size());
        assertEquals(Integer.valueOf(1), result.getDimensionCounts().get("R"));
        assertEquals(Integer.valueOf(1), result.getDimensionCounts().get("I"));
    }

    private AssessmentScaleDto scale(String title, AssessmentQuestionDto... questions) {
        AssessmentScaleDto scale = new AssessmentScaleDto();
        scale.setScaleId(Long.valueOf(100L));
        scale.setTitle(title);
        scale.setQuestions(Arrays.asList(questions));
        return scale;
    }

    private AssessmentQuestionDto question(Long questionId, AssessmentOptionDto... options) {
        AssessmentQuestionDto question = new AssessmentQuestionDto();
        question.setQuestionId(questionId);
        question.setOptions(Arrays.asList(options));
        for (AssessmentOptionDto option : options) {
            option.setQuestionId(questionId);
        }
        return question;
    }

    private AssessmentOptionDto option(Long optionId, String dimensionCode) {
        return option(optionId, dimensionCode, BigDecimal.ONE);
    }

    private AssessmentOptionDto option(Long optionId, String dimensionCode, BigDecimal score) {
        AssessmentOptionDto option = new AssessmentOptionDto();
        option.setOptionId(optionId);
        option.setDimensionCode(dimensionCode);
        option.setScoreValue(score);
        return option;
    }

    private AssessmentSubmitRequest answers(long[]... entries) {
        AssessmentSubmitRequest request = new AssessmentSubmitRequest();
        Map<Long, Long> answers = new LinkedHashMap<Long, Long>();
        for (long[] entry : entries) {
            answers.put(Long.valueOf(entry[0]), Long.valueOf(entry[1]));
        }
        request.setAnswers(answers);
        return request;
    }

    private long[] answer(long questionId, long optionId) {
        return new long[] { questionId, optionId };
    }
}
