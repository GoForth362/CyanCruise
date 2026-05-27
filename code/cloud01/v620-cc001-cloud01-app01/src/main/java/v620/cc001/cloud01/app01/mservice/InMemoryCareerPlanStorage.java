package v620.cc001.cloud01.app01.mservice;

import v620.cc001.base.common.dto.career.CareerPlanRecordDto;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Non-durable career plan storage for focused migration tests.
 */
public class InMemoryCareerPlanStorage implements CareerPlanStorage {

    private static final Map<String, CareerPlanRecordDto> PLANS =
            new ConcurrentHashMap<String, CareerPlanRecordDto>();

    public CareerPlanRecordDto load(String userId) {
        return PLANS.get(userId);
    }

    public void save(String userId, CareerPlanRecordDto plan) {
        if (plan != null) {
            PLANS.put(userId, plan);
        }
    }

    public boolean exists(String userId) {
        return PLANS.containsKey(userId);
    }
}
