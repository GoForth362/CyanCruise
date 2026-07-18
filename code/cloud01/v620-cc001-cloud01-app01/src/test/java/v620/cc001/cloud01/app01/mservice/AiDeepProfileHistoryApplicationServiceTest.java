package v620.cc001.cloud01.app01.mservice;

import org.junit.jupiter.api.Test;
import v620.base.helper.career.CareerProfileBuildService;
import v620.base.helper.career.CareerProfileSnapshotMergeService;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;
import v620.cc001.cloud01.app01.mservice.ai.impl.AgentPlatformDeepProfileAnalyzer;
import v620.cc001.cloud01.app01.mservice.application.AiDeepProfileApplicationService;
import v620.cc001.cloud01.app01.mservice.application.CareerProfileApplicationService;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerProfileStorage;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AiDeepProfileHistoryApplicationServiceTest {

    @Test
    void keepsLatestTwentyProfilesAndLoadsOwnedDetail() {
        CareerProfileApplicationService profileService = new CareerProfileApplicationService(
                new InMemoryCareerProfileStorage(),
                new CareerProfileSnapshotMergeService(),
                new CareerProfileBuildService());
        String userId = "deep-profile-history-user";
        for (int index = 1; index <= 21; index++) {
            profileService.saveAiDeepProfile(userId, profile("record-" + index, "画像 " + index));
        }

        AiDeepProfileApplicationService service = new AiDeepProfileApplicationService(
                null, profileService, new AgentPlatformDeepProfileAnalyzer(null));
        List<UserProfileSnapshot.AiDeepProfileBlock> history = service.history(userId);

        assertEquals(20, history.size());
        assertEquals("record-21", history.get(0).getRecordId());
        assertEquals("record-2", history.get(19).getRecordId());
        assertEquals("画像 10", service.detail(userId, "record-10").getProfileSummary());
        assertThrows(IllegalArgumentException.class,
                () -> service.detail("another-deep-profile-user", "record-10"));
    }

    @Test
    void backfillsLegacyLatestProfileIntoHistory() {
        InMemoryCareerProfileStorage storage = new InMemoryCareerProfileStorage();
        String userId = "legacy-deep-profile-user";
        UserProfileSnapshot snapshot = new UserProfileSnapshot();
        UserProfileSnapshot.AiDeepProfileBlock legacy = new UserProfileSnapshot.AiDeepProfileBlock();
        legacy.setProfileSummary("旧版深度画像");
        legacy.setSource("AI_ASSESSMENT");
        snapshot.setAiDeepProfile(legacy);
        snapshot.setUpdatedAt(LocalDateTime.of(2026, 7, 15, 9, 30));
        storage.saveSnapshot(userId, snapshot);

        CareerProfileApplicationService profileService = new CareerProfileApplicationService(
                storage,
                new CareerProfileSnapshotMergeService(),
                new CareerProfileBuildService());
        AiDeepProfileApplicationService service = new AiDeepProfileApplicationService(
                null, profileService, new AgentPlatformDeepProfileAnalyzer(null));

        List<UserProfileSnapshot.AiDeepProfileBlock> history = service.history(userId);

        assertEquals(1, history.size());
        assertEquals("旧版深度画像", history.get(0).getProfileSummary());
        assertEquals(LocalDateTime.of(2026, 7, 15, 9, 30), history.get(0).getGeneratedAt());
        assertEquals(history.get(0).getRecordId(),
                service.detail(userId, history.get(0).getRecordId()).getRecordId());
        assertEquals(1, storage.loadSnapshot(userId).getAiDeepProfileHistory().size());
    }

    private UserProfileSnapshot.AiDeepProfileBlock profile(String recordId, String summary) {
        UserProfileSnapshot.AiDeepProfileBlock profile = new UserProfileSnapshot.AiDeepProfileBlock();
        profile.setRecordId(recordId);
        profile.setProfileSummary(summary);
        profile.setGeneratedAt(LocalDateTime.now());
        profile.setSource("AI_ASSESSMENT");
        return profile;
    }
}
