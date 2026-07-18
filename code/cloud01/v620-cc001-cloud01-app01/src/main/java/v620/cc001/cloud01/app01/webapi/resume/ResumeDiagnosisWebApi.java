package v620.cc001.cloud01.app01.webapi.resume;

import kd.bos.openapi.common.custom.annotation.ApiController;
import kd.bos.openapi.common.custom.annotation.ApiMapping;
import kd.bos.openapi.common.custom.annotation.ApiPostMapping;
import kd.bos.openapi.common.custom.annotation.ApiRequestBody;
import kd.bos.openapi.common.custom.annotation.ApiResponseBody;
import v620.cc001.base.common.dto.career.ResumeDiagnosisRequest;
import v620.cc001.base.common.dto.career.ResumeDiagnosisResultDto;
import v620.cc001.base.common.dto.career.ResumeKeywordStatusDto;
import v620.cc001.cloud01.app01.mservice.application.ResumeDiagnosisApplicationService;

import java.util.List;

/**
 * WebAPI boundary for migrated resume diagnosis.
 */
@ApiController(value = "resumeDiagnosisWebApi", desc = "简历诊断 API")
@ApiMapping("/cc001/resume-diagnosis")
public class ResumeDiagnosisWebApi {

    private final ResumeDiagnosisApplicationService applicationService;

    public ResumeDiagnosisWebApi() {
        this(new ResumeDiagnosisApplicationService());
    }

    public ResumeDiagnosisWebApi(ResumeDiagnosisApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @ApiPostMapping(value = "/analyze", desc = "触发简历诊断", methodParamNames = {"userId", "request"})
    public @ApiResponseBody(value = "简历诊断结果") ResumeDiagnosisResultDto analyze(
            @ApiRequestBody(value = "用户ID", required = true) String userId,
            @ApiRequestBody(value = "诊断请求", required = true) ResumeDiagnosisRequest request) {
        return applicationService.diagnose(userId, request);
    }

    @ApiPostMapping(value = "/history/list", desc = "resume diagnosis history", methodParamNames = {"userId", "resumeId"})
    public @ApiResponseBody(value = "resume diagnosis history") List<ResumeDiagnosisResultDto> listHistory(
            @ApiRequestBody(value = "user id", required = true) String userId,
            @ApiRequestBody(value = "resume id", required = true) Long resumeId) {
        return applicationService.listDiagnosisHistory(userId, resumeId);
    }

    @ApiPostMapping(value = "/history/delete", desc = "delete resume diagnosis history", methodParamNames = {"userId", "resumeId", "diagnosisId"})
    public @ApiResponseBody(value = "delete result") Boolean deleteHistory(
            @ApiRequestBody(value = "user id", required = true) String userId,
            @ApiRequestBody(value = "resume id", required = true) Long resumeId,
            @ApiRequestBody(value = "diagnosis id", required = true) Long diagnosisId) {
        return Boolean.valueOf(applicationService.deleteDiagnosisHistory(userId, resumeId, diagnosisId));
    }

    @ApiPostMapping(value = "/keywords/status", desc = "读取关键词状态", methodParamNames = {"userId", "resumeId"})
    public @ApiResponseBody(value = "关键词状态") ResumeKeywordStatusDto keywordStatus(
            @ApiRequestBody(value = "用户ID", required = true) String userId,
            @ApiRequestBody(value = "简历ID", required = true) Long resumeId) {
        return applicationService.getKeywordStatus(userId, resumeId);
    }

    @ApiPostMapping(value = "/keywords/extract", desc = "触发关键词抽取", methodParamNames = {"userId", "resumeId", "force"})
    public @ApiResponseBody(value = "关键词状态") ResumeKeywordStatusDto extractKeywords(
            @ApiRequestBody(value = "用户ID", required = true) String userId,
            @ApiRequestBody(value = "简历ID", required = true) Long resumeId,
            @ApiRequestBody(value = "强制重算", required = false) Boolean force) {
        return applicationService.triggerKeywordExtraction(userId, resumeId, force != null && force.booleanValue());
    }
}
