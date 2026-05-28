## ADDED Requirements

### Requirement: Platform mounting documents backend adapter enablement

CareerLoop platform mounting SHALL document how the backend Cosmic identity adapter is enabled, which platform context fields are expected, which role aliases authorize administrators, and how tenant validation distinguishes disabled adapter from missing identity.

#### Scenario: Tenant validates backend adapter

- **WHEN** a tenant operator reviews CareerLoop platform mounting documentation
- **THEN** they SHALL find the adapter enablement property or configuration, default field candidates, admin role alias expectations, route/API validation command, and rollback behavior

#### Scenario: Adapter remains disabled

- **WHEN** the backend adapter is not enabled in a tenant
- **THEN** mounted production routes SHALL continue to fail safely with identity-required behavior instead of using development fallback
