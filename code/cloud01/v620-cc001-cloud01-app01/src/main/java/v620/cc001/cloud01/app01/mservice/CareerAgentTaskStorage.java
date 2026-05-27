package v620.cc001.cloud01.app01.mservice;

import v620.cc001.base.common.dto.career.CareerAgentTodayDto;

import java.time.LocalDate;
import java.util.List;

public interface CareerAgentTaskStorage {

    void saveTodayActions(String userId, LocalDate dueDate, List<CareerAgentTodayDto.Action> actions);

    List<CareerAgentTodayDto.Action> listTodayActions(String userId, LocalDate dueDate);
}
