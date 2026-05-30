package v620.cc001.cloud01.app01.webapi;

import kd.bos.openapi.common.custom.annotation.ApiController;
import kd.bos.openapi.common.custom.annotation.ApiMapping;
import kd.bos.openapi.common.custom.annotation.ApiPostMapping;
import kd.bos.openapi.common.custom.annotation.ApiResponseBody;
import v620.cc001.base.common.dto.career.CosmicIdentityContextDto;
import v620.cc001.cloud01.app01.mservice.IdentityAwareCareerLoopWebApiBoundary;

/**
 * Identity endpoint used by the static CareerLoop shell after it is opened from a Cosmic menu.
 */
@ApiController(value = "careerLoopIdentityWebApi", desc = "CareerLoop 身份 API")
@ApiMapping("/cc001/identity")
public class CareerLoopIdentityWebApi {

    private final IdentityAwareCareerLoopWebApiBoundary identityBoundary;

    public CareerLoopIdentityWebApi() {
        this(new IdentityAwareCareerLoopWebApiBoundary());
    }

    CareerLoopIdentityWebApi(IdentityAwareCareerLoopWebApiBoundary identityBoundary) {
        this.identityBoundary = identityBoundary;
    }

    @ApiPostMapping(value = "/current", desc = "获取当前平台身份")
    public @ApiResponseBody(value = "当前平台身份") CosmicIdentityContextDto current() {
        return identityBoundary.currentIdentity();
    }
}
