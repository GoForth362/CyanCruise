package v620.cc001.cloud01.app01.mservice.storage.impl;

import v620.cc001.cloud01.app01.mservice.storage.*;
import v620.cc001.base.common.dto.career.AssistantChatMessageDto;
import v620.cc001.base.common.dto.career.AssistantChatSessionDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PostgresqlAssistantChatStorage extends PostgresqlStorageSupport implements AssistantChatStorage {

    public PostgresqlAssistantChatStorage(PostgresqlStorageConfig config) {
        super(config, "assistant chat storage");
    }

    public AssistantChatSessionDto saveSession(AssistantChatSessionDto session) {
        if (session == null) {
            throw new IllegalArgumentException("session is required");
        }
        return session.getSessionId() == null ? insertSession(session) : upsertSession(session);
    }

    public AssistantChatSessionDto loadSession(Long sessionId) {
        String sql = "SELECT payload_json FROM " + table("cc_assistant_chat_session") + " WHERE session_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setLong(1, sessionId == null ? -1L : sessionId.longValue());
            resultSet = statement.executeQuery();
            return resultSet.next()
                    ? readJson(resultSet.getString("payload_json"), AssistantChatSessionDto.class, null)
                    : null;
        } catch (SQLException e) {
            throw storageException("load assistant chat session", e);
        } finally {
            close(resultSet);
            close(statement);
            close(connection);
        }
    }

    public List<AssistantChatSessionDto> listSessions(String userId) {
        String sql = "SELECT payload_json FROM " + table("cc_assistant_chat_session")
                + " WHERE user_id = ? ORDER BY updated_at DESC NULLS LAST, session_id DESC";
        List<AssistantChatSessionDto> result = new ArrayList<AssistantChatSessionDto>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, requireText(userId, "userId"));
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                AssistantChatSessionDto session = readJson(resultSet.getString("payload_json"),
                        AssistantChatSessionDto.class, null);
                if (session != null) {
                    result.add(session);
                }
            }
            return result;
        } catch (SQLException e) {
            throw storageException("list assistant chat sessions", e);
        } finally {
            close(resultSet);
            close(statement);
            close(connection);
        }
    }

    public void deleteSession(Long sessionId) {
        if (sessionId == null) {
            return;
        }
        String sql = "DELETE FROM " + table("cc_assistant_chat_session") + " WHERE session_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setLong(1, sessionId.longValue());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw storageException("delete assistant chat session", e);
        } finally {
            close(statement);
            close(connection);
        }
    }

    public AssistantChatMessageDto saveMessage(AssistantChatMessageDto message) {
        if (message == null) {
            throw new IllegalArgumentException("message is required");
        }
        return message.getMsgId() == null ? insertMessage(message) : upsertMessage(message);
    }

    public List<AssistantChatMessageDto> listMessages(Long sessionId) {
        String sql = "SELECT payload_json FROM " + table("cc_assistant_chat_message")
                + " WHERE session_id = ? ORDER BY created_at ASC NULLS LAST, msg_id ASC";
        List<AssistantChatMessageDto> result = new ArrayList<AssistantChatMessageDto>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setLong(1, sessionId == null ? -1L : sessionId.longValue());
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                AssistantChatMessageDto message = readJson(resultSet.getString("payload_json"),
                        AssistantChatMessageDto.class, null);
                if (message != null) {
                    result.add(message);
                }
            }
            return result;
        } catch (SQLException e) {
            throw storageException("list assistant chat messages", e);
        } finally {
            close(resultSet);
            close(statement);
            close(connection);
        }
    }

    public void deleteMessages(Long sessionId) {
        if (sessionId == null) {
            return;
        }
        String sql = "DELETE FROM " + table("cc_assistant_chat_message") + " WHERE session_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setLong(1, sessionId.longValue());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw storageException("delete assistant chat messages", e);
        } finally {
            close(statement);
            close(connection);
        }
    }

    private AssistantChatSessionDto insertSession(AssistantChatSessionDto session) {
        String sql = "INSERT INTO " + table("cc_assistant_chat_session")
                + " (user_id, persona, title, model_name, created_at, updated_at, payload_json)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?::jsonb)";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet keys = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            bindSession(statement, session, 1);
            statement.executeUpdate();
            keys = statement.getGeneratedKeys();
            if (keys.next()) {
                session.setSessionId(Long.valueOf(keys.getLong(1)));
                updatePayload("cc_assistant_chat_session", "session_id", session.getSessionId(), session);
            }
            return session;
        } catch (SQLException e) {
            throw storageException("insert assistant chat session", e);
        } finally {
            close(keys);
            close(statement);
            close(connection);
        }
    }

    private AssistantChatSessionDto upsertSession(AssistantChatSessionDto session) {
        String sql = "INSERT INTO " + table("cc_assistant_chat_session")
                + " (session_id, user_id, persona, title, model_name, created_at, updated_at, payload_json)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?::jsonb)"
                + " ON CONFLICT (session_id) DO UPDATE SET"
                + " user_id = EXCLUDED.user_id,"
                + " persona = EXCLUDED.persona,"
                + " title = EXCLUDED.title,"
                + " model_name = EXCLUDED.model_name,"
                + " created_at = EXCLUDED.created_at,"
                + " updated_at = EXCLUDED.updated_at,"
                + " payload_json = EXCLUDED.payload_json";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setLong(1, session.getSessionId().longValue());
            bindSession(statement, session, 2);
            statement.executeUpdate();
            return session;
        } catch (SQLException e) {
            throw storageException("upsert assistant chat session", e);
        } finally {
            close(statement);
            close(connection);
        }
    }

    private void bindSession(PreparedStatement statement, AssistantChatSessionDto session, int offset) throws SQLException {
        statement.setString(offset, requireText(session.getUserId(), "userId"));
        statement.setString(offset + 1, session.getPersona());
        statement.setString(offset + 2, session.getTitle());
        statement.setString(offset + 3, session.getModelName());
        statement.setTimestamp(offset + 4, timestamp(session.getCreatedAt()));
        statement.setTimestamp(offset + 5, timestamp(session.getUpdatedAt()));
        statement.setString(offset + 6, toJson(session));
    }

    private AssistantChatMessageDto insertMessage(AssistantChatMessageDto message) {
        String sql = "INSERT INTO " + table("cc_assistant_chat_message")
                + " (session_id, role, created_at, payload_json) VALUES (?, ?, ?, ?::jsonb)";
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
                message.setMsgId(Long.valueOf(keys.getLong(1)));
                updatePayload("cc_assistant_chat_message", "msg_id", message.getMsgId(), message);
            }
            return message;
        } catch (SQLException e) {
            throw storageException("insert assistant chat message", e);
        } finally {
            close(keys);
            close(statement);
            close(connection);
        }
    }

    private AssistantChatMessageDto upsertMessage(AssistantChatMessageDto message) {
        String sql = "INSERT INTO " + table("cc_assistant_chat_message")
                + " (msg_id, session_id, role, created_at, payload_json) VALUES (?, ?, ?, ?, ?::jsonb)"
                + " ON CONFLICT (msg_id) DO UPDATE SET"
                + " session_id = EXCLUDED.session_id,"
                + " role = EXCLUDED.role,"
                + " created_at = EXCLUDED.created_at,"
                + " payload_json = EXCLUDED.payload_json";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setLong(1, message.getMsgId().longValue());
            bindMessage(statement, message, 2);
            statement.executeUpdate();
            return message;
        } catch (SQLException e) {
            throw storageException("upsert assistant chat message", e);
        } finally {
            close(statement);
            close(connection);
        }
    }

    private void bindMessage(PreparedStatement statement, AssistantChatMessageDto message, int offset) throws SQLException {
        if (message.getSessionId() == null) {
            throw new IllegalArgumentException("sessionId is required");
        }
        statement.setLong(offset, message.getSessionId().longValue());
        statement.setString(offset + 1, message.getRole());
        statement.setTimestamp(offset + 2, timestamp(message.getCreatedAt()));
        statement.setString(offset + 3, toJson(message));
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
