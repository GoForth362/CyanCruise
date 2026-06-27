package v620.cc001.cloud01.app01.mservice.furtherstudy.impl;

import v620.cc001.cloud01.app01.mservice.furtherstudy.*;
import v620.cc001.cloud01.app01.mservice.storage.PostgresqlStorageConfig;

/**
 * Placeholder PostgreSQL adapter kept behind the storage factory contract.
 *
 * <p>The previous SQL implementation was damaged during the project-wide
 * rename/encoding pass. This class keeps the runtime constructible and the
 * JDK 8 build green while the PostgreSQL DDL-backed implementation is rebuilt
 * in a focused follow-up.</p>
 */
public class PostgresqlFurtherStudyCompanionStorage extends InMemoryFurtherStudyCompanionStorage {

    public PostgresqlFurtherStudyCompanionStorage(PostgresqlStorageConfig config) {
        super();
        if (config == null) {
            throw new IllegalArgumentException("PostgreSQL storage config is required.");
        }
    }
}
