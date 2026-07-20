package v620.cc001.cloud01.app01.mservice.furtherstudy;

import v620.cc001.cloud01.app01.mservice.furtherstudy.impl.InMemoryFurtherStudyCompanionStorage;
import v620.cc001.cloud01.app01.mservice.furtherstudy.impl.PostgresqlFurtherStudyCompanionStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyConstants;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyMaterialDto;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyRecordDetailDto;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyRecordQueryRequest;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyRecordStatusUpdateRequest;
import v620.cc001.base.common.dto.furtherstudy.FurtherStudyRecordSummaryDto;
import v620.cc001.cloud01.app01.mservice.storage.PostgresqlStorageConfig;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FurtherStudyCompanionStorageTest {

    @Test
    void storesListsUpdatesAndIsolatesRecords() {
        InMemoryFurtherStudyCompanionStorage storage = new InMemoryFurtherStudyCompanionStorage();
        FurtherStudyRecordDetailDto first = record("u1", FurtherStudyConstants.TRACK_POSTGRADUATE_EXAM,
                FurtherStudyConstants.RECORD_REVIEW_PLAN, "postgraduate plan");
        FurtherStudyRecordDetailDto second = record("u1", FurtherStudyConstants.TRACK_STUDY_ABROAD,
                FurtherStudyConstants.RECORD_LANGUAGE_PLAN, "language plan");
        FurtherStudyRecordDetailDto other = record("u2", FurtherStudyConstants.TRACK_POSTGRADUATE_EXAM,
                FurtherStudyConstants.RECORD_MISTAKE_ANALYSIS, "mistake analysis");

        first = storage.saveRecord("u1", first);
        second = storage.saveRecord("u1", second);
        other = storage.saveRecord("u2", other);

        FurtherStudyRecordQueryRequest query = new FurtherStudyRecordQueryRequest();
        query.setTrack(FurtherStudyConstants.TRACK_POSTGRADUATE_EXAM);
        List<FurtherStudyRecordSummaryDto> records = storage.listRecords("u1", query);

        assertEquals(1, records.size());
        assertEquals(first.getRecordId(), records.get(0).getRecordId());
        assertNull(storage.loadRecord("u1", other.getRecordId()));
        assertNotNull(storage.loadRecord("u2", other.getRecordId()));

        FurtherStudyRecordStatusUpdateRequest update = new FurtherStudyRecordStatusUpdateRequest();
        update.setRecordId(first.getRecordId());
        update.setStatus(FurtherStudyConstants.STATUS_DONE);
        update.setEventSummary("first review round completed");
        FurtherStudyRecordDetailDto updated = storage.updateRecordStatus("u1", update);

        assertEquals(FurtherStudyConstants.STATUS_DONE, updated.getStatus());
        assertFalse(storage.listEvents("u1", first.getRecordId()).isEmpty());
        assertEquals(2, storage.listRecords("u1", new FurtherStudyRecordQueryRequest()).size());
        assertNotNull(second.getRecordId());
    }

    @Test
    void storesMaterialsAndRejectsIncompletePostgresqlConfiguration(@TempDir Path tempDir) throws Exception {
        InMemoryFurtherStudyCompanionStorage storage = new InMemoryFurtherStudyCompanionStorage();
        FurtherStudyMaterialDto material = new FurtherStudyMaterialDto();
        material.setTrack(FurtherStudyConstants.TRACK_STUDY_ABROAD);
        material.setMaterialType(FurtherStudyConstants.MATERIAL_VISA);
        material.setTitle("visa checklist");
        material.setFileKey("bos-key");

        FurtherStudyMaterialDto saved = storage.saveMaterial("u1", material);

        assertNotNull(saved.getMaterialId());
        assertEquals(1, storage.listMaterials("u1", FurtherStudyConstants.TRACK_STUDY_ABROAD, null).size());
        assertThrows(IllegalStateException.class,
                () -> new PostgresqlFurtherStudyCompanionStorage(new PostgresqlStorageConfig()));
        assertFalse(Files.exists(tempDir.resolve("filestorage").resolve("further-study")));
    }

    private FurtherStudyRecordDetailDto record(String userId, String track, String type, String title) {
        FurtherStudyRecordDetailDto record = new FurtherStudyRecordDetailDto();
        record.setUserId(userId);
        record.setTrack(track);
        record.setRecordType(type);
        record.setTitle(title);
        record.setRequestJson("{}");
        record.setResultJson("{}");
        return record;
    }
}
