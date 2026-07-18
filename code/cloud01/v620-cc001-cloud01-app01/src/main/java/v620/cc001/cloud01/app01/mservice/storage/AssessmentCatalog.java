package v620.cc001.cloud01.app01.mservice.storage;

import v620.cc001.base.common.dto.career.AssessmentScaleDto;
import v620.cc001.base.common.dto.career.AssessmentQuestionDto;

import java.util.List;

public interface AssessmentCatalog {

    List<AssessmentScaleDto> listScales();

    AssessmentScaleDto loadScale(Long scaleId);

    AssessmentQuestionDto saveQuestion(Long scaleId, AssessmentQuestionDto question);

    boolean deleteQuestion(Long scaleId, Long questionId);

    AssessmentScaleDto saveAnswerQuestionCount(Long scaleId, Integer answerQuestionCount);
}
