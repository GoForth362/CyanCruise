package v620.cc001.cloud01.app01.mservice.storage.impl;

import v620.cc001.base.common.dto.career.CareerDailyTaskDto;
import v620.cc001.cloud01.app01.mservice.storage.CareerDailyTaskStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryCareerDailyTaskStorage implements CareerDailyTaskStorage {

    private final Map<String, Map<String, CareerDailyTaskDto>> records =
            new ConcurrentHashMap<String, Map<String, CareerDailyTaskDto>>();

    public List<CareerDailyTaskDto> list(String userId) {
        Map<String, CareerDailyTaskDto> userTasks = records.get(userId);
        List<CareerDailyTaskDto> result = userTasks == null
                ? new ArrayList<CareerDailyTaskDto>()
                : new ArrayList<CareerDailyTaskDto>(userTasks.values());
        Collections.sort(result, new Comparator<CareerDailyTaskDto>() {
            public int compare(CareerDailyTaskDto first, CareerDailyTaskDto second) {
                int date = compareText(String.valueOf(first.getPlanDate()), String.valueOf(second.getPlanDate()));
                if (date != 0) return date;
                return compareInteger(first.getSequence(), second.getSequence());
            }
        });
        return result;
    }

    public CareerDailyTaskDto find(String userId, String taskId) {
        Map<String, CareerDailyTaskDto> userTasks = records.get(userId);
        return userTasks == null ? null : userTasks.get(taskId);
    }

    public void save(String userId, CareerDailyTaskDto task) {
        Map<String, CareerDailyTaskDto> userTasks = records.get(userId);
        if (userTasks == null) {
            userTasks = new ConcurrentHashMap<String, CareerDailyTaskDto>();
            records.put(userId, userTasks);
        }
        userTasks.put(task.getTaskId(), task);
    }

    private static int compareText(String first, String second) {
        return first == null ? (second == null ? 0 : -1) : first.compareTo(second);
    }

    private static int compareInteger(Integer first, Integer second) {
        int left = first == null ? 0 : first.intValue();
        int right = second == null ? 0 : second.intValue();
        return left < right ? -1 : left == right ? 0 : 1;
    }
}
