package v620.cc001.cloud01.app01.mservice.ai.impl;

import v620.cc001.base.common.dto.career.ResumeDiagnosisRequest;
import v620.cc001.cloud01.app01.mservice.ai.ResumeDiagnosisAnalyzer;

/**
 * Compatibility boundary retained for callers compiled against the old rule analyzer.
 * Runtime diagnosis must come from a configured AI provider and never from fabricated
 * deterministic scores or recommendations.
 */
@Deprecated
public class DefaultResumeDiagnosisAnalyzer implements ResumeDiagnosisAnalyzer {

    public String analyze(ResumeDiagnosisRequest request, String resumeText) {
        throw new IllegalStateException("AI 简历诊断暂不可用，请检查 AI 服务配置后重试。");
    }
}
