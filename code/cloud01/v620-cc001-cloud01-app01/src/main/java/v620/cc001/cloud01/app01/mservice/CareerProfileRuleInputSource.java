package v620.cc001.cloud01.app01.mservice;

import v620.cc001.base.common.dto.career.CareerAgentRuleInput;

/**
 * Rule input source backed by the migrated career profile snapshot service.
 */
public class CareerProfileRuleInputSource implements CareerAgentRuleInputSource {

    private final CareerProfileApplicationService profileApplicationService;

    public CareerProfileRuleInputSource() {
        this(new CareerProfileApplicationService());
    }

    public CareerProfileRuleInputSource(CareerProfileApplicationService profileApplicationService) {
        this.profileApplicationService = profileApplicationService;
    }

    public CareerAgentRuleInput loadByUserId(String userId) {
        CareerAgentRuleInput input = new CareerAgentRuleInput();
        input.setSnapshot(profileApplicationService.getSnapshot(userId));
        input.setCheckInStatus(new CareerAgentRuleInput.CheckInStatus());
        return input;
    }
}
