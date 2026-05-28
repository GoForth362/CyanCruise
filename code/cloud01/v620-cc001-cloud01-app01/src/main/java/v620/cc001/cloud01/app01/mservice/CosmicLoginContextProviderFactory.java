package v620.cc001.cloud01.app01.mservice;

public final class CosmicLoginContextProviderFactory {

    private CosmicLoginContextProviderFactory() {
    }

    public static CosmicIdentityContextProvider production() {
        return production(CosmicLoginContextProviderConfig.fromSystemProperties());
    }

    public static CosmicIdentityContextProvider production(CosmicLoginContextProviderConfig config) {
        CosmicLoginContextProviderConfig safeConfig = config == null
                ? CosmicLoginContextProviderConfig.disabled()
                : config;
        return new PlatformCosmicIdentityContextProvider(loadBridge(safeConfig), safeConfig);
    }

    public static CosmicIdentityContextProvider production(CosmicLoginContextBridge bridge,
                                                           CosmicLoginContextProviderConfig config) {
        return new PlatformCosmicIdentityContextProvider(bridge, config);
    }

    private static CosmicLoginContextBridge loadBridge(CosmicLoginContextProviderConfig config) {
        if (config == null || !config.isEnabled() || config.getBridgeClassName() == null) {
            return new UnavailableCosmicLoginContextBridge();
        }
        try {
            Class<?> bridgeClass = Class.forName(config.getBridgeClassName());
            Object instance = bridgeClass.newInstance();
            if (instance instanceof CosmicLoginContextBridge) {
                return (CosmicLoginContextBridge) instance;
            }
            return new UnavailableCosmicLoginContextBridge();
        } catch (Exception ex) {
            return new UnavailableCosmicLoginContextBridge();
        }
    }
}
