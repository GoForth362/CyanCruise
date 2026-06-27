package v620.cc001.cloud01.app01.mservice.auth.impl;

import v620.cc001.cloud01.app01.mservice.auth.*;
import java.util.Collections;
import java.util.Map;

public class UnavailableCosmicLoginContextBridge implements CosmicLoginContextBridge {

    public Map<String, Object> currentLoginContext() {
        return Collections.emptyMap();
    }
}
