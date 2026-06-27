package v620.cc001.cloud01.app01.mservice.auth;


import v620.cc001.cloud01.app01.mservice.auth.impl.ConfigurableCosmicIdentityResolver;
import v620.cc001.cloud01.app01.mservice.auth.impl.UnavailableCosmicIdentityResolver;
public final class CyanCruiseIdentityResolverFactory {

    private CyanCruiseIdentityResolverFactory() {
    }

    public static CyanCruiseIdentityResolver production() {
        CosmicIdentityAdapterConfig config = CosmicIdentityAdapterConfig.fromSystemProperties();
        return production(CosmicLoginContextProviderFactory.production(), config);
    }

    public static CyanCruiseIdentityResolver production(CosmicIdentityContextProvider provider,
                                                        CosmicIdentityAdapterConfig config) {
        if (config != null && config.isEnabled()) {
            return new ConfigurableCosmicIdentityResolver(provider, config);
        }
        return new UnavailableCosmicIdentityResolver();
    }
}
