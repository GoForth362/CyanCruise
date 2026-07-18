package v620.cc001.cloud01.app01.mservice.storage;

import v620.cc001.base.common.dto.career.AssessmentAttemptDto;

public interface AssessmentAttemptStorage {

    void save(AssessmentAttemptDto attempt);

    AssessmentAttemptDto load(String userId, String attemptId);

    AssessmentAttemptDto latest(String userId, Long scaleId);

    void complete(String userId, String attemptId);
}
