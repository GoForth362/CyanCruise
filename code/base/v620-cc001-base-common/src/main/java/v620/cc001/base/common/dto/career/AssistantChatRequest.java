package v620.cc001.base.common.dto.career;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Request for a single assistant chat turn.
 */
public class AssistantChatRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String message;
    private List<AssistantChatMessageDto> history = new ArrayList<AssistantChatMessageDto>();
    private String persona;
    private Long sessionId;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<AssistantChatMessageDto> getHistory() {
        return history;
    }

    public void setHistory(List<AssistantChatMessageDto> history) {
        this.history = history;
    }

    public String getPersona() {
        return persona;
    }

    public void setPersona(String persona) {
        this.persona = persona;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }
}
