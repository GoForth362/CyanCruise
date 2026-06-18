package v620.cc001.cloud01.app01.webapi;

import kd.bos.entity.api.ApiResult;
import kd.bos.openapi.common.result.CustomApiResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import v620.base.helper.career.AssessmentScoringService;
import v620.base.helper.career.AssistantChatHelper;
import v620.base.helper.career.CareerAgentTodayRuleService;
import v620.base.helper.career.CareerPlanSummaryService;
import v620.cc001.base.common.dto.career.CosmicIdentityConstants;
import v620.cc001.base.common.dto.career.CosmicIdentityContextDto;
import v620.cc001.base.common.dto.career.CareerProfileDraftDto;
import v620.cc001.base.common.dto.career.NotificationUnreadCountDto;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;
import v620.base.helper.career.CareerProfileBuildService;
import v620.base.helper.career.CareerProfileSnapshotMergeService;
import v620.base.helper.career.InterviewCoreService;
import v620.base.helper.career.ResumeDiagnosisService;
import v620.cc001.cloud01.app01.mservice.AssessmentApplicationService;
import v620.cc001.cloud01.app01.mservice.AssistantChatApplicationService;
import v620.cc001.cloud01.app01.mservice.CareerAgentTodayApplicationService;
import v620.cc001.cloud01.app01.mservice.CareerPlanApplicationService;
import v620.cc001.cloud01.app01.mservice.CareerProfileApplicationService;
import v620.cc001.cloud01.app01.mservice.CareerProfileRuleInputSource;
import v620.cc001.cloud01.app01.mservice.DefaultResumeDiagnosisAnalyzer;
import v620.cc001.cloud01.app01.mservice.DevelopmentCareerLoopIdentityResolver;
import v620.cc001.cloud01.app01.mservice.EmptyAssistantChatContextProvider;
import v620.cc001.cloud01.app01.mservice.FileAssistantChatStorage;
import v620.cc001.cloud01.app01.mservice.FileCareerPlanStorage;
import v620.cc001.cloud01.app01.mservice.FileCareerProfileStorage;
import v620.cc001.cloud01.app01.mservice.FileInterviewStorage;
import v620.cc001.cloud01.app01.mservice.FileResumeDiagnosisStorage;
import v620.cc001.cloud01.app01.mservice.FileResumeStorage;
import v620.cc001.cloud01.app01.mservice.IdentityAwareCareerLoopWebApiBoundary;
import v620.cc001.cloud01.app01.mservice.InterviewApplicationService;
import v620.cc001.cloud01.app01.mservice.ResumeApplicationService;
import v620.cc001.cloud01.app01.mservice.ResumeDiagnosisApplicationService;
import v620.cc001.cloud01.app01.mservice.UnavailableAssistantChatGenerator;

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
    void routesIdentityCurrentThroughCustomWebApiContract(@TempDir Path tempDir) {
        IdentityAwareCareerLoopWebApiBoundary boundary =
                new IdentityAwareCareerLoopWebApiBoundary(new DevelopmentCareerLoopIdentityResolver("api-user"));
        CareerLoopCustomWebApiPlugin plugin = plugin(tempDir, boundary);

        ApiResult result = plugin.doCustomService(params("/cc001/identity/current", new HashMap<String, Object>()));

        assertTrue(result.getSuccess());
        CosmicIdentityContextDto identity = (CosmicIdentityContextDto) result.getData();
        assertEquals("api-user", identity.getUserId());
        assertEquals(CosmicIdentityConstants.STATUS_OK, identity.getStatus());
    }

    @Test
    void routeReturnsCustomApiResultForOpenApiJavaPlugin(@TempDir Path tempDir) {
        IdentityAwareCareerLoopWebApiBoundary boundary =
                new IdentityAwareCareerLoopWebApiBoundary(new DevelopmentCareerLoopIdentityResolver("api-user"));
        CareerLoopCustomWebApiPlugin plugin = plugin(tempDir, boundary);

        CustomApiResult<Object> result = plugin.route(params("/cc001/identity/current", new HashMap<String, Object>()));

        assertTrue(result.isStatus());
        CosmicIdentityContextDto identity = (CosmicIdentityContextDto) result.getData();
        assertEquals("api-user", identity.getUserId());
    }

    @Test
    void routesOnboardingSaveThroughCustomWebApiContract(@TempDir Path tempDir) {
        IdentityAwareCareerLoopWebApiBoundary boundary =
                new IdentityAwareCareerLoopWebApiBoundary(new DevelopmentCareerLoopIdentityResolver("api-user"));
        CareerLoopCustomWebApiPlugin plugin = plugin(tempDir, boundary);
        Map<String, Object> request = new HashMap<String, Object>();
        request.put("identityType", "career_switcher");
        request.put("educationStage", "undergraduate");
        request.put("schoolMajor", "软件工程");
        request.put("experience", "自学数据结构与算法");
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
        assertEquals("undergraduate", snapshot.getOnboarding().getStage());
        assertEquals("软件工程", snapshot.getOnboarding().getEducation().getMajor());
        assertEquals("自学数据结构与算法", snapshot.getOnboarding().getExperience());
        assertEquals("ready", snapshot.getOnboarding().getResumeStatus());
        assertEquals("yes", snapshot.getOnboarding().getHasResume());
        assertEquals("backend", snapshot.getOnboarding().getPainPoint());
        assertEquals("Java Backend Engineer", snapshot.getPreferences().getTargetRole());
    }

    @Test
    void routesProfileDraftSaveAndGetThroughCustomWebApiContract(@TempDir Path tempDir) {
        IdentityAwareCareerLoopWebApiBoundary boundary =
                new IdentityAwareCareerLoopWebApiBoundary(new DevelopmentCareerLoopIdentityResolver("api-user"));
        CareerLoopCustomWebApiPlugin plugin = plugin(tempDir, boundary);
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
    }

    @Test
    void routesSecondaryReadEndpointsThroughCustomWebApiContract(@TempDir Path tempDir) {
        IdentityAwareCareerLoopWebApiBoundary boundary =
                new IdentityAwareCareerLoopWebApiBoundary(new DevelopmentCareerLoopIdentityResolver("api-user"));
        CareerLoopCustomWebApiPlugin plugin = plugin(tempDir, boundary);

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
    void routesAssessmentCatalogAndRecordsThroughCustomWebApiContract(@TempDir Path tempDir) {
        IdentityAwareCareerLoopWebApiBoundary boundary =
                new IdentityAwareCareerLoopWebApiBoundary(new DevelopmentCareerLoopIdentityResolver("api-user"));
        CareerLoopCustomWebApiPlugin plugin = plugin(tempDir, boundary);

        ApiResult scales = plugin.doCustomService(params("/cc001/assessment/scales", new HashMap<String, Object>()));
        Map<String, Object> questionsBody = new HashMap<String, Object>();
        questionsBody.put("scaleId", Long.valueOf(1001L));
        ApiResult questions = plugin.doCustomService(params("/cc001/assessment/questions", questionsBody));
        Map<String, Object> answers = new HashMap<String, Object>();
        answers.put("100101", "100101");
        answers.put("100102", "100201");
        answers.put("100103", "100301");
        answers.put("100104", "100401");
        answers.put("100105", "100502");
        answers.put("100106", "100602");
        answers.put("100107", "100702");
        answers.put("100108", "100802");
        answers.put("100109", "100901");
        answers.put("100110", "101001");
        answers.put("100111", "101101");
        answers.put("100112", "101201");
        answers.put("100113", "101302");
        answers.put("100114", "101402");
        answers.put("100115", "101502");
        answers.put("100116", "101602");
        Map<String, Object> submitBody = new HashMap<String, Object>();
        submitBody.put("userId", "api-user");
        submitBody.put("scaleId", Long.valueOf(1001L));
        submitBody.put("answers", answers);
        ApiResult submit = plugin.doCustomService(params("/cc001/assessment/submit", submitBody));
        ApiResult records = plugin.doCustomService(params("/cc001/assessment/records", submitBody));

        assertTrue(scales.getSuccess());
        assertTrue(questions.getSuccess());
        assertTrue(submit.getSuccess());
        assertTrue(records.getSuccess());
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
    void rejectsUnsupportedPath(@TempDir Path tempDir) {
        CareerLoopCustomWebApiPlugin plugin = plugin(tempDir,
                new IdentityAwareCareerLoopWebApiBoundary(new DevelopmentCareerLoopIdentityResolver("api-user")));

        ApiResult result = plugin.doCustomService(params("/cc001/missing", new HashMap<String, Object>()));

        assertFalse(result.getSuccess());
        assertEquals("Unsupported CyanCruise custom WebAPI path: /cc001/missing", result.getMessage());
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

    private CareerLoopCustomWebApiPlugin plugin(Path tempDir, IdentityAwareCareerLoopWebApiBoundary boundary) {
        CareerProfileApplicationService profileService = profileService(tempDir);
        ResumeApplicationService resumeService = new ResumeApplicationService(
                new FileResumeStorage(tempDir.resolve("resume").toFile()), profileService);
        CareerPlanApplicationService planService = new CareerPlanApplicationService(
                new FileCareerPlanStorage(tempDir.resolve("plan-main").toFile()),
                profileService,
                new CareerPlanSummaryService());
        InterviewApplicationService interviewService = new InterviewApplicationService(
                new FileInterviewStorage(tempDir.resolve("interview").toFile()),
                profileService,
                new InterviewCoreService());
        AssistantChatApplicationService assistantService = new AssistantChatApplicationService(
                new FileAssistantChatStorage(tempDir.resolve("assistant").toFile()),
                new UnavailableAssistantChatGenerator(),
                new EmptyAssistantChatContextProvider(),
                new AssistantChatHelper());
        ResumeDiagnosisApplicationService diagnosisService = new ResumeDiagnosisApplicationService(
                resumeService,
                new FileResumeDiagnosisStorage(tempDir.resolve("diagnosis").toFile()),
                new DefaultResumeDiagnosisAnalyzer(),
                new ResumeDiagnosisService());
        AssessmentApplicationService assessmentService = new AssessmentApplicationService(
                new AssessmentScoringService(), profileService);
        return new CareerLoopCustomWebApiPlugin(
                new CareerLoopIdentityWebApi(boundary),
                new CareerProfileWebApi(boundary, profileService),
                new CareerAgentWebApi(agentService(tempDir), boundary),
                new ResumeWebApi(resumeService, boundary),
                new CareerPlanWebApi(planService),
                new InterviewWebApi(interviewService),
                new AssistantChatWebApi(assistantService),
                new EmploymentInsightsResourcesWebApi(),
                new NotificationsSubscriptionsWebApi(),
                new AdminConsoleGovernanceWebApi(),
                new FileUploadPreviewWebApi(),
                new ResumeDiagnosisWebApi(diagnosisService),
                new AssessmentWebApi(assessmentService));
    }

    private CareerProfileApplicationService profileService(Path tempDir) {
        return new CareerProfileApplicationService(
                new FileCareerProfileStorage(tempDir.toFile()),
                new FileCareerPlanStorage(tempDir.resolve("plan").toFile()),
                new CareerProfileSnapshotMergeService(),
                new CareerProfileBuildService());
    }

    private CareerAgentTodayApplicationService agentService(Path tempDir) {
        return new CareerAgentTodayApplicationService(
                new CareerAgentTodayRuleService(),
                new CareerProfileRuleInputSource(profileService(tempDir)));
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

}
