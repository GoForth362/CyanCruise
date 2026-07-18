package v620.cc001.cloud01.app01.mservice.storage.impl;

import v620.cc001.base.common.dto.career.CareerPlanRecordDto;
import v620.cc001.cloud01.app01.mservice.storage.CareerPlanStorage;
import v620.cc001.cloud01.app01.mservice.storage.StudyCenterStorage;

/** Adapts the independent study-plan slot to the shared plan engine. */
public class StudyPlanStorageAdapter implements CareerPlanStorage {
    private final StudyCenterStorage storage;
    private final String direction;
    public StudyPlanStorageAdapter(StudyCenterStorage storage, String direction) { this.storage = storage; this.direction = direction; }
    public CareerPlanRecordDto load(String userId) { return storage.loadPlan(userId, direction); }
    public void save(String userId, CareerPlanRecordDto plan) { storage.savePlan(userId, direction, plan); }
    public boolean exists(String userId) { return storage.loadPlan(userId, direction) != null; }
}
