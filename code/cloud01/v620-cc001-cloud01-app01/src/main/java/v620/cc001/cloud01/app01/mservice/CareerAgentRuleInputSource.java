package v620.cc001.cloud01.app01.mservice;

import v620.cc001.base.common.dto.career.CareerAgentRuleInput;

/**
 * Data-source boundary for assembling the career agent rule input.
 */
public interface CareerAgentRuleInputSource {

    CareerAgentRuleInput loadByUserId(String userId);
}
