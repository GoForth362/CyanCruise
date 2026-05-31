package v620.cc001.cloud01.app01.mservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import v620.base.helper.career.CareerProfileBuildService;
import v620.base.helper.career.CareerProfileSnapshotMergeService;
import v620.cc001.base.common.dto.career.CareerUserProfileDto;
import v620.cc001.base.common.dto.career.ResumeCreateRequest;
import v620.cc001.base.common.dto.career.ResumeRecordDto;
import v620.cc001.base.common.dto.career.ResumeUpdateRequest;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResumeApplicationServiceTest {

    @TempDir
    File tempDir;

    @Test
    void createsResumeAndListsByUser() {
        ResumeApplicationService service = service(new InMemoryResumeStorage(), new InMemoryCareerProfileStorage());

        ResumeRecordDto created = service.create("user-1", request("Java Resume", "Java Engineer", "resumes/java.pdf"));
        List<ResumeRecordDto> records = service.listByUser("user-1");

        assertNotNull(created.getResumeId());
        assertEquals("user-1", created.getUserId());
        assertEquals("Java Resume", records.get(0).getTitle());
        assertEquals("resumes/java.pdf", records.get(0).getFileKey());
    }

    @Test
    void rejectsCrossUserAccess() {
        ResumeApplicationService service = service(new InMemoryResumeStorage(), new InMemoryCareerProfileStorage());
        ResumeRecordDto created = service.create("owner", request("Owner Resume", "PM", "resumes/pm.pdf"));

        assertThrows(IllegalArgumentException.class, new ThrowingRunnableAdapter(new Runnable() {
            public void run() {
                service.get("intruder", created.getResumeId());
            }
        }));
        assertThrows(IllegalArgumentException.class, new ThrowingRunnableAdapter(new Runnable() {
            public void run() {
                service.update("intruder", created.getResumeId(), new ResumeUpdateRequest());
            }
        }));
        assertThrows(IllegalArgumentException.class, new ThrowingRunnableAdapter(new Runnable() {
            public void run() {
                service.delete("intruder", created.getResumeId());
            }
        }));
    }

    @Test
    void fileStorageReloadsResumeRecords() {
        FileResumeStorage firstStorage = new FileResumeStorage(tempDir);
        ResumeApplicationService first = service(firstStorage, new FileCareerProfileStorage(new File(tempDir, "profile")));
        ResumeRecordDto created = first.create("user-2", request("Data Resume", "Data Analyst", "resumes/data.pdf"));

        FileResumeStorage secondStorage = new FileResumeStorage(tempDir);
        ResumeRecordDto reloaded = secondStorage.load(created.getResumeId());
        List<ResumeRecordDto> records = secondStorage.listByUser("user-2");

        assertEquals("Data Resume", reloaded.getTitle());
        assertEquals(1, records.size());
        assertEquals("Data Analyst", records.get(0).getTargetJob());
    }

    @Test
    void createResumeRefreshesProfileSignal() {
        CareerProfileStorage profileStorage = new InMemoryCareerProfileStorage();
        ResumeApplicationService service = service(new InMemoryResumeStorage(), profileStorage);
        CareerProfileApplicationService profileService = profileService(profileStorage);

        CareerUserProfileDto before = profileService.getProfile("user-3");
        ResumeRecordDto created = service.create("user-3", request("Backend Resume", "Backend Engineer", "resumes/backend.pdf"));
        UserProfileSnapshot snapshot = profileService.getSnapshot("user-3");
        CareerUserProfileDto after = profileService.getProfile("user-3");

        assertTrue(hasMissingSignal(before, "resume"));
        assertEquals(created.getResumeId(), snapshot.getResume().getLastResumeId());
        assertEquals("resumes/backend.pdf", snapshot.getResume().getLastResumeKey());
        assertTrue(after.getReadiness().getHasResume().booleanValue());
        assertFalse(hasMissingSignal(after, "resume"));
    }

    @Test
    void creatingSameFileKeyUpdatesExistingResumeInsteadOfDuplicating() {
        CareerProfileStorage profileStorage = new InMemoryCareerProfileStorage();
        ResumeApplicationService service = service(new InMemoryResumeStorage(), profileStorage);
        CareerProfileApplicationService profileService = profileService(profileStorage);

        ResumeRecordDto first = service.create("user-dedup", request("Backend Resume", "Backend Engineer", "resumes/same.pdf"));
        ResumeRecordDto second = service.create("user-dedup", request("Backend Resume Updated", "Java Engineer", "resumes/same.pdf"));
        List<ResumeRecordDto> records = service.listByUser("user-dedup");

        assertEquals(first.getResumeId(), second.getResumeId());
        assertEquals(1, records.size());
        assertEquals("Backend Resume Updated", records.get(0).getTitle());
        assertEquals("Java Engineer", records.get(0).getTargetJob());
        assertEquals(second.getResumeId(), profileService.getSnapshot("user-dedup").getResume().getLastResumeId());
    }

    @Test
    void deletingCurrentResumeSwitchesToRemainingLatestThenClearsWhenEmpty() {
        CareerProfileStorage profileStorage = new InMemoryCareerProfileStorage();
        ResumeApplicationService service = service(new InMemoryResumeStorage(), profileStorage);
        CareerProfileApplicationService profileService = profileService(profileStorage);

        ResumeRecordDto first = service.create("user-4", request("First Resume", "Java Engineer", "resumes/first.pdf"));
        ResumeRecordDto second = service.create("user-4", request("Second Resume", "Product Manager", "resumes/second.pdf"));
        assertEquals(second.getResumeId(), profileService.getSnapshot("user-4").getResume().getLastResumeId());

        service.delete("user-4", second.getResumeId());
        assertEquals(first.getResumeId(), profileService.getSnapshot("user-4").getResume().getLastResumeId());

        service.delete("user-4", first.getResumeId());
        assertEquals(null, profileService.getSnapshot("user-4").getResume());
        assertTrue(hasMissingSignal(profileService.getProfile("user-4"), "resume"));
    }

    private ResumeApplicationService service(ResumeStorage resumeStorage, CareerProfileStorage profileStorage) {
        return new ResumeApplicationService(resumeStorage, profileService(profileStorage));
    }

    private CareerProfileApplicationService profileService(CareerProfileStorage storage) {
        return new CareerProfileApplicationService(
                storage,
                new CareerProfileSnapshotMergeService(),
                new CareerProfileBuildService());
    }

    private ResumeCreateRequest request(String title, String targetJob, String fileKey) {
        ResumeCreateRequest request = new ResumeCreateRequest();
        request.setTitle(title);
        request.setTargetJob(targetJob);
        request.setFileKey(fileKey);
        request.setParsedContent("{\"rawContent\":\"resume\"}");
        return request;
    }

    private boolean hasMissingSignal(CareerUserProfileDto profile, String key) {
        if (profile == null || profile.getMissingSignals() == null) {
            return false;
        }
        for (CareerUserProfileDto.MissingSignal signal : profile.getMissingSignals()) {
            if (signal != null && key.equals(signal.getKey())) {
                return true;
            }
        }
        return false;
    }

    private static class ThrowingRunnableAdapter implements org.junit.jupiter.api.function.Executable {
        private final Runnable runnable;

        ThrowingRunnableAdapter(Runnable runnable) {
            this.runnable = runnable;
        }

        public void execute() {
            runnable.run();
        }
    }
}
