package v620.cc001.cloud01.app01.mservice.ai.impl;

import v620.cc001.cloud01.app01.mservice.ai.*;
import v620.cc001.base.common.dto.career.CareerAgentRuleInput;

/**
 * Placeholder source used until the BOS data model is created.
 */
public class CareerAgentRuleInputSourceUnavailable implements CareerAgentRuleInputSource {

    public CareerAgentRuleInput loadByUserId(String userId) {
        throw new IllegalStateException("Career agent BOS data model is not configured yet.");
    }
}
