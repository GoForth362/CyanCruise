package v620.base.helper.furtherstudy;

import v620.cc001.base.common.dto.furtherstudy.RecommendationDiagnosisResult;
import v620.cc001.base.common.dto.furtherstudy.RecommendationDocumentPolishRequest;
import v620.cc001.base.common.dto.furtherstudy.RecommendationDocumentPolishResult;
import v620.cc001.base.common.dto.furtherstudy.RecommendationPlanResult;
import v620.cc001.base.common.dto.furtherstudy.RecommendationProfileRequest;
import v620.cc001.base.common.dto.furtherstudy.RecommendationTutorLetterRequest;
import v620.cc001.base.common.dto.furtherstudy.RecommendationTutorLetterResult;

/** Compatibility boundary retained until the real recommendation Agent is connected. */
public class RecommendationCompanionService {
    public RecommendationDiagnosisResult diagnose(RecommendationProfileRequest request) {
        throw unavailable();
    }

    public RecommendationPlanResult generatePlan(RecommendationProfileRequest request) {
        throw unavailable();
    }

    public RecommendationDocumentPolishResult polishDocument(RecommendationDocumentPolishRequest request) {
        throw unavailable();
    }

    public RecommendationTutorLetterResult generateTutorLetter(RecommendationTutorLetterRequest request) {
        throw unavailable();
    }

    private IllegalStateException unavailable() {
        return new IllegalStateException("保研陪伴真实智能服务尚未接通，请稍后重试。");
    }
}
