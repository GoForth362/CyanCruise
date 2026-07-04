package v620.cc001.cloud01.app01.mservice.application;

import v620.cc001.cloud01.app01.mservice.ai.impl.DefaultAiGateway;
import v620.cc001.cloud01.app01.mservice.ai.InterviewAiService;
import v620.cc001.cloud01.app01.mservice.storage.CyanCruiseStorageFactory;
import v620.cc001.cloud01.app01.mservice.storage.InterviewStorage;
import v620.base.helper.career.InterviewCoreService;
import v620.cc001.base.common.dto.career.InterviewConstants;
import v620.cc001.base.common.dto.career.InterviewMessageDto;
import v620.cc001.base.common.dto.career.InterviewMessageRequest;
import v620.cc001.base.common.dto.career.InterviewPageResultDto;
import v620.cc001.base.common.dto.career.InterviewReportDto;
import v620.cc001.base.common.dto.career.InterviewSessionDto;
import v620.cc001.base.common.dto.career.InterviewStartResultDto;
import v620.cc001.base.common.dto.career.InterviewStartRequest;
import v620.cc001.base.common.dto.career.InterviewTurnResultDto;
import v620.cc001.base.common.dto.career.ResumeRecordDto;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;
import v620.cc001.cloud01.app01.mservice.ai.AiProviderAdapterFactory;
import v620.cc001.cloud01.app01.mservice.ai.impl.DefaultAiGateway;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Application boundary for mock interview sessions and report summaries.
 */
public class InterviewApplicationService {

    private final InterviewStorage storage;
    private final CareerProfileApplicationService profileApplicationService;
    private final InterviewCoreService helper;
    private final ResumeApplicationService resumeApplicationService;
    private final InterviewAiService aiService;

    public InterviewApplicationService() {
        this(CyanCruiseStorageFactory.interviewStorage(), new CareerProfileApplicationService(), new InterviewCoreService(),
                new ResumeApplicationService(), new InterviewAiService(new DefaultAiGateway(AiProviderAdapterFactory.fromSystemProperties())));
    }

    public InterviewApplicationService(InterviewStorage storage,
                                       CareerProfileApplicationService profileApplicationService,
                                       InterviewCoreService helper) {
        this(storage, profileApplicationService, helper, new ResumeApplicationService(),
                new InterviewAiService(new DefaultAiGateway(AiProviderAdapterFactory.fromSystemProperties())));
    }

    public InterviewApplicationService(InterviewStorage storage,
                                       CareerProfileApplicationService profileApplicationService,
                                       InterviewCoreService helper,
                                       ResumeApplicationService resumeApplicationService,
                                       InterviewAiService aiService) {
        this.storage = storage;
        this.profileApplicationService = profileApplicationService;
        this.helper = helper;
        this.resumeApplicationService = resumeApplicationService;
        this.aiService = aiService;
    }

    public InterviewStartResultDto startGuided(String userId, InterviewStartRequest request) {
        String safeUserId = requireUserId(userId);
        InterviewStartRequest safeRequest = request == null ? new InterviewStartRequest() : request;
        ResumeRecordDto resume = safeRequest.getResumeId() == null ? null : resumeApplicationService.get(safeUserId, safeRequest.getResumeId());
        UserProfileSnapshot snapshot = profileApplicationService.getSnapshot(safeUserId);
        if (!hasText(safeRequest.getPositionName())) {
            safeRequest.setPositionName(targetRole(snapshot, resume));
        }
        InterviewSessionDto session = start(safeUserId, safeRequest);
        String question = aiService.question(session.getPositionName(), session.getDifficulty(),
                resume == null ? null : resume.getParsedContent(), profileSummary(snapshot), "", 0, true);
        InterviewMessageDto opening = appendAiMessage(session, question);
        InterviewStartResultDto result = new InterviewStartResultDto();
        result.setSession(session); result.setOpeningMessage(opening); return result;
    }

    public InterviewTurnResultDto answer(String userId, Long interviewId, String answer) {
        InterviewSessionDto session = ownedInterview(userId, interviewId);
        if (!InterviewConstants.STATUS_ONGOING.equals(session.getStatus())) {
            throw new IllegalArgumentException("本次练习已经结束，不能继续回答");
        }
        List<InterviewMessageDto> existingMessages = getMessages(userId, interviewId);
        int existingAnswers = answerCount(existingMessages);
        if (existingAnswers >= InterviewConstants.MAX_AI_INTERVIEW_QUESTIONS) {
            throw new IllegalArgumentException("本次面试的 7 道题已经全部完成");
        }
        InterviewMessageRequest request = new InterviewMessageRequest(); request.setRole(InterviewConstants.ROLE_USER); request.setContent(answer);
        InterviewMessageDto userMessage = appendMessage(userId, interviewId, request);
        List<InterviewMessageDto> messages = getMessages(userId, interviewId);
        int answers = answerCount(messages);
        String question = answers >= InterviewConstants.MAX_AI_INTERVIEW_QUESTIONS
                ? "本次面试的 7 道题已经完成，正在为你生成复盘。"
                : aiService.question(session.getPositionName(), session.getDifficulty(), null, null,
                transcript(messages), answers, false);
        InterviewMessageDto interviewer = appendAiMessage(session, question);
        InterviewTurnResultDto result = new InterviewTurnResultDto();
        result.setSession(session); result.setUserMessage(userMessage); result.setInterviewerMessage(interviewer); return result;
    }

    public InterviewReportDto finishAndReport(String userId, Long interviewId) {
        InterviewSessionDto session = ownedInterview(userId, interviewId);
        if (session.getReport() != null) {
            if (InterviewConstants.STATUS_ONGOING.equals(session.getStatus())) {
                end(userId, interviewId, session.getReport().getOverallScore());
            }
            return session.getReport();
        }
        List<InterviewMessageDto> messages = getMessages(userId, interviewId);
        int answers = answerCount(messages);
        if (answers == 0) throw new IllegalArgumentException("请至少完成一道回答后再查看复盘");
        if (InterviewConstants.STATUS_ONGOING.equals(session.getStatus())) session = end(userId, interviewId, null);
        InterviewReportDto report = aiService.report(session, transcript(messages), answers);
        return saveReport(userId, interviewId, report);
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

    public InterviewPageResultDto listPage(String userId, int page, String mode) {
        int safePage = Math.max(1, page);
        List<InterviewSessionDto> filtered = new ArrayList<InterviewSessionDto>();
        for (InterviewSessionDto session : storage.listByUser(requireUserId(userId))) {
            if (matchesPageMode(session, mode)) filtered.add(session);
        }
        int size = InterviewConstants.INTERVIEW_HISTORY_PAGE_SIZE;
        int total = filtered.size();
        int totalPages = total == 0 ? 0 : (total + size - 1) / size;
        if (totalPages > 0 && safePage > totalPages) safePage = totalPages;
        int from = Math.min(total, (safePage - 1) * size);
        int to = Math.min(total, from + size);
        InterviewPageResultDto result = new InterviewPageResultDto();
        result.setItems(new ArrayList<InterviewSessionDto>(filtered.subList(from, to)));
        result.setPage(Integer.valueOf(safePage));
        result.setSize(Integer.valueOf(size));
        result.setTotal(Integer.valueOf(total));
        result.setTotalPages(Integer.valueOf(totalPages));
        return result;
    }

    public InterviewSessionDto get(String userId, Long interviewId) {
        return ownedInterview(userId, interviewId);
    }

    public void delete(String userId, Long interviewId) {
        InterviewSessionDto interview = ownedInterview(userId, interviewId);
        storage.deleteMessages(interview.getInterviewId());
        storage.deleteInterview(interview.getInterviewId());
    }

    private boolean matchesPageMode(InterviewSessionDto session, String mode) {
        if (!hasText(mode)) return true;
        String requested = mode.trim().toUpperCase();
        String actual = session == null || session.getMode() == null ? "" : session.getMode().trim().toUpperCase();
        if (InterviewConstants.MODE_TEXT.equals(requested)) {
            return actual.length() == 0 || InterviewConstants.MODE_TEXT.equals(actual);
        }
        if (InterviewConstants.MODE_VOICE.equals(requested)) {
            return InterviewConstants.MODE_VOICE.equals(actual) || "PANORAMA".equals(actual);
        }
        return requested.equals(actual);
    }

    private void syncProfile(String userId, InterviewSessionDto interview) {
        UserProfileSnapshot.InterviewBlock block = helper.toInterviewBlock(interview);
        if (block != null) {
            profileApplicationService.saveInterview(userId, block);
        }
    }

    private InterviewMessageDto appendAiMessage(InterviewSessionDto session, String content) {
        InterviewMessageDto message = new InterviewMessageDto(); message.setInterviewId(session.getInterviewId());
        message.setRole(InterviewConstants.ROLE_AI); message.setContent(content); message.setCreatedAt(LocalDateTime.now());
        return storage.saveMessage(message);
    }

    private int answerCount(List<InterviewMessageDto> messages) {
        int count = 0; for (InterviewMessageDto message : messages) if (InterviewConstants.ROLE_USER.equals(message.getRole())) count++; return count;
    }

    private String transcript(List<InterviewMessageDto> messages) {
        StringBuilder value = new StringBuilder();
        for (InterviewMessageDto message : messages) value.append(InterviewConstants.ROLE_USER.equals(message.getRole()) ? "[候选人] " : "[面试官] ").append(message.getContent()).append('\n');
        return value.toString();
    }

    private String targetRole(UserProfileSnapshot snapshot, ResumeRecordDto resume) {
        if (resume != null && hasText(resume.getTargetJob())) return resume.getTargetJob();
        if (snapshot != null && snapshot.getPreferences() != null && hasText(snapshot.getPreferences().getTargetRole())) return snapshot.getPreferences().getTargetRole();
        if (snapshot != null && snapshot.getResume() != null && hasText(snapshot.getResume().getTargetJob())) return snapshot.getResume().getTargetJob();
        return "待确认岗位";
    }

    private String profileSummary(UserProfileSnapshot snapshot) {
        if (snapshot == null) return ""; StringBuilder value = new StringBuilder();
        if (snapshot.getAssessment() != null) value.append("测评摘要：").append(firstText(snapshot.getAssessment().getSummary(), "")).append('；');
        if (snapshot.getOnboarding() != null) value.append("当前阶段：").append(firstText(snapshot.getOnboarding().getStage(), "")).append("；经历：").append(firstText(snapshot.getOnboarding().getExperience(), ""));
        return value.toString();
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
