package v620.cc001.cloud01.app01.mservice.furtherstudy;

import v620.base.helper.furtherstudy.PostgraduateCompanionService;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateMistakeAnalysisResult;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateMistakeAnalyzeRequest;
import v620.cc001.base.common.dto.furtherstudy.PostgraduatePlanRequest;
import v620.cc001.base.common.dto.furtherstudy.PostgraduatePlanResult;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateReexamPreparationResult;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateReexamPrepareRequest;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateSchoolRecommendRequest;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateSchoolRecommendationResult;

/** Application boundary for postgraduate exam companion. */
public class PostgraduateApplicationService {

    private final PostgraduateCompanionService helper;

    public PostgraduateApplicationService() {
        this(new PostgraduateCompanionService());
    }

    public PostgraduateApplicationService(PostgraduateCompanionService helper) {
        this.helper = helper;
    }

    public PostgraduateSchoolRecommendationResult recommendSchools(String userId, PostgraduateSchoolRecommendRequest request) {
        requireUserId(userId);
        return helper.recommendSchools(request);
    }

    public PostgraduatePlanResult generatePlan(String userId, PostgraduatePlanRequest request) {
        requireUserId(userId);
        return helper.generatePlan(request);
    }

    public PostgraduateMistakeAnalysisResult analyzeMistake(String userId, PostgraduateMistakeAnalyzeRequest request) {
        requireUserId(userId);
        return helper.analyzeMistake(request);
    }

    public PostgraduateReexamPreparationResult prepareReexam(String userId, PostgraduateReexamPrepareRequest request) {
        requireUserId(userId);
        return helper.prepareReexam(request);
    }

    private String requireUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("请先确认当前登录身份，再使用考研陪伴功能。");
        }
        return userId.trim();
    }
}
