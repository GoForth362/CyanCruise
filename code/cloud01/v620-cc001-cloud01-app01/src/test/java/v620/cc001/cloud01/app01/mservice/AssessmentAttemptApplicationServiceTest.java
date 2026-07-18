package v620.cc001.cloud01.app01.mservice;

import org.junit.jupiter.api.Test;
import v620.base.helper.career.AssessmentQuestionSelectionService;
import v620.base.helper.career.AssessmentScoringService;
import v620.base.helper.career.CareerProfileBuildService;
import v620.base.helper.career.CareerProfileSnapshotMergeService;
import v620.cc001.base.common.dto.career.AssessmentOptionDto;
import v620.cc001.base.common.dto.career.AssessmentQuestionDto;
import v620.cc001.base.common.dto.career.AssessmentScaleDto;
import v620.cc001.base.common.dto.career.AssessmentSubmitRequest;
import v620.cc001.cloud01.app01.mservice.application.AssessmentApplicationService;
import v620.cc001.cloud01.app01.mservice.application.CareerProfileApplicationService;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryAssessmentAttemptStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryAssessmentCatalog;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryAssessmentResultStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerProfileStorage;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AssessmentAttemptApplicationServiceTest {

    @Test
    void freezesBalancedAttemptAndRedrawsWithoutImmediateOverlap() {
        AssessmentApplicationService service = service();
        service.saveAnswerQuestionCount(Long.valueOf(1001L), Integer.valueOf(4));

        AssessmentScaleDto first = service.startAttempt("attempt-user", Long.valueOf(1001L));
        AssessmentScaleDto second = service.startAttempt("attempt-user", Long.valueOf(1001L));

        assertEquals(4, first.getQuestions().size());
        assertEquals(4, dimensions(first).size());
        assertNotEquals(first.getAttemptId(), second.getAttemptId());
        assertTrue(disjoint(questionIds(first), questionIds(second)));
    }

    @Test
    void isolatesAttemptsAndRejectsInvalidOrRepeatedSubmission() {
        AssessmentApplicationService service = service();
        service.saveAnswerQuestionCount(Long.valueOf(1001L), Integer.valueOf(4));
        AssessmentScaleDto attempt = service.startAttempt("owner-user", Long.valueOf(1001L));

        AssessmentSubmitRequest valid = answers(attempt);
        assertThrows(IllegalArgumentException.class, () -> service.submit("other-user", valid));

        AssessmentSubmitRequest invalidOption = answers(attempt);
        Long firstQuestion = attempt.getQuestions().get(0).getQuestionId();
        invalidOption.getAnswers().put(firstQuestion, Long.valueOf(-1L));
        assertThrows(IllegalArgumentException.class, () -> service.submit("owner-user", invalidOption));

        service.deleteQuestion(Long.valueOf(1001L), firstQuestion);
        service.submit("owner-user", valid);
        assertThrows(IllegalArgumentException.class, () -> service.submit("owner-user", valid));
    }

    @Test
    void validatesAdministratorAnswerCount() {
        AssessmentApplicationService service = service();

        assertThrows(IllegalArgumentException.class,
                () -> service.saveAnswerQuestionCount(Long.valueOf(1001L), Integer.valueOf(0)));
        assertThrows(IllegalArgumentException.class,
                () -> service.saveAnswerQuestionCount(Long.valueOf(1001L), Integer.valueOf(17)));
        assertEquals(Integer.valueOf(8), service.saveAnswerQuestionCount(
                Long.valueOf(1001L), Integer.valueOf(8)).getAnswerQuestionCount());
    }

    @Test
    void rejectsSubmissionWithoutAttemptId() {
        AssessmentApplicationService service = service();
        AssessmentSubmitRequest request = new AssessmentSubmitRequest();
        request.setScaleId(Long.valueOf(1001L));
        request.setAnswers(new LinkedHashMap<Long, Long>());

        assertThrows(IllegalArgumentException.class, () -> service.submit("attempt-user", request));
    }

    private AssessmentApplicationService service() {
        CareerProfileApplicationService profileService = new CareerProfileApplicationService(
                new InMemoryCareerProfileStorage(), new CareerProfileSnapshotMergeService(),
                new CareerProfileBuildService());
        return new AssessmentApplicationService(new AssessmentScoringService(),
                new InMemoryAssessmentCatalog(), new InMemoryAssessmentAttemptStorage(),
                new InMemoryAssessmentResultStorage(), profileService,
                new AssessmentQuestionSelectionService(), new Random(17L));
    }

    private AssessmentSubmitRequest answers(AssessmentScaleDto attempt) {
        AssessmentSubmitRequest request = new AssessmentSubmitRequest();
        request.setAttemptId(attempt.getAttemptId());
        request.setScaleId(attempt.getScaleId());
        Map<Long, Long> answers = new LinkedHashMap<Long, Long>();
        for (AssessmentQuestionDto question : attempt.getQuestions()) {
            AssessmentOptionDto option = question.getOptions().get(0);
            answers.put(question.getQuestionId(), option.getOptionId());
        }
        request.setAnswers(answers);
        return request;
    }

    private Set<Long> questionIds(AssessmentScaleDto scale) {
        Set<Long> ids = new HashSet<Long>();
        for (AssessmentQuestionDto question : scale.getQuestions()) ids.add(question.getQuestionId());
        return ids;
    }

    private Set<String> dimensions(AssessmentScaleDto scale) {
        Set<String> dimensions = new HashSet<String>();
        for (AssessmentQuestionDto question : scale.getQuestions()) dimensions.add(question.getDimensionCode());
        return dimensions;
    }

    private boolean disjoint(Set<Long> left, Set<Long> right) {
        Set<Long> overlap = new HashSet<Long>(left);
        overlap.retainAll(right);
        return overlap.isEmpty();
    }
}
