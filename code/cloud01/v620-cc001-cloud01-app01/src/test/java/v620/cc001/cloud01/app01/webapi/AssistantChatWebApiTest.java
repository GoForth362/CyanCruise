package v620.cc001.cloud01.app01.webapi;

import org.junit.jupiter.api.Test;
import v620.base.helper.career.AssistantChatHelper;
import v620.cc001.base.common.dto.career.AssistantChatAppendMessageRequest;
import v620.cc001.base.common.dto.career.AssistantChatCreateSessionRequest;
import v620.cc001.base.common.dto.career.AssistantChatMessageDto;
import v620.cc001.base.common.dto.career.AssistantChatRequest;
import v620.cc001.base.common.dto.career.AssistantChatResponse;
import v620.cc001.base.common.dto.career.AssistantChatSessionDto;
import v620.cc001.cloud01.app01.mservice.AssistantChatApplicationService;
import v620.cc001.cloud01.app01.mservice.AssistantChatContextProvider;
import v620.cc001.cloud01.app01.mservice.AssistantChatGenerator;
import v620.cc001.cloud01.app01.mservice.AssistantChatStorage;
import v620.cc001.cloud01.app01.mservice.EmptyAssistantChatContextProvider;
import v620.cc001.cloud01.app01.mservice.InMemoryAssistantChatStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AssistantChatWebApiTest {

    @Test
    void webApiSendsAndManagesHistory() {
        AssistantChatWebApi api = api(new InMemoryAssistantChatStorage(), new EchoGenerator(),
                new EmptyAssistantChatContextProvider());
        AssistantChatRequest chatRequest = new AssistantChatRequest();
        chatRequest.setMessage("给我一个下一步");
        chatRequest.setPersona("MENTOR");

        AssistantChatResponse response = api.send("api-user-1", chatRequest);
        AssistantChatSessionDto session = api.createSession("api-user-1", createRequest());
        AssistantChatSessionDto appended = api.append("api-user-1", session.getSessionId(),
                append("第一问", response.getReply()));
        List<AssistantChatMessageDto> messages = api.messages("api-user-1", session.getSessionId());

        assertEquals("echo:给我一个下一步", response.getReply());
        assertEquals(1, api.listSessions("api-user-1").size());
        assertEquals("第一问", appended.getTitle());
        assertEquals(2, messages.size());
        assertEquals("OK", api.delete("api-user-1", session.getSessionId()));
        assertEquals(0, api.listSessions("api-user-1").size());
    }

    @Test
    void webApiRejectsCrossUserSessionAccess() {
        AssistantChatWebApi api = api(new InMemoryAssistantChatStorage(), new EchoGenerator(),
                new EmptyAssistantChatContextProvider());
        AssistantChatSessionDto session = api.createSession("owner-user", createRequest());

        assertThrows(IllegalArgumentException.class, new ThrowingRunnableAdapter(new Runnable() {
            public void run() {
                api.messages("other-user", session.getSessionId());
            }
        }));
    }

    private AssistantChatWebApi api(AssistantChatStorage storage,
                                    AssistantChatGenerator generator,
                                    AssistantChatContextProvider contextProvider) {
        return new AssistantChatWebApi(new AssistantChatApplicationService(storage, generator, contextProvider,
                new AssistantChatHelper()));
    }

    private AssistantChatCreateSessionRequest createRequest() {
        AssistantChatCreateSessionRequest request = new AssistantChatCreateSessionRequest();
        request.setTitle("新的求职对话");
        return request;
    }

    private AssistantChatAppendMessageRequest append(String userMessage, String assistantReply) {
        AssistantChatAppendMessageRequest request = new AssistantChatAppendMessageRequest();
        request.setUserMessage(userMessage);
        request.setAssistantReply(assistantReply);
        return request;
    }

    private static class EchoGenerator implements AssistantChatGenerator {
        public AssistantChatResponse chat(String userId, String persona, List<AssistantChatMessageDto> messages) {
            AssistantChatResponse response = new AssistantChatResponse();
            response.setReply("echo:" + messages.get(messages.size() - 1).getContent());
            return response;
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
