package v620.cc001.cloud01.app01.mservice;

import v620.cc001.base.common.dto.career.AdminAnalyticsSummaryDto;
import v620.cc001.base.common.dto.career.AdminAuditLogDto;
import v620.cc001.base.common.dto.career.AdminCareerNodeDto;
import v620.cc001.base.common.dto.career.AdminCareerPathDto;
import v620.cc001.base.common.dto.career.AdminContentItemDto;
import v620.cc001.base.common.dto.career.AdminInterviewSummaryDto;
import v620.cc001.base.common.dto.career.AdminOrganizationDto;
import v620.cc001.base.common.dto.career.AdminQuestionDto;
import v620.cc001.base.common.dto.career.AdminUserDto;

import java.util.List;
import java.util.Map;

public interface AdminGovernanceStorage {

    boolean isAdmin(String userId);

    List<AdminOrganizationDto> listOrganizations();

    AdminOrganizationDto saveOrganization(AdminOrganizationDto organization);

    AdminOrganizationDto findOrganization(String orgId);

    List<AdminUserDto> listUsers();

    AdminUserDto findUser(String userId);

    AdminUserDto saveUser(AdminUserDto user);

    List<AdminInterviewSummaryDto> listInterviewsByUser(String userId);

    List<AdminCareerPathDto> listCareerPaths();

    AdminCareerPathDto saveCareerPath(AdminCareerPathDto path);

    boolean deleteCareerPath(String pathId);

    List<AdminCareerNodeDto> listCareerNodes(String pathId);

    AdminCareerNodeDto saveCareerNode(AdminCareerNodeDto node);

    boolean deleteCareerNode(String nodeId);

    List<AdminQuestionDto> listQuestions();

    AdminQuestionDto findQuestion(String questionId);

    AdminQuestionDto saveQuestion(AdminQuestionDto question);

    boolean deleteQuestion(String questionId);

    List<AdminContentItemDto> listContent(String type);

    AdminContentItemDto findContent(String contentId);

    AdminContentItemDto saveContent(AdminContentItemDto content);

    boolean deleteContent(String contentId);

    AdminAnalyticsSummaryDto analyticsSummary();

    List<AdminAuditLogDto> listAuditLogs();

    boolean saveAudit(AdminAuditLogDto auditLog);

    Map<String, List<AdminInterviewSummaryDto>> listInterviewsByUsers(List<AdminUserDto> users);
}
