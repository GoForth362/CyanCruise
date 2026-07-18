package v620.cc001.base.common.dto.career;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class CareerAgentTodayDtoTest {

    @Test
    void todayRecommendationAndActionsAreSerializableForRpcResponses() {
        final CareerAgentTodayDto today = new CareerAgentTodayDto();
        CareerAgentTodayDto.Action action = new CareerAgentTodayDto.Action();
        action.setLabel("完善简历");
        today.getActions().add(action);

        assertDoesNotThrow(new org.junit.jupiter.api.function.Executable() {
            public void execute() throws Throwable {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                ObjectOutputStream output = new ObjectOutputStream(bytes);
                output.writeObject(today);
                output.close();
            }
        });
    }
}
