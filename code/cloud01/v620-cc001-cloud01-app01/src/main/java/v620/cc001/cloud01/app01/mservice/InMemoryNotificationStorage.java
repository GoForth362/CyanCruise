package v620.cc001.cloud01.app01.mservice;

import v620.cc001.base.common.dto.career.NotificationRecordDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryNotificationStorage implements NotificationStorage {

    private final Map<String, NotificationRecordDto> notifications = new ConcurrentHashMap<String, NotificationRecordDto>();
    private final AtomicLong ids = new AtomicLong(1L);

    public NotificationRecordDto save(NotificationRecordDto notification) {
        if (notification.getNotificationId() == null || notification.getNotificationId().trim().length() == 0) {
            notification.setNotificationId(String.valueOf(ids.getAndIncrement()));
        }
        notifications.put(notification.getNotificationId(), notification);
        return notification;
    }

    public NotificationRecordDto find(String notificationId) {
        return notifications.get(notificationId);
    }

    public List<NotificationRecordDto> listByUser(String userId) {
        List<NotificationRecordDto> out = new ArrayList<NotificationRecordDto>();
        for (NotificationRecordDto notification : notifications.values()) {
            if (userId != null && userId.equals(notification.getUserId())) {
                out.add(notification);
            }
        }
        Collections.sort(out, new Comparator<NotificationRecordDto>() {
            public int compare(NotificationRecordDto left, NotificationRecordDto right) {
                if (left.getCreatedAt() == null && right.getCreatedAt() == null) return 0;
                if (left.getCreatedAt() == null) return 1;
                if (right.getCreatedAt() == null) return -1;
                return right.getCreatedAt().compareTo(left.getCreatedAt());
            }
        });
        return out;
    }

    public void delete(String notificationId) {
        notifications.remove(notificationId);
    }
}
