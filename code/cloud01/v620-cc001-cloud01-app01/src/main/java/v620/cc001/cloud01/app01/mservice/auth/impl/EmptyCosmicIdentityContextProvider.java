package v620.cc001.cloud01.app01.mservice.auth.impl;

import v620.cc001.cloud01.app01.mservice.auth.*;
import java.util.Collections;
import java.util.Map;

public class EmptyCosmicIdentityContextProvider implements CosmicIdentityContextProvider {

    public Map<String, Object> currentContext() {
        return Collections.emptyMap();
    }
}
