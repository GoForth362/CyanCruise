package v620.cc001.cloud01.app01.mservice.storage.impl;


import v620.cc001.cloud01.app01.mservice.storage.*;
import v620.cc001.base.common.dto.career.AssessmentScoreResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryAssessmentResultStorage implements AssessmentResultStorage {

    private final AtomicLong nextId = new AtomicLong(1L);
    private final List<AssessmentScoreResult> records = new ArrayList<AssessmentScoreResult>();

    public synchronized Long saveResult(String userId, AssessmentScoreResult result) {
        if (result == null) {
            throw new IllegalArgumentException("result is required");
        }
        AssessmentScoreResult copy = AssessmentResultCopies.copy(result);
        copy.setRecordId(Long.valueOf(nextId.getAndIncrement()));
        copy.setUserId(userId);
        if (copy.getCreatedAt() == null) {
            copy.setCreatedAt(LocalDateTime.now());
        }
        records.add(copy);
        result.setRecordId(copy.getRecordId());
        result.setUserId(copy.getUserId());
        result.setCreatedAt(copy.getCreatedAt());
        return copy.getRecordId();
    }

    public synchronized AssessmentScoreResult loadResult(String userId, Long recordId) {
        for (AssessmentScoreResult record : records) {
            if (same(userId, record.getUserId()) && same(recordId, record.getRecordId())) {
                return AssessmentResultCopies.copy(record);
            }
        }
        return null;
    }

    public synchronized List<AssessmentScoreResult> listResults(String userId) {
        List<AssessmentScoreResult> out = new ArrayList<AssessmentScoreResult>();
        for (AssessmentScoreResult record : records) {
            if (same(userId, record.getUserId())) {
                out.add(AssessmentResultCopies.copy(record));
            }
        }
        Collections.sort(out, new Comparator<AssessmentScoreResult>() {
            public int compare(AssessmentScoreResult left, AssessmentScoreResult right) {
                long l = left.getRecordId() == null ? 0L : left.getRecordId().longValue();
                long r = right.getRecordId() == null ? 0L : right.getRecordId().longValue();
                return Long.valueOf(r).compareTo(Long.valueOf(l));
            }
        });
        return out;
    }

    private boolean same(Object left, Object right) {
        return left == null ? right == null : left.equals(right);
    }
}
