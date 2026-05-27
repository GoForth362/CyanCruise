package v620.cc001.cloud01.app01.mservice;

import v620.cc001.base.common.dto.career.InterviewMessageDto;
import v620.cc001.base.common.dto.career.InterviewSessionDto;

import java.util.List;

/**
 * Replaceable storage boundary for mock interview sessions and messages.
 */
public interface InterviewStorage {

    InterviewSessionDto saveInterview(InterviewSessionDto interview);

    InterviewSessionDto loadInterview(Long interviewId);

    List<InterviewSessionDto> listByUser(String userId);

    void deleteInterview(Long interviewId);

    InterviewMessageDto saveMessage(InterviewMessageDto message);

    List<InterviewMessageDto> listMessages(Long interviewId);

    void deleteMessages(Long interviewId);
}
