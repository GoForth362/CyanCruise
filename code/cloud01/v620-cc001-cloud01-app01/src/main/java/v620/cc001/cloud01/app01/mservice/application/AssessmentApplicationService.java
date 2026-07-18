package v620.cc001.cloud01.app01.mservice.application;

import v620.cc001.cloud01.app01.mservice.ai.AgentPlatformTaskFlowConfig;
import v620.cc001.cloud01.app01.mservice.ai.impl.AgentPlatformAssessmentInterpreter;
import v620.cc001.cloud01.app01.mservice.ai.impl.DefaultAgentPlatformTaskFlowClient;
import v620.cc001.cloud01.app01.mservice.ai.impl.KingdeeAgentSdkTaskFlowClient;
import v620.cc001.cloud01.app01.mservice.storage.AssessmentCatalog;
import v620.cc001.cloud01.app01.mservice.storage.AssessmentAttemptStorage;
import v620.cc001.cloud01.app01.mservice.storage.AssessmentResultStorage;
import v620.cc001.cloud01.app01.mservice.storage.CyanCruiseStorageFactory;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryAssessmentAttemptStorage;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryAssessmentCatalog;
import v620.cc001.cloud01.app01.mservice.storage.impl.InMemoryAssessmentResultStorage;
import v620.base.helper.career.AssessmentQuestionSelectionService;
import v620.base.helper.career.AssessmentScoringService;
import v620.cc001.base.common.dto.career.AssessmentAttemptDto;
import v620.cc001.base.common.dto.career.AssessmentOptionDto;
import v620.cc001.base.common.dto.career.AssessmentScaleDto;
import v620.cc001.base.common.dto.career.AssessmentQuestionDto;
import v620.cc001.base.common.dto.career.AssessmentScoreResult;
import v620.cc001.base.common.dto.career.AssessmentSubmitRequest;
import v620.cc001.base.common.dto.career.UserProfileSnapshot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/**
 * Application boundary for assessment scoring and profile snapshot integration.
 */
public class AssessmentApplicationService {

    private final AssessmentScoringService scoringService;
    private final AssessmentCatalog catalog;
    private final AssessmentAttemptStorage attemptStorage;
    private final AssessmentResultStorage resultStorage;
    private final CareerProfileApplicationService profileApplicationService;
    private final AssessmentQuestionSelectionService selectionService;
    private final Random random;

    public AssessmentApplicationService() {
        this(new AssessmentScoringService(), CyanCruiseStorageFactory.assessmentCatalog(),
                CyanCruiseStorageFactory.assessmentAttemptStorage(),
                CyanCruiseStorageFactory.assessmentResultStorage(), new CareerProfileApplicationService(),
                new AssessmentQuestionSelectionService(), new Random());
    }

    public AssessmentApplicationService(AssessmentScoringService scoringService,
                                        CareerProfileApplicationService profileApplicationService) {
        this(scoringService, new InMemoryAssessmentCatalog(),
                new InMemoryAssessmentAttemptStorage(), new InMemoryAssessmentResultStorage(),
                profileApplicationService, new AssessmentQuestionSelectionService(), new Random());
    }

    public AssessmentApplicationService(AssessmentScoringService scoringService,
                                        AssessmentCatalog catalog,
                                        AssessmentResultStorage resultStorage,
                                        CareerProfileApplicationService profileApplicationService) {
        this(scoringService, catalog, new InMemoryAssessmentAttemptStorage(), resultStorage,
                profileApplicationService, new AssessmentQuestionSelectionService(), new Random());
    }

    public AssessmentApplicationService(AssessmentScoringService scoringService,
                                        AssessmentCatalog catalog,
                                        AssessmentAttemptStorage attemptStorage,
                                        AssessmentResultStorage resultStorage,
                                        CareerProfileApplicationService profileApplicationService,
                                        AssessmentQuestionSelectionService selectionService,
                                        Random random) {
        this.scoringService = scoringService;
        this.catalog = catalog;
        this.attemptStorage = attemptStorage;
        this.resultStorage = resultStorage;
        this.profileApplicationService = profileApplicationService;
        this.selectionService = selectionService;
        this.random = random;
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

    public AssessmentQuestionDto saveQuestion(Long scaleId, AssessmentQuestionDto question) {
        if (question == null || question.getQuestionText() == null || question.getQuestionText().trim().length() == 0) {
            throw new IllegalArgumentException("assessment question text is required");
        }
        if (question.getDimensionCode() == null || question.getDimensionCode().trim().length() == 0) {
            throw new IllegalArgumentException("assessment question dimension is required");
        }
        if (question.getOptions() == null || question.getOptions().size() < 2) {
            throw new IllegalArgumentException("assessment question requires at least two options");
        }
        getScale(scaleId);
        return catalog.saveQuestion(scaleId, question);
    }

    public AssessmentScaleDto saveAnswerQuestionCount(Long scaleId, Integer answerQuestionCount) {
        getScale(scaleId);
        return catalog.saveAnswerQuestionCount(scaleId, answerQuestionCount);
    }

    public boolean deleteQuestion(Long scaleId, Long questionId) {
        getScale(scaleId);
        return catalog.deleteQuestion(scaleId, questionId);
    }

    public AssessmentScoreResult submit(String userId, AssessmentSubmitRequest request) {
        AssessmentSubmitRequest safeRequest = request == null ? new AssessmentSubmitRequest() : request;
        if (!hasText(safeRequest.getAttemptId())) {
            throw new IllegalArgumentException("测评批次不能为空，请重新开始测评");
        }
        AssessmentAttemptDto attempt = attemptStorage.load(userId, safeRequest.getAttemptId());
        if (attempt == null || attempt.getScale() == null) {
            throw new IllegalArgumentException("测评批次不存在或无权访问，请重新开始测评");
        }
        if ("COMPLETED".equals(attempt.getStatus())) {
            throw new IllegalArgumentException("本次测评已提交，请重新开始测评");
        }
        if (safeRequest.getScaleId() == null || !safeRequest.getScaleId().equals(attempt.getScaleId())) {
            throw new IllegalArgumentException("测评批次与量表不匹配，请重新开始测评");
        }
        validateAttemptAnswers(attempt.getScale(), safeRequest);
        AssessmentScoreResult result = submitAndSaveProfile(userId, attempt.getScale(), safeRequest);
        attemptStorage.complete(userId, attempt.getAttemptId());
        return result;
    }

    public AssessmentScaleDto startAttempt(String userId, Long scaleId) {
        if (!hasText(userId)) {
            throw new IllegalArgumentException("用户身份不能为空");
        }
        AssessmentScaleDto fullScale = getScale(scaleId);
        List<AssessmentQuestionDto> pool = availableQuestions(fullScale.getQuestions());
        int requestedCount = fullScale.getAnswerQuestionCount() == null
                ? pool.size() : fullScale.getAnswerQuestionCount().intValue();
        AssessmentAttemptDto previous = attemptStorage.latest(userId, scaleId);
        Set<Long> previousIds = questionIds(previous == null ? null : previous.getScale());
        List<AssessmentQuestionDto> selected = selectionService.select(pool, requestedCount, previousIds, random);
        if (selected.isEmpty()) {
            throw new IllegalArgumentException("当前测评题库没有可用题目，请联系管理员");
        }

        String attemptId = UUID.randomUUID().toString();
        AssessmentScaleDto attemptScale = copyScale(fullScale, selected);
        attemptScale.setAttemptId(attemptId);
        attemptScale.setQuestionCount(Integer.valueOf(selected.size()));

        AssessmentAttemptDto attempt = new AssessmentAttemptDto();
        attempt.setAttemptId(attemptId);
        attempt.setUserId(userId);
        attempt.setScaleId(scaleId);
        attempt.setStatus("IN_PROGRESS");
        attempt.setScale(attemptScale);
        attempt.setCreatedAt(LocalDateTime.now());
        attemptStorage.save(attempt);
        return attemptScale;
    }

    public AssessmentScoreResult loadResult(String userId, Long recordId) {
        return resultStorage.loadResult(userId, recordId);
    }

    public List<AssessmentScoreResult> listResults(String userId) {
        return resultStorage.listResults(userId);
    }

    public AssessmentScoreResult generateAiInterpretation(String userId, Long recordId) {
        long startedAt = System.currentTimeMillis();
        System.out.println("[CyanCruise AI Entry] operation=assessment-interpretation, recordId=" + recordId);
        AssessmentScoreResult result = resultStorage.loadResult(userId, recordId);
        if (result == null) throw new IllegalArgumentException("测评记录不存在或无权访问");
        try {
            result.setAiInterpretation(defaultAssessmentInterpreter().interpret(result));
            resultStorage.updateResult(userId, result);
            System.out.println("[CyanCruise AI Entry] operation=assessment-interpretation, status=completed"
                    + ", recordId=" + recordId + ", scaleId=" + result.getScaleId()
                    + ", answerCount=" + (result.getAnswers() == null ? 0 : result.getAnswers().size())
                    + ", elapsedMs=" + (System.currentTimeMillis() - startedAt));
            return result;
        } catch (RuntimeException error) {
            System.out.println("[CyanCruise AI Entry] operation=assessment-interpretation, status=failed"
                    + ", recordId=" + recordId + ", scaleId=" + result.getScaleId()
                    + ", errorType=" + error.getClass().getSimpleName()
                    + ", elapsedMs=" + (System.currentTimeMillis() - startedAt));
            throw error;
        }
    }

    private static AgentPlatformAssessmentInterpreter defaultAssessmentInterpreter() {
        AgentPlatformTaskFlowConfig config = assessmentAiConfig();
        if (config.isAgentSdkAvailable()) {
            return new AgentPlatformAssessmentInterpreter(new KingdeeAgentSdkTaskFlowClient(config));
        }
        if (config.isEnabled()) {
            return new AgentPlatformAssessmentInterpreter(new DefaultAgentPlatformTaskFlowClient(config));
        }
        return new AgentPlatformAssessmentInterpreter(null);
    }

    static AgentPlatformTaskFlowConfig assessmentAiConfig() {
        return AgentPlatformTaskFlowConfig.fromSystemProperties("cc001.agent.platform.profile");
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

    private void validateAttemptAnswers(AssessmentScaleDto scale, AssessmentSubmitRequest request) {
        Set<Long> expected = questionIds(scale);
        Map<Long, List<Long>> answers = request.effectiveAnswerOptionIds();
        if (answers == null || answers.size() != expected.size()
                || !expected.equals(new HashSet<Long>(answers.keySet()))) {
            throw new IllegalArgumentException("请完成本次画像补全的全部题目后再提交");
        }
        for (AssessmentQuestionDto question : scale.getQuestions()) {
            List<Long> selectedOptionIds = answers.get(question.getQuestionId());
            if (selectedOptionIds == null || selectedOptionIds.isEmpty()) {
                throw new IllegalArgumentException("请完成本次画像补全的全部题目后再提交");
            }
            if (!isMultiChoice(question) && selectedOptionIds.size() != 1) {
                throw new IllegalArgumentException("答案与本次画像补全题目不匹配，请重新检查");
            }
            Set<Long> validOptionIds = optionIds(question);
            boolean validOption = false;
            for (Long selectedOptionId : selectedOptionIds) {
                if (!validOptionIds.contains(selectedOptionId)) {
                    validOption = false;
                    break;
                }
                validOption = true;
            }
            if (!validOption) {
                throw new IllegalArgumentException("答案与本次画像补全题目不匹配，请重新检查");
            }
        }
    }

    private boolean isMultiChoice(AssessmentQuestionDto question) {
        return question != null && "MULTI".equalsIgnoreCase(question.getQuestionType());
    }

    private Set<Long> optionIds(AssessmentQuestionDto question) {
        Set<Long> ids = new LinkedHashSet<Long>();
        if (question == null || question.getOptions() == null) {
            return ids;
        }
        for (AssessmentOptionDto option : question.getOptions()) {
            if (option != null && option.getOptionId() != null) {
                ids.add(option.getOptionId());
            }
        }
        return ids;
    }

    private Set<Long> questionIds(AssessmentScaleDto scale) {
        Set<Long> ids = new LinkedHashSet<Long>();
        if (scale == null || scale.getQuestions() == null) {
            return ids;
        }
        for (AssessmentQuestionDto question : scale.getQuestions()) {
            if (question != null && question.getQuestionId() != null) {
                ids.add(question.getQuestionId());
            }
        }
        return ids;
    }

    private List<AssessmentQuestionDto> availableQuestions(List<AssessmentQuestionDto> questions) {
        if (questions == null || questions.isEmpty()) {
            return Collections.emptyList();
        }
        List<AssessmentQuestionDto> available = new ArrayList<AssessmentQuestionDto>();
        for (AssessmentQuestionDto question : questions) {
            if (question != null && question.isPublished()) {
                available.add(question);
            }
        }
        return available;
    }

    private AssessmentScaleDto copyScale(AssessmentScaleDto source, List<AssessmentQuestionDto> questions) {
        AssessmentScaleDto copy = new AssessmentScaleDto();
        copy.setScaleId(source.getScaleId());
        copy.setTitle(source.getTitle());
        copy.setDescription(source.getDescription());
        copy.setVersion(source.getVersion());
        copy.setPoolQuestionCount(source.getPoolQuestionCount());
        copy.setAnswerQuestionCount(source.getAnswerQuestionCount());
        copy.setQuestions(new ArrayList<AssessmentQuestionDto>(questions));
        return copy;
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
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
