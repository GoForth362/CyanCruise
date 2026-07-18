package v620.cc001.cloud01.app01.mservice.storage;

import v620.cc001.base.common.dto.career.ResumeDiagnosisResultDto;
import v620.cc001.base.common.dto.career.ResumeKeywordStatusDto;

import java.util.List;

/**
 * Storage boundary for resume diagnosis and keyword extraction state.
 */
public interface ResumeDiagnosisStorage {

    ResumeDiagnosisResultDto saveDiagnosis(ResumeDiagnosisResultDto result);

    ResumeDiagnosisResultDto loadDiagnosis(Long resumeId);

    List<ResumeDiagnosisResultDto> listDiagnoses(String userId, Long resumeId);

    boolean deleteDiagnosis(String userId, Long diagnosisId);

    ResumeKeywordStatusDto saveKeywordStatus(ResumeKeywordStatusDto status);

    ResumeKeywordStatusDto loadKeywordStatus(Long resumeId);
}
