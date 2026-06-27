package v620.cc001.cloud01.app01.mservice.application;

import v620.cc001.cloud01.app01.mservice.ai.CareerAgentRuleInputSource;
import v620.cc001.cloud01.app01.mservice.ai.CareerProfileRuleInputSource;
import v620.base.helper.career.CareerAgentTodayRuleService;
import v620.cc001.base.common.dto.career.CareerAgentRuleInput;
import v620.cc001.base.common.dto.career.CareerAgentTodayDto;

/**
 * Application service that keeps WebAPI, future form plugins, and data adapters
 * outside the pure rule implementation.
 */
public class CareerAgentTodayApplicationService {

    private final CareerAgentTodayRuleService ruleService;
    private final CareerAgentRuleInputSource inputSource;

    public CareerAgentTodayApplicationService() {
        this(new CareerAgentTodayRuleService(), new CareerProfileRuleInputSource());
    }

    public CareerAgentTodayApplicationService(CareerAgentTodayRuleService ruleService,
                                              CareerAgentRuleInputSource inputSource) {
        this.ruleService = ruleService;
        this.inputSource = inputSource;
    }

    public CareerAgentTodayDto recommend(CareerAgentRuleInput input) {
        return ruleService.recommend(input);
    }

    public CareerAgentTodayDto recommendByUserId(String userId) {
        return ruleService.recommend(inputSource.loadByUserId(userId));
    }
}
