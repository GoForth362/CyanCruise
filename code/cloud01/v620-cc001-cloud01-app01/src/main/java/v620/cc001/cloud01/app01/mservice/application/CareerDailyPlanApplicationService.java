package v620.cc001.cloud01.app01.mservice.application;

import v620.cc001.base.common.dto.career.CareerDailyPlanDto;
import v620.cc001.base.common.dto.career.CareerDailyTaskDto;
import v620.cc001.base.common.dto.career.CareerDailyTaskUpdateRequest;
import v620.cc001.base.common.dto.career.CareerPlanPhaseDto;
import v620.cc001.base.common.dto.career.CareerPlanRecordDto;
import v620.cc001.base.common.dto.career.CareerPlanSubStageDto;
import v620.cc001.base.common.dto.career.CareerPlanWeeklyPlanDto;
import v620.cc001.base.common.dto.career.CareerRouteContext;
import v620.cc001.cloud01.app01.mservice.storage.CareerDailyTaskStorage;
import v620.cc001.cloud01.app01.mservice.storage.CareerPlanStorage;
import v620.cc001.cloud01.app01.mservice.storage.CyanCruiseStorageFactory;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Creates a stable, date-aware execution slice from the AI career plan.
 */
public class CareerDailyPlanApplicationService {

    static final int DAILY_LIMIT = 5;

    private final CareerPlanStorage planStorage;
    private final CareerDailyTaskStorage taskStorage;
    private final Clock clock;
    private final String routeType;

    public CareerDailyPlanApplicationService() {
        this(CyanCruiseStorageFactory.careerPlanStorage(),
                CyanCruiseStorageFactory.careerDailyTaskStorage(), Clock.systemDefaultZone(), CareerRouteContext.EMPLOYMENT);
    }

    public CareerDailyPlanApplicationService(CareerPlanStorage planStorage,
                                             CareerDailyTaskStorage taskStorage,
                                             Clock clock) {
        this(planStorage, taskStorage, clock, CareerRouteContext.EMPLOYMENT);
    }

    public CareerDailyPlanApplicationService(CareerPlanStorage planStorage,
                                             CareerDailyTaskStorage taskStorage,
                                             Clock clock, String routeType) {
        this.planStorage = planStorage;
        this.taskStorage = taskStorage;
        this.clock = clock;
        this.routeType = CareerRouteContext.STUDY.equals(routeType) ? CareerRouteContext.STUDY : CareerRouteContext.EMPLOYMENT;
    }

    public CareerDailyPlanDto getToday(String userId) {
        String safeUserId = requireText(userId, "userId");
        CareerPlanRecordDto plan = planStorage.load(safeUserId);
        if (plan == null || plan.getPhases() == null || plan.getPhases().isEmpty()) {
            return empty(LocalDate.now(clock), "请先生成职业路线图，再开始每日计划。", null);
        }
        int version = plan.getVersion() == null ? 1 : plan.getVersion().intValue();
        LocalDate today = LocalDate.now(clock);
        List<CareerDailyTaskDto> history = taskStorage.list(safeUserId);
        List<CareerDailyTaskDto> existingToday = tasksForDate(history, today, version);
        if (!existingToday.isEmpty()) {
            return response(plan, existingToday, completedSources(history, version), today);
        }

        Set<String> completed = completedSources(history, version);
        PhaseSources active = activePhaseSources(plan, completed);
        if (active == null) {
            return empty(today, "路线图中的阶段任务已经全部完成。", Integer.valueOf(version));
        }

        LinkedHashMap<String, SourceTask> selected = new LinkedHashMap<String, SourceTask>();
        for (CareerDailyTaskDto old : history) {
            if (sameVersion(old, version) && old.getPlanDate() != null && old.getPlanDate().isBefore(today)
                    && !old.isCompleted() && !completed.contains(old.getSourceTaskId())) {
                SourceTask source = active.byId.get(old.getSourceTaskId());
                if (source != null) selected.put(source.id, source.withCarryOver());
            }
        }
        for (SourceTask source : active.ordered) {
            if (selected.size() >= DAILY_LIMIT) break;
            if (!completed.contains(source.id) && !selected.containsKey(source.id)) selected.put(source.id, source);
        }

        List<CareerDailyTaskDto> created = new ArrayList<CareerDailyTaskDto>();
        int sequence = 0;
        for (SourceTask source : selected.values()) {
            CareerDailyTaskDto task = new CareerDailyTaskDto();
            task.setRouteType(routeType);
            task.setTaskId("daily-v" + version + "-" + today + "-" + sequence + "-" + safeHash(source.id));
            task.setSourceTaskId(source.id);
            task.setPhaseId(active.phaseId);
            task.setText(cleanDailyText(source.text));
            task.setPlanDate(today);
            task.setStatus("PENDING");
            task.setSequence(Integer.valueOf(sequence));
            task.setPlanVersion(Integer.valueOf(version));
            task.setCarriedOver(Boolean.valueOf(source.carriedOver));
            task.setUpdatedAt(LocalDateTime.now(clock));
            taskStorage.save(safeUserId, task);
            created.add(task);
            sequence += 1;
        }
        return response(plan, created, completed, today);
    }

    public CareerDailyPlanDto update(String userId, CareerDailyTaskUpdateRequest request) {
        String safeUserId = requireText(userId, "userId");
        String taskId = requireText(request == null ? null : request.getTaskId(), "taskId");
        CareerDailyTaskDto task = taskStorage.find(safeUserId, taskId);
        if (task == null) throw new IllegalArgumentException("未找到需要更新的每日任务。");
        boolean completed = request != null && Boolean.TRUE.equals(request.getCompleted());
        task.setStatus(completed ? "COMPLETED" : "PENDING");
        task.setCompletedAt(completed ? LocalDateTime.now(clock) : null);
        task.setUpdatedAt(LocalDateTime.now(clock));
        taskStorage.save(safeUserId, task);

        CareerPlanRecordDto plan = planStorage.load(safeUserId);
        List<CareerDailyTaskDto> history = taskStorage.list(safeUserId);
        int version = task.getPlanVersion() == null ? 1 : task.getPlanVersion().intValue();
        Set<String> completedSources = completedSources(history, version);
        if (plan != null && (plan.getVersion() == null || plan.getVersion().intValue() == version)) {
            recomputePhaseStatus(plan, completedSources);
            plan.setLastUpdatedAt(LocalDateTime.now(clock));
            planStorage.save(safeUserId, plan);
        }
        LocalDate date = task.getPlanDate() == null ? LocalDate.now(clock) : task.getPlanDate();
        return response(plan, tasksForDate(history, date, version), completedSources, date);
    }

    private CareerDailyPlanDto response(CareerPlanRecordDto plan, List<CareerDailyTaskDto> tasks,
                                        Set<String> completedSources, LocalDate date) {
        CareerDailyPlanDto result = new CareerDailyPlanDto();
        result.setRouteType(routeType);
        result.setStudyDirection(plan == null ? null : plan.getStudyDirection());
        result.setPlanDate(date);
        result.setPlanVersion(plan == null ? null : plan.getVersion());
        result.setItems(tasks);
        result.setCompletedSourceTaskIds(new ArrayList<String>(completedSources));
        int completedCount = 0;
        for (CareerDailyTaskDto task : tasks) if (task.isCompleted()) completedCount += 1;
        result.setCompletedCount(Integer.valueOf(completedCount));
        result.setTotalCount(Integer.valueOf(tasks.size()));
        result.setAllCompleted(Boolean.valueOf(!tasks.isEmpty() && completedCount == tasks.size()));
        String phaseId = tasks.isEmpty() ? null : tasks.get(0).getPhaseId();
        CareerPlanPhaseDto phase = findPhase(plan, phaseId);
        result.setPhaseId(phaseId);
        result.setPhaseTitle(phase == null ? null : phase.getTitle());
        if (tasks.isEmpty()) {
            result.setSummary("路线图中的阶段任务已经全部完成。");
        } else if (completedCount == tasks.size()) {
            result.setSummary("今天的任务已全部完成。明天会沿着当前路线继续安排下一步。");
        } else {
            result.setSummary("今天按顺序推进“" + firstText(phase == null ? null : phase.getTitle(), "当前阶段")
                    + "”；未完成的任务会在下一天优先顺延。");
        }
        return result;
    }

    private CareerDailyPlanDto empty(LocalDate date, String summary, Integer version) {
        CareerDailyPlanDto result = new CareerDailyPlanDto();
        result.setRouteType(routeType);
        result.setPlanDate(date);
        result.setPlanVersion(version);
        result.setSummary(summary);
        result.setCompletedCount(Integer.valueOf(0));
        result.setTotalCount(Integer.valueOf(0));
        result.setAllCompleted(Boolean.FALSE);
        return result;
    }

    private PhaseSources activePhaseSources(CareerPlanRecordDto plan, Set<String> completed) {
        List<CareerPlanPhaseDto> phases = plan.getPhases();
        for (int i = 0; i < phases.size(); i += 1) {
            CareerPlanPhaseDto phase = phases.get(i);
            String phaseId = phaseKey(phase, i);
            List<SourceTask> ordered = phaseSources(phase, i);
            boolean allDone = !ordered.isEmpty();
            for (SourceTask source : ordered) if (!completed.contains(source.id)) allDone = false;
            if (!allDone) return new PhaseSources(phaseId, ordered);
        }
        return null;
    }

    private List<SourceTask> phaseSources(CareerPlanPhaseDto phase, int phaseIndex) {
        List<SourceTask> result = new ArrayList<SourceTask>();
        String phaseId = phaseKey(phase, phaseIndex);
        addSources(result, phase == null ? null : phase.getActions(), phaseId + ".actions");
        List<CareerPlanSubStageDto> subStages = phase == null ? null : phase.getSubStages();
        if (subStages != null) {
            for (int i = 0; i < subStages.size(); i += 1) {
                CareerPlanSubStageDto sub = subStages.get(i);
                addSources(result, sub == null ? null : sub.getActions(), phaseId + ".substage." + i + ".actions");
            }
        }
        addSources(result, phase == null ? null : phase.getKpis(), phaseId + ".kpis");
        return result;
    }

    private void addSources(List<SourceTask> result, List<String> values, String scope) {
        if (values == null) return;
        for (int i = 0; i < values.size(); i += 1) {
            String value = values.get(i);
            if (hasText(value)) result.add(new SourceTask(scope + "." + i, value, false));
        }
    }

    private void recomputePhaseStatus(CareerPlanRecordDto plan, Set<String> completed) {
        if (!CareerRouteContext.STUDY.equals(routeType)) {
            recomputeSequentialPhaseStatus(plan, completed);
            return;
        }
        List<CareerPlanPhaseDto> phases = plan.getPhases();
        for (int i = 0; i < phases.size(); i += 1) {
            CareerPlanPhaseDto phase = phases.get(i);
            List<SourceTask> sources = phaseSources(phase, i);
            int completedCount = 0;
            for (SourceTask source : sources) {
                if (completed.contains(source.id)) completedCount += 1;
            }
            if (!sources.isEmpty() && completedCount == sources.size()) {
                phase.setStatus("COMPLETED");
            } else if (completedCount > 0) {
                phase.setStatus("IN_PROGRESS");
            } else {
                phase.setStatus("NOT_STARTED");
            }
        }
    }

    private void recomputeSequentialPhaseStatus(CareerPlanRecordDto plan, Set<String> completed) {
        boolean previousComplete = true;
        List<CareerPlanPhaseDto> phases = plan.getPhases();
        for (int i = 0; i < phases.size(); i += 1) {
            CareerPlanPhaseDto phase = phases.get(i);
            List<SourceTask> sources = phaseSources(phase, i);
            boolean complete = !sources.isEmpty();
            for (SourceTask source : sources) if (!completed.contains(source.id)) complete = false;
            if (complete) {
                phase.setStatus("COMPLETED");
            } else if (previousComplete) {
                phase.setStatus("IN_PROGRESS");
            } else {
                phase.setStatus("NOT_STARTED");
            }
            previousComplete = previousComplete && complete;
        }
    }

    private Set<String> completedSources(List<CareerDailyTaskDto> tasks, int version) {
        Set<String> result = new HashSet<String>();
        for (CareerDailyTaskDto task : tasks) {
            if (sameVersion(task, version) && task.isCompleted() && hasText(task.getSourceTaskId())) {
                result.add(task.getSourceTaskId());
            }
        }
        return result;
    }

    private List<CareerDailyTaskDto> tasksForDate(List<CareerDailyTaskDto> tasks, LocalDate date, int version) {
        List<CareerDailyTaskDto> result = new ArrayList<CareerDailyTaskDto>();
        for (CareerDailyTaskDto task : tasks) {
            if (sameVersion(task, version) && date.equals(task.getPlanDate())) result.add(task);
        }
        return result;
    }

    private boolean sameVersion(CareerDailyTaskDto task, int version) {
        return task != null && task.getPlanVersion() != null && task.getPlanVersion().intValue() == version;
    }

    private CareerPlanPhaseDto findPhase(CareerPlanRecordDto plan, String phaseId) {
        if (plan == null || plan.getPhases() == null || phaseId == null) return null;
        for (int i = 0; i < plan.getPhases().size(); i += 1) {
            CareerPlanPhaseDto phase = plan.getPhases().get(i);
            if (phaseId.equals(phaseKey(phase, i))) return phase;
        }
        return null;
    }

    private String phaseKey(CareerPlanPhaseDto phase, int index) {
        return hasText(phase == null ? null : phase.getPhaseId())
                ? phase.getPhaseId().trim()
                : "phase-" + index;
    }

    private String cleanDailyText(String text) {
        return text == null ? "" : text.trim().replaceFirst("^第\\s*[0-9一二三四五六七八九十]+\\s*天[：:]\\s*", "");
    }

    private String sanitize(String value) {
        String safe = value == null ? "" : value.trim().toLowerCase()
                .replaceAll("[^a-z0-9\\u4e00-\\u9fa5]+", "-")
                .replaceAll("^-+|-+$", "");
        return safe.length() == 0 ? "item" : safe;
    }

    private String safeHash(String value) { return Integer.toHexString(value == null ? 0 : value.hashCode()); }
    private String requireText(String value, String field) {
        if (!hasText(value)) throw new IllegalArgumentException(field + " is required");
        return value.trim();
    }
    private boolean hasText(String value) { return value != null && value.trim().length() > 0; }
    private String firstText(String value, String fallback) { return hasText(value) ? value.trim() : fallback; }

    private static final class SourceTask {
        private final String id;
        private final String text;
        private final boolean carriedOver;
        private SourceTask(String id, String text, boolean carriedOver) {
            this.id = id;
            this.text = text;
            this.carriedOver = carriedOver;
        }
        private SourceTask withCarryOver() { return new SourceTask(id, text, true); }
    }

    private static final class PhaseSources {
        private final String phaseId;
        private final List<SourceTask> ordered;
        private final Map<String, SourceTask> byId = new LinkedHashMap<String, SourceTask>();
        private PhaseSources(String phaseId, List<SourceTask> ordered) {
            this.phaseId = phaseId;
            this.ordered = ordered;
            for (SourceTask task : ordered) byId.put(task.id, task);
        }
    }
}
