package v620.cc001.cloud01.app01.mservice;

import v620.cc001.cloud01.app01.mservice.storage.impl.FileCareerProfileStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryAssessmentCatalog;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryAssessmentResultStorage;
import v620.cc001.cloud01.app01.mservice.application.AssessmentApplicationService;
import v620.cc001.cloud01.app01.mservice.application.CareerProfileApplicationService;
import v620.cc001.cloud01.app01.mservice.storage.CareerProfileStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.FileCareerProfileStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryAssessmentCatalog;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryAssessmentResultStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import v620.base.helper.career.AssessmentScoringService;
import v620.base.helper.career.CareerProfileBuildService;
import v620.base.helper.career.CareerProfileSnapshotMergeService;
import v620.cc001.base.common.dto.career.AssessmentOptionDto;
import v620.cc001.base.common.dto.career.AssessmentQuestionDto;
import v620.cc001.base.common.dto.career.AssessmentScaleDto;
import v620.cc001.base.common.dto.career.AssessmentScoreResult;
import v620.cc001.base.common.dto.career.AssessmentSubmitRequest;
import v620.cc001.base.common.dto.career.CareerProfileOnboardingRequest;
import v620.cc001.base.common.dto.career.CareerProfilePreferencesRequest;
import v620.cc001.base.common.dto.career.CareerUserProfileDto;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AssessmentApplicationServiceTest {

    @TempDir
    File tempDir;

    @Test
    void saveAssessmentMergesSnapshotWithoutClearingOnboardingOrPreferences() {
        CareerProfileApplicationService profileService = profileService(new FileCareerProfileStorage(tempDir));
        CareerProfileOnboardingRequest onboarding = new CareerProfileOnboardingRequest();
        onboarding.setIdentityType("career_switcher");
        onboarding.setTargetRole("Data Analyst");
        onboarding.setHasResume("no");
        profileService.saveOnboarding("user-1", onboarding);

        CareerProfilePreferencesRequest preferences = new CareerProfilePreferencesRequest();
        preferences.setTargetRole("Product Manager");
        preferences.setInterviewMode("behavioral");
        profileService.savePreferences("user-1", preferences);

        UserProfileSnapshot.AssessmentBlock assessment = new UserProfileSnapshot.AssessmentBlock();
        assessment.setLastRecordId(Long.valueOf(9001L));
        assessment.setScaleId(Long.valueOf(100L));
        assessment.setScaleTitle("MBTI");
        assessment.setSummary("ENTP");
        assessment.setCompletedAt(LocalDateTime.now());
        profileService.saveAssessment("user-1", assessment);

        UserProfileSnapshot saved = new FileCareerProfileStorage(tempDir).loadSnapshot("user-1");

        assertEquals("ENTP", saved.getAssessment().getSummary());
        assertEquals(Long.valueOf(9001L), saved.getAssessment().getLastRecordId());
        assertEquals("career_switcher", saved.getOnboarding().getIdentityType());
        assertEquals("Product Manager", saved.getPreferences().getTargetRole());
        assertEquals("behavioral", saved.getPreferences().getInterviewMode());
    }

    @Test
    void submitAndSaveProfileScoresAssessmentAndRefreshesProfile() {
        CareerProfileStorage storage = new FileCareerProfileStorage(tempDir);
        CareerProfileApplicationService profileService = profileService(storage);
        AssessmentApplicationService assessmentService = new AssessmentApplicationService(
                new AssessmentScoringService(), profileService);

        AssessmentScoreResult result = assessmentService.submitAndSaveProfile("user-2", mbtiScale(),
                answers(answer(1L, 11L), answer(2L, 21L), answer(3L, 31L), answer(4L, 41L)));

        UserProfileSnapshot snapshot = storage.loadSnapshot("user-2");
        CareerUserProfileDto profile = profileService.getProfile("user-2");

        assertEquals("ENTP", result.getResultSummary());
        assertEquals(Long.valueOf(1L), result.getRecordId());
        assertEquals("ENTP", snapshot.getAssessment().getSummary());
        assertEquals(Long.valueOf(1L), snapshot.getAssessment().getLastRecordId());
        assertEquals(Long.valueOf(100L), snapshot.getAssessment().getScaleId());
        assertEquals("MBTI", snapshot.getAssessment().getScaleTitle());
        assertFalse(snapshot.getAssessment().getSuggestedRoles().isEmpty());
        assertNotNull(snapshot.getAssessment().getCompletedAt());
        assertTrue(profile.getReadiness().getHasAssessment().booleanValue());
    }

    @Test
    void catalogSubmitLoadsServerSideScaleAndListsRecordsNewestFirst() {
        CareerProfileStorage storage = new FileCareerProfileStorage(tempDir);
        AssessmentApplicationService assessmentService = new AssessmentApplicationService(
                new AssessmentScoringService(),
                new InMemoryAssessmentCatalog(),
                new InMemoryAssessmentResultStorage(),
                profileService(storage));

        AssessmentScaleDto firstAttempt = assessmentService.startAttempt("user-catalog", Long.valueOf(1001L));
        AssessmentScoreResult first = assessmentService.submit("user-catalog", answersForAttempt(firstAttempt));
        AssessmentScaleDto secondAttempt = assessmentService.startAttempt("user-catalog", Long.valueOf(1001L));
        AssessmentScoreResult second = assessmentService.submit("user-catalog", answersForAttempt(secondAttempt));

        assertEquals(Long.valueOf(1001L), first.getScaleId());
        assertEquals(16, first.getAnswers().size());
        assertEquals(Long.valueOf(2L), assessmentService.listResults("user-catalog").get(0).getRecordId());
        assertEquals(second.getRecordId(), storage.loadSnapshot("user-catalog").getAssessment().getLastRecordId());
    }

    @Test
    void adminManagedAssessmentQuestionIsVisibleAndDeletable() {
        AssessmentApplicationService assessmentService = new AssessmentApplicationService(
                new AssessmentScoringService(),
                new InMemoryAssessmentCatalog(),
                new InMemoryAssessmentResultStorage(),
                profileService(new FileCareerProfileStorage(tempDir)));
        AssessmentQuestionDto question = new AssessmentQuestionDto();
        question.setQuestionText("你更喜欢哪类工作节奏？");
        question.setDimensionCode("PACE");
        question.setOptions(Arrays.asList(option(null, "FAST"), option(null, "STEADY")));

        AssessmentQuestionDto saved = assessmentService.saveQuestion(Long.valueOf(1001L), question);

        assertNotNull(saved.getQuestionId());
        assertEquals(Integer.valueOf(17), assessmentService.getScale(Long.valueOf(1001L)).getPoolQuestionCount());
        assertEquals(Integer.valueOf(16), assessmentService.getScale(Long.valueOf(1001L)).getQuestionCount());
        assertTrue(assessmentService.deleteQuestion(Long.valueOf(1001L), saved.getQuestionId()));
        assertEquals(Integer.valueOf(16), assessmentService.getScale(Long.valueOf(1001L)).getPoolQuestionCount());
    }

    @Test
    void unpublishedAssessmentQuestionIsNotSelectedForUserAttempt() {
        AssessmentApplicationService assessmentService = new AssessmentApplicationService(
                new AssessmentScoringService(),
                new InMemoryAssessmentCatalog(),
                new InMemoryAssessmentResultStorage(),
                profileService(new FileCareerProfileStorage(tempDir)));
        AssessmentScaleDto scale = assessmentService.getScale(Long.valueOf(1001L));
        AssessmentQuestionDto unpublished = scale.getQuestions().get(0);
        unpublished.setPublished(false);
        assessmentService.saveQuestion(scale.getScaleId(), unpublished);

        AssessmentScaleDto attempt = assessmentService.startAttempt("user-unpublished", scale.getScaleId());

        for (AssessmentQuestionDto question : attempt.getQuestions()) {
            assertFalse(unpublished.getQuestionId().equals(question.getQuestionId()));
        }
    }

    @Test
    void assessmentSignalIsNoLongerMissingAfterSave() {
        CareerProfileApplicationService profileService = profileService(new FileCareerProfileStorage(tempDir));
        CareerUserProfileDto before = profileService.getProfile("user-3");

        UserProfileSnapshot.AssessmentBlock assessment = new UserProfileSnapshot.AssessmentBlock();
        assessment.setScaleId(Long.valueOf(200L));
        assessment.setScaleTitle("Holland");
        assessment.setSummary("ARI");
        assessment.setCompletedAt(LocalDateTime.now());
        profileService.saveAssessment("user-3", assessment);

        CareerUserProfileDto after = profileService.getProfile("user-3");

        assertTrue(hasMissingSignal(before, "assessment"));
        assertTrue(after.getReadiness().getHasAssessment().booleanValue());
        assertFalse(hasMissingSignal(after, "assessment"));
    }

    private CareerProfileApplicationService profileService(CareerProfileStorage storage) {
        return new CareerProfileApplicationService(
                storage,
                new CareerProfileSnapshotMergeService(),
                new CareerProfileBuildService());
    }

    private boolean hasMissingSignal(CareerUserProfileDto profile, String key) {
        if (profile == null || profile.getMissingSignals() == null) {
            return false;
        }
        for (CareerUserProfileDto.MissingSignal signal : profile.getMissingSignals()) {
            if (signal != null && key.equals(signal.getKey())) {
                return true;
            }
        }
        return false;
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
