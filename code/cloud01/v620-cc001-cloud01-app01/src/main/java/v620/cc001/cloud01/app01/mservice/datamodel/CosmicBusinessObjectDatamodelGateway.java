package v620.cc001.cloud01.app01.mservice.datamodel;

import v620.cc001.cloud01.app01.mservice.storage.PostgresqlStorageConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CosmicBusinessObjectDatamodelGateway implements CosmicDatamodelGateway {

    private final CosmicBusinessObjectClient client;

    public CosmicBusinessObjectDatamodelGateway(CosmicBusinessObjectClient client) {
        if (client == null) {
            throw new IllegalArgumentException("cosmic business object client must not be null");
        }
        this.client = client;
    }

    public static CosmicBusinessObjectDatamodelGateway fromConfig(PostgresqlStorageConfig config) {
        return new CosmicBusinessObjectDatamodelGateway(clientFromConfig(config));
    }

    public CosmicDatamodelRecord save(CosmicDatamodelRecord record) {
        requireAvailable("save", record == null ? null : record.getObjectName());
        if (record == null) {
            throw new IllegalArgumentException("record must not be null");
        }
        Map<String, Object> saved = client.save(record.getObjectName(), record.getFields());
        return toRecord(record.getObjectName(), saved == null || saved.isEmpty() ? record.getFields() : saved);
    }

    public CosmicDatamodelRecord load(String objectName, Long id) {
        requireAvailable("load", objectName);
        if (id == null) {
            return null;
        }
        Map<String, Object> loaded = client.load(objectName, id);
        return loaded == null ? null : toRecord(objectName, loaded);
    }

    public CosmicDatamodelRecord findOne(String objectName, CosmicRecordFilter filter) {
        List<CosmicDatamodelRecord> rows = list(objectName, filter, null);
        return rows.isEmpty() ? null : rows.get(0);
    }

    public List<CosmicDatamodelRecord> list(String objectName,
                                           CosmicRecordFilter filter,
                                           Comparator<CosmicDatamodelRecord> comparator) {
        requireAvailable("list", objectName);
        List<Map<String, Object>> loaded = client.list(objectName);
        List<CosmicDatamodelRecord> rows = new ArrayList<CosmicDatamodelRecord>();
        if (loaded != null) {
            for (Map<String, Object> fields : loaded) {
                CosmicDatamodelRecord record = toRecord(objectName, fields);
                if (filter == null || filter.matches(record)) {
                    rows.add(record);
                }
            }
        }
        if (comparator != null) {
            Collections.sort(rows, comparator);
        }
        return rows;
    }

    public void delete(String objectName, Long id) {
        requireAvailable("delete", objectName);
        if (id != null) {
            client.delete(objectName, id);
        }
    }

    public void deleteWhere(String objectName, CosmicRecordFilter filter) {
        List<CosmicDatamodelRecord> rows = list(objectName, filter, null);
        for (CosmicDatamodelRecord row : rows) {
            Long id = DatamodelFieldMapper.asLong(row.get(CyanCruiseDatamodelObjects.ID));
            if (id != null) {
                delete(objectName, id);
            }
        }
    }

    public boolean isAvailable() {
        return client.isAvailable();
    }

    private void requireAvailable(String operation, String objectName) {
        if (!client.isAvailable()) {
            throw new IllegalStateException("Cosmic business object storage is unavailable for "
                    + operation + " on " + safeObject(objectName)
                    + ". Configure a platform client or run inside a Cosmic runtime with business data services.");
        }
    }

    private static CosmicDatamodelRecord toRecord(String objectName, Map<String, Object> fields) {
        CosmicDatamodelRecord record = new CosmicDatamodelRecord(objectName);
        if (fields != null) {
            for (Map.Entry<String, Object> entry : fields.entrySet()) {
                record.set(entry.getKey(), entry.getValue());
            }
        }
        return record;
    }

    private static CosmicBusinessObjectClient clientFromConfig(PostgresqlStorageConfig config) {
        String className = config == null ? null : config.getCosmicClientClass();
        if (PostgresqlStorageConfig.hasText(className)) {
            try {
                Object instance = Class.forName(className).newInstance();
                if (!(instance instanceof CosmicBusinessObjectClient)) {
                    throw new IllegalStateException("Configured Cosmic client does not implement CosmicBusinessObjectClient: "
                            + className);
                }
                return (CosmicBusinessObjectClient) instance;
            } catch (RuntimeException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new IllegalStateException("Unable to create configured Cosmic client: " + className, ex);
            }
        }
        return new ReflectiveCosmicBusinessObjectClient();
    }

    private static String safeObject(String objectName) {
        return objectName == null ? "<unknown>" : objectName;
    }

    static Map<String, Object> copy(Map<String, Object> source) {
        return source == null ? new LinkedHashMap<String, Object>() : new LinkedHashMap<String, Object>(source);
    }
}
