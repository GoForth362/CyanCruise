package v620.cc001.cloud01.app01.mservice.datamodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryCosmicDatamodelGateway implements CosmicDatamodelGateway {

    private final Map<String, Map<Long, CosmicDatamodelRecord>> records = new LinkedHashMap<String, Map<Long, CosmicDatamodelRecord>>();
    private final Map<String, AtomicLong> sequences = new LinkedHashMap<String, AtomicLong>();

    public synchronized CosmicDatamodelRecord save(CosmicDatamodelRecord record) {
        Long id = DatamodelFieldMapper.asLong(record.get(CareerLoopDatamodelObjects.ID));
        if (id == null) {
            id = nextId(record.getObjectName());
            record.set(CareerLoopDatamodelObjects.ID, id);
        }
        bucket(record.getObjectName()).put(id, record.copy());
        return record.copy();
    }

    public synchronized CosmicDatamodelRecord load(String objectName, Long id) {
        if (id == null) {
            return null;
        }
        CosmicDatamodelRecord record = bucket(objectName).get(id);
        return record == null ? null : record.copy();
    }

    public synchronized CosmicDatamodelRecord findOne(String objectName, CosmicRecordFilter filter) {
        List<CosmicDatamodelRecord> list = list(objectName, filter, null);
        return list.isEmpty() ? null : list.get(0);
    }

    public synchronized List<CosmicDatamodelRecord> list(String objectName,
                                                        CosmicRecordFilter filter,
                                                        Comparator<CosmicDatamodelRecord> comparator) {
        List<CosmicDatamodelRecord> result = new ArrayList<CosmicDatamodelRecord>();
        for (CosmicDatamodelRecord record : bucket(objectName).values()) {
            if (filter == null || filter.matches(record)) {
                result.add(record.copy());
            }
        }
        if (comparator != null) {
            Collections.sort(result, comparator);
        }
        return result;
    }

    public synchronized void delete(String objectName, Long id) {
        if (id != null) {
            bucket(objectName).remove(id);
        }
    }

    public synchronized void deleteWhere(String objectName, CosmicRecordFilter filter) {
        List<Long> ids = new ArrayList<Long>();
        for (Map.Entry<Long, CosmicDatamodelRecord> entry : bucket(objectName).entrySet()) {
            if (filter == null || filter.matches(entry.getValue())) {
                ids.add(entry.getKey());
            }
        }
        for (Long id : ids) {
            bucket(objectName).remove(id);
        }
    }

    private Long nextId(String objectName) {
        AtomicLong sequence = sequences.get(objectName);
        if (sequence == null) {
            sequence = new AtomicLong(1L);
            sequences.put(objectName, sequence);
        }
        return Long.valueOf(sequence.getAndIncrement());
    }

    private Map<Long, CosmicDatamodelRecord> bucket(String objectName) {
        Map<Long, CosmicDatamodelRecord> bucket = records.get(objectName);
        if (bucket == null) {
            bucket = new LinkedHashMap<Long, CosmicDatamodelRecord>();
            records.put(objectName, bucket);
        }
        return bucket;
    }
}
