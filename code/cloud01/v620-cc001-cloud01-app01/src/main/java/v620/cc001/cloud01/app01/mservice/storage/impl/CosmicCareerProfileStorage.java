package v620.cc001.cloud01.app01.mservice.storage.impl;

import v620.cc001.cloud01.app01.mservice.storage.*;
import v620.cc001.base.common.dto.career.CareerUserProfileDto;
import v620.cc001.base.common.dto.career.CareerProfileDraftDto;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;
import v620.cc001.cloud01.app01.mservice.datamodel.CyanCruiseDatamodelObjects;
import v620.cc001.cloud01.app01.mservice.datamodel.CosmicDatamodelGateway;
import v620.cc001.cloud01.app01.mservice.datamodel.CosmicDatamodelRecord;
import v620.cc001.cloud01.app01.mservice.datamodel.CosmicRecordFilter;
import v620.cc001.cloud01.app01.mservice.datamodel.DatamodelFieldMapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CosmicCareerProfileStorage implements CareerProfileStorage {

    private final CosmicDatamodelGateway gateway;

    public CosmicCareerProfileStorage(CosmicDatamodelGateway gateway) {
        this.gateway = gateway;
    }

    public UserProfileSnapshot loadSnapshot(String userId) {
        CosmicDatamodelRecord record = findByUser(CyanCruiseDatamodelObjects.PROFILE_SNAPSHOT, userId);
        return record == null ? null : (UserProfileSnapshot) record.get("snapshot_json");
    }

    public void saveSnapshot(String userId, UserProfileSnapshot snapshot) {
        CosmicDatamodelRecord record = existingOrNew(CyanCruiseDatamodelObjects.PROFILE_SNAPSHOT, userId);
        gateway.save(record.set("version", snapshot == null ? null : snapshot.getVersion())
                .set("snapshot_json", snapshot));
    }

    public Map<String, String> loadFacts(String userId) {
        List<CosmicDatamodelRecord> rows = gateway.list(CyanCruiseDatamodelObjects.PROFILE_FACT, userFilter(userId), null);
        Map<String, String> facts = new LinkedHashMap<String, String>();
        for (CosmicDatamodelRecord row : rows) {
            facts.put(DatamodelFieldMapper.asString(row.get("fact_key")), DatamodelFieldMapper.asString(row.get("fact_value")));
        }
        return facts;
    }

    public void saveFact(String userId, String key, String value) {
        CosmicDatamodelRecord record = gateway.findOne(CyanCruiseDatamodelObjects.PROFILE_FACT, factFilter(userId, key));
        if (record == null) {
            record = new CosmicDatamodelRecord(CyanCruiseDatamodelObjects.PROFILE_FACT)
                    .set(CyanCruiseDatamodelObjects.USER_ID, userId)
                    .set("fact_key", key);
        }
        gateway.save(record.set("fact_value", value));
    }

    public CareerUserProfileDto loadProfile(String userId) {
        CosmicDatamodelRecord record = findByUser(CyanCruiseDatamodelObjects.USER_PROFILE, userId);
        return record == null ? null : (CareerUserProfileDto) record.get("profile_json");
    }

    public void saveProfile(String userId, CareerUserProfileDto profile) {
        CosmicDatamodelRecord record = existingOrNew(CyanCruiseDatamodelObjects.USER_PROFILE, userId);
        String targetRole = null;
        if (profile != null && profile.getTarget() != null) {
            targetRole = profile.getTarget().getRole();
        }
        gateway.save(record.set("personalization_level", profile == null ? null : profile.getPersonalizationLevel())
                .set("completeness_score", profile == null ? null : profile.getCompletenessScore())
                .set("current_stage", profile == null ? null : profile.getCurrentStage())
                .set("target_role", targetRole)
                .set("profile_json", profile));
    }

    public CareerProfileDraftDto loadDraft(String userId) {
        CosmicDatamodelRecord record = findByUser(CyanCruiseDatamodelObjects.PROFILE_DRAFT, userId);
        Object draft = record == null ? null : record.get("draft_json");
        return draft instanceof CareerProfileDraftDto ? (CareerProfileDraftDto) draft : new CareerProfileDraftDto();
    }

    public void saveDraft(String userId, CareerProfileDraftDto draft) {
        CosmicDatamodelRecord record = existingOrNew(CyanCruiseDatamodelObjects.PROFILE_DRAFT, userId);
        gateway.save(record.set("draft_json", draft == null ? new CareerProfileDraftDto() : draft));
    }

    public void clearDraft(String userId) {
        CosmicDatamodelRecord record = existingOrNew(CyanCruiseDatamodelObjects.PROFILE_DRAFT, userId);
        gateway.save(record.set("draft_json", new CareerProfileDraftDto()));
    }

    private CosmicDatamodelRecord existingOrNew(String objectName, String userId) {
        CosmicDatamodelRecord record = findByUser(objectName, userId);
        if (record == null) {
            record = new CosmicDatamodelRecord(objectName).set(CyanCruiseDatamodelObjects.USER_ID, userId);
        }
        return record;
    }

    private CosmicDatamodelRecord findByUser(String objectName, String userId) {
        return gateway.findOne(objectName, userFilter(userId));
    }

    private CosmicRecordFilter userFilter(final String userId) {
        return new CosmicRecordFilter() {
            public boolean matches(CosmicDatamodelRecord record) {
                return DatamodelFieldMapper.same(userId, record.get(CyanCruiseDatamodelObjects.USER_ID));
            }
        };
    }

    private CosmicRecordFilter factFilter(final String userId, final String key) {
        return new CosmicRecordFilter() {
            public boolean matches(CosmicDatamodelRecord record) {
                return DatamodelFieldMapper.same(userId, record.get(CyanCruiseDatamodelObjects.USER_ID))
                        && DatamodelFieldMapper.same(key, record.get("fact_key"));
            }
        };
    }
}
