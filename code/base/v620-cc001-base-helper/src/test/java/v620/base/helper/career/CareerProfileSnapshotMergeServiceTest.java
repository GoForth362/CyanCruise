package v620.base.helper.career;

import org.junit.jupiter.api.Test;
import v620.cc001.base.common.dto.career.CareerProfileOnboardingRequest;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CareerProfileSnapshotMergeServiceTest {

    private final CareerProfileSnapshotMergeService service = new CareerProfileSnapshotMergeService();

    @Test
    void mergeOnboardingPreservesExistingSnapshotBlocks() {
        UserProfileSnapshot snapshot = new UserProfileSnapshot();
        UserProfileSnapshot.AssessmentBlock assessment = new UserProfileSnapshot.AssessmentBlock();
        assessment.setScaleTitle("MBTI");
        snapshot.setAssessment(assessment);
        UserProfileSnapshot.ResumeBlock resume = new UserProfileSnapshot.ResumeBlock();
        resume.setTitle("Java Resume");
        snapshot.setResume(resume);

        CareerProfileOnboardingRequest request = new CareerProfileOnboardingRequest();
        request.setIdentityType("new_graduate");
        request.setPainPoint("resume_weak");
        request.setSelfProfileSupplement("每周可稳定学习 12 小时，负责过项目接口联调");

        UserProfileSnapshot merged = service.mergeOnboarding(snapshot, request);

        assertEquals("MBTI", merged.getAssessment().getScaleTitle());
        assertEquals("Java Resume", merged.getResume().getTitle());
        assertEquals("new_graduate", merged.getOnboarding().getIdentityType());
        assertEquals("resume_weak", merged.getOnboarding().getPainPoint());
        assertEquals("每周可稳定学习 12 小时，负责过项目接口联调",
                merged.getOnboarding().getSelfProfileSupplement());
        assertNotNull(merged.getUpdatedAt());
    }

    @Test
    void blankOnboardingTargetRoleDoesNotClearExistingPreference() {
        UserProfileSnapshot snapshot = new UserProfileSnapshot();
        UserProfileSnapshot.PreferencesBlock preferences = new UserProfileSnapshot.PreferencesBlock();
        preferences.setTargetRole("Java Backend Engineer");
        snapshot.setPreferences(preferences);
        snapshot.setUpdatedAt(LocalDateTime.now().minusDays(1));

        CareerProfileOnboardingRequest request = new CareerProfileOnboardingRequest();
        request.setIdentityType("student");
        request.setTargetRole("   ");

        UserProfileSnapshot merged = service.mergeOnboarding(snapshot, request);

        assertEquals("Java Backend Engineer", merged.getPreferences().getTargetRole());
        assertEquals("student", merged.getOnboarding().getIdentityType());
    }

    @Test
    void mergeOnboardingStoresTargetSchoolSeparatelyFromTargetRole() {
        UserProfileSnapshot snapshot = new UserProfileSnapshot();
        UserProfileSnapshot.PreferencesBlock preferences = new UserProfileSnapshot.PreferencesBlock();
        preferences.setTargetRole("Java Backend Engineer");
        snapshot.setPreferences(preferences);

        CareerProfileOnboardingRequest request = new CareerProfileOnboardingRequest();
        request.setTargetSchool("电子科技大学");

        UserProfileSnapshot merged = service.mergeOnboarding(snapshot, request);

        assertEquals("电子科技大学", merged.getOnboarding().getTargetSchool());
        assertEquals("Java Backend Engineer", merged.getPreferences().getTargetRole());
    }

    @Test
    void mergeResumePreservesAssessmentOnboardingAndPreferences() {
        UserProfileSnapshot snapshot = new UserProfileSnapshot();
        UserProfileSnapshot.AssessmentBlock assessment = new UserProfileSnapshot.AssessmentBlock();
        assessment.setSummary("ENTP");
        snapshot.setAssessment(assessment);
        UserProfileSnapshot.OnboardingBlock onboarding = new UserProfileSnapshot.OnboardingBlock();
        onboarding.setIdentityType("new_graduate");
        snapshot.setOnboarding(onboarding);
        UserProfileSnapshot.PreferencesBlock preferences = new UserProfileSnapshot.PreferencesBlock();
        preferences.setTargetRole("Product Manager");
        snapshot.setPreferences(preferences);

        UserProfileSnapshot.ResumeBlock resume = new UserProfileSnapshot.ResumeBlock();
        resume.setLastResumeId(Long.valueOf(1001L));
        resume.setLastResumeKey("resumes/1001.pdf");
        resume.setTitle("PM Resume");
        resume.setTargetJob("Product Manager");
        resume.setDiagnosisScore(Integer.valueOf(80));

        UserProfileSnapshot merged = service.mergeResume(snapshot, resume);

        assertEquals("ENTP", merged.getAssessment().getSummary());
        assertEquals("new_graduate", merged.getOnboarding().getIdentityType());
        assertEquals("Product Manager", merged.getPreferences().getTargetRole());
        assertEquals(Long.valueOf(1001L), merged.getResume().getLastResumeId());
        assertEquals("resumes/1001.pdf", merged.getResume().getLastResumeKey());
        assertEquals("PM Resume", merged.getResume().getTitle());
    }

    @Test
    void clearResumeLeavesOtherBlocksIntact() {
        UserProfileSnapshot snapshot = new UserProfileSnapshot();
        UserProfileSnapshot.AssessmentBlock assessment = new UserProfileSnapshot.AssessmentBlock();
        assessment.setSummary("ARI");
        snapshot.setAssessment(assessment);
        UserProfileSnapshot.ResumeBlock resume = new UserProfileSnapshot.ResumeBlock();
        resume.setLastResumeId(Long.valueOf(7L));
        snapshot.setResume(resume);

        UserProfileSnapshot cleared = service.clearResume(snapshot);

        assertEquals(null, cleared.getResume());
        assertEquals("ARI", cleared.getAssessment().getSummary());
        assertNotNull(cleared.getUpdatedAt());
    }
}
