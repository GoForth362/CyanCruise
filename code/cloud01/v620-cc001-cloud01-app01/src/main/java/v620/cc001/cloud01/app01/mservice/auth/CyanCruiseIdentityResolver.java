package v620.cc001.cloud01.app01.mservice.auth;

import v620.cc001.base.common.dto.career.CosmicIdentityContextDto;

/**
 * Boundary for resolving CyanCruise identity from Cosmic or explicit development context.
 */
public interface CyanCruiseIdentityResolver {

    CosmicIdentityContextDto resolve();
}
