package v620.cc001.cloud01.app01.mservice;

import v620.cc001.base.common.dto.career.NotificationRecordDto;

import java.util.List;

public interface NotificationStorage {

    NotificationRecordDto save(NotificationRecordDto notification);

    NotificationRecordDto find(String notificationId);

    List<NotificationRecordDto> listByUser(String userId);

    void delete(String notificationId);
}
