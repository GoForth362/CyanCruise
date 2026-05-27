package v620.cc001.cloud01.app01.mservice;

/**
 * Empty context provider used before profile memory adapters are configured.
 */
public class EmptyAssistantChatContextProvider implements AssistantChatContextProvider {

    public String renderProfile(String userId) {
        return "";
    }

    public String renderMemory(String userId, String persona) {
        return "";
    }

    public String renderFacts(String userId) {
        return "";
    }
}
