package v620.cc001.cloud01.app01.mservice;

import v620.cc001.base.common.dto.career.CareerPlanRecordDto;
import v620.cc001.cloud01.app01.mservice.datamodel.CareerLoopDatamodelObjects;
import v620.cc001.cloud01.app01.mservice.datamodel.CosmicDatamodelGateway;
import v620.cc001.cloud01.app01.mservice.datamodel.CosmicDatamodelRecord;
import v620.cc001.cloud01.app01.mservice.datamodel.CosmicRecordFilter;
import v620.cc001.cloud01.app01.mservice.datamodel.DatamodelFieldMapper;

import java.time.LocalDateTime;

public class CosmicCareerPlanStorage implements CareerPlanStorage {

    private final CosmicDatamodelGateway gateway;

    public CosmicCareerPlanStorage(CosmicDatamodelGateway gateway) {
        this.gateway = gateway;
    }

    public CareerPlanRecordDto load(String userId) {
        CosmicDatamodelRecord record = find(userId);
        return record == null ? null : (CareerPlanRecordDto) record.get("plan_json");
    }

    public void save(String userId, CareerPlanRecordDto plan) {
        CosmicDatamodelRecord record = find(userId);
        if (record == null) {
            record = new CosmicDatamodelRecord(CareerLoopDatamodelObjects.CAREER_PLAN)
                    .set(CareerLoopDatamodelObjects.USER_ID, userId)
                    .set(CareerLoopDatamodelObjects.CREATED_AT, plan == null ? LocalDateTime.now() : plan.getGeneratedAt());
        }
        gateway.save(record.set("target_role", plan == null ? null : plan.getTargetRole())
                .set("model_used", plan == null ? null : plan.getModelUsed())
                .set("tokens_consumed", plan == null ? null : plan.getTokensConsumed())
                .set("version", plan == null ? null : plan.getVersion())
                .set("plan_json", plan)
                .set(CareerLoopDatamodelObjects.UPDATED_AT, LocalDateTime.now()));
    }

    public boolean exists(String userId) {
        return find(userId) != null;
    }

    private CosmicDatamodelRecord find(final String userId) {
        return gateway.findOne(CareerLoopDatamodelObjects.CAREER_PLAN, new CosmicRecordFilter() {
            public boolean matches(CosmicDatamodelRecord record) {
                return DatamodelFieldMapper.same(userId, record.get(CareerLoopDatamodelObjects.USER_ID));
            }
        });
    }
}
