package v620.cc001.cloud01.app01.mservice.storage.impl;

import v620.cc001.cloud01.app01.mservice.storage.*;
import v620.cc001.base.common.dto.career.CareerPlanRecordDto;
import v620.cc001.cloud01.app01.mservice.datamodel.CyanCruiseDatamodelObjects;
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
            record = new CosmicDatamodelRecord(CyanCruiseDatamodelObjects.CAREER_PLAN)
                    .set(CyanCruiseDatamodelObjects.USER_ID, userId)
                    .set(CyanCruiseDatamodelObjects.CREATED_AT, plan == null ? LocalDateTime.now() : plan.getGeneratedAt());
        }
        gateway.save(record.set("target_role", plan == null ? null : plan.getTargetRole())
                .set("model_used", plan == null ? null : plan.getModelUsed())
                .set("tokens_consumed", plan == null ? null : plan.getTokensConsumed())
                .set("version", plan == null ? null : plan.getVersion())
                .set("plan_json", plan)
                .set(CyanCruiseDatamodelObjects.UPDATED_AT, LocalDateTime.now()));
    }

    public boolean exists(String userId) {
        return find(userId) != null;
    }

    private CosmicDatamodelRecord find(final String userId) {
        return gateway.findOne(CyanCruiseDatamodelObjects.CAREER_PLAN, new CosmicRecordFilter() {
            public boolean matches(CosmicDatamodelRecord record) {
                return DatamodelFieldMapper.same(userId, record.get(CyanCruiseDatamodelObjects.USER_ID));
            }
        });
    }
}
