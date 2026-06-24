package v620.cc001.cloud01.app01.webapi;

import kd.bos.openapi.common.custom.annotation.ApiController;
import kd.bos.openapi.common.custom.annotation.ApiMapping;
import kd.bos.openapi.common.custom.annotation.ApiPostMapping;
import kd.bos.openapi.common.custom.annotation.ApiRequestBody;
import kd.bos.openapi.common.custom.annotation.ApiResponseBody;
import v620.cc001.base.common.dto.career.RecommendationDiagnosisResult;
import v620.cc001.base.common.dto.career.RecommendationDocumentPolishRequest;
import v620.cc001.base.common.dto.career.RecommendationDocumentPolishResult;
import v620.cc001.base.common.dto.career.RecommendationPlanResult;
import v620.cc001.base.common.dto.career.RecommendationProfileRequest;
import v620.cc001.base.common.dto.career.RecommendationTutorLetterRequest;
import v620.cc001.base.common.dto.career.RecommendationTutorLetterResult;
import v620.cc001.cloud01.app01.mservice.IdentityAwareCareerLoopWebApiBoundary;
import v620.cc001.cloud01.app01.mservice.RecommendationApplicationService;

/** WebAPI boundary for postgraduate recommendation companion. */
@ApiController(value = "recommendationWebApi", desc = "保研陪伴 API")
@ApiMapping("/cc001/recommendation")
public class RecommendationWebApi {

    private final RecommendationApplicationService applicationService;
    private final IdentityAwareCareerLoopWebApiBoundary identityBoundary;

    public RecommendationWebApi() {
        this(new RecommendationApplicationService(), new IdentityAwareCareerLoopWebApiBoundary());
    }

    RecommendationWebApi(RecommendationApplicationService applicationService,
                         IdentityAwareCareerLoopWebApiBoundary identityBoundary) {
        this.applicationService = applicationService;
        this.identityBoundary = identityBoundary;
    }

    @ApiPostMapping(value = "/diagnose", desc = "诊断保研竞争力", methodParamNames = {"userId", "request"})
    public @ApiResponseBody(value = "保研竞争力诊断") RecommendationDiagnosisResult diagnose(
            @ApiRequestBody(value = "用户ID", required = true) String userId,
            @ApiRequestBody(value = "保研背景", required = true) RecommendationProfileRequest request) {
        return applicationService.diagnose(identityBoundary.requireUser(userId), request);
    }

    @ApiPostMapping(value = "/plan/generate", desc = "生成保研行动计划", methodParamNames = {"userId", "request"})
    public @ApiResponseBody(value = "保研行动计划") RecommendationPlanResult generatePlan(
            @ApiRequestBody(value = "用户ID", required = true) String userId,
            @ApiRequestBody(value = "保研背景", required = true) RecommendationProfileRequest request) {
        return applicationService.generatePlan(identityBoundary.requireUser(userId), request);
    }

    @ApiPostMapping(value = "/document/polish", desc = "润色保研文书", methodParamNames = {"userId", "request"})
    public @ApiResponseBody(value = "保研文书润色") RecommendationDocumentPolishResult polishDocument(
            @ApiRequestBody(value = "用户ID", required = true) String userId,
            @ApiRequestBody(value = "文书润色请求", required = true) RecommendationDocumentPolishRequest request) {
        return applicationService.polishDocument(identityBoundary.requireUser(userId), request);
    }

    @ApiPostMapping(value = "/tutor-letter/generate", desc = "生成导师意向信", methodParamNames = {"userId", "request"})
    public @ApiResponseBody(value = "导师意向信") RecommendationTutorLetterResult generateTutorLetter(
            @ApiRequestBody(value = "用户ID", required = true) String userId,
            @ApiRequestBody(value = "导师意向信请求", required = true) RecommendationTutorLetterRequest request) {
        return applicationService.generateTutorLetter(identityBoundary.requireUser(userId), request);
    }
}
