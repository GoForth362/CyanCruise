package v620.cc001.cloud01.app01.webapi.assessment;

import kd.bos.openapi.common.custom.annotation.ApiController;
import kd.bos.openapi.common.custom.annotation.ApiMapping;
import kd.bos.openapi.common.custom.annotation.ApiPostMapping;
import kd.bos.openapi.common.custom.annotation.ApiRequestBody;
import kd.bos.openapi.common.custom.annotation.ApiResponseBody;
import v620.cc001.base.common.dto.career.AssessmentScaleDto;
import v620.cc001.base.common.dto.career.AssessmentScoreResult;
import v620.cc001.base.common.dto.career.AssessmentSubmitRequest;
import v620.cc001.cloud01.app01.mservice.application.AssessmentApplicationService;

import java.util.List;

/**
 * WebAPI boundary for migrated career assessment submission behavior.
 */
@ApiController(value = "assessmentWebApi", desc = "职业测评 API")
@ApiMapping("/cc001/assessment")
public class AssessmentWebApi {

    private final AssessmentApplicationService applicationService;

    public AssessmentWebApi() {
        this(new AssessmentApplicationService());
    }

    public AssessmentWebApi(AssessmentApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @ApiPostMapping(value = "/scales", desc = "列出职业测评量表")
    public @ApiResponseBody(value = "职业测评量表") List<AssessmentScaleDto> scales() {
        return applicationService.listScales();
    }

    @ApiPostMapping(value = "/questions", desc = "读取职业测评题目", methodParamNames = {"scaleId"})
    public @ApiResponseBody(value = "职业测评量表与题目") AssessmentScaleDto questions(
            @ApiRequestBody(value = "量表ID", required = true) Long scaleId) {
        return applicationService.getScale(scaleId);
    }

    @ApiPostMapping(value = "/submit", desc = "提交职业测评答案", methodParamNames = {"userId", "scale", "request"})
    public @ApiResponseBody(value = "职业测评评分结果") AssessmentScoreResult submit(
            @ApiRequestBody(value = "用户ID", required = true) String userId,
            @ApiRequestBody(value = "测评量表", required = true) AssessmentScaleDto scale,
            @ApiRequestBody(value = "测评答案", required = true) AssessmentSubmitRequest request) {
        if (scale == null || scale.getScaleId() == null || scale.getQuestions() == null || scale.getQuestions().isEmpty()) {
            return applicationService.submit(userId, request);
        }
        return applicationService.submitAndSaveProfile(userId, scale, request);
    }

    @ApiPostMapping(value = "/records", desc = "列出职业测评记录", methodParamNames = {"userId"})
    public @ApiResponseBody(value = "职业测评记录") List<AssessmentScoreResult> records(
            @ApiRequestBody(value = "用户ID", required = true) String userId) {
        return applicationService.listResults(userId);
    }

    @ApiPostMapping(value = "/record/get", desc = "读取职业测评记录", methodParamNames = {"userId", "recordId"})
    public @ApiResponseBody(value = "职业测评记录") AssessmentScoreResult record(
            @ApiRequestBody(value = "用户ID", required = true) String userId,
            @ApiRequestBody(value = "记录ID", required = true) Long recordId) {
        return applicationService.loadResult(userId, recordId);
    }
}
