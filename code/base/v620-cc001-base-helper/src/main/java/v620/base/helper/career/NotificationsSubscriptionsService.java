package v620.base.helper.career;

import v620.cc001.base.common.dto.career.NotificationConstants;
import v620.cc001.base.common.dto.career.NotificationOperationResult;
import v620.cc001.base.common.dto.career.NotificationRecordDto;
import v620.cc001.base.common.dto.career.SubscriptionQuotaDto;
import v620.cc001.base.common.dto.career.SubscriptionSendRequest;
import v620.cc001.base.common.dto.career.SubscriptionSendResult;
import v620.cc001.base.common.dto.career.WeeklyReportSummaryDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Pure Java rules for notification grouping, ownership, subscription quota and weekly summaries.
 */
public class NotificationsSubscriptionsService {

    public NotificationRecordDto decorate(NotificationRecordDto notification) {
        if (notification == null) {
            return null;
        }
        String type = normalizeType(notification.getType());
        notification.setType(type);
        notification.setGroupKey(groupForType(type));
        notification.setLabel(labelForType(type));
        notification.setIconKey(iconForType(type));
        if (!hasText(notification.getLink())) {
            notification.setLink(defaultLinkForType(type));
        }
        if (notification.getReadFlag() == null) {
            notification.setReadFlag(Boolean.FALSE);
        }
        return notification;
    }

    public int unreadCount(List<NotificationRecordDto> notifications, String userId) {
        int count = 0;
        if (notifications == null) {
            return count;
        }
        for (NotificationRecordDto notification : notifications) {
            if (belongsTo(notification, userId) && !Boolean.TRUE.equals(notification.getReadFlag())) {
                count++;
            }
        }
        return count;
    }

    public boolean belongsTo(NotificationRecordDto notification, String userId) {
        return notification != null && hasText(userId) && userId.trim().equals(notification.getUserId());
    }

    public NotificationOperationResult ownershipFailure(String message) {
        NotificationOperationResult result = new NotificationOperationResult();
        result.setStatus(NotificationConstants.RESULT_FAILED);
        result.setMessage(hasText(message) ? message : "Notification ownership check failed");
        result.setUpdated(Integer.valueOf(0));
        return result;
    }

    public Map<String, Integer> grantQuotaDelta(Map<String, String> grantResults) {
        Map<String, Integer> delta = new LinkedHashMap<String, Integer>();
        if (grantResults == null) {
            return delta;
        }
        for (Map.Entry<String, String> entry : grantResults.entrySet()) {
            if (hasText(entry.getKey()) && "accept".equalsIgnoreCase(trim(entry.getValue()))) {
                delta.put(entry.getKey().trim(), Integer.valueOf(1));
            }
        }
        return delta;
    }

    public SubscriptionSendResult decideSend(SubscriptionSendRequest request,
                                             SubscriptionQuotaDto quota,
                                             boolean providerAvailable) {
        SubscriptionSendResult result = new SubscriptionSendResult();
        result.setUserId(request == null ? null : request.getUserId());
        result.setTemplateId(request == null ? null : request.getTemplateId());
        if (request == null || !hasText(request.getUserId())) {
            return skipped(result, "MISSING_USER");
        }
        if (!hasText(request.getTemplateId())) {
            return skipped(result, "MISSING_TEMPLATE");
        }
        if (!hasText(request.getRecipientId())) {
            return skipped(result, "MISSING_RECIPIENT");
        }
        if (quota == null || quota.getRemaining() == null || quota.getRemaining().intValue() <= 0) {
            return skipped(result, "NO_QUOTA");
        }
        if (!providerAvailable) {
            return unavailable(result, "PROVIDER_UNAVAILABLE");
        }
        result.setStatus(NotificationConstants.RESULT_OK);
        result.setReason("READY");
        result.setRemainingAfterSend(Integer.valueOf(quota.getRemaining().intValue() - 1));
        return result;
    }

    public WeeklyReportSummaryDto weeklyReport(String userId, List<String> highlights, LocalDateTime now) {
        WeeklyReportSummaryDto summary = new WeeklyReportSummaryDto();
        summary.setUserId(userId);
        summary.setRunAt(now == null ? LocalDateTime.now() : now);
        List<String> safeHighlights = cleanHighlights(highlights);
        summary.setHighlights(safeHighlights);
        summary.setActivityCount(Integer.valueOf(safeHighlights.size()));
        if (safeHighlights.isEmpty()) {
            summary.setDelivered(Boolean.FALSE);
            summary.setStatus(NotificationConstants.RESULT_SKIPPED);
            summary.setSummary("近期活动还不够生成每周回顾。");
            return summary;
        }
        summary.setDelivered(Boolean.TRUE);
        summary.setStatus(NotificationConstants.RESULT_OK);
        StringBuilder builder = new StringBuilder("本周进展：");
        builder.append(safeHighlights.get(0));
        if (safeHighlights.size() > 1) {
            builder.append("。下周重点：").append(safeHighlights.get(1));
        }
        summary.setSummary(builder.toString());
        return summary;
    }

    public String groupForType(String type) {
        String normalized = normalizeType(type);
        if (NotificationConstants.TYPE_AI_PROACTIVE.equals(normalized)) {
            return NotificationConstants.GROUP_AI;
        }
        if (NotificationConstants.TYPE_SYSTEM.equals(normalized)
                || NotificationConstants.TYPE_ADMIN_BROADCAST.equals(normalized)) {
            return NotificationConstants.GROUP_SYSTEM;
        }
        if (NotificationConstants.TYPE_INTERVIEW_REPORT.equals(normalized)
                || NotificationConstants.TYPE_ASSESSMENT_RESULT.equals(normalized)
                || NotificationConstants.TYPE_RESUME_DIAGNOSIS.equals(normalized)
                || NotificationConstants.TYPE_WEEKLY_REPORT.equals(normalized)
                || NotificationConstants.TYPE_STREAK_WARNING.equals(normalized)
                || NotificationConstants.TYPE_MARKET_LIKE.equals(normalized)) {
            return NotificationConstants.GROUP_CAREER;
        }
        return NotificationConstants.GROUP_SYSTEM;
    }

    public String normalizeType(String type) {
        if (!hasText(type)) {
            return NotificationConstants.TYPE_SYSTEM;
        }
        String value = type.trim().toUpperCase(Locale.ROOT);
        if ("INTERVIEW_COMPLETED".equals(value)) {
            return NotificationConstants.TYPE_INTERVIEW_REPORT;
        }
        if ("ASSESSMENT_DONE".equals(value)) {
            return NotificationConstants.TYPE_ASSESSMENT_RESULT;
        }
        if ("RESUME_REVIEWED".equals(value)) {
            return NotificationConstants.TYPE_RESUME_DIAGNOSIS;
        }
        if (NotificationConstants.TYPE_INTERVIEW_REPORT.equals(value)
                || NotificationConstants.TYPE_ASSESSMENT_RESULT.equals(value)
                || NotificationConstants.TYPE_RESUME_DIAGNOSIS.equals(value)
                || NotificationConstants.TYPE_WEEKLY_REPORT.equals(value)
                || NotificationConstants.TYPE_STREAK_WARNING.equals(value)
                || NotificationConstants.TYPE_MARKET_LIKE.equals(value)
                || NotificationConstants.TYPE_AI_PROACTIVE.equals(value)
                || NotificationConstants.TYPE_ADMIN_BROADCAST.equals(value)
                || NotificationConstants.TYPE_SYSTEM.equals(value)) {
            return value;
        }
        return NotificationConstants.TYPE_SYSTEM;
    }

    public String labelForType(String type) {
        String normalized = normalizeType(type);
        if (NotificationConstants.TYPE_INTERVIEW_REPORT.equals(normalized)) return "面试复盘";
        if (NotificationConstants.TYPE_ASSESSMENT_RESULT.equals(normalized)) return "测评结果";
        if (NotificationConstants.TYPE_RESUME_DIAGNOSIS.equals(normalized)) return "简历诊断";
        if (NotificationConstants.TYPE_WEEKLY_REPORT.equals(normalized)) return "每周回顾";
        if (NotificationConstants.TYPE_STREAK_WARNING.equals(normalized)) return "练习提醒";
        if (NotificationConstants.TYPE_MARKET_LIKE.equals(normalized)) return "资源动态";
        if (NotificationConstants.TYPE_AI_PROACTIVE.equals(normalized)) return "智能助手";
        if (NotificationConstants.TYPE_ADMIN_BROADCAST.equals(normalized)) return "管理员公告";
        return "系统消息";
    }

    public String iconForType(String type) {
        String normalized = normalizeType(type);
        if (NotificationConstants.TYPE_INTERVIEW_REPORT.equals(normalized)) return "mic";
        if (NotificationConstants.TYPE_ASSESSMENT_RESULT.equals(normalized)) return "brain";
        if (NotificationConstants.TYPE_RESUME_DIAGNOSIS.equals(normalized)) return "file-text";
        if (NotificationConstants.TYPE_WEEKLY_REPORT.equals(normalized)) return "bar-chart";
        if (NotificationConstants.TYPE_STREAK_WARNING.equals(normalized)) return "flame";
        if (NotificationConstants.TYPE_MARKET_LIKE.equals(normalized)) return "heart";
        if (NotificationConstants.TYPE_AI_PROACTIVE.equals(normalized)) return "assistant";
        if (NotificationConstants.TYPE_ADMIN_BROADCAST.equals(normalized)) return "megaphone";
        return "notification";
    }

    public String defaultLinkForType(String type) {
        String normalized = normalizeType(type);
        if (NotificationConstants.TYPE_INTERVIEW_REPORT.equals(normalized)) return "index.html#interview";
        if (NotificationConstants.TYPE_ASSESSMENT_RESULT.equals(normalized)) return "index.html#assessment";
        if (NotificationConstants.TYPE_RESUME_DIAGNOSIS.equals(normalized)) return "index.html#resume-diagnosis";
        if (NotificationConstants.TYPE_WEEKLY_REPORT.equals(normalized)) return "index.html#career-plan";
        if (NotificationConstants.TYPE_AI_PROACTIVE.equals(normalized)) return "index.html#assistant";
        return "index.html#messages";
    }

    private SubscriptionSendResult skipped(SubscriptionSendResult result, String reason) {
        result.setStatus(NotificationConstants.RESULT_SKIPPED);
        result.setReason(reason);
        result.setRemainingAfterSend(null);
        return result;
    }

    private SubscriptionSendResult unavailable(SubscriptionSendResult result, String reason) {
        result.setStatus(NotificationConstants.RESULT_UNAVAILABLE);
        result.setReason(reason);
        result.setRemainingAfterSend(null);
        return result;
    }

    private List<String> cleanHighlights(List<String> highlights) {
        List<String> out = new ArrayList<String>();
        if (highlights == null) {
            return out;
        }
        for (String highlight : highlights) {
            if (hasText(highlight)) {
                out.add(highlight.trim());
            }
        }
        return out;
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
