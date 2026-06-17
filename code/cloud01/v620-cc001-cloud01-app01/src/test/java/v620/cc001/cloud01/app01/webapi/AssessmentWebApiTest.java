package v620.cc001.cloud01.app01.webapi;

import org.junit.jupiter.api.Test;
import v620.base.helper.career.AssessmentScoringService;
import v620.base.helper.career.CareerProfileBuildService;
import v620.base.helper.career.CareerProfileSnapshotMergeService;
import v620.cc001.base.common.dto.career.AssessmentOptionDto;
import v620.cc001.base.common.dto.career.AssessmentQuestionDto;
import v620.cc001.base.common.dto.career.AssessmentScaleDto;
import v620.cc001.base.common.dto.career.AssessmentScoreResult;
import v620.cc001.base.common.dto.career.AssessmentSubmitRequest;
import v620.cc001.base.common.dto.career.CareerUserProfileDto;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;
import v620.cc001.cloud01.app01.mservice.AssessmentApplicationService;
import v620.cc001.cloud01.app01.mservice.InMemoryAssessmentCatalog;
import v620.cc001.cloud01.app01.mservice.InMemoryAssessmentResultStorage;
import v620.cc001.cloud01.app01.mservice.CareerProfileApplicationService;
import v620.cc001.cloud01.app01.mservice.InMemoryCareerProfileStorage;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AssessmentWebApiTest {

    @Test
    void submitReturnsScoreAndRefreshesProfileAssessmentSignal() {
        String userId = "assessment-webapi-user-" + System.nanoTime();
        CareerProfileApplicationService profileService = profileService();
        AssessmentWebApi webApi = new AssessmentWebApi(new AssessmentApplicationService(
                new AssessmentScoringService(), profileService));

        AssessmentScoreResult result = webApi.submit(userId, mbtiScale(),
                answers(answer(1L, 11L), answer(2L, 21L), answer(3L, 31L), answer(4L, 41L)));

        UserProfileSnapshot snapshot = profileService.getSnapshot(userId);
        CareerUserProfileDto profile = profileService.getProfile(userId);

        assertEquals("COMPLETED", result.getStatus());
        assertEquals("ENTP", result.getResultSummary());
        assertEquals(Integer.valueOf(1), result.getDimensionCounts().get("E"));
        assertEquals(4, result.getAnswers().size());
        assertNotNull(snapshot.getAssessment());
        assertEquals(Long.valueOf(100L), snapshot.getAssessment().getScaleId());
        assertEquals("MBTI", snapshot.getAssessment().getScaleTitle());
        assertEquals("ENTP", snapshot.getAssessment().getSummary());
        assertTrue(profile.getReadiness().getHasAssessment().booleanValue());
    }

    @Test
    void exposesCatalogQuestionsAndRecordList() {
        String userId = "assessment-catalog-user-" + System.nanoTime();
        CareerProfileApplicationService profileService = profileService();
        AssessmentWebApi webApi = new AssessmentWebApi(new AssessmentApplicationService(
                new AssessmentScoringService(),
                new InMemoryAssessmentCatalog(),
                new InMemoryAssessmentResultStorage(),
                profileService));

        AssessmentScaleDto scale = webApi.questions(Long.valueOf(1001L));
        AssessmentSubmitRequest request = answers(answer(100101L, 100101L), answer(100102L, 100201L),
                answer(100103L, 100301L), answer(100104L, 100401L),
                answer(100105L, 100502L), answer(100106L, 100602L),
                answer(100107L, 100702L), answer(100108L, 100802L),
                answer(100109L, 100901L), answer(100110L, 101001L),
                answer(100111L, 101101L), answer(100112L, 101201L),
                answer(100113L, 101302L), answer(100114L, 101402L),
                answer(100115L, 101502L), answer(100116L, 101602L));
        request.setScaleId(scale.getScaleId());

        AssessmentScoreResult result = webApi.submit(userId, null, request);

        assertEquals(Integer.valueOf(16), webApi.scales().get(0).getQuestionCount());
        assertEquals(16, scale.getQuestions().size());
        assertEquals("ENTP", result.getResultSummary());
        assertEquals(1, webApi.records(userId).size());
        assertEquals(result.getRecordId(), webApi.record(userId, result.getRecordId()).getRecordId());
    }

    private CareerProfileApplicationService profileService() {
        return new CareerProfileApplicationService(
                new InMemoryCareerProfileStorage(),
                new CareerProfileSnapshotMergeService(),
                new CareerProfileBuildService());
    }

    private AssessmentScaleDto mbtiScale() {
        AssessmentScaleDto scale = new AssessmentScaleDto();
        scale.setScaleId(Long.valueOf(100L));
        scale.setTitle("MBTI");
        scale.setQuestions(Arrays.asList(
                question(1L, option(11L, "E"), option(12L, "I")),
                question(2L, option(21L, "N"), option(22L, "S")),
                question(3L, option(31L, "T"), option(32L, "F")),
                question(4L, option(41L, "P"), option(42L, "J"))));
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
        AssessmentOptionDto option = new AssessmentOptionDto();
        option.setOptionId(optionId);
        option.setDimensionCode(dimensionCode);
        option.setScoreValue(BigDecimal.ONE);
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
