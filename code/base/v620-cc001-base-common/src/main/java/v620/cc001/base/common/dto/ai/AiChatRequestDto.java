package v620.cc001.base.common.dto.ai;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AiChatRequestDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String modelName;
    private List<AiMessageDto> messages = new ArrayList<AiMessageDto>();
    private List<AiToolSchemaDto> tools = new ArrayList<AiToolSchemaDto>();
    private boolean stream;
    private Integer timeoutSeconds;
    private boolean retryOnce;

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public List<AiMessageDto> getMessages() {
        return messages;
    }

    public void setMessages(List<AiMessageDto> messages) {
        this.messages = messages == null ? new ArrayList<AiMessageDto>() : messages;
    }

    public List<AiToolSchemaDto> getTools() {
        return tools;
    }

    public void setTools(List<AiToolSchemaDto> tools) {
        this.tools = tools == null ? new ArrayList<AiToolSchemaDto>() : tools;
    }

    public boolean isStream() {
        return stream;
    }

    public void setStream(boolean stream) {
        this.stream = stream;
    }

    public Integer getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(Integer timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    public boolean isRetryOnce() {
        return retryOnce;
    }

    public void setRetryOnce(boolean retryOnce) {
        this.retryOnce = retryOnce;
    }
}
