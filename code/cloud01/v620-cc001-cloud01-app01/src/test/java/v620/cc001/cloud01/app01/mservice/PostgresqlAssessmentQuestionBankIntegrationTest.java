package v620.cc001.cloud01.app01.mservice;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import v620.cc001.base.common.dto.career.AssessmentAttemptDto;
import v620.cc001.base.common.dto.career.AssessmentScaleDto;
import v620.cc001.cloud01.app01.mservice.storage.PostgresqlStorageConfig;
import v620.cc001.cloud01.app01.mservice.storage.impl.PostgresqlAssessmentAttemptStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.PostgresqlAssessmentCatalog;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PostgresqlAssessmentQuestionBankIntegrationTest {

    @Test
    void persistsCatalogConfigurationAndAttemptAcrossAdapterRestart() throws Exception {
        Assumptions.assumeTrue(Boolean.getBoolean("cc001.test.postgresql.integration"));
        PostgresqlStorageConfig source = PostgresqlStorageConfig.fromSystemProperties();
        Assumptions.assumeTrue(source.isComplete());
        String schema = "cc_assessment_test_" + Long.toHexString(System.nanoTime());
        createSchema(source, schema);
        try {
            PostgresqlStorageConfig config = copy(source, schema);
            PostgresqlAssessmentCatalog firstCatalog = new PostgresqlAssessmentCatalog(config);
            firstCatalog.saveAnswerQuestionCount(Long.valueOf(1001L), Integer.valueOf(4));

            AssessmentScaleDto selected = firstCatalog.loadScale(Long.valueOf(1001L));
            selected.setQuestions(selected.getQuestions().subList(0, 4));
            AssessmentAttemptDto attempt = new AssessmentAttemptDto();
            attempt.setAttemptId("restart-attempt");
            attempt.setUserId("restart-user");
            attempt.setScaleId(Long.valueOf(1001L));
            attempt.setStatus("IN_PROGRESS");
            attempt.setScale(selected);
            attempt.setCreatedAt(LocalDateTime.now());
            new PostgresqlAssessmentAttemptStorage(config).save(attempt);

            PostgresqlAssessmentCatalog restartedCatalog = new PostgresqlAssessmentCatalog(config);
            PostgresqlAssessmentAttemptStorage restartedAttempts = new PostgresqlAssessmentAttemptStorage(config);

            assertEquals(Integer.valueOf(4), restartedCatalog.loadScale(Long.valueOf(1001L)).getAnswerQuestionCount());
            assertNotNull(restartedAttempts.load("restart-user", "restart-attempt"));
            assertEquals(4, restartedAttempts.load("restart-user", "restart-attempt").getScale().getQuestions().size());
        } finally {
            dropSchema(source, schema);
        }
    }

    private PostgresqlStorageConfig copy(PostgresqlStorageConfig source, String schema) {
        PostgresqlStorageConfig config = new PostgresqlStorageConfig();
        config.setBackend("postgresql");
        config.setUrl(source.getUrl());
        config.setUsername(source.getUsername());
        config.setPassword(source.getPassword());
        config.setSchema(schema);
        config.setInitialize(true);
        return config;
    }

    private void createSchema(PostgresqlStorageConfig config, String schema) throws Exception {
        execute(config, "CREATE SCHEMA " + schema);
    }

    private void dropSchema(PostgresqlStorageConfig config, String schema) throws Exception {
        execute(config, "DROP SCHEMA IF EXISTS " + schema + " CASCADE");
    }

    private void execute(PostgresqlStorageConfig config, String sql) throws Exception {
        Class.forName("org.postgresql.Driver");
        Connection connection = null;
        Statement statement = null;
        try {
            connection = DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
            statement = connection.createStatement();
            statement.execute(sql);
        } finally {
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        }
    }
}
