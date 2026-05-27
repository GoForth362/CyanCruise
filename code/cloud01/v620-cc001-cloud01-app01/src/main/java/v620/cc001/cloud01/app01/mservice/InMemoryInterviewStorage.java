package v620.cc001.cloud01.app01.mservice;

import v620.cc001.base.common.dto.career.InterviewMessageDto;
import v620.cc001.base.common.dto.career.InterviewSessionDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Non-durable interview storage for focused migration tests.
 */
public class InMemoryInterviewStorage implements InterviewStorage {

    private static final AtomicLong INTERVIEW_IDS = new AtomicLong(1L);
    private static final AtomicLong MESSAGE_IDS = new AtomicLong(1L);
    private static final Map<Long, InterviewSessionDto> INTERVIEWS =
            new ConcurrentHashMap<Long, InterviewSessionDto>();
    private static final Map<Long, List<InterviewMessageDto>> MESSAGES =
            new ConcurrentHashMap<Long, List<InterviewMessageDto>>();

    public InterviewSessionDto saveInterview(InterviewSessionDto interview) {
        if (interview.getInterviewId() == null) {
            interview.setInterviewId(Long.valueOf(INTERVIEW_IDS.getAndIncrement()));
        }
        INTERVIEWS.put(interview.getInterviewId(), interview);
        return interview;
    }

    public InterviewSessionDto loadInterview(Long interviewId) {
        return INTERVIEWS.get(interviewId);
    }

    public List<InterviewSessionDto> listByUser(String userId) {
        List<InterviewSessionDto> result = new ArrayList<InterviewSessionDto>();
        for (InterviewSessionDto interview : INTERVIEWS.values()) {
            if (interview != null && userId.equals(interview.getUserId())) {
                result.add(interview);
            }
        }
        Collections.sort(result, new Comparator<InterviewSessionDto>() {
            public int compare(InterviewSessionDto left, InterviewSessionDto right) {
                int startedAtOrder = compareStartedAt(left, right);
                if (startedAtOrder != 0) {
                    return startedAtOrder;
                }
                return compareInterviewId(left, right);
            }
        });
        return result;
    }

    private int compareStartedAt(InterviewSessionDto left, InterviewSessionDto right) {
        if (left.getStartedAt() == null && right.getStartedAt() == null) return 0;
        if (left.getStartedAt() == null) return 1;
        if (right.getStartedAt() == null) return -1;
        return right.getStartedAt().compareTo(left.getStartedAt());
    }

    private int compareInterviewId(InterviewSessionDto left, InterviewSessionDto right) {
        if (left.getInterviewId() == null && right.getInterviewId() == null) return 0;
        if (left.getInterviewId() == null) return 1;
        if (right.getInterviewId() == null) return -1;
        return right.getInterviewId().compareTo(left.getInterviewId());
    }

    public void deleteInterview(Long interviewId) {
        INTERVIEWS.remove(interviewId);
    }

    public InterviewMessageDto saveMessage(InterviewMessageDto message) {
        if (message.getMessageId() == null) {
            message.setMessageId(Long.valueOf(MESSAGE_IDS.getAndIncrement()));
        }
        List<InterviewMessageDto> list = MESSAGES.get(message.getInterviewId());
        if (list == null) {
            list = Collections.synchronizedList(new ArrayList<InterviewMessageDto>());
            MESSAGES.put(message.getInterviewId(), list);
        }
        list.add(message);
        return message;
    }

    public List<InterviewMessageDto> listMessages(Long interviewId) {
        List<InterviewMessageDto> list = MESSAGES.get(interviewId);
        return list == null ? new ArrayList<InterviewMessageDto>() : new ArrayList<InterviewMessageDto>(list);
    }

    public void deleteMessages(Long interviewId) {
        MESSAGES.remove(interviewId);
    }
}
