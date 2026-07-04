package v620.cc001.cloud01.app01.mservice.storage.impl;

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
import v620.cc001.cloud01.app01.mservice.storage.AdminGovernanceStorage;
import v620.cc001.cloud01.app01.mservice.storage.PostgresqlStorageConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PostgresqlAdminGovernanceStorage extends PostgresqlStorageSupport implements AdminGovernanceStorage {

    public PostgresqlAdminGovernanceStorage(PostgresqlStorageConfig config) {
        super(config, "admin governance storage");
        if (config.isInitialize()) {
            initialize();
        }
    }

    public boolean isAdmin(String userId) {
        return false;
    }

    public List<AdminOrganizationDto> listOrganizations() {
        return listPayloads("cc_admin_organization", "active DESC, name ASC", AdminOrganizationDto.class);
    }

    public AdminOrganizationDto saveOrganization(AdminOrganizationDto organization) {
        if (organization.getOrgId() == null) organization.setOrgId(nextId("org"));
        if (organization.getActive() == null) organization.setActive(Boolean.TRUE);
        String sql = "INSERT INTO " + table("cc_admin_organization")
                + " (org_id, code, name, active, payload_json)"
                + " VALUES (?, ?, ?, ?, ?::jsonb)"
                + " ON CONFLICT (org_id) DO UPDATE SET"
                + " code = EXCLUDED.code, name = EXCLUDED.name, active = EXCLUDED.active,"
                + " payload_json = EXCLUDED.payload_json";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, organization.getOrgId());
            statement.setString(2, organization.getCode());
            statement.setString(3, organization.getName());
            if (organization.getActive() == null) statement.setNull(4, java.sql.Types.BOOLEAN);
            else statement.setBoolean(4, organization.getActive().booleanValue());
            statement.setString(5, toJson(organization));
            statement.executeUpdate();
            return organization;
        } catch (SQLException e) {
            throw storageException("save admin organization", e);
        } finally {
            close(statement);
            close(connection);
        }
    }

    public AdminOrganizationDto findOrganization(String orgId) {
        return findPayload("cc_admin_organization", "org_id", orgId, AdminOrganizationDto.class);
    }

    public List<AdminUserDto> listUsers() {
        return listPayloads("cc_admin_user", "created_at DESC, user_id ASC", AdminUserDto.class);
    }

    public AdminUserDto findUser(String userId) {
        return findPayload("cc_admin_user", "user_id", userId, AdminUserDto.class);
    }

    public AdminUserDto saveUser(AdminUserDto user) {
        if (user.getUserId() == null) user.setUserId(nextId("user"));
        if (user.getStatus() == null) user.setStatus(AdminConstants.USER_STATUS_ACTIVE);
        if (user.getCreatedAt() == null) user.setCreatedAt(LocalDateTime.now());
        String sql = "INSERT INTO " + table("cc_admin_user")
                + " (user_id, org_id, nickname, status, payload_json, created_at, deleted_at)"
                + " VALUES (?, ?, ?, ?, ?::jsonb, ?, ?)"
                + " ON CONFLICT (user_id) DO UPDATE SET"
                + " org_id = EXCLUDED.org_id, nickname = EXCLUDED.nickname, status = EXCLUDED.status,"
                + " payload_json = EXCLUDED.payload_json, deleted_at = EXCLUDED.deleted_at";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, user.getUserId());
            statement.setString(2, user.getOrgId());
            statement.setString(3, user.getNickname());
            statement.setString(4, user.getStatus());
            statement.setString(5, toJson(user));
            statement.setTimestamp(6, timestamp(user.getCreatedAt()));
            statement.setTimestamp(7, timestamp(user.getDeletedAt()));
            statement.executeUpdate();
            return user;
        } catch (SQLException e) {
            throw storageException("save admin user", e);
        } finally {
            close(statement);
            close(connection);
        }
    }

    public List<AdminInterviewSummaryDto> listInterviewsByUser(String userId) {
        String sql = "SELECT interview_id, user_id, final_score, payload_json, started_at FROM "
                + table("cc_interview_session") + " WHERE user_id = ? ORDER BY started_at DESC, interview_id DESC";
        List<AdminInterviewSummaryDto> out = new ArrayList<AdminInterviewSummaryDto>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, requireText(userId, "userId"));
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                AdminInterviewSummaryDto item = new AdminInterviewSummaryDto();
                item.setInterviewId(String.valueOf(resultSet.getLong("interview_id")));
                item.setUserId(resultSet.getString("user_id"));
                int score = resultSet.getInt("final_score");
                item.setFinalScore(resultSet.wasNull() ? null : Integer.valueOf(score));
                item.setReportJson(resultSet.getString("payload_json"));
                item.setStartedAt(localDateTime(resultSet.getTimestamp("started_at")));
                out.add(item);
            }
            return out;
        } catch (SQLException e) {
            if (isMissingTable(e)) return out;
            throw storageException("list admin interviews", e);
        } finally {
            close(resultSet);
            close(statement);
            close(connection);
        }
    }

    public List<AdminCareerPathDto> listCareerPaths() {
        return listPayloads("cc_admin_career_path", "code ASC", AdminCareerPathDto.class);
    }

    public AdminCareerPathDto saveCareerPath(AdminCareerPathDto path) {
        if (path.getPathId() == null) path.setPathId(nextId("path"));
        String sql = "INSERT INTO " + table("cc_admin_career_path")
                + " (path_id, code, name, payload_json) VALUES (?, ?, ?, ?::jsonb)"
                + " ON CONFLICT (path_id) DO UPDATE SET"
                + " code = EXCLUDED.code, name = EXCLUDED.name, payload_json = EXCLUDED.payload_json";
        executePath(sql, path);
        return path;
    }

    public boolean deleteCareerPath(String pathId) {
        return deleteById("cc_admin_career_path", "path_id", pathId);
    }

    public List<AdminCareerNodeDto> listCareerNodes(String pathId) {
        List<AdminCareerNodeDto> out = new ArrayList<AdminCareerNodeDto>();
        String sql = "SELECT payload_json FROM " + table("cc_admin_career_node")
                + " WHERE path_id = ? ORDER BY sort_order ASC, node_id ASC";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, requireText(pathId, "pathId"));
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                AdminCareerNodeDto item = readJson(resultSet.getString("payload_json"), AdminCareerNodeDto.class, null);
                if (item != null) out.add(item);
            }
            return out;
        } catch (SQLException e) {
            throw storageException("list admin career nodes", e);
        } finally {
            close(resultSet);
            close(statement);
            close(connection);
        }
    }

    public AdminCareerNodeDto saveCareerNode(AdminCareerNodeDto node) {
        if (node.getNodeId() == null) node.setNodeId(nextId("node"));
        String sql = "INSERT INTO " + table("cc_admin_career_node")
                + " (node_id, path_id, parent_id, sort_order, name, payload_json)"
                + " VALUES (?, ?, ?, ?, ?, ?::jsonb)"
                + " ON CONFLICT (node_id) DO UPDATE SET"
                + " path_id = EXCLUDED.path_id, parent_id = EXCLUDED.parent_id,"
                + " sort_order = EXCLUDED.sort_order, name = EXCLUDED.name, payload_json = EXCLUDED.payload_json";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, node.getNodeId());
            statement.setString(2, node.getPathId());
            statement.setString(3, node.getParentId());
            if (node.getSortOrder() == null) statement.setNull(4, java.sql.Types.INTEGER);
            else statement.setInt(4, node.getSortOrder().intValue());
            statement.setString(5, node.getName());
            statement.setString(6, toJson(node));
            statement.executeUpdate();
            return node;
        } catch (SQLException e) {
            throw storageException("save admin career node", e);
        } finally {
            close(statement);
            close(connection);
        }
    }

    public boolean deleteCareerNode(String nodeId) {
        return deleteById("cc_admin_career_node", "node_id", nodeId);
    }

    public List<AdminQuestionDto> listQuestions() {
        return listPayloads("cc_admin_question", "created_at DESC, question_id DESC", AdminQuestionDto.class);
    }

    public AdminQuestionDto findQuestion(String questionId) {
        return findPayload("cc_admin_question", "question_id", questionId, AdminQuestionDto.class);
    }

    public AdminQuestionDto saveQuestion(AdminQuestionDto question) {
        if (question.getQuestionId() == null) question.setQuestionId(nextId("q"));
        if (question.getCreatedAt() == null) question.setCreatedAt(LocalDateTime.now());
        String sql = "INSERT INTO " + table("cc_admin_question")
                + " (question_id, source, review_status, status, position, payload_json, created_at)"
                + " VALUES (?, ?, ?, ?, ?, ?::jsonb, ?)"
                + " ON CONFLICT (question_id) DO UPDATE SET"
                + " source = EXCLUDED.source, review_status = EXCLUDED.review_status,"
                + " status = EXCLUDED.status, position = EXCLUDED.position, payload_json = EXCLUDED.payload_json";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, question.getQuestionId());
            statement.setString(2, question.getSource());
            statement.setString(3, question.getReviewStatus());
            statement.setString(4, question.getStatus());
            statement.setString(5, question.getPosition());
            statement.setString(6, toJson(question));
            statement.setTimestamp(7, timestamp(question.getCreatedAt()));
            statement.executeUpdate();
            return question;
        } catch (SQLException e) {
            throw storageException("save admin question", e);
        } finally {
            close(statement);
            close(connection);
        }
    }

    public boolean deleteQuestion(String questionId) {
        return deleteById("cc_admin_question", "question_id", questionId);
    }

    public List<AdminContentItemDto> listContent(String type) {
        List<AdminContentItemDto> out = new ArrayList<AdminContentItemDto>();
        String sql = "SELECT payload_json FROM " + table("cc_admin_content")
                + (PostgresqlStorageConfig.hasText(type) ? " WHERE type = ?" : "")
                + " ORDER BY pinned DESC, published_at DESC, content_id DESC";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            if (PostgresqlStorageConfig.hasText(type)) statement.setString(1, type.trim());
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                AdminContentItemDto item = readJson(resultSet.getString("payload_json"), AdminContentItemDto.class, null);
                if (item != null) out.add(item);
            }
            return out;
        } catch (SQLException e) {
            throw storageException("list admin content", e);
        } finally {
            close(resultSet);
            close(statement);
            close(connection);
        }
    }

    public AdminContentItemDto findContent(String contentId) {
        return findPayload("cc_admin_content", "content_id", contentId, AdminContentItemDto.class);
    }

    public AdminContentItemDto saveContent(AdminContentItemDto content) {
        if (content.getContentId() == null) content.setContentId(nextId("content"));
        if (content.getPublishedAt() == null) content.setPublishedAt(LocalDateTime.now());
        if (content.getType() == null) content.setType(AdminConstants.CONTENT_TYPE_ARTICLE);
        if (content.getPinned() == null) content.setPinned(Boolean.FALSE);
        if (content.getHidden() == null) content.setHidden(Boolean.FALSE);
        String sql = "INSERT INTO " + table("cc_admin_content")
                + " (content_id, type, title, pinned, hidden, payload_json, published_at)"
                + " VALUES (?, ?, ?, ?, ?, ?::jsonb, ?)"
                + " ON CONFLICT (content_id) DO UPDATE SET"
                + " type = EXCLUDED.type, title = EXCLUDED.title, pinned = EXCLUDED.pinned,"
                + " hidden = EXCLUDED.hidden, payload_json = EXCLUDED.payload_json,"
                + " published_at = EXCLUDED.published_at";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, content.getContentId());
            statement.setString(2, content.getType());
            statement.setString(3, content.getTitle());
            statement.setBoolean(4, Boolean.TRUE.equals(content.getPinned()));
            statement.setBoolean(5, Boolean.TRUE.equals(content.getHidden()));
            statement.setString(6, toJson(content));
            statement.setTimestamp(7, timestamp(content.getPublishedAt()));
            statement.executeUpdate();
            return content;
        } catch (SQLException e) {
            throw storageException("save admin content", e);
        } finally {
            close(statement);
            close(connection);
        }
    }

    public boolean deleteContent(String contentId) {
        return deleteById("cc_admin_content", "content_id", contentId);
    }

    public AdminAnalyticsSummaryDto analyticsSummary() {
        AdminAnalyticsSummaryDto summary = new AdminAnalyticsSummaryDto();
        summary.setTotalUsers(Integer.valueOf(count("cc_admin_user")));
        summary.setTotalInterviews(Integer.valueOf(countIfExists("cc_interview_session")));
        summary.setTotalAssessments(Integer.valueOf(countIfExists("cc_assessment_record")));
        summary.setTotalCheckIns(Integer.valueOf(0));
        summary.setSince(LocalDateTime.now().minusDays(30));
        Map<String, Integer> events = new LinkedHashMap<String, Integer>();
        events.put("USERS", summary.getTotalUsers());
        events.put("QUESTIONS", Integer.valueOf(count("cc_admin_question")));
        events.put("CONTENT", Integer.valueOf(count("cc_admin_content")));
        events.put("AUDIT", Integer.valueOf(count("cc_admin_audit_log")));
        summary.setEventBreakdown30d(events);
        return summary;
    }

    public List<AdminAuditLogDto> listAuditLogs() {
        return listPayloads("cc_admin_audit_log", "created_at DESC, audit_id DESC", AdminAuditLogDto.class);
    }

    public boolean saveAudit(AdminAuditLogDto auditLog) {
        if (auditLog.getAuditId() == null) auditLog.setAuditId(nextId("audit"));
        if (auditLog.getCreatedAt() == null) auditLog.setCreatedAt(LocalDateTime.now());
        String sql = "INSERT INTO " + table("cc_admin_audit_log")
                + " (audit_id, admin_id, action, target_type, target_id, payload_json, created_at)"
                + " VALUES (?, ?, ?, ?, ?, ?::jsonb, ?)";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, auditLog.getAuditId());
            statement.setString(2, auditLog.getAdminId());
            statement.setString(3, auditLog.getAction());
            statement.setString(4, auditLog.getTargetType());
            statement.setString(5, auditLog.getTargetId());
            statement.setString(6, toJson(auditLog));
            statement.setTimestamp(7, timestamp(auditLog.getCreatedAt()));
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw storageException("save admin audit log", e);
        } finally {
            close(statement);
            close(connection);
        }
    }

    public Map<String, List<AdminInterviewSummaryDto>> listInterviewsByUsers(List<AdminUserDto> users) {
        Map<String, List<AdminInterviewSummaryDto>> out = new LinkedHashMap<String, List<AdminInterviewSummaryDto>>();
        if (users == null) return out;
        for (AdminUserDto user : users) {
            out.put(user.getUserId(), listInterviewsByUser(user.getUserId()));
        }
        return out;
    }

    private <T> List<T> listPayloads(String tableName, String orderBy, Class<T> type) {
        String sql = "SELECT payload_json FROM " + table(tableName)
                + (PostgresqlStorageConfig.hasText(orderBy) ? " ORDER BY " + orderBy : "");
        List<T> out = new ArrayList<T>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                T item = readJson(resultSet.getString("payload_json"), type, null);
                if (item != null) out.add(item);
            }
            return out;
        } catch (SQLException e) {
            throw storageException("list " + tableName, e);
        } finally {
            close(resultSet);
            close(statement);
            close(connection);
        }
    }

    private <T> T findPayload(String tableName, String idColumn, String id, Class<T> type) {
        if (!PostgresqlStorageConfig.hasText(id)) return null;
        String sql = "SELECT payload_json FROM " + table(tableName) + " WHERE " + idColumn + " = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, id.trim());
            resultSet = statement.executeQuery();
            return resultSet.next() ? readJson(resultSet.getString("payload_json"), type, null) : null;
        } catch (SQLException e) {
            throw storageException("find " + tableName, e);
        } finally {
            close(resultSet);
            close(statement);
            close(connection);
        }
    }

    private void executePath(String sql, AdminCareerPathDto path) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, path.getPathId());
            statement.setString(2, path.getCode());
            statement.setString(3, path.getName());
            statement.setString(4, toJson(path));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw storageException("save admin career path", e);
        } finally {
            close(statement);
            close(connection);
        }
    }

    private boolean deleteById(String tableName, String idColumn, String id) {
        if (!PostgresqlStorageConfig.hasText(id)) return false;
        String sql = "DELETE FROM " + table(tableName) + " WHERE " + idColumn + " = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, id.trim());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw storageException("delete " + tableName, e);
        } finally {
            close(statement);
            close(connection);
        }
    }

    private int count(String tableName) {
        return count(tableName, false);
    }

    private int countIfExists(String tableName) {
        return count(tableName, true);
    }

    private int count(String tableName, boolean tolerateMissing) {
        String sql = "SELECT COUNT(*) FROM " + table(tableName);
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            return resultSet.next() ? resultSet.getInt(1) : 0;
        } catch (SQLException e) {
            if (tolerateMissing && isMissingTable(e)) return 0;
            throw storageException("count " + tableName, e);
        } finally {
            close(resultSet);
            close(statement);
            close(connection);
        }
    }

    private String nextId(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().replace("-", "");
    }

    private boolean isMissingTable(SQLException e) {
        return "42P01".equals(e.getSQLState());
    }

    private void initialize() {
        String[] sql = new String[] {
                "CREATE TABLE IF NOT EXISTS " + table("cc_admin_organization") + " ("
                        + "org_id VARCHAR(128) PRIMARY KEY,"
                        + "code VARCHAR(128),"
                        + "name VARCHAR(255),"
                        + "active BOOLEAN,"
                        + "payload_json JSONB NOT NULL DEFAULT '{}'::jsonb)",
                "CREATE TABLE IF NOT EXISTS " + table("cc_admin_user") + " ("
                        + "user_id VARCHAR(128) PRIMARY KEY,"
                        + "org_id VARCHAR(128),"
                        + "nickname VARCHAR(255),"
                        + "status VARCHAR(64),"
                        + "payload_json JSONB NOT NULL DEFAULT '{}'::jsonb,"
                        + "created_at TIMESTAMP NOT NULL DEFAULT now(),"
                        + "deleted_at TIMESTAMP)",
                "CREATE TABLE IF NOT EXISTS " + table("cc_admin_career_path") + " ("
                        + "path_id VARCHAR(128) PRIMARY KEY,"
                        + "code VARCHAR(128),"
                        + "name VARCHAR(255),"
                        + "payload_json JSONB NOT NULL DEFAULT '{}'::jsonb)",
                "CREATE TABLE IF NOT EXISTS " + table("cc_admin_career_node") + " ("
                        + "node_id VARCHAR(128) PRIMARY KEY,"
                        + "path_id VARCHAR(128),"
                        + "parent_id VARCHAR(128),"
                        + "sort_order INTEGER,"
                        + "name VARCHAR(255),"
                        + "payload_json JSONB NOT NULL DEFAULT '{}'::jsonb)",
                "CREATE TABLE IF NOT EXISTS " + table("cc_admin_question") + " ("
                        + "question_id VARCHAR(128) PRIMARY KEY,"
                        + "source VARCHAR(64),"
                        + "review_status VARCHAR(64),"
                        + "status VARCHAR(64),"
                        + "position VARCHAR(255),"
                        + "payload_json JSONB NOT NULL DEFAULT '{}'::jsonb,"
                        + "created_at TIMESTAMP NOT NULL DEFAULT now())",
                "CREATE TABLE IF NOT EXISTS " + table("cc_admin_content") + " ("
                        + "content_id VARCHAR(128) PRIMARY KEY,"
                        + "type VARCHAR(64),"
                        + "title VARCHAR(255),"
                        + "pinned BOOLEAN NOT NULL DEFAULT false,"
                        + "hidden BOOLEAN NOT NULL DEFAULT false,"
                        + "payload_json JSONB NOT NULL DEFAULT '{}'::jsonb,"
                        + "published_at TIMESTAMP NOT NULL DEFAULT now())",
                "CREATE TABLE IF NOT EXISTS " + table("cc_admin_audit_log") + " ("
                        + "audit_id VARCHAR(128) PRIMARY KEY,"
                        + "admin_id VARCHAR(128),"
                        + "action VARCHAR(128),"
                        + "target_type VARCHAR(128),"
                        + "target_id VARCHAR(128),"
                        + "payload_json JSONB NOT NULL DEFAULT '{}'::jsonb,"
                        + "created_at TIMESTAMP NOT NULL DEFAULT now())",
                "CREATE INDEX IF NOT EXISTS idx_cc_admin_user_org ON " + table("cc_admin_user") + " (org_id)",
                "CREATE INDEX IF NOT EXISTS idx_cc_admin_user_status ON " + table("cc_admin_user") + " (status)",
                "CREATE INDEX IF NOT EXISTS idx_cc_admin_question_review ON "
                        + table("cc_admin_question") + " (review_status, source)",
                "CREATE INDEX IF NOT EXISTS idx_cc_admin_content_type ON " + table("cc_admin_content") + " (type)",
                "CREATE INDEX IF NOT EXISTS idx_cc_admin_audit_created ON "
                        + table("cc_admin_audit_log") + " (created_at DESC)"
        };
        Connection connection = null;
        Statement statement = null;
        try {
            connection = connection();
            statement = connection.createStatement();
            for (String command : sql) {
                statement.execute(command);
            }
        } catch (SQLException e) {
            throw storageException("initialize admin governance schema", e);
        } finally {
            close(statement);
            close(connection);
        }
    }
}
