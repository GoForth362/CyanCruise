package v620.cc001.cloud01.app01.webapi;

import kd.bos.entity.api.ApiResult;
import kd.bos.openapi.common.custom.annotation.ApiController;
import kd.bos.openapi.common.custom.annotation.ApiMapping;
import kd.bos.openapi.common.custom.annotation.ApiPostMapping;
import kd.bos.openapi.common.custom.annotation.ApiRequestBody;
import kd.bos.openapi.common.custom.annotation.ApiResponseBody;
import kd.bos.openapi.common.result.CustomApiResult;
import v620.cc001.cloud01.app01.mservice.auth.impl.ServerManagedKapiRouteService;

import java.util.Map;

/**
 * Server-managed browser route for CyanCruise. Tokens stay on the backend.
 */
@ApiController(value = "cyanCruiseServerManagedWebApi", desc = "CyanCruise server-managed WebAPI router")
@ApiMapping("/cc001/cyancruise/server")
public class CyanCruiseServerManagedWebApi {

    private final ServerManagedKapiRouteService routeService;

    public CyanCruiseServerManagedWebApi() {
        this(new ServerManagedKapiRouteService());
    }

    CyanCruiseServerManagedWebApi(ServerManagedKapiRouteService routeService) {
        this.routeService = routeService == null ? new ServerManagedKapiRouteService() : routeService;
    }

    @ApiPostMapping(value = "/route", desc = "Route CyanCruise request with server-managed KAPI token",
            methodParamNames = {"params"})
    public @ApiResponseBody(value = "CyanCruise server-managed route result") CustomApiResult<Object> route(
            @ApiRequestBody(value = "CyanCruise route params", required = true) Map<String, Object> params) {
        ApiResult result = routeService.route(params);
        if (result.getSuccess()) {
            return CustomApiResult.success(result.getData());
        }
        return CustomApiResult.fail(result.getErrorCode(), result.getMessage());
    }
}
