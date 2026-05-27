package v620.cc001.cloud01.app01.mservice.ai;

import v620.cc001.base.common.dto.ai.AiChatRequestDto;
import v620.cc001.base.common.dto.ai.AiChatResponseDto;
import v620.cc001.base.common.dto.ai.AiStreamEventDto;

import java.util.List;

public interface AiProviderAdapter {

    boolean isAvailable();

    AiChatResponseDto chat(AiChatRequestDto request);

    List<AiStreamEventDto> stream(AiChatRequestDto request);
}
