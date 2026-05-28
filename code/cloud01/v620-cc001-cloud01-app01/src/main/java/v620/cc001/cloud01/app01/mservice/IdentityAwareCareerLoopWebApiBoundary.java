package v620.cc001.cloud01.app01.mservice;

import v620.base.helper.career.CosmicIdentityContextHelper;
import v620.cc001.base.common.dto.career.AdminConstants;
import v620.cc001.base.common.dto.career.AdminIdentityDto;
import v620.cc001.base.common.dto.career.AdminOperationResult;
import v620.cc001.base.common.dto.career.CosmicIdentityConstants;
import v620.cc001.base.common.dto.career.CosmicIdentityContextDto;

/**
 * Small WebAPI boundary that validates platform identity before service calls.
 */
public class IdentityAwareCareerLoopWebApiBoundary {

    private final CareerLoopIdentityResolver resolver;
    private final CosmicIdentityContextHelper helper;

    public IdentityAwareCareerLoopWebApiBoundary() {
        this(new UnavailableCosmicIdentityResolver(), new CosmicIdentityContextHelper());
    }

    public IdentityAwareCareerLoopWebApiBoundary(CareerLoopIdentityResolver resolver) {
        this(resolver, new CosmicIdentityContextHelper());
    }

    public IdentityAwareCareerLoopWebApiBoundary(CareerLoopIdentityResolver resolver,
                                                CosmicIdentityContextHelper helper) {
        this.resolver = resolver == null ? new UnavailableCosmicIdentityResolver() : resolver;
        this.helper = helper == null ? new CosmicIdentityContextHelper() : helper;
    }

    public String requireUser(String explicitUserId) {
        CosmicIdentityContextDto context = resolver.resolve();
        String status = helper.userStatus(context, explicitUserId);
        if (!CosmicIdentityConstants.STATUS_OK.equals(status)) {
            throw new IdentityBoundaryException(status, "CareerLoop user identity rejected: " + status);
        }
        return helper.resolvedUserId(context);
    }

    public String requireAdmin(String explicitAdminId) {
        CosmicIdentityContextDto context = resolver.resolve();
        String status = helper.adminStatus(context, explicitAdminId);
        if (!CosmicIdentityConstants.STATUS_OK.equals(status)) {
            throw new IdentityBoundaryException(status, "CareerLoop admin identity rejected: " + status);
        }
        return helper.resolvedAdminId(context);
    }

    public AdminIdentityDto rejectAsAdminIdentity(IdentityBoundaryException ex) {
        AdminIdentityDto identity = new AdminIdentityDto();
        identity.setAdmin(Boolean.FALSE);
        identity.setStatus(toAdminStatus(ex.getStatus()));
        identity.setMessage(ex.getMessage());
        return identity;
    }

    public AdminOperationResult rejectAsAdminOperation(IdentityBoundaryException ex, String targetId) {
        AdminOperationResult result = new AdminOperationResult();
        result.setStatus(toAdminStatus(ex.getStatus()));
        result.setMessage(ex.getMessage());
        result.setTargetId(targetId);
        result.setUpdated(Integer.valueOf(0));
        result.setAuditRecorded(Boolean.FALSE);
        return result;
    }

    private String toAdminStatus(String status) {
        if (CosmicIdentityConstants.STATUS_IDENTITY_REQUIRED.equals(status)) {
            return AdminConstants.STATUS_IDENTITY_REQUIRED;
        }
        if (CosmicIdentityConstants.STATUS_FORBIDDEN.equals(status)
                || CosmicIdentityConstants.STATUS_IDENTITY_MISMATCH.equals(status)) {
            return AdminConstants.STATUS_FORBIDDEN;
        }
        return AdminConstants.STATUS_FAILED;
    }
}
