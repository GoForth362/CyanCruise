package v620.base.helper.career;

import org.junit.jupiter.api.Test;
import v620.cc001.base.common.dto.career.CareerAgentRuleInput;
import v620.cc001.base.common.dto.career.CareerAgentTodayDto;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CareerAgentTodayRuleServiceTest {

    private final CareerAgentTodayRuleService service = new CareerAgentTodayRuleService();

    @Test
    void missingTargetRoleRecommendsTargetSelectionAndAssessment() {
        CareerAgentTodayDto today = service.recommend(new CareerAgentRuleInput());

        assertEquals("TARGET_ROLE_SELECTION", today.getStage());
        assertEquals("选择目标岗位", today.getActions().get(0).getLabel());
        assertEquals("完成职业测评", today.getActions().get(1).getLabel());
        assertTrue(today.getRiskReasonKeys().contains("agent.risk.reason.NO_TARGET_ROLE"));
    }

    @Test
    void targetRoleWithoutAssessmentRecommendsAssessmentBaseline() {
        CareerAgentTodayDto today = service.recommend(input(snapshotWithPreference("Java Engineer")));

        assertEquals("ASSESSMENT_BASELINE", today.getStage());
        assertEquals("完成 5 分钟测评", today.getActions().get(0).getLabel());
        assertTrue(today.getTodayFocus().contains("Java Engineer"));
    }

    @Test
    void targetAndAssessmentWithoutResumeRecommendsResumeBootstrap() {
        UserProfileSnapshot snapshot = snapshotWithPreference("Product Manager");
        snapshot.setAssessment(assessment("MBTI", "ENTP"));

        CareerAgentTodayDto today = service.recommend(input(snapshot));

        assertEquals("RESUME_BOOTSTRAP", today.getStage());
        assertEquals("RESUME", today.getActions().get(0).getType());
        assertEquals("HIGH", today.getActions().get(0).getPriority());
    }

    @Test
    void lowResumeScoreRecommendsResumeImprovement() {
        UserProfileSnapshot snapshot = readyUntilResume("Data Analyst", Integer.valueOf(50));

        CareerAgentTodayDto today = service.recommend(input(snapshot));

        assertEquals("RESUME_IMPROVEMENT", today.getStage());
        assertEquals("优化简历", today.getActions().get(0).getLabel());
        assertTrue(today.getRiskReasonKeys().contains("agent.risk.reason.RESUME_LOW_SCORE"));
    }

    @Test
    void missingInterviewRecommendsInterviewBootstrap() {
        UserProfileSnapshot snapshot = readyUntilResume("Backend Engineer", Integer.valueOf(80));

        CareerAgentTodayDto today = service.recommend(input(snapshot));

        assertEquals("INTERVIEW_BOOTSTRAP", today.getStage());
        assertEquals("开始模拟面试", today.getActions().get(0).getLabel());
    }

    @Test
    void lowInterviewScoreRecommendsInterviewImprovement() {
        UserProfileSnapshot snapshot = readyUntilResume("Backend Engineer", Integer.valueOf(80));
        UserProfileSnapshot.InterviewBlock interview = new UserProfileSnapshot.InterviewBlock();
        interview.setPositionName("Backend Engineer");
        interview.setLastScore(Integer.valueOf(60));
        interview.setWeakDimensions(Arrays.asList("项目表达"));
        snapshot.setInterview(interview);

        CareerAgentTodayDto today = service.recommend(input(snapshot));

        assertEquals("INTERVIEW_IMPROVEMENT", today.getStage());
        assertEquals("练习面试", today.getActions().get(0).getLabel());
        assertTrue(contains(today, "薄弱维度：项目表达"));
    }

    @Test
    void lowCheckinRecommendsExecutionRhythm() {
        UserProfileSnapshot snapshot = readyUntilResume("Backend Engineer", Integer.valueOf(80));
        UserProfileSnapshot.InterviewBlock interview = new UserProfileSnapshot.InterviewBlock();
        interview.setPositionName("Backend Engineer");
        interview.setLastScore(Integer.valueOf(85));
        snapshot.setInterview(interview);
        CareerAgentRuleInput input = input(snapshot);
        CareerAgentRuleInput.CheckInStatus checkIn = new CareerAgentRuleInput.CheckInStatus();
        checkIn.setWeeklyDays(1);
        input.setCheckInStatus(checkIn);

        CareerAgentTodayDto today = service.recommend(input);

        assertEquals("EXECUTION_RHYTHM", today.getStage());
        assertEquals("查看打卡计划", today.getActions().get(0).getLabel());
    }

    @Test
    void onboardingSpecialPathsArePreserved() {
        assertEquals("CAREER_SWITCH_POSITIONING",
                service.recommend(input(onboardingSnapshot("career_switcher", "yes", "Product Manager"))).getStage());
        assertEquals("INTERNSHIP_RESUME_BOOTSTRAP",
                service.recommend(input(onboardingSnapshot("internship_seeker", "no", "Frontend Intern"))).getStage());
        assertEquals("GRADUATE_RESUME_UPLOAD",
                service.recommend(input(onboardingSnapshot("new_graduate", "yes", "Java Engineer"))).getStage());
    }

    @Test
    void weeklyFocusAddsAtMostTwoPlanActions() {
        CareerAgentRuleInput input = input(snapshotWithPreference("Java Engineer"));
        input.setWeeklyFocusItems(Arrays.asList("优化第一段项目经历", "投递 3 个 Java 岗位", "第三个不应出现"));

        CareerAgentTodayDto today = service.recommend(input);

        int planWeeklyCount = 0;
        for (CareerAgentTodayDto.Action action : today.getActions()) {
            if ("PLAN_WEEKLY".equals(action.getSource())) {
                planWeeklyCount++;
            }
        }
        assertEquals(2, planWeeklyCount);
        assertTrue(today.getReason().contains("长期规划"));
    }

    private CareerAgentRuleInput input(UserProfileSnapshot snapshot) {
        CareerAgentRuleInput input = new CareerAgentRuleInput();
        input.setSnapshot(snapshot);
        return input;
    }

    private UserProfileSnapshot snapshotWithPreference(String targetRole) {
        UserProfileSnapshot snapshot = new UserProfileSnapshot();
        UserProfileSnapshot.PreferencesBlock preferences = new UserProfileSnapshot.PreferencesBlock();
        preferences.setTargetRole(targetRole);
        snapshot.setPreferences(preferences);
        return snapshot;
    }

    private UserProfileSnapshot readyUntilResume(String targetRole, Integer resumeScore) {
        UserProfileSnapshot snapshot = snapshotWithPreference(targetRole);
        snapshot.setAssessment(assessment("MBTI", "ENTP"));
        UserProfileSnapshot.ResumeBlock resume = new UserProfileSnapshot.ResumeBlock();
        resume.setLastResumeId(Long.valueOf(1L));
        resume.setLastResumeKey("resumes/1.pdf");
        resume.setTitle("Resume");
        resume.setTargetJob(targetRole);
        resume.setDiagnosisScore(resumeScore);
        snapshot.setResume(resume);
        return snapshot;
    }

    private UserProfileSnapshot onboardingSnapshot(String identityType, String hasResume, String targetRole) {
        UserProfileSnapshot snapshot = snapshotWithPreference(targetRole);
        UserProfileSnapshot.OnboardingBlock onboarding = new UserProfileSnapshot.OnboardingBlock();
        onboarding.setIdentityType(identityType);
        onboarding.setHasResume(hasResume);
        snapshot.setOnboarding(onboarding);
        return snapshot;
    }

    private UserProfileSnapshot.AssessmentBlock assessment(String title, String summary) {
        UserProfileSnapshot.AssessmentBlock assessment = new UserProfileSnapshot.AssessmentBlock();
        assessment.setScaleTitle(title);
        assessment.setSummary(summary);
        return assessment;
    }

    private boolean contains(CareerAgentTodayDto today, String value) {
        for (String reason : today.getRiskReasons()) {
            if (reason != null && reason.contains(value)) {
                return true;
            }
        }
        return false;
    }
}
