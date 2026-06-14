package v620.cc001.cloud01.app01.mservice;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import v620.base.helper.career.CareerProfileBuildService;
import v620.base.helper.career.CareerProfileSnapshotMergeService;
import v620.cc001.base.common.dto.career.CareerProfileDraftDto;
import v620.cc001.base.common.dto.career.CareerProfileInputsRequest;
import v620.cc001.base.common.dto.career.CareerProfileOnboardingRequest;
import v620.cc001.base.common.dto.career.CareerUserProfileDto;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class PostgresqlCareerProfileStorageLiveTest {

    private static final String LIVE_TEST_PROPERTY = "cc001.profile.postgresql.liveTest";

    private PostgresqlProfileStorageConfig config;
    private String userId;
    private String otherUserId;

    @BeforeEach
    void setUp() {
        config = PostgresqlProfileStorageConfig.fromSystemProperties();
        Assumptions.assumeTrue(Boolean.parseBoolean(configuredValue(LIVE_TEST_PROPERTY,
                        "CC001_PROFILE_POSTGRESQL_LIVE_TEST", "false")),
                "Set " + LIVE_TEST_PROPERTY + "=true to run PostgreSQL live storage tests.");
        Assumptions.assumeTrue(config.isComplete(),
                "PostgreSQL profile storage properties are required for live storage tests.");
        userId = "pg-live-" + UUID.randomUUID().toString();
        otherUserId = userId + "-other";
        cleanup(userId);
        cleanup(otherUserId);
    }

    @AfterEach
    void tearDown() {
        if (config != null && config.isComplete()) {
            cleanup(userId);
            cleanup(otherUserId);
        }
    }

    @Test
    void reloadsDraftSnapshotFactsAndProfileAcrossStorageInstances() {
        CareerProfileApplicationService first = service(new PostgresqlCareerProfileStorage(config));

        CareerProfileDraftDto draft = new CareerProfileDraftDto();
        draft.setIdentityType("student");
        draft.setTargetRole("AI Product Manager");
        first.saveDraft(userId, draft);

        CareerProfileOnboardingRequest onboarding = new CareerProfileOnboardingRequest();
        onboarding.setIdentityType("graduate");
        onboarding.setTargetRole("Data Analyst");
        onboarding.setHasResume("yes");
        first.saveOnboarding(userId, onboarding);

        CareerProfileInputsRequest facts = new CareerProfileInputsRequest();
        facts.setTargetCity("Shanghai");
        facts.setTargetIndustry("Software");
        facts.setWeeklyHours("8");
        first.saveProfileInputs(userId, facts);

        CareerProfileApplicationService second = service(new PostgresqlCareerProfileStorage(config));
        CareerProfileDraftDto reloadedDraft = second.getDraft(userId);
        UserProfileSnapshot reloadedSnapshot = second.getSnapshot(userId);
        CareerUserProfileDto reloadedProfile = second.getProfile(userId);

        assertEquals("student", reloadedDraft.getIdentityType());
        assertEquals("AI Product Manager", reloadedDraft.getTargetRole());
        assertNotNull(reloadedDraft.getUpdatedAt());
        assertEquals("graduate", reloadedSnapshot.getOnboarding().getIdentityType());
        assertEquals("Data Analyst", reloadedSnapshot.getPreferences().getTargetRole());
        assertEquals("Shanghai", new PostgresqlCareerProfileStorage(config).loadFacts(userId).get("target_city"));
        assertEquals("Software", new PostgresqlCareerProfileStorage(config).loadFacts(userId).get("target_industry"));
        assertNotNull(reloadedProfile);
        assertNotNull(reloadedProfile.getCurrentStage());
    }

    @Test
    void clearDraftDoesNotClearSnapshotAndUsersStayIsolated() {
        CareerProfileApplicationService service = service(new PostgresqlCareerProfileStorage(config));
        CareerProfileDraftDto draft = new CareerProfileDraftDto();
        draft.setTargetRole("Consultant");
        service.saveDraft(userId, draft);

        CareerProfileOnboardingRequest onboarding = new CareerProfileOnboardingRequest();
        onboarding.setIdentityType("owner");
        onboarding.setTargetRole("Backend Engineer");
        service.saveOnboarding(userId, onboarding);

        CareerProfileDraftDto otherDraft = new CareerProfileDraftDto();
        otherDraft.setTargetRole("Other Role");
        service.saveDraft(otherUserId, otherDraft);

        service.clearDraft(userId);

        assertNull(service.getDraft(userId).getTargetRole());
        assertEquals("owner", service.getSnapshot(userId).getOnboarding().getIdentityType());
        assertEquals("Other Role", service.getDraft(otherUserId).getTargetRole());
        assertNotEquals("Other Role", service.getDraft(userId).getTargetRole());
    }

    private CareerProfileApplicationService service(CareerProfileStorage storage) {
        return new CareerProfileApplicationService(storage,
                new CareerProfileSnapshotMergeService(),
                new CareerProfileBuildService());
    }

    private void cleanup(String targetUserId) {
        if (targetUserId == null || !config.isComplete()) {
            return;
        }
        String[] sql = new String[] {
                "DELETE FROM " + config.getSchema() + ".cc_profile_draft WHERE user_id = ?",
                "DELETE FROM " + config.getSchema() + ".cc_profile_snapshot WHERE user_id = ?",
                "DELETE FROM " + config.getSchema() + ".cc_profile_fact WHERE user_id = ?",
                "DELETE FROM " + config.getSchema() + ".cc_user_profile WHERE user_id = ?"
        };
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
            for (int i = 0; i < sql.length; i++) {
                close(statement);
                statement = connection.prepareStatement(sql[i]);
                statement.setString(1, targetUserId);
                statement.executeUpdate();
            }
        } catch (Exception e) {
            throw new IllegalStateException("Unable to clean PostgreSQL live profile test data", e);
        } finally {
            close(statement);
            close(connection);
        }
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

    private String configuredValue(String propertyName, String envName, String defaultValue) {
        String propertyValue = System.getProperty(propertyName);
        if (propertyValue != null && propertyValue.trim().length() > 0) {
            return propertyValue;
        }
        String envValue = System.getenv(envName);
        return envValue != null && envValue.trim().length() > 0 ? envValue : defaultValue;
    }
}
