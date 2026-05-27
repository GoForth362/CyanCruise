package v620.cc001.base.common.dto.ai;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AiChatResponseDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String content;
    private String modelName = AiConstants.DEFAULT_MODEL_NAME;
    private String finishReason = AiConstants.FINISH_STOP;
    private String errorCode;
    private String errorMessage;
    private boolean fallback;
    private AiUsageDto usage = new AiUsageDto();
    private List<AiToolCallDto> toolCalls = new ArrayList<AiToolCallDto>();

    public static AiChatResponseDto unavailable(String message) {
        AiChatResponseDto response = new AiChatResponseDto();
        response.setFallback(true);
        response.setFinishReason(AiConstants.FINISH_ERROR);
        response.setErrorCode(AiConstants.ERROR_UNAVAILABLE);
        response.setErrorMessage(message);
        return response;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName == null ? AiConstants.DEFAULT_MODEL_NAME : modelName;
    }

    public String getFinishReason() {
        return finishReason;
    }

    public void setFinishReason(String finishReason) {
        this.finishReason = finishReason == null ? AiConstants.FINISH_STOP : finishReason;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isFallback() {
        return fallback;
    }

    public void setFallback(boolean fallback) {
        this.fallback = fallback;
    }

    public AiUsageDto getUsage() {
        return usage;
    }

    public void setUsage(AiUsageDto usage) {
        this.usage = usage == null ? new AiUsageDto() : usage;
    }

    public List<AiToolCallDto> getToolCalls() {
        return toolCalls;
    }

    public void setToolCalls(List<AiToolCallDto> toolCalls) {
        this.toolCalls = toolCalls == null ? new ArrayList<AiToolCallDto>() : toolCalls;
    }
}
