## 1. Backend token service

- [x] 1.1 Add KAPI token configuration and result types that read only from runtime properties.
- [x] 1.2 Implement JDK 8 compatible token acquisition with nonce/timestamp generation, sanitized errors, and cache expiry.
- [x] 1.3 Add focused unit tests for missing config, successful fetch, cache reuse, and failed token response.

## 2. Webapp API mode

- [x] 2.1 Change CyanCruise webapp default API mode to same-origin direct mode.
- [x] 2.2 Add `apiMode=direct`/`apiMode=reset` cleanup for cached KAPI mode and token values.
- [x] 2.3 Keep explicit KAPI mode behavior compatible with existing `apiMode=kapi&access_token=...` validation.

## 3. Documentation and configuration

- [x] 3.1 Document backend KAPI token properties in `gradle.properties.example`.
- [x] 3.2 Update runtime deployment guidance and migration map with server-managed token boundaries and direct-mode self-built app entry.

## 4. Verification

- [x] 4.1 Run focused Gradle tests for the KAPI token service.
- [x] 4.2 Run `node --check webapp\isv\v620\cyancruise\assets\app.js`.
- [x] 4.3 Run `node webapp\isv\v620\cyancruise\validate-routes.js`.
- [x] 4.4 Run `openspec validate enable-server-managed-kapi-auth --strict`.

## 5. Server-managed browser proxy

- [x] 5.1 Add a backend server-managed route that receives browser calls without frontend tokens.
- [x] 5.2 Reuse backend KAPI token acquisition/cache to call the existing `cc001/cyancruise/route` custom WebAPI.
- [x] 5.3 Preserve current Cosmic login context as CyanCruise user identity and do not infer user identity from KAPI tokens.
- [x] 5.4 Add `apiMode=server` in the static webapp and keep explicit `apiMode=kapi` only for debugging.
- [x] 5.5 Update deployment guidance for fixed menu URLs without `access_token`.
