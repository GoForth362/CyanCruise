package v620.cc001.cloud01.app01.mservice;

import v620.cc001.base.common.dto.career.ResumeDiagnosisResultDto;
import v620.cc001.base.common.dto.career.ResumeKeywordStatusDto;
import v620.cc001.cloud01.app01.mservice.datamodel.CareerLoopDatamodelObjects;
import v620.cc001.cloud01.app01.mservice.datamodel.CosmicDatamodelGateway;
import v620.cc001.cloud01.app01.mservice.datamodel.CosmicDatamodelRecord;
import v620.cc001.cloud01.app01.mservice.datamodel.CosmicRecordFilter;
import v620.cc001.cloud01.app01.mservice.datamodel.DatamodelFieldMapper;

import java.time.LocalDateTime;

public class CosmicResumeDiagnosisStorage implements ResumeDiagnosisStorage {

    private final CosmicDatamodelGateway gateway;

    public CosmicResumeDiagnosisStorage(CosmicDatamodelGateway gateway) {
        this.gateway = gateway;
    }

    public ResumeDiagnosisResultDto saveDiagnosis(ResumeDiagnosisResultDto result) {
        CosmicDatamodelRecord record = existing(result.getResumeId());
        gateway.save(record.set("resume_id", result.getResumeId())
                .set("score", result.getOverallScore())
                .set("diagnosis_json", result)
                .set(CareerLoopDatamodelObjects.UPDATED_AT, LocalDateTime.now()));
        return result;
    }

    public ResumeDiagnosisResultDto loadDiagnosis(Long resumeId) {
        CosmicDatamodelRecord record = find(resumeId);
        return record == null ? null : (ResumeDiagnosisResultDto) record.get("diagnosis_json");
    }

    public ResumeKeywordStatusDto saveKeywordStatus(ResumeKeywordStatusDto status) {
        CosmicDatamodelRecord record = existing(status.getResumeId());
        gateway.save(record.set("resume_id", status.getResumeId())
                .set("keyword_status_json", status)
                .set(CareerLoopDatamodelObjects.UPDATED_AT, LocalDateTime.now()));
        return status;
    }

    public ResumeKeywordStatusDto loadKeywordStatus(Long resumeId) {
        CosmicDatamodelRecord record = find(resumeId);
        return record == null ? null : (ResumeKeywordStatusDto) record.get("keyword_status_json");
    }

    private CosmicDatamodelRecord existing(Long resumeId) {
        CosmicDatamodelRecord record = find(resumeId);
        if (record == null) {
            record = new CosmicDatamodelRecord(CareerLoopDatamodelObjects.RESUME_DIAGNOSIS)
                    .set("resume_id", resumeId)
                    .set(CareerLoopDatamodelObjects.CREATED_AT, LocalDateTime.now());
        }
        return record;
    }

    private CosmicDatamodelRecord find(final Long resumeId) {
        return gateway.findOne(CareerLoopDatamodelObjects.RESUME_DIAGNOSIS, new CosmicRecordFilter() {
            public boolean matches(CosmicDatamodelRecord record) {
                return DatamodelFieldMapper.same(resumeId, record.get("resume_id"));
            }
        });
    }
}
