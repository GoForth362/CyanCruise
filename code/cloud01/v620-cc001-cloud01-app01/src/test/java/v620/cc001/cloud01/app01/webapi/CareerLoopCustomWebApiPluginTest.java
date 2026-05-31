package v620.cc001.cloud01.app01.webapi;

import kd.bos.entity.api.ApiResult;
import kd.bos.openapi.common.result.CustomApiResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import v620.cc001.base.common.dto.career.CosmicIdentityConstants;
import v620.cc001.base.common.dto.career.CosmicIdentityContextDto;
import v620.cc001.base.common.dto.career.NotificationUnreadCountDto;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;
import v620.cc001.cloud01.app01.mservice.DevelopmentCareerLoopIdentityResolver;
import v620.cc001.cloud01.app01.mservice.FileCareerProfileStorage;
import v620.cc001.cloud01.app01.mservice.IdentityAwareCareerLoopWebApiBoundary;

import java.nio.file.Path;
import java.util.ArrayList;
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
    void routesOnboardingSaveThroughCustomWebApiContract(@TempDir Path tempDir) {
        System.setProperty(FileCareerProfileStorage.STORAGE_DIR_PROPERTY, tempDir.toString());
        try {
            IdentityAwareCareerLoopWebApiBoundary boundary =
                    new IdentityAwareCareerLoopWebApiBoundary(new DevelopmentCareerLoopIdentityResolver("api-user"));
            CareerLoopCustomWebApiPlugin plugin = new CareerLoopCustomWebApiPlugin(
                    new CareerLoopIdentityWebApi(boundary),
                    new CareerProfileWebApi(boundary),
                    new CareerAgentWebApi(new v620.cc001.cloud01.app01.mservice.CareerAgentTodayApplicationService(), boundary));
            Map<String, Object> request = new HashMap<String, Object>();
            request.put("identityType", "career_switcher");
            request.put("targetRole", "Java Backend Engineer");
            request.put("resumeStatus", "ready");
            request.put("preference", "backend");
            Map<String, Object> body = new HashMap<String, Object>();
            body.put("userId", "api-user");
            body.put("request", request);

            ApiResult result = plugin.doCustomService(params("/cc001/career-profile/onboarding/save", body));

            assertTrue(result.getSuccess());
            UserProfileSnapshot snapshot = (UserProfileSnapshot) result.getData();
            assertEquals("career_switcher", snapshot.getOnboarding().getIdentityType());
            assertEquals("ready", snapshot.getOnboarding().getResumeStatus());
            assertEquals("yes", snapshot.getOnboarding().getHasResume());
            assertEquals("backend", snapshot.getOnboarding().getPainPoint());
            assertEquals("Java Backend Engineer", snapshot.getPreferences().getTargetRole());
        } finally {
            System.clearProperty(FileCareerProfileStorage.STORAGE_DIR_PROPERTY);
        }
    }

    @Test
    void routesSecondaryReadEndpointsThroughCustomWebApiContract() {
        IdentityAwareCareerLoopWebApiBoundary boundary =
                new IdentityAwareCareerLoopWebApiBoundary(new DevelopmentCareerLoopIdentityResolver("api-user"));
        CareerLoopCustomWebApiPlugin plugin = new CareerLoopCustomWebApiPlugin(
                new CareerLoopIdentityWebApi(boundary),
                new CareerProfileWebApi(boundary),
                new CareerAgentWebApi(new v620.cc001.cloud01.app01.mservice.CareerAgentTodayApplicationService(), boundary));

        ApiResult notifications = plugin.doCustomService(params("/cc001/notifications/unread-count", "api-user"));
        ApiResult resources = plugin.doCustomService(params("/cc001/career-employment/resources/list", new HashMap<String, Object>()));
        Map<String, Object> fileBody = new HashMap<String, Object>();
        fileBody.put("fileUrlOrKey", "");
        ApiResult fileDelete = plugin.doCustomService(params("/cc001/files/delete", fileBody));
        Map<String, Object> weeklyBody = new HashMap<String, Object>();
        weeklyBody.put("userId", "api-user");
        weeklyBody.put("highlights", new ArrayList<String>());
        ApiResult weeklyReport = plugin.doCustomService(params("/cc001/notifications/weekly-report/run", weeklyBody));

        assertTrue(notifications.getSuccess());
        assertEquals(Integer.valueOf(0), ((NotificationUnreadCountDto) notifications.getData()).getCount());
        assertTrue(resources.getSuccess());
        assertTrue(fileDelete.getSuccess());
        assertTrue(weeklyReport.getSuccess());
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
