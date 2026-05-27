package v620.cc001.cloud01.app01.mservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import v620.base.helper.career.CareerProfileBuildService;
import v620.base.helper.career.CareerProfileSnapshotMergeService;
import v620.base.helper.career.ResumeDiagnosisService;
import v620.cc001.base.common.dto.career.ResumeCreateRequest;
import v620.cc001.base.common.dto.career.ResumeDiagnosisConstants;
import v620.cc001.base.common.dto.career.ResumeDiagnosisRequest;
import v620.cc001.base.common.dto.career.ResumeDiagnosisResultDto;
import v620.cc001.base.common.dto.career.ResumeKeywordStatusDto;
import v620.cc001.base.common.dto.career.ResumeRecordDto;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResumeDiagnosisApplicationServiceTest {

    @TempDir
    File tempDir;

    @Test
    void diagnosesResumeAndWritesScoreToProfile() {
        CareerProfileStorage profileStorage = new InMemoryCareerProfileStorage();
        ResumeApplicationService resumeService = resumeService(new InMemoryResumeStorage(), profileStorage);
        ResumeRecordDto resume = resumeService.create("diagnosis-user-1", request());
        ResumeDiagnosisApplicationService service = diagnosisService(resumeService, new InMemoryResumeDiagnosisStorage(),
                "{\"overallScore\":84,\"strengths\":[\"Java 项目\"],\"weaknesses\":[\"量化不足\"],\"suggestions\":[\"补充指标\"]}");

        ResumeDiagnosisRequest diagnosisRequest = new ResumeDiagnosisRequest();
        diagnosisRequest.setResumeId(resume.getResumeId());
        ResumeDiagnosisResultDto result = service.diagnose("diagnosis-user-1", diagnosisRequest);

        assertEquals(Integer.valueOf(84), result.getOverallScore());
        assertEquals(Integer.valueOf(84), resumeService.get("diagnosis-user-1", resume.getResumeId()).getDiagnosisScore());
        assertEquals(Integer.valueOf(84), profileService(profileStorage).getSnapshot("diagnosis-user-1").getResume().getDiagnosisScore());
    }

    @Test
    void directTextDiagnosisDoesNotUpdateSavedResume() {
        ResumeApplicationService resumeService = resumeService(new InMemoryResumeStorage(), new InMemoryCareerProfileStorage());
        ResumeRecordDto resume = resumeService.create("diagnosis-user-2", request());
        ResumeDiagnosisApplicationService service = diagnosisService(resumeService, new InMemoryResumeDiagnosisStorage(),
                "匹配分 91，建议补充项目。");
        ResumeDiagnosisRequest diagnosisRequest = new ResumeDiagnosisRequest();
        diagnosisRequest.setResumeText("Java Spring Redis");

        ResumeDiagnosisResultDto result = service.diagnose("diagnosis-user-2", diagnosisRequest);

        assertEquals(Integer.valueOf(91), result.getOverallScore());
        assertEquals(Integer.valueOf(0), resumeService.get("diagnosis-user-2", resume.getResumeId()).getDiagnosisScore());
    }

    @Test
    void rejectsCrossUserDiagnosisAndKeywordAccess() {
        ResumeApplicationService resumeService = resumeService(new InMemoryResumeStorage(), new InMemoryCareerProfileStorage());
        ResumeRecordDto resume = resumeService.create("owner-diagnosis", request());
        ResumeDiagnosisApplicationService service = diagnosisService(resumeService, new InMemoryResumeDiagnosisStorage(),
                "{\"overallScore\":80}");
        ResumeDiagnosisRequest request = new ResumeDiagnosisRequest();
        request.setResumeId(resume.getResumeId());

        assertThrows(IllegalArgumentException.class, new ThrowingRunnableAdapter(new Runnable() {
            public void run() {
                service.diagnose("intruder-diagnosis", request);
            }
        }));
        assertThrows(IllegalArgumentException.class, new ThrowingRunnableAdapter(new Runnable() {
            public void run() {
                service.getKeywordStatus("intruder-diagnosis", resume.getResumeId());
            }
        }));
    }

    @Test
    void extractsKeywordsAndReusesReadyStatusUntilForced() {
        ResumeApplicationService resumeService = resumeService(new InMemoryResumeStorage(), new InMemoryCareerProfileStorage());
        ResumeRecordDto resume = resumeService.create("diagnosis-user-3", request());
        ResumeDiagnosisApplicationService service = diagnosisService(resumeService, new InMemoryResumeDiagnosisStorage(),
                "{\"overallScore\":80}");

        ResumeKeywordStatusDto first = service.triggerKeywordExtraction("diagnosis-user-3", resume.getResumeId(), false);
        ResumeKeywordStatusDto second = service.triggerKeywordExtraction("diagnosis-user-3", resume.getResumeId(), false);
        ResumeKeywordStatusDto forced = service.triggerKeywordExtraction("diagnosis-user-3", resume.getResumeId(), true);

        assertEquals(ResumeDiagnosisConstants.STATUS_READY, first.getStatus());
        assertEquals(first.getKeywords().size(), second.getKeywords().size());
        assertTrue(forced.getKeywords().size() > 0);
    }

    @Test
    void fileStorageReloadsKeywordStatus() {
        FileResumeDiagnosisStorage firstStorage = new FileResumeDiagnosisStorage(tempDir);
        ResumeKeywordStatusDto status = new ResumeKeywordStatusDto();
        status.setResumeId(Long.valueOf(77L));
        status.setStatus(ResumeDiagnosisConstants.STATUS_READY);
        firstStorage.saveKeywordStatus(status);

        FileResumeDiagnosisStorage secondStorage = new FileResumeDiagnosisStorage(tempDir);
        assertEquals(ResumeDiagnosisConstants.STATUS_READY, secondStorage.loadKeywordStatus(Long.valueOf(77L)).getStatus());
    }

    @Test
    void rejectsEmptyResumeContent() {
        ResumeApplicationService resumeService = resumeService(new InMemoryResumeStorage(), new InMemoryCareerProfileStorage());
        ResumeCreateRequest create = new ResumeCreateRequest();
        create.setTitle("Empty");
        ResumeRecordDto resume = resumeService.create("diagnosis-user-4", create);
        ResumeDiagnosisApplicationService service = diagnosisService(resumeService, new InMemoryResumeDiagnosisStorage(),
                "{\"overallScore\":80}");
        ResumeDiagnosisRequest request = new ResumeDiagnosisRequest();
        request.setResumeId(resume.getResumeId());

        assertThrows(IllegalArgumentException.class, new ThrowingRunnableAdapter(new Runnable() {
            public void run() {
                service.diagnose("diagnosis-user-4", request);
            }
        }));
    }

    private ResumeDiagnosisApplicationService diagnosisService(ResumeApplicationService resumeService,
                                                              ResumeDiagnosisStorage storage,
                                                              final String analysis) {
        return new ResumeDiagnosisApplicationService(resumeService, storage, new ResumeDiagnosisAnalyzer() {
            public String analyze(ResumeDiagnosisRequest request, String resumeText) {
                assertNotNull(resumeText);
                return analysis;
            }
        }, new ResumeDiagnosisService());
    }

    private ResumeApplicationService resumeService(ResumeStorage resumeStorage, CareerProfileStorage profileStorage) {
        return new ResumeApplicationService(resumeStorage, profileService(profileStorage));
    }

    private CareerProfileApplicationService profileService(CareerProfileStorage storage) {
        return new CareerProfileApplicationService(
                storage,
                new CareerProfileSnapshotMergeService(),
                new CareerProfileBuildService());
    }

    private ResumeCreateRequest request() {
        ResumeCreateRequest request = new ResumeCreateRequest();
        request.setTitle("Java Resume");
        request.setTargetJob("Java Backend Engineer");
        request.setFileKey("resumes/java.pdf");
        request.setParsedContent("{\"skills\":[\"Java\",\"SpringBoot\"],\"projects\":\"Redis 项目\",\"rawContent\":\"Java Spring Redis\"}");
        return request;
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
