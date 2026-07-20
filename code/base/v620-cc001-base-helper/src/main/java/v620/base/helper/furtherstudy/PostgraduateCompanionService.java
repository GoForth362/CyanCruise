package v620.base.helper.furtherstudy;

import v620.cc001.base.common.dto.furtherstudy.PostgraduateMistakeAnalysisResult;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateMistakeAnalyzeRequest;
import v620.cc001.base.common.dto.furtherstudy.PostgraduatePlanRequest;
import v620.cc001.base.common.dto.furtherstudy.PostgraduatePlanResult;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateReexamPreparationResult;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateReexamPrepareRequest;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateSchoolRecommendRequest;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateSchoolRecommendationResult;

/** Compatibility boundary retained until the real postgraduate Agent is connected. */
public class PostgraduateCompanionService {
    public PostgraduateSchoolRecommendationResult recommendSchools(PostgraduateSchoolRecommendRequest request) {
        throw unavailable();
    }

    public PostgraduatePlanResult generatePlan(PostgraduatePlanRequest request) {
        throw unavailable();
    }

    public PostgraduateMistakeAnalysisResult analyzeMistake(PostgraduateMistakeAnalyzeRequest request) {
        throw unavailable();
    }

    public PostgraduateReexamPreparationResult prepareReexam(PostgraduateReexamPrepareRequest request) {
        throw unavailable();
    }

    private IllegalStateException unavailable() {
        return new IllegalStateException("考研陪伴真实智能服务尚未接通，请稍后重试。");
    }
}
