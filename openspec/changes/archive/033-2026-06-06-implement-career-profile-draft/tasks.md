## 1. Contract And Storage

- [x] 1.1 Add a serializable profile draft DTO in base common.
- [x] 1.2 Extend the profile storage boundary with load, save, and clear draft operations.
- [x] 1.3 Implement draft persistence in file, memory, and Cosmic placeholder adapters.

## 2. Application And API

- [x] 2.1 Add application service methods for get, save-merge, and clear draft.
- [x] 2.2 Expose draft operations on `CareerProfileWebApi`.
- [x] 2.3 Route draft operations through `CareerLoopCustomWebApiPlugin`.

## 3. Tests And Validation

- [x] 3.1 Add storage and application tests for persistence, partial merge, blank handling, and clear behavior.
- [x] 3.2 Add WebAPI/plugin tests for draft save and read routing.
- [x] 3.3 Run OpenSpec strict validation and targeted Gradle tests.
