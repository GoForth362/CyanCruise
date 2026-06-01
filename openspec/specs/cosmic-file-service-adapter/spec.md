# cosmic-file-service-adapter Specification

## Purpose
TBD - created by archiving change migrate-cosmic-file-service-adapter. Update Purpose after archive.
## Requirements
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

### Requirement: Text extraction adapter boundary
The Cosmic file service adapter SHALL expose text extraction through a separate `FileTextExtractor`-compatible boundary. Text extraction SHALL cap output to the configured maximum and SHALL fail with empty text rather than breaking file metadata flow.

#### Scenario: Text extraction succeeds
- **WHEN** a readable PDF, text, or document file is available through the provider
- **THEN** the extractor SHALL return extracted text capped to 20000 characters, charCount, truncated flag, objectKey, status, and message

#### Scenario: Text extraction is unsupported
- **WHEN** the provider does not support PDF/OCR/Office extraction
- **THEN** the extractor SHALL return empty text with unavailable status and SHALL preserve resume diagnosis or file metadata navigation

### Requirement: Safe diagnostics and configuration
The adapter SHALL expose safe diagnostics for disabled adapter, missing provider, platform errors, normalized object key, TTL, provider name, and status, but SHALL NOT log or return secrets, access tokens, full signed query strings, or raw platform credential objects.

#### Scenario: Provider error is reported
- **WHEN** a Cosmic provider call fails
- **THEN** the adapter SHALL return status and sanitized message that identify the operation and provider without exposing credentials or signed URL query parameters

#### Scenario: Configuration is inspected
- **WHEN** tenant operators review adapter configuration
- **THEN** they SHALL find enablement flag, provider name, endpoint or service binding identifier if applicable, max preview TTL, text extraction mode, and rollback behavior

### Requirement: Verification coverage
The Cosmic file service adapter migration SHALL include focused verification for disabled/default behavior, upload validation, object key normalization, preview TTL clamp, download, idempotent delete, text extraction fallback, diagnostics sanitization, route/API map consistency, OpenSpec strict validation, and JDK 8 Gradle build.

#### Scenario: Implementation is verified
- **WHEN** implementation is complete
- **THEN** verification SHALL include focused adapter/service/WebAPI tests, `node webapp\isv\v620\careerloop\validate-routes.js` when route metadata changes, OpenSpec strict validation, JDK 8 `.\gradlew.bat clean build`, and migration map updates

### Requirement: Migration boundary for IPD file service
The adapter migration SHALL transfer IPD file service business semantics, data contracts, and failure rules, but SHALL NOT directly migrate IPD implementation frameworks or runtime dependencies.

#### Scenario: Source evidence is reviewed
- **WHEN** reviewers inspect change artifacts
- **THEN** they SHALL find IPD source paths for `FileController`, `FileService`, `FileServiceImpl`, `OssConfigProperties`, `PdfTextExtractor`, and `frontend\src\api\file.ts`

#### Scenario: Implementation dependencies are checked
- **WHEN** dependency changes are reviewed
- **THEN** the change SHALL NOT introduce new external dependencies unless necessity and Cosmic/KDDT/JDK 8 compatibility are documented

