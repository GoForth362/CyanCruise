# server-managed-kapi-auth Specification

## Purpose
定义 CyanCruise 后端托管 KAPI 调用凭证的获取、缓存、隔离和浏览器路由方式，确保前端不暴露 access_token 或 clientSecret，且不把 KAPI 凭证误当作登录用户身份。

## Requirements

### Requirement: Server-managed KAPI token configuration
CyanCruise SHALL provide a JDK 1.8 compatible backend configuration for KAPI AccessToken acquisition. The configuration SHALL read client id, client secret, token endpoint, language, timeout, and cache TTL from system properties or equivalent runtime configuration, and SHALL NOT require hardcoded secrets in Java business code or webapp assets. Username and account id SHOULD be resolved from the current Cosmic `RequestContext`; configured username and account id SHALL be optional fallback values only.

#### Scenario: Complete token configuration is present
- **WHEN** endpoint, client id, and client secret are configured and the current Cosmic request context provides username and account id
- **THEN** the backend token service SHALL treat token acquisition as available and SHALL use the configured endpoint plus the current request principal

#### Scenario: Required token configuration is missing
- **WHEN** client id, client secret, or token endpoint is missing
- **THEN** the backend token service SHALL report an unavailable token state and SHALL NOT call the token endpoint

#### Scenario: Current request principal is missing
- **WHEN** username and account id cannot be resolved from Cosmic request context or optional fallback configuration
- **THEN** the backend token service SHALL report an unavailable token state and SHALL NOT call the token endpoint

### Requirement: Server-managed KAPI token acquisition and cache
CyanCruise SHALL acquire KAPI AccessToken from `/ierp/kapi/oauth2/getToken` or the configured equivalent endpoint using a fresh nonce and timestamp per request. The token service SHALL cache successful tokens per username and account id until a configured safety window before expiration and SHALL retry acquisition after cached tokens expire.

#### Scenario: Token endpoint returns a valid token
- **WHEN** the backend token service receives a successful token response
- **THEN** it SHALL return the access token and cache it without exposing client secret in diagnostics

#### Scenario: Cached token is still valid
- **WHEN** a token is requested before the configured cache expiry
- **THEN** the backend token service SHALL return the cached token without calling the token endpoint again

#### Scenario: Different users request tokens
- **WHEN** two different Cosmic users or account ids request KAPI tokens
- **THEN** the backend token service SHALL cache and return their tokens separately

#### Scenario: Token endpoint rejects the request
- **WHEN** the token endpoint returns an error or a response without a usable access token
- **THEN** the backend token service SHALL return a failed token state with sanitized diagnostics

### Requirement: KAPI token is not production user identity
Server-managed KAPI AccessToken SHALL be treated as an OpenAPI calling credential, not as proof of the current browser user. CyanCruise SHALL continue to resolve production user identity from Cosmic login context, approved platform context bridge, or explicit development fallback only when development mode is requested.

#### Scenario: Fallback proxy token is configured
- **WHEN** the backend uses configured fallback username and account id because request context principal is unavailable
- **THEN** CyanCruise SHALL NOT treat that fallback proxy user as the current logged-in CyanCruise user

#### Scenario: Production identity is missing
- **WHEN** no Cosmic login context is available for a protected user-owned WebAPI
- **THEN** CyanCruise SHALL preserve identity-required behavior instead of trusting the KAPI token configuration as user identity

### Requirement: Server-managed browser route
CyanCruise SHALL provide a server-managed backend route at KAPI v2 api code `cc001/cyancruise/server/route` that accepts `{path, body}` from the static webapp without requiring the browser to provide `access_token`, `clientSecret`, or any KAPI credential. The backend route SHALL acquire or reuse a cached KAPI AccessToken server-side, call the existing `cc001/cyancruise/route` custom WebAPI, and return the routed business response to the browser.

#### Scenario: Browser calls server-managed route
- **WHEN** the webapp posts a CyanCruise business path to the server-managed route
- **THEN** the backend SHALL attach a server-acquired KAPI token only to the backend-to-KAPI request and SHALL NOT include that token in the browser response

#### Scenario: Identity endpoint is requested
- **WHEN** the webapp requests `/cc001/identity/current` through the server-managed route
- **THEN** the backend SHALL return the current Cosmic login identity directly and SHALL NOT acquire a KAPI token for that identity check

#### Scenario: Protected route lacks login context
- **WHEN** a protected business path is requested but the current Cosmic login context is unavailable
- **THEN** the backend SHALL reject the request with identity-required diagnostics before acquiring or using a KAPI token

