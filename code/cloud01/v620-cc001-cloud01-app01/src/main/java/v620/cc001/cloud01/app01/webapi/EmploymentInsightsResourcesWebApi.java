package v620.cc001.cloud01.app01.webapi;

import kd.bos.openapi.common.custom.annotation.ApiController;
import kd.bos.openapi.common.custom.annotation.ApiMapping;
import kd.bos.openapi.common.custom.annotation.ApiPostMapping;
import kd.bos.openapi.common.custom.annotation.ApiRequestBody;
import kd.bos.openapi.common.custom.annotation.ApiResponseBody;
import v620.cc001.base.common.dto.career.CareerResourceFeedDto;
import v620.cc001.base.common.dto.career.EmploymentInsightDto;
import v620.cc001.cloud01.app01.mservice.EmploymentInsightsResourcesApplicationService;

/**
 * CyanCruise 就业洞察与资源 WebAPI 入口。
 */
@ApiController(value = "employmentInsightsResourcesWebApi", desc = "CyanCruise 就业洞察与资源 API")
@ApiMapping("/cc001/career-employment")
public class EmploymentInsightsResourcesWebApi {

    private final EmploymentInsightsResourcesApplicationService applicationService;

    public EmploymentInsightsResourcesWebApi() {
        this(new EmploymentInsightsResourcesApplicationService());
    }

    EmploymentInsightsResourcesWebApi(EmploymentInsightsResourcesApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @ApiPostMapping(value = "/insight/get", desc = "获取基于用户画像的就业洞察", methodParamNames = {"userId"})
    public @ApiResponseBody(value = "就业洞察") EmploymentInsightDto insight(
            @ApiRequestBody(value = "userId", required = true) String userId) {
        return applicationService.getInsight(userId);
    }

    @ApiPostMapping(value = "/resources/list", desc = "获取 CyanCruise 就业资源列表", methodParamNames = {"userId"})
    public @ApiResponseBody(value = "CyanCruise 就业资源") CareerResourceFeedDto resources(
            @ApiRequestBody(value = "userId", required = false) String userId) {
        return applicationService.getResources(userId);
    }
}
