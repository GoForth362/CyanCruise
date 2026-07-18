package v620.cc001.base.common.dto.ai;

import java.io.Serializable;

/**
 * Sanitized result returned by a server-side Agent platform task flow call.
 */
public class AgentTaskFlowResponseDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean success;
    private String answer;
    private String errorCode;
    private String errorMessage;
    private Integer statusCode;

    public static AgentTaskFlowResponseDto unavailable(String errorCode, String errorMessage) {
        AgentTaskFlowResponseDto response = new AgentTaskFlowResponseDto();
        response.setSuccess(false);
        response.setErrorCode(errorCode);
        response.setErrorMessage(errorMessage);
        return response;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
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

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }
}
