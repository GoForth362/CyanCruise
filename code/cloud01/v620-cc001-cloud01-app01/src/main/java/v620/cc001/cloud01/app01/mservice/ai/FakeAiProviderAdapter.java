package v620.cc001.cloud01.app01.mservice.ai;

import v620.cc001.base.common.dto.ai.AiChatRequestDto;
import v620.cc001.base.common.dto.ai.AiChatResponseDto;
import v620.cc001.base.common.dto.ai.AiConstants;
import v620.cc001.base.common.dto.ai.AiMessageDto;
import v620.cc001.base.common.dto.ai.AiStreamEventDto;
import v620.cc001.base.common.dto.ai.AiUsageDto;

import java.util.ArrayList;
import java.util.List;

public class FakeAiProviderAdapter implements AiProviderAdapter {

    private final String reply;
    private final String modelName;

    public FakeAiProviderAdapter(String reply) {
        this(reply, "fake-ai");
    }

    public FakeAiProviderAdapter(String reply, String modelName) {
        this.reply = reply;
        this.modelName = modelName;
    }

    public boolean isAvailable() {
        return true;
    }

    public AiChatResponseDto chat(AiChatRequestDto request) {
        AiChatResponseDto response = new AiChatResponseDto();
        response.setModelName(modelName);
        response.setContent(reply == null ? lastUserMessage(request) : reply);
        response.setFinishReason(AiConstants.FINISH_STOP);
        AiUsageDto usage = new AiUsageDto();
        usage.setPromptTokens(Integer.valueOf(request == null ? 0 : request.getMessages().size()));
        usage.setCompletionTokens(Integer.valueOf(1));
        usage.setTotalTokens(Integer.valueOf(usage.getPromptTokens().intValue() + 1));
        response.setUsage(usage);
        return response;
    }

    public List<AiStreamEventDto> stream(AiChatRequestDto request) {
        String content = reply == null ? lastUserMessage(request) : reply;
        List<AiStreamEventDto> events = new ArrayList<AiStreamEventDto>();
        int i = 0;
        for (String token : content.split("")) {
            if (token.length() > 0) {
                events.add(AiStreamEventDto.token(token, i++));
            }
        }
        events.add(AiStreamEventDto.done(i));
        return events;
    }

    private String lastUserMessage(AiChatRequestDto request) {
        if (request == null || request.getMessages() == null) {
            return "";
        }
        for (int i = request.getMessages().size() - 1; i >= 0; i--) {
            AiMessageDto message = request.getMessages().get(i);
            if (message != null && AiConstants.ROLE_USER.equals(message.getRole())) {
                return message.getContent();
            }
        }
        return "";
    }
}
