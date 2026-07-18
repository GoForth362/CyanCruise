package v620.cc001.cloud01.app01.mservice.storage.impl;

import v620.cc001.base.common.dto.career.AssessmentAttemptDto;
import v620.cc001.cloud01.app01.mservice.storage.AssessmentAttemptStorage;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

public class InMemoryAssessmentAttemptStorage implements AssessmentAttemptStorage {

    private final Map<String, AssessmentAttemptDto> attempts = new LinkedHashMap<String, AssessmentAttemptDto>();

    public synchronized void save(AssessmentAttemptDto attempt) {
        attempts.put(key(attempt.getUserId(), attempt.getAttemptId()), attempt);
    }

    public synchronized AssessmentAttemptDto load(String userId, String attemptId) {
        return attempts.get(key(userId, attemptId));
    }

    public synchronized AssessmentAttemptDto latest(String userId, Long scaleId) {
        AssessmentAttemptDto latest = null;
        for (AssessmentAttemptDto attempt : attempts.values()) {
            if (same(userId, attempt.getUserId()) && same(scaleId, attempt.getScaleId())
                    && (latest == null || later(attempt.getCreatedAt(), latest.getCreatedAt()))) {
                latest = attempt;
            }
        }
        return latest;
    }

    public synchronized void complete(String userId, String attemptId) {
        AssessmentAttemptDto attempt = load(userId, attemptId);
        if (attempt == null) {
            throw new IllegalArgumentException("测评作答记录不存在或无权访问");
        }
        attempt.setStatus("COMPLETED");
        attempt.setCompletedAt(LocalDateTime.now());
    }

    private String key(String userId, String attemptId) { return userId + "|" + attemptId; }
    private boolean same(Object left, Object right) { return left == null ? right == null : left.equals(right); }
    private boolean later(LocalDateTime left, LocalDateTime right) {
        return left != null && (right == null || left.isAfter(right));
    }
}
