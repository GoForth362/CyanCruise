package v620.cc001.cloud01.app01.webapi;

import kd.bos.bill.IBillWebApiPlugin;
import kd.bos.entity.api.ApiResult;
import v620.cc001.cloud01.app01.mservice.IdentityBoundaryException;

import java.util.Map;

/**
 * Cosmic Custom Web API entry that routes platform kapi calls to CareerLoop WebAPI contracts.
 */
public class CareerLoopCustomWebApiPlugin implements IBillWebApiPlugin {

    public static final String PARAM_PATH = "path";
    public static final String PARAM_BODY = "body";

    private final CareerLoopIdentityWebApi identityWebApi;
    private final CareerProfileWebApi profileWebApi;
    private final CareerAgentWebApi agentWebApi;

    public CareerLoopCustomWebApiPlugin() {
        this(new CareerLoopIdentityWebApi(), new CareerProfileWebApi(), new CareerAgentWebApi());
    }

    CareerLoopCustomWebApiPlugin(CareerLoopIdentityWebApi identityWebApi,
                                 CareerProfileWebApi profileWebApi,
                                 CareerAgentWebApi agentWebApi) {
        this.identityWebApi = identityWebApi;
        this.profileWebApi = profileWebApi;
        this.agentWebApi = agentWebApi;
    }

    @Override
    public ApiResult doCustomService(Map<String, Object> params) {
        if (params == null) {
            return ApiResult.fail("CareerLoop custom WebAPI params are required");
        }
        String path = normalizePath(text(params.get(PARAM_PATH)));
        Object body = params.get(PARAM_BODY);
        try {
            if ("/cc001/identity/current".equals(path)) {
                return ApiResult.success(identityWebApi.current());
            }
            if ("/cc001/career-profile/snapshot/get".equals(path)) {
                return ApiResult.success(profileWebApi.snapshot(extractUserId(body)));
            }
            if ("/cc001/career-agent/today/get".equals(path)) {
                return ApiResult.success(agentWebApi.todayByUserId(extractUserId(body)));
            }
            return ApiResult.fail("Unsupported CareerLoop custom WebAPI path: " + path);
        } catch (IdentityBoundaryException ex) {
            return ApiResult.fail(ex.getStatus(), ex.getMessage());
        } catch (RuntimeException ex) {
            return ApiResult.ex(ex);
        }
    }

    private String normalizePath(String path) {
        if (path == null || path.trim().isEmpty()) {
            return "";
        }
        String normalized = path.trim();
        if (normalized.startsWith("/ierp/")) {
            normalized = normalized.substring("/ierp".length());
        }
        return normalized.charAt(0) == '/' ? normalized : "/" + normalized;
    }

    private String extractUserId(Object body) {
        if (body instanceof Map) {
            Object userId = ((Map<?, ?>) body).get("userId");
            if (userId != null) {
                return text(userId);
            }
        }
        return text(body);
    }

    private String text(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
