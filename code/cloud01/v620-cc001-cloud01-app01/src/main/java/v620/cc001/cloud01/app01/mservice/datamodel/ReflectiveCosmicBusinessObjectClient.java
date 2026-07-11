package v620.cc001.cloud01.app01.mservice.datamodel;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ReflectiveCosmicBusinessObjectClient implements CosmicBusinessObjectClient {

    private static final String BUSINESS_DATA_HELPER = "kd.bos.servicehelper.BusinessDataServiceHelper";
    private static final String SAVE_HELPER = "kd.bos.servicehelper.operation.SaveServiceHelper";

    public Map<String, Object> save(String objectName, Map<String, Object> fields) {
        requireRuntime();
        throw unavailable("save", objectName);
    }

    public Map<String, Object> load(String objectName, Long id) {
        requireRuntime();
        throw unavailable("load", objectName);
    }

    public List<Map<String, Object>> list(String objectName) {
        requireRuntime();
        throw unavailable("list", objectName);
    }

    public void delete(String objectName, Long id) {
        requireRuntime();
        throw unavailable("delete", objectName);
    }

    public boolean isAvailable() {
        return classExists(BUSINESS_DATA_HELPER) && classExists(SAVE_HELPER);
    }

    private void requireRuntime() {
        if (!isAvailable()) {
            throw new IllegalStateException("Kingdee Cosmic business data service classes are not available in this runtime.");
        }
    }

    private IllegalStateException unavailable(String operation, String objectName) {
        return new IllegalStateException("Default reflective Cosmic client detected platform classes but no tenant-specific "
                + operation + " bridge is configured for " + objectName
                + ". Set cc001.storage.cosmic.clientClass to a CosmicBusinessObjectClient implementation.");
    }

    private boolean classExists(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (Throwable ex) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    static List<Map<String, Object>> asMapList(Object value) {
        if (value instanceof List) {
            return (List<Map<String, Object>>) value;
        }
        return Collections.emptyList();
    }

    static Method findMethod(Class<?> type, String name, int parameterCount) {
        Method[] methods = type.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (name.equals(method.getName()) && method.getParameterTypes().length == parameterCount) {
                return method;
            }
        }
        return null;
    }
}
