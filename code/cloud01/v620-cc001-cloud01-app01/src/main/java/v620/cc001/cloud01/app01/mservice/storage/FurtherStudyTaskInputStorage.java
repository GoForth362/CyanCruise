package v620.cc001.cloud01.app01.mservice.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Persists one current-user input snapshot in a dedicated table for each study task. */
public final class FurtherStudyTaskInputStorage {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Map<String, String> TABLES = tables();

    private FurtherStudyTaskInputStorage() { }

    public static Map<String, Object> saveAndLoad(String userId, String taskType, Map<String, Object> payload) {
        Map<String, Object> safePayload = payload == null ? new LinkedHashMap<String, Object>() : payload;
        PostgresqlStorageConfig config = PostgresqlStorageConfig.fromSystemProperties();
        String table = TABLES.get(taskType);
        if (!config.isPostgresqlBackend() || table == null || !PostgresqlStorageConfig.hasText(userId)) {
            return safePayload;
        }
        config.requireComplete("further-study task input");
        String schema = safeSchema(config.getSchema());
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = null;
            try {
                connection = DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
                ensureTable(connection, schema, table);
                save(connection, schema, table, userId.trim(), safePayload);
                Map<String, Object> restored = load(connection, schema, table, userId.trim());
                return restored == null ? safePayload : restored;
            } finally {
                close(connection);
            }
        } catch (Exception error) {
            throw new IllegalStateException("无法保存本次升学分析输入，请稍后重试。", error);
        }
    }

    private static void ensureTable(Connection connection, String schema, String table) throws SQLException {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS " + schema + "." + table
                    + " (user_id VARCHAR(128) PRIMARY KEY, payload_json JSONB NOT NULL, created_at TIMESTAMP NOT NULL DEFAULT now(), updated_at TIMESTAMP NOT NULL DEFAULT now())");
        } finally { close(statement); }
    }

    private static void save(Connection connection, String schema, String table, String userId, Map<String, Object> payload) throws Exception {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement("INSERT INTO " + schema + "." + table
                    + " (user_id,payload_json,created_at,updated_at) VALUES (?,CAST(? AS JSONB),now(),now())"
                    + " ON CONFLICT (user_id) DO UPDATE SET payload_json=EXCLUDED.payload_json,updated_at=now()");
            statement.setString(1, userId);
            statement.setString(2, MAPPER.writeValueAsString(payload));
            statement.executeUpdate();
        } finally { close(statement); }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> load(Connection connection, String schema, String table, String userId) throws Exception {
        PreparedStatement statement = null; ResultSet result = null;
        try {
            statement = connection.prepareStatement("SELECT payload_json::text FROM " + schema + "." + table + " WHERE user_id=?");
            statement.setString(1, userId); result = statement.executeQuery();
            if (!result.next()) return null;
            Object mapped = MAPPER.readValue(result.getString(1), LinkedHashMap.class);
            return mapped instanceof Map ? (Map<String, Object>) mapped : null;
        } finally { close(result); close(statement); }
    }

    private static Map<String, String> tables() {
        Map<String, String> out = new LinkedHashMap<String, String>();
        out.put("POSTGRADUATE_SCHOOL_RECOMMEND", "cc_fs_pg_school_input");
        out.put("POSTGRADUATE_PLAN_GENERATE", "cc_fs_pg_plan_input");
        out.put("POSTGRADUATE_MISTAKE_ANALYZE", "cc_fs_pg_mistake_input");
        out.put("POSTGRADUATE_REEXAM_PREPARE", "cc_fs_pg_reexam_input");
        out.put("RECOMMENDATION_DIAGNOSE", "cc_fs_rec_diagnose_input");
        out.put("RECOMMENDATION_PLAN_GENERATE", "cc_fs_rec_plan_input");
        out.put("RECOMMENDATION_DOCUMENT_POLISH", "cc_fs_rec_document_input");
        out.put("RECOMMENDATION_TUTOR_LETTER", "cc_fs_rec_tutor_input");
        out.put("STUDY_ABROAD_PROFILE_DIAGNOSE", "cc_fs_abroad_profile_input");
        out.put("STUDY_ABROAD_LANGUAGE_PLAN", "cc_fs_abroad_language_input");
        out.put("STUDY_ABROAD_SCHOOL_POSITION", "cc_fs_abroad_school_input");
        out.put("STUDY_ABROAD_STATEMENT_OUTLINE", "cc_fs_abroad_statement_input");
        out.put("STUDY_ABROAD_VISA_CHECKLIST", "cc_fs_abroad_visa_input");
        return Collections.unmodifiableMap(out);
    }

    private static String safeSchema(String value) {
        if (value == null || !value.matches("[A-Za-z_][A-Za-z0-9_]*")) throw new IllegalArgumentException("数据库 schema 配置无效。");
        return value;
    }
    private static void close(AutoCloseable value) { if (value != null) try { value.close(); } catch (Exception ignored) { } }
}
