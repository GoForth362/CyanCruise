## ADDED Requirements

### Requirement: Admin governance uses Cosmic identity context

Admin governance SHALL use the shared Cosmic identity context boundary for administrator identity and authorization. Admin operations SHALL NOT rely solely on request body `adminId`, legacy admin token, or hardcoded administrator identifiers.

#### Scenario: Admin operation receives platform admin identity

- **WHEN** a management operation is requested by a Cosmic platform user with administrator equivalent role
- **THEN** admin governance SHALL use the resolved identity context as the admin identity and proceed through the existing management service contract

#### Scenario: Admin operation has mismatched admin id

- **WHEN** a management operation supplies an explicit adminId that conflicts with the resolved platform administrator identity
- **THEN** admin governance SHALL reject the operation with identity-mismatch or forbidden semantics and SHALL NOT write audit or business changes as if it succeeded
