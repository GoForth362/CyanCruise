package v620.cc001.cloud01.app01.mservice.ai;

import v620.cc001.base.common.dto.ai.AiChatRequestDto;
import v620.cc001.base.common.dto.ai.AiChatResponseDto;
import v620.cc001.base.common.dto.ai.AiConstants;
import v620.cc001.base.common.dto.ai.AiStreamEventDto;

import java.util.Collections;
import java.util.List;

public class UnavailableAiProviderAdapter implements AiProviderAdapter {

    public boolean isAvailable() {
        return false;
    }

    public AiChatResponseDto chat(AiChatRequestDto request) {
        return AiChatResponseDto.unavailable("AI provider is not configured");
    }

    public List<AiStreamEventDto> stream(AiChatRequestDto request) {
        return Collections.singletonList(AiStreamEventDto.error(AiConstants.ERROR_UNAVAILABLE,
                "AI provider is not configured", 0));
    }
}
