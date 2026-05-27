package v620.base.helper.career;

import org.junit.jupiter.api.Test;
import v620.cc001.base.common.dto.career.InterviewConstants;
import v620.cc001.base.common.dto.career.InterviewRadarScoreDto;
import v620.cc001.base.common.dto.career.InterviewReportDto;
import v620.cc001.base.common.dto.career.InterviewSessionDto;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InterviewCoreServiceTest {

    private final InterviewCoreService service = new InterviewCoreService();

    @Test
    void normalizesModeDifficultyStatusAndRole() {
        assertEquals(InterviewConstants.MODE_TEXT, service.normalizeMode(null));
        assertEquals(InterviewConstants.MODE_VOICE, service.normalizeMode(" voice "));
        assertEquals(InterviewConstants.MODE_TEXT, service.normalizeMode("video"));
        assertEquals(InterviewConstants.DIFFICULTY_NORMAL, service.normalizeDifficulty(" "));
        assertEquals(InterviewConstants.DIFFICULTY_HARD, service.normalizeDifficulty("hard"));
        assertEquals(InterviewConstants.STATUS_COMPLETED, service.normalizeStatus("completed"));
        assertEquals(InterviewConstants.STATUS_ONGOING, service.normalizeStatus("bad"));
        assertEquals(InterviewConstants.ROLE_AI, service.normalizeRole("ai"));
        assertEquals(InterviewConstants.ROLE_USER, service.normalizeRole("candidate"));
    }

    @Test
    void computesDurationSeconds() {
        LocalDateTime started = LocalDateTime.of(2026, 5, 27, 10, 0);
        LocalDateTime ended = started.plusMinutes(8).plusSeconds(5);

        assertEquals(485, service.durationSeconds(started, ended));
        assertEquals(0, service.durationSeconds(ended, started));
    }

    @Test
    void extractsStrongAndWeakRadarDimensions() {
        InterviewReportDto report = new InterviewReportDto();
        InterviewRadarScoreDto radar = new InterviewRadarScoreDto();
        radar.setExpression(Integer.valueOf(82));
        radar.setLogic(Integer.valueOf(55));
        radar.setTechnical(Integer.valueOf(79));
        radar.setCommunication(Integer.valueOf(40));
        report.setRadarScore(radar);

        assertTrue(service.strongDimensions(report).contains("expression"));
        assertTrue(service.weakDimensions(report).contains("logic"));
        assertTrue(service.weakDimensions(report).contains("communication"));
        assertEquals(1, service.strongDimensions(report).size());
    }

    @Test
    void convertsSessionAndReportToProfileInterviewBlock() {
        InterviewSessionDto session = new InterviewSessionDto();
        session.setInterviewId(Long.valueOf(7L));
        session.setPositionName("Java Engineer");
        session.setDifficulty("Normal");
        session.setFinalScore(Integer.valueOf(76));
        session.setEndedAt(LocalDateTime.of(2026, 5, 27, 11, 0));
        InterviewReportDto report = new InterviewReportDto();
        InterviewRadarScoreDto radar = new InterviewRadarScoreDto();
        radar.setTechnical(Integer.valueOf(88));
        radar.setCommunication(Integer.valueOf(58));
        report.setRadarScore(radar);
        session.setReport(report);

        UserProfileSnapshot.InterviewBlock block = service.toInterviewBlock(session);

        assertEquals(Long.valueOf(7L), block.getLastInterviewId());
        assertEquals("Java Engineer", block.getPositionName());
        assertEquals(Integer.valueOf(76), block.getLastScore());
        assertTrue(block.getStrongDimensions().contains("technical"));
        assertTrue(block.getWeakDimensions().contains("communication"));
    }
}
