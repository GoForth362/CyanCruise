package v620.cc001.cloud01.app01.mservice.furtherstudy;

import v620.cc001.base.common.dto.furtherstudy.FurtherStudyEventDto;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyMaterialDto;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyMaterialSaveRequest;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyRecordDetailDto;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyRecordQueryRequest;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyRecordStatusUpdateRequest;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyRecordSummaryDto;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyTargetDto;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyTargetSaveRequest;
import v620.cc001.cloud01.app01.mservice.storage.CyanCruiseStorageFactory;

import java.util.List;

public class FurtherStudyApplicationService {

    private final FurtherStudyCompanionStorage storage;

    public FurtherStudyApplicationService() {
        this(CyanCruiseStorageFactory.furtherStudyCompanionStorage());
    }

    public FurtherStudyApplicationService(FurtherStudyCompanionStorage storage) {
        this.storage = storage;
    }

    public FurtherStudyTargetDto saveTarget(String userId, FurtherStudyTargetSaveRequest request) {
        String safeUserId = requireUserId(userId);
        if (request == null) {
            throw new IllegalArgumentException("Further-study target request is required.");
        }
        FurtherStudyTargetDto target = new FurtherStudyTargetDto();
        target.setTargetId(request.getTargetId());
        target.setTrack(request.getTrack());
        target.setTargetSchool(request.getTargetSchool());
        target.setTargetMajor(request.getTargetMajor());
        target.setTargetRegion(request.getTargetRegion());
        target.setTargetStage(request.getTargetStage());
        target.setStatus(request.getStatus());
        target.setTargetJson(request.getTargetJson());
        return storage.saveTarget(safeUserId, target);
    }

    public List<FurtherStudyRecordSummaryDto> listRecords(String userId, FurtherStudyRecordQueryRequest query) {
        return storage.listRecords(requireUserId(userId), query);
    }

    public FurtherStudyRecordDetailDto loadRecord(String userId, String recordId) {
        FurtherStudyRecordDetailDto record = storage.loadRecord(requireUserId(userId), recordId);
        if (record == null) {
            throw new IllegalArgumentException("Further-study record was not found for the current user.");
        }
        return record;
    }

    public FurtherStudyRecordDetailDto updateRecordStatus(String userId, FurtherStudyRecordStatusUpdateRequest request) {
        return storage.updateRecordStatus(requireUserId(userId), request);
    }

    public FurtherStudyMaterialDto saveMaterial(String userId, FurtherStudyMaterialSaveRequest request) {
        return storage.saveMaterial(requireUserId(userId), FurtherStudyRecordSupport.material(request));
    }

    public List<FurtherStudyMaterialDto> listMaterials(String userId, String track, String recordId) {
        return storage.listMaterials(requireUserId(userId), track, recordId);
    }

    public List<FurtherStudyEventDto> listEvents(String userId, String recordId) {
        return storage.listEvents(requireUserId(userId), recordId);
    }

    private String requireUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("Current user identity is required.");
        }
        return userId.trim();
    }
}
