package v620.cc001.cloud01.app01.mservice.datamodel;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Reflective bridge to Kingdee Cosmic business object services.
 *
 * <p>The repository may be built outside the final Cosmic runtime, so this class
 * avoids compile-time references to platform APIs. If a tenant runtime exposes
 * different service helper signatures, keep this class as the narrow adapter
 * to adjust.</p>
 */
public class PlatformCosmicBusinessObjectClient implements CosmicBusinessObjectClient {

    private static final String BUSINESS_DATA_HELPER = "kd.bos.servicehelper.BusinessDataServiceHelper";
    private static final String SAVE_HELPER = "kd.bos.servicehelper.operation.SaveServiceHelper";
    private static final String DYNAMIC_OBJECT = "kd.bos.dataentity.entity.DynamicObject";

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public Map<String, Object> save(String objectName, Map<String, Object> fields) {
        requireAvailable("save", objectName);
        Object row = dynamicObject(objectName, DatamodelFieldMapper.asLong(fields.get(CyanCruiseDatamodelObjects.ID)));
        setFields(row, fields);
        saveDynamicObject(row);
        return readFields(objectName, row, fields);
    }

    public Map<String, Object> load(String objectName, Long id) {
        requireAvailable("load", objectName);
        Object row = loadDynamicObject(objectName, id);
        return row == null ? null : readFields(objectName, row, null);
    }

    public List<Map<String, Object>> list(String objectName) {
        requireAvailable("list", objectName);
        Object loaded = invokeLoadAll(objectName);
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        if (loaded == null) {
            return rows;
        }
        if (loaded.getClass().isArray()) {
            int length = Array.getLength(loaded);
            for (int i = 0; i < length; i++) {
                rows.add(readFields(objectName, Array.get(loaded, i), null));
            }
            return rows;
        }
        if (loaded instanceof Iterable) {
            for (Object row : (Iterable<?>) loaded) {
                rows.add(readFields(objectName, row, null));
            }
        }
        return rows;
    }

    public void delete(String objectName, Long id) {
        requireAvailable("delete", objectName);
        throw new IllegalStateException("Physical delete is not enabled for Cosmic business object storage: "
                + objectName + "/" + id + ". Use status/archive semantics in domain storage.");
    }

    public boolean isAvailable() {
        return classExists(BUSINESS_DATA_HELPER) && classExists(SAVE_HELPER) && classExists(DYNAMIC_OBJECT);
    }

    private Object dynamicObject(String objectName, Long id) {
        if (id != null) {
            Object loaded = loadDynamicObject(objectName, id);
            if (loaded != null) {
                return loaded;
            }
        }
        return newDynamicObject(objectName);
    }

    private Object newDynamicObject(String objectName) {
        try {
            Class<?> helper = Class.forName(BUSINESS_DATA_HELPER);
            Method method = findMethod(helper, "newDynamicObject", String.class);
            if (method == null) {
                throw new IllegalStateException("BusinessDataServiceHelper.newDynamicObject(String) is unavailable.");
            }
            return method.invoke(null, objectName);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to create Cosmic business object: " + objectName, ex);
        }
    }

    private Object loadDynamicObject(String objectName, Long id) {
        if (id == null) {
            return null;
        }
        try {
            Class<?> helper = Class.forName(BUSINESS_DATA_HELPER);
            Method loadSingle = findMethod(helper, "loadSingle", String.class, Object.class);
            if (loadSingle != null) {
                return loadSingle.invoke(null, objectName, id);
            }
            loadSingle = findMethod(helper, "loadSingle", String.class, Long.class);
            if (loadSingle != null) {
                return loadSingle.invoke(null, objectName, id);
            }
            throw new IllegalStateException("BusinessDataServiceHelper.loadSingle(String, Object) is unavailable.");
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to load Cosmic business object: " + objectName + "/" + id, ex);
        }
    }

    private Object invokeLoadAll(String objectName) {
        try {
            Class<?> helper = Class.forName(BUSINESS_DATA_HELPER);
            Method load = findMethod(helper, "load", String.class, String.class, Object[].class);
            if (load != null) {
                return load.invoke(null, objectName, selectFields(objectName), null);
            }
            load = findMethod(helper, "load", String.class, String.class);
            if (load != null) {
                return load.invoke(null, objectName, selectFields(objectName));
            }
            throw new IllegalStateException("BusinessDataServiceHelper.load(String, String, ...) is unavailable.");
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to list Cosmic business object: " + objectName, ex);
        }
    }

    private void saveDynamicObject(Object row) {
        try {
            Class<?> dynamicObjectType = Class.forName(DYNAMIC_OBJECT);
            Object array = Array.newInstance(dynamicObjectType, 1);
            Array.set(array, 0, row);
            Class<?> helper = Class.forName(SAVE_HELPER);
            Method save = findMethod(helper, "save", array.getClass());
            if (save == null) {
                throw new IllegalStateException("SaveServiceHelper.save(DynamicObject[]) is unavailable.");
            }
            save.invoke(null, array);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to save Cosmic business object.", ex);
        }
    }

    private void setFields(Object row, Map<String, Object> fields) {
        if (fields == null) {
            return;
        }
        Method set = findMethod(row.getClass(), "set", String.class, Object.class);
        if (set == null) {
            throw new IllegalStateException("DynamicObject.set(String, Object) is unavailable.");
        }
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            if (CyanCruiseDatamodelObjects.ID.equals(entry.getKey())) {
                continue;
            }
            try {
                set.invoke(row, entry.getKey(), toPlatformValue(entry.getValue()));
            } catch (Exception ex) {
                throw new IllegalStateException("Unable to set Cosmic field: " + entry.getKey(), ex);
            }
        }
    }

    private Map<String, Object> readFields(String objectName, Object row, Map<String, Object> fallback) {
        Map<String, Object> result = fallback == null
                ? new LinkedHashMap<String, Object>()
                : new LinkedHashMap<String, Object>(fallback);
        Object id = invokeNoArg(row, "getPkValue");
        if (id == null) {
            id = getField(row, CyanCruiseDatamodelObjects.ID);
        }
        if (id != null) {
            result.put(CyanCruiseDatamodelObjects.ID, id);
        }
        List<String> fields = CyanCruiseBusinessModelMapping.platformFieldsForObject(objectName);
        for (String field : fields) {
            Object value = getField(row, field);
            if (value != null) {
                result.put(field, value);
            }
        }
        return result;
    }

    private Object getField(Object row, String fieldName) {
        Method get = findMethod(row.getClass(), "get", String.class);
        if (get == null) {
            return null;
        }
        try {
            return get.invoke(row, fieldName);
        } catch (Exception ex) {
            return null;
        }
    }

    private Object invokeNoArg(Object target, String methodName) {
        Method method = findMethod(target.getClass(), methodName);
        if (method == null) {
            return null;
        }
        try {
            return method.invoke(target);
        } catch (Exception ex) {
            return null;
        }
    }

    private Object toPlatformValue(Object value) {
        if (value == null || value instanceof String || value instanceof Number || value instanceof Boolean
                || value instanceof LocalDate || value instanceof LocalDateTime || value instanceof BigDecimal) {
            return value;
        }
        try {
            return MAPPER.writeValueAsString(value);
        } catch (Exception ex) {
            return String.valueOf(value);
        }
    }

    private String selectFields(String objectName) {
        List<String> fields = CyanCruiseBusinessModelMapping.platformFieldsForObject(objectName);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < fields.size(); i++) {
            if (i > 0) {
                builder.append(',');
            }
            builder.append(fields.get(i));
        }
        return builder.toString();
    }

    private void requireAvailable(String operation, String objectName) {
        if (!isAvailable()) {
            throw new IllegalStateException("Kingdee Cosmic business object API is unavailable for "
                    + operation + " on " + objectName + ". Run inside Cosmic runtime or check platform libraries.");
        }
    }

    private boolean classExists(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (Throwable ex) {
            return false;
        }
    }

    private static Method findMethod(Class<?> type, String name, Class<?>... parameterTypes) {
        try {
            return type.getMethod(name, parameterTypes);
        } catch (Exception ex) {
            return null;
        }
    }
}
