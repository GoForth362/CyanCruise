package v620.cc001.cloud01.app01.mservice.storage.impl;

import java.util.List;
import v620.cc001.base.common.dto.career.CareerDailyTaskDto;
import v620.cc001.cloud01.app01.mservice.storage.CareerDailyTaskStorage;
import v620.cc001.cloud01.app01.mservice.storage.StudyCenterStorage;

/** Adapts independent study daily tasks to the shared daily execution engine. */
public class StudyDailyTaskStorageAdapter implements CareerDailyTaskStorage {
    private final StudyCenterStorage storage;
    public StudyDailyTaskStorageAdapter(StudyCenterStorage storage) { this.storage = storage; }
    public List<CareerDailyTaskDto> list(String userId) { return storage.listDailyTasks(userId); }
    public CareerDailyTaskDto find(String userId, String taskId) { return storage.findDailyTask(userId, taskId); }
    public void save(String userId, CareerDailyTaskDto task) { storage.saveDailyTask(userId, task); }
}
