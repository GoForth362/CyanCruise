package v620.cc001.cloud01.app01.webapi.furtherstudy;

import v620.cc001.cloud01.app01.mservice.auth.impl.IdentityAwareCyanCruiseWebApiBoundary;
import kd.bos.openapi.common.custom.annotation.ApiController;
import kd.bos.openapi.common.custom.annotation.ApiMapping;
import kd.bos.openapi.common.custom.annotation.ApiPostMapping;
import kd.bos.openapi.common.custom.annotation.ApiRequestBody;
import kd.bos.openapi.common.custom.annotation.ApiResponseBody;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyEventDto;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyMaterialDto;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyMaterialSaveRequest;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyRecordDetailDto;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyRecordQueryRequest;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyRecordStatusUpdateRequest;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyRecordSummaryDto;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyTargetDto;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyTargetSaveRequest;
import v620.cc001.cloud01.app01.mservice.auth.impl.IdentityAwareCyanCruiseWebApiBoundary;
import v620.cc001.cloud01.app01.mservice.furtherstudy.FurtherStudyApplicationService;

import java.util.List;

@ApiController(value = "furtherStudyWebApi", desc = "Further study companion API")
@ApiMapping("/cc001/further-study")
public class FurtherStudyWebApi {

    private final FurtherStudyApplicationService applicationService;
    private final IdentityAwareCyanCruiseWebApiBoundary identityBoundary;

    public FurtherStudyWebApi() {
        this(new FurtherStudyApplicationService(), new IdentityAwareCyanCruiseWebApiBoundary());
    }

    public FurtherStudyWebApi(FurtherStudyApplicationService applicationService,
                              IdentityAwareCyanCruiseWebApiBoundary identityBoundary) {
        this.applicationService = applicationService;
        this.identityBoundary = identityBoundary;
    }

    @ApiPostMapping(value = "/target/save", desc = "Save further-study target", methodParamNames = {"userId", "request"})
    public @ApiResponseBody(value = "Further-study target") FurtherStudyTargetDto saveTarget(
            @ApiRequestBody(value = "userId", required = true) String userId,
            @ApiRequestBody(value = "target", required = true) FurtherStudyTargetSaveRequest request) {
        return applicationService.saveTarget(identityBoundary.requireUser(userId), request);
    }

    @ApiPostMapping(value = "/records/list", desc = "List further-study records", methodParamNames = {"userId", "request"})
    public @ApiResponseBody(value = "Further-study record list") List<FurtherStudyRecordSummaryDto> listRecords(
            @ApiRequestBody(value = "userId", required = true) String userId,
            @ApiRequestBody(value = "query", required = false) FurtherStudyRecordQueryRequest request) {
        return applicationService.listRecords(identityBoundary.requireUser(userId), request);
    }

    @ApiPostMapping(value = "/records/detail", desc = "Load further-study record", methodParamNames = {"userId", "recordId"})
    public @ApiResponseBody(value = "Further-study record detail") FurtherStudyRecordDetailDto recordDetail(
            @ApiRequestBody(value = "userId", required = true) String userId,
            @ApiRequestBody(value = "recordId", required = true) String recordId) {
        return applicationService.loadRecord(identityBoundary.requireUser(userId), recordId);
    }

    @ApiPostMapping(value = "/records/status/update", desc = "Update further-study record status", methodParamNames = {"userId", "request"})
    public @ApiResponseBody(value = "Further-study record detail") FurtherStudyRecordDetailDto updateRecordStatus(
            @ApiRequestBody(value = "userId", required = true) String userId,
            @ApiRequestBody(value = "status update", required = true) FurtherStudyRecordStatusUpdateRequest request) {
        return applicationService.updateRecordStatus(identityBoundary.requireUser(userId), request);
    }

    @ApiPostMapping(value = "/materials/save", desc = "Save further-study material", methodParamNames = {"userId", "request"})
    public @ApiResponseBody(value = "Further-study material") FurtherStudyMaterialDto saveMaterial(
            @ApiRequestBody(value = "userId", required = true) String userId,
            @ApiRequestBody(value = "material", required = true) FurtherStudyMaterialSaveRequest request) {
        return applicationService.saveMaterial(identityBoundary.requireUser(userId), request);
    }

    @ApiPostMapping(value = "/materials/list", desc = "List further-study materials", methodParamNames = {"userId", "track", "recordId"})
    public @ApiResponseBody(value = "Further-study material list") List<FurtherStudyMaterialDto> listMaterials(
            @ApiRequestBody(value = "userId", required = true) String userId,
            @ApiRequestBody(value = "track", required = false) String track,
            @ApiRequestBody(value = "recordId", required = false) String recordId) {
        return applicationService.listMaterials(identityBoundary.requireUser(userId), track, recordId);
    }

    @ApiPostMapping(value = "/records/events", desc = "List further-study record events", methodParamNames = {"userId", "recordId"})
    public @ApiResponseBody(value = "Further-study event list") List<FurtherStudyEventDto> listEvents(
            @ApiRequestBody(value = "userId", required = true) String userId,
            @ApiRequestBody(value = "recordId", required = true) String recordId) {
        return applicationService.listEvents(identityBoundary.requireUser(userId), recordId);
    }
}
