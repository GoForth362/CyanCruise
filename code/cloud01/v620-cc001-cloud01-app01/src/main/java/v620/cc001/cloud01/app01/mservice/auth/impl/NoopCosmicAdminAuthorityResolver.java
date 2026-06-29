package v620.cc001.cloud01.app01.mservice.auth.impl;

import v620.cc001.cloud01.app01.mservice.auth.CosmicAdminAuthorityResolver;

public class NoopCosmicAdminAuthorityResolver implements CosmicAdminAuthorityResolver {

    public boolean isAdmin(String userId, String adminId) {
        return false;
    }
}
