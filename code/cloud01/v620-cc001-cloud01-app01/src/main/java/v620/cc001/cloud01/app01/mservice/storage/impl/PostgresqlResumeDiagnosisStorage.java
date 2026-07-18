package v620.cc001.cloud01.app01.mservice.storage.impl;

import v620.cc001.cloud01.app01.mservice.storage.*;
import v620.cc001.base.common.dto.career.ResumeDiagnosisResultDto;
import v620.cc001.base.common.dto.career.ResumeKeywordStatusDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostgresqlResumeDiagnosisStorage extends PostgresqlStorageSupport implements ResumeDiagnosisStorage {

    public PostgresqlResumeDiagnosisStorage(PostgresqlStorageConfig config) {
        super(config, "resume diagnosis storage");
    }

    public ResumeDiagnosisResultDto saveDiagnosis(ResumeDiagnosisResultDto result) {
        if (result == null || result.getResumeId() == null) {
            return result;
        }
        String sql = "INSERT INTO " + table("cc_resume_diagnosis")
                + " (resume_id, overall_score, payload_json, updated_at)"
                + " VALUES (?, ?, ?::jsonb, now())"
                + " ON CONFLICT (resume_id) DO UPDATE SET"
                + " overall_score = EXCLUDED.overall_score,"
                + " payload_json = EXCLUDED.payload_json,"
                + " updated_at = now()";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connection();
            ensureHistoryTable(connection);
            statement = connection.prepareStatement(sql);
            statement.setLong(1, result.getResumeId().longValue());
            if (result.getOverallScore() == null) {
                statement.setNull(2, java.sql.Types.INTEGER);
            } else {
                statement.setInt(2, result.getOverallScore().intValue());
            }
            statement.setString(3, toJson(result));
            statement.executeUpdate();
            close(statement);
            statement = connection.prepareStatement("INSERT INTO " + table("cc_resume_diagnosis_history")
                    + " (user_id, resume_id, target_job, overall_score, payload_json, created_at)"
                    + " VALUES (?, ?, ?, ?, ?::jsonb, ?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, result.getUserId());
            statement.setLong(2, result.getResumeId().longValue());
            statement.setString(3, result.getTargetJob());
            if (result.getOverallScore() == null) statement.setNull(4, java.sql.Types.INTEGER);
            else statement.setInt(4, result.getOverallScore().intValue());
            statement.setString(5, toJson(result));
            statement.setTimestamp(6, java.sql.Timestamp.valueOf(result.getDiagnosedAt() == null ? LocalDateTime.now() : result.getDiagnosedAt()));
            statement.executeUpdate();
            ResultSet keys = statement.getGeneratedKeys();
            if (keys.next()) result.setDiagnosisId(Long.valueOf(keys.getLong(1)));
            close(keys);
            return result;
        } catch (SQLException e) {
            throw storageException("save resume diagnosis", e);
        } finally {
            close(statement);
            close(connection);
        }
    }

    public ResumeDiagnosisResultDto loadDiagnosis(Long resumeId) {
        String sql = "SELECT payload_json FROM " + table("cc_resume_diagnosis") + " WHERE resume_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setLong(1, resumeId == null ? -1L : resumeId.longValue());
            resultSet = statement.executeQuery();
            return resultSet.next()
                    ? readJson(resultSet.getString("payload_json"), ResumeDiagnosisResultDto.class, null)
                    : null;
        } catch (SQLException e) {
            throw storageException("load resume diagnosis", e);
        } finally {
            close(resultSet);
            close(statement);
            close(connection);
        }
    }

    public List<ResumeDiagnosisResultDto> listDiagnoses(String userId, Long resumeId) {
        String sql = "SELECT diagnosis_id, payload_json, created_at FROM " + table("cc_resume_diagnosis_history")
                + " WHERE user_id = ? AND resume_id = ? ORDER BY created_at DESC, diagnosis_id DESC";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<ResumeDiagnosisResultDto> result = new ArrayList<ResumeDiagnosisResultDto>();
        try {
            connection = connection();
            ensureHistoryTable(connection);
            statement = connection.prepareStatement(sql);
            statement.setString(1, userId);
            statement.setLong(2, resumeId == null ? -1L : resumeId.longValue());
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                ResumeDiagnosisResultDto item = readJson(resultSet.getString("payload_json"), ResumeDiagnosisResultDto.class, null);
                if (item != null) {
                    item.setDiagnosisId(Long.valueOf(resultSet.getLong("diagnosis_id")));
                    item.setDiagnosedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                    result.add(item);
                }
            }
            return result;
        } catch (SQLException e) {
            throw storageException("list resume diagnosis history", e);
        } finally {
            close(resultSet);
            close(statement);
            close(connection);
        }
    }

    public boolean deleteDiagnosis(String userId, Long diagnosisId) {
        if (diagnosisId == null) return false;
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connection();
            ensureHistoryTable(connection);
            statement = connection.prepareStatement("DELETE FROM " + table("cc_resume_diagnosis_history")
                    + " WHERE diagnosis_id = ? AND user_id = ?");
            statement.setLong(1, diagnosisId.longValue());
            statement.setString(2, userId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw storageException("delete resume diagnosis history", e);
        } finally {
            close(statement);
            close(connection);
        }
    }

    public ResumeKeywordStatusDto saveKeywordStatus(ResumeKeywordStatusDto status) {
        if (status == null || status.getResumeId() == null) {
            return status;
        }
        String sql = "INSERT INTO " + table("cc_resume_keyword_status")
                + " (resume_id, status, payload_json, updated_at)"
                + " VALUES (?, ?, ?::jsonb, now())"
                + " ON CONFLICT (resume_id) DO UPDATE SET"
                + " status = EXCLUDED.status,"
                + " payload_json = EXCLUDED.payload_json,"
                + " updated_at = now()";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setLong(1, status.getResumeId().longValue());
            statement.setString(2, status.getStatus());
            statement.setString(3, toJson(status));
            statement.executeUpdate();
            return status;
        } catch (SQLException e) {
            throw storageException("save resume keyword status", e);
        } finally {
            close(statement);
            close(connection);
        }
    }

    public ResumeKeywordStatusDto loadKeywordStatus(Long resumeId) {
        String sql = "SELECT payload_json FROM " + table("cc_resume_keyword_status") + " WHERE resume_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setLong(1, resumeId == null ? -1L : resumeId.longValue());
            resultSet = statement.executeQuery();
            return resultSet.next()
                    ? readJson(resultSet.getString("payload_json"), ResumeKeywordStatusDto.class, null)
                    : null;
        } catch (SQLException e) {
            throw storageException("load resume keyword status", e);
        } finally {
            close(resultSet);
            close(statement);
            close(connection);
        }
    }

    private void ensureHistoryTable(Connection connection) throws SQLException {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS " + table("cc_resume_diagnosis_history")
                    + " (diagnosis_id BIGSERIAL PRIMARY KEY, user_id VARCHAR(128) NOT NULL, resume_id BIGINT NOT NULL,"
                    + " target_job TEXT, overall_score INTEGER, payload_json JSONB NOT NULL,"
                    + " created_at TIMESTAMP NOT NULL DEFAULT now())");
            statement.execute("CREATE INDEX IF NOT EXISTS idx_cc_resume_diagnosis_history_user_resume"
                    + " ON " + table("cc_resume_diagnosis_history") + " (user_id, resume_id, created_at DESC)");
        } finally {
            close(statement);
        }
    }
}
