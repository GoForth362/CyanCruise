## ADDED Requirements

### Requirement: KAPI token broker isolation from login context
The Cosmic login context provider SHALL remain the production source for current user identity when CyanCruise is opened from a self-built Cosmic application. Server-managed KAPI token configuration SHALL NOT override, synthesize, or replace the current login context.

#### Scenario: Backend token configuration exists
- **WHEN** server-managed KAPI token properties are configured
- **THEN** the login context provider SHALL still resolve user/admin identity from Cosmic request context or approved platform context fields

#### Scenario: Login context is unavailable
- **WHEN** the login context provider cannot resolve a current user
- **THEN** protected production WebAPI SHALL return identity-required diagnostics even if server-managed KAPI token configuration is available
