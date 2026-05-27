package v620.cc001.cloud01.app01.mservice;

import v620.cc001.base.common.dto.career.CareerAgentRuleInput;

/**
 * Rule input source backed by the migrated career profile snapshot service.
 */
public class CareerProfileRuleInputSource implements CareerAgentRuleInputSource {

    private final CareerProfileApplicationService profileApplicationService;
    private final CareerPlanApplicationService planApplicationService;

    public CareerProfileRuleInputSource() {
        this(new CareerProfileApplicationService(), new CareerPlanApplicationService());
    }

    public CareerProfileRuleInputSource(CareerProfileApplicationService profileApplicationService) {
        this(profileApplicationService, null);
    }

    public CareerProfileRuleInputSource(CareerProfileApplicationService profileApplicationService,
                                        CareerPlanApplicationService planApplicationService) {
        this.profileApplicationService = profileApplicationService;
        this.planApplicationService = planApplicationService;
    }

    public CareerAgentRuleInput loadByUserId(String userId) {
        CareerAgentRuleInput input = new CareerAgentRuleInput();
        input.setSnapshot(profileApplicationService.getSnapshot(userId));
        input.setCheckInStatus(new CareerAgentRuleInput.CheckInStatus());
        if (planApplicationService != null) {
            input.setWeeklyFocusItems(planApplicationService.getSummary(userId).getWeeklyFocus());
        }
        return input;
    }
}
