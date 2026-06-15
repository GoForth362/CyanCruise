package v620.cc001.cloud01.app01.webapi;

import kd.bos.openapi.common.custom.annotation.ApiController;
import kd.bos.openapi.common.custom.annotation.ApiMapping;
import kd.bos.openapi.common.custom.annotation.ApiPostMapping;
import kd.bos.openapi.common.custom.annotation.ApiRequestBody;
import kd.bos.openapi.common.custom.annotation.ApiResponseBody;
import v620.cc001.base.common.dto.career.CareerProfileDraftDto;
import v620.cc001.base.common.dto.career.CareerProfileInputsRequest;
import v620.cc001.base.common.dto.career.CareerProfileOnboardingRequest;
import v620.cc001.base.common.dto.career.CareerProfilePreferencesRequest;
import v620.cc001.base.common.dto.career.CareerUserProfileDto;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;
import v620.cc001.cloud01.app01.mservice.CareerProfileApplicationService;
import v620.cc001.cloud01.app01.mservice.IdentityAwareCareerLoopWebApiBoundary;

/**
 * WebAPI boundary for migrated career profile and onboarding behavior.
 */
@ApiController(value = "careerProfileWebApi", desc = "职业画像 API")
@ApiMapping("/cc001/career-profile")
public class CareerProfileWebApi {

    private final CareerProfileApplicationService applicationService;
    private final IdentityAwareCareerLoopWebApiBoundary identityBoundary;

    public CareerProfileWebApi() {
        this(new IdentityAwareCareerLoopWebApiBoundary());
    }

    CareerProfileWebApi(IdentityAwareCareerLoopWebApiBoundary identityBoundary) {
        this(identityBoundary, new CareerProfileApplicationService());
    }

    CareerProfileWebApi(IdentityAwareCareerLoopWebApiBoundary identityBoundary,
                        CareerProfileApplicationService applicationService) {
        this.identityBoundary = identityBoundary;
        this.applicationService = applicationService;
    }

    @ApiPostMapping(value = "/snapshot/get", desc = "获取职业画像快照", methodParamNames = {"userId"})
    public @ApiResponseBody(value = "职业画像快照") UserProfileSnapshot snapshot(
            @ApiRequestBody(value = "用户ID", required = true) String userId) {
        return applicationService.getSnapshot(identityBoundary.requireUser(userId));
    }

    @ApiPostMapping(value = "/draft/get", desc = "get career profile draft", methodParamNames = {"userId"})
    public @ApiResponseBody(value = "career profile draft") CareerProfileDraftDto draft(
            @ApiRequestBody(value = "userId", required = true) String userId) {
        return applicationService.getDraft(identityBoundary.requireUser(userId));
    }

    @ApiPostMapping(value = "/draft/save", desc = "save career profile draft", methodParamNames = {"userId", "request"})
    public @ApiResponseBody(value = "career profile draft") CareerProfileDraftDto saveDraft(
            @ApiRequestBody(value = "userId", required = true) String userId,
            @ApiRequestBody(value = "career profile draft", required = true) CareerProfileDraftDto request) {
        return applicationService.saveDraft(identityBoundary.requireUser(userId), request);
    }

    @ApiPostMapping(value = "/draft/clear", desc = "clear career profile draft", methodParamNames = {"userId"})
    public @ApiResponseBody(value = "career profile draft") CareerProfileDraftDto clearDraft(
            @ApiRequestBody(value = "userId", required = true) String userId) {
        return applicationService.clearDraft(identityBoundary.requireUser(userId));
    }

    @ApiPostMapping(value = "/preferences/save", desc = "保存职业偏好", methodParamNames = {"userId", "request"})
    public @ApiResponseBody(value = "职业画像快照") UserProfileSnapshot savePreferences(
            @ApiRequestBody(value = "用户ID", required = true) String userId,
            @ApiRequestBody(value = "职业偏好", required = true) CareerProfilePreferencesRequest request) {
        return applicationService.savePreferences(userId, request);
    }

    @ApiPostMapping(value = "/onboarding/save", desc = "保存新用户引导信息", methodParamNames = {"userId", "request"})
    public @ApiResponseBody(value = "职业画像快照") UserProfileSnapshot saveOnboarding(
            @ApiRequestBody(value = "用户ID", required = true) String userId,
            @ApiRequestBody(value = "新用户引导信息", required = true) CareerProfileOnboardingRequest request) {
        return applicationService.saveOnboarding(identityBoundary.requireUser(userId), request);
    }

    @ApiPostMapping(value = "/inputs/save", desc = "保存用户补充画像输入", methodParamNames = {"userId", "request"})
    public @ApiResponseBody(value = "统一职业画像") CareerUserProfileDto saveInputs(
            @ApiRequestBody(value = "用户ID", required = true) String userId,
            @ApiRequestBody(value = "用户补充画像输入", required = true) CareerProfileInputsRequest request) {
        return applicationService.saveProfileInputs(userId, request);
    }

    @ApiPostMapping(value = "/profile/get", desc = "获取统一职业画像", methodParamNames = {"userId"})
    public @ApiResponseBody(value = "统一职业画像") CareerUserProfileDto profile(
            @ApiRequestBody(value = "用户ID", required = true) String userId) {
        return applicationService.getProfile(userId);
    }

    @ApiPostMapping(value = "/profile/refresh", desc = "刷新统一职业画像", methodParamNames = {"userId"})
    public @ApiResponseBody(value = "统一职业画像") CareerUserProfileDto refresh(
            @ApiRequestBody(value = "用户ID", required = true) String userId) {
        return applicationService.refreshProfile(userId);
    }
}
