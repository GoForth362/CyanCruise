package v620.cc001.cloud01.app01.webapi;

import kd.bos.openapi.common.custom.annotation.ApiController;
import kd.bos.openapi.common.custom.annotation.ApiMapping;
import kd.bos.openapi.common.custom.annotation.ApiPostMapping;
import kd.bos.openapi.common.custom.annotation.ApiRequestBody;
import kd.bos.openapi.common.custom.annotation.ApiResponseBody;
import v620.cc001.base.common.dto.career.AssessmentScaleDto;
import v620.cc001.base.common.dto.career.AssessmentScoreResult;
import v620.cc001.base.common.dto.career.AssessmentSubmitRequest;
import v620.cc001.cloud01.app01.mservice.AssessmentApplicationService;

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

    AssessmentWebApi(AssessmentApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @ApiPostMapping(value = "/submit", desc = "提交职业测评答案", methodParamNames = {"userId", "scale", "request"})
    public @ApiResponseBody(value = "职业测评评分结果") AssessmentScoreResult submit(
            @ApiRequestBody(value = "用户ID", required = true) String userId,
            @ApiRequestBody(value = "测评量表", required = true) AssessmentScaleDto scale,
            @ApiRequestBody(value = "测评答案", required = true) AssessmentSubmitRequest request) {
        return applicationService.submitAndSaveProfile(userId, scale, request);
    }
}
