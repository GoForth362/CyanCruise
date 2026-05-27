package v620.cc001.cloud01.app01.mservice;

import v620.cc001.base.common.dto.career.AssistantChatMessageDto;
import v620.cc001.base.common.dto.career.AssistantChatSessionDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * In-memory assistant chat storage for focused tests.
 */
public class InMemoryAssistantChatStorage implements AssistantChatStorage {

    private final Map<Long, AssistantChatSessionDto> sessions = new LinkedHashMap<Long, AssistantChatSessionDto>();
    private final Map<Long, List<AssistantChatMessageDto>> messages = new LinkedHashMap<Long, List<AssistantChatMessageDto>>();
    private long nextSessionId = 1L;
    private long nextMessageId = 1L;

    public synchronized AssistantChatSessionDto saveSession(AssistantChatSessionDto session) {
        if (session.getSessionId() == null) {
            session.setSessionId(Long.valueOf(nextSessionId++));
        }
        sessions.put(session.getSessionId(), session);
        return session;
    }

    public synchronized AssistantChatSessionDto loadSession(Long sessionId) {
        return sessions.get(sessionId);
    }

    public synchronized List<AssistantChatSessionDto> listSessions(String userId) {
        List<AssistantChatSessionDto> result = new ArrayList<AssistantChatSessionDto>();
        for (AssistantChatSessionDto session : sessions.values()) {
            if (session != null && userId.equals(session.getUserId())) {
                result.add(session);
            }
        }
        sortSessions(result);
        return result;
    }

    public synchronized void deleteSession(Long sessionId) {
        sessions.remove(sessionId);
    }

    public synchronized AssistantChatMessageDto saveMessage(AssistantChatMessageDto message) {
        if (message.getMsgId() == null) {
            message.setMsgId(Long.valueOf(nextMessageId++));
        }
        List<AssistantChatMessageDto> list = messages.get(message.getSessionId());
        if (list == null) {
            list = new ArrayList<AssistantChatMessageDto>();
            messages.put(message.getSessionId(), list);
        }
        list.add(message);
        return message;
    }

    public synchronized List<AssistantChatMessageDto> listMessages(Long sessionId) {
        List<AssistantChatMessageDto> list = messages.get(sessionId);
        return list == null ? new ArrayList<AssistantChatMessageDto>() : new ArrayList<AssistantChatMessageDto>(list);
    }

    public synchronized void deleteMessages(Long sessionId) {
        messages.remove(sessionId);
    }

    static void sortSessions(List<AssistantChatSessionDto> result) {
        Collections.sort(result, new Comparator<AssistantChatSessionDto>() {
            public int compare(AssistantChatSessionDto left, AssistantChatSessionDto right) {
                if (left.getUpdatedAt() == null && right.getUpdatedAt() == null) return compareId(left, right);
                if (left.getUpdatedAt() == null) return 1;
                if (right.getUpdatedAt() == null) return -1;
                int order = right.getUpdatedAt().compareTo(left.getUpdatedAt());
                return order != 0 ? order : compareId(left, right);
            }
        });
    }

    private static int compareId(AssistantChatSessionDto left, AssistantChatSessionDto right) {
        if (left.getSessionId() == null && right.getSessionId() == null) return 0;
        if (left.getSessionId() == null) return 1;
        if (right.getSessionId() == null) return -1;
        return right.getSessionId().compareTo(left.getSessionId());
    }
}
