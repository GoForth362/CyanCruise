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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
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
    void currentAdminCannotBanOwnUserFacingAccess() {
        InMemoryAdminGovernanceStorage storage = new InMemoryAdminGovernanceStorage();
        AdminConsoleGovernanceApplicationService service = service(storage);
        AdminUserDto adminUser = new AdminUserDto();
        adminUser.setUserId("admin");
        adminUser.setStatus(AdminConstants.USER_STATUS_ACTIVE);
        storage.saveUser(adminUser);

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, new org.junit.jupiter.api.function.Executable() {
            public void execute() {
                service.banUser("admin", "admin", "test");
            }
        });

        assertTrue(error.getMessage().contains("不能禁用自己"));
        assertTrue(service.isUserAllowed("admin"));
        assertEquals(AdminConstants.STATUS_OK, service.whoami("admin").getStatus());
        assertTrue(service.listOrganizations("admin").isEmpty());
    }

    @Test
    void administratorsCannotBanEachOther() {
        InMemoryAdminGovernanceStorage storage = new InMemoryAdminGovernanceStorage();
        storage.addAdmin("admin-a");
        storage.addAdmin("admin-b");
        AdminUserDto target = new AdminUserDto();
        target.setUserId("admin-b");
        target.setStatus(AdminConstants.USER_STATUS_ACTIVE);
        storage.saveUser(target);
        AdminConsoleGovernanceApplicationService service = service(storage);

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, new org.junit.jupiter.api.function.Executable() {
            public void execute() {
                service.banUser("admin-a", "admin-b", "test");
            }
        });

        assertTrue(error.getMessage().contains("管理员之间不能相互禁用"));
        assertTrue(service.isUserAllowed("admin-b"));
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
    void differentAdministratorsSeeTheSameGovernanceUsers() {
        InMemoryAdminGovernanceStorage storage = new InMemoryAdminGovernanceStorage();
        storage.addAdmin("admin-a");
        storage.addAdmin("admin-b");
        AdminConsoleGovernanceApplicationService service = service(storage);
        service.registerActiveUserIfAbsent("shared-user", "Shared User", "100000");

        AdminPageResult<AdminUserDto> first = service.listUsers("admin-a", 0, 20, null);
        AdminPageResult<AdminUserDto> second = service.listUsers("admin-b", 0, 20, null);

        assertEquals(first.getTotal(), second.getTotal());
        assertEquals(first.getItems().get(0).getUserId(), second.getItems().get(0).getUserId());
    }

    @Test
    void userListSearchesOnlyAccountFieldsAndMarksAccountType() {
        InMemoryAdminGovernanceStorage storage = new InMemoryAdminGovernanceStorage();
        storage.addAdmin("platform-admin");
        AdminUserDto administrator = new AdminUserDto();
        administrator.setUserId("platform-admin");
        administrator.setNickname("管理员甲");
        administrator.setStatus(AdminConstants.USER_STATUS_ACTIVE);
        storage.saveUser(administrator);
        AdminUserDto regular = new AdminUserDto();
        regular.setUserId("user-1001");
        regular.setNickname("普通用户乙");
        regular.setSchool("成都理工大学");
        regular.setMajor("软件工程");
        regular.setStatus(AdminConstants.USER_STATUS_ACTIVE);
        storage.saveUser(regular);
        AdminConsoleGovernanceApplicationService service = service(storage);

        assertEquals("user-1001", service.listUsers("admin", 0, 20, "普通用户").getItems().get(0).getUserId());
        assertEquals("user-1001", service.listUsers("admin", 0, 20, "1001").getItems().get(0).getUserId());
        assertEquals(Integer.valueOf(0), service.listUsers("admin", 0, 20, "成都理工").getTotal());
        assertEquals(Integer.valueOf(0), service.listUsers("admin", 0, 20, "软件工程").getTotal());
        assertEquals(Boolean.TRUE, service.listUsers("admin", 0, 20, "管理员甲").getItems().get(0).getAdministrator());
        assertEquals(Boolean.FALSE, service.listUsers("admin", 0, 20, "普通用户乙").getItems().get(0).getAdministrator());
    }

    @Test
    void broadcastCanTargetMultipleActiveUsers() {
        InMemoryAdminGovernanceStorage storage = new InMemoryAdminGovernanceStorage();
        AdminConsoleGovernanceApplicationService service = service(storage);
        AdminUserDto first = new AdminUserDto();
        first.setUserId("u1");
        first.setStatus(AdminConstants.USER_STATUS_ACTIVE);
        storage.saveUser(first);
        AdminUserDto second = new AdminUserDto();
        second.setUserId("u2");
        second.setStatus(AdminConstants.USER_STATUS_ACTIVE);
        storage.saveUser(second);
        AdminUserDto banned = new AdminUserDto();
        banned.setUserId("u3");
        banned.setStatus(AdminConstants.USER_STATUS_BANNED);
        storage.saveUser(banned);
        AdminBroadcastRequest request = new AdminBroadcastRequest();
        request.setTitle("Notice");
        request.setContent("Content");
        request.setUserIds(Arrays.asList("u1", "u2", "u2", "u3"));

        AdminBroadcastResult result = service.broadcast("admin", request);

        assertEquals(Integer.valueOf(2), result.getTargetCount());
    }

    @Test
    void userManagementHidesPersonalAndOrganizationDataWithoutChangingStoredAssociation() {
        InMemoryAdminGovernanceStorage storage = new InMemoryAdminGovernanceStorage();
        AdminConsoleGovernanceApplicationService service = service(storage);
        service.registerActiveUserIfAbsent("api-user", "测试账号", null);
        service.registerActiveUserIfAbsent("real-user", "冯如", "100000");
        AdminUserDto stored = storage.findUser("real-user");
        stored.setSchool("成都理工大学");
        stored.setMajor("软件工程");
        storage.saveUser(stored);

        AdminPageResult<AdminUserDto> users = service.listUsers("admin", 0, 20, null);
        AdminUserDto listed = users.getItems().get(0);
        AdminUserDto detailed = service.userDetail("admin", "real-user");

        assertEquals(Integer.valueOf(1), users.getTotal());
        assertEquals("冯如", listed.getNickname());
        assertNull(listed.getSchool());
        assertNull(listed.getMajor());
        assertNull(listed.getOrgId());
        assertNull(detailed.getSchool());
        assertNull(detailed.getMajor());
        assertNull(detailed.getOrgId());

        service.banUser("admin", "real-user", "test");
        assertEquals("100000", storage.findUser("real-user").getOrgId());
        assertEquals("成都理工大学", storage.findUser("real-user").getSchool());
        assertEquals("软件工程", storage.findUser("real-user").getMajor());
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
    void adminSavedInterviewQuestionDefaultsToPendingReview() {
        InMemoryAdminGovernanceStorage storage = new InMemoryAdminGovernanceStorage();
        AdminConsoleGovernanceApplicationService service = service(storage);
        AdminQuestionDto question = new AdminQuestionDto();
        question.setPosition("后端开发");
        question.setDifficulty("NORMAL");
        question.setContent("请说明一次你解决线上问题的过程。");

        AdminQuestionDto saved = service.saveQuestion("admin", question);

        assertEquals("ADMIN", saved.getSource());
        assertEquals(AdminConstants.QUESTION_REVIEW_PENDING, saved.getReviewStatus());
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

    @Test
    void savedContentCategoryIsNormalizedByType() {
        InMemoryAdminGovernanceStorage storage = new InMemoryAdminGovernanceStorage();
        AdminConsoleGovernanceApplicationService service = service(storage);
        AdminContentItemDto content = new AdminContentItemDto();
        content.setTitle("Article");
        content.setType(AdminConstants.CONTENT_TYPE_ARTICLE);
        content.setCategory("求职指导");

        AdminContentItemDto saved = service.saveContent("admin", content);

        assertEquals("精选文章", saved.getCategory());
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
