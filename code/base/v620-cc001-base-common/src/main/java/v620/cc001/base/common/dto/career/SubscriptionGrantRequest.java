package v620.cc001.base.common.dto.career;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class SubscriptionGrantRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;
    private Map<String, String> results = new LinkedHashMap<String, String>();

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Map<String, String> getResults() {
        return results;
    }

    public void setResults(Map<String, String> results) {
        this.results = results;
    }
}
