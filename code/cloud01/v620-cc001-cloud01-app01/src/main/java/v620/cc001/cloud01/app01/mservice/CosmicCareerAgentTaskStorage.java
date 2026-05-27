package v620.cc001.cloud01.app01.mservice;

import v620.cc001.base.common.dto.career.CareerAgentTodayDto;
import v620.cc001.cloud01.app01.mservice.datamodel.CareerLoopDatamodelObjects;
import v620.cc001.cloud01.app01.mservice.datamodel.CosmicDatamodelGateway;
import v620.cc001.cloud01.app01.mservice.datamodel.CosmicDatamodelRecord;
import v620.cc001.cloud01.app01.mservice.datamodel.CosmicRecordFilter;
import v620.cc001.cloud01.app01.mservice.datamodel.DatamodelFieldMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CosmicCareerAgentTaskStorage implements CareerAgentTaskStorage {

    private final CosmicDatamodelGateway gateway;

    public CosmicCareerAgentTaskStorage(CosmicDatamodelGateway gateway) {
        this.gateway = gateway;
    }

    public void saveTodayActions(final String userId, final LocalDate dueDate, List<CareerAgentTodayDto.Action> actions) {
        gateway.deleteWhere(CareerLoopDatamodelObjects.TASK, sameUserAndDate(userId, dueDate));
        if (actions == null) {
            return;
        }
        int index = 0;
        for (CareerAgentTodayDto.Action action : actions) {
            gateway.save(new CosmicDatamodelRecord(CareerLoopDatamodelObjects.TASK)
                    .set(CareerLoopDatamodelObjects.USER_ID, userId)
                    .set("task_key", action.getLabelKey())
                    .set("title", action.getLabel())
                    .set("task_type", action.getType())
                    .set("priority", action.getPriority())
                    .set("target", action.getTarget())
                    .set("source", action.getSource())
                    .set("status", "TODO")
                    .set("due_date", dueDate)
                    .set("sub_index", Integer.valueOf(index++))
                    .set(CareerLoopDatamodelObjects.CREATED_AT, LocalDateTime.now())
                    .set(CareerLoopDatamodelObjects.UPDATED_AT, LocalDateTime.now()));
        }
    }

    public List<CareerAgentTodayDto.Action> listTodayActions(final String userId, final LocalDate dueDate) {
        List<CosmicDatamodelRecord> rows = gateway.list(CareerLoopDatamodelObjects.TASK, sameUserAndDate(userId, dueDate),
                new Comparator<CosmicDatamodelRecord>() {
                    public int compare(CosmicDatamodelRecord left, CosmicDatamodelRecord right) {
                        Integer leftIndex = DatamodelFieldMapper.asInteger(left.get("sub_index"));
                        Integer rightIndex = DatamodelFieldMapper.asInteger(right.get("sub_index"));
                        if (leftIndex == null && rightIndex == null) {
                            return 0;
                        }
                        if (leftIndex == null) {
                            return -1;
                        }
                        if (rightIndex == null) {
                            return 1;
                        }
                        return leftIndex.compareTo(rightIndex);
                    }
                });
        List<CareerAgentTodayDto.Action> result = new ArrayList<CareerAgentTodayDto.Action>();
        for (CosmicDatamodelRecord row : rows) {
            CareerAgentTodayDto.Action action = new CareerAgentTodayDto.Action();
            action.setLabel(DatamodelFieldMapper.asString(row.get("title")));
            action.setLabelKey(DatamodelFieldMapper.asString(row.get("task_key")));
            action.setType(DatamodelFieldMapper.asString(row.get("task_type")));
            action.setPriority(DatamodelFieldMapper.asString(row.get("priority")));
            action.setTarget(DatamodelFieldMapper.asString(row.get("target")));
            action.setSource(DatamodelFieldMapper.asString(row.get("source")));
            result.add(action);
        }
        return result;
    }

    private CosmicRecordFilter sameUserAndDate(final String userId, final LocalDate dueDate) {
        return new CosmicRecordFilter() {
            public boolean matches(CosmicDatamodelRecord record) {
                return DatamodelFieldMapper.same(userId, record.get(CareerLoopDatamodelObjects.USER_ID))
                        && DatamodelFieldMapper.same(dueDate, record.get("due_date"));
            }
        };
    }
}
