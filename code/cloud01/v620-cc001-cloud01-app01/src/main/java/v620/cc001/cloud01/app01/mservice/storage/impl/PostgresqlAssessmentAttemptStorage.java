package v620.cc001.cloud01.app01.mservice.storage.impl;

import v620.cc001.base.common.dto.career.AssessmentAttemptDto;
import v620.cc001.cloud01.app01.mservice.storage.AssessmentAttemptStorage;
import v620.cc001.cloud01.app01.mservice.storage.PostgresqlStorageConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

public class PostgresqlAssessmentAttemptStorage extends PostgresqlStorageSupport implements AssessmentAttemptStorage {

    public PostgresqlAssessmentAttemptStorage(PostgresqlStorageConfig config) {
        super(config, "assessment attempt storage");
        if (config.isInitialize()) initialize();
    }

    public void save(AssessmentAttemptDto attempt) {
        String sql = "INSERT INTO " + table("cc_assessment_attempt")
                + " (attempt_id, user_id, scale_id, status, payload_json, created_at, completed_at)"
                + " VALUES (?, ?, ?, ?, ?::jsonb, ?, ?)";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, requireText(attempt.getAttemptId(), "attemptId"));
            statement.setString(2, requireText(attempt.getUserId(), "userId"));
            statement.setLong(3, attempt.getScaleId().longValue());
            statement.setString(4, attempt.getStatus());
            statement.setString(5, toJson(attempt));
            statement.setTimestamp(6, timestamp(attempt.getCreatedAt()));
            statement.setTimestamp(7, timestamp(attempt.getCompletedAt()));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw storageException("save assessment attempt", e);
        } finally {
            close(statement); close(connection);
        }
    }

    public AssessmentAttemptDto load(String userId, String attemptId) {
        String sql = "SELECT payload_json FROM " + table("cc_assessment_attempt")
                + " WHERE user_id = ? AND attempt_id = ?";
        return queryOne(sql, requireText(userId, "userId"), requireText(attemptId, "attemptId"));
    }

    public AssessmentAttemptDto latest(String userId, Long scaleId) {
        String sql = "SELECT payload_json FROM " + table("cc_assessment_attempt")
                + " WHERE user_id = ? AND scale_id = ? ORDER BY created_at DESC LIMIT 1";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, requireText(userId, "userId"));
            statement.setLong(2, scaleId == null ? -1L : scaleId.longValue());
            resultSet = statement.executeQuery();
            return resultSet.next() ? readJson(resultSet.getString(1), AssessmentAttemptDto.class, null) : null;
        } catch (SQLException e) {
            throw storageException("load latest assessment attempt", e);
        } finally {
            close(resultSet); close(statement); close(connection);
        }
    }

    public void complete(String userId, String attemptId) {
        AssessmentAttemptDto attempt = load(userId, attemptId);
        if (attempt == null) throw new IllegalArgumentException("测评作答记录不存在或无权访问");
        if ("COMPLETED".equals(attempt.getStatus())) throw new IllegalArgumentException("该测评已经提交，请重新开始测评");
        attempt.setStatus("COMPLETED");
        attempt.setCompletedAt(LocalDateTime.now());
        String sql = "UPDATE " + table("cc_assessment_attempt")
                + " SET status = ?, payload_json = ?::jsonb, completed_at = ? WHERE user_id = ? AND attempt_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, attempt.getStatus());
            statement.setString(2, toJson(attempt));
            statement.setTimestamp(3, timestamp(attempt.getCompletedAt()));
            statement.setString(4, userId);
            statement.setString(5, attemptId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw storageException("complete assessment attempt", e);
        } finally {
            close(statement); close(connection);
        }
    }

    private AssessmentAttemptDto queryOne(String sql, String userId, String attemptId) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, userId);
            statement.setString(2, attemptId);
            resultSet = statement.executeQuery();
            return resultSet.next() ? readJson(resultSet.getString(1), AssessmentAttemptDto.class, null) : null;
        } catch (SQLException e) {
            throw storageException("load assessment attempt", e);
        } finally {
            close(resultSet); close(statement); close(connection);
        }
    }

    private void initialize() {
        String[] sql = new String[] {
                "CREATE TABLE IF NOT EXISTS " + table("cc_assessment_attempt") + " ("
                        + "attempt_id VARCHAR(64) PRIMARY KEY, user_id VARCHAR(128) NOT NULL, scale_id BIGINT NOT NULL,"
                        + "status VARCHAR(32) NOT NULL, payload_json JSONB NOT NULL, created_at TIMESTAMP NOT NULL, completed_at TIMESTAMP)",
                "CREATE INDEX IF NOT EXISTS idx_cc_assessment_attempt_user_scale ON "
                        + table("cc_assessment_attempt") + " (user_id, scale_id, created_at DESC)"
        };
        Connection connection = null;
        Statement statement = null;
        try {
            connection = connection();
            statement = connection.createStatement();
            for (String command : sql) statement.execute(command);
        } catch (SQLException e) {
            throw storageException("initialize assessment attempt schema", e);
        } finally {
            close(statement); close(connection);
        }
    }
}
