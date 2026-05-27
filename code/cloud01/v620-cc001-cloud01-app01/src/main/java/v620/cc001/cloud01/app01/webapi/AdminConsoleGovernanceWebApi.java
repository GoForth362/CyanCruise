package v620.cc001.cloud01.app01.webapi;

import kd.bos.openapi.common.custom.annotation.ApiController;
import kd.bos.openapi.common.custom.annotation.ApiMapping;
import kd.bos.openapi.common.custom.annotation.ApiPostMapping;
import kd.bos.openapi.common.custom.annotation.ApiRequestBody;
import kd.bos.openapi.common.custom.annotation.ApiResponseBody;
import v620.cc001.base.common.dto.career.AdminAnalyticsSummaryDto;
import v620.cc001.base.common.dto.career.AdminAuditLogDto;
import v620.cc001.base.common.dto.career.AdminBroadcastRequest;
import v620.cc001.base.common.dto.career.AdminBroadcastResult;
import v620.cc001.base.common.dto.career.AdminCareerNodeDto;
import v620.cc001.base.common.dto.career.AdminCareerPathDto;
import v620.cc001.base.common.dto.career.AdminContentItemDto;
import v620.cc001.base.common.dto.career.AdminIdentityDto;
import v620.cc001.base.common.dto.career.AdminOperationResult;
import v620.cc001.base.common.dto.career.AdminOrgDashboardDto;
import v620.cc001.base.common.dto.career.AdminOrganizationDto;
import v620.cc001.base.common.dto.career.AdminPageResult;
import v620.cc001.base.common.dto.career.AdminQuestionContributionRequest;
import v620.cc001.base.common.dto.career.AdminQuestionDto;
import v620.cc001.base.common.dto.career.AdminStudentRowDto;
import v620.cc001.base.common.dto.career.AdminUserDto;
import v620.cc001.cloud01.app01.mservice.AdminConsoleGovernanceApplicationService;

import java.util.List;

@ApiController(value = "adminConsoleGovernanceWebApi", desc = "CareerLoop admin console governance API")
@ApiMapping("/cc001/admin")
public class AdminConsoleGovernanceWebApi {

    private final AdminConsoleGovernanceApplicationService applicationService;

    public AdminConsoleGovernanceWebApi() {
        this(new AdminConsoleGovernanceApplicationService());
    }

    AdminConsoleGovernanceWebApi(AdminConsoleGovernanceApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @ApiPostMapping(value = "/whoami", desc = "Check admin identity", methodParamNames = {"adminId"})
    public @ApiResponseBody(value = "Admin identity") AdminIdentityDto whoami(
            @ApiRequestBody(value = "adminId", required = true) String adminId) {
        return applicationService.whoami(adminId);
    }

    @ApiPostMapping(value = "/organizations/list", desc = "List organizations", methodParamNames = {"adminId"})
    public @ApiResponseBody(value = "Organizations") List<AdminOrganizationDto> organizations(
            @ApiRequestBody(value = "adminId", required = true) String adminId) {
        return applicationService.listOrganizations(adminId);
    }

    @ApiPostMapping(value = "/organizations/save", desc = "Save organization", methodParamNames = {"adminId", "organization"})
    public @ApiResponseBody(value = "Organization") AdminOrganizationDto saveOrganization(
            @ApiRequestBody(value = "adminId", required = true) String adminId,
            @ApiRequestBody(value = "organization", required = true) AdminOrganizationDto organization) {
        return applicationService.saveOrganization(adminId, organization);
    }

    @ApiPostMapping(value = "/organizations/dashboard", desc = "Organization dashboard", methodParamNames = {"adminId", "orgId"})
    public @ApiResponseBody(value = "Dashboard") AdminOrgDashboardDto dashboard(
            @ApiRequestBody(value = "adminId", required = true) String adminId,
            @ApiRequestBody(value = "orgId", required = true) String orgId) {
        return applicationService.organizationDashboard(adminId, orgId);
    }

    @ApiPostMapping(value = "/organizations/students", desc = "Organization students", methodParamNames = {"adminId", "orgId"})
    public @ApiResponseBody(value = "Students") List<AdminStudentRowDto> students(
            @ApiRequestBody(value = "adminId", required = true) String adminId,
            @ApiRequestBody(value = "orgId", required = true) String orgId) {
        return applicationService.organizationStudents(adminId, orgId);
    }

    @ApiPostMapping(value = "/users/list", desc = "List users", methodParamNames = {"adminId", "page", "size", "keyword"})
    public @ApiResponseBody(value = "Users") AdminPageResult<AdminUserDto> users(
            @ApiRequestBody(value = "adminId", required = true) String adminId,
            @ApiRequestBody(value = "page", required = false) int page,
            @ApiRequestBody(value = "size", required = false) int size,
            @ApiRequestBody(value = "keyword", required = false) String keyword) {
        return applicationService.listUsers(adminId, page, size, keyword);
    }

    @ApiPostMapping(value = "/users/detail", desc = "User detail", methodParamNames = {"adminId", "userId"})
    public @ApiResponseBody(value = "User") AdminUserDto userDetail(
            @ApiRequestBody(value = "adminId", required = true) String adminId,
            @ApiRequestBody(value = "userId", required = true) String userId) {
        return applicationService.userDetail(adminId, userId);
    }

    @ApiPostMapping(value = "/users/ban", desc = "Ban user", methodParamNames = {"adminId", "userId", "reason"})
    public @ApiResponseBody(value = "Ban result") AdminOperationResult ban(
            @ApiRequestBody(value = "adminId", required = true) String adminId,
            @ApiRequestBody(value = "userId", required = true) String userId,
            @ApiRequestBody(value = "reason", required = false) String reason) {
        return applicationService.banUser(adminId, userId, reason);
    }

    @ApiPostMapping(value = "/users/unban", desc = "Unban user", methodParamNames = {"adminId", "userId"})
    public @ApiResponseBody(value = "Unban result") AdminOperationResult unban(
            @ApiRequestBody(value = "adminId", required = true) String adminId,
            @ApiRequestBody(value = "userId", required = true) String userId) {
        return applicationService.unbanUser(adminId, userId);
    }

    @ApiPostMapping(value = "/career-paths/list", desc = "List career paths", methodParamNames = {"adminId"})
    public @ApiResponseBody(value = "Paths") List<AdminCareerPathDto> paths(
            @ApiRequestBody(value = "adminId", required = true) String adminId) {
        return applicationService.listCareerPaths(adminId);
    }

    @ApiPostMapping(value = "/career-paths/save", desc = "Save career path", methodParamNames = {"adminId", "path"})
    public @ApiResponseBody(value = "Path") AdminCareerPathDto savePath(
            @ApiRequestBody(value = "adminId", required = true) String adminId,
            @ApiRequestBody(value = "path", required = true) AdminCareerPathDto path) {
        return applicationService.saveCareerPath(adminId, path);
    }

    @ApiPostMapping(value = "/career-paths/nodes/list", desc = "List career nodes", methodParamNames = {"adminId", "pathId"})
    public @ApiResponseBody(value = "Nodes") List<AdminCareerNodeDto> nodes(
            @ApiRequestBody(value = "adminId", required = true) String adminId,
            @ApiRequestBody(value = "pathId", required = true) String pathId) {
        return applicationService.listCareerNodes(adminId, pathId);
    }

    @ApiPostMapping(value = "/questions/list", desc = "List admin questions", methodParamNames = {"adminId", "source", "reviewStatus"})
    public @ApiResponseBody(value = "Questions") List<AdminQuestionDto> questions(
            @ApiRequestBody(value = "adminId", required = true) String adminId,
            @ApiRequestBody(value = "source", required = false) String source,
            @ApiRequestBody(value = "reviewStatus", required = false) String reviewStatus) {
        return applicationService.listQuestions(adminId, source, reviewStatus);
    }

    @ApiPostMapping(value = "/questions/update", desc = "Update question", methodParamNames = {"adminId", "questionId", "patch"})
    public @ApiResponseBody(value = "Question") AdminQuestionDto updateQuestion(
            @ApiRequestBody(value = "adminId", required = true) String adminId,
            @ApiRequestBody(value = "questionId", required = true) String questionId,
            @ApiRequestBody(value = "patch", required = true) AdminQuestionDto patch) {
        return applicationService.updateQuestion(adminId, questionId, patch);
    }

    @ApiPostMapping(value = "/questions/approve", desc = "Approve question", methodParamNames = {"adminId", "questionId"})
    public @ApiResponseBody(value = "Question") AdminQuestionDto approveQuestion(
            @ApiRequestBody(value = "adminId", required = true) String adminId,
            @ApiRequestBody(value = "questionId", required = true) String questionId) {
        return applicationService.approveQuestion(adminId, questionId);
    }

    @ApiPostMapping(value = "/questions/reject", desc = "Reject question", methodParamNames = {"adminId", "questionId"})
    public @ApiResponseBody(value = "Question") AdminQuestionDto rejectQuestion(
            @ApiRequestBody(value = "adminId", required = true) String adminId,
            @ApiRequestBody(value = "questionId", required = true) String questionId) {
        return applicationService.rejectQuestion(adminId, questionId);
    }

    @ApiPostMapping(value = "/questions/contribute", desc = "Contribute public question", methodParamNames = {"request"})
    public @ApiResponseBody(value = "Question") AdminQuestionDto contributeQuestion(
            @ApiRequestBody(value = "request", required = true) AdminQuestionContributionRequest request) {
        return applicationService.contributeQuestion(request);
    }

    @ApiPostMapping(value = "/content/list", desc = "List content", methodParamNames = {"adminId", "type"})
    public @ApiResponseBody(value = "Content") List<AdminContentItemDto> content(
            @ApiRequestBody(value = "adminId", required = true) String adminId,
            @ApiRequestBody(value = "type", required = false) String type) {
        return applicationService.listContent(adminId, type);
    }

    @ApiPostMapping(value = "/content/save", desc = "Save content", methodParamNames = {"adminId", "content"})
    public @ApiResponseBody(value = "Content") AdminContentItemDto saveContent(
            @ApiRequestBody(value = "adminId", required = true) String adminId,
            @ApiRequestBody(value = "content", required = true) AdminContentItemDto content) {
        return applicationService.saveContent(adminId, content);
    }

    @ApiPostMapping(value = "/content/pin", desc = "Toggle content pinned", methodParamNames = {"adminId", "contentId"})
    public @ApiResponseBody(value = "Content") AdminContentItemDto pinContent(
            @ApiRequestBody(value = "adminId", required = true) String adminId,
            @ApiRequestBody(value = "contentId", required = true) String contentId) {
        return applicationService.toggleContentPinned(adminId, contentId);
    }

    @ApiPostMapping(value = "/content/hide", desc = "Toggle content hidden", methodParamNames = {"adminId", "contentId"})
    public @ApiResponseBody(value = "Content") AdminContentItemDto hideContent(
            @ApiRequestBody(value = "adminId", required = true) String adminId,
            @ApiRequestBody(value = "contentId", required = true) String contentId) {
        return applicationService.toggleContentHidden(adminId, contentId);
    }

    @ApiPostMapping(value = "/broadcast", desc = "Admin broadcast", methodParamNames = {"adminId", "request"})
    public @ApiResponseBody(value = "Broadcast result") AdminBroadcastResult broadcast(
            @ApiRequestBody(value = "adminId", required = true) String adminId,
            @ApiRequestBody(value = "request", required = true) AdminBroadcastRequest request) {
        return applicationService.broadcast(adminId, request);
    }

    @ApiPostMapping(value = "/analytics/summary", desc = "Analytics summary", methodParamNames = {"adminId"})
    public @ApiResponseBody(value = "Analytics") AdminAnalyticsSummaryDto analytics(
            @ApiRequestBody(value = "adminId", required = true) String adminId) {
        return applicationService.analyticsSummary(adminId);
    }

    @ApiPostMapping(value = "/audit-log/list", desc = "Audit log", methodParamNames = {"adminId", "page", "size"})
    public @ApiResponseBody(value = "Audit logs") AdminPageResult<AdminAuditLogDto> auditLogs(
            @ApiRequestBody(value = "adminId", required = true) String adminId,
            @ApiRequestBody(value = "page", required = false) int page,
            @ApiRequestBody(value = "size", required = false) int size) {
        return applicationService.auditLogs(adminId, page, size);
    }
}
