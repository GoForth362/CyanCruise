package v620.cc001.cloud01.app01.mservice.datamodel;

import java.util.List;
import java.util.Map;

public interface CosmicBusinessObjectClient {

    Map<String, Object> save(String objectName, Map<String, Object> fields);

    Map<String, Object> load(String objectName, Long id);

    List<Map<String, Object>> list(String objectName);

    void delete(String objectName, Long id);

    boolean isAvailable();
}
