## Context

CareerLoop now starts on the user profile page and lets users enter personal context before choosing a route. Existing backend profile operations persist committed onboarding, preferences, facts, snapshots, and derived unified profiles, but there is no first-class draft object for partial home-page input.

The implementation must stay inside the Cosmic second-development project constraints: JDK 1.8, no new dependency, no hardcoded local platform path, and replaceable storage boundaries so a later Cosmic datamodel adapter can take over.

## Goals / Non-Goals

**Goals:**
- Persist partial profile draft data per user and allow it to be read back by a later web session.
- Keep drafts independent from completed onboarding snapshots until the user explicitly saves onboarding.
- Reuse the existing `CareerProfileApplicationService`, `CareerProfileStorage`, WebAPI, and custom plugin boundaries.
- Cover file storage persistence and API behavior with focused tests.

**Non-Goals:**
- Do not migrate resume upload, assessment, path planning, or today-action generation in this change.
- Do not introduce a new database, Spring/JPA/Flyway layer, or frontend framework.
- Do not change existing snapshot/onboarding/preference response semantics.

## Decisions

- Store drafts through `CareerProfileStorage` instead of a new service-specific repository. This keeps the current file adapter and future Cosmic datamodel adapter aligned behind one replaceable boundary. Alternative considered: only use browser/local storage, but that would not satisfy cross-session backend persistence.
- Add a dedicated draft DTO rather than overloading `CareerProfileOnboardingRequest`. Drafts need UI-oriented fields such as education stage, school/major, experience, route intent, and optional target role without implying onboarding completion. Alternative considered: extend onboarding request directly, but that would blur completed onboarding and partial draft behavior.
- Expose `/draft/get`, `/draft/save`, and `/draft/clear` under `/cc001/career-profile`. These paths are colocated with existing profile APIs and can be routed by the custom WebAPI plugin without changing `ccRoute`.
- Keep draft save as a partial merge. Non-empty submitted fields update the draft; omitted or blank fields do not clear previously stored values. Clearing is explicit through `/draft/clear`.

## Risks / Trade-offs

- Draft and onboarding fields can drift over time. Mitigation: keep the draft DTO small and map only route-entry fields needed by the current UI.
- File serialization is a temporary persistence format. Mitigation: add storage interface methods so the Cosmic adapter has an explicit contract to implement later.
- Partial merge cannot intentionally blank a single field. Mitigation: provide explicit full-draft clear now; add per-field clearing later only if UX needs it.
