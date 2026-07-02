package v620.cc001.cloud01.app01.mservice.notification.impl;

import v620.cc001.base.common.dto.career.NotificationRecordDto;
import v620.cc001.cloud01.app01.mservice.notification.NotificationStorage;
import v620.cc001.cloud01.app01.mservice.storage.PostgresqlStorageConfig;
import v620.cc001.cloud01.app01.mservice.storage.impl.PostgresqlStorageSupport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PostgresqlNotificationStorage extends PostgresqlStorageSupport implements NotificationStorage {

    private static final String STATUS_SENT = "sent";
    private static final String STATUS_READ = "read";
    private static final String STATUS_ARCHIVED = "archived";

    public PostgresqlNotificationStorage(PostgresqlStorageConfig config) {
        super(config, "notification storage");
        if (config.isInitialize()) {
            initialize();
        }
    }

    public NotificationRecordDto save(NotificationRecordDto notification) {
        if (notification == null) {
            throw new IllegalArgumentException("notification is required");
        }
        if (!hasText(notification.getNotificationId())) {
            notification.setNotificationId("notice-" + UUID.randomUUID().toString());
        }
        if (notification.getCreatedAt() == null) {
            notification.setCreatedAt(LocalDateTime.now());
        }
        upsert(notification);
        return notification;
    }

    public NotificationRecordDto find(String notificationId) {
        String sql = "SELECT payload_json, status, created_at, read_at FROM " + table("cc_notice")
                + " WHERE notice_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, requireText(notificationId, "notificationId"));
            resultSet = statement.executeQuery();
            return resultSet.next() ? readNotification(resultSet) : null;
        } catch (SQLException e) {
            throw storageException("find notification", e);
        } finally {
            close(resultSet);
            close(statement);
            close(connection);
        }
    }

    public List<NotificationRecordDto> listByUser(String userId) {
        String sql = "SELECT payload_json, status, created_at, read_at FROM " + table("cc_notice")
                + " WHERE user_id = ? AND status <> ?"
                + " ORDER BY created_at DESC NULLS LAST, notice_id DESC";
        List<NotificationRecordDto> result = new ArrayList<NotificationRecordDto>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, requireText(userId, "userId"));
            statement.setString(2, STATUS_ARCHIVED);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                NotificationRecordDto notification = readNotification(resultSet);
                if (notification != null) {
                    result.add(notification);
                }
            }
            return result;
        } catch (SQLException e) {
            throw storageException("list notifications", e);
        } finally {
            close(resultSet);
            close(statement);
            close(connection);
        }
    }

    public void delete(String notificationId) {
        String sql = "UPDATE " + table("cc_notice")
                + " SET status = ?, updated_at = now() WHERE notice_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, STATUS_ARCHIVED);
            statement.setString(2, requireText(notificationId, "notificationId"));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw storageException("archive notification", e);
        } finally {
            close(statement);
            close(connection);
        }
    }

    private void upsert(NotificationRecordDto notification) {
        String sql = "INSERT INTO " + table("cc_notice")
                + " (notice_id, user_id, notice_type, title, content, link_route, status, created_at, read_at, payload_json, updated_at)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?::jsonb, now())"
                + " ON CONFLICT (notice_id) DO UPDATE SET"
                + " user_id = EXCLUDED.user_id,"
                + " notice_type = EXCLUDED.notice_type,"
                + " title = EXCLUDED.title,"
                + " content = EXCLUDED.content,"
                + " link_route = EXCLUDED.link_route,"
                + " status = EXCLUDED.status,"
                + " created_at = EXCLUDED.created_at,"
                + " read_at = CASE WHEN EXCLUDED.status = '" + STATUS_READ
                + "' THEN COALESCE(EXCLUDED.read_at, now()) ELSE NULL END,"
                + " payload_json = EXCLUDED.payload_json,"
                + " updated_at = now()";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            bindNotification(statement, notification);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw storageException("save notification", e);
        } finally {
            close(statement);
            close(connection);
        }
    }

    private void bindNotification(PreparedStatement statement, NotificationRecordDto notification) throws SQLException {
        String status = Boolean.TRUE.equals(notification.getReadFlag()) ? STATUS_READ : STATUS_SENT;
        statement.setString(1, requireText(notification.getNotificationId(), "notificationId"));
        statement.setString(2, requireText(notification.getUserId(), "userId"));
        statement.setString(3, firstText(notification.getType(), "SYSTEM"));
        statement.setString(4, firstText(notification.getTitle(), "通知"));
        statement.setString(5, firstText(notification.getContent(), ""));
        statement.setString(6, notification.getLink());
        statement.setString(7, status);
        statement.setTimestamp(8, timestamp(notification.getCreatedAt()));
        statement.setTimestamp(9, STATUS_READ.equals(status) ? timestamp(LocalDateTime.now()) : null);
        statement.setString(10, toJson(notification));
    }

    private NotificationRecordDto readNotification(ResultSet resultSet) throws SQLException {
        NotificationRecordDto notification = readJson(resultSet.getString("payload_json"), NotificationRecordDto.class, null);
        if (notification == null) {
            return null;
        }
        String status = resultSet.getString("status");
        notification.setReadFlag(Boolean.valueOf(STATUS_READ.equalsIgnoreCase(status)));
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        if (createdAt != null) {
            notification.setCreatedAt(localDateTime(createdAt));
        }
        return notification;
    }

    private void initialize() {
        String[] sql = new String[] {
                "CREATE TABLE IF NOT EXISTS " + table("cc_notice") + " ("
                        + "notice_id VARCHAR(128) PRIMARY KEY,"
                        + "user_id VARCHAR(128) NOT NULL,"
                        + "notice_type VARCHAR(64) NOT NULL,"
                        + "title VARCHAR(255) NOT NULL,"
                        + "content TEXT NOT NULL,"
                        + "link_route VARCHAR(255),"
                        + "status VARCHAR(64) NOT NULL DEFAULT 'sent',"
                        + "admin_id VARCHAR(128),"
                        + "created_at TIMESTAMP,"
                        + "read_at TIMESTAMP,"
                        + "payload_json JSONB NOT NULL,"
                        + "updated_at TIMESTAMP NOT NULL DEFAULT now())",
                "CREATE INDEX IF NOT EXISTS idx_cc_notice_user_status_created ON "
                        + table("cc_notice") + " (user_id, status, created_at DESC)",
                "CREATE INDEX IF NOT EXISTS idx_cc_notice_type_created ON "
                        + table("cc_notice") + " (notice_type, created_at DESC)"
        };
        Connection connection = null;
        Statement statement = null;
        try {
            connection = connection();
            statement = connection.createStatement();
            for (int i = 0; i < sql.length; i += 1) {
                statement.execute(sql[i]);
            }
        } catch (SQLException e) {
            throw storageException("initialize notification storage", e);
        } finally {
            close(statement);
            close(connection);
        }
    }

    private String firstText(String first, String fallback) {
        return hasText(first) ? first.trim() : fallback;
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
