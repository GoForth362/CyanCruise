package v620.cc001.cloud01.app01.webapi;

import org.junit.jupiter.api.Test;
import v620.base.helper.career.CareerAgentTodayRuleService;
import v620.base.helper.career.CareerProfileBuildService;
import v620.base.helper.career.CareerProfileSnapshotMergeService;
import v620.cc001.base.common.dto.career.CareerAgentTodayDto;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;
import v620.cc001.cloud01.app01.mservice.CareerAgentTodayApplicationService;
import v620.cc001.cloud01.app01.mservice.CareerProfileApplicationService;
import v620.cc001.cloud01.app01.mservice.CareerProfileRuleInputSource;
import v620.cc001.cloud01.app01.mservice.InMemoryCareerProfileStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CareerAgentWebApiTest {

    @Test
    void todayByUserIdReturnsRecommendationFromProfileSnapshot() {
        CareerProfileApplicationService profileService = new CareerProfileApplicationService(
                new InMemoryCareerProfileStorage(),
                new CareerProfileSnapshotMergeService(),
                new CareerProfileBuildService());
        UserProfileSnapshot.PreferencesBlock preferences = new UserProfileSnapshot.PreferencesBlock();
        preferences.setTargetRole("Data Analyst");
        UserProfileSnapshot snapshot = new UserProfileSnapshot();
        snapshot.setPreferences(preferences);
        profileService.saveResume("api-user", resume("Data Analyst"));
        profileService.savePreferences("api-user", toPreferences("Data Analyst"));

        CareerAgentWebApi webApi = new CareerAgentWebApi(new CareerAgentTodayApplicationService(
                new CareerAgentTodayRuleService(),
                new CareerProfileRuleInputSource(profileService)));

        CareerAgentTodayDto today = webApi.todayByUserId("api-user");

        assertEquals("ASSESSMENT_BASELINE", today.getStage());
        assertEquals("完成 5 分钟测评", today.getActions().get(0).getLabel());
    }

    private UserProfileSnapshot.ResumeBlock resume(String targetJob) {
        UserProfileSnapshot.ResumeBlock resume = new UserProfileSnapshot.ResumeBlock();
        resume.setLastResumeId(Long.valueOf(9L));
        resume.setLastResumeKey("resumes/api.pdf");
        resume.setTargetJob(targetJob);
        resume.setDiagnosisScore(Integer.valueOf(80));
        return resume;
    }

    private v620.cc001.base.common.dto.career.CareerProfilePreferencesRequest toPreferences(String targetRole) {
        v620.cc001.base.common.dto.career.CareerProfilePreferencesRequest request =
                new v620.cc001.base.common.dto.career.CareerProfilePreferencesRequest();
        request.setTargetRole(targetRole);
        return request;
    }
}
