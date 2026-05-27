package v620.cc001.cloud01.app01.mservice;

import org.junit.jupiter.api.Test;
import v620.cc001.base.common.dto.career.AssessmentScoreResult;
import v620.cc001.base.common.dto.career.AssistantChatMessageDto;
import v620.cc001.base.common.dto.career.AssistantChatSessionDto;
import v620.cc001.base.common.dto.career.CareerAgentTodayDto;
import v620.cc001.base.common.dto.career.CareerPlanRecordDto;
import v620.cc001.base.common.dto.career.CareerUserProfileDto;
import v620.cc001.base.common.dto.career.InterviewMessageDto;
import v620.cc001.base.common.dto.career.InterviewSessionDto;
import v620.cc001.base.common.dto.career.ResumeRecordDto;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;
import v620.cc001.cloud01.app01.mservice.datamodel.CareerLoopDatamodelObjects;
import v620.cc001.cloud01.app01.mservice.datamodel.CosmicDatamodelRecord;
import v620.cc001.cloud01.app01.mservice.datamodel.InMemoryCosmicDatamodelGateway;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CosmicDatamodelStorageTest {

    @Test
    void profileStorageKeepsStructuredUserFieldsAndFactMap() {
        InMemoryCosmicDatamodelGateway gateway = new InMemoryCosmicDatamodelGateway();
        CosmicCareerProfileStorage storage = new CosmicCareerProfileStorage(gateway);
        UserProfileSnapshot snapshot = new UserProfileSnapshot();
        snapshot.setVersion(Integer.valueOf(2));
        CareerUserProfileDto profile = new CareerUserProfileDto();
        profile.setPersonalizationLevel("HIGH");
        profile.setCompletenessScore(Integer.valueOf(88));

        storage.saveSnapshot("user-1", snapshot);
        storage.saveFact("user-1", "target", "Java backend");
        storage.saveProfile("user-1", profile);

        assertEquals(Integer.valueOf(2), storage.loadSnapshot("user-1").getVersion());
        assertEquals("Java backend", storage.loadFacts("user-1").get("target"));
        assertEquals(Integer.valueOf(88), storage.loadProfile("user-1").getCompletenessScore());
        CosmicDatamodelRecord row = gateway.findOne(CareerLoopDatamodelObjects.USER_PROFILE, null);
        assertEquals("user-1", row.get(CareerLoopDatamodelObjects.USER_ID));
        assertEquals("HIGH", row.get("personalization_level"));
    }

    @Test
    void resumeStorageMapsFieldsAndSortsByUpdatedAt() {
        CosmicResumeStorage storage = new CosmicResumeStorage(new InMemoryCosmicDatamodelGateway());
        ResumeRecordDto first = resume("user-2", "first", LocalDateTime.of(2026, 1, 1, 8, 0));
        ResumeRecordDto second = resume("user-2", "second", LocalDateTime.of(2026, 1, 2, 8, 0));

        ResumeRecordDto savedFirst = storage.save(first);
        ResumeRecordDto savedSecond = storage.save(second);
        List<ResumeRecordDto> list = storage.listByUser("user-2");

        assertEquals(savedSecond.getResumeId(), list.get(0).getResumeId());
        assertEquals(savedFirst.getResumeId(), list.get(1).getResumeId());
        assertEquals("Java", storage.load(savedSecond.getResumeId()).getTargetJob());
        storage.delete(savedFirst.getResumeId());
        assertEquals(1, storage.listByUser("user-2").size());
    }

    @Test
    void assessmentStorageRejectsCrossUserLoad() {
        CosmicAssessmentResultStorage storage = new CosmicAssessmentResultStorage(new InMemoryCosmicDatamodelGateway());
        AssessmentScoreResult result = new AssessmentScoreResult();
        result.setScaleId(Long.valueOf(10L));
        result.setScaleTitle("MBTI");
        result.setResultSummary("INTJ");

        Long recordId = storage.saveResult("owner", result);

        assertEquals("INTJ", storage.loadResult("owner", recordId).getResultSummary());
        assertThrows(IllegalArgumentException.class, new ThrowingRunnableAdapter(new Runnable() {
            public void run() {
                storage.loadResult("other", recordId);
            }
        }));
    }

    @Test
    void interviewStorageOrdersMessagesAndDeletesCascade() {
        CosmicInterviewStorage storage = new CosmicInterviewStorage(new InMemoryCosmicDatamodelGateway());
        InterviewSessionDto session = new InterviewSessionDto();
        session.setUserId("interview-user");
        session.setPositionName("Backend");
        session.setStatus("ONGOING");
        session.setStartedAt(LocalDateTime.of(2026, 1, 1, 9, 0));
        InterviewSessionDto saved = storage.saveInterview(session);
        storage.saveMessage(message(saved.getInterviewId(), "assistant", "q1", LocalDateTime.of(2026, 1, 1, 9, 1)));
        storage.saveMessage(message(saved.getInterviewId(), "user", "a1", LocalDateTime.of(2026, 1, 1, 9, 2)));

        assertEquals(2, storage.listMessages(saved.getInterviewId()).size());
        assertEquals("q1", storage.listMessages(saved.getInterviewId()).get(0).getContent());
        storage.deleteInterview(saved.getInterviewId());
        assertTrue(storage.listMessages(saved.getInterviewId()).isEmpty());
    }

    @Test
    void assistantStorageKeepsTokenFieldsAndDeletesCascade() {
        CosmicAssistantChatStorage storage = new CosmicAssistantChatStorage(new InMemoryCosmicDatamodelGateway());
        AssistantChatSessionDto session = new AssistantChatSessionDto();
        session.setUserId("chat-user");
        session.setTitle("Plan");
        session.setPersona("MENTOR");
        AssistantChatSessionDto saved = storage.saveSession(session);
        AssistantChatMessageDto message = new AssistantChatMessageDto();
        message.setSessionId(saved.getSessionId());
        message.setRole("assistant");
        message.setContent("reply");
        message.setTotalTokens(Integer.valueOf(9));
        storage.saveMessage(message);

        assertEquals(Integer.valueOf(9), storage.listMessages(saved.getSessionId()).get(0).getTotalTokens());
        storage.deleteSession(saved.getSessionId());
        assertTrue(storage.listMessages(saved.getSessionId()).isEmpty());
    }

    @Test
    void planAndFallbackStorageRemainReplaceable() {
        InMemoryCosmicDatamodelGateway gateway = new InMemoryCosmicDatamodelGateway();
        CosmicCareerPlanStorage planStorage = new CosmicCareerPlanStorage(gateway);
        CareerPlanRecordDto plan = new CareerPlanRecordDto();
        plan.setUserId("plan-user");
        plan.setTargetRole("Java backend");
        planStorage.save("plan-user", plan);
        CosmicCareerAgentTaskStorage taskStorage = new CosmicCareerAgentTaskStorage(gateway);
        List<CareerAgentTodayDto.Action> actions = new ArrayList<CareerAgentTodayDto.Action>();
        actions.add(action("resume", "HIGH"));
        actions.add(action("interview", "MEDIUM"));
        taskStorage.saveTodayActions("plan-user", LocalDate.of(2026, 5, 28), actions);

        assertTrue(planStorage.exists("plan-user"));
        assertEquals("Java backend", planStorage.load("plan-user").getTargetRole());
        assertEquals("resume", taskStorage.listTodayActions("plan-user", LocalDate.of(2026, 5, 28)).get(0).getLabelKey());
        assertNotNull(new InMemoryResumeStorage());
        assertNotNull(new InMemoryAssistantChatStorage());
    }

    private ResumeRecordDto resume(String userId, String title, LocalDateTime updatedAt) {
        ResumeRecordDto record = new ResumeRecordDto();
        record.setUserId(userId);
        record.setTitle(title);
        record.setTargetJob("Java");
        record.setFileKey("resumes/" + title + ".pdf");
        record.setCreatedAt(updatedAt.minusDays(1L));
        record.setUpdatedAt(updatedAt);
        return record;
    }

    private InterviewMessageDto message(Long interviewId, String role, String content, LocalDateTime createdAt) {
        InterviewMessageDto message = new InterviewMessageDto();
        message.setInterviewId(interviewId);
        message.setRole(role);
        message.setContent(content);
        message.setCreatedAt(createdAt);
        return message;
    }

    private CareerAgentTodayDto.Action action(String key, String priority) {
        CareerAgentTodayDto.Action action = new CareerAgentTodayDto.Action();
        action.setLabel(key + " task");
        action.setLabelKey(key);
        action.setType("ASSISTANT");
        action.setPriority(priority);
        action.setTarget("/pages/assistant/index");
        action.setSource("DAILY_AGENT");
        return action;
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
