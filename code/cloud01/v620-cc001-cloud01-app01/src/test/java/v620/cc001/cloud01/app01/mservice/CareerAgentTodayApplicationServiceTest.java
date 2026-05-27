package v620.cc001.cloud01.app01.mservice;

import org.junit.jupiter.api.Test;
import v620.base.helper.career.CareerAgentTodayRuleService;
import v620.base.helper.career.CareerPlanSummaryService;
import v620.base.helper.career.CareerProfileBuildService;
import v620.base.helper.career.CareerProfileSnapshotMergeService;
import v620.cc001.base.common.dto.career.CareerPlanSaveRequest;
import v620.cc001.base.common.dto.career.CareerAgentTodayDto;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CareerAgentTodayApplicationServiceTest {

    @Test
    void recommendByUserIdReadsTargetRoleFromProfileSnapshot() {
        CareerProfileApplicationService profileService = profileService(new InMemoryCareerProfileStorage());
        UserProfileSnapshot.ResumeBlock resume = new UserProfileSnapshot.ResumeBlock();
        resume.setLastResumeId(Long.valueOf(1L));
        resume.setLastResumeKey("resumes/java.pdf");
        resume.setTargetJob("Java Engineer");
        resume.setDiagnosisScore(Integer.valueOf(80));
        profileService.saveResume("user-1", resume);

        CareerAgentTodayApplicationService service = new CareerAgentTodayApplicationService(
                new CareerAgentTodayRuleService(),
                new CareerProfileRuleInputSource(profileService));

        CareerAgentTodayDto today = service.recommendByUserId("user-1");

        assertEquals("ASSESSMENT_BASELINE", today.getStage());
        assertEquals("完成 5 分钟测评", today.getActions().get(0).getLabel());
        assertEquals(Integer.valueOf(45), today.getProgressPercent());
    }

    @Test
    void recommendByUserIdConsumesAssessmentAndResumeSignals() {
        CareerProfileApplicationService profileService = profileService(new InMemoryCareerProfileStorage());
        UserProfileSnapshot.AssessmentBlock assessment = new UserProfileSnapshot.AssessmentBlock();
        assessment.setScaleTitle("MBTI");
        assessment.setSummary("ENTP");
        profileService.saveAssessment("user-2", assessment);
        UserProfileSnapshot.ResumeBlock resume = new UserProfileSnapshot.ResumeBlock();
        resume.setLastResumeId(Long.valueOf(2L));
        resume.setLastResumeKey("resumes/pm.pdf");
        resume.setTitle("PM Resume");
        resume.setTargetJob("Product Manager");
        resume.setDiagnosisScore(Integer.valueOf(82));
        profileService.saveResume("user-2", resume);

        CareerAgentTodayApplicationService service = new CareerAgentTodayApplicationService(
                new CareerAgentTodayRuleService(),
                new CareerProfileRuleInputSource(profileService));

        CareerAgentTodayDto today = service.recommendByUserId("user-2");

        assertEquals("INTERVIEW_BOOTSTRAP", today.getStage());
        assertEquals("开始模拟面试", today.getActions().get(0).getLabel());
    }

    @Test
    void recommendByUserIdAddsPlanWeeklyActionsWhenPlanExists() {
        CareerProfileStorage profileStorage = new InMemoryCareerProfileStorage();
        CareerPlanStorage planStorage = new InMemoryCareerPlanStorage();
        CareerProfileApplicationService profileService = profileService(profileStorage, planStorage);
        profileService.saveAssessment("plan-today-user-1", assessment());
        UserProfileSnapshot.ResumeBlock resume = new UserProfileSnapshot.ResumeBlock();
        resume.setLastResumeId(Long.valueOf(3L));
        resume.setLastResumeKey("resumes/java.pdf");
        resume.setTargetJob("Java Engineer");
        resume.setDiagnosisScore(Integer.valueOf(80));
        profileService.saveResume("plan-today-user-1", resume);
        CareerPlanApplicationService planService = new CareerPlanApplicationService(
                planStorage, profileService, new CareerPlanSummaryService());
        CareerPlanSaveRequest request = new CareerPlanSaveRequest();
        request.setTargetRole("Java Engineer");
        request.setWeeklyFocus(Arrays.asList("优化 Java 项目经历", "投递 3 个 Java 岗位"));
        planService.savePlan("plan-today-user-1", request);

        CareerAgentTodayApplicationService service = new CareerAgentTodayApplicationService(
                new CareerAgentTodayRuleService(),
                new CareerProfileRuleInputSource(profileService, planService));

        CareerAgentTodayDto today = service.recommendByUserId("plan-today-user-1");

        int planWeekly = 0;
        for (CareerAgentTodayDto.Action action : today.getActions()) {
            if ("PLAN_WEEKLY".equals(action.getSource())) {
                planWeekly++;
            }
        }
        assertEquals(2, planWeekly);
    }

    @Test
    void recommendByUserIdDoesNotInventPlanWeeklyActionsWhenPlanMissing() {
        CareerProfileApplicationService profileService = profileService(new InMemoryCareerProfileStorage(),
                new InMemoryCareerPlanStorage());
        profileService.saveAssessment("plan-today-user-2", assessment());

        CareerAgentTodayApplicationService service = new CareerAgentTodayApplicationService(
                new CareerAgentTodayRuleService(),
                new CareerProfileRuleInputSource(profileService, new CareerPlanApplicationService(
                        new InMemoryCareerPlanStorage(), profileService, new CareerPlanSummaryService())));

        CareerAgentTodayDto today = service.recommendByUserId("plan-today-user-2");

        for (CareerAgentTodayDto.Action action : today.getActions()) {
            assertEquals(false, "PLAN_WEEKLY".equals(action.getSource()));
        }
    }

    private CareerProfileApplicationService profileService(CareerProfileStorage storage) {
        return profileService(storage, null);
    }

    private CareerProfileApplicationService profileService(CareerProfileStorage storage, CareerPlanStorage planStorage) {
        return new CareerProfileApplicationService(
                storage,
                planStorage,
                new CareerProfileSnapshotMergeService(),
                new CareerProfileBuildService());
    }

    private UserProfileSnapshot.AssessmentBlock assessment() {
        UserProfileSnapshot.AssessmentBlock assessment = new UserProfileSnapshot.AssessmentBlock();
        assessment.setScaleTitle("MBTI");
        assessment.setSummary("ENTP");
        return assessment;
    }
}
