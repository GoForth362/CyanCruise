package v620.cc001.cloud01.app01.mservice.storage.impl;

import v620.cc001.base.common.dto.career.AdminContentItemDto;
import v620.cc001.base.common.dto.career.CareerDailyTaskDto;
import v620.cc001.base.common.dto.career.CareerPlanRecordDto;
import v620.cc001.base.common.dto.furtherstudy.StudyCenterSelectionDto;
import v620.cc001.base.common.dto.furtherstudy.StudyPlanningMaterialDto;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyAnalysisDraftDto;
import v620.cc001.cloud01.app01.mservice.storage.StudyCenterStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory storage without pre-published resources or generated business records. */
public class InMemoryStudyCenterStorage implements StudyCenterStorage {
    private volatile boolean publishedResourceCatalogInitialized;
    private final Map<String, StudyCenterSelectionDto> selections = new ConcurrentHashMap<String, StudyCenterSelectionDto>();
    private final Map<String, AdminContentItemDto> resources = new ConcurrentHashMap<String, AdminContentItemDto>();
    private final Map<String, CareerPlanRecordDto> plans = new ConcurrentHashMap<String, CareerPlanRecordDto>();
    private final Map<String, Map<String, CareerDailyTaskDto>> dailyTasks = new ConcurrentHashMap<String, Map<String, CareerDailyTaskDto>>();
    private final Map<String, StudyPlanningMaterialDto> materials = new ConcurrentHashMap<String, StudyPlanningMaterialDto>();
    private final Map<String, FurtherStudyAnalysisDraftDto> analysisDrafts = new ConcurrentHashMap<String, FurtherStudyAnalysisDraftDto>();

    public StudyCenterSelectionDto loadSelection(String userId) { return copy(selections.get(userId)); }

    public StudyCenterSelectionDto saveSelection(StudyCenterSelectionDto selection) {
        StudyCenterSelectionDto saved = copy(selection);
        selections.put(saved.getUserId(), saved);
        return copy(saved);
    }

    public CareerPlanRecordDto loadPlan(String userId, String direction) { return plans.get(routeKey(userId, direction)); }
    public void savePlan(String userId, String direction, CareerPlanRecordDto plan) {
        String key = routeKey(userId, direction);
        if (plan == null) plans.remove(key); else plans.put(key, plan);
    }
    public void deletePlan(String userId, String direction) { plans.remove(routeKey(userId, direction)); }
    public void deleteDailyTasks(String userId, String direction) { dailyTasks.remove(routeKey(userId, direction)); }

    public List<CareerDailyTaskDto> listDailyTasks(String userId, String direction) {
        Map<String, CareerDailyTaskDto> values = dailyTasks.get(routeKey(userId, direction));
        return values == null ? new ArrayList<CareerDailyTaskDto>() : new ArrayList<CareerDailyTaskDto>(values.values());
    }

    public CareerDailyTaskDto findDailyTask(String userId, String direction, String taskId) {
        Map<String, CareerDailyTaskDto> values = dailyTasks.get(routeKey(userId, direction));
        return values == null ? null : values.get(taskId);
    }

    public void saveDailyTask(String userId, String direction, CareerDailyTaskDto task) {
        String key = routeKey(userId, direction);
        Map<String, CareerDailyTaskDto> values = dailyTasks.get(key);
        if (values == null) {
            values = new ConcurrentHashMap<String, CareerDailyTaskDto>();
            dailyTasks.put(key, values);
        }
        values.put(task.getTaskId(), task);
    }

    public StudyPlanningMaterialDto saveMaterial(String userId, StudyPlanningMaterialDto material) {
        StudyPlanningMaterialDto saved = copy(material);
        saved.setUserId(userId);
        materials.put(saved.getMaterialId(), saved);
        return copy(saved);
    }

    public StudyPlanningMaterialDto findMaterial(String userId, String direction, String materialId) {
        StudyPlanningMaterialDto material = materials.get(materialId);
        return material == null || !userId.equals(material.getUserId()) || !direction.equals(material.getDirection())
                ? null : copy(material);
    }

    public List<StudyPlanningMaterialDto> listMaterials(String userId, String direction) {
        List<StudyPlanningMaterialDto> out = new ArrayList<StudyPlanningMaterialDto>();
        for (StudyPlanningMaterialDto material : materials.values()) {
            if (!userId.equals(material.getUserId())) continue;
            if (direction != null && direction.trim().length() > 0
                    && !direction.trim().equals(material.getDirection())) continue;
            out.add(copy(material));
        }
        return out;
    }

    public boolean deleteMaterial(String userId, String direction, String materialId) {
        StudyPlanningMaterialDto material = materials.get(materialId);
        return material != null && userId.equals(material.getUserId()) && direction.equals(material.getDirection())
                && materials.remove(materialId, material);
    }

    public FurtherStudyAnalysisDraftDto loadAnalysisDraft(String userId, String taskType) {
        return copy(analysisDrafts.get(routeKey(userId, taskType)));
    }

    public FurtherStudyAnalysisDraftDto saveAnalysisDraft(String userId, FurtherStudyAnalysisDraftDto draft) {
        FurtherStudyAnalysisDraftDto saved = copy(draft);
        analysisDrafts.put(routeKey(userId, saved.getTaskType()), saved);
        return copy(saved);
    }

    public List<AdminContentItemDto> listResources() {
        List<AdminContentItemDto> out = new ArrayList<AdminContentItemDto>();
        for (AdminContentItemDto item : resources.values()) out.add(copy(item));
        return out;
    }
    public AdminContentItemDto findResource(String resourceId) { return copy(resources.get(resourceId)); }
    public AdminContentItemDto saveResource(AdminContentItemDto resource) {
        AdminContentItemDto saved = copy(resource);
        resources.put(saved.getContentId(), saved);
        return copy(saved);
    }
    public boolean deleteResource(String resourceId) { return resources.remove(resourceId) != null; }
    public boolean isPublishedResourceCatalogInitialized() { return publishedResourceCatalogInitialized; }
    public void markPublishedResourceCatalogInitialized() { publishedResourceCatalogInitialized = true; }

    private String routeKey(String userId, String direction) { return userId + "\u0000" + direction; }

    private StudyCenterSelectionDto copy(StudyCenterSelectionDto value) {
        if (value == null) return null;
        StudyCenterSelectionDto copy = new StudyCenterSelectionDto();
        copy.setUserId(value.getUserId());
        copy.setDirection(value.getDirection());
        copy.setTargetSchool(value.getTargetSchool());
        copy.setUpdatedAt(value.getUpdatedAt());
        return copy;
    }

    private StudyPlanningMaterialDto copy(StudyPlanningMaterialDto value) {
        if (value == null) return null;
        StudyPlanningMaterialDto copy = new StudyPlanningMaterialDto();
        copy.setMaterialId(value.getMaterialId()); copy.setUserId(value.getUserId());
        copy.setDirection(value.getDirection()); copy.setMaterialType(value.getMaterialType());
        copy.setTitle(value.getTitle()); copy.setOriginalFilename(value.getOriginalFilename());
        copy.setObjectKey(value.getObjectKey()); copy.setMediaType(value.getMediaType());
        copy.setSizeBytes(value.getSizeBytes()); copy.setExtractionStatus(value.getExtractionStatus());
        copy.setExtractionMessage(value.getExtractionMessage()); copy.setExtractedText(value.getExtractedText());
        copy.setExtractedCharCount(value.getExtractedCharCount()); copy.setTruncated(value.getTruncated());
        copy.setCreatedAt(value.getCreatedAt()); copy.setUpdatedAt(value.getUpdatedAt());
        return copy;
    }

    private FurtherStudyAnalysisDraftDto copy(FurtherStudyAnalysisDraftDto value) {
        if (value == null) return null;
        FurtherStudyAnalysisDraftDto copy = new FurtherStudyAnalysisDraftDto();
        copy.setTaskType(value.getTaskType());
        copy.setPayloadJson(value.getPayloadJson());
        copy.setUpdatedAt(value.getUpdatedAt());
        return copy;
    }

    private AdminContentItemDto copy(AdminContentItemDto value) {
        if (value == null) return null;
        AdminContentItemDto copy = new AdminContentItemDto();
        copy.setContentId(value.getContentId()); copy.setType(value.getType());
        copy.setTitle(value.getTitle()); copy.setSummary(value.getSummary());
        copy.setCategory(value.getCategory()); copy.setSourceUrl(value.getSourceUrl());
        copy.setImageUrl(value.getImageUrl()); copy.setPinned(value.getPinned());
        copy.setHidden(value.getHidden()); copy.setPublishedAt(value.getPublishedAt());
        return copy;
    }
}
