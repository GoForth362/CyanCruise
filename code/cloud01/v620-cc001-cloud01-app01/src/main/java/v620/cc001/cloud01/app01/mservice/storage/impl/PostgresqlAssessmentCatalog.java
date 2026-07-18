package v620.cc001.cloud01.app01.mservice.storage.impl;

import v620.cc001.base.common.dto.career.AssessmentOptionDto;
import v620.cc001.base.common.dto.career.AssessmentQuestionDto;
import v620.cc001.base.common.dto.career.AssessmentScaleDto;
import v620.cc001.cloud01.app01.mservice.storage.AssessmentCatalog;
import v620.cc001.cloud01.app01.mservice.storage.PostgresqlStorageConfig;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** PostgreSQL-backed assessment scale, question and option catalog. */
public class PostgresqlAssessmentCatalog extends PostgresqlStorageSupport implements AssessmentCatalog {

    public PostgresqlAssessmentCatalog(PostgresqlStorageConfig config) {
        super(config, "assessment catalog");
        if (config.isInitialize()) {
            initialize();
        }
        seedIfEmpty();
        repairLegacySeedQuestionPublication();
    }

    public List<AssessmentScaleDto> listScales() {
        String sql = "SELECT payload_json FROM " + table("cc_assessment_scale") + " ORDER BY scale_id";
        List<AssessmentScaleDto> out = new ArrayList<AssessmentScaleDto>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                AssessmentScaleDto scale = readJson(resultSet.getString(1), AssessmentScaleDto.class, null);
                if (scale != null) {
                    applyCounts(scale);
                    scale.setQuestions(new ArrayList<AssessmentQuestionDto>());
                    out.add(scale);
                }
            }
            return out;
        } catch (SQLException e) {
            throw storageException("list assessment catalog", e);
        } finally {
            close(resultSet); close(statement); close(connection);
        }
    }

    public AssessmentScaleDto loadScale(Long scaleId) {
        String sql = "SELECT payload_json FROM " + table("cc_assessment_scale") + " WHERE scale_id = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setLong(1, scaleId == null ? -1L : scaleId.longValue());
            resultSet = statement.executeQuery();
            AssessmentScaleDto scale = resultSet.next()
                    ? readJson(resultSet.getString(1), AssessmentScaleDto.class, null) : null;
            if (scale != null) applyCounts(scale);
            return scale;
        } catch (SQLException e) {
            throw storageException("load assessment scale", e);
        } finally {
            close(resultSet); close(statement); close(connection);
        }
    }

    public synchronized AssessmentQuestionDto saveQuestion(Long scaleId, AssessmentQuestionDto question) {
        AssessmentScaleDto scale = requireScale(scaleId);
        List<AssessmentQuestionDto> questions = scale.getQuestions() == null
                ? new ArrayList<AssessmentQuestionDto>() : new ArrayList<AssessmentQuestionDto>(scale.getQuestions());
        AssessmentQuestionDto saved = question;
        saved.setScaleId(scaleId);
        if (saved.getQuestionId() == null) saved.setQuestionId(Long.valueOf(nextQuestionId(scaleId, questions)));
        if (saved.getSortOrder() == null) saved.setSortOrder(Integer.valueOf(nextSortOrder(questions)));
        if (!hasText(saved.getDimensionCode())) throw new IllegalArgumentException("题目维度不能为空");
        if (!hasText(saved.getQuestionType())) saved.setQuestionType("SINGLE");
        normalizeOptions(saved);
        boolean replaced = false;
        for (int i = 0; i < questions.size(); i += 1) {
            if (saved.getQuestionId().equals(questions.get(i).getQuestionId())) {
                questions.set(i, saved);
                replaced = true;
                break;
            }
        }
        if (!replaced) questions.add(saved);
        scale.setQuestions(questions);
        applyCounts(scale);
        saveScale(scale);
        return saved;
    }

    public synchronized boolean deleteQuestion(Long scaleId, Long questionId) {
        AssessmentScaleDto scale = requireScale(scaleId);
        List<AssessmentQuestionDto> kept = new ArrayList<AssessmentQuestionDto>();
        boolean deleted = false;
        for (AssessmentQuestionDto question : scale.getQuestions()) {
            if (questionId != null && questionId.equals(question.getQuestionId())) deleted = true;
            else kept.add(question);
        }
        if (deleted) {
            scale.setQuestions(kept);
            applyCounts(scale);
            saveScale(scale);
        }
        return deleted;
    }

    public synchronized AssessmentScaleDto saveAnswerQuestionCount(Long scaleId, Integer answerQuestionCount) {
        AssessmentScaleDto scale = requireScale(scaleId);
        int pool = scale.getQuestions() == null ? 0 : scale.getQuestions().size();
        if (answerQuestionCount == null || answerQuestionCount.intValue() < 1
                || answerQuestionCount.intValue() > pool) {
            throw new IllegalArgumentException("作答题数必须在 1 到题库总数之间");
        }
        scale.setAnswerQuestionCount(answerQuestionCount);
        applyCounts(scale);
        saveScale(scale);
        scale.setQuestions(new ArrayList<AssessmentQuestionDto>());
        return scale;
    }

    private AssessmentScaleDto requireScale(Long scaleId) {
        AssessmentScaleDto scale = loadScale(scaleId);
        if (scale == null) throw new IllegalArgumentException("测评量表不存在");
        return scale;
    }

    private void saveScale(AssessmentScaleDto scale) {
        String sql = "INSERT INTO " + table("cc_assessment_scale")
                + " (scale_id, payload_json, updated_at) VALUES (?, ?::jsonb, ?)"
                + " ON CONFLICT (scale_id) DO UPDATE SET payload_json = EXCLUDED.payload_json, updated_at = EXCLUDED.updated_at";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setLong(1, scale.getScaleId().longValue());
            statement.setString(2, toJson(scale));
            statement.setTimestamp(3, timestamp(LocalDateTime.now()));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw storageException("save assessment scale", e);
        } finally {
            close(statement); close(connection);
        }
    }

    private void applyCounts(AssessmentScaleDto scale) {
        int pool = scale.getQuestions() == null ? 0 : scale.getQuestions().size();
        int answer = scale.getAnswerQuestionCount() == null ? pool
                : Math.min(scale.getAnswerQuestionCount().intValue(), pool);
        if (pool > 0 && answer < 1) answer = pool;
        scale.setPoolQuestionCount(Integer.valueOf(pool));
        scale.setAnswerQuestionCount(Integer.valueOf(answer));
        scale.setQuestionCount(Integer.valueOf(answer));
    }

    private void normalizeOptions(AssessmentQuestionDto question) {
        List<AssessmentOptionDto> options = question.getOptions() == null
                ? new ArrayList<AssessmentOptionDto>() : question.getOptions();
        if (options.size() < 2) throw new IllegalArgumentException("测评题至少需要两个选项");
        long base = question.getQuestionId().longValue() * 10L;
        for (int i = 0; i < options.size(); i += 1) {
            AssessmentOptionDto option = options.get(i);
            if (option.getOptionId() == null) option.setOptionId(Long.valueOf(base + i + 1L));
            option.setQuestionId(question.getQuestionId());
            if (option.getSortOrder() == null) option.setSortOrder(Integer.valueOf(i));
            if (option.getScoreValue() == null) option.setScoreValue(BigDecimal.ONE);
        }
    }

    private long nextQuestionId(Long scaleId, List<AssessmentQuestionDto> questions) {
        long max = scaleId.longValue() * 100L;
        for (AssessmentQuestionDto question : questions) {
            if (question.getQuestionId() != null) max = Math.max(max, question.getQuestionId().longValue());
        }
        return max + 1L;
    }

    private int nextSortOrder(List<AssessmentQuestionDto> questions) {
        int max = 0;
        for (AssessmentQuestionDto question : questions) {
            if (question.getSortOrder() != null) max = Math.max(max, question.getSortOrder().intValue());
        }
        return max + 1;
    }

    private boolean hasText(String value) { return value != null && value.trim().length() > 0; }

    private void initialize() {
        String sql = "CREATE TABLE IF NOT EXISTS " + table("cc_assessment_scale") + " ("
                + "scale_id BIGINT PRIMARY KEY, payload_json JSONB NOT NULL, updated_at TIMESTAMP NOT NULL DEFAULT now())";
        Connection connection = null;
        Statement statement = null;
        try {
            connection = connection();
            statement = connection.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            throw storageException("initialize assessment catalog", e);
        } finally {
            close(statement); close(connection);
        }
    }

    private void seedIfEmpty() {
        if (!listScales().isEmpty()) return;
        InMemoryAssessmentCatalog seed = new InMemoryAssessmentCatalog();
        for (AssessmentScaleDto summary : seed.listScales()) {
            AssessmentScaleDto scale = seed.loadScale(summary.getScaleId());
            saveScale(scale);
        }
    }

    /** Restores publication for pre-installed questions created before draft-by-default was introduced. */
    private void repairLegacySeedQuestionPublication() {
        String migrationKey = "20260717_publish_builtin_assessment_questions";
        if (migrationApplied(migrationKey)) return;
        InMemoryAssessmentCatalog seed = new InMemoryAssessmentCatalog();
        for (AssessmentScaleDto summary : seed.listScales()) {
            AssessmentScaleDto stored = loadScale(summary.getScaleId());
            AssessmentScaleDto builtIn = seed.loadScale(summary.getScaleId());
            if (stored != null && builtIn != null && restoreBuiltInQuestionPublication(stored, builtIn)) {
                applyCounts(stored);
                saveScale(stored);
            }
        }
        markMigrationApplied(migrationKey);
    }

    static boolean restoreBuiltInQuestionPublication(AssessmentScaleDto stored, AssessmentScaleDto builtIn) {
        if (stored == null || builtIn == null || stored.getQuestions() == null) return false;
        Set<Long> builtInQuestionIds = new HashSet<Long>();
        for (AssessmentQuestionDto question : builtIn.getQuestions()) {
            if (question != null && question.getQuestionId() != null) {
                builtInQuestionIds.add(question.getQuestionId());
            }
        }
        boolean changed = false;
        for (AssessmentQuestionDto question : stored.getQuestions()) {
            if (question != null && builtInQuestionIds.contains(question.getQuestionId()) && !question.isPublished()) {
                question.setPublished(true);
                changed = true;
            }
        }
        return changed;
    }

    private boolean migrationApplied(String migrationKey) {
        ensureMigrationTable();
        String sql = "SELECT 1 FROM " + table("cc_assessment_catalog_migration") + " WHERE migration_key = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, migrationKey);
            resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            throw storageException("read assessment catalog migration", e);
        } finally {
            close(resultSet); close(statement); close(connection);
        }
    }

    private void markMigrationApplied(String migrationKey) {
        String sql = "INSERT INTO " + table("cc_assessment_catalog_migration")
                + " (migration_key, applied_at) VALUES (?, ?) ON CONFLICT (migration_key) DO NOTHING";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, migrationKey);
            statement.setTimestamp(2, timestamp(LocalDateTime.now()));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw storageException("mark assessment catalog migration", e);
        } finally {
            close(statement); close(connection);
        }
    }

    private void ensureMigrationTable() {
        String sql = "CREATE TABLE IF NOT EXISTS " + table("cc_assessment_catalog_migration")
                + " (migration_key VARCHAR(128) PRIMARY KEY, applied_at TIMESTAMP NOT NULL)";
        Connection connection = null;
        Statement statement = null;
        try {
            connection = connection();
            statement = connection.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            throw storageException("initialize assessment catalog migration", e);
        } finally {
            close(statement); close(connection);
        }
    }
}
