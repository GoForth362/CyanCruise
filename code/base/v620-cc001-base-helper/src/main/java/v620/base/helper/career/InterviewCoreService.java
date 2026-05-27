package v620.base.helper.career;

import v620.cc001.base.common.dto.career.InterviewConstants;
import v620.cc001.base.common.dto.career.InterviewRadarScoreDto;
import v620.cc001.base.common.dto.career.InterviewReportDto;
import v620.cc001.base.common.dto.career.InterviewSessionDto;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Pure Java helper rules for the migrated interview core.
 */
public class InterviewCoreService {

    private static final int STRONG_THRESHOLD = 80;
    private static final int WEAK_THRESHOLD = 60;

    public String normalizeMode(String mode) {
        if (!hasText(mode)) {
            return InterviewConstants.MODE_TEXT;
        }
        String normalized = mode.trim().toUpperCase();
        if (InterviewConstants.MODE_VOICE.equals(normalized)) {
            return InterviewConstants.MODE_VOICE;
        }
        return InterviewConstants.MODE_TEXT;
    }

    public String normalizeDifficulty(String difficulty) {
        if (!hasText(difficulty)) {
            return InterviewConstants.DIFFICULTY_NORMAL;
        }
        String value = difficulty.trim();
        if ("easy".equalsIgnoreCase(value)) {
            return InterviewConstants.DIFFICULTY_EASY;
        }
        if ("hard".equalsIgnoreCase(value)) {
            return InterviewConstants.DIFFICULTY_HARD;
        }
        return InterviewConstants.DIFFICULTY_NORMAL;
    }

    public String normalizeStatus(String status) {
        if (InterviewConstants.STATUS_COMPLETED.equalsIgnoreCase(status)) {
            return InterviewConstants.STATUS_COMPLETED;
        }
        if (InterviewConstants.STATUS_CANCELLED.equalsIgnoreCase(status)) {
            return InterviewConstants.STATUS_CANCELLED;
        }
        return InterviewConstants.STATUS_ONGOING;
    }

    public String normalizeRole(String role) {
        if (InterviewConstants.ROLE_AI.equalsIgnoreCase(role)) {
            return InterviewConstants.ROLE_AI;
        }
        return InterviewConstants.ROLE_USER;
    }

    public int durationSeconds(LocalDateTime startedAt, LocalDateTime endedAt) {
        if (startedAt == null || endedAt == null || endedAt.isBefore(startedAt)) {
            return 0;
        }
        long seconds = Duration.between(startedAt, endedAt).getSeconds();
        return seconds > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) seconds;
    }

    public List<String> strongDimensions(InterviewReportDto report) {
        return dimensions(report, true);
    }

    public List<String> weakDimensions(InterviewReportDto report) {
        return dimensions(report, false);
    }

    public UserProfileSnapshot.InterviewBlock toInterviewBlock(InterviewSessionDto session) {
        if (session == null) {
            return null;
        }
        UserProfileSnapshot.InterviewBlock block = new UserProfileSnapshot.InterviewBlock();
        block.setLastInterviewId(session.getInterviewId());
        block.setPositionName(session.getPositionName());
        block.setDifficulty(session.getDifficulty());
        block.setLastScore(session.getFinalScore());
        block.setCompletedAt(session.getEndedAt() == null ? LocalDateTime.now() : session.getEndedAt());
        if (session.getReport() != null) {
            block.setStrongDimensions(strongDimensions(session.getReport()));
            block.setWeakDimensions(weakDimensions(session.getReport()));
        }
        return block;
    }

    private List<String> dimensions(InterviewReportDto report, boolean strong) {
        List<String> values = new ArrayList<String>();
        if (report == null || report.getRadarScore() == null) {
            return values;
        }
        InterviewRadarScoreDto radar = report.getRadarScore();
        addDimension(values, "expression", radar.getExpression(), strong);
        addDimension(values, "logic", radar.getLogic(), strong);
        addDimension(values, "technical", radar.getTechnical(), strong);
        addDimension(values, "pressureResistance", radar.getPressureResistance(), strong);
        addDimension(values, "communication", radar.getCommunication(), strong);
        addDimension(values, "bodyLanguage", radar.getBodyLanguage(), strong);
        return values;
    }

    private void addDimension(List<String> values, String name, Integer score, boolean strong) {
        if (score == null) {
            return;
        }
        if (strong && score.intValue() >= STRONG_THRESHOLD) {
            values.add(name);
        }
        if (!strong && score.intValue() < WEAK_THRESHOLD) {
            values.add(name);
        }
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
