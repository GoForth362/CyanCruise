package v620.cc001.cloud01.app01.webapi.career;

import org.junit.jupiter.api.Test;
import v620.base.helper.career.CareerPlanSummaryService;
import v620.base.helper.career.CareerProfileBuildService;
import v620.base.helper.career.CareerProfileSnapshotMergeService;
import v620.cc001.base.common.dto.career.CareerPlanPhaseDto;
import v620.cc001.base.common.dto.career.CareerPlanRecordDto;
import v620.cc001.base.common.dto.career.CareerPlanSummaryDto;
import v620.cc001.base.common.dto.career.CareerPlanWeeklyPlanDto;
import v620.cc001.cloud01.app01.mservice.application.CareerPlanApplicationService;
import v620.cc001.cloud01.app01.mservice.application.CareerProfileApplicationService;
import v620.cc001.cloud01.app01.mservice.auth.impl.DevelopmentCyanCruiseIdentityResolver;
import v620.cc001.cloud01.app01.mservice.auth.impl.IdentityAwareCyanCruiseWebApiBoundary;
import v620.cc001.cloud01.app01.mservice.auth.IdentityBoundaryException;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerPlanStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerProfileStorage;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CareerPlanAgentWebApiTest {

    @Test
    void generationUsesResolvedCosmicUserAndRejectsUserMismatch() {
        InMemoryCareerPlanStorage planStorage = new InMemoryCareerPlanStorage();
        CareerProfileApplicationService profileService = new CareerProfileApplicationService(
                new InMemoryCareerProfileStorage(), planStorage,
                new CareerProfileSnapshotMergeService(), new CareerProfileBuildService());
        CareerPlanApplicationService service = new CareerPlanApplicationService(planStorage, profileService,
                new CareerPlanSummaryService(), (userId, targetRole, profile) -> agentPlan());
        CareerPlanWebApi webApi = new CareerPlanWebApi(service,
                new IdentityAwareCyanCruiseWebApiBoundary(
                        new DevelopmentCyanCruiseIdentityResolver("resolved-user")));

        CareerPlanSummaryDto result = webApi.generate("resolved-user");

        assertEquals(Boolean.TRUE, result.getHasPlan());
        assertEquals("后端开发工程师", planStorage.load("resolved-user").getTargetRole());
        assertThrows(IdentityBoundaryException.class, () -> webApi.generate("different-user"));
    }

    private CareerPlanRecordDto agentPlan() {
        CareerPlanRecordDto plan = new CareerPlanRecordDto();
        plan.setTargetRole("后端开发工程师");
        plan.setStartStateSummary("根据当前资料生成的路线图。");
        plan.setPlanningMode("AGENT");
        CareerPlanPhaseDto phase = new CareerPlanPhaseDto();
        phase.setPhaseId("year-1");
        phase.setHorizon("1年");
        phase.setTitle("完成求职准备");
        phase.setGoal("形成可投递能力");
        phase.setActions(Arrays.asList("完善项目证据"));
        plan.setPhases(Arrays.asList(phase));
        CareerPlanWeeklyPlanDto weekly = new CareerPlanWeeklyPlanDto();
        weekly.setWeekTitle("本周重点");
        weekly.setWeekGoal("整理项目");
        weekly.setActions(Arrays.asList("整理项目证据"));
        plan.setWeeklyPlan(weekly);
        plan.setDailySuggestions(Arrays.asList("今天整理一个项目"));
        return plan;
    }
}
