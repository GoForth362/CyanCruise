package v620.cc001.cloud01.app01.mservice.ai;

import v620.base.helper.ai.AiFunctionCallingLoopHelper;
import v620.cc001.base.common.dto.ai.AiChatRequestDto;
import v620.cc001.base.common.dto.ai.AiChatResponseDto;
import v620.cc001.base.common.dto.ai.AiConstants;
import v620.cc001.base.common.dto.ai.AiMessageDto;
import v620.cc001.base.common.dto.ai.AiToolCallDto;

import java.util.ArrayList;
import java.util.List;

public class AiFunctionCallingService {

    private final AiGateway gateway;
    private final AiToolRegistry registry;
    private final AiFunctionCallingLoopHelper helper;
    private final int maxToolCalls;

    public AiFunctionCallingService(AiGateway gateway, AiToolRegistry registry) {
        this(gateway, registry, new AiFunctionCallingLoopHelper(), 5);
    }

    public AiFunctionCallingService(AiGateway gateway,
                                    AiToolRegistry registry,
                                    AiFunctionCallingLoopHelper helper,
                                    int maxToolCalls) {
        this.gateway = gateway;
        this.registry = registry;
        this.helper = helper;
        this.maxToolCalls = maxToolCalls <= 0 ? 5 : maxToolCalls;
    }

    public String chat(List<AiMessageDto> messages, final String serverUserId) {
        List<AiMessageDto> loopMessages = new ArrayList<AiMessageDto>();
        if (messages != null) {
            loopMessages.addAll(messages);
        }
        int callCount = 0;
        String lastAssistant = "";
        while (callCount < maxToolCalls) {
            AiChatRequestDto request = new AiChatRequestDto();
            request.setMessages(loopMessages);
            request.setTools(registry.schemas());
            AiChatResponseDto response = gateway.chat(request);
            if (response == null) {
                return lastAssistant.length() == 0 ? "AI service error" : lastAssistant;
            }
            if (response.getContent() != null && response.getContent().trim().length() > 0) {
                lastAssistant = response.getContent();
            }
            if (!helper.shouldContinue(callCount, maxToolCalls, response)) {
                return lastAssistant;
            }
            for (AiToolCallDto call : response.getToolCalls()) {
                String result = helper.resolveToolResult(call, new AiFunctionCallingLoopHelper.ToolExecutor() {
                    public boolean supports(String toolName) {
                        return registry.supports(toolName);
                    }

                    public String execute(String toolName, String argumentsJson, String userId) {
                        return registry.find(toolName).execute(argumentsJson, serverUserId);
                    }
                }, serverUserId);
                AiMessageDto toolMessage = new AiMessageDto(AiConstants.ROLE_TOOL, result);
                toolMessage.setToolCallId(call.getId());
                loopMessages.add(toolMessage);
                callCount++;
                if (callCount >= maxToolCalls) {
                    break;
                }
            }
        }
        return helper.finalReplyOrFallback(lastAssistant, callCount, maxToolCalls);
    }
}
