package v620.cc001.cloud01.app01.webapi;

import org.junit.jupiter.api.Test;
import v620.base.helper.career.CareerProfileBuildService;
import v620.base.helper.career.CareerProfileSnapshotMergeService;
import v620.base.helper.career.ResumeDiagnosisService;
import v620.cc001.base.common.dto.career.ResumeCreateRequest;
import v620.cc001.base.common.dto.career.ResumeDiagnosisConstants;
import v620.cc001.base.common.dto.career.ResumeDiagnosisRequest;
import v620.cc001.base.common.dto.career.ResumeDiagnosisResultDto;
import v620.cc001.base.common.dto.career.ResumeKeywordStatusDto;
import v620.cc001.base.common.dto.career.ResumeRecordDto;
import v620.cc001.cloud01.app01.mservice.CareerProfileApplicationService;
import v620.cc001.cloud01.app01.mservice.InMemoryCareerProfileStorage;
import v620.cc001.cloud01.app01.mservice.InMemoryResumeDiagnosisStorage;
import v620.cc001.cloud01.app01.mservice.InMemoryResumeStorage;
import v620.cc001.cloud01.app01.mservice.ResumeApplicationService;
import v620.cc001.cloud01.app01.mservice.ResumeDiagnosisAnalyzer;
import v620.cc001.cloud01.app01.mservice.ResumeDiagnosisApplicationService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResumeDiagnosisWebApiTest {

    @Test
    void exposesDiagnosisAndKeywordEndpoints() {
        ResumeApplicationService resumeService = new ResumeApplicationService(
                new InMemoryResumeStorage(),
                new CareerProfileApplicationService(
                        new InMemoryCareerProfileStorage(),
                        new CareerProfileSnapshotMergeService(),
                        new CareerProfileBuildService()));
        ResumeRecordDto resume = resumeService.create("api-diagnosis-user", request());
        ResumeDiagnosisWebApi webApi = new ResumeDiagnosisWebApi(new ResumeDiagnosisApplicationService(
                resumeService,
                new InMemoryResumeDiagnosisStorage(),
                new ResumeDiagnosisAnalyzer() {
                    public String analyze(ResumeDiagnosisRequest request, String resumeText) {
                        return "{\"overallScore\":87,\"suggestions\":[\"补充指标\"]}";
                    }
                },
                new ResumeDiagnosisService()));
        ResumeDiagnosisRequest diagnosisRequest = new ResumeDiagnosisRequest();
        diagnosisRequest.setResumeId(resume.getResumeId());

        ResumeDiagnosisResultDto diagnosis = webApi.analyze("api-diagnosis-user", diagnosisRequest);
        ResumeKeywordStatusDto pending = webApi.keywordStatus("api-diagnosis-user", resume.getResumeId());
        ResumeKeywordStatusDto ready = webApi.extractKeywords("api-diagnosis-user", resume.getResumeId(), Boolean.TRUE);

        assertEquals(Integer.valueOf(87), diagnosis.getOverallScore());
        assertEquals(ResumeDiagnosisConstants.STATUS_PENDING, pending.getStatus());
        assertEquals(ResumeDiagnosisConstants.STATUS_READY, ready.getStatus());
        assertTrue(ready.getKeywords().size() > 0);
    }

    @Test
    void rejectsCrossUserWebApiAccess() {
        ResumeApplicationService resumeService = new ResumeApplicationService(
                new InMemoryResumeStorage(),
                new CareerProfileApplicationService(
                        new InMemoryCareerProfileStorage(),
                        new CareerProfileSnapshotMergeService(),
                        new CareerProfileBuildService()));
        ResumeRecordDto resume = resumeService.create("api-owner", request());
        ResumeDiagnosisWebApi webApi = new ResumeDiagnosisWebApi(new ResumeDiagnosisApplicationService(
                resumeService,
                new InMemoryResumeDiagnosisStorage(),
                new ResumeDiagnosisAnalyzer() {
                    public String analyze(ResumeDiagnosisRequest request, String resumeText) {
                        return "{\"overallScore\":87}";
                    }
                },
                new ResumeDiagnosisService()));
        ResumeDiagnosisRequest diagnosisRequest = new ResumeDiagnosisRequest();
        diagnosisRequest.setResumeId(resume.getResumeId());

        assertThrows(IllegalArgumentException.class, new ThrowingRunnableAdapter(new Runnable() {
            public void run() {
                webApi.analyze("api-intruder", diagnosisRequest);
            }
        }));
    }

    private ResumeCreateRequest request() {
        ResumeCreateRequest request = new ResumeCreateRequest();
        request.setTitle("API Resume");
        request.setTargetJob("Java Engineer");
        request.setFileKey("resumes/api.pdf");
        request.setParsedContent("{\"skills\":[\"Java\",\"SpringBoot\"],\"rawContent\":\"Java Redis 项目\"}");
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
