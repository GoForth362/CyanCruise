package v620.cc001.cloud01.app01.mservice;

import v620.cc001.base.common.dto.ai.AiChatRequestDto;
import v620.cc001.base.common.dto.ai.AiChatResponseDto;
import v620.cc001.base.common.dto.ai.AiMessageDto;
import v620.cc001.cloud01.app01.mservice.ai.AiGateway;

import java.util.Collections;

public class ConversationSummaryAiService {

    private final AiGateway gateway;

    public ConversationSummaryAiService(AiGateway gateway) {
        this.gateway = gateway;
    }

    public String summarize(String previousSummary, String transcript) {
        AiChatRequestDto request = new AiChatRequestDto();
        request.setModelName("qwen-turbo");
        request.setMessages(Collections.singletonList(new AiMessageDto("user",
                "请用 3-5 句话总结长期记忆。PREVIOUS:\n" + safe(previousSummary) + "\nTRANSCRIPT:\n" + safe(transcript))));
        AiChatResponseDto response = gateway.chat(request);
        return response == null || response.getErrorCode() != null ? "" : response.getContent();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
