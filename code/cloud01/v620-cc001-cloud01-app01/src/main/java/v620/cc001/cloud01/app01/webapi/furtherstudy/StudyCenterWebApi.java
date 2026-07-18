package v620.cc001.cloud01.app01.webapi.furtherstudy;

import kd.bos.openapi.common.custom.annotation.ApiController;
import kd.bos.openapi.common.custom.annotation.ApiMapping;
import kd.bos.openapi.common.custom.annotation.ApiPostMapping;
import kd.bos.openapi.common.custom.annotation.ApiRequestBody;
import kd.bos.openapi.common.custom.annotation.ApiResponseBody;
import v620.cc001.base.common.dto.career.CareerResourceFeedDto;
import v620.cc001.base.common.dto.career.CareerPlanSummaryDto;
import v620.cc001.base.common.dto.career.CareerDailyPlanDto;
import v620.cc001.base.common.dto.career.CareerDailyTaskUpdateRequest;
import v620.cc001.base.common.dto.career.AdminContentItemDto;
import java.util.List;
import v620.cc001.base.common.dto.furtherstudy.StudyCenterInsightDto;
import v620.cc001.base.common.dto.furtherstudy.StudyCenterSelectionDto;
import v620.cc001.base.common.dto.furtherstudy.StudyPlanningMaterialDto;
import v620.cc001.base.common.dto.furtherstudy.StudyPlanningMaterialUploadRequest;
import v620.cc001.base.common.dto.furtherstudy.StudyPlanningMaterialDeleteResult;
import v620.cc001.cloud01.app01.mservice.application.StudyCenterApplicationService;
import v620.cc001.cloud01.app01.mservice.application.StudyPlanApplicationService;
import v620.cc001.cloud01.app01.mservice.application.StudyPlanningMaterialApplicationService;
import v620.cc001.cloud01.app01.mservice.auth.impl.IdentityAwareCyanCruiseWebApiBoundary;

/** API for study-center data, intentionally independent from career-employment. */
@ApiController(value = "studyCenterWebApi", desc = "青途启航升学中心 API")
@ApiMapping("/cc001/study-center")
public class StudyCenterWebApi {
    private final StudyCenterApplicationService applicationService;
    private final StudyPlanApplicationService planService;
    private final StudyPlanningMaterialApplicationService materialService;
    private final IdentityAwareCyanCruiseWebApiBoundary identityBoundary;

    public StudyCenterWebApi() { this(new StudyCenterApplicationService(), new StudyPlanApplicationService(),
            new StudyPlanningMaterialApplicationService(), new IdentityAwareCyanCruiseWebApiBoundary()); }
    public StudyCenterWebApi(StudyCenterApplicationService applicationService, IdentityAwareCyanCruiseWebApiBoundary identityBoundary) {
        this(applicationService, new StudyPlanApplicationService(),
                new StudyPlanningMaterialApplicationService(), identityBoundary);
    }
    public StudyCenterWebApi(StudyCenterApplicationService applicationService, StudyPlanApplicationService planService,
                             IdentityAwareCyanCruiseWebApiBoundary identityBoundary) {
        this(applicationService, planService, new StudyPlanningMaterialApplicationService(), identityBoundary);
    }
    public StudyCenterWebApi(StudyCenterApplicationService applicationService, StudyPlanApplicationService planService,
                             StudyPlanningMaterialApplicationService materialService,
                             IdentityAwareCyanCruiseWebApiBoundary identityBoundary) {
        this.applicationService = applicationService; this.planService = planService;
        this.materialService = materialService; this.identityBoundary = identityBoundary;
    }
    @ApiPostMapping(value = "/selection/get", desc = "获取升学方向", methodParamNames = {"userId"})
    public @ApiResponseBody(value = "升学方向") StudyCenterSelectionDto selection(@ApiRequestBody(value = "userId", required = true) String userId) {
        return applicationService.getSelection(identityBoundary.requireUser(userId));
    }
    @ApiPostMapping(value = "/selection/save", desc = "保存升学方向", methodParamNames = {"userId", "direction", "targetSchool"})
    public @ApiResponseBody(value = "升学方向") StudyCenterSelectionDto saveSelection(
            @ApiRequestBody(value = "userId", required = true) String userId,
            @ApiRequestBody(value = "direction", required = true) String direction,
            @ApiRequestBody(value = "targetSchool", required = false) String targetSchool) {
        return applicationService.saveSelection(identityBoundary.requireUser(userId), direction, targetSchool);
    }
    @ApiPostMapping(value = "/insight/get", desc = "获取升学洞察", methodParamNames = {"userId"})
    public @ApiResponseBody(value = "升学洞察") StudyCenterInsightDto insight(@ApiRequestBody(value = "userId", required = true) String userId) {
        return applicationService.getInsight(identityBoundary.requireUser(userId));
    }
    @ApiPostMapping(value = "/resources/list", desc = "获取升学资讯", methodParamNames = {})
    public @ApiResponseBody(value = "升学资讯") CareerResourceFeedDto resources() { return applicationService.resources(); }
    @ApiPostMapping(value = "/plan/summary", desc = "获取升学规划摘要", methodParamNames = {"userId"})
    public @ApiResponseBody(value = "升学规划摘要") CareerPlanSummaryDto planSummary(@ApiRequestBody(value = "userId", required = true) String userId) {
        return planService.getSummary(identityBoundary.requireUser(userId));
    }
    @ApiPostMapping(value = "/plan/ensure", desc = "确保已有升学规划", methodParamNames = {"userId"})
    public @ApiResponseBody(value = "升学规划摘要") CareerPlanSummaryDto ensurePlan(@ApiRequestBody(value = "userId", required = true) String userId) {
        return planService.ensurePlan(identityBoundary.requireUser(userId));
    }
    @ApiPostMapping(value = "/plan/generate", desc = "按所选方向生成升学规划", methodParamNames = {"userId"})
    public @ApiResponseBody(value = "升学规划摘要") CareerPlanSummaryDto generatePlan(@ApiRequestBody(value = "userId", required = true) String userId) {
        return planService.generateAgentPlan(identityBoundary.requireUser(userId));
    }
    @ApiPostMapping(value = "/daily/get", desc = "获取升学今日行动", methodParamNames = {"userId"})
    public @ApiResponseBody(value = "升学今日行动") CareerDailyPlanDto daily(@ApiRequestBody(value = "userId", required = true) String userId) {
        return planService.getToday(identityBoundary.requireUser(userId));
    }
    @ApiPostMapping(value = "/daily/task/update", desc = "更新升学每日任务", methodParamNames = {"userId", "request"})
    public @ApiResponseBody(value = "更新后的升学每日计划") CareerDailyPlanDto updateDaily(
            @ApiRequestBody(value = "userId", required = true) String userId,
            @ApiRequestBody(value = "任务状态", required = true) CareerDailyTaskUpdateRequest request) {
        return planService.updateToday(identityBoundary.requireUser(userId), request);
    }
    @ApiPostMapping(value = "/materials/upload", desc = "上传考研规划资料", methodParamNames = {"userId", "request"})
    public @ApiResponseBody(value = "考研规划资料") StudyPlanningMaterialDto uploadMaterial(
            @ApiRequestBody(value = "userId", required = true) String userId,
            @ApiRequestBody(value = "资料", required = true) StudyPlanningMaterialUploadRequest request) {
        return materialService.upload(identityBoundary.requireUser(userId), request);
    }
    @ApiPostMapping(value = "/materials/list", desc = "获取升学规划资料", methodParamNames = {"userId", "direction"})
    public @ApiResponseBody(value = "升学规划资料列表") List<StudyPlanningMaterialDto> materials(
            @ApiRequestBody(value = "userId", required = true) String userId,
            @ApiRequestBody(value = "direction", required = true) String direction) {
        return materialService.list(identityBoundary.requireUser(userId), direction);
    }
    @ApiPostMapping(value = "/materials/delete", desc = "删除升学规划资料", methodParamNames = {"userId", "materialId"})
    public @ApiResponseBody(value = "删除结果") StudyPlanningMaterialDeleteResult deleteMaterial(
            @ApiRequestBody(value = "userId", required = true) String userId,
            @ApiRequestBody(value = "materialId", required = true) String materialId) {
        return materialService.delete(identityBoundary.requireUser(userId), materialId);
    }
    @ApiPostMapping(value = "/admin/resources/list", desc = "管理升学资讯", methodParamNames = {"adminId"})
    public @ApiResponseBody(value = "升学资讯列表") List<AdminContentItemDto> adminResources(@ApiRequestBody(value = "adminId", required = true) String adminId) { return applicationService.listResourcesForAdmin(adminId); }
    @ApiPostMapping(value = "/admin/resources/save", desc = "保存升学资讯", methodParamNames = {"adminId", "content"})
    public @ApiResponseBody(value = "升学资讯") AdminContentItemDto saveResource(@ApiRequestBody(value = "adminId", required = true) String adminId, @ApiRequestBody(value = "content", required = true) AdminContentItemDto content) { return applicationService.saveResourceForAdmin(adminId, content); }
    @ApiPostMapping(value = "/admin/resources/pin", desc = "切换升学资讯置顶", methodParamNames = {"adminId", "contentId"})
    public @ApiResponseBody(value = "升学资讯") AdminContentItemDto pinResource(@ApiRequestBody(value = "adminId", required = true) String adminId, @ApiRequestBody(value = "contentId", required = true) String contentId) { return applicationService.toggleResourcePinned(adminId, contentId); }
    @ApiPostMapping(value = "/admin/resources/hide", desc = "切换升学资讯发布状态", methodParamNames = {"adminId", "contentId"})
    public @ApiResponseBody(value = "升学资讯") AdminContentItemDto hideResource(@ApiRequestBody(value = "adminId", required = true) String adminId, @ApiRequestBody(value = "contentId", required = true) String contentId) { return applicationService.toggleResourceHidden(adminId, contentId); }
    @ApiPostMapping(value = "/admin/resources/delete", desc = "删除升学资讯", methodParamNames = {"adminId", "contentId"})
    public @ApiResponseBody(value = "删除结果") boolean deleteResource(@ApiRequestBody(value = "adminId", required = true) String adminId, @ApiRequestBody(value = "contentId", required = true) String contentId) { return applicationService.deleteResource(adminId, contentId); }
}
