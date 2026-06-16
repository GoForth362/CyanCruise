package v620.cc001.cloud01.app01.mservice;

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
import v620.cc001.base.common.dto.career.NotificationConstants;
import v620.cc001.base.common.dto.career.NotificationOperationResult;
import v620.cc001.base.common.dto.career.NotificationPushRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Application boundary for CyanCruise admin console governance.
 */
public class AdminConsoleGovernanceApplicationService {

    private final AdminGovernanceStorage storage;
    private final NotificationsSubscriptionsApplicationService notifications;
    private final AdminConsoleGovernanceService helper;

    public AdminConsoleGovernanceApplicationService() {
        this(new InMemoryAdminGovernanceStorage(), new NotificationsSubscriptionsApplicationService(),
                new AdminConsoleGovernanceService());
    }

    public AdminConsoleGovernanceApplicationService(AdminGovernanceStorage storage,
                                                     NotificationsSubscriptionsApplicationService notifications,
                                                     AdminConsoleGovernanceService helper) {
        this.storage = storage;
        this.notifications = notifications;
        this.helper = helper;
    }

    public AdminIdentityDto whoami(String adminId) {
        return helper.authorize(adminId, storage.isAdmin(trim(adminId)));
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
            if (hasText(keyword) && (user.getNickname() == null
                    || !user.getNickname().toLowerCase().contains(keyword.trim().toLowerCase()))) {
                continue;
            }
            all.add(user);
        }
        return page(all, page, size);
    }

    public AdminUserDto userDetail(String adminId, String userId) {
        requireAdmin(adminId);
        AdminUserDto user = storage.findUser(userId);
        if (user == null) throw new IllegalArgumentException("user not found");
        return user;
    }

    public AdminOperationResult banUser(String adminId, String userId, String reason) {
        requireAdmin(adminId);
        AdminUserDto user = userDetail(adminId, userId);
        String before = helper.auditSnapshot(simple("status", user.getStatus(), "bannedReason", user.getBannedReason()));
        helper.applyBan(user, reason);
        storage.saveUser(user);
        NotificationOperationResult notification = pushUserNotice(userId, "Account suspended",
                "Your account has been suspended. Reason: " + user.getBannedReason(), null);
        boolean audited = audit(adminId, AdminConstants.ACTION_BAN_USER, "USER", userId, before,
                helper.auditSnapshot(simple("status", user.getStatus(), "bannedReason", user.getBannedReason())));
        return op(AdminConstants.STATUS_OK, "banned; notification=" + notification.getStatus(), userId, 1, audited);
    }

    public AdminOperationResult unbanUser(String adminId, String userId) {
        requireAdmin(adminId);
        AdminUserDto user = userDetail(adminId, userId);
        String before = helper.auditSnapshot(simple("status", user.getStatus(), "bannedReason", user.getBannedReason()));
        helper.applyUnban(user);
        storage.saveUser(user);
        NotificationOperationResult notification = pushUserNotice(userId, "Account restored",
                "Your account restriction has been lifted. Welcome back!", null);
        boolean audited = audit(adminId, AdminConstants.ACTION_UNBAN_USER, "USER", userId, before,
                helper.auditSnapshot(simple("status", user.getStatus(), "bannedReason", user.getBannedReason())));
        return op(AdminConstants.STATUS_OK, "unbanned; notification=" + notification.getStatus(), userId, 1, audited);
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
        return storage.listContent(type);
    }

    public AdminContentItemDto saveContent(String adminId, AdminContentItemDto content) {
        requireAdmin(adminId);
        if (content == null || !hasText(content.getTitle())) throw new IllegalArgumentException("content title is required");
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
            NotificationOperationResult result = pushUserNotice(user.getUserId(), request.getTitle(), request.getContent(), request.getLink());
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
        return storage.analyticsSummary();
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

    private List<AdminUserDto> broadcastTargets(AdminBroadcastRequest request) {
        List<AdminUserDto> targets = new ArrayList<AdminUserDto>();
        if (request != null && hasText(request.getUserId())) {
            AdminUserDto user = storage.findUser(request.getUserId().trim());
            if (user != null) targets.add(user);
            return targets;
        }
        for (AdminUserDto user : storage.listUsers()) {
            if (user.getDeletedAt() == null && AdminConstants.USER_STATUS_ACTIVE.equals(user.getStatus())) {
                targets.add(user);
            }
        }
        return targets;
    }

    private NotificationOperationResult pushUserNotice(String userId, String title, String content, String link) {
        NotificationPushRequest request = new NotificationPushRequest();
        request.setUserId(userId);
        request.setType(NotificationConstants.TYPE_ADMIN_BROADCAST);
        request.setTitle(title);
        request.setContent(content);
        request.setLink(link);
        return notifications.pushBestEffort(request);
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
        AdminIdentityDto identity = whoami(adminId);
        if (!AdminConstants.STATUS_OK.equals(identity.getStatus())) {
            throw new IllegalArgumentException(identity.getStatus());
        }
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

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
