package v620.cc001.cloud01.app01.mservice.application;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.UUID;

import v620.cc001.base.common.dto.career.AdminContentItemDto;
import v620.cc001.base.common.dto.career.CareerResourceCardDto;
import v620.cc001.base.common.dto.career.CareerResourceFeedDto;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;
import v620.cc001.base.common.dto.furtherstudy.StudyCenterInsightDto;
import v620.cc001.base.common.dto.furtherstudy.StudyCenterSelectionDto;
import v620.cc001.cloud01.app01.mservice.storage.CyanCruiseStorageFactory;
import v620.cc001.cloud01.app01.mservice.storage.StudyCenterStorage;

/** Application service for the isolated study center. */
public class StudyCenterApplicationService {
    private final StudyCenterStorage storage;
    private final CareerProfileApplicationService profileService;
    private final AdminConsoleGovernanceApplicationService adminService;

    public StudyCenterApplicationService() {
        this(CyanCruiseStorageFactory.studyCenterStorage(), new CareerProfileApplicationService(), new AdminConsoleGovernanceApplicationService());
    }

    public StudyCenterApplicationService(StudyCenterStorage storage, CareerProfileApplicationService profileService) {
        this(storage, profileService, new AdminConsoleGovernanceApplicationService());
    }

    public StudyCenterApplicationService(StudyCenterStorage storage, CareerProfileApplicationService profileService, AdminConsoleGovernanceApplicationService adminService) {
        this.storage = storage;
        this.profileService = profileService;
        this.adminService = adminService;
    }

    public StudyCenterSelectionDto getSelection(String userId) {
        String safeUserId = requireUserId(userId);
        StudyCenterSelectionDto selection = storage.loadSelection(safeUserId);
        if (selection != null) {
            selection.setTargetSchool(profileTargetSchool(safeUserId));
            return selection;
        }
        StudyCenterSelectionDto empty = new StudyCenterSelectionDto();
        empty.setUserId(safeUserId);
        return empty;
    }

    public StudyCenterSelectionDto saveSelection(String userId, String direction, String targetSchool) {
        String safeUserId = requireUserId(userId);
        String safeDirection = requireDirection(direction);
        StudyCenterSelectionDto value = new StudyCenterSelectionDto();
        value.setUserId(safeUserId);
        value.setDirection(safeDirection);
        value.setTargetSchool(profileTargetSchool(safeUserId));
        value.setUpdatedAt(LocalDateTime.now());
        return storage.saveSelection(value);
    }

    public StudyCenterInsightDto getInsight(String userId) {
        String safeUserId = requireUserId(userId);
        StudyCenterSelectionDto selection = getSelection(safeUserId);
        UserProfileSnapshot snapshot = profileService.getSnapshot(safeUserId);
        UserProfileSnapshot.OnboardingBlock onboarding = snapshot == null ? null : snapshot.getOnboarding();
        UserProfileSnapshot.EducationBlock education = onboarding == null ? null : onboarding.getEducation();
        String school = education == null ? null : education.getSchool();
        String major = education == null ? null : education.getMajor();
        String direction = selection.getDirection();
        String targetSchool = profileTargetSchool(safeUserId);

        StudyCenterInsightDto insight = new StudyCenterInsightDto();
        insight.setDirection(direction);
        insight.setDirectionLabel(directionLabel(direction));
        insight.setSchool(school);
        insight.setMajor(major);
        insight.setTargetSchool(targetSchool);
        insight.setSourceCount(Integer.valueOf(resources().getArticles().size() + resources().getVideos().size()
                + resources().getConsultations().size()));
        insight.setUpdatedAt(selection.getUpdatedAt());
        List<String> focusItems = new ArrayList<String>();
        if (!hasText(direction)) {
            insight.setStatus("PENDING_SELECTION");
            insight.setSummary("先选择考研、保研或留学方向，系统才能给出对应的准备建议。");
            focusItems.add("选择一条主要升学方向");
        } else {
            insight.setStatus("READY");
            insight.setSummary("已选择" + directionLabel(direction) + "，可围绕当前条件安排下一步准备。");
            if ("POSTGRADUATE".equals(direction)) {
                focusItems.add("确定报考专业和目标院校范围");
                focusItems.add("制定复习科目与阶段计划");
            } else if ("RECOMMENDATION".equals(direction)) {
                focusItems.add("核对成绩、排名和推免资格");
                focusItems.add("整理科研、竞赛和申请材料");
            } else {
                focusItems.add("确定国家或地区与目标院校范围");
                focusItems.add("安排语言考试和申请时间表");
            }
        }
        if (!hasText(school)) focusItems.add("在自画像中补充学校信息");
        if (!hasText(major)) focusItems.add("在自画像中补充专业信息");
        if (!hasText(targetSchool)) focusItems.add("请先在升学自画像中填写目标院校，方便缩小准备范围");
        insight.setFocusItems(focusItems);
        return insight;
    }

    public CareerResourceFeedDto resources() {
        CareerResourceFeedDto feed = new CareerResourceFeedDto();
        feed.setStatus("READY");
        feed.setMessage("升学资讯仅由升学中心维护，不与就业资讯混用。");
        feed.setUpdatedAt(LocalDateTime.now());
        List<CareerResourceCardDto> articles = new ArrayList<CareerResourceCardDto>();
        List<CareerResourceCardDto> videos = new ArrayList<CareerResourceCardDto>();
        List<CareerResourceCardDto> services = new ArrayList<CareerResourceCardDto>();
        List<AdminContentItemDto> items = storage.listResources();
        if (shouldSeedDefaultResources(items)) { seedDefaultResources(); items = storage.listResources(); }
        for (AdminContentItemDto item : items) {
            if (item == null || Boolean.TRUE.equals(item.getHidden())) continue;
            CareerResourceCardDto card = resource(item);
            if ("VIDEO".equals(item.getType())) videos.add(card);
            else if ("CONSULTATION".equals(item.getType())) services.add(card);
            else articles.add(card);
        }
        feed.setArticles(articles);
        feed.setVideos(videos);
        feed.setConsultations(services);
        return feed;
    }

    public List<AdminContentItemDto> listResourcesForAdmin(String adminId) { adminService.requireAdministrator(adminId); List<AdminContentItemDto> items = storage.listResources(); if (shouldSeedDefaultResources(items)) { seedDefaultResources(); items = storage.listResources(); } return items; }
    public AdminContentItemDto saveResourceForAdmin(String adminId, AdminContentItemDto resource) {
        adminService.requireAdministrator(adminId);
        if (resource == null || !hasText(resource.getTitle())) throw new IllegalArgumentException("请填写内容标题");
        String type = trimToNull(resource.getType());
        if ("RESOURCE".equals(type)) type = "CONSULTATION";
        if (!"ARTICLE".equals(type) && !"VIDEO".equals(type) && !"CONSULTATION".equals(type)) throw new IllegalArgumentException("请选择内容类型");
        resource.setType(type); resource.setContentId(hasText(resource.getContentId()) ? resource.getContentId().trim() : "study-resource-" + UUID.randomUUID().toString());
        resource.setTitle(resource.getTitle().trim()); resource.setSummary(trimToNull(resource.getSummary())); resource.setCategory(trimToNull(resource.getCategory())); resource.setSourceUrl(trimToNull(resource.getSourceUrl())); resource.setImageUrl(null);
        resource.setPinned(Boolean.valueOf(Boolean.TRUE.equals(resource.getPinned()))); resource.setHidden(Boolean.valueOf(Boolean.TRUE.equals(resource.getHidden())));
        if (resource.getPublishedAt() == null) resource.setPublishedAt(LocalDateTime.now());
        return storage.saveResource(resource);
    }
    public AdminContentItemDto toggleResourcePinned(String adminId, String resourceId) { adminService.requireAdministrator(adminId); AdminContentItemDto item = requireResource(resourceId); item.setPinned(Boolean.valueOf(!Boolean.TRUE.equals(item.getPinned()))); return storage.saveResource(item); }
    public AdminContentItemDto toggleResourceHidden(String adminId, String resourceId) { adminService.requireAdministrator(adminId); AdminContentItemDto item = requireResource(resourceId); item.setHidden(Boolean.valueOf(!Boolean.TRUE.equals(item.getHidden()))); return storage.saveResource(item); }
    public boolean deleteResource(String adminId, String resourceId) { adminService.requireAdministrator(adminId); return storage.deleteResource(requireResource(resourceId).getContentId()); }

    private void seedDefaultResources() {
        saveDefault("study-service-chsi", "CONSULTATION", "中国研究生招生信息网", "查询硕士研究生招生、考试和调剂信息。", "官方服务", "https://yz.chsi.com.cn/", true);
        saveDefault("study-service-chsi-account", "CONSULTATION", "中国高等教育学生信息网", "查询学籍学历、成绩与教育背景核验相关服务。", "官方服务", "https://www.chsi.com.cn/", false);
        saveDefault("study-service-overseas", "CONSULTATION", "教育部留学服务中心", "了解国（境）外学历学位认证、留学档案与回国服务。", "官方服务", "https://www.cscse.edu.cn/", false);
        saveDefault("study-article-direction", "ARTICLE", "如何选择升学方向", "从发展目标、成绩基础、时间安排和家庭预算判断考研、保研或留学。", "升学规划", "https://yz.chsi.com.cn/", true);
        saveDefault("study-article-school", "ARTICLE", "考研择校的四个判断步骤", "依次比较专业方向、院校层次、地区机会和近年招生要求，形成冲刺与稳妥选择。", "院校选择", "https://yz.chsi.com.cn/", false);
        saveDefault("study-article-reexam", "ARTICLE", "复试准备清单", "围绕专业基础、个人介绍、项目经历、英语表达和材料核对安排复试准备。", "复试准备", "https://yz.chsi.com.cn/", false);
        saveDefault("study-article-recommendation", "ARTICLE", "保研申请时间线", "梳理成绩排名、科研竞赛、夏令营、预推免和志愿填报等关键节点。", "保研申请", "https://yz.chsi.com.cn/tm/", false);
        saveDefault("study-article-abroad", "ARTICLE", "留学申请时间线", "从选校定位、语言考试、文书准备、网申递交到签证办理安排申请节奏。", "留学申请", "https://www.csc.edu.cn/", false);
        saveDefault("study-article-material", "ARTICLE", "升学材料准备清单", "梳理成绩证明、个人陈述、推荐材料、获奖经历和关键提交时间。", "申请材料", "https://www.chsi.com.cn/", false);
        saveDefault("study-video-preparation", "VIDEO", "研究生备考的阶段安排", "了解从确定方向、基础复习到冲刺和复试准备的常见节奏。", "备考方法", "https://yz.chsi.com.cn/", true);
        saveDefault("study-video-reexam", "VIDEO", "复试表达与材料复盘", "用常见问题检查个人介绍、专业回答和项目经历的表达是否清楚。", "复试准备", "https://yz.chsi.com.cn/", false);
        saveDefault("study-video-overseas", "VIDEO", "留学申请准备公开课", "了解选校、语言成绩、申请材料和学历认证等常见准备事项。", "留学申请", "https://www.cscse.edu.cn/", false);
    }
    private boolean shouldSeedDefaultResources(List<AdminContentItemDto> items) {
        if (items == null || items.isEmpty()) return true;
        for (AdminContentItemDto item : items) {
            if (item == null || !isLegacyDefaultResource(item.getContentId())) return false;
        }
        return items.size() <= 4;
    }
    private boolean isLegacyDefaultResource(String id) {
        return "study-service-chsi".equals(id)
                || "study-article-direction".equals(id)
                || "study-article-material".equals(id)
                || "study-video-preparation".equals(id);
    }
    private void saveDefault(String id, String type, String title, String summary, String category, String sourceUrl, boolean pinned) { AdminContentItemDto item = new AdminContentItemDto(); item.setContentId(id); item.setType(type); item.setTitle(title); item.setSummary(summary); item.setCategory(category); item.setSourceUrl(sourceUrl); item.setPinned(Boolean.valueOf(pinned)); item.setHidden(Boolean.FALSE); item.setPublishedAt(LocalDateTime.now()); storage.saveResource(item); }
    private AdminContentItemDto requireResource(String resourceId) { AdminContentItemDto item = storage.findResource(resourceId); if (item == null) throw new IllegalArgumentException("升学资讯不存在"); return item; }
    private CareerResourceCardDto resource(AdminContentItemDto source) {
        CareerResourceCardDto item = new CareerResourceCardDto();
        item.setId(source.getContentId()); item.setType("CONSULTATION".equals(source.getType()) ? "service" : source.getType().toLowerCase()); item.setTitle(source.getTitle()); item.setSummary(source.getSummary());
        item.setCategory(source.getCategory()); item.setSourceUrl(source.getSourceUrl()); item.setPublishedAt(source.getPublishedAt()); item.setPinned(source.getPinned());
        return item;
    }

    private String requireUserId(String userId) {
        if (!hasText(userId)) throw new IllegalArgumentException("userId is required");
        return userId.trim();
    }

    private String requireDirection(String direction) {
        String value = trimToNull(direction);
        if (!"POSTGRADUATE".equals(value) && !"RECOMMENDATION".equals(value) && !"STUDY_ABROAD".equals(value)) {
            throw new IllegalArgumentException("请选择考研、保研或留学方向");
        }
        return value;
    }

    private String profileTargetSchool(String userId) {
        UserProfileSnapshot snapshot = profileService.getSnapshot(userId);
        UserProfileSnapshot.OnboardingBlock onboarding = snapshot == null ? null : snapshot.getOnboarding();
        return onboarding == null ? null : trimToNull(onboarding.getTargetSchool());
    }

    private String directionLabel(String direction) {
        if ("POSTGRADUATE".equals(direction)) return "考研";
        if ("RECOMMENDATION".equals(direction)) return "保研";
        if ("STUDY_ABROAD".equals(direction)) return "留学";
        return "待选择";
    }

    private boolean hasText(String value) { return value != null && value.trim().length() > 0; }
    private String trimToNull(String value) { return hasText(value) ? value.trim() : null; }
}
