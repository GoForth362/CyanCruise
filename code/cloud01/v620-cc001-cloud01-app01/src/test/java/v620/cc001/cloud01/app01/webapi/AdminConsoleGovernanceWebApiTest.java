package v620.cc001.cloud01.app01.webapi;

import org.junit.jupiter.api.Test;
import v620.base.helper.career.AdminConsoleGovernanceService;
import v620.cc001.base.common.dto.career.AdminBroadcastRequest;
import v620.cc001.base.common.dto.career.AdminConstants;
import v620.cc001.base.common.dto.career.AdminContentItemDto;
import v620.cc001.base.common.dto.career.AdminOrganizationDto;
import v620.cc001.base.common.dto.career.AdminQuestionDto;
import v620.cc001.base.common.dto.career.AdminUserDto;
import v620.cc001.cloud01.app01.mservice.AdminConsoleGovernanceApplicationService;
import v620.cc001.cloud01.app01.mservice.InMemoryAdminGovernanceStorage;
import v620.cc001.cloud01.app01.mservice.InMemoryNotificationStorage;
import v620.cc001.cloud01.app01.mservice.InMemorySubscriptionQuotaStorage;
import v620.cc001.cloud01.app01.mservice.NotificationsSubscriptionsApplicationService;
import v620.cc001.cloud01.app01.mservice.UnavailableSubscriptionSender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class AdminConsoleGovernanceWebApiTest {

    @Test
    void webApiExposesAdminContracts() {
        InMemoryAdminGovernanceStorage storage = new InMemoryAdminGovernanceStorage();
        AdminConsoleGovernanceWebApi webApi = new AdminConsoleGovernanceWebApi(
                new AdminConsoleGovernanceApplicationService(storage,
                        new NotificationsSubscriptionsApplicationService(
                                new InMemoryNotificationStorage(),
                                new InMemorySubscriptionQuotaStorage(),
                                new UnavailableSubscriptionSender(),
                                new v620.base.helper.career.NotificationsSubscriptionsService()),
                        new AdminConsoleGovernanceService()));
        AdminOrganizationDto org = new AdminOrganizationDto();
        org.setCode("cdut");
        org.setName("CDUT");
        AdminUserDto user = new AdminUserDto();
        user.setUserId("u1");
        user.setNickname("Ming");
        user.setStatus(AdminConstants.USER_STATUS_ACTIVE);
        storage.saveUser(user);

        AdminOrganizationDto savedOrg = webApi.saveOrganization("admin", org);
        AdminQuestionDto question = new AdminQuestionDto();
        question.setContent("Question content");
        AdminQuestionDto savedQuestion = storage.saveQuestion(question);
        AdminContentItemDto content = new AdminContentItemDto();
        content.setTitle("Article");
        AdminBroadcastRequest broadcast = new AdminBroadcastRequest();
        broadcast.setTitle("Notice");
        broadcast.setContent("Hello");

        assertEquals(AdminConstants.STATUS_OK, webApi.whoami("admin").getStatus());
        assertEquals(1, webApi.organizations("admin").size());
        assertEquals(savedOrg.getOrgId(), webApi.dashboard("admin", savedOrg.getOrgId()).getOrgId());
        assertEquals(Integer.valueOf(1), webApi.broadcast("admin", broadcast).getTargetCount());
        assertEquals(AdminConstants.STATUS_OK, webApi.ban("admin", "u1", "risk").getStatus());
        assertEquals(AdminConstants.QUESTION_REVIEW_PUBLISHED,
                webApi.approveQuestion("admin", savedQuestion.getQuestionId()).getReviewStatus());
        assertEquals(Boolean.TRUE, webApi.pinContent("admin", webApi.saveContent("admin", content).getContentId()).getPinned());
        assertFalse(webApi.auditLogs("admin", 0, 20).getItems().isEmpty());
    }
}
