package v620.cc001.cloud01.app01.mservice;

import v620.cc001.base.common.dto.career.ResumeRecordDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * In-memory resume storage for focused tests and early migration validation.
 */
public class InMemoryResumeStorage implements ResumeStorage {

    private final Map<Long, ResumeRecordDto> records = new LinkedHashMap<Long, ResumeRecordDto>();
    private long nextId = 1L;

    public synchronized ResumeRecordDto save(ResumeRecordDto record) {
        if (record == null) {
            throw new IllegalArgumentException("record is required");
        }
        ResumeRecordDto copy = copy(record);
        if (copy.getResumeId() == null) {
            copy.setResumeId(Long.valueOf(nextId++));
        } else if (copy.getResumeId().longValue() >= nextId) {
            nextId = copy.getResumeId().longValue() + 1L;
        }
        records.put(copy.getResumeId(), copy);
        return copy(copy);
    }

    public synchronized ResumeRecordDto load(Long resumeId) {
        return copy(records.get(resumeId));
    }

    public synchronized List<ResumeRecordDto> listByUser(String userId) {
        List<ResumeRecordDto> result = new ArrayList<ResumeRecordDto>();
        if (userId == null || userId.trim().length() == 0) {
            return result;
        }
        String safeUserId = userId.trim();
        for (ResumeRecordDto record : records.values()) {
            if (record != null && safeUserId.equals(record.getUserId())) {
                result.add(copy(record));
            }
        }
        Collections.sort(result, new Comparator<ResumeRecordDto>() {
            public int compare(ResumeRecordDto left, ResumeRecordDto right) {
                if (left == right) return 0;
                if (left == null) return 1;
                if (right == null) return -1;
                if (left.getUpdatedAt() == null && right.getUpdatedAt() == null) return 0;
                if (left.getUpdatedAt() == null) return 1;
                if (right.getUpdatedAt() == null) return -1;
                return right.getUpdatedAt().compareTo(left.getUpdatedAt());
            }
        });
        return result;
    }

    public synchronized void delete(Long resumeId) {
        records.remove(resumeId);
    }

    private ResumeRecordDto copy(ResumeRecordDto source) {
        if (source == null) {
            return null;
        }
        ResumeRecordDto copy = new ResumeRecordDto();
        copy.setResumeId(source.getResumeId());
        copy.setUserId(source.getUserId());
        copy.setTitle(source.getTitle());
        copy.setTargetJob(source.getTargetJob());
        copy.setFileKey(source.getFileKey());
        copy.setVersion(source.getVersion());
        copy.setStatus(source.getStatus());
        copy.setParsedContent(source.getParsedContent());
        copy.setDiagnosisScore(source.getDiagnosisScore());
        copy.setCreatedAt(source.getCreatedAt());
        copy.setUpdatedAt(source.getUpdatedAt());
        return copy;
    }
}
