package v620.cc001.cloud01.app01.mservice.datamodel;

import java.util.Comparator;
import java.util.List;

public interface CosmicDatamodelGateway {

    CosmicDatamodelRecord save(CosmicDatamodelRecord record);

    CosmicDatamodelRecord load(String objectName, Long id);

    CosmicDatamodelRecord findOne(String objectName, CosmicRecordFilter filter);

    List<CosmicDatamodelRecord> list(String objectName, CosmicRecordFilter filter, Comparator<CosmicDatamodelRecord> comparator);

    void delete(String objectName, Long id);

    void deleteWhere(String objectName, CosmicRecordFilter filter);
}
