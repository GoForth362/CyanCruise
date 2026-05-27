package v620.cc001.cloud01.app01.mservice;

import org.junit.jupiter.api.Test;
import v620.base.helper.career.CareerAgentTodayRuleService;
import v620.base.helper.career.CareerProfileBuildService;
import v620.base.helper.career.CareerProfileSnapshotMergeService;
import v620.cc001.base.common.dto.career.CareerAgentTodayDto;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;

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

    private CareerProfileApplicationService profileService(CareerProfileStorage storage) {
        return new CareerProfileApplicationService(
                storage,
                new CareerProfileSnapshotMergeService(),
                new CareerProfileBuildService());
    }
}
