package v620.cc001.cloud01.app01.mservice.storage;

import java.util.List;
import v620.cc001.base.common.dto.career.AdminContentItemDto;
import v620.cc001.base.common.dto.furtherstudy.StudyCenterSelectionDto;
import v620.cc001.base.common.dto.career.CareerPlanRecordDto;
import v620.cc001.base.common.dto.career.CareerDailyTaskDto;
import v620.cc001.base.common.dto.career.CareerRouteContext;
import v620.cc001.base.common.dto.furtherstudy.StudyPlanningMaterialDto;

/** 独立于就业内容的升学中心用户选择存储。 */
public interface StudyCenterStorage {
    StudyCenterSelectionDto loadSelection(String userId);
    StudyCenterSelectionDto saveSelection(StudyCenterSelectionDto selection);
    CareerPlanRecordDto loadPlan(String userId, String direction);
    void savePlan(String userId, String direction, CareerPlanRecordDto plan);
    void deletePlan(String userId, String direction);
    void deleteDailyTasks(String userId, String direction);
    List<CareerDailyTaskDto> listDailyTasks(String userId, String direction);
    CareerDailyTaskDto findDailyTask(String userId, String direction, String taskId);
    void saveDailyTask(String userId, String direction, CareerDailyTaskDto task);
    StudyPlanningMaterialDto saveMaterial(String userId, StudyPlanningMaterialDto material);
    StudyPlanningMaterialDto findMaterial(String userId, String direction, String materialId);
    List<StudyPlanningMaterialDto> listMaterials(String userId, String direction);
    boolean deleteMaterial(String userId, String direction, String materialId);
    List<AdminContentItemDto> listResources();
    AdminContentItemDto findResource(String resourceId);
    AdminContentItemDto saveResource(AdminContentItemDto resource);
    boolean deleteResource(String resourceId);

    default String selectedDirection(String userId) {
        StudyCenterSelectionDto selection = loadSelection(userId);
        return selection != null && CareerRouteContext.isStudyDirection(selection.getDirection())
                ? selection.getDirection() : CareerRouteContext.POSTGRADUATE;
    }
    default CareerPlanRecordDto loadPlan(String userId) { return loadPlan(userId, selectedDirection(userId)); }
    default void savePlan(String userId, CareerPlanRecordDto plan) {
        String direction = plan != null && CareerRouteContext.isStudyDirection(plan.getStudyDirection())
                ? plan.getStudyDirection() : selectedDirection(userId);
        savePlan(userId, direction, plan);
    }
    default void deletePlan(String userId) { deletePlan(userId, selectedDirection(userId)); }
    default void deleteDailyTasks(String userId) { deleteDailyTasks(userId, selectedDirection(userId)); }
    default List<CareerDailyTaskDto> listDailyTasks(String userId) { return listDailyTasks(userId, selectedDirection(userId)); }
    default CareerDailyTaskDto findDailyTask(String userId, String taskId) { return findDailyTask(userId, selectedDirection(userId), taskId); }
    default void saveDailyTask(String userId, CareerDailyTaskDto task) { saveDailyTask(userId, selectedDirection(userId), task); }
    default StudyPlanningMaterialDto findMaterial(String userId, String materialId) { return findMaterial(userId, selectedDirection(userId), materialId); }
    default boolean deleteMaterial(String userId, String materialId) { return deleteMaterial(userId, selectedDirection(userId), materialId); }
}
