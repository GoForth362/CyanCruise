package v620.cc001.cloud01.app01.mservice.storage.impl;

import v620.cc001.cloud01.app01.mservice.storage.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import v620.cc001.base.common.dto.career.CareerProfileDraftDto;
import v620.cc001.base.common.dto.career.CareerUserProfileDto;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * PostgreSQL implementation of the CyanCruise profile storage boundary.
 */
public class PostgresqlCareerProfileStorage implements CareerProfileStorage {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    private final PostgresqlProfileStorageConfig config;
    private final String schema;

    public PostgresqlCareerProfileStorage(PostgresqlProfileStorageConfig config) {
        if (config == null || !config.isComplete()) {
            throw new IllegalArgumentException("Complete PostgreSQL profile storage configuration is required");
        }
        loadDriver();
        this.config = config;
        this.schema = validateSchema(config.getSchema());
        if (config.isInitialize()) {
            initializeTables();
        }
    }

    public UserProfileSnapshot loadSnapshot(String userId) {
        String sql = "SELECT snapshot_json FROM " + table("cc_profile_snapshot") + " WHERE user_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, requireText(userId, "userId"));
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return readJson(resultSet.getString("snapshot_json"), UserProfileSnapshot.class, new UserProfileSnapshot());
            }
            return new UserProfileSnapshot();
        } catch (SQLException e) {
            throw storageException("load profile snapshot", e);
        } finally {
            close(resultSet);
            close(statement);
            close(connection);
        }
    }

    public void saveSnapshot(String userId, UserProfileSnapshot snapshot) {
        UserProfileSnapshot safeSnapshot = snapshot == null ? new UserProfileSnapshot() : snapshot;
        String sql = "INSERT INTO " + table("cc_profile_snapshot")
                + " (user_id, version, target_role, snapshot_json, created_at, updated_at)"
                + " VALUES (?, ?, ?, ?::jsonb, now(), now())"
                + " ON CONFLICT (user_id) DO UPDATE SET"
                + " version = EXCLUDED.version,"
                + " target_role = EXCLUDED.target_role,"
                + " snapshot_json = EXCLUDED.snapshot_json,"
                + " updated_at = now()";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, requireText(userId, "userId"));
            statement.setInt(2, safeSnapshot.getVersion() == null ? 1 : safeSnapshot.getVersion().intValue());
            statement.setString(3, snapshotTargetRole(safeSnapshot));
            statement.setString(4, toJson(safeSnapshot));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw storageException("save profile snapshot", e);
        } finally {
            close(statement);
            close(connection);
        }
    }

    public Map<String, String> loadFacts(String userId) {
        String sql = "SELECT fact_key, fact_value FROM " + table("cc_profile_fact")
                + " WHERE user_id = ? ORDER BY fact_key";
        Map<String, String> facts = new LinkedHashMap<String, String>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, requireText(userId, "userId"));
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                facts.put(resultSet.getString("fact_key"), resultSet.getString("fact_value"));
            }
            return facts;
        } catch (SQLException e) {
            throw storageException("load profile facts", e);
        } finally {
            close(resultSet);
            close(statement);
            close(connection);
        }
    }

    public void saveFact(String userId, String key, String value) {
        if (!hasText(key) || !hasText(value)) {
            return;
        }
        String sql = "INSERT INTO " + table("cc_profile_fact")
                + " (user_id, fact_key, fact_value, created_at, updated_at)"
                + " VALUES (?, ?, ?, now(), now())"
                + " ON CONFLICT (user_id, fact_key) DO UPDATE SET"
                + " fact_value = EXCLUDED.fact_value,"
                + " updated_at = now()";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, requireText(userId, "userId"));
            statement.setString(2, key.trim());
            statement.setString(3, value.trim());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw storageException("save profile fact", e);
        } finally {
            close(statement);
            close(connection);
        }
    }

    public CareerUserProfileDto loadProfile(String userId) {
        String sql = "SELECT profile_json FROM " + table("cc_user_profile") + " WHERE user_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, requireText(userId, "userId"));
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return readJson(resultSet.getString("profile_json"), CareerUserProfileDto.class, null);
            }
            return null;
        } catch (SQLException e) {
            throw storageException("load unified profile", e);
        } finally {
            close(resultSet);
            close(statement);
            close(connection);
        }
    }

    public void saveProfile(String userId, CareerUserProfileDto profile) {
        if (profile == null) {
            return;
        }
        String sql = "INSERT INTO " + table("cc_user_profile")
                + " (user_id, personalization_level, completeness_score, current_stage, target_role, profile_json, created_at, updated_at)"
                + " VALUES (?, ?, ?, ?, ?, ?::jsonb, now(), now())"
                + " ON CONFLICT (user_id) DO UPDATE SET"
                + " personalization_level = EXCLUDED.personalization_level,"
                + " completeness_score = EXCLUDED.completeness_score,"
                + " current_stage = EXCLUDED.current_stage,"
                + " target_role = EXCLUDED.target_role,"
                + " profile_json = EXCLUDED.profile_json,"
                + " updated_at = now()";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, requireText(userId, "userId"));
            statement.setString(2, profile.getPersonalizationLevel());
            if (profile.getCompletenessScore() == null) {
                statement.setNull(3, java.sql.Types.INTEGER);
            } else {
                statement.setInt(3, profile.getCompletenessScore().intValue());
            }
            statement.setString(4, profile.getCurrentStage());
            statement.setString(5, profileTargetRole(profile));
            statement.setString(6, toJson(profile));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw storageException("save unified profile", e);
        } finally {
            close(statement);
            close(connection);
        }
    }

    public CareerProfileDraftDto loadDraft(String userId) {
        String sql = "SELECT identity_type, education_stage, school, major, school_major, resume_status, target_role,"
                + " preference, experience_text, route_intent, draft_json, updated_at FROM "
                + table("cc_profile_draft") + " WHERE user_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, requireText(userId, "userId"));
            resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                return new CareerProfileDraftDto();
            }
            CareerProfileDraftDto draft = readJson(resultSet.getString("draft_json"),
                    CareerProfileDraftDto.class, new CareerProfileDraftDto());
            draft.setIdentityType(resultSet.getString("identity_type"));
            draft.setEducationStage(resultSet.getString("education_stage"));
            draft.setSchool(resultSet.getString("school"));
            draft.setMajor(firstText(resultSet.getString("major"), resultSet.getString("school_major")));
            draft.setSchoolMajor(resultSet.getString("school_major"));
            draft.setResumeStatus(resultSet.getString("resume_status"));
            draft.setTargetRole(resultSet.getString("target_role"));
            draft.setPreference(resultSet.getString("preference"));
            draft.setExperience(resultSet.getString("experience_text"));
            draft.setRouteIntent(resultSet.getString("route_intent"));
            Timestamp updatedAt = resultSet.getTimestamp("updated_at");
            if (updatedAt != null) {
                draft.setUpdatedAt(updatedAt.toLocalDateTime());
            }
            return draft;
        } catch (SQLException e) {
            throw storageException("load profile draft", e);
        } finally {
            close(resultSet);
            close(statement);
            close(connection);
        }
    }

    public void saveDraft(String userId, CareerProfileDraftDto draft) {
        CareerProfileDraftDto safeDraft = draft == null ? new CareerProfileDraftDto() : draft;
        String sql = "INSERT INTO " + table("cc_profile_draft")
                + " (user_id, identity_type, education_stage, school, major, school_major, resume_status, target_role,"
                + " preference, route_intent, experience_text, draft_json, created_at, updated_at)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?::jsonb, now(), now())"
                + " ON CONFLICT (user_id) DO UPDATE SET"
                + " identity_type = EXCLUDED.identity_type,"
                + " education_stage = EXCLUDED.education_stage,"
                + " school = EXCLUDED.school,"
                + " major = EXCLUDED.major,"
                + " school_major = EXCLUDED.school_major,"
                + " resume_status = EXCLUDED.resume_status,"
                + " target_role = EXCLUDED.target_role,"
                + " preference = EXCLUDED.preference,"
                + " route_intent = EXCLUDED.route_intent,"
                + " experience_text = EXCLUDED.experience_text,"
                + " draft_json = EXCLUDED.draft_json,"
                + " updated_at = now()";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, requireText(userId, "userId"));
            statement.setString(2, safeDraft.getIdentityType());
            statement.setString(3, safeDraft.getEducationStage());
            statement.setString(4, safeDraft.getSchool());
            statement.setString(5, safeDraft.getMajor());
            statement.setString(6, firstText(safeDraft.getSchoolMajor(), safeDraft.getMajor()));
            statement.setString(7, safeDraft.getResumeStatus());
            statement.setString(8, safeDraft.getTargetRole());
            statement.setString(9, safeDraft.getPreference());
            statement.setString(10, safeDraft.getRouteIntent());
            statement.setString(11, safeDraft.getExperience());
            statement.setString(12, toJson(safeDraft));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw storageException("save profile draft", e);
        } finally {
            close(statement);
            close(connection);
        }
    }

    public void clearDraft(String userId) {
        String sql = "DELETE FROM " + table("cc_profile_draft") + " WHERE user_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, requireText(userId, "userId"));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw storageException("clear profile draft", e);
        } finally {
            close(statement);
            close(connection);
        }
    }

    private void initializeTables() {
        String[] statements = new String[] {
                "CREATE TABLE IF NOT EXISTS " + table("cc_profile_draft") + " ("
                        + "user_id VARCHAR(128) PRIMARY KEY,"
                        + "identity_type VARCHAR(64), education_stage VARCHAR(64),"
                        + "school VARCHAR(255), major VARCHAR(255), school_major VARCHAR(255),"
                        + "resume_status VARCHAR(64), target_role VARCHAR(255), preference TEXT,"
                        + "route_intent VARCHAR(128), experience_text TEXT,"
                        + "draft_json JSONB NOT NULL DEFAULT '{}'::jsonb,"
                        + "created_at TIMESTAMPTZ NOT NULL DEFAULT now(),"
                        + "updated_at TIMESTAMPTZ NOT NULL DEFAULT now())",
                "CREATE TABLE IF NOT EXISTS " + table("cc_profile_snapshot") + " ("
                        + "user_id VARCHAR(128) PRIMARY KEY,"
                        + "version INTEGER NOT NULL DEFAULT 1, target_role VARCHAR(255),"
                        + "snapshot_json JSONB NOT NULL DEFAULT '{}'::jsonb,"
                        + "created_at TIMESTAMPTZ NOT NULL DEFAULT now(),"
                        + "updated_at TIMESTAMPTZ NOT NULL DEFAULT now())",
                "CREATE TABLE IF NOT EXISTS " + table("cc_profile_fact") + " ("
                        + "user_id VARCHAR(128) NOT NULL,"
                        + "fact_key VARCHAR(128) NOT NULL,"
                        + "fact_value TEXT,"
                        + "created_at TIMESTAMPTZ NOT NULL DEFAULT now(),"
                        + "updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),"
                        + "PRIMARY KEY (user_id, fact_key))",
                "CREATE TABLE IF NOT EXISTS " + table("cc_user_profile") + " ("
                        + "user_id VARCHAR(128) PRIMARY KEY,"
                        + "personalization_level VARCHAR(64), completeness_score INTEGER,"
                        + "current_stage VARCHAR(128), target_role VARCHAR(255),"
                        + "profile_json JSONB NOT NULL DEFAULT '{}'::jsonb,"
                        + "created_at TIMESTAMPTZ NOT NULL DEFAULT now(),"
                        + "updated_at TIMESTAMPTZ NOT NULL DEFAULT now())",
                "CREATE INDEX IF NOT EXISTS idx_cc_profile_draft_updated_at ON " + table("cc_profile_draft") + " (updated_at)",
                "CREATE INDEX IF NOT EXISTS idx_cc_profile_snapshot_target_role ON " + table("cc_profile_snapshot") + " (target_role)",
                "CREATE INDEX IF NOT EXISTS idx_cc_profile_snapshot_updated_at ON " + table("cc_profile_snapshot") + " (updated_at)",
                "CREATE INDEX IF NOT EXISTS idx_cc_profile_fact_key ON " + table("cc_profile_fact") + " (fact_key)",
                "CREATE INDEX IF NOT EXISTS idx_cc_user_profile_target_role ON " + table("cc_user_profile") + " (target_role)",
                "CREATE INDEX IF NOT EXISTS idx_cc_user_profile_updated_at ON " + table("cc_user_profile") + " (updated_at)",
                "ALTER TABLE " + table("cc_profile_draft") + " ADD COLUMN IF NOT EXISTS school VARCHAR(255)",
                "ALTER TABLE " + table("cc_profile_draft") + " ADD COLUMN IF NOT EXISTS major VARCHAR(255)"
        };
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connection();
            for (int i = 0; i < statements.length; i++) {
                close(statement);
                statement = connection.prepareStatement(statements[i]);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw storageException("initialize PostgreSQL profile tables", e);
        } finally {
            close(statement);
            close(connection);
        }
    }

    private Connection connection() throws SQLException {
        return DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
    }

    private void loadDriver() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("PostgreSQL JDBC driver is required for profile storage", e);
        }
    }

    private String table(String tableName) {
        return schema + "." + tableName;
    }

    private String firstText(String first, String second) {
        if (hasText(first)) {
            return first.trim();
        }
        return hasText(second) ? second.trim() : null;
    }

    private String toJson(Object value) {
        try {
            return MAPPER.writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to serialize profile payload", e);
        }
    }

    private <T> T readJson(String json, Class<T> type, T defaultValue) {
        if (!hasText(json)) {
            return defaultValue;
        }
        try {
            return MAPPER.readValue(json, type);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private String snapshotTargetRole(UserProfileSnapshot snapshot) {
        if (snapshot == null) {
            return null;
        }
        if (snapshot.getPreferences() != null && hasText(snapshot.getPreferences().getTargetRole())) {
            return snapshot.getPreferences().getTargetRole().trim();
        }
        if (snapshot.getResume() != null && hasText(snapshot.getResume().getTargetJob())) {
            return snapshot.getResume().getTargetJob().trim();
        }
        return null;
    }

    private String profileTargetRole(CareerUserProfileDto profile) {
        if (profile == null || profile.getTarget() == null || !hasText(profile.getTarget().getRole())) {
            return null;
        }
        return profile.getTarget().getRole().trim();
    }

    private String validateSchema(String value) {
        String safe = requireText(value, "schema");
        if (!safe.matches("[A-Za-z_][A-Za-z0-9_]*")) {
            throw new IllegalArgumentException("Invalid PostgreSQL schema name: " + safe);
        }
        return safe;
    }

    private String requireText(String value, String name) {
        if (!hasText(value)) {
            throw new IllegalArgumentException(name + " is required");
        }
        return value.trim();
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }

    private IllegalStateException storageException(String action, SQLException cause) {
        return new IllegalStateException("Unable to " + action + " using PostgreSQL profile storage", cause);
    }

    private void close(AutoCloseable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Exception ignored) {
        }
    }
}
