## ADDED Requirements

### Requirement: Self-built app defaults to same-origin API mode
CyanCruise webapp SHALL default to same-origin direct `/cc001/*` API calls when opened from the self-built Cosmic application without an explicit API mode. KAPI/OpenAPI mode SHALL be used only when the URL or stored configuration explicitly requests `apiMode=kapi`.

#### Scenario: User opens self-built app without apiMode
- **WHEN** a user opens `/ierp/isv/v620/cyancruise/index.htm?ccRoute=workbench` without `apiMode`
- **THEN** the webapp SHALL call same-origin `/cc001/*` contracts and SHALL NOT require an `access_token` query parameter

#### Scenario: Developer explicitly opens KAPI mode
- **WHEN** a developer opens the webapp with `apiMode=kapi` and a valid `access_token`
- **THEN** the webapp SHALL keep using the configured KAPI route and token for API calls

#### Scenario: Historical KAPI mode is cleared
- **WHEN** a user opens the webapp with `apiMode=direct` or `apiMode=reset`
- **THEN** the webapp SHALL remove cached KAPI mode and token values from localStorage and use direct mode

### Requirement: Token diagnostics remain safe
CyanCruise webapp SHALL diagnose whether KAPI mode and token are present without displaying full tokens or secrets. Diagnostics SHALL guide users toward direct self-built app mode or explicit KAPI validation mode.

#### Scenario: KAPI mode has no token
- **WHEN** the webapp is in KAPI mode but no `access_token` is available
- **THEN** the identity diagnostic SHALL report that the KAPI token is missing without exposing secrets or suggesting front-end client secret storage
