package v620.cc001.cloud01.app01.mservice.auth;

/**
 * Resolves whether a Cosmic platform user has administrator authority.
 */
public interface CosmicAdminAuthorityResolver {

    boolean isAdmin(String userId, String adminId);
}
