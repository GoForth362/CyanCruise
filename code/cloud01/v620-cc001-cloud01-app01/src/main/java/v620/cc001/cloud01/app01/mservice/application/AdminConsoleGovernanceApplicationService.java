package v620.cc001.cloud01.app01.mservice.application;

import v620.cc001.cloud01.app01.mservice.storage.AdminGovernanceStorage;
import v620.cc001.cloud01.app01.mservice.storage.CareerResourceStorage;
import v620.cc001.cloud01.app01.mservice.storage.CareerProfileStorage;
import v620.cc001.cloud01.app01.mservice.storage.CareerProfileStorageFactory;
import v620.cc001.cloud01.app01.mservice.storage.CyanCruiseStorageFactory;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerResourceStorage;
import v620.cc001.cloud01.app01.mservice.auth.CosmicAdminAuthorityResolver;
import v620.cc001.cloud01.app01.mservice.auth.impl.ReflectiveCosmicAdminAuthorityResolver;
import v620.base.helper.career.AdminConsoleGovernanceService;
import v620.cc001.base.common.dto.career.AdminAnalyticsSummaryDto;
import v620.cc001.base.common.dto.career.AdminAuditLogDto;
import v620.cc001.base.common.dto.career.AdminBroadcastRequest;
import v620.cc001.base.common.dto.career.AdminBroadcastResult;
import v620.cc001.base.common.dto.career.AdminCareerNodeDto;
import v620.cc001.base.common.dto.career.AdminCareerPathDto;
import v620.cc001.base.common.dto.career.AdminConstants;
import v620.cc001.base.common.dto.career.AdminContentItemDto;
import v620.cc001.base.common.dto.career.AdminIdentityDto;
import v620.cc001.base.common.dto.career.AdminOperationResult;
import v620.cc001.base.common.dto.career.AdminOrgDashboardDto;
import v620.cc001.base.common.dto.career.AdminOrganizationDto;
import v620.cc001.base.common.dto.career.AdminPageResult;
import v620.cc001.base.common.dto.career.AdminQuestionContributionRequest;
import v620.cc001.base.common.dto.career.AdminQuestionDto;
import v620.cc001.base.common.dto.career.AdminStudentRowDto;
import v620.cc001.base.common.dto.career.AdminUserDto;
import v620.cc001.base.common.dto.career.CareerResourceCardDto;
import v620.cc001.base.common.dto.career.NotificationConstants;
import v620.cc001.base.common.dto.career.NotificationOperationResult;
import v620.cc001.base.common.dto.career.NotificationPushRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Application boundary for CyanCruise admin console governance.
 */
public class AdminConsoleGovernanceApplicationService {

    private final AdminGovernanceStorage storage;
    private final CareerProfileStorage profileStorage;
    private final CareerResourceStorage defaultResourceStorage;
    private final NotificationsSubscriptionsApplicationService notifications;
    private final AdminConsoleGovernanceService helper;
    private final boolean trustResolvedAdminIdentity;
    private final CosmicAdminAuthorityResolver adminAuthorityResolver;

    public AdminConsoleGovernanceApplicationService() {
        this(CyanCruiseStorageFactory.adminGovernanceStorage(), profileStorageOrNull(), new NotificationsSubscriptionsApplicationService(),
                new AdminConsoleGovernanceService(), true, new InMemoryCareerResourceStorage(),
                new ReflectiveCosmicAdminAuthorityResolver());
        AdminAuditLogRetentionScheduler.ensureStarted(storage);
    }

    public AdminConsoleGovernanceApplicationService(AdminGovernanceStorage storage,
                                                     NotificationsSubscriptionsApplicationService notifications,
                                                     AdminConsoleGovernanceService helper) {
        this(storage, null, notifications, helper, false);
    }

    public AdminConsoleGovernanceApplicationService(AdminGovernanceStorage storage,
                                                     CareerProfileStorage profileStorage,
                                                     NotificationsSubscriptionsApplicationService notifications,
                                                     AdminConsoleGovernanceService helper) {
        this(storage, profileStorage, notifications, helper, false);
    }

    public AdminConsoleGovernanceApplicationService(AdminGovernanceStorage storage,
                                                     NotificationsSubscriptionsApplicationService notifications,
                                                     AdminConsoleGovernanceService helper,
                                                     boolean trustResolvedAdminIdentity) {
        this(storage, null, notifications, helper, trustResolvedAdminIdentity);
    }

    public AdminConsoleGovernanceApplicationService(AdminGovernanceStorage storage,
                                                     CareerProfileStorage profileStorage,
                                                     NotificationsSubscriptionsApplicationService notifications,
                                                     AdminConsoleGovernanceService helper,
                                                     boolean trustResolvedAdminIdentity) {
        this(storage, profileStorage, notifications, helper, trustResolvedAdminIdentity, null);
    }

    public AdminConsoleGovernanceApplicationService(AdminGovernanceStorage storage,
                                                     CareerProfileStorage profileStorage,
                                                     NotificationsSubscriptionsApplicationService notifications,
                                                     AdminConsoleGovernanceService helper,
                                                     boolean trustResolvedAdminIdentity,
                                                     CareerResourceStorage defaultResourceStorage) {
        this(storage, profileStorage, notifications, helper, trustResolvedAdminIdentity, defaultResourceStorage,
                new ReflectiveCosmicAdminAuthorityResolver());
    }

    public AdminConsoleGovernanceApplicationService(AdminGovernanceStorage storage,
                                              CareerProfileStorage profileStorage,
                                              NotificationsSubscriptionsApplicationService notifications,
                                              AdminConsoleGovernanceService helper,
                                              boolean trustResolvedAdminIdentity,
                                              CareerResourceStorage defaultResourceStorage,
                                              CosmicAdminAuthorityResolver adminAuthorityResolver) {
        this.storage = storage;
        this.profileStorage = profileStorage;
        this.defaultResourceStorage = defaultResourceStorage;
        this.notifications = notifications;
        this.helper = helper;
        this.trustResolvedAdminIdentity = trustResolvedAdminIdentity;
        this.adminAuthorityResolver = adminAuthorityResolver;
    }

    public AdminIdentityDto whoami(String adminId) {
        return helper.authorize(adminId, trustResolvedAdminIdentity || storage.isAdmin(trim(adminId)));
    }

    public boolean isUserAllowed(String userId) {
        if (!hasText(userId)) {
            return true;
        }
        AdminUserDto user = storage.findUser(userId.trim());
        if (user == null) {
            return true;
        }
        return user.getDeletedAt() == null && !AdminConstants.USER_STATUS_BANNED.equals(user.getStatus());
    }

    public void registerActiveUserIfAbsent(String userId) {
        registerActiveUserIfAbsent(userId, null, null);
    }

    public void registerActiveUserIfAbsent(String userId, String displayName, String orgId) {
        if (!hasText(userId)) {
            return;
        }
        String safeUserId = userId.trim();
        if (isDevelopmentUserId(safeUserId)) {
            return;
        }
        AdminUserDto existing = storage.findUser(safeUserId);
        if (existing != null) {
            boolean changed = false;
            if (hasText(displayName) && (isBlank(existing.getNickname()) || sameText(existing.getNickname(), existing.getUserId()))) {
                existing.setNickname(displayName.trim());
                changed = true;
            }
            if (hasText(orgId) && isBlank(existing.getOrgId())) {
                existing.setOrgId(orgId.trim());
                changed = true;
            }
            if (changed) {
                storage.saveUser(existing);
            }
            return;
        }
        AdminUserDto user = new AdminUserDto();
        user.setUserId(safeUserId);
        user.setNickname(firstText(displayName, safeUserId));
        user.setOrgId(trim(orgId));
        user.setStatus(AdminConstants.USER_STATUS_ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        storage.saveUser(user);
    }

    public List<AdminOrganizationDto> listOrganizations(String adminId) {
        requireAdmin(adminId);
        return storage.listOrganizations();
    }

    public AdminOrganizationDto saveOrganization(String adminId, AdminOrganizationDto organization) {
        requireAdmin(adminId);
        if (organization == null || !hasText(organization.getCode()) || !hasText(organization.getName())) {
            throw new IllegalArgumentException("organization code and name are required");
        }
        AdminOrganizationDto saved = storage.saveOrganization(organization);
        audit(adminId, "SAVE_ORGANIZATION", "ORGANIZATION", saved.getOrgId(), null,
                helper.auditSnapshot(simple("code", saved.getCode())));
        return saved;
    }

    public AdminOrgDashboardDto organizationDashboard(String adminId, String orgId) {
        requireAdmin(adminId);
        List<AdminUserDto> students = usersByOrg(orgId);
        return helper.dashboard(orgId, students, storage.listInterviewsByUsers(students));
    }

    public List<AdminStudentRowDto> organizationStudents(String adminId, String orgId) {
        requireAdmin(adminId);
        List<AdminUserDto> students = usersByOrg(orgId);
        return helper.studentRows(students, storage.listInterviewsByUsers(students));
    }

    public AdminPageResult<AdminUserDto> listUsers(String adminId, int page, int size, String keyword) {
        requireAdmin(adminId);
        List<AdminUserDto> all = new ArrayList<AdminUserDto>();
        for (AdminUserDto user : storage.listUsers()) {
            if (user.getDeletedAt() != null) continue;
            if (isDevelopmentUserId(user.getUserId())) continue;
            markAdministrator(user);
            if (!matchesUserKeyword(user, keyword)) {
                continue;
            }
            all.add(governanceView(user));
        }
        return page(all, page, size);
    }

    public AdminUserDto userDetail(String adminId, String userId) {
        requireAdmin(adminId);
        AdminUserDto user = requireStoredUser(userId);
        markAdministrator(user);
        return governanceView(user);
    }

    private boolean matchesUserKeyword(AdminUserDto user, String keyword) {
        if (!hasText(keyword)) {
            return true;
        }
        String normalized = keyword.trim().toLowerCase(java.util.Locale.ENGLISH);
        return containsIgnoreCase(user.getNickname(), normalized)
                || containsIgnoreCase(user.getUserId(), normalized);
    }

    private AdminUserDto governanceView(AdminUserDto user) {
        AdminUserDto view = new AdminUserDto();
        view.setUserId(user.getUserId());
        view.setNickname(user.getNickname());
        view.setStatus(user.getStatus());
        view.setBannedReason(user.getBannedReason());
        view.setAdministrator(user.getAdministrator());
        view.setCreatedAt(user.getCreatedAt());
        view.setDeletedAt(user.getDeletedAt());
        return view;
    }

    private AdminUserDto requireStoredUser(String userId) {
        AdminUserDto user = storage.findUser(userId);
        if (user == null) throw new IllegalArgumentException("user not found");
        return user;
    }

    private boolean containsIgnoreCase(String value, String normalizedKeyword) {
        return value != null && value.toLowerCase(java.util.Locale.ENGLISH).contains(normalizedKeyword);
    }

    private void markAdministrator(AdminUserDto user) {
        user.setAdministrator(Boolean.valueOf(isAdministratorAccount(user.getUserId())));
    }

    private boolean isAdministratorAccount(String userId) {
        boolean administrator = storage.isAdmin(trim(userId));
        if (!administrator && adminAuthorityResolver != null) {
            try {
                administrator = adminAuthorityResolver.isAdmin(userId, userId);
            } catch (RuntimeException ignored) {
                // Platform permission lookup is best-effort for list decoration and fails closed.
            }
        }
        return administrator;
    }

    public AdminOperationResult banUser(String adminId, String userId, String reason) {
        requireAdmin(adminId);
        String safeAdminId = trim(adminId);
        String safeUserId = trim(userId);
        AdminUserDto user = requireStoredUser(userId);
        if (sameText(safeAdminId, safeUserId)) {
            throw new IllegalArgumentException("不能禁用自己的用户端");
        }
        if (isAdministratorAccount(safeUserId)) {
            throw new IllegalArgumentException("管理员之间不能相互禁用用户端");
        }
        String before = helper.auditSnapshot(simple("status", user.getStatus(), "bannedReason", user.getBannedReason()));
        helper.applyBan(user, reason);
        storage.saveUser(user);
        NotificationOperationResult notification = pushUserNotice(userId, "用户端访问已受限",
                "你的 CyanCruise 用户端访问已被管理员限制。管理后台权限不在这里调整。", null);
        boolean audited = audit(adminId, AdminConstants.ACTION_BAN_USER, "USER", userId, before,
                helper.auditSnapshot(simple("status", user.getStatus(), "bannedReason", user.getBannedReason())));
        return op(AdminConstants.STATUS_OK, "已禁用该账号的用户端功能。", userId, 1, audited);
    }

    public AdminOperationResult unbanUser(String adminId, String userId) {
        requireAdmin(adminId);
        AdminUserDto user = requireStoredUser(userId);
        String before = helper.auditSnapshot(simple("status", user.getStatus(), "bannedReason", user.getBannedReason()));
        helper.applyUnban(user);
        storage.saveUser(user);
        NotificationOperationResult notification = pushUserNotice(userId, "用户端访问已恢复",
                "你的 CyanCruise 用户端访问已恢复。", null);
        boolean audited = audit(adminId, AdminConstants.ACTION_UNBAN_USER, "USER", userId, before,
                helper.auditSnapshot(simple("status", user.getStatus(), "bannedReason", user.getBannedReason())));
        return op(AdminConstants.STATUS_OK, "已恢复该账号的用户端功能。", userId, 1, audited);
    }

    public List<AdminCareerPathDto> listCareerPaths(String adminId) {
        requireAdmin(adminId);
        return storage.listCareerPaths();
    }

    public AdminCareerPathDto saveCareerPath(String adminId, AdminCareerPathDto path) {
        requireAdmin(adminId);
        if (path == null || !hasText(path.getCode()) || !hasText(path.getName())) {
            throw new IllegalArgumentException("path code and name are required");
        }
        AdminCareerPathDto saved = storage.saveCareerPath(path);
        audit(adminId, AdminConstants.ACTION_SAVE_SKILL_MAP, "CAREER_PATH", saved.getPathId(), null,
                helper.auditSnapshot(simple("code", saved.getCode(), "name", saved.getName())));
        return saved;
    }

    public boolean deleteCareerPath(String adminId, String pathId) {
        requireAdmin(adminId);
        boolean deleted = storage.deleteCareerPath(pathId);
        audit(adminId, AdminConstants.ACTION_SAVE_SKILL_MAP, "CAREER_PATH", pathId, null, null);
        return deleted;
    }

    public List<AdminCareerNodeDto> listCareerNodes(String adminId, String pathId) {
        requireAdmin(adminId);
        return storage.listCareerNodes(pathId);
    }

    public AdminCareerNodeDto saveCareerNode(String adminId, String pathId, AdminCareerNodeDto node) {
        requireAdmin(adminId);
        if (node == null || !hasText(node.getName())) throw new IllegalArgumentException("node name is required");
        node.setPathId(pathId);
        AdminCareerNodeDto saved = storage.saveCareerNode(node);
        audit(adminId, AdminConstants.ACTION_SAVE_SKILL_MAP, "CAREER_NODE", saved.getNodeId(), null,
                helper.auditSnapshot(simple("pathId", saved.getPathId(), "name", saved.getName())));
        return saved;
    }

    public boolean deleteCareerNode(String adminId, String nodeId) {
        requireAdmin(adminId);
        boolean deleted = storage.deleteCareerNode(nodeId);
        audit(adminId, AdminConstants.ACTION_SAVE_SKILL_MAP, "CAREER_NODE", nodeId, null, null);
        return deleted;
    }

    public List<AdminQuestionDto> listQuestions(String adminId, String source, String reviewStatus) {
        requireAdmin(adminId);
        List<AdminQuestionDto> out = new ArrayList<AdminQuestionDto>();
        for (AdminQuestionDto question : storage.listQuestions()) {
            if (hasText(source) && !source.trim().equals(question.getSource())) continue;
            if (hasText(reviewStatus) && !reviewStatus.trim().equals(question.getReviewStatus())) continue;
            out.add(question);
        }
        return out;
    }

    public AdminQuestionDto saveQuestion(String adminId, AdminQuestionDto question) {
        requireAdmin(adminId);
        if (question == null || !hasText(question.getContent())) {
            throw new IllegalArgumentException("question content is required");
        }
        String before = null;
        if (hasText(question.getQuestionId())) {
            AdminQuestionDto existing = storage.findQuestion(question.getQuestionId());
            if (existing != null) {
                before = helper.auditSnapshot(simple("reviewStatus", existing.getReviewStatus(), "content", existing.getContent()));
            }
        }
        if (!hasText(question.getSource())) question.setSource("ADMIN");
        if (!hasText(question.getPosition())) question.setPosition("通用岗位");
        if (!hasText(question.getDifficulty())) question.setDifficulty("NORMAL");
        if (!hasText(question.getReviewStatus())) question.setReviewStatus(AdminConstants.QUESTION_REVIEW_PENDING);
        if (!hasText(question.getStatus())) {
            question.setStatus(AdminConstants.QUESTION_REVIEW_REJECTED.equals(question.getReviewStatus())
                    ? AdminConstants.QUESTION_STATUS_HIDDEN : AdminConstants.QUESTION_STATUS_APPROVED);
        }
        if (question.getLikes() == null) question.setLikes(Integer.valueOf(0));
        if (question.getDrawCount() == null) question.setDrawCount(Integer.valueOf(0));
        AdminQuestionDto saved = storage.saveQuestion(question);
        audit(adminId, AdminConstants.ACTION_UPDATE_QUESTION, "QUESTION", saved.getQuestionId(), before,
                helper.auditSnapshot(simple("reviewStatus", saved.getReviewStatus(), "content", saved.getContent())));
        return saved;
    }

    public AdminQuestionDto updateQuestion(String adminId, String questionId, AdminQuestionDto patch) {
        requireAdmin(adminId);
        AdminQuestionDto existing = requireQuestion(questionId);
        String before = helper.auditSnapshot(simple("reviewStatus", existing.getReviewStatus(), "content", existing.getContent()));
        helper.applyQuestionPatch(existing, patch);
        AdminQuestionDto saved = storage.saveQuestion(existing);
        audit(adminId, AdminConstants.ACTION_UPDATE_QUESTION, "QUESTION", questionId, before,
                helper.auditSnapshot(simple("reviewStatus", saved.getReviewStatus(), "content", saved.getContent())));
        return saved;
    }

    public AdminQuestionDto approveQuestion(String adminId, String questionId) {
        requireAdmin(adminId);
        AdminQuestionDto question = requireQuestion(questionId);
        question.setReviewStatus(helper.approveReviewStatus(question.getReviewStatus()));
        AdminQuestionDto saved = storage.saveQuestion(question);
        audit(adminId, AdminConstants.ACTION_APPROVE_QUESTION, "QUESTION", questionId, null,
                helper.auditSnapshot(simple("reviewStatus", saved.getReviewStatus())));
        return saved;
    }

    public AdminQuestionDto rejectQuestion(String adminId, String questionId) {
        requireAdmin(adminId);
        AdminQuestionDto question = requireQuestion(questionId);
        question.setReviewStatus(helper.rejectReviewStatus(question.getReviewStatus()));
        AdminQuestionDto saved = storage.saveQuestion(question);
        audit(adminId, AdminConstants.ACTION_REJECT_QUESTION, "QUESTION", questionId, null,
                helper.auditSnapshot(simple("reviewStatus", saved.getReviewStatus())));
        return saved;
    }

    public boolean deleteQuestion(String adminId, String questionId) {
        requireAdmin(adminId);
        boolean deleted = storage.deleteQuestion(questionId);
        audit(adminId, AdminConstants.ACTION_DELETE_QUESTION, "QUESTION", questionId, null, null);
        return deleted;
    }

    public AdminQuestionDto contributeQuestion(AdminQuestionContributionRequest request) {
        AdminQuestionDto question = helper.buildContribution(request, "cyancruise-qbank");
        return storage.saveQuestion(question);
    }

    public List<AdminContentItemDto> listContent(String adminId, String type) {
        requireAdmin(adminId);
        syncDefaultResources();
        return storage.listContent(type);
    }

    public AdminContentItemDto saveContent(String adminId, AdminContentItemDto content) {
        requireAdmin(adminId);
        if (content == null || !hasText(content.getTitle())) throw new IllegalArgumentException("content title is required");
        normalizeContentGroup(content);
        content.setImageUrl(null);
        AdminContentItemDto saved = storage.saveContent(content);
        audit(adminId, AdminConstants.ACTION_SAVE_CONTENT, firstText(saved.getType(), "CONTENT"), saved.getContentId(), null,
                helper.auditSnapshot(simple("title", saved.getTitle(), "hidden", String.valueOf(saved.getHidden()))));
        return saved;
    }

    public AdminContentItemDto toggleContentPinned(String adminId, String contentId) {
        requireAdmin(adminId);
        AdminContentItemDto item = requireContent(contentId);
        helper.togglePinned(item);
        AdminContentItemDto saved = storage.saveContent(item);
        audit(adminId, AdminConstants.ACTION_TOGGLE_CONTENT, firstText(saved.getType(), "CONTENT"), contentId, null,
                helper.auditSnapshot(simple("pinned", String.valueOf(saved.getPinned()))));
        return saved;
    }

    public AdminContentItemDto toggleContentHidden(String adminId, String contentId) {
        requireAdmin(adminId);
        AdminContentItemDto item = requireContent(contentId);
        helper.toggleHidden(item);
        AdminContentItemDto saved = storage.saveContent(item);
        audit(adminId, AdminConstants.ACTION_TOGGLE_CONTENT, firstText(saved.getType(), "CONTENT"), contentId, null,
                helper.auditSnapshot(simple("hidden", String.valueOf(saved.getHidden()))));
        return saved;
    }

    public boolean deleteContent(String adminId, String contentId) {
        requireAdmin(adminId);
        AdminContentItemDto item = requireContent(contentId);
        boolean deleted = storage.deleteContent(contentId);
        audit(adminId, AdminConstants.ACTION_DELETE_CONTENT, firstText(item.getType(), "CONTENT"), contentId, null, null);
        return deleted;
    }

    public AdminBroadcastResult broadcast(String adminId, AdminBroadcastRequest request) {
        requireAdmin(adminId);
        List<AdminUserDto> targets = broadcastTargets(request);
        AdminBroadcastResult validation = helper.validateBroadcast(request, targets.size());
        if (!AdminConstants.STATUS_OK.equals(validation.getStatus())) return validation;
        int success = 0;
        int failed = 0;
        int skipped = 0;
        for (AdminUserDto user : targets) {
            NotificationOperationResult result = pushUserNotice(user.getUserId(), request.getTitle(), request.getContent(), null);
            if (NotificationConstants.RESULT_OK.equals(result.getStatus())) success++;
            else if (NotificationConstants.RESULT_SKIPPED.equals(result.getStatus())) skipped++;
            else failed++;
        }
        AdminBroadcastResult result = helper.broadcastResult(targets.size(), success, failed, skipped);
        audit(adminId, AdminConstants.ACTION_BROADCAST, "NOTIFICATION", null, null,
                helper.auditSnapshot(simple("targetCount", String.valueOf(targets.size()), "title", request.getTitle())));
        return result;
    }

    public AdminAnalyticsSummaryDto analyticsSummary(String adminId) {
        requireAdmin(adminId);
        AdminAnalyticsSummaryDto summary = storage.analyticsSummary();
        int visibleUsers = visibleUserCount();
        summary.setTotalUsers(Integer.valueOf(visibleUsers));
        if (summary.getEventBreakdown30d() != null) {
            summary.getEventBreakdown30d().put("USERS", Integer.valueOf(visibleUsers));
        }
        return summary;
    }

    public AdminPageResult<AdminAuditLogDto> auditLogs(String adminId, int page, int size) {
        requireAdmin(adminId);
        return page(storage.listAuditLogs(), page, size);
    }

    private List<AdminUserDto> usersByOrg(String orgId) {
        List<AdminUserDto> users = new ArrayList<AdminUserDto>();
        for (AdminUserDto user : storage.listUsers()) {
            if (orgId != null && orgId.equals(user.getOrgId())) users.add(user);
        }
        return users;
    }

    private int visibleUserCount() {
        int count = 0;
        for (AdminUserDto user : storage.listUsers()) {
            if (user.getDeletedAt() != null) continue;
            if (isDevelopmentUserId(user.getUserId())) continue;
            count++;
        }
        return count;
    }

    private List<AdminUserDto> broadcastTargets(AdminBroadcastRequest request) {
        List<AdminUserDto> targets = new ArrayList<AdminUserDto>();
        Set<String> requestedUserIds = new LinkedHashSet<String>();
        if (request != null) {
            if (hasText(request.getUserId())) {
                requestedUserIds.add(request.getUserId().trim());
            }
            if (request.getUserIds() != null) {
                for (String userId : request.getUserIds()) {
                    if (hasText(userId)) requestedUserIds.add(userId.trim());
                }
            }
        }
        if (!requestedUserIds.isEmpty()) {
            for (String userId : requestedUserIds) {
                AdminUserDto user = storage.findUser(userId);
                if (isBroadcastEligible(user)) targets.add(user);
            }
            return targets;
        }
        for (AdminUserDto user : storage.listUsers()) {
            if (isBroadcastEligible(user)) targets.add(user);
        }
        return targets;
    }

    private boolean isBroadcastEligible(AdminUserDto user) {
        return user != null && user.getDeletedAt() == null && AdminConstants.USER_STATUS_ACTIVE.equals(user.getStatus());
    }

    private NotificationOperationResult pushUserNotice(String userId, String title, String content, String link) {
        try {
            NotificationPushRequest request = new NotificationPushRequest();
            request.setUserId(userId);
            request.setType(NotificationConstants.TYPE_ADMIN_BROADCAST);
            request.setTitle(title);
            request.setContent(content);
            request.setLink(link);
            return notifications.pushBestEffort(request);
        } catch (Throwable ex) {
            NotificationOperationResult result = new NotificationOperationResult();
            result.setStatus(NotificationConstants.RESULT_FAILED);
            result.setMessage("通知暂未发送，不影响本次管理操作。");
            result.setUpdated(Integer.valueOf(0));
            return result;
        }
    }

    private boolean audit(String adminId, String action, String targetType, String targetId, String beforeJson, String afterJson) {
        try {
            AdminAuditLogDto log = new AdminAuditLogDto();
            log.setAdminId(adminId);
            log.setAction(action);
            log.setTargetType(targetType);
            log.setTargetId(targetId);
            log.setBeforeJson(beforeJson);
            log.setAfterJson(afterJson);
            log.setCreatedAt(LocalDateTime.now());
            return storage.saveAudit(log);
        } catch (Exception ex) {
            return false;
        }
    }

    private void requireAdmin(String adminId) {
        if (trustResolvedAdminIdentity && hasText(adminId)) {
            return;
        }
        AdminIdentityDto identity = whoami(adminId);
        if (!AdminConstants.STATUS_OK.equals(identity.getStatus())) {
            throw new IllegalArgumentException(identity.getStatus());
        }
    }

    /** Allows isolated administration modules to reuse the same administrator boundary. */
    public void requireAdministrator(String adminId) {
        requireAdmin(adminId);
    }

    private AdminQuestionDto requireQuestion(String questionId) {
        AdminQuestionDto question = storage.findQuestion(questionId);
        if (question == null) throw new IllegalArgumentException("question not found");
        return question;
    }

    private AdminContentItemDto requireContent(String contentId) {
        AdminContentItemDto item = storage.findContent(contentId);
        if (item == null) throw new IllegalArgumentException("content not found");
        return item;
    }

    private void syncDefaultResources() {
        if (defaultResourceStorage == null) {
            return;
        }
        List<CareerResourceCardDto> cards = defaultResourceStorage.listCards();
        for (CareerResourceCardDto card : cards) {
            if (card == null || !hasText(card.getId()) || !hasText(card.getTitle())) {
                continue;
            }
            if (storage.findContent(card.getId()) != null) {
                continue;
            }
            storage.saveContent(contentFromResource(card));
        }
    }

    private AdminContentItemDto contentFromResource(CareerResourceCardDto card) {
        AdminContentItemDto item = new AdminContentItemDto();
        item.setContentId(card.getId());
        item.setType(contentTypeFromResource(card.getType()));
        item.setTitle(card.getTitle());
        item.setSummary(firstText(card.getSummary(), card.getBody()));
        item.setCategory(contentCategoryFromType(item.getType()));
        item.setSourceUrl(card.getSourceUrl());
        item.setImageUrl(card.getImageUrl());
        item.setPinned(Boolean.FALSE);
        item.setHidden(Boolean.FALSE);
        item.setPublishedAt(card.getPublishedAt());
        return item;
    }

    private String contentTypeFromResource(String type) {
        String normalized = type == null ? "" : type.trim().toLowerCase();
        if ("video".equals(normalized)) {
            return AdminConstants.CONTENT_TYPE_VIDEO;
        }
        if ("article".equals(normalized)) {
            return AdminConstants.CONTENT_TYPE_ARTICLE;
        }
        return AdminConstants.CONTENT_TYPE_RESOURCE;
    }

    private void normalizeContentGroup(AdminContentItemDto content) {
        String type = content.getType();
        if (AdminConstants.CONTENT_TYPE_VIDEO.equals(type)) {
            content.setCategory("相关视频");
            return;
        }
        if (AdminConstants.CONTENT_TYPE_ARTICLE.equals(type)) {
            content.setCategory("精选文章");
            return;
        }
        content.setType(AdminConstants.CONTENT_TYPE_RESOURCE);
        content.setCategory("公共服务");
    }

    private String contentCategoryFromType(String type) {
        if (AdminConstants.CONTENT_TYPE_VIDEO.equals(type)) return "相关视频";
        if (AdminConstants.CONTENT_TYPE_ARTICLE.equals(type)) return "精选文章";
        return "公共服务";
    }

    private AdminOperationResult op(String status, String message, String targetId, int updated, boolean auditRecorded) {
        AdminOperationResult result = new AdminOperationResult();
        result.setStatus(status);
        result.setMessage(message);
        result.setTargetId(targetId);
        result.setUpdated(Integer.valueOf(updated));
        result.setAuditRecorded(Boolean.valueOf(auditRecorded));
        return result;
    }

    private <T> AdminPageResult<T> page(List<T> all, int page, int size) {
        int safePage = helper.safePage(page);
        int safeSize = helper.safeSize(size);
        int from = Math.min(all.size(), safePage * safeSize);
        int to = Math.min(all.size(), from + safeSize);
        AdminPageResult<T> result = new AdminPageResult<T>();
        result.setItems(new ArrayList<T>(all.subList(from, to)));
        result.setPage(Integer.valueOf(safePage));
        result.setSize(Integer.valueOf(safeSize));
        result.setTotal(Integer.valueOf(all.size()));
        return result;
    }

    private Map<String, String> simple(String k1, String v1) {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put(k1, v1);
        return map;
    }

    private Map<String, String> simple(String k1, String v1, String k2, String v2) {
        Map<String, String> map = simple(k1, v1);
        map.put(k2, v2);
        return map;
    }

    private String firstText(String first, String second) {
        return hasText(first) ? first.trim() : second;
    }

    private boolean isDevelopmentUserId(String userId) {
        if (!hasText(userId)) {
            return false;
        }
        String safe = userId.trim();
        return "api-user".equals(safe) || "demo-user".equals(safe) || "dev-user".equals(safe)
                || "test-user".equals(safe);
    }

    private boolean isBlank(String value) {
        return !hasText(value);
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private boolean sameText(String left, String right) {
        return hasText(left) && hasText(right) && left.trim().equals(right.trim());
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }

    private static CareerProfileStorage profileStorageOrNull() {
        try {
            return CareerProfileStorageFactory.fromSystemProperties();
        } catch (RuntimeException ex) {
            return null;
        }
    }
}
