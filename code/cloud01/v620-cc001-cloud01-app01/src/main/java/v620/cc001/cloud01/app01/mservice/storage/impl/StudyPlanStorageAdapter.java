package v620.cc001.cloud01.app01.mservice.storage.impl;

import v620.cc001.base.common.dto.career.CareerPlanRecordDto;
import v620.cc001.cloud01.app01.mservice.storage.CareerPlanStorage;
import v620.cc001.cloud01.app01.mservice.storage.StudyCenterStorage;

/** Adapts the independent study-plan slot to the shared plan engine. */
public class StudyPlanStorageAdapter implements CareerPlanStorage {
    private final StudyCenterStorage storage;
    public StudyPlanStorageAdapter(StudyCenterStorage storage) { this.storage = storage; }
    public CareerPlanRecordDto load(String userId) { return storage.loadPlan(userId); }
    public void save(String userId, CareerPlanRecordDto plan) { storage.savePlan(userId, plan); }
    public boolean exists(String userId) { return storage.loadPlan(userId) != null; }
}
