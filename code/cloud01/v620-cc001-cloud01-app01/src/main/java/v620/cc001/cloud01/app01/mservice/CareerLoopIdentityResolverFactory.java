package v620.cc001.cloud01.app01.mservice;

public final class CareerLoopIdentityResolverFactory {

    private CareerLoopIdentityResolverFactory() {
    }

    public static CareerLoopIdentityResolver production() {
        return production(new EmptyCosmicIdentityContextProvider(), CosmicIdentityAdapterConfig.fromSystemProperties());
    }

    public static CareerLoopIdentityResolver production(CosmicIdentityContextProvider provider,
                                                        CosmicIdentityAdapterConfig config) {
        if (config != null && config.isEnabled()) {
            return new ConfigurableCosmicIdentityResolver(provider, config);
        }
        return new UnavailableCosmicIdentityResolver();
    }
}
