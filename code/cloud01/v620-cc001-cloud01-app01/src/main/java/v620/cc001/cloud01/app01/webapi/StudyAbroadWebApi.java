package v620.cc001.cloud01.app01.webapi;

import kd.bos.openapi.common.custom.annotation.ApiController;
import kd.bos.openapi.common.custom.annotation.ApiMapping;
import kd.bos.openapi.common.custom.annotation.ApiPostMapping;
import kd.bos.openapi.common.custom.annotation.ApiRequestBody;
import kd.bos.openapi.common.custom.annotation.ApiResponseBody;
import v620.cc001.base.common.dto.career.StudyAbroadLanguagePlanRequest;
import v620.cc001.base.common.dto.career.StudyAbroadLanguagePlanResult;
import v620.cc001.base.common.dto.career.StudyAbroadProfileDiagnosisResult;
import v620.cc001.base.common.dto.career.StudyAbroadProfileRequest;
import v620.cc001.base.common.dto.career.StudyAbroadSchoolPositionRequest;
import v620.cc001.base.common.dto.career.StudyAbroadSchoolPositionResult;
import v620.cc001.base.common.dto.career.StudyAbroadStatementOutlineResult;
import v620.cc001.base.common.dto.career.StudyAbroadStatementRequest;
import v620.cc001.base.common.dto.career.StudyAbroadVisaChecklistRequest;
import v620.cc001.base.common.dto.career.StudyAbroadVisaChecklistResult;
import v620.cc001.cloud01.app01.mservice.IdentityAwareCareerLoopWebApiBoundary;
import v620.cc001.cloud01.app01.mservice.StudyAbroadApplicationService;

/** WebAPI boundary for study abroad companion. */
@ApiController(value = "studyAbroadWebApi", desc = "留学陪伴 API")
@ApiMapping("/cc001/study-abroad")
public class StudyAbroadWebApi {

    private final StudyAbroadApplicationService applicationService;
    private final IdentityAwareCareerLoopWebApiBoundary identityBoundary;

    public StudyAbroadWebApi() {
        this(new StudyAbroadApplicationService(), new IdentityAwareCareerLoopWebApiBoundary());
    }

    StudyAbroadWebApi(StudyAbroadApplicationService applicationService,
                      IdentityAwareCareerLoopWebApiBoundary identityBoundary) {
        this.applicationService = applicationService;
        this.identityBoundary = identityBoundary;
    }

    @ApiPostMapping(value = "/profile/diagnose", desc = "诊断留学准备度", methodParamNames = {"userId", "request"})
    public @ApiResponseBody(value = "留学准备度诊断") StudyAbroadProfileDiagnosisResult diagnoseProfile(
            @ApiRequestBody(value = "用户ID", required = true) String userId,
            @ApiRequestBody(value = "留学背景", required = true) StudyAbroadProfileRequest request) {
        return applicationService.diagnoseProfile(identityBoundary.requireUser(userId), request);
    }

    @ApiPostMapping(value = "/language/plan", desc = "生成语言考试计划", methodParamNames = {"userId", "request"})
    public @ApiResponseBody(value = "语言考试计划") StudyAbroadLanguagePlanResult generateLanguagePlan(
            @ApiRequestBody(value = "用户ID", required = true) String userId,
            @ApiRequestBody(value = "语言考试规划请求", required = true) StudyAbroadLanguagePlanRequest request) {
        return applicationService.generateLanguagePlan(identityBoundary.requireUser(userId), request);
    }

    @ApiPostMapping(value = "/school/position", desc = "生成留学选校定位", methodParamNames = {"userId", "request"})
    public @ApiResponseBody(value = "留学选校定位") StudyAbroadSchoolPositionResult positionSchools(
            @ApiRequestBody(value = "用户ID", required = true) String userId,
            @ApiRequestBody(value = "留学选校定位请求", required = true) StudyAbroadSchoolPositionRequest request) {
        return applicationService.positionSchools(identityBoundary.requireUser(userId), request);
    }

    @ApiPostMapping(value = "/statement/outline", desc = "生成个人陈述主线", methodParamNames = {"userId", "request"})
    public @ApiResponseBody(value = "个人陈述主线") StudyAbroadStatementOutlineResult buildStatementOutline(
            @ApiRequestBody(value = "用户ID", required = true) String userId,
            @ApiRequestBody(value = "个人陈述请求", required = true) StudyAbroadStatementRequest request) {
        return applicationService.buildStatementOutline(identityBoundary.requireUser(userId), request);
    }

    @ApiPostMapping(value = "/visa/checklist", desc = "生成签证与网申清单", methodParamNames = {"userId", "request"})
    public @ApiResponseBody(value = "签证与网申清单") StudyAbroadVisaChecklistResult buildVisaChecklist(
            @ApiRequestBody(value = "用户ID", required = true) String userId,
            @ApiRequestBody(value = "签证与网申请求", required = true) StudyAbroadVisaChecklistRequest request) {
        return applicationService.buildVisaChecklist(identityBoundary.requireUser(userId), request);
    }
}
