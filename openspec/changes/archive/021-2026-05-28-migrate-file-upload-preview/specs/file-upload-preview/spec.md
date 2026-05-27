## ADDED Requirements

### Requirement: File object reference

CyanCruise SHALL represent uploaded files with a stable object key and SHALL NOT require business records to persist long-lived absolute object storage URLs.

#### Scenario: File upload succeeds

- **WHEN** a file is uploaded to a valid folder
- **THEN** the system SHALL return a bare object key such as `resumes/<generated-id>.pdf`, original filename, size, folder, extension, and status

#### Scenario: Business record stores file reference

- **WHEN** resume, avatar, content, or diagnosis records reference an uploaded file
- **THEN** they SHALL store the object key or file reference DTO and SHALL request preview/download URLs just-in-time

### Requirement: Upload validation and key generation

CyanCruise SHALL validate uploads and generate normalized keys without depending on IPD Spring Multipart or Aliyun OSS runtime classes.

#### Scenario: Empty file is uploaded

- **WHEN** upload content is missing or byte length is zero
- **THEN** the system SHALL reject the upload with a file-empty state and SHALL NOT create an object key

#### Scenario: Folder is blank

- **WHEN** an upload request omits folder or provides a blank folder
- **THEN** the system SHALL default the folder to `others`

#### Scenario: Original filename has extension

- **WHEN** the original filename contains an extension
- **THEN** the generated object key SHALL preserve the extension after a generated unique id

### Requirement: Object key normalization

CyanCruise SHALL normalize file references from bare keys, leading-slash keys, and legacy absolute URLs.

#### Scenario: Bare key is normalized

- **WHEN** the input is `resumes/a.pdf`
- **THEN** normalization SHALL return `resumes/a.pdf`

#### Scenario: Leading slash key is normalized

- **WHEN** the input is `/resumes/a.pdf`
- **THEN** normalization SHALL return `resumes/a.pdf`

#### Scenario: Presigned URL is normalized

- **WHEN** the input is `https://bucket.endpoint/resumes/a.pdf?signature=x`
- **THEN** normalization SHALL return `resumes/a.pdf` and SHALL strip the query string

#### Scenario: Malformed URL is normalized

- **WHEN** the input contains scheme and host but no object path
- **THEN** the system SHALL return a malformed-reference state instead of guessing a key

### Requirement: Preview URL generation

CyanCruise SHALL provide short-lived preview URL contracts for browser or Cosmic webapp rendering.

#### Scenario: Preview URL is requested

- **WHEN** a valid object key or legacy URL is provided with ttlSeconds
- **THEN** the system SHALL clamp ttlSeconds to the allowed preview window and return a preview URL or platform temporary URL

#### Scenario: Preview reference is blank

- **WHEN** preview is requested for a blank file reference
- **THEN** the system SHALL return an empty or skipped preview result and SHALL NOT throw into the caller flow

#### Scenario: Preview provider is unavailable

- **WHEN** the storage adapter cannot generate a preview URL
- **THEN** the system SHALL return an unavailable state with reason and keep the caller page navigable

### Requirement: Download and delete boundary

CyanCruise SHALL expose download and delete operations through a file storage adapter.

#### Scenario: File bytes are downloaded

- **WHEN** a valid object key is requested by an authorized service
- **THEN** the system SHALL return bytes, key, content length, and status

#### Scenario: File delete is requested

- **WHEN** delete is requested for a valid, missing, or blank object reference
- **THEN** the operation SHALL be idempotent and SHALL return ok/skipped status without rolling back the originating business operation

### Requirement: Text extraction boundary

CyanCruise SHALL provide a text extraction boundary for resume diagnosis and similar AI workflows.

#### Scenario: PDF text extraction succeeds

- **WHEN** a text extractor receives readable PDF or document bytes
- **THEN** the system SHALL return extracted text capped to the configured maximum character count

#### Scenario: Text extraction fails

- **WHEN** the file is unreadable, unsupported, or extraction adapter fails
- **THEN** the system SHALL return empty text with failed/unavailable reason and SHALL NOT break the primary file metadata flow

### Requirement: File WebAPI and webapp mapping

The migration SHALL define Cosmic WebAPI and webapp route/API mapping for file upload, preview URL, download, delete, and text extraction.

#### Scenario: Route map is reviewed

- **WHEN** reviewers inspect webapp migration artifacts
- **THEN** they SHALL find file upload/preview route keys, consumed WebAPI paths, DTO fields, identity requirements, and fallback states

#### Scenario: File backend is unavailable

- **WHEN** file WebAPI is unavailable
- **THEN** the webapp SHALL keep CareerLoop workbench and resume metadata routes navigable and display upload/preview unavailable state

### Requirement: Migration boundary for file upload preview

The file upload preview migration SHALL rebuild business semantics for CyanCruise and SHALL NOT directly migrate IPD Spring Multipart, Aliyun OSS SDK, PDFBox, Flyway SQL, Java 17 APIs, Vue, uni-app, or production secrets.

#### Scenario: Implementation is inspected

- **WHEN** implementation files are reviewed
- **THEN** they SHALL reside in CyanCruise target modules and SHALL NOT require `F:\Project\IPD` source files or IPD runtime dependencies

#### Scenario: Dependencies are checked

- **WHEN** dependency changes are reviewed
- **THEN** the change SHALL NOT introduce new external dependencies unless their necessity and Cosmic/KDDT/JDK 8 compatibility are documented

### Requirement: Verification and migration documentation

The file upload preview migration SHALL include verification and documentation that prove the proposed contracts are OpenSpec-valid and aligned with the migration map.

#### Scenario: Change is verified before archive

- **WHEN** implementation is complete
- **THEN** verification SHALL include strict OpenSpec validation, focused helper/service/WebAPI tests or equivalent static checks, route/API map checks when webapp artifacts change, JDK 8 Gradle build validation, and migration map updates

#### Scenario: Migration map is updated

- **WHEN** the change is finalized
- **THEN** `docs/ipd-to-cyancruise-migration-map.md` SHALL record IPD source paths, CyanCruise target modules, data mapping, temporarily excluded items, and validation results
