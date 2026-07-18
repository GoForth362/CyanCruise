package v620.cc001.cloud01.app01.mservice.notification.impl;

import v620.cc001.base.common.dto.career.SubscriptionQuotaDto;
import v620.cc001.cloud01.app01.mservice.notification.SubscriptionQuotaStorage;
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

/**
 * PostgreSQL persistence for a user's subscription send quota.
 */
public class PostgresqlSubscriptionQuotaStorage extends PostgresqlStorageSupport implements SubscriptionQuotaStorage {

    public PostgresqlSubscriptionQuotaStorage(PostgresqlStorageConfig config) {
        super(config, "subscription quota storage");
        if (config.isInitialize()) {
            initialize();
        }
    }

    public SubscriptionQuotaDto find(String userId, String templateId) {
        String sql = "SELECT user_id, template_id, remaining, updated_at FROM " + table("cc_subscription_quota")
                + " WHERE user_id = ? AND template_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, requireText(userId, "userId"));
            statement.setString(2, requireText(templateId, "templateId"));
            resultSet = statement.executeQuery();
            return resultSet.next() ? readQuota(resultSet) : null;
        } catch (SQLException e) {
            throw storageException("find subscription quota", e);
        } finally {
            close(resultSet);
            close(statement);
            close(connection);
        }
    }

    public SubscriptionQuotaDto addQuota(String userId, String templateId, int delta) {
        String sql = "INSERT INTO " + table("cc_subscription_quota")
                + " (user_id, template_id, remaining, updated_at) VALUES (?, ?, ?, now())"
                + " ON CONFLICT (user_id, template_id) DO UPDATE SET"
                + " remaining = GREATEST(0, " + table("cc_subscription_quota") + ".remaining + EXCLUDED.remaining),"
                + " updated_at = now()"
                + " RETURNING user_id, template_id, remaining, updated_at";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, requireText(userId, "userId"));
            statement.setString(2, requireText(templateId, "templateId"));
            statement.setInt(3, delta);
            resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                throw new IllegalStateException("Unable to update subscription quota");
            }
            return readQuota(resultSet);
        } catch (SQLException e) {
            throw storageException("update subscription quota", e);
        } finally {
            close(resultSet);
            close(statement);
            close(connection);
        }
    }

    public SubscriptionQuotaDto consumeOne(String userId, String templateId) {
        String sql = "UPDATE " + table("cc_subscription_quota")
                + " SET remaining = remaining - 1, updated_at = now()"
                + " WHERE user_id = ? AND template_id = ? AND remaining > 0"
                + " RETURNING user_id, template_id, remaining, updated_at";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, requireText(userId, "userId"));
            statement.setString(2, requireText(templateId, "templateId"));
            resultSet = statement.executeQuery();
            return resultSet.next() ? readQuota(resultSet) : find(userId, templateId);
        } catch (SQLException e) {
            throw storageException("consume subscription quota", e);
        } finally {
            close(resultSet);
            close(statement);
            close(connection);
        }
    }

    public List<SubscriptionQuotaDto> listByUser(String userId) {
        String sql = "SELECT user_id, template_id, remaining, updated_at FROM " + table("cc_subscription_quota")
                + " WHERE user_id = ? ORDER BY template_id ASC";
        List<SubscriptionQuotaDto> result = new ArrayList<SubscriptionQuotaDto>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, requireText(userId, "userId"));
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(readQuota(resultSet));
            }
            return result;
        } catch (SQLException e) {
            throw storageException("list subscription quota", e);
        } finally {
            close(resultSet);
            close(statement);
            close(connection);
        }
    }

    private SubscriptionQuotaDto readQuota(ResultSet resultSet) throws SQLException {
        SubscriptionQuotaDto quota = new SubscriptionQuotaDto();
        quota.setUserId(resultSet.getString("user_id"));
        quota.setTemplateId(resultSet.getString("template_id"));
        quota.setRemaining(Integer.valueOf(resultSet.getInt("remaining")));
        Timestamp updatedAt = resultSet.getTimestamp("updated_at");
        if (updatedAt != null) {
            quota.setUpdatedAt(localDateTime(updatedAt));
        }
        return quota;
    }

    private void initialize() {
        String[] sql = new String[] {
                "CREATE TABLE IF NOT EXISTS " + table("cc_subscription_quota") + " ("
                        + "user_id VARCHAR(128) NOT NULL,"
                        + "template_id VARCHAR(128) NOT NULL,"
                        + "remaining INTEGER NOT NULL DEFAULT 0,"
                        + "updated_at TIMESTAMP NOT NULL DEFAULT now(),"
                        + "PRIMARY KEY (user_id, template_id))",
                "CREATE INDEX IF NOT EXISTS idx_cc_subscription_quota_user ON "
                        + table("cc_subscription_quota") + " (user_id, template_id)"
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
            throw storageException("initialize subscription quota storage", e);
        } finally {
            close(statement);
            close(connection);
        }
    }
}
