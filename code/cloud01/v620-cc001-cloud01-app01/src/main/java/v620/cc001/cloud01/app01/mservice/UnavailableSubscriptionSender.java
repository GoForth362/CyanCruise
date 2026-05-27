package v620.cc001.cloud01.app01.mservice;

import v620.cc001.base.common.dto.career.NotificationConstants;
import v620.cc001.base.common.dto.career.SubscriptionSendRequest;
import v620.cc001.base.common.dto.career.SubscriptionSendResult;

/**
 * Placeholder sender until Cosmic/WeChat provider configuration is approved.
 */
public class UnavailableSubscriptionSender implements SubscriptionSender {

    public boolean isAvailable() {
        return false;
    }

    public SubscriptionSendResult send(SubscriptionSendRequest request) {
        SubscriptionSendResult result = new SubscriptionSendResult();
        result.setStatus(NotificationConstants.RESULT_UNAVAILABLE);
        result.setReason("PROVIDER_UNAVAILABLE");
        result.setUserId(request == null ? null : request.getUserId());
        result.setTemplateId(request == null ? null : request.getTemplateId());
        return result;
    }
}
