package v620.cc001.cloud01.app01.webapi;

import kd.bos.bill.IBillWebApiPlugin;
import kd.bos.entity.api.ApiResult;
import kd.bos.openapi.common.custom.annotation.ApiController;
import kd.bos.openapi.common.custom.annotation.ApiMapping;
import kd.bos.openapi.common.custom.annotation.ApiPostMapping;
import kd.bos.openapi.common.custom.annotation.ApiRequestBody;
import kd.bos.openapi.common.custom.annotation.ApiResponseBody;
import kd.bos.openapi.common.result.CustomApiResult;
import v620.cc001.base.common.dto.career.CareerProfileOnboardingRequest;
import v620.cc001.cloud01.app01.mservice.IdentityBoundaryException;

import java.util.Map;

/**
 * Cosmic Custom Web API entry that routes platform kapi calls to CareerLoop WebAPI contracts.
 */
@ApiController(value = "careerLoopCustomWebApiPlugin", desc = "CareerLoop custom WebAPI router")
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

    public CareerLoopCustomWebApiPlugin() {
        this(new CareerLoopIdentityWebApi(), new CareerProfileWebApi(), new CareerAgentWebApi(),
                new ResumeWebApi(), new CareerPlanWebApi(), new InterviewWebApi());
    }

    CareerLoopCustomWebApiPlugin(CareerLoopIdentityWebApi identityWebApi,
                                 CareerProfileWebApi profileWebApi,
                                 CareerAgentWebApi agentWebApi) {
        this(identityWebApi, profileWebApi, agentWebApi,
                new ResumeWebApi(), new CareerPlanWebApi(), new InterviewWebApi());
    }

    CareerLoopCustomWebApiPlugin(CareerLoopIdentityWebApi identityWebApi,
                                 CareerProfileWebApi profileWebApi,
                                 CareerAgentWebApi agentWebApi,
                                 ResumeWebApi resumeWebApi,
                                 CareerPlanWebApi planWebApi,
                                 InterviewWebApi interviewWebApi) {
        this.identityWebApi = identityWebApi;
        this.profileWebApi = profileWebApi;
        this.agentWebApi = agentWebApi;
        this.resumeWebApi = resumeWebApi;
        this.planWebApi = planWebApi;
        this.interviewWebApi = interviewWebApi;
    }

    @ApiPostMapping(value = "/route", desc = "Route CareerLoop custom WebAPI call", methodParamNames = {"params"})
    public @ApiResponseBody(value = "CareerLoop custom WebAPI result") CustomApiResult<Object> route(
            @ApiRequestBody(value = "CareerLoop custom WebAPI params", required = true) Map<String, Object> params) {
        ApiResult result = doCustomService(params);
        if (result.getSuccess()) {
            return CustomApiResult.success(result.getData());
        }
        return CustomApiResult.fail(result.getErrorCode(), result.getMessage());
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
            if ("/cc001/career-profile/onboarding/save".equals(path)) {
                return ApiResult.success(profileWebApi.saveOnboarding(
                        extractUserId(body), extractOnboardingRequest(body)));
            }
            if ("/cc001/career-agent/today/get".equals(path)) {
                return ApiResult.success(agentWebApi.todayByUserId(extractUserId(body)));
            }
            if ("/cc001/resume/list".equals(path)) {
                return ApiResult.success(resumeWebApi.list(extractUserId(body)));
            }
            if ("/cc001/career-plan/summary".equals(path)) {
                return ApiResult.success(planWebApi.summary(extractUserId(body)));
            }
            if ("/cc001/interview/list".equals(path)) {
                return ApiResult.success(interviewWebApi.list(extractUserId(body)));
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
        onboarding.setStage(textOrNull(values.get("stage")));
        onboarding.setPainPoint(textOrNull(firstPresent(values, "painPoint", "preference")));
        onboarding.setHasResume(textOrNull(firstPresent(values, "hasResume")));
        onboarding.setResumeStatus(textOrNull(values.get("resumeStatus")));
        onboarding.setTimeline(textOrNull(values.get("timeline")));
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
