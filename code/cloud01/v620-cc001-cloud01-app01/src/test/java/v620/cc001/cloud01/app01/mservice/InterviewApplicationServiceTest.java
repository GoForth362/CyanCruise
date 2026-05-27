package v620.cc001.cloud01.app01.mservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import v620.base.helper.career.CareerProfileBuildService;
import v620.base.helper.career.CareerProfileSnapshotMergeService;
import v620.base.helper.career.InterviewCoreService;
import v620.cc001.base.common.dto.career.InterviewConstants;
import v620.cc001.base.common.dto.career.InterviewMessageRequest;
import v620.cc001.base.common.dto.career.InterviewRadarScoreDto;
import v620.cc001.base.common.dto.career.InterviewReportDto;
import v620.cc001.base.common.dto.career.InterviewSessionDto;
import v620.cc001.base.common.dto.career.InterviewStartRequest;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InterviewApplicationServiceTest {

    @TempDir
    File tempDir;

    @Test
    void fileStorageReloadsInterviewAndMessagesFromFreshInstance() {
        CareerProfileApplicationService profileService = profileService(new InMemoryCareerProfileStorage());
        InterviewApplicationService first = service(new FileInterviewStorage(tempDir), profileService);
        InterviewSessionDto interview = first.start("interview-user-1", startRequest("Java Engineer"));
        first.appendMessage("interview-user-1", interview.getInterviewId(), message("USER", "我准备好了"));

        InterviewApplicationService second = service(new FileInterviewStorage(tempDir), profileService);
        InterviewSessionDto reloaded = second.get("interview-user-1", interview.getInterviewId());

        assertEquals("Java Engineer", reloaded.getPositionName());
        assertEquals(1, second.getMessages("interview-user-1", interview.getInterviewId()).size());
    }

    @Test
    void endInterviewSyncsBasicProfileBlock() {
        CareerProfileApplicationService profileService = profileService(new InMemoryCareerProfileStorage());
        InterviewApplicationService service = service(new InMemoryInterviewStorage(), profileService);
        InterviewSessionDto interview = service.start("interview-user-2", startRequest("Product Manager"));

        service.end("interview-user-2", interview.getInterviewId(), Integer.valueOf(72));
        UserProfileSnapshot snapshot = profileService.getSnapshot("interview-user-2");

        assertEquals(interview.getInterviewId(), snapshot.getInterview().getLastInterviewId());
        assertEquals("Product Manager", snapshot.getInterview().getPositionName());
        assertEquals(Integer.valueOf(72), snapshot.getInterview().getLastScore());
    }

    @Test
    void saveReportSyncsStrongAndWeakProfileDimensions() {
        CareerProfileApplicationService profileService = profileService(new InMemoryCareerProfileStorage());
        InterviewApplicationService service = service(new InMemoryInterviewStorage(), profileService);
        InterviewSessionDto interview = service.start("interview-user-3", startRequest("Data Analyst"));
        service.end("interview-user-3", interview.getInterviewId(), Integer.valueOf(70));

        service.saveReport("interview-user-3", interview.getInterviewId(), report());
        UserProfileSnapshot.InterviewBlock block = profileService.getSnapshot("interview-user-3").getInterview();

        assertEquals(Integer.valueOf(86), block.getLastScore());
        assertTrue(block.getStrongDimensions().contains("logic"));
        assertTrue(block.getWeakDimensions().contains("communication"));
    }

    @Test
    void rejectsCrossUserOperations() {
        InterviewApplicationService service = service(new InMemoryInterviewStorage(),
                profileService(new InMemoryCareerProfileStorage()));
        InterviewSessionDto interview = service.start("owner-user", startRequest("Java Engineer"));

        assertThrows(IllegalArgumentException.class, new ThrowingRunnableAdapter(new Runnable() {
            public void run() {
                service.get("other-user", interview.getInterviewId());
            }
        }));
        assertThrows(IllegalArgumentException.class, new ThrowingRunnableAdapter(new Runnable() {
            public void run() {
                service.appendMessage("other-user", interview.getInterviewId(), message("USER", "hello"));
            }
        }));
    }

    @Test
    void listHistorySortsRecentFirstAndDeleteRemovesMessages() {
        InterviewApplicationService service = service(new InMemoryInterviewStorage(),
                profileService(new InMemoryCareerProfileStorage()));
        InterviewSessionDto first = service.start("interview-user-4", startRequest("Java Engineer"));
        InterviewSessionDto second = service.start("interview-user-4", startRequest("Data Analyst"));
        service.appendMessage("interview-user-4", first.getInterviewId(), message("USER", "old"));

        List<InterviewSessionDto> history = service.listByUser("interview-user-4");
        service.delete("interview-user-4", first.getInterviewId());

        assertEquals(second.getInterviewId(), history.get(0).getInterviewId());
        assertEquals(0, service.getMessages("interview-user-4", second.getInterviewId()).size());
        assertThrows(IllegalArgumentException.class, new ThrowingRunnableAdapter(new Runnable() {
            public void run() {
                service.get("interview-user-4", first.getInterviewId());
            }
        }));
    }

    private InterviewApplicationService service(InterviewStorage storage,
                                                CareerProfileApplicationService profileService) {
        return new InterviewApplicationService(storage, profileService, new InterviewCoreService());
    }

    private CareerProfileApplicationService profileService(CareerProfileStorage storage) {
        return new CareerProfileApplicationService(storage,
                new CareerProfileSnapshotMergeService(), new CareerProfileBuildService());
    }

    private InterviewStartRequest startRequest(String position) {
        InterviewStartRequest request = new InterviewStartRequest();
        request.setPositionName(position);
        request.setDifficulty("Hard");
        request.setMode("bad");
        return request;
    }

    private InterviewMessageRequest message(String role, String content) {
        InterviewMessageRequest request = new InterviewMessageRequest();
        request.setRole(role);
        request.setContent(content);
        return request;
    }

    private InterviewReportDto report() {
        InterviewReportDto report = new InterviewReportDto();
        report.setOverallScore(Integer.valueOf(86));
        report.setTotalQuestions(Integer.valueOf(2));
        InterviewRadarScoreDto radar = new InterviewRadarScoreDto();
        radar.setLogic(Integer.valueOf(90));
        radar.setCommunication(Integer.valueOf(55));
        report.setRadarScore(radar);
        return report;
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
