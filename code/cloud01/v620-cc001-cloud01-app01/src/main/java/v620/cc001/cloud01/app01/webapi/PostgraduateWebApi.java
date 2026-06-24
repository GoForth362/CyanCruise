package v620.cc001.cloud01.app01.webapi;

import kd.bos.openapi.common.custom.annotation.ApiController;
import kd.bos.openapi.common.custom.annotation.ApiMapping;
import kd.bos.openapi.common.custom.annotation.ApiPostMapping;
import kd.bos.openapi.common.custom.annotation.ApiRequestBody;
import kd.bos.openapi.common.custom.annotation.ApiResponseBody;
import v620.cc001.base.common.dto.career.PostgraduateMistakeAnalysisResult;
import v620.cc001.base.common.dto.career.PostgraduateMistakeAnalyzeRequest;
import v620.cc001.base.common.dto.career.PostgraduatePlanRequest;
import v620.cc001.base.common.dto.career.PostgraduatePlanResult;
import v620.cc001.base.common.dto.career.PostgraduateReexamPreparationResult;
import v620.cc001.base.common.dto.career.PostgraduateReexamPrepareRequest;
import v620.cc001.base.common.dto.career.PostgraduateSchoolRecommendRequest;
import v620.cc001.base.common.dto.career.PostgraduateSchoolRecommendationResult;
import v620.cc001.cloud01.app01.mservice.IdentityAwareCareerLoopWebApiBoundary;
import v620.cc001.cloud01.app01.mservice.PostgraduateApplicationService;

/** WebAPI boundary for postgraduate exam companion. */
@ApiController(value = "postgraduateWebApi", desc = "考研陪伴 API")
@ApiMapping("/cc001/postgraduate")
public class PostgraduateWebApi {

    private final PostgraduateApplicationService applicationService;
    private final IdentityAwareCareerLoopWebApiBoundary identityBoundary;

    public PostgraduateWebApi() {
        this(new PostgraduateApplicationService(), new IdentityAwareCareerLoopWebApiBoundary());
    }

    PostgraduateWebApi(PostgraduateApplicationService applicationService,
                       IdentityAwareCareerLoopWebApiBoundary identityBoundary) {
        this.applicationService = applicationService;
        this.identityBoundary = identityBoundary;
    }

    @ApiPostMapping(value = "/school-recommend", desc = "生成考研择校建议", methodParamNames = {"userId", "request"})
    public @ApiResponseBody(value = "考研择校建议") PostgraduateSchoolRecommendationResult recommendSchools(
            @ApiRequestBody(value = "用户ID", required = true) String userId,
            @ApiRequestBody(value = "择校画像", required = true) PostgraduateSchoolRecommendRequest request) {
        return applicationService.recommendSchools(identityBoundary.requireUser(userId), request);
    }

    @ApiPostMapping(value = "/plan/generate", desc = "生成考研复习计划", methodParamNames = {"userId", "request"})
    public @ApiResponseBody(value = "考研复习计划") PostgraduatePlanResult generatePlan(
            @ApiRequestBody(value = "用户ID", required = true) String userId,
            @ApiRequestBody(value = "计划请求", required = true) PostgraduatePlanRequest request) {
        return applicationService.generatePlan(identityBoundary.requireUser(userId), request);
    }

    @ApiPostMapping(value = "/mistake/analyze", desc = "解析考研错题", methodParamNames = {"userId", "request"})
    public @ApiResponseBody(value = "错题解析") PostgraduateMistakeAnalysisResult analyzeMistake(
            @ApiRequestBody(value = "用户ID", required = true) String userId,
            @ApiRequestBody(value = "错题请求", required = true) PostgraduateMistakeAnalyzeRequest request) {
        return applicationService.analyzeMistake(identityBoundary.requireUser(userId), request);
    }

    @ApiPostMapping(value = "/reexam/prepare", desc = "生成考研复试准备清单", methodParamNames = {"userId", "request"})
    public @ApiResponseBody(value = "复试准备清单") PostgraduateReexamPreparationResult prepareReexam(
            @ApiRequestBody(value = "用户ID", required = true) String userId,
            @ApiRequestBody(value = "复试准备请求", required = true) PostgraduateReexamPrepareRequest request) {
        return applicationService.prepareReexam(identityBoundary.requireUser(userId), request);
    }
}
