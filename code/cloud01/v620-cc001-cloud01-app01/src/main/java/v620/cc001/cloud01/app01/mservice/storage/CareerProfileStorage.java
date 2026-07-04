package v620.cc001.cloud01.app01.mservice.storage;

import v620.cc001.base.common.dto.career.CareerUserProfileDto;
import v620.cc001.base.common.dto.career.CareerProfileDraftDto;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;

import java.util.Map;

/**
 * Storage boundary for profile migration. Replace with a Cosmic data adapter when datamodel is ready.
 */
public interface CareerProfileStorage {

    UserProfileSnapshot loadSnapshot(String userId);

    void saveSnapshot(String userId, UserProfileSnapshot snapshot);

    Map<String, String> loadFacts(String userId);

    void saveFact(String userId, String key, String value);

    CareerUserProfileDto loadProfile(String userId);

    void saveProfile(String userId, CareerUserProfileDto profile);

    CareerProfileDraftDto loadDraft(String userId);

    void saveDraft(String userId, CareerProfileDraftDto draft);

    void clearDraft(String userId);
}
