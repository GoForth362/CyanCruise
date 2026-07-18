package v620.base.helper.career;

import org.junit.jupiter.api.Test;
import v620.cc001.base.common.dto.career.AssessmentQuestionDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AssessmentQuestionSelectionServiceTest {

    private final AssessmentQuestionSelectionService service = new AssessmentQuestionSelectionService();

    @Test
    void balancesQuestionsAcrossAvailableDimensions() {
        List<AssessmentQuestionDto> selected = service.select(pool(), 4,
                Collections.<Long>emptySet(), new Random(7L));

        Set<String> dimensions = new HashSet<String>();
        for (AssessmentQuestionDto question : selected) {
            dimensions.add(question.getDimensionCode());
        }

        assertEquals(4, selected.size());
        assertEquals(new HashSet<String>(Arrays.asList("EI", "SN", "TF", "JP")), dimensions);
    }

    @Test
    void prefersQuestionsNotUsedInPreviousAttempt() {
        Set<Long> previous = new HashSet<Long>(Arrays.asList(11L, 21L, 31L, 41L));

        List<AssessmentQuestionDto> selected = service.select(pool(), 4, previous, new Random(3L));

        assertEquals(4, selected.size());
        for (AssessmentQuestionDto question : selected) {
            assertTrue(!previous.contains(question.getQuestionId()));
        }
    }

    @Test
    void returnsWholePoolWhenConfiguredCountExceedsCapacity() {
        List<AssessmentQuestionDto> selected = service.select(pool(), 99,
                Collections.<Long>emptySet(), new Random(1L));

        assertEquals(8, selected.size());
    }

    private List<AssessmentQuestionDto> pool() {
        List<AssessmentQuestionDto> questions = new ArrayList<AssessmentQuestionDto>();
        questions.add(question(11L, "EI", 1));
        questions.add(question(12L, "EI", 2));
        questions.add(question(21L, "SN", 3));
        questions.add(question(22L, "SN", 4));
        questions.add(question(31L, "TF", 5));
        questions.add(question(32L, "TF", 6));
        questions.add(question(41L, "JP", 7));
        questions.add(question(42L, "JP", 8));
        return questions;
    }

    private AssessmentQuestionDto question(Long id, String dimension, int order) {
        AssessmentQuestionDto question = new AssessmentQuestionDto();
        question.setQuestionId(id);
        question.setDimensionCode(dimension);
        question.setSortOrder(Integer.valueOf(order));
        return question;
    }
}
