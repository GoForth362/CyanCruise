package v620.cc001.cloud01.app01.mservice.storage.impl;

import v620.cc001.cloud01.app01.mservice.storage.*;
import v620.cc001.base.common.dto.career.InterviewMessageDto;
import v620.cc001.base.common.dto.career.InterviewSessionDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PostgresqlInterviewStorage extends PostgresqlStorageSupport implements InterviewStorage {

    public PostgresqlInterviewStorage(PostgresqlStorageConfig config) {
        super(config, "interview storage");
    }

    public InterviewSessionDto saveInterview(InterviewSessionDto interview) {
        if (interview == null) {
            throw new IllegalArgumentException("interview is required");
        }
        return interview.getInterviewId() == null ? insertInterview(interview) : upsertInterview(interview);
    }

    public InterviewSessionDto loadInterview(Long interviewId) {
        String sql = "SELECT payload_json FROM " + table("cc_interview_session") + " WHERE interview_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setLong(1, interviewId == null ? -1L : interviewId.longValue());
            resultSet = statement.executeQuery();
            return resultSet.next()
                    ? readJson(resultSet.getString("payload_json"), InterviewSessionDto.class, null)
                    : null;
        } catch (SQLException e) {
            throw storageException("load interview", e);
        } finally {
            close(resultSet);
            close(statement);
            close(connection);
        }
    }

    public List<InterviewSessionDto> listByUser(String userId) {
        String sql = "SELECT payload_json FROM " + table("cc_interview_session")
                + " WHERE user_id = ? ORDER BY started_at DESC NULLS LAST, interview_id DESC";
        List<InterviewSessionDto> result = new ArrayList<InterviewSessionDto>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, requireText(userId, "userId"));
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                InterviewSessionDto interview = readJson(resultSet.getString("payload_json"), InterviewSessionDto.class, null);
                if (interview != null) {
                    result.add(interview);
                }
            }
            return result;
        } catch (SQLException e) {
            throw storageException("list interviews", e);
        } finally {
            close(resultSet);
            close(statement);
            close(connection);
        }
    }

    public void deleteInterview(Long interviewId) {
        if (interviewId == null) {
            return;
        }
        String sql = "DELETE FROM " + table("cc_interview_session") + " WHERE interview_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setLong(1, interviewId.longValue());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw storageException("delete interview", e);
        } finally {
            close(statement);
            close(connection);
        }
    }

    public InterviewMessageDto saveMessage(InterviewMessageDto message) {
        if (message == null) {
            throw new IllegalArgumentException("message is required");
        }
        return message.getMessageId() == null ? insertMessage(message) : upsertMessage(message);
    }

    public List<InterviewMessageDto> listMessages(Long interviewId) {
        String sql = "SELECT payload_json FROM " + table("cc_interview_message")
                + " WHERE interview_id = ? ORDER BY created_at ASC NULLS LAST, message_id ASC";
        List<InterviewMessageDto> result = new ArrayList<InterviewMessageDto>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setLong(1, interviewId == null ? -1L : interviewId.longValue());
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                InterviewMessageDto message = readJson(resultSet.getString("payload_json"), InterviewMessageDto.class, null);
                if (message != null) {
                    result.add(message);
                }
            }
            return result;
        } catch (SQLException e) {
            throw storageException("list interview messages", e);
        } finally {
            close(resultSet);
            close(statement);
            close(connection);
        }
    }

    public void deleteMessages(Long interviewId) {
        if (interviewId == null) {
            return;
        }
        String sql = "DELETE FROM " + table("cc_interview_message") + " WHERE interview_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setLong(1, interviewId.longValue());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw storageException("delete interview messages", e);
        } finally {
            close(statement);
            close(connection);
        }
    }

    private InterviewSessionDto insertInterview(InterviewSessionDto interview) {
        String sql = "INSERT INTO " + table("cc_interview_session")
                + " (user_id, resume_id, target_role, status, mode, started_at, ended_at, final_score, payload_json, created_at, updated_at)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?::jsonb, now(), now())";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet keys = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            bindInterview(statement, interview, 1);
            statement.executeUpdate();
            keys = statement.getGeneratedKeys();
            if (keys.next()) {
                interview.setInterviewId(Long.valueOf(keys.getLong(1)));
                updateInterviewPayload(interview);
            }
            return interview;
        } catch (SQLException e) {
            throw storageException("insert interview", e);
        } finally {
            close(keys);
            close(statement);
            close(connection);
        }
    }

    private InterviewSessionDto upsertInterview(InterviewSessionDto interview) {
        String sql = "INSERT INTO " + table("cc_interview_session")
                + " (interview_id, user_id, resume_id, target_role, status, mode, started_at, ended_at, final_score, payload_json, created_at, updated_at)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?::jsonb, now(), now())"
                + " ON CONFLICT (interview_id) DO UPDATE SET"
                + " user_id = EXCLUDED.user_id,"
                + " resume_id = EXCLUDED.resume_id,"
                + " target_role = EXCLUDED.target_role,"
                + " status = EXCLUDED.status,"
                + " mode = EXCLUDED.mode,"
                + " started_at = EXCLUDED.started_at,"
                + " ended_at = EXCLUDED.ended_at,"
                + " final_score = EXCLUDED.final_score,"
                + " payload_json = EXCLUDED.payload_json,"
                + " updated_at = now()";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setLong(1, interview.getInterviewId().longValue());
            bindInterview(statement, interview, 2);
            statement.executeUpdate();
            return interview;
        } catch (SQLException e) {
            throw storageException("upsert interview", e);
        } finally {
            close(statement);
            close(connection);
        }
    }

    private void bindInterview(PreparedStatement statement, InterviewSessionDto interview, int offset) throws SQLException {
        statement.setString(offset, requireText(interview.getUserId(), "userId"));
        if (interview.getResumeId() == null) {
            statement.setNull(offset + 1, java.sql.Types.BIGINT);
        } else {
            statement.setLong(offset + 1, interview.getResumeId().longValue());
        }
        statement.setString(offset + 2, interview.getPositionName());
        statement.setString(offset + 3, interview.getStatus());
        statement.setString(offset + 4, interview.getMode());
        statement.setTimestamp(offset + 5, timestamp(interview.getStartedAt()));
        statement.setTimestamp(offset + 6, timestamp(interview.getEndedAt()));
        if (interview.getFinalScore() == null) {
            statement.setNull(offset + 7, java.sql.Types.INTEGER);
        } else {
            statement.setInt(offset + 7, interview.getFinalScore().intValue());
        }
        statement.setString(offset + 8, toJson(interview));
    }

    private InterviewMessageDto insertMessage(InterviewMessageDto message) {
        String sql = "INSERT INTO " + table("cc_interview_message")
                + " (interview_id, role, created_at, payload_json) VALUES (?, ?, ?, ?::jsonb)";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet keys = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            bindMessage(statement, message, 1);
            statement.executeUpdate();
            keys = statement.getGeneratedKeys();
            if (keys.next()) {
                message.setMessageId(Long.valueOf(keys.getLong(1)));
                updateMessagePayload(message);
            }
            return message;
        } catch (SQLException e) {
            throw storageException("insert interview message", e);
        } finally {
            close(keys);
            close(statement);
            close(connection);
        }
    }

    private InterviewMessageDto upsertMessage(InterviewMessageDto message) {
        String sql = "INSERT INTO " + table("cc_interview_message")
                + " (message_id, interview_id, role, created_at, payload_json) VALUES (?, ?, ?, ?, ?::jsonb)"
                + " ON CONFLICT (message_id) DO UPDATE SET"
                + " interview_id = EXCLUDED.interview_id,"
                + " role = EXCLUDED.role,"
                + " created_at = EXCLUDED.created_at,"
                + " payload_json = EXCLUDED.payload_json";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setLong(1, message.getMessageId().longValue());
            bindMessage(statement, message, 2);
            statement.executeUpdate();
            return message;
        } catch (SQLException e) {
            throw storageException("upsert interview message", e);
        } finally {
            close(statement);
            close(connection);
        }
    }

    private void bindMessage(PreparedStatement statement, InterviewMessageDto message, int offset) throws SQLException {
        if (message.getInterviewId() == null) {
            throw new IllegalArgumentException("interviewId is required");
        }
        statement.setLong(offset, message.getInterviewId().longValue());
        statement.setString(offset + 1, message.getRole());
        statement.setTimestamp(offset + 2, timestamp(message.getCreatedAt()));
        statement.setString(offset + 3, toJson(message));
    }

    private void updateInterviewPayload(InterviewSessionDto interview) throws SQLException {
        updatePayload("cc_interview_session", "interview_id", interview.getInterviewId(), interview);
    }

    private void updateMessagePayload(InterviewMessageDto message) throws SQLException {
        updatePayload("cc_interview_message", "message_id", message.getMessageId(), message);
    }

    private void updatePayload(String tableName, String idColumn, Long id, Object value) throws SQLException {
        String sql = "UPDATE " + table(tableName) + " SET payload_json = ?::jsonb WHERE " + idColumn + " = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, toJson(value));
            statement.setLong(2, id.longValue());
            statement.executeUpdate();
        } finally {
            close(statement);
            close(connection);
        }
    }
}
