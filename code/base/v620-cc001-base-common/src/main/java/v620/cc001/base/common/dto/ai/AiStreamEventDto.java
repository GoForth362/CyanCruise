package v620.cc001.base.common.dto.ai;

import java.io.Serializable;

public class AiStreamEventDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String type;
    private String data;
    private String errorCode;
    private String errorMessage;
    private Integer index;

    public static AiStreamEventDto token(String data, int index) {
        AiStreamEventDto event = new AiStreamEventDto();
        event.setType(AiConstants.STREAM_TOKEN);
        event.setData(data);
        event.setIndex(Integer.valueOf(index));
        return event;
    }

    public static AiStreamEventDto done(int index) {
        AiStreamEventDto event = new AiStreamEventDto();
        event.setType(AiConstants.STREAM_DONE);
        event.setIndex(Integer.valueOf(index));
        return event;
    }

    public static AiStreamEventDto error(String errorCode, String errorMessage, int index) {
        AiStreamEventDto event = new AiStreamEventDto();
        event.setType(AiConstants.STREAM_ERROR);
        event.setErrorCode(errorCode);
        event.setErrorMessage(errorMessage);
        event.setIndex(Integer.valueOf(index));
        return event;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
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

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}
