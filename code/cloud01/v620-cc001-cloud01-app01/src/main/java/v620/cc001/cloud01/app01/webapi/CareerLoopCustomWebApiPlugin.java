package v620.cc001.cloud01.app01.webapi;

import kd.bos.bill.IBillWebApiPlugin;
import kd.bos.entity.api.ApiResult;
import kd.bos.openapi.common.custom.annotation.ApiController;
import kd.bos.openapi.common.custom.annotation.ApiMapping;
import kd.bos.openapi.common.custom.annotation.ApiPostMapping;
import kd.bos.openapi.common.custom.annotation.ApiRequestBody;
import kd.bos.openapi.common.custom.annotation.ApiResponseBody;
import kd.bos.openapi.common.result.CustomApiResult;
import v620.cc001.base.common.dto.career.AdminBroadcastRequest;
import v620.cc001.base.common.dto.career.AssessmentScaleDto;
import v620.cc001.base.common.dto.career.AssessmentSubmitRequest;
import v620.cc001.base.common.dto.career.AssistantChatRequest;
import v620.cc001.base.common.dto.career.CareerProfileDraftDto;
import v620.cc001.base.common.dto.career.CareerProfileOnboardingRequest;
import v620.cc001.base.common.dto.career.FileUploadRequest;
import v620.cc001.base.common.dto.career.InterviewStartRequest;
import v620.cc001.base.common.dto.career.ResumeCreateRequest;
import v620.cc001.base.common.dto.career.ResumeDiagnosisRequest;
import v620.cc001.base.common.dto.career.SubscriptionGrantRequest;
import v620.cc001.base.common.dto.career.SubscriptionSendRequest;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;
import v620.cc001.cloud01.app01.mservice.IdentityBoundaryException;

import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Cosmic Custom Web API entry that routes platform kapi calls to CyanCruise WebAPI contracts.
 */
@ApiController(value = "careerLoopCustomWebApiPlugin", desc = "CyanCruise custom WebAPI router")
@ApiMapping("/cc001/careerloop")
public class CareerLoopCustomWebApiPlugin implements IBillWebApiPlugin {

    public static final String PARAM_PATH = "path";
    public static final String PARAM_BODY = "body";

    private final CareerLoopIdentityWebApi identityWebApi;
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

    public CareerLoopCustomWebApiPlugin() {
        this(new CareerLoopIdentityWebApi(), new CareerProfileWebApi(), new CareerAgentWebApi(),
                new ResumeWebApi(), new CareerPlanWebApi(), new InterviewWebApi(),
                new AssistantChatWebApi(), new EmploymentInsightsResourcesWebApi(),
                new NotificationsSubscriptionsWebApi(), new AdminConsoleGovernanceWebApi(),
                new FileUploadPreviewWebApi(), new ResumeDiagnosisWebApi(), new AssessmentWebApi());
    }

    CareerLoopCustomWebApiPlugin(CareerLoopIdentityWebApi identityWebApi,
                                 CareerProfileWebApi profileWebApi,
                                 CareerAgentWebApi agentWebApi) {
        this(identityWebApi, profileWebApi, agentWebApi,
                new ResumeWebApi(), new CareerPlanWebApi(), new InterviewWebApi(),
                new AssistantChatWebApi(), new EmploymentInsightsResourcesWebApi(),
                new NotificationsSubscriptionsWebApi(), new AdminConsoleGovernanceWebApi(),
                new FileUploadPreviewWebApi(), new ResumeDiagnosisWebApi(), new AssessmentWebApi());
    }

    CareerLoopCustomWebApiPlugin(CareerLoopIdentityWebApi identityWebApi,
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
    }

    @ApiPostMapping(value = "/route", desc = "Route CyanCruise custom WebAPI call", methodParamNames = {"params"})
    public @ApiResponseBody(value = "CyanCruise custom WebAPI result") CustomApiResult<Object> route(
            @ApiRequestBody(value = "CyanCruise custom WebAPI params", required = true) Map<String, Object> params) {
        ApiResult result = doCustomService(params);
        if (result.getSuccess()) {
            return CustomApiResult.success(result.getData());
        }
        return CustomApiResult.fail(result.getErrorCode(), result.getMessage());
    }

    @Override
    public ApiResult doCustomService(Map<String, Object> params) {
        if (params == null) {
            return ApiResult.fail("CyanCruise custom WebAPI params are required");
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
                return ApiResult.success(assessmentWebApi.questions(longObject(value(body, "scaleId"))));
            }
            if ("/cc001/assessment/submit".equals(path)) {
                return ApiResult.success(assessmentWebApi.submit(
                        extractUserId(body), extractAssessmentScale(body), extractAssessmentSubmitRequest(body)));
            }
            if ("/cc001/assessment/records".equals(path)) {
                return ApiResult.success(assessmentWebApi.records(extractUserId(body)));
            }
            if ("/cc001/assessment/record/get".equals(path)) {
                return ApiResult.success(assessmentWebApi.record(
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
            if ("/cc001/admin/questions/list".equals(path)) {
                return ApiResult.success(adminWebApi.questions(
                        extractAdminId(body), textOrNull(value(body, "source")),
                        textOrNull(value(body, "reviewStatus"))));
            }
            if ("/cc001/admin/content/list".equals(path)) {
                return ApiResult.success(adminWebApi.content(
                        extractAdminId(body), textOrNull(value(body, "type"))));
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
            if ("/cc001/resume-diagnosis/keywords/status".equals(path)) {
                return ApiResult.success(resumeDiagnosisWebApi.keywordStatus(
                        extractUserId(body), longObject(value(body, "resumeId"))));
            }
            return ApiResult.fail("Unsupported CyanCruise custom WebAPI path: " + path);
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

    private String extractOptionalUserId(Object body) {
        if (body instanceof Map) {
            return textOrNull(((Map<?, ?>) body).get("userId"));
        }
        return textOrNull(body);
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
        onboarding.setExperience(textOrNull(firstPresent(values, "experience", "strengths")));
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
        }
        return out;
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
            out.setTitle(textOrNull(values.get("title")));
            out.setContent(textOrNull(values.get("content")));
            out.setLink(textOrNull(values.get("link")));
        }
        return out;
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
