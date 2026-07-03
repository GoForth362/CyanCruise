package v620.cc001.cloud01.app01.mservice;

import v620.cc001.cloud01.app01.mservice.notification.impl.InMemoryNotificationStorage;
import v620.cc001.cloud01.app01.mservice.notification.impl.InMemorySubscriptionQuotaStorage;
import v620.cc001.cloud01.app01.mservice.notification.impl.UnavailableSubscriptionSender;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerProfileStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerResourceStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryAdminGovernanceStorage;
import v620.cc001.cloud01.app01.mservice.application.AdminConsoleGovernanceApplicationService;
import v620.cc001.cloud01.app01.mservice.application.NotificationsSubscriptionsApplicationService;
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
import v620.cc001.base.common.dto.career.AdminPageResult;
import v620.cc001.base.common.dto.career.AdminQuestionContributionRequest;
import v620.cc001.base.common.dto.career.AdminQuestionDto;
import v620.cc001.base.common.dto.career.AdminUserDto;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;

import java.util.List;

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
        assertFalse(service.isUserAllowed("u1"));
        AdminOperationResult unbanned = service.unbanUser("admin", "u1");
        assertTrue(service.isUserAllowed("u1"));
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
        assertTrue(service.deleteContent("admin", savedContent.getContentId()));
        assertEquals(Integer.valueOf(0), service.listContent("admin", null).size());
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
    void banningCurrentAdminOnlyRestrictsUserFacingAccess() {
        InMemoryAdminGovernanceStorage storage = new InMemoryAdminGovernanceStorage();
        AdminConsoleGovernanceApplicationService service = service(storage);
        AdminUserDto adminUser = new AdminUserDto();
        adminUser.setUserId("admin");
        adminUser.setStatus(AdminConstants.USER_STATUS_ACTIVE);
        storage.saveUser(adminUser);

        AdminOperationResult result = service.banUser("admin", "admin", "test");

        assertEquals(AdminConstants.STATUS_OK, result.getStatus());
        assertTrue(result.getMessage().contains("管理后台权限不会受影响"));
        assertFalse(service.isUserAllowed("admin"));
        assertEquals(AdminConstants.STATUS_OK, service.whoami("admin").getStatus());
        assertTrue(service.listOrganizations("admin").isEmpty());
    }

    @Test
    void banUserSucceedsWhenNotificationPushFails() {
        InMemoryAdminGovernanceStorage storage = new InMemoryAdminGovernanceStorage();
        AdminConsoleGovernanceApplicationService service = new AdminConsoleGovernanceApplicationService(storage,
                new FailingNotificationsSubscriptionsApplicationService(),
                new AdminConsoleGovernanceService());
        AdminUserDto user = new AdminUserDto();
        user.setUserId("u1");
        user.setStatus(AdminConstants.USER_STATUS_ACTIVE);
        storage.saveUser(user);

        AdminOperationResult result = service.banUser("admin", "u1", "test");

        assertEquals(AdminConstants.STATUS_OK, result.getStatus());
        assertFalse(service.isUserAllowed("u1"));
        assertFalse(result.getMessage().contains("notification="));
    }

    @Test
    void activeUserCanBeRegisteredFromUserFacingTraffic() {
        InMemoryAdminGovernanceStorage storage = new InMemoryAdminGovernanceStorage();
        AdminConsoleGovernanceApplicationService service = service(storage);

        service.registerActiveUserIfAbsent(" u-real ");
        service.registerActiveUserIfAbsent("u-real");

        AdminUserDto user = storage.findUser("u-real");
        assertEquals("u-real", user.getUserId());
        assertEquals("u-real", user.getNickname());
        assertEquals(AdminConstants.USER_STATUS_ACTIVE, user.getStatus());
        assertEquals(Integer.valueOf(1), service.listUsers("admin", 0, 20, null).getTotal());
    }

    @Test
    void userListSkipsDevelopmentUserAndEnrichesProfileFields() {
        InMemoryAdminGovernanceStorage storage = new InMemoryAdminGovernanceStorage();
        InMemoryCareerProfileStorage profiles = new InMemoryCareerProfileStorage();
        AdminConsoleGovernanceApplicationService service = service(storage, profiles);
        UserProfileSnapshot snapshot = new UserProfileSnapshot();
        UserProfileSnapshot.OnboardingBlock onboarding = new UserProfileSnapshot.OnboardingBlock();
        UserProfileSnapshot.EducationBlock education = new UserProfileSnapshot.EducationBlock();
        education.setSchool("成都理工大学");
        education.setMajor("软件工程");
        onboarding.setEducation(education);
        snapshot.setOnboarding(onboarding);
        profiles.saveSnapshot("real-user", snapshot);

        service.registerActiveUserIfAbsent("api-user", "测试账号", null);
        service.registerActiveUserIfAbsent("real-user", "冯如", "100000");

        AdminPageResult<AdminUserDto> users = service.listUsers("admin", 0, 20, null);

        assertEquals(Integer.valueOf(1), users.getTotal());
        assertEquals("冯如", users.getItems().get(0).getNickname());
        assertEquals("成都理工大学", users.getItems().get(0).getSchool());
        assertEquals("软件工程", users.getItems().get(0).getMajor());
        assertEquals("100000", users.getItems().get(0).getOrgId());
    }

    @Test
    void analyticsUserCountUsesVisibleUserListScope() {
        InMemoryAdminGovernanceStorage storage = new InMemoryAdminGovernanceStorage();
        AdminConsoleGovernanceApplicationService service = service(storage);
        AdminUserDto visible = new AdminUserDto();
        visible.setUserId("real-user");
        visible.setStatus(AdminConstants.USER_STATUS_ACTIVE);
        storage.saveUser(visible);
        AdminUserDto development = new AdminUserDto();
        development.setUserId("api-user");
        development.setStatus(AdminConstants.USER_STATUS_ACTIVE);
        storage.saveUser(development);

        assertEquals(Integer.valueOf(1), service.listUsers("admin", 0, 20, null).getTotal());
        assertEquals(Integer.valueOf(1), service.analyticsSummary("admin").getTotalUsers());
        assertEquals(Integer.valueOf(1), service.analyticsSummary("admin").getEventBreakdown30d().get("USERS"));
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

    @Test
    void adminCanSaveAndDeleteInterviewQuestion() {
        InMemoryAdminGovernanceStorage storage = new InMemoryAdminGovernanceStorage();
        AdminConsoleGovernanceApplicationService service = service(storage);
        AdminQuestionDto question = new AdminQuestionDto();
        question.setPosition("后端开发");
        question.setDifficulty("NORMAL");
        question.setContent("请说明一次你解决线上问题的过程。");

        AdminQuestionDto saved = service.saveQuestion("admin", question);

        assertEquals("ADMIN", saved.getSource());
        assertEquals(AdminConstants.QUESTION_REVIEW_PUBLISHED, saved.getReviewStatus());
        assertEquals(AdminConstants.QUESTION_STATUS_APPROVED, saved.getStatus());
        assertEquals(Integer.valueOf(1), service.listQuestions("admin", null, null).size());
        assertTrue(service.deleteQuestion("admin", saved.getQuestionId()));
        assertEquals(Integer.valueOf(0), service.listQuestions("admin", null, null).size());
    }

    @Test
    void defaultCareerResourcesAreImportedForAdminEditing() {
        InMemoryAdminGovernanceStorage storage = new InMemoryAdminGovernanceStorage();
        AdminConsoleGovernanceApplicationService service = new AdminConsoleGovernanceApplicationService(storage,
                null,
                new NotificationsSubscriptionsApplicationService(
                        new InMemoryNotificationStorage(),
                        new InMemorySubscriptionQuotaStorage(),
                        new UnavailableSubscriptionSender(),
                        new v620.base.helper.career.NotificationsSubscriptionsService()),
                new AdminConsoleGovernanceService(),
                true,
                new InMemoryCareerResourceStorage());

        List<AdminContentItemDto> content = service.listContent("admin", null);

        assertFalse(content.isEmpty());
        assertTrue(hasContent(content, "service-ncss-001"));
        assertTrue(hasContent(content, "video-bilibili-interview-001"));
    }

    private boolean hasContent(List<AdminContentItemDto> content, String contentId) {
        for (AdminContentItemDto item : content) {
            if (contentId.equals(item.getContentId())) {
                return true;
            }
        }
        return false;
    }

    private AdminConsoleGovernanceApplicationService service(InMemoryAdminGovernanceStorage storage) {
        return service(storage, null);
    }

    private AdminConsoleGovernanceApplicationService service(InMemoryAdminGovernanceStorage storage,
                                                             InMemoryCareerProfileStorage profileStorage) {
        return new AdminConsoleGovernanceApplicationService(storage,
                profileStorage,
                new NotificationsSubscriptionsApplicationService(
                        new InMemoryNotificationStorage(),
                        new InMemorySubscriptionQuotaStorage(),
                        new UnavailableSubscriptionSender(),
                        new v620.base.helper.career.NotificationsSubscriptionsService()),
                new AdminConsoleGovernanceService());
    }

    private static class FailingNotificationsSubscriptionsApplicationService
            extends NotificationsSubscriptionsApplicationService {
        FailingNotificationsSubscriptionsApplicationService() {
            super(new InMemoryNotificationStorage(),
                    new InMemorySubscriptionQuotaStorage(),
                    new UnavailableSubscriptionSender(),
                    new v620.base.helper.career.NotificationsSubscriptionsService());
        }

        @Override
        public v620.cc001.base.common.dto.career.NotificationOperationResult pushBestEffort(
                v620.cc001.base.common.dto.career.NotificationPushRequest request) {
            throw new AssertionError("notification unavailable");
        }
    }
}
