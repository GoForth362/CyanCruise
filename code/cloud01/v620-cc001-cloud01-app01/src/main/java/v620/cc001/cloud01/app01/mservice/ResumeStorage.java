package v620.cc001.cloud01.app01.mservice;

import v620.cc001.base.common.dto.career.ResumeRecordDto;

import java.util.List;

/**
 * Storage boundary for resume records before the Cosmic datamodel adapter exists.
 */
public interface ResumeStorage {

    ResumeRecordDto save(ResumeRecordDto record);

    ResumeRecordDto load(Long resumeId);

    List<ResumeRecordDto> listByUser(String userId);

    void delete(Long resumeId);
}
