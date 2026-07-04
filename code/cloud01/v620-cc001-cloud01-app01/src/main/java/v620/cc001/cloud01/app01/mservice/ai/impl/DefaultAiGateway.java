package v620.cc001.cloud01.app01.mservice.ai.impl;

import v620.cc001.cloud01.app01.mservice.ai.*;
import v620.base.helper.ai.AiMessageHelper;
import v620.cc001.base.common.dto.ai.AiChatRequestDto;
import v620.cc001.base.common.dto.ai.AiChatResponseDto;
import v620.cc001.base.common.dto.ai.AiConstants;
import v620.cc001.base.common.dto.ai.AiStreamEventDto;

import java.util.Collections;
import java.util.List;

public class DefaultAiGateway implements AiGateway {

    private final AiProviderAdapter provider;
    private final AiMessageHelper messageHelper;
    private final int defaultTimeoutSeconds;

    public DefaultAiGateway(AiProviderAdapter provider) {
        this(provider, new AiMessageHelper(), 60);
    }

    public DefaultAiGateway(AiProviderAdapter provider, AiMessageHelper messageHelper, int defaultTimeoutSeconds) {
        this.provider = provider;
        this.messageHelper = messageHelper;
        this.defaultTimeoutSeconds = defaultTimeoutSeconds <= 0 ? 60 : defaultTimeoutSeconds;
    }

    public AiChatResponseDto chat(AiChatRequestDto request) {
        AiChatRequestDto safe = normalize(request);
        if (provider == null || !provider.isAvailable()) {
            return AiChatResponseDto.unavailable("AI provider is not configured");
        }
        long started = System.currentTimeMillis();
        AiChatResponseDto response = provider.chat(safe);
        if (response == null) {
            return AiChatResponseDto.unavailable("AI provider returned empty response");
        }
        if (response.getModelName() == null) {
            response.setModelName(safe.getModelName());
        }
        if (response.getFinishReason() == null) {
            response.setFinishReason(response.getErrorCode() == null ? AiConstants.FINISH_STOP : AiConstants.FINISH_ERROR);
        }
        // Minimal audit hook for providers that do not expose timing yet.
        if (System.currentTimeMillis() - started > (long) safe.getTimeoutSeconds().intValue() * 1000L) {
            response.setErrorCode(AiConstants.ERROR_TIMEOUT);
            response.setFinishReason(AiConstants.FINISH_ERROR);
        }
        return response;
    }

    public List<AiStreamEventDto> stream(AiChatRequestDto request) {
        AiChatRequestDto safe = normalize(request);
        if (provider == null || !provider.isAvailable()) {
            return Collections.singletonList(AiStreamEventDto.error(AiConstants.ERROR_UNAVAILABLE,
                    "AI provider is not configured", 0));
        }
        List<AiStreamEventDto> events = provider.stream(safe);
        return events == null ? Collections.singletonList(AiStreamEventDto.done(0)) : events;
    }

    private AiChatRequestDto normalize(AiChatRequestDto request) {
        AiChatRequestDto safe = request == null ? new AiChatRequestDto() : request;
        if (safe.getTimeoutSeconds() == null || safe.getTimeoutSeconds().intValue() <= 0) {
            safe.setTimeoutSeconds(Integer.valueOf(defaultTimeoutSeconds));
        }
        if (safe.getModelName() == null || safe.getModelName().trim().length() == 0) {
            safe.setModelName(AiConstants.DEFAULT_MODEL_NAME);
        }
        safe.setMessages(messageHelper.withDefaultSystemPrompt(safe.getMessages()));
        return safe;
    }
}
