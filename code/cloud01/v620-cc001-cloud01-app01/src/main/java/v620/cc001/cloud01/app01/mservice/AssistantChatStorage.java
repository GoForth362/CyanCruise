package v620.cc001.cloud01.app01.mservice;

import v620.cc001.base.common.dto.career.AssistantChatMessageDto;
import v620.cc001.base.common.dto.career.AssistantChatSessionDto;

import java.util.List;

/**
 * Replaceable storage boundary for assistant chat sessions and messages.
 */
public interface AssistantChatStorage {

    AssistantChatSessionDto saveSession(AssistantChatSessionDto session);

    AssistantChatSessionDto loadSession(Long sessionId);

    List<AssistantChatSessionDto> listSessions(String userId);

    void deleteSession(Long sessionId);

    AssistantChatMessageDto saveMessage(AssistantChatMessageDto message);

    List<AssistantChatMessageDto> listMessages(Long sessionId);

    void deleteMessages(Long sessionId);
}
