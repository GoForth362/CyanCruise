package v620.cc001.cloud01.app01.mservice.datamodel;

import org.junit.jupiter.api.Test;
import v620.cc001.cloud01.app01.mservice.storage.CareerProfileStorageFactory;
import v620.cc001.cloud01.app01.mservice.storage.PostgresqlStorageConfig;
import v620.cc001.cloud01.app01.mservice.storage.CyanCruiseStorageFactory;
import v620.cc001.cloud01.app01.mservice.storage.impl.CosmicCareerProfileStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.CosmicResumeStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerPlanStorage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CosmicBusinessObjectStorageTest {

    @Test
    void mapsCoreAndAdminObjectsToPlatformCodes() {
        assertEquals("v620_cc_user_profile",
                CyanCruiseBusinessModelMapping.toPlatformObject(CyanCruiseDatamodelObjects.USER_PROFILE));
        assertEquals("v620_cc_resume_record",
                CyanCruiseBusinessModelMapping.toPlatformObject(CyanCruiseDatamodelObjects.RESUME));
        assertEquals("v620_cc_notice",
                CyanCruiseBusinessModelMapping.toPlatformObject(CyanCruiseDatamodelObjects.NOTICE));
        assertEquals("v620_cc_admin_audit",
                CyanCruiseBusinessModelMapping.toPlatformObject(CyanCruiseDatamodelObjects.ADMIN_AUDIT));
    }

    @Test
    void mapsLogicalFieldsToPlatformFields() {
        assertEquals("fk_v620_userid", CyanCruiseBusinessModelMapping.toPlatformField("user_id"));
        assertEquals("fk_v620_resumeid", CyanCruiseBusinessModelMapping.toPlatformField("resume_id"));
        assertEquals("fk_v620_noticeid", CyanCruiseBusinessModelMapping.toPlatformField("notice_id"));
        assertEquals("fk_v620_reviewstatus", CyanCruiseBusinessModelMapping.toPlatformField("review_status"));
        assertEquals("fk_v620_useragent", CyanCruiseBusinessModelMapping.toPlatformField("user_agent"));
    }

    @Test
    void mappedGatewaySavesAndLoadsThroughBusinessObjectClient() {
        FakeCosmicBusinessObjectClient client = new FakeCosmicBusinessObjectClient();
        CosmicDatamodelGateway gateway = new MappedCosmicDatamodelGateway(
                new CosmicBusinessObjectDatamodelGateway(client));

        CosmicDatamodelRecord saved = gateway.save(new CosmicDatamodelRecord(CyanCruiseDatamodelObjects.RESUME)
                .set(CyanCruiseDatamodelObjects.USER_ID, "u-1")
                .set("resume_id", Long.valueOf(77L))
                .set("title", "产品经理简历"));

        assertEquals(Long.valueOf(1L), saved.get(CyanCruiseDatamodelObjects.ID));
        assertEquals("产品经理简历", saved.get("title"));

        CosmicDatamodelRecord platformRow = client.first("v620_cc_resume_record");
        assertNotNull(platformRow);
        assertEquals("u-1", platformRow.get("fk_v620_userid"));
        assertEquals(Long.valueOf(77L), platformRow.get("fk_v620_resumeid"));

        CosmicDatamodelRecord loaded = gateway.load(CyanCruiseDatamodelObjects.RESUME, Long.valueOf(1L));
        assertEquals("u-1", loaded.get(CyanCruiseDatamodelObjects.USER_ID));
        assertEquals(Long.valueOf(77L), loaded.get("resume_id"));
    }

    @Test
    void cosmicModuleConfigurationIsExplicit() {
        PostgresqlStorageConfig config = new PostgresqlStorageConfig();
        config.setBackend("cosmic");
        config.setCosmicModules("profile,resume");

        assertTrue(config.isCosmicModuleEnabled("profile"));
        assertTrue(config.isCosmicModuleEnabled("resume"));
        assertFalse(config.isCosmicModuleEnabled("notification"));
    }

    @Test
    void factoriesSelectCosmicStorageForEnabledModules() {
        String oldBackend = System.getProperty(PostgresqlStorageConfig.BACKEND_PROPERTY);
        String oldModules = System.getProperty(PostgresqlStorageConfig.COSMIC_MODULES_PROPERTY);
        String oldClient = System.getProperty(PostgresqlStorageConfig.COSMIC_CLIENT_CLASS_PROPERTY);
        try {
            System.setProperty(PostgresqlStorageConfig.BACKEND_PROPERTY, "cosmic");
            System.setProperty(PostgresqlStorageConfig.COSMIC_MODULES_PROPERTY, "profile,resume");
            System.setProperty(PostgresqlStorageConfig.COSMIC_CLIENT_CLASS_PROPERTY,
                    FakeCosmicBusinessObjectClient.class.getName());

            assertTrue(CareerProfileStorageFactory.fromSystemProperties() instanceof CosmicCareerProfileStorage);
            assertTrue(CyanCruiseStorageFactory.resumeStorage() instanceof CosmicResumeStorage);
            assertTrue(CyanCruiseStorageFactory.careerPlanStorage() instanceof InMemoryCareerPlanStorage);
        } finally {
            restore(PostgresqlStorageConfig.BACKEND_PROPERTY, oldBackend);
            restore(PostgresqlStorageConfig.COSMIC_MODULES_PROPERTY, oldModules);
            restore(PostgresqlStorageConfig.COSMIC_CLIENT_CLASS_PROPERTY, oldClient);
        }
    }

    private static void restore(String propertyName, String oldValue) {
        if (oldValue == null) {
            System.clearProperty(propertyName);
        } else {
            System.setProperty(propertyName, oldValue);
        }
    }

    public static class FakeCosmicBusinessObjectClient implements CosmicBusinessObjectClient {

        private final Map<String, Map<Long, Map<String, Object>>> records =
                new LinkedHashMap<String, Map<Long, Map<String, Object>>>();
        private final AtomicLong sequence = new AtomicLong(1L);

        public Map<String, Object> save(String objectName, Map<String, Object> fields) {
            Long id = DatamodelFieldMapper.asLong(fields.get(CyanCruiseDatamodelObjects.ID));
            if (id == null) {
                id = Long.valueOf(sequence.getAndIncrement());
            }
            Map<String, Object> saved = new LinkedHashMap<String, Object>(fields);
            saved.put(CyanCruiseDatamodelObjects.ID, id);
            bucket(objectName).put(id, saved);
            return new LinkedHashMap<String, Object>(saved);
        }

        public Map<String, Object> load(String objectName, Long id) {
            Map<String, Object> loaded = bucket(objectName).get(id);
            return loaded == null ? null : new LinkedHashMap<String, Object>(loaded);
        }

        public List<Map<String, Object>> list(String objectName) {
            List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
            for (Map<String, Object> row : bucket(objectName).values()) {
                result.add(new LinkedHashMap<String, Object>(row));
            }
            return result;
        }

        public void delete(String objectName, Long id) {
            bucket(objectName).remove(id);
        }

        public boolean isAvailable() {
            return true;
        }

        CosmicDatamodelRecord first(String objectName) {
            List<Map<String, Object>> rows = list(objectName);
            if (rows.isEmpty()) {
                return null;
            }
            CosmicDatamodelRecord record = new CosmicDatamodelRecord(objectName);
            for (Map.Entry<String, Object> entry : rows.get(0).entrySet()) {
                record.set(entry.getKey(), entry.getValue());
            }
            return record;
        }

        private Map<Long, Map<String, Object>> bucket(String objectName) {
            Map<Long, Map<String, Object>> bucket = records.get(objectName);
            if (bucket == null) {
                bucket = new LinkedHashMap<Long, Map<String, Object>>();
                records.put(objectName, bucket);
            }
            return bucket;
        }
    }
}
