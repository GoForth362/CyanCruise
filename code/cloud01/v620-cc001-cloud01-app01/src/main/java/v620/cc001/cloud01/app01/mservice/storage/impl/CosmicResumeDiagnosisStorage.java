package v620.cc001.cloud01.app01.mservice.storage.impl;

import v620.cc001.cloud01.app01.mservice.storage.*;
import v620.cc001.base.common.dto.career.ResumeDiagnosisResultDto;
import v620.cc001.base.common.dto.career.ResumeKeywordStatusDto;
import v620.cc001.cloud01.app01.mservice.datamodel.CyanCruiseDatamodelObjects;
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
                .set(CyanCruiseDatamodelObjects.UPDATED_AT, LocalDateTime.now()));
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
                .set(CyanCruiseDatamodelObjects.UPDATED_AT, LocalDateTime.now()));
        return status;
    }

    public ResumeKeywordStatusDto loadKeywordStatus(Long resumeId) {
        CosmicDatamodelRecord record = find(resumeId);
        return record == null ? null : (ResumeKeywordStatusDto) record.get("keyword_status_json");
    }

    private CosmicDatamodelRecord existing(Long resumeId) {
        CosmicDatamodelRecord record = find(resumeId);
        if (record == null) {
            record = new CosmicDatamodelRecord(CyanCruiseDatamodelObjects.RESUME_DIAGNOSIS)
                    .set("resume_id", resumeId)
                    .set(CyanCruiseDatamodelObjects.CREATED_AT, LocalDateTime.now());
        }
        return record;
    }

    private CosmicDatamodelRecord find(final Long resumeId) {
        return gateway.findOne(CyanCruiseDatamodelObjects.RESUME_DIAGNOSIS, new CosmicRecordFilter() {
            public boolean matches(CosmicDatamodelRecord record) {
                return DatamodelFieldMapper.same(resumeId, record.get("resume_id"));
            }
        });
    }
}
