package v620.cc001.cloud01.app01.webapi.admin;

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
import v620.cc001.base.common.dto.career.AdminConstants;
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
import v620.cc001.cloud01.app01.mservice.application.AdminConsoleGovernanceApplicationService;
import v620.cc001.cloud01.app01.mservice.auth.impl.IdentityAwareCyanCruiseWebApiBoundary;
import v620.cc001.cloud01.app01.mservice.auth.IdentityBoundaryException;

import java.util.Collections;
import java.util.List;

@ApiController(value = "adminConsoleGovernanceWebApi", desc = "CyanCruise admin console governance API")
@ApiMapping("/cc001/admin")
public class AdminConsoleGovernanceWebApi {

    private final AdminConsoleGovernanceApplicationService applicationService;
    private final IdentityAwareCyanCruiseWebApiBoundary identityBoundary;

    public AdminConsoleGovernanceWebApi() {
        this(new AdminConsoleGovernanceApplicationService());
    }

    public AdminConsoleGovernanceWebApi(AdminConsoleGovernanceApplicationService applicationService) {
        this(applicationService, new IdentityAwareCyanCruiseWebApiBoundary());
    }

    public AdminConsoleGovernanceWebApi(AdminConsoleGovernanceApplicationService applicationService,
                                 IdentityAwareCyanCruiseWebApiBoundary identityBoundary) {
        this.applicationService = applicationService;
        this.identityBoundary = identityBoundary;
    }

    @ApiPostMapping(value = "/whoami", desc = "Check admin identity", methodParamNames = {"adminId"})
    public @ApiResponseBody(value = "Admin identity") AdminIdentityDto whoami(
            @ApiRequestBody(value = "adminId", required = true) String adminId) {
        try {
            return applicationService.whoami(identityBoundary.requireAdmin(adminId));
        } catch (IdentityBoundaryException ex) {
            return identityBoundary.rejectAsAdminIdentity(ex);
        }
    }

    @ApiPostMapping(value = "/organizations/list", desc = "List organizations", methodParamNames = {"adminId"})
    public @ApiResponseBody(value = "Organizations") List<AdminOrganizationDto> organizations(
            @ApiRequestBody(value = "adminId", required = true) String adminId) {
        try {
            return applicationService.listOrganizations(resolveAdminId(adminId));
        } catch (IdentityBoundaryException ex) {
            return Collections.emptyList();
        }
    }

    @ApiPostMapping(value = "/organizations/save", desc = "Save organization", methodParamNames = {"adminId", "organization"})
    public @ApiResponseBody(value = "Organization") AdminOrganizationDto saveOrganization(
            @ApiRequestBody(value = "adminId", required = true) String adminId,
            @ApiRequestBody(value = "organization", required = true) AdminOrganizationDto organization) {
        try {
            return applicationService.saveOrganization(resolveAdminId(adminId), organization);
        } catch (IdentityBoundaryException ex) {
            return null;
        }
    }

    @ApiPostMapping(value = "/organizations/dashboard", desc = "Organization dashboard", methodParamNames = {"adminId", "orgId"})
    public @ApiResponseBody(value = "Dashboard") AdminOrgDashboardDto dashboard(
            @ApiRequestBody(value = "adminId", required = true) String adminId,
            @ApiRequestBody(value = "orgId", required = true) String orgId) {
        try {
            return applicationService.organizationDashboard(resolveAdminId(adminId), orgId);
        } catch (IdentityBoundaryException ex) {
            return new AdminOrgDashboardDto();
        }
    }

    @ApiPostMapping(value = "/organizations/students", desc = "Organization students", methodParamNames = {"adminId", "orgId"})
    public @ApiResponseBody(value = "Students") List<AdminStudentRowDto> students(
            @ApiRequestBody(value = "adminId", required = true) String adminId,
            @ApiRequestBody(value = "orgId", required = true) String orgId) {
        try {
            return applicationService.organizationStudents(resolveAdminId(adminId), orgId);
        } catch (IdentityBoundaryException ex) {
            return Collections.emptyList();
        }
    }

    @ApiPostMapping(value = "/users/list", desc = "List users", methodParamNames = {"adminId", "page", "size", "keyword"})
    public @ApiResponseBody(value = "Users") AdminPageResult<AdminUserDto> users(
            @ApiRequestBody(value = "adminId", required = true) String adminId,
            @ApiRequestBody(value = "page", required = false) int page,
            @ApiRequestBody(value = "size", required = false) int size,
            @ApiRequestBody(value = "keyword", required = false) String keyword) {
        try {
            return applicationService.listUsers(resolveAdminId(adminId), page, size, keyword);
        } catch (IdentityBoundaryException ex) {
            return emptyPage(page, size);
        }
    }

    @ApiPostMapping(value = "/users/detail", desc = "User detail", methodParamNames = {"adminId", "userId"})
    public @ApiResponseBody(value = "User") AdminUserDto userDetail(
            @ApiRequestBody(value = "adminId", required = true) String adminId,
            @ApiRequestBody(value = "userId", required = true) String userId) {
        try {
            return applicationService.userDetail(resolveAdminId(adminId), userId);
        } catch (IdentityBoundaryException ex) {
            return null;
        }
    }

    @ApiPostMapping(value = "/users/ban", desc = "Ban user", methodParamNames = {"adminId", "userId", "reason"})
    public @ApiResponseBody(value = "Ban result") AdminOperationResult ban(
            @ApiRequestBody(value = "adminId", required = true) String adminId,
            @ApiRequestBody(value = "userId", required = true) String userId,
            @ApiRequestBody(value = "reason", required = false) String reason) {
        try {
            return applicationService.banUser(identityBoundary.requireAdmin(adminId), userId, reason);
        } catch (IdentityBoundaryException ex) {
            return identityBoundary.rejectAsAdminOperation(ex, userId);
        }
    }

    @ApiPostMapping(value = "/users/unban", desc = "Unban user", methodParamNames = {"adminId", "userId"})
    public @ApiResponseBody(value = "Unban result") AdminOperationResult unban(
            @ApiRequestBody(value = "adminId", required = true) String adminId,
            @ApiRequestBody(value = "userId", required = true) String userId) {
        try {
            return applicationService.unbanUser(resolveAdminId(adminId), userId);
        } catch (IdentityBoundaryException ex) {
            return identityBoundary.rejectAsAdminOperation(ex, userId);
        }
    }

    @ApiPostMapping(value = "/career-paths/list", desc = "List career paths", methodParamNames = {"adminId"})
    public @ApiResponseBody(value = "Paths") List<AdminCareerPathDto> paths(
            @ApiRequestBody(value = "adminId", required = true) String adminId) {
        try {
            return applicationService.listCareerPaths(resolveAdminId(adminId));
        } catch (IdentityBoundaryException ex) {
            return Collections.emptyList();
        }
    }

    @ApiPostMapping(value = "/career-paths/save", desc = "Save career path", methodParamNames = {"adminId", "path"})
    public @ApiResponseBody(value = "Path") AdminCareerPathDto savePath(
            @ApiRequestBody(value = "adminId", required = true) String adminId,
            @ApiRequestBody(value = "path", required = true) AdminCareerPathDto path) {
        try {
            return applicationService.saveCareerPath(resolveAdminId(adminId), path);
        } catch (IdentityBoundaryException ex) {
            return null;
        }
    }

    @ApiPostMapping(value = "/career-paths/nodes/list", desc = "List career nodes", methodParamNames = {"adminId", "pathId"})
    public @ApiResponseBody(value = "Nodes") List<AdminCareerNodeDto> nodes(
            @ApiRequestBody(value = "adminId", required = true) String adminId,
            @ApiRequestBody(value = "pathId", required = true) String pathId) {
        try {
            return applicationService.listCareerNodes(resolveAdminId(adminId), pathId);
        } catch (IdentityBoundaryException ex) {
            return Collections.emptyList();
        }
    }

    @ApiPostMapping(value = "/questions/list", desc = "List admin questions", methodParamNames = {"adminId", "source", "reviewStatus"})
    public @ApiResponseBody(value = "Questions") List<AdminQuestionDto> questions(
            @ApiRequestBody(value = "adminId", required = true) String adminId,
            @ApiRequestBody(value = "source", required = false) String source,
            @ApiRequestBody(value = "reviewStatus", required = false) String reviewStatus) {
        try {
            return applicationService.listQuestions(resolveAdminId(adminId), source, reviewStatus);
        } catch (IdentityBoundaryException ex) {
            return Collections.emptyList();
        }
    }

    @ApiPostMapping(value = "/questions/update", desc = "Update question", methodParamNames = {"adminId", "questionId", "patch"})
    public @ApiResponseBody(value = "Question") AdminQuestionDto updateQuestion(
            @ApiRequestBody(value = "adminId", required = true) String adminId,
            @ApiRequestBody(value = "questionId", required = true) String questionId,
            @ApiRequestBody(value = "patch", required = true) AdminQuestionDto patch) {
        try {
            return applicationService.updateQuestion(resolveAdminId(adminId), questionId, patch);
        } catch (IdentityBoundaryException ex) {
            return null;
        }
    }

    @ApiPostMapping(value = "/questions/approve", desc = "Approve question", methodParamNames = {"adminId", "questionId"})
    public @ApiResponseBody(value = "Question") AdminQuestionDto approveQuestion(
            @ApiRequestBody(value = "adminId", required = true) String adminId,
            @ApiRequestBody(value = "questionId", required = true) String questionId) {
        try {
            return applicationService.approveQuestion(resolveAdminId(adminId), questionId);
        } catch (IdentityBoundaryException ex) {
            return null;
        }
    }

    @ApiPostMapping(value = "/questions/reject", desc = "Reject question", methodParamNames = {"adminId", "questionId"})
    public @ApiResponseBody(value = "Question") AdminQuestionDto rejectQuestion(
            @ApiRequestBody(value = "adminId", required = true) String adminId,
            @ApiRequestBody(value = "questionId", required = true) String questionId) {
        try {
            return applicationService.rejectQuestion(resolveAdminId(adminId), questionId);
        } catch (IdentityBoundaryException ex) {
            return null;
        }
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
        try {
            return applicationService.listContent(resolveAdminId(adminId), type);
        } catch (IdentityBoundaryException ex) {
            return Collections.emptyList();
        }
    }

    @ApiPostMapping(value = "/content/save", desc = "Save content", methodParamNames = {"adminId", "content"})
    public @ApiResponseBody(value = "Content") AdminContentItemDto saveContent(
            @ApiRequestBody(value = "adminId", required = true) String adminId,
            @ApiRequestBody(value = "content", required = true) AdminContentItemDto content) {
        try {
            return applicationService.saveContent(resolveAdminId(adminId), content);
        } catch (IdentityBoundaryException ex) {
            return null;
        }
    }

    @ApiPostMapping(value = "/content/pin", desc = "Toggle content pinned", methodParamNames = {"adminId", "contentId"})
    public @ApiResponseBody(value = "Content") AdminContentItemDto pinContent(
            @ApiRequestBody(value = "adminId", required = true) String adminId,
            @ApiRequestBody(value = "contentId", required = true) String contentId) {
        try {
            return applicationService.toggleContentPinned(resolveAdminId(adminId), contentId);
        } catch (IdentityBoundaryException ex) {
            return null;
        }
    }

    @ApiPostMapping(value = "/content/hide", desc = "Toggle content hidden", methodParamNames = {"adminId", "contentId"})
    public @ApiResponseBody(value = "Content") AdminContentItemDto hideContent(
            @ApiRequestBody(value = "adminId", required = true) String adminId,
            @ApiRequestBody(value = "contentId", required = true) String contentId) {
        try {
            return applicationService.toggleContentHidden(resolveAdminId(adminId), contentId);
        } catch (IdentityBoundaryException ex) {
            return null;
        }
    }

    @ApiPostMapping(value = "/broadcast", desc = "Admin broadcast", methodParamNames = {"adminId", "request"})
    public @ApiResponseBody(value = "Broadcast result") AdminBroadcastResult broadcast(
            @ApiRequestBody(value = "adminId", required = true) String adminId,
            @ApiRequestBody(value = "request", required = true) AdminBroadcastRequest request) {
        try {
            return applicationService.broadcast(resolveAdminId(adminId), request);
        } catch (IdentityBoundaryException ex) {
            AdminBroadcastResult result = new AdminBroadcastResult();
            result.setStatus(AdminConstants.STATUS_FORBIDDEN);
            result.setMessage(ex.getMessage());
            result.setTargetCount(Integer.valueOf(0));
            result.setSuccessCount(Integer.valueOf(0));
            result.setFailedCount(Integer.valueOf(0));
            result.setSkippedCount(Integer.valueOf(0));
            return result;
        }
    }

    @ApiPostMapping(value = "/analytics/summary", desc = "Analytics summary", methodParamNames = {"adminId"})
    public @ApiResponseBody(value = "Analytics") AdminAnalyticsSummaryDto analytics(
            @ApiRequestBody(value = "adminId", required = true) String adminId) {
        try {
            return applicationService.analyticsSummary(resolveAdminId(adminId));
        } catch (IdentityBoundaryException ex) {
            return new AdminAnalyticsSummaryDto();
        }
    }

    @ApiPostMapping(value = "/audit-log/list", desc = "Audit log", methodParamNames = {"adminId", "page", "size"})
    public @ApiResponseBody(value = "Audit logs") AdminPageResult<AdminAuditLogDto> auditLogs(
            @ApiRequestBody(value = "adminId", required = true) String adminId,
            @ApiRequestBody(value = "page", required = false) int page,
            @ApiRequestBody(value = "size", required = false) int size) {
        try {
            return applicationService.auditLogs(resolveAdminId(adminId), page, size);
        } catch (IdentityBoundaryException ex) {
            return emptyPage(page, size);
        }
    }

    private String resolveAdminId(String adminId) {
        return identityBoundary.requireAdmin(adminId);
    }

    private <T> AdminPageResult<T> emptyPage(int page, int size) {
        AdminPageResult<T> result = new AdminPageResult<T>();
        result.setItems(Collections.<T>emptyList());
        result.setPage(Integer.valueOf(Math.max(0, page)));
        result.setSize(Integer.valueOf(Math.max(1, size)));
        result.setTotal(Integer.valueOf(0));
        return result;
    }
}
