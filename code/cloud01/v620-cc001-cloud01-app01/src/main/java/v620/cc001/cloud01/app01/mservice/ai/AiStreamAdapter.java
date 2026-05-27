package v620.cc001.cloud01.app01.mservice.ai;

import v620.cc001.base.common.dto.ai.AiConstants;
import v620.cc001.base.common.dto.ai.AiStreamEventDto;

import java.util.ArrayList;
import java.util.List;

public class AiStreamAdapter {

    public List<AiStreamEventDto> tokens(String text) {
        List<AiStreamEventDto> events = new ArrayList<AiStreamEventDto>();
        int index = 0;
        if (text != null) {
            for (String token : text.split("")) {
                if (token.length() > 0) {
                    events.add(AiStreamEventDto.token(token, index++));
                }
            }
        }
        events.add(AiStreamEventDto.done(index));
        return events;
    }

    public List<AiStreamEventDto> error(String errorCode, String message) {
        List<AiStreamEventDto> events = new ArrayList<AiStreamEventDto>();
        events.add(AiStreamEventDto.error(errorCode == null ? AiConstants.ERROR_PROVIDER : errorCode, message, 0));
        return events;
    }
}
