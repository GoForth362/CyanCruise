package v620.cc001.cloud01.app01.mservice;

import v620.cc001.base.common.dto.career.ResumeDiagnosisResultDto;
import v620.cc001.base.common.dto.career.ResumeKeywordDto;
import v620.cc001.base.common.dto.career.ResumeKeywordStatusDto;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Non-durable resume diagnosis storage for tests.
 */
public class InMemoryResumeDiagnosisStorage implements ResumeDiagnosisStorage {

    private final Map<Long, ResumeDiagnosisResultDto> DIAGNOSES =
            new ConcurrentHashMap<Long, ResumeDiagnosisResultDto>();
    private final Map<Long, ResumeKeywordStatusDto> KEYWORDS =
            new ConcurrentHashMap<Long, ResumeKeywordStatusDto>();

    public ResumeDiagnosisResultDto saveDiagnosis(ResumeDiagnosisResultDto result) {
        if (result != null && result.getResumeId() != null) {
            DIAGNOSES.put(result.getResumeId(), copy(result));
        }
        return copy(result);
    }

    public ResumeDiagnosisResultDto loadDiagnosis(Long resumeId) {
        return copy(DIAGNOSES.get(resumeId));
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
        copy.setOverallScore(source.getOverallScore());
        copy.setStrengths(new ArrayList<String>(source.getStrengths()));
        copy.setWeaknesses(new ArrayList<String>(source.getWeaknesses()));
        copy.setSuggestions(new ArrayList<String>(source.getSuggestions()));
        copy.setRawAnalysis(source.getRawAnalysis());
        return copy;
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
