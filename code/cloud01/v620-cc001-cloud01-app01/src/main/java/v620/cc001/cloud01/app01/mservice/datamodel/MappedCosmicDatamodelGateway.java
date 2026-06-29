package v620.cc001.cloud01.app01.mservice.datamodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Applies CyanCruise business object mapping while preserving logical storage APIs.
 */
public class MappedCosmicDatamodelGateway implements CosmicDatamodelGateway {

    private final CosmicDatamodelGateway delegate;

    public MappedCosmicDatamodelGateway(CosmicDatamodelGateway delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("delegate gateway must not be null");
        }
        this.delegate = delegate;
    }

    public CosmicDatamodelRecord save(CosmicDatamodelRecord record) {
        CosmicDatamodelRecord platformRecord = CyanCruiseBusinessModelMapping.toPlatformRecord(record);
        CosmicDatamodelRecord saved = delegate.save(platformRecord);
        return CyanCruiseBusinessModelMapping.toLogicalRecord(saved);
    }

    public CosmicDatamodelRecord load(String objectName, Long id) {
        CosmicDatamodelRecord loaded = delegate.load(CyanCruiseBusinessModelMapping.toPlatformObject(objectName), id);
        return CyanCruiseBusinessModelMapping.toLogicalRecord(loaded);
    }

    public CosmicDatamodelRecord findOne(String objectName, CosmicRecordFilter filter) {
        List<CosmicDatamodelRecord> rows = list(objectName, filter, null);
        return rows.isEmpty() ? null : rows.get(0);
    }

    public List<CosmicDatamodelRecord> list(String objectName,
                                           CosmicRecordFilter filter,
                                           Comparator<CosmicDatamodelRecord> comparator) {
        List<CosmicDatamodelRecord> platformRows = delegate.list(
                CyanCruiseBusinessModelMapping.toPlatformObject(objectName),
                null,
                null);
        List<CosmicDatamodelRecord> logicalRows = new ArrayList<CosmicDatamodelRecord>();
        for (CosmicDatamodelRecord platformRow : platformRows) {
            CosmicDatamodelRecord logicalRow = CyanCruiseBusinessModelMapping.toLogicalRecord(platformRow);
            if (filter == null || filter.matches(logicalRow)) {
                logicalRows.add(logicalRow);
            }
        }
        if (comparator != null) {
            Collections.sort(logicalRows, comparator);
        }
        return logicalRows;
    }

    public void delete(String objectName, Long id) {
        delegate.delete(CyanCruiseBusinessModelMapping.toPlatformObject(objectName), id);
    }

    public void deleteWhere(String objectName, CosmicRecordFilter filter) {
        List<CosmicDatamodelRecord> rows = list(objectName, filter, null);
        String platformObject = CyanCruiseBusinessModelMapping.toPlatformObject(objectName);
        for (CosmicDatamodelRecord row : rows) {
            Long id = DatamodelFieldMapper.asLong(row.get(CyanCruiseDatamodelObjects.ID));
            if (id != null) {
                delegate.delete(platformObject, id);
            }
        }
    }
}
