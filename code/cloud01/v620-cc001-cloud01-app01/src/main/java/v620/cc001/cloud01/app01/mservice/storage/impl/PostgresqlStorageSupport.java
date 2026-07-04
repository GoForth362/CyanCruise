package v620.cc001.cloud01.app01.mservice.storage.impl;

import v620.cc001.cloud01.app01.mservice.storage.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public abstract class PostgresqlStorageSupport {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    private final PostgresqlStorageConfig config;
    private final String schema;
    private final String storageName;

    protected PostgresqlStorageSupport(PostgresqlStorageConfig config, String storageName) {
        if (config == null) {
            throw new IllegalArgumentException("PostgreSQL configuration is required");
        }
        config.requireComplete(storageName);
        loadDriver(storageName);
        this.config = config;
        this.schema = validateSchema(config.getSchema());
        this.storageName = storageName;
    }

    protected Connection connection() throws SQLException {
        return DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
    }

    protected String table(String tableName) {
        return schema + "." + tableName;
    }

    protected String toJson(Object value) {
        try {
            return MAPPER.writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to serialize " + storageName + " payload", e);
        }
    }

    protected <T> T readJson(String json, Class<T> type, T defaultValue) {
        if (!PostgresqlStorageConfig.hasText(json)) {
            return defaultValue;
        }
        try {
            return MAPPER.readValue(json, type);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    protected String requireText(String value, String name) {
        if (!PostgresqlStorageConfig.hasText(value)) {
            throw new IllegalArgumentException(name + " is required");
        }
        return value.trim();
    }

    protected Timestamp timestamp(LocalDateTime value) {
        return value == null ? null : Timestamp.valueOf(value);
    }

    protected LocalDateTime localDateTime(Timestamp value) {
        return value == null ? null : value.toLocalDateTime();
    }

    protected IllegalStateException storageException(String action, SQLException cause) {
        return new IllegalStateException("Unable to " + action + " using PostgreSQL " + storageName, cause);
    }

    protected void close(AutoCloseable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Exception ignored) {
        }
    }

    private void loadDriver(String storageName) {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("PostgreSQL JDBC driver is required for " + storageName, e);
        }
    }

    private String validateSchema(String value) {
        String safe = requireText(value, "schema");
        if (!safe.matches("[A-Za-z_][A-Za-z0-9_]*")) {
            throw new IllegalArgumentException("Invalid PostgreSQL schema name: " + safe);
        }
        return safe;
    }
}
