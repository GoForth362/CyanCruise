package v620.cc001.cloud01.app01.mservice.ai;

import v620.base.helper.ai.AiJsonHelper;
import v620.cc001.base.common.dto.ai.AiChatRequestDto;
import v620.cc001.base.common.dto.ai.AiChatResponseDto;
import v620.cc001.base.common.dto.ai.AiMessageDto;
import v620.cc001.base.common.dto.career.CareerAgentTodayDto;
import v620.cc001.cloud01.app01.mservice.ai.AiGateway;

import java.util.Collections;

public class TaskDecompositionAiService {

    private final AiGateway gateway;
    private final AiJsonHelper jsonHelper;

    public TaskDecompositionAiService(AiGateway gateway) {
        this(gateway, new AiJsonHelper());
    }

    public TaskDecompositionAiService(AiGateway gateway, AiJsonHelper jsonHelper) {
        this.gateway = gateway;
        this.jsonHelper = jsonHelper;
    }

    public String decompose(CareerAgentTodayDto.Action action) {
        AiChatRequestDto request = new AiChatRequestDto();
        request.setModelName("qwen-turbo");
        request.setMessages(Collections.singletonList(new AiMessageDto("user",
                "请将任务拆成 2-4 个子任务并只返回 JSON 数组。任务：" + (action == null ? "" : action.getLabel()))));
        AiChatResponseDto response = gateway.chat(request);
        if (response == null || response.getErrorCode() != null) {
            return null;
        }
        return jsonHelper.extractJsonArray(response.getContent());
    }
}
