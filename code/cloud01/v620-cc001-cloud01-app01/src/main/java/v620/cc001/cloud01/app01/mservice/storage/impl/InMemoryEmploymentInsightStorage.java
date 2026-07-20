package v620.cc001.cloud01.app01.mservice.storage.impl;

import v620.cc001.base.common.dto.career.EmploymentInsightRecordDto;
import v620.cc001.cloud01.app01.mservice.storage.EmploymentInsightStorage;

import java.util.ArrayList;
import java.util.List;

/** Empty storage used where no real employment data source is configured. */
public class InMemoryEmploymentInsightStorage implements EmploymentInsightStorage {

    private final List<EmploymentInsightRecordDto> records = new ArrayList<EmploymentInsightRecordDto>();

    public InMemoryEmploymentInsightStorage() {
    }

    public InMemoryEmploymentInsightStorage(List<EmploymentInsightRecordDto> records) {
        if (records != null) this.records.addAll(records);
    }

    public List<EmploymentInsightRecordDto> listRecords() {
        return new ArrayList<EmploymentInsightRecordDto>(records);
    }
}
