package v620.cc001.base.common.dto.ai;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Server-side request contract for an Agent platform task flow.
 */
public class AgentTaskFlowRequestDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String taskFlowCode;
    private Map<String, String> inputs = new LinkedHashMap<String, String>();

    public String getTaskFlowCode() {
        return taskFlowCode;
    }

    public void setTaskFlowCode(String taskFlowCode) {
        this.taskFlowCode = taskFlowCode;
    }

    public Map<String, String> getInputs() {
        return inputs;
    }

    public void setInputs(Map<String, String> inputs) {
        this.inputs = inputs == null ? new LinkedHashMap<String, String>() : inputs;
    }

    public void putInput(String name, String value) {
        if (name != null && name.trim().length() > 0) {
            inputs.put(name.trim(), value);
        }
    }
}
