package v620.cc001.cloud01.app01.mservice;

import org.junit.jupiter.api.Test;
import v620.base.helper.career.AdminConsoleGovernanceService;
import v620.cc001.base.common.dto.career.AdminBroadcastRequest;
import v620.cc001.base.common.dto.career.AdminBroadcastResult;
import v620.cc001.base.common.dto.career.AdminConstants;
import v620.cc001.base.common.dto.career.AdminContentItemDto;
import v620.cc001.base.common.dto.career.AdminInterviewSummaryDto;
import v620.cc001.base.common.dto.career.AdminOperationResult;
import v620.cc001.base.common.dto.career.AdminOrgDashboardDto;
import v620.cc001.base.common.dto.career.AdminOrganizationDto;
import v620.cc001.base.common.dto.career.AdminQuestionContributionRequest;
import v620.cc001.base.common.dto.career.AdminQuestionDto;
import v620.cc001.base.common.dto.career.AdminUserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdminConsoleGovernanceApplicationServiceTest {

    @Test
    void adminFlowCoversDashboardBanBroadcastQuestionContentAnalyticsAndAudit() {
        InMemoryAdminGovernanceStorage storage = new InMemoryAdminGovernanceStorage();
        AdminConsoleGovernanceApplicationService service = service(storage);
        AdminOrganizationDto org = new AdminOrganizationDto();
        org.setCode("cdut");
        org.setName("CDUT");
        AdminOrganizationDto savedOrg = service.saveOrganization("admin", org);
        AdminUserDto user = new AdminUserDto();
        user.setUserId("u1");
        user.setOrgId(savedOrg.getOrgId());
        user.setNickname("Ming");
        user.setSchool("CDUT");
        user.setMajor("CS");
        user.setStatus(AdminConstants.USER_STATUS_ACTIVE);
        storage.saveUser(user);
        AdminInterviewSummaryDto interview = new AdminInterviewSummaryDto();
        interview.setUserId("u1");
        interview.setFinalScore(Integer.valueOf(90));
        interview.setReportJson("{\"radarChart\":{\"expression\":4,\"logic\":3,\"technical\":5,\"pressureResistance\":2,\"communication\":1}}");
        storage.addInterview(interview);
        storage.setEventCount("ADMIN_UNKNOWN_EVENT", 3);

        AdminOrgDashboardDto dashboard = service.organizationDashboard("admin", savedOrg.getOrgId());
        AdminOperationResult banned = service.banUser("admin", "u1", "");
        AdminOperationResult unbanned = service.unbanUser("admin", "u1");
        AdminBroadcastRequest broadcast = new AdminBroadcastRequest();
        broadcast.setTitle("Notice");
        broadcast.setContent("Content");
        AdminBroadcastResult broadcastResult = service.broadcast("admin", broadcast);
        AdminQuestionDto question = new AdminQuestionDto();
        question.setContent("Tell me about yourself");
        question.setReviewStatus(AdminConstants.QUESTION_REVIEW_PENDING);
        AdminQuestionDto savedQuestion = storage.saveQuestion(question);
        AdminQuestionDto approved = service.approveQuestion("admin", savedQuestion.getQuestionId());
        AdminContentItemDto content = new AdminContentItemDto();
        content.setTitle("Article");
        AdminContentItemDto savedContent = service.saveContent("admin", content);

        assertEquals(Integer.valueOf(1), dashboard.getStudentCount());
        assertEquals(AdminConstants.STATUS_OK, banned.getStatus());
        assertEquals(Boolean.TRUE, banned.getAuditRecorded());
        assertEquals(AdminConstants.STATUS_OK, unbanned.getStatus());
        assertEquals(Integer.valueOf(1), broadcastResult.getTargetCount());
        assertEquals(AdminConstants.QUESTION_REVIEW_PUBLISHED, approved.getReviewStatus());
        assertEquals(Boolean.TRUE, service.toggleContentPinned("admin", savedContent.getContentId()).getPinned());
        assertEquals(Integer.valueOf(3), service.analyticsSummary("admin").getEventBreakdown30d().get("ADMIN_UNKNOWN_EVENT"));
        assertFalse(service.auditLogs("admin", 0, 50).getItems().isEmpty());
    }

    @Test
    void missingAndForbiddenIdentityAreRejected() {
        AdminConsoleGovernanceApplicationService service = service(new InMemoryAdminGovernanceStorage());

        assertEquals(AdminConstants.STATUS_IDENTITY_REQUIRED, service.whoami(" ").getStatus());
        assertEquals(AdminConstants.STATUS_FORBIDDEN, service.whoami("user").getStatus());
        assertThrows(IllegalArgumentException.class, new org.junit.jupiter.api.function.Executable() {
            public void execute() {
                service.listOrganizations("user");
            }
        });
    }

    @Test
    void publicContributionUsesSafetyAndAnonymization() {
        AdminConsoleGovernanceApplicationService service = service(new InMemoryAdminGovernanceStorage());
        AdminQuestionContributionRequest request = new AdminQuestionContributionRequest();
        request.setUserId("u1");
        request.setContent("What project are you proud of and why?");

        AdminQuestionDto question = service.contributeQuestion(request);

        assertEquals("General", question.getPosition());
        assertTrue(question.getContributorHash().length() > 20);
        assertFalse(question.getContributorHash().contains("u1"));
    }

    private AdminConsoleGovernanceApplicationService service(InMemoryAdminGovernanceStorage storage) {
        return new AdminConsoleGovernanceApplicationService(storage,
                new NotificationsSubscriptionsApplicationService(
                        new InMemoryNotificationStorage(),
                        new InMemorySubscriptionQuotaStorage(),
                        new UnavailableSubscriptionSender(),
                        new v620.base.helper.career.NotificationsSubscriptionsService()),
                new AdminConsoleGovernanceService());
    }
}
