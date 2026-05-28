## 1. Context and Boundaries

- [x] 1.1 Review IPD sources `FileController.java`, `FileService.java`, `FileServiceImpl.java`, `OssConfigProperties.java`, `PdfTextExtractor.java`, and `frontend/src/api/file.ts`; record upload/key/preview/download/delete/text semantics in implementation notes or migration map.
- [x] 1.2 Review existing CyanCruise `file-upload-preview` DTOs, helper, `CareerFileStorage`, `FileTextExtractor`, `FileUploadPreviewApplicationService`, WebAPI, route map, and tests before editing.
- [x] 1.3 Confirm no Spring Multipart, Aliyun OSS SDK, PDFBox, Flyway, Java 17, Vue, uni-app, or production secret dependency is introduced.

## 2. Adapter Contracts and Configuration

- [x] 2.1 Add JDK 8-compatible Cosmic file adapter configuration with explicit enablement, provider name, preview TTL limit, text extraction mode, diagnostics flag, and default disabled-safe behavior.
- [x] 2.2 Add provider boundary types for upload, preview URL, download, delete, and text extraction that can wrap real Cosmic file service APIs without leaking platform SDK details into business services.
- [x] 2.3 Add default unavailable provider that returns stable skipped/unavailable statuses and never stores production files in memory.
- [x] 2.4 Add factory or wiring helper so production construction can choose disabled provider or configured Cosmic provider while tests can still inject in-memory storage/extractor.

## 3. Cosmic File Service Adapter Implementation

- [x] 3.1 Implement object key normalization and provider result mapping so platform absolute references or temporary URLs are converted back to stable object keys/file ids.
- [x] 3.2 Implement upload delegation that validates non-empty content, preserves original filename/folder/extension/size, and returns existing `FileUploadResult` semantics.
- [x] 3.3 Implement preview URL delegation with TTL clamp, sanitized diagnostics, and no persisted long-lived absolute URL.
- [x] 3.4 Implement authenticated download delegation that returns bytes/content length/status without exposing credentials or signed query strings.
- [x] 3.5 Implement idempotent delete delegation that treats blank/missing/platform-unavailable cases as skipped or unavailable without rolling back caller workflows.
- [x] 3.6 Implement text extraction adapter boundary that caps text to 20000 characters and returns empty text with unavailable/unsupported reason when platform extraction is absent.

## 4. WebAPI, Route Map, and Documentation

- [x] 4.1 Keep `/cc001/files/upload`, `/preview-url`, `/download`, `/delete`, and `/extract-text` request/response contracts stable while routing through the configured adapter.
- [x] 4.2 Update `webapp/isv/v620/careerloop/careerloop-routes.json` only if production adapter metadata or fallback copy changes; keep route validation passing.
- [x] 4.3 Update `docs/ipd-to-cyancruise-migration-map.md` with change id, branch, IPD source paths, CyanCruise target modules, data mapping, temporarily excluded items, verification commands, and tenant validation notes.
- [x] 4.4 Document rollback behavior: disabling the adapter returns unavailable states and does not fall back to production in-memory storage.

## 5. Verification

- [x] 5.1 Add focused tests for default disabled behavior, explicit enabled provider delegation, upload validation, object key normalization, preview TTL clamp, download result mapping, idempotent delete, text extraction fallback, and diagnostics sanitization.
- [x] 5.2 Run focused Gradle tests covering file helper/service/WebAPI/adapter behavior.
- [x] 5.3 Run `node webapp\isv\v620\careerloop\validate-routes.js` if route metadata changes.
- [x] 5.4 Run `openspec validate migrate-cosmic-file-service-adapter --strict`.
- [x] 5.5 Run `openspec validate --all --strict`.
- [x] 5.6 Run JDK 8 `.\gradlew.bat clean build`.
- [x] 5.7 After implementation review, archive the change, update migration map final status, commit, and push `codex/migrate-cosmic-file-service-adapter`.
