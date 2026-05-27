package v620.cc001.cloud01.app01.mservice;

import v620.base.helper.career.AssistantChatHelper;
import v620.cc001.base.common.dto.career.AssistantChatAppendMessageRequest;
import v620.cc001.base.common.dto.career.AssistantChatConstants;
import v620.cc001.base.common.dto.career.AssistantChatCreateSessionRequest;
import v620.cc001.base.common.dto.career.AssistantChatMessageDto;
import v620.cc001.base.common.dto.career.AssistantChatRequest;
import v620.cc001.base.common.dto.career.AssistantChatResponse;
import v620.cc001.base.common.dto.career.AssistantChatSessionDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Application boundary for assistant chat turns and history.
 */
public class AssistantChatApplicationService {

    private final AssistantChatStorage storage;
    private final AssistantChatGenerator generator;
    private final AssistantChatContextProvider contextProvider;
    private final AssistantChatHelper helper;

    public AssistantChatApplicationService() {
        this(new FileAssistantChatStorage(),
                new UnavailableAssistantChatGenerator(),
                new EmptyAssistantChatContextProvider(),
                new AssistantChatHelper());
    }

    public AssistantChatApplicationService(AssistantChatStorage storage,
                                           AssistantChatGenerator generator,
                                           AssistantChatContextProvider contextProvider,
                                           AssistantChatHelper helper) {
        this.storage = storage;
        this.generator = generator;
        this.contextProvider = contextProvider;
        this.helper = helper;
    }

    public AssistantChatResponse send(String userId, AssistantChatRequest request) {
        String safeUserId = requireUserId(userId);
        AssistantChatRequest safeRequest = request == null ? new AssistantChatRequest() : request;
        helper.requireMessage(safeRequest.getMessage(), "message is required");
        String persona = helper.normalizePersona(safeRequest.getPersona());
        if (safeRequest.getSessionId() != null) {
            ownedSession(safeUserId, safeRequest.getSessionId());
        }
        List<AssistantChatMessageDto> messages = helper.buildMessages(safeRequest.getHistory(),
                safeRequest.getMessage(),
                persona,
                contextProvider.renderProfile(safeUserId),
                contextProvider.renderMemory(safeUserId, persona),
                contextProvider.renderFacts(safeUserId));
        AssistantChatResponse response = generator.chat(safeUserId, persona, messages);
        if (response == null) {
            throw new IllegalStateException("assistant chat generator returned empty response");
        }
        response.setPersona(persona);
        if (response.getModelName() == null) {
            response.setModelName(AssistantChatConstants.DEFAULT_MODEL_NAME);
        }
        return response;
    }

    public AssistantChatSessionDto createSession(String userId, AssistantChatCreateSessionRequest request) {
        String safeUserId = requireUserId(userId);
        AssistantChatCreateSessionRequest safeRequest = request == null ? new AssistantChatCreateSessionRequest() : request;
        LocalDateTime now = LocalDateTime.now();
        AssistantChatSessionDto session = new AssistantChatSessionDto();
        session.setUserId(safeUserId);
        session.setTitle(helper.defaultTitle(safeRequest.getTitle()));
        session.setPersona(helper.normalizePersona(safeRequest.getPersona()));
        session.setModelName(hasText(safeRequest.getModelName()) ? safeRequest.getModelName().trim() : AssistantChatConstants.DEFAULT_MODEL_NAME);
        session.setCreatedAt(now);
        session.setUpdatedAt(now);
        return storage.saveSession(session);
    }

    public List<AssistantChatSessionDto> listSessions(String userId) {
        return storage.listSessions(requireUserId(userId));
    }

    public List<AssistantChatMessageDto> getMessages(String userId, Long sessionId) {
        AssistantChatSessionDto session = ownedSession(userId, sessionId);
        return storage.listMessages(session.getSessionId());
    }

    public AssistantChatSessionDto appendMessage(String userId, Long sessionId, AssistantChatAppendMessageRequest request) {
        AssistantChatSessionDto session = ownedSession(userId, sessionId);
        if (request == null) {
            throw new IllegalArgumentException("append message request is required");
        }
        helper.requireMessage(request.getUserMessage(), "userMessage is required");
        helper.requireMessage(request.getAssistantReply(), "assistantReply is required");
        LocalDateTime now = LocalDateTime.now();
        storage.saveMessage(message(session.getSessionId(), AssistantChatConstants.ROLE_USER, request.getUserMessage().trim(), now,
                null, null, null, null));
        storage.saveMessage(message(session.getSessionId(), AssistantChatConstants.ROLE_ASSISTANT, request.getAssistantReply().trim(), now,
                request.getPromptTokens(), request.getCompletionTokens(), request.getTotalTokens(), request.getCostMicros()));
        if (helper.isDefaultTitle(session.getTitle())) {
            session.setTitle(helper.titleFromFirstMessage(request.getUserMessage()));
        }
        session.setPersona(helper.normalizePersona(firstText(request.getPersona(), session.getPersona())));
        session.setUpdatedAt(now);
        return storage.saveSession(session);
    }

    public void deleteSession(String userId, Long sessionId) {
        AssistantChatSessionDto session = ownedSession(userId, sessionId);
        storage.deleteMessages(session.getSessionId());
        storage.deleteSession(session.getSessionId());
    }

    public AssistantChatSessionDto getSession(String userId, Long sessionId) {
        return ownedSession(userId, sessionId);
    }

    private AssistantChatMessageDto message(Long sessionId,
                                            String role,
                                            String content,
                                            LocalDateTime createdAt,
                                            Integer promptTokens,
                                            Integer completionTokens,
                                            Integer totalTokens,
                                            Long costMicros) {
        AssistantChatMessageDto message = new AssistantChatMessageDto();
        message.setSessionId(sessionId);
        message.setRole(role);
        message.setContent(content);
        message.setCreatedAt(createdAt);
        message.setPromptTokens(promptTokens == null ? Integer.valueOf(AssistantChatConstants.DEFAULT_PROMPT_TOKENS) : promptTokens);
        message.setCompletionTokens(completionTokens == null ? Integer.valueOf(AssistantChatConstants.DEFAULT_COMPLETION_TOKENS) : completionTokens);
        message.setTotalTokens(totalTokens == null ? Integer.valueOf(AssistantChatConstants.DEFAULT_TOTAL_TOKENS) : totalTokens);
        message.setCostMicros(costMicros == null ? Long.valueOf(AssistantChatConstants.DEFAULT_COST_MICROS) : costMicros);
        return message;
    }

    private AssistantChatSessionDto ownedSession(String userId, Long sessionId) {
        String safeUserId = requireUserId(userId);
        if (sessionId == null) {
            throw new IllegalArgumentException("sessionId is required");
        }
        AssistantChatSessionDto session = storage.loadSession(sessionId);
        if (session == null || !safeUserId.equals(session.getUserId())) {
            throw new IllegalArgumentException("assistant session does not exist or is not owned by user");
        }
        return session;
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

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
