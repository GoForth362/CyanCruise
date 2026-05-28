## ADDED Requirements

### Requirement: Platform mount backend identity adapter

CareerLoop platform mounting SHALL be backed by a server-side identity adapter contract. The webapp mount identity metadata SHALL align with the backend identity context so that production menu calls rely on Cosmic platform identity instead of development fallback or hardcoded identifiers.

#### Scenario: Mounted user route calls backend

- **WHEN** a user-facing mounted route calls a user-owned `/cc001/*` WebAPI in production
- **THEN** the backend identity adapter SHALL resolve the platform user and SHALL reject missing or mismatched identity according to the identity context contract

#### Scenario: Mounted admin route calls backend

- **WHEN** the admin console mount calls `/cc001/admin/*` in production
- **THEN** the backend identity adapter SHALL resolve administrator identity and role before management service execution
