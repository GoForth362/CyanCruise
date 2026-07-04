package v620.cc001.cloud01.app01.mservice.storage.impl;

import v620.cc001.cloud01.app01.mservice.storage.*;
import v620.cc001.base.common.dto.career.ResumeRecordDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostgresqlResumeStorage extends PostgresqlStorageSupport implements ResumeStorage {

    public PostgresqlResumeStorage(PostgresqlStorageConfig config) {
        super(config, "resume storage");
    }

    public ResumeRecordDto save(ResumeRecordDto record) {
        if (record == null) {
            throw new IllegalArgumentException("record is required");
        }
        ResumeRecordDto copy = copy(record);
        LocalDateTime now = LocalDateTime.now();
        if (copy.getCreatedAt() == null) {
            copy.setCreatedAt(now);
        }
        copy.setUpdatedAt(now);
        return copy.getResumeId() == null ? insert(copy) : upsert(copy);
    }

    public ResumeRecordDto load(Long resumeId) {
        String sql = "SELECT payload_json FROM " + table("cc_resume_record") + " WHERE resume_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setLong(1, resumeId == null ? -1L : resumeId.longValue());
            resultSet = statement.executeQuery();
            return resultSet.next() ? readJson(resultSet.getString("payload_json"), ResumeRecordDto.class, null) : null;
        } catch (SQLException e) {
            throw storageException("load resume", e);
        } finally {
            close(resultSet);
            close(statement);
            close(connection);
        }
    }

    public List<ResumeRecordDto> listByUser(String userId) {
        String sql = "SELECT payload_json FROM " + table("cc_resume_record")
                + " WHERE user_id = ? ORDER BY updated_at DESC, resume_id DESC";
        List<ResumeRecordDto> records = new ArrayList<ResumeRecordDto>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, requireText(userId, "userId"));
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                ResumeRecordDto record = readJson(resultSet.getString("payload_json"), ResumeRecordDto.class, null);
                if (record != null) {
                    records.add(record);
                }
            }
            return records;
        } catch (SQLException e) {
            throw storageException("list resumes", e);
        } finally {
            close(resultSet);
            close(statement);
            close(connection);
        }
    }

    public void delete(Long resumeId) {
        if (resumeId == null) {
            return;
        }
        String sql = "DELETE FROM " + table("cc_resume_record") + " WHERE resume_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setLong(1, resumeId.longValue());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw storageException("delete resume", e);
        } finally {
            close(statement);
            close(connection);
        }
    }

    private ResumeRecordDto insert(ResumeRecordDto record) {
        String sql = "INSERT INTO " + table("cc_resume_record")
                + " (user_id, title, target_job, file_key, version, status, diagnosis_score, payload_json, created_at, updated_at)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?::jsonb, ?, ?)";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet keys = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            bindRecord(statement, record, 1, false);
            statement.executeUpdate();
            keys = statement.getGeneratedKeys();
            if (keys.next()) {
                record.setResumeId(Long.valueOf(keys.getLong(1)));
                updatePayload(record);
            }
            return copy(record);
        } catch (SQLException e) {
            throw storageException("insert resume", e);
        } finally {
            close(keys);
            close(statement);
            close(connection);
        }
    }

    private ResumeRecordDto upsert(ResumeRecordDto record) {
        String sql = "INSERT INTO " + table("cc_resume_record")
                + " (resume_id, user_id, title, target_job, file_key, version, status, diagnosis_score, payload_json, created_at, updated_at)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?::jsonb, ?, ?)"
                + " ON CONFLICT (resume_id) DO UPDATE SET"
                + " user_id = EXCLUDED.user_id,"
                + " title = EXCLUDED.title,"
                + " target_job = EXCLUDED.target_job,"
                + " file_key = EXCLUDED.file_key,"
                + " version = EXCLUDED.version,"
                + " status = EXCLUDED.status,"
                + " diagnosis_score = EXCLUDED.diagnosis_score,"
                + " payload_json = EXCLUDED.payload_json,"
                + " updated_at = EXCLUDED.updated_at";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setLong(1, record.getResumeId().longValue());
            bindRecord(statement, record, 2, true);
            statement.executeUpdate();
            return copy(record);
        } catch (SQLException e) {
            throw storageException("upsert resume", e);
        } finally {
            close(statement);
            close(connection);
        }
    }

    private void bindRecord(PreparedStatement statement, ResumeRecordDto record, int offset, boolean includesCreatedAt)
            throws SQLException {
        statement.setString(offset, requireText(record.getUserId(), "userId"));
        statement.setString(offset + 1, record.getTitle());
        statement.setString(offset + 2, record.getTargetJob());
        statement.setString(offset + 3, record.getFileKey());
        statement.setString(offset + 4, record.getVersion());
        statement.setString(offset + 5, record.getStatus());
        if (record.getDiagnosisScore() == null) {
            statement.setNull(offset + 6, java.sql.Types.INTEGER);
        } else {
            statement.setInt(offset + 6, record.getDiagnosisScore().intValue());
        }
        statement.setString(offset + 7, toJson(record));
        statement.setTimestamp(offset + 8, timestamp(record.getCreatedAt()));
        statement.setTimestamp(offset + 9, timestamp(record.getUpdatedAt()));
    }

    private void updatePayload(ResumeRecordDto record) throws SQLException {
        String sql = "UPDATE " + table("cc_resume_record") + " SET payload_json = ?::jsonb WHERE resume_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, toJson(record));
            statement.setLong(2, record.getResumeId().longValue());
            statement.executeUpdate();
        } finally {
            close(statement);
            close(connection);
        }
    }

    private ResumeRecordDto copy(ResumeRecordDto source) {
        if (source == null) {
            return null;
        }
        ResumeRecordDto copy = new ResumeRecordDto();
        copy.setResumeId(source.getResumeId());
        copy.setUserId(source.getUserId());
        copy.setTitle(source.getTitle());
        copy.setTargetJob(source.getTargetJob());
        copy.setFileKey(source.getFileKey());
        copy.setVersion(source.getVersion());
        copy.setStatus(source.getStatus());
        copy.setParsedContent(source.getParsedContent());
        copy.setDiagnosisScore(source.getDiagnosisScore());
        copy.setCreatedAt(source.getCreatedAt());
        copy.setUpdatedAt(source.getUpdatedAt());
        return copy;
    }
}
