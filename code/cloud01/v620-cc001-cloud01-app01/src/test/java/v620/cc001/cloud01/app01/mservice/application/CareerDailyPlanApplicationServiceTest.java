package v620.cc001.cloud01.app01.mservice.application;

import org.junit.jupiter.api.Test;
import v620.cc001.base.common.dto.career.CareerDailyPlanDto;
import v620.cc001.base.common.dto.career.CareerDailyTaskDto;
import v620.cc001.base.common.dto.career.CareerDailyTaskUpdateRequest;
import v620.cc001.base.common.dto.career.CareerPlanPhaseDto;
import v620.cc001.base.common.dto.career.CareerPlanRecordDto;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerDailyTaskStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerPlanStorage;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CareerDailyPlanApplicationServiceTest {

    @Test
    void keepsSameDayStableAndCarriesOnlyUnfinishedTasksToNextDay() {
        String userId = "daily-sequence-user";
        InMemoryCareerPlanStorage planStorage = new InMemoryCareerPlanStorage();
        InMemoryCareerDailyTaskStorage taskStorage = new InMemoryCareerDailyTaskStorage();
        planStorage.save(userId, plan(userId));

        CareerDailyPlanApplicationService dayOneService = service(planStorage, taskStorage, "2026-07-16T08:00:00Z");
        CareerDailyPlanDto first = dayOneService.getToday(userId);
        CareerDailyPlanDto repeated = dayOneService.getToday(userId);

        assertEquals(5, first.getItems().size());
        assertEquals(first.getItems().get(0).getTaskId(), repeated.getItems().get(0).getTaskId());

        for (int i = 0; i < 4; i += 1) {
            dayOneService.update(userId, update(first.getItems().get(i), true));
        }

        CareerDailyPlanApplicationService dayTwoService = service(planStorage, taskStorage, "2026-07-17T08:00:00Z");
        CareerDailyPlanDto second = dayTwoService.getToday(userId);

        assertEquals(2, second.getItems().size());
        assertEquals(first.getItems().get(4).getSourceTaskId(), second.getItems().get(0).getSourceTaskId());
        assertTrue(Boolean.TRUE.equals(second.getItems().get(0).getCarriedOver()));
        assertFalse(second.getItems().get(1).getSourceTaskId().equals(first.getItems().get(0).getSourceTaskId()));
    }

    @Test
    void movesToNextPhaseOnlyAfterCurrentPhaseSourcesAreCompleted() {
        String userId = "daily-phase-user";
        InMemoryCareerPlanStorage planStorage = new InMemoryCareerPlanStorage();
        InMemoryCareerDailyTaskStorage taskStorage = new InMemoryCareerDailyTaskStorage();
        planStorage.save(userId, plan(userId));

        CareerDailyPlanApplicationService dayOne = service(planStorage, taskStorage, "2026-07-16T08:00:00Z");
        CareerDailyPlanDto first = dayOne.getToday(userId);
        for (CareerDailyTaskDto task : first.getItems()) dayOne.update(userId, update(task, true));

        CareerDailyPlanApplicationService dayTwo = service(planStorage, taskStorage, "2026-07-17T08:00:00Z");
        CareerDailyPlanDto second = dayTwo.getToday(userId);
        for (CareerDailyTaskDto task : second.getItems()) dayTwo.update(userId, update(task, true));

        assertEquals("COMPLETED", planStorage.load(userId).getPhases().get(0).getStatus());
        assertEquals("IN_PROGRESS", planStorage.load(userId).getPhases().get(1).getStatus());

        CareerDailyPlanApplicationService dayThree = service(planStorage, taskStorage, "2026-07-18T08:00:00Z");
        CareerDailyPlanDto third = dayThree.getToday(userId);

        assertEquals("phase-2", third.getPhaseId());
        assertEquals("COMPLETED", planStorage.load(userId).getPhases().get(0).getStatus());
        assertEquals("IN_PROGRESS", planStorage.load(userId).getPhases().get(1).getStatus());
    }

    private CareerDailyPlanApplicationService service(InMemoryCareerPlanStorage planStorage,
                                                      InMemoryCareerDailyTaskStorage taskStorage,
                                                      String instant) {
        return new CareerDailyPlanApplicationService(planStorage, taskStorage,
                Clock.fixed(Instant.parse(instant), ZoneId.of("Asia/Shanghai")));
    }

    private CareerDailyTaskUpdateRequest update(CareerDailyTaskDto task, boolean completed) {
        CareerDailyTaskUpdateRequest request = new CareerDailyTaskUpdateRequest();
        request.setTaskId(task.getTaskId());
        request.setCompleted(Boolean.valueOf(completed));
        return request;
    }

    private CareerPlanRecordDto plan(String userId) {
        CareerPlanRecordDto plan = new CareerPlanRecordDto();
        plan.setUserId(userId);
        plan.setVersion(Integer.valueOf(3));
        plan.setTargetRole("Java 后端开发工程师");
        CareerPlanPhaseDto first = new CareerPlanPhaseDto();
        first.setPhaseId("phase-1");
        first.setTitle("工程基础与项目验证");
        first.setStatus("IN_PROGRESS");
        first.setActions(Arrays.asList("完成动作一", "完成动作二", "完成动作三", "完成动作四", "完成动作五", "完成动作六"));
        CareerPlanPhaseDto second = new CareerPlanPhaseDto();
        second.setPhaseId("phase-2");
        second.setTitle("面试与投递准备");
        second.setStatus("NOT_STARTED");
        second.setActions(Arrays.asList("准备一次模拟面试"));
        plan.setPhases(new ArrayList<CareerPlanPhaseDto>(Arrays.asList(first, second)));
        return plan;
    }
}
