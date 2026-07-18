package v620.cc001.cloud01.app01.mservice.storage;

import v620.cc001.base.common.dto.career.AssessmentScoreResult;

import java.util.List;

public interface AssessmentResultStorage {

    Long saveResult(String userId, AssessmentScoreResult result);

    void updateResult(String userId, AssessmentScoreResult result);

    AssessmentScoreResult loadResult(String userId, Long recordId);

    List<AssessmentScoreResult> listResults(String userId);
}
