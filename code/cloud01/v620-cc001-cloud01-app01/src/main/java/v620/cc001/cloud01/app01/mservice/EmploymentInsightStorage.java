package v620.cc001.cloud01.app01.mservice;

import v620.cc001.base.common.dto.career.EmploymentInsightRecordDto;

import java.util.List;

/**
 * Storage boundary for traceable employment insight records.
 */
public interface EmploymentInsightStorage {

    List<EmploymentInsightRecordDto> listRecords();
}
