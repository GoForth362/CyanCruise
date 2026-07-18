package v620.cc001.cloud01.app01.mservice;

import org.junit.jupiter.api.Test;
import v620.base.helper.career.CareerPlanSummaryService;
import v620.base.helper.career.CareerProfileBuildService;
import v620.base.helper.career.CareerProfileSnapshotMergeService;
import v620.cc001.base.common.dto.career.CareerPlanPhaseDto;
import v620.cc001.base.common.dto.career.CareerPlanRecordDto;
import v620.cc001.base.common.dto.career.CareerPlanSaveRequest;
import v620.cc001.base.common.dto.career.CareerPlanSummaryDto;
import v620.cc001.base.common.dto.career.CareerPlanWeeklyPlanDto;
import v620.cc001.base.common.dto.career.CareerProfilePreferencesRequest;
import v620.cc001.base.common.dto.career.CareerUserProfileDto;
import v620.cc001.cloud01.app01.mservice.ai.CareerPlanAiGenerator;
import v620.cc001.cloud01.app01.mservice.application.CareerPlanApplicationService;
import v620.cc001.cloud01.app01.mservice.application.CareerProfileApplicationService;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerPlanStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerProfileStorage;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CareerPlanAgentGenerationApplicationServiceTest {

    @Test
    void explicitAgentGenerationPersistsPlanForTodayActions() {
        InMemoryCareerPlanStorage planStorage = new InMemoryCareerPlanStorage();
        CareerProfileApplicationService profileService = profileService(planStorage);
        saveTarget(profileService, "agent-plan-user", "Java 开发工程师");
        CareerPlanApplicationService service = new CareerPlanApplicationService(planStorage, profileService,
                new CareerPlanSummaryService(), successfulGenerator());

        CareerPlanSummaryDto result = service.generateAgentPlan("agent-plan-user");

        assertEquals("AGENT", result.getPlanningMode());
        assertEquals("整理项目证据", result.getWeeklyPlan().getActions().get(0));
        assertEquals("今天整理一个项目", result.getDailySuggestions().get(0));
        assertEquals("Java 开发工程师", planStorage.load("agent-plan-user").getTargetRole());
    }

    @Test
    void failedAgentGenerationKeepsExistingPlan() {
        InMemoryCareerPlanStorage planStorage = new InMemoryCareerPlanStorage();
        CareerProfileApplicationService profileService = profileService(planStorage);
        CareerPlanApplicationService service = new CareerPlanApplicationService(planStorage, profileService,
                new CareerPlanSummaryService(), (userId, targetRole, profile) -> {
                    throw new IllegalStateException("platform failed");
                });
        CareerPlanSaveRequest existing = new CareerPlanSaveRequest();
        existing.setTargetRole("产品经理");
        existing.setWeeklyFocus(Arrays.asList("保留原计划"));
        service.savePlan("agent-plan-failure", existing);
        Integer versionBeforeFailure = planStorage.load("agent-plan-failure").getVersion();

        assertThrows(IllegalStateException.class, () -> service.generateAgentPlan("agent-plan-failure"));

        assertEquals("保留原计划", planStorage.load("agent-plan-failure").getWeeklyFocus().get(0));
        assertEquals(versionBeforeFailure, planStorage.load("agent-plan-failure").getVersion());
    }

    @Test
    void planWithOnlyStartedPhaseCannotBeRegeneratedOrOverwritten() {
        InMemoryCareerPlanStorage planStorage = new InMemoryCareerPlanStorage();
        CareerProfileApplicationService profileService = profileService(planStorage);
        final int[] generationCalls = {0};
        CareerPlanApplicationService service = new CareerPlanApplicationService(planStorage, profileService,
                new CareerPlanSummaryService(), (userId, targetRole, profile) -> {
                    generationCalls[0] += 1;
                    return successfulGenerator().generate(userId, targetRole, profile);
                });
        CareerPlanPhaseDto startedPhase = new CareerPlanPhaseDto();
        startedPhase.setPhaseId("phase-1");
        startedPhase.setTitle("保留正在执行的阶段");
        startedPhase.setStatus("IN_PROGRESS");
        CareerPlanSaveRequest existing = new CareerPlanSaveRequest();
        existing.setTargetRole("Java 开发工程师");
        existing.setPhases(Arrays.asList(startedPhase));
        service.savePlan("started-plan-user", existing);
        Integer versionBeforeAttempt = planStorage.load("started-plan-user").getVersion();

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> service.generateAgentPlan("started-plan-user"));

        assertEquals("当前路线图的所有阶段都已开始或完成，不能重新生成。请继续完成现有计划。", error.getMessage());
        assertEquals(0, generationCalls[0]);
        assertEquals("保留正在执行的阶段", planStorage.load("started-plan-user").getPhases().get(0).getTitle());
        assertEquals(versionBeforeAttempt, planStorage.load("started-plan-user").getVersion());
    }

    @Test
    void startedPhaseIsPreservedWhileUnstartedPhaseIsRegenerated() {
        InMemoryCareerPlanStorage planStorage = new InMemoryCareerPlanStorage();
        CareerProfileApplicationService profileService = profileService(planStorage);
        CareerPlanApplicationService service = new CareerPlanApplicationService(planStorage, profileService,
                new CareerPlanSummaryService(), (userId, targetRole, profile) -> {
                    CareerPlanRecordDto plan = new CareerPlanRecordDto();
                    CareerPlanPhaseDto generatedFirst = phase("phase-1", "智能体生成的第一阶段", "NOT_STARTED");
                    CareerPlanPhaseDto generatedSecond = phase("phase-2", "智能体更新的未开始阶段", "NOT_STARTED");
                    plan.setPhases(Arrays.asList(generatedFirst, generatedSecond));
                    CareerPlanWeeklyPlanDto weekly = new CareerPlanWeeklyPlanDto();
                    weekly.setWeekTitle("智能体新周计划");
                    plan.setWeeklyPlan(weekly);
                    plan.setDailySuggestions(Arrays.asList("智能体新每日建议"));
                    return plan;
                });
        CareerPlanSaveRequest existing = new CareerPlanSaveRequest();
        existing.setTargetRole("Java 开发工程师");
        existing.setPhases(Arrays.asList(
                phase("phase-1", "保留正在执行的阶段", "IN_PROGRESS"),
                phase("phase-2", "等待刷新的未开始阶段", "NOT_STARTED")));
        CareerPlanWeeklyPlanDto currentWeekly = new CareerPlanWeeklyPlanDto();
        currentWeekly.setWeekTitle("保留当前周计划");
        existing.setWeeklyPlan(currentWeekly);
        existing.setDailySuggestions(Arrays.asList("保留当前每日建议"));
        service.savePlan("partial-refresh-user", existing);
        Integer versionBeforeRefresh = planStorage.load("partial-refresh-user").getVersion();

        service.generateAgentPlan("partial-refresh-user");

        CareerPlanRecordDto saved = planStorage.load("partial-refresh-user");
        assertEquals("保留正在执行的阶段", saved.getPhases().get(0).getTitle());
        assertEquals("智能体更新的未开始阶段", saved.getPhases().get(1).getTitle());
        assertEquals("保留当前周计划", saved.getWeeklyPlan().getWeekTitle());
        assertEquals("保留当前每日建议", saved.getDailySuggestions().get(0));
        assertEquals(versionBeforeRefresh, saved.getVersion());
    }

    private CareerPlanPhaseDto phase(String phaseId, String title, String status) {
        CareerPlanPhaseDto phase = new CareerPlanPhaseDto();
        phase.setPhaseId(phaseId);
        phase.setTitle(title);
        phase.setStatus(status);
        phase.setActions(Arrays.asList("完成阶段行动"));
        return phase;
    }

    private CareerPlanAiGenerator successfulGenerator() {
        return new CareerPlanAiGenerator() {
            @Override
            public CareerPlanRecordDto generate(String userId, String targetRole, CareerUserProfileDto profile) {
                CareerPlanRecordDto plan = new CareerPlanRecordDto();
                plan.setTargetRole(targetRole);
                plan.setStartStateSummary("已有项目基础，需要补充量化证据。");
                plan.setPlanningMode("AGENT");
                plan.setAgentStatus("AGENT_GENERATED");
                CareerPlanPhaseDto phase = new CareerPlanPhaseDto();
                phase.setPhaseId("year-1");
                phase.setHorizon("1年");
                phase.setTitle("形成可投递能力");
                phase.setGoal("完成求职准备");
                phase.setActions(Arrays.asList("完善项目"));
                plan.setPhases(Arrays.asList(phase));
                CareerPlanWeeklyPlanDto weeklyPlan = new CareerPlanWeeklyPlanDto();
                weeklyPlan.setWeekTitle("本周重点");
                weeklyPlan.setWeekGoal("补齐证据");
                weeklyPlan.setActions(Arrays.asList("整理项目证据"));
                plan.setWeeklyPlan(weeklyPlan);
                plan.setDailySuggestions(Arrays.asList("今天整理一个项目"));
                return plan;
            }
        };
    }

    private CareerProfileApplicationService profileService(InMemoryCareerPlanStorage planStorage) {
        return new CareerProfileApplicationService(new InMemoryCareerProfileStorage(), planStorage,
                new CareerProfileSnapshotMergeService(), new CareerProfileBuildService());
    }

    private void saveTarget(CareerProfileApplicationService profileService, String userId, String targetRole) {
        CareerProfilePreferencesRequest request = new CareerProfilePreferencesRequest();
        request.setTargetRole(targetRole);
        profileService.savePreferences(userId, request);
    }
}
