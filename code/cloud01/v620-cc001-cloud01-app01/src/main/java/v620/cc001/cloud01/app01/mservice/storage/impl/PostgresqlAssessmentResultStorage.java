package v620.cc001.cloud01.app01.mservice.storage.impl;


import v620.cc001.cloud01.app01.mservice.storage.*;
import v620.cc001.base.common.dto.career.AssessmentScoreResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostgresqlAssessmentResultStorage extends PostgresqlStorageSupport implements AssessmentResultStorage {

    public PostgresqlAssessmentResultStorage(PostgresqlStorageConfig config) {
        super(config, "assessment result storage");
        if (config.isInitialize()) {
            initialize();
        }
    }

    public Long saveResult(String userId, AssessmentScoreResult result) {
        if (result == null) {
            throw new IllegalArgumentException("result is required");
        }
        String safeUserId = requireText(userId, "userId");
        AssessmentScoreResult copy = AssessmentResultCopies.copy(result);
        copy.setUserId(safeUserId);
        if (copy.getCreatedAt() == null) {
            copy.setCreatedAt(LocalDateTime.now());
        }
        String sql = "INSERT INTO " + table("cc_assessment_record")
                + " (user_id, scale_id, scale_title, status, result_summary, result_json,"
                + " answers_json, suggested_roles_json, payload_json, created_at)"
                + " VALUES (?, ?, ?, ?, ?, ?::jsonb, ?::jsonb, ?::jsonb, ?::jsonb, ?)";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet keys = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, safeUserId);
            if (copy.getScaleId() == null) {
                statement.setNull(2, java.sql.Types.BIGINT);
            } else {
                statement.setLong(2, copy.getScaleId().longValue());
            }
            statement.setString(3, copy.getScaleTitle());
            statement.setString(4, copy.getStatus());
            statement.setString(5, copy.getResultSummary());
            statement.setString(6, toJson(copy.getDimensionCounts()));
            statement.setString(7, toJson(copy.getAnswers()));
            statement.setString(8, toJson(copy.getSuggestedRoles()));
            statement.setString(9, toJson(copy));
            statement.setTimestamp(10, timestamp(copy.getCreatedAt()));
            statement.executeUpdate();
            keys = statement.getGeneratedKeys();
            Long recordId = keys.next() ? Long.valueOf(keys.getLong(1)) : null;
            copy.setRecordId(recordId);
            result.setRecordId(recordId);
            result.setUserId(copy.getUserId());
            result.setCreatedAt(copy.getCreatedAt());
            if (recordId != null) {
                updatePayload(recordId, copy);
            }
            return recordId;
        } catch (SQLException e) {
            throw storageException("save assessment result", e);
        } finally {
            close(keys);
            close(statement);
            close(connection);
        }
    }

    public AssessmentScoreResult loadResult(String userId, Long recordId) {
        String sql = "SELECT payload_json FROM " + table("cc_assessment_record")
                + " WHERE user_id = ? AND record_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, requireText(userId, "userId"));
            statement.setLong(2, recordId == null ? -1L : recordId.longValue());
            resultSet = statement.executeQuery();
            return resultSet.next()
                    ? readJson(resultSet.getString("payload_json"), AssessmentScoreResult.class, null)
                    : null;
        } catch (SQLException e) {
            throw storageException("load assessment result", e);
        } finally {
            close(resultSet);
            close(statement);
            close(connection);
        }
    }

    public void updateResult(String userId, AssessmentScoreResult result) {
        if (result == null || result.getRecordId() == null) {
            throw new IllegalArgumentException("assessment result recordId is required");
        }
        String sql = "UPDATE " + table("cc_assessment_record") + " SET payload_json = ?::jsonb WHERE user_id = ? AND record_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, toJson(result));
            statement.setString(2, requireText(userId, "userId"));
            statement.setLong(3, result.getRecordId().longValue());
            if (statement.executeUpdate() != 1) throw new IllegalArgumentException("assessment result does not exist or is not owned by user");
        } catch (SQLException e) {
            throw storageException("update assessment result", e);
        } finally {
            close(statement);
            close(connection);
        }
    }

    public List<AssessmentScoreResult> listResults(String userId) {
        String sql = "SELECT payload_json FROM " + table("cc_assessment_record")
                + " WHERE user_id = ? ORDER BY created_at DESC, record_id DESC";
        List<AssessmentScoreResult> out = new ArrayList<AssessmentScoreResult>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, requireText(userId, "userId"));
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                AssessmentScoreResult record = readJson(resultSet.getString("payload_json"),
                        AssessmentScoreResult.class, null);
                if (record != null) {
                    out.add(record);
                }
            }
            return out;
        } catch (SQLException e) {
            throw storageException("list assessment results", e);
        } finally {
            close(resultSet);
            close(statement);
            close(connection);
        }
    }

    private void updatePayload(Long recordId, AssessmentScoreResult result) throws SQLException {
        String sql = "UPDATE " + table("cc_assessment_record")
                + " SET payload_json = ?::jsonb WHERE record_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, toJson(result));
            statement.setLong(2, recordId.longValue());
            statement.executeUpdate();
        } finally {
            close(statement);
            close(connection);
        }
    }

    private void initialize() {
        String[] sql = new String[] {
                "CREATE TABLE IF NOT EXISTS " + table("cc_assessment_record") + " ("
                        + "record_id BIGSERIAL PRIMARY KEY,"
                        + "user_id VARCHAR(128) NOT NULL,"
                        + "scale_id BIGINT,"
                        + "scale_title VARCHAR(255),"
                        + "status VARCHAR(32),"
                        + "result_summary VARCHAR(128),"
                        + "result_json JSONB,"
                        + "answers_json JSONB,"
                        + "suggested_roles_json JSONB,"
                        + "payload_json JSONB NOT NULL,"
                        + "created_at TIMESTAMP NOT NULL DEFAULT now())",
                "CREATE INDEX IF NOT EXISTS idx_cc_assessment_record_user_time ON "
                        + table("cc_assessment_record") + " (user_id, created_at DESC)",
                "CREATE INDEX IF NOT EXISTS idx_cc_assessment_record_scale ON "
                        + table("cc_assessment_record") + " (scale_id)"
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
            throw storageException("initialize assessment result schema", e);
        } finally {
            close(statement);
            close(connection);
        }
    }
}
