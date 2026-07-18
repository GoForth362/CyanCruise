package v620.cc001.cloud01.app01.mservice.storage.impl;

import v620.cc001.base.common.dto.career.CareerDailyTaskDto;
import v620.cc001.cloud01.app01.mservice.datamodel.CyanCruiseDatamodelObjects;
import v620.cc001.cloud01.app01.mservice.datamodel.CosmicDatamodelGateway;
import v620.cc001.cloud01.app01.mservice.datamodel.CosmicDatamodelRecord;
import v620.cc001.cloud01.app01.mservice.datamodel.CosmicRecordFilter;
import v620.cc001.cloud01.app01.mservice.datamodel.DatamodelFieldMapper;
import v620.cc001.cloud01.app01.mservice.storage.CareerDailyTaskStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CosmicCareerDailyTaskStorage implements CareerDailyTaskStorage {

    private final CosmicDatamodelGateway gateway;

    public CosmicCareerDailyTaskStorage(CosmicDatamodelGateway gateway) {
        this.gateway = gateway;
    }

    public List<CareerDailyTaskDto> list(final String userId) {
        List<CosmicDatamodelRecord> records = gateway.list(CyanCruiseDatamodelObjects.TASK,
                new CosmicRecordFilter() {
                    public boolean matches(CosmicDatamodelRecord record) {
                        return DatamodelFieldMapper.same(userId, record.get(CyanCruiseDatamodelObjects.USER_ID));
                    }
                }, new Comparator<CosmicDatamodelRecord>() {
                    public int compare(CosmicDatamodelRecord first, CosmicDatamodelRecord second) {
                        return String.valueOf(first.get("task_id")).compareTo(String.valueOf(second.get("task_id")));
                    }
                });
        List<CareerDailyTaskDto> result = new ArrayList<CareerDailyTaskDto>();
        for (CosmicDatamodelRecord record : records) result.add(fromRecord(record));
        return result;
    }

    public CareerDailyTaskDto find(final String userId, final String taskId) {
        CosmicDatamodelRecord record = gateway.findOne(CyanCruiseDatamodelObjects.TASK,
                new CosmicRecordFilter() {
                    public boolean matches(CosmicDatamodelRecord value) {
                        return DatamodelFieldMapper.same(userId, value.get(CyanCruiseDatamodelObjects.USER_ID))
                                && DatamodelFieldMapper.same(taskId, value.get("task_id"));
                    }
                });
        return record == null ? null : fromRecord(record);
    }

    public void save(String userId, CareerDailyTaskDto task) {
        CosmicDatamodelRecord record = findRecord(userId, task.getTaskId());
        if (record == null) {
            record = new CosmicDatamodelRecord(CyanCruiseDatamodelObjects.TASK)
                    .set(CyanCruiseDatamodelObjects.USER_ID, userId)
                    .set("task_id", task.getTaskId());
        }
        gateway.save(record.set("task_key", task.getSourceTaskId())
                .set("title", task.getText())
                .set("description", task.getText())
                .set("due_date", task.getPlanDate())
                .set("status", task.getStatus())
                .set("priority", task.getSequence())
                .set("parent_task_id", task.getPhaseId())
                .set("sub_index", task.getSequence())
                .set(CyanCruiseDatamodelObjects.UPDATED_AT, LocalDateTime.now()));
    }

    private CosmicDatamodelRecord findRecord(final String userId, final String taskId) {
        return gateway.findOne(CyanCruiseDatamodelObjects.TASK, new CosmicRecordFilter() {
            public boolean matches(CosmicDatamodelRecord value) {
                return DatamodelFieldMapper.same(userId, value.get(CyanCruiseDatamodelObjects.USER_ID))
                        && DatamodelFieldMapper.same(taskId, value.get("task_id"));
            }
        });
    }

    private CareerDailyTaskDto fromRecord(CosmicDatamodelRecord record) {
        CareerDailyTaskDto task = new CareerDailyTaskDto();
        task.setTaskId(string(record.get("task_id")));
        task.setSourceTaskId(string(record.get("task_key")));
        task.setText(string(record.get("title")));
        task.setPlanDate((java.time.LocalDate) record.get("due_date"));
        task.setStatus(string(record.get("status")));
        task.setSequence(integer(record.get("sub_index")));
        task.setPhaseId(string(record.get("parent_task_id")));
        task.setPlanVersion(parsePlanVersion(task.getTaskId()));
        return task;
    }

    private String string(Object value) { return value == null ? null : String.valueOf(value); }
    private Integer integer(Object value) { return value instanceof Number ? Integer.valueOf(((Number) value).intValue()) : null; }
    private Integer parsePlanVersion(String taskId) {
        if (taskId == null || !taskId.startsWith("daily-v")) return Integer.valueOf(1);
        int end = taskId.indexOf('-', 7);
        try { return Integer.valueOf(taskId.substring(7, end)); } catch (RuntimeException ignored) { return Integer.valueOf(1); }
    }
}
