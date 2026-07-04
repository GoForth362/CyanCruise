package v620.cc001.cloud01.app01.mservice.storage.impl;

import v620.cc001.cloud01.app01.mservice.storage.*;
import v620.cc001.base.common.dto.career.CareerPlanRecordDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PostgresqlCareerPlanStorage extends PostgresqlStorageSupport implements CareerPlanStorage {

    public PostgresqlCareerPlanStorage(PostgresqlStorageConfig config) {
        super(config, "career plan storage");
    }

    public CareerPlanRecordDto load(String userId) {
        String sql = "SELECT payload_json FROM " + table("cc_career_plan") + " WHERE user_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, requireText(userId, "userId"));
            resultSet = statement.executeQuery();
            return resultSet.next()
                    ? readJson(resultSet.getString("payload_json"), CareerPlanRecordDto.class, null)
                    : null;
        } catch (SQLException e) {
            throw storageException("load career plan", e);
        } finally {
            close(resultSet);
            close(statement);
            close(connection);
        }
    }

    public void save(String userId, CareerPlanRecordDto plan) {
        if (plan == null) {
            return;
        }
        String safeUserId = requireText(userId, "userId");
        plan.setUserId(safeUserId);
        String sql = "INSERT INTO " + table("cc_career_plan")
                + " (user_id, target_role, version, generated_from, payload_json, created_at, updated_at)"
                + " VALUES (?, ?, ?, ?, ?::jsonb, now(), now())"
                + " ON CONFLICT (user_id) DO UPDATE SET"
                + " target_role = EXCLUDED.target_role,"
                + " version = EXCLUDED.version,"
                + " generated_from = EXCLUDED.generated_from,"
                + " payload_json = EXCLUDED.payload_json,"
                + " updated_at = now()";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, safeUserId);
            statement.setString(2, plan.getTargetRole());
            statement.setInt(3, plan.getVersion() == null ? 1 : plan.getVersion().intValue());
            statement.setString(4, plan.getModelUsed());
            statement.setString(5, toJson(plan));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw storageException("save career plan", e);
        } finally {
            close(statement);
            close(connection);
        }
    }

    public boolean exists(String userId) {
        String sql = "SELECT 1 FROM " + table("cc_career_plan") + " WHERE user_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, requireText(userId, "userId"));
            resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            throw storageException("check career plan", e);
        } finally {
            close(resultSet);
            close(statement);
            close(connection);
        }
    }
}
