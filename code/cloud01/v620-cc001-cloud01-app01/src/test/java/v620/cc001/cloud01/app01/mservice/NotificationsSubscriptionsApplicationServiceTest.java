package v620.cc001.cloud01.app01.mservice;

import v620.cc001.cloud01.app01.mservice.notification.impl.InMemoryNotificationStorage;
import v620.cc001.cloud01.app01.mservice.notification.impl.InMemorySubscriptionQuotaStorage;
import v620.cc001.cloud01.app01.mservice.notification.impl.UnavailableSubscriptionSender;
import v620.cc001.cloud01.app01.mservice.application.NotificationsSubscriptionsApplicationService;
import v620.cc001.cloud01.app01.mservice.notification.impl.InMemoryNotificationStorage;
import v620.cc001.cloud01.app01.mservice.notification.impl.InMemorySubscriptionQuotaStorage;
import v620.cc001.cloud01.app01.mservice.notification.impl.UnavailableSubscriptionSender;
import org.junit.jupiter.api.Test;
import v620.base.helper.career.NotificationsSubscriptionsService;
import v620.cc001.base.common.dto.career.NotificationConstants;
import v620.cc001.base.common.dto.career.NotificationOperationResult;
import v620.cc001.base.common.dto.career.NotificationPushRequest;
import v620.cc001.base.common.dto.career.NotificationRecordDto;
import v620.cc001.base.common.dto.career.SubscriptionGrantRequest;
import v620.cc001.base.common.dto.career.SubscriptionQuotaDto;
import v620.cc001.base.common.dto.career.SubscriptionSendRequest;
import v620.cc001.base.common.dto.career.SubscriptionSendResult;
import v620.cc001.base.common.dto.career.WeeklyReportSummaryDto;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NotificationsSubscriptionsApplicationServiceTest {

    @Test
    void notificationFlowUsesOwnershipAndUnreadState() {
        NotificationsSubscriptionsApplicationService service = service();
        NotificationOperationResult pushed = service.pushBestEffort(push("u1", "INTERVIEW_COMPLETED"));
        service.pushBestEffort(push("u2", "SYSTEM"));

        assertEquals(NotificationConstants.RESULT_OK, pushed.getStatus());
        assertEquals(Integer.valueOf(1), service.unreadCount("u1").getCount());
        assertEquals(1, service.list("u1").size());

        NotificationRecordDto notification = service.list("u1").get(0);
        NotificationOperationResult forbidden = service.delete("u2", notification.getNotificationId());
        NotificationOperationResult read = service.markRead("u1", notification.getNotificationId());

        assertEquals(NotificationConstants.RESULT_FAILED, forbidden.getStatus());
        assertEquals(NotificationConstants.RESULT_OK, read.getStatus());
        assertEquals(Integer.valueOf(0), service.unreadCount("u1").getCount());
    }

    @Test
    void missingIdentityIsRejectedForOwnedOperations() {
        NotificationsSubscriptionsApplicationService service = service();

        assertThrows(IllegalArgumentException.class, new org.junit.jupiter.api.function.Executable() {
            public void execute() {
                service.list(" ");
            }
        });
    }

    @Test
    void bestEffortPushSkipsMissingUserInsteadOfThrowing() {
        NotificationsSubscriptionsApplicationService service = service();
        NotificationPushRequest request = push(null, "SYSTEM");

        NotificationOperationResult result = service.pushBestEffort(request);

        assertEquals(NotificationConstants.RESULT_SKIPPED, result.getStatus());
    }

    @Test
    void subscriptionGrantAndUnavailableSendAreSafe() {
        NotificationsSubscriptionsApplicationService service = service();
        SubscriptionGrantRequest grant = new SubscriptionGrantRequest();
        grant.setUserId("u1");
        Map<String, String> results = new LinkedHashMap<String, String>();
        results.put("tpl-weekly", "accept");
        results.put("tpl-ai", "reject");
        grant.setResults(results);

        List<SubscriptionQuotaDto> quotas = service.recordGrant(grant);
        SubscriptionSendRequest send = new SubscriptionSendRequest();
        send.setUserId("u1");
        send.setTemplateId("tpl-weekly");
        send.setRecipientId("openid");
        SubscriptionSendResult sendResult = service.sendSubscription(send);

        assertEquals(1, quotas.size());
        assertEquals(Integer.valueOf(1), service.listQuota("u1").get(0).getRemaining());
        assertEquals(NotificationConstants.RESULT_UNAVAILABLE, sendResult.getStatus());
        assertEquals(Integer.valueOf(1), service.listQuota("u1").get(0).getRemaining());
    }

    @Test
    void weeklyReportCreatesNotificationOnlyWhenDelivered() {
        NotificationsSubscriptionsApplicationService service = service();

        WeeklyReportSummaryDto skipped = service.runWeeklyReport("u1", Arrays.asList(" "));
        WeeklyReportSummaryDto delivered = service.runWeeklyReport("u1", Arrays.asList("finished interview", "practice STAR"));

        assertEquals(Boolean.FALSE, skipped.getDelivered());
        assertEquals(Boolean.TRUE, delivered.getDelivered());
        assertFalse(service.list("u1").isEmpty());
        assertEquals(NotificationConstants.TYPE_WEEKLY_REPORT, service.list("u1").get(0).getType());
    }

    private NotificationsSubscriptionsApplicationService service() {
        return new NotificationsSubscriptionsApplicationService(
                new InMemoryNotificationStorage(),
                new InMemorySubscriptionQuotaStorage(),
                new UnavailableSubscriptionSender(),
                new NotificationsSubscriptionsService());
    }

    private NotificationPushRequest push(String userId, String type) {
        NotificationPushRequest request = new NotificationPushRequest();
        request.setUserId(userId);
        request.setType(type);
        request.setTitle("title");
        request.setContent("content");
        return request;
    }
}
