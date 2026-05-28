## ADDED Requirements

### Requirement: Production Cosmic file adapter integration
The file upload preview capability SHALL integrate with a production Cosmic file service adapter when explicitly enabled. Existing `/cc001/files/*` WebAPI contracts SHALL remain stable, and adapter unavailable states SHALL be returned as recoverable results rather than uncaught platform errors.

#### Scenario: Production adapter is enabled
- **WHEN** `/cc001/files/upload`, `/cc001/files/preview-url`, `/cc001/files/download`, `/cc001/files/delete`, or `/cc001/files/extract-text` is invoked with the Cosmic file adapter enabled
- **THEN** the operation SHALL delegate to the configured Cosmic provider through the file service adapter and SHALL preserve existing DTO status semantics

#### Scenario: Production adapter is disabled
- **WHEN** file WebAPI is invoked in production without an enabled Cosmic file adapter
- **THEN** upload, preview, download, delete, and extract-text SHALL return skipped or unavailable states and SHALL NOT persist files in an in-memory production fallback

#### Scenario: Existing business record references a file
- **WHEN** resume, diagnosis, avatar, or content flows request preview/download/text extraction for an existing object key
- **THEN** the file upload preview service SHALL keep the business route navigable even if the Cosmic file adapter returns unavailable or unsupported

#### Scenario: Adapter verification is prepared
- **WHEN** reviewers inspect the file upload preview migration tasks
- **THEN** they SHALL find adapter-focused tests and validation steps that cover default disabled behavior, enabled provider delegation, TTL clamp, object key normalization, idempotent delete, text extraction fallback, route/API map checks, OpenSpec validation, and JDK 8 build
