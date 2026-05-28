package v620.cc001.cloud01.app01.mservice;

import v620.cc001.base.common.dto.career.CosmicIdentityContextDto;

/**
 * Boundary for resolving CareerLoop identity from Cosmic or explicit development context.
 */
public interface CareerLoopIdentityResolver {

    CosmicIdentityContextDto resolve();
}
