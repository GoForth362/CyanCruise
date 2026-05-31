## MODIFIED Requirements

### Requirement: Configurable Cosmic file service adapter
CyanCruise SHALL provide a configurable Cosmic file service adapter for CareerLoop file upload, preview URL, download, delete, and text extraction, and SHALL NOT depend on IPD Spring Multipart, Aliyun OSS SDK, PDFBox, Flyway, Java 17 APIs, Vue, uni-app, or production secrets.

#### Scenario: Adapter is explicitly enabled
- **WHEN** `cc001.file.adapter.enabled=true` and BOS attachment file service is available
- **THEN** file WebAPI operations SHALL use BOS attachment file service through `CosmicCareerFileServiceProvider` for upload, preview URL, download, and delete

#### Scenario: Adapter is not enabled
- **WHEN** no explicit file adapter enablement is configured
- **THEN** production file operations SHALL return a stable unavailable status and SHALL NOT use in-memory storage as a production substitute

### Requirement: Stable object key contract
The Cosmic file service adapter SHALL preserve the existing CareerLoop object key contract. Business records SHALL persist stable object keys or file reference DTOs and SHALL request preview/download URLs just-in-time.

#### Scenario: Upload succeeds through BOS provider
- **WHEN** a non-empty file upload request is accepted by BOS attachment file service
- **THEN** the system SHALL return the BOS stable object key with originalFilename, size, folder, extension, provider, status, and message

#### Scenario: Platform returns an absolute file reference
- **WHEN** the platform provider returns a temporary URL or absolute file reference
- **THEN** the adapter SHALL normalize and persist only the stable object key or platform file id suitable for later preview/download calls

### Requirement: Preview URL and download boundary
The adapter SHALL generate short-lived preview URLs and authenticated downloads through the Cosmic provider boundary while retaining TTL clamp, malformed-reference handling, and recoverable unavailable results.

#### Scenario: Preview URL is requested
- **WHEN** a valid object key is provided with ttlSeconds
- **THEN** the adapter SHALL request BOS preview metadata first and SHALL return a temporary preview/download URL or unavailable status with reason

#### Scenario: Download is requested
- **WHEN** a valid object key is requested by an authorized application service
- **THEN** the adapter SHALL return bytes, objectKey, content length, provider, status, and message without exposing platform credentials

### Requirement: Delete is idempotent
The Cosmic file service adapter SHALL treat delete as best-effort cleanup. Missing, blank, or already-deleted objects SHALL NOT roll back the originating business operation.

#### Scenario: Delete succeeds through BOS provider
- **WHEN** the provider deletes a valid BOS object key
- **THEN** the adapter SHALL return ok status with objectKey and provider diagnostics

#### Scenario: Delete provider is unavailable
- **WHEN** the provider cannot complete deletion because the platform is unavailable or the object is missing
- **THEN** the adapter SHALL return skipped or unavailable status and SHALL keep the caller workflow recoverable
