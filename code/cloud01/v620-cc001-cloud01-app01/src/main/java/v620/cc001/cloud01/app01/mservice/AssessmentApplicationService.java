package v620.cc001.cloud01.app01.mservice;

import v620.base.helper.career.AssessmentScoringService;
import v620.cc001.base.common.dto.career.AssessmentScaleDto;
import v620.cc001.base.common.dto.career.AssessmentScoreResult;
import v620.cc001.base.common.dto.career.AssessmentSubmitRequest;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Application boundary for assessment scoring and profile snapshot integration.
 */
public class AssessmentApplicationService {

    private final AssessmentScoringService scoringService;
    private final AssessmentCatalog catalog;
    private final AssessmentResultStorage resultStorage;
    private final CareerProfileApplicationService profileApplicationService;

    public AssessmentApplicationService() {
        this(new AssessmentScoringService(), new InMemoryAssessmentCatalog(),
                CyanCruiseStorageFactory.assessmentResultStorage(), new CareerProfileApplicationService());
    }

    public AssessmentApplicationService(AssessmentScoringService scoringService,
                                        CareerProfileApplicationService profileApplicationService) {
        this(scoringService, new InMemoryAssessmentCatalog(),
                new InMemoryAssessmentResultStorage(), profileApplicationService);
    }

    public AssessmentApplicationService(AssessmentScoringService scoringService,
                                        AssessmentCatalog catalog,
                                        AssessmentResultStorage resultStorage,
                                        CareerProfileApplicationService profileApplicationService) {
        this.scoringService = scoringService;
        this.catalog = catalog;
        this.resultStorage = resultStorage;
        this.profileApplicationService = profileApplicationService;
    }

    public List<AssessmentScaleDto> listScales() {
        return catalog.listScales();
    }

    public AssessmentScaleDto getScale(Long scaleId) {
        AssessmentScaleDto scale = catalog.loadScale(scaleId);
        if (scale == null) {
            throw new IllegalArgumentException("Unknown assessment scale: " + scaleId);
        }
        return scale;
    }

    public AssessmentScoreResult submit(String userId, AssessmentSubmitRequest request) {
        AssessmentSubmitRequest safeRequest = request == null ? new AssessmentSubmitRequest() : request;
        AssessmentScaleDto scale = getScale(safeRequest.getScaleId());
        return submitAndSaveProfile(userId, scale, safeRequest);
    }

    public AssessmentScoreResult loadResult(String userId, Long recordId) {
        return resultStorage.loadResult(userId, recordId);
    }

    public List<AssessmentScoreResult> listResults(String userId) {
        return resultStorage.listResults(userId);
    }

    public AssessmentScoreResult submitAndSaveProfile(String userId,
                                                      AssessmentScaleDto scale,
                                                      AssessmentSubmitRequest request) {
        AssessmentScoreResult result = scoringService.score(scale, request);
        result.setUserId(userId);
        result.setSuggestedRoles(suggestRoles(result.getResultSummary(), result.getDimensionCounts()));
        result.setCreatedAt(LocalDateTime.now());
        Long recordId = resultStorage.saveResult(userId, result);
        UserProfileSnapshot.AssessmentBlock block = new UserProfileSnapshot.AssessmentBlock();
        block.setLastRecordId(recordId);
        block.setScaleId(result.getScaleId());
        block.setScaleTitle(result.getScaleTitle());
        block.setSummary(result.getResultSummary());
        block.setSuggestedRoles(result.getSuggestedRoles());
        block.setCompletedAt(LocalDateTime.now());
        profileApplicationService.saveAssessment(userId, block);
        return result;
    }

    private List<String> suggestRoles(String summary, Map<String, Integer> counts) {
        if (summary == null) {
            return Collections.emptyList();
        }
        if (summary.indexOf('N') >= 0 && summary.indexOf('T') >= 0) {
            return Arrays.asList("产品经理", "数据分析师", "后端开发工程师");
        }
        if (summary.indexOf('S') >= 0 && summary.indexOf('J') >= 0) {
            return Arrays.asList("项目执行专员", "测试工程师", "运营分析师");
        }
        if (summary.indexOf('F') >= 0) {
            return Arrays.asList("用户研究员", "人力资源专员", "客户成功顾问");
        }
        return Arrays.asList("软件工程师", "业务分析师", "解决方案顾问");
    }
}
