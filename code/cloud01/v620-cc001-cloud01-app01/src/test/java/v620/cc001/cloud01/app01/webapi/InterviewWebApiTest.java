package v620.cc001.cloud01.app01.webapi;

import org.junit.jupiter.api.Test;
import v620.base.helper.career.CareerProfileBuildService;
import v620.base.helper.career.CareerProfileSnapshotMergeService;
import v620.base.helper.career.InterviewCoreService;
import v620.cc001.base.common.dto.career.InterviewMessageRequest;
import v620.cc001.base.common.dto.career.InterviewRadarScoreDto;
import v620.cc001.base.common.dto.career.InterviewReportDto;
import v620.cc001.base.common.dto.career.InterviewSessionDto;
import v620.cc001.base.common.dto.career.InterviewStartRequest;
import v620.cc001.cloud01.app01.mservice.CareerProfileApplicationService;
import v620.cc001.cloud01.app01.mservice.InMemoryCareerProfileStorage;
import v620.cc001.cloud01.app01.mservice.InMemoryInterviewStorage;
import v620.cc001.cloud01.app01.mservice.InterviewApplicationService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InterviewWebApiTest {

    @Test
    void webApiCoversInterviewLifecycleAndOwnership() {
        InterviewWebApi webApi = new InterviewWebApi(new InterviewApplicationService(
                new InMemoryInterviewStorage(),
                profileService(),
                new InterviewCoreService()));

        InterviewSessionDto interview = webApi.start("api-interview-user", startRequest());
        webApi.addMessage("api-interview-user", interview.getInterviewId(), message("USER", "我的项目是..."));
        webApi.end("api-interview-user", interview.getInterviewId(), Integer.valueOf(75));
        InterviewReportDto savedReport = webApi.saveReport("api-interview-user", interview.getInterviewId(), report());

        assertEquals("TEXT", interview.getMode());
        assertEquals(1, webApi.messages("api-interview-user", interview.getInterviewId()).size());
        assertEquals(Integer.valueOf(88), savedReport.getOverallScore());
        assertEquals(1, webApi.list("api-interview-user").size());
        assertEquals(interview.getInterviewId(), webApi.get("api-interview-user", interview.getInterviewId()).getInterviewId());
        assertThrows(IllegalArgumentException.class, new ThrowingRunnableAdapter(new Runnable() {
            public void run() {
                webApi.get("intruder", interview.getInterviewId());
            }
        }));
        assertEquals("OK", webApi.delete("api-interview-user", interview.getInterviewId()));
    }

    private CareerProfileApplicationService profileService() {
        return new CareerProfileApplicationService(new InMemoryCareerProfileStorage(),
                new CareerProfileSnapshotMergeService(), new CareerProfileBuildService());
    }

    private InterviewStartRequest startRequest() {
        InterviewStartRequest request = new InterviewStartRequest();
        request.setPositionName("Java Engineer");
        request.setDifficulty("Normal");
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
        report.setOverallScore(Integer.valueOf(88));
        InterviewRadarScoreDto radar = new InterviewRadarScoreDto();
        radar.setTechnical(Integer.valueOf(90));
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
