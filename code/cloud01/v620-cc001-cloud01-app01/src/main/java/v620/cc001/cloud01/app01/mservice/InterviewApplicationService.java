package v620.cc001.cloud01.app01.mservice;

import v620.base.helper.career.InterviewCoreService;
import v620.cc001.base.common.dto.career.InterviewConstants;
import v620.cc001.base.common.dto.career.InterviewMessageDto;
import v620.cc001.base.common.dto.career.InterviewMessageRequest;
import v620.cc001.base.common.dto.career.InterviewReportDto;
import v620.cc001.base.common.dto.career.InterviewSessionDto;
import v620.cc001.base.common.dto.career.InterviewStartRequest;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Application boundary for mock interview sessions and report summaries.
 */
public class InterviewApplicationService {

    private final InterviewStorage storage;
    private final CareerProfileApplicationService profileApplicationService;
    private final InterviewCoreService helper;

    public InterviewApplicationService() {
        this(CyanCruiseStorageFactory.interviewStorage(), new CareerProfileApplicationService(), new InterviewCoreService());
    }

    public InterviewApplicationService(InterviewStorage storage,
                                       CareerProfileApplicationService profileApplicationService,
                                       InterviewCoreService helper) {
        this.storage = storage;
        this.profileApplicationService = profileApplicationService;
        this.helper = helper;
    }

    public InterviewSessionDto start(String userId, InterviewStartRequest request) {
        String safeUserId = requireUserId(userId);
        InterviewStartRequest safeRequest = request == null ? new InterviewStartRequest() : request;
        InterviewSessionDto interview = new InterviewSessionDto();
        interview.setUserId(safeUserId);
        interview.setResumeId(safeRequest.getResumeId());
        interview.setPositionName(trimToNull(safeRequest.getPositionName()));
        interview.setDifficulty(helper.normalizeDifficulty(safeRequest.getDifficulty()));
        interview.setMode(helper.normalizeMode(safeRequest.getMode()));
        interview.setStatus(InterviewConstants.STATUS_ONGOING);
        interview.setStartedAt(LocalDateTime.now());
        return storage.saveInterview(interview);
    }

    public InterviewMessageDto appendMessage(String userId, Long interviewId, InterviewMessageRequest request) {
        InterviewSessionDto interview = ownedInterview(userId, interviewId);
        if (request == null || !hasText(request.getContent())) {
            throw new IllegalArgumentException("message content is required");
        }
        InterviewMessageDto message = new InterviewMessageDto();
        message.setInterviewId(interview.getInterviewId());
        message.setRole(helper.normalizeRole(request.getRole()));
        message.setContent(request.getContent().trim());
        message.setCreatedAt(LocalDateTime.now());
        return storage.saveMessage(message);
    }

    public List<InterviewMessageDto> getMessages(String userId, Long interviewId) {
        InterviewSessionDto interview = ownedInterview(userId, interviewId);
        return storage.listMessages(interview.getInterviewId());
    }

    public InterviewSessionDto end(String userId, Long interviewId, Integer finalScore) {
        InterviewSessionDto interview = ownedInterview(userId, interviewId);
        LocalDateTime endedAt = LocalDateTime.now();
        interview.setStatus(InterviewConstants.STATUS_COMPLETED);
        interview.setEndedAt(endedAt);
        interview.setDurationSeconds(Integer.valueOf(helper.durationSeconds(interview.getStartedAt(), endedAt)));
        if (finalScore != null) {
            interview.setFinalScore(finalScore);
        }
        InterviewSessionDto saved = storage.saveInterview(interview);
        syncProfile(userId, saved);
        return saved;
    }

    public InterviewReportDto saveReport(String userId, Long interviewId, InterviewReportDto report) {
        InterviewSessionDto interview = ownedInterview(userId, interviewId);
        if (report == null) {
            throw new IllegalArgumentException("report is required");
        }
        report.setInterviewId(interview.getInterviewId());
        report.setPositionName(firstText(report.getPositionName(), interview.getPositionName()));
        report.setDifficulty(firstText(report.getDifficulty(), interview.getDifficulty()));
        report.setMode(firstText(report.getMode(), interview.getMode()));
        report.setDurationSeconds(firstInteger(report.getDurationSeconds(), interview.getDurationSeconds()));
        interview.setReport(report);
        if (report.getOverallScore() != null) {
            interview.setFinalScore(report.getOverallScore());
        }
        InterviewSessionDto saved = storage.saveInterview(interview);
        syncProfile(userId, saved);
        return saved.getReport();
    }

    public InterviewReportDto getReport(String userId, Long interviewId) {
        InterviewSessionDto interview = ownedInterview(userId, interviewId);
        return interview.getReport();
    }

    public List<InterviewSessionDto> listByUser(String userId) {
        return storage.listByUser(requireUserId(userId));
    }

    public InterviewSessionDto get(String userId, Long interviewId) {
        return ownedInterview(userId, interviewId);
    }

    public void delete(String userId, Long interviewId) {
        InterviewSessionDto interview = ownedInterview(userId, interviewId);
        storage.deleteMessages(interview.getInterviewId());
        storage.deleteInterview(interview.getInterviewId());
    }

    private void syncProfile(String userId, InterviewSessionDto interview) {
        UserProfileSnapshot.InterviewBlock block = helper.toInterviewBlock(interview);
        if (block != null) {
            profileApplicationService.saveInterview(userId, block);
        }
    }

    private InterviewSessionDto ownedInterview(String userId, Long interviewId) {
        String safeUserId = requireUserId(userId);
        if (interviewId == null) {
            throw new IllegalArgumentException("interviewId is required");
        }
        InterviewSessionDto interview = storage.loadInterview(interviewId);
        if (interview == null || !safeUserId.equals(interview.getUserId())) {
            throw new IllegalArgumentException("interview does not exist or is not owned by user");
        }
        return interview;
    }

    private String requireUserId(String userId) {
        if (!hasText(userId)) {
            throw new IllegalArgumentException("userId is required");
        }
        return userId.trim();
    }

    private String firstText(String first, String second) {
        return hasText(first) ? first.trim() : second;
    }

    private Integer firstInteger(Integer first, Integer second) {
        return first != null ? first : second;
    }

    private String trimToNull(String value) {
        if (!hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
