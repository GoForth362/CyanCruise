package v620.cc001.cloud01.app01.mservice.ai.impl;

import v620.cc001.cloud01.app01.mservice.ai.*;
import v620.cc001.base.common.dto.career.AssistantChatConstants;
import v620.cc001.base.common.dto.career.AssistantChatMessageDto;
import v620.cc001.base.common.dto.career.AssistantChatResponse;

import java.util.List;

/**
 * Default generator until a platform AI adapter is wired.
 */
public class UnavailableAssistantChatGenerator implements AssistantChatGenerator {

    public AssistantChatResponse chat(String userId, String persona, List<AssistantChatMessageDto> messages) {
        throw new IllegalStateException(AssistantChatConstants.ERROR_CHAT_GENERATOR_UNAVAILABLE);
    }
}
