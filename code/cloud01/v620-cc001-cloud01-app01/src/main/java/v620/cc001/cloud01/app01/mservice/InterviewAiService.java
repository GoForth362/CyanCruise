package v620.cc001.cloud01.app01.mservice;

import v620.cc001.base.common.dto.ai.AiChatRequestDto;
import v620.cc001.base.common.dto.ai.AiChatResponseDto;
import v620.cc001.base.common.dto.ai.AiMessageDto;
import v620.cc001.cloud01.app01.mservice.ai.AiGateway;

import java.util.Collections;

public class InterviewAiService {

    private final AiGateway gateway;

    public InterviewAiService(AiGateway gateway) {
        this.gateway = gateway;
    }

    public String nextQuestion(String context) {
        return ask("请作为面试官追问一个问题：" + context);
    }

    public String report(String transcript) {
        return ask("请生成模拟面试报告 JSON：" + transcript);
    }

    private String ask(String prompt) {
        AiChatRequestDto request = new AiChatRequestDto();
        request.setMessages(Collections.singletonList(new AiMessageDto("user", prompt)));
        AiChatResponseDto response = gateway.chat(request);
        return response == null || response.getErrorCode() != null ? null : response.getContent();
    }
}
