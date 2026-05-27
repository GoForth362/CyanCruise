package v620.cc001.cloud01.app01.mservice;

import v620.cc001.base.common.dto.career.AssessmentScoreResult;
import v620.cc001.cloud01.app01.mservice.datamodel.CareerLoopDatamodelObjects;
import v620.cc001.cloud01.app01.mservice.datamodel.CosmicDatamodelGateway;
import v620.cc001.cloud01.app01.mservice.datamodel.CosmicDatamodelRecord;
import v620.cc001.cloud01.app01.mservice.datamodel.CosmicRecordFilter;
import v620.cc001.cloud01.app01.mservice.datamodel.DatamodelFieldMapper;
import v620.cc001.cloud01.app01.mservice.datamodel.DatamodelOwnershipGuard;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CosmicAssessmentResultStorage implements AssessmentResultStorage {

    private final CosmicDatamodelGateway gateway;

    public CosmicAssessmentResultStorage(CosmicDatamodelGateway gateway) {
        this.gateway = gateway;
    }

    public Long saveResult(String userId, AssessmentScoreResult result) {
        CosmicDatamodelRecord record = new CosmicDatamodelRecord(CareerLoopDatamodelObjects.ASSESSMENT_RECORD)
                .set(CareerLoopDatamodelObjects.USER_ID, userId)
                .set("scale_id", result.getScaleId())
                .set("scale_title", result.getScaleTitle())
                .set("status", result.getStatus())
                .set("result_summary", result.getResultSummary())
                .set("result_json", result.getDimensionCounts())
                .set("answers_json", result.getAnswers())
                .set(CareerLoopDatamodelObjects.CREATED_AT, LocalDateTime.now());
        return DatamodelFieldMapper.asLong(gateway.save(record).get(CareerLoopDatamodelObjects.ID));
    }

    public AssessmentScoreResult loadResult(String userId, Long recordId) {
        CosmicDatamodelRecord record = gateway.load(CareerLoopDatamodelObjects.ASSESSMENT_RECORD, recordId);
        DatamodelOwnershipGuard.requireUser(userId, record, CareerLoopDatamodelObjects.ASSESSMENT_RECORD);
        return toDto(record);
    }

    public List<AssessmentScoreResult> listResults(final String userId) {
        List<CosmicDatamodelRecord> rows = gateway.list(CareerLoopDatamodelObjects.ASSESSMENT_RECORD, new CosmicRecordFilter() {
            public boolean matches(CosmicDatamodelRecord record) {
                return DatamodelFieldMapper.same(userId, record.get(CareerLoopDatamodelObjects.USER_ID));
            }
        }, DatamodelFieldMapper.dateTimeDesc(CareerLoopDatamodelObjects.CREATED_AT));
        List<AssessmentScoreResult> result = new ArrayList<AssessmentScoreResult>();
        for (CosmicDatamodelRecord row : rows) {
            result.add(toDto(row));
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private AssessmentScoreResult toDto(CosmicDatamodelRecord record) {
        AssessmentScoreResult result = new AssessmentScoreResult();
        result.setScaleId(DatamodelFieldMapper.asLong(record.get("scale_id")));
        result.setScaleTitle(DatamodelFieldMapper.asString(record.get("scale_title")));
        result.setStatus(DatamodelFieldMapper.asString(record.get("status")));
        result.setResultSummary(DatamodelFieldMapper.asString(record.get("result_summary")));
        result.setDimensionCounts((java.util.Map<String, Integer>) record.get("result_json"));
        result.setAnswers((java.util.List<v620.cc001.base.common.dto.career.AssessmentAnswerSnapshot>) record.get("answers_json"));
        return result;
    }
}
