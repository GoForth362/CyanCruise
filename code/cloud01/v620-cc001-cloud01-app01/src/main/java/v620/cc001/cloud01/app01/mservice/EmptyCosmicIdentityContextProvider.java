package v620.cc001.cloud01.app01.mservice;

import java.util.Collections;
import java.util.Map;

public class EmptyCosmicIdentityContextProvider implements CosmicIdentityContextProvider {

    public Map<String, Object> currentContext() {
        return Collections.emptyMap();
    }
}
