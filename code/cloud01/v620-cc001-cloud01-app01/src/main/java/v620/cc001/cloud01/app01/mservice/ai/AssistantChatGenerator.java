package v620.cc001.cloud01.app01.mservice.ai;

import v620.cc001.base.common.dto.career.AssistantChatMessageDto;
import v620.cc001.base.common.dto.career.AssistantChatResponse;

import java.util.List;

/**
 * Replaceable boundary for assistant chat generation.
 */
public interface AssistantChatGenerator {

    AssistantChatResponse chat(String userId, String persona, List<AssistantChatMessageDto> messages);
}
