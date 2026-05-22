## 1. Test Setup

- [x] 1.1 Add JUnit Jupiter test dependency to `v620-cc001-cloud01-app01`.
- [x] 1.2 Keep dependency scoped to tests only.

## 2. Storage Tests

- [x] 2.1 Test file storage reloads saved snapshot from a fresh storage instance.
- [x] 2.2 Test file storage reloads saved facts from a fresh storage instance.
- [x] 2.3 Test file storage reloads saved derived profile from a fresh storage instance.
- [x] 2.4 Test application service can read onboarding data through a fresh service using the same storage directory.

## 3. Validation

- [x] 3.1 Run `openspec validate add-career-profile-storage-tests --strict`.
- [x] 3.2 Run `openspec validate --all --strict`.
- [x] 3.3 Run cloud app test task.
- [x] 3.4 Run JDK 8 Gradle build.
