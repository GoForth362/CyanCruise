## ADDED Requirements

### Requirement: Resolver factory production adapter selection

Cosmic identity context SHALL include a resolver factory or equivalent selection boundary that chooses between safe unavailable production resolver, configurable Cosmic adapter, and explicit development resolver according to environment and configuration.

#### Scenario: Production adapter is configured

- **WHEN** production adapter configuration is explicitly enabled
- **THEN** the factory SHALL return a resolver that reads Cosmic platform context candidates

#### Scenario: Production adapter is not configured

- **WHEN** production adapter configuration is absent or disabled
- **THEN** the factory SHALL return the safe unavailable resolver and preserve identity-required behavior

### Requirement: Adapter diagnostics in identity context

Cosmic identity context SHALL expose safe adapter diagnostics through status and message fields so deployment checks can distinguish disabled adapter, missing platform context, missing identity fields, and successful resolution.

#### Scenario: Deployment checks identity adapter

- **WHEN** tenant deployment validation invokes a representative protected WebAPI
- **THEN** the identity context result or fallback status SHALL make adapter disabled, missing context, missing role, or identity mismatch distinguishable without exposing secrets
