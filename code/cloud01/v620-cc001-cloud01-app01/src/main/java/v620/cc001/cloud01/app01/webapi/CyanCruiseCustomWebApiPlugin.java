package v620.cc001.cloud01.app01.webapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import v620.cc001.cloud01.app01.mservice.auth.impl.DevelopmentCyanCruiseIdentityResolver;
import v620.cc001.cloud01.app01.mservice.auth.impl.IdentityAwareCyanCruiseWebApiBoundary;
import v620.cc001.cloud01.app01.mservice.auth.impl.RequestContextCosmicLoginContextBridge;
import v620.cc001.cloud01.app01.mservice.furtherstudy.impl.InMemoryFurtherStudyCompanionStorage;
import kd.bos.bill.IBillWebApiPlugin;
import kd.bos.entity.api.ApiResult;
import kd.bos.openapi.common.custom.annotation.ApiController;
import kd.bos.openapi.common.custom.annotation.ApiMapping;
import kd.bos.openapi.common.custom.annotation.ApiPostMapping;
import kd.bos.openapi.common.custom.annotation.ApiRequestBody;
import kd.bos.openapi.common.custom.annotation.ApiResponseBody;
import kd.bos.openapi.common.result.CustomApiResult;
import v620.base.helper.furtherstudy.PostgraduateCompanionService;
import v620.base.helper.furtherstudy.RecommendationCompanionService;
import v620.base.helper.furtherstudy.StudyAbroadCompanionService;
import v620.cc001.base.common.dto.career.AdminBroadcastRequest;
import v620.cc001.base.common.dto.career.AdminConstants;
import v620.cc001.base.common.dto.career.AdminContentItemDto;
import v620.cc001.base.common.dto.career.AdminIdentityDto;
import v620.cc001.base.common.dto.career.AdminQuestionDto;
import v620.cc001.base.common.dto.career.AssessmentQuestionDto;
import v620.cc001.base.common.dto.career.AssessmentScaleDto;
import v620.cc001.base.common.dto.career.AssessmentSubmitRequest;
import v620.cc001.base.common.dto.career.AssistantChatRequest;
import v620.cc001.base.common.dto.career.CareerProfileDraftDto;
import v620.cc001.base.common.dto.career.CareerProfileOnboardingRequest;
import v620.cc001.base.common.dto.career.CareerDailyTaskUpdateRequest;
import v620.cc001.base.common.dto.career.CosmicIdentityContextDto;
import v620.cc001.base.common.dto.career.FileUploadRequest;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyMaterialSaveRequest;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyRecordQueryRequest;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyRecordStatusUpdateRequest;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyTargetSaveRequest;
import v620.cc001.base.common.dto.career.InterviewStartRequest;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateMistakeAnalyzeRequest;
import v620.cc001.base.common.dto.furtherstudy.PostgraduatePlanRequest;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateReexamPrepareRequest;
import v620.cc001.base.common.dto.furtherstudy.PostgraduateSchoolRecommendRequest;
import v620.cc001.base.common.dto.furtherstudy.RecommendationDocumentPolishRequest;
import v620.cc001.base.common.dto.furtherstudy.RecommendationProfileRequest;
import v620.cc001.base.common.dto.furtherstudy.RecommendationTutorLetterRequest;
import v620.cc001.base.common.dto.career.ResumeCreateRequest;
import v620.cc001.base.common.dto.career.ResumeDiagnosisRequest;
import v620.cc001.base.common.dto.furtherstudy.StudyAbroadLanguagePlanRequest;
import v620.cc001.base.common.dto.furtherstudy.StudyAbroadProfileRequest;
import v620.cc001.base.common.dto.furtherstudy.StudyAbroadSchoolPositionRequest;
import v620.cc001.base.common.dto.furtherstudy.StudyAbroadStatementRequest;
import v620.cc001.base.common.dto.furtherstudy.StudyAbroadVisaChecklistRequest;
import v620.cc001.base.common.dto.furtherstudy.StudyPlanningMaterialUploadRequest;
import v620.cc001.base.common.dto.career.SubscriptionGrantRequest;
import v620.cc001.base.common.dto.career.SubscriptionSendRequest;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;
import v620.cc001.cloud01.app01.mservice.auth.impl.DevelopmentCyanCruiseIdentityResolver;
import v620.cc001.cloud01.app01.mservice.furtherstudy.FurtherStudyApplicationService;
import v620.cc001.cloud01.app01.mservice.auth.IdentityBoundaryException;
import v620.cc001.cloud01.app01.mservice.auth.impl.IdentityAwareCyanCruiseWebApiBoundary;
import v620.cc001.cloud01.app01.mservice.auth.impl.RequestContextCosmicLoginContextBridge;
import v620.cc001.cloud01.app01.mservice.furtherstudy.impl.InMemoryFurtherStudyCompanionStorage;
import v620.cc001.cloud01.app01.mservice.furtherstudy.PostgraduateApplicationService;
import v620.cc001.cloud01.app01.mservice.furtherstudy.RecommendationApplicationService;
import v620.cc001.cloud01.app01.mservice.furtherstudy.StudyAbroadApplicationService;
import v620.cc001.cloud01.app01.webapi.furtherstudy.FurtherStudyWebApi;
import v620.cc001.cloud01.app01.webapi.furtherstudy.StudyCenterWebApi;
import v620.cc001.cloud01.app01.webapi.furtherstudy.PostgraduateWebApi;
import v620.cc001.cloud01.app01.webapi.furtherstudy.RecommendationWebApi;
import v620.cc001.cloud01.app01.webapi.furtherstudy.StudyAbroadWebApi;

import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Cosmic Custom Web API entry that routes platform kapi calls to CyanCruise WebAPI contracts.
 */
@ApiController(value = "cyanCruiseCustomWebApiPlugin", desc = "CyanCruise custom WebAPI router")
@ApiMapping("/cc001/cyancruise")
public class CyanCruiseCustomWebApiPlugin implements IBillWebApiPlugin {

    public static final String PARAM_PATH = "path";
    public static final String PARAM_BODY = "body";
    public static final String PARAM_PLATFORM_IDENTITY = "platformIdentity";
    private static final ObjectMapper RPC_RESPONSE_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private final CyanCruiseIdentityWebApi identityWebApi;
    private final CareerProfileWebApi profileWebApi;
    private final CareerAgentWebApi agentWebApi;
    private final ResumeWebApi resumeWebApi;
    private final CareerPlanWebApi planWebApi;
    private final InterviewWebApi interviewWebApi;
    private final AssistantChatWebApi assistantWebApi;
    private final EmploymentInsightsResourcesWebApi employmentWebApi;
    private final NotificationsSubscriptionsWebApi notificationsWebApi;
    private final AdminConsoleGovernanceWebApi adminWebApi;
    private final FileUploadPreviewWebApi fileWebApi;
    private final ResumeDiagnosisWebApi resumeDiagnosisWebApi;
    private final AssessmentWebApi assessmentWebApi;
    private final PostgraduateWebApi postgraduateWebApi;
    private final RecommendationWebApi recommendationWebApi;
    private final StudyAbroadWebApi studyAbroadWebApi;
    private final FurtherStudyWebApi furtherStudyWebApi;
    private final StudyCenterWebApi studyCenterWebApi;

    public CyanCruiseCustomWebApiPlugin() {
        this(new CyanCruiseIdentityWebApi(), new CareerProfileWebApi(), new CareerAgentWebApi(),
                new ResumeWebApi(), new CareerPlanWebApi(), new InterviewWebApi(),
                new AssistantChatWebApi(), new EmploymentInsightsResourcesWebApi(),
                new NotificationsSubscriptionsWebApi(), new AdminConsoleGovernanceWebApi(),
                new FileUploadPreviewWebApi(), new ResumeDiagnosisWebApi(), new AssessmentWebApi(),
                new PostgraduateWebApi(), new RecommendationWebApi(), new StudyAbroadWebApi(),
                new FurtherStudyWebApi(), new StudyCenterWebApi());
    }

    CyanCruiseCustomWebApiPlugin(CyanCruiseIdentityWebApi identityWebApi,
                                 CareerProfileWebApi profileWebApi,
                                 CareerAgentWebApi agentWebApi) {
        this(identityWebApi, profileWebApi, agentWebApi,
                new ResumeWebApi(), new CareerPlanWebApi(), new InterviewWebApi(),
                new AssistantChatWebApi(), new EmploymentInsightsResourcesWebApi(),
                new NotificationsSubscriptionsWebApi(), new AdminConsoleGovernanceWebApi(),
                new FileUploadPreviewWebApi(), new ResumeDiagnosisWebApi(), new AssessmentWebApi(),
                new PostgraduateWebApi(), new RecommendationWebApi(), new StudyAbroadWebApi(),
                new FurtherStudyWebApi(), testStudyCenterWebApi());
    }

    CyanCruiseCustomWebApiPlugin(CyanCruiseIdentityWebApi identityWebApi,
                                 CareerProfileWebApi profileWebApi,
                                 CareerAgentWebApi agentWebApi,
                                 ResumeWebApi resumeWebApi,
                                 CareerPlanWebApi planWebApi,
                                 InterviewWebApi interviewWebApi,
                                 AssistantChatWebApi assistantWebApi,
                                 EmploymentInsightsResourcesWebApi employmentWebApi,
                                 NotificationsSubscriptionsWebApi notificationsWebApi,
                                 AdminConsoleGovernanceWebApi adminWebApi,
                                 FileUploadPreviewWebApi fileWebApi,
                                 ResumeDiagnosisWebApi resumeDiagnosisWebApi,
                                 AssessmentWebApi assessmentWebApi) {
        this(identityWebApi, profileWebApi, agentWebApi, resumeWebApi, planWebApi, interviewWebApi,
                assistantWebApi, employmentWebApi, notificationsWebApi, adminWebApi, fileWebApi,
                resumeDiagnosisWebApi, assessmentWebApi, testPostgraduateWebApi(), testRecommendationWebApi(),
                testStudyAbroadWebApi(), testFurtherStudyWebApi(), testStudyCenterWebApi());
    }

    CyanCruiseCustomWebApiPlugin(CyanCruiseIdentityWebApi identityWebApi,
                                 CareerProfileWebApi profileWebApi,
                                 CareerAgentWebApi agentWebApi,
                                 ResumeWebApi resumeWebApi,
                                 CareerPlanWebApi planWebApi,
                                 InterviewWebApi interviewWebApi,
                                 AssistantChatWebApi assistantWebApi,
                                 EmploymentInsightsResourcesWebApi employmentWebApi,
                                 NotificationsSubscriptionsWebApi notificationsWebApi,
                                 AdminConsoleGovernanceWebApi adminWebApi,
                                 FileUploadPreviewWebApi fileWebApi,
                                 ResumeDiagnosisWebApi resumeDiagnosisWebApi,
                                 AssessmentWebApi assessmentWebApi,
                                 PostgraduateWebApi postgraduateWebApi,
                                 RecommendationWebApi recommendationWebApi,
                                 StudyAbroadWebApi studyAbroadWebApi,
                                 FurtherStudyWebApi furtherStudyWebApi) {
        this(identityWebApi, profileWebApi, agentWebApi, resumeWebApi, planWebApi, interviewWebApi,
                assistantWebApi, employmentWebApi, notificationsWebApi, adminWebApi, fileWebApi,
                resumeDiagnosisWebApi, assessmentWebApi, postgraduateWebApi, recommendationWebApi,
                studyAbroadWebApi, furtherStudyWebApi, testStudyCenterWebApi());
    }

    CyanCruiseCustomWebApiPlugin(CyanCruiseIdentityWebApi identityWebApi,
                                 CareerProfileWebApi profileWebApi,
                                 CareerAgentWebApi agentWebApi,
                                 ResumeWebApi resumeWebApi,
                                 CareerPlanWebApi planWebApi,
                                 InterviewWebApi interviewWebApi,
                                 AssistantChatWebApi assistantWebApi,
                                 EmploymentInsightsResourcesWebApi employmentWebApi,
                                 NotificationsSubscriptionsWebApi notificationsWebApi,
                                 AdminConsoleGovernanceWebApi adminWebApi,
                                 FileUploadPreviewWebApi fileWebApi,
                                 ResumeDiagnosisWebApi resumeDiagnosisWebApi,
                                 AssessmentWebApi assessmentWebApi,
                                 PostgraduateWebApi postgraduateWebApi,
                                 RecommendationWebApi recommendationWebApi,
                                 StudyAbroadWebApi studyAbroadWebApi,
                                 FurtherStudyWebApi furtherStudyWebApi,
                                 StudyCenterWebApi studyCenterWebApi) {
        this.identityWebApi = identityWebApi;
        this.profileWebApi = profileWebApi;
        this.agentWebApi = agentWebApi;
        this.resumeWebApi = resumeWebApi;
        this.planWebApi = planWebApi;
        this.interviewWebApi = interviewWebApi;
        this.assistantWebApi = assistantWebApi;
        this.employmentWebApi = employmentWebApi;
        this.notificationsWebApi = notificationsWebApi;
        this.adminWebApi = adminWebApi;
        this.fileWebApi = fileWebApi;
        this.resumeDiagnosisWebApi = resumeDiagnosisWebApi;
        this.assessmentWebApi = assessmentWebApi;
        this.postgraduateWebApi = postgraduateWebApi;
        this.recommendationWebApi = recommendationWebApi;
        this.studyAbroadWebApi = studyAbroadWebApi;
        this.furtherStudyWebApi = furtherStudyWebApi;
        this.studyCenterWebApi = studyCenterWebApi;
    }

    private static PostgraduateWebApi testPostgraduateWebApi() {
        return new PostgraduateWebApi(new PostgraduateApplicationService(new PostgraduateCompanionService()),
                testIdentityBoundary());
    }

    private static RecommendationWebApi testRecommendationWebApi() {
        return new RecommendationWebApi(new RecommendationApplicationService(new RecommendationCompanionService()),
                testIdentityBoundary());
    }

    private static StudyAbroadWebApi testStudyAbroadWebApi() {
        return new StudyAbroadWebApi(new StudyAbroadApplicationService(new StudyAbroadCompanionService()),
                testIdentityBoundary());
    }

    private static FurtherStudyWebApi testFurtherStudyWebApi() {
        return new FurtherStudyWebApi(new FurtherStudyApplicationService(new InMemoryFurtherStudyCompanionStorage()),
                testIdentityBoundary());
    }

    private static StudyCenterWebApi testStudyCenterWebApi() {
        return new StudyCenterWebApi(null, null, null, testIdentityBoundary());
    }

    private static IdentityAwareCyanCruiseWebApiBoundary testIdentityBoundary() {
        return new IdentityAwareCyanCruiseWebApiBoundary(new DevelopmentCyanCruiseIdentityResolver("api-user"));
    }

    @ApiPostMapping(value = "/route", desc = "Route CyanCruise custom WebAPI call", methodParamNames = {"params"})
    public @ApiResponseBody(value = "CyanCruise custom WebAPI result") CustomApiResult<Object> route(
            @ApiRequestBody(value = "CyanCruise custom WebAPI params", required = true) Map<String, Object> params) {
        ApiResult result = doCustomService(params);
        if (result.getSuccess()) {
            try {
                return CustomApiResult.success(toRpcSafeData(result.getData()));
            } catch (RuntimeException ex) {
                return CustomApiResult.fail("RPC_RESPONSE_CONVERSION_FAILED", "响应数据暂时无法处理，请稍后重试。");
            }
        }
        return CustomApiResult.fail(result.getErrorCode(), result.getMessage());
    }

    static Object toRpcSafeData(Object data) {
        if (data == null || data instanceof String || data instanceof Number || data instanceof Boolean) {
            return data;
        }
        try {
            byte[] json = RPC_RESPONSE_MAPPER.writeValueAsBytes(data);
            return RPC_RESPONSE_MAPPER.readValue(json, Object.class);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to convert Custom WebAPI response to RPC-safe data", ex);
        }
    }

    @Override
    public ApiResult doCustomService(Map<String, Object> params) {
        if (params == null) {
            return ApiResult.fail("CyanCruise custom WebAPI params are required");
        }
        String path = normalizePath(text(params.get(PARAM_PATH)));
        Object body = params.get(PARAM_BODY);
        RequestContextCosmicLoginContextBridge.setPlatformContext(mapValue(params.get(PARAM_PLATFORM_IDENTITY)));
        try {
            if ("/cc001/identity/current".equals(path)) {
                return ApiResult.success(identityWebApi.current());
            }
            if (requiresActiveUser(path) && !isActiveUserAllowed(body)) {
                return ApiResult.fail("当前账号已被管理员禁用，请联系管理员。", "USER_BANNED");
            }
            if ("/cc001/career-profile/snapshot/get".equals(path)) {
                return ApiResult.success(profileWebApi.snapshot(extractUserId(body)));
            }
            if ("/cc001/career-profile/deep-profile/generate".equals(path)) {
                return ApiResult.success(profileWebApi.generateDeepProfile(extractUserId(body)));
            }
            if ("/cc001/career-profile/deep-profile/latest".equals(path)) {
                return ApiResult.success(profileWebApi.latestDeepProfile(extractUserId(body)));
            }
            if ("/cc001/career-profile/deep-profile/history".equals(path)) {
                return ApiResult.success(profileWebApi.deepProfileHistory(extractUserId(body)));
            }
            if ("/cc001/career-profile/deep-profile/detail".equals(path)) {
                return ApiResult.success(profileWebApi.deepProfileDetail(
                        extractUserId(body), text(value(body, "recordId"))));
            }
            if ("/cc001/career-profile/draft/get".equals(path)) {
                return ApiResult.success(profileWebApi.draft(extractUserId(body)));
            }
            if ("/cc001/career-profile/draft/save".equals(path)) {
                return ApiResult.success(profileWebApi.saveDraft(
                        extractUserId(body), extractProfileDraft(body)));
            }
            if ("/cc001/career-profile/draft/clear".equals(path)) {
                return ApiResult.success(profileWebApi.clearDraft(extractUserId(body)));
            }
            if ("/cc001/career-profile/onboarding/save".equals(path)) {
                return ApiResult.success(profileWebApi.saveOnboarding(
                        extractUserId(body), extractOnboardingRequest(body)));
            }
            if ("/cc001/career-agent/today/get".equals(path)) {
                return ApiResult.success(agentWebApi.todayByUserId(extractUserId(body)));
            }
            if ("/cc001/assessment/scales".equals(path)) {
                return ApiResult.success(assessmentWebApi.scales());
            }
            if ("/cc001/assessment/questions".equals(path)) {
                return ApiResult.success(assessmentWebApi.start(
                        extractUserId(body), longObject(value(body, "scaleId"))));
            }
            if ("/cc001/assessment/submit".equals(path)) {
                return ApiResult.success(assessmentWebApi.submit(
                        extractUserId(body), null, extractAssessmentSubmitRequest(body)));
            }
            if ("/cc001/assessment/records".equals(path)) {
                return ApiResult.success(assessmentWebApi.records(extractUserId(body)));
            }
            if ("/cc001/assessment/record/get".equals(path)) {
                return ApiResult.success(assessmentWebApi.record(
                        extractUserId(body), longObject(value(body, "recordId"))));
            }
            if ("/cc001/assessment/ai-interpretation/generate".equals(path)) {
                return ApiResult.success(assessmentWebApi.generateAiInterpretation(
                        extractUserId(body), longObject(value(body, "recordId"))));
            }
            if ("/cc001/resume/list".equals(path)) {
                return ApiResult.success(resumeWebApi.list(extractUserId(body)));
            }
            if ("/cc001/resume/create".equals(path)) {
                return ApiResult.success(resumeWebApi.create(extractUserId(body), extractResumeCreateRequest(body)));
            }
            if ("/cc001/resume/delete".equals(path)) {
                return ApiResult.success(resumeWebApi.delete(extractUserId(body), longObject(value(body, "resumeId"))));
            }
            if ("/cc001/career-plan/summary".equals(path)) {
                return ApiResult.success(planWebApi.summary(extractUserId(body)));
            }
            if ("/cc001/career-plan/ensure".equals(path)) {
                return ApiResult.success(planWebApi.ensure(extractUserId(body)));
            }
            if ("/cc001/career-plan/generate".equals(path)) {
                return ApiResult.success(planWebApi.generate(extractUserId(body)));
            }
            if ("/cc001/career-plan/daily/get".equals(path)) {
                return ApiResult.success(planWebApi.daily(extractUserId(body)));
            }
            if ("/cc001/career-plan/daily/task/update".equals(path)) {
                CareerDailyTaskUpdateRequest request = new CareerDailyTaskUpdateRequest();
                Map<String, Object> values = mapValue(value(body, "request"));
                request.setTaskId(text(values.get("taskId")));
                request.setCompleted(booleanObject(values.get("completed")));
                return ApiResult.success(planWebApi.updateDailyTask(extractUserId(body), request));
            }
            if ("/cc001/interview/list".equals(path)) {
                return ApiResult.success(interviewWebApi.list(extractUserId(body)));
            }
            if ("/cc001/interview/page".equals(path)) {
                return ApiResult.success(interviewWebApi.page(extractUserId(body),
                        Integer.valueOf(intValue(value(body, "page"), 1)), textOrNull(value(body, "mode"))));
            }
            if ("/cc001/interview/start".equals(path)) {
                return ApiResult.success(interviewWebApi.start(extractUserId(body), extractInterviewStartRequest(body)));
            }
            if ("/cc001/interview/guided/start".equals(path)) {
                return ApiResult.success(interviewWebApi.guidedStart(extractUserId(body), extractInterviewStartRequest(body)));
            }
            if ("/cc001/interview/guided/answer".equals(path)) {
                return ApiResult.success(interviewWebApi.guidedAnswer(extractUserId(body),
                        longObject(value(body, "interviewId")), textOrNull(value(body, "answer"))));
            }
            if ("/cc001/interview/guided/finish".equals(path)) {
                return ApiResult.success(interviewWebApi.guidedFinish(extractUserId(body),
                        longObject(value(body, "interviewId"))));
            }
            if ("/cc001/interview/messages".equals(path)) {
                return ApiResult.success(interviewWebApi.messages(extractUserId(body),
                        longObject(value(body, "interviewId"))));
            }
            if ("/cc001/interview/delete".equals(path)) {
                return ApiResult.success(interviewWebApi.delete(extractUserId(body),
                        longObject(value(body, "interviewId"))));
            }
            if ("/cc001/assistant-chat/send".equals(path)) {
                return ApiResult.success(assistantWebApi.send(extractUserId(body), extractAssistantChatRequest(body)));
            }
            if ("/cc001/assistant-chat/session/list".equals(path)) {
                return ApiResult.success(assistantWebApi.listSessions(extractUserId(body)));
            }
            if ("/cc001/career-employment/insight/get".equals(path)) {
                return ApiResult.success(employmentWebApi.insight(extractUserId(body)));
            }
            if ("/cc001/career-employment/resources/list".equals(path)) {
                return ApiResult.success(employmentWebApi.resources(extractOptionalUserId(body)));
            }
            if ("/cc001/study-center/selection/get".equals(path)) {
                return ApiResult.success(studyCenterWebApi.selection(extractUserId(body)));
            }
            if ("/cc001/study-center/selection/save".equals(path)) {
                return ApiResult.success(studyCenterWebApi.saveSelection(extractUserId(body),
                        textOrNull(value(body, "direction")), textOrNull(value(body, "targetSchool"))));
            }
            if ("/cc001/study-center/insight/get".equals(path)) {
                return ApiResult.success(studyCenterWebApi.insight(extractUserId(body)));
            }
            if ("/cc001/study-center/resources/list".equals(path)) {
                return ApiResult.success(studyCenterWebApi.resources());
            }
            if ("/cc001/study-center/plan/summary".equals(path)) {
                return ApiResult.success(studyCenterWebApi.planSummary(extractUserId(body)));
            }
            if ("/cc001/study-center/plan/ensure".equals(path)) {
                return ApiResult.success(studyCenterWebApi.ensurePlan(extractUserId(body)));
            }
            if ("/cc001/study-center/plan/generate".equals(path)) {
                return ApiResult.success(studyCenterWebApi.generatePlan(extractUserId(body)));
            }
            if ("/cc001/study-center/daily/get".equals(path)) {
                return ApiResult.success(studyCenterWebApi.daily(extractUserId(body)));
            }
            if ("/cc001/study-center/daily/task/update".equals(path)) {
                CareerDailyTaskUpdateRequest request = new CareerDailyTaskUpdateRequest();
                Map<String, Object> values = mapValue(value(body, "request"));
                request.setTaskId(text(values.get("taskId")));
                request.setCompleted(booleanObject(values.get("completed")));
                return ApiResult.success(studyCenterWebApi.updateDaily(extractUserId(body), request));
            }
            if ("/cc001/study-center/materials/upload".equals(path)) {
                return ApiResult.success(studyCenterWebApi.uploadMaterial(
                        extractUserId(body), extractStudyPlanningMaterialUploadRequest(body)));
            }
            if ("/cc001/study-center/materials/list".equals(path)) {
                return ApiResult.success(studyCenterWebApi.materials(
                        extractUserId(body), textOrNull(value(body, "direction"))));
            }
            if ("/cc001/study-center/materials/delete".equals(path)) {
                return ApiResult.success(studyCenterWebApi.deleteMaterial(
                        extractUserId(body), textOrNull(value(body, "materialId"))));
            }
            if ("/cc001/study-center/admin/resources/list".equals(path)) {
                return ApiResult.success(studyCenterWebApi.adminResources(extractAdminId(body)));
            }
            if ("/cc001/study-center/admin/resources/save".equals(path)) {
                return ApiResult.success(studyCenterWebApi.saveResource(extractAdminId(body), extractAdminContentItem(body)));
            }
            if ("/cc001/study-center/admin/resources/pin".equals(path)) {
                return ApiResult.success(studyCenterWebApi.pinResource(extractAdminId(body), textOrNull(value(body, "contentId"))));
            }
            if ("/cc001/study-center/admin/resources/hide".equals(path)) {
                return ApiResult.success(studyCenterWebApi.hideResource(extractAdminId(body), textOrNull(value(body, "contentId"))));
            }
            if ("/cc001/study-center/admin/resources/delete".equals(path)) {
                return ApiResult.success(studyCenterWebApi.deleteResource(extractAdminId(body), textOrNull(value(body, "contentId"))));
            }
            if ("/cc001/notifications/list".equals(path)) {
                return ApiResult.success(notificationsWebApi.list(extractUserId(body)));
            }
            if ("/cc001/notifications/unread-count".equals(path)) {
                return ApiResult.success(notificationsWebApi.unreadCount(extractUserId(body)));
            }
            if ("/cc001/notifications/read".equals(path)) {
                return ApiResult.success(notificationsWebApi.read(
                        extractUserId(body), textOrNull(value(body, "notificationId"))));
            }
            if ("/cc001/notifications/read-all".equals(path)) {
                return ApiResult.success(notificationsWebApi.readAll(extractUserId(body)));
            }
            if ("/cc001/notifications/delete".equals(path)) {
                return ApiResult.success(notificationsWebApi.delete(
                        extractUserId(body), textOrNull(value(body, "notificationId"))));
            }
            if ("/cc001/notifications/subscription/grant".equals(path)) {
                return ApiResult.success(notificationsWebApi.grant(extractSubscriptionGrantRequest(body)));
            }
            if ("/cc001/notifications/subscription/quota".equals(path)) {
                return ApiResult.success(notificationsWebApi.quota(extractUserId(body)));
            }
            if ("/cc001/notifications/subscription/send".equals(path)) {
                return ApiResult.success(notificationsWebApi.send(extractSubscriptionSendRequest(body)));
            }
            if ("/cc001/notifications/weekly-report/run".equals(path)) {
                return ApiResult.success(notificationsWebApi.weeklyReport(
                        extractUserId(body), stringList(value(body, "highlights"))));
            }
            if ("/cc001/admin/whoami".equals(path)) {
                return ApiResult.success(adminWebApi.whoami(extractAdminId(body)));
            }
            if ("/cc001/admin/organizations/dashboard".equals(path)) {
                return ApiResult.success(adminWebApi.dashboard(
                        extractAdminId(body), textOrNull(value(body, "orgId"))));
            }
            if ("/cc001/admin/organizations/students".equals(path)) {
                return ApiResult.success(adminWebApi.students(
                        extractAdminId(body), textOrNull(value(body, "orgId"))));
            }
            if ("/cc001/admin/users/list".equals(path)) {
                return ApiResult.success(adminWebApi.users(
                        extractAdminId(body), intValue(value(body, "page"), 1),
                        intValue(value(body, "size"), 20), textOrNull(value(body, "keyword"))));
            }
            if ("/cc001/admin/users/ban".equals(path)) {
                return ApiResult.success(adminWebApi.ban(
                        extractAdminId(body), textOrNull(value(body, "userId")),
                        textOrNull(value(body, "reason"))));
            }
            if ("/cc001/admin/users/unban".equals(path)) {
                return ApiResult.success(adminWebApi.unban(
                        extractAdminId(body), textOrNull(value(body, "userId"))));
            }
            if ("/cc001/admin/questions/list".equals(path)) {
                return ApiResult.success(adminWebApi.questions(
                        extractAdminId(body), textOrNull(value(body, "source")),
                        textOrNull(value(body, "reviewStatus"))));
            }
            if ("/cc001/admin/questions/save".equals(path)) {
                return ApiResult.success(adminWebApi.saveQuestion(
                        extractAdminId(body), extractAdminQuestion(body, "question")));
            }
            if ("/cc001/admin/questions/update".equals(path)) {
                return ApiResult.success(adminWebApi.updateQuestion(
                        extractAdminId(body), textOrNull(value(body, "questionId")),
                        extractAdminQuestion(body, "patch")));
            }
            if ("/cc001/admin/questions/approve".equals(path)) {
                return ApiResult.success(adminWebApi.approveQuestion(
                        extractAdminId(body), textOrNull(value(body, "questionId"))));
            }
            if ("/cc001/admin/questions/reject".equals(path)) {
                return ApiResult.success(adminWebApi.rejectQuestion(
                        extractAdminId(body), textOrNull(value(body, "questionId"))));
            }
            if ("/cc001/admin/questions/delete".equals(path)) {
                return ApiResult.success(adminWebApi.deleteQuestion(
                        extractAdminId(body), textOrNull(value(body, "questionId"))));
            }
            if ("/cc001/admin/assessment/questions/save".equals(path)) {
                ApiResult admin = requireAdmin(body);
                if (!admin.getSuccess()) return admin;
                return ApiResult.success(assessmentWebApi.saveQuestion(
                        longObject(value(body, "scaleId")), extractAssessmentQuestion(body)));
            }
            if ("/cc001/admin/assessment/questions/delete".equals(path)) {
                ApiResult admin = requireAdmin(body);
                if (!admin.getSuccess()) return admin;
                return ApiResult.success(assessmentWebApi.deleteQuestion(
                        longObject(value(body, "scaleId")), longObject(value(body, "questionId"))));
            }
            if ("/cc001/admin/content/list".equals(path)) {
                return ApiResult.success(adminWebApi.content(
                        extractAdminId(body), textOrNull(value(body, "type"))));
            }
            if ("/cc001/admin/content/save".equals(path)) {
                return ApiResult.success(adminWebApi.saveContent(
                        extractAdminId(body), extractAdminContentItem(body)));
            }
            if ("/cc001/admin/content/pin".equals(path)) {
                return ApiResult.success(adminWebApi.pinContent(
                        extractAdminId(body), textOrNull(value(body, "contentId"))));
            }
            if ("/cc001/admin/content/hide".equals(path)) {
                return ApiResult.success(adminWebApi.hideContent(
                        extractAdminId(body), textOrNull(value(body, "contentId"))));
            }
            if ("/cc001/admin/content/delete".equals(path)) {
                return ApiResult.success(adminWebApi.deleteContent(
                        extractAdminId(body), textOrNull(value(body, "contentId"))));
            }
            if ("/cc001/admin/broadcast".equals(path)) {
                return ApiResult.success(adminWebApi.broadcast(
                        extractAdminId(body), extractAdminBroadcastRequest(body)));
            }
            if ("/cc001/admin/analytics/summary".equals(path)) {
                return ApiResult.success(adminWebApi.analytics(extractAdminId(body)));
            }
            if ("/cc001/admin/audit-log/list".equals(path)) {
                return ApiResult.success(adminWebApi.auditLogs(
                        extractAdminId(body), intValue(value(body, "page"), 1),
                        intValue(value(body, "size"), 20)));
            }
            if ("/cc001/files/upload".equals(path)) {
                return ApiResult.success(fileWebApi.upload(extractFileUploadRequest(body)));
            }
            if ("/cc001/files/preview-url".equals(path)) {
                return ApiResult.success(fileWebApi.previewUrl(
                        textOrNull(value(body, "fileUrlOrKey")), longValue(value(body, "ttlSeconds"), 0L)));
            }
            if ("/cc001/files/download".equals(path)) {
                return ApiResult.success(fileWebApi.download(textOrNull(value(body, "fileUrlOrKey"))));
            }
            if ("/cc001/files/delete".equals(path)) {
                return ApiResult.success(fileWebApi.delete(textOrNull(value(body, "fileUrlOrKey"))));
            }
            if ("/cc001/files/extract-text".equals(path)) {
                return ApiResult.success(fileWebApi.extractText(textOrNull(value(body, "fileUrlOrKey"))));
            }
            if ("/cc001/resume-diagnosis/analyze".equals(path)) {
                return ApiResult.success(resumeDiagnosisWebApi.analyze(
                        extractUserId(body), extractResumeDiagnosisRequest(body)));
            }
            if ("/cc001/admin/assessment/catalog".equals(path)) {
                ApiResult admin = requireAdmin(body);
                if (!admin.getSuccess()) return admin;
                return ApiResult.success(assessmentWebApi.questions(longObject(value(body, "scaleId"))));
            }
            if ("/cc001/admin/assessment/scales/save".equals(path)) {
                ApiResult admin = requireAdmin(body);
                if (!admin.getSuccess()) return admin;
                return ApiResult.success(assessmentWebApi.saveAnswerQuestionCount(
                        longObject(value(body, "scaleId")), integerObject(value(body, "answerQuestionCount"))));
            }
            if ("/cc001/resume-diagnosis/history/list".equals(path)) {
                return ApiResult.success(resumeDiagnosisWebApi.listHistory(
                        extractUserId(body), longObject(value(body, "resumeId"))));
            }
            if ("/cc001/resume-diagnosis/history/delete".equals(path)) {
                return ApiResult.success(resumeDiagnosisWebApi.deleteHistory(
                        extractUserId(body), longObject(value(body, "resumeId")),
                        longObject(value(body, "diagnosisId"))));
            }
            if ("/cc001/resume-diagnosis/keywords/status".equals(path)) {
                return ApiResult.success(resumeDiagnosisWebApi.keywordStatus(
                        extractUserId(body), longObject(value(body, "resumeId"))));
            }
            if ("/cc001/postgraduate/school-recommend".equals(path)) {
                return ApiResult.success(postgraduateWebApi.recommendSchools(
                        extractUserId(body), extractPostgraduateSchoolRecommendRequest(body)));
            }
            if ("/cc001/postgraduate/plan/generate".equals(path)) {
                return ApiResult.success(postgraduateWebApi.generatePlan(
                        extractUserId(body), extractPostgraduatePlanRequest(body)));
            }
            if ("/cc001/postgraduate/mistake/analyze".equals(path)) {
                return ApiResult.success(postgraduateWebApi.analyzeMistake(
                        extractUserId(body), extractPostgraduateMistakeAnalyzeRequest(body)));
            }
            if ("/cc001/postgraduate/reexam/prepare".equals(path)) {
                return ApiResult.success(postgraduateWebApi.prepareReexam(
                        extractUserId(body), extractPostgraduateReexamPrepareRequest(body)));
            }
            if ("/cc001/recommendation/diagnose".equals(path)) {
                return ApiResult.success(recommendationWebApi.diagnose(
                        extractUserId(body), extractRecommendationProfileRequest(body)));
            }
            if ("/cc001/recommendation/plan/generate".equals(path)) {
                return ApiResult.success(recommendationWebApi.generatePlan(
                        extractUserId(body), extractRecommendationProfileRequest(body)));
            }
            if ("/cc001/recommendation/document/polish".equals(path)) {
                return ApiResult.success(recommendationWebApi.polishDocument(
                        extractUserId(body), extractRecommendationDocumentPolishRequest(body)));
            }
            if ("/cc001/recommendation/tutor-letter/generate".equals(path)) {
                return ApiResult.success(recommendationWebApi.generateTutorLetter(
                        extractUserId(body), extractRecommendationTutorLetterRequest(body)));
            }
            if ("/cc001/study-abroad/profile/diagnose".equals(path)) {
                return ApiResult.success(studyAbroadWebApi.diagnoseProfile(
                        extractUserId(body), extractStudyAbroadProfileRequest(body)));
            }
            if ("/cc001/study-abroad/language/plan".equals(path)) {
                return ApiResult.success(studyAbroadWebApi.generateLanguagePlan(
                        extractUserId(body), extractStudyAbroadLanguagePlanRequest(body)));
            }
            if ("/cc001/study-abroad/school/position".equals(path)) {
                return ApiResult.success(studyAbroadWebApi.positionSchools(
                        extractUserId(body), extractStudyAbroadSchoolPositionRequest(body)));
            }
            if ("/cc001/study-abroad/statement/outline".equals(path)) {
                return ApiResult.success(studyAbroadWebApi.buildStatementOutline(
                        extractUserId(body), extractStudyAbroadStatementRequest(body)));
            }
            if ("/cc001/study-abroad/visa/checklist".equals(path)) {
                return ApiResult.success(studyAbroadWebApi.buildVisaChecklist(
                        extractUserId(body), extractStudyAbroadVisaChecklistRequest(body)));
            }
            if ("/cc001/further-study/target/save".equals(path)) {
                return ApiResult.success(furtherStudyWebApi.saveTarget(
                        extractUserId(body), extractFurtherStudyTargetSaveRequest(body)));
            }
            if ("/cc001/further-study/records/list".equals(path)) {
                return ApiResult.success(furtherStudyWebApi.listRecords(
                        extractUserId(body), extractFurtherStudyRecordQueryRequest(body)));
            }
            if ("/cc001/further-study/records/detail".equals(path)) {
                return ApiResult.success(furtherStudyWebApi.recordDetail(
                        extractUserId(body), textOrNull(value(body, "recordId"))));
            }
            if ("/cc001/further-study/records/status/update".equals(path)) {
                return ApiResult.success(furtherStudyWebApi.updateRecordStatus(
                        extractUserId(body), extractFurtherStudyRecordStatusUpdateRequest(body)));
            }
            if ("/cc001/further-study/materials/save".equals(path)) {
                return ApiResult.success(furtherStudyWebApi.saveMaterial(
                        extractUserId(body), extractFurtherStudyMaterialSaveRequest(body)));
            }
            if ("/cc001/further-study/materials/list".equals(path)) {
                return ApiResult.success(furtherStudyWebApi.listMaterials(
                        extractUserId(body), textOrNull(value(body, "track")), textOrNull(value(body, "recordId"))));
            }
            if ("/cc001/further-study/records/events".equals(path)) {
                return ApiResult.success(furtherStudyWebApi.listEvents(
                        extractUserId(body), textOrNull(value(body, "recordId"))));
            }
            return ApiResult.fail("Unsupported CyanCruise custom WebAPI path: " + path);
        } catch (IdentityBoundaryException ex) {
            return ApiResult.fail(ex.getStatus(), ex.getMessage());
        } catch (RuntimeException ex) {
            return ApiResult.ex(ex);
        } finally {
            RequestContextCosmicLoginContextBridge.clearPlatformContext();
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

    private Object requestObject(Object body) {
        if (body instanceof Map) {
            Object request = ((Map<?, ?>) body).get("request");
            return request == null ? body : request;
        }
        return body;
    }

    private Map<?, ?> requestMap(Object body) {
        Object request = requestObject(body);
        return request instanceof Map ? (Map<?, ?>) request : null;
    }

    private Map<String, Object> mapValue(Object value) {
        Map<String, Object> out = new java.util.LinkedHashMap<String, Object>();
        if (!(value instanceof Map)) {
            return out;
        }
        for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
            String key = textOrNull(entry.getKey());
            if (key != null) {
                out.put(key, entry.getValue());
            }
        }
        return out;
    }

    private String extractOptionalUserId(Object body) {
        if (body instanceof Map) {
            return textOrNull(((Map<?, ?>) body).get("userId"));
        }
        return textOrNull(body);
    }

    private boolean requiresActiveUser(String path) {
        return path != null
                && path.startsWith("/cc001/")
                && !path.startsWith("/cc001/admin/")
                && !path.startsWith("/cc001/identity/")
                && !path.startsWith("/cc001/files/");
    }

    private boolean isActiveUserAllowed(Object body) {
        String userId = extractOptionalUserId(body);
        try {
            CosmicIdentityContextDto identity = identityWebApi.current();
            String resolvedUserId = firstText(identity == null ? null : identity.getUserId(), userId);
            if (!adminWebApi.isUserAllowed(resolvedUserId)) {
                return false;
            }
            if (identity != null && hasText(identity.getUserId())) {
                adminWebApi.registerActiveUserIfAbsent(identity);
            } else {
                adminWebApi.registerActiveUserIfAbsent(userId);
            }
            return true;
        } catch (RuntimeException ex) {
            return true;
        }
    }

    private String extractAdminId(Object body) {
        if (body instanceof Map) {
            Object adminId = ((Map<?, ?>) body).get("adminId");
            if (adminId != null) {
                return text(adminId);
            }
            Object userId = ((Map<?, ?>) body).get("userId");
            if (userId != null) {
                return text(userId);
            }
        }
        return text(body);
    }

    private String firstText(String first, String second) {
        return hasText(first) ? first.trim() : second;
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }

    private CareerProfileOnboardingRequest extractOnboardingRequest(Object body) {
        Object request = body;
        if (body instanceof Map) {
            request = ((Map<?, ?>) body).get("request");
        }
        if (request instanceof CareerProfileOnboardingRequest) {
            return (CareerProfileOnboardingRequest) request;
        }
        CareerProfileOnboardingRequest onboarding = new CareerProfileOnboardingRequest();
        if (!(request instanceof Map)) {
            return onboarding;
        }

        Map<?, ?> values = (Map<?, ?>) request;
        onboarding.setIdentityType(textOrNull(values.get("identityType")));
        onboarding.setStage(textOrNull(firstPresent(values, "stage", "educationStage")));
        onboarding.setPainPoint(textOrNull(firstPresent(values, "painPoint", "preference")));
        onboarding.setHasResume(textOrNull(firstPresent(values, "hasResume")));
        onboarding.setResumeStatus(textOrNull(values.get("resumeStatus")));
        onboarding.setSelectedResumeId(longObject(firstPresent(values, "selectedResumeId")));
        onboarding.setExperience(textOrNull(firstPresent(values, "experience", "strengths")));
        onboarding.setSelfProfileSupplement(textOrNull(values.get("selfProfileSupplement")));
        onboarding.setTimeline(textOrNull(values.get("timeline")));
        UserProfileSnapshot.EducationBlock education = extractEducation(values);
        if (education != null) {
            onboarding.setEducation(education);
        }
        onboarding.setWeeklyAvailability(textOrNull(values.get("weeklyAvailability")));
        onboarding.setPriorityHelp(textOrNull(values.get("priorityHelp")));
        onboarding.setRecommendedEntry(textOrNull(values.get("recommendedEntry")));
        onboarding.setOnboardingCompletedAt(textOrNull(values.get("onboardingCompletedAt")));
        onboarding.setTargetRole(textOrNull(values.get("targetRole")));
        onboarding.setTargetSchool(textOrNull(values.get("targetSchool")));
        onboarding.setRouteGoal(textOrNull(values.get("routeGoal")));
        if (onboarding.getHasResume() == null) {
            onboarding.setHasResume(hasResumeFromStatus(onboarding.getResumeStatus()));
        }
        return onboarding;
    }

    private UserProfileSnapshot.EducationBlock extractEducation(Map<?, ?> values) {
        Object educationValue = values.get("education");
        UserProfileSnapshot.EducationBlock education = null;
        if (educationValue instanceof UserProfileSnapshot.EducationBlock) {
            education = (UserProfileSnapshot.EducationBlock) educationValue;
        } else if (educationValue instanceof Map) {
            education = new UserProfileSnapshot.EducationBlock();
            Map<?, ?> educationMap = (Map<?, ?>) educationValue;
            education.setSchool(textOrNull(educationMap.get("school")));
            education.setMajor(textOrNull(firstPresent(educationMap, "major", "schoolMajor")));
            education.setDegree(textOrNull(educationMap.get("degree")));
            education.setGraduationYear(textOrNull(educationMap.get("graduationYear")));
        }
        String schoolMajor = textOrNull(values.get("schoolMajor"));
        if (schoolMajor != null) {
            if (education == null) {
                education = new UserProfileSnapshot.EducationBlock();
            }
            education.setMajor(schoolMajor);
        }
        String school = textOrNull(values.get("school"));
        String major = textOrNull(values.get("major"));
        if (school != null || major != null) {
            if (education == null) {
                education = new UserProfileSnapshot.EducationBlock();
            }
            if (school != null) {
                education.setSchool(school);
            }
            if (major != null) {
                education.setMajor(major);
            }
        }
        return education;
    }

    private CareerProfileDraftDto extractProfileDraft(Object body) {
        Object request = requestObject(body);
        if (request instanceof CareerProfileDraftDto) {
            return (CareerProfileDraftDto) request;
        }
        CareerProfileDraftDto draft = new CareerProfileDraftDto();
        Map<?, ?> values = requestMap(body);
        if (values != null) {
            draft.setIdentityType(textOrNull(values.get("identityType")));
            draft.setEducationStage(textOrNull(values.get("educationStage")));
            draft.setSchool(textOrNull(values.get("school")));
            draft.setMajor(textOrNull(values.get("major")));
            draft.setSchoolMajor(textOrNull(values.get("schoolMajor")));
            draft.setResumeStatus(textOrNull(values.get("resumeStatus")));
            draft.setTargetRole(textOrNull(values.get("targetRole")));
            draft.setPreference(textOrNull(values.get("preference")));
            draft.setExperience(textOrNull(firstPresent(values, "experience", "strengths")));
            draft.setRouteIntent(textOrNull(firstPresent(values, "routeIntent", "selectedGoal")));
        }
        return draft;
    }

    private ResumeCreateRequest extractResumeCreateRequest(Object body) {
        Object request = requestObject(body);
        if (request instanceof ResumeCreateRequest) {
            return (ResumeCreateRequest) request;
        }
        ResumeCreateRequest out = new ResumeCreateRequest();
        Map<?, ?> values = requestMap(body);
        if (values != null) {
            out.setTitle(textOrNull(values.get("title")));
            out.setTargetJob(textOrNull(firstPresent(values, "targetJob", "targetRole")));
            out.setFileKey(textOrNull(firstPresent(values, "fileKey", "objectKey")));
            out.setParsedContent(textOrNull(firstPresent(values, "parsedContent", "content")));
        }
        return out;
    }

    private ResumeDiagnosisRequest extractResumeDiagnosisRequest(Object body) {
        Object request = requestObject(body);
        if (request instanceof ResumeDiagnosisRequest) {
            return (ResumeDiagnosisRequest) request;
        }
        ResumeDiagnosisRequest out = new ResumeDiagnosisRequest();
        Map<?, ?> values = requestMap(body);
        if (values != null) {
            out.setResumeId(longObject(values.get("resumeId")));
            out.setResumeText(textOrNull(values.get("resumeText")));
            out.setJobDescription(textOrNull(values.get("jobDescription")));
            out.setProfileContext(textOrNull(values.get("profileContext")));
        }
        return out;
    }

    private PostgraduateSchoolRecommendRequest extractPostgraduateSchoolRecommendRequest(Object body) {
        Object request = requestObject(body);
        if (request instanceof PostgraduateSchoolRecommendRequest) {
            return (PostgraduateSchoolRecommendRequest) request;
        }
        PostgraduateSchoolRecommendRequest out = new PostgraduateSchoolRecommendRequest();
        Map<?, ?> values = requestMap(body);
        if (values != null) {
            out.setUndergraduateSchool(textOrNull(values.get("undergraduateSchool")));
            out.setUndergraduateLevel(textOrNull(values.get("undergraduateLevel")));
            out.setMajor(textOrNull(values.get("major")));
            out.setTargetMajor(textOrNull(firstPresent(values, "targetMajor", "targetSubject")));
            out.setGpa(textOrNull(firstPresent(values, "gpa", "averageScore")));
            out.setEnglishLevel(textOrNull(values.get("englishLevel")));
            out.setPreferredRegion(textOrNull(firstPresent(values, "preferredRegion", "region")));
            out.setPreference(textOrNull(values.get("preference")));
        }
        return out;
    }

    private PostgraduatePlanRequest extractPostgraduatePlanRequest(Object body) {
        Object request = requestObject(body);
        if (request instanceof PostgraduatePlanRequest) {
            return (PostgraduatePlanRequest) request;
        }
        PostgraduatePlanRequest out = new PostgraduatePlanRequest();
        Map<?, ?> values = requestMap(body);
        if (values != null) {
            out.setTargetSchool(textOrNull(values.get("targetSchool")));
            out.setTargetMajor(textOrNull(firstPresent(values, "targetMajor", "targetSubject")));
            out.setExamDate(textOrNull(values.get("examDate")));
            out.setStartDate(textOrNull(values.get("startDate")));
            out.setWeeklyHours(textOrNull(values.get("weeklyHours")));
            out.setSubjects(stringList(values.get("subjects")));
            out.setCurrentStage(textOrNull(values.get("currentStage")));
        }
        return out;
    }

    private PostgraduateMistakeAnalyzeRequest extractPostgraduateMistakeAnalyzeRequest(Object body) {
        Object request = requestObject(body);
        if (request instanceof PostgraduateMistakeAnalyzeRequest) {
            return (PostgraduateMistakeAnalyzeRequest) request;
        }
        PostgraduateMistakeAnalyzeRequest out = new PostgraduateMistakeAnalyzeRequest();
        Map<?, ?> values = requestMap(body);
        if (values != null) {
            out.setSubject(textOrNull(values.get("subject")));
            out.setQuestionText(textOrNull(firstPresent(values, "questionText", "question")));
            out.setWrongAnswer(textOrNull(values.get("wrongAnswer")));
            out.setTargetExam(textOrNull(values.get("targetExam")));
        }
        return out;
    }

    private PostgraduateReexamPrepareRequest extractPostgraduateReexamPrepareRequest(Object body) {
        Object request = requestObject(body);
        if (request instanceof PostgraduateReexamPrepareRequest) {
            return (PostgraduateReexamPrepareRequest) request;
        }
        PostgraduateReexamPrepareRequest out = new PostgraduateReexamPrepareRequest();
        Map<?, ?> values = requestMap(body);
        if (values != null) {
            out.setTargetSchool(textOrNull(values.get("targetSchool")));
            out.setTargetMajor(textOrNull(firstPresent(values, "targetMajor", "targetSubject")));
            out.setPreliminaryStatus(textOrNull(firstPresent(values, "preliminaryStatus", "examStatus")));
            out.setMaterials(stringList(values.get("materials")));
            out.setResearchExperience(textOrNull(values.get("researchExperience")));
        }
        return out;
    }

    private RecommendationProfileRequest extractRecommendationProfileRequest(Object body) {
        Object request = requestObject(body);
        if (request instanceof RecommendationProfileRequest) {
            return (RecommendationProfileRequest) request;
        }
        RecommendationProfileRequest out = new RecommendationProfileRequest();
        Map<?, ?> values = requestMap(body);
        if (values != null) {
            out.setGrade(textOrNull(values.get("grade")));
            out.setSchool(textOrNull(values.get("school")));
            out.setMajor(textOrNull(values.get("major")));
            out.setGpa(textOrNull(firstPresent(values, "gpa", "averageScore")));
            out.setRank(textOrNull(values.get("rank")));
            out.setEnglishLevel(textOrNull(values.get("englishLevel")));
            out.setAwards(textOrNull(firstPresent(values, "awards", "competitions")));
            out.setResearch(textOrNull(values.get("research")));
            out.setPapers(textOrNull(values.get("papers")));
            out.setPatentsOrCopyrights(textOrNull(firstPresent(values, "patentsOrCopyrights", "softwareCopyrights")));
            out.setTargetSchools(textOrNull(values.get("targetSchools")));
            out.setTargetMajor(textOrNull(firstPresent(values, "targetMajor", "targetSubject")));
        }
        return out;
    }

    private RecommendationDocumentPolishRequest extractRecommendationDocumentPolishRequest(Object body) {
        Object request = requestObject(body);
        if (request instanceof RecommendationDocumentPolishRequest) {
            return (RecommendationDocumentPolishRequest) request;
        }
        RecommendationDocumentPolishRequest out = new RecommendationDocumentPolishRequest();
        Map<?, ?> values = requestMap(body);
        if (values != null) {
            out.setDocumentType(textOrNull(values.get("documentType")));
            out.setTargetMajor(textOrNull(firstPresent(values, "targetMajor", "targetSubject")));
            out.setDraft(textOrNull(values.get("draft")));
            out.setHighlights(textOrNull(values.get("highlights")));
        }
        return out;
    }

    private RecommendationTutorLetterRequest extractRecommendationTutorLetterRequest(Object body) {
        Object request = requestObject(body);
        if (request instanceof RecommendationTutorLetterRequest) {
            return (RecommendationTutorLetterRequest) request;
        }
        RecommendationTutorLetterRequest out = new RecommendationTutorLetterRequest();
        Map<?, ?> values = requestMap(body);
        if (values != null) {
            out.setTutorName(textOrNull(values.get("tutorName")));
            out.setTargetSchool(textOrNull(values.get("targetSchool")));
            out.setTargetMajor(textOrNull(firstPresent(values, "targetMajor", "targetSubject")));
            out.setResearchDirection(textOrNull(firstPresent(values, "researchDirection", "paperKeywords")));
            out.setPersonalBackground(textOrNull(values.get("personalBackground")));
            out.setPurpose(textOrNull(values.get("purpose")));
        }
        return out;
    }

    private StudyAbroadProfileRequest extractStudyAbroadProfileRequest(Object body) {
        Object request = requestObject(body);
        if (request instanceof StudyAbroadProfileRequest) {
            return (StudyAbroadProfileRequest) request;
        }
        StudyAbroadProfileRequest out = new StudyAbroadProfileRequest();
        Map<?, ?> values = requestMap(body);
        if (values != null) {
            out.setCountryOrRegion(textOrNull(firstPresent(values, "countryOrRegion", "country", "region")));
            out.setTargetDegree(textOrNull(firstPresent(values, "targetDegree", "degree")));
            out.setTargetMajor(textOrNull(firstPresent(values, "targetMajor", "targetSubject")));
            out.setSchool(textOrNull(values.get("school")));
            out.setMajor(textOrNull(values.get("major")));
            out.setGpa(textOrNull(firstPresent(values, "gpa", "averageScore")));
            out.setLanguageScore(textOrNull(firstPresent(values, "languageScore", "englishLevel")));
            out.setBudget(textOrNull(values.get("budget")));
            out.setBackground(textOrNull(firstPresent(values, "background", "experience")));
            out.setPreference(textOrNull(values.get("preference")));
        }
        return out;
    }

    private StudyAbroadLanguagePlanRequest extractStudyAbroadLanguagePlanRequest(Object body) {
        Object request = requestObject(body);
        if (request instanceof StudyAbroadLanguagePlanRequest) {
            return (StudyAbroadLanguagePlanRequest) request;
        }
        StudyAbroadLanguagePlanRequest out = new StudyAbroadLanguagePlanRequest();
        Map<?, ?> values = requestMap(body);
        if (values != null) {
            out.setExamType(textOrNull(firstPresent(values, "examType", "exam")));
            out.setCurrentScore(textOrNull(values.get("currentScore")));
            out.setTargetScore(textOrNull(values.get("targetScore")));
            out.setExamDate(textOrNull(values.get("examDate")));
            out.setWeeklyHours(textOrNull(values.get("weeklyHours")));
            out.setWeakParts(textOrNull(firstPresent(values, "weakParts", "weaknesses")));
        }
        return out;
    }

    private StudyAbroadSchoolPositionRequest extractStudyAbroadSchoolPositionRequest(Object body) {
        Object request = requestObject(body);
        if (request instanceof StudyAbroadSchoolPositionRequest) {
            return (StudyAbroadSchoolPositionRequest) request;
        }
        StudyAbroadSchoolPositionRequest out = new StudyAbroadSchoolPositionRequest();
        Map<?, ?> values = requestMap(body);
        if (values != null) {
            out.setCountryOrRegion(textOrNull(firstPresent(values, "countryOrRegion", "country", "region")));
            out.setTargetMajor(textOrNull(firstPresent(values, "targetMajor", "targetSubject")));
            out.setGpa(textOrNull(firstPresent(values, "gpa", "averageScore")));
            out.setLanguageScore(textOrNull(firstPresent(values, "languageScore", "englishLevel")));
            out.setBudget(textOrNull(values.get("budget")));
            out.setBackground(textOrNull(firstPresent(values, "background", "experience")));
            out.setPreference(textOrNull(values.get("preference")));
        }
        return out;
    }

    private StudyAbroadStatementRequest extractStudyAbroadStatementRequest(Object body) {
        Object request = requestObject(body);
        if (request instanceof StudyAbroadStatementRequest) {
            return (StudyAbroadStatementRequest) request;
        }
        StudyAbroadStatementRequest out = new StudyAbroadStatementRequest();
        Map<?, ?> values = requestMap(body);
        if (values != null) {
            out.setTargetMajor(textOrNull(firstPresent(values, "targetMajor", "targetSubject")));
            out.setProfessorTopic(textOrNull(firstPresent(values, "professorTopic", "projectDirection", "researchDirection")));
            out.setPersonalStory(textOrNull(values.get("personalStory")));
            out.setAcademicExperience(textOrNull(firstPresent(values, "academicExperience", "experience")));
            out.setCareerGoal(textOrNull(firstPresent(values, "careerGoal", "goal")));
            out.setLanguage(textOrNull(values.get("language")));
        }
        return out;
    }

    private StudyAbroadVisaChecklistRequest extractStudyAbroadVisaChecklistRequest(Object body) {
        Object request = requestObject(body);
        if (request instanceof StudyAbroadVisaChecklistRequest) {
            return (StudyAbroadVisaChecklistRequest) request;
        }
        StudyAbroadVisaChecklistRequest out = new StudyAbroadVisaChecklistRequest();
        Map<?, ?> values = requestMap(body);
        if (values != null) {
            out.setCountryOrRegion(textOrNull(firstPresent(values, "countryOrRegion", "country", "region")));
            out.setApplicationSeason(textOrNull(firstPresent(values, "applicationSeason", "season")));
            out.setAdmissionStatus(textOrNull(values.get("admissionStatus")));
            out.setMaterialStatus(textOrNull(values.get("materialStatus")));
        }
        return out;
    }

    private FurtherStudyTargetSaveRequest extractFurtherStudyTargetSaveRequest(Object body) {
        Object request = requestObject(body);
        if (request instanceof FurtherStudyTargetSaveRequest) {
            return (FurtherStudyTargetSaveRequest) request;
        }
        FurtherStudyTargetSaveRequest out = new FurtherStudyTargetSaveRequest();
        Map<?, ?> values = requestMap(body);
        if (values != null) {
            out.setTargetId(textOrNull(values.get("targetId")));
            out.setTrack(textOrNull(values.get("track")));
            out.setTargetSchool(textOrNull(values.get("targetSchool")));
            out.setTargetMajor(textOrNull(firstPresent(values, "targetMajor", "targetSubject")));
            out.setTargetRegion(textOrNull(firstPresent(values, "targetRegion", "countryOrRegion", "region")));
            out.setTargetStage(textOrNull(values.get("targetStage")));
            out.setStatus(textOrNull(values.get("status")));
            out.setTargetJson(textOrNull(values.get("targetJson")));
        }
        return out;
    }

    private FurtherStudyRecordQueryRequest extractFurtherStudyRecordQueryRequest(Object body) {
        Object request = requestObject(body);
        if (request instanceof FurtherStudyRecordQueryRequest) {
            return (FurtherStudyRecordQueryRequest) request;
        }
        FurtherStudyRecordQueryRequest out = new FurtherStudyRecordQueryRequest();
        Map<?, ?> values = requestMap(body);
        if (values != null) {
            out.setTrack(textOrNull(values.get("track")));
            out.setRecordType(textOrNull(values.get("recordType")));
            out.setStatus(textOrNull(values.get("status")));
            out.setKeyword(textOrNull(values.get("keyword")));
            out.setLimit(Integer.valueOf(intValue(values.get("limit"), 100)));
            out.setOffset(Integer.valueOf(intValue(values.get("offset"), 0)));
        }
        return out;
    }

    private FurtherStudyRecordStatusUpdateRequest extractFurtherStudyRecordStatusUpdateRequest(Object body) {
        Object request = requestObject(body);
        if (request instanceof FurtherStudyRecordStatusUpdateRequest) {
            return (FurtherStudyRecordStatusUpdateRequest) request;
        }
        FurtherStudyRecordStatusUpdateRequest out = new FurtherStudyRecordStatusUpdateRequest();
        Map<?, ?> values = requestMap(body);
        if (values != null) {
            out.setRecordId(textOrNull(values.get("recordId")));
            out.setStatus(textOrNull(values.get("status")));
            out.setEventSummary(textOrNull(values.get("eventSummary")));
            out.setEventJson(textOrNull(values.get("eventJson")));
        }
        return out;
    }

    private FurtherStudyMaterialSaveRequest extractFurtherStudyMaterialSaveRequest(Object body) {
        Object request = requestObject(body);
        if (request instanceof FurtherStudyMaterialSaveRequest) {
            return (FurtherStudyMaterialSaveRequest) request;
        }
        FurtherStudyMaterialSaveRequest out = new FurtherStudyMaterialSaveRequest();
        Map<?, ?> values = requestMap(body);
        if (values != null) {
            out.setMaterialId(textOrNull(values.get("materialId")));
            out.setTrack(textOrNull(values.get("track")));
            out.setRecordId(textOrNull(values.get("recordId")));
            out.setMaterialType(textOrNull(values.get("materialType")));
            out.setTitle(textOrNull(values.get("title")));
            out.setStatus(textOrNull(values.get("status")));
            out.setFileKey(textOrNull(values.get("fileKey")));
            out.setContentJson(textOrNull(values.get("contentJson")));
        }
        return out;
    }

    private InterviewStartRequest extractInterviewStartRequest(Object body) {
        Object request = requestObject(body);
        if (request instanceof InterviewStartRequest) {
            return (InterviewStartRequest) request;
        }
        InterviewStartRequest out = new InterviewStartRequest();
        Map<?, ?> values = requestMap(body);
        if (values != null) {
            out.setResumeId(longObject(values.get("resumeId")));
            out.setPositionName(textOrNull(firstPresent(values, "positionName", "targetRole", "jobTitle")));
            out.setDifficulty(textOrNull(values.get("difficulty")));
            out.setMode(textOrNull(values.get("mode")));
        }
        return out;
    }

    private AssistantChatRequest extractAssistantChatRequest(Object body) {
        Object request = requestObject(body);
        if (request instanceof AssistantChatRequest) {
            return (AssistantChatRequest) request;
        }
        AssistantChatRequest out = new AssistantChatRequest();
        Map<?, ?> values = requestMap(body);
        if (values != null) {
            out.setMessage(textOrNull(values.get("message")));
            out.setPersona(textOrNull(values.get("persona")));
            out.setSessionId(longObject(values.get("sessionId")));
        }
        return out;
    }

    private FileUploadRequest extractFileUploadRequest(Object body) {
        Object request = requestObject(body);
        if (request instanceof FileUploadRequest) {
            return (FileUploadRequest) request;
        }
        FileUploadRequest out = new FileUploadRequest();
        Map<?, ?> values = requestMap(body);
        if (values != null) {
            out.setFolder(textOrNull(values.get("folder")));
            out.setOriginalFilename(textOrNull(firstPresent(values, "originalFilename", "filename", "name")));
            out.setBytes(byteArray(firstPresent(values, "bytes", "base64", "contentBase64")));
        }
        return out;
    }

    private AssessmentScaleDto extractAssessmentScale(Object body) {
        Object scale = value(body, "scale");
        if (scale instanceof AssessmentScaleDto) {
            return (AssessmentScaleDto) scale;
        }
        AssessmentScaleDto out = new AssessmentScaleDto();
        if (scale instanceof Map) {
            Map<?, ?> values = (Map<?, ?>) scale;
            out.setScaleId(longObject(values.get("scaleId")));
            out.setTitle(textOrNull(values.get("title")));
            out.setDescription(textOrNull(values.get("description")));
            out.setVersion(textOrNull(values.get("version")));
        }
        return out;
    }

    private AssessmentSubmitRequest extractAssessmentSubmitRequest(Object body) {
        Object request = requestObject(body);
        if (request instanceof AssessmentSubmitRequest) {
            return (AssessmentSubmitRequest) request;
        }
        AssessmentSubmitRequest out = new AssessmentSubmitRequest();
        Map<?, ?> values = requestMap(body);
        if (values != null) {
            out.setScaleId(longObject(values.get("scaleId")));
            out.setAttemptId(textOrNull(values.get("attemptId")));
            Object answers = values.get("answers");
            if (answers instanceof Map) {
                Map<Long, Long> mapped = new LinkedHashMap<Long, Long>();
                for (Map.Entry<?, ?> entry : ((Map<?, ?>) answers).entrySet()) {
                    Long key = longObject(entry.getKey());
                    Long answer = longObject(entry.getValue());
                    if (key != null && answer != null) {
                        mapped.put(key, answer);
                    }
                }
                out.setAnswers(mapped);
            }
            Object answerOptionIds = values.get("answerOptionIds");
            if (answerOptionIds instanceof Map) {
                Map<Long, List<Long>> mapped = new LinkedHashMap<Long, List<Long>>();
                for (Map.Entry<?, ?> entry : ((Map<?, ?>) answerOptionIds).entrySet()) {
                    Long key = longObject(entry.getKey());
                    List<Long> selected = longList(entry.getValue());
                    if (key != null && !selected.isEmpty()) {
                        mapped.put(key, selected);
                    }
                }
                out.setAnswerOptionIds(mapped);
            }
        }
        return out;
    }

    private StudyPlanningMaterialUploadRequest extractStudyPlanningMaterialUploadRequest(Object body) {
        Object request = requestObject(body);
        if (request instanceof StudyPlanningMaterialUploadRequest) {
            return (StudyPlanningMaterialUploadRequest) request;
        }
        StudyPlanningMaterialUploadRequest out = new StudyPlanningMaterialUploadRequest();
        Map<?, ?> values = requestMap(body);
        if (values == null) return out;
        out.setDirection(textOrNull(values.get("direction")));
        out.setMaterialType(textOrNull(values.get("materialType")));
        out.setTitle(textOrNull(values.get("title")));
        out.setMediaType(textOrNull(values.get("mediaType")));
        Object fileValue = values.get("file");
        Map<?, ?> fileValues = fileValue instanceof Map ? (Map<?, ?>) fileValue : values;
        FileUploadRequest file = new FileUploadRequest();
        file.setFolder(textOrNull(fileValues.get("folder")));
        file.setOriginalFilename(textOrNull(firstPresent(fileValues,
                "originalFilename", "filename", "name")));
        file.setBytes(byteArray(firstPresent(fileValues, "bytes", "base64", "contentBase64")));
        out.setFile(file);
        return out;
    }

    private List<Long> longList(Object value) {
        List<Long> result = new ArrayList<Long>();
        if (value instanceof Iterable) {
            for (Object item : (Iterable<?>) value) {
                Long parsed = longObject(item);
                if (parsed != null && !result.contains(parsed)) {
                    result.add(parsed);
                }
            }
            return result;
        }
        Long single = longObject(value);
        if (single != null) {
            result.add(single);
        }
        return result;
    }

    private SubscriptionGrantRequest extractSubscriptionGrantRequest(Object body) {
        Object request = requestObject(body);
        if (request instanceof SubscriptionGrantRequest) {
            return (SubscriptionGrantRequest) request;
        }
        SubscriptionGrantRequest out = new SubscriptionGrantRequest();
        Map<?, ?> values = requestMap(body);
        if (values != null) {
            out.setUserId(textOrNull(firstPresent(values, "userId", "openid")));
            out.setResults(stringMap(values.get("results")));
        }
        return out;
    }

    private SubscriptionSendRequest extractSubscriptionSendRequest(Object body) {
        Object request = requestObject(body);
        if (request instanceof SubscriptionSendRequest) {
            return (SubscriptionSendRequest) request;
        }
        SubscriptionSendRequest out = new SubscriptionSendRequest();
        Map<?, ?> values = requestMap(body);
        if (values != null) {
            out.setUserId(textOrNull(values.get("userId")));
            out.setTemplateId(textOrNull(values.get("templateId")));
            out.setRecipientId(textOrNull(values.get("recipientId")));
            out.setPage(textOrNull(values.get("page")));
            out.setData(stringMap(values.get("data")));
        }
        return out;
    }

    private AdminBroadcastRequest extractAdminBroadcastRequest(Object body) {
        Object request = requestObject(body);
        if (request instanceof AdminBroadcastRequest) {
            return (AdminBroadcastRequest) request;
        }
        AdminBroadcastRequest out = new AdminBroadcastRequest();
        Map<?, ?> values = requestMap(body);
        if (values != null) {
            out.setUserId(textOrNull(values.get("userId")));
            out.setUserIds(stringList(values.get("userIds")));
            out.setTitle(textOrNull(values.get("title")));
            out.setContent(textOrNull(values.get("content")));
        }
        return out;
    }

    private AdminContentItemDto extractAdminContentItem(Object body) {
        Object request = value(body, "content");
        if (request instanceof AdminContentItemDto) {
            return (AdminContentItemDto) request;
        }
        AdminContentItemDto out = new AdminContentItemDto();
        if (request instanceof Map) {
            Map<?, ?> values = (Map<?, ?>) request;
            out.setContentId(textOrNull(values.get("contentId")));
            out.setType(textOrNull(values.get("type")));
            out.setTitle(textOrNull(values.get("title")));
            out.setSummary(textOrNull(values.get("summary")));
            out.setSourceUrl(textOrNull(values.get("sourceUrl")));
            out.setCategory(textOrNull(values.get("category")));
            out.setPinned(booleanObject(values.get("pinned")));
            out.setHidden(booleanObject(values.get("hidden")));
        }
        return out;
    }

    private AdminQuestionDto extractAdminQuestion(Object body, String key) {
        Object request = value(body, key);
        if (request instanceof AdminQuestionDto) {
            return (AdminQuestionDto) request;
        }
        AdminQuestionDto out = new AdminQuestionDto();
        if (request instanceof Map) {
            Map<?, ?> values = (Map<?, ?>) request;
            out.setQuestionId(textOrNull(values.get("questionId")));
            out.setPosition(textOrNull(values.get("position")));
            out.setDifficulty(textOrNull(values.get("difficulty")));
            out.setContent(textOrNull(values.get("content")));
            out.setSummary(textOrNull(values.get("summary")));
            out.setAnswer(textOrNull(values.get("answer")));
            out.setStatus(textOrNull(values.get("status")));
            out.setSource(textOrNull(values.get("source")));
            out.setReviewStatus(textOrNull(values.get("reviewStatus")));
            out.setContributorHash(textOrNull(values.get("contributorHash")));
            out.setLikes(integerObject(values.get("likes")));
            out.setDrawCount(integerObject(values.get("drawCount")));
        }
        return out;
    }

    private AssessmentQuestionDto extractAssessmentQuestion(Object body) {
        Object request = value(body, "question");
        if (request instanceof AssessmentQuestionDto) {
            return (AssessmentQuestionDto) request;
        }
        AssessmentQuestionDto out = new AssessmentQuestionDto();
        if (request instanceof Map) {
            Map<?, ?> values = (Map<?, ?>) request;
            out.setQuestionId(longObject(values.get("questionId")));
            out.setScaleId(longObject(values.get("scaleId")));
            out.setQuestionText(textOrNull(values.get("questionText")));
            out.setQuestionType(textOrNull(values.get("questionType")));
            out.setDimensionCode(textOrNull(values.get("dimensionCode")));
            out.setSortOrder(integerObject(values.get("sortOrder")));
            Boolean published = booleanObject(values.get("published"));
            if (published != null) {
                out.setPublished(published.booleanValue());
            }
            out.setOptions(extractAssessmentOptions(values.get("options")));
        }
        return out;
    }

    private java.util.List<v620.cc001.base.common.dto.career.AssessmentOptionDto> extractAssessmentOptions(Object value) {
        java.util.List<v620.cc001.base.common.dto.career.AssessmentOptionDto> out =
                new java.util.ArrayList<v620.cc001.base.common.dto.career.AssessmentOptionDto>();
        if (!(value instanceof java.util.List)) {
            return out;
        }
        java.util.List<?> values = (java.util.List<?>) value;
        for (Object item : values) {
            if (item instanceof v620.cc001.base.common.dto.career.AssessmentOptionDto) {
                out.add((v620.cc001.base.common.dto.career.AssessmentOptionDto) item);
            } else if (item instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) item;
                v620.cc001.base.common.dto.career.AssessmentOptionDto option =
                        new v620.cc001.base.common.dto.career.AssessmentOptionDto();
                option.setOptionId(longObject(map.get("optionId")));
                option.setQuestionId(longObject(map.get("questionId")));
                option.setOptionLabel(textOrNull(map.get("optionLabel")));
                option.setOptionText(textOrNull(map.get("optionText")));
                option.setDimensionCode(textOrNull(map.get("dimensionCode")));
                option.setSortOrder(integerObject(map.get("sortOrder")));
                out.add(option);
            }
        }
        return out;
    }

    private ApiResult requireAdmin(Object body) {
        AdminIdentityDto identity = adminWebApi.whoami(extractAdminId(body));
        if (identity == null || !AdminConstants.STATUS_OK.equals(identity.getStatus())) {
            return ApiResult.fail("当前账号没有管理后台权限。", "FORBIDDEN");
        }
        return ApiResult.success(identity);
    }

    private Object value(Object body, String key) {
        if (body instanceof Map) {
            return ((Map<?, ?>) body).get(key);
        }
        return null;
    }

    private int intValue(Object value, int defaultValue) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        String text = textOrNull(value);
        if (text == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    private long longValue(Object value, long defaultValue) {
        Long converted = longObject(value);
        return converted == null ? defaultValue : converted.longValue();
    }

    private Long longObject(Object value) {
        if (value instanceof Number) {
            return Long.valueOf(((Number) value).longValue());
        }
        String text = textOrNull(value);
        if (text == null) {
            return null;
        }
        try {
            return Long.valueOf(text);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Integer integerObject(Object value) {
        if (value instanceof Number) {
            return Integer.valueOf(((Number) value).intValue());
        }
        String text = textOrNull(value);
        if (text == null) {
            return null;
        }
        try {
            return Integer.valueOf(text);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Boolean booleanObject(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        String text = textOrNull(value);
        if (text == null) {
            return null;
        }
        return Boolean.valueOf("true".equalsIgnoreCase(text) || "1".equals(text) || "yes".equalsIgnoreCase(text));
    }

    private byte[] byteArray(Object value) {
        if (value instanceof byte[]) {
            return (byte[]) value;
        }
        if (value instanceof Iterable) {
            List<Byte> bytes = new ArrayList<Byte>();
            for (Object item : (Iterable<?>) value) {
                if (item instanceof Number) {
                    bytes.add(Byte.valueOf(((Number) item).byteValue()));
                }
            }
            byte[] out = new byte[bytes.size()];
            for (int i = 0; i < bytes.size(); i++) {
                out[i] = bytes.get(i).byteValue();
            }
            return out;
        }
        String text = textOrNull(value);
        if (text == null) {
            return null;
        }
        try {
            return Base64.getDecoder().decode(text);
        } catch (IllegalArgumentException ex) {
            return text.getBytes();
        }
    }

    private Map<String, String> stringMap(Object value) {
        Map<String, String> out = new LinkedHashMap<String, String>();
        if (value instanceof Map) {
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
                String key = textOrNull(entry.getKey());
                String item = textOrNull(entry.getValue());
                if (key != null && item != null) {
                    out.put(key, item);
                }
            }
        }
        return out;
    }

    private List<String> stringList(Object value) {
        List<String> out = new ArrayList<String>();
        if (value instanceof Iterable) {
            for (Object item : (Iterable<?>) value) {
                String text = textOrNull(item);
                if (text != null) {
                    out.add(text);
                }
            }
            return out;
        }
        String text = textOrNull(value);
        if (text != null) {
            out.add(text);
        }
        return out;
    }

    private Object firstPresent(Map<?, ?> values, String... keys) {
        if (values == null || keys == null) {
            return null;
        }
        for (String key : keys) {
            Object value = values.get(key);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    private String hasResumeFromStatus(String resumeStatus) {
        if (resumeStatus == null) {
            return null;
        }
        String normalized = resumeStatus.trim().toLowerCase();
        if ("none".equals(normalized) || "no".equals(normalized)) {
            return "no";
        }
        if ("draft".equals(normalized) || "ready".equals(normalized) || "yes".equals(normalized)) {
            return "yes";
        }
        return null;
    }

    private String textOrNull(Object value) {
        if (value == null) {
            return null;
        }
        String normalized = String.valueOf(value).trim();
        return normalized.length() == 0 ? null : normalized;
    }

    private String text(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
