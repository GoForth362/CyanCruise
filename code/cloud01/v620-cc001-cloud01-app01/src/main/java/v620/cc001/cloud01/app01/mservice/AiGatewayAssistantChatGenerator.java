package v620.cc001.cloud01.app01.mservice;

import v620.cc001.base.common.dto.ai.AiChatRequestDto;
import v620.cc001.base.common.dto.ai.AiChatResponseDto;
import v620.cc001.base.common.dto.ai.AiMessageDto;
import v620.cc001.base.common.dto.career.AssistantChatMessageDto;
import v620.cc001.base.common.dto.career.AssistantChatResponse;
import v620.cc001.cloud01.app01.mservice.ai.AiGateway;

import java.util.ArrayList;
import java.util.List;

public class AiGatewayAssistantChatGenerator implements AssistantChatGenerator {

    private final AiGateway gateway;

    public AiGatewayAssistantChatGenerator(AiGateway gateway) {
        this.gateway = gateway;
    }

    public AssistantChatResponse chat(String userId, String persona, List<AssistantChatMessageDto> messages) {
        AiChatRequestDto request = new AiChatRequestDto();
        List<AiMessageDto> aiMessages = new ArrayList<AiMessageDto>();
        if (messages != null) {
            for (AssistantChatMessageDto message : messages) {
                aiMessages.add(new AiMessageDto(message.getRole(), message.getContent()));
            }
        }
        request.setMessages(aiMessages);
        AiChatResponseDto aiResponse = gateway.chat(request);
        if (aiResponse.getErrorCode() != null) {
            throw new IllegalStateException(aiResponse.getErrorMessage());
        }
        AssistantChatResponse response = new AssistantChatResponse();
        response.setReply(aiResponse.getContent());
        response.setModelName(aiResponse.getModelName());
        response.setPromptTokens(aiResponse.getUsage().getPromptTokens());
        response.setCompletionTokens(aiResponse.getUsage().getCompletionTokens());
        response.setTotalTokens(aiResponse.getUsage().getTotalTokens());
        response.setCostMicros(aiResponse.getUsage().getCostMicros());
        return response;
    }
}
