package v620.cc001.cloud01.app01.mservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import v620.base.helper.career.CareerProfileBuildService;
import v620.base.helper.career.CareerProfileSnapshotMergeService;
import v620.cc001.base.common.dto.career.CareerProfileDraftDto;
import v620.cc001.base.common.dto.career.CareerProfileOnboardingRequest;
import v620.cc001.base.common.dto.career.CareerProfilePreferencesRequest;
import v620.cc001.base.common.dto.career.CareerUserProfileDto;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FileCareerProfileStorageTest {

    @TempDir
    File tempDir;

    @Test
    void reloadsSnapshotFactsAndProfileFromFreshStorageInstance() {
        FileCareerProfileStorage first = new FileCareerProfileStorage(tempDir);
        UserProfileSnapshot snapshot = new UserProfileSnapshot();
        UserProfileSnapshot.PreferencesBlock preferences = new UserProfileSnapshot.PreferencesBlock();
        preferences.setTargetRole("Java Backend Engineer");
        snapshot.setPreferences(preferences);
        first.saveSnapshot("user-1", snapshot);
        first.saveFact("user-1", "target_city", "Shanghai");

        CareerUserProfileDto profile = new CareerUserProfileDto();
        profile.setCurrentStage("ASSESSMENT_BASELINE");
        profile.setCompletenessScore(Integer.valueOf(23));
        first.saveProfile("user-1", profile);

        FileCareerProfileStorage second = new FileCareerProfileStorage(tempDir);

        assertEquals("Java Backend Engineer", second.loadSnapshot("user-1").getPreferences().getTargetRole());
        assertEquals("Shanghai", second.loadFacts("user-1").get("target_city"));
        assertEquals("ASSESSMENT_BASELINE", second.loadProfile("user-1").getCurrentStage());
        assertEquals(Integer.valueOf(23), second.loadProfile("user-1").getCompletenessScore());
    }

    @Test
    void applicationServiceReadsOnboardingThroughFreshServiceWithSameStorageDirectory() {
        CareerProfileApplicationService first = service(new FileCareerProfileStorage(tempDir));
        CareerProfileOnboardingRequest request = new CareerProfileOnboardingRequest();
        request.setIdentityType("career_switcher");
        request.setTargetRole("Data Analyst");
        request.setHasResume("no");
        first.saveOnboarding("user-2", request);

        CareerProfileApplicationService second = service(new FileCareerProfileStorage(tempDir));
        UserProfileSnapshot snapshot = second.getSnapshot("user-2");
        CareerUserProfileDto profile = second.getProfile("user-2");

        assertEquals("career_switcher", snapshot.getOnboarding().getIdentityType());
        assertEquals("Data Analyst", snapshot.getPreferences().getTargetRole());
        assertNotNull(profile);
        assertEquals("CAREER_SWITCH_POSITIONING", profile.getCurrentStage());
    }

    @Test
    void blankPreferenceDoesNotClearReloadedTargetRole() {
        CareerProfileApplicationService first = service(new FileCareerProfileStorage(tempDir));
        CareerProfilePreferencesRequest preference = new CareerProfilePreferencesRequest();
        preference.setTargetRole("Product Manager");
        first.savePreferences("user-3", preference);

        CareerProfileOnboardingRequest onboarding = new CareerProfileOnboardingRequest();
        onboarding.setTargetRole(" ");
        first.saveOnboarding("user-3", onboarding);

        CareerProfileApplicationService second = service(new FileCareerProfileStorage(tempDir));

        assertEquals("Product Manager", second.getSnapshot("user-3").getPreferences().getTargetRole());
    }

    @Test
    void reloadsProfileDraftFromFreshStorageInstance() {
        CareerProfileApplicationService first = service(new FileCareerProfileStorage(tempDir));
        CareerProfileDraftDto draft = new CareerProfileDraftDto();
        draft.setIdentityType("student");
        draft.setTargetRole("AI Product Manager");
        first.saveDraft("user-4", draft);

        CareerProfileApplicationService second = service(new FileCareerProfileStorage(tempDir));

        assertEquals("student", second.getDraft("user-4").getIdentityType());
        assertEquals("AI Product Manager", second.getDraft("user-4").getTargetRole());
        assertNotNull(second.getDraft("user-4").getUpdatedAt());
    }

    @Test
    void profileDraftMergePreservesExistingValuesAndIgnoresBlankValues() {
        CareerProfileApplicationService service = service(new FileCareerProfileStorage(tempDir));
        CareerProfileDraftDto first = new CareerProfileDraftDto();
        first.setTargetRole("Data Analyst");
        first.setPreference("analytics");
        service.saveDraft("user-5", first);

        CareerProfileDraftDto second = new CareerProfileDraftDto();
        second.setTargetRole(" ");
        second.setPreference("");
        second.setResumeStatus("draft");
        CareerProfileDraftDto saved = service.saveDraft("user-5", second);

        assertEquals("Data Analyst", saved.getTargetRole());
        assertEquals("analytics", saved.getPreference());
        assertEquals("draft", saved.getResumeStatus());
    }

    @Test
    void clearProfileDraftDoesNotClearSnapshot() {
        CareerProfileApplicationService service = service(new FileCareerProfileStorage(tempDir));
        CareerProfileOnboardingRequest onboarding = new CareerProfileOnboardingRequest();
        onboarding.setIdentityType("graduate");
        service.saveOnboarding("user-6", onboarding);
        CareerProfileDraftDto draft = new CareerProfileDraftDto();
        draft.setTargetRole("Consultant");
        service.saveDraft("user-6", draft);

        service.clearDraft("user-6");

        assertEquals(null, service.getDraft("user-6").getTargetRole());
        assertEquals("graduate", service.getSnapshot("user-6").getOnboarding().getIdentityType());
    }

    private CareerProfileApplicationService service(CareerProfileStorage storage) {
        return new CareerProfileApplicationService(
                storage,
                new CareerProfileSnapshotMergeService(),
                new CareerProfileBuildService());
    }
}
