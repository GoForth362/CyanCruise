package v620.cc001.base.common.dto.ai;

import java.io.Serializable;

public class AiToolCallDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String argumentsJson;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArgumentsJson() {
        return argumentsJson;
    }

    public void setArgumentsJson(String argumentsJson) {
        this.argumentsJson = argumentsJson;
    }
}
