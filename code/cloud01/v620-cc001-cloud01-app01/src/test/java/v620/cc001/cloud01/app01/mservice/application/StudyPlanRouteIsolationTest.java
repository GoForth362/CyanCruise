package v620.cc001.cloud01.app01.mservice.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import v620.base.helper.career.CareerPlanSummaryService;
import v620.base.helper.career.CareerProfileBuildService;
import v620.base.helper.career.CareerProfileSnapshotMergeService;
import v620.base.helper.career.StudyPlanSummaryService;
import v620.cc001.base.common.dto.ai.AgentTaskFlowResponseDto;
import v620.cc001.base.common.dto.career.CareerDailyPlanDto;
import v620.cc001.base.common.dto.career.CareerDailyTaskDto;
import v620.cc001.base.common.dto.career.CareerDailyTaskUpdateRequest;
import v620.cc001.base.common.dto.career.CareerPlanPhaseDto;
import v620.cc001.base.common.dto.career.CareerPlanRecordDto;
import v620.cc001.base.common.dto.career.CareerRouteContext;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;
import v620.cc001.base.common.dto.furtherstudy.StudyCenterSelectionDto;
import v620.cc001.base.common.dto.furtherstudy.StudyPlanningMaterialDto;
import v620.cc001.base.common.dto.ai.AgentTaskFlowRequestDto;
import v620.cc001.cloud01.app01.mservice.ai.AgentPlatformTaskFlowClient;
import v620.cc001.cloud01.app01.mservice.ai.AgentPlatformTaskFlowConfig;
import v620.cc001.cloud01.app01.mservice.ai.impl.AgentPlatformStudyPlanGenerator;
import v620.cc001.cloud01.app01.mservice.ai.StudyPlanAiGenerator;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerDailyTaskStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerPlanStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerProfileStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryStudyCenterStorage;

class StudyPlanRouteIsolationTest {

    @Test
    void keepsEmploymentAndStudyPlansInDifferentSlots() {
        String userId = "route-isolation-user";
        InMemoryCareerPlanStorage employment = new InMemoryCareerPlanStorage();
        InMemoryStudyCenterStorage study = new InMemoryStudyCenterStorage();
        employment.save(userId, plan(userId, "Java 后端开发", "employment-1"));
        study.savePlan(userId, plan(userId, "电子科技大学考研", "study-1"));

        study.loadPlan(userId).setTargetRole("浙江大学保研");

        assertEquals("Java 后端开发", employment.load(userId).getTargetRole());
        assertEquals("浙江大学保研", study.loadPlan(userId).getTargetRole());
    }

    @Test
    void choosesAgentByStudyDirection() {
        final AtomicInteger postgraduateCalls = new AtomicInteger();
        final AtomicInteger recommendationCalls = new AtomicInteger();
        final AtomicInteger abroadCalls = new AtomicInteger();
        Map<String, AgentPlatformTaskFlowClient> clients = new LinkedHashMap<String, AgentPlatformTaskFlowClient>();
        clients.put(CareerRouteContext.POSTGRADUATE, client(postgraduateCalls));
        clients.put(CareerRouteContext.RECOMMENDATION, client(recommendationCalls));
        clients.put(CareerRouteContext.STUDY_ABROAD, client(abroadCalls));
        Map<String, AgentPlatformTaskFlowConfig> configs = new LinkedHashMap<String, AgentPlatformTaskFlowConfig>();
        configs.put(CareerRouteContext.POSTGRADUATE, config("postgraduate-flow"));
        configs.put(CareerRouteContext.RECOMMENDATION, config("recommendation-flow"));
        configs.put(CareerRouteContext.STUDY_ABROAD, config("abroad-flow"));
        AgentPlatformStudyPlanGenerator generator = new AgentPlatformStudyPlanGenerator(clients, configs);

        generator.generate("u1", CareerRouteContext.POSTGRADUATE, "电子科技大学", null);
        generator.generate("u1", CareerRouteContext.RECOMMENDATION, "浙江大学", null);
        CareerPlanRecordDto result = generator.generate("u1", CareerRouteContext.STUDY_ABROAD, "东京大学", null);

        assertEquals(1, postgraduateCalls.get());
        assertEquals(1, recommendationCalls.get());
        assertEquals(1, abroadCalls.get());
        assertEquals(CareerRouteContext.STUDY_ABROAD, result.getStudyDirection());
        assertEquals(CareerRouteContext.STUDY, result.getRouteType());
    }

    @Test
    void sendsCurrentUsersExtractedMaterialsToPostgraduateAgent() {
        final AtomicReference<AgentTaskFlowRequestDto> captured = new AtomicReference<AgentTaskFlowRequestDto>();
        AgentPlatformTaskFlowClient client = request -> {
            captured.set(request);
            return successfulAgentResponse();
        };
        Map<String, AgentPlatformTaskFlowClient> clients = new LinkedHashMap<String, AgentPlatformTaskFlowClient>();
        clients.put(CareerRouteContext.POSTGRADUATE, client);
        Map<String, AgentPlatformTaskFlowConfig> configs = new LinkedHashMap<String, AgentPlatformTaskFlowConfig>();
        configs.put(CareerRouteContext.POSTGRADUATE, config("postgraduate-flow"));
        AgentPlatformStudyPlanGenerator generator = new AgentPlatformStudyPlanGenerator(clients, configs);
        StudyPlanningMaterialDto material = new StudyPlanningMaterialDto();
        material.setMaterialId("m1");
        material.setTitle("电子科技大学招生说明");
        material.setOriginalFilename("admission.pdf");
        material.setMaterialType("ADMISSION_GUIDE");
        material.setExtractedText("考试科目和培养方向说明");

        generator.generate("u1", CareerRouteContext.POSTGRADUATE, "电子科技大学",
                null, null, Arrays.asList(material));

        String question = captured.get().getInputs().get("question");
        assertTrue(question.contains("电子科技大学招生说明"));
        assertTrue(question.contains("考试科目和培养方向说明"));
        assertTrue(question.contains("\"currentDate\""));
        assertTrue(question.contains("\"existingProgress\""));
    }

    @Test
    void sendsFlatStudyProfileAndProvisionalGenerationContractToTaskFlow() {
        final AtomicReference<AgentTaskFlowRequestDto> captured = new AtomicReference<AgentTaskFlowRequestDto>();
        AgentPlatformTaskFlowClient client = request -> {
            captured.set(request);
            return successfulAgentResponse();
        };
        Map<String, AgentPlatformTaskFlowClient> clients = new LinkedHashMap<String, AgentPlatformTaskFlowClient>();
        clients.put(CareerRouteContext.POSTGRADUATE, client);
        Map<String, AgentPlatformTaskFlowConfig> configs = new LinkedHashMap<String, AgentPlatformTaskFlowConfig>();
        configs.put(CareerRouteContext.POSTGRADUATE, config("postgraduate-flow"));
        UserProfileSnapshot snapshot = new UserProfileSnapshot();
        UserProfileSnapshot.OnboardingBlock onboarding = new UserProfileSnapshot.OnboardingBlock();
        UserProfileSnapshot.EducationBlock education = new UserProfileSnapshot.EducationBlock();
        education.setSchool("成都理工大学");
        education.setMajor("软件工程");
        education.setGraduationYear("2027");
        onboarding.setEducation(education);
        onboarding.setStage("大三暑期");
        onboarding.setWeeklyAvailability("每周 20 小时");
        snapshot.setOnboarding(onboarding);
        snapshot.setUpdatedAt(LocalDateTime.of(2026, 7, 18, 16, 0));
        UserProfileSnapshot.AssessmentBlock assessment = new UserProfileSnapshot.AssessmentBlock();
        assessment.setSummary("数学基础较好，但英语阅读需要补强");
        snapshot.setAssessment(assessment);

        new AgentPlatformStudyPlanGenerator(clients, configs).generate("u1",
                CareerRouteContext.POSTGRADUATE, "电子科技大学", null, snapshot,
                null, Arrays.<StudyPlanningMaterialDto>asList());

        String question = captured.get().getInputs().get("question");
        assertTrue(question.contains("\"direction\":\"POSTGRADUATE_EXAM\""));
        assertTrue(question.contains("\"school\":\"成都理工大学\""));
        assertTrue(question.contains("\"major\":\"软件工程\""));
        assertTrue(question.contains("\"weeklyHours\":\"每周 20 小时\""));
        assertTrue(question.contains("\"assessmentSummary\":\"数学基础较好，但英语阅读需要补强\""));
        assertFalse(question.contains("\"updatedAt\""));
        assertTrue(question.contains("不得拒绝生成"));
        assertTrue(question.contains("只输出一个 camelCase JSON 对象"));
        assertTrue(question.contains("中文分号分隔 4 至 6 条事实"));
        assertTrue(question.contains("当前阶段、已有基础、准备差距、考研目标和待确认信息"));
    }

    @Test
    void truncatesOversizedMaterialBeforeCallingAgent() {
        final AtomicReference<AgentTaskFlowRequestDto> captured = new AtomicReference<AgentTaskFlowRequestDto>();
        AgentPlatformTaskFlowClient client = request -> {
            captured.set(request);
            return successfulAgentResponse();
        };
        Map<String, AgentPlatformTaskFlowClient> clients = new LinkedHashMap<String, AgentPlatformTaskFlowClient>();
        clients.put(CareerRouteContext.POSTGRADUATE, client);
        Map<String, AgentPlatformTaskFlowConfig> configs = new LinkedHashMap<String, AgentPlatformTaskFlowConfig>();
        configs.put(CareerRouteContext.POSTGRADUATE, config("postgraduate-flow"));
        StudyPlanningMaterialDto material = new StudyPlanningMaterialDto();
        material.setMaterialId("oversized");
        material.setTitle("超长招生资料");
        material.setExtractedText(repeat("考试说明", 10000));

        new AgentPlatformStudyPlanGenerator(clients, configs).generate("u1",
                CareerRouteContext.POSTGRADUATE, "电子科技大学", null, null, Arrays.asList(material));

        String question = captured.get().getInputs().get("question");
        assertTrue(question.length() < 20000);
        assertTrue(question.contains("超长招生资料"));
    }

    @Test
    void acceptsPostgraduateTaskFlowContractWithNestedDailySuggestions() {
        AgentPlatformTaskFlowClient client = request -> {
            AgentTaskFlowResponseDto response = new AgentTaskFlowResponseDto();
            response.setSuccess(true);
            response.setAnswer("{\"direction\":\"POSTGRADUATE_EXAM\","
                    + "\"targetSummary\":\"电子科技大学计算机方向考研\","
                    + "\"horizonMonths\":5,"
                    + "\"phases\":[{\"phaseId\":\"phase-1\",\"horizon\":\"未来1个月\","
                    + "\"title\":\"信息核验\",\"status\":\"IN_PROGRESS\","
                    + "\"actions\":[\"核验招生目录\"],\"kpis\":[\"形成核验清单\"]}],"
                    + "\"weeklyPlan\":{\"weekTitle\":\"第1周\","
                    + "\"actions\":[\"核验招生目录\"],"
                    + "\"dailySuggestions\":[\"第1天核验目标专业\"]}}");
            response.setAnswer(richFullYearAnswer());
            return response;
        };
        Map<String, AgentPlatformTaskFlowClient> clients =
                new LinkedHashMap<String, AgentPlatformTaskFlowClient>();
        clients.put(CareerRouteContext.POSTGRADUATE, client);
        Map<String, AgentPlatformTaskFlowConfig> configs =
                new LinkedHashMap<String, AgentPlatformTaskFlowConfig>();
        configs.put(CareerRouteContext.POSTGRADUATE, config("postgraduate-flow"));

        CareerPlanRecordDto plan = new AgentPlatformStudyPlanGenerator(clients, configs)
                .generate("u1", CareerRouteContext.POSTGRADUATE, "电子科技大学", null);

        assertEquals("电子科技大学计算机方向考研", plan.getTargetRole());
        assertEquals(Integer.valueOf(1), plan.getHorizonYears());
        assertEquals(Arrays.asList("第1天核验目标专业"), plan.getDailySuggestions());
        assertEquals("AGENT_GENERATED", plan.getAgentStatus());
    }

    @Test
    void usesSameDataWrappedPlanContractAsEmploymentParser() {
        AgentPlatformTaskFlowClient client = request -> {
            AgentTaskFlowResponseDto response = new AgentTaskFlowResponseDto();
            response.setSuccess(true);
            response.setAnswer("{\"data\":" + fullYearAnswer() + "}");
            return response;
        };
        Map<String, AgentPlatformTaskFlowClient> clients =
                new LinkedHashMap<String, AgentPlatformTaskFlowClient>();
        clients.put(CareerRouteContext.POSTGRADUATE, client);
        Map<String, AgentPlatformTaskFlowConfig> configs =
                new LinkedHashMap<String, AgentPlatformTaskFlowConfig>();
        configs.put(CareerRouteContext.POSTGRADUATE, config("postgraduate-flow"));

        CareerPlanRecordDto plan = new AgentPlatformStudyPlanGenerator(clients, configs)
                .generate("u1", CareerRouteContext.POSTGRADUATE, "target school", null);

        assertEquals("postgraduate goal", plan.getTargetRole());
        assertEquals(3, plan.getPhases().size());
        assertEquals("months 7-12", plan.getPhases().get(2).getHorizon());
    }

    private String richFullYearAnswer() {
        return "{\"direction\":\"POSTGRADUATE_EXAM\"," 
                + "\"targetSummary\":\"\u7535\u5b50\u79d1\u6280\u5927\u5b66\u8ba1\u7b97\u673a\u65b9\u5411\u8003\u7814\"," 
                + "\"startStateSummary\":\"\u5f53\u524d\u57fa\u7840\u5df2\u6838\u9a8c\",\"horizonMonths\":12,\"phases\":["
                + phaseJson("phase-1", "months 1-2") + ","
                + phaseJson("phase-2", "months 3-6") + ","
                + phaseJson("phase-3", "months 7-12") + "],"
                + "\"weeklyPlan\":{\"weekTitle\":\"\u7b2c1\u5468\"," 
                + "\"actions\":[\"\u6838\u9a8c\u62db\u751f\u76ee\u5f55\"],"
                + "\"dailySuggestions\":[\"\u7b2c1\u5929\u6838\u9a8c\u76ee\u6807\u4e13\u4e1a\"]},"
                + "\"weeklyFocus\":[\"\u6838\u9a8c\u62db\u751f\u76ee\u5f55\"]}";
    }

    @Test
    void retriesPostgraduateTaskFlowWhenFirstResultOnlyCoversOneMonth() {
        final AtomicInteger calls = new AtomicInteger();
        AgentPlatformTaskFlowClient client = request -> {
            AgentTaskFlowResponseDto response = new AgentTaskFlowResponseDto();
            response.setSuccess(true);
            if (calls.incrementAndGet() == 1) {
                response.setAnswer("{\"targetRole\":\"goal\",\"phases\":["
                        + phaseJson("short", "month 1") + "],"
                        + "\"weeklyPlan\":{\"actions\":[\"action\"]},"
                        + "\"dailySuggestions\":[\"action\"]}");
            } else {
                response.setAnswer(fullYearAnswer());
            }
            return response;
        };
        Map<String, AgentPlatformTaskFlowClient> clients =
                new LinkedHashMap<String, AgentPlatformTaskFlowClient>();
        clients.put(CareerRouteContext.POSTGRADUATE, client);
        Map<String, AgentPlatformTaskFlowConfig> configs =
                new LinkedHashMap<String, AgentPlatformTaskFlowConfig>();
        configs.put(CareerRouteContext.POSTGRADUATE, config("postgraduate-flow"));

        CareerPlanRecordDto plan = new AgentPlatformStudyPlanGenerator(clients, configs)
                .generate("u1", CareerRouteContext.POSTGRADUATE, "target school", null);

        assertEquals(2, calls.get());
        assertEquals(3, plan.getPhases().size());
        assertEquals("months 7-12", plan.getPhases().get(2).getHorizon());
    }

    @Test
    void replacesLegacySingleStartedPhaseWithVerifiedAgentRoute() {
        String userId = "legacy-short-study-route";
        InMemoryStudyCenterStorage storage = new InMemoryStudyCenterStorage();
        StudyCenterSelectionDto selection = new StudyCenterSelectionDto();
        selection.setUserId(userId);
        selection.setDirection(CareerRouteContext.POSTGRADUATE);
        storage.saveSelection(selection);
        CareerPlanRecordDto old = plan(userId, "old goal", "protected-first");
        old.setStudyDirection(CareerRouteContext.POSTGRADUATE);
        old.getPhases().get(0).setStatus("IN_PROGRESS");
        storage.savePlan(userId, old);
        StudyPlanAiGenerator generator = (id, direction, school, profile) -> {
            CareerPlanRecordDto generated = plan(id, "new goal", "replacement-first");
            generated.setStudyDirection(direction);
            generated.setPhases(Arrays.asList(
                    generated.getPhases().get(0),
                    phase("generated-second", "NOT_STARTED"),
                    phase("generated-third", "NOT_STARTED")));
            return generated;
        };
        StudyPlanApplicationService service = new StudyPlanApplicationService(storage, profileService(),
                new StudyPlanSummaryService(), generator, Clock.systemUTC());

        service.generateAgentPlan(userId);

        assertEquals(3, storage.loadPlan(userId).getPhases().size());
        assertEquals("replacement-first", storage.loadPlan(userId).getPhases().get(0).getPhaseId());
        assertEquals("generated-second", storage.loadPlan(userId).getPhases().get(1).getPhaseId());
        assertEquals("generated-third", storage.loadPlan(userId).getPhases().get(2).getPhaseId());
        assertEquals("AGENT", storage.loadPlan(userId).getPlanningMode());
        assertEquals("AGENT_GENERATED", storage.loadPlan(userId).getAgentStatus());
    }

    @Test
    void rejectsMissingDirectionAndKeepsOldPlanWhenAgentFails() {
        String userId = "study-failure-user";
        InMemoryStudyCenterStorage storage = new InMemoryStudyCenterStorage();
        StudyPlanAiGenerator failing = (id, direction, school, profile) -> { throw new IllegalStateException("offline"); };
        StudyPlanApplicationService service = new StudyPlanApplicationService(storage, profileService(),
                new StudyPlanSummaryService(), failing, Clock.systemUTC());
        assertThrows(IllegalArgumentException.class, () -> service.ensurePlan(userId));

        StudyCenterSelectionDto selection = new StudyCenterSelectionDto();
        selection.setUserId(userId); selection.setDirection(CareerRouteContext.RECOMMENDATION);
        storage.saveSelection(selection);
        CareerPlanRecordDto old = plan(userId, "原保研规划", "study-old");
        old.setStudyDirection(CareerRouteContext.RECOMMENDATION);
        storage.savePlan(userId, old);

        assertThrows(IllegalStateException.class, () -> service.generateAgentPlan(userId));
        assertEquals("原保研规划", storage.loadPlan(userId).getTargetRole());
    }

    @Test
    void ensureDoesNotCreateRuleFallbackStudyPlan() {
        String userId = "study-no-fallback-user";
        InMemoryStudyCenterStorage storage = new InMemoryStudyCenterStorage();
        StudyCenterSelectionDto selection = new StudyCenterSelectionDto();
        selection.setUserId(userId);
        selection.setDirection(CareerRouteContext.POSTGRADUATE);
        storage.saveSelection(selection);
        StudyPlanApplicationService service = new StudyPlanApplicationService(storage, profileService(),
                new StudyPlanSummaryService(), null, Clock.systemUTC());

        assertFalse(Boolean.TRUE.equals(service.ensurePlan(userId).getHasPlan()));
        assertEquals(null, storage.loadPlan(userId));
    }

    @Test
    void readingSummaryDeletesHistoricalRuleFallbackAndItsDailyTasks() {
        String userId = "study-remove-fallback-user";
        InMemoryStudyCenterStorage storage = new InMemoryStudyCenterStorage();
        StudyCenterSelectionDto selection = new StudyCenterSelectionDto();
        selection.setUserId(userId);
        selection.setDirection(CareerRouteContext.POSTGRADUATE);
        storage.saveSelection(selection);
        CareerPlanRecordDto fallback = plan(userId, "虚假考研路线", "fallback-phase");
        fallback.setStudyDirection(CareerRouteContext.POSTGRADUATE);
        fallback.setPlanningMode("RULE_FALLBACK");
        fallback.setAgentStatus("FALLBACK_READY");
        fallback.setModelUsed("study-rule-fallback");
        storage.savePlan(userId, fallback);
        CareerDailyTaskDto task = new CareerDailyTaskDto();
        task.setTaskId("fallback-task");
        storage.saveDailyTask(userId, task);
        StudyPlanApplicationService service = new StudyPlanApplicationService(storage, profileService(),
                new StudyPlanSummaryService(), null, Clock.systemUTC());

        assertFalse(Boolean.TRUE.equals(service.getSummary(userId).getHasPlan()));
        assertEquals(null, storage.loadPlan(userId));
        assertTrue(storage.listDailyTasks(userId).isEmpty());
    }

    @Test
    void readingSummaryDeletesUnmarkedLegacySinglePhaseAndItsDailyTasks() {
        String userId = "study-remove-unmarked-legacy-user";
        InMemoryStudyCenterStorage storage = new InMemoryStudyCenterStorage();
        StudyCenterSelectionDto selection = new StudyCenterSelectionDto();
        selection.setUserId(userId);
        selection.setDirection(CareerRouteContext.POSTGRADUATE);
        storage.saveSelection(selection);
        CareerPlanRecordDto legacy = plan(userId, "目标与考试信息确认", "legacy-single-phase");
        legacy.setStudyDirection(CareerRouteContext.POSTGRADUATE);
        legacy.getPhases().get(0).setStatus("IN_PROGRESS");
        storage.savePlan(userId, legacy);
        CareerDailyTaskDto task = new CareerDailyTaskDto();
        task.setTaskId("legacy-daily-task");
        storage.saveDailyTask(userId, task);
        StudyPlanApplicationService service = new StudyPlanApplicationService(storage, profileService(),
                new StudyPlanSummaryService(), null, Clock.systemUTC());

        assertFalse(Boolean.TRUE.equals(service.getSummary(userId).getHasPlan()));
        assertEquals(null, storage.loadPlan(userId));
        assertTrue(storage.listDailyTasks(userId).isEmpty());
    }

    @Test
    void readingSummaryDeletesThreePhaseRouteThatEndsBeforeTwelfthMonth() {
        String userId = "study-remove-incomplete-three-phase-route";
        InMemoryStudyCenterStorage storage = new InMemoryStudyCenterStorage();
        StudyCenterSelectionDto selection = new StudyCenterSelectionDto();
        selection.setUserId(userId);
        selection.setDirection(CareerRouteContext.POSTGRADUATE);
        storage.saveSelection(selection);
        CareerPlanRecordDto legacy = plan(userId, "目标与考试信息确认", "legacy-first");
        legacy.setStudyDirection(CareerRouteContext.POSTGRADUATE);
        legacy.setPlanningMode("AGENT");
        legacy.setAgentStatus("AGENT_GENERATED");
        CareerPlanPhaseDto first = legacy.getPhases().get(0);
        first.setHorizon("现在-1个月");
        first.setStatus("IN_PROGRESS");
        CareerPlanPhaseDto second = phase("legacy-second", "NOT_STARTED");
        second.setHorizon("2026-09-01 至 2026-12-31");
        CareerPlanPhaseDto third = phase("legacy-third", "NOT_STARTED");
        third.setHorizon("2027-01-01 至 2027-04-30");
        legacy.setPhases(Arrays.asList(first, second, third));
        storage.savePlan(userId, legacy);
        CareerDailyTaskDto task = new CareerDailyTaskDto();
        task.setTaskId("legacy-incomplete-daily-task");
        storage.saveDailyTask(userId, task);
        StudyPlanApplicationService service = new StudyPlanApplicationService(storage, profileService(),
                new StudyPlanSummaryService(), null, Clock.systemUTC());

        assertFalse(Boolean.TRUE.equals(service.getSummary(userId).getHasPlan()));
        assertEquals(null, storage.loadPlan(userId));
        assertTrue(storage.listDailyTasks(userId).isEmpty());
    }

    @Test
    void changingStudyDirectionDoesNotMergeStartedPhasesFromOldDirection() {
        String userId = "study-direction-switch-user";
        InMemoryStudyCenterStorage storage = new InMemoryStudyCenterStorage();
        StudyCenterSelectionDto selection = new StudyCenterSelectionDto();
        selection.setUserId(userId); selection.setDirection(CareerRouteContext.STUDY_ABROAD);
        storage.saveSelection(selection);
        CareerPlanRecordDto old = plan(userId, "旧考研规划", "postgraduate-started");
        old.setStudyDirection(CareerRouteContext.POSTGRADUATE);
        old.getPhases().get(0).setStatus("COMPLETED");
        storage.savePlan(userId, old);
        StudyPlanAiGenerator generator = (id, direction, school, profile) -> {
            CareerPlanRecordDto generated = plan(id, "新留学规划", "abroad-new");
            generated.setStudyDirection(direction);
            return generated;
        };
        StudyPlanApplicationService service = new StudyPlanApplicationService(storage, profileService(),
                new StudyPlanSummaryService(), generator, Clock.systemUTC());

        service.generateAgentPlan(userId);

        assertEquals("abroad-new", storage.loadPlan(userId).getPhases().get(0).getPhaseId());
        assertEquals(CareerRouteContext.STUDY_ABROAD, storage.loadPlan(userId).getStudyDirection());
    }

    @Test
    void refreshesOnlyNotStartedPhasesForTheSameDirection() {
        String userId = "study-protected-phase-user";
        InMemoryStudyCenterStorage storage = new InMemoryStudyCenterStorage();
        StudyCenterSelectionDto selection = new StudyCenterSelectionDto();
        selection.setUserId(userId);
        selection.setDirection(CareerRouteContext.POSTGRADUATE);
        storage.saveSelection(selection);
        CareerPlanRecordDto old = plan(userId, "原考研规划", "protected");
        old.setStudyDirection(CareerRouteContext.POSTGRADUATE);
        old.getPhases().get(0).setStatus("IN_PROGRESS");
        CareerPlanPhaseDto refreshable = phase("refreshable-old", "NOT_STARTED");
        old.setPhases(Arrays.asList(old.getPhases().get(0), refreshable,
                phase("refreshable-third-old", "NOT_STARTED")));
        applyFullYearHorizons(old);
        old.setPlanningMode("AGENT");
        old.setAgentStatus("AGENT_GENERATED");
        storage.savePlan(userId, old);
        StudyPlanAiGenerator generator = (id, direction, school, profile) -> {
            CareerPlanRecordDto generated = plan(id, "新考研规划", "replacement-first");
            generated.setStudyDirection(direction);
            generated.setPhases(Arrays.asList(generated.getPhases().get(0),
                    phase("replacement-second", "NOT_STARTED"),
                    phase("replacement-third", "NOT_STARTED")));
            applyFullYearHorizons(generated);
            return generated;
        };
        StudyPlanApplicationService service = new StudyPlanApplicationService(storage, profileService(),
                new StudyPlanSummaryService(), generator, Clock.systemUTC());

        service.generateAgentPlan(userId);

        assertEquals("protected", storage.loadPlan(userId).getPhases().get(0).getPhaseId());
        assertEquals("replacement-second", storage.loadPlan(userId).getPhases().get(1).getPhaseId());
        assertEquals("replacement-third", storage.loadPlan(userId).getPhases().get(2).getPhaseId());
        assertEquals("IN_PROGRESS", storage.loadPlan(userId).getPhases().get(0).getStatus());
    }

    @Test
    void keepsDailyCompletionIsolatedBetweenRoutes() {
        String userId = "daily-route-user";
        Clock clock = Clock.fixed(Instant.parse("2026-07-17T08:00:00Z"), ZoneId.of("Asia/Shanghai"));
        InMemoryCareerPlanStorage employmentPlans = new InMemoryCareerPlanStorage();
        InMemoryCareerDailyTaskStorage employmentTasks = new InMemoryCareerDailyTaskStorage();
        employmentPlans.save(userId, plan(userId, "Java 后端开发", "employment-1"));
        CareerDailyPlanApplicationService employment = new CareerDailyPlanApplicationService(employmentPlans, employmentTasks, clock);

        InMemoryStudyCenterStorage studyStorage = new InMemoryStudyCenterStorage();
        StudyCenterSelectionDto selection = new StudyCenterSelectionDto();
        selection.setUserId(userId); selection.setDirection(CareerRouteContext.POSTGRADUATE); selection.setTargetSchool("电子科技大学");
        studyStorage.saveSelection(selection);
        StudyPlanApplicationService study = new StudyPlanApplicationService(studyStorage, profileService(),
                new StudyPlanSummaryService(), null, clock);
        CareerPlanRecordDto realStudyPlan = plan(userId, "电子科技大学考研规划", "study-1");
        realStudyPlan.setStudyDirection(CareerRouteContext.POSTGRADUATE);
        realStudyPlan.setPhases(Arrays.asList(realStudyPlan.getPhases().get(0),
                phase("study-2", "NOT_STARTED"), phase("study-3", "NOT_STARTED")));
        applyFullYearHorizons(realStudyPlan);
        realStudyPlan.setPlanningMode("AGENT");
        realStudyPlan.setAgentStatus("AGENT_GENERATED");
        studyStorage.savePlan(userId, realStudyPlan);

        CareerDailyPlanDto employmentToday = employment.getToday(userId);
        CareerDailyPlanDto studyToday = study.getToday(userId);
        CareerDailyTaskUpdateRequest update = new CareerDailyTaskUpdateRequest();
        update.setTaskId(studyToday.getItems().get(0).getTaskId()); update.setCompleted(Boolean.TRUE);
        study.updateToday(userId, update);

        assertEquals(CareerRouteContext.EMPLOYMENT, employmentToday.getRouteType());
        assertEquals(CareerRouteContext.STUDY, studyToday.getRouteType());
        assertFalse(employment.getToday(userId).getItems().get(0).isCompleted());
        assertTrue(study.getToday(userId).getCompletedCount().intValue() > 0);
    }

    private CareerProfileApplicationService profileService() {
        return new CareerProfileApplicationService(new InMemoryCareerProfileStorage(),
                new CareerProfileSnapshotMergeService(), new CareerProfileBuildService());
    }

    private CareerPlanRecordDto plan(String userId, String target, String phaseId) {
        CareerPlanRecordDto plan = new CareerPlanRecordDto();
        plan.setUserId(userId); plan.setTargetRole(target); plan.setVersion(Integer.valueOf(1));
        CareerPlanPhaseDto phase = new CareerPlanPhaseDto();
        phase.setPhaseId(phaseId); phase.setTitle("第一阶段"); phase.setStatus("NOT_STARTED");
        phase.setActions(Arrays.asList("完成第一项", "完成第二项"));
        plan.setPhases(Arrays.asList(phase));
        return plan;
    }

    private CareerPlanPhaseDto phase(String phaseId, String status) {
        CareerPlanPhaseDto phase = new CareerPlanPhaseDto();
        phase.setPhaseId(phaseId);
        phase.setTitle(phaseId);
        phase.setStatus(status);
        phase.setActions(Arrays.asList("完成行动"));
        return phase;
    }

    private void applyFullYearHorizons(CareerPlanRecordDto plan) {
        plan.getPhases().get(0).setHorizon("months 1-2");
        plan.getPhases().get(1).setHorizon("months 3-6");
        plan.getPhases().get(2).setHorizon("months 7-12");
    }

    private String repeat(String value, int count) {
        StringBuilder out = new StringBuilder(value.length() * count);
        for (int i = 0; i < count; i++) out.append(value);
        return out.toString();
    }

    private AgentPlatformTaskFlowClient client(final AtomicInteger calls) {
        return request -> {
            calls.incrementAndGet();
            return successfulAgentResponse();
        };
    }

    private AgentTaskFlowResponseDto successfulAgentResponse() {
        AgentTaskFlowResponseDto response = new AgentTaskFlowResponseDto();
        response.setSuccess(true);
        response.setAnswer("{\"targetRole\":\"升学目标\",\"startStateSummary\":\"当前基础\","
                + "\"phases\":[{\"phaseId\":\"s1\",\"horizon\":\"未来1至2个月\",\"title\":\"准备\","
                + "\"status\":\"NOT_STARTED\",\"actions\":[\"行动\"],\"kpis\":[\"完成阶段成果\"]}],"
                + "\"weeklyPlan\":{\"weekTitle\":\"本周\",\"actions\":[\"行动\"]},"
                + "\"dailySuggestions\":[\"行动\"]}");
        response.setAnswer(fullYearAnswer());
        return response;
    }

    private String fullYearAnswer() {
        return "{\"targetRole\":\"postgraduate goal\",\"startStateSummary\":\"current basis\","
                + "\"horizonYears\":1,\"phases\":["
                + phaseJson("s1", "months 1-2") + ","
                + phaseJson("s2", "months 3-6") + ","
                + phaseJson("s3", "months 7-12") + "],"
                + "\"weeklyPlan\":{\"weekTitle\":\"this week\",\"actions\":[\"action\"]},"
                + "\"dailySuggestions\":[\"action\"],\"weeklyFocus\":[\"action\"]}";
    }

    private String phaseJson(String phaseId, String horizon) {
        return "{\"phaseId\":\"" + phaseId + "\",\"horizon\":\"" + horizon
                + "\",\"title\":\"stage\",\"status\":\"NOT_STARTED\","
                + "\"actions\":[\"action\"],\"kpis\":[\"deliverable\"]}";
    }

    private AgentPlatformTaskFlowConfig config(String code) {
        AgentPlatformTaskFlowConfig config = new AgentPlatformTaskFlowConfig();
        config.setTaskFlowCode(code);
        return config;
    }
}
