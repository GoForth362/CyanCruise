package v620.cc001.cloud01.app01.mservice.storage.impl;

import v620.cc001.cloud01.app01.mservice.storage.*;
import v620.cc001.base.common.dto.career.AdminAnalyticsSummaryDto;
import v620.cc001.base.common.dto.career.AdminAuditLogDto;
import v620.cc001.base.common.dto.career.AdminCareerNodeDto;
import v620.cc001.base.common.dto.career.AdminCareerPathDto;
import v620.cc001.base.common.dto.career.AdminConstants;
import v620.cc001.base.common.dto.career.AdminContentItemDto;
import v620.cc001.base.common.dto.career.AdminInterviewSummaryDto;
import v620.cc001.base.common.dto.career.AdminOrganizationDto;
import v620.cc001.base.common.dto.career.AdminQuestionDto;
import v620.cc001.base.common.dto.career.AdminUserDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryAdminGovernanceStorage implements AdminGovernanceStorage {

    private final Map<String, Boolean> admins = new LinkedHashMap<String, Boolean>();
    private final Map<String, AdminOrganizationDto> organizations = new LinkedHashMap<String, AdminOrganizationDto>();
    private final Map<String, AdminUserDto> users = new LinkedHashMap<String, AdminUserDto>();
    private final Map<String, List<AdminInterviewSummaryDto>> interviews = new LinkedHashMap<String, List<AdminInterviewSummaryDto>>();
    private final Map<String, AdminCareerPathDto> paths = new LinkedHashMap<String, AdminCareerPathDto>();
    private final Map<String, AdminCareerNodeDto> nodes = new LinkedHashMap<String, AdminCareerNodeDto>();
    private final Map<String, AdminQuestionDto> questions = new LinkedHashMap<String, AdminQuestionDto>();
    private final Map<String, AdminContentItemDto> contents = new LinkedHashMap<String, AdminContentItemDto>();
    private final List<AdminAuditLogDto> auditLogs = new ArrayList<AdminAuditLogDto>();
    private final Map<String, Integer> eventBreakdown = new LinkedHashMap<String, Integer>();
    private final AtomicInteger ids = new AtomicInteger(1);

    public InMemoryAdminGovernanceStorage() {
        admins.put("admin", Boolean.TRUE);
    }

    public void addAdmin(String userId) {
        admins.put(userId, Boolean.TRUE);
    }

    public void addInterview(AdminInterviewSummaryDto interview) {
        if (interview == null || interview.getUserId() == null) return;
        List<AdminInterviewSummaryDto> list = interviews.get(interview.getUserId());
        if (list == null) {
            list = new ArrayList<AdminInterviewSummaryDto>();
            interviews.put(interview.getUserId(), list);
        }
        list.add(interview);
    }

    public void setEventCount(String eventType, int count) {
        eventBreakdown.put(eventType, Integer.valueOf(count));
    }

    public boolean isAdmin(String userId) {
        return Boolean.TRUE.equals(admins.get(userId));
    }

    public List<AdminOrganizationDto> listOrganizations() {
        return new ArrayList<AdminOrganizationDto>(organizations.values());
    }

    public AdminOrganizationDto saveOrganization(AdminOrganizationDto organization) {
        if (organization.getOrgId() == null) organization.setOrgId(nextId("org"));
        if (organization.getActive() == null) organization.setActive(Boolean.TRUE);
        organizations.put(organization.getOrgId(), organization);
        return organization;
    }

    public AdminOrganizationDto findOrganization(String orgId) {
        return organizations.get(orgId);
    }

    public List<AdminUserDto> listUsers() {
        return new ArrayList<AdminUserDto>(users.values());
    }

    public AdminUserDto findUser(String userId) {
        return users.get(userId);
    }

    public AdminUserDto saveUser(AdminUserDto user) {
        if (user.getUserId() == null) user.setUserId(nextId("user"));
        if (user.getStatus() == null) user.setStatus(AdminConstants.USER_STATUS_ACTIVE);
        if (user.getCreatedAt() == null) user.setCreatedAt(LocalDateTime.now());
        users.put(user.getUserId(), user);
        return user;
    }

    public List<AdminInterviewSummaryDto> listInterviewsByUser(String userId) {
        List<AdminInterviewSummaryDto> list = interviews.get(userId);
        return list == null ? new ArrayList<AdminInterviewSummaryDto>() : new ArrayList<AdminInterviewSummaryDto>(list);
    }

    public List<AdminCareerPathDto> listCareerPaths() {
        return new ArrayList<AdminCareerPathDto>(paths.values());
    }

    public AdminCareerPathDto saveCareerPath(AdminCareerPathDto path) {
        if (path.getPathId() == null) path.setPathId(nextId("path"));
        paths.put(path.getPathId(), path);
        return path;
    }

    public boolean deleteCareerPath(String pathId) {
        return paths.remove(pathId) != null;
    }

    public List<AdminCareerNodeDto> listCareerNodes(String pathId) {
        List<AdminCareerNodeDto> out = new ArrayList<AdminCareerNodeDto>();
        for (AdminCareerNodeDto node : nodes.values()) {
            if (pathId != null && pathId.equals(node.getPathId())) out.add(node);
        }
        Collections.sort(out, new Comparator<AdminCareerNodeDto>() {
            public int compare(AdminCareerNodeDto a, AdminCareerNodeDto b) {
                int av = a.getSortOrder() == null ? 0 : a.getSortOrder().intValue();
                int bv = b.getSortOrder() == null ? 0 : b.getSortOrder().intValue();
                return Integer.compare(av, bv);
            }
        });
        return out;
    }

    public AdminCareerNodeDto saveCareerNode(AdminCareerNodeDto node) {
        if (node.getNodeId() == null) node.setNodeId(nextId("node"));
        nodes.put(node.getNodeId(), node);
        return node;
    }

    public boolean deleteCareerNode(String nodeId) {
        return nodes.remove(nodeId) != null;
    }

    public List<AdminQuestionDto> listQuestions() {
        return new ArrayList<AdminQuestionDto>(questions.values());
    }

    public AdminQuestionDto findQuestion(String questionId) {
        return questions.get(questionId);
    }

    public AdminQuestionDto saveQuestion(AdminQuestionDto question) {
        if (question.getQuestionId() == null) question.setQuestionId(nextId("q"));
        if (question.getCreatedAt() == null) question.setCreatedAt(LocalDateTime.now());
        questions.put(question.getQuestionId(), question);
        return question;
    }

    public boolean deleteQuestion(String questionId) {
        return questions.remove(questionId) != null;
    }

    public List<AdminContentItemDto> listContent(String type) {
        List<AdminContentItemDto> out = new ArrayList<AdminContentItemDto>();
        for (AdminContentItemDto item : contents.values()) {
            if (type == null || type.equals(item.getType())) out.add(item);
        }
        return out;
    }

    public AdminContentItemDto findContent(String contentId) {
        return contents.get(contentId);
    }

    public AdminContentItemDto saveContent(AdminContentItemDto content) {
        if (content.getContentId() == null) content.setContentId(nextId("content"));
        if (content.getPublishedAt() == null) content.setPublishedAt(LocalDateTime.now());
        if (content.getType() == null) content.setType(AdminConstants.CONTENT_TYPE_ARTICLE);
        contents.put(content.getContentId(), content);
        return content;
    }

    public boolean deleteContent(String contentId) {
        return contents.remove(contentId) != null;
    }

    public AdminAnalyticsSummaryDto analyticsSummary() {
        AdminAnalyticsSummaryDto summary = new AdminAnalyticsSummaryDto();
        summary.setTotalUsers(Integer.valueOf(users.size()));
        int totalInterviews = 0;
        for (List<AdminInterviewSummaryDto> list : interviews.values()) totalInterviews += list.size();
        summary.setTotalInterviews(Integer.valueOf(totalInterviews));
        summary.setTotalAssessments(Integer.valueOf(0));
        summary.setTotalCheckIns(Integer.valueOf(0));
        summary.setSince(LocalDateTime.now().minusDays(30));
        summary.setEventBreakdown30d(new LinkedHashMap<String, Integer>(eventBreakdown));
        return summary;
    }

    public List<AdminAuditLogDto> listAuditLogs() {
        List<AdminAuditLogDto> out = new ArrayList<AdminAuditLogDto>(auditLogs);
        Collections.reverse(out);
        return out;
    }

    public boolean saveAudit(AdminAuditLogDto auditLog) {
        if (auditLog.getAuditId() == null) auditLog.setAuditId(nextId("audit"));
        if (auditLog.getCreatedAt() == null) auditLog.setCreatedAt(LocalDateTime.now());
        auditLogs.add(auditLog);
        return true;
    }

    public int deleteAuditLogsBefore(LocalDateTime cutoff) {
        if (cutoff == null) return 0;
        int before = auditLogs.size();
        auditLogs.removeIf(log -> log != null && log.getCreatedAt() != null && log.getCreatedAt().isBefore(cutoff));
        return before - auditLogs.size();
    }

    public Map<String, List<AdminInterviewSummaryDto>> listInterviewsByUsers(List<AdminUserDto> users) {
        Map<String, List<AdminInterviewSummaryDto>> out = new LinkedHashMap<String, List<AdminInterviewSummaryDto>>();
        if (users == null) return out;
        for (AdminUserDto user : users) {
            out.put(user.getUserId(), listInterviewsByUser(user.getUserId()));
        }
        return out;
    }

    private String nextId(String prefix) {
        return prefix + "-" + ids.getAndIncrement();
    }
}
