package v620.cc001.cloud01.app01.webapi;

import v620.cc001.cloud01.app01.webapi.admin.AdminConsoleGovernanceWebApi;
import v620.cc001.cloud01.app01.webapi.assessment.AssessmentWebApi;
import v620.cc001.cloud01.app01.webapi.assistant.AssistantChatWebApi;
import v620.cc001.cloud01.app01.webapi.auth.CyanCruiseIdentityWebApi;
import v620.cc001.cloud01.app01.webapi.career.CareerAgentWebApi;
import v620.cc001.cloud01.app01.webapi.career.CareerPlanWebApi;
import v620.cc001.cloud01.app01.webapi.career.CareerProfileWebApi;
import v620.cc001.cloud01.app01.webapi.employment.EmploymentInsightsResourcesWebApi;
import v620.cc001.cloud01.app01.webapi.file.FileUploadPreviewWebApi;
import v620.cc001.cloud01.app01.webapi.interview.InterviewWebApi;
import v620.cc001.cloud01.app01.webapi.notification.NotificationsSubscriptionsWebApi;
import v620.cc001.cloud01.app01.webapi.resume.ResumeDiagnosisWebApi;
import v620.cc001.cloud01.app01.webapi.resume.ResumeWebApi;
import v620.cc001.cloud01.app01.mservice.ai.impl.DefaultResumeDiagnosisAnalyzer;
import v620.cc001.cloud01.app01.mservice.ai.impl.EmptyAssistantChatContextProvider;
import v620.cc001.cloud01.app01.mservice.ai.impl.UnavailableAssistantChatGenerator;
import v620.cc001.cloud01.app01.mservice.auth.impl.DevelopmentCyanCruiseIdentityResolver;
import v620.cc001.cloud01.app01.mservice.auth.impl.IdentityAwareCyanCruiseWebApiBoundary;
import v620.cc001.cloud01.app01.mservice.storage.impl.FileAssistantChatStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.FileCareerPlanStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.FileCareerProfileStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.FileInterviewStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.FileResumeDiagnosisStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.FileResumeStorage;
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
import v620.cc001.base.common.dto.career.AdminConstants;
import v620.cc001.base.common.dto.career.AdminQuestionDto;
import v620.cc001.base.common.dto.career.AssessmentQuestionDto;
import v620.cc001.base.common.dto.career.AssessmentScaleDto;
import v620.cc001.base.common.dto.career.AdminUserDto;
import v620.cc001.base.common.dto.career.NotificationUnreadCountDto;
import v620.cc001.base.common.dto.career.InterviewSessionDto;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;
import v620.base.helper.career.CareerProfileBuildService;
import v620.base.helper.career.CareerProfileSnapshotMergeService;
import v620.base.helper.career.InterviewCoreService;
import v620.base.helper.career.ResumeDiagnosisService;
import v620.cc001.cloud01.app01.mservice.application.AssessmentApplicationService;
import v620.cc001.cloud01.app01.mservice.application.AssistantChatApplicationService;
import v620.cc001.cloud01.app01.mservice.application.AdminConsoleGovernanceApplicationService;
import v620.cc001.cloud01.app01.mservice.application.CareerAgentTodayApplicationService;
import v620.cc001.cloud01.app01.mservice.application.CareerPlanApplicationService;
import v620.cc001.cloud01.app01.mservice.application.CareerProfileApplicationService;
import v620.cc001.cloud01.app01.mservice.ai.CareerProfileRuleInputSource;
import v620.cc001.cloud01.app01.mservice.application.InterviewApplicationService;
import v620.cc001.cloud01.app01.mservice.application.ResumeApplicationService;
import v620.cc001.cloud01.app01.mservice.application.ResumeDiagnosisApplicationService;
import v620.cc001.cloud01.app01.mservice.application.NotificationsSubscriptionsApplicationService;
import v620.cc001.cloud01.app01.mservice.notification.impl.InMemoryNotificationStorage;
import v620.cc001.cloud01.app01.mservice.notification.impl.InMemorySubscriptionQuotaStorage;
import v620.cc001.cloud01.app01.mservice.notification.impl.UnavailableSubscriptionSender;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryAdminGovernanceStorage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
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

class CyanCruiseCustomWebApiPluginTest {

    @Test
    void routesIdentityCurrentThroughCustomWebApiContract(@TempDir Path tempDir) {
        IdentityAwareCyanCruiseWebApiBoundary boundary =
                new IdentityAwareCyanCruiseWebApiBoundary(new DevelopmentCyanCruiseIdentityResolver("api-user"));
        CyanCruiseCustomWebApiPlugin plugin = plugin(tempDir, boundary);

        ApiResult result = plugin.doCustomService(params("/cc001/identity/current", new HashMap<String, Object>()));

        assertTrue(result.getSuccess());
        CosmicIdentityContextDto identity = (CosmicIdentityContextDto) result.getData();
        assertEquals("api-user", identity.getUserId());
        assertEquals(CosmicIdentityConstants.STATUS_OK, identity.getStatus());
    }

    @Test
    void routeReturnsCustomApiResultForOpenApiJavaPlugin(@TempDir Path tempDir) {
        IdentityAwareCyanCruiseWebApiBoundary boundary =
                new IdentityAwareCyanCruiseWebApiBoundary(new DevelopmentCyanCruiseIdentityResolver("api-user"));
        CyanCruiseCustomWebApiPlugin plugin = plugin(tempDir, boundary);

        CustomApiResult<Object> result = plugin.route(params("/cc001/identity/current", new HashMap<String, Object>()));

        assertTrue(result.isStatus());
        Map<?, ?> identity = (Map<?, ?>) result.getData();
        assertEquals("api-user", identity.get("userId"));
    }

    @Test
    void routeConvertsAdminJavaTimeFieldsToRpcSafeStrings() {
        AdminUserDto user = new AdminUserDto();
        user.setUserId("api-user");
        user.setStatus(AdminConstants.USER_STATUS_ACTIVE);
        user.setCreatedAt(LocalDateTime.of(2026, 7, 12, 14, 4, 2));

        Map<?, ?> safe = (Map<?, ?>) CyanCruiseCustomWebApiPlugin.toRpcSafeData(user);

        assertEquals("api-user", safe.get("userId"));
        assertEquals("2026-07-12T14:04:02", safe.get("createdAt"));
    }

    @Test
    void routesOnboardingSaveThroughCustomWebApiContract(@TempDir Path tempDir) {
        IdentityAwareCyanCruiseWebApiBoundary boundary =
                new IdentityAwareCyanCruiseWebApiBoundary(new DevelopmentCyanCruiseIdentityResolver("api-user"));
        CyanCruiseCustomWebApiPlugin plugin = plugin(tempDir, boundary);
        Map<String, Object> request = new HashMap<String, Object>();
        request.put("identityType", "career_switcher");
        request.put("educationStage", "undergraduate");
        request.put("schoolMajor", "软件工程");
        request.put("experience", "自学数据结构与算法");
        request.put("targetRole", "Java Backend Engineer");
        request.put("targetSchool", "电子科技大学");
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
        assertEquals("电子科技大学", snapshot.getOnboarding().getTargetSchool());
        assertEquals("Java Backend Engineer", snapshot.getPreferences().getTargetRole());

        ApiResult reloaded = plugin.doCustomService(params("/cc001/career-profile/snapshot/get", body));
        assertTrue(reloaded.getSuccess());
        UserProfileSnapshot persisted = (UserProfileSnapshot) reloaded.getData();
        assertEquals("电子科技大学", persisted.getOnboarding().getTargetSchool());
        assertEquals("Java Backend Engineer", persisted.getPreferences().getTargetRole());
    }

    @Test
    void routesProfileDraftSaveAndGetThroughCustomWebApiContract(@TempDir Path tempDir) {
        IdentityAwareCyanCruiseWebApiBoundary boundary =
                new IdentityAwareCyanCruiseWebApiBoundary(new DevelopmentCyanCruiseIdentityResolver("api-user"));
        CyanCruiseCustomWebApiPlugin plugin = plugin(tempDir, boundary);
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
        IdentityAwareCyanCruiseWebApiBoundary boundary =
                new IdentityAwareCyanCruiseWebApiBoundary(new DevelopmentCyanCruiseIdentityResolver("api-user"));
        CyanCruiseCustomWebApiPlugin plugin = plugin(tempDir, boundary);

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
        IdentityAwareCyanCruiseWebApiBoundary boundary =
                new IdentityAwareCyanCruiseWebApiBoundary(new DevelopmentCyanCruiseIdentityResolver("api-user"));
        CyanCruiseCustomWebApiPlugin plugin = plugin(tempDir, boundary);

        ApiResult scales = plugin.doCustomService(params("/cc001/assessment/scales", new HashMap<String, Object>()));
        Map<String, Object> questionsBody = new HashMap<String, Object>();
        questionsBody.put("userId", "api-user");
        questionsBody.put("scaleId", Long.valueOf(1001L));
        ApiResult questions = plugin.doCustomService(params("/cc001/assessment/questions", questionsBody));
        AssessmentScaleDto attempt = (AssessmentScaleDto) questions.getData();
        Map<String, Object> answers = new HashMap<String, Object>();
        for (AssessmentQuestionDto question : attempt.getQuestions()) {
            answers.put(String.valueOf(question.getQuestionId()),
                    String.valueOf(question.getOptions().get(0).getOptionId()));
        }
        Map<String, Object> submitBody = new HashMap<String, Object>();
        submitBody.put("userId", "api-user");
        submitBody.put("scaleId", Long.valueOf(1001L));
        submitBody.put("attemptId", attempt.getAttemptId());
        submitBody.put("answers", answers);
        ApiResult submit = plugin.doCustomService(params("/cc001/assessment/submit", submitBody));
        ApiResult records = plugin.doCustomService(params("/cc001/assessment/records", submitBody));

        assertTrue(scales.getSuccess());
        assertTrue(questions.getSuccess());
        assertTrue(submit.getSuccess(), submit.getMessage());
        assertTrue(records.getSuccess());
    }

    @Test
    void routeMapWebApisAreDeclaredInCustomRouter() throws Exception {
        String routeMap = new String(Files.readAllBytes(workspaceFile(
                "webapp/isv/v620/cyancruise/cyancruise-routes.json")), "UTF-8");
        String router = new String(Files.readAllBytes(workspaceFile(
                "code/cloud01/v620-cc001-cloud01-app01/src/main/java/v620/cc001/cloud01/app01/webapi/CyanCruiseCustomWebApiPlugin.java")), "UTF-8");

        Set<String> declared = extractPaths(routeMap);
        Set<String> routed = extractPaths(router);
        declared.removeAll(routed);

        assertTrue(declared.isEmpty(), "custom router missing route map paths: " + declared);
    }

    @Test
    void routesInterviewDeleteThroughCustomWebApiContract(@TempDir Path tempDir) {
        CyanCruiseCustomWebApiPlugin plugin = plugin(tempDir,
                new IdentityAwareCyanCruiseWebApiBoundary(new DevelopmentCyanCruiseIdentityResolver("api-user")));
        Map<String, Object> request = new HashMap<String, Object>();
        request.put("positionName", "前端开发");
        request.put("difficulty", "Normal");
        Map<String, Object> startBody = new HashMap<String, Object>();
        startBody.put("userId", "api-user");
        startBody.put("request", request);
        ApiResult started = plugin.doCustomService(params("/cc001/interview/start", startBody));
        InterviewSessionDto session = (InterviewSessionDto) started.getData();

        Map<String, Object> deleteBody = new HashMap<String, Object>();
        deleteBody.put("userId", "api-user");
        deleteBody.put("interviewId", session.getInterviewId());
        ApiResult deleted = plugin.doCustomService(params("/cc001/interview/delete", deleteBody));
        ApiResult listed = plugin.doCustomService(params("/cc001/interview/list", "api-user"));

        assertTrue(deleted.getSuccess());
        assertEquals("OK", deleted.getData());
        assertTrue(((java.util.List<?>) listed.getData()).isEmpty());
    }

    @Test
    void rejectsUnsupportedPath(@TempDir Path tempDir) {
        CyanCruiseCustomWebApiPlugin plugin = plugin(tempDir,
                new IdentityAwareCyanCruiseWebApiBoundary(new DevelopmentCyanCruiseIdentityResolver("api-user")));

        ApiResult result = plugin.doCustomService(params("/cc001/missing", new HashMap<String, Object>()));

        assertFalse(result.getSuccess());
        assertEquals("Unsupported CyanCruise custom WebAPI path: /cc001/missing", result.getMessage());
    }

    @Test
    void blocksBannedUserBeforeUserRouteDispatch(@TempDir Path tempDir) {
        IdentityAwareCyanCruiseWebApiBoundary boundary =
                new IdentityAwareCyanCruiseWebApiBoundary(new DevelopmentCyanCruiseIdentityResolver("api-user"));
        InMemoryAdminGovernanceStorage storage = new InMemoryAdminGovernanceStorage();
        AdminUserDto user = new AdminUserDto();
        user.setUserId("api-user");
        user.setStatus(AdminConstants.USER_STATUS_BANNED);
        storage.saveUser(user);
        AdminConsoleGovernanceApplicationService adminService = new AdminConsoleGovernanceApplicationService(
                storage,
                new NotificationsSubscriptionsApplicationService(
                        new InMemoryNotificationStorage(),
                        new InMemorySubscriptionQuotaStorage(),
                        new UnavailableSubscriptionSender(),
                        new v620.base.helper.career.NotificationsSubscriptionsService()),
                new v620.base.helper.career.AdminConsoleGovernanceService());
        CyanCruiseCustomWebApiPlugin plugin = plugin(tempDir, boundary,
                new AdminConsoleGovernanceWebApi(adminService, boundary));
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("userId", "api-user");

        ApiResult result = plugin.doCustomService(params("/cc001/career-profile/snapshot/get", body));

        assertFalse(result.getSuccess());
        assertEquals("USER_BANNED", result.getErrorCode());
    }

    @Test
    void adminRouteStillWorksWhenAdminUserFacingAccessIsBanned(@TempDir Path tempDir) {
        IdentityAwareCyanCruiseWebApiBoundary boundary =
                new IdentityAwareCyanCruiseWebApiBoundary(new DevelopmentCyanCruiseIdentityResolver(
                        "api-admin", "api-admin", CosmicIdentityConstants.ROLE_ADMIN));
        InMemoryAdminGovernanceStorage storage = new InMemoryAdminGovernanceStorage();
        storage.addAdmin("api-admin");
        AdminUserDto user = new AdminUserDto();
        user.setUserId("api-admin");
        user.setStatus(AdminConstants.USER_STATUS_BANNED);
        storage.saveUser(user);
        AdminConsoleGovernanceApplicationService adminService = new AdminConsoleGovernanceApplicationService(
                storage,
                new NotificationsSubscriptionsApplicationService(
                        new InMemoryNotificationStorage(),
                        new InMemorySubscriptionQuotaStorage(),
                        new UnavailableSubscriptionSender(),
                        new v620.base.helper.career.NotificationsSubscriptionsService()),
                new v620.base.helper.career.AdminConsoleGovernanceService());
        CyanCruiseCustomWebApiPlugin plugin = plugin(tempDir, boundary,
                new AdminConsoleGovernanceWebApi(adminService, boundary));

        ApiResult result = plugin.doCustomService(params("/cc001/admin/whoami", "api-admin"));

        assertTrue(result.getSuccess());
    }

    @Test
    void routesAdminQuestionSaveAndDelete(@TempDir Path tempDir) {
        IdentityAwareCyanCruiseWebApiBoundary boundary =
                new IdentityAwareCyanCruiseWebApiBoundary(new DevelopmentCyanCruiseIdentityResolver(
                        "api-admin", "api-admin", CosmicIdentityConstants.ROLE_ADMIN));
        InMemoryAdminGovernanceStorage storage = new InMemoryAdminGovernanceStorage();
        storage.addAdmin("api-admin");
        AdminConsoleGovernanceApplicationService adminService = new AdminConsoleGovernanceApplicationService(
                storage,
                new NotificationsSubscriptionsApplicationService(
                        new InMemoryNotificationStorage(),
                        new InMemorySubscriptionQuotaStorage(),
                        new UnavailableSubscriptionSender(),
                        new v620.base.helper.career.NotificationsSubscriptionsService()),
                new v620.base.helper.career.AdminConsoleGovernanceService());
        CyanCruiseCustomWebApiPlugin plugin = plugin(tempDir, boundary,
                new AdminConsoleGovernanceWebApi(adminService, boundary));
        Map<String, Object> question = new HashMap<String, Object>();
        question.put("content", "请描述一次你定位性能问题的过程。");
        question.put("position", "后端开发");
        Map<String, Object> saveBody = new HashMap<String, Object>();
        saveBody.put("adminId", "api-admin");
        saveBody.put("question", question);

        ApiResult saved = plugin.doCustomService(params("/cc001/admin/questions/save", saveBody));
        AdminQuestionDto savedQuestion = (AdminQuestionDto) saved.getData();
        Map<String, Object> deleteBody = new HashMap<String, Object>();
        deleteBody.put("adminId", "api-admin");
        deleteBody.put("questionId", savedQuestion.getQuestionId());
        ApiResult deleted = plugin.doCustomService(params("/cc001/admin/questions/delete", deleteBody));

        assertTrue(saved.getSuccess());
        assertEquals("ADMIN", savedQuestion.getSource());
        assertTrue(deleted.getSuccess());
        assertEquals(Boolean.TRUE, deleted.getData());
    }

    @Test
    void routesAdminAssessmentQuestionSaveAndDelete(@TempDir Path tempDir) {
        IdentityAwareCyanCruiseWebApiBoundary boundary =
                new IdentityAwareCyanCruiseWebApiBoundary(new DevelopmentCyanCruiseIdentityResolver(
                        "api-admin", "api-admin", CosmicIdentityConstants.ROLE_ADMIN));
        InMemoryAdminGovernanceStorage storage = new InMemoryAdminGovernanceStorage();
        storage.addAdmin("api-admin");
        AdminConsoleGovernanceApplicationService adminService = new AdminConsoleGovernanceApplicationService(
                storage,
                new NotificationsSubscriptionsApplicationService(
                        new InMemoryNotificationStorage(),
                        new InMemorySubscriptionQuotaStorage(),
                        new UnavailableSubscriptionSender(),
                        new v620.base.helper.career.NotificationsSubscriptionsService()),
                new v620.base.helper.career.AdminConsoleGovernanceService());
        CyanCruiseCustomWebApiPlugin plugin = plugin(tempDir, boundary,
                new AdminConsoleGovernanceWebApi(adminService, boundary));
        Map<String, Object> optionA = new HashMap<String, Object>();
        optionA.put("optionLabel", "A");
        optionA.put("optionText", "分析复杂问题");
        optionA.put("dimensionCode", "I");
        Map<String, Object> optionB = new HashMap<String, Object>();
        optionB.put("optionLabel", "B");
        optionB.put("optionText", "帮助他人成长");
        optionB.put("dimensionCode", "S");
        java.util.List<Map<String, Object>> options = new ArrayList<Map<String, Object>>();
        options.add(optionA);
        options.add(optionB);
        Map<String, Object> question = new HashMap<String, Object>();
        question.put("questionText", "你更有成就感的是？");
        question.put("dimensionCode", "I/S");
        question.put("options", options);
        Map<String, Object> saveBody = new HashMap<String, Object>();
        saveBody.put("adminId", "api-admin");
        saveBody.put("scaleId", Long.valueOf(1002L));
        saveBody.put("question", question);

        ApiResult saved = plugin.doCustomService(params("/cc001/admin/assessment/questions/save", saveBody));
        AssessmentQuestionDto savedQuestion = (AssessmentQuestionDto) saved.getData();
        Map<String, Object> deleteBody = new HashMap<String, Object>();
        deleteBody.put("adminId", "api-admin");
        deleteBody.put("scaleId", Long.valueOf(1002L));
        deleteBody.put("questionId", savedQuestion.getQuestionId());
        ApiResult deleted = plugin.doCustomService(params("/cc001/admin/assessment/questions/delete", deleteBody));

        assertTrue(saved.getSuccess());
        assertEquals("I/S", savedQuestion.getDimensionCode());
        assertTrue(deleted.getSuccess());
        assertEquals(Boolean.TRUE, deleted.getData());
    }

    @Test
    void registersActiveUserWhenUserRouteIsCalled(@TempDir Path tempDir) {
        IdentityAwareCyanCruiseWebApiBoundary boundary =
                new IdentityAwareCyanCruiseWebApiBoundary(new DevelopmentCyanCruiseIdentityResolver("real-user"));
        InMemoryAdminGovernanceStorage storage = new InMemoryAdminGovernanceStorage();
        AdminConsoleGovernanceApplicationService adminService = new AdminConsoleGovernanceApplicationService(
                storage,
                new NotificationsSubscriptionsApplicationService(
                        new InMemoryNotificationStorage(),
                        new InMemorySubscriptionQuotaStorage(),
                        new UnavailableSubscriptionSender(),
                        new v620.base.helper.career.NotificationsSubscriptionsService()),
                new v620.base.helper.career.AdminConsoleGovernanceService());
        CyanCruiseCustomWebApiPlugin plugin = plugin(tempDir, boundary,
                new AdminConsoleGovernanceWebApi(adminService, boundary));
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("userId", "real-user");

        ApiResult result = plugin.doCustomService(params("/cc001/career-profile/snapshot/get", body));

        assertTrue(result.getSuccess());
        assertEquals(AdminConstants.USER_STATUS_ACTIVE, storage.findUser("real-user").getStatus());
    }

    private Map<String, Object> params(String path, Object body) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(CyanCruiseCustomWebApiPlugin.PARAM_PATH, path);
        params.put(CyanCruiseCustomWebApiPlugin.PARAM_BODY, body);
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

    private CyanCruiseCustomWebApiPlugin plugin(Path tempDir, IdentityAwareCyanCruiseWebApiBoundary boundary) {
        return plugin(tempDir, boundary, new AdminConsoleGovernanceWebApi());
    }

    private CyanCruiseCustomWebApiPlugin plugin(Path tempDir, IdentityAwareCyanCruiseWebApiBoundary boundary,
                                               AdminConsoleGovernanceWebApi adminWebApi) {
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
        return new CyanCruiseCustomWebApiPlugin(
                new CyanCruiseIdentityWebApi(boundary),
                new CareerProfileWebApi(boundary, profileService),
                new CareerAgentWebApi(agentService(tempDir), boundary),
                new ResumeWebApi(resumeService, boundary),
                new CareerPlanWebApi(planService, boundary),
                new InterviewWebApi(interviewService),
                new AssistantChatWebApi(assistantService),
                new EmploymentInsightsResourcesWebApi(),
                new NotificationsSubscriptionsWebApi(new NotificationsSubscriptionsApplicationService(
                        new InMemoryNotificationStorage(),
                        new InMemorySubscriptionQuotaStorage(),
                        new UnavailableSubscriptionSender(),
                        new v620.base.helper.career.NotificationsSubscriptionsService())),
                adminWebApi,
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
