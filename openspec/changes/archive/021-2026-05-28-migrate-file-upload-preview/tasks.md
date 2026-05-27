## 1. Migration Context

- [x] 1.1 Review IPD file sources listed in proposal.md and confirm migrated semantics: object key persistence, folder defaulting, extension preservation, authenticated download, short-lived preview URL, idempotent delete, and PDF/text extraction fallback.
- [x] 1.2 Confirm CyanCruise DTO/helper/app01/WebAPI package patterns before editing code.
- [x] 1.3 Keep Spring Multipart, Aliyun OSS SDK, PDFBox, Flyway SQL, Java 17 APIs, Vue, uni-app, production secrets, and IPD runtime code out of scope.

## 2. Common DTO And Helper Rules

- [x] 2.1 Add JDK 8 compatible file DTOs and constants in `code/base/v620-cc001-base-common`, including file reference, upload request/result, preview URL result, download result, delete result, text extraction result, and file status constants.
- [x] 2.2 Add helper rules in `code/base/v620-cc001-base-helper` for folder normalization, extension extraction, object key generation inputs, URL/key normalization, malformed reference detection, TTL clamp, empty file validation, file type hints, and text truncation.
- [x] 2.3 Add focused helper tests for blank folder defaulting, extension preservation, bare key normalization, leading slash normalization, presigned URL query stripping, malformed URL rejection, TTL clamp, empty file rejection, and text truncation.

## 3. Application Service And WebAPI

- [x] 3.1 Add app01 storage boundaries for file upload/download/presign/delete and text extraction, with in-memory or local test adapter suitable for JDK 8 tests.
- [x] 3.2 Add file application service for upload bytes, preview URL, download bytes, delete object, and extract text with explicit unavailable/skipped states.
- [x] 3.3 Add Cosmic WebAPI contracts under `/cc001/files/*` or equivalent paths for upload, preview-url, download, delete, and extract-text.
- [x] 3.4 Add focused service/WebAPI tests for upload success, empty upload rejection, preview TTL clamp, blank preview skip, malformed URL failure, provider unavailable preview, download, delete idempotency, and text extraction fallback.

## 4. Webapp Contract Mapping

- [x] 4.1 Update `webapp/isv/v620/careerloop/careerloop-routes.json` or equivalent route/API map with file upload/preview route keys, WebAPI paths, identity requirements, and unavailable fallback states.
- [x] 4.2 Add or update static webapp entry assets only as needed to expose file upload/preview contract near resume entry without introducing Vue/uni-app runtime dependencies.
- [x] 4.3 Add static checks or lightweight validation that route/API map references the new file contracts and keeps existing CareerLoop workbench routes unchanged.

## 5. Documentation And Verification

- [x] 5.1 Update `docs/ipd-to-cyancruise-migration-map.md` with IPD source paths, CyanCruise target modules, data mapping, temporarily excluded items, and validation approach for `migrate-file-upload-preview`.
- [x] 5.2 Run `openspec validate migrate-file-upload-preview --strict`.
- [x] 5.3 Run `openspec validate --all --strict`.
- [x] 5.4 Set JDK 8 environment and run `.\gradlew.bat clean build`.
- [x] 5.5 After implementation review passes, sync specs, archive with the next numbered archive folder, commit, and push the migration branch.
