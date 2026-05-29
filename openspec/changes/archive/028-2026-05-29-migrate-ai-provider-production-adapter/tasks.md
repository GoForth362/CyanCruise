## 1. Source Mapping and Baseline

- [x] 1.1 Review IPD `AiService.java`, `AiServiceImpl.java`, `FunctionCallingService*.java`, `service/ai/tools/`, and `frontend/src/api/ai.ts`; record migrated semantics, excluded runtime code, and target CyanCruise modules.
- [x] 1.2 Review existing CyanCruise `AiGateway`, `AiProviderAdapter`, `CompatibleEndpointAiProviderAdapter`, AI DTOs, helper tests, and CareerLoop scenario adapters to avoid changing public WebAPI contracts.

## 2. Common Contracts and Helpers

- [x] 2.1 Add or extend AI constants/DTO fields needed for production provider diagnostics, retry count, elapsed time, status code, provider name, invalid response, authentication failure, and timeout classification.
- [x] 2.2 Add JDK 8 compatible helper logic for OpenAI-compatible request building, response parsing, usage mapping, tool call mapping, stream chunk normalization, and secret redaction.
- [x] 2.3 Add focused helper tests for successful response parsing, usage defaults, invalid JSON, tool calls, stream token/done/error events, and apiKey/Authorization redaction.

## 3. Production Provider Adapter

- [x] 3.1 Implement production provider configuration and factory rules using system properties or existing Cosmic-compatible configuration access, with disabled-safe defaults.
- [x] 3.2 Replace the placeholder compatible endpoint behavior with a JDK 8 compatible OpenAI-compatible provider adapter that maps `AiChatRequestDto` to provider JSON and returns `AiChatResponseDto`.
- [x] 3.3 Implement timeout, one optional 5xx retry, non-retry authentication/4xx handling, network/provider error classification, elapsed time, retry count, and minimal diagnostics.
- [x] 3.4 Implement stream chunk normalization to `AiStreamEventDto` without introducing Spring `SseEmitter` or Java 17 HTTP APIs.
- [x] 3.5 Ensure unavailable, disabled, or incomplete configuration never performs a network call and never returns fake successful AI content.

## 4. Function Calling and Scenario Integration

- [x] 4.1 Verify `AiFunctionCallingService` continues to use server-side userId, loop cap, registered tools only, and provider-neutral tool call DTOs with the real provider adapter.
- [x] 4.2 Wire assistant chat, interview, task decomposition, career plan, resume diagnosis, and long-memory entry points through the existing gateway/scenario adapters without changing WebAPI DTOs.
- [x] 4.3 Add focused service tests covering provider-enabled success, provider-disabled fallback, unknown tool, tool loop cap, and scenario fallback on provider failure.

## 5. Documentation and Migration Map

- [x] 5.1 Update `docs/ipd-to-cyancruise-migration-map.md` with IPD source paths, CyanCruise targets, request/response mapping, temporarily excluded items, tenant verification, rollback, and validation commands.
- [x] 5.2 Update `webapp/isv/v620/careerloop/careerloop-routes.json` or related README metadata to show production AI provider adapter status and secret-free fallback behavior.
- [x] 5.3 Ensure OpenSpec markdown remains Chinese-first while preserving SHALL/WHEN/THEN keywords.

## 6. Verification

- [x] 6.1 Run focused AI helper/provider/function-calling tests.
- [x] 6.2 Run `openspec validate migrate-ai-provider-production-adapter --strict`.
- [x] 6.3 Run `openspec validate --all --strict`.
- [x] 6.4 Run JDK 8 `.\gradlew.bat clean build`.
- [x] 6.5 Commit the implementation and migration-map changes, then push `codex/migrate-ai-provider-production-adapter`.
