package v620.cc001.base.common.dto.career;

import java.io.Serializable;

public class InterviewStartResultDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private InterviewSessionDto session;
    private InterviewMessageDto openingMessage;

    public InterviewSessionDto getSession() { return session; }
    public void setSession(InterviewSessionDto session) { this.session = session; }
    public InterviewMessageDto getOpeningMessage() { return openingMessage; }
    public void setOpeningMessage(InterviewMessageDto openingMessage) { this.openingMessage = openingMessage; }
}
