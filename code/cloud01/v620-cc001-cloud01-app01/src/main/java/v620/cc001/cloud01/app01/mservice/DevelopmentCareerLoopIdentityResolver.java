package v620.cc001.cloud01.app01.mservice;

import v620.cc001.base.common.dto.career.CosmicIdentityContextDto;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Explicit non-production resolver for focused tests and local validation.
 */
public class DevelopmentCareerLoopIdentityResolver implements CareerLoopIdentityResolver {

    private final String userId;
    private final String adminId;
    private final List<String> roles;

    public DevelopmentCareerLoopIdentityResolver(String userId) {
        this(userId, null, Collections.<String>emptyList());
    }

    public DevelopmentCareerLoopIdentityResolver(String userId, String adminId, String... roles) {
        this(userId, adminId, roles == null ? Collections.<String>emptyList() : Arrays.asList(roles));
    }

    public DevelopmentCareerLoopIdentityResolver(String userId, String adminId, List<String> roles) {
        this.userId = userId;
        this.adminId = adminId;
        this.roles = roles;
    }

    public CosmicIdentityContextDto resolve() {
        return CosmicIdentityContextDto.development(userId, adminId, roles);
    }
}
