package v620.cc001.cloud01.app01.mservice.auth.impl;

import v620.cc001.cloud01.app01.mservice.auth.*;
import v620.cc001.base.common.dto.career.CosmicIdentityContextDto;

/**
 * Production-safe default until a tenant-specific Cosmic identity adapter is wired.
 */
public class UnavailableCosmicIdentityResolver implements CyanCruiseIdentityResolver {

    public CosmicIdentityContextDto resolve() {
        return CosmicIdentityContextDto.identityRequired("Cosmic platform identity context is unavailable");
    }
}
