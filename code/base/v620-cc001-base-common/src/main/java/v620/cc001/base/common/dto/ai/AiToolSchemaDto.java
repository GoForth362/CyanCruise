package v620.cc001.base.common.dto.ai;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class AiToolSchemaDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private Map<String, String> parameters = new LinkedHashMap<String, String>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters == null ? new LinkedHashMap<String, String>() : parameters;
    }
}
