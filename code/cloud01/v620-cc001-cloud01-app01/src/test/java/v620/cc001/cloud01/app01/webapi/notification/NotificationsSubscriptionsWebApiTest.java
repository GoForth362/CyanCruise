package v620.cc001.cloud01.app01.webapi.notification;

import v620.cc001.cloud01.app01.mservice.notification.impl.InMemoryNotificationStorage;
import v620.cc001.cloud01.app01.mservice.notification.impl.InMemorySubscriptionQuotaStorage;
import v620.cc001.cloud01.app01.mservice.notification.impl.UnavailableSubscriptionSender;
import org.junit.jupiter.api.Test;
import v620.base.helper.career.NotificationsSubscriptionsService;
import v620.cc001.base.common.dto.career.NotificationConstants;
import v620.cc001.base.common.dto.career.NotificationOperationResult;
import v620.cc001.base.common.dto.career.NotificationPushRequest;
import v620.cc001.base.common.dto.career.SubscriptionGrantRequest;
import v620.cc001.cloud01.app01.mservice.notification.impl.InMemoryNotificationStorage;
import v620.cc001.cloud01.app01.mservice.notification.impl.InMemorySubscriptionQuotaStorage;
import v620.cc001.cloud01.app01.mservice.application.NotificationsSubscriptionsApplicationService;
import v620.cc001.cloud01.app01.mservice.notification.impl.UnavailableSubscriptionSender;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class NotificationsSubscriptionsWebApiTest {

    @Test
    void webApiExposesNotificationAndSubscriptionContracts() {
        NotificationsSubscriptionsWebApi webApi = new NotificationsSubscriptionsWebApi(
                new NotificationsSubscriptionsApplicationService(
                        new InMemoryNotificationStorage(),
                        new InMemorySubscriptionQuotaStorage(),
                        new UnavailableSubscriptionSender(),
                        new NotificationsSubscriptionsService()));
        NotificationPushRequest push = new NotificationPushRequest();
        push.setUserId("api-user");
        push.setType(NotificationConstants.TYPE_ASSESSMENT_RESULT);
        push.setTitle("Assessment done");
        push.setContent("Result is ready");
        NotificationOperationResult pushed = webApi.push(push);
        SubscriptionGrantRequest grant = new SubscriptionGrantRequest();
        grant.setUserId("api-user");
        Map<String, String> grants = new LinkedHashMap<String, String>();
        grants.put("tpl-weekly", "accept");
        grant.setResults(grants);

        assertEquals(NotificationConstants.RESULT_OK, pushed.getStatus());
        assertEquals(Integer.valueOf(1), webApi.unreadCount("api-user").getCount());
        assertFalse(webApi.list("api-user").isEmpty());
        assertEquals(1, webApi.grant(grant).size());
        assertEquals(Boolean.TRUE, webApi.weeklyReport("api-user", Arrays.asList("finished interview")).getDelivered());
    }
}
