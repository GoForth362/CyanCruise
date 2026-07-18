package v620.cc001.cloud01.app01.mservice.storage;

import v620.cc001.base.common.dto.career.CareerDailyTaskDto;

import java.util.List;

/** Storage boundary backed by the existing career task business object. */
public interface CareerDailyTaskStorage {

    List<CareerDailyTaskDto> list(String userId);

    CareerDailyTaskDto find(String userId, String taskId);

    void save(String userId, CareerDailyTaskDto task);
}
