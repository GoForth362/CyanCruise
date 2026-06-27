package v620.cc001.cloud01.app01.webapi.assistant;

import kd.bos.openapi.common.custom.annotation.ApiController;
import kd.bos.openapi.common.custom.annotation.ApiMapping;
import kd.bos.openapi.common.custom.annotation.ApiPostMapping;
import kd.bos.openapi.common.custom.annotation.ApiRequestBody;
import kd.bos.openapi.common.custom.annotation.ApiResponseBody;
import v620.cc001.base.common.dto.career.AssistantChatAppendMessageRequest;
import v620.cc001.base.common.dto.career.AssistantChatCreateSessionRequest;
import v620.cc001.base.common.dto.career.AssistantChatMessageDto;
import v620.cc001.base.common.dto.career.AssistantChatRequest;
import v620.cc001.base.common.dto.career.AssistantChatResponse;
import v620.cc001.base.common.dto.career.AssistantChatSessionDto;
import v620.cc001.cloud01.app01.mservice.application.AssistantChatApplicationService;

import java.util.List;

/**
 * WebAPI entry for migrated assistant chat capability.
 */
@ApiController(value = "assistantChatWebApi", desc = "助手聊天 API")
@ApiMapping("/cc001/assistant-chat")
public class AssistantChatWebApi {

    private final AssistantChatApplicationService applicationService;

    public AssistantChatWebApi() {
        this(new AssistantChatApplicationService());
    }

    public AssistantChatWebApi(AssistantChatApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @ApiPostMapping(value = "/send", desc = "发送助手聊天消息", methodParamNames = {"userId", "request"})
    public @ApiResponseBody(value = "助手回复") AssistantChatResponse send(
            @ApiRequestBody(value = "用户ID", required = true) String userId,
            @ApiRequestBody(value = "聊天请求", required = true) AssistantChatRequest request) {
        return applicationService.send(userId, request);
    }

    @ApiPostMapping(value = "/session/create", desc = "创建助手会话", methodParamNames = {"userId", "request"})
    public @ApiResponseBody(value = "助手会话") AssistantChatSessionDto createSession(
            @ApiRequestBody(value = "用户ID", required = true) String userId,
            @ApiRequestBody(value = "创建会话请求", required = true) AssistantChatCreateSessionRequest request) {
        return applicationService.createSession(userId, request);
    }

    @ApiPostMapping(value = "/session/list", desc = "查询助手会话", methodParamNames = {"userId"})
    public @ApiResponseBody(value = "助手会话列表") List<AssistantChatSessionDto> listSessions(
            @ApiRequestBody(value = "用户ID", required = true) String userId) {
        return applicationService.listSessions(userId);
    }

    @ApiPostMapping(value = "/session/messages", desc = "读取助手会话消息", methodParamNames = {"userId", "sessionId"})
    public @ApiResponseBody(value = "助手消息列表") List<AssistantChatMessageDto> messages(
            @ApiRequestBody(value = "用户ID", required = true) String userId,
            @ApiRequestBody(value = "会话ID", required = true) Long sessionId) {
        return applicationService.getMessages(userId, sessionId);
    }

    @ApiPostMapping(value = "/session/append", desc = "追加助手消息对", methodParamNames = {"userId", "sessionId", "request"})
    public @ApiResponseBody(value = "助手会话") AssistantChatSessionDto append(
            @ApiRequestBody(value = "用户ID", required = true) String userId,
            @ApiRequestBody(value = "会话ID", required = true) Long sessionId,
            @ApiRequestBody(value = "追加消息请求", required = true) AssistantChatAppendMessageRequest request) {
        return applicationService.appendMessage(userId, sessionId, request);
    }

    @ApiPostMapping(value = "/session/delete", desc = "删除助手会话", methodParamNames = {"userId", "sessionId"})
    public @ApiResponseBody(value = "删除结果") String delete(
            @ApiRequestBody(value = "用户ID", required = true) String userId,
            @ApiRequestBody(value = "会话ID", required = true) Long sessionId) {
        applicationService.deleteSession(userId, sessionId);
        return "OK";
    }
}
