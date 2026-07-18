package v620.cc001.cloud01.app01.mservice.storage.impl;

import v620.cc001.base.common.dto.furtherstudy.StudyCenterSelectionDto;
import v620.cc001.base.common.dto.career.AdminContentItemDto;
import v620.cc001.base.common.dto.career.CareerPlanRecordDto;
import v620.cc001.base.common.dto.career.CareerDailyTaskDto;
import v620.cc001.base.common.dto.furtherstudy.StudyPlanningMaterialDto;
import v620.cc001.cloud01.app01.mservice.storage.StudyCenterStorage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;

public class InMemoryStudyCenterStorage implements StudyCenterStorage {
    private final Map<String, StudyCenterSelectionDto> selections = new ConcurrentHashMap<String, StudyCenterSelectionDto>();
    private final Map<String, AdminContentItemDto> resources = new ConcurrentHashMap<String, AdminContentItemDto>();
    private final Map<String, CareerPlanRecordDto> plans = new ConcurrentHashMap<String, CareerPlanRecordDto>();
    private final Map<String, Map<String, CareerDailyTaskDto>> dailyTasks = new ConcurrentHashMap<String, Map<String, CareerDailyTaskDto>>();
    private final Map<String, StudyPlanningMaterialDto> materials = new ConcurrentHashMap<String, StudyPlanningMaterialDto>();
    public InMemoryStudyCenterStorage() {
        seed("study-service-chsi", "CONSULTATION", "中国研究生招生信息网", "查询硕士研究生招生、考试和调剂信息。", "官方服务", "https://yz.chsi.com.cn/", true);
        seed("study-service-chsi-account", "CONSULTATION", "中国高等教育学生信息网", "查询学籍学历、成绩与教育背景核验相关服务。", "官方服务", "https://www.chsi.com.cn/", false);
        seed("study-service-overseas", "CONSULTATION", "教育部留学服务中心", "了解国（境）外学历学位认证、留学档案与回国服务。", "官方服务", "https://www.cscse.edu.cn/", false);
        seed("study-article-direction", "ARTICLE", "如何选择升学方向", "从发展目标、成绩基础、时间安排和家庭预算判断考研、保研或留学。", "升学规划", "https://yz.chsi.com.cn/", true);
        seed("study-article-school", "ARTICLE", "考研择校的四个判断步骤", "依次比较专业方向、院校层次、地区机会和近年招生要求，形成冲刺与稳妥选择。", "院校选择", "https://yz.chsi.com.cn/", false);
        seed("study-article-reexam", "ARTICLE", "复试准备清单", "围绕专业基础、个人介绍、项目经历、英语表达和材料核对安排复试准备。", "复试准备", "https://yz.chsi.com.cn/", false);
        seed("study-article-recommendation", "ARTICLE", "保研申请时间线", "梳理成绩排名、科研竞赛、夏令营、预推免和志愿填报等关键节点。", "保研申请", "https://yz.chsi.com.cn/tm/", false);
        seed("study-article-abroad", "ARTICLE", "留学申请时间线", "从选校定位、语言考试、文书准备、网申递交到签证办理安排申请节奏。", "留学申请", "https://www.csc.edu.cn/", false);
        seed("study-article-material", "ARTICLE", "升学材料准备清单", "梳理成绩证明、个人陈述、推荐材料、获奖经历和关键提交时间。", "申请材料", "https://www.chsi.com.cn/", false);
        seed("study-video-preparation", "VIDEO", "研究生备考的阶段安排", "了解从确定方向、基础复习到冲刺和复试准备的常见节奏。", "备考方法", "https://yz.chsi.com.cn/", true);
        seed("study-video-reexam", "VIDEO", "复试表达与材料复盘", "用常见问题检查个人介绍、专业回答和项目经历的表达是否清楚。", "复试准备", "https://yz.chsi.com.cn/", false);
        seed("study-video-overseas", "VIDEO", "留学申请准备公开课", "了解选校、语言成绩、申请材料和学历认证等常见准备事项。", "留学申请", "https://www.cscse.edu.cn/", false);
    }
    public StudyCenterSelectionDto loadSelection(String userId) { return copy(selections.get(userId)); }
    public StudyCenterSelectionDto saveSelection(StudyCenterSelectionDto selection) {
        StudyCenterSelectionDto saved = copy(selection);
        selections.put(saved.getUserId(), saved);
        return copy(saved);
    }
    public CareerPlanRecordDto loadPlan(String userId) { return plans.get(userId); }
    public void savePlan(String userId, CareerPlanRecordDto plan) { if (plan == null) plans.remove(userId); else plans.put(userId, plan); }
    public void deletePlan(String userId) { plans.remove(userId); }
    public void deleteDailyTasks(String userId) { dailyTasks.remove(userId); }
    public List<CareerDailyTaskDto> listDailyTasks(String userId) {
        Map<String, CareerDailyTaskDto> values = dailyTasks.get(userId);
        return values == null ? new ArrayList<CareerDailyTaskDto>() : new ArrayList<CareerDailyTaskDto>(values.values());
    }
    public CareerDailyTaskDto findDailyTask(String userId, String taskId) {
        Map<String, CareerDailyTaskDto> values = dailyTasks.get(userId);
        return values == null ? null : values.get(taskId);
    }
    public void saveDailyTask(String userId, CareerDailyTaskDto task) {
        Map<String, CareerDailyTaskDto> values = dailyTasks.get(userId);
        if (values == null) { values = new ConcurrentHashMap<String, CareerDailyTaskDto>(); dailyTasks.put(userId, values); }
        values.put(task.getTaskId(), task);
    }
    public StudyPlanningMaterialDto saveMaterial(String userId, StudyPlanningMaterialDto material) {
        StudyPlanningMaterialDto saved = copy(material);
        saved.setUserId(userId);
        materials.put(saved.getMaterialId(), saved);
        return copy(saved);
    }
    public StudyPlanningMaterialDto findMaterial(String userId, String materialId) {
        StudyPlanningMaterialDto material = materials.get(materialId);
        return material == null || !userId.equals(material.getUserId()) ? null : copy(material);
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
    public boolean deleteMaterial(String userId, String materialId) {
        StudyPlanningMaterialDto material = materials.get(materialId);
        return material != null && userId.equals(material.getUserId())
                && materials.remove(materialId, material);
    }
    public List<AdminContentItemDto> listResources() { List<AdminContentItemDto> out = new ArrayList<AdminContentItemDto>(); for (AdminContentItemDto item : resources.values()) out.add(copy(item)); return out; }
    public AdminContentItemDto findResource(String resourceId) { return copy(resources.get(resourceId)); }
    public AdminContentItemDto saveResource(AdminContentItemDto resource) { AdminContentItemDto saved = copy(resource); resources.put(saved.getContentId(), saved); return copy(saved); }
    public boolean deleteResource(String resourceId) { return resources.remove(resourceId) != null; }
    private StudyCenterSelectionDto copy(StudyCenterSelectionDto value) {
        if (value == null) return null;
        StudyCenterSelectionDto copy = new StudyCenterSelectionDto();
        copy.setUserId(value.getUserId()); copy.setDirection(value.getDirection());
        copy.setTargetSchool(value.getTargetSchool()); copy.setUpdatedAt(value.getUpdatedAt());
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
    private void seed(String id, String type, String title, String summary, String category, String sourceUrl, boolean pinned) { AdminContentItemDto item = new AdminContentItemDto(); item.setContentId(id); item.setType(type); item.setTitle(title); item.setSummary(summary); item.setCategory(category); item.setSourceUrl(sourceUrl); item.setPinned(Boolean.valueOf(pinned)); item.setHidden(Boolean.FALSE); item.setPublishedAt(LocalDateTime.now()); resources.put(id, item); }
    private AdminContentItemDto copy(AdminContentItemDto value) { if (value == null) return null; AdminContentItemDto copy = new AdminContentItemDto(); copy.setContentId(value.getContentId()); copy.setType(value.getType()); copy.setTitle(value.getTitle()); copy.setSummary(value.getSummary()); copy.setCategory(value.getCategory()); copy.setSourceUrl(value.getSourceUrl()); copy.setImageUrl(value.getImageUrl()); copy.setPinned(value.getPinned()); copy.setHidden(value.getHidden()); copy.setPublishedAt(value.getPublishedAt()); return copy; }
}
