package v620.base.helper.career;

import v620.cc001.base.common.dto.career.AssessmentQuestionDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/** Selects a balanced question subset while preferring questions not used last time. */
public class AssessmentQuestionSelectionService {

    public List<AssessmentQuestionDto> select(List<AssessmentQuestionDto> pool,
                                              int requestedCount,
                                              Set<Long> previousQuestionIds,
                                              Random random) {
        List<AssessmentQuestionDto> safePool = pool == null
                ? Collections.<AssessmentQuestionDto>emptyList() : pool;
        if (safePool.isEmpty() || requestedCount < 1) {
            return Collections.emptyList();
        }
        int target = Math.min(requestedCount, safePool.size());
        Set<Long> previous = previousQuestionIds == null
                ? Collections.<Long>emptySet() : previousQuestionIds;
        Random source = random == null ? new Random() : random;
        Map<String, CandidateGroup> groups = new LinkedHashMap<String, CandidateGroup>();
        for (AssessmentQuestionDto question : safePool) {
            String dimension = normalizeDimension(question == null ? null : question.getDimensionCode());
            CandidateGroup group = groups.get(dimension);
            if (group == null) {
                group = new CandidateGroup();
                groups.put(dimension, group);
            }
            if (question != null && previous.contains(question.getQuestionId())) {
                group.recent.add(question);
            } else if (question != null) {
                group.fresh.add(question);
            }
        }
        for (CandidateGroup group : groups.values()) {
            Collections.shuffle(group.fresh, source);
            Collections.shuffle(group.recent, source);
        }

        Map<String, Integer> quotas = balancedQuotas(groups, target);
        List<AssessmentQuestionDto> selected = new ArrayList<AssessmentQuestionDto>();
        Set<Long> selectedIds = new LinkedHashSet<Long>();
        for (Map.Entry<String, CandidateGroup> entry : groups.entrySet()) {
            CandidateGroup group = entry.getValue();
            int quota = quotas.get(entry.getKey()).intValue();
            while (quota > 0) {
                AssessmentQuestionDto next = group.next();
                if (next == null) {
                    break;
                }
                if (selectedIds.add(next.getQuestionId())) {
                    selected.add(next);
                    quota -= 1;
                }
            }
        }
        Collections.sort(selected, new Comparator<AssessmentQuestionDto>() {
            public int compare(AssessmentQuestionDto left, AssessmentQuestionDto right) {
                int a = left.getSortOrder() == null ? 0 : left.getSortOrder().intValue();
                int b = right.getSortOrder() == null ? 0 : right.getSortOrder().intValue();
                return a < b ? -1 : (a == b ? 0 : 1);
            }
        });
        return selected;
    }

    private Map<String, Integer> balancedQuotas(Map<String, CandidateGroup> groups, int target) {
        Map<String, Integer> quotas = new LinkedHashMap<String, Integer>();
        for (String key : groups.keySet()) {
            quotas.put(key, Integer.valueOf(0));
        }
        int assigned = 0;
        boolean progressed = true;
        while (assigned < target && progressed) {
            progressed = false;
            for (Map.Entry<String, CandidateGroup> entry : groups.entrySet()) {
                int current = quotas.get(entry.getKey()).intValue();
                if (current < entry.getValue().size()) {
                    quotas.put(entry.getKey(), Integer.valueOf(current + 1));
                    assigned += 1;
                    progressed = true;
                    if (assigned >= target) {
                        break;
                    }
                }
            }
        }
        return quotas;
    }

    private String normalizeDimension(String value) {
        return value == null || value.trim().length() == 0 ? "GENERAL" : value.trim().toUpperCase();
    }

    private static final class CandidateGroup {
        private final List<AssessmentQuestionDto> fresh = new ArrayList<AssessmentQuestionDto>();
        private final List<AssessmentQuestionDto> recent = new ArrayList<AssessmentQuestionDto>();
        private int freshIndex;
        private int recentIndex;

        private int size() {
            return fresh.size() + recent.size();
        }

        private AssessmentQuestionDto next() {
            if (freshIndex < fresh.size()) {
                return fresh.get(freshIndex++);
            }
            if (recentIndex < recent.size()) {
                return recent.get(recentIndex++);
            }
            return null;
        }
    }
}
