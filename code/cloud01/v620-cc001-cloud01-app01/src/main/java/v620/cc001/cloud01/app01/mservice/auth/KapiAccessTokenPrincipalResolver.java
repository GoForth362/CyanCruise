package v620.cc001.cloud01.app01.mservice.auth;

public interface KapiAccessTokenPrincipalResolver {

    KapiAccessTokenPrincipal resolve(KapiAccessTokenConfig config);
}
