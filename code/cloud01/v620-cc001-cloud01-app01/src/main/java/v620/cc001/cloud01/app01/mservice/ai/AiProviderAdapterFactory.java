package v620.cc001.cloud01.app01.mservice.ai;

public final class AiProviderAdapterFactory {

    private AiProviderAdapterFactory() {
    }

    public static AiProviderAdapter fromSystemProperties() {
        return fromConfig(AiProviderConfig.fromSystemProperties());
    }

    public static AiProviderAdapter fromConfig(AiProviderConfig config) {
        if (config == null || !config.isComplete()) {
            return new UnavailableAiProviderAdapter();
        }
        return new CompatibleEndpointAiProviderAdapter(config);
    }
}
