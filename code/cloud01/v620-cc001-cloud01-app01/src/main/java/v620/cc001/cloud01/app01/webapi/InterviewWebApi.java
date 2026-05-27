package v620.cc001.cloud01.app01.webapi;

import kd.bos.openapi.common.custom.annotation.ApiController;
import kd.bos.openapi.common.custom.annotation.ApiMapping;
import kd.bos.openapi.common.custom.annotation.ApiPostMapping;
import kd.bos.openapi.common.custom.annotation.ApiRequestBody;
import kd.bos.openapi.common.custom.annotation.ApiResponseBody;
import v620.cc001.base.common.dto.career.InterviewMessageDto;
import v620.cc001.base.common.dto.career.InterviewMessageRequest;
import v620.cc001.base.common.dto.career.InterviewReportDto;
import v620.cc001.base.common.dto.career.InterviewSessionDto;
import v620.cc001.base.common.dto.career.InterviewStartRequest;
import v620.cc001.cloud01.app01.mservice.InterviewApplicationService;

import java.util.List;

/**
 * WebAPI entry for migrated mock interview core capability.
 */
@ApiController(value = "interviewWebApi", desc = "模拟面试 API")
@ApiMapping("/cc001/interview")
public class InterviewWebApi {

    private final InterviewApplicationService applicationService;

    public InterviewWebApi() {
        this(new InterviewApplicationService());
    }

    InterviewWebApi(InterviewApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @ApiPostMapping(value = "/start", desc = "开始模拟面试", methodParamNames = {"userId", "request"})
    public @ApiResponseBody(value = "面试会话") InterviewSessionDto start(
            @ApiRequestBody(value = "用户ID", required = true) String userId,
            @ApiRequestBody(value = "开始面试请求", required = true) InterviewStartRequest request) {
        return applicationService.start(userId, request);
    }

    @ApiPostMapping(value = "/message/add", desc = "追加面试消息", methodParamNames = {"userId", "interviewId", "request"})
    public @ApiResponseBody(value = "面试消息") InterviewMessageDto addMessage(
            @ApiRequestBody(value = "用户ID", required = true) String userId,
            @ApiRequestBody(value = "面试ID", required = true) Long interviewId,
            @ApiRequestBody(value = "消息请求", required = true) InterviewMessageRequest request) {
        return applicationService.appendMessage(userId, interviewId, request);
    }

    @ApiPostMapping(value = "/messages", desc = "读取面试消息", methodParamNames = {"userId", "interviewId"})
    public @ApiResponseBody(value = "面试消息列表") List<InterviewMessageDto> messages(
            @ApiRequestBody(value = "用户ID", required = true) String userId,
            @ApiRequestBody(value = "面试ID", required = true) Long interviewId) {
        return applicationService.getMessages(userId, interviewId);
    }

    @ApiPostMapping(value = "/end", desc = "结束模拟面试", methodParamNames = {"userId", "interviewId", "finalScore"})
    public @ApiResponseBody(value = "面试会话") InterviewSessionDto end(
            @ApiRequestBody(value = "用户ID", required = true) String userId,
            @ApiRequestBody(value = "面试ID", required = true) Long interviewId,
            @ApiRequestBody(value = "最终分数", required = false) Integer finalScore) {
        return applicationService.end(userId, interviewId, finalScore);
    }

    @ApiPostMapping(value = "/report/save", desc = "保存面试报告", methodParamNames = {"userId", "interviewId", "report"})
    public @ApiResponseBody(value = "面试报告") InterviewReportDto saveReport(
            @ApiRequestBody(value = "用户ID", required = true) String userId,
            @ApiRequestBody(value = "面试ID", required = true) Long interviewId,
            @ApiRequestBody(value = "面试报告", required = true) InterviewReportDto report) {
        return applicationService.saveReport(userId, interviewId, report);
    }

    @ApiPostMapping(value = "/report/get", desc = "读取面试报告", methodParamNames = {"userId", "interviewId"})
    public @ApiResponseBody(value = "面试报告") InterviewReportDto report(
            @ApiRequestBody(value = "用户ID", required = true) String userId,
            @ApiRequestBody(value = "面试ID", required = true) Long interviewId) {
        return applicationService.getReport(userId, interviewId);
    }

    @ApiPostMapping(value = "/list", desc = "读取用户面试历史", methodParamNames = {"userId"})
    public @ApiResponseBody(value = "面试历史") List<InterviewSessionDto> list(
            @ApiRequestBody(value = "用户ID", required = true) String userId) {
        return applicationService.listByUser(userId);
    }

    @ApiPostMapping(value = "/get", desc = "读取面试详情", methodParamNames = {"userId", "interviewId"})
    public @ApiResponseBody(value = "面试详情") InterviewSessionDto get(
            @ApiRequestBody(value = "用户ID", required = true) String userId,
            @ApiRequestBody(value = "面试ID", required = true) Long interviewId) {
        return applicationService.get(userId, interviewId);
    }

    @ApiPostMapping(value = "/delete", desc = "删除模拟面试", methodParamNames = {"userId", "interviewId"})
    public @ApiResponseBody(value = "删除结果") String delete(
            @ApiRequestBody(value = "用户ID", required = true) String userId,
            @ApiRequestBody(value = "面试ID", required = true) Long interviewId) {
        applicationService.delete(userId, interviewId);
        return "OK";
    }
}
