package v620.cc001.cloud01.app01.webapi.career;

import kd.bos.openapi.common.custom.annotation.ApiController;
import kd.bos.openapi.common.custom.annotation.ApiMapping;
import kd.bos.openapi.common.custom.annotation.ApiPostMapping;
import kd.bos.openapi.common.custom.annotation.ApiRequestBody;
import kd.bos.openapi.common.custom.annotation.ApiResponseBody;
import v620.cc001.base.common.dto.career.CareerPlanSaveRequest;
import v620.cc001.base.common.dto.career.CareerPlanSummaryDto;
import v620.cc001.cloud01.app01.mservice.application.CareerPlanApplicationService;

/**
 * WebAPI entry for migrated career plan summary capability.
 */
@ApiController(value = "careerPlanWebApi", desc = "求职计划 API")
@ApiMapping("/cc001/career-plan")
public class CareerPlanWebApi {

    private final CareerPlanApplicationService applicationService;

    public CareerPlanWebApi() {
        this(new CareerPlanApplicationService());
    }

    public CareerPlanWebApi(CareerPlanApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @ApiPostMapping(value = "/summary", desc = "按用户获取求职计划摘要", methodParamNames = {"userId"})
    public @ApiResponseBody(value = "求职计划摘要") CareerPlanSummaryDto summary(
            @ApiRequestBody(value = "用户ID", required = true) String userId) {
        return applicationService.getSummary(userId);
    }

    @ApiPostMapping(value = "/ensure", desc = "确保用户有求职计划", methodParamNames = {"userId"})
    public @ApiResponseBody(value = "求职计划摘要") CareerPlanSummaryDto ensure(
            @ApiRequestBody(value = "用户ID", required = true) String userId) {
        return applicationService.ensurePlan(userId);
    }

    @ApiPostMapping(value = "/save", desc = "保存或更新求职计划", methodParamNames = {"userId", "request"})
    public @ApiResponseBody(value = "求职计划摘要") CareerPlanSummaryDto save(
            @ApiRequestBody(value = "用户ID", required = true) String userId,
            @ApiRequestBody(value = "求职计划", required = true) CareerPlanSaveRequest request) {
        return applicationService.savePlan(userId, request);
    }
}
