package v620.cc001.cloud01.app01.mservice.storage.impl;

import v620.cc001.cloud01.app01.mservice.storage.*;
import v620.cc001.base.common.dto.career.ResumeDiagnosisResultDto;
import v620.cc001.base.common.dto.career.ResumeKeywordDto;
import v620.cc001.base.common.dto.career.ResumeKeywordStatusDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Non-durable resume diagnosis storage for tests.
 */
public class InMemoryResumeDiagnosisStorage implements ResumeDiagnosisStorage {

    private final Map<Long, ResumeDiagnosisResultDto> DIAGNOSES =
            new ConcurrentHashMap<Long, ResumeDiagnosisResultDto>();
    private final Map<Long, ResumeDiagnosisResultDto> HISTORY =
            new ConcurrentHashMap<Long, ResumeDiagnosisResultDto>();
    private final AtomicLong NEXT_DIAGNOSIS_ID = new AtomicLong(1L);
    private final Map<Long, ResumeKeywordStatusDto> KEYWORDS =
            new ConcurrentHashMap<Long, ResumeKeywordStatusDto>();

    public ResumeDiagnosisResultDto saveDiagnosis(ResumeDiagnosisResultDto result) {
        if (result != null && result.getResumeId() != null) {
            if (result.getDiagnosisId() == null) {
                result.setDiagnosisId(Long.valueOf(NEXT_DIAGNOSIS_ID.getAndIncrement()));
            }
            DIAGNOSES.put(result.getResumeId(), copy(result));
            HISTORY.put(result.getDiagnosisId(), copy(result));
        }
        return copy(result);
    }

    public ResumeDiagnosisResultDto loadDiagnosis(Long resumeId) {
        return copy(DIAGNOSES.get(resumeId));
    }

    public List<ResumeDiagnosisResultDto> listDiagnoses(final String userId, final Long resumeId) {
        List<ResumeDiagnosisResultDto> results = new ArrayList<ResumeDiagnosisResultDto>();
        for (ResumeDiagnosisResultDto item : HISTORY.values()) {
            if (same(userId, item.getUserId()) && same(resumeId, item.getResumeId())) {
                results.add(copy(item));
            }
        }
        Collections.sort(results, new Comparator<ResumeDiagnosisResultDto>() {
            public int compare(ResumeDiagnosisResultDto left, ResumeDiagnosisResultDto right) {
                if (left.getDiagnosedAt() == null) return right.getDiagnosedAt() == null ? 0 : 1;
                if (right.getDiagnosedAt() == null) return -1;
                return right.getDiagnosedAt().compareTo(left.getDiagnosedAt());
            }
        });
        return results;
    }

    public boolean deleteDiagnosis(String userId, Long diagnosisId) {
        ResumeDiagnosisResultDto item = HISTORY.get(diagnosisId);
        if (item == null || !same(userId, item.getUserId())) {
            return false;
        }
        HISTORY.remove(diagnosisId);
        ResumeDiagnosisResultDto latest = DIAGNOSES.get(item.getResumeId());
        if (latest != null && same(diagnosisId, latest.getDiagnosisId())) {
            List<ResumeDiagnosisResultDto> remaining = listDiagnoses(userId, item.getResumeId());
            if (remaining.isEmpty()) {
                DIAGNOSES.remove(item.getResumeId());
            } else {
                DIAGNOSES.put(item.getResumeId(), copy(remaining.get(0)));
            }
        }
        return true;
    }

    public ResumeKeywordStatusDto saveKeywordStatus(ResumeKeywordStatusDto status) {
        if (status != null && status.getResumeId() != null) {
            KEYWORDS.put(status.getResumeId(), copy(status));
        }
        return copy(status);
    }

    public ResumeKeywordStatusDto loadKeywordStatus(Long resumeId) {
        return copy(KEYWORDS.get(resumeId));
    }

    private ResumeDiagnosisResultDto copy(ResumeDiagnosisResultDto source) {
        if (source == null) return null;
        ResumeDiagnosisResultDto copy = new ResumeDiagnosisResultDto();
        copy.setResumeId(source.getResumeId());
        copy.setDiagnosisId(source.getDiagnosisId());
        copy.setUserId(source.getUserId());
        copy.setTargetJob(source.getTargetJob());
        copy.setDiagnosedAt(source.getDiagnosedAt());
        copy.setOverallScore(source.getOverallScore());
        copy.setStrengths(new ArrayList<String>(source.getStrengths()));
        copy.setWeaknesses(new ArrayList<String>(source.getWeaknesses()));
        copy.setSuggestions(new ArrayList<String>(source.getSuggestions()));
        copy.setScoreBreakdown(source.getScoreBreakdown());
        copy.setRevisionSuggestions(source.getRevisionSuggestions());
        copy.setRevisionPlan(source.getRevisionPlan());
        copy.setContextSources(new ArrayList<String>(source.getContextSources()));
        copy.setFallbackStatus(source.getFallbackStatus());
        copy.setRawAnalysis(source.getRawAnalysis());
        return copy;
    }

    private boolean same(Object left, Object right) {
        return left == null ? right == null : left.equals(right);
    }

    private ResumeKeywordStatusDto copy(ResumeKeywordStatusDto source) {
        if (source == null) return null;
        ResumeKeywordStatusDto copy = new ResumeKeywordStatusDto();
        copy.setResumeId(source.getResumeId());
        copy.setStatus(source.getStatus());
        copy.setErrorMsg(source.getErrorMsg());
        ArrayList<ResumeKeywordDto> keywords = new ArrayList<ResumeKeywordDto>();
        for (ResumeKeywordDto keyword : source.getKeywords()) {
            ResumeKeywordDto dto = new ResumeKeywordDto();
            dto.setCategory(keyword.getCategory());
            dto.setLabel(keyword.getLabel());
            dto.setWeight(keyword.getWeight());
            dto.setEvidence(keyword.getEvidence());
            keywords.add(dto);
        }
        copy.setKeywords(keywords);
        return copy;
    }
}
