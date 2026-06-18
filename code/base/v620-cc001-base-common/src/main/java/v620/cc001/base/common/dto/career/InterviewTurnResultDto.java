package v620.cc001.base.common.dto.career;

import java.io.Serializable;

public class InterviewTurnResultDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private InterviewSessionDto session;
    private InterviewMessageDto userMessage;
    private InterviewMessageDto interviewerMessage;

    public InterviewSessionDto getSession() { return session; }
    public void setSession(InterviewSessionDto session) { this.session = session; }
    public InterviewMessageDto getUserMessage() { return userMessage; }
    public void setUserMessage(InterviewMessageDto userMessage) { this.userMessage = userMessage; }
    public InterviewMessageDto getInterviewerMessage() { return interviewerMessage; }
    public void setInterviewerMessage(InterviewMessageDto interviewerMessage) { this.interviewerMessage = interviewerMessage; }
}
