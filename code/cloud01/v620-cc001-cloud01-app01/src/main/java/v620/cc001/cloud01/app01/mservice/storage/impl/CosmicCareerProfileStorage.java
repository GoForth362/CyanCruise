package v620.cc001.cloud01.app01.mservice.storage.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import v620.cc001.base.common.dto.career.CareerProfileDraftDto;
import v620.cc001.base.common.dto.career.CareerUserProfileDto;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;
import v620.cc001.cloud01.app01.mservice.datamodel.CosmicDatamodelGateway;
import v620.cc001.cloud01.app01.mservice.datamodel.CosmicDatamodelRecord;
import v620.cc001.cloud01.app01.mservice.datamodel.CosmicRecordFilter;
import v620.cc001.cloud01.app01.mservice.datamodel.CyanCruiseDatamodelObjects;
import v620.cc001.cloud01.app01.mservice.datamodel.DatamodelFieldMapper;
import v620.cc001.cloud01.app01.mservice.storage.CareerProfileStorage;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Stores all profile state for one user in the modeled v620_cc_user_profile object.
 */
public class CosmicCareerProfileStorage implements CareerProfileStorage {

    private static final ObjectMapper JSON = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private final CosmicDatamodelGateway gateway;

    public CosmicCareerProfileStorage(CosmicDatamodelGateway gateway) {
        this.gateway = gateway;
    }

    public UserProfileSnapshot loadSnapshot(String userId) {
        return readJson(findByUser(userId), "readiness_json", UserProfileSnapshot.class);
    }

    public void saveSnapshot(String userId, UserProfileSnapshot snapshot) {
        CosmicDatamodelRecord record = existingOrNew(userId);
        gateway.save(record.set("readiness_json", writeJson(snapshot)));
    }

    public Map<String, String> loadFacts(String userId) {
        return evidence(findByUser(userId)).getFacts();
    }

    public void saveFact(String userId, String key, String value) {
        CosmicDatamodelRecord record = existingOrNew(userId);
        ProfileEvidence evidence = evidence(record);
        evidence.getFacts().put(key, value);
        gateway.save(record.set("evidence_json", writeJson(evidence)));
    }

    public CareerUserProfileDto loadProfile(String userId) {
        return readJson(findByUser(userId), "profile_json", CareerUserProfileDto.class);
    }

    public void saveProfile(String userId, CareerUserProfileDto profile) {
        CosmicDatamodelRecord record = existingOrNew(userId);
        String targetRole = profile == null || profile.getTarget() == null ? null : profile.getTarget().getRole();
        gateway.save(record.set("personalization_level", profile == null ? null : profile.getPersonalizationLevel())
                .set("completeness_score", profile == null ? null : profile.getCompletenessScore())
                .set("current_stage", profile == null ? null : profile.getCurrentStage())
                .set("target_role", targetRole)
                .set("profile_json", writeJson(profile)));
    }

    public CareerProfileDraftDto loadDraft(String userId) {
        CareerProfileDraftDto draft = evidence(findByUser(userId)).getDraft();
        return draft == null ? new CareerProfileDraftDto() : draft;
    }

    public void saveDraft(String userId, CareerProfileDraftDto draft) {
        CosmicDatamodelRecord record = existingOrNew(userId);
        ProfileEvidence evidence = evidence(record);
        evidence.setDraft(draft == null ? new CareerProfileDraftDto() : draft);
        gateway.save(record.set("evidence_json", writeJson(evidence)));
    }

    public void clearDraft(String userId) {
        saveDraft(userId, new CareerProfileDraftDto());
    }

    private CosmicDatamodelRecord existingOrNew(String userId) {
        CosmicDatamodelRecord record = findByUser(userId);
        return record == null
                ? new CosmicDatamodelRecord(CyanCruiseDatamodelObjects.USER_PROFILE)
                        .set(CyanCruiseDatamodelObjects.USER_ID, userId)
                : record;
    }

    private CosmicDatamodelRecord findByUser(String userId) {
        return gateway.findOne(CyanCruiseDatamodelObjects.USER_PROFILE, userFilter(userId));
    }

    private CosmicRecordFilter userFilter(final String userId) {
        return new CosmicRecordFilter() {
            public boolean matches(CosmicDatamodelRecord record) {
                return DatamodelFieldMapper.same(userId, record.get(CyanCruiseDatamodelObjects.USER_ID));
            }
        };
    }

    private ProfileEvidence evidence(CosmicDatamodelRecord record) {
        Object value = record == null ? null : record.get("evidence_json");
        if (value instanceof ProfileEvidence) {
            return ((ProfileEvidence) value).normalize();
        }
        if (value instanceof Map) {
            return evidenceFromMap((Map<?, ?>) value);
        }
        if (value instanceof String && hasText((String) value)) {
            try {
                JsonNode node = JSON.readTree((String) value);
                if (node.has("facts") || node.has("draft")) {
                    return JSON.readValue((String) value, ProfileEvidence.class).normalize();
                }
                Map<String, String> facts = JSON.readValue((String) value,
                        new TypeReference<Map<String, String>>() { });
                ProfileEvidence result = new ProfileEvidence();
                result.setFacts(facts);
                return result.normalize();
            } catch (Exception ex) {
                throw new IllegalStateException("Unable to read profile evidence JSON.", ex);
            }
        }
        return new ProfileEvidence();
    }

    private ProfileEvidence evidenceFromMap(Map<?, ?> source) {
        ProfileEvidence result = new ProfileEvidence();
        Object facts = source.get("facts");
        if (facts instanceof Map) {
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) facts).entrySet()) {
                result.getFacts().put(String.valueOf(entry.getKey()), DatamodelFieldMapper.asString(entry.getValue()));
            }
        } else {
            for (Map.Entry<?, ?> entry : source.entrySet()) {
                result.getFacts().put(String.valueOf(entry.getKey()), DatamodelFieldMapper.asString(entry.getValue()));
            }
        }
        Object draft = source.get("draft");
        if (draft instanceof CareerProfileDraftDto) {
            result.setDraft((CareerProfileDraftDto) draft);
        }
        return result.normalize();
    }

    private <T> T readJson(CosmicDatamodelRecord record, String fieldName, Class<T> type) {
        Object value = record == null ? null : record.get(fieldName);
        if (type.isInstance(value)) {
            return type.cast(value);
        }
        if (!(value instanceof String) || !hasText((String) value)) {
            return null;
        }
        try {
            return JSON.readValue((String) value, type);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to read profile JSON field: " + fieldName, ex);
        }
    }

    private String writeJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return JSON.writeValueAsString(value);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to write profile JSON.", ex);
        }
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }

    public static class ProfileEvidence {

        private Map<String, String> facts = new LinkedHashMap<String, String>();
        private CareerProfileDraftDto draft;

        public Map<String, String> getFacts() {
            return facts;
        }

        public void setFacts(Map<String, String> facts) {
            this.facts = facts;
        }

        public CareerProfileDraftDto getDraft() {
            return draft;
        }

        public void setDraft(CareerProfileDraftDto draft) {
            this.draft = draft;
        }

        private ProfileEvidence normalize() {
            if (facts == null) {
                facts = new LinkedHashMap<String, String>();
            }
            return this;
        }
    }
}
