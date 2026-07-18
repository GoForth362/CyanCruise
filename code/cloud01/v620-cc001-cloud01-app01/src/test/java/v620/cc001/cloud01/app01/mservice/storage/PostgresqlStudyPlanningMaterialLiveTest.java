package v620.cc001.cloud01.app01.mservice.storage;

import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import v620.cc001.base.common.dto.career.CareerRouteContext;
import v620.cc001.base.common.dto.furtherstudy.StudyPlanningMaterialDto;
import v620.cc001.cloud01.app01.mservice.storage.impl.PostgresqlStudyCenterStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PostgresqlStudyPlanningMaterialLiveTest {
    private static final String LIVE_TEST_PROPERTY = "cc001.study.postgresql.liveTest";

    private PostgresqlStudyCenterStorage storage;
    private String userId;
    private String otherUserId;
    private String materialId;

    @BeforeEach
    void setUp() {
        PostgresqlStorageConfig config = PostgresqlStorageConfig.fromSystemProperties();
        Assumptions.assumeTrue(Boolean.parseBoolean(configuredValue(LIVE_TEST_PROPERTY,
                        "CC001_STUDY_POSTGRESQL_LIVE_TEST", "false")),
                "Set " + LIVE_TEST_PROPERTY + "=true to run PostgreSQL study material tests.");
        Assumptions.assumeTrue(config.isComplete(),
                "PostgreSQL business storage properties are required for live storage tests.");
        storage = new PostgresqlStudyCenterStorage(config);
        userId = "study-material-" + UUID.randomUUID().toString();
        otherUserId = userId + "-other";
        materialId = "material-" + UUID.randomUUID().toString();
    }

    @AfterEach
    void tearDown() {
        if (storage != null && userId != null && materialId != null) {
            storage.deleteMaterial(userId, materialId);
        }
    }

    @Test
    void persistsListsAndDeletesMaterialWithUserIsolation() {
        StudyPlanningMaterialDto material = new StudyPlanningMaterialDto();
        material.setMaterialId(materialId);
        material.setUserId(userId);
        material.setDirection(CareerRouteContext.POSTGRADUATE);
        material.setMaterialType("TARGET_SCHOOL_NOTICE");
        material.setTitle("目标院校招生说明");
        material.setOriginalFilename("notice.txt");
        material.setObjectKey("study/" + materialId + "/notice.txt");
        material.setMediaType("text/plain");
        material.setSizeBytes(Long.valueOf(16L));
        material.setExtractionStatus("OK");
        material.setExtractedText("考试科目与复试要求");
        material.setExtractedCharCount(Integer.valueOf(9));
        material.setTruncated(Boolean.FALSE);
        material.setCreatedAt(LocalDateTime.now());
        material.setUpdatedAt(material.getCreatedAt());

        storage.saveMaterial(userId, material);

        assertEquals("目标院校招生说明", storage.findMaterial(userId, materialId).getTitle());
        assertEquals(1, storage.listMaterials(userId, CareerRouteContext.POSTGRADUATE).size());
        assertNull(storage.findMaterial(otherUserId, materialId));
        assertFalse(storage.deleteMaterial(otherUserId, materialId));
        assertTrue(storage.deleteMaterial(userId, materialId));
        assertNull(storage.findMaterial(userId, materialId));
    }

    private String configuredValue(String property, String environment, String fallback) {
        String value = System.getProperty(property);
        if (value == null || value.trim().isEmpty()) value = System.getenv(environment);
        return value == null || value.trim().isEmpty() ? fallback : value.trim();
    }
}
