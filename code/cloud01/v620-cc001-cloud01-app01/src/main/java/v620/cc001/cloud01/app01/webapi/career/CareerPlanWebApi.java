package v620.cc001.cloud01.app01.webapi.career;

import kd.bos.openapi.common.custom.annotation.ApiController;
import kd.bos.openapi.common.custom.annotation.ApiMapping;
import kd.bos.openapi.common.custom.annotation.ApiPostMapping;
import kd.bos.openapi.common.custom.annotation.ApiRequestBody;
import kd.bos.openapi.common.custom.annotation.ApiResponseBody;
import v620.cc001.base.common.dto.career.CareerPlanSaveRequest;
import v620.cc001.base.common.dto.career.CareerPlanSummaryDto;
import v620.cc001.base.common.dto.career.CareerDailyPlanDto;
import v620.cc001.base.common.dto.career.CareerDailyTaskUpdateRequest;
import v620.cc001.cloud01.app01.mservice.application.CareerDailyPlanApplicationService;
import v620.cc001.cloud01.app01.mservice.application.CareerPlanApplicationService;
import v620.cc001.cloud01.app01.mservice.auth.impl.IdentityAwareCyanCruiseWebApiBoundary;

/**
 * WebAPI entry for migrated career plan summary capability.
 */
@ApiController(value = "careerPlanWebApi", desc = "求职计划 API")
@ApiMapping("/cc001/career-plan")
public class CareerPlanWebApi {

    private final CareerPlanApplicationService applicationService;
    private final IdentityAwareCyanCruiseWebApiBoundary identityBoundary;
    private final CareerDailyPlanApplicationService dailyPlanApplicationService;

    public CareerPlanWebApi() {
        this(new CareerPlanApplicationService(), new IdentityAwareCyanCruiseWebApiBoundary(),
                new CareerDailyPlanApplicationService());
    }

    public CareerPlanWebApi(CareerPlanApplicationService applicationService) {
        this(applicationService, new IdentityAwareCyanCruiseWebApiBoundary());
    }

    public CareerPlanWebApi(CareerPlanApplicationService applicationService,
                            IdentityAwareCyanCruiseWebApiBoundary identityBoundary) {
        this(applicationService, identityBoundary, new CareerDailyPlanApplicationService());
    }

    public CareerPlanWebApi(CareerPlanApplicationService applicationService,
                            IdentityAwareCyanCruiseWebApiBoundary identityBoundary,
                            CareerDailyPlanApplicationService dailyPlanApplicationService) {
        this.applicationService = applicationService;
        this.identityBoundary = identityBoundary;
        this.dailyPlanApplicationService = dailyPlanApplicationService;
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

    @ApiPostMapping(value = "/generate", desc = "使用就业规划智能体生成路线图", methodParamNames = {"userId"})
    public @ApiResponseBody(value = "求职计划摘要") CareerPlanSummaryDto generate(
            @ApiRequestBody(value = "用户ID", required = true) String userId) {
        return applicationService.generateAgentPlan(identityBoundary.requireUser(userId));
    }

    @ApiPostMapping(value = "/daily/get", desc = "获取今天的路线执行任务", methodParamNames = {"userId"})
    public @ApiResponseBody(value = "今天的路线执行任务") CareerDailyPlanDto daily(
            @ApiRequestBody(value = "用户ID", required = true) String userId) {
        return dailyPlanApplicationService.getToday(identityBoundary.requireUser(userId));
    }

    @ApiPostMapping(value = "/daily/task/update", desc = "更新每日任务完成状态",
            methodParamNames = {"userId", "request"})
    public @ApiResponseBody(value = "更新后的每日计划") CareerDailyPlanDto updateDailyTask(
            @ApiRequestBody(value = "用户ID", required = true) String userId,
            @ApiRequestBody(value = "任务状态", required = true) CareerDailyTaskUpdateRequest request) {
        return dailyPlanApplicationService.update(identityBoundary.requireUser(userId), request);
    }
}
