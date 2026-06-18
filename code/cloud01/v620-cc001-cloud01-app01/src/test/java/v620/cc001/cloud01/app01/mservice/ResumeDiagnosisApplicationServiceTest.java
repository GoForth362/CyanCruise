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
import v620.cc001.base.common.dto.career.FileUploadRequest;
import v620.cc001.base.common.dto.career.FileUploadResult;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResumeDiagnosisApplicationServiceTest {

    @TempDir
    File tempDir;

    @Test
    void diagnosisResultCarriesRevisionSuggestionsAndContextSources() {
        CareerProfileStorage profileStorage = new InMemoryCareerProfileStorage();
        ResumeApplicationService resumeService = resumeService(new InMemoryResumeStorage(), profileStorage);
        ResumeRecordDto resume = resumeService.create("diagnosis-user-context", request());
        ResumeDiagnosisApplicationService service = diagnosisService(resumeService, new InMemoryResumeDiagnosisStorage(),
                "{\"overallScore\":86,\"suggestions\":[\"补充指标\"],\"revisionSuggestions\":[{\"suggestionId\":\"rev-context\",\"priority\":\"HIGH\",\"resumeSection\":\"projects\",\"action\":\"补充项目结果\",\"targetKeywords\":[\"Java\"]}]}");
        ResumeDiagnosisRequest diagnosisRequest = new ResumeDiagnosisRequest();
        diagnosisRequest.setResumeId(resume.getResumeId());
        diagnosisRequest.setJobDescription("Java Redis 项目经验");

        ResumeDiagnosisResultDto result = service.diagnose("diagnosis-user-context", diagnosisRequest);

        assertEquals("rev-context", result.getRevisionSuggestions().get(0).getSuggestionId());
        assertEquals(Integer.valueOf(1), result.getRevisionPlan().getHighPrioritySuggestions());
        assertTrue(result.getContextSources().contains("resume:" + resume.getResumeId()));
        assertTrue(result.getContextSources().contains("request.jobDescription"));
    }

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

    @Test
    void savedResumeTargetJobOverridesDiagnosisPageInput() {
        ResumeApplicationService resumeService = resumeService(
                new InMemoryResumeStorage(), new InMemoryCareerProfileStorage());
        ResumeRecordDto resume = resumeService.create("saved-role-user", request());
        final String[] analyzedRole = new String[1];
        ResumeDiagnosisApplicationService service = new ResumeDiagnosisApplicationService(
                resumeService,
                new InMemoryResumeDiagnosisStorage(),
                new ResumeDiagnosisAnalyzer() {
                    public String analyze(ResumeDiagnosisRequest request, String resumeText) {
                        analyzedRole[0] = request.getTargetJob();
                        return "{\"overallScore\":80}";
                    }
                },
                new ResumeDiagnosisService());
        ResumeDiagnosisRequest diagnosisRequest = new ResumeDiagnosisRequest();
        diagnosisRequest.setResumeId(resume.getResumeId());
        diagnosisRequest.setTargetJob("Frontend Engineer");

        service.diagnose("saved-role-user", diagnosisRequest);

        assertEquals("Java Backend Engineer", analyzedRole[0]);
    }

    @Test
    void extractsMissingPdfTextAndWritesItBackBeforeDiagnosis() {
        CareerProfileStorage profileStorage = new InMemoryCareerProfileStorage();
        ResumeApplicationService resumeService = resumeService(new InMemoryResumeStorage(), profileStorage);
        FileUploadPreviewApplicationService files = fileService(new FileTextExtractor() {
            public String extract(byte[] bytes, String objectKey) {
                return "Java PDF resume with project metrics";
            }

            public boolean available() {
                return true;
            }
        });
        String fileKey = uploadPdf(files);
        ResumeCreateRequest create = new ResumeCreateRequest();
        create.setTitle("PDF Resume");
        create.setFileKey(fileKey);
        ResumeRecordDto resume = resumeService.create("pdf-diagnosis-user", create);
        ResumeDiagnosisApplicationService service = diagnosisService(
                resumeService, profileStorage, files, "{\"overallScore\":82}");
        ResumeDiagnosisRequest request = new ResumeDiagnosisRequest();
        request.setResumeId(resume.getResumeId());

        ResumeDiagnosisResultDto result = service.diagnose("pdf-diagnosis-user", request);

        assertEquals(Integer.valueOf(82), result.getOverallScore());
        assertEquals("Java PDF resume with project metrics",
                resumeService.get("pdf-diagnosis-user", resume.getResumeId()).getParsedContent());
        assertTrue(result.getContextSources().contains("resume.fileText"));
    }

    @Test
    void existingResumeTextDoesNotReadPdfAgain() {
        final int[] extractionCount = new int[]{0};
        CareerProfileStorage profileStorage = new InMemoryCareerProfileStorage();
        ResumeApplicationService resumeService = resumeService(new InMemoryResumeStorage(), profileStorage);
        ResumeRecordDto resume = resumeService.create("existing-text-user", request());
        FileUploadPreviewApplicationService files = fileService(new FileTextExtractor() {
            public String extract(byte[] bytes, String objectKey) {
                extractionCount[0] += 1;
                return "unexpected";
            }

            public boolean available() {
                return true;
            }
        });
        ResumeDiagnosisApplicationService service = diagnosisService(
                resumeService, profileStorage, files, "{\"overallScore\":80}");
        ResumeDiagnosisRequest diagnosisRequest = new ResumeDiagnosisRequest();
        diagnosisRequest.setResumeId(resume.getResumeId());

        service.diagnose("existing-text-user", diagnosisRequest);

        assertEquals(0, extractionCount[0]);
    }

    @Test
    void blankPdfTextStopsDiagnosisWithoutWritingEmptyContent() {
        CareerProfileStorage profileStorage = new InMemoryCareerProfileStorage();
        ResumeApplicationService resumeService = resumeService(new InMemoryResumeStorage(), profileStorage);
        FileUploadPreviewApplicationService files = fileService(new FileTextExtractor() {
            public String extract(byte[] bytes, String objectKey) {
                return "";
            }

            public boolean available() {
                return true;
            }
        });
        ResumeCreateRequest create = new ResumeCreateRequest();
        create.setTitle("Scanned Resume");
        create.setFileKey(uploadPdf(files));
        final ResumeRecordDto resume = resumeService.create("scanned-pdf-user", create);
        final ResumeDiagnosisApplicationService service = diagnosisService(
                resumeService, profileStorage, files, "{\"overallScore\":80}");
        final ResumeDiagnosisRequest diagnosisRequest = new ResumeDiagnosisRequest();
        diagnosisRequest.setResumeId(resume.getResumeId());

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                new ThrowingRunnableAdapter(new Runnable() {
                    public void run() {
                        service.diagnose("scanned-pdf-user", diagnosisRequest);
                    }
                }));

        assertTrue(error.getMessage().contains("扫描件"));
        assertEquals(null, resumeService.get("scanned-pdf-user", resume.getResumeId()).getParsedContent());
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

    private ResumeDiagnosisApplicationService diagnosisService(ResumeApplicationService resumeService,
                                                               CareerProfileStorage profileStorage,
                                                               FileUploadPreviewApplicationService files,
                                                               final String analysis) {
        return new ResumeDiagnosisApplicationService(resumeService,
                new InMemoryResumeDiagnosisStorage(),
                new ResumeDiagnosisAnalyzer() {
                    public String analyze(ResumeDiagnosisRequest request, String resumeText) {
                        assertNotNull(resumeText);
                        return analysis;
                    }
                },
                new ResumeDiagnosisService(),
                profileService(profileStorage),
                files);
    }

    private FileUploadPreviewApplicationService fileService(FileTextExtractor extractor) {
        return new FileUploadPreviewApplicationService(
                new InMemoryCareerFileStorage(), extractor, new v620.base.helper.career.FileUploadPreviewService());
    }

    private String uploadPdf(FileUploadPreviewApplicationService files) {
        FileUploadRequest request = new FileUploadRequest();
        request.setFolder("resumes");
        request.setOriginalFilename("resume.pdf");
        request.setBytes(new byte[]{1, 2, 3});
        FileUploadResult result = files.upload(request);
        return result.getFile().getObjectKey();
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
