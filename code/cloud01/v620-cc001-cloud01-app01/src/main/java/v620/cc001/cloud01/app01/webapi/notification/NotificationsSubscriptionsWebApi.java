package v620.cc001.cloud01.app01.webapi.notification;

import kd.bos.openapi.common.custom.annotation.ApiController;
import kd.bos.openapi.common.custom.annotation.ApiMapping;
import kd.bos.openapi.common.custom.annotation.ApiPostMapping;
import kd.bos.openapi.common.custom.annotation.ApiRequestBody;
import kd.bos.openapi.common.custom.annotation.ApiResponseBody;
import v620.cc001.base.common.dto.career.NotificationOperationResult;
import v620.cc001.base.common.dto.career.NotificationPushRequest;
import v620.cc001.base.common.dto.career.NotificationRecordDto;
import v620.cc001.base.common.dto.career.NotificationUnreadCountDto;
import v620.cc001.base.common.dto.career.SubscriptionGrantRequest;
import v620.cc001.base.common.dto.career.SubscriptionQuotaDto;
import v620.cc001.base.common.dto.career.SubscriptionSendRequest;
import v620.cc001.base.common.dto.career.SubscriptionSendResult;
import v620.cc001.base.common.dto.career.WeeklyReportSummaryDto;
import v620.cc001.cloud01.app01.mservice.application.NotificationsSubscriptionsApplicationService;

import java.util.List;

@ApiController(value = "notificationsSubscriptionsWebApi", desc = "CyanCruise notifications and subscriptions API")
@ApiMapping("/cc001/notifications")
public class NotificationsSubscriptionsWebApi {

    private final NotificationsSubscriptionsApplicationService applicationService;

    public NotificationsSubscriptionsWebApi() {
        this(new NotificationsSubscriptionsApplicationService());
    }

    public NotificationsSubscriptionsWebApi(NotificationsSubscriptionsApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @ApiPostMapping(value = "/list", desc = "List user notifications", methodParamNames = {"userId"})
    public @ApiResponseBody(value = "Notifications") List<NotificationRecordDto> list(
            @ApiRequestBody(value = "userId", required = true) String userId) {
        return applicationService.list(userId);
    }

    @ApiPostMapping(value = "/unread-count", desc = "Unread notification count", methodParamNames = {"userId"})
    public @ApiResponseBody(value = "Unread count") NotificationUnreadCountDto unreadCount(
            @ApiRequestBody(value = "userId", required = true) String userId) {
        return applicationService.unreadCount(userId);
    }

    @ApiPostMapping(value = "/push", desc = "Best-effort notification push", methodParamNames = {"request"})
    public @ApiResponseBody(value = "Push result") NotificationOperationResult push(
            @ApiRequestBody(value = "request", required = true) NotificationPushRequest request) {
        return applicationService.pushBestEffort(request);
    }

    @ApiPostMapping(value = "/read", desc = "Mark notification read", methodParamNames = {"userId", "notificationId"})
    public @ApiResponseBody(value = "Read result") NotificationOperationResult read(
            @ApiRequestBody(value = "userId", required = true) String userId,
            @ApiRequestBody(value = "notificationId", required = true) String notificationId) {
        return applicationService.markRead(userId, notificationId);
    }

    @ApiPostMapping(value = "/read-all", desc = "Mark all notifications read", methodParamNames = {"userId"})
    public @ApiResponseBody(value = "Read all result") NotificationOperationResult readAll(
            @ApiRequestBody(value = "userId", required = true) String userId) {
        return applicationService.markAllRead(userId);
    }

    @ApiPostMapping(value = "/delete", desc = "Delete notification", methodParamNames = {"userId", "notificationId"})
    public @ApiResponseBody(value = "Delete result") NotificationOperationResult delete(
            @ApiRequestBody(value = "userId", required = true) String userId,
            @ApiRequestBody(value = "notificationId", required = true) String notificationId) {
        return applicationService.delete(userId, notificationId);
    }

    @ApiPostMapping(value = "/subscription/grant", desc = "Record subscription grant", methodParamNames = {"request"})
    public @ApiResponseBody(value = "Quota after grant") List<SubscriptionQuotaDto> grant(
            @ApiRequestBody(value = "request", required = true) SubscriptionGrantRequest request) {
        return applicationService.recordGrant(request);
    }

    @ApiPostMapping(value = "/subscription/quota", desc = "List subscription quota", methodParamNames = {"userId"})
    public @ApiResponseBody(value = "Quota") List<SubscriptionQuotaDto> quota(
            @ApiRequestBody(value = "userId", required = true) String userId) {
        return applicationService.listQuota(userId);
    }

    @ApiPostMapping(value = "/subscription/send", desc = "Attempt subscription send", methodParamNames = {"request"})
    public @ApiResponseBody(value = "Send result") SubscriptionSendResult send(
            @ApiRequestBody(value = "request", required = true) SubscriptionSendRequest request) {
        return applicationService.sendSubscription(request);
    }

    @ApiPostMapping(value = "/weekly-report/run", desc = "Run weekly report notification", methodParamNames = {"userId", "highlights"})
    public @ApiResponseBody(value = "Weekly report") WeeklyReportSummaryDto weeklyReport(
            @ApiRequestBody(value = "userId", required = true) String userId,
            @ApiRequestBody(value = "highlights", required = false) List<String> highlights) {
        return applicationService.runWeeklyReport(userId, highlights);
    }
}
