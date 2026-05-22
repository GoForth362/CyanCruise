## 1. Storage Adapter

- [x] 1.1 Make career profile DTO roots and nested DTOs serializable.
- [x] 1.2 Add file-backed `CareerProfileStorage` implementation.
- [x] 1.3 Use file-backed storage as the default in `CareerProfileApplicationService`.
- [x] 1.4 Keep in-memory storage available as a test/dummy adapter.

## 2. Documentation

- [x] 2.1 Update migration map to distinguish durable file adapter from final Cosmic datamodel persistence.

## 3. Validation

- [x] 3.1 Run `openspec validate prepare-career-profile-persistence-adapter --strict`.
- [x] 3.2 Run `openspec validate --all --strict`.
- [x] 3.3 Run JDK 8 Gradle build.
