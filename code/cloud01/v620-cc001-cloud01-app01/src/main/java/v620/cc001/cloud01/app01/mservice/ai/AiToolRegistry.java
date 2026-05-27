package v620.cc001.cloud01.app01.mservice.ai;

import v620.cc001.base.common.dto.ai.AiToolSchemaDto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AiToolRegistry {

    private final Map<String, AiTool> tools = new LinkedHashMap<String, AiTool>();

    public void register(AiTool tool) {
        if (tool != null && tool.schema() != null && tool.schema().getName() != null) {
            tools.put(tool.schema().getName(), tool);
        }
    }

    public AiTool find(String name) {
        return tools.get(name);
    }

    public boolean supports(String name) {
        return tools.containsKey(name);
    }

    public List<AiToolSchemaDto> schemas() {
        List<AiToolSchemaDto> result = new ArrayList<AiToolSchemaDto>();
        for (AiTool tool : tools.values()) {
            AiToolSchemaDto schema = tool.schema();
            if (schema.getParameters().containsKey("userId") || schema.getParameters().containsKey("user_id")) {
                throw new IllegalStateException("AI tool schema must not expose userId: " + schema.getName());
            }
            result.add(schema);
        }
        return result;
    }
}
