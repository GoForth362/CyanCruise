package v620.cc001.cloud01.app01.mservice;

import v620.cc001.base.common.dto.career.ResumeDiagnosisResultDto;
import v620.cc001.base.common.dto.career.ResumeKeywordStatusDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
            statement = connection.prepareStatement(sql);
            statement.setLong(1, result.getResumeId().longValue());
            if (result.getOverallScore() == null) {
                statement.setNull(2, java.sql.Types.INTEGER);
            } else {
                statement.setInt(2, result.getOverallScore().intValue());
            }
            statement.setString(3, toJson(result));
            statement.executeUpdate();
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
}
