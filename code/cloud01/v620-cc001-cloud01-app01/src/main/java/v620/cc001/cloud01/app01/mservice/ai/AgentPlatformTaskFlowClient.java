package v620.cc001.cloud01.app01.mservice.ai;

import v620.cc001.base.common.dto.ai.AgentTaskFlowRequestDto;
import v620.cc001.base.common.dto.ai.AgentTaskFlowResponseDto;

/**
 * Boundary for server-side invocation of an Agent platform task flow.
 */
public interface AgentPlatformTaskFlowClient {

    AgentTaskFlowResponseDto execute(AgentTaskFlowRequestDto request);
}
