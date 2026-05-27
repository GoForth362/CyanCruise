package v620.cc001.cloud01.app01.mservice;

/**
 * Replaceable prompt context source for assistant chat.
 */
public interface AssistantChatContextProvider {

    String renderProfile(String userId);

    String renderMemory(String userId, String persona);

    String renderFacts(String userId);
}
