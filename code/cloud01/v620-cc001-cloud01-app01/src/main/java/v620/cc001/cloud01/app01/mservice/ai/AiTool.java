package v620.cc001.cloud01.app01.mservice.ai;

import v620.cc001.base.common.dto.ai.AiToolSchemaDto;

public interface AiTool {

    AiToolSchemaDto schema();

    String execute(String argumentsJson, String serverUserId);
}
