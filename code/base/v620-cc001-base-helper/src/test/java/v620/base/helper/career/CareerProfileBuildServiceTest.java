package v620.base.helper.career;

import org.junit.jupiter.api.Test;
import v620.cc001.base.common.dto.career.CareerAgentRuleInput;
import v620.cc001.base.common.dto.career.CareerUserProfileDto;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class CareerProfileBuildServiceTest {

    private final CareerProfileBuildService service = new CareerProfileBuildService();

    @Test
    void preferenceTargetRoleOverridesAssessmentSuggestion() {
        UserProfileSnapshot snapshot = new UserProfileSnapshot();
        UserProfileSnapshot.PreferencesBlock preferences = new UserProfileSnapshot.PreferencesBlock();
        preferences.setTargetRole("Java Backend Engineer");
        snapshot.setPreferences(preferences);
        UserProfileSnapshot.AssessmentBlock assessment = new UserProfileSnapshot.AssessmentBlock();
        assessment.setSuggestedRoles(Arrays.asList("Product Manager", "Data Analyst"));
        snapshot.setAssessment(assessment);

        CareerUserProfileDto profile = service.build(snapshot, facts(), checkIn(), false);

        assertEquals("Java Backend Engineer", profile.getTarget().getRole());
        assertEquals("PREFERENCES", profile.getTarget().getSource());
    }

    @Test
    void assessmentSuggestionIsFallbackTargetRole() {
        UserProfileSnapshot snapshot = new UserProfileSnapshot();
        UserProfileSnapshot.AssessmentBlock assessment = new UserProfileSnapshot.AssessmentBlock();
        assessment.setSuggestedRoles(Arrays.asList("Frontend Engineer", "UX Designer"));
        snapshot.setAssessment(assessment);

        CareerUserProfileDto profile = service.build(snapshot, facts(), checkIn(), false);

        assertEquals("Frontend Engineer", profile.getTarget().getRole());
        assertEquals("INFERRED", profile.getTarget().getSource());
    }

    @Test
    void careerSwitcherOnboardingControlsCurrentStage() {
        UserProfileSnapshot snapshot = new UserProfileSnapshot();
        UserProfileSnapshot.PreferencesBlock preferences = new UserProfileSnapshot.PreferencesBlock();
        preferences.setTargetRole("Data Analyst");
        snapshot.setPreferences(preferences);
        UserProfileSnapshot.OnboardingBlock onboarding = new UserProfileSnapshot.OnboardingBlock();
        onboarding.setIdentityType("career_switcher");
        snapshot.setOnboarding(onboarding);

        CareerUserProfileDto profile = service.build(snapshot, facts(), checkIn(), false);

        assertEquals("CAREER_SWITCH_POSITIONING", profile.getCurrentStage());
    }

    @Test
    void selfReportedResumeReadinessDoesNotSetRealResumePresence() {
        UserProfileSnapshot snapshot = new UserProfileSnapshot();
        UserProfileSnapshot.PreferencesBlock preferences = new UserProfileSnapshot.PreferencesBlock();
        preferences.setTargetRole("Java Backend Engineer");
        snapshot.setPreferences(preferences);
        UserProfileSnapshot.OnboardingBlock onboarding = new UserProfileSnapshot.OnboardingBlock();
        onboarding.setHasResume("yes");
        onboarding.setResumeStatus("draft");
        snapshot.setOnboarding(onboarding);

        CareerUserProfileDto profile = service.build(snapshot, facts(), checkIn(), false);

        assertEquals(Boolean.FALSE, profile.getReadiness().getHasResume());
        assertEquals(Integer.valueOf(10), profile.getReadiness().getResumeReadinessPercent());
        assertFalse(profile.getMissingSignals().isEmpty());
    }

    private Map<String, String> facts() {
        return new LinkedHashMap<String, String>();
    }

    private CareerAgentRuleInput.CheckInStatus checkIn() {
        return new CareerAgentRuleInput.CheckInStatus();
    }
}
