package v620.cc001.cloud01.app01.mservice.storage;

import v620.cc001.base.common.dto.career.CareerPlanRecordDto;

/**
 * Replaceable storage boundary for the current career plan.
 */
public interface CareerPlanStorage {

    CareerPlanRecordDto load(String userId);

    void save(String userId, CareerPlanRecordDto plan);

    boolean exists(String userId);
}
