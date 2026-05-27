package v620.cc001.cloud01.app01.mservice;

import v620.cc001.base.common.dto.career.SubscriptionSendRequest;
import v620.cc001.base.common.dto.career.SubscriptionSendResult;

public interface SubscriptionSender {

    boolean isAvailable();

    SubscriptionSendResult send(SubscriptionSendRequest request);
}
