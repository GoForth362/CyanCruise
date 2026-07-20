package v620.cc001.cloud01.app01.mservice.application;

import v620.cc001.base.common.dto.career.AdminContentItemDto;
import v620.cc001.base.common.dto.career.CareerResourceCardDto;
import v620.cc001.base.common.dto.career.CareerResourceFeedDto;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;
import v620.cc001.base.common.dto.furtherstudy.StudyCenterInsightDto;
import v620.cc001.base.common.dto.furtherstudy.StudyCenterSelectionDto;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyAnalysisDraftDto;
import v620.cc001.cloud01.app01.mservice.storage.CyanCruiseStorageFactory;
import v620.cc001.cloud01.app01.mservice.storage.StudyCenterStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Application service for the isolated study center. */
public class StudyCenterApplicationService {
    private final StudyCenterStorage storage;
    private final CareerProfileApplicationService profileService;
    private final AdminConsoleGovernanceApplicationService adminService;

    public StudyCenterApplicationService() {
        this(CyanCruiseStorageFactory.studyCenterStorage(), new CareerProfileApplicationService(),
                new AdminConsoleGovernanceApplicationService());
    }

    public StudyCenterApplicationService(StudyCenterStorage storage,
                                         CareerProfileApplicationService profileService) {
        this(storage, profileService, new AdminConsoleGovernanceApplicationService());
    }

    public StudyCenterApplicationService(StudyCenterStorage storage,
                                         CareerProfileApplicationService profileService,
                                         AdminConsoleGovernanceApplicationService adminService) {
        this.storage = storage;
        this.profileService = profileService;
        this.adminService = adminService;
        restorePublishedResourceCatalogWhenEmpty();
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
        StudyCenterSelectionDto value = new StudyCenterSelectionDto();
        value.setUserId(safeUserId);
        value.setDirection(requireDirection(direction));
        value.setTargetSchool(profileTargetSchool(safeUserId));
        value.setUpdatedAt(LocalDateTime.now());
        return storage.saveSelection(value);
    }

    public FurtherStudyAnalysisDraftDto getAnalysisDraft(String userId, String taskType) {
        return storage.loadAnalysisDraft(requireUserId(userId), requireTaskType(taskType));
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
        CareerResourceFeedDto feed = resources();
        insight.setSourceCount(Integer.valueOf(feed.getArticles().size() + feed.getVideos().size()
                + feed.getConsultations().size()));
        insight.setUpdatedAt(selection.getUpdatedAt());

        List<String> focusItems = new ArrayList<String>();
        if (!hasText(direction)) {
            insight.setStatus("PENDING_SELECTION");
            insight.setSummary("请先选择考研、保研或留学方向。");
            focusItems.add("选择一条主要升学方向");
        } else {
            insight.setStatus("READY");
            insight.setSummary("已选择" + directionLabel(direction) + "，请结合自己的真实情况继续准备。");
        }
        if (!hasText(school)) focusItems.add("在自画像中补充学校信息");
        if (!hasText(major)) focusItems.add("在自画像中补充专业信息");
        if (!hasText(targetSchool)) focusItems.add("在升学自画像中填写目标院校");
        insight.setFocusItems(focusItems);
        return insight;
    }

    private String requireTaskType(String taskType) {
        if (!hasText(taskType)) throw new IllegalArgumentException("请选择需要恢复的升学分析表单。");
        return taskType.trim();
    }

    public CareerResourceFeedDto resources() {
        CareerResourceFeedDto feed = new CareerResourceFeedDto();
        feed.setStatus("READY");
        feed.setMessage("仅展示管理员已发布的真实升学资讯。");
        feed.setUpdatedAt(LocalDateTime.now());
        List<CareerResourceCardDto> articles = new ArrayList<CareerResourceCardDto>();
        List<CareerResourceCardDto> videos = new ArrayList<CareerResourceCardDto>();
        List<CareerResourceCardDto> services = new ArrayList<CareerResourceCardDto>();
        List<AdminContentItemDto> items = storage.listResources();
        if (items != null) {
            for (AdminContentItemDto item : items) {
                if (item == null || Boolean.TRUE.equals(item.getHidden())) continue;
                CareerResourceCardDto card = resource(item);
                if ("VIDEO".equals(item.getType())) videos.add(card);
                else if ("CONSULTATION".equals(item.getType())) services.add(card);
                else articles.add(card);
            }
        }
        feed.setArticles(articles);
        feed.setVideos(videos);
        feed.setConsultations(services);
        return feed;
    }

    public List<AdminContentItemDto> listResourcesForAdmin(String adminId) {
        adminService.requireAdministrator(adminId);
        List<AdminContentItemDto> out = new ArrayList<AdminContentItemDto>();
        List<AdminContentItemDto> items = storage.listResources();
        if (items != null) {
            for (AdminContentItemDto item : items) {
                if (item != null) out.add(item);
            }
        }
        return out;
    }

    public AdminContentItemDto saveResourceForAdmin(String adminId, AdminContentItemDto resource) {
        adminService.requireAdministrator(adminId);
        if (resource == null || !hasText(resource.getTitle())) {
            throw new IllegalArgumentException("请填写内容标题");
        }
        String type = trimToNull(resource.getType());
        if ("RESOURCE".equals(type)) type = "CONSULTATION";
        if (!"ARTICLE".equals(type) && !"VIDEO".equals(type) && !"CONSULTATION".equals(type)) {
            throw new IllegalArgumentException("请选择内容类型");
        }
        resource.setType(type);
        resource.setContentId(hasText(resource.getContentId()) ? resource.getContentId().trim()
                : "study-resource-" + UUID.randomUUID().toString());
        resource.setTitle(resource.getTitle().trim());
        resource.setSummary(trimToNull(resource.getSummary()));
        resource.setCategory(trimToNull(resource.getCategory()));
        resource.setSourceUrl(trimToNull(resource.getSourceUrl()));
        resource.setImageUrl(null);
        resource.setPinned(Boolean.valueOf(Boolean.TRUE.equals(resource.getPinned())));
        resource.setHidden(Boolean.valueOf(Boolean.TRUE.equals(resource.getHidden())));
        if (resource.getPublishedAt() == null) resource.setPublishedAt(LocalDateTime.now());
        return storage.saveResource(resource);
    }

    public AdminContentItemDto toggleResourcePinned(String adminId, String resourceId) {
        adminService.requireAdministrator(adminId);
        AdminContentItemDto item = requireResource(resourceId);
        item.setPinned(Boolean.valueOf(!Boolean.TRUE.equals(item.getPinned())));
        return storage.saveResource(item);
    }

    public AdminContentItemDto toggleResourceHidden(String adminId, String resourceId) {
        adminService.requireAdministrator(adminId);
        AdminContentItemDto item = requireResource(resourceId);
        item.setHidden(Boolean.valueOf(!Boolean.TRUE.equals(item.getHidden())));
        return storage.saveResource(item);
    }

    public boolean deleteResource(String adminId, String resourceId) {
        adminService.requireAdministrator(adminId);
        return storage.deleteResource(requireResource(resourceId).getContentId());
    }

    private AdminContentItemDto requireResource(String resourceId) {
        AdminContentItemDto item = storage.findResource(resourceId);
        if (item == null) throw new IllegalArgumentException("升学资讯不存在");
        return item;
    }

    private void restorePublishedResourceCatalogWhenEmpty() {
        synchronized (storage) {
            if (storage.isPublishedResourceCatalogInitialized()) return;
            List<AdminContentItemDto> current = storage.listResources();
            if (current != null && !current.isEmpty()) {
                storage.markPublishedResourceCatalogInitialized();
                return;
            }

            LocalDateTime publishedAt = LocalDateTime.now();
            savePublishedResource("study-service-chsi", "CONSULTATION", "中国研究生招生信息网",
                    "查询硕士研究生招生、考试和调剂信息。", "官方服务", "https://yz.chsi.com.cn/", true,
                    publishedAt);
            savePublishedResource("study-service-chsi-account", "CONSULTATION", "中国高等教育学生信息网",
                    "查询学籍学历、成绩与教育背景核验相关服务。", "官方服务", "https://www.chsi.com.cn/", false,
                    publishedAt.minusMinutes(1));
            savePublishedResource("study-service-overseas", "CONSULTATION", "教育部留学服务中心",
                    "了解国（境）外学历学位认证、留学档案与回国服务。", "官方服务", "https://www.cscse.edu.cn/", false,
                    publishedAt.minusMinutes(2));
            savePublishedResource("study-article-direction", "ARTICLE", "如何选择升学方向",
                    "从发展目标、成绩基础、时间安排和家庭预算判断考研、保研或留学。", "升学规划", "https://yz.chsi.com.cn/", true,
                    publishedAt.minusMinutes(3));
            savePublishedResource("study-article-school", "ARTICLE", "考研择校的四个判断步骤",
                    "依次比较专业方向、院校层次、地区机会和近年招生要求，形成冲刺与稳妥选择。", "院校选择", "https://yz.chsi.com.cn/", false,
                    publishedAt.minusMinutes(4));
            savePublishedResource("study-article-reexam", "ARTICLE", "复试准备清单",
                    "围绕专业基础、个人介绍、项目经历、英语表达和材料核对安排复试准备。", "复试准备", "https://yz.chsi.com.cn/", false,
                    publishedAt.minusMinutes(5));
            savePublishedResource("study-article-recommendation", "ARTICLE", "保研申请时间线",
                    "梳理成绩排名、科研竞赛、夏令营、预推免和志愿填报等关键节点。", "保研申请", "https://yz.chsi.com.cn/tm/", false,
                    publishedAt.minusMinutes(6));
            savePublishedResource("study-article-abroad", "ARTICLE", "留学申请时间线",
                    "从选校定位、语言考试、文书准备、网申递交到签证办理安排申请节奏。", "留学申请", "https://www.csc.edu.cn/", false,
                    publishedAt.minusMinutes(7));
            savePublishedResource("study-article-material", "ARTICLE", "升学材料准备清单",
                    "梳理成绩证明、个人陈述、推荐材料、获奖经历和关键提交时间。", "申请材料", "https://www.chsi.com.cn/", false,
                    publishedAt.minusMinutes(8));
            savePublishedResource("study-video-preparation", "VIDEO", "研究生备考的阶段安排",
                    "了解从确定方向、基础复习到冲刺和复试准备的常见节奏。", "备考方法", "https://yz.chsi.com.cn/", true,
                    publishedAt.minusMinutes(9));
            savePublishedResource("study-video-reexam", "VIDEO", "复试表达与材料复盘",
                    "用常见问题检查个人介绍、专业回答和项目经历的表达是否清楚。", "复试准备", "https://yz.chsi.com.cn/", false,
                    publishedAt.minusMinutes(10));
            savePublishedResource("study-video-overseas", "VIDEO", "留学申请准备公开课",
                    "了解选校、语言成绩、申请材料和学历认证等常见准备事项。", "留学申请", "https://www.cscse.edu.cn/", false,
                    publishedAt.minusMinutes(11));
            storage.markPublishedResourceCatalogInitialized();
        }
    }

    private void savePublishedResource(String id, String type, String title, String summary,
                                       String category, String sourceUrl, boolean pinned,
                                       LocalDateTime publishedAt) {
        if (storage.findResource(id) != null) return;
        AdminContentItemDto item = new AdminContentItemDto();
        item.setContentId(id);
        item.setType(type);
        item.setTitle(title);
        item.setSummary(summary);
        item.setCategory(category);
        item.setSourceUrl(sourceUrl);
        item.setPinned(Boolean.valueOf(pinned));
        item.setHidden(Boolean.FALSE);
        item.setPublishedAt(publishedAt);
        storage.saveResource(item);
    }

    private CareerResourceCardDto resource(AdminContentItemDto source) {
        CareerResourceCardDto item = new CareerResourceCardDto();
        item.setId(source.getContentId());
        item.setType("CONSULTATION".equals(source.getType()) ? "service" : source.getType().toLowerCase());
        item.setTitle(source.getTitle()); item.setSummary(source.getSummary());
        item.setCategory(source.getCategory()); item.setSourceUrl(source.getSourceUrl());
        item.setPublishedAt(source.getPublishedAt()); item.setPinned(source.getPinned());
        return item;
    }

    private String requireUserId(String userId) {
        if (!hasText(userId)) throw new IllegalArgumentException("userId is required");
        return userId.trim();
    }

    private String requireDirection(String direction) {
        String value = trimToNull(direction);
        if (!"POSTGRADUATE".equals(value) && !"RECOMMENDATION".equals(value)
                && !"STUDY_ABROAD".equals(value)) {
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
