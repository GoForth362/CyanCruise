package v620.cc001.cloud01.app01.mservice.storage.impl;

import org.junit.jupiter.api.Test;
import v620.cc001.base.common.dto.career.AssessmentQuestionDto;
import v620.cc001.base.common.dto.career.AssessmentScaleDto;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PostgresqlAssessmentCatalogTest {

    @Test
    void restoresOnlyBuiltInQuestionsToPublished() {
        InMemoryAssessmentCatalog seedCatalog = new InMemoryAssessmentCatalog();
        AssessmentScaleDto builtIn = seedCatalog.loadScale(Long.valueOf(1005L));
        AssessmentScaleDto stored = seedCatalog.loadScale(Long.valueOf(1005L));
        for (AssessmentQuestionDto question : stored.getQuestions()) {
            question.setPublished(false);
        }
        AssessmentQuestionDto draft = new AssessmentQuestionDto();
        draft.setQuestionId(Long.valueOf(999999L));
        draft.setPublished(false);
        stored.setQuestions(new ArrayList<AssessmentQuestionDto>(stored.getQuestions()));
        stored.getQuestions().add(draft);

        assertTrue(PostgresqlAssessmentCatalog.restoreBuiltInQuestionPublication(stored, builtIn));
        for (int i = 0; i < 10; i += 1) {
            assertTrue(stored.getQuestions().get(i).isPublished());
        }
        assertFalse(draft.isPublished());
    }
}
