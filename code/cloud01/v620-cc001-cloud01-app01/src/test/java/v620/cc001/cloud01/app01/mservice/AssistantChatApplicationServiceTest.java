package v620.cc001.cloud01.app01.mservice;

import v620.cc001.cloud01.app01.mservice.ai.impl.EmptyAssistantChatContextProvider;
import v620.cc001.cloud01.app01.mservice.ai.impl.UnavailableAssistantChatGenerator;
import v620.cc001.cloud01.app01.mservice.storage.impl.FileAssistantChatStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryAssistantChatStorage;
import v620.cc001.cloud01.app01.mservice.ai.AssistantChatContextProvider;
import v620.cc001.cloud01.app01.mservice.ai.AssistantChatGenerator;
import v620.cc001.cloud01.app01.mservice.ai.impl.EmptyAssistantChatContextProvider;
import v620.cc001.cloud01.app01.mservice.ai.impl.UnavailableAssistantChatGenerator;
import v620.cc001.cloud01.app01.mservice.application.AssistantChatApplicationService;
import v620.cc001.cloud01.app01.mservice.storage.AssistantChatStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.FileAssistantChatStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryAssistantChatStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import v620.base.helper.career.AssistantChatHelper;
import v620.cc001.base.common.dto.career.AssistantChatAppendMessageRequest;
import v620.cc001.base.common.dto.career.AssistantChatConstants;
import v620.cc001.base.common.dto.career.AssistantChatCreateSessionRequest;
import v620.cc001.base.common.dto.career.AssistantChatMessageDto;
import v620.cc001.base.common.dto.career.AssistantChatRequest;
import v620.cc001.base.common.dto.career.AssistantChatResponse;
import v620.cc001.base.common.dto.career.AssistantChatSessionDto;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AssistantChatApplicationServiceTest {

    @TempDir
    File tempDir;

    @Test
    void sendBuildsContextAndReturnsGeneratorReply() {
        CapturingGenerator generator = new CapturingGenerator();
        AssistantChatApplicationService service = service(new InMemoryAssistantChatStorage(), generator,
                new FixedContextProvider());
        AssistantChatRequest request = new AssistantChatRequest();
        request.setPersona("challenger");
        request.setMessage("帮我看简历");

        AssistantChatResponse response = service.send("chat-user-1", request);

        assertEquals("reply:帮我看简历", response.getReply());
        assertEquals(AssistantChatConstants.PERSONA_CHALLENGER, response.getPersona());
        assertEquals(2, generator.lastMessages.size());
        assertTrue(generator.lastMessages.get(0).getContent().contains("[PROFILE] Java 后端"));
        assertTrue(generator.lastMessages.get(0).getContent().contains("[MEMORY] 上次提到项目经历"));
        assertTrue(generator.lastMessages.get(0).getContent().contains("[FACTS] 熟悉 Spring"));
    }

    @Test
    void fileStorageReloadsSessionsAndMessagesFromFreshInstance() {
        AssistantChatApplicationService first = service(new FileAssistantChatStorage(tempDir), new CapturingGenerator(),
                new EmptyAssistantChatContextProvider());
        AssistantChatSessionDto session = first.createSession("chat-user-2", createRequest(null, "INTERVIEWER"));
        first.appendMessage("chat-user-2", session.getSessionId(), append("请面试我", "请先介绍一个项目"));

        AssistantChatApplicationService second = service(new FileAssistantChatStorage(tempDir), new CapturingGenerator(),
                new EmptyAssistantChatContextProvider());

        assertEquals(1, second.listSessions("chat-user-2").size());
        assertEquals(2, second.getMessages("chat-user-2", session.getSessionId()).size());
        assertEquals(AssistantChatConstants.PERSONA_INTERVIEWER,
                second.getSession("chat-user-2", session.getSessionId()).getPersona());
    }

    @Test
    void appendMessageUpdatesTitleAndTokenFields() {
        AssistantChatApplicationService service = service(new InMemoryAssistantChatStorage(), new CapturingGenerator(),
                new EmptyAssistantChatContextProvider());
        AssistantChatSessionDto session = service.createSession("chat-user-3", createRequest(null, null));
        AssistantChatAppendMessageRequest request = append("帮我优化 Java 后端简历并准备面试追问和复盘", "可以，先给我 JD");
        request.setPromptTokens(Integer.valueOf(10));
        request.setCompletionTokens(Integer.valueOf(20));
        request.setTotalTokens(Integer.valueOf(30));
        request.setCostMicros(Long.valueOf(40L));

        AssistantChatSessionDto saved = service.appendMessage("chat-user-3", session.getSessionId(), request);
        List<AssistantChatMessageDto> messages = service.getMessages("chat-user-3", session.getSessionId());

        assertEquals("帮我优化 Java 后端简历并准备面试追...", saved.getTitle());
        assertEquals(2, messages.size());
        assertEquals(Integer.valueOf(30), messages.get(1).getTotalTokens());
        assertEquals(Long.valueOf(40L), messages.get(1).getCostMicros());
    }

    @Test
    void rejectsCrossUserOperationsAndDeletesMessages() {
        AssistantChatApplicationService service = service(new InMemoryAssistantChatStorage(), new CapturingGenerator(),
                new EmptyAssistantChatContextProvider());
        AssistantChatSessionDto session = service.createSession("owner-user", createRequest("会话", "MENTOR"));
        service.appendMessage("owner-user", session.getSessionId(), append("hello", "hi"));

        assertThrows(IllegalArgumentException.class, new ThrowingRunnableAdapter(new Runnable() {
            public void run() {
                service.getMessages("other-user", session.getSessionId());
            }
        }));
        assertThrows(IllegalArgumentException.class, new ThrowingRunnableAdapter(new Runnable() {
            public void run() {
                service.appendMessage("other-user", session.getSessionId(), append("bad", "bad"));
            }
        }));

        service.deleteSession("owner-user", session.getSessionId());
        assertThrows(IllegalArgumentException.class, new ThrowingRunnableAdapter(new Runnable() {
            public void run() {
                service.getSession("owner-user", session.getSessionId());
            }
        }));
    }

    @Test
    void defaultGeneratorReportsUnavailable() {
        AssistantChatApplicationService service = service(new InMemoryAssistantChatStorage(),
                new UnavailableAssistantChatGenerator(), new EmptyAssistantChatContextProvider());
        AssistantChatRequest request = new AssistantChatRequest();
        request.setMessage("你好");

        assertThrows(IllegalStateException.class, new ThrowingRunnableAdapter(new Runnable() {
            public void run() {
                service.send("chat-user-4", request);
            }
        }));
    }

    private AssistantChatApplicationService service(AssistantChatStorage storage,
                                                    AssistantChatGenerator generator,
                                                    AssistantChatContextProvider contextProvider) {
        return new AssistantChatApplicationService(storage, generator, contextProvider, new AssistantChatHelper());
    }

    private AssistantChatCreateSessionRequest createRequest(String title, String persona) {
        AssistantChatCreateSessionRequest request = new AssistantChatCreateSessionRequest();
        request.setTitle(title);
        request.setPersona(persona);
        return request;
    }

    private AssistantChatAppendMessageRequest append(String userMessage, String assistantReply) {
        AssistantChatAppendMessageRequest request = new AssistantChatAppendMessageRequest();
        request.setUserMessage(userMessage);
        request.setAssistantReply(assistantReply);
        return request;
    }

    private static class CapturingGenerator implements AssistantChatGenerator {
        private List<AssistantChatMessageDto> lastMessages;

        public AssistantChatResponse chat(String userId, String persona, List<AssistantChatMessageDto> messages) {
            this.lastMessages = messages;
            AssistantChatResponse response = new AssistantChatResponse();
            response.setReply("reply:" + messages.get(messages.size() - 1).getContent());
            response.setModelName("test-model");
            return response;
        }
    }

    private static class FixedContextProvider implements AssistantChatContextProvider {
        public String renderProfile(String userId) {
            return "[PROFILE] Java 后端";
        }

        public String renderMemory(String userId, String persona) {
            return "[MEMORY] 上次提到项目经历";
        }

        public String renderFacts(String userId) {
            return "[FACTS] 熟悉 Spring";
        }
    }

    private static class ThrowingRunnableAdapter implements org.junit.jupiter.api.function.Executable {
        private final Runnable runnable;

        ThrowingRunnableAdapter(Runnable runnable) {
            this.runnable = runnable;
        }

        public void execute() {
            runnable.run();
        }
    }
}
