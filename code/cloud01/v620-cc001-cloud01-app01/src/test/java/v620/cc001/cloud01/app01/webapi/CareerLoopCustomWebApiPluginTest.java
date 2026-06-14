package v620.cc001.cloud01.app01.webapi;

import kd.bos.entity.api.ApiResult;
import kd.bos.openapi.common.result.CustomApiResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import v620.cc001.base.common.dto.career.CosmicIdentityConstants;
import v620.cc001.base.common.dto.career.CosmicIdentityContextDto;
import v620.cc001.base.common.dto.career.CareerProfileDraftDto;
import v620.cc001.base.common.dto.career.NotificationUnreadCountDto;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;
import v620.cc001.cloud01.app01.mservice.DevelopmentCareerLoopIdentityResolver;
import v620.cc001.cloud01.app01.mservice.FileCareerProfileStorage;
import v620.cc001.cloud01.app01.mservice.IdentityAwareCareerLoopWebApiBoundary;
import v620.cc001.cloud01.app01.mservice.PostgresqlProfileStorageConfig;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        String previousAdapter = System.getProperty(PostgresqlProfileStorageConfig.ADAPTER_PROPERTY);
        System.setProperty(PostgresqlProfileStorageConfig.ADAPTER_PROPERTY, "file");
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
            restoreProperty(PostgresqlProfileStorageConfig.ADAPTER_PROPERTY, previousAdapter);
        }
    }

    @Test
    void routesProfileDraftSaveAndGetThroughCustomWebApiContract(@TempDir Path tempDir) {
        String previousAdapter = System.getProperty(PostgresqlProfileStorageConfig.ADAPTER_PROPERTY);
        System.setProperty(PostgresqlProfileStorageConfig.ADAPTER_PROPERTY, "file");
        System.setProperty(FileCareerProfileStorage.STORAGE_DIR_PROPERTY, tempDir.toString());
        try {
            IdentityAwareCareerLoopWebApiBoundary boundary =
                    new IdentityAwareCareerLoopWebApiBoundary(new DevelopmentCareerLoopIdentityResolver("api-user"));
            CareerLoopCustomWebApiPlugin plugin = new CareerLoopCustomWebApiPlugin(
                    new CareerLoopIdentityWebApi(boundary),
                    new CareerProfileWebApi(boundary),
                    new CareerAgentWebApi(new v620.cc001.cloud01.app01.mservice.CareerAgentTodayApplicationService(), boundary));
            Map<String, Object> request = new HashMap<String, Object>();
            request.put("identityType", "student");
            request.put("educationStage", "undergraduate");
            request.put("targetRole", "Data Analyst");
            request.put("selectedGoal", "employment");
            Map<String, Object> body = new HashMap<String, Object>();
            body.put("userId", "api-user");
            body.put("request", request);

            ApiResult save = plugin.doCustomService(params("/cc001/career-profile/draft/save", body));
            ApiResult get = plugin.doCustomService(params("/cc001/career-profile/draft/get", body));
            ApiResult snapshot = plugin.doCustomService(params("/cc001/career-profile/snapshot/get", body));

            assertTrue(save.getSuccess());
            assertTrue(get.getSuccess());
            CareerProfileDraftDto draft = (CareerProfileDraftDto) get.getData();
            assertEquals("student", draft.getIdentityType());
            assertEquals("undergraduate", draft.getEducationStage());
            assertEquals("Data Analyst", draft.getTargetRole());
            assertEquals("employment", draft.getRouteIntent());
            assertEquals(null, ((UserProfileSnapshot) snapshot.getData()).getOnboarding());
        } finally {
            System.clearProperty(FileCareerProfileStorage.STORAGE_DIR_PROPERTY);
            restoreProperty(PostgresqlProfileStorageConfig.ADAPTER_PROPERTY, previousAdapter);
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
    void routeMapWebApisAreDeclaredInCustomRouter() throws Exception {
        String routeMap = new String(Files.readAllBytes(workspaceFile(
                "webapp/isv/v620/cyancruise/cyancruise-routes.json")), "UTF-8");
        String router = new String(Files.readAllBytes(workspaceFile(
                "code/cloud01/v620-cc001-cloud01-app01/src/main/java/v620/cc001/cloud01/app01/webapi/CareerLoopCustomWebApiPlugin.java")), "UTF-8");

        Set<String> declared = extractPaths(routeMap);
        Set<String> routed = extractPaths(router);
        declared.removeAll(routed);

        assertTrue(declared.isEmpty(), "custom router missing route map paths: " + declared);
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

    private Set<String> extractPaths(String text) {
        Set<String> paths = new LinkedHashSet<String>();
        Matcher matcher = Pattern.compile("\"(/cc001/[^\"]+)\"").matcher(text);
        while (matcher.find()) {
            paths.add(matcher.group(1));
        }
        return paths;
    }

    private Path workspaceFile(String relativePath) {
        Path current = Paths.get("").toAbsolutePath();
        while (current != null) {
            Path candidate = current.resolve(relativePath);
            if (Files.exists(candidate)) {
                return candidate;
            }
            current = current.getParent();
        }
        throw new IllegalStateException("workspace file not found: " + relativePath);
    }

    private void restoreProperty(String key, String value) {
        if (value == null) {
            System.clearProperty(key);
            return;
        }
        System.setProperty(key, value);
    }
}
