package v620.cc001.cloud01.app01.mservice.auth;

import java.util.Map;

public interface CosmicIdentityContextProvider {

    Map<String, Object> currentContext();
}
