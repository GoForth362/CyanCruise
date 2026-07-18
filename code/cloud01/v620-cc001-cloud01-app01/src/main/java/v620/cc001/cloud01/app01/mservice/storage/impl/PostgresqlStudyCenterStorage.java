package v620.cc001.cloud01.app01.mservice.storage.impl;

import v620.cc001.base.common.dto.furtherstudy.StudyCenterSelectionDto;
import v620.cc001.base.common.dto.career.AdminContentItemDto;
import v620.cc001.base.common.dto.career.CareerPlanRecordDto;
import v620.cc001.base.common.dto.career.CareerDailyTaskDto;
import v620.cc001.base.common.dto.furtherstudy.StudyPlanningMaterialDto;
import v620.cc001.cloud01.app01.mservice.storage.PostgresqlStorageConfig;
import v620.cc001.cloud01.app01.mservice.storage.StudyCenterStorage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;

/** PostgreSQL tables dedicated to study-center state; employment content is never read here. */
public class PostgresqlStudyCenterStorage extends PostgresqlStorageSupport implements StudyCenterStorage {
    public PostgresqlStudyCenterStorage(PostgresqlStorageConfig config) {
        super(config, "study center");
        initialize();
    }
    public StudyCenterSelectionDto loadSelection(String userId) {
        String sql = "SELECT direction, target_school, updated_at FROM " + table("cc_study_center_selection") + " WHERE user_id = ?";
        Connection c = null; PreparedStatement s = null; ResultSet r = null;
        try { c = connection(); s = c.prepareStatement(sql); s.setString(1, requireText(userId, "userId")); r = s.executeQuery();
            if (!r.next()) return null;
            StudyCenterSelectionDto dto = new StudyCenterSelectionDto(); dto.setUserId(userId.trim()); dto.setDirection(r.getString(1)); dto.setTargetSchool(r.getString(2)); dto.setUpdatedAt(localDateTime(r.getTimestamp(3))); return dto;
        } catch (SQLException e) { throw storageException("load study-center selection", e); }
        finally { close(r); close(s); close(c); }
    }
    public StudyCenterSelectionDto saveSelection(StudyCenterSelectionDto selection) {
        String sql = "INSERT INTO " + table("cc_study_center_selection") + " (user_id, direction, target_school, updated_at) VALUES (?, ?, ?, ?) " +
                "ON CONFLICT (user_id) DO UPDATE SET direction = EXCLUDED.direction, target_school = EXCLUDED.target_school, updated_at = EXCLUDED.updated_at";
        Connection c = null; PreparedStatement s = null;
        try { c = connection(); s = c.prepareStatement(sql); s.setString(1, requireText(selection.getUserId(), "userId")); s.setString(2, selection.getDirection()); s.setString(3, selection.getTargetSchool()); s.setTimestamp(4, timestamp(selection.getUpdatedAt())); s.executeUpdate(); return selection;
        } catch (SQLException e) { throw storageException("save study-center selection", e); }
        finally { close(s); close(c); }
    }
    public CareerPlanRecordDto loadPlan(String userId, String direction) {
        String sql = "SELECT plan_json::text FROM " + table("cc_study_center_plan") + " WHERE user_id = ? AND direction = ?";
        Connection c = null; PreparedStatement s = null; ResultSet r = null;
        try { c = connection(); s = c.prepareStatement(sql); s.setString(1, requireText(userId, "userId")); s.setString(2, requireText(direction, "direction")); r = s.executeQuery(); return r.next() ? readJson(r.getString(1), CareerPlanRecordDto.class, null) : null; }
        catch (SQLException e) { throw storageException("load study plan", e); } finally { close(r); close(s); close(c); }
    }
    public void savePlan(String userId, String direction, CareerPlanRecordDto plan) {
        String sql = "INSERT INTO " + table("cc_study_center_plan") + " (user_id, direction, plan_json, updated_at) VALUES (?, ?, CAST(? AS JSONB), now()) ON CONFLICT (user_id, direction) DO UPDATE SET plan_json = EXCLUDED.plan_json, updated_at = now()";
        Connection c = null; PreparedStatement s = null;
        try { c = connection(); s = c.prepareStatement(sql); s.setString(1, requireText(userId, "userId")); s.setString(2, requireText(direction, "direction")); s.setString(3, toJson(plan)); s.executeUpdate(); }
        catch (SQLException e) { throw storageException("save study plan", e); } finally { close(s); close(c); }
    }
    public void deletePlan(String userId, String direction) {
        String sql = "DELETE FROM " + table("cc_study_center_plan") + " WHERE user_id = ? AND direction = ?";
        Connection c = null; PreparedStatement s = null;
        try { c = connection(); s = c.prepareStatement(sql); s.setString(1, requireText(userId, "userId")); s.setString(2, requireText(direction, "direction")); s.executeUpdate(); }
        catch (SQLException e) { throw storageException("delete study plan", e); } finally { close(s); close(c); }
    }
    public void deleteDailyTasks(String userId, String direction) {
        String sql = "DELETE FROM " + table("cc_study_center_daily_task") + " WHERE user_id = ? AND direction = ?";
        Connection c = null; PreparedStatement s = null;
        try { c = connection(); s = c.prepareStatement(sql); s.setString(1, requireText(userId, "userId")); s.setString(2, requireText(direction, "direction")); s.executeUpdate(); }
        catch (SQLException e) { throw storageException("delete study daily tasks", e); } finally { close(s); close(c); }
    }
    public List<CareerDailyTaskDto> listDailyTasks(String userId, String direction) {
        String sql = "SELECT task_json::text FROM " + table("cc_study_center_daily_task") + " WHERE user_id = ? AND direction = ? ORDER BY plan_date, task_id";
        List<CareerDailyTaskDto> out = new ArrayList<CareerDailyTaskDto>(); Connection c = null; PreparedStatement s = null; ResultSet r = null;
        try { c = connection(); s = c.prepareStatement(sql); s.setString(1, requireText(userId, "userId")); s.setString(2, requireText(direction, "direction")); r = s.executeQuery(); while (r.next()) { CareerDailyTaskDto task = readJson(r.getString(1), CareerDailyTaskDto.class, null); if (task != null) out.add(task); } return out; }
        catch (SQLException e) { throw storageException("list study daily tasks", e); } finally { close(r); close(s); close(c); }
    }
    public CareerDailyTaskDto findDailyTask(String userId, String direction, String taskId) {
        String sql = "SELECT task_json::text FROM " + table("cc_study_center_daily_task") + " WHERE user_id = ? AND direction = ? AND task_id = ?";
        Connection c = null; PreparedStatement s = null; ResultSet r = null;
        try { c = connection(); s = c.prepareStatement(sql); s.setString(1, requireText(userId, "userId")); s.setString(2, requireText(direction, "direction")); s.setString(3, requireText(taskId, "taskId")); r = s.executeQuery(); return r.next() ? readJson(r.getString(1), CareerDailyTaskDto.class, null) : null; }
        catch (SQLException e) { throw storageException("find study daily task", e); } finally { close(r); close(s); close(c); }
    }
    public void saveDailyTask(String userId, String direction, CareerDailyTaskDto task) {
        String sql = "INSERT INTO " + table("cc_study_center_daily_task") + " (user_id, task_id, direction, plan_date, task_json, updated_at) VALUES (?, ?, ?, ?, CAST(? AS JSONB), now()) ON CONFLICT (user_id, direction, task_id) DO UPDATE SET plan_date = EXCLUDED.plan_date, task_json = EXCLUDED.task_json, updated_at = now()";
        Connection c = null; PreparedStatement s = null;
        try { c = connection(); s = c.prepareStatement(sql); s.setString(1, requireText(userId, "userId")); s.setString(2, requireText(task.getTaskId(), "taskId")); s.setString(3, requireText(direction, "direction")); s.setObject(4, task.getPlanDate()); s.setString(5, toJson(task)); s.executeUpdate(); }
        catch (SQLException e) { throw storageException("save study daily task", e); } finally { close(s); close(c); }
    }
    public StudyPlanningMaterialDto saveMaterial(String userId, StudyPlanningMaterialDto material) {
        String sql = "INSERT INTO " + table("cc_study_center_material")
                + " (material_id, user_id, direction, material_type, object_key, original_filename, extraction_status, payload_json, created_at, updated_at)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, CAST(? AS JSONB), ?, ?)"
                + " ON CONFLICT (material_id) DO UPDATE SET direction = EXCLUDED.direction,"
                + " material_type = EXCLUDED.material_type, object_key = EXCLUDED.object_key,"
                + " original_filename = EXCLUDED.original_filename, extraction_status = EXCLUDED.extraction_status,"
                + " payload_json = EXCLUDED.payload_json, updated_at = EXCLUDED.updated_at"
                + " WHERE " + table("cc_study_center_material") + ".user_id = EXCLUDED.user_id";
        Connection c = null; PreparedStatement s = null;
        try {
            c = connection(); s = c.prepareStatement(sql);
            s.setString(1, requireText(material.getMaterialId(), "materialId"));
            s.setString(2, requireText(userId, "userId"));
            s.setString(3, requireText(material.getDirection(), "direction"));
            s.setString(4, material.getMaterialType());
            s.setString(5, requireText(material.getObjectKey(), "objectKey"));
            s.setString(6, material.getOriginalFilename());
            s.setString(7, material.getExtractionStatus());
            s.setString(8, toJson(material));
            s.setTimestamp(9, timestamp(material.getCreatedAt()));
            s.setTimestamp(10, timestamp(material.getUpdatedAt()));
            s.executeUpdate();
            return material;
        } catch (SQLException e) { throw storageException("save study planning material", e); }
        finally { close(s); close(c); }
    }
    public StudyPlanningMaterialDto findMaterial(String userId, String direction, String materialId) {
        String sql = "SELECT payload_json::text FROM " + table("cc_study_center_material")
                + " WHERE user_id = ? AND direction = ? AND material_id = ?";
        Connection c = null; PreparedStatement s = null; ResultSet r = null;
        try {
            c = connection(); s = c.prepareStatement(sql);
            s.setString(1, requireText(userId, "userId"));
            s.setString(2, requireText(direction, "direction"));
            s.setString(3, requireText(materialId, "materialId"));
            r = s.executeQuery();
            return r.next() ? readJson(r.getString(1), StudyPlanningMaterialDto.class, null) : null;
        } catch (SQLException e) { throw storageException("find study planning material", e); }
        finally { close(r); close(s); close(c); }
    }
    public List<StudyPlanningMaterialDto> listMaterials(String userId, String direction) {
        StringBuilder sql = new StringBuilder("SELECT payload_json::text FROM ")
                .append(table("cc_study_center_material")).append(" WHERE user_id = ?");
        if (direction != null && direction.trim().length() > 0) sql.append(" AND direction = ?");
        sql.append(" ORDER BY updated_at DESC, material_id");
        List<StudyPlanningMaterialDto> out = new ArrayList<StudyPlanningMaterialDto>();
        Connection c = null; PreparedStatement s = null; ResultSet r = null;
        try {
            c = connection(); s = c.prepareStatement(sql.toString());
            s.setString(1, requireText(userId, "userId"));
            if (direction != null && direction.trim().length() > 0) s.setString(2, direction.trim());
            r = s.executeQuery();
            while (r.next()) {
                StudyPlanningMaterialDto material = readJson(r.getString(1), StudyPlanningMaterialDto.class, null);
                if (material != null) out.add(material);
            }
            return out;
        } catch (SQLException e) { throw storageException("list study planning materials", e); }
        finally { close(r); close(s); close(c); }
    }
    public boolean deleteMaterial(String userId, String direction, String materialId) {
        String sql = "DELETE FROM " + table("cc_study_center_material")
                + " WHERE user_id = ? AND direction = ? AND material_id = ?";
        Connection c = null; PreparedStatement s = null;
        try {
            c = connection(); s = c.prepareStatement(sql);
            s.setString(1, requireText(userId, "userId"));
            s.setString(2, requireText(direction, "direction"));
            s.setString(3, requireText(materialId, "materialId"));
            return s.executeUpdate() > 0;
        } catch (SQLException e) { throw storageException("delete study planning material", e); }
        finally { close(s); close(c); }
    }
    public List<AdminContentItemDto> listResources() {
        String sql = "SELECT payload_json::text, pinned, hidden, published_at FROM " + table("cc_study_center_resource") + " ORDER BY pinned DESC, published_at DESC";
        List<AdminContentItemDto> out = new ArrayList<AdminContentItemDto>(); Connection c = null; PreparedStatement s = null; ResultSet r = null;
        try { c = connection(); s = c.prepareStatement(sql); r = s.executeQuery(); while (r.next()) { AdminContentItemDto item = readJson(r.getString(1), AdminContentItemDto.class, null); if (item != null) { item.setPinned(Boolean.valueOf(r.getBoolean(2))); item.setHidden(Boolean.valueOf(r.getBoolean(3))); item.setPublishedAt(localDateTime(r.getTimestamp(4))); out.add(item); } } return out; }
        catch (SQLException e) { throw storageException("list study-center resources", e); } finally { close(r); close(s); close(c); }
    }
    public AdminContentItemDto findResource(String resourceId) { for (AdminContentItemDto item : listResources()) if (resourceId != null && resourceId.equals(item.getContentId())) return item; return null; }
    public AdminContentItemDto saveResource(AdminContentItemDto resource) {
        String sql = "INSERT INTO " + table("cc_study_center_resource") + " (resource_id, resource_type, payload_json, pinned, hidden, published_at) VALUES (?, ?, CAST(? AS JSONB), ?, ?, ?) ON CONFLICT (resource_id) DO UPDATE SET resource_type = EXCLUDED.resource_type, payload_json = EXCLUDED.payload_json, pinned = EXCLUDED.pinned, hidden = EXCLUDED.hidden, published_at = EXCLUDED.published_at";
        Connection c = null; PreparedStatement s = null; try { c = connection(); s = c.prepareStatement(sql); s.setString(1, requireText(resource.getContentId(), "resourceId")); s.setString(2, requireText(resource.getType(), "resourceType")); s.setString(3, toJson(resource)); s.setBoolean(4, Boolean.TRUE.equals(resource.getPinned())); s.setBoolean(5, Boolean.TRUE.equals(resource.getHidden())); s.setTimestamp(6, timestamp(resource.getPublishedAt())); s.executeUpdate(); return resource; } catch (SQLException e) { throw storageException("save study-center resource", e); } finally { close(s); close(c); }
    }
    public boolean deleteResource(String resourceId) { String sql = "DELETE FROM " + table("cc_study_center_resource") + " WHERE resource_id = ?"; Connection c = null; PreparedStatement s = null; try { c = connection(); s = c.prepareStatement(sql); s.setString(1, requireText(resourceId, "resourceId")); return s.executeUpdate() > 0; } catch (SQLException e) { throw storageException("delete study-center resource", e); } finally { close(s); close(c); } }
    private void initialize() {
        Connection c = null; Statement s = null;
        try { c = connection(); s = c.createStatement();
            s.execute("CREATE TABLE IF NOT EXISTS " + table("cc_study_center_selection") + " (user_id VARCHAR(128) PRIMARY KEY, direction VARCHAR(64) NOT NULL, target_school VARCHAR(255), updated_at TIMESTAMP NOT NULL)");
            s.execute("CREATE TABLE IF NOT EXISTS " + table("cc_study_center_resource") + " (resource_id VARCHAR(128) PRIMARY KEY, resource_type VARCHAR(64) NOT NULL, payload_json JSONB NOT NULL, pinned BOOLEAN NOT NULL DEFAULT FALSE, hidden BOOLEAN NOT NULL DEFAULT FALSE, published_at TIMESTAMP NOT NULL DEFAULT now())");
            s.execute("CREATE TABLE IF NOT EXISTS " + table("cc_study_center_plan") + " (user_id VARCHAR(128) NOT NULL, direction VARCHAR(64) NOT NULL, plan_json JSONB NOT NULL, updated_at TIMESTAMP NOT NULL DEFAULT now(), PRIMARY KEY (user_id, direction))");
            s.execute("CREATE TABLE IF NOT EXISTS " + table("cc_study_center_daily_task") + " (user_id VARCHAR(128) NOT NULL, task_id VARCHAR(255) NOT NULL, direction VARCHAR(64) NOT NULL, plan_date DATE, task_json JSONB NOT NULL, updated_at TIMESTAMP NOT NULL DEFAULT now(), PRIMARY KEY (user_id, direction, task_id))");
            s.execute("CREATE TABLE IF NOT EXISTS " + table("cc_study_center_material") + " (material_id VARCHAR(128) PRIMARY KEY, user_id VARCHAR(128) NOT NULL, direction VARCHAR(64) NOT NULL, material_type VARCHAR(64), object_key VARCHAR(1024) NOT NULL, original_filename VARCHAR(512), extraction_status VARCHAR(64), payload_json JSONB NOT NULL, created_at TIMESTAMP NOT NULL, updated_at TIMESTAMP NOT NULL)");
            s.execute("CREATE INDEX IF NOT EXISTS idx_cc_study_material_user_direction ON "
                    + table("cc_study_center_material") + " (user_id, direction, updated_at DESC)");
            migrateDirectionKeys(s, table("cc_study_center_plan"), "idx_cc_study_plan_user_direction", "user_id, direction");
            migrateDirectionKeys(s, table("cc_study_center_daily_task"), "idx_cc_study_daily_user_direction_task", "user_id, direction, task_id");
        } catch (SQLException e) { throw storageException("initialize study-center tables", e); }
        finally { close(s); close(c); }
    }
    private void migrateDirectionKeys(Statement statement, String tableName,
                                      String indexName, String columns) throws SQLException {
        statement.execute("UPDATE " + tableName + " SET direction = 'POSTGRADUATE' WHERE direction IS NULL OR btrim(direction) = ''");
        statement.execute("ALTER TABLE " + tableName + " ALTER COLUMN direction SET NOT NULL");
        statement.execute("DO $$ DECLARE pk_name text; BEGIN "
                + "SELECT conname INTO pk_name FROM pg_constraint WHERE conrelid = '" + tableName + "'::regclass "
                + "AND contype = 'p' AND NOT EXISTS (SELECT 1 FROM unnest(conkey) AS key_no(attnum) "
                + "JOIN pg_attribute attr ON attr.attrelid = conrelid AND attr.attnum = key_no.attnum "
                + "WHERE attr.attname = 'direction'); "
                + "IF pk_name IS NOT NULL THEN EXECUTE 'ALTER TABLE " + tableName
                + " DROP CONSTRAINT ' || quote_ident(pk_name); END IF; END $$");
        statement.execute("CREATE UNIQUE INDEX IF NOT EXISTS " + indexName + " ON " + tableName + " (" + columns + ")");
    }
}
