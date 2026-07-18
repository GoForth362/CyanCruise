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
    public CareerPlanRecordDto loadPlan(String userId) {
        String sql = "SELECT plan_json::text FROM " + table("cc_study_center_plan") + " WHERE user_id = ?";
        Connection c = null; PreparedStatement s = null; ResultSet r = null;
        try { c = connection(); s = c.prepareStatement(sql); s.setString(1, requireText(userId, "userId")); r = s.executeQuery(); return r.next() ? readJson(r.getString(1), CareerPlanRecordDto.class, null) : null; }
        catch (SQLException e) { throw storageException("load study plan", e); } finally { close(r); close(s); close(c); }
    }
    public void savePlan(String userId, CareerPlanRecordDto plan) {
        String sql = "INSERT INTO " + table("cc_study_center_plan") + " (user_id, direction, plan_json, updated_at) VALUES (?, ?, CAST(? AS JSONB), now()) ON CONFLICT (user_id) DO UPDATE SET direction = EXCLUDED.direction, plan_json = EXCLUDED.plan_json, updated_at = now()";
        Connection c = null; PreparedStatement s = null;
        try { c = connection(); s = c.prepareStatement(sql); s.setString(1, requireText(userId, "userId")); s.setString(2, plan == null ? null : plan.getStudyDirection()); s.setString(3, toJson(plan)); s.executeUpdate(); }
        catch (SQLException e) { throw storageException("save study plan", e); } finally { close(s); close(c); }
    }
    public void deletePlan(String userId) {
        String sql = "DELETE FROM " + table("cc_study_center_plan") + " WHERE user_id = ?";
        Connection c = null; PreparedStatement s = null;
        try { c = connection(); s = c.prepareStatement(sql); s.setString(1, requireText(userId, "userId")); s.executeUpdate(); }
        catch (SQLException e) { throw storageException("delete study plan", e); } finally { close(s); close(c); }
    }
    public void deleteDailyTasks(String userId) {
        String sql = "DELETE FROM " + table("cc_study_center_daily_task") + " WHERE user_id = ?";
        Connection c = null; PreparedStatement s = null;
        try { c = connection(); s = c.prepareStatement(sql); s.setString(1, requireText(userId, "userId")); s.executeUpdate(); }
        catch (SQLException e) { throw storageException("delete study daily tasks", e); } finally { close(s); close(c); }
    }
    public List<CareerDailyTaskDto> listDailyTasks(String userId) {
        String sql = "SELECT task_json::text FROM " + table("cc_study_center_daily_task") + " WHERE user_id = ? ORDER BY plan_date, task_id";
        List<CareerDailyTaskDto> out = new ArrayList<CareerDailyTaskDto>(); Connection c = null; PreparedStatement s = null; ResultSet r = null;
        try { c = connection(); s = c.prepareStatement(sql); s.setString(1, requireText(userId, "userId")); r = s.executeQuery(); while (r.next()) { CareerDailyTaskDto task = readJson(r.getString(1), CareerDailyTaskDto.class, null); if (task != null) out.add(task); } return out; }
        catch (SQLException e) { throw storageException("list study daily tasks", e); } finally { close(r); close(s); close(c); }
    }
    public CareerDailyTaskDto findDailyTask(String userId, String taskId) {
        String sql = "SELECT task_json::text FROM " + table("cc_study_center_daily_task") + " WHERE user_id = ? AND task_id = ?";
        Connection c = null; PreparedStatement s = null; ResultSet r = null;
        try { c = connection(); s = c.prepareStatement(sql); s.setString(1, requireText(userId, "userId")); s.setString(2, requireText(taskId, "taskId")); r = s.executeQuery(); return r.next() ? readJson(r.getString(1), CareerDailyTaskDto.class, null) : null; }
        catch (SQLException e) { throw storageException("find study daily task", e); } finally { close(r); close(s); close(c); }
    }
    public void saveDailyTask(String userId, CareerDailyTaskDto task) {
        String sql = "INSERT INTO " + table("cc_study_center_daily_task") + " (user_id, task_id, direction, plan_date, task_json, updated_at) VALUES (?, ?, ?, ?, CAST(? AS JSONB), now()) ON CONFLICT (user_id, task_id) DO UPDATE SET direction = EXCLUDED.direction, plan_date = EXCLUDED.plan_date, task_json = EXCLUDED.task_json, updated_at = now()";
        Connection c = null; PreparedStatement s = null;
        try { c = connection(); s = c.prepareStatement(sql); s.setString(1, requireText(userId, "userId")); s.setString(2, requireText(task.getTaskId(), "taskId")); s.setString(3, task.getRouteType()); s.setObject(4, task.getPlanDate()); s.setString(5, toJson(task)); s.executeUpdate(); }
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
    public StudyPlanningMaterialDto findMaterial(String userId, String materialId) {
        String sql = "SELECT payload_json::text FROM " + table("cc_study_center_material")
                + " WHERE user_id = ? AND material_id = ?";
        Connection c = null; PreparedStatement s = null; ResultSet r = null;
        try {
            c = connection(); s = c.prepareStatement(sql);
            s.setString(1, requireText(userId, "userId"));
            s.setString(2, requireText(materialId, "materialId"));
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
    public boolean deleteMaterial(String userId, String materialId) {
        String sql = "DELETE FROM " + table("cc_study_center_material")
                + " WHERE user_id = ? AND material_id = ?";
        Connection c = null; PreparedStatement s = null;
        try {
            c = connection(); s = c.prepareStatement(sql);
            s.setString(1, requireText(userId, "userId"));
            s.setString(2, requireText(materialId, "materialId"));
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
            s.execute("CREATE TABLE IF NOT EXISTS " + table("cc_study_center_plan") + " (user_id VARCHAR(128) PRIMARY KEY, direction VARCHAR(64) NOT NULL, plan_json JSONB NOT NULL, updated_at TIMESTAMP NOT NULL DEFAULT now())");
            s.execute("CREATE TABLE IF NOT EXISTS " + table("cc_study_center_daily_task") + " (user_id VARCHAR(128) NOT NULL, task_id VARCHAR(255) NOT NULL, direction VARCHAR(64), plan_date DATE, task_json JSONB NOT NULL, updated_at TIMESTAMP NOT NULL DEFAULT now(), PRIMARY KEY (user_id, task_id))");
            s.execute("CREATE TABLE IF NOT EXISTS " + table("cc_study_center_material") + " (material_id VARCHAR(128) PRIMARY KEY, user_id VARCHAR(128) NOT NULL, direction VARCHAR(64) NOT NULL, material_type VARCHAR(64), object_key VARCHAR(1024) NOT NULL, original_filename VARCHAR(512), extraction_status VARCHAR(64), payload_json JSONB NOT NULL, created_at TIMESTAMP NOT NULL, updated_at TIMESTAMP NOT NULL)");
            s.execute("CREATE INDEX IF NOT EXISTS idx_cc_study_material_user_direction ON "
                    + table("cc_study_center_material") + " (user_id, direction, updated_at DESC)");
        } catch (SQLException e) { throw storageException("initialize study-center tables", e); }
        finally { close(s); close(c); }
    }
}
