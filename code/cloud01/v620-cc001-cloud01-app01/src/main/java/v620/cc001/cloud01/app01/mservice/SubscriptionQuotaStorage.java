package v620.cc001.cloud01.app01.mservice;

import v620.cc001.base.common.dto.career.SubscriptionQuotaDto;

import java.util.List;

public interface SubscriptionQuotaStorage {

    SubscriptionQuotaDto find(String userId, String templateId);

    SubscriptionQuotaDto addQuota(String userId, String templateId, int delta);

    SubscriptionQuotaDto consumeOne(String userId, String templateId);

    List<SubscriptionQuotaDto> listByUser(String userId);
}
