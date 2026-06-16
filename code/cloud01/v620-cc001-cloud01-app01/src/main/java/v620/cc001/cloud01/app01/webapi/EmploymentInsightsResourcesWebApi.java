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
 * WebAPI entry for CyanCruise employment insights and resources.
 */
@ApiController(value = "employmentInsightsResourcesWebApi", desc = "CyanCruise employment insights and resources API")
@ApiMapping("/cc001/career-employment")
public class EmploymentInsightsResourcesWebApi {

    private final EmploymentInsightsResourcesApplicationService applicationService;

    public EmploymentInsightsResourcesWebApi() {
        this(new EmploymentInsightsResourcesApplicationService());
    }

    EmploymentInsightsResourcesWebApi(EmploymentInsightsResourcesApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @ApiPostMapping(value = "/insight/get", desc = "Get source-backed employment insight", methodParamNames = {"userId"})
    public @ApiResponseBody(value = "Employment insight") EmploymentInsightDto insight(
            @ApiRequestBody(value = "userId", required = true) String userId) {
        return applicationService.getInsight(userId);
    }

    @ApiPostMapping(value = "/resources/list", desc = "Get CyanCruise resource feed", methodParamNames = {"userId"})
    public @ApiResponseBody(value = "CyanCruise resources") CareerResourceFeedDto resources(
            @ApiRequestBody(value = "userId", required = false) String userId) {
        return applicationService.getResources(userId);
    }
}
