package v620.cc001.cloud01.app01.mservice.storage.impl;

import v620.cc001.base.common.dto.career.CareerDailyTaskDto;
import v620.cc001.cloud01.app01.mservice.storage.CareerDailyTaskStorage;
import v620.cc001.cloud01.app01.mservice.storage.PostgresqlStorageConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PostgresqlCareerDailyTaskStorage extends PostgresqlStorageSupport
        implements CareerDailyTaskStorage {

    public PostgresqlCareerDailyTaskStorage(PostgresqlStorageConfig config) {
        super(config, "career daily task storage");
    }

    public List<CareerDailyTaskDto> list(String userId) {
        String sql = "SELECT task_id, task_key, title, due_date, status, priority, parent_task_id, sub_index, updated_at"
                + " FROM " + table("cc_career_task") + " WHERE user_id = ? ORDER BY due_date, sub_index";
        List<CareerDailyTaskDto> result = new ArrayList<CareerDailyTaskDto>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rows = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, requireText(userId, "userId"));
            rows = statement.executeQuery();
            while (rows.next()) result.add(read(rows));
            return result;
        } catch (SQLException e) {
            throw storageException("list career daily tasks", e);
        } finally {
            close(rows);
            close(statement);
            close(connection);
        }
    }

    public CareerDailyTaskDto find(String userId, String taskId) {
        String sql = "SELECT task_id, task_key, title, due_date, status, priority, parent_task_id, sub_index, updated_at"
                + " FROM " + table("cc_career_task") + " WHERE user_id = ? AND task_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rows = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, requireText(userId, "userId"));
            statement.setString(2, requireText(taskId, "taskId"));
            rows = statement.executeQuery();
            return rows.next() ? read(rows) : null;
        } catch (SQLException e) {
            throw storageException("find career daily task", e);
        } finally {
            close(rows);
            close(statement);
            close(connection);
        }
    }

    public void save(String userId, CareerDailyTaskDto task) {
        String sql = "INSERT INTO " + table("cc_career_task")
                + " (task_id, user_id, task_key, title, description, due_date, status, priority, parent_task_id, sub_index, updated_at)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())"
                + " ON CONFLICT (task_id) DO UPDATE SET status = EXCLUDED.status, title = EXCLUDED.title,"
                + " due_date = EXCLUDED.due_date, priority = EXCLUDED.priority, parent_task_id = EXCLUDED.parent_task_id,"
                + " sub_index = EXCLUDED.sub_index, updated_at = now()";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, task.getTaskId());
            statement.setString(2, requireText(userId, "userId"));
            statement.setString(3, task.getSourceTaskId());
            statement.setString(4, task.getText());
            statement.setString(5, task.getText());
            statement.setObject(6, task.getPlanDate());
            statement.setString(7, task.getStatus());
            statement.setInt(8, task.getSequence() == null ? 0 : task.getSequence().intValue());
            statement.setString(9, task.getPhaseId());
            statement.setInt(10, task.getSequence() == null ? 0 : task.getSequence().intValue());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw storageException("save career daily task", e);
        } finally {
            close(statement);
            close(connection);
        }
    }

    private CareerDailyTaskDto read(ResultSet rows) throws SQLException {
        CareerDailyTaskDto task = new CareerDailyTaskDto();
        task.setTaskId(rows.getString("task_id"));
        task.setSourceTaskId(rows.getString("task_key"));
        task.setText(rows.getString("title"));
        java.sql.Date dueDate = rows.getDate("due_date");
        task.setPlanDate(dueDate == null ? null : dueDate.toLocalDate());
        task.setStatus(rows.getString("status"));
        task.setSequence(Integer.valueOf(rows.getInt("sub_index")));
        task.setPhaseId(rows.getString("parent_task_id"));
        task.setPlanVersion(parsePlanVersion(task.getTaskId()));
        java.sql.Timestamp updatedAt = rows.getTimestamp("updated_at");
        task.setUpdatedAt(updatedAt == null ? null : updatedAt.toLocalDateTime());
        return task;
    }

    private Integer parsePlanVersion(String taskId) {
        if (taskId == null || !taskId.startsWith("daily-v")) return Integer.valueOf(1);
        int end = taskId.indexOf('-', 7);
        try { return Integer.valueOf(taskId.substring(7, end)); } catch (RuntimeException ignored) { return Integer.valueOf(1); }
    }
}
