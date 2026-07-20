package v620.cc001.cloud01.app01.mservice;

import v620.cc001.cloud01.app01.mservice.storage.impl.FileInterviewStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerProfileStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryInterviewStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryResumeStorage;
import v620.cc001.cloud01.app01.mservice.ai.InterviewAiService;
import v620.cc001.cloud01.app01.mservice.ai.InterviewReportAnalyzer;
import v620.cc001.cloud01.app01.mservice.ai.AiGateway;
import v620.cc001.cloud01.app01.mservice.application.CareerProfileApplicationService;
import v620.cc001.cloud01.app01.mservice.application.InterviewApplicationService;
import v620.cc001.cloud01.app01.mservice.application.ResumeApplicationService;
import v620.cc001.cloud01.app01.mservice.storage.CareerProfileStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.FileInterviewStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerProfileStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryInterviewStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryResumeStorage;
import v620.cc001.cloud01.app01.mservice.storage.InterviewStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import v620.base.helper.career.CareerProfileBuildService;
import v620.base.helper.career.CareerProfileSnapshotMergeService;
import v620.base.helper.career.InterviewCoreService;
import v620.cc001.base.common.dto.career.InterviewConstants;
import v620.cc001.base.common.dto.career.InterviewMessageRequest;
import v620.cc001.base.common.dto.career.InterviewPageResultDto;
import v620.cc001.base.common.dto.career.InterviewRadarScoreDto;
import v620.cc001.base.common.dto.career.InterviewReportDto;
import v620.cc001.base.common.dto.career.InterviewSessionDto;
import v620.cc001.base.common.dto.career.InterviewStartRequest;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;
import v620.cc001.base.common.dto.ai.AiChatRequestDto;
import v620.cc001.base.common.dto.ai.AiChatResponseDto;
import v620.cc001.base.common.dto.ai.AiStreamEventDto;

import java.io.File;
import java.util.List;
import java.util.Collections;

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
        service.appendMessage("interview-user-4", first.getInterviewId(), message("USER", "old"));
        service.end("interview-user-4", first.getInterviewId(), Integer.valueOf(70));
        InterviewSessionDto second = service.start("interview-user-4", startRequest("Data Analyst"));
        service.appendMessage("interview-user-4", second.getInterviewId(), message("USER", "new"));
        service.end("interview-user-4", second.getInterviewId(), Integer.valueOf(80));

        List<InterviewSessionDto> history = service.listByUser("interview-user-4");
        service.delete("interview-user-4", first.getInterviewId());

        assertEquals(second.getInterviewId(), history.get(0).getInterviewId());
        assertEquals(1, service.getMessages("interview-user-4", second.getInterviewId()).size());
        assertThrows(IllegalArgumentException.class, new ThrowingRunnableAdapter(new Runnable() {
            public void run() {
                service.get("interview-user-4", first.getInterviewId());
            }
        }));
    }

    @Test
    void historyOnlyReturnsCompletedInterviewsWithUserAnswers() {
        InterviewApplicationService service = service(new InMemoryInterviewStorage(),
                profileService(new InMemoryCareerProfileStorage()));
        InterviewSessionDto completed = service.start("history-filter-user", startRequest("后端开发"));
        service.appendMessage("history-filter-user", completed.getInterviewId(), message("USER", "我完成了一道回答"));
        service.end("history-filter-user", completed.getInterviewId(), Integer.valueOf(75));
        InterviewSessionDto emptyCompleted = service.start("history-filter-user", startRequest("测试开发"));
        service.end("history-filter-user", emptyCompleted.getInterviewId(), null);
        service.start("history-filter-user", startRequest("产品经理"));

        List<InterviewSessionDto> history = service.listByUser("history-filter-user");
        InterviewPageResultDto page = service.listPage("history-filter-user", 1, InterviewConstants.MODE_TEXT);

        assertEquals(1, history.size());
        assertEquals(completed.getInterviewId(), history.get(0).getInterviewId());
        assertEquals(Integer.valueOf(1), page.getTotal());
    }

    @Test
    void startingNewInterviewRemovesOngoingSessionAndMessages() {
        InterviewApplicationService service = service(new InMemoryInterviewStorage(),
                profileService(new InMemoryCareerProfileStorage()));
        InterviewSessionDto abandoned = service.start("cleanup-user", startRequest("后端开发"));
        service.appendMessage("cleanup-user", abandoned.getInterviewId(), message("USER", "尚未主动结束"));

        InterviewSessionDto current = service.start("cleanup-user", startRequest("测试开发"));

        assertThrows(IllegalArgumentException.class, new ThrowingRunnableAdapter(new Runnable() {
            public void run() {
                service.get("cleanup-user", abandoned.getInterviewId());
            }
        }));
        assertEquals(current.getInterviewId(), service.get("cleanup-user", current.getInterviewId()).getInterviewId());
    }

    @Test
    void discardingInterviewRemovesSessionAndMessages() {
        InterviewApplicationService service = service(new InMemoryInterviewStorage(),
                profileService(new InMemoryCareerProfileStorage()));
        InterviewSessionDto session = service.start("discard-user", startRequest("后端开发"));

        service.discard("discard-user", session.getInterviewId());

        assertTrue(service.listByUser("discard-user").isEmpty());
        assertThrows(IllegalArgumentException.class, new ThrowingRunnableAdapter(new Runnable() {
            public void run() {
                service.get("discard-user", session.getInterviewId());
            }
        }));
    }

    @Test
    void guidedInterviewUsesRealAnalyzerAndCachesItsReport() {
        CareerProfileApplicationService profileService = profileService(new InMemoryCareerProfileStorage());
        ResumeApplicationService resumeService = new ResumeApplicationService(new InMemoryResumeStorage(), profileService);
        InterviewApplicationService service = new InterviewApplicationService(new InMemoryInterviewStorage(), profileService,
                new InterviewCoreService(), resumeService, new InterviewAiService(questionGateway(), evidenceAnalyzer()));

        v620.cc001.base.common.dto.career.InterviewStartResultDto started = service.startGuided("guided-user", startRequest("测试工程师"));
        assertEquals("请结合一段真实经历说明你的处理方式。", started.getOpeningMessage().getContent());

        v620.cc001.base.common.dto.career.InterviewTurnResultDto turn = service.answer("guided-user", started.getSession().getInterviewId(), "我通过自动化测试减少了重复验证时间。");
        assertEquals(3, service.getMessages("guided-user", started.getSession().getInterviewId()).size());
        assertEquals(InterviewConstants.ROLE_AI, turn.getInterviewerMessage().getRole());

        InterviewReportDto first = service.finishAndReport("guided-user", started.getSession().getInterviewId());
        InterviewReportDto cached = service.finishAndReport("guided-user", started.getSession().getInterviewId());
        assertEquals(first.getOverallScore(), cached.getOverallScore());
        assertEquals(InterviewReportDto.ANALYSIS_SOURCE_AI_AGENT, first.getAnalysisSource());
        assertEquals(InterviewConstants.STATUS_COMPLETED, service.get("guided-user", started.getSession().getInterviewId()).getStatus());
    }

    @Test
    void guidedInterviewRejectsScoringWithoutAnAnswer() {
        CareerProfileApplicationService profileService = profileService(new InMemoryCareerProfileStorage());
        InterviewApplicationService service = new InterviewApplicationService(new InMemoryInterviewStorage(),
                profileService, new InterviewCoreService(),
                new ResumeApplicationService(new InMemoryResumeStorage(), profileService),
                new InterviewAiService(questionGateway(), evidenceAnalyzer()));
        InterviewSessionDto session = service.startGuided("early-finish-user", startRequest("前端开发工程师")).getSession();

        assertThrows(IllegalArgumentException.class, new ThrowingRunnableAdapter(new Runnable() {
            public void run() {
                service.finishAndReport("early-finish-user", session.getInterviewId());
            }
        }));
        assertEquals(InterviewConstants.STATUS_ONGOING,
                service.get("early-finish-user", session.getInterviewId()).getStatus());
    }

    @Test
    void guidedInterviewStopsAfterSevenAnswers() {
        CareerProfileApplicationService profileService = profileService(new InMemoryCareerProfileStorage());
        InterviewApplicationService service = new InterviewApplicationService(new InMemoryInterviewStorage(), profileService,
                new InterviewCoreService(), new ResumeApplicationService(new InMemoryResumeStorage(), profileService),
                new InterviewAiService(questionGateway(), evidenceAnalyzer()));
        InterviewSessionDto session = service.startGuided("seven-answer-user", startRequest("产品经理")).getSession();

        v620.cc001.base.common.dto.career.InterviewTurnResultDto last = null;
        for (int index = 1; index <= InterviewConstants.MAX_AI_INTERVIEW_QUESTIONS; index++) {
            last = service.answer("seven-answer-user", session.getInterviewId(), "第" + index + "次有效回答");
        }

        assertTrue(last.getInterviewerMessage().getContent().contains("7 道题已经完成"));
        assertThrows(IllegalArgumentException.class, new ThrowingRunnableAdapter(new Runnable() {
            public void run() {
                service.answer("seven-answer-user", session.getInterviewId(), "第八次回答");
            }
        }));
        assertEquals(Integer.valueOf(7), service.finishAndReport("seven-answer-user", session.getInterviewId()).getTotalQuestions());
    }

    @Test
    void cachedReportRepairsOngoingSessionStatus() {
        InterviewApplicationService service = service(new InMemoryInterviewStorage(),
                profileService(new InMemoryCareerProfileStorage()));
        InterviewSessionDto session = service.start("cached-report-user", startRequest("前端开发"));
        service.saveReport("cached-report-user", session.getInterviewId(), report());

        InterviewReportDto cached = service.finishAndReport("cached-report-user", session.getInterviewId());

        assertEquals(Integer.valueOf(86), cached.getOverallScore());
        assertEquals(InterviewConstants.STATUS_COMPLETED,
                service.get("cached-report-user", session.getInterviewId()).getStatus());
    }

    @Test
    void unavailableAnalyzerSavesTransparentBasicRulesReport() {
        CareerProfileApplicationService profileService = profileService(new InMemoryCareerProfileStorage());
        InterviewApplicationService service = new InterviewApplicationService(new InMemoryInterviewStorage(), profileService,
                new InterviewCoreService(), new ResumeApplicationService(new InMemoryResumeStorage(), profileService),
                new InterviewAiService(null, new InterviewReportAnalyzer() {
                    public InterviewReportDto analyze(InterviewSessionDto session, String transcript, int answerCount) {
                        throw new IllegalStateException("AI 面试分析暂时不可用，请稍后重试。");
                    }
                }));
        InterviewSessionDto session = service.start("analysis-unavailable-user", startRequest("测试工程师"));
        service.appendMessage("analysis-unavailable-user", session.getInterviewId(), message("USER", "我完成了真实回答"));

        InterviewReportDto report = service.finishAndReport("analysis-unavailable-user", session.getInterviewId());

        InterviewSessionDto saved = service.get("analysis-unavailable-user", session.getInterviewId());
        assertEquals(InterviewConstants.STATUS_COMPLETED, saved.getStatus());
        assertEquals(InterviewReportDto.ANALYSIS_SOURCE_BASIC_RULES, report.getAnalysisSource());
        assertEquals(report.getOverallScore(), saved.getFinalScore());
        assertTrue(report.getTextSummary().contains("基础规则复盘"));
    }

    @Test
    void unavailableQuestionGatewayStillStartsAndContinuesInterview() {
        CareerProfileApplicationService profileService = profileService(new InMemoryCareerProfileStorage());
        InterviewApplicationService service = new InterviewApplicationService(new InMemoryInterviewStorage(), profileService,
                new InterviewCoreService(), new ResumeApplicationService(new InMemoryResumeStorage(), profileService),
                new InterviewAiService(null, evidenceAnalyzer()));

        v620.cc001.base.common.dto.career.InterviewStartResultDto started = service.startGuided(
                "question-fallback-user", startRequest("后端开发工程师"));
        v620.cc001.base.common.dto.career.InterviewTurnResultDto turn = service.answer(
                "question-fallback-user", started.getSession().getInterviewId(), "我完成过接口开发和上线。" );

        assertTrue(started.getOpeningMessage().getContent().startsWith("【基础练习题】"));
        assertTrue(turn.getInterviewerMessage().getContent().startsWith("【基础练习题·"));
    }

    @Test
    void pagesPersistedTextInterviewHistoryTenAtATime() {
        InterviewApplicationService service = service(new InMemoryInterviewStorage(),
                profileService(new InMemoryCareerProfileStorage()));
        for (int index = 1; index <= 12; index++) {
            InterviewSessionDto session = service.start("page-history-user", startRequest("前端开发 " + index));
            service.appendMessage("page-history-user", session.getInterviewId(), message("USER", "第" + index + "次回答"));
            service.end("page-history-user", session.getInterviewId(), Integer.valueOf(60 + index));
        }
        InterviewStartRequest voice = startRequest("全景练习");
        voice.setMode(InterviewConstants.MODE_VOICE);
        InterviewSessionDto panoramicSession = service.start("page-history-user", voice);
        service.appendMessage("page-history-user", panoramicSession.getInterviewId(), message("USER", "全景回答"));
        service.end("page-history-user", panoramicSession.getInterviewId(), Integer.valueOf(80));

        InterviewPageResultDto first = service.listPage("page-history-user", 1, InterviewConstants.MODE_TEXT);
        InterviewPageResultDto second = service.listPage("page-history-user", 2, InterviewConstants.MODE_TEXT);
        InterviewPageResultDto panoramic = service.listPage("page-history-user", 1, InterviewConstants.MODE_VOICE);

        assertEquals(Integer.valueOf(10), Integer.valueOf(first.getItems().size()));
        assertEquals(Integer.valueOf(12), first.getTotal());
        assertEquals(Integer.valueOf(2), first.getTotalPages());
        assertEquals(Integer.valueOf(2), Integer.valueOf(second.getItems().size()));
        assertEquals(Integer.valueOf(2), second.getPage());
        assertEquals(Integer.valueOf(1), panoramic.getTotal());
        assertEquals(Integer.valueOf(1), Integer.valueOf(panoramic.getItems().size()));
        assertEquals(InterviewConstants.MODE_VOICE, panoramic.getItems().get(0).getMode());
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

    private InterviewReportAnalyzer evidenceAnalyzer() {
        return new InterviewReportAnalyzer() {
            public InterviewReportDto analyze(InterviewSessionDto session, String transcript, int answerCount) {
                InterviewReportDto report = report();
                report.setTotalQuestions(Integer.valueOf(answerCount));
                return report;
            }
        };
    }

    private AiGateway questionGateway() {
        return new AiGateway() {
            public AiChatResponseDto chat(AiChatRequestDto request) {
                AiChatResponseDto response = new AiChatResponseDto();
                response.setContent("请结合一段真实经历说明你的处理方式。");
                return response;
            }

            public List<AiStreamEventDto> stream(AiChatRequestDto request) {
                return Collections.emptyList();
            }
        };
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
