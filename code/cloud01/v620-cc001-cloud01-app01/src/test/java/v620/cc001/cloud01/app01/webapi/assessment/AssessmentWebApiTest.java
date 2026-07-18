package v620.cc001.cloud01.app01.webapi.assessment;

import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryAssessmentCatalog;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryAssessmentResultStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerProfileStorage;
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
import v620.cc001.cloud01.app01.mservice.application.AssessmentApplicationService;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryAssessmentCatalog;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryAssessmentResultStorage;
import v620.cc001.cloud01.app01.mservice.application.CareerProfileApplicationService;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerProfileStorage;

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

        AssessmentScaleDto attempt = webApi.start(userId, Long.valueOf(1001L));
        AssessmentScoreResult result = webApi.submit(userId, null, answersForAttempt(attempt));

        UserProfileSnapshot snapshot = profileService.getSnapshot(userId);
        CareerUserProfileDto profile = profileService.getProfile(userId);

        assertEquals("COMPLETED", result.getStatus());
        assertEquals(16, result.getAnswers().size());
        assertNotNull(snapshot.getAssessment());
        assertEquals(Long.valueOf(1001L), snapshot.getAssessment().getScaleId());
        assertEquals(result.getResultSummary(), snapshot.getAssessment().getSummary());
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
        AssessmentScaleDto attempt = webApi.start(userId, Long.valueOf(1001L));
        AssessmentScoreResult result = webApi.submit(userId, null, answersForAttempt(attempt));

        assertEquals(Integer.valueOf(16), webApi.scales().get(0).getQuestionCount());
        assertEquals(16, scale.getQuestions().size());
        assertEquals(1, webApi.records(userId).size());
        assertEquals(result.getRecordId(), webApi.record(userId, result.getRecordId()).getRecordId());
    }

    @Test
    void webApiSavesAndDeletesAssessmentQuestionForAdminRoute() {
        AssessmentWebApi webApi = new AssessmentWebApi(new AssessmentApplicationService(
                new AssessmentScoringService(),
                new InMemoryAssessmentCatalog(),
                new InMemoryAssessmentResultStorage(),
                profileService()));
        AssessmentQuestionDto question = new AssessmentQuestionDto();
        question.setQuestionText("你更容易在哪种任务中进入心流？");
        question.setDimensionCode("FLOW");
        question.setOptions(Arrays.asList(option(null, "ANALYZE"), option(null, "CREATE")));

        AssessmentQuestionDto saved = webApi.saveQuestion(Long.valueOf(1002L), question);

        assertNotNull(saved.getQuestionId());
        assertEquals(Integer.valueOf(13), webApi.questions(Long.valueOf(1002L)).getPoolQuestionCount());
        assertEquals(Boolean.TRUE, webApi.deleteQuestion(Long.valueOf(1002L), saved.getQuestionId()));
        assertEquals(Integer.valueOf(12), webApi.questions(Long.valueOf(1002L)).getPoolQuestionCount());
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

    private AssessmentSubmitRequest answersForAttempt(AssessmentScaleDto attempt) {
        AssessmentSubmitRequest request = new AssessmentSubmitRequest();
        request.setScaleId(attempt.getScaleId());
        request.setAttemptId(attempt.getAttemptId());
        Map<Long, Long> answers = new LinkedHashMap<Long, Long>();
        for (AssessmentQuestionDto question : attempt.getQuestions()) {
            answers.put(question.getQuestionId(), question.getOptions().get(0).getOptionId());
        }
        request.setAnswers(answers);
        return request;
    }

    private long[] answer(long questionId, long optionId) {
        return new long[] { questionId, optionId };
    }
}
