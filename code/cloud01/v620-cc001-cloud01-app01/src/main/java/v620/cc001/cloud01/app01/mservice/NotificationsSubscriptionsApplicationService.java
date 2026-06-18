package v620.cc001.cloud01.app01.mservice;

import v620.base.helper.career.NotificationsSubscriptionsService;
import v620.cc001.base.common.dto.career.NotificationConstants;
import v620.cc001.base.common.dto.career.NotificationOperationResult;
import v620.cc001.base.common.dto.career.NotificationPushRequest;
import v620.cc001.base.common.dto.career.NotificationRecordDto;
import v620.cc001.base.common.dto.career.NotificationUnreadCountDto;
import v620.cc001.base.common.dto.career.SubscriptionGrantRequest;
import v620.cc001.base.common.dto.career.SubscriptionQuotaDto;
import v620.cc001.base.common.dto.career.SubscriptionSendRequest;
import v620.cc001.base.common.dto.career.SubscriptionSendResult;
import v620.cc001.base.common.dto.career.WeeklyReportSummaryDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Application boundary for in-app notifications, subscription quotas and weekly report notifications.
 */
public class NotificationsSubscriptionsApplicationService {

    private final NotificationStorage notificationStorage;
    private final SubscriptionQuotaStorage quotaStorage;
    private final SubscriptionSender subscriptionSender;
    private final NotificationsSubscriptionsService helper;

    public NotificationsSubscriptionsApplicationService() {
        this(new InMemoryNotificationStorage(), new InMemorySubscriptionQuotaStorage(),
                new UnavailableSubscriptionSender(), new NotificationsSubscriptionsService());
    }

    public NotificationsSubscriptionsApplicationService(NotificationStorage notificationStorage,
                                                         SubscriptionQuotaStorage quotaStorage,
                                                         SubscriptionSender subscriptionSender,
                                                         NotificationsSubscriptionsService helper) {
        this.notificationStorage = notificationStorage;
        this.quotaStorage = quotaStorage;
        this.subscriptionSender = subscriptionSender;
        this.helper = helper;
    }

    public NotificationOperationResult pushBestEffort(NotificationPushRequest request) {
        try {
            if (request == null || !hasText(request.getUserId())) {
                return result(NotificationConstants.RESULT_SKIPPED, "MISSING_USER", null, 0);
            }
            NotificationRecordDto notification = new NotificationRecordDto();
            notification.setUserId(request.getUserId().trim());
            notification.setType(request.getType());
            notification.setTitle(firstText(request.getTitle(), helper.labelForType(request.getType())));
            notification.setContent(request.getContent());
            notification.setLink(request.getLink());
            notification.setReadFlag(Boolean.FALSE);
            notification.setCreatedAt(LocalDateTime.now());
            notification = helper.decorate(notification);
            return result(NotificationConstants.RESULT_OK, "CREATED", notificationStorage.save(notification), 1);
        } catch (Exception ex) {
            return result(NotificationConstants.RESULT_FAILED, ex.toString(), null, 0);
        }
    }

    public List<NotificationRecordDto> list(String userId) {
        String safeUserId = requireUserId(userId);
        List<NotificationRecordDto> out = new ArrayList<NotificationRecordDto>();
        for (NotificationRecordDto notification : notificationStorage.listByUser(safeUserId)) {
            out.add(helper.decorate(notification));
        }
        return out;
    }

    public NotificationUnreadCountDto unreadCount(String userId) {
        String safeUserId = requireUserId(userId);
        NotificationUnreadCountDto count = new NotificationUnreadCountDto();
        count.setUserId(safeUserId);
        count.setCount(Integer.valueOf(helper.unreadCount(notificationStorage.listByUser(safeUserId), safeUserId)));
        return count;
    }

    public NotificationOperationResult markRead(String userId, String notificationId) {
        String safeUserId = requireUserId(userId);
        NotificationRecordDto notification = requireNotification(notificationId);
        if (!helper.belongsTo(notification, safeUserId)) {
            return helper.ownershipFailure("You do not own this notification");
        }
        if (!Boolean.TRUE.equals(notification.getReadFlag())) {
            notification.setReadFlag(Boolean.TRUE);
            notificationStorage.save(notification);
            return result(NotificationConstants.RESULT_OK, "READ", helper.decorate(notification), 1);
        }
        return result(NotificationConstants.RESULT_OK, "ALREADY_READ", helper.decorate(notification), 0);
    }

    public NotificationOperationResult markAllRead(String userId) {
        String safeUserId = requireUserId(userId);
        int updated = 0;
        for (NotificationRecordDto notification : notificationStorage.listByUser(safeUserId)) {
            if (!Boolean.TRUE.equals(notification.getReadFlag())) {
                notification.setReadFlag(Boolean.TRUE);
                notificationStorage.save(notification);
                updated++;
            }
        }
        return result(NotificationConstants.RESULT_OK, "READ_ALL", null, updated);
    }

    public NotificationOperationResult delete(String userId, String notificationId) {
        String safeUserId = requireUserId(userId);
        NotificationRecordDto notification = requireNotification(notificationId);
        if (!helper.belongsTo(notification, safeUserId)) {
            return helper.ownershipFailure("You do not own this notification");
        }
        notificationStorage.delete(notificationId);
        return result(NotificationConstants.RESULT_OK, "DELETED", null, 1);
    }

    public List<SubscriptionQuotaDto> recordGrant(SubscriptionGrantRequest request) {
        String safeUserId = requireUserId(request == null ? null : request.getUserId());
        List<SubscriptionQuotaDto> out = new ArrayList<SubscriptionQuotaDto>();
        Map<String, Integer> delta = helper.grantQuotaDelta(request.getResults());
        for (Map.Entry<String, Integer> entry : delta.entrySet()) {
            out.add(quotaStorage.addQuota(safeUserId, entry.getKey(), entry.getValue().intValue()));
        }
        return out;
    }

    public List<SubscriptionQuotaDto> listQuota(String userId) {
        return quotaStorage.listByUser(requireUserId(userId));
    }

    public SubscriptionSendResult sendSubscription(SubscriptionSendRequest request) {
        String safeUserId = requireUserId(request == null ? null : request.getUserId());
        SubscriptionQuotaDto quota = quotaStorage.find(safeUserId, request.getTemplateId());
        SubscriptionSendResult decision = helper.decideSend(request, quota, subscriptionSender.isAvailable());
        if (!NotificationConstants.RESULT_OK.equals(decision.getStatus())) {
            return decision;
        }
        SubscriptionQuotaDto remaining = quotaStorage.consumeOne(safeUserId, request.getTemplateId());
        SubscriptionSendResult sent = subscriptionSender.send(request);
        if (sent == null) {
            sent = decision;
        }
        sent.setRemainingAfterSend(remaining == null ? decision.getRemainingAfterSend() : remaining.getRemaining());
        return sent;
    }

    public WeeklyReportSummaryDto runWeeklyReport(String userId, List<String> highlights) {
        String safeUserId = requireUserId(userId);
        WeeklyReportSummaryDto summary = helper.weeklyReport(safeUserId, highlights, LocalDateTime.now());
        if (Boolean.TRUE.equals(summary.getDelivered())) {
            NotificationPushRequest request = new NotificationPushRequest();
            request.setUserId(safeUserId);
            request.setType(NotificationConstants.TYPE_WEEKLY_REPORT);
            request.setTitle("Weekly CyanCruise report");
            request.setContent(summary.getSummary());
            request.setLink("index.html#career-plan");
            pushBestEffort(request);
        }
        return summary;
    }

    private NotificationRecordDto requireNotification(String notificationId) {
        if (!hasText(notificationId)) {
            throw new IllegalArgumentException("notificationId is required");
        }
        NotificationRecordDto notification = notificationStorage.find(notificationId.trim());
        if (notification == null) {
            throw new IllegalArgumentException("notification not found");
        }
        return notification;
    }

    private NotificationOperationResult result(String status, String message, NotificationRecordDto notification, int updated) {
        NotificationOperationResult result = new NotificationOperationResult();
        result.setStatus(status);
        result.setMessage(message);
        result.setNotification(notification);
        result.setUpdated(Integer.valueOf(updated));
        return result;
    }

    private String requireUserId(String userId) {
        if (!hasText(userId)) {
            throw new IllegalArgumentException("userId is required");
        }
        return userId.trim();
    }

    private String firstText(String first, String second) {
        return hasText(first) ? first.trim() : second;
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
