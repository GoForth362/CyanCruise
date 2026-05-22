package v620.cc001.cloud01.app01.mservice;

import v620.cc001.base.common.dto.career.CareerUserProfileDto;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Non-durable placeholder storage for early migration validation before Cosmic datamodel binding.
 */
public class InMemoryCareerProfileStorage implements CareerProfileStorage {

    private static final Map<String, UserProfileSnapshot> SNAPSHOTS =
            new ConcurrentHashMap<String, UserProfileSnapshot>();
    private static final Map<String, Map<String, String>> FACTS =
            new ConcurrentHashMap<String, Map<String, String>>();
    private static final Map<String, CareerUserProfileDto> PROFILES =
            new ConcurrentHashMap<String, CareerUserProfileDto>();

    public UserProfileSnapshot loadSnapshot(String userId) {
        UserProfileSnapshot snapshot = SNAPSHOTS.get(userId);
        return snapshot == null ? new UserProfileSnapshot() : snapshot;
    }

    public void saveSnapshot(String userId, UserProfileSnapshot snapshot) {
        SNAPSHOTS.put(userId, snapshot == null ? new UserProfileSnapshot() : snapshot);
    }

    public Map<String, String> loadFacts(String userId) {
        Map<String, String> facts = FACTS.get(userId);
        return facts == null ? new LinkedHashMap<String, String>() : new LinkedHashMap<String, String>(facts);
    }

    public void saveFact(String userId, String key, String value) {
        if (key == null || key.trim().length() == 0 || value == null || value.trim().length() == 0) {
            return;
        }
        Map<String, String> facts = FACTS.get(userId);
        if (facts == null) {
            facts = new ConcurrentHashMap<String, String>();
            FACTS.put(userId, facts);
        }
        facts.put(key.trim(), value.trim());
    }

    public CareerUserProfileDto loadProfile(String userId) {
        return PROFILES.get(userId);
    }

    public void saveProfile(String userId, CareerUserProfileDto profile) {
        if (profile != null) {
            PROFILES.put(userId, profile);
        }
    }
}
