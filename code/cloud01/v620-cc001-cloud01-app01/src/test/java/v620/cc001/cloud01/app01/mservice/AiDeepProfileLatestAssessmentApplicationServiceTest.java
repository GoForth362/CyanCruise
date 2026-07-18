package v620.cc001.cloud01.app01.mservice;

import org.junit.jupiter.api.Test;
import v620.base.helper.career.CareerProfileBuildService;
import v620.base.helper.career.CareerProfileSnapshotMergeService;
import v620.cc001.base.common.dto.career.AssessmentScoreResult;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;
import v620.cc001.cloud01.app01.mservice.ai.impl.AgentPlatformDeepProfileAnalyzer;
import v620.cc001.cloud01.app01.mservice.application.AiDeepProfileApplicationService;
import v620.cc001.cloud01.app01.mservice.application.CareerProfileApplicationService;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryAssessmentResultStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryCareerProfileStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AiDeepProfileLatestAssessmentApplicationServiceTest {

    @Test
    void sendsOnlyLatestCompletedResultForEachScaleToAgent() {
        String userId = "latest-assessment-user";
        InMemoryAssessmentResultStorage assessmentStorage = new InMemoryAssessmentResultStorage();
        assessmentStorage.saveResult(userId, result(1L, "MBTI", "old", 1));
        assessmentStorage.saveResult(userId, result(2L, "RIASEC", "interest", 2));
        assessmentStorage.saveResult(userId, result(1L, "MBTI", "latest", 3));

        CareerProfileApplicationService profileService = new CareerProfileApplicationService(
                new InMemoryCareerProfileStorage(),
                new CareerProfileSnapshotMergeService(),
                new CareerProfileBuildService());
        CapturingAnalyzer analyzer = new CapturingAnalyzer();
        AiDeepProfileApplicationService service = new AiDeepProfileApplicationService(
                assessmentStorage, profileService, analyzer);

        service.generate(userId);

        assertEquals(2, analyzer.results.size());
        assertEquals("interest", summaryForScale(analyzer.results, 2L));
        assertEquals("latest", summaryForScale(analyzer.results, 1L));
    }

    private AssessmentScoreResult result(Long scaleId, String title, String summary, int minute) {
        AssessmentScoreResult result = new AssessmentScoreResult();
        result.setScaleId(scaleId);
        result.setScaleTitle(title);
        result.setResultSummary(summary);
        result.setStatus("COMPLETED");
        result.setCreatedAt(LocalDateTime.of(2026, 7, 17, 12, minute));
        return result;
    }

    private String summaryForScale(List<AssessmentScoreResult> results, Long scaleId) {
        for (AssessmentScoreResult result : results) {
            if (scaleId.equals(result.getScaleId())) return result.getResultSummary();
        }
        return null;
    }

    private static class CapturingAnalyzer extends AgentPlatformDeepProfileAnalyzer {
        private List<AssessmentScoreResult> results = new ArrayList<AssessmentScoreResult>();

        CapturingAnalyzer() {
            super(null);
        }

        @Override
        public UserProfileSnapshot.AiDeepProfileBlock analyze(List<AssessmentScoreResult> results,
                                                               UserProfileSnapshot snapshot) {
            this.results = new ArrayList<AssessmentScoreResult>(results);
            UserProfileSnapshot.AiDeepProfileBlock block = new UserProfileSnapshot.AiDeepProfileBlock();
            block.setProfileSummary("latest only");
            block.setSource("AI_ASSESSMENT");
            block.setGeneratedAt(LocalDateTime.now());
            return block;
        }
    }
}
