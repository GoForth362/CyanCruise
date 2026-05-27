package v620.base.helper.career;

import v620.cc001.base.common.dto.career.CareerProfileOnboardingRequest;
import v620.cc001.base.common.dto.career.CareerProfilePreferencesRequest;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;

import java.time.LocalDateTime;

/**
 * Field-by-field merge helper for the cross-tool career profile snapshot.
 */
public class CareerProfileSnapshotMergeService {

    public UserProfileSnapshot mergePreferences(UserProfileSnapshot current,
                                                CareerProfilePreferencesRequest request) {
        UserProfileSnapshot snapshot = ensureSnapshot(current);
        if (request == null) {
            return touch(snapshot);
        }

        UserProfileSnapshot.PreferencesBlock preferences = snapshot.getPreferences();
        if (preferences == null) {
            preferences = new UserProfileSnapshot.PreferencesBlock();
        }

        if (hasText(request.getTargetRole())) {
            preferences.setTargetRole(request.getTargetRole().trim());
        }
        if (hasText(request.getInterviewMode())) {
            preferences.setInterviewMode(request.getInterviewMode().trim());
        }

        snapshot.setPreferences(preferences);
        return touch(snapshot);
    }

    public UserProfileSnapshot mergeOnboarding(UserProfileSnapshot current,
                                               CareerProfileOnboardingRequest request) {
        UserProfileSnapshot snapshot = ensureSnapshot(current);
        if (request == null) {
            return touch(snapshot);
        }

        UserProfileSnapshot.OnboardingBlock onboarding = snapshot.getOnboarding();
        if (onboarding == null) {
            onboarding = new UserProfileSnapshot.OnboardingBlock();
        }

        if (request.getIdentityType() != null) onboarding.setIdentityType(trimToNull(request.getIdentityType()));
        if (request.getStage() != null) onboarding.setStage(trimToNull(request.getStage()));
        if (request.getPainPoint() != null) onboarding.setPainPoint(trimToNull(request.getPainPoint()));
        if (request.getHasResume() != null) onboarding.setHasResume(trimToNull(request.getHasResume()));
        if (request.getResumeStatus() != null) onboarding.setResumeStatus(trimToNull(request.getResumeStatus()));
        if (request.getTimeline() != null) onboarding.setTimeline(trimToNull(request.getTimeline()));
        if (request.getEducation() != null) onboarding.setEducation(mergeEducation(onboarding.getEducation(), request.getEducation()));
        if (request.getWeeklyAvailability() != null) onboarding.setWeeklyAvailability(trimToNull(request.getWeeklyAvailability()));
        if (request.getPriorityHelp() != null) onboarding.setPriorityHelp(trimToNull(request.getPriorityHelp()));
        if (request.getRecommendedEntry() != null) onboarding.setRecommendedEntry(trimToNull(request.getRecommendedEntry()));
        if (request.getOnboardingCompletedAt() != null) {
            onboarding.setOnboardingCompletedAt(trimToNull(request.getOnboardingCompletedAt()));
        }

        snapshot.setOnboarding(onboarding);
        if (hasText(request.getTargetRole())) {
            CareerProfilePreferencesRequest preferencesRequest = new CareerProfilePreferencesRequest();
            preferencesRequest.setTargetRole(request.getTargetRole());
            snapshot = mergePreferences(snapshot, preferencesRequest);
        }
        return touch(snapshot);
    }

    public UserProfileSnapshot mergeAssessment(UserProfileSnapshot current,
                                               UserProfileSnapshot.AssessmentBlock block) {
        UserProfileSnapshot snapshot = ensureSnapshot(current);
        if (block == null) {
            return touch(snapshot);
        }
        UserProfileSnapshot.AssessmentBlock assessment = snapshot.getAssessment();
        if (assessment == null) {
            assessment = new UserProfileSnapshot.AssessmentBlock();
        }
        if (block.getLastRecordId() != null) assessment.setLastRecordId(block.getLastRecordId());
        if (block.getScaleId() != null) assessment.setScaleId(block.getScaleId());
        if (block.getScaleTitle() != null) assessment.setScaleTitle(trimToNull(block.getScaleTitle()));
        if (block.getSummary() != null) assessment.setSummary(trimToNull(block.getSummary()));
        if (block.getSuggestedRoles() != null) assessment.setSuggestedRoles(block.getSuggestedRoles());
        if (block.getCompletedAt() != null) assessment.setCompletedAt(block.getCompletedAt());
        snapshot.setAssessment(assessment);
        return touch(snapshot);
    }

    public UserProfileSnapshot mergeResume(UserProfileSnapshot current,
                                           UserProfileSnapshot.ResumeBlock block) {
        UserProfileSnapshot snapshot = ensureSnapshot(current);
        if (block == null) {
            return touch(snapshot);
        }
        UserProfileSnapshot.ResumeBlock resume = snapshot.getResume();
        if (resume == null) {
            resume = new UserProfileSnapshot.ResumeBlock();
        }
        if (block.getLastResumeId() != null) resume.setLastResumeId(block.getLastResumeId());
        if (block.getLastResumeKey() != null) resume.setLastResumeKey(trimToNull(block.getLastResumeKey()));
        if (block.getTitle() != null) resume.setTitle(trimToNull(block.getTitle()));
        if (block.getTargetJob() != null) resume.setTargetJob(trimToNull(block.getTargetJob()));
        if (block.getDiagnosisScore() != null) resume.setDiagnosisScore(block.getDiagnosisScore());
        if (block.getUpdatedAt() != null) resume.setUpdatedAt(block.getUpdatedAt());
        snapshot.setResume(resume);
        return touch(snapshot);
    }

    public UserProfileSnapshot clearResume(UserProfileSnapshot current) {
        UserProfileSnapshot snapshot = ensureSnapshot(current);
        snapshot.setResume(null);
        return touch(snapshot);
    }

    public UserProfileSnapshot mergeInterview(UserProfileSnapshot current,
                                              UserProfileSnapshot.InterviewBlock block) {
        UserProfileSnapshot snapshot = ensureSnapshot(current);
        if (block == null) {
            return touch(snapshot);
        }
        UserProfileSnapshot.InterviewBlock interview = snapshot.getInterview();
        if (interview == null) {
            interview = new UserProfileSnapshot.InterviewBlock();
        }
        if (block.getLastInterviewId() != null) interview.setLastInterviewId(block.getLastInterviewId());
        if (block.getPositionName() != null) interview.setPositionName(trimToNull(block.getPositionName()));
        if (block.getDifficulty() != null) interview.setDifficulty(trimToNull(block.getDifficulty()));
        if (block.getLastScore() != null) interview.setLastScore(block.getLastScore());
        if (block.getWeakDimensions() != null) interview.setWeakDimensions(block.getWeakDimensions());
        if (block.getStrongDimensions() != null) interview.setStrongDimensions(block.getStrongDimensions());
        if (block.getCompletedAt() != null) interview.setCompletedAt(block.getCompletedAt());
        snapshot.setInterview(interview);
        return touch(snapshot);
    }

    public UserProfileSnapshot ensureSnapshot(UserProfileSnapshot snapshot) {
        UserProfileSnapshot safe = snapshot == null ? new UserProfileSnapshot() : snapshot;
        if (safe.getVersion() == null) {
            safe.setVersion(Integer.valueOf(1));
        }
        return safe;
    }

    private UserProfileSnapshot.EducationBlock mergeEducation(UserProfileSnapshot.EducationBlock current,
                                                              UserProfileSnapshot.EducationBlock incoming) {
        UserProfileSnapshot.EducationBlock education = current == null
                ? new UserProfileSnapshot.EducationBlock()
                : current;
        if (incoming.getSchool() != null) education.setSchool(trimToNull(incoming.getSchool()));
        if (incoming.getMajor() != null) education.setMajor(trimToNull(incoming.getMajor()));
        if (incoming.getDegree() != null) education.setDegree(trimToNull(incoming.getDegree()));
        if (incoming.getGraduationYear() != null) education.setGraduationYear(trimToNull(incoming.getGraduationYear()));
        return education;
    }

    private UserProfileSnapshot touch(UserProfileSnapshot snapshot) {
        snapshot.setUpdatedAt(LocalDateTime.now());
        return snapshot;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.length() == 0 ? null : trimmed;
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
