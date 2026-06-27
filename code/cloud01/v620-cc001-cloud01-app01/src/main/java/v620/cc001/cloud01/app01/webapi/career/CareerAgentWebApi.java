package v620.cc001.cloud01.app01.webapi.career;

import v620.cc001.cloud01.app01.mservice.auth.impl.IdentityAwareCyanCruiseWebApiBoundary;
import kd.bos.openapi.common.custom.annotation.ApiController;
import kd.bos.openapi.common.custom.annotation.ApiMapping;
import kd.bos.openapi.common.custom.annotation.ApiPostMapping;
import kd.bos.openapi.common.custom.annotation.ApiRequestBody;
import kd.bos.openapi.common.custom.annotation.ApiResponseBody;
import v620.cc001.cloud01.app01.mservice.application.CareerAgentTodayApplicationService;
import v620.cc001.base.common.dto.career.CareerAgentRuleInput;
import v620.cc001.base.common.dto.career.CareerAgentTodayDto;
import v620.cc001.cloud01.app01.mservice.auth.impl.IdentityAwareCyanCruiseWebApiBoundary;

/**
 * WebAPI entry for the migrated career agent daily recommendation rule.
 */
@ApiController(value = "careerAgentWebApi", desc = "职业规划 Agent API")
@ApiMapping("/cc001/career-agent")
public class CareerAgentWebApi {

    private final CareerAgentTodayApplicationService todayApplicationService;
    private final IdentityAwareCyanCruiseWebApiBoundary identityBoundary;

    public CareerAgentWebApi() {
        this(new CareerAgentTodayApplicationService());
    }

    public CareerAgentWebApi(CareerAgentTodayApplicationService todayApplicationService) {
        this(todayApplicationService, new IdentityAwareCyanCruiseWebApiBoundary());
    }

    public CareerAgentWebApi(CareerAgentTodayApplicationService todayApplicationService,
                      IdentityAwareCyanCruiseWebApiBoundary identityBoundary) {
        this.todayApplicationService = todayApplicationService;
        this.identityBoundary = identityBoundary;
    }

    @ApiPostMapping(value = "/today", desc = "生成今日职业规划建议", methodParamNames = {"input"})
    public @ApiResponseBody(value = "今日职业规划建议") CareerAgentTodayDto today(
            @ApiRequestBody(value = "规则输入", required = true) CareerAgentRuleInput input) {
        return todayApplicationService.recommend(input);
    }

    @ApiPostMapping(value = "/today/get", desc = "按用户生成今日职业规划建议", methodParamNames = {"userId"})
    public @ApiResponseBody(value = "今日职业规划建议") CareerAgentTodayDto todayByUserId(
            @ApiRequestBody(value = "用户ID", required = true) String userId) {
        return todayApplicationService.recommendByUserId(identityBoundary.requireUser(userId));
    }
}
