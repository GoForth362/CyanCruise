package v620.cc001.cloud01.app01.mservice.datamodel;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Platform-neutral record used by adapters before binding to the real Cosmic runtime.
 */
public class CosmicDatamodelRecord {

    private final String objectName;
    private final Map<String, Object> fields = new LinkedHashMap<String, Object>();

    public CosmicDatamodelRecord(String objectName) {
        this.objectName = objectName;
    }

    public String getObjectName() {
        return objectName;
    }

    public Object get(String fieldName) {
        return fields.get(fieldName);
    }

    public CosmicDatamodelRecord set(String fieldName, Object value) {
        fields.put(fieldName, value);
        return this;
    }

    public Map<String, Object> getFields() {
        return new LinkedHashMap<String, Object>(fields);
    }

    public CosmicDatamodelRecord copy() {
        CosmicDatamodelRecord copy = new CosmicDatamodelRecord(objectName);
        copy.fields.putAll(fields);
        return copy;
    }
}
