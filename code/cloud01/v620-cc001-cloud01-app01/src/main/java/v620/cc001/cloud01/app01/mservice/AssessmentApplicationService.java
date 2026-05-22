package v620.cc001.cloud01.app01.mservice;

import v620.base.helper.career.AssessmentScoringService;
import v620.cc001.base.common.dto.career.AssessmentScaleDto;
import v620.cc001.base.common.dto.career.AssessmentScoreResult;
import v620.cc001.base.common.dto.career.AssessmentSubmitRequest;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;

import java.time.LocalDateTime;

/**
 * Application boundary for assessment scoring and profile snapshot integration.
 */
public class AssessmentApplicationService {

    private final AssessmentScoringService scoringService;
    private final CareerProfileApplicationService profileApplicationService;

    public AssessmentApplicationService() {
        this(new AssessmentScoringService(), new CareerProfileApplicationService());
    }

    public AssessmentApplicationService(AssessmentScoringService scoringService,
                                        CareerProfileApplicationService profileApplicationService) {
        this.scoringService = scoringService;
        this.profileApplicationService = profileApplicationService;
    }

    public AssessmentScoreResult submitAndSaveProfile(String userId,
                                                      AssessmentScaleDto scale,
                                                      AssessmentSubmitRequest request) {
        AssessmentScoreResult result = scoringService.score(scale, request);
        UserProfileSnapshot.AssessmentBlock block = new UserProfileSnapshot.AssessmentBlock();
        block.setScaleId(result.getScaleId());
        block.setScaleTitle(result.getScaleTitle());
        block.setSummary(result.getResultSummary());
        block.setCompletedAt(LocalDateTime.now());
        profileApplicationService.saveAssessment(userId, block);
        return result;
    }
}
