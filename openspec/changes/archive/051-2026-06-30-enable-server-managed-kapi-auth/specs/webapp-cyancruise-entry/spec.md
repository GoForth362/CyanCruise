## ADDED Requirements

### Requirement: Self-built app defaults to server-managed API mode
CyanCruise webapp SHALL default to server-managed KAPI v2 API calls to the registered route `cc001/cyancruise/route` when opened from the self-built Cosmic application without an explicit API mode. KAPI/OpenAPI direct-debug mode SHALL be used only when the URL or stored configuration explicitly requests `apiMode=kapi`.

#### Scenario: User opens self-built app without apiMode
- **WHEN** a user opens `/ierp/isv/v620/cyancruise/index.htm?ccRoute=workbench` without `apiMode`
- **THEN** the webapp SHALL call `/ierp/kapi/v2/v620/v620_cc001/cc001/cyancruise/route` and SHALL NOT require an `access_token` query parameter

#### Scenario: Developer explicitly opens KAPI mode
- **WHEN** a developer opens the webapp with `apiMode=kapi` and a valid `access_token`
- **THEN** the webapp SHALL keep using the configured KAPI route and token for API calls

#### Scenario: Historical KAPI mode is cleared
- **WHEN** a user opens the webapp with `apiMode=direct` or `apiMode=reset`
- **THEN** the webapp SHALL remove cached KAPI mode and token values from localStorage and use direct mode

#### Scenario: Historical KAPI token exists but no apiMode is provided
- **WHEN** a user opens the webapp without `apiMode` and localStorage has no active `apiMode=kapi`
- **THEN** the webapp SHALL use server-managed mode by default

### Requirement: Token diagnostics remain safe
CyanCruise webapp SHALL diagnose whether KAPI mode and token are present without displaying full tokens or secrets. Diagnostics SHALL guide users toward direct self-built app mode or explicit KAPI validation mode.

#### Scenario: KAPI mode has no token
- **WHEN** the webapp is in KAPI mode but no `access_token` is available
- **THEN** the identity diagnostic SHALL report that the KAPI token is missing without exposing secrets or suggesting front-end client secret storage

### Requirement: Server-managed API mode
CyanCruise webapp SHALL support `apiMode=server` for production self-built app menus. In this mode the frontend SHALL post all CyanCruise business requests to the registered custom WebAPI through the KAPI v2 route and SHALL NOT read, store, or send KAPI `access_token` values.

#### Scenario: User opens fixed server-managed menu URL
- **WHEN** a user opens `/ierp/isv/v620/cyancruise/index.htm?apiMode=server#workbench`
- **THEN** the webapp SHALL call `/ierp/kapi/v2/v620/v620_cc001/cc001/cyancruise/route` with `{path, body}` and SHALL NOT require an `access_token` query parameter

#### Scenario: Historical KAPI token exists in localStorage
- **WHEN** a user opens the webapp with `apiMode=server`
- **THEN** the webapp SHALL clear cached KAPI token and route parameters while preserving server-managed API mode
