package v620.cc001.cloud01.app01.mservice.ai;

import v620.cc001.base.common.dto.ai.AiChatRequestDto;
import v620.cc001.base.common.dto.ai.AiChatResponseDto;
import v620.cc001.base.common.dto.ai.AiConstants;
import v620.cc001.base.common.dto.ai.AiStreamEventDto;

import java.util.Collections;
import java.util.List;

/**
 * Placeholder for DashScope-compatible or Cosmic platform AI providers.
 * The real HTTP/platform binding can replace this class without changing business services.
 */
public class CompatibleEndpointAiProviderAdapter implements AiProviderAdapter {

    private final String endpoint;
    private final String apiKey;

    public CompatibleEndpointAiProviderAdapter(String endpoint, String apiKey) {
        this.endpoint = endpoint;
        this.apiKey = apiKey;
    }

    public boolean isAvailable() {
        return hasText(endpoint) && hasText(apiKey);
    }

    public AiChatResponseDto chat(AiChatRequestDto request) {
        if (!isAvailable()) {
            return AiChatResponseDto.unavailable("AI endpoint or api key is not configured");
        }
        AiChatResponseDto response = AiChatResponseDto.unavailable("Compatible endpoint binding is not implemented in this migration");
        response.setErrorCode(AiConstants.ERROR_PROVIDER);
        return response;
    }

    public List<AiStreamEventDto> stream(AiChatRequestDto request) {
        return Collections.singletonList(AiStreamEventDto.error(AiConstants.ERROR_PROVIDER,
                "Compatible endpoint stream binding is not implemented in this migration", 0));
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
