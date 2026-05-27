package v620.cc001.cloud01.app01.mservice;

import v620.cc001.base.common.dto.career.SubscriptionQuotaDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemorySubscriptionQuotaStorage implements SubscriptionQuotaStorage {

    private final Map<String, SubscriptionQuotaDto> quotas = new ConcurrentHashMap<String, SubscriptionQuotaDto>();

    public SubscriptionQuotaDto find(String userId, String templateId) {
        return quotas.get(key(userId, templateId));
    }

    public SubscriptionQuotaDto addQuota(String userId, String templateId, int delta) {
        String key = key(userId, templateId);
        SubscriptionQuotaDto quota = quotas.get(key);
        if (quota == null) {
            quota = new SubscriptionQuotaDto();
            quota.setUserId(userId);
            quota.setTemplateId(templateId);
            quota.setRemaining(Integer.valueOf(0));
        }
        int next = Math.max(0, quota.getRemaining().intValue() + delta);
        quota.setRemaining(Integer.valueOf(next));
        quota.setUpdatedAt(LocalDateTime.now());
        quotas.put(key, quota);
        return quota;
    }

    public SubscriptionQuotaDto consumeOne(String userId, String templateId) {
        SubscriptionQuotaDto quota = find(userId, templateId);
        if (quota == null || quota.getRemaining() == null || quota.getRemaining().intValue() <= 0) {
            return quota;
        }
        return addQuota(userId, templateId, -1);
    }

    public List<SubscriptionQuotaDto> listByUser(String userId) {
        List<SubscriptionQuotaDto> out = new ArrayList<SubscriptionQuotaDto>();
        for (SubscriptionQuotaDto quota : quotas.values()) {
            if (userId != null && userId.equals(quota.getUserId())) {
                out.add(quota);
            }
        }
        return out;
    }

    private String key(String userId, String templateId) {
        return (userId == null ? "" : userId) + "::" + (templateId == null ? "" : templateId);
    }
}
