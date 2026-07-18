package v620.cc001.cloud01.app01.mservice.storage;

import java.util.List;
import v620.cc001.base.common.dto.career.AdminContentItemDto;
import v620.cc001.base.common.dto.furtherstudy.StudyCenterSelectionDto;
import v620.cc001.base.common.dto.career.CareerPlanRecordDto;
import v620.cc001.base.common.dto.career.CareerDailyTaskDto;
import v620.cc001.base.common.dto.furtherstudy.StudyPlanningMaterialDto;

/** 独立于就业内容的升学中心用户选择存储。 */
public interface StudyCenterStorage {
    StudyCenterSelectionDto loadSelection(String userId);
    StudyCenterSelectionDto saveSelection(StudyCenterSelectionDto selection);
    CareerPlanRecordDto loadPlan(String userId);
    void savePlan(String userId, CareerPlanRecordDto plan);
    void deletePlan(String userId);
    void deleteDailyTasks(String userId);
    List<CareerDailyTaskDto> listDailyTasks(String userId);
    CareerDailyTaskDto findDailyTask(String userId, String taskId);
    void saveDailyTask(String userId, CareerDailyTaskDto task);
    StudyPlanningMaterialDto saveMaterial(String userId, StudyPlanningMaterialDto material);
    StudyPlanningMaterialDto findMaterial(String userId, String materialId);
    List<StudyPlanningMaterialDto> listMaterials(String userId, String direction);
    boolean deleteMaterial(String userId, String materialId);
    List<AdminContentItemDto> listResources();
    AdminContentItemDto findResource(String resourceId);
    AdminContentItemDto saveResource(AdminContentItemDto resource);
    boolean deleteResource(String resourceId);
}
