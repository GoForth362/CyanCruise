package v620.base.helper.career;

import org.junit.jupiter.api.Test;
import v620.cc001.base.common.dto.career.NotificationConstants;
import v620.cc001.base.common.dto.career.NotificationRecordDto;
import v620.cc001.base.common.dto.career.SubscriptionQuotaDto;
import v620.cc001.base.common.dto.career.SubscriptionSendRequest;
import v620.cc001.base.common.dto.career.SubscriptionSendResult;
import v620.cc001.base.common.dto.career.WeeklyReportSummaryDto;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificationsSubscriptionsServiceTest {

    private final NotificationsSubscriptionsService service = new NotificationsSubscriptionsService();

    @Test
    void knownAndUnknownTypesAreGroupedSafely() {
        NotificationRecordDto known = notification("n1", "u1", "INTERVIEW_COMPLETED", false);
        NotificationRecordDto unknown = notification("n2", "u1", "SOMETHING_NEW", false);

        service.decorate(known);
        service.decorate(unknown);

        assertEquals(NotificationConstants.TYPE_INTERVIEW_REPORT, known.getType());
        assertEquals(NotificationConstants.GROUP_CAREER, known.getGroupKey());
        assertEquals(NotificationConstants.TYPE_SYSTEM, unknown.getType());
        assertEquals(NotificationConstants.GROUP_SYSTEM, unknown.getGroupKey());
    }

    @Test
    void unreadCountUsesUserOwnership() {
        int count = service.unreadCount(Arrays.asList(
                notification("n1", "u1", "SYSTEM", false),
                notification("n2", "u1", "SYSTEM", true),
                notification("n3", "u2", "SYSTEM", false)), "u1");

        assertEquals(1, count);
    }

    @Test
    void ownershipFailureIsExplicit() {
        assertEquals(NotificationConstants.RESULT_FAILED, service.ownershipFailure("not yours").getStatus());
        assertTrue(service.ownershipFailure("not yours").getMessage().contains("not yours"));
    }

    @Test
    void grantQuotaOnlyIncrementsAcceptedTemplates() {
        Map<String, String> grants = new LinkedHashMap<String, String>();
        grants.put("tpl-weekly", "accept");
        grants.put("tpl-ai", "reject");
        grants.put("tpl-ban", "ban");

        Map<String, Integer> delta = service.grantQuotaDelta(grants);

        assertEquals(1, delta.size());
        assertEquals(Integer.valueOf(1), delta.get("tpl-weekly"));
    }

    @Test
    void sendDecisionSkipsUnsafeCasesAndAcceptsReadyCase() {
        SubscriptionSendRequest request = new SubscriptionSendRequest();
        request.setUserId("u1");
        request.setTemplateId("tpl-weekly");
        request.setRecipientId("openid");
        SubscriptionQuotaDto quota = new SubscriptionQuotaDto();
        quota.setRemaining(Integer.valueOf(2));

        SubscriptionSendResult ready = service.decideSend(request, quota, true);
        SubscriptionSendResult noQuota = service.decideSend(request, null, true);
        SubscriptionSendResult unavailable = service.decideSend(request, quota, false);

        assertEquals(NotificationConstants.RESULT_OK, ready.getStatus());
        assertEquals(Integer.valueOf(1), ready.getRemainingAfterSend());
        assertEquals(NotificationConstants.RESULT_SKIPPED, noQuota.getStatus());
        assertEquals("NO_QUOTA", noQuota.getReason());
        assertEquals(NotificationConstants.RESULT_UNAVAILABLE, unavailable.getStatus());
    }

    @Test
    void weeklyReportFallbackSkipsEmptyActivity() {
        WeeklyReportSummaryDto empty = service.weeklyReport("u1", Arrays.asList(" "), null);
        WeeklyReportSummaryDto delivered = service.weeklyReport("u1",
                Arrays.asList("completed one mock interview", "improve logic answers"), null);

        assertEquals(Boolean.FALSE, empty.getDelivered());
        assertEquals(NotificationConstants.RESULT_SKIPPED, empty.getStatus());
        assertEquals(Boolean.TRUE, delivered.getDelivered());
        assertTrue(delivered.getSummary().contains("completed one mock interview"));
    }

    private NotificationRecordDto notification(String id, String userId, String type, boolean read) {
        NotificationRecordDto notification = new NotificationRecordDto();
        notification.setNotificationId(id);
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle("title");
        notification.setReadFlag(Boolean.valueOf(read));
        return notification;
    }
}
