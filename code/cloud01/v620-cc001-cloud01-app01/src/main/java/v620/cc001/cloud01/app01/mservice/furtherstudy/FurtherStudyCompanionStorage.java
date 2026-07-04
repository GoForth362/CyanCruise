package v620.cc001.cloud01.app01.mservice.furtherstudy;

import v620.cc001.base.common.dto.furtherstudy.FurtherStudyEventDto;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyMaterialDto;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyRecordDetailDto;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyRecordQueryRequest;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyRecordStatusUpdateRequest;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyRecordSummaryDto;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyTargetDto;

import java.util.List;

public interface FurtherStudyCompanionStorage {

    FurtherStudyTargetDto saveTarget(String userId, FurtherStudyTargetDto target);

    FurtherStudyRecordDetailDto saveRecord(String userId, FurtherStudyRecordDetailDto record);

    List<FurtherStudyRecordSummaryDto> listRecords(String userId, FurtherStudyRecordQueryRequest query);

    FurtherStudyRecordDetailDto loadRecord(String userId, String recordId);

    FurtherStudyRecordDetailDto updateRecordStatus(String userId, FurtherStudyRecordStatusUpdateRequest request);

    FurtherStudyMaterialDto saveMaterial(String userId, FurtherStudyMaterialDto material);

    List<FurtherStudyMaterialDto> listMaterials(String userId, String track, String recordId);

    FurtherStudyEventDto appendEvent(String userId, FurtherStudyEventDto event);

    List<FurtherStudyEventDto> listEvents(String userId, String recordId);
}
