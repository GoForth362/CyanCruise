package v620.cc001.cloud01.app01.mservice.application;

import org.junit.jupiter.api.Test;
import v620.cc001.cloud01.app01.mservice.ai.AgentPlatformTaskFlowConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AssessmentApplicationServiceTest {

    @Test
    void neverUsesAnotherBusinessAgentWhenProfileAgentIsUnavailable() {
        String[] keys = {
                "cc001.agent.platform.profile.enabled",
                "cc001.agent.platform.profile.agentNumber",
                "cc001.agent.platform.resume.enabled",
                "cc001.agent.platform.resume.agentNumber"
        };
        String[] before = values(keys);
        try {
            System.setProperty(keys[0], "false");
            System.clearProperty(keys[1]);
            System.setProperty(keys[2], "true");
            System.setProperty(keys[3], "shared-ai-agent");

            AgentPlatformTaskFlowConfig config = AssessmentApplicationService.assessmentAiConfig();

            assertTrue(!config.isAgentSdkAvailable());
            assertEquals(null, config.getAgentNumber());
        } finally {
            restore(keys, before);
        }
    }

    private static String[] values(String[] keys) {
        String[] values = new String[keys.length];
        for (int index = 0; index < keys.length; index++) {
            values[index] = System.getProperty(keys[index]);
        }
        return values;
    }

    private static void restore(String[] keys, String[] values) {
        for (int index = 0; index < keys.length; index++) {
            if (values[index] == null) {
                System.clearProperty(keys[index]);
            } else {
                System.setProperty(keys[index], values[index]);
            }
        }
    }
}
