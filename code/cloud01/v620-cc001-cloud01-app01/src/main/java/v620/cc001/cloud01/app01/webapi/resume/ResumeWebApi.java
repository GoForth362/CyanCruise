package v620.cc001.cloud01.app01.webapi.resume;

import v620.cc001.cloud01.app01.mservice.auth.impl.IdentityAwareCyanCruiseWebApiBoundary;
import kd.bos.openapi.common.custom.annotation.ApiController;
import kd.bos.openapi.common.custom.annotation.ApiMapping;
import kd.bos.openapi.common.custom.annotation.ApiPostMapping;
import kd.bos.openapi.common.custom.annotation.ApiRequestBody;
import kd.bos.openapi.common.custom.annotation.ApiResponseBody;
import v620.cc001.base.common.dto.career.ResumeCreateRequest;
import v620.cc001.base.common.dto.career.ResumeRecordDto;
import v620.cc001.base.common.dto.career.ResumeUpdateRequest;
import v620.cc001.cloud01.app01.mservice.auth.impl.IdentityAwareCyanCruiseWebApiBoundary;
import v620.cc001.cloud01.app01.mservice.application.ResumeApplicationService;

import java.util.List;

/**
 * WebAPI boundary for migrated resume record management.
 */
@ApiController(value = "resumeWebApi", desc = "简历基础 API")
@ApiMapping("/cc001/resume")
public class ResumeWebApi {

    private final ResumeApplicationService applicationService;
    private final IdentityAwareCyanCruiseWebApiBoundary identityBoundary;

    public ResumeWebApi() {
        this(new ResumeApplicationService());
    }

    public ResumeWebApi(ResumeApplicationService applicationService) {
        this(applicationService, new IdentityAwareCyanCruiseWebApiBoundary());
    }

    public ResumeWebApi(ResumeApplicationService applicationService,
                 IdentityAwareCyanCruiseWebApiBoundary identityBoundary) {
        this.applicationService = applicationService;
        this.identityBoundary = identityBoundary;
    }

    @ApiPostMapping(value = "/create", desc = "创建简历记录", methodParamNames = {"userId", "request"})
    public @ApiResponseBody(value = "简历记录") ResumeRecordDto create(
            @ApiRequestBody(value = "用户ID", required = true) String userId,
            @ApiRequestBody(value = "简历创建请求", required = true) ResumeCreateRequest request) {
        return applicationService.create(identityBoundary.requireUser(userId), request);
    }

    @ApiPostMapping(value = "/get", desc = "获取简历详情", methodParamNames = {"userId", "resumeId"})
    public @ApiResponseBody(value = "简历记录") ResumeRecordDto get(
            @ApiRequestBody(value = "用户ID", required = true) String userId,
            @ApiRequestBody(value = "简历ID", required = true) Long resumeId) {
        return applicationService.get(userId, resumeId);
    }

    @ApiPostMapping(value = "/list", desc = "获取用户简历列表", methodParamNames = {"userId"})
    public @ApiResponseBody(value = "简历记录列表") List<ResumeRecordDto> list(
            @ApiRequestBody(value = "用户ID", required = true) String userId) {
        return applicationService.listByUser(identityBoundary.requireUser(userId));
    }

    @ApiPostMapping(value = "/update", desc = "更新简历元数据", methodParamNames = {"userId", "resumeId", "request"})
    public @ApiResponseBody(value = "简历记录") ResumeRecordDto update(
            @ApiRequestBody(value = "用户ID", required = true) String userId,
            @ApiRequestBody(value = "简历ID", required = true) Long resumeId,
            @ApiRequestBody(value = "简历更新请求", required = true) ResumeUpdateRequest request) {
        return applicationService.update(userId, resumeId, request);
    }

    @ApiPostMapping(value = "/delete", desc = "删除简历记录", methodParamNames = {"userId", "resumeId"})
    public @ApiResponseBody(value = "删除结果") String delete(
            @ApiRequestBody(value = "用户ID", required = true) String userId,
            @ApiRequestBody(value = "简历ID", required = true) Long resumeId) {
        applicationService.delete(userId, resumeId);
        return "OK";
    }
}
