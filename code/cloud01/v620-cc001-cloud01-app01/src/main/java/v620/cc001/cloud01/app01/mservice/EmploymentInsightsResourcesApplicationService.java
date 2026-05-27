package v620.cc001.cloud01.app01.mservice;

import v620.base.helper.career.EmploymentInsightsResourcesService;
import v620.cc001.base.common.dto.career.CareerResourceFeedDto;
import v620.cc001.base.common.dto.career.EmploymentInsightDto;
import v620.cc001.base.common.dto.career.EmploymentInsightProfileContext;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;

import java.time.LocalDateTime;

/**
 * Application boundary for employment insight and CareerLoop resources.
 */
public class EmploymentInsightsResourcesApplicationService {

    private final EmploymentInsightStorage employmentStorage;
    private final CareerResourceStorage resourceStorage;
    private final CareerProfileApplicationService profileApplicationService;
    private final EmploymentInsightsResourcesService helper;

    public EmploymentInsightsResourcesApplicationService() {
        this(new InMemoryEmploymentInsightStorage(), new InMemoryCareerResourceStorage(),
                new CareerProfileApplicationService(), new EmploymentInsightsResourcesService());
    }

    public EmploymentInsightsResourcesApplicationService(EmploymentInsightStorage employmentStorage,
                                                         CareerResourceStorage resourceStorage,
                                                         CareerProfileApplicationService profileApplicationService,
                                                         EmploymentInsightsResourcesService helper) {
        this.employmentStorage = employmentStorage;
        this.resourceStorage = resourceStorage;
        this.profileApplicationService = profileApplicationService;
        this.helper = helper;
    }

    public EmploymentInsightDto getInsight(String userId) {
        String safeUserId = requireUserId(userId);
        UserProfileSnapshot snapshot = profileApplicationService.getSnapshot(safeUserId);
        EmploymentInsightProfileContext context = context(safeUserId, snapshot);
        return helper.buildInsight(context, employmentStorage.listRecords(), LocalDateTime.now());
    }

    public CareerResourceFeedDto getResources(String userId) {
        String safeUserId = hasText(userId) ? userId.trim() : null;
        return helper.buildResourceFeed(resourceStorage.listCards(), safeUserId, LocalDateTime.now());
    }

    private EmploymentInsightProfileContext context(String userId, UserProfileSnapshot snapshot) {
        UserProfileSnapshot safeSnapshot = snapshot == null ? new UserProfileSnapshot() : snapshot;
        UserProfileSnapshot.OnboardingBlock onboarding = safeSnapshot.getOnboarding();
        UserProfileSnapshot.EducationBlock education = onboarding == null ? null : onboarding.getEducation();
        UserProfileSnapshot.PreferencesBlock preferences = safeSnapshot.getPreferences();
        UserProfileSnapshot.ResumeBlock resume = safeSnapshot.getResume();
        UserProfileSnapshot.InterviewBlock interview = safeSnapshot.getInterview();

        EmploymentInsightProfileContext context = new EmploymentInsightProfileContext();
        context.setUserId(userId);
        context.setSchool(education == null ? null : education.getSchool());
        context.setMajor(education == null ? null : education.getMajor());
        context.setTargetRole(firstText(
                preferences == null ? null : preferences.getTargetRole(),
                resume == null ? null : resume.getTargetJob(),
                interview == null ? null : interview.getPositionName()));
        return context;
    }

    private String requireUserId(String userId) {
        if (!hasText(userId)) {
            throw new IllegalArgumentException("userId is required");
        }
        return userId.trim();
    }

    private String firstText(String first, String second, String third) {
        if (hasText(first)) {
            return first.trim();
        }
        if (hasText(second)) {
            return second.trim();
        }
        return hasText(third) ? third.trim() : null;
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
