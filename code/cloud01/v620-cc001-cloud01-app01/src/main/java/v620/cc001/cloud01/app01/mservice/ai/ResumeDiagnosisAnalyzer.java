package v620.cc001.cloud01.app01.mservice.ai;

import v620.cc001.base.common.dto.career.ResumeDiagnosisRequest;

/**
 * Replaceable boundary for resume diagnosis analysis.
 */
public interface ResumeDiagnosisAnalyzer {

    String analyze(ResumeDiagnosisRequest request, String resumeText);
}
