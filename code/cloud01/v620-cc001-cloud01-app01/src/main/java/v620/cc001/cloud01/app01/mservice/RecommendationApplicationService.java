package v620.cc001.cloud01.app01.mservice;

import v620.base.helper.career.RecommendationCompanionService;
import v620.cc001.base.common.dto.career.RecommendationDiagnosisResult;
import v620.cc001.base.common.dto.career.RecommendationDocumentPolishRequest;
import v620.cc001.base.common.dto.career.RecommendationDocumentPolishResult;
import v620.cc001.base.common.dto.career.RecommendationPlanResult;
import v620.cc001.base.common.dto.career.RecommendationProfileRequest;
import v620.cc001.base.common.dto.career.RecommendationTutorLetterRequest;
import v620.cc001.base.common.dto.career.RecommendationTutorLetterResult;

/** Application boundary for postgraduate recommendation companion. */
public class RecommendationApplicationService {

    private final RecommendationCompanionService helper;

    public RecommendationApplicationService() {
        this(new RecommendationCompanionService());
    }

    public RecommendationApplicationService(RecommendationCompanionService helper) {
        this.helper = helper;
    }

    public RecommendationDiagnosisResult diagnose(String userId, RecommendationProfileRequest request) {
        requireUserId(userId);
        return helper.diagnose(request);
    }

    public RecommendationPlanResult generatePlan(String userId, RecommendationProfileRequest request) {
        requireUserId(userId);
        return helper.generatePlan(request);
    }

    public RecommendationDocumentPolishResult polishDocument(String userId, RecommendationDocumentPolishRequest request) {
        requireUserId(userId);
        return helper.polishDocument(request);
    }

    public RecommendationTutorLetterResult generateTutorLetter(String userId, RecommendationTutorLetterRequest request) {
        requireUserId(userId);
        return helper.generateTutorLetter(request);
    }

    private void requireUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("请先确认当前登录身份，再使用保研陪伴功能。");
        }
    }
}
