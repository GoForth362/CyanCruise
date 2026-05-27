package v620.cc001.base.common.dto.ai;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AiMessageDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String role;
    private String content;
    private String toolCallId;
    private List<AiToolCallDto> toolCalls = new ArrayList<AiToolCallDto>();

    public AiMessageDto() {
    }

    public AiMessageDto(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getToolCallId() {
        return toolCallId;
    }

    public void setToolCallId(String toolCallId) {
        this.toolCallId = toolCallId;
    }

    public List<AiToolCallDto> getToolCalls() {
        return toolCalls;
    }

    public void setToolCalls(List<AiToolCallDto> toolCalls) {
        this.toolCalls = toolCalls == null ? new ArrayList<AiToolCallDto>() : toolCalls;
    }
}
