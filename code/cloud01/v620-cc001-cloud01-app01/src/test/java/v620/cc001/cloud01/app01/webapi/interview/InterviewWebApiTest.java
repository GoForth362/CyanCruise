package v620.cc001.cloud01.app01.webapi.interview;

import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerProfileStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryInterviewStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryResumeStorage;
import org.junit.jupiter.api.Test;
import v620.base.helper.career.CareerProfileBuildService;
import v620.base.helper.career.CareerProfileSnapshotMergeService;
import v620.base.helper.career.InterviewCoreService;
import v620.cc001.base.common.dto.career.InterviewMessageRequest;
import v620.cc001.base.common.dto.career.InterviewRadarScoreDto;
import v620.cc001.base.common.dto.career.InterviewReportDto;
import v620.cc001.base.common.dto.career.InterviewSessionDto;
import v620.cc001.base.common.dto.career.InterviewStartRequest;
import v620.cc001.cloud01.app01.mservice.application.CareerProfileApplicationService;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerProfileStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryInterviewStorage;
import v620.cc001.cloud01.app01.mservice.application.InterviewApplicationService;
import v620.cc001.cloud01.app01.mservice.application.ResumeApplicationService;
import v620.cc001.cloud01.app01.mservice.ai.InterviewAiService;
import v620.cc001.cloud01.app01.mservice.ai.InterviewReportAnalyzer;
import v620.cc001.cloud01.app01.mservice.ai.AiGateway;
import v620.cc001.base.common.dto.ai.AiChatRequestDto;
import v620.cc001.base.common.dto.ai.AiChatResponseDto;
import v620.cc001.base.common.dto.ai.AiStreamEventDto;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InterviewWebApiTest {

    @Test
    void webApiCoversInterviewLifecycleAndOwnership() {
        InterviewWebApi webApi = new InterviewWebApi(new InterviewApplicationService(
                new InMemoryInterviewStorage(),
                profileService(),
                new InterviewCoreService()));

        InterviewSessionDto interview = webApi.start("api-interview-user", startRequest());
        webApi.addMessage("api-interview-user", interview.getInterviewId(), message("USER", "我的项目是..."));

        assertEquals("TEXT", interview.getMode());
        assertEquals(1, webApi.messages("api-interview-user", interview.getInterviewId()).size());
        assertEquals(null, webApi.report("api-interview-user", interview.getInterviewId()));
        assertEquals(0, webApi.list("api-interview-user").size());
        assertThrows(IllegalArgumentException.class, new ThrowingRunnableAdapter(new Runnable() {
            public void run() {
                webApi.finish("another-user", interview.getInterviewId());
            }
        }));
        webApi.finish("api-interview-user", interview.getInterviewId());
        assertThrows(IllegalArgumentException.class, new ThrowingRunnableAdapter(new Runnable() {
            public void run() {
                webApi.get("api-interview-user", interview.getInterviewId());
            }
        }));
    }

    @Test
    void webApiExposesGuidedTextInterviewFlow() {
        InterviewWebApi webApi = new InterviewWebApi(serviceWithAnalyzer());
        v620.cc001.base.common.dto.career.InterviewStartResultDto started = webApi.guidedStart("guided-api-user", startRequest());
        assertTrue(started.getOpeningMessage().getContent().length() > 0);
        v620.cc001.base.common.dto.career.InterviewTurnResultDto turn = webApi.guidedAnswer(
                "guided-api-user", started.getSession().getInterviewId(), "我负责过一个课程项目，并按期完成交付。");
        assertEquals("USER", turn.getUserMessage().getRole());
        assertTrue(webApi.guidedFinish("guided-api-user", started.getSession().getInterviewId()).getOverallScore().intValue() > 0);
    }

    @Test
    void webApiDiscardsInterviewWithoutAnyAnswer() {
        InterviewWebApi webApi = new InterviewWebApi(serviceWithAnalyzer());
        v620.cc001.base.common.dto.career.InterviewStartResultDto started =
                webApi.guidedStart("empty-interview-user", startRequest());

        String finished = webApi.finish("empty-interview-user", started.getSession().getInterviewId());

        assertEquals("OK", finished);
        assertTrue(webApi.list("empty-interview-user").isEmpty());
        assertThrows(IllegalArgumentException.class, new ThrowingRunnableAdapter(new Runnable() {
            public void run() {
                webApi.get("empty-interview-user", started.getSession().getInterviewId());
            }
        }));
    }

    private CareerProfileApplicationService profileService() {
        return new CareerProfileApplicationService(new InMemoryCareerProfileStorage(),
                new CareerProfileSnapshotMergeService(), new CareerProfileBuildService());
    }

    private InterviewApplicationService serviceWithAnalyzer() {
        final CareerProfileApplicationService profileService = profileService();
        return new InterviewApplicationService(new InMemoryInterviewStorage(), profileService,
                new InterviewCoreService(), new ResumeApplicationService(new InMemoryResumeStorage(), profileService),
                new InterviewAiService(questionGateway(), new InterviewReportAnalyzer() {
                    public InterviewReportDto analyze(InterviewSessionDto session, String transcript, int answerCount) {
                        InterviewReportDto report = InterviewWebApiTest.this.report();
                        report.setTotalQuestions(Integer.valueOf(answerCount));
                        return report;
                    }
                }));
    }

    private AiGateway questionGateway() {
        return new AiGateway() {
            public AiChatResponseDto chat(AiChatRequestDto request) {
                AiChatResponseDto response = new AiChatResponseDto();
                response.setContent("请结合真实项目说明你的具体行动和结果。");
                return response;
            }

            public List<AiStreamEventDto> stream(AiChatRequestDto request) {
                return Collections.emptyList();
            }
        };
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
