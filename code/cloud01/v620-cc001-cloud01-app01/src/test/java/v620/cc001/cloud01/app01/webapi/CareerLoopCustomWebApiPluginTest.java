package v620.cc001.cloud01.app01.webapi;

import kd.bos.entity.api.ApiResult;
import kd.bos.openapi.common.result.CustomApiResult;
import org.junit.jupiter.api.Test;
import v620.cc001.base.common.dto.career.CosmicIdentityConstants;
import v620.cc001.base.common.dto.career.CosmicIdentityContextDto;
import v620.cc001.cloud01.app01.mservice.DevelopmentCareerLoopIdentityResolver;
import v620.cc001.cloud01.app01.mservice.IdentityAwareCareerLoopWebApiBoundary;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CareerLoopCustomWebApiPluginTest {

    @Test
    void routesIdentityCurrentThroughCustomWebApiContract() {
        IdentityAwareCareerLoopWebApiBoundary boundary =
                new IdentityAwareCareerLoopWebApiBoundary(new DevelopmentCareerLoopIdentityResolver("api-user"));
        CareerLoopCustomWebApiPlugin plugin = new CareerLoopCustomWebApiPlugin(
                new CareerLoopIdentityWebApi(boundary),
                new CareerProfileWebApi(boundary),
                new CareerAgentWebApi(new v620.cc001.cloud01.app01.mservice.CareerAgentTodayApplicationService(), boundary));

        ApiResult result = plugin.doCustomService(params("/cc001/identity/current", new HashMap<String, Object>()));

        assertTrue(result.getSuccess());
        CosmicIdentityContextDto identity = (CosmicIdentityContextDto) result.getData();
        assertEquals("api-user", identity.getUserId());
        assertEquals(CosmicIdentityConstants.STATUS_OK, identity.getStatus());
    }

    @Test
    void routeReturnsCustomApiResultForOpenApiJavaPlugin() {
        IdentityAwareCareerLoopWebApiBoundary boundary =
                new IdentityAwareCareerLoopWebApiBoundary(new DevelopmentCareerLoopIdentityResolver("api-user"));
        CareerLoopCustomWebApiPlugin plugin = new CareerLoopCustomWebApiPlugin(
                new CareerLoopIdentityWebApi(boundary),
                new CareerProfileWebApi(boundary),
                new CareerAgentWebApi(new v620.cc001.cloud01.app01.mservice.CareerAgentTodayApplicationService(), boundary));

        CustomApiResult<Object> result = plugin.route(params("/cc001/identity/current", new HashMap<String, Object>()));

        assertTrue(result.isStatus());
        CosmicIdentityContextDto identity = (CosmicIdentityContextDto) result.getData();
        assertEquals("api-user", identity.getUserId());
    }

    @Test
    void rejectsUnsupportedPath() {
        CareerLoopCustomWebApiPlugin plugin = new CareerLoopCustomWebApiPlugin();

        ApiResult result = plugin.doCustomService(params("/cc001/missing", new HashMap<String, Object>()));

        assertFalse(result.getSuccess());
        assertEquals("Unsupported CareerLoop custom WebAPI path: /cc001/missing", result.getMessage());
    }

    private Map<String, Object> params(String path, Object body) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(CareerLoopCustomWebApiPlugin.PARAM_PATH, path);
        params.put(CareerLoopCustomWebApiPlugin.PARAM_BODY, body);
        return params;
    }
}
